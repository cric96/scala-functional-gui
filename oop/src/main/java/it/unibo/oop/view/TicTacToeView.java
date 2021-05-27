package it.unibo.oop.view;

public interface TicTacToeView extends ClickCellSource {
    void render(ViewBoard viewBoard);
    void winner(String player);
}
