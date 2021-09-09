import org.apache.commons.lang3.tuple.MutablePair;
import org.jgrapht.graph.DefaultEdge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class Angulator {


    private final Hashtable<TreeVertex, ArrayList<TreeVertex>> embedding;
    private final SPQTree tree;
    private FaceGenerator<TreeVertex, DefaultEdge> treeVertexFaceGenerator;

    public Angulator(SPQTree tree, Hashtable<TreeVertex, ArrayList<TreeVertex>> embedding, FaceGenerator<TreeVertex, DefaultEdge> treeVertexFaceGenerator) {
        this.tree = tree;
        this.embedding = embedding;
        this.treeVertexFaceGenerator = treeVertexFaceGenerator;

    }


    public void run() throws Exception {


        HashMap<MutablePair<TreeVertex, TreeVertex>, Integer> pairIntegerMap = new HashMap<>();
        long startTime = System.currentTimeMillis();
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


}
