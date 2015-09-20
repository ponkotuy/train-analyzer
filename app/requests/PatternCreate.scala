package requests

import models.PatternBuilder
import play.api.data.Forms._
import scalikejdbc.DBSession

case class PatternCreate(name: String, trains: Seq[TrainCreate]) {
  private def build(lineId: Long): PatternBuilder = PatternBuilder(lineId, name)
  def save(lineId: Long)(implicit session: DBSession): Long = {
    build(lineId).save()
  }
}

object PatternCreate {
  val mapper = mapping(
    "name" -> text(maxLength = 255),
    "trains" -> seq(TrainCreate.mapper)
  )(PatternCreate.apply)(PatternCreate.unapply)
}
