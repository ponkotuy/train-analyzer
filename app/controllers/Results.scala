package controllers

import play.api.data.Form
import play.api.mvc.Result
import play.api.mvc.Results._

object Results {
  def validationError(form: Form[_]): Result = {
    val message = form.errors.map(_.message).mkString("\n")
    BadRequest(message)
  }
}
