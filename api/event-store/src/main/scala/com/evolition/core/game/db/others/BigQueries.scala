package com.evolition.core.game.db.others

import cats.effect.IO
import com.evolition.core.game.db.bets.Bet
import com.evolition.core.game.db.config.Config
import com.evolition.core.game.db.lobby.Lobby
import com.evolition.core.game.db.lub.LUB
import com.evolition.core.game.db.user.User
import com.evolition.core.http.routes.UserRequest
import doobie.{HC, HPS}
import doobie.implicits._

final case class LobbyAndBet(lobbyId: Int, betId: Int, bet: Double)
object BigQueries {

  def getLobbyIdAndBet(userId: Int): IO[Option[LobbyAndBet]] = {
    val queryString = "select lub.lobby_id, lub.bet_id, b.bet from lobbies_users_bets lub join bets b on lub.user_id = ? and b.id = lub.bet_id;"
    HC.stream[LobbyAndBet](
      queryString,
      HPS.set(userId),
      100
    ).compile.toList.map(_.headOption).transact(Config().xa).debug().debug()
  }

  def gameOverQuery(values: LobbyAndBet, userId: Int, isWin: Boolean) = {
    for {
      _ <- LUB.updateBetEqNull(userId)
      _ <- Bet.updateIsWinInBet(values.betId, isWin)
      _ <- User.updateUserMoney(userId, values.bet * 2)
    } yield ()
  }

  def connectToLobby(userId: Int, lobbyId: Int) = {
    for {
      checkInLobby <- LUB.getLUB(userId)
      _ <- checkInLobby match {
        case Some(value) => for {
          update <- Lobby.updateLobby(value.lobby_id, -1)
          delete <- LUB.deleteLUB(value.id)
          create <- LUB.createWithLobbyLUB(userId, lobbyId)
        } yield update + delete + create
        case None => LUB.createWithLobbyLUB(userId, lobbyId)
      }
      _ <- Lobby.updateLobby(lobbyId)
      lobby <- Lobby.getLobby(lobbyId)
    } yield lobby
  }

//  def endGame(userId: Int, bet: Double) = {
//    for {
//      lIdAndBet <- getLobbyIdAndBet(userId)
//    } yield
//  }

  // TODO: change the type option[UserRequest] to Either[ErrorMessage, UserRequest]
  def disconnectFromLobby(userId: Int, token: String): IO[Option[UserRequest]] = {
    for {
      checkLUB <- LUB.getLUB(userId)
      delete <- checkLUB match {
        case Some(value) => for {
          _ <- Lobby.updateLobby(value.lobby_id, -1)
          _ <- value.bet_id match {
            case Some(v) => Bet.updateIsWinInBet(v, false)
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
