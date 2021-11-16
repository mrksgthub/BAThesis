import Algorithms.Embedder;
import Algorithms.FaceGenerator;
import Algorithms.Flow.MaxFlow;
import Datatypes.PlanarGraphFace;
import Datatypes.SPQNode;
import Datatypes.SPQTree;
import Datatypes.Vertex;
import Helperclasses.SPQImporter;
import org.apache.commons.lang3.tuple.MutablePair;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Hashtable;

class TamassiaMaxFlowTest {


/*
    @Test
    void name() {


        // Helperclasses.SPQImporter spqImporter = new Helperclasses.SPQImporter("C:/a.txt");

        SPQImporter spqImporter = new SPQImporter("C:/Graphs/19139N2214F.txt");
        spqImporter.run();
        SPQTree tree = spqImporter.getTree();

        SPQNode root = tree.getRoot();

        Hashtable<Experimental.Vertex, ArrayList<Experimental.Vertex>> embedding = new Hashtable<>();
        Embedder embedder = new Embedder(embedding);
        embedder.run(root);


        FaceGenerator<Experimental.Vertex, DefaultEdge> treeVertexFaceGenerator = new FaceGenerator<>(tree.getConstructedGraph(), root.getStartVertex(), root.getSinkVertex(), embedding);
        treeVertexFaceGenerator.generateFaces2();


        MaxFlow test2 = new MaxFlow(tree, treeVertexFaceGenerator);
        test2.run();

        MaxFlow test = new MaxFlow(tree, treeVertexFaceGenerator);
        test.run2();


        MaxFlow test3 = new MaxFlow(tree, treeVertexFaceGenerator);
        test3.run3();


        for (DefaultWeightedEdge edge : test.flowMap2.keySet()
        ) {
            Double aDouble = test.flowMap2.get(edge);
            Double aDouble1 = test2.flowMap.get(edge);
            boolean asdf = aDouble == aDouble1;
        }


        for (PlanarGraphFace<Experimental.Vertex, DefaultEdge> face : test.getTreeVertexFaceGenerator().getPlanarGraphFaces()
        ) {
            int sum = 0;
            for (MutablePair<Experimental.Vertex, Experimental.Vertex> edge : face.getOrthogonalRep().keySet()
            ) {
                sum += face.getOrthogonalRep().get(edge);
            }

            assert (Math.abs(sum) == 4);

        }

    }
}
*/
}