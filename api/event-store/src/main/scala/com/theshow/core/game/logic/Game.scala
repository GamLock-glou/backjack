package com.theshow.core.game.logic

import cats.implicits.catsSyntaxApply

import scala.util.Random
import com.theshow.core.http.routes.CardRequest


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

case object Desk {
  def cards() = List() ++ Random.shuffle(initDeck)
  def getCard(): Card = {
    cards.head
  }
  private def initDeck =
    for {
      suit <- List(Heart, Diamond, Spade, Club)
      rank <- List(King, Queen, Jack, Ten, Nine, Eight, Seven, Six, Five, Four, Three, Two, Ace)
    }
    yield Card(suit, rank)
}

final case class Options(
                          isHit: Boolean = false,
                          isStand: Boolean = false,
                          isSplit: Boolean = false,
                          isDouble: Boolean = false,
                          isTwentyOne: Boolean = false,
                          isBlackJack: Boolean = false,
                          isWin: Int = -1
                        )

final case class Hand(
                       cards: List[Card],
                       options: Options
                     )


object Hand {
  private val winningValue = 21

  def dillerGetCard(diller: List[Card]): List[Card] =
    rankCards(diller) < 17 || specialValue(diller) < 17 match {
    case true => dillerGetCard (diller ::: Desk.getCard () :: Nil)
    case false => diller
  }

  def apply(cardsUser: List[Card]): Hand = {
    (isBlackJack(cardsUser), isDouble(cardsUser)) match {
      case (true, false) => {
        Hand(cards = cardsUser, Options(isBlackJack = true))
      }
      case (false, true) => Hand(
        cards = cardsUser,
        Options(
          isDouble = true,
          isStand = true,
          isSplit = true
        )
      )
      case (false, false) => Hand(
        cards = cardsUser,
        Options(
          isStand = true,
          isSplit = true
        )
      )
      // TODO: it can't be
      case (true, true) => Hand(cards = cardsUser, Options(isBlackJack = true))
    }
  }

  def winsOver(user: Hand, diller: Hand): Boolean = {
    val dillerBestValue = List(rankCards(diller.cards), specialValue(diller.cards)).filter(v => v <= winningValue).max
    val bestValue = List(rankCards(user.cards), specialValue(user.cards)).filter(v => v <= winningValue).max
    bestValue > dillerBestValue
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

//  def winsOver(userHand: List[Card], otherHand: List[Card]): Boolean = {
//    val opponentBestValue = List(rankCards(otherHand), specialValue(otherHand)).filter(v => v <= winningValue).max
//    val bestValue = List(value(), specialValue()).filter(v => v <= winningValue).max
//    // Return true if all of this hand's best value is higher than the other hand's best value
//    bestValue > opponentBestValue
//  }

  def stand(cards: List[Card]) = cards ::: Desk.getCard() :: Nil
}
final case class GameRequest(user: List[CardRequest], diller: List[CardRequest])
//object GameRequest {
//  private def convertGameInGameRequest(value: List[Card]): List[CardRequest] =
//    value.map(a => CardRequest(a.suit.getSymbols(), a.rank.symbol))
//  def apply(game: Game): GameRequest = GameRequest(convertGameInGameRequest(game.user), convertGameInGameRequest(game.diller))
//}
final case class Game(lobbyId: Int, user: Hand, diller: Hand, isOverGame: Boolean)

object Game {

  def apply(lobbyId: Int):Game = {
    val desk = Desk
    val userCards = List(desk.getCard(), desk.getCard())
    val dillerCards = List(desk.getCard())
    val userHand = Hand(userCards)
    val dillerHand = Hand(dillerCards)
    Game(lobbyId, userHand, dillerHand, false)
  }

  def startGame() = {
    val desk = Desk
    val user = List(desk.getCard(), desk.getCard())
    val diller = List(desk.getCard())
    val hand = Hand(user)
    hand.options match {
      case options: Options if(options.isBlackJack) => {
        val handDiller= Hand(diller)
      }
    }
  }

}
