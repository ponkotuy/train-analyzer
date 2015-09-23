package models

import scalikejdbc._
import skinny.orm.{Alias, SkinnyCRUDMapperWithId}

case class Line(id: Long, name: String, timeTablePeriod: Int) {
  def stations()(implicit session: DBSession = AutoSession): Seq[Station] = {
    Station.findAllBy(sqls.eq(Station.st.lineId, id))
  }
}

object Line extends SkinnyCRUDMapperWithId[Long, Line] {
  override def defaultAlias: Alias[Line] = createAlias("l")

  override def extract(rs: WrappedResultSet, n: ResultName[Line]): Line = autoConstruct(rs, n)

  override def idToRawValue(id: Long): Any = id

  override def rawValueToId(value: Any): Long = value.toString.toLong
}

case class LineBuilder(name: String, timeTablePeriod: Int) {
  def save()(implicit session: DBSession = AutoSession): Long = {
    Line.createWithAttributes(
      'name -> name,
      'timeTablePeriod -> timeTablePeriod
    )
  }
}
