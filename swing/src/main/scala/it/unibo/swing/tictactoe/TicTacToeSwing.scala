package it.unibo.swing.tictactoe

import it.unibo.core.{Boundary, GameLoop, UpdateFn}
import it.unibo.tictactoe.TicTacToe.X
import it.unibo.tictactoe.{Hit, InProgress, TicTacToe, TicTacToeOps}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object TicTacToeSwing extends App {
  implicit val global = monix.execution.Scheduler.global
  val view : Boundary[TicTacToe, Hit]= new View()
  val gameLogic : UpdateFn[TicTacToe, Hit] = {
    case (world, _, inputs) => {
      println(Thread.currentThread().getName)
      TicTacToeOps.processInput(world, inputs)
    }
  }
  val loop = GameLoop(view, InProgress(X, Map.empty), gameLogic)
  //val loop = Controller.reactive(view, InProgress(X, Map.empty), gameLogic)
  val started = loop.runToFuture(monix.execution.Scheduler.global)
  Await.result(started, Duration.Inf)
}
