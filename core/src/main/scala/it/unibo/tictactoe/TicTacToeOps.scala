package it.unibo.tictactoe

import it.unibo.tictactoe.TicTacToe._
/**
 *  Game logic.
 */
object TicTacToeOps {
  private val gameSize = TicTacToe.defaultSize

  def advanceWith(ticTacToe: TicTacToe, hit: Position): TicTacToe = {
    val updateGame = for {
      _ <- rightPosition(ticTacToe, hit)
    } yield updateBoard(ticTacToe, hit)
    updateGame.getOrElse(ticTacToe)
  }

  def rightPosition(ticTacToe: TicTacToe, position: Position): Option[TicTacToe] =
    Some(ticTacToe).collect { case game: InProgress => game }.filterNot(_.board.contains(position))

  def updateBoard(ticTacToe: TicTacToe, position: Position): TicTacToe = ticTacToe match {
    case ticTacToe: InProgress =>
      val updated = ticTacToe.copy(ticTacToe.turn.other, updateMatrix(ticTacToe, position))
      someoneWon(updated.board).map(winner => End(winner, updated.board)).getOrElse(updated)
    case end => end
  }

  private def updateMatrix(ticTacToe: InProgress, position: Position): Map[Position, Player] =
    ticTacToe.board + (position -> ticTacToe.turn)

  private def someoneWon(game: Map[Position, Player]): Option[Player] =
    checkColumns(game).orElse(checkDiagonals(game)).orElse(checkRows(game))

  private def checkRows(game: Map[Position, Player]): Option[Player] = {
    def checkRow(row: Int): Option[Player] =
      isWinnerInPosition(getPlayerFrom(game, (0 until gameSize).map((row, _))))
    (0 until gameSize).map(checkRow).reduce((a, b) => a.orElse(b))
  }

  private def checkColumns(game: Map[Position, Player]): Option[Player] = {
    def checkColumn(col: Int): Option[Player] =
      isWinnerInPosition(getPlayerFrom(game, (0 until gameSize).map((_, col))))
    (0 until gameSize).map(checkColumn).reduce((a, b) => a.orElse(b))
  }

  private def checkDiagonals(game: Map[Position, Player]): Option[Player] = {
    def checkDiagonal: Option[Player] =
      isWinnerInPosition(getPlayerFrom(game, (0 until gameSize).map(pos => (pos, pos))))
    def checkAntiDiagonal: Option[Player] = {
      val positions = (0 until gameSize).zip(0.until(gameSize).reverse)
      isWinnerInPosition(getPlayerFrom(game, positions))
    }
    checkAntiDiagonal.orElse(checkDiagonal)
  }

  private def getPlayerFrom(game: Map[Position, Player], positions: Seq[Position]): Seq[Player] =
    positions.map(game.get).collect { case Some(p) => p }

  private def isWinnerInPosition(position: Seq[Player]): Option[Player] =
    isWinner(X, position).orElse(isWinner(O, position))

  private def isWinner(p: Player, positions: Seq[Player]): Option[Player] =
    Some(p).filter(p => (0 until gameSize).map(_ => p) == positions)
}
