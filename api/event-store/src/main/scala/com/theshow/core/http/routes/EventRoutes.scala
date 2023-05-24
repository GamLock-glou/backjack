package com.theshow.core.http.routes

import cats.effect.Sync
import cats.effect.kernel.Ref
import cats.syntax.all._
import com.theshow.core.game.db.bets.Bet
import com.theshow.core.game.db.lobby.Lobby
import com.theshow.core.game.db.others.BigQueries
import org.http4s.{HttpRoutes, Request, Response}
import org.http4s.dsl.Http4sDsl
import com.theshow.core.game.db.user.User
import com.theshow.core.game.logic.Game
import com.theshow.core.utils.HeaderToken
import com.theshow.core.utils.HeaderToken.getToken
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec._
import cats.effect.IO


case class EventRoutes() extends Http4sDsl[IO] {

//  val games =

  private def pureToken(req: Request[IO])(callback: UserRequest =>IO[Response[IO]]): IO[Response[IO]] = {
    getToken(HeaderToken(req)) match {
      case Some(token) => User.getUserByToken(token).flatMap {
        case Some(value) => callback(value)
        case None => BadRequest(Left(ErrorMessage("The user was not found by token")))
      }
      case None => BadRequest(Left(ErrorMessage("There is no token in the header")))
    }
  }

  val signInRoutes = HttpRoutes.of[IO] {
    case req@GET -> Root / "signin" => {
      pureToken(req)(value => Ok(value))
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
    }
  }


  val lobbyRoutes = HttpRoutes.of[IO] {
    case GET -> Root / "lobbies" => Ok(Lobby.getLobbies())
    case req@GET -> Root / "lobby" / IntVar(lobby_id) => {
      pureToken(req) {
        case v => Lobby.getLobby(lobby_id).flatMap {
          case Some(value) if (value.limit_users > value.users_count) => Ok(BigQueries.connectToLobby(v.id, lobby_id))
          case Some(_) => BadRequest(ErrorMessage("The lobby is crowded"))
          case None => BadRequest(ErrorMessage("Lobby not found"))
        }
      }
    }
    case req@DELETE -> Root / "lobby" => {
      def callback(user: UserRequest) = user match {
        case value => Ok(BigQueries.disconnectFromLobby(value.id, value.token))
      }
      pureToken(req)(callback)
    }

  }

  val betsRouters = HttpRoutes.of[IO] {
    case req@POST -> Root / "bet" =>
      req.as[BetRequest].flatMap { body => {
        pureToken(req) {
          case value if (value.money >= body.bet) => Bet.placeABet(value.id, body.lobby_id, body.bet).flatMap {
            case Some(value) => Ok(value)
            case None => BadRequest(ErrorMessage("Something went wrong"))
          }
        }
      }
      }
  }

  def gameRouters(ref: Ref[IO, List[Game]]) = HttpRoutes.of[IO] {
    case req@POST -> Root / "game" / "start" => req.as[StartGame].flatMap { body => {
      val game: Game = Game(body.lobbyId)
      for {
        _ <- ref.update((a: List[Game]) => game :: a)
        request <- Ok("request")
      } yield request

    }

    }
    case req@POST -> Root / "game" / "stand" => Ok("stand")
  }
}
