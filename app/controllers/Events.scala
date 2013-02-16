package controllers

import models._
import play.api._
import play.api.mvc._
import java.util.UUID
import play.api.libs.json._
import slick.session.Session
import play.api.data._
import play.api.data.Forms._
import views._

case class EventData(
  name: String,
  date: String,
  nbrePlaces: String
)

object Events extends Controller with Secured {

  implicit val session = AppDB.database.withSession {
    implicit session: Session => session
  }

  def add(uid: String) = IsAuthenticated(username => _ =>
    Ok(html.newEvent(uid, createEventForm))
  )

  def saveNew(uid: String) = IsAuthenticated (username => implicit request =>
    createEventForm.bindFromRequest.fold(
      errors => BadRequest(html.newEvent(uid, errors)),
      newEvent => {
        val eventToSave = Event(
          id = UUID.randomUUID().toString,
          creationDate = new java.sql.Timestamp(new java.util.Date().getTime()),
          updateDate = new java.sql.Timestamp(new java.util.Date().getTime()),
          creatorId = uid,
          name = newEvent.name,
          date = newEvent.date,
          nbrePlaces = newEvent.nbrePlaces
        )
        AppDB.dal.Events.add(eventToSave)

        /*val userEventToSave = UserEvent(
          userId = uid,
          eventId = eventToSave.id
        )
        AppDB.dal.UserEvents.add(userEventToSave)*/
        Redirect(routes.Users.open(uid))
      }
    )
  )

  def book(eid: String) = Action { implicit request => 
    //request.session.get("username")
    request.session.get("username") map { username =>
      //TODO
      AppDB.dal.Users.getByUsername(username) map { user =>
        val userEventToSave = UserEvent(
          userId = user.id,
          eventId = eid
        )
        AppDB.dal.UserEvents.add(userEventToSave)
        Ok("ok inscrit")  
      } getOrElse BadRequest
    } getOrElse Ok("notconnected")
  }

  def createEventForm = Form(
    mapping(
      "name" -> text,
      "date" -> text,
      "nbrePlaces" -> text
    )(EventData.apply)(EventData.unapply)
  )
}