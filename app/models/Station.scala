package models

import scalikejdbc._
import skinny.orm.{Alias, SkinnyCRUDMapperWithId}

case class Station(id: Long, name: String, lineId: Long)

object Station extends SkinnyCRUDMapperWithId[Long, Station] {
  override def defaultAlias: Alias[Station] = createAlias("st")

  override def extract(rs: WrappedResultSet, n: ResultName[Station]): Station = autoConstruct(rs, n)

  override def idToRawValue(id: Long): Any = id

  override def rawValueToId(value: Any): Long = value.toString.toLong
}

case class StationBuilder(name: String, lineId: Long) {
  def save()(implicit session: DBSession): Long = {
    Station.createWithAttributes(
      'name -> name,
      'lineID -> lineId
    )
  }
}
