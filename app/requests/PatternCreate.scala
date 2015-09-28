package requests

import models.PatternBuilder
import play.api.data.Form
import play.api.data.Forms._
import scalikejdbc.{AutoSession, DBSession}

case class PatternCreate(name: String, timeTablePeriod: Int, trains: Seq[TrainCreate]) {
  private def build(lineId: Long): PatternBuilder = PatternBuilder(lineId, name, timeTablePeriod)
  def save(lineId: Long)(implicit session: DBSession = AutoSession): Long = {
    val patternId = build(lineId).save()
    trains.foreach(_.save(patternId))
    patternId
  }
}

object PatternCreate {
  val mapper = mapping(
    "name" -> text(maxLength = 255),
    "timeTablePeriod" -> number(min = 1),
    "trains" -> seq(TrainCreate.mapper)
  )(PatternCreate.apply)(PatternCreate.unapply)

  val form = Form(mapper)
}
