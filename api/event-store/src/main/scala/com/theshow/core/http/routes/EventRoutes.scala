package com.theshow.core.http.routes

import cats.data.Kleisli
import cats.effect.kernel.Async
import org.http4s.{HttpApp, HttpRoutes, Request, Response}
import org.http4s.dsl.Http4sDsl
import cats.implicits._
import com.theshow.core.domain.{Password, SignInDomain, UserName}
import com.theshow.core.game.db.user.User
import com.theshow.core.http.routes.Protocol.{ErrorMessage, LobbiesCustom, LobbyCustom, SignInUser, UserCustom}
import org.http4s.server.middleware.ErrorHandling
import org.http4s.server.websocket.WebSocketBuilder
import org.http4s.websocket.WebSocketFrame
//import org.http4s.server.Router
//import org.http4s.circe.{jsonEncoder, jsonOf}
//import com.theshow.core.utils.Token
//import io.circe.Json
//import io.circe.parser._
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.implicits._

object Protocol {
  final case class SignInUser(name: String, password: String)

  final case class UserCustom(name: String, balance: Double, token: String)
  final case class ErrorMessage(message: String)
  final case class LobbiesCustom(rooms: List[LobbyCustom])
  final case class LobbyCustom(id: Number, room_name: String)
}

case class EventRoutes[F[_]: Async]() extends Http4sDsl[F]
{
  private val signInRoutes = HttpRoutes.of[F] {
    case GET -> Root / "users" => {
      Ok(User.findAllUsers)
    }
    case req@POST -> Root / "signin" => {
      req.as[SignInUser].flatMap { user =>
        val newUser = UserCustom(name = user.name, balance = 1000, token = "132423543")
        user match {
          case _ if (user.name == "admin" && user.password == "111") => Ok(newUser)
          case _ => BadRequest("User is not found")
        }
      }
    }
  }

//  private val lobbyRoutes = HttpRoutes.of[F] {
//    case GET -> Root / "lobbies" => Ok(LobbiesCustom(List(LobbyCustom(0, "room_1"), LobbyCustom(1, "room_2"), LobbyCustom(2, "room_3"))))
//    case GET -> Root / "lobby" / lobby_id =>
//  }

  private[http] val httpApp = ErrorHandling {
    Seq(
      signInRoutes
    ).reduce(_ <+> _)
  }.orNotFound

  def getHttpApp(): Kleisli[F, Request[F], Response[F]] = httpApp

}
