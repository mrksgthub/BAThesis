import org.jgrapht.alg.flow.EdmondsKarpMFImpl;
import org.jgrapht.alg.interfaces.MaximumFlowAlgorithm;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class MaxFLow {


    @Test
    public void run() {

        SPQTree tree;
        SPQNode root;


        SPQImporter spqImporter = new SPQImporter("C:/a.txt");
        spqImporter.run();


        tree = spqImporter.tree;
        root = tree.getRoot();


        Hashtable<TreeVertex, ArrayList<TreeVertex>> embedding = new Hashtable<>();
        Embedder embedder = new Embedder(embedding, root);
        embedder.run(root);


        FaceGenerator<TreeVertex, DefaultEdge> treeVertexFaceGenerator = new FaceGenerator<>(tree.constructedGraph, root.getStartVertex(), root.getSinkVertex(), embedding);
        treeVertexFaceGenerator.generateFaces2();


        MaxFLow maxFLow = new MaxFLow();

        DirectedWeightedMultigraph<TreeVertex, DefaultWeightedEdge> simple =
                new DirectedWeightedMultigraph<>(DefaultWeightedEdge.class);


        TreeVertex solverSource = new TreeVertex("solverSource");
        TreeVertex solverSink = new TreeVertex("solverSink");

        simple.addVertex(solverSource);
        simple.addVertex(solverSink);

        // solverSource to Vertex
        int neighbors = 0;
        for (TreeVertex vertex : tree.constructedGraph.vertexSet()
        ) {

            neighbors = tree.constructedGraph.outDegreeOf(vertex) + tree.constructedGraph.inDegreeOf(vertex);
            simple.addVertex(vertex);
            DefaultWeightedEdge e1 = simple.addEdge(solverSource, vertex);
            simple.setEdgeWeight(e1, 4-neighbors);
        }


        int neighborOfFace = 0;

        // OuterFace
        List<TreeVertex> vertexList = treeVertexFaceGenerator.listOfFaces2.get(0);
        TreeVertex outerFace = treeVertexFaceGenerator.planarGraphFaces.get(0);

        // vertexList.size() - 1 Aufgrund der Struktur von vertexList: Das erste und letzte Element sind die gleichen die Formel bleibt 2*d(f) +/- 4
        neighborOfFace = 2 * (vertexList.size() - 1) + 4 - (vertexList.size() - 1);

        // Face zu Sink
        TreeVertex face = treeVertexFaceGenerator.planarGraphFaces.get(0);
        simple.addVertex(face);
        DefaultWeightedEdge edge = simple.addEdge(face, solverSink);
        simple.setEdgeWeight(edge, neighborOfFace);

        // Vertex zu Face
        for (int j = 0; j < vertexList.size() - 1; j++) {
            TreeVertex vertex = vertexList.get(j);
            neighbors = tree.constructedGraph.outDegreeOf(vertex) + tree.constructedGraph.inDegreeOf(vertex);
            edge = simple.addEdge(vertex, face);
            simple.setEdgeWeight(edge, 4 - neighbors);
        }


        System.out.println("OuterFace to Sink: " + neighborOfFace);

        // Inner Faces:

        for (int i = 1; i < treeVertexFaceGenerator.listOfFaces2.size(); i++) {

            vertexList = treeVertexFaceGenerator.listOfFaces2.get(i);

            neighborOfFace = 2 * (vertexList.size() - 1) - 4 - (vertexList.size() - 1);

            // Face zu Sink
            face = treeVertexFaceGenerator.planarGraphFaces.get(i);
            simple.addVertex(face);
            edge = simple.addEdge(face, solverSink);
            simple.setEdgeWeight(edge, neighborOfFace);

            System.out.println("InnerFace to Sink: " + neighborOfFace);

            // Vertex zu Face
            for (int j = 0; j < vertexList.size() - 1; j++) {
                TreeVertex vertex = vertexList.get(j);
                neighbors = tree.constructedGraph.outDegreeOf(vertex) + tree.constructedGraph.inDegreeOf(vertex);
                edge = simple.addEdge(vertex, face);
                simple.setEdgeWeight(edge, 4 - neighbors);
            }

        }


        MaximumFlowAlgorithm<TreeVertex, DefaultWeightedEdge> test33 = new EdmondsKarpMFImpl<>(simple);


        test33.getMaximumFlow(solverSource, solverSink);
        test33.getFlowMap();




    }



}
