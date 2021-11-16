package Datatypes;

import org.apache.commons.lang3.tuple.MutablePair;

public class TupleEdge<L, R> extends MutablePair<L, R> {

    private static int idCounter = 0;
    private int id = 0;
    private int winkel = -10;
    private int counter = -20;

    public static int getIdCounter() {
        return idCounter;
    }

    public static void setIdCounter(int idCounter) {
        TupleEdge.idCounter = idCounter;
    }

    public static TupleEdge<Vertex, Vertex> reverseEdge(TupleEdge<Vertex, Vertex> edge, boolean newEdge) {

        if (newEdge) {
            return new TupleEdge<>(edge.getRight(), edge.getLeft(), edge.getWinkel()*-1);
        } else {
            return new TupleEdge<>(edge.getRight(), edge.getLeft());
        }

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


    @Override
    public boolean equals(Object obj) {
        TupleEdge<Vertex, Vertex> edge = (TupleEdge<Vertex, Vertex>) obj;
        TupleEdge<Vertex, Vertex> thisEdge = (TupleEdge<Vertex, Vertex>) this;

        if (thisEdge.left.getId() == edge.left.getId() && thisEdge.right.getId() == edge.right.getId()) {
            return true;
        } else {
            return false;
        }


    }


}
