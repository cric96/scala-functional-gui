package it.unibo.oop.view;

import java.util.ArrayList;
import java.util.List;

public class AbstractClickCellSource implements ClickCellSource {
    private final List<Observer> observers = new ArrayList<>();
    @Override
    public void attach(Observer observer) {
        observers.add(observer);
    }

    protected void notifyAll(int x, int y) {
        observers.forEach(obs -> obs.notify(x, y));
    }
}
