package controllers

import models._
import play.api._
import play.api.mvc._
import java.util.UUID
import play.api.libs.json._
import slick.session.Session
import play.api.data._
import play.api.data.Forms._

case class UserData(
  status: Option[String],
  prename: String,
  lastname: String,
  email: String,
  password: String,
  phone: String
)

case class SignInData(
  email: String,
  password: String,
  eid: String
)

object Users extends Controller with Secured {

  implicit val session = AppDB.database.withSession {
    implicit session: Session => session
  }

  /*def main = IsAuthenticated { username => _ =>
    Ok(views.html.app(AppDB.dal.Users.getByUsername(username)))
  }*/

  def index() = Action(
    Ok(views.html.index())
  )

  def open(uid: String) = Action{ request =>//IsAuthenticated { username => _ =>
    //request.session.get("username") map { username =>
      (for {
        user <- AppDB.dal.Users.get(uid)
        events <- Some(AppDB.dal.Events.getByUser(uid)) // liste des events créés par cet admin
        userevents <- Some(AppDB.dal.UserEvents.listInscrits(events)) // liste des userid/eventid des inscrits à ces events
        inscrits <- Some(AppDB.dal.UserEvents.listUsers(userevents)) // récupération des user de ces couples userid/eventid
      } yield {
        Ok(views.html.openUser(Some(user), events, userevents, inscrits))
      }) getOrElse BadRequest
    //} getOrElse Ok(views.html.openUser(None, events, inscrits))
  }

  def login(eid: String) = Action {
    Ok(views.html.login(signUpForm, signInForm.fill(SignInData(
      email = "",
      password = "",
      eid = eid
      )
    )))
  }

  def signIn = Action { implicit request =>
    signInForm.bindFromRequest.fold(
      formWithErrors => {
        println(formWithErrors)
        BadRequest(views.html.login(signUpForm, formWithErrors))
      },
      user => {
        AppDB.dal.Users.getByUsername(user.email) map { user2 =>
          if (user.eid != "") {
            Redirect(routes.Visits.open(user.eid)).withSession(Security.username -> user.email)
          } else {
            Redirect(routes.Users.open(user2.id)).withSession(Security.username -> user.email)  
          }
        } getOrElse Redirect("routes.Users.login()")
        //Redirect(routes.Users.main(AppDB.dal.Users.getByUsername(user._1).id)).withSession(Security.username -> user._1)
      }
    )
  }

  def check(email: String, password: String) = {
    AppDB.dal.Users.check(email, password)
  }

  def signUp = Action { implicit request =>
    signUpForm.bindFromRequest.fold(
      errors => {
        println(errors)
        BadRequest(views.html.login(errors, signInForm))
      },
      myForm => {
        val user = new User(
          id = UUID.randomUUID().toString,
          creationDate = new java.sql.Timestamp(new java.util.Date().getTime()),
          updateDate = new java.sql.Timestamp(new java.util.Date().getTime()),
          status = "visitor",
          prename = myForm.prename,
          lastname = myForm.lastname,
          email = myForm.email,
          password = myForm.password,
          phone = myForm.phone
        )
        AppDB.dal.Users.add(user)
        Redirect(routes.Users.open(user.id)).withSession(Security.username -> user.email)
      }
    )
  }

  /*def update(uid: String) = IsAuthenticated { username => implicit request =>
    (for {
      user <- AppDB.dal.Users.get(uid)
    } yield {
      userForm.bindFromRequest.fold(
        errors => {
          BadRequest(views.html.editUser(errors, user))
        },
        userForm => {
          val user2 = user.copy(
            id = user.id,
            updateDate = new java.sql.Timestamp(new java.util.Date().getTime()),
            prename = userForm.prename,
            lastname = userForm.lastname,
            email = userForm.email,
            maidenName = userForm.maidenName,
            birthDate = userForm.birthDate,
            address = userForm.address,
            postalCode = userForm.postalCode,
            city = userForm.city,
            job = userForm.job,
            json = Json.stringify(Json.toJson(
              Map(
                "doctor" -> userForm.medicine.doctor,
                "gynecologist" -> userForm.medicine.gynecologist,
                "socialSupport" -> userForm.medicine.socialSupport,
                "secuNumber" -> userForm.medicine.secuNumber,
                "nationality" -> userForm.medicine.nationality,
                "relationshipStatus" -> userForm.medicine.relationshipStatus,
                "size" -> userForm.medicine.size,
                "preWeight" -> userForm.medicine.preWeight,
                "tobacco" -> userForm.medicine.tobacco.getOrElse(""),
                "cigarettePerDayBefore" -> userForm.medicine.cigarettePerDayBefore,
                "cigarettePerDayAfter" -> userForm.medicine.cigarettePerDayAfter,
                "bloodGroup" -> userForm.medicine.bloodGroup,
                "rhesus" -> userForm.medicine.rhesus,
                "fatherFirstName" -> userForm.father.fatherFirstName,
                "fatherLastName" -> userForm.father.fatherLastName,
                "fatherBirthDate" -> userForm.father.fatherBirthDate,
                "fatherBloodGroup" -> userForm.father.fatherBloodGroup,
                "fatherRhesus" -> userForm.father.fatherRhesus,
                "fatherJob" -> userForm.father.fatherJob,
                "antMedicaux" -> userForm.antecedents.antMedicaux,
                "antChirurgicaux"-> userForm.antecedents.antChirurgicaux,
                "antFamiliaux"-> userForm.antecedents.antFamiliaux,
                "allergiesRadio"-> userForm.antecedents.allergiesRadio.getOrElse(""),
                "allergiesText"-> userForm.antecedents.allergiesText,
                "antTransRadio"-> userForm.antecedents.antTransRadio.getOrElse(""),
                "antTransText"-> userForm.antecedents.antTransText,
                "antGynCyclesRadio"-> userForm.antecedents.antGynCyclesRadio.getOrElse(""),
                "antGynCyclesText"-> userForm.antecedents.antGynCyclesText,
                "antGynContraception"-> userForm.antecedents.antGynContraception,
                "antGynFrottiDate"-> userForm.antecedents.antGynFrottiDate,
                "antGynFrottiText"-> userForm.antecedents.antGynFrottiText,
                "antGynRqs"-> userForm.antecedents.antGynRqs,
                "antGynObsGestite" -> userForm.antecedents.antGynObsGestite,
                "antGynObsParite" -> userForm.antecedents.antGynObsParite
              )
            ))
          )
          AppDB.dal.Users.save(user2)
          Redirect( routes.Users.openUser(uid) )
        }
      )
    }) getOrElse BadRequest
  }

  */

  /*def signInForm = Form(
    tuple(
      "email" -> email,
      "password" -> text,
      "eid" -> text
    ) verifying ("Invalid email or password", result => result match {
      case (email, password, eid) => check(email, password)
    })
  )*/

  

  def signInForm = Form(
    mapping(
      "email" -> email,
      "password" -> text,
      "eid" -> text
    )(SignInData.apply)(SignInData.unapply)
    verifying ("Invalid email or password", result => result match {
      case SignInData(email, password, _) => check(email, password)
    })
  )

  def signUpForm = Form(
    mapping(
      "status" -> optional(text),
      "prename" -> nonEmptyText,
      "lastname" -> nonEmptyText,
      "email" -> text,
      "password" -> text,
      "phone" -> text
    )(UserData.apply)(UserData.unapply)
  )
}