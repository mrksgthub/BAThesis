package PlanarityAndAngles;

import Datastructures.SPQNode;
import Datastructures.SPQStarTree;
import Datastructures.Vertex;
import GUI.GraphDrawOptions;
import PlanarityAndAngles.Didimo.DidimoRepresentability;
import PlanarityAndAngles.Flow.MaxFlow;
import org.jgrapht.graph.DefaultEdge;

import java.util.ArrayList;
import java.util.Hashtable;

public class PlanarityAndAngleDistributorRunner {


    private SPQStarTree tree;
    private SPQNode root;
    private double time = Integer.MAX_VALUE;
    private FaceGenerator<Vertex, DefaultEdge> treeVertexFaceGenerator;
    private Hashtable<Vertex, ArrayList<Vertex>> embedding;

    public PlanarityAndAngleDistributorRunner(SPQStarTree tree, SPQNode root) {
        this.tree = tree;
        this.root = root;
    }

    public SPQStarTree getTree() {
        return tree;
    }

    public void setTree(SPQStarTree tree) {
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
            embedding = tree.getVertexToAdjecencyListMap();
            treeVertexFaceGenerator = new FaceGenerator<>(tree.getConstructedGraph(), root.getSourceVertex(), root.getSinkVertex());
            treeVertexFaceGenerator.generateFaces();


            long startTime3 = System.currentTimeMillis();
            long stopTime3 = System.currentTimeMillis();
            if (algorithmm == GraphDrawOptions.WinkelAlgorithmus.DIDIMO) {
                startTime3 = System.currentTimeMillis();
                DidimoRepresentability didimoRepresentability = new DidimoRepresentability();
                boolean isValid = didimoRepresentability.run(tree.getRoot());
                stopTime3 = System.currentTimeMillis();

                if (!isValid) {
                    throw new RuntimeException("inValidGraph");
                }

                Angulator angulator = new Angulator();
                try {
                    angulator.runSpiralityAlg(tree.getRoot(), treeVertexFaceGenerator.getPlanarGraphFaces());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            } else if (algorithmm == GraphDrawOptions.WinkelAlgorithmus.PUSH_RELABEL) {
                MaxFlow test = new MaxFlow(treeVertexFaceGenerator.getPlanarGraphFaces());
                startTime3 = System.currentTimeMillis();
                boolean isValid = test.runJGraphTPushRelabelImplementation();
                stopTime3 = System.currentTimeMillis();
                if (!isValid) {
                    throw new RuntimeException("inValidGraph");
                }


                Angulator angulator = new Angulator();
                try {
                    angulator.runMaxFlowAngles(test);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            time = stopTime3 - startTime3;

        }
    }
}










