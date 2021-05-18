package it.unibo.swing

import monix.eval.Task
import monix.execution.Scheduler
import monix.execution.cancelables.SingleAssignCancelable
import monix.reactive.Observable
import monix.reactive.OverflowStrategy.Unbounded

import java.awt.Component
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.AbstractButton
import javax.swing.SwingUtilities
import scala.concurrent.ExecutionContext
import scala.language.implicitConversions

package object monadic {

  implicit class RichButton(component: AbstractButton) {

    def eventObservable: Observable[ActionEvent] = Observable.create(Unbounded) { out =>
      val cancellable = SingleAssignCancelable()
      val listener = new ActionListener { override def actionPerformed(e: ActionEvent): Unit = out.onNext(e) }
      component.addActionListener(listener)
      cancellable
    }
  }

  implicit class RichComponent[E <: Component](component: E) {
    def monad: Task[E] = Task.evalOnce(component)
  }
  def task(e: => Unit): Task[Unit] = Task(e)

  val swingScheduler: Scheduler = Scheduler.apply(new ExecutionContext {
    override def execute(runnable: Runnable): Unit = SwingUtilities.invokeAndWait(runnable)
    override def reportFailure(cause: Throwable): Unit = {} // Todo
  })
}
