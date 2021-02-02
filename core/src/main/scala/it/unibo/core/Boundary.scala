package it.unibo.core

import monix.eval.Task
import monix.reactive.Observable

trait Boundary[-Model, +Input] {
  def input: Observable[Input]
  def render(model: Model): Task[Unit]
}
