package it.unibo.oop.tictactoe;

public class TicTacToeFactory {
    private TicTacToeFactory() {}
    public static TicTacToe empty() {
        return new TicTacToe() {
            @Override
            public Player get(int x, int y) {
                return null;
            }

            @Override
            public void update(int x, int y) {

            }

            @Override
            public boolean isOver() {
                return false;
            }

            @Override
            public Player getTurn() {
                return null;
            }
        };
    }
}
