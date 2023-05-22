package com.theshow.core.http.routes

final case class UserResponse(name: String, password: String)

final case class UserRequest(id: Int, name: String, money: Double, token: String)

final case class ErrorMessage(message: String)

final case class LobbyCustom(id: Int, room_name: String, limit_users: Int, users_count: Int)

final case class LUBRequest(user_id: Int, lobby_id: Int)

final case class BetRequest(lobby_id: Int, bet: Double)

