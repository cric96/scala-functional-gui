package it.unibo.core

import monix.eval.Task

/**
 * Some utility functions that helps to define the update model function
 */
object UpdateFn {
  /**
   * produces always the same model
   * @tparam M the model structure that describes our application
   * @tparam I the input root type accepted by the model
   * @return an update function that produces always the same model
   */
  def identity[M, I]: UpdateFn[M, I] = (a: M, _, _: Seq[I]) => Task.pure(a)

  /**
   * an update function independent from the time (i.e. reactive only from user inputs).
   * @param fn the update logic that abstracts over the time
   * @tparam M the model structure that describes our application
   * @tparam I the input root type accepted by the model
   * @return the update function
   */
  def timeIndependent[M, I](fn: (M, Seq[I]) => Task[M]): UpdateFn[M, I] = (model: M, _, inputs: Seq[I]) =>
    fn(model, inputs)
}
