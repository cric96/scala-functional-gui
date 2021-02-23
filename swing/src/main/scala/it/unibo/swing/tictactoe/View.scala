package it.unibo.swing.tictactoe

import it.unibo.core.Boundary
import it.unibo.swing.monadic._
import it.unibo.swing.tictactoe.View.Cell
import it.unibo.tictactoe.TicTacToe.{Player, Position}
import it.unibo.tictactoe.{Hit, InProgress}
import monix.eval.Task
import monix.reactive.Observable

import java.awt.GridLayout
import javax.swing.{JButton, JFrame, JPanel, WindowConstants}

class View extends Boundary[InProgress, Hit] {
  val container: Task[JFrame] = for {
    frame <- new JFrame("TicTacToe").monad
    _ <- task(frame.setSize(800, 600))
    _ <- task(frame.setVisible(true))
    _ <- task(frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE))
  } yield frame

  val board: Task[JPanel] = for {
    panel <- new JPanel(new GridLayout(3, 3)).monad
    jframe <- container
    _ <- task(jframe.getContentPane.add(panel))
  } yield panel

  val cells: Task[Seq[Cell]] = Task.evalOnce[Seq[Cell]] {
    for {
      i <- 0 to 2
      j <- 0 to 2
    } yield Cell(i, j, new JButton("_"))
  }

  def checkObservable(cell : Cell) : Observable[Hit] = cell.button.eventObservable.map(_ => Hit(cell.i, cell.j))

  override def input: Observable[Hit] = Observable.fromTask(cells)
    .flatMapIterable(a => a)
    .map(checkObservable)
    .merge

  override def render(model: InProgress): Task[Unit] = {
    for {
      frame <- container.asyncBoundary(swingScheduler) //go to AWT Thread
      panel <- board
      _ <- task(panel.removeAll())
      _ <- renderButtons(panel, model.matrix)
      _ <- task(frame.repaint()) //force repaint
      _ <- task(frame.setVisible(true)) //force repaint
    } yield ()
  }


  def renderButtons(panel: JPanel, matrix : Map[Position, Player]) : Task[Unit] = for {
    buttons <- cells
    _ <- task {
      buttons.foreach {
        case Cell(i, j, button) => matrix.get((i, j))
          .foreach(p => updateButton(button, p))
      }
    }
    _ <- task { buttons.map(_.button).foreach(panel.add) }
  } yield ()

  def updateButton(button : JButton, player: Player) : Unit = button.setText(player.toString)
}

object View {
  case class Cell(i : Int, j : Int, button : JButton)
}
