package Datatypes;

import org.apache.commons.lang3.tuple.MutablePair;

public class TupleEdge<L, R> extends MutablePair<L, R> {

    static int idCounter = 0;
    int id = 0;
    int winkel = -10;
    int counter = -20;

    public static int getIdCounter() {
        return idCounter;
    }

    public static void setIdCounter(int idCounter) {
        TupleEdge.idCounter = idCounter;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWinkel() {
        return winkel;
    }

    public void setWinkel(int winkel) {
        this.winkel = winkel;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public TupleEdge(L l, R r) {
        setLeft(l);
        setRight(r);
        id = idCounter++;

    }

    public TupleEdge(L l, R r, int winkel) {
        setLeft(l);
        setRight(r);
        this.winkel = winkel;
        id = idCounter++;

    }



    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + getLeft().hashCode();
        result = 31 * result + getRight().hashCode();
        return result;
    }



}
