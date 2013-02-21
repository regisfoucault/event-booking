package controllers

import play.api._
import play.api.mvc._

import play.api.data._
import play.api.data.Forms._

case class LoginData(
  pseudo: String,
  password: String
)

object Application extends Controller {


  // -- Authentification

  /*val loginForm = Form(
    tuple(
      "pseudo" -> text,
      "password" -> text
    ) verifying ("Invalid email or password", result => result match {
      case (pseudo, password) => check(pseudo, password)
    })
  )*/

  /**
   * Login page.
   */
  //def login = Action {
    //Ok(views.html.login(controllers.Users.userForm))
    //Ok(views.html.app(None))
  //}

  /**
   * Handle login form submission.
   */
  /*def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest("Mauvaise authentification"),
      user => Redirect(routes.Users.main).withSession(Security.username -> user._1)
    )
  }*/

  /**
   * Logout and clean the session.
   */
  def logout = Action {
    Redirect(routes.Users.login("")).withNewSession
  }

  /*def check(username: String, password: String) = {
    (username == "clairefoucault" && password == "titcoeur") || (username == "host" && password == "host")
  }*/

  /*def index = Action {
    Redirect(routes.Users.main)
  }*/

  // -- Javascript routing

  def javascriptRoutes = Action { implicit request =>
    import routes.javascript._
    Ok(
      Routes.javascriptRouter("jsRoutes")(
        Events.book,
        Events.unBook,
        Events.deleteEvent
      )
    ).as("text/javascript")
  }

}

/**
 * Provide security features
 */
trait Secured {
  
  /**
   * Retrieve the connected user email.
   */
  private def username(request: RequestHeader) = request.session.get("username")

  /**
   * Redirect to login if the user in not authorized.
   */
  private def onUnauthorized(request: RequestHeader) = {
    Results.Redirect(routes.Users.login(""))
  }

  // --

  /**
   * Action for authenticated users.
   */
  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) { user =>
    Action(request => f(user)(request))
  }

}