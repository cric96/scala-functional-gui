package it.unibo.core

import cats._
import cats.arrow.FunctionK
import monix.eval.Task
import monix.reactive.Observable
trait MonadTransform {
  implicit def observableToTask: Observable ~> Task = new FunctionK[Observable, Task] {
    override def apply[A](fa: Observable[A]): Task[A] = fa.headL
  }
}
