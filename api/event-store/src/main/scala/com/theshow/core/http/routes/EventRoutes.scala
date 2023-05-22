package com.theshow.core.http.routes

import com.theshow.core.game.db.bets.Bet
import com.theshow.core.game.db.lobby.Lobby
import org.http4s.{HttpRoutes, Request, Response}
import org.http4s.dsl.Http4sDsl
import com.theshow.core.game.db.user.User
import com.theshow.core.game.logic.{LogicGame}
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

  private def pureToken(req: Request[IO]): IO[Either[ErrorMessage, UserRequest]] = {
    getToken(HeaderToken(req)) match {
      case Some(token) => User.getUserByToken(token).flatMap {
        case Some(value) => IO(Right(value))
        case None => IO(Left(ErrorMessage("The user was not found by token")))
      }
      case None => IO(Left(ErrorMessage("There is no token in the header")))
    }
  }

  val signInRoutes = HttpRoutes.of[IO] {
    case req@GET -> Root / "signin" => {
      for {
        pt <- pureToken(req)
        v <- pt match {
          case Right(value) => Ok(value)
          case Left(error) => BadRequest(error)
        }
      } yield v
    }
    case req@POST -> Root / "signin" => {
      for {
        user <- req.as[UserResponse]
        data <- User.getUser(user.name, user.password)
        response <- data match {
          case Some(value) => Ok(value)
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


  val lobbyRoutes = HttpRoutes.of[IO] {
    case GET -> Root / "lobbies" => Ok(Lobby.getLobbies())
    case req@GET  -> Root / "lobby" / IntVar(lobby_id) => {
      for {
        pt <- pureToken(req)
        v <- pt match {
          case Right(user) => Lobby.getLobby(lobby_id).flatMap {
            case Some(value) if(value.limit_users > value.users_count) => Ok(LogicGame.connectToLobby(user.id, lobby_id))
            case Some(_) => BadRequest(ErrorMessage("The lobby is crowded"))
            case None => BadRequest(ErrorMessage("Lobby not found"))
          }
          case Left(error) => BadRequest(error)
        }
      } yield v
    }
    case req@DELETE -> Root / "lobby" => {
      for {
        pt <- pureToken(req)
        v <- pt match {
          case Right(value) => Ok(LogicGame.disconnectFromLobby(value.id, value.token))
          case Left(error) => BadRequest(error)
        }
      } yield v
    }

  }

  val betsRouters = HttpRoutes.of[IO] {
    case req@POST -> Root / "bet" => {
      req.as[BetRequest].flatMap { body => {
        for {
          pt <- pureToken(req)
          v <- pt match {
            case Right(user) if(user.money >= body.bet) => Bet.placeABet(user.id, body.lobby_id, body.bet).flatMap {
              case Some(value) => Ok(value)
              case None => BadRequest(ErrorMessage("Something went wrong"))
            }
            case Right(_) => BadRequest(ErrorMessage("The bid is more than the user's balance"))
            case Left(error) => BadRequest(error)
          }
        } yield v
        }
      }
    }
  }

}
