package models

import reactivemongo.bson._
import reactivemongo.api.collections.default.BSONCollection
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.Some
import reactivemongo.api.collections.default.BSONCollection
import scala.Some
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.api.DefaultDB
import play.api.Play
import play.api.Play.current

/**
 * Created with IntelliJ IDEA.
 * User: fred
 * Date: 14/10/13
 * Time: 14:34
 * To change this template use File | Settings | File Templates.
 */
case class Configuration(_id: Option[BSONObjectID], login: String, password: String, welcome_text: String,index_text:String) {

}


object Configuration extends ModelObject[Configuration]{


  val collectionName: String = "Configuration"
  implicit val format = Macros.handler[Configuration]



  def defaultConfiguration(implicit db :DefaultDB): Future[Configuration] = {




    val confF = collection.find(BSONDocument()).one[Configuration]
    confF.flatMap {
      conf => conf match {
        case None => {

          val login_default = Play.application.configuration.getString("default.login").getOrElse("admin")
          val pwd_default =  Play.application.configuration.getString("default.pwd").getOrElse("admin")

          val id = BSONObjectID.generate
          val c = Configuration(Some(id), login_default,pwd_default, "","Go to <a href=\"/admin\">Admin </a> :)")
          collection.insert(c)
          future(c)
        }
        case Some(conf) => {
          future(conf)


        }
      }

    }
  }

}
