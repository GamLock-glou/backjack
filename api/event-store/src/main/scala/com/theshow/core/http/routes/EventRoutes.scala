package com.theshow.core.http.routes

import org.http4s.{HttpRoutes}
import org.http4s.dsl.Http4sDsl
import com.theshow.core.game.db.user.User
import com.theshow.core.utils.HeaderToken
import com.theshow.core.utils.HeaderToken.getToken
import org.http4s.server.middleware.ErrorHandling
import org.http4s.server.websocket.WebSocketBuilder
import org.http4s.websocket.WebSocketFrame
//import org.http4s.server.Router
//import org.http4s.circe.{jsonEncoder, jsonOf}
//import com.theshow.core.utils.Token
//import io.circe.Json
//import io.circe.parser._
//import cats.data.Kleisli
//import cats.effect.kernel.Async
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.implicits._
import cats.effect.IO


case class EventRoutes() extends Http4sDsl[IO] {
  val signInRoutes = HttpRoutes.of[IO] {
    case req@GET -> Root / "signin" => {
      getToken(HeaderToken(req)) match {
        case Some(token) => User.getUserByToken(token).flatMap(_ match {
          case Some(value) => Ok(value)
          case None => BadRequest(ErrorMessage("The user was not found by token"))
        })
        case None => BadRequest(ErrorMessage("There is no token in the header"))
      }
    }
    case req@POST -> Root / "signin" => {
      for {
        user <- req.as[UserResponse]
        data <- User.getUser(user.name, user.password)
        response <- data match {
          case Some(value) => Ok.apply(value)
          case None => BadRequest(ErrorMessage("User is not find"))
        }
      } yield response

//      req.as[UserResponse].flatMap { user =>
//        User.getUser(user.name, user.password).flatMap(_ match {
//          case Some(value) => Ok(value)
//          case None => BadRequest(ErrorMessage("User is not find"))
//        })
//      }
    }
  }

    private val lobbyRoutes = HttpRoutes.of[IO] {
      case GET -> Root / "lobbies" => ???
//      case GET -> Root / "lobby" / lobby_id =>
    }

}
