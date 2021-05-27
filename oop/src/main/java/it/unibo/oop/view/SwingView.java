package it.unibo.oop.view;

public class SwingView extends AbstractClickCellSource implements TicTacToeView {
    public static SwingView createAndShow() {
        return new SwingView();
    }

    private SwingView() {

    }
    @Override
    public void attach(Observer observer) {

    }

    @Override
    public void render(ViewBoard viewBoard) {

    }

    @Override
    public void winner(String player) {

    }
}
