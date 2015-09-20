package requests

import models.TimeTableBuilder
import play.api.data.Forms._

case class TimeTableCreate(minutes: Int, isArrive: Boolean) {
  private def build(trainId: Long, stationId: Long): TimeTableBuilder =
    TimeTableBuilder(trainId: Long, stationId: Long, minutes, isArrive)

  private def save(trainId: Long, stationId: Long): Long = {
    build(trainId, stationId).save()
  }
}

object TimeTableCreate {
  val mapper = mapping(
    "minutes" -> number(min = 0),
    "isArrive" -> boolean
  )(TimeTableCreate.apply)(TimeTableCreate.unapply)
}
