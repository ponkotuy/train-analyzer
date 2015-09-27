package analyzer

import models._
import scalikejdbc._

import scala.collection.breakOut

case class FastestPath(line: Line, pattern: Pattern, stations: Seq[Station], trains: Seq[Train], table: Seq[TimeTable]) {
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
    if(stations.isEmpty) return Nil
    def f(sts: Seq[Station], paths: Seq[TrainPath]): Seq[TrainPath] = sts match {
      case st +: rest =>
        val grouped = paths.groupBy(_.end.stationId == st.id)
        val passed = grouped.getOrElse(false, Nil)
        val stop = grouped.getOrElse(true, Nil)
        val newPaths = passed ++ stop.flatMap(nextPaths)
        if(stop.nonEmpty) stop.minBy(_.period) +: f(rest, newPaths) else f(rest, newPaths)
      case _ => Nil
    }

    val initPaths = paths.flatMap { case (_, xs) =>
      xs.filter(_.start.stationId == stations.head.id)
    }(breakOut)
    println(initPaths)
    f(stations.tail, initPaths)
  }

  def nextPaths(now: TimeTable): Seq[TrainPath] = paths.flatMap { case (tId, xs) =>
    xs.find(_.start.stationId == now.stationId)
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
