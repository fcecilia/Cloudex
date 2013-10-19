package models

/**
 * Created with IntelliJ IDEA.
 * User: fred
 * Date: 29/09/13
 * Time: 21:08
 * To change this template use File | Settings | File Templates.
 */


import reactivemongo.bson._

import play.api.libs.concurrent.Execution.Implicits._
import reactivemongo.api.collections.default.BSONCollection
import java.text.SimpleDateFormat
import java.util.Date
import reactivemongo.api.DefaultDB

case class Redirection(_id: Option[BSONObjectID], path: String, url: String, comment: String) {

  val key = _id.map(_.stringify)

}

object Redirection extends ModelObject[Redirection] {

  val collectionName: String = "Redirections"
  implicit val format = Macros.handler[Redirection]


  def whithPath(path: String)(implicit db: DefaultDB) = {

    collection.
      find(BSONDocument("path" -> BSONString(path))).
      one[Redirection]
  }


}