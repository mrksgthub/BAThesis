package Algorithms;

import Datatypes.SPQNode;
import Datatypes.Vertex;

import java.util.ArrayList;
import java.util.Hashtable;

public class Embedder {


    private final Hashtable<Vertex, ArrayList<Vertex>> embedding;

    public Embedder(Hashtable<Vertex, ArrayList<Vertex>> embedding) {
        this.embedding = embedding;

    }

    public void run(SPQNode node) {

        embedding.putIfAbsent(node.getStartVertex(), node.getStartVertex().getAdjecentVertices());
        embedding.putIfAbsent(node.getSinkVertex(), node.getSinkVertex().getAdjecentVertices());

        assert (node.getStartVertex() != null);
        assert (node.getSinkVertex() != null);

        for (SPQNode child : node.getMergedChildren()
        ) {
            run(child);
        }


    }


}
