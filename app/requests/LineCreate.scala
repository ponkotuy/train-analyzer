package requests

import models.LineBuilder
import play.api.data.Form
import play.api.data.Forms._
import scalikejdbc.{AutoSession, DBSession}

case class LineCreate(name: String, stations: Seq[StationCreate], patterns: Seq[PatternCreate]) {
  private def build: LineBuilder = LineBuilder(name)

  def save()(implicit session: DBSession = AutoSession): Unit = {
    val lineId = build.save()
    stations.foreach(_.save(lineId))
    patterns.foreach(_.save(lineId))
  }
}

object LineCreate {
  val form = Form(
    mapping(
      "name" -> text(maxLength = 255),
      "stations" -> seq(StationCreate.mapper),
      "patterns" -> seq(PatternCreate.mapper)
    )(LineCreate.apply)(LineCreate.unapply)
  )
}
