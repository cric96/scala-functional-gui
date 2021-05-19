package it.unibo.tictactoe

import it.unibo.tictactoe.TicTacToe._

sealed trait TicTacToe {
  def board: Map[Position, Player]
}
object TicTacToe {
  case class End(winner: Player, board: Map[Position, Player]) extends TicTacToe
  case class InProgress(turn: Player, board: Map[Position, Player]) extends TicTacToe

  val defaultSize: Int = 3
  type Position = (Int, Int)

  sealed trait Player {
    def other: Player = this match {
      case X => O
      case O => X
    }
  }
  case object X extends Player
  case object O extends Player

}
