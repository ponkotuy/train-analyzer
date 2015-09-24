package analyzer

import models._
import scalikejdbc._

import scala.collection.mutable
import scala.collection.breakOut

case class FastestPath(line: Line, pattern: Pattern, stations: Seq[Station], trains: Seq[Train], table: Seq[TimeTable]) {
  val trainTable = table.filterNot(_.isArrive).groupBy(_.trainId).mapValues(_.sortBy(_.minutes))
  def calc: Seq[TrainPath] = {
    stations.headOption.map { start =>
      val result = mutable.Map[Long, TrainPath]()
      val temp = mutable.Buffer[TrainPath]()
      temp ++= nextPaths(start.id)
      while(temp.nonEmpty) {
        val min = temp.minBy(_.period)
        temp -= min
        result(min.end.stationId) = min
        temp ++= nextPaths(min.end.stationId).map(min.join)
        temp.filter { p => result.contains(p.end.stationId) }.foreach(temp -= _)
        println(temp.map(_.period))
      }
      result.values.toVector
    }.getOrElse(Nil)
  }

  def stopTable(stationId: Long) = table.filter { t => t.stationId == stationId && !t.isArrive }
  def nextPaths(stationId: Long) = stopTable(stationId).flatMap { table =>
    trainTable(table.trainId).dropWhile(_.stationId != table.stationId).lift(1).map { end =>
      TrainPath(table, end)
    }
  }
}

object FastestPath {
  def fixTable(line: Line, pattern: Pattern, stations: Seq[Station], trains: Seq[Train], table: Seq[TimeTable]): FastestPath = {
    val newTable: Seq[TimeTable] = table.groupBy { t => (t.stationId, t.trainId) }.flatMap { case (_, xs) =>
      val arrive = xs.find(_.isArrive).getOrElse(copyArriveDepart(xs.head))
      val depart = xs.find(!_.isArrive).getOrElse(copyArriveDepart(xs.head))
      arrive :: depart :: Nil
    }(breakOut)
    println(table)
    new FastestPath(line, pattern, stations, trains, newTable)
  }

  private def copyArriveDepart(elem: TimeTable) = TimeTable(-1L, elem.trainId, elem.stationId, elem.minutes, !elem.isArrive)

  def fromPattern(pattern: Pattern)(implicit session: DBSession = AutoSession): Option[FastestPath] = {
    pattern.line().map { line =>
      val stations = line.stations()
      val table = TimeTable.findAllBy(sqls.in(TimeTable.tt.stationId, stations.map(_.id)))
      val trains = pattern.trains()
      fixTable(line, pattern, stations, trains, table)
    }
  }
}

case class TrainPath(start: TimeTable, end: TimeTable) {
  lazy val period = end.minutes - start.minutes
  def join(next: TimeTable): TrainPath = copy(end = next)
  def join(path: TrainPath): TrainPath = join(path.end)
}
