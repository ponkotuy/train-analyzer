package analyzer

import models._
import scalikejdbc._

import scala.collection.mutable

case class FastestPath(line: Line, pattern: Pattern, stations: Seq[Station], trains: Seq[Train], table: Seq[TimeTable]) {
  val trainTable = table.groupBy(_.trainId).mapValues(_.sortBy(_.minutes))
  def calc: Seq[TrainPath] = {
    stations.headOption.map { start =>
      val result = mutable.Map[Long, TrainPath]()
      val temp = mutable.Buffer[TrainPath]()
      temp ++= nextPaths(start.id)
      while(temp.nonEmpty) {
        val min = temp.minBy(_.period)
        temp -= min
        result(min.end.stationId) = min
        temp ++= nextPaths(min.end.stationId).filterNot { p => result.contains(p.end.stationId) }.map(min.join)
      }
      result.values.toVector
    }.getOrElse(Nil)
  }

  def stopTable(stationId: Long) = table.filter(_.stationId == stationId)
  def nextPaths(stationId: Long) = stopTable(stationId).flatMap { table =>
    trainTable(table.trainId).dropWhile(_.stationId != table.stationId).lift(1).map { end =>
      TrainPath(table, end)
    }
  }
}

object FastestPath {
  def fromPattern(pattern: Pattern)(implicit session: DBSession = AutoSession): Option[FastestPath] = {
    pattern.line().map { line =>
      val stations = line.stations()
      val table = TimeTable.findAllBy(sqls.in(TimeTable.tt.stationId, stations.map(_.id)))
      val trains = pattern.trains()
      FastestPath(line, pattern, stations, trains, table)
    }
  }
}

case class TrainPath(start: TimeTable, end: TimeTable) {
  lazy val period = end.minutes - start.minutes
  def join(next: TimeTable): TrainPath = copy(end = next)
  def join(path: TrainPath): TrainPath = join(path.end)
}
