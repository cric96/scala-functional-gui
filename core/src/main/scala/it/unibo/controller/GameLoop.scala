package it.unibo.controller

import it.unibo.Boundary
import it.unibo.controller.Controller.ProactiveConfig
import monix.eval.Task

/**
 * a facade for Controller.proactive
 */
object GameLoop {
  def apply[M, I](
      boundary: Boundary[M, I],
      initialWorld: M,
      updateLogic: UpdateFn[M, I],
      config: ProactiveConfig = ProactiveConfig()
  ): Task[Unit] =
    Controller.proactive(boundary, initialWorld, updateLogic, config)
}
