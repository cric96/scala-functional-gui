package it.unibo.oop.controller;

import it.unibo.oop.view.ClickCellSource;

//a.k.a controller?
public interface Game extends ClickCellSource.Observer {
    void start();
}
