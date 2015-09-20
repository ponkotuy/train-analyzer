package models

import scalikejdbc._
import skinny.orm.{Alias, SkinnyCRUDMapperWithId}

case class TimeTable(id: Long, trainId: Long, stationId: Long, minutes: Int, isArrive: Boolean)

object TimeTable extends SkinnyCRUDMapperWithId[Long, TimeTable] {
  override def defaultAlias: Alias[TimeTable] = createAlias("tt")

  override def extract(rs: WrappedResultSet, n: ResultName[TimeTable]): TimeTable = autoConstruct(rs, n)

  override def idToRawValue(id: Long): Any = id

  override def rawValueToId(value: Any): Long = value.toString.toLong
}

case class TimeTableBuilder(trainId: Long, stationId: Long, minutes: Int, isArrive: Boolean) {
  def save()(implicit session: DBSession): Long = {
    TimeTable.createWithAttributes(
      'trainId -> trainId,
      'stationId -> stationId,
      'minutes -> minutes,
      'isArrive -> isArrive
    )
  }
}
