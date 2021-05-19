package it.unibo.swing.tictactoe

import cats.effect.ExitCode
import it.unibo.core.Boundary
import it.unibo.core.GameLoop
import it.unibo.core.UnsafeMonadTransform
import it.unibo.core.UpdateFn
import it.unibo.tictactoe.TicTacToe.X
import it.unibo.tictactoe.Hit
import it.unibo.tictactoe.InProgress
import it.unibo.tictactoe.TicTacToe
import it.unibo.tictactoe.TicTacToeInputProcess
import monix.eval.Task
import monix.eval.TaskApp

object TicTacToeSafe extends TaskApp with UnsafeMonadTransform {
  override def run(args: List[String]): Task[ExitCode] = {
    implicit val sch = scheduler
    val view: Boundary[TicTacToe, Hit] = new View()
    //main logic
    val gameLogic: UpdateFn[TicTacToe, Hit] = { case (world, _, inputs) =>
      println(inputs); TicTacToeInputProcess(world, inputs)
    }
    val loop = GameLoop(view, InProgress(X, Map.empty), gameLogic)
    //val loop = Controller.reactive(view, InProgress(X, Map.empty), gameLogic)
    loop.map(_ => ExitCode.Success)
  }
}
