package it.unibo.swing.tictactoe

import it.unibo.Boundary
import it.unibo.swing.monadic._
import it.unibo.swing.tictactoe.View.Cell
import it.unibo.tictactoe.Hit
import it.unibo.tictactoe.TicTacToe
import it.unibo.tictactoe.TicTacToe.End
import it.unibo.tictactoe.TicTacToe.InProgress
import it.unibo.tictactoe.TicTacToe.Player
import it.unibo.tictactoe.TicTacToe.Position
import monix.eval.Task
import monix.reactive.Observable

import java.awt.GridLayout
import javax.swing._

class View(width: Int = 800, height: Int = 600) extends Boundary[TicTacToe, Hit] {
  private val title = "TicTacToe"
  private val empty = "_"
  private def endGame(winner: TicTacToe.Player) = s"Game ended! winner : $winner"

  private lazy val container: Task[JFrame] = for {
    frame <- new JFrame(title).lift
    _ <- io(frame.setSize(width, height))
    _ <- io(frame.setVisible(true))
    _ <- io(frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE))
  } yield frame

  private lazy val board: Task[JPanel] = for {
    panel <- new JPanel(new GridLayout(TicTacToe.defaultSize, TicTacToe.defaultSize)).lift
    jframe <- container
    _ <- io(jframe.getContentPane.add(panel))
  } yield panel

  private lazy val cells: Seq[Cell] = for {
    i <- 0 until TicTacToe.defaultSize
    j <- 0 until TicTacToe.defaultSize
  } yield Cell(i, j, new JButton(empty))

  override lazy val input: Observable[Hit] = Observable
    .fromIterable(cells)
    .map(liftToObservable)
    .merge

  override def consume(model: TicTacToe): Task[Unit] = {
    for {
      frame <- container.asyncBoundary(swingScheduler) //go to AWT Thread, todo it is a view task or controller task?
      panel <- board
      _ <- io(panel.removeAll())
      _ <- renderButtons(panel, model.board)
      _ <- renderVictory(model)
      _ <- io(frame.repaint()) //force repaint
      _ <- io(frame.setVisible(true)) //force repaint
    } yield ()
  }

  private def liftToObservable(cell: Cell): Observable[Hit] =
    cell.button.eventObservable.map(_ => Hit((cell.i, cell.j)))

  private def renderButtons(panel: JPanel, matrix: Map[Position, Player]): Task[Unit] = for {
    buttons <- io(cells)
    _ <- io {
      buttons.foreach { case Cell(i, j, button) =>
        matrix.get((i, j)).foreach(p => updateButton(button, p))
      }
    }
    _ <- io(buttons.map(_.button).foreach(panel.add))
  } yield ()

  private def updateButton(button: JButton, player: Player): Unit = button.setText(player.toString)

  private def renderVictory(toe: TicTacToe): Task[Unit] = toe match {
    case InProgress(_, _) => Task.pure {}
    case End(winner, _) =>
      for {
        frame <- container
        _ <- io(frame.setTitle(endGame(winner)))
      } yield ()
  }
}

object View {
  case class Cell(i: Int, j: Int, button: JButton)
}
