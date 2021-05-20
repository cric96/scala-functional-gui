package it.unibo.controller

import it.unibo.Boundary
import monix.catnap.ConcurrentQueue
import monix.eval.Task

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.DurationInt
import scala.concurrent.duration.FiniteDuration

/*
 * improvement: consider to introduce monad transformer instead of hide the transformation inside the methods (as I done here)
 */
object Controller {
  /**
   * Configuration used for proactive loops (i.e. the next state of the world is influenced not only by the user input but
   * also by the time). It is used to create a "game loop" like controller
   * @param timeTarget define the temporal difference between two word computations
   * @param bufferSize define how many input could be cached during the sleeping time.
   */
  case class ProactiveConfig(timeTarget: FiniteDuration = 33.millis, bufferSize: Int = 5)

  /**
   * create a reactive controller, i.e. the model updates are input driven. Tasks evaluation order is:
   * 1. init boundary
   * 2. consume the model
   * 3. take input (blocking request)
   * 4. return to point 2.
   * @param boundary external to the model, define the logic to consume it (i.e. how to visualize a snapshot).
   * @param model initial model state.
   * @param updateLogic define the model update progression
   * @tparam M the model structure that describes our application
   * @tparam I the input root type accepted from the model
   * @return a Task associated with input-driven loop.
   */
  def reactive[M, I](boundary: Boundary[M, I], model: M, updateLogic: UpdateFn[M, I]): Task[Unit] =
    createLoop[M, I](boundary, queue => inputDrivenLoop(boundary, model, updateLogic, queue))

  /**
   * create a proactive controller, i.e. the model update are time driven. Tasks evaluation order is:
   * 1. init boundary
   * 2. take input (non-blocking)
   * 3. consume the model
   * 4. sleep to reach time target (blocking)
   * 5. return to point 2
   * @param boundary external to the model, define the logic to consume it (i.e. how to visualize a snapshot).
   * @param model initial model state.
   * @param updateLogic define the model update progression
   * @param config define additional information to control the loop
   * @tparam M the model structure that describes our application
   * @tparam I the input root type accepted by the model
   * @return a Task associated with input-driven loop.
   */
  def proactive[M, I](
      boundary: Boundary[M, I],
      model: M,
      updateLogic: UpdateFn[M, I],
      config: ProactiveConfig
  ): Task[Unit] =
    createLoop[M, I](boundary, queue => nonBlockingLoop(boundary, model, updateLogic, queue, config))
  //resume the common part of reactive and proactive loop. I leverage a ConcurrentQueue in order to create either reactive and proactive controller
  @annotation.nowarn
  private def createLoop[M, I](
      boundary: Boundary[M, I],
      loopHandler: ConcurrentQueue[Task, I] => Task[Unit]
  ): Task[Unit] = {
    for {
      _ <- boundary.init()
      queue <- ConcurrentQueue.unbounded[Task, I]()
      sink = boundary.input.mapEval(data => queue.offer(data)).foreachL { _ =>
      } //task to sink the data from observable to queue
      task <- Task.parMap2(sink, loopHandler(queue)) { (_, _) => }
    } yield task
  }

  private def nonBlockingLoop[M, I](
      boundary: Boundary[M, I],
      model: M,
      updateLogic: UpdateFn[M, I],
      queue: ConcurrentQueue[Task, I],
      config: ProactiveConfig
  ): Task[Unit] = for {
    inputs <- queue.drain(0, config.bufferSize) //drain input, if there aren't any input, return List.empty
    timeTarget = config.timeTarget
    prevTime <- time(timeTarget.unit)
    _ <- boundary.consume(model)
    _ <- Task.shift //if the render has changed the thread, the loop returns to the main thread
    updatedModel <- updateLogic(model, prevTime, inputs.toList)
    newTime <- time(timeTarget.unit)
    temporalDiff = FiniteDuration(prevTime - newTime, timeTarget.unit)
    _ <- waitForNextEvaluation(temporalDiff, timeTarget)
    _ <- nonBlockingLoop[M, I](boundary, updatedModel, updateLogic, queue, config)
  } yield ()

  private def waitForNextEvaluation(temporalDifference: FiniteDuration, timeTarget: FiniteDuration): Task[Unit] = if (
    temporalDifference > timeTarget
  ) {
    Task.pure {}
  } else {
    Task.sleep(timeTarget - temporalDifference)
  }

  private def inputDrivenLoop[M, I](
      boundary: Boundary[M, I],
      model: M,
      updateLogic: UpdateFn[M, I],
      queue: ConcurrentQueue[Task, I]
  ): Task[Unit] = for {
    _ <- boundary.consume(model)
    _ <- Task.shift //if the render has changed the thread, the loop returns to the main thread
    input <- queue.poll
    current <- time(TimeUnit.MILLISECONDS)
    updatedModel <- updateLogic(model, current, Seq(input))
    _ <- inputDrivenLoop[M, I](boundary, updatedModel, updateLogic, queue)
  } yield ()

  private def time(timeUnit: TimeUnit): Task[Long] = Task.clock.monotonic(timeUnit)
}
