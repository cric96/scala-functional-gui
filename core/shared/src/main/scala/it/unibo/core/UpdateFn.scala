package it.unibo.core

import monix.eval.Task

object UpdateFn {
  def empty[W, I] : UpdateFn[W, I] = (a : W, _, _ : Seq[I]) => Task.pure(a)
}