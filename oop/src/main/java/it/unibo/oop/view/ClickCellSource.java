package it.unibo.oop.view;

public interface ClickCellSource {
    void attach(Observer observer);
    interface Observer {
        void notify(int X, int Y);
    }
}
