package it.unibo.swing.tictactoe

import it.unibo.core.{Boundary, GameLoop}
import it.unibo.core.GameLoop.UpdateFn
import it.unibo.tictactoe.{Check, TicTacToe, TicTacToeOps}
import it.unibo.tictactoe.TicTacToe.X
import monix.eval.Task

import scala.concurrent.duration.DurationInt

object MainApp extends App {
  implicit val global = monix.execution.Scheduler.global
  val view : Boundary[TicTacToe, Check]= new View()
  val gameLogic : UpdateFn[TicTacToe, Check] = {
    case (world, time, inputs) => Task {
      inputs.headOption match {
        case Some(input) => TicTacToeOps.check(world, input)
        case _ => world
      }
    }
  }
  val loop = GameLoop(view, TicTacToe(X, Map.empty), 300.millis, gameLogic)
  loop.runSyncUnsafe()
}
