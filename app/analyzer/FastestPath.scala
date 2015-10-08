package analyzer

import models._
import scalikejdbc._

import scala.collection.breakOut
import scala.collection.mutable

case class FastestPath(line: Line, pattern: Pattern, stations: Seq[Station], trains: Seq[Train], table: Seq[TimeTable]) {
  val entirePeriod = pattern.timeTablePeriod
  val paths: Map[Long, List[TrainPath]] = table.groupBy(_.trainId).mapValues { case xs =>
    def f(xs: List[TimeTable]): List[TrainPath] = {
      xs match {
        case x :: y :: rest =>
          if(y.isArrive) TrainPath(x, y) :: f(rest)
          else TrainPath(x, y.copy(isArrive = true)) :: f(y :: rest)
        case _ => Nil
      }
    }
    f(xs.sortBy { x => (x.minutes, !x.isArrive) }.toList)
  }

  def calc: Seq[TrainPath] = {
    if(stations.isEmpty || trains.isEmpty) return Nil
    val initPaths = paths.flatMap { case (_, xs) =>
      xs.filter(_.start.stationId == stations.head.id)
    }(breakOut)
    lookup(stations.tail, initPaths)
  }

  /** 待ち時間も含めた平均所要時間
    *
    * @return StationId -> Minutes
    */
  def averageCalc: scala.collection.immutable.Map[Long, Double] = {
    if(stations.isEmpty || trains.isEmpty) return Map.empty
    val sums = mutable.Map[Long, Int]().withDefaultValue(0)
    val start = stations.head
    (0 until entirePeriod).foreach { minutes =>
      val init = TimeTable(0L, 0L, start.id, minutes, true)
      val result = lookup(stations.tail, nextPaths(init).map(_.copy(start = init)))
      result.foreach { path =>
        sums(path.end.stationId) += path.period
      }
    }
    sums.mapValues(_ / entirePeriod.toDouble).toMap
  }

  private def lookup(sts: Seq[Station], paths: Seq[TrainPath]): Seq[TrainPath] = sts match {
    case st +: rest =>
      val grouped = paths.groupBy(_.end.stationId == st.id)
      val passed = grouped.getOrElse(false, Nil)
      val stop = grouped.getOrElse(true, Nil)
      val minStop = stop.minBy(_.period)
      val newPaths = passed ++ stop.flatMap(nextPaths).filter(_.period <= minStop.period + entirePeriod)
      if(stop.nonEmpty) minStop +: lookup(rest, newPaths) else lookup(rest, newPaths)
    case _ => Nil
  }

  def nextPaths(now: TimeTable): Seq[TrainPath] = paths.flatMap { case (tId, xs) =>
    xs.find(_.start.stationId == now.stationId)
  }.map { path =>
    val connMinutes = ((now.minutes - path.start.minutes) / entirePeriod.toDouble).ceil.toInt * entirePeriod
    val connPath = path.end.next(connMinutes)
    path.copy(end = connPath)
  }(breakOut)

  def nextPaths(path: TrainPath): Seq[TrainPath] = nextPaths(path.end).map(path.join)
}

object FastestPath {
  def fromPattern(pattern: Pattern)(implicit session: DBSession = AutoSession): Option[FastestPath] = {
    pattern.line().map { line =>
      val stations = line.stations()
      val table = TimeTable.findAllBy(sqls.in(TimeTable.tt.stationId, stations.map(_.id)))
      val trains = pattern.trains()
      apply(line, pattern, stations, trains, table)
    }
  }
}

case class TrainPath(start: TimeTable, end: TimeTable) {
  lazy val period = end.minutes - start.minutes
  def join(next: TimeTable): TrainPath = copy(end = next)
  def join(path: TrainPath): TrainPath = join(path.end)
}
