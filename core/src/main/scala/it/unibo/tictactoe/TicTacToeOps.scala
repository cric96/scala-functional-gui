package it.unibo.tictactoe

import it.unibo.tictactoe.TicTacToe.{O, Player, Position, X}
import monix.eval.Task

object TicTacToeOps {
  private val gameSize = 3

  def advanceWith(ticTacToe : TicTacToe, hit : Hit) : TicTacToe = {
    val updateGame = for {
      _ <- rightPosition(ticTacToe, hit)
    } yield (act(ticTacToe, hit))
    updateGame.getOrElse(ticTacToe)
  }

  def processInput(game : TicTacToe, inputs : Seq[Hit]) : Task[TicTacToe] = inputs match {
    case input :: others => processInput(advanceWith(game, input), others)
    case empty => Task.pure(game)
  }

  private def rightPosition(ticTacToe: TicTacToe, hit: Hit) : Option[TicTacToe] = {
    Some(ticTacToe).collect { case game : InProgress => game }.filterNot(_.matrix.contains(hit.position))
  }

  private def act(ticTacToe: TicTacToe, hit: Hit) : TicTacToe = ticTacToe match {
    case ticTacToe : InProgress => val updated = ticTacToe.copy(ticTacToe.turn.other, updateMatrix(ticTacToe, hit))
      someoneWon(updated.matrix).map(winner => End(winner, updated.matrix)).getOrElse(updated)
    case end => end
  }

  private def updateMatrix(ticTacToe: InProgress, hit: Hit) : Map[Position, Player] = {
    ticTacToe.matrix + (hit.position -> ticTacToe.turn)
  }

  private def someoneWon(game : Map[Position, Player]) : Option[Player] = {
    checkColumns(game).orElse(checkDiagonals(game)).orElse(checkRows(game))
  }

  private def checkRows(game : Map[Position, Player]) : Option[Player] = {
    def checkRow(row : Int) : Option[Player] = {
      isWinnerInPosition(getPlayerFrom(game, (0 until gameSize).map((row, _))))
    }
    (0 until gameSize).map(checkRow).reduce((a, b) => a.orElse(b))
  }

  private def checkColumns(game : Map[Position, Player]) : Option[Player] = {
    def checkColumn(col : Int) : Option[Player] = {
      isWinnerInPosition(getPlayerFrom(game, (0 until gameSize).map((_, col))))
    }
    (0 until gameSize).map(checkColumn).reduce((a, b) => a.orElse(b))
  }

  private def checkDiagonals(game : Map[Position, Player]) : Option[Player] = {
    def checkDiagonal : Option[Player] = {
      isWinnerInPosition(getPlayerFrom(game, (0 until gameSize).map(pos => (pos, pos))))
    }
    def checkAntiDiagonal : Option[Player] = {
      val positions = (0 until gameSize).zip(0.until(gameSize).reverse)
      isWinnerInPosition(getPlayerFrom(game, positions))
    }
    checkAntiDiagonal.orElse(checkDiagonal)
  }

  private def getPlayerFrom(game : Map[Position, Player], positions : Seq[Position]) : Seq[Player] = {
    positions.map(game.get).collect { case Some(p) => p }
  }

  private def isWinnerInPosition(position: Seq[Player]) : Option[Player] = {
    isWinner(X, position).orElse(isWinner(O, position))
  }
  private def isWinner(p : Player, positions : Seq[Player]) : Option[Player] = {
    Some(p).filter(p => (0 until gameSize).map(_ => p) == positions)
  }
  implicit class RichPlayer(p : Player) {
    def other : Player = p match {
      case X => O
      case O => X
    }
  }
}
