package models

import java.util.UUID

case class Event(
  id: String,
  creationDate: java.sql.Timestamp,
  updateDate: java.sql.Timestamp,
  creatorId: String,
  name: String,
  date: String,
  nbrePlaces: String,
  description: String
) 

trait EventComponent {
  this: Profile =>

  import profile.simple._

  object Events extends Table[Event]("events") {
    def id = column[String]("id", O.PrimaryKey)
    def creationDate = column[java.sql.Timestamp]("creationDate")
    def updateDate = column[java.sql.Timestamp]("updateDate")
    def creatorId = column[String]("creatorId")
    def name = column[String]("name")
    def date = column[String]("date")
    def nbrePlaces = column[String]("nbrePlaces")
    def description = column[String]("description")
    def * = id ~ creationDate ~ updateDate ~ creatorId ~ name ~ date ~ nbrePlaces ~ description <> (Event, Event.unapply _)

    def add(event: Event)(implicit session: Session) = {
      this.insert(event)
    }

    def get(eid: String)(implicit session: Session): Option[Event] = {
      val q = for(u <- Events if u.id === eid) yield u
      q.list().headOption
    }

    def getByUser(uid: String)(implicit session: Session): List[Event] = {
      val q = for(u <- Events if u.creatorId === uid) yield u
      q.list()
    }

    def save(event: Event)(implicit session: Session) = {
      val q = for(u <- Events if u.id === event.id) yield u
      q.update(event)
    }

    def remove(uid: String)(implicit session: Session) = {
      val q = for(u <- Events if u.id === uid) yield u
      q.delete
    }
  }

}