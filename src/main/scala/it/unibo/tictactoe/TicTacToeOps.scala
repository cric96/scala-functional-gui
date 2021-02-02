package it.unibo.tictactoe

import it.unibo.tictactoe.TicTacToe.{O, Player, Position, X}

object TicTacToeOps {
  def check(ticTacToe : TicTacToe, check : Check) : TicTacToe = {
    val updateGame = for {
      _ <- rightPosition(ticTacToe, check)
      _ <- rightPlayer(ticTacToe, check)
    } yield (act(ticTacToe, check))

    updateGame.getOrElse(ticTacToe)
  }

  def rightPlayer(ticTacToe: TicTacToe, check: Check) : Option[TicTacToe] = Some(ticTacToe).filter(_.turn != check.player)

  def rightPosition(ticTacToe: TicTacToe, check: Check) : Option[TicTacToe] = Some(ticTacToe).filter(_.matrix.contains(check.position))

  def act(ticTacToe: TicTacToe, check: Check) : TicTacToe = ticTacToe.copy(check.player.other, updateMatrix(ticTacToe, check))

  def updateMatrix(ticTacToe: TicTacToe, check: Check) : Map[Position, Player] = ticTacToe.matrix + (check.position -> check.player)

  implicit class RichPlayer(p : Player) {
    def other : Player = p match {
      case X => O
      case O => X
    }
  }
}
