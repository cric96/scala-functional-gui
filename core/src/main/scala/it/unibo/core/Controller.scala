package it.unibo.core

import cats._
import monix.eval.Task
import monix.execution.AsyncQueue
import monix.reactive.Observable

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.DurationInt
import scala.concurrent.duration.FiniteDuration
object Controller {
  case class ProactiveConfig(timeTarget: FiniteDuration = 33.millis, bufferSize: Int = 5)

  def reactive[M, I](boundary: Boundary[M, I], model: M, updateLogic: UpdateFn[M, I])(implicit
      transform: Observable ~> Task
  ): Task[Unit] =
    createLoop[M, I](boundary, queue => inputDrivenLoop(boundary, model, updateLogic, transform(queue)))

  def proactive[M, I](
      boundary: Boundary[M, I],
      model: M,
      updateLogic: UpdateFn[M, I],
      config: ProactiveConfig
  )(implicit transform: Observable ~> AsyncQueue): Task[Unit] = {
    createLoop[M, I](
      boundary,
      queue => {
        val asyncQueue = transform(queue)
        nonBlockingLoop(
          boundary,
          model,
          updateLogic,
          Task(asyncQueue.tryPoll()),
          config
        )
      }
    )
  }

  private def createLoop[M, I](
      boundary: Boundary[M, I],
      loopHandler: Observable[I] => Task[Unit]
  ): Task[Unit] =
    boundary.init().flatMap(_ => loopHandler(boundary.input))

  private def nonBlockingLoop[M, I](
      boundary: Boundary[M, I],
      model: M,
      updateLogic: UpdateFn[M, I],
      queue: Task[Option[I]],
      config: ProactiveConfig
  ): Task[Unit] = for {
    inputs <- queue
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
      inputTask: Task[I]
  )(implicit transform: Observable ~> Task): Task[Unit] = {
    for {
      _ <- boundary.render(model)
      _ <- Task.shift //if the render has changed the thread, the loop returns to the main thread
      input <- inputTask
      current <- time(TimeUnit.MILLISECONDS)
      updatedModel <- updateLogic(model, current, Seq(input))
      _ <- inputDrivenLoop[M, I](boundary, updatedModel, updateLogic, inputTask)
    } yield ()
  }

  private def time(timeUnit: TimeUnit): Task[Long] = Task.clock.monotonic(timeUnit)
}
