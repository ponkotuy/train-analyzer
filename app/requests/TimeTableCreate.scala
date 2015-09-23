package requests

import models._
import play.api.data.Forms._
import scalikejdbc.{AutoSession, DBSession}

case class TimeTableCreate(minutes: Int, isArrive: Boolean, stationNo: Int) {
  private def build(trainId: Long, stationId: Long): TimeTableBuilder =
    TimeTableBuilder(trainId: Long, stationId: Long, minutes, isArrive)

  def save(trainId: Long)(implicit session: DBSession = AutoSession): Option[Long] = {
    for {
      train <- Train.findById(trainId)
      pattern <- Pattern.findById(train.patternId)
      line <- Line.findById(pattern.lineId)
      station <- Station.findByNo(line.id, stationNo)
    } yield build(trainId, station.id).save()
  }
}

object TimeTableCreate {
  val mapper = mapping(
    "minutes" -> number(min = 0),
    "isArrive" -> boolean,
    "stationNo" -> number
  )(TimeTableCreate.apply)(TimeTableCreate.unapply)
}
