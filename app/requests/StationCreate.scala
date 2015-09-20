package requests

import models.StationBuilder
import play.api.data.Forms._
import scalikejdbc.DBSession

case class StationCreate(name: String) {
  private def build(lineId: Long): StationBuilder = StationBuilder(name, lineId)
  def save(lineId: Long)(implicit session: DBSession): Long = {
    build(lineId).save()
  }
}

object StationCreate {
  val mapper = mapping(
    "name" -> text(maxLength = 255)
  )(StationCreate.apply)(StationCreate.unapply)
}
