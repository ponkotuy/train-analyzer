package models

import scalikejdbc._
import skinny.orm.{Alias, SkinnyCRUDMapperWithId}

case class TimeTable(id: Long, trainId: Long, stationId: Long, minutes: Int, isArrive: Boolean) {
  def next(period: Int) = copy(minutes = minutes + period)
}

object TimeTable extends SkinnyCRUDMapperWithId[Long, TimeTable] {
  override def defaultAlias: Alias[TimeTable] = createAlias("tt")

  val tt = defaultAlias

  override def extract(rs: WrappedResultSet, n: ResultName[TimeTable]): TimeTable = autoConstruct(rs, n)

  override def idToRawValue(id: Long): Any = id

  override def rawValueToId(value: Any): Long = value.toString.toLong
}

case class TimeTableBuilder(trainId: Long, stationId: Long, minutes: Int, isArrive: Boolean) {
  def save()(implicit session: DBSession = AutoSession): Long = {
    TimeTable.createWithAttributes(
      'trainId -> trainId,
      'stationId -> stationId,
      'minutes -> minutes,
      'isArrive -> isArrive
    )
  }
}
