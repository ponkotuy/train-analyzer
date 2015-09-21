package controllers

import play.api.mvc._
import requests.LineCreate
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
}
