package com.theshow.core.http.routes

final case class UserResponse(name: String, password: String)

final case class UserRequest(id: Int, name: String, money: BigDecimal, token: String)

final case class ErrorMessage(message: String)

final case class LobbiesCustom(rooms: List[LobbyCustom])

final case class LobbyCustom(id: Number, room_name: String)
