package it.unibo.tictactoe

import it.unibo.tictactoe.TicTacToe._

/**
 * ADT that describe a TicTacToe game
 */
sealed trait TicTacToe {
  def board: Map[Position, Player]
}
object TicTacToe {
  /**
   * The game stage that waits for the play of the Player turn gives the current board.
   */
  case class InProgress(turn: Player, board: Map[Position, Player]) extends TicTacToe

  /**
   * The game is over (i.e. exist a winner).
   */
  case class End(winner: Player, board: Map[Position, Player]) extends TicTacToe

  val defaultSize: Int = 3

  type Position = (Int, Int)

  /**
   * ADT that describes a TicTacToe player. In this game exist only two value, X and O.
   */
  sealed trait Player {
    def other: Player = this match {
      case X => O
      case O => X
    }
  }
  case object X extends Player
  case object O extends Player
}
