package it.unibo.swing.tictactoe

import cats.effect.ExitCode
import it.unibo.core.Boundary
import it.unibo.core.GameLoop
import it.unibo.core.UpdateFn
import it.unibo.tictactoe.Hit
import it.unibo.tictactoe.TicTacToe
import it.unibo.tictactoe.TicTacToeInputProcess
import it.unibo.tictactoe.TicTacToe._
import monix.eval.Task
import monix.eval.TaskApp

object TicTacToeSafe extends TaskApp {

  override def run(args: List[String]): Task[ExitCode] = {
    val view: Boundary[TicTacToe, Hit] = new View()
    //main logic
    val gameLogic: UpdateFn[TicTacToe, Hit] = UpdateFn.timeIndependent { case (world, inputs) =>
      TicTacToeInputProcess(world, inputs)
    }
    val loop = GameLoop(view, InProgress(X, Map.empty), gameLogic)
    //val loop = Controller.reactive(view, InProgress(X, Map.empty), gameLogic)
    loop.map(_ => ExitCode.Success)
  }
}
