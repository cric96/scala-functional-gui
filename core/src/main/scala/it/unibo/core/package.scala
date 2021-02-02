package it.unibo

import monix.eval.Task

package object core {
  type UpdateFn[Model, Input] = (Model, Long, Seq[Input]) => Task[Model]
}
