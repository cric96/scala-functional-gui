package it.unibo.oop;

import it.unibo.oop.controller.Game;
import it.unibo.oop.controller.TicTacToeGame;
import it.unibo.oop.tictactoe.TicTacToe;
import it.unibo.oop.tictactoe.TicTacToeFactory;
import it.unibo.oop.view.SwingView;
import it.unibo.oop.view.TicTacToeView;

public class Main {
    public static void main(String[] args) {
        final TicTacToeView view = SwingView.createAndShow();
        final TicTacToe model = TicTacToeFactory.empty();
        final Game game = TicTacToeGame.playWith(model, view);
        game.start();
    }
}
