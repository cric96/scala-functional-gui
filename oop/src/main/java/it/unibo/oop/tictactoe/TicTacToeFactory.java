package it.unibo.oop.tictactoe;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TicTacToeFactory {
    private TicTacToeFactory() {}
    public static TicTacToe startX() {
        return new TicTacToeImpl(Player.X);
    }

    public static TicTacToe startO() {
        return new TicTacToeImpl(Player.O);
    }

    private static class TicTacToeImpl implements TicTacToe {
        private final List<Player> cells;
        private Player player;
        TicTacToeImpl(final Player player) {
            cells = IntStream.range(0, TicTacToe.SIZE * TicTacToe.SIZE)
                    .mapToObj(i -> Player.None)
                    .collect(Collectors.toList());
            this.player = player;
        }
        @Override
        public Player get(final int x, final int y) {
            checkCoordinates(x, y);
            return cells.get(linearCoord(x, y));
        }

        @Override
        public void update(final int x, final int y) {
            if(isOver()) { return; }
            var player =  cells.get(linearCoord(x, y));
            if(player != Player.None) {
                throw new IllegalStateException("in row " + x + " col " + y + " exists already a player");
            }
            cells.set(linearCoord(x, y), getTurn());
            if(!isOver()) {
                this.player = this.player.getOther();
            }
        }

        @Override
        public boolean isOver() {
            final var rowsCheck = IntStream.range(0, TicTacToe.SIZE).mapToObj(this::rowWinner);
            final var colCheck = IntStream.range(0, TicTacToe.SIZE).mapToObj(this::colWinner);
            return Stream.concat(rowsCheck, colCheck).anyMatch(a -> a) || diagonalWinner() || antiDiagonalWinner();
        }

        @Override
        public Player getTurn() {
            return player;
        }

        private void checkCoordinates(final int column, final int rows) {
            if(nonValidPosition(column) || nonValidPosition(rows)) {
                throw new IllegalArgumentException("column or row should be greater then 0 and lesser then " + TicTacToe.SIZE);
            }
        }

        private boolean nonValidPosition(final int coord) {
            return coord < 0 || coord >= TicTacToe.SIZE;
        }

        private int linearCoord(final int x, final int y) {
            return x * TicTacToe.SIZE + y;
        }

        private boolean rowWinner(final int row) {
            return IntStream.range(0, TicTacToe.SIZE).allMatch(col -> getTurn() == cells.get(linearCoord(row, col)));
        }

        private boolean colWinner(final int col) {
            return IntStream.range(0, TicTacToe.SIZE).allMatch(row -> getTurn() == cells.get(linearCoord(row, col)));
        }

        private boolean diagonalWinner() {
            return IntStream.range(0, TicTacToe.SIZE).allMatch(i -> getTurn() == cells.get(linearCoord(i, i)));
        }

        private boolean antiDiagonalWinner() {
            final var lastValue = TicTacToe.SIZE - 1;
            return IntStream.range(0, TicTacToe.SIZE).allMatch(i -> getTurn() == cells.get(linearCoord(i, lastValue - i)));
        }
    }
}
