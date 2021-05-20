package it.unibo.tictactoe

import it.unibo.tictactoe.TicTacToeOps.advanceWith
import monix.eval.Task

import scala.annotation.tailrec

/**
 * module in which are contained functions used for manage inputs received.
 */
object TicTacToeInputProcess {
  @tailrec
  def apply(game: TicTacToe, inputs: Seq[Hit]): Task[TicTacToe] = inputs match {
    case input :: others => TicTacToeInputProcess(advanceWith(game, input.position), others)
    case _ => Task.pure(game)
  }
}
