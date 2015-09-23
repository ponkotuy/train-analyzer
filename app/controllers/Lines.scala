package controllers

import models.Train
import play.api.mvc._
import requests.{LineCreate, PatternCreate, TrainCreate}

object Lines extends Controller {
  def save() = Action { implicit req =>
    LineCreate.form.bindFromRequest().fold(Results.validationError, saveLine)
  }

  private def saveLine(create: LineCreate): Result = {
    create.save()
    Results.success
  }

  def savePattern(lineId: Long) = Action { implicit seq =>
    PatternCreate.form.bindFromRequest().fold(Results.validationError, createPattern(lineId))
  }

  private def createPattern(lineId: Long)(create: PatternCreate): Result = {
    create.save(lineId)
    Results.success
  }

  def saveTrain(patternId: Long) = Action { implicit seq =>
    TrainCreate.form.bindFromRequest().fold(Results.validationError, createTrain(patternId))
  }

  private def createTrain(patternId: Long)(create: TrainCreate): Result = {
    create.save(patternId)
    Results.success
  }

  def deleteTrain(trainId: Long) = Action {
    if(Train.deleteWithTable(trainId)) Results.success else NotFound("Not found train")
  }
}
