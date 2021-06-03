package it.unibo.oop;

import it.unibo.oop.controller.Game;
import it.unibo.oop.controller.TicTacToeGame;
import it.unibo.oop.tictactoe.TicTacToe;
import it.unibo.oop.tictactoe.TicTacToeFactory;
import it.unibo.oop.view.SwingView;
import it.unibo.oop.view.TicTacToeView;

public class Main {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    public static void main(String[] args) {
        final TicTacToeView view = SwingView.createAndShow(WIDTH, HEIGHT);
        final TicTacToe model = TicTacToeFactory.startO();
        final Game game = TicTacToeGame.playWith(model, view);
        game.start();
    }
}
