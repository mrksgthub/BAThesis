import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;

public class graphBuilderST {


    public static void main(String[] args) throws Exception {


        int runs = 30;
        SPQTree tree;
        SPQNode root;

        int CHANCE_OF_P = 0;


        for (int j = 0; j < 6; j++) {

            CHANCE_OF_P += 5;
            int OPS = 2500;

            for (int i = 0; i < runs; i++) {


                OPS += 2000;

                SPQGenerator spqGenerator = new SPQGenerator(OPS, CHANCE_OF_P);
                spqGenerator.run();

                tree = spqGenerator.getTree();
                root = spqGenerator.getRoot();

                SPQExporter spqExporter = new SPQExporter(tree);
                spqExporter.run(root);
                spqExporter.run(root, "C:/a.txt");


                SPQImporter spqImporter = new SPQImporter("C:/a.txt");
                spqImporter.run();

                DirectedMultigraph<TreeVertex, DefaultEdge> graph = spqImporter.tree.constructedGraph;

                tree = spqImporter.tree;
                root = tree.getRoot();


                Hashtable<TreeVertex, ArrayList<TreeVertex>> embedding = new Hashtable<>();
                Embedder embedder = new Embedder(embedding, root);
                embedder.run(root);

                FaceGenerator<TreeVertex, DefaultEdge> treeVertexFaceGenerator = new FaceGenerator<>(tree.constructedGraph, root.getStartVertex(), root.getSinkVertex(), embedding);
                treeVertexFaceGenerator.generateFaces2();


                System.out.println("Anzahl Faces:" + treeVertexFaceGenerator.planarGraphFaces.size());


                int faces = treeVertexFaceGenerator.planarGraphFaces.size();
                int nodes = graph.vertexSet().size();

                try {
                    Files.copy(Paths.get("C:/a.txt"), Paths.get("C:/" + nodes + "N" + faces + "F.txt"));
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

        }


    }


}
