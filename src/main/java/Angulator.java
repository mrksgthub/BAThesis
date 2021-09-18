import org.apache.commons.lang3.tuple.MutablePair;
import org.jgrapht.graph.DefaultEdge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

public class Angulator {


    private final Hashtable<TreeVertex, ArrayList<TreeVertex>> embedding;
    private final SPQTree tree;
    private FaceGenerator<TreeVertex, DefaultEdge> treeVertexFaceGenerator;

    public Angulator(SPQTree tree, Hashtable<TreeVertex, ArrayList<TreeVertex>> embedding, FaceGenerator<TreeVertex, DefaultEdge> treeVertexFaceGenerator) {
        this.tree = tree;
        this.embedding = embedding;
        this.treeVertexFaceGenerator = treeVertexFaceGenerator;

    }


    /**
     * Wandelt die berechneten Spiralitäten in Winkel um und fügt diese den orthogonalen Repräsentationen der Facetten
     * hinzu.
     *
     */
    public void run()  {

        // initialize
        HashMap<MutablePair<TreeVertex, TreeVertex>, Integer> pairIntegerMap = new HashMap<>(); // Diese Map wird alle Kanten und deren Winkel enthalten.
        long startTime = System.currentTimeMillis();
        // Rekrusives bestimmen der Winkel: https://arxiv.org/abs/2008.03784 Abschnitt 4 Construction Algorithm:
        winkelHinzufuegen(tree.getRoot(), pairIntegerMap);

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Winkel Section  :" + elapsedTime);

        long startTime3 = System.currentTimeMillis();
        for (MutablePair<TreeVertex, TreeVertex> pair :
                treeVertexFaceGenerator.adjFaces2.keySet()) {
            pairIntegerMap.putIfAbsent(pair, 0);
        }

        // Füge Winkel zu den Faces hinzu
        for (PlanarGraphFace<TreeVertex, DefaultEdge> face : treeVertexFaceGenerator.planarGraphFaces
        ) {
            for (MutablePair<TreeVertex, TreeVertex> pair :
                    face.getOrthogonalRep().keySet()) {
                face.getOrthogonalRep().put(pair, pairIntegerMap.get(pair));
            }
        }


        long stopTime3 = System.currentTimeMillis();
        long elapsedTime3 = stopTime3 - startTime3;
        System.out.println("Angulator Section  :" + elapsedTime3);
    }


    public void winkelHinzufuegen(SPQNode root, HashMap<MutablePair<TreeVertex, TreeVertex>, Integer> hashmap) {

        for (SPQNode node :
                root.getMergedChildren()) {
            winkelHinzufuegen(node, hashmap);
        }
        if (root.getMergedChildren().size() > 1 && !root.isIsroot()) {
            root.computeOrthogonalRepresentation(hashmap);
        }

    }


    /**
     * Wie run(), aber mit einem Test, um zu sehen, ob man auch einen gültigen Graphen erstellt hat.
     *
     * @throws Exception
     */
    public void runDebug() throws Exception {

        //    this.treeVertexFaceGenerator = new FaceGenerator<>(tree.constructedGraph, tree.getRoot().getStartVertex(), tree.getRoot().getSinkVertex(), embedding);
        //    treeVertexFaceGenerator.generateFaces2(); // counterclockwise = inner, clockwise = outerFacette

        //    DefaultDirectedWeightedGraph<TreeVertex, DefaultWeightedEdge> treeVertexDefaultEdgeDefaultDirectedWeightedGraph = treeVertexFaceGenerator.generateFlowNetworkLayout2();
        //    treeVertexFaceGenerator.generateCapacities();


        HashMap<MutablePair<TreeVertex, TreeVertex>, Integer> pairIntegerMap = new HashMap<>();

        for (MutablePair<TreeVertex, TreeVertex> pair :
                treeVertexFaceGenerator.adjFaces2.keySet()) {
            pairIntegerMap.put(pair, 0);
        }

        winkelHinzufuegen(tree.getRoot(), pairIntegerMap);

        List<PlanarGraphFace<TreeVertex, DefaultEdge>> test = new ArrayList<>();
        HashMap<TreeVertex, Integer> anglesMap = new HashMap<>();



        for (PlanarGraphFace<TreeVertex, DefaultEdge> face : treeVertexFaceGenerator.planarGraphFaces
        ) {
            int edgeCount = 0;
            for (MutablePair<TreeVertex, TreeVertex> pair :
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


        for (TreeVertex vertex: anglesMap.keySet()
        ) {
            Integer integer = anglesMap.get(vertex);
            if (integer == 4) {
            } else {
                System.out.println(("FehlerWinkel"));
            }

        }

    }


}
