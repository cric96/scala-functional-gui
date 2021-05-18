package it.unibo.html

import it.unibo.core.Boundary
import it.unibo.tictactoe.TicTacToe.Player
import it.unibo.tictactoe.TicTacToe.Position
import it.unibo.tictactoe.End
import it.unibo.tictactoe.Hit
import it.unibo.tictactoe.TicTacToe
import monix.eval.Task
import monix.reactive.Observable
import org.scalajs.dom
import org.scalajs.dom.Element
import org.scalajs.dom.html.Paragraph
import org.scalajs.dom.html.Table
import scalatags.JsDom.all._

class View extends Boundary[TicTacToe, Hit] {
  private val rowPosition = 0
  private val colPosition = 1

  private lazy val boardGame: Task[Table] = Task.evalOnce {
    val rows = 0 until TicTacToe.defaultSize map line
    table(rows: _*).render
  }

  private lazy val label: Task[Paragraph] = Task.evalOnce(p("").render)

  private lazy val cells: Task[Seq[Element]] = Task[Seq[Element]] {
    for {
      i <- 0 until TicTacToe.defaultSize
      j <- 0 until TicTacToe.defaultSize
      elem = dom.document.getElementById(s"$i$j")
      if elem != null
    } yield elem
  }

  override def input: Observable[Hit] = {
    Observable
      .fromTask(cells)
      .flatMapIterable(a => a)
      .map(createObservable)
      .merge
  }

  override def init: Task[Unit] = for {
    game <- boardGame
    labelEl <- label
    _ <- attach(game)
    _ <- attach(labelEl)
  } yield ()

  override def render(model: TicTacToe): Task[Unit] = for {
    _ <- refreshTable(model.board)
    _ <- renderVictory(model)
  } yield ()

  private def refreshTable(board: Map[Position, Player]) = Task {
    board.foreach { case ((row, column), p) =>
      dom.document.getElementById(s"$row$column").innerHTML = p.toString
    }
  }

  private def renderVictory(board: TicTacToe): Task[Unit] = for {
    labelEl <- label
    _ <- Task {
      board match {
        case End(winner, _) => labelEl.textContent = s"Winner $winner"
        case _ =>
      }
    }
  } yield ()

  private def createObservable(element: Element): Observable[Hit] = DomUtils.eventObservable(element).map { _ =>
    Hit((element.id(rowPosition).asDigit, element.id(colPosition).asDigit))
  }

  private def line(row: Int) = {
    val elements = 0 until TicTacToe.defaultSize map (column => td(button("_", id := s"$row$column")))
    tr(elements: _*)
  }

  private def attach(elem: Element): Task[Unit] = Task {
    dom.document.body.appendChild(elem).normalize()
  }

}
