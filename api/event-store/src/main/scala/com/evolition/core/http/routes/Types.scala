package com.evolition.core.http.routes

import com.evolition.core.game.logic.Game

final case class UserResponse(name: String, password: String)

final case class UserRequest(id: Int, name: String, money: Double, token: String)

final case class ErrorMessage(message: String)
final case class LobbyCustom(id: Int, roomName: String, limitUsers: Int, users_count: Int)
final case class BetRequest(lobbyId: Int, bet: Double)
final case class CardRequest(suit: String, rank: String)
final case class FinishGame(user: Option[UserRequest], game: Game)


