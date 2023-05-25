package com.evolition.core.utils

import cats.effect.IO
import cats.effect.kernel.Ref
import com.evolition.core.game.db.lub.LUB
import com.evolition.core.game.db.user.User
import com.evolition.core.game.logic.Game
import com.evolition.core.game.logic.Game.{commandHit, createGame}
import com.evolition.core.http.routes.{ErrorMessage, FinishGame, UserRequest}


object Other {
 def getResponseStartGame(user: UserRequest, ref: Ref[IO, List[Game]]): IO[Either[ErrorMessage, FinishGame]] = {
   for {
     g <- createGame(user.id)
     response <- g match {
       case Some(v) => v.isWin match {
         case Some(_) => {
           for {
             user <- User.getUserByToken(user.token)
             response <- IO(Right(FinishGame(user, v)))
           } yield response
         }
         case None => for {
           _ <- ref.update(a => v :: a)
           resp <- IO(Right(FinishGame(Some(user), v)))
         } yield resp
       }
       case None => IO(Left(ErrorMessage("Something error")))
     }
   } yield response
 }

  def getResponseHit(game: Game, user: UserRequest, ref: Ref[IO, List[Game]], lub: LUB): IO[Either[ErrorMessage, FinishGame]] = for {
    g <- commandHit(game, user.id)
    response <- g match {
      case Some(v) => v.isWin match {
        case Some(_) => {
          for {
            user <- User.getUserByToken(user.token)
            _ <- ref.update(_.filter(_.lobbyId != lub.lobby_id))
            response <- IO(Right(FinishGame(user, v)))
          } yield response
        }
        case None => for {
          _ <- ref.update(v :: _.filter(_.lobbyId != lub.lobby_id))
          resp <- IO(Right(FinishGame(Some(user), v)))
        } yield resp
      }
      case None => IO(Left(ErrorMessage("Something error")))
    }
  } yield response
}
