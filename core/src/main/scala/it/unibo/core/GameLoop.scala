package it.unibo.core

import monix.catnap.ConcurrentQueue
import monix.eval.Task

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration

object GameLoop {
  type UpdateFn[W, I] = (W, Long, Seq[I]) => Task[W]

  object UpdateFn {
    def empty[W, I] : UpdateFn[W, I] = (a : W, _, _ : Seq[I]) => Task.pure(a)
  }

  def apply[W, I](boundary: Boundary[W, I], initialWorld: W, timeTarget: FiniteDuration, updateLogic: UpdateFn[W, I]): Task[Unit] = {
    val concurrentQueue = ConcurrentQueue.unbounded[Task, I]()
    concurrentQueue.flatMap(queue => {
      val sink = boundary.input.mapEval(data => queue.offer(data)).foreachL(_ => {}) //task to sink the data
      val gameLoop = loop(initialWorld, boundary, timeTarget, queue, updateLogic)
      Task.parMap2(sink, gameLoop) {(_, _) => {}}
    })
  }

  def create[W, I](boundary: Boundary[W, I], initialWorld: W, timeTarget: FiniteDuration)(updateLogic: UpdateFn[W, I]): Task[Unit] = {
    GameLoop(boundary, initialWorld, timeTarget, updateLogic)
  }

  private def loop[W, I](world: W, boundary: Boundary[W, I], timeTarget: FiniteDuration, inputQueue: ConcurrentQueue[Task, I], updateLogic: UpdateFn[W, I]): Task[Unit] = for {
    inputs <- inputQueue.drain(0, 5) //TODO put external
    prevTime <- time(timeTarget.unit)
    _ <- boundary.render(world)
    newWorld <- updateLogic(world, prevTime, inputs.toList)
    newTime <- time(timeTarget.unit)
    temporalDiff = prevTime - newTime
    _ <- if (temporalDiff.longValue() > timeTarget._1) {
      Task.pure()
    } else {
      Task.sleep(FiniteDuration(timeTarget._1 - temporalDiff, timeTarget.unit))
    }
    value <- loop[W, I](newWorld, boundary, timeTarget, inputQueue, updateLogic)
  } yield value

  private def time(timeUnit: TimeUnit): Task[Long] = Task.clock.monotonic(timeUnit)
}
