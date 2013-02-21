package models

import java.util.UUID

case class User(
  id: String,
  creationDate: java.sql.Timestamp,
  updateDate: java.sql.Timestamp,
  status: String,
  prename: String,
  lastname: String,
  email: String,
  password: String,
  phone: String
) 

trait UserComponent {
  this: Profile =>

  import profile.simple._

  object Users extends Table[User]("users") {
    def id = column[String]("id", O.PrimaryKey)
    def creationDate = column[java.sql.Timestamp]("creationdate")
    def updateDate = column[java.sql.Timestamp]("updatedate")
    def status = column[String]("status")
    def prename = column[String]("prename")
    def lastname = column[String]("lastname")
    def email = column[String]("email")
    def password = column[String]("password")
    def phone = column[String]("phone")
    def * = id ~ creationDate ~ updateDate ~ status ~ prename ~ lastname ~ email ~ password ~ phone <> (User, User.unapply _)

    def add(user: User)(implicit session: Session) = {
      this.insert(user)
    }

    /*def all(username: String)(implicit session: Session): List[User] = {
      val q = for(u <- Users if u.adminName === username) yield u
      q.list()
    }*/

    def get(uid: String)(implicit session: Session): Option[User] = {
      val q = for(u <- Users if u.id === uid) yield u
      q.list().headOption
    }

    def getByUsername(username: String)(implicit session: Session): Option[User] = {
      val q = for(u <- Users if u.email === username) yield u
      q.list().headOption
    }

    def save(user: User)(implicit session: Session) = {
      val q = for(u <- Users if u.id === user.id) yield u
      q.update(user)
    }

    def remove(uid: String)(implicit session: Session) = {
      val q = for(u <- Users if u.id === uid) yield u
      q.delete
    }

    def check(email: String, password: String)(implicit sessions: Session): Boolean = {
      val q = for(u <- Users if (u.email === email && u.password === password)) yield u
      q.list().headOption map { user =>
        true
      } getOrElse false
    }
  }

}