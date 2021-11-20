package PlanarityAndAngles.Didimo;

import Datatypes.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Angulator {



    private final SPQStarTree tree;
    private final List<PlanarGraphFace<Vertex>> listOfFaces;

    public Angulator(SPQStarTree tree, List<PlanarGraphFace<Vertex>> listOfFaces) {
        this.tree = tree;
        this.listOfFaces = listOfFaces;

    }


    /**
     * Wandelt die berechneten Spiralitäten in Winkel um und fügt diese den orthogonalen Repräsentationen der Facetten
     * hinzu.
     *
     */
    public void run()  {

        // initialize
        HashMap<TupleEdge<Vertex, Vertex>, Integer> pairIntegerMap = new HashMap<>(); // Diese Map wird alle Kanten und deren Winkel enthalten.
        long startTime = System.currentTimeMillis();

        computeSpirality(tree.getRoot().getSpqStarChildren().get(0));
        // Rekrusives bestimmen der Winkel: https://arxiv.org/abs/2008.03784 Abschnitt 4 Construction Algorithm:
        winkelHinzufuegen(tree.getRoot(), pairIntegerMap);

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Winkel Section  :" + elapsedTime);

        long startTime3 = System.currentTimeMillis();
      //  for (TupleEdge<Vertex, Vertex> pair :
        //        treeVertexFaceGenerator.getAdjFaces2().keySet()) {
       //     pairIntegerMap.putIfAbsent(pair, 0);
      //  }

        // Füge Winkel zu den Faces hinzu
        for (PlanarGraphFace<Vertex> face : listOfFaces
        ) {
            for (TupleEdge<Vertex, Vertex> pair : face.getOrthogonalRep().keySet()) {
                pairIntegerMap.putIfAbsent(pair, 0);
            //    face.getOrthogonalRep().put(pair, pairIntegerMap.get(pair));
                face.setEdgeAngle(pair, pairIntegerMap.get(pair));
            }
        }

    /*    for (PlanarGraphFace<Vertex, DefaultEdge> face : treeVertexFaceGenerator.getPlanarGraphFaces()
        ) {
            for (TupleEdge<Vertex, Vertex> pair : face.getOrthogonalRep().keySet()) {
                face.getOrthogonalRep().put(pair, pairIntegerMap.get(pair));
            }
        }*/






        long stopTime3 = System.currentTimeMillis();
        long elapsedTime3 = stopTime3 - startTime3;
        System.out.println("Algorithms.Didimo.Angulator Section  :" + elapsedTime3);
    }


    private void winkelHinzufuegen(SPQNode root, HashMap<TupleEdge<Vertex, Vertex>, Integer> hashmap) {

        for (SPQNode node :
                root.getSpqStarChildren()) {
            winkelHinzufuegen(node, hashmap);
        }
        if (root.getSpqStarChildren().size() > 1) { // IsRoot IS WICHTIG!!
            root.computeAngles(hashmap);
        }

    }

    public void computeSpirality(SPQNode root) {

        root.setSpiralityOfChildren();

        for (SPQNode node : root.getSpqStarChildren()
        ) {
            computeSpirality(node);
        }

    }

}
