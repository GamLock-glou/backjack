package com.theshow.core.domain

import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.deriveConfiguredCodec

case class Event(
                userName: UserName,
                message: Message,
                mood: Mood,
                whatIPlanToAchieve: WhatIPlanToAchieve,
                regrets: Option[Regrets]
                )

object Event {
  implicit val config: Configuration = Configuration.default
  implicit val codec = deriveConfiguredCodec[Event]
}