package it.unibo.tictactoe

import it.unibo.tictactoe.TicTacToe.{O, Player, Position, X}

object TicTacToeOps {
  def check(ticTacToe : TicTacToe, check : Check) : TicTacToe = {
    val updateGame = for {
      _ <- rightPosition(ticTacToe, check)
    } yield (act(ticTacToe, check))
    updateGame.getOrElse(ticTacToe)
  }

  private def rightPosition(ticTacToe: TicTacToe, check: Check) : Option[TicTacToe] = Some(ticTacToe).filterNot(_.matrix.contains(check.position))

  private def act(ticTacToe: TicTacToe, check: Check) : TicTacToe = ticTacToe.copy(ticTacToe.turn.other, updateMatrix(ticTacToe, check))

  private def updateMatrix(ticTacToe: TicTacToe, check: Check) : Map[Position, Player] = ticTacToe.matrix + (check.position -> ticTacToe.turn)

  implicit class RichPlayer(p : Player) {
    def other : Player = p match {
      case X => O
      case O => X
    }
  }
}
