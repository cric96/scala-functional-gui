package it.unibo.oop.tictactoe;

public interface TicTacToe {
    int SIZE = 3;
    Player get(int x, int y);
    void update(int x, int y);
    boolean isOver();
    Player getTurn();
}
