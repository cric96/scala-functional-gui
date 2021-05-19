package it.unibo.core

import cats._
import cats.arrow.FunctionK
import monix.eval.Task
import monix.execution.AsyncQueue
import monix.execution.Scheduler
import monix.reactive.Observable
trait UnsafeMonadTransform {
  implicit def observableToTask(implicit sch: Scheduler): Observable ~> Task = new FunctionK[Observable, Task] {
    override def apply[A](fa: Observable[A]): Task[A] = {
      val queue = monix.execution.AsyncQueue.unbounded[A]()
      val producer =
        fa.mapEval(data => Task.fromFuture(queue.offer(data))).foreachL { _ => }.startAndForget.memoize

      for {
        _ <- producer
        fiberConsumer <- Task.fromFuture(queue.poll()).start
        data <- fiberConsumer.join
      } yield data
    }
  }
  implicit def observableToQueue(implicit sch: Scheduler): Observable ~> AsyncQueue =
    new FunctionK[Observable, AsyncQueue] {
      override def apply[A](fa: Observable[A]): AsyncQueue[A] = {
        val queue = monix.execution.AsyncQueue.unbounded[A]()
        fa.mapEval(data => Task.fromFuture(queue.offer(data))).foreach { _ => } //unsafe
        queue
      }
    }
}
