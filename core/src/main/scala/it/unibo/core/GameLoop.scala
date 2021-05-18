package it.unibo.core

import it.unibo.core.Controller.ProactiveConfig
import monix.eval.Task

object GameLoop {
  def apply[M, I](
      boundary: Boundary[M, I],
      initialWorld: M,
      updateLogic: UpdateFn[M, I],
      config: ProactiveConfig = ProactiveConfig()
  ): Task[Unit] =
    Controller.proactive(boundary, initialWorld, updateLogic, config)
}
