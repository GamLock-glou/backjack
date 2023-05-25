package com.evolition.core.utils

import cats.effect.IO
import cats.effect.kernel.Ref
import com.evolition.core.game.db.lub.LUB
import com.evolition.core.game.logic.Game

object RefOptions {
 def getGameFromRef(ref: Ref[IO, List[Game]], userId: Int, lub: LUB) = for {
   cg <- ref.get
   gameOption = cg.find(v => v.lobbyId == lub.lobby_id)
 } yield gameOption
}
