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
  nbrePlaces: String,
  description: String
)

object Events extends Controller with Secured {

  implicit val session = AppDB.database.withSession {
    implicit session: Session => session
  }

  def add(uid: String) = IsAuthenticated(username => _ =>
    (for {
      creator <- AppDB.dal.Users.get(uid)
    } yield {
      Ok(html.newEvent(creator, uid, createEventForm))
    }) getOrElse BadRequest
  )

  def saveNew(uid: String) = IsAuthenticated (username => implicit request =>
    (for {
      creator <- AppDB.dal.Users.get(uid)
    } yield {
      createEventForm.bindFromRequest.fold(
        errors => BadRequest(html.newEvent(creator, uid, errors)),
        newEvent => {
          val eventToSave = Event(
            id = UUID.randomUUID().toString,
            creationDate = new java.sql.Timestamp(new java.util.Date().getTime()),
            updateDate = new java.sql.Timestamp(new java.util.Date().getTime()),
            creatorId = uid,
            name = newEvent.name,
            date = newEvent.date,
            nbrePlaces = newEvent.nbrePlaces,
            description = newEvent.description
          )
          AppDB.dal.Events.add(eventToSave)
          Redirect(routes.Users.open(uid))
        }
      )
    }) getOrElse BadRequest
  )

  def edit(uid: String, eid: String) = IsAuthenticated (username => implicit request =>
    (for {
      visitor <- AppDB.dal.Users.getByUsername(username)
      event <- AppDB.dal.Events.get(eid)
    } yield {
      Ok(html.editEvent(visitor, uid, eid, createEventForm.fill(EventData(
        event.name,
        event.date,
        event.nbrePlaces,
        event.description
        )
      )))  
    }) getOrElse BadRequest
  )

  def update(uid: String, eid: String) = IsAuthenticated (username => implicit request =>
    (for {
      creator <- AppDB.dal.Users.get(uid)
      event <- AppDB.dal.Events.get(eid)
    } yield {
      createEventForm.bindFromRequest.fold(
        errors => {
          BadRequest(html.editEvent(creator, uid, eid, errors))
        },
        myForm => {
          val eventToSave = event.copy(
            updateDate = new java.sql.Timestamp(new java.util.Date().getTime()),
            name = myForm.name,
            date = myForm.date,
            nbrePlaces = myForm.nbrePlaces,
            description = myForm.description
          )
          AppDB.dal.Events.save(eventToSave)
          Redirect(routes.Users.open(uid))
        }
      )
    }) getOrElse BadRequest
  )

  def book(eid: String) = Action { implicit request => 
    request.session.get("username") map { username =>
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

  def deleteEvent(uid: String, eid: String) = IsAuthenticated { username => _ =>
    AppDB.dal.Events.remove(eid)
    Ok("event deleted")
  }

  def unBook(eid: String) = IsAuthenticated { username => implicit request =>
    AppDB.dal.Users.getByUsername(username) map { user =>
      AppDB.dal.UserEvents.remove(user.id, eid)
      Ok("desinscrit")
    } getOrElse BadRequest
  }

  def createEventForm = Form(
    mapping(
      "name" -> text,
      "date" -> text,
      "nbrePlaces" -> text,
      "description" -> text
    )(EventData.apply)(EventData.unapply)
  )
}