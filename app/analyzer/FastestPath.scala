package analyzer

import models._
import scalikejdbc._

import scala.collection.mutable
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
  println(paths.map(_._2.map(_.start.stationId)))

  def calc: Seq[TrainPath] = {
    stations.headOption.map { start =>
      val result = mutable.Map[Long, TrainPath]()
      val temp = mutable.Buffer[TrainPath]()
      temp ++= paths.flatMap { case (_, xs) =>
        xs.filter(_.start.stationId == start.id)
      }
      while(temp.nonEmpty) {
        println(temp.map(_.period))
        val min = temp.minBy(_.period)
        temp -= min
        result(min.end.stationId) = min
        temp ++= nextPaths(min.end).map(min.join)
        temp.filter { p => result.contains(p.end.stationId) }.foreach(temp -= _)
      }
      result.values.toVector
    }.getOrElse(Nil)
  }

  def nextPaths(now: TimeTable): Seq[TrainPath] = paths.flatMap { case (tId, xs) =>
    xs.find(_.start.stationId == now.stationId)
  }(breakOut)
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
