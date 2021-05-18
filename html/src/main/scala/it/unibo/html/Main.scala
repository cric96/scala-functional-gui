package it.unibo.html

import it.unibo.core.GameLoop
import it.unibo.core.UpdateFn
import it.unibo.tictactoe.TicTacToe.X
import it.unibo.tictactoe.Hit
import it.unibo.tictactoe.InProgress
import it.unibo.tictactoe.TicTacToe
import it.unibo.tictactoe.TicTacToeInputProcess

object Main {
  def main(args: Array[String]): Unit = {
    val view: View = new View()
    //main logic
    val gameLogic: UpdateFn[TicTacToe, Hit] = { case (world, _, inputs) =>
      TicTacToeInputProcess(world, inputs)
    }
    val loop = GameLoop(view, InProgress(X, Map.empty), gameLogic)
    //val loop = Controller.reactive(view, InProgress(X, Map.empty), gameLogic)
    @annotation.nowarn
    val nothing = loop.runToFuture(monix.execution.Scheduler.global)
  }
}
