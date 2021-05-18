package it.unibo.html

import monix.execution.cancelables.SingleAssignCancelable
import monix.reactive.Observable
import monix.reactive.OverflowStrategy.Unbounded
import org.scalajs.dom.Element
import org.scalajs.dom.MouseEvent

object DomUtils {
  def eventObservable(element: Element): Observable[MouseEvent] = Observable.create(Unbounded) { out =>
    val cancellable = SingleAssignCancelable()
    element.addEventListener[MouseEvent](
      "click",
      ev => out.onNext(ev)
    )
    cancellable
  }
}
