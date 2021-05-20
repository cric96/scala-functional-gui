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

/**
 * Some utility functions that helps the building of monadic GUI easier
 */
package object monadic {
  //TYPE ENRICHMENT (a.k.a. pimp my library)
  /**
   * enrich plain swing button adding event handling with monix observable.
   * @param component enriched object
   */
  implicit class ObservableButton(component: AbstractButton) {

    def eventObservable: Observable[ActionEvent] = Observable.create(Unbounded) { out =>
      val cancellable = SingleAssignCancelable()
      @annotation.nowarn
      val listener = new ActionListener { override def actionPerformed(e: ActionEvent): Unit = out.onNext(e) } //unsafe
      component.addActionListener(listener)
      cancellable
    }
  }

  /**
   * Enrich a generic swing command with lift method, i.e. wrap a component inside a task
   * @param component  enriched object
   * @tparam E type of swing component
   */
  implicit class RichComponent[E <: Component](component: E) {
    def lift: Task[E] = Task.evalOnce(component)
  }
  //a facade, improve code writing
  def io[E](e: => E): Task[E] = Task(e)

  //scheduler used from shifting the task execution to the AWT thread.
  val swingScheduler: Scheduler = Scheduler.apply(new ExecutionContext {
    override def execute(runnable: Runnable): Unit = SwingUtilities.invokeAndWait(runnable)
    override def reportFailure(cause: Throwable): Unit = {} // Todo
  })
}
