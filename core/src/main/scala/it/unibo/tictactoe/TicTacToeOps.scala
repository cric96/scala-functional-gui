package it.unibo.tictactoe

import it.unibo.tictactoe.TicTacToe.{O, Player, Position, X}
import monix.eval.Task

object TicTacToeOps {
  def advanceWith(ticTacToe : InProgress, hit : Hit) : InProgress = {
    val updateGame = for {
      _ <- rightPosition(ticTacToe, hit)
    } yield (act(ticTacToe, hit))
    updateGame.getOrElse(ticTacToe)
  }

  def processInput(game : InProgress, inputs : Seq[Hit]) : Task[InProgress] = inputs match {
    case input :: others => processInput(advanceWith(game, input), others)
    case empty => Task.pure(game)
  }

  private def rightPosition(ticTacToe: InProgress, hit: Hit) : Option[InProgress] = Some(ticTacToe).filterNot(_.matrix.contains(hit.position))

  private def act(ticTacToe: InProgress, hit: Hit) : InProgress = ticTacToe.copy(ticTacToe.turn.other, updateMatrix(ticTacToe, hit))

  private def updateMatrix(ticTacToe: InProgress, hit: Hit) : Map[Position, Player] = ticTacToe.matrix + (hit.position -> ticTacToe.turn)

  implicit class RichPlayer(p : Player) {
    def other : Player = p match {
      case X => O
      case O => X
    }
  }
}
