package it.unibo.tictactoe

import it.unibo.tictactoe.TicTacToe.{O, Player, Position, X}
import monix.eval.Task

object TicTacToeOps {
  def advanceWith(ticTacToe : TicTacToe, hit : Hit) : TicTacToe = {
    val updateGame = for {
      _ <- rightPosition(ticTacToe, hit)
    } yield (act(ticTacToe, hit))
    updateGame.getOrElse(ticTacToe)
  }

  def processInput(game : TicTacToe, inputs : Seq[Hit]) : Task[TicTacToe] = inputs match {
    case input :: others => processInput(advanceWith(game, input), others)
    case empty => Task.pure(game)
  }

  private def rightPosition(ticTacToe: TicTacToe, hit: Hit) : Option[TicTacToe] = Some(ticTacToe).filterNot(_.matrix.contains(hit.position))

  private def act(ticTacToe: TicTacToe, hit: Hit) : TicTacToe = ticTacToe.copy(ticTacToe.turn.other, updateMatrix(ticTacToe, hit))

  private def updateMatrix(ticTacToe: TicTacToe, hit: Hit) : Map[Position, Player] = ticTacToe.matrix + (hit.position -> ticTacToe.turn)

  implicit class RichPlayer(p : Player) {
    def other : Player = p match {
      case X => O
      case O => X
    }
  }
}
