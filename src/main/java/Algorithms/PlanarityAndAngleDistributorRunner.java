package Algorithms;

import Algorithms.Didimo.Angulator;
import Algorithms.Didimo.DidimoRepresentability;
import Algorithms.Flow.MaxFlow;
import Datatypes.SPQNode;
import Datatypes.SPQTree;
import Datatypes.Vertex;
import GUI.GraphDrawOptions;
import org.jgrapht.graph.DefaultEdge;

import java.util.ArrayList;
import java.util.Hashtable;

public class PlanarityAndAngleDistributorRunner {


    SPQTree tree;
    SPQNode root;
    double time = Integer.MAX_VALUE;
    FaceGenerator<Vertex, DefaultEdge> treeVertexFaceGenerator;
    Hashtable<Vertex, ArrayList<Vertex>> embedding;

    public PlanarityAndAngleDistributorRunner(SPQTree tree, SPQNode root) {
        this.tree = tree;
        this.root = root;
    }

    public SPQTree getTree() {
        return tree;
    }

    public void setTree(SPQTree tree) {
        this.tree = tree;
    }

    public SPQNode getRoot() {
        return root;
    }

    public void setRoot(SPQNode root) {
        this.root = root;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public FaceGenerator<Vertex, DefaultEdge> getTreeVertexFaceGenerator() {
        return treeVertexFaceGenerator;
    }

    public void setTreeVertexFaceGenerator(FaceGenerator<Vertex, DefaultEdge> treeVertexFaceGenerator) {
        this.treeVertexFaceGenerator = treeVertexFaceGenerator;
    }

    public Hashtable<Vertex, ArrayList<Vertex>> getEmbedding() {
        return embedding;
    }

    public void setEmbedding(Hashtable<Vertex, ArrayList<Vertex>> embedding) {
        this.embedding = embedding;
    }

    public void run(boolean wasAlgorithmnSelected, GraphDrawOptions.WinkelAlgorithmus algorithmm) {
        if (wasAlgorithmnSelected) {
           // embedding = new Hashtable<>();
          //  Embedder embedder = new Embedder(embedding);
           // embedder.run(root);

            tree.generateAdjecencyListMaP(tree.getRoot());
            embedding = tree.getVertexToAdjecencyListMap();
            treeVertexFaceGenerator = new FaceGenerator<>(tree.getConstructedGraph(), root.getStartVertex(), root.getSinkVertex(), embedding);
            treeVertexFaceGenerator.generateFaces();


            long startTime3 = System.currentTimeMillis();
            if (algorithmm == GraphDrawOptions.WinkelAlgorithmus.DIDIMO) {

                DidimoRepresentability didimoRepresentability = new DidimoRepresentability(tree, root);
                didimoRepresentability.run();

               // root.getMergedChildren().get(0).computeSpirality();
                tree.computeSpirality(root.getMergedChildren().get(0));

                Angulator angulator = new Angulator(tree, treeVertexFaceGenerator.getPlanarGraphFaces());
                try {
                    angulator.run();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            } else if (algorithmm == GraphDrawOptions.WinkelAlgorithmus.PUSH_RELABEL) {
                MaxFlow test = new MaxFlow(tree, treeVertexFaceGenerator.getPlanarGraphFaces());
                test.run3();
            }
            long stopTime3 = System.currentTimeMillis();
            time = stopTime3 - startTime3;

        }
    }
}










