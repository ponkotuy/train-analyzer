package controllers

import play.api.mvc._
import requests.{PatternCreate, LineCreate}
import scalikejdbc.DB

object Lines extends Controller {
  def save() = Action { implicit req =>
    LineCreate.form.bindFromRequest().fold(Results.validationError, saveLine)
  }

  private def saveLine(create: LineCreate): Result = {
    DB localTx { implicit session =>
      create.save()
      Results.success
    }
  }

  def savePattern(lineId: Long) = Action { implicit seq =>
    PatternCreate.form.bindFromRequest().fold(Results.validationError, createPattern(lineId))
  }

  private def createPattern(lineId: Long)(create: PatternCreate): Result = {
    DB localTx { implicit session =>
      create.save(lineId)
      Results.success
    }
  }
}
