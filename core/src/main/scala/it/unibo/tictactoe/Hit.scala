package it.unibo.tictactoe

import it.unibo.tictactoe.TicTacToe.Position

/**
 * the input received by the game that change some cell in the board marked by a position
 * @param position the position to change.
 */
case class Hit(position: Position)
