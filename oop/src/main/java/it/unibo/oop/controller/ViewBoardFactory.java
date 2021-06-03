package it.unibo.oop.controller;

import it.unibo.oop.tictactoe.Player;
import it.unibo.oop.tictactoe.TicTacToe;
import it.unibo.oop.view.ViewBoard;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ViewBoardFactory {
    private ViewBoardFactory() {}
    static ViewBoard fromTicTacToe(final TicTacToe ticTacToe) {
        return new ViewBoard() {
            @Override
            public List<String> getRow(int row) {
                return IntStream.range(0, TicTacToe.SIZE)
                        .mapToObj(col -> ticTacToe.get(row, col))
                        .map(Player::getStringRepresentation)
                        .collect(Collectors.toList());
            }

            @Override
            public List<List<String>> getAllBoard() {
                return IntStream.range(0, TicTacToe.SIZE)
                        .mapToObj(this::getRow)
                        .collect(Collectors.toList());
            }
        };
    }
}
