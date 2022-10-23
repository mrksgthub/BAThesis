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


/**
 * Diese Klasse implementiert die Durchführung der Festlegung der Winkel.
 *
 *
 */
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

    /**
     * Führt die Umwandlung von Spiralitäten in WInkel durch und speichert die Winkel in hashmap
     *
     *
     * @param root Wurzel des SPQ*-Baums, dessen Spiralitäten festgelegt wurden
     * @param hashmap - Hashmap, in welcher die Winkel gespeichert werden, bei den richtigen TupleEdges gespeichert werden.
     */
    private void winkelHinzufuegen(SPQNode root, HashMap<TupleEdge<Vertex, Vertex>, Integer> hashmap) {



        Deque<SPQNode> s = DFSIterator.buildPostOrderStackPlanarityTest(root);

        while (!s.isEmpty()) {

            SPQNode node = s.pop();
            if (node.getSpqChildren().size() > 1) {
                node.computeAngles(hashmap);
            }
        }

    }

    /**
     * Führt einen iterativen pre-order Durchlauf der Knoten durch, um die Spiralitäten festzulegen
     *
     *
     * @param root - linkes Kind der Wurzel.
     */
    private void computeSpirality(SPQNode root) {



        Deque<SPQNode> s = DFSIterator.buildPreOrderStack(root);

        while (!s.isEmpty()) {
            s.pop().setSpiralityOfChildren();
        }

    }

    /**
     * führt flowAlg.setOrthogonalRep(); aus, was bedeutet, dass die Winkel in den PlanarGraphFace Objekten, auf die
     * das Max-Flow Objekt eine Referenz hat aus dem gültigen Fluss berechnet werden.
     *
     *
     * @param flowAlg
     */
    public void runMaxFlowAngles(MaxFlow flowAlg) {
        flowAlg.setOrthogonalRep();
    }

    public void runMinFlowAngles(MinFlow flowAlg) {
        flowAlg.setOrthogonalRep();
    }



}
