package models

import java.util.UUID

case class UserEvent(
  userId: String,
  eventId: String
) 

trait UserEventComponent {
  this: Profile =>

  import profile.simple._

  object UserEvents extends Table[UserEvent]("userevents") {
    def userId = column[String]("userid")
    def eventId = column[String]("eventid")
    def * = userId ~ eventId  <> (UserEvent, UserEvent.unapply _)

    def add(userEvent: UserEvent)(implicit session: Session) = {
      this.insert(userEvent)
    }

    /*def all(username: String)(implicit session: Session): List[User] = {
      val q = for(u <- Users if u.adminName === username) yield u
      q.list()
    }*/

    def getByUser(uid: String)(implicit session: Session): List[UserEvent] = {
      val q = for(u <- UserEvents if u.userId === uid) yield u
      q.list()
    }

    def listByEvents(events: List[Event])(implicit session: Session): List[UserEvent] = {
      events flatMap { event =>
        val q = for(u <- UserEvents if u.eventId === event.id) yield u
        q.list()
      }
    }

    def listUsers(userEvents: List[UserEvent])(implicit session: Session): List[User] = {
      userEvents flatMap { userEvent =>
        AppDB.dal.Users.get(userEvent.userId).map{ user =>
          user
        }
      }
    }

    def listEvents(userEvents: List[UserEvent])(implicit session: Session): List[Event] = {
      userEvents flatMap { userEvent =>
        AppDB.dal.Events.get(userEvent.eventId).map{ event =>
          event
        }
      }
    }

    def save(userEvent: UserEvent)(implicit session: Session) = {
      val q = for(u <- UserEvents if u.userId === userEvent.userId) yield u
      q.update(userEvent)
    }

    def remove(uid: String, eid: String)(implicit session: Session) = {
      val q = for(u <- UserEvents if (u.eventId === eid && u.userId === uid)) yield u
      q.delete
    }

  }

}