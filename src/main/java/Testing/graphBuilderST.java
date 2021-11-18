package Testing;

import PlanarityAndAngles.FaceGenerator;
import Datatypes.SPQNode;
import Datatypes.SPQTree;
import Datatypes.Vertex;
import GraphGenerators.SPQGenerator;
import Helperclasses.SPQExporter;
import Helperclasses.SPQImporter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;

public class graphBuilderST {

    private final int chanceOfP;
    private final int chanceOfPIncr;
    private final int minOps;
    private final int opsIncrement;
    private final int maxDegree ;
    private final int chainLength ;
    private final String filePathString;



    public graphBuilderST(int minOps, int opsIncrement, int chanceOfP, int chanceOfPIncr, int maxDegree, int chainLength, String filePathString) {

        this.minOps = minOps;
        this.opsIncrement = opsIncrement;
        this.chanceOfP = chanceOfP;
        this.chanceOfPIncr = chanceOfPIncr;
        this.maxDegree = maxDegree;
        this.chainLength = chainLength;
        this.filePathString = filePathString;
    }

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

                DirectedMultigraph<Vertex, DefaultEdge> graph = spqImporter.getTree().getConstructedGraph();

                tree = spqImporter.getTree();
                root = tree.getRoot();


                Hashtable<Vertex, ArrayList<Vertex>> embedding = tree.getVertexToAdjecencyListMap();
          /*      Embedder embedder = new Embedder(embedding);
                embedder.run(root);*/

                FaceGenerator<Vertex, DefaultEdge> treeVertexFaceGenerator = new FaceGenerator<>(tree.getConstructedGraph(), root.getStartVertex(), root.getSinkVertex(), embedding);
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


    public void run(int CHANCE_OF_P, int OPS) {


      //   runs = 30;
        SPQTree tree;
        SPQNode root;


                SPQGenerator spqGenerator = new SPQGenerator(OPS, CHANCE_OF_P, maxDegree, chainLength);
                spqGenerator.generateGraph(OPS, CHANCE_OF_P);


                tree = spqGenerator.getTree();
                root = spqGenerator.getRoot();

             /*   SPQExporter spqExporter = new SPQExporter(tree);
                spqExporter.run(root);
                spqExporter.run(root, "C:/a.txt");


                SPQImporter spqImporter = new SPQImporter("C:/a.txt");
                spqImporter.run();*/

                DirectedMultigraph<Vertex, DefaultEdge> graph = tree.getConstructedGraph();
/*
                tree = spqImporter.getTree();
                root = tree.getRoot();*/


         /*       Hashtable<Vertex, ArrayList<Vertex>> embedding = new Hashtable<>();
                Embedder embedder = new Embedder(embedding);
                embedder.run(root);*/

                FaceGenerator<Vertex, DefaultEdge> treeVertexFaceGenerator = new FaceGenerator<>(tree.getConstructedGraph(), root.getStartVertex(), root.getSinkVertex(), tree.getVertexToAdjecencyListMap());
                treeVertexFaceGenerator.generateFaces();


                System.out.println("Anzahl Faces:" + treeVertexFaceGenerator.getPlanarGraphFaces().size());


                int faces = treeVertexFaceGenerator.getPlanarGraphFaces().size();
                int nodes = graph.vertexSet().size();


                    SPQExporter spqExporter = new SPQExporter(tree);
                  //  spqExporter.run(root);
                File filePath = new File(filePathString,
                        nodes + "N" + faces + "F.dot");

                    spqExporter.run(root, filePath.toString());

                 //   Files.copy(Paths.get("C:/a.txt"), Paths.get("C:/" + nodes + "N" + faces + "F.txt"));



            }




















}
