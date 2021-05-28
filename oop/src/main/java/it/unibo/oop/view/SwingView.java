package it.unibo.oop.view;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SwingView extends AbstractClickCellSource implements TicTacToeView {
    private static final String WINDOW_NAME = "TicTacToo OOP";
    private static final Integer CELLS = 3;
    private final JFrame frame;
    private final JPanel mainPanel;
    private final java.util.List<JButton> buttons;
    public static SwingView createAndShow(final int width, final int height) {
        final SwingView view = new SwingView(width, height);
        view.visualizeFrame();
        return view;
    }

    private SwingView(final int width, final int height) {
        frame = new JFrame(WINDOW_NAME);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        buttons = initializeBoard();
        mainPanel = new JPanel(new GridLayout(0, CELLS));
        buttons.forEach(mainPanel::add);
        frame.getContentPane().add(mainPanel);
    }

    @Override
    public void render(ViewBoard viewBoard) {
        SwingUtilities.invokeLater(() -> {
            var elements = viewBoard.getAllBoard().stream().flatMap(Collection::stream).collect(Collectors.toList());
            IntStream.range(0, elements.size()).forEach(i -> buttons.get(i).setText(elements.get(i)));
        });
    }

    @Override
    public void winner(String player) {
        SwingUtilities.invokeLater(() -> frame.setTitle("Game Over! The winner is " + player));
    }

    private void visualizeFrame() {
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private java.util.List<JButton> initializeBoard() {
        return IntStream.range(0, CELLS)
                .boxed()
                .flatMap(row -> IntStream.range(0, CELLS).mapToObj(column -> initButton(row, column)))
                .collect(Collectors.toUnmodifiableList());
    }

    private JButton initButton(int row, int column) {
        final JButton button = new JButton();
        button.addActionListener(ev -> this.notifyAll(row, column));
        return button;
    }
}
