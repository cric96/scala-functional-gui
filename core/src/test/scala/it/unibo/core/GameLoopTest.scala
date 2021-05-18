package it.unibo.core

import it.unibo.core.Controller.ProactiveConfig
import it.unibo.core.GameLoopTest._
import monix.eval.Task
import monix.reactive.Observable
import monix.reactive.subjects.PublishSubject
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Promise
import scala.concurrent.duration.DurationInt
import scala.util.Success

//NB! this is not used to show how to use GameLoop but it is used for testing internals.
//In general, you can't access to the GameLoop state outside of it, is the root of all side effect.
//
class GameLoopTest extends AsyncFlatSpec with Matchers {
  import monix.execution.Scheduler.Implicits.global
  private val period = ProactiveConfig(100.millis)
  private val longSleep = 200.millis
  private val initialWorld = 0

  "GameLoop" should "be lazy" in {
    val unsafeBoundary = new AttachPromiseBoundary[W, I]()
    GameLoop[W, I](unsafeBoundary, initialWorld, UpdateFn.empty, period)
    Task
      .sleep(longSleep)
      .runToFuture
      .map(_ => unsafeBoundary.promise.isCompleted shouldBe false)
  }

  "GameLoop" should "be cancellable" in {
    val unsafeBoundary = new AttachPromiseBoundary[W, I]()
    val gameLoop = GameLoop[W, I](unsafeBoundary, initialWorld, UpdateFn.empty, period)
    val loop = gameLoop.runAsync { cb => }
    val firstExecutionFuture = unsafeBoundary.promise
    val afterExecutionFuture = firstExecutionFuture.future.flatMap { _ =>
      loop.cancel() //stop the execution
      unsafeBoundary.clearPromise //remove old promise
      unsafeBoundary.promise.future
    }
    Task
      .sleep(longSleep)
      .runToFuture
      .map(_ => firstExecutionFuture.isCompleted shouldBe true)
      .map(_ => afterExecutionFuture.isCompleted shouldBe false)
  }

  "GameLoop" should "should process inputs" in {
    val subject = PublishSubject[String]()
    val unsafeBoundary = new AttachPromiseBoundary[W, I](Observable("act"))
    val updatedWorld = 10
    val input = "act"
    val gameLoop = GameLoop[W, I](
      unsafeBoundary,
      initialWorld,
      (w, time, inputs) =>
        inputs match {
          case `input` :: _ => Task.pure(updatedWorld)
          case _ => Task.pure(w)
        },
      period
    )
    gameLoop.runAsyncAndForget
    val firstExecutionFuture = unsafeBoundary.promise
    val updatedWorldFuture = firstExecutionFuture.future.flatMap { w =>
      subject.onNext(input) // update world
      w shouldBe initialWorld
    }

    Task
      .sleep(longSleep)
      .runToFuture
      .flatMap { _ =>
        unsafeBoundary.clearPromise
        unsafeBoundary.promise.future
      }
      .flatMap { world =>
        world shouldBe updatedWorld
      }
      .flatMap(_ => updatedWorldFuture)
  }
}

object GameLoopTest {
  type W = Int
  type I = String

  //Unsafe
  class AttachPromiseBoundary[W, I](val input: Observable[I] = Observable.empty) extends Boundary[W, I] {
    var promise: Promise[W] = Promise()

    override def render(model: W): Task[Unit] = Task {
      if (!promise.isCompleted) {
        promise.complete(Success(model))
      }
    }
    def clearPromise = promise = synchronized(Promise())
  }
}
