package it.unibo.oop.view;

import java.util.List;

public interface ViewBoard {
    List<String> getRow(int row);
    List<List<String>> getAllBoard();
}
