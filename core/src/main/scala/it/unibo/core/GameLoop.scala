package it.unibo.core

import it.unibo.core.Controller.ProactiveConfig
import monix.eval.Task
import cats._
import monix.execution.AsyncQueue
import monix.reactive.Observable

object GameLoop {
  def apply[M, I](
      boundary: Boundary[M, I],
      initialWorld: M,
      updateLogic: UpdateFn[M, I],
      config: ProactiveConfig = ProactiveConfig()
  )(implicit transform: Observable ~> AsyncQueue): Task[Unit] =
    Controller.proactive(boundary, initialWorld, updateLogic, config)
}
