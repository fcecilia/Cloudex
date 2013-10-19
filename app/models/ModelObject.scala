package models

import reactivemongo.bson.{BSONDocumentWriter, BSONDocumentReader, BSONObjectID, BSONDocument}
import reactivemongo.api.collections.default.BSONCollection
import scala.concurrent.ExecutionContext.Implicits.global
import reactivemongo.api.DefaultDB


/**
 * Created with IntelliJ IDEA.
 * User: fred
 * Date: 17/10/13
 * Time: 12:01
 * To change this template use File | Settings | File Templates.
 */
trait ModelObject[T] {

  val collectionName: String

  def collection(implicit db: DefaultDB) = db[BSONCollection](collectionName)

  def byId(objectId: String)(implicit db: DefaultDB, format: BSONDocumentReader[T]) = {

    collection.
      find(BSONDocument("_id" -> BSONObjectID(objectId))).
      one[T]
  }


  def all(implicit db: DefaultDB, format: BSONDocumentReader[T]) = {
    //TODO Mieux gérer ça !
    collection.find(BSONDocument()).cursor[T].collect[List](Int.MaxValue, true)

  }


  /*    Reactivemongo  0.9 Version
def all(implicit db: DefaultDB, format: BSONDocumentReader[T]) = {
//TODO Mieux gérer ça !
collection.find(BSONDocument()).cursor[T].toList()

}
 */


  def one[T](filter: BSONDocument)(implicit db: DefaultDB, format: BSONDocumentReader[T]) = {
    collection.
      find(filter).one[T]
  }

  def list[T](filter: BSONDocument)(implicit db: DefaultDB, format: BSONDocumentReader[T]) = {
    collection.
      find(filter).cursor[T].collect[List](Int.MaxValue, true)
  }

  
  
  /*  Reactivemongo 0.9 Version
  def list[T](filter:BSONDocument)(implicit db: DefaultDB, format: BSONDocumentReader[T]) = {
    collection.
      find(filter).cursor[T].toList()
  }
   */


  def update[T <: ModelClass](data:T)(implicit db: DefaultDB, format: BSONDocumentWriter[T]) = {

    collection.update(BSONDocument("_id" -> data._id),data)
  }
}


trait ModelClass {

  val _id: Option[BSONObjectID]


}
