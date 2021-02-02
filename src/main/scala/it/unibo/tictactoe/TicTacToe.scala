package it.unibo.tictactoe

import it.unibo.tictactoe.TicTacToe._

case class TicTacToe(turn : Player, matrix : Map[Position, Player])

object TicTacToe {
  type Position = (Int, Int)
  sealed trait Player
  case object X extends Player
  case object O extends Player
}
