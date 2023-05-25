package com.evolition.core.http.routes

import cats.effect.Sync
import cats.effect.kernel.Ref
import cats.syntax.all._
import org.http4s.{HttpRoutes, Request, Response}
import org.http4s.dsl.Http4sDsl
import com.evolition.core.utils.HeaderToken.getToken
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec._
import cats.effect.IO
import com.evolition.core.game.db.bets.Bet
import com.evolition.core.game.db.lobby.Lobby
import com.evolition.core.game.db.others.BigQueries
import com.evolition.core.game.db.user.User
import com.evolition.core.game.logic.Game
import Game.{commandHit, commandStand, createGame}
import com.evolition.core.game.db.lub.LUB
import com.evolition.core.utils.{HeaderToken, Other, RefOptions}

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
          case Some(value) if (value.limitUsers > value.users_count) => Ok(BigQueries.connectToLobby(v.id, lobby_id))
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
          case value if (value.money >= body.bet) => Bet.placeABet(value.id, body.lobbyId, body.bet).flatMap {
            case Some(value) => Ok(value)
            case None => BadRequest(ErrorMessage("Something went wrong"))
          }
          case _ => BadRequest(ErrorMessage("The bid exceeds the amount of funds the user has"))
        }
      }
      }
  }

  def gameRouters(ref: Ref[IO, List[Game]]) = HttpRoutes.of[IO] {
    case req@GET -> Root / "game" / "start" => pureToken(req) {
        case value => for {
          lub <- LUB.getLUB(value.id)
          response <- lub match {
            case Some(lub) => for {
              gameOption <- RefOptions.getGameFromRef(ref, value.id, lub)
              response <- gameOption match {
                case Some(_) => BadRequest(ErrorMessage("Game is started"))
                case None => for {
                  r <- Other.getResponseStartGame(value, ref)
                  response <- r match {
                    case Right(value) => Ok(value)
                    case Left(error) => Ok(error)
                  }
                } yield response
              }
            } yield response
            case None => BadRequest(ErrorMessage("Something error"))
          }
        } yield response
      }
    case req@GET -> Root / "game" / "hit" => pureToken(req) {
      case value => {
        for {
          lub <- LUB.getLUB(value.id)
          response <- lub match {
            case Some(lub) => {
              for {
                gameOption <- RefOptions.getGameFromRef(ref, value.id, lub)
                game <- gameOption match {
                  case Some(game) => for {
                    r <- Other.getResponseHit(game, value, ref, lub)
                    response <- r match {
                      case Right(value) => Ok(value)
                      case Left(error) => BadRequest(error)
                    }
                  } yield response
                  case None => BadRequest(ErrorMessage("not found game"))
                }
              } yield game
            }
            case None => BadRequest(ErrorMessage("you are not in the lobby"))
          }
        } yield response
      }
    }
    case req@GET -> Root / "game" / "stand" => pureToken(req) {
      case value => {
        for {
          lub <- LUB.getLUB(value.id)
          response <- lub match {
            case Some(lub) => {
              for {
                gameOption <- RefOptions.getGameFromRef(ref, value.id, lub)
                game <- gameOption match {
                  case Some(game) => for {
                    g <- commandStand(game, value.id)
                    response <- g match {
                      case Some(v) => for {
                        user <- User.getUserByToken(value.token)
                        _ <- ref.update(_.filter(_.lobbyId != lub.lobby_id))
                        response <- Ok(FinishGame(user, v))
                      } yield response
                      case None => BadRequest(ErrorMessage("Something error"))
                    }
                  } yield response
                  case None => BadRequest(ErrorMessage("not found game"))
                }
              } yield game
            }
            case None => BadRequest(ErrorMessage("you are not in the lobby"))
          }
        } yield response
      }
    }
  }
}
