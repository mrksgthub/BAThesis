package PlanarityAndAngles;

import Datastructures.PlanarGraphFace;
import Datastructures.SPQNode;
import Datastructures.TupleEdge;
import Datastructures.Vertex;
import Helperclasses.DFSIterator;
import PlanarityAndAngles.Flow.MaxFlow;
import PlanarityAndAngles.Flow.MinFlow;

import java.util.Deque;
import java.util.HashMap;
import java.util.List;

public class Angulator {


    public Angulator() {


    }


    /**
     * Wandelt die berechneten Spiralitäten in Winkel um und fügt diese den orthogonalen Repräsentationen der Facetten
     * hinzu.
     */
    public void runSpiralityAlg(SPQNode root, List<PlanarGraphFace<Vertex>> listOfFaces) {

        // initialize
        HashMap<TupleEdge<Vertex, Vertex>, Integer> pairIntegerMap = new HashMap<>(); // Diese Map wird alle Kanten und deren Winkel enthalten.
        long startTime = System.currentTimeMillis();

        computeSpirality(root.getSpqChildren().get(0));
        // Rekrusives bestimmen der Winkel: https://arxiv.org/abs/2008.03784 Abschnitt 4 Construction Algorithm:
        winkelHinzufuegen(root, pairIntegerMap);

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Winkel Section  :" + elapsedTime);

        long startTime3 = System.currentTimeMillis();

        // Füge Winkel zu den Faces hinzu
        for (PlanarGraphFace<Vertex> face : listOfFaces
        ) {
            for (TupleEdge<Vertex, Vertex> pair : face.getOrthogonalRep().keySet()) {
                pairIntegerMap.putIfAbsent(pair, 0);
                face.setEdgeAngle(pair, pairIntegerMap.get(pair));
            }
        }


        long stopTime3 = System.currentTimeMillis();
        long elapsedTime3 = stopTime3 - startTime3;
        System.out.println("Algorithms.Didimo.Angulator Section  :" + elapsedTime3);
    }


    private void winkelHinzufuegen(SPQNode root, HashMap<TupleEdge<Vertex, Vertex>, Integer> hashmap) {

    /*    for (SPQNode node :
                root.getSpqChildren()) {
            winkelHinzufuegen(node, hashmap);
        }
        if (root.getSpqChildren().size() > 1) { // IsRoot IS WICHTIG!!
            root.computeAngles(hashmap);
        }*/

        Deque<SPQNode> s = DFSIterator.buildPostOrderStackPlanarityTest(root);

        while (!s.isEmpty()) {

            SPQNode node = s.pop();
            if (node.getSpqChildren().size() > 1) {
                node.computeAngles(hashmap);
            }
        }

    }

    private void computeSpirality(SPQNode root) {

    /*    for (SPQNode node : root.getSpqChildren()
        ) {
            computeSpirality(node);
        }*/

        Deque<SPQNode> s = DFSIterator.buildPreOrderStack(root);

        while (!s.isEmpty()) {
            s.pop().setSpiralityOfChildren();
        }

    }

    public void runMaxFlowAngles(MaxFlow flowAlg) {
        flowAlg.setOrthogonalRep();
    }

    public void runMinFlowAngles(MinFlow flowAlg) {
        flowAlg.setOrthogonalRep();
    }



}
