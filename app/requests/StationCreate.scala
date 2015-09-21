package requests

import models.StationBuilder
import play.api.data.Forms._
import scalikejdbc.DBSession

case class StationCreate(name: String, no: Int) {
  private def build(lineId: Long): StationBuilder = StationBuilder(name, no, lineId)
  def save(lineId: Long)(implicit session: DBSession): Long = {
    build(lineId).save()
  }
}

object StationCreate {
  val mapper = mapping(
    "name" -> text(maxLength = 255),
    "no" -> number
  )(StationCreate.apply)(StationCreate.unapply)
}
