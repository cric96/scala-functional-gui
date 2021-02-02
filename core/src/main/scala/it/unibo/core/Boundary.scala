package it.unibo.core

import monix.eval.Task
import monix.reactive.Observable

trait Boundary[-W, +I] {
  def input: Observable[I]

  def render(world: W): Task[Unit]
}
