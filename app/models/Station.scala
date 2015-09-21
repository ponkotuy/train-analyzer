package models

import scalikejdbc._
import skinny.orm.{Alias, SkinnyCRUDMapperWithId}

case class Station(id: Long, name: String, no: Int, lineId: Long)

object Station extends SkinnyCRUDMapperWithId[Long, Station] {
  override def defaultAlias: Alias[Station] = createAlias("st")

  val st = defaultAlias

  override def extract(rs: WrappedResultSet, n: ResultName[Station]): Station = autoConstruct(rs, n)

  override def idToRawValue(id: Long): Any = id

  override def rawValueToId(value: Any): Long = value.toString.toLong

  def findByNo(lineId: Long, no: Int)(implicit session: DBSession): Option[Station] = {
    findBy(sqls.eq(st.lineId, lineId).and.eq(st.no, no))
  }
}

case class StationBuilder(name: String, no: Int, lineId: Long) {
  def save()(implicit session: DBSession): Long = {
    Station.createWithAttributes(
      'name -> name,
      'no -> no,
      'lineID -> lineId
    )
  }
}
