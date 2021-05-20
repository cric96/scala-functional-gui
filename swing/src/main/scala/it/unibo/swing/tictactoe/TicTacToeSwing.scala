package it.unibo.swing.tictactoe

import it.unibo.Boundary
import it.unibo.controller.GameLoop
import it.unibo.controller.UpdateFn
import it.unibo.tictactoe.TicTacToe.InProgress
import it.unibo.tictactoe.TicTacToe.X
import it.unibo.tictactoe.Hit
import it.unibo.tictactoe.TicTacToe
import it.unibo.tictactoe.TicTacToeInputProcess

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object TicTacToeSwing extends App {
  val view: Boundary[TicTacToe, Hit] = new View()
  //main logic
  val gameLogic: UpdateFn[TicTacToe, Hit] = UpdateFn.timeIndependent { case (world, inputs) =>
    TicTacToeInputProcess(world, inputs)
  }
  val loop = GameLoop(view, InProgress(X, Map.empty), gameLogic)
  //val loop = Controller.reactive(view, InProgress(X, Map.empty), gameLogic)
  val started = loop.runToFuture(monix.execution.Scheduler.global)
  Await.result(started, Duration.Inf)
}
