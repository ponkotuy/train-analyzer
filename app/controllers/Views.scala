package controllers

import analyzer.FastestPath
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
      val ts = pattern.trains()
      val stations = line.stations()
      val tt = TimeTable.findAllBy(sqls.in(TimeTable.tt.trainId, ts.map(_.id)))
      val fastest: Map[Long, Int] = FastestPath.fixTable(line, pattern, stations, ts, tt).calc.map { path =>
        path.end.stationId -> path.period
      }(breakOut)
      Ok(views.html.trains(pattern, line, stations, ts, tt, fastest))
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
      val table = TimeTable.findAllBy(sqls.eq(tt.trainId, trainId), Seq(tt.minutes.asc, tt.isArrive.desc))
      val stations: Map[Long, Station] =
        Station.findAllBy(sqls.eq(Station.st.lineId, line.id)).map { st => st.id -> st }(breakOut)
      Ok(views.html.time_table(train, pattern, line, table, stations))
    }
    result.getOrElse(NotFound(s"NotFound train"))
  }
}
