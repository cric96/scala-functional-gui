package it.unibo.swing.tictactoe

import it.unibo.core.{Boundary, Controller, GameLoop, UpdateFn}
import it.unibo.tictactoe.TicTacToe.X
import it.unibo.tictactoe.{Hit, TicTacToe, TicTacToeOps}

object TicTacToeSwing extends App {
  implicit val global = monix.execution.Scheduler.global
  val view : Boundary[TicTacToe, Hit]= new View()
  val gameLogic : UpdateFn[TicTacToe, Hit] = {
    case (world, _, inputs) => {
      println(Thread.currentThread().getName)
      TicTacToeOps.processInput(world, inputs)
    }
  }
  val loop = GameLoop(view, TicTacToe(X, Map.empty), gameLogic)
  //val loop = Controller.reactive(view, TicTacToe(X, Map.empty), gameLogic)
  loop.runAsyncAndForget(monix.execution.Scheduler.global)
}
