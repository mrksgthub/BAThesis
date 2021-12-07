package Testing;

import Datastructures.SPQNode;
import Datastructures.SPQStarTree;
import Datastructures.Vertex;
import GraphGenerators.SPQGenerator;
import Helperclasses.SPQExporter;
import Helperclasses.SPQImporter;
import PlanarityAndAngles.FaceGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GraphBuilder {

    private final String filePathString;
    private static int counter = 0;


    public GraphBuilder(String filePathString) {

        this.filePathString = filePathString;
    }

    public static void main(String[] args) throws Exception {


        int runs = 30;
        SPQStarTree tree;
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

                SPQExporter spqExporter = new SPQExporter();
            //    spqExporter.run(root);
                spqExporter.run(root, "C:/a.txt");


                SPQImporter spqImporter = new SPQImporter();
                spqImporter.runFromFile("C:/a.txt");

                DirectedMultigraph<Vertex, DefaultEdge> graph = spqImporter.getTree().getConstructedGraph();

                tree = spqImporter.getTree();
                root = tree.getRoot();


                /*      Embedder embedder = new Embedder(embedding);
                embedder.run(root);*/

                FaceGenerator<Vertex, DefaultEdge> treeVertexFaceGenerator = new FaceGenerator<>(tree.getConstructedGraph(), root.getSourceVertex(), root.getSinkVertex());
                treeVertexFaceGenerator.generateFaces();


                System.out.println("Anzahl Faces:" + treeVertexFaceGenerator.getPlanarGraphFaces().size());


                int faces = treeVertexFaceGenerator.getPlanarGraphFaces().size();
                int nodes = graph.vertexSet().size();

                try {
                    Files.copy(Paths.get("C:/a.txt"), Paths.get("C:/" + nodes + "N" + faces + "F.txt"));
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

        }


    }


    public boolean run(int CHANCE_OF_P, int OPS, int maxDeg, int einfachheit, int mode, boolean isInvalidAllowed) {


        //   runs = 30;
        SPQStarTree tree;
        SPQNode root;


        SPQGenerator spqGenerator = new SPQGenerator();
        boolean valid = spqGenerator.generateGraph(OPS, CHANCE_OF_P, maxDeg, einfachheit, mode);


        if (valid || isInvalidAllowed) {
            tree = spqGenerator.getTree();
            root = spqGenerator.getRoot();

            DirectedMultigraph<Vertex, DefaultEdge> graph = tree.getConstructedGraph();
            FaceGenerator<Vertex, DefaultEdge> treeVertexFaceGenerator = new FaceGenerator<>(tree.getConstructedGraph(), root.getSourceVertex(), root.getSinkVertex());
            treeVertexFaceGenerator.generateFaces();

            System.out.println("Anzahl Faces:" + treeVertexFaceGenerator.getPlanarGraphFaces().size());

            int faces = treeVertexFaceGenerator.getPlanarGraphFaces().size();
            int nodes = graph.vertexSet().size();


            SPQExporter spqExporter = new SPQExporter();
            File filePath = new File(filePathString,
                    nodes + "N" + faces + "F"+ "D"+counter++ +".dot");

            spqExporter.run(root, filePath.toString());
            return true;
        } else {
            return false;
        }



    }


}
