package it.unibo.swing.tictactoe

import it.unibo.core.Boundary
import it.unibo.swing.monadic._
import it.unibo.swing.tictactoe.View.Cell
import it.unibo.tictactoe.TicTacToe.{Player, Position}
import it.unibo.tictactoe.{Check, TicTacToe}
import monix.eval.Task
import monix.reactive.Observable

import java.awt.GridLayout
import javax.swing.{JButton, JFrame, JPanel, WindowConstants}

class View extends Boundary[TicTacToe, Check] {
  val container: Task[JFrame] = for {
    frame <- new JFrame("TicTacToe").monad
    _ <- of(frame.setSize(800, 600))
    _ <- of(frame.setVisible(true))
    _ <- of(frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE))
  } yield frame

  val board: Task[JPanel] = for {
    panel <- new JPanel(new GridLayout(3, 3)).monad
    jframe <- container
    _ <- of(jframe.getContentPane.add(panel))
  } yield panel

  val cells: Task[Seq[Cell]] = Task.evalOnce[Seq[Cell]] {
    for {
      i <- 0 to 2
      j <- 0 to 2
    } yield Cell(i, j, new JButton("_"))
  }

  def checkObservable(cell : Cell) : Observable[Check] = cell.button.eventObservable.map(_ => Check(cell.i, cell.j))

  override def input: Observable[Check] = Observable.fromTask(cells)
    .flatMapIterable(a => a)
    .map(checkObservable)
    .merge

  override def render(world: TicTacToe): Task[Unit] = for {
    _ <- container
    panel <- board
    _ <- of(panel.removeAll())
    _ <- renderButtons(panel, world.matrix)
    _ <- of(panel.repaint())
  } yield ()


  def renderButtons(panel: JPanel, matrix : Map[Position, Player]) : Task[Unit] = for {
    buttons <- cells
    _ <- of {
      buttons.foreach {
        case Cell(i, j, button) => matrix.get((i, j))
          .foreach(p => updateButton(button, p))
      }
    }
    _ <- of { buttons.map(_.button).foreach(panel.add) }
  } yield ()

  def updateButton(button : JButton, player: Player) : Unit = button.setText(player.toString)
}

object View {
  case class Cell(i : Int, j : Int, button : JButton)
}
