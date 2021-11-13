package Algorithms.Didimo;

import Algorithms.FaceGenerator;
import Datatypes.*;
import org.jgrapht.graph.DefaultEdge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

public class Angulator {



    private final SPQTree tree;
    private FaceGenerator<Vertex, DefaultEdge> treeVertexFaceGenerator;

    public Angulator(SPQTree tree,  FaceGenerator<Vertex, DefaultEdge> treeVertexFaceGenerator) {
        this.tree = tree;
        this.treeVertexFaceGenerator = treeVertexFaceGenerator;

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
        // Rekrusives bestimmen der Winkel: https://arxiv.org/abs/2008.03784 Abschnitt 4 Construction Algorithm:
        winkelHinzufuegen(tree.getRoot(), pairIntegerMap);

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Winkel Section  :" + elapsedTime);

        long startTime3 = System.currentTimeMillis();
        for (TupleEdge<Vertex, Vertex> pair :
                treeVertexFaceGenerator.getAdjFaces2().keySet()) {
            pairIntegerMap.putIfAbsent(pair, 0);
        }

        // Füge Winkel zu den Faces hinzu
        for (PlanarGraphFace<Vertex, DefaultEdge> face : treeVertexFaceGenerator.getPlanarGraphFaces()
        ) {
            for (TupleEdge<Vertex, Vertex> pair :
                    face.getOrthogonalRep().keySet()) {
                face.getOrthogonalRep().put(pair, pairIntegerMap.get(pair));
            }
        }


        long stopTime3 = System.currentTimeMillis();
        long elapsedTime3 = stopTime3 - startTime3;
        System.out.println("Algorithms.Didimo.Angulator Section  :" + elapsedTime3);
    }


    public void winkelHinzufuegen(SPQNode root, HashMap<TupleEdge<Vertex, Vertex>, Integer> hashmap) {

        for (SPQNode node :
                root.getMergedChildren()) {
            winkelHinzufuegen(node, hashmap);
        }
        if (root.getMergedChildren().size() > 1 && !root.isIsroot()) { // IsRoot IS WICHTIG!!
            root.computeOrthogonalRepresentation(hashmap);
        }

    }


    /**
     * Wie run(), aber mit einem Test, um zu sehen, ob man auch einen gültigen Graphen erstellt hat.
     *
     * @throws Exception
     */
    public void runDebug() throws Exception {

        //    this.treeVertexFaceGenerator = new Algorithms.FaceGenerator<>(tree.constructedGraph, tree.getRoot().getStartVertex(), tree.getRoot().getSinkVertex(), embedding);
        //    treeVertexFaceGenerator.generateFaces2(); // counterclockwise = inner, clockwise = outerFacette

        //    DefaultDirectedWeightedGraph<Datatypes.TreeVertex, DefaultWeightedEdge> treeVertexDefaultEdgeDefaultDirectedWeightedGraph = treeVertexFaceGenerator.generateFlowNetworkLayout2();
        //    treeVertexFaceGenerator.generateCapacities();


        HashMap<TupleEdge<Vertex, Vertex>, Integer> pairIntegerMap = new HashMap<>();

        for (TupleEdge<Vertex, Vertex> pair :
                treeVertexFaceGenerator.getAdjFaces2().keySet()) {
            pairIntegerMap.put(pair, 0);
        }

        winkelHinzufuegen(tree.getRoot(), pairIntegerMap);

        List<PlanarGraphFace<Vertex, DefaultEdge>> test = new ArrayList<>();
        HashMap<Vertex, Integer> anglesMap = new HashMap<>();



        for (PlanarGraphFace<Vertex, DefaultEdge> face : treeVertexFaceGenerator.getPlanarGraphFaces()
        ) {
            int edgeCount = 0;
            for (TupleEdge<Vertex, Vertex> pair :
                    face.getOrthogonalRep().keySet()) {
                face.getOrthogonalRep().put(pair, pairIntegerMap.get(pair));
                edgeCount += pairIntegerMap.get(pair);

                int angle = pairIntegerMap.get(pair);

                if (angle == -1) {
                    angle = 3;
                }
                if (angle == 0) {
                    angle = 2;
                }
                if (angle == 1) {
                    angle = 1;
                }

                if( anglesMap.putIfAbsent(pair.getRight(), angle) != null){

                    anglesMap.put(pair.getRight(), anglesMap.get(pair.getRight()) + angle);
                }
            }


            if (Math.abs(edgeCount) != 4) {
                //    assert(Math.abs(edgeCount) == 4);
                test.add(face);
                if (Math.abs(edgeCount) != 4) {
                    System.out.println("Fehler");

                }
            }

        }


        for (Vertex vertex: anglesMap.keySet()
        ) {
            Integer integer = anglesMap.get(vertex);
            if (integer == 4) {
            } else {
                System.out.println(("FehlerWinkel"));
            }

        }

    }


}
