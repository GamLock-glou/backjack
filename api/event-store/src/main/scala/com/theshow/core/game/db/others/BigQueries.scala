package com.theshow.core.game.db.others

import cats.effect.IO
import com.theshow.core.game.db.bets.Bet
import com.theshow.core.game.db.lobby.Lobby
import com.theshow.core.game.db.lub.LUB
import com.theshow.core.game.db.user.User
import com.theshow.core.http.routes.UserRequest

object BigQueries {
  def connectToLobby(user_id: Int, lobby_id: Int) = {
    for {
      checkInLobby <- LUB.checkUserInLUB(user_id)
      _ <- checkInLobby match {
        case Some(value) => for {
          update <- Lobby.updateLobby(value.lobby_id, -1)
          delete <- LUB.deleteLUB(value.id)
          create <- LUB.createWithLobbyLUB(user_id, lobby_id)
        } yield update + delete + create
        case None => LUB.createWithLobbyLUB(user_id, lobby_id)
      }
      _ <- Lobby.updateLobby(lobby_id)
      lobby <- Lobby.getLobby(lobby_id)
    } yield lobby
  }

  // TODO: change the type option[UserRequest] to Either[ErrorMessage, UserRequest]
  def disconnectFromLobby(user_id: Int, token: String): IO[Option[UserRequest]] = {
    for {
      checkLUB <- LUB.checkUserInLUB(user_id)
      delete <- checkLUB match {
        case Some(value) => for {
          _ <- Lobby.updateLobby(value.lobby_id, -1)
          _ <- value.bet_id match {
            case Some(v) => Bet.updateIsWinInBet(v, 0)
            case None => IO.none
          }
          _ <- LUB.deleteLUB(value.id)
          user <- User.getUserByToken(token)
        } yield user
        case None => IO(None)
      }
    } yield delete
  }
}
