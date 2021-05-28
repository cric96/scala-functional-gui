package it.unibo.oop.controller;

import it.unibo.oop.tictactoe.TicTacToe;
import it.unibo.oop.view.TicTacToeView;

public class TicTacToeGame implements Game {
    private final TicTacToe ticTacToe;
    private final TicTacToeView ticTacToeView;

    public static TicTacToeGame playWith(final TicTacToe ticTacToe, final TicTacToeView ticTacToeView) {
        return new TicTacToeGame(ticTacToe, ticTacToeView);
    }

    private TicTacToeGame(final TicTacToe ticTacToe, final TicTacToeView ticTacToeView) {
        this.ticTacToe = ticTacToe;
        this.ticTacToeView = ticTacToeView;
    }
    //it is good for you??
    @Override
    public void start() {
        ticTacToeView.attach(this);
        ticTacToeView.render(ViewBoardFactory.fromTicTacToe(ticTacToe));
    }

    @Override
    public void notify(final int x, final int y) {
        ticTacToe.update(x, y);
        ticTacToeView.render(ViewBoardFactory.fromTicTacToe(ticTacToe));
        if(ticTacToe.isOver()) {
            ticTacToeView.winner(ticTacToe.getTurn().getStringRepresentation());
        }
    }
}
