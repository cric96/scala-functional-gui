package it.unibo.swing

import monix.eval.Task
import monix.execution.cancelables.SingleAssignCancelable
import monix.reactive.Observable
import monix.reactive.OverflowStrategy.Unbounded
import java.awt.Component
import java.awt.event.{ActionEvent, ActionListener}

import javax.swing.{AbstractButton, SwingUtilities}
import monix.execution
import monix.execution.Scheduler

import scala.concurrent.ExecutionContext
import scala.language.implicitConversions

package object monadic {
  implicit class RichButton(component : AbstractButton) {
    def eventObservable : Observable[ActionEvent] = Observable.create(Unbounded) { out =>
      val c = SingleAssignCancelable()
      val listener = new ActionListener { override def actionPerformed(e: ActionEvent): Unit = out.onNext(e) }
      component.addActionListener(listener)
      c
    }
  }
  implicit class RichComponent[E <: Component](component : E) {
    def monad : Task[E] = Task.evalOnce(component)
  }
  def task(e : => Unit) : Task[Unit] = Task(e)

  val swingScheduler = Scheduler.apply(new ExecutionContext {
    override def execute(runnable: Runnable): Unit = SwingUtilities.invokeAndWait(runnable)
    override def reportFailure(cause: Throwable): Unit = {} // Todo
  })
}
