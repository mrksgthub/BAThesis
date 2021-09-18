import org.jgrapht.graph.DefaultEdge;

import java.util.ArrayList;
import java.util.Hashtable;

public class DidimoTestAndAngles {


    SPQTree tree;
    SPQNode root;
    double time = Integer.MAX_VALUE;
    FaceGenerator<TreeVertex, DefaultEdge> treeVertexFaceGenerator;
    Hashtable<TreeVertex, ArrayList<TreeVertex>> embedding;

    public DidimoTestAndAngles(SPQTree tree, SPQNode root) {
        this.tree = tree;
        this.root = root;
    }


    public void run(boolean wasAlgorithmnSelected, GraphDrawOptions.WinkelAlgorithmus algorithmm) {
        if (wasAlgorithmnSelected) {
            embedding = new Hashtable<>();
            Embedder embedder = new Embedder(embedding, root);
            embedder.run(root);

            treeVertexFaceGenerator = new FaceGenerator<>(tree.constructedGraph, root.getStartVertex(), root.getSinkVertex(), embedding);
            treeVertexFaceGenerator.generateFaces2();


            long startTime3 = System.currentTimeMillis();
            if (algorithmm == GraphDrawOptions.WinkelAlgorithmus.DIDIMO) {

                DidimoRepresentability didimoRepresentability = new DidimoRepresentability(tree, root);
                didimoRepresentability.run();

                root.getMergedChildren().get(0).computeSpirality();

                Angulator angulator = new Angulator(tree, embedding, treeVertexFaceGenerator);
                try {
                    angulator.run();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            } else if (algorithmm == GraphDrawOptions.WinkelAlgorithmus.PUSH_RELABEL) {
                MaxFlow test = new MaxFlow(tree, root, treeVertexFaceGenerator);
                test.run3();
            }
            long stopTime3 = System.currentTimeMillis();
            time = stopTime3 - startTime3;

        }
    }
}










