package models

import scalikejdbc._
import skinny.orm.{Alias, SkinnyCRUDMapperWithId}

case class Train(id: Long, patternId: Long, trainClass: String, name: String)

object Train extends SkinnyCRUDMapperWithId[Long, Train] {
  override def defaultAlias: Alias[Train] = createAlias("t")

  override def extract(rs: WrappedResultSet, n: ResultName[Train]): Train = autoConstruct(rs, n)

  override def idToRawValue(id: Long): Any = id

  override def rawValueToId(value: Any): Long = value.toString.toLong
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
