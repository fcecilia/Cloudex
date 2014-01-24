package controllers

import play.api.mvc.{Action, Controller}
import play.api.data.Form
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models._
import models.Configuration._
import views._
import play.modules.reactivemongo.MongoController
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.api.collections.default.BSONCollection
import play.api.mvc.SimpleResult
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created with IntelliJ IDEA.
 * User: fred
 * Date: 14/10/13
 * Time: 20:44
 */

import scala.concurrent.duration._
object Auth extends Controller with MongoController {

  implicit val implicitdbs = db
  val loginForm = Form(
    tuple(
      "name" -> text,
      "password" -> text
    ) verifying ("Invalid email or password", result => result match {
      case (email, password) => Await.result(check(email, password),5 seconds)
    })
  )

  def check(username: String, password: String) = {
    implicit val collection = db[BSONCollection](models.Configuration.collectionName)

    val conF =models.Configuration.defaultConfiguration
    conF.map{  conf =>
    (username == conf.login && password == conf.password)

    }
  }

  def login = Action { implicit request =>
    Ok(html.login(loginForm))
  }

  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.login(formWithErrors)),
      user => Redirect(routes.Application.admin()).withSession(Security.username -> user._1)
    )
  }

  def logout = Action {
    Redirect(routes.Auth.login).withNewSession.flashing(
      "success" -> "You are now logged out."
    )
  }
}

trait Secured {

  def username(request: RequestHeader) = request.session.get(Security.username)

  def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Auth.login)

  def withAuth(f: => String => Request[AnyContent] => scala.concurrent.Future[SimpleResult]) = {
    Security.Authenticated(username, onUnauthorized) { user =>
      Action.async(request => f(user)(request))
    }
  }


}