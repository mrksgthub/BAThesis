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


    public void run() {

    //    this.treeVertexFaceGenerator = new FaceGenerator<>(tree.constructedGraph, tree.getRoot().getStartVertex(), tree.getRoot().getSinkVertex(), embedding);
    //    treeVertexFaceGenerator.generateFaces2(); // counterclockwise = inner, clockwise = outerFacette

    //    DefaultDirectedWeightedGraph<TreeVertex, DefaultWeightedEdge> treeVertexDefaultEdgeDefaultDirectedWeightedGraph = treeVertexFaceGenerator.generateFlowNetworkLayout2();
    //    treeVertexFaceGenerator.generateCapacities();



        HashMap<MutablePair<TreeVertex, TreeVertex>, Integer> pairIntegerMap = new HashMap<>();

        for (MutablePair<TreeVertex, TreeVertex> pair :
                treeVertexFaceGenerator.adjFaces2.keySet()) {
            pairIntegerMap.put(pair, 0);
        }

        winkelHinzufügen(tree.getRoot(), pairIntegerMap);


        List<PlanarGraphFace<TreeVertex, DefaultEdge>> test = new ArrayList<>();
        for (PlanarGraphFace<TreeVertex, DefaultEdge> face : treeVertexFaceGenerator.planarGraphFaces
        ) {
            int edgeCount = 0;
            for (MutablePair<TreeVertex, TreeVertex> pair :
                    face.getOrthogonalRep().keySet()) {
                face.getOrthogonalRep().put(pair, pairIntegerMap.get(pair));
                edgeCount += pairIntegerMap.get(pair);
            }

            if (Math.abs(edgeCount) != 4) {
                //    assert(Math.abs(edgeCount) == 4);
                test.add(face);
                if (Math.abs(edgeCount) == 4) {
                    System.out.println("Fehler");

                }
            }


        }









    }
















    public void winkelHinzufügen(SPQNode root, HashMap<MutablePair<TreeVertex, TreeVertex>, Integer> hashmap) {

        for (SPQNode node :
                root.getMergedChildren()) {
            winkelHinzufügen(node, hashmap);
        }
        if (root.getMergedChildren().size() > 1 && !root.isIsroot()) {
            root.computeOrthogonalRepresentation(hashmap);
        }

    }








}
