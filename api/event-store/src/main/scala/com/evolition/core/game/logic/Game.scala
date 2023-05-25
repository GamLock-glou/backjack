package com.evolition.core.game.logic

import cats.effect.IO
import cats.implicits.catsSyntaxApply
import com.evolition.core.game.db.bets.Bet
import com.evolition.core.game.db.lub.LUB
import com.evolition.core.game.db.others.{BigQueries, LobbyAndBet}
import com.evolition.core.game.logic.Desk.getCard
import com.evolition.core.http.routes.CardRequest
import io.circe.{Encoder, Json}
import cats.effect.std.Random


sealed trait Suit {
  def getSymbols(): String
}
case object Heart extends Suit {
  override def getSymbols(): String = "♥"
}
case object Diamond extends Suit {
  override def getSymbols(): String = "♦"
}
case object Spade extends Suit {
  override def getSymbols(): String = "♠"
}
case object Club extends Suit {
  override def getSymbols(): String = "♣"
}

sealed abstract class Rank(val value: Int, val symbol: String)
case object King extends Rank(10, "K")
case object Queen extends Rank(10, "Q")
case object Jack extends Rank(10, "J")
case object Ten extends Rank(10, "10")
case object Nine extends Rank(9, "9")
case object Eight extends Rank(8, "8")
case object Seven extends Rank(7, "7")
case object Six extends Rank(6, "6")
case object Five extends Rank(5, "5")
case object Four extends Rank(4, "4")
case object Three extends Rank(3, "3")
case object Two extends Rank(2, "2")
case object Ace extends Rank(1, "A")
final case class Card(suit: Suit, rank: Rank)

object Card {
  implicit val encodeCard: Encoder[Card] = new Encoder[Card] {
    final def apply(a: Card): Json = Json.obj(
      ("suit", Json.fromString(a.suit.getSymbols())),
      ("rank", Json.fromString(a.rank.symbol))
    )
  }
}

case object Desk {

//  def createCards() = Random.shuffle(initDeck)
//  def getCard() = createCards().head
  def getCard(): IO[Card] = Random.scalaUtilRandom[IO].flatMap { implicit rnd =>
    for {
      cards <- Random[IO].shuffleList(initDeck)
      _ <- IO.println(cards.head)
    } yield {
      cards.head
    }
  }
  private def initDeck =
    for {
      suit <- List(Heart, Diamond, Spade, Club)
      rank <- List(King, Queen, Jack, Ten, Nine, Eight, Seven, Six, Five, Four, Three, Two, Ace)
    }
    yield Card(suit, rank)
}


object Hand {
  private val winningValue = 21

  def dillerGetCard(diller: List[Card]): IO[List[Card]] =
    rankCards(diller) < 17 || specialValue(diller) < 17 match {
    case true => for {
      card <- Desk.getCard ()
      list <- dillerGetCard(card :: diller)
    } yield list
    case false => IO(diller)
  }

  def isBlackJack(cards: List[Card]) = rankCards(cards) == winningValue || specialValue(cards) == winningValue

  def isLose(cards: List[Card]) = rankCards(cards) > winningValue || specialValue(cards) > winningValue

  def isDouble(cards: List[Card]) = {
    val card1 = cards.head
    val card2 = cards.tail.head
    card1.rank.value == card2.rank.value
  }

  def rankCards(cards: List[Card]) = cards.map(_.rank.value).sum

  def isAce(cards: List[Card]) = cards.exists(_.rank == Ace)

  def specialValue(cards: List[Card]) = isAce(cards) match {
    case true => rankCards(cards) + 10
    case false => rankCards(cards)
  }

  def winsOver(userHand: List[Card], otherHand: List[Card]) = {
    val dillerBestValue = List(rankCards(otherHand), specialValue(otherHand)).filter(v => v <= winningValue).max
    val bestValue = List(rankCards(userHand), specialValue(userHand)).filter(v => v <= winningValue).max
    bestValue >= dillerBestValue
  }

}
final case class Game(lobbyId: Int, user: List[Card], diller: List[Card], isOverGame: Boolean, isWin: Option[Boolean])

object Game {

  val desk = Desk
  val hand = Hand

  def apply(lobbyId: Int): IO[Game] = {
    for {
      card <- desk.getCard()
      card1 <- desk.getCard()
      card2 <- desk.getCard()
    } yield Game(lobbyId, List(card, card1), List(card2), false, None)

  }

  def gameOver(game: Game, value: LobbyAndBet, userId: Int)= {
    for {
      newDiller <- Hand.dillerGetCard(game.diller)
      g <- Hand.winsOver(game.user, newDiller) match {
        case true => for {
          _ <- BigQueries.gameOverQuery(value, userId, true)
        } yield game.copy(diller = newDiller, isOverGame = true, isWin = Option(true))
        case false => for {
          _ <- LUB.updateBetEqNull(userId)
        } yield game.copy(diller = newDiller, isOverGame = true, isWin = Option(false) )
      }
    } yield g
  }

  def createGame(userId: Int): IO[Option[Game]] = {
    for {
      v <- BigQueries.getLobbyIdAndBet(userId)
      game <- v match {
        case Some(value) => for {
          gVal <- Game(value.lobbyId)
          game <- Hand.isBlackJack(gVal.user) match {
            case true => gameOver(gVal, value, userId)
            case false => IO(gVal)
          }
        } yield Some(game)
        case None => IO.none
      }
    } yield game
  }

  def commandHit(game: Game, userId: Int) = {
    for {
      card <- desk.getCard()
      newGame = game.copy(user = card :: game.user)
      v <- BigQueries.getLobbyIdAndBet(userId)
      game <- v match {
        case Some(value) => for {
          game <- Hand.isBlackJack(newGame.user) match {
            case true => gameOver(newGame, value, userId)
            case false => Hand.isLose(newGame.user) match {
              case true => IO(newGame.copy(isWin = Some(false), isOverGame = true))
              case false => IO(newGame)
            }
          }

        } yield Some(game)
        case None => IO.none
      }
    } yield game
  }

  def commandStand(game: Game, userId: Int) = {
    for {
      v <- BigQueries.getLobbyIdAndBet(userId)
      game <- v match {
        case Some(value) => for {
          game <- gameOver(game, value, userId)

        } yield Some(game)
        case None => IO.none
      }
    } yield game
  }
}
