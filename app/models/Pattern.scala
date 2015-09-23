package models

import scalikejdbc._
import skinny.orm.{Alias, SkinnyCRUDMapperWithId}

case class Pattern(id: Long, lineId: Long, name: String) {
  def line()(implicit session: DBSession = AutoSession): Option[Line] = Line.findById(lineId)
  def trains()(implicit session: DBSession = AutoSession): Seq[Train] = Train.findAllBy(sqls.eq(Train.t.patternId, id))
}

object Pattern extends SkinnyCRUDMapperWithId[Long, Pattern] {
  override def defaultAlias: Alias[Pattern] = createAlias("p")

  val p = defaultAlias

  override def extract(rs: WrappedResultSet, n: ResultName[Pattern]): Pattern = autoConstruct(rs, n)

  override def idToRawValue(id: Long): Any = id

  override def rawValueToId(value: Any): Long = value.toString.toLong
}

case class PatternBuilder(lineId: Long, name: String) {
  def save()(implicit session: DBSession = AutoSession): Long = {
    Pattern.createWithAttributes(
      'lineId -> lineId,
      'name -> name
    )
  }
}
