package it.unibo.oop.tictactoe;

public enum Player {
    X, O, None;

    public String getStringRepresentation() {
        return this == None ? " " : this.toString();
    }
}
