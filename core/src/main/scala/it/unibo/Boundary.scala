package it.unibo

import monix.eval.Task
import monix.reactive.Observable

/**
 * IO boundary outside of the model control. Here side effect happens. Examples of boundaries could be: a console,
 * a graphical interface or a network interface.
 *
 * @tparam Model the model structure that describes our application
 * @tparam Input the input root type accepted by the model
 */
trait Boundary[-Model, +Input] {
  /**
   * initialize the boundary, it is supposed to be async to the object creation.
   * @return a Task that initialize the boundary, side effect could happens.
   */
  def init(): Task[Unit] = Task.pure {}

  /**
   * inspired by Functional Reactive Programming, input is seen as an ordered functional stream of data. It is well
   * encoded by Observable (https://monix.io/docs/current/reactive/observable.html) abstraction given by Monix.
   * @return the stream of input captured by the boundary.
   */
  def input: Observable[Input]

  /**
   * consume the data associated with the model by the internal boundary semantics. E.g. if the boundary represents a
   * GUI, this methods produce its graphical representation in the GUI.
   * Also this method is supposed to produce side effects.
   * @param model what is suppose to be consumed by the boundary
   * @return a task that wrap the computation associated with the evaluation to the model.
   */
  def consume(model: Model): Task[Unit]
}
