package controllers

import models._
import play.api.mvc._
import scalikejdbc._

import scala.collection.breakOut

object Views extends Controller {
  def index = Action {
    val lines = Line.findAll()
    Ok(views.html.index(lines))
  }

  def patterns(lineId: Long) = Action {
    val ps = Pattern.findAllBy(sqls.eq(Pattern.p.lineId, lineId))
    Line.findById(lineId).fold(NotFound(s"Not found line")) { line =>
      Ok(views.html.patterns(line, ps))
    }
  }

  def trains(patternId: Long) = Action {
    val result = for {
      pattern <- Pattern.findById(patternId)
      line <- pattern.line()
    } yield {
      val ts = Train.findAllBy(sqls.eq(Train.t.patternId, patternId))
      val stations = Station.findAllBy(sqls.eq(Station.st.lineId, line.id))
      Ok(views.html.trains(pattern, line, stations, ts))
    }
    result.getOrElse(NotFound(s"Not found pattern"))
  }

  def timeTable(trainId: Long) = Action {
    val result = for {
      train <- Train.findById(trainId)
      pattern <- train.pattern()
      line <- pattern.line()
    } yield {
      val tt = TimeTable.tt
      val table = TimeTable.findAllBy(sqls.eq(tt.trainId, trainId), Seq(tt.minutes.asc))
      val stations: Map[Long, Station] =
        Station.findAllBy(sqls.eq(Station.st.lineId, line.id)).map { st => st.id -> st }(breakOut)
      Ok(views.html.time_table(train, pattern, line, table, stations))
    }
    result.getOrElse(NotFound(s"NotFound train"))
  }
}
