package models

import slick.driver.ExtendedProfile


class DAL(override val profile: ExtendedProfile) extends UserComponent with EventComponent with UserEventComponent with
  Profile {

  import profile.simple._

  def create(implicit session: Session): Unit = {
    //UserEvents.ddl.drop
    //UserEvents.ddl.create
    //Events.ddl.create
    //Users.ddl.create
  }
}
