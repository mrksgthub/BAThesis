import org.apache.commons.lang3.tuple.MutablePair;
import org.jgrapht.graph.DefaultEdge;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Hashtable;

public class TamassiaMaxFlowTest {


    @Test
    void name() {


        SPQImporter spqImporter = new SPQImporter("C:/a.txt");
        spqImporter.run();
        SPQTree tree = spqImporter.tree;

        SPQNode root = tree.getRoot();

        Hashtable<TreeVertex, ArrayList<TreeVertex>> embedding = new Hashtable<>();
        Embedder embedder = new Embedder(embedding, root);
        embedder.run(root);


       FaceGenerator<TreeVertex, DefaultEdge> treeVertexFaceGenerator = new FaceGenerator<>(tree.constructedGraph, root.getStartVertex(), root.getSinkVertex(), embedding);
        treeVertexFaceGenerator.generateFaces2();


        MaxFlow test = new MaxFlow(tree, root, treeVertexFaceGenerator);


        test.run();


        for (PlanarGraphFace<TreeVertex, DefaultEdge> face: test.getTreeVertexFaceGenerator().getPlanarGraphFaces()
             ) {
            int sum = 0;
            for (MutablePair<TreeVertex, TreeVertex> edge: face.orthogonalRep.keySet()
                 ) {
                sum += face.orthogonalRep.get(edge);
            }

            assert (Math.abs(sum) == 4);

        }







    }
}
