package controllers

import models.{Station, Train, Pattern, Line}
import play.api.mvc._
import scalikejdbc._

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
      line <- Line.findById(pattern.lineId)
    } yield {
      val ts = Train.findAllBy(sqls.eq(Train.t.patternId, patternId))
      val stations = Station.findAllBy(sqls.eq(Station.st.lineId, line.id))
      Ok(views.html.trains(pattern, line, stations, ts))
    }
    result.getOrElse(NotFound(s"Not found pattern"))
  }
}
