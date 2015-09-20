package controllers

import play.api.mvc._
import requests.LineCreate

object Lines extends Controller {
  def save() = Action {
    LineCreate.form.bindFromRequest().fold(Results.validationError, saveLine)
  }

  def saveLine(create: LineCreate): Result = {

  }
}
