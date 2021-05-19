package it.unibo

import monix.eval.Task

package object core {
  /**
   * describe the model evolution in time. It is supposed to be functional, with
   * the same tuple (model, time, inputs) should produce the same output model.
   * It returns a Task because the computation is decouple from the behaviour.
   * @tparam Model the model structure that describes our application
   * @tparam Input the input root type accepted by the model
   */
  type UpdateFn[Model, Input] = (Model, Long, Seq[Input]) => Task[Model]
}
