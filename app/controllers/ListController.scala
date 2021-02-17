package controllers

import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import models.{ListItem, NewListItem}
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.{Json, OFormat}

import javax.inject.{Inject, Singleton}
import scala.collection.mutable

@Singleton
class ListController @Inject()(val controllerComponents: ControllerComponents) extends BaseController{

  private val List = new mutable.ListBuffer[ListItem]()
  List += ListItem(1, "test", true)
  List += ListItem(2, "some other value", false)

  implicit val ListToJson = Json.format[ListItem]
  implicit val newListJson = Json.format[NewListItem]

  def getAll: Action[AnyContent] = Action {

    Ok(Json.toJson(List))

  }

  def getById(itemId: Long) = Action {
    val foundItem = List.find(_.id == itemId)
    foundItem match{
      case Some(item) => Ok(Json.toJson(item))
      case None => NotFound
    }
  }

  def addNewItem() = Action { implicit request =>
    val content = request.body
    val jsonObject = content.asJson
    val ListItem: Option[NewListItem] =
      jsonObject.flatMap(
        Json.fromJson[NewListItem](_).asOpt
      )

    ListItem match {
      case Some(newItem) =>
        val nextId = List.map(_.id).max + 1
        val toBeAdded = models.ListItem(nextId, newItem.description, isItDone = false)
        List += toBeAdded
        Created(Json.toJson(toBeAdded))
      case None => BadRequest
    }

  }

}



