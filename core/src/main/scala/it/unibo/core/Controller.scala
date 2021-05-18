package it.unibo.core

import monix.catnap.ConcurrentQueue
import monix.eval.Task

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.DurationInt
import scala.concurrent.duration.FiniteDuration

object Controller {
  case class ProactiveConfig(timeTarget: FiniteDuration = 33.millis, bufferSize: Int = 5)

  def reactive[M, I](boundary: Boundary[M, I], model: M, updateLogic: UpdateFn[M, I]): Task[Unit] =
    createLoop[M, I](boundary, queue => inputDrivenLoop(boundary, model, updateLogic, queue))

  def proactive[M, I](
      boundary: Boundary[M, I],
      model: M,
      updateLogic: UpdateFn[M, I],
      config: ProactiveConfig
  ): Task[Unit] =
    createLoop[M, I](boundary, queue => nonBlockingLoop(boundary, model, updateLogic, queue, config))

  private def createLoop[M, I](
      boundary: Boundary[M, I],
      loopHandler: ConcurrentQueue[Task, I] => Task[Unit]
  ): Task[Unit] = {
    val concurrentQueue = ConcurrentQueue.unbounded[Task, I]()
    boundary.init().flatMap(_ => concurrentQueue).flatMap { queue =>
      val sink = boundary.input.mapEval(data => queue.offer(data)).foreachL { _ => } //task to sink the data
      Task.parMap2(sink, loopHandler(queue)) { (_, _) => }
    }
  }

  private def nonBlockingLoop[M, I](
      boundary: Boundary[M, I],
      model: M,
      updateLogic: UpdateFn[M, I],
      queue: ConcurrentQueue[Task, I],
      config: ProactiveConfig
  ): Task[Unit] = for {
    inputs <- queue.drain(0, config.bufferSize)
    timeTarget = config.timeTarget
    prevTime <- time(timeTarget.unit)
    _ <- boundary.render(model)
    _ <- Task.shift //if the render has changed the thread, the loop returns to the main thread
    updatedModel <- updateLogic(model, prevTime, inputs.toList)
    newTime <- time(timeTarget.unit)
    temporalDiff = prevTime - newTime
    _ <-
      if (temporalDiff.longValue() > timeTarget.length) {
        Task.pure {}
      } else {
        Task.sleep(FiniteDuration(timeTarget.length - temporalDiff, timeTarget.unit))
      }
    _ <- nonBlockingLoop[M, I](boundary, updatedModel, updateLogic, queue, config)
  } yield ()

  private def inputDrivenLoop[M, I](
      boundary: Boundary[M, I],
      model: M,
      updateLogic: UpdateFn[M, I],
      queue: ConcurrentQueue[Task, I]
  ): Task[Unit] = for {
    _ <- boundary.render(model)
    _ <- Task.shift //if the render has changed the thread, the loop returns to the main thread
    input <- queue.poll
    current <- time(TimeUnit.MILLISECONDS)
    updatedModel <- updateLogic(model, current, Seq(input))
    _ <- inputDrivenLoop[M, I](boundary, updatedModel, updateLogic, queue)
  } yield ()

  private def time(timeUnit: TimeUnit): Task[Long] = Task.clock.monotonic(timeUnit)
}
