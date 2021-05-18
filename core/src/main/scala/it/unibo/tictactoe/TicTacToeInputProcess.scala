package it.unibo.tictactoe

import it.unibo.tictactoe.TicTacToeOps.advanceWith
import monix.eval.Task

import scala.annotation.tailrec

object TicTacToeInputProcess {
  @tailrec
  def apply(game: TicTacToe, inputs: Seq[Hit]): Task[TicTacToe] = inputs match {
    case input :: others => TicTacToeInputProcess(advanceWith(game, input), others)
    case _ => Task.pure(game)
  }
}
