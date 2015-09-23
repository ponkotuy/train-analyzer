package models

import scalikejdbc._
import skinny.orm.{Alias, SkinnyCRUDMapperWithId}

case class Train(id: Long, patternId: Long, trainClass: String, name: String) {
  def pattern()(implicit session: DBSession = AutoSession): Option[Pattern] = Pattern.findById(patternId)
}

object Train extends SkinnyCRUDMapperWithId[Long, Train] {
  override def defaultAlias: Alias[Train] = createAlias("t")

  val t = defaultAlias

  override def extract(rs: WrappedResultSet, n: ResultName[Train]): Train = autoConstruct(rs, n)

  override def idToRawValue(id: Long): Any = id

  override def rawValueToId(value: Any): Long = value.toString.toLong

  def deleteWithTable(trainId: Long)(implicit session: DBSession = AutoSession): Boolean = {
    TimeTable.deleteBy(sqls.eq(TimeTable.column.trainId, trainId)) >= 1 &&
        Train.deleteById(trainId) == 1
  }
}

case class TrainBuilder(patternId: Long, trainClass: String, name: String) {
  def save()(implicit session: DBSession): Long = {
    Train.createWithAttributes(
      'patternId -> patternId,
      'trainClass -> trainClass,
      'name -> name
    )
  }
}
