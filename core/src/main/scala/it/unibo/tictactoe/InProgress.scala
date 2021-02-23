package it.unibo.tictactoe

import it.unibo.tictactoe.TicTacToe._

sealed trait TicTacToe {
  def matrix : Map[Position, Player]
}
case class End(winner : Player, matrix : Map[Position, Player]) extends TicTacToe
case class InProgress(turn : Player, matrix : Map[Position, Player]) extends TicTacToe

object TicTacToe {
  type Position = (Int, Int)
  sealed trait Player
  case object X extends Player
  case object O extends Player
}
