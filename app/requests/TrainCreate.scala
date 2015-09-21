package requests

import models.TrainBuilder
import play.api.data.Forms._
import scalikejdbc.DBSession

case class TrainCreate(trainClass: String, name: String, timeTable: Seq[TimeTableCreate]) {
  private def build(patternId: Long): TrainBuilder = TrainBuilder(patternId, trainClass, name)

  def save(patternId: Long)(implicit session: DBSession): Long = {
    val trainId = build(patternId).save()
    timeTable.foreach(_.save(trainId))
    trainId
  }
}

object TrainCreate {
  val mapper = mapping(
    "trainClass" -> text(maxLength = 64),
    "name" -> text(maxLength = 255),
    "timeTable" -> seq(TimeTableCreate.mapper)
  )(TrainCreate.apply)(TrainCreate.unapply)
}
