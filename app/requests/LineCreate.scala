package requests

import models.LineBuilder
import play.api.data.Form
import play.api.data.Forms._
import scalikejdbc.DBSession

case class LineCreate(name: String, timeTablePeriod: Int, stations: Seq[StationCreate], patterns: Seq[PatternCreate]) {
  private def build: LineBuilder = LineBuilder(name, timeTablePeriod)

  def save()(implicit session: DBSession): Unit = {
    build.save()
  }
}

object LineCreate {
  val form = Form(
    mapping(
      "name" -> text(maxLength = 255),
      "timeTablePeriod" -> number(min = 1),
      "stations" -> seq(StationCreate.mapper),
      "patterns" -> seq(PatternCreate.mapper)
    )(LineCreate.apply)(LineCreate.unapply)
  )
}
