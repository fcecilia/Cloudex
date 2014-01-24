package controllers

import play.api.mvc._
import play.modules.reactivemongo.{MongoController}
import reactivemongo.api.collections.default.BSONCollection
import models.{Configuration, Redirection}
import reactivemongo.bson.{BSONObjectID, BSONString, BSONDocument}
import scala.concurrent.future
import play.api.libs.json.Json
import scala.concurrent.ExecutionContext.Implicits.global
object Application extends Controller with MongoController with Secured {


  implicit val implicitdbs = db

  def index = Action.async {

    val confF = Configuration.defaultConfiguration

    confF.map {
      conf => Ok(views.html.index(conf.index_text, conf.welcome_text))
    }

  }


  def redirect(path: String) = Action.async {
    Redirection.whithPath(path).map {
      case None => Redirect(routes.Application.index())
      case Some(redirect) => Redirect(redirect.url)
    }
  }

  def admin =

    withAuth {
      username => implicit request =>

        val listF = {
          implicit val collection = db[BSONCollection](Redirection.collectionName)
          Redirection.all
        }


        val confF = {
          implicit val collection = db[BSONCollection](Configuration.collectionName)
          Configuration.defaultConfiguration

        }

        for (list <- listF; conf <- confF) yield {

          Ok(views.html.admin(list, conf))
        }
    }


  def saveWelcomeText() = withAuth {
    username =>
      request =>

        val value = request.body.asFormUrlEncoded.get("value")(0)

        println(value)
        implicit val collection = db[BSONCollection](Configuration.collectionName)
        val confF = Configuration.defaultConfiguration

        confF.map {
          conf =>

            val modifier = BSONDocument(
              "$set" -> BSONDocument(
                "welcome_text" -> BSONString(value)))

            collection.update(BSONDocument("_id" -> conf._id), modifier)
            Ok(value)
        }


  }

  def saveIndexText() = withAuth {
    username =>
      request =>

        val value = request.body.asFormUrlEncoded.get("value")(0)

        println(value)
        implicit val collection = db[BSONCollection](Configuration.collectionName)
        val confF = Configuration.defaultConfiguration

        confF.map {
          conf =>

            val modifier = BSONDocument(
              "$set" -> BSONDocument(
                "index_text" -> BSONString(value)))

            collection.update(BSONDocument("_id" -> conf._id), modifier)
            Ok(value)
        }


  }

  def saveRedirection() = withAuth {
    username =>
      request =>

        implicit val collection = db[BSONCollection](Redirection.collectionName)
        val elements_id = request.body.asFormUrlEncoded.get("id")(0)
        val valueSub = request.body.asFormUrlEncoded.get("value")(0)



        if (elements_id.contains("path_")) {
          val id = elements_id.replace("path_", "")

          val modifier = BSONDocument(
            "$set" -> BSONDocument(
              "path" -> BSONString(valueSub)))

          val result = collection.update(BSONDocument("_id" -> BSONObjectID(id)), modifier)
          result.map {
            res => res.inError match {
              case true => BadRequest(res.stringify)
              case false => Ok(valueSub)

            }
          }
        } else if (elements_id.contains("edit_")) {
          val value = if(valueSub.contains("://")) valueSub else "http://"+valueSub
          println(value)

          val id = elements_id.replace("edit_", "")

          val modifier = BSONDocument(
            "$set" -> BSONDocument(
              "url" -> BSONString(value)))

          val result = collection.update(BSONDocument("_id" -> BSONObjectID(id)), modifier)

          result.map {
            res => res.inError match {
              case true => BadRequest(res.stringify)
              case false => Ok(value)

            }
          }
        }
        else {
          future(BadRequest("Oups"))
        }
  }

  def deleteRedirection(id: String) = withAuth {
    username =>
      implicit request =>

        implicit val collection = db[BSONCollection](Redirection.collectionName)
        println(id)

        val result = collection.remove(BSONDocument("_id" -> BSONObjectID(id)))

        result.map {
          res => res.inError match {
            case true => BadRequest(res.stringify)
            case false => Ok("id")
          }
        }
  }

  def newRedirection() = withAuth {
    username =>
      request =>
        implicit val collection = db[BSONCollection](Redirection.collectionName)
        val id = BSONObjectID.generate
        collection.insert(Redirection(Some(id), "", "", ""))


        val result = Json.toJson(Map("id" -> id.stringify))
        future(Ok(result))
  }

  def savePassword() = withAuth {
    username =>
      request =>

        val value = request.body.asFormUrlEncoded.get("value")(0)

        println(value)
        implicit val collection = db[BSONCollection](Configuration.collectionName)
        val confF = Configuration.defaultConfiguration

        confF.map {
          conf =>

            val modifier = BSONDocument(
              "$set" -> BSONDocument(
                "password" -> BSONString(value)))

            collection.update(BSONDocument("_id" -> conf._id), modifier)
            Ok(value)
        }
  }

  def saveLogin() = withAuth {
    username =>
      request =>

        val value = request.body.asFormUrlEncoded.get("value")(0)

        println(value)
        implicit val collection = db[BSONCollection](Configuration.collectionName)
        val confF = Configuration.defaultConfiguration
        confF.map {
          conf =>

            val modifier = BSONDocument(
              "$set" -> BSONDocument(
                "login" -> BSONString(value)))

            collection.update(BSONDocument("_id" -> conf._id), modifier)
            Ok(value)
        }
  }
}