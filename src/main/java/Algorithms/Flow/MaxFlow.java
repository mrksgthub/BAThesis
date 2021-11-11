package Algorithms.Flow;

import Algorithms.FaceGenerator;
import Datatypes.*;
import org.jgrapht.alg.flow.PushRelabelMFImpl;
import org.jgrapht.alg.interfaces.MaximumFlowAlgorithm;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaxFlow {


    public Map<DefaultWeightedEdge, Double> flowMap;
    public Map<DefaultWeightedEdge, Double> flowMap2;
    private Vertex solverSource;
    private Vertex solverSink;
    private SPQTree tree;
    private SPQNode root;
    private FaceGenerator<Vertex, DefaultEdge> treeVertexFaceGenerator;
    private int counter;
    private DirectedWeightedMultigraph<Vertex, DefaultWeightedEdge> simple = new DirectedWeightedMultigraph<>(DefaultWeightedEdge.class);

    public MaxFlow(SPQTree tree, SPQNode root, FaceGenerator<Vertex, DefaultEdge> treeVertexFaceGenerator) {
        this.tree = tree;
        this.root = root;
        this.treeVertexFaceGenerator = treeVertexFaceGenerator;
    }

    public FaceGenerator<Vertex, DefaultEdge> getTreeVertexFaceGenerator() {
        return treeVertexFaceGenerator;
    }

    public void setTreeVertexFaceGenerator(FaceGenerator<Vertex, DefaultEdge> treeVertexFaceGenerator) {
        this.treeVertexFaceGenerator = treeVertexFaceGenerator;
    }

    public void run() {


        solverSource = new Vertex("solverSource");
        solverSink = new Vertex("solverSink");

        generateFlowGraph(tree, treeVertexFaceGenerator, simple);


        // MaximumFlowAlgorithm<Datatypes.TreeVertex, DefaultWeightedEdge> test33 = new EdmondsKarpMFImpl<>(simple);
        MaximumFlowAlgorithm<Vertex, DefaultWeightedEdge> test33 = new PushRelabelMFImpl<>(simple);

        MaximumFlowAlgorithm.MaximumFlow<DefaultWeightedEdge> maxFlowValue = test33.getMaximumFlow(solverSource, solverSink);
        flowMap = test33.getFlowMap();

        if (maxFlowValue.getValue() != counter) {
            throw new IllegalArgumentException("Blub");
        }


        setOrthogonalRep(flowMap, treeVertexFaceGenerator.getPlanarGraphFaces());

    }


    public void run2() {

        solverSource = new Vertex("solverSource");
        solverSink = new Vertex("solverSink");
        generateFlowGraph(tree, treeVertexFaceGenerator, simple);

        EdmondsKarp edmondsKarp = new EdmondsKarp(simple);

        edmondsKarp.initialize();


        flowMap2 = edmondsKarp.maxFlow;
        setOrthogonalRep(edmondsKarp.maxFlow, treeVertexFaceGenerator.getPlanarGraphFaces());

    }


    /**
     * Erstellt Flussnetzwerk, führt Push-Relabel Maxflow Algorithmus aus und legt dann Winkel in den orthogonalen
     * Repräsentationen der Faces fest.
     */
    public void run3() {

        solverSource = new Vertex("solverSource");
        solverSink = new Vertex("solverSink");
        generateFlowGraph(tree, treeVertexFaceGenerator, simple);

        PushRelabel pushRelabel = new PushRelabel(simple);

        pushRelabel.initialize();


        flowMap2 = pushRelabel.maxFlow;
        setOrthogonalRep(pushRelabel.maxFlow, treeVertexFaceGenerator.getPlanarGraphFaces());

    }


    /**
     * Erstellt ein für den Tamassia-Algorithmus zugeschnittenes Flussnetzwerk (Garg Tamassia 96).
     * Um die benötigten lower bounds zu gewährleisten müssen die capacities und Kanten angepasst werden.
     *
     *
     *
     * @param tree
     * @param treeVertexFaceGenerator
     * @param simple
     */
    private void generateFlowGraph(SPQTree tree, FaceGenerator<Vertex, DefaultEdge> treeVertexFaceGenerator, DirectedWeightedMultigraph<Vertex, DefaultWeightedEdge> simple) {
        simple.addVertex(solverSource);
        simple.addVertex(solverSink);

        // solverSource to Vertex
        int neighbors = 0;
        counter = 0;
        for (Vertex vertex : tree.getConstructedGraph().vertexSet()
        ) {
            neighbors = tree.getConstructedGraph().outDegreeOf(vertex) + tree.getConstructedGraph().inDegreeOf(vertex);
            simple.addVertex(vertex);
            DefaultWeightedEdge e1 = simple.addEdge(solverSource, vertex);
            simple.setEdgeWeight(e1, 4 - neighbors);
            counter += 4 - neighbors;

        }

        int neighborOfFace = 0;

        // OuterFace
        List<Vertex> vertexList = treeVertexFaceGenerator.getListOfFaces2().get(0);
        Vertex outerFace = treeVertexFaceGenerator.getPlanarGraphFaces().get(0);

        // vertexList.size() - 1 Aufgrund der Struktur von vertexList: Das erste und letzte Element sind die gleichen die Formel bleibt 2*d(f) +/- 4
        neighborOfFace = 2 * (vertexList.size() - 1) + 4 - (vertexList.size() - 1);
        if (neighborOfFace < 0) {
            throw new IllegalArgumentException("Negative Capacity");
        }


        // Face zu Sink
        Vertex face = treeVertexFaceGenerator.getPlanarGraphFaces().get(0);
        simple.addVertex(face);
        DefaultWeightedEdge edge = simple.addEdge(face, solverSink);
        simple.setEdgeWeight(edge, neighborOfFace);

        // Vertex zu Face
        for (int j = 0; j < vertexList.size() - 1; j++) {
            Vertex vertex = vertexList.get(j);
            neighbors = tree.getConstructedGraph().outDegreeOf(vertex) + tree.getConstructedGraph().inDegreeOf(vertex);
            edge = simple.addEdge(vertex, face);
            simple.setEdgeWeight(edge, 4 - neighbors);
        }


        // Inner Faces:
        for (int i = 1; i < treeVertexFaceGenerator.getListOfFaces2().size(); i++) {

            vertexList = treeVertexFaceGenerator.getListOfFaces2().get(i);

            neighborOfFace = 2 * (vertexList.size() - 1) - 4 - (vertexList.size() - 1);

            if (neighborOfFace < 0) {
                throw new IllegalArgumentException("Negative Capacity");
            }


            // Face zu Sink
            face = treeVertexFaceGenerator.getPlanarGraphFaces().get(i);
            simple.addVertex(face);
            edge = simple.addEdge(face, solverSink);
            simple.setEdgeWeight(edge, neighborOfFace);

            //    System.out.println("InnerFace to Sink: " + neighborOfFace);

            // Vertex zu Face
            for (int j = 0; j < vertexList.size() - 1; j++) {
                Vertex vertex = vertexList.get(j);
                neighbors = tree.getConstructedGraph().outDegreeOf(vertex) + tree.getConstructedGraph().inDegreeOf(vertex);
                edge = simple.addEdge(vertex, face);
                simple.setEdgeWeight(edge, 4 - neighbors);
            }

        }
    }


    private void setOrthogonalRep(Map<DefaultWeightedEdge, Double> flowMap, List<PlanarGraphFace<Vertex, DefaultEdge>> planarGraphFaces) {

        // Erstelle Map um die Kante (y,z) zu beommen, welche in Facette x auf Knoten z endet.
        HashMap<PlanarGraphFace<Vertex, DefaultEdge>, HashMap<Vertex, TupleEdge<Vertex, Vertex>>> map = new HashMap<>(); // Facette -> Map (Vertex x -> Kante (y,x) in Facette


        for (PlanarGraphFace<Vertex, DefaultEdge> face : planarGraphFaces
        ) {
            HashMap<Vertex, TupleEdge<Vertex, Vertex>> pairVectorMap = new HashMap<>();
            map.put(face, pairVectorMap);

            Map<TupleEdge<Vertex, Vertex>, Integer> s1 = face.getOrthogonalRep();
            for (TupleEdge<Vertex, Vertex> pair :
                    face.getOrthogonalRep().keySet()) {

                pairVectorMap.put(pair.getRight(), pair);

            }
        }

        // Weise der Kante y, aus Facette x. doe Knoten z als Endknoten hat den entsprechenden Wert zu
        for (DefaultWeightedEdge edge : flowMap.keySet()
        ) {

            if (simple.getEdgeSource(edge) != solverSink && simple.getEdgeSource(edge) != solverSource && simple.getEdgeTarget(edge) != solverSink && simple.getEdgeTarget(edge) != solverSource) {
                DirectedWeightedMultigraph<Vertex, DefaultWeightedEdge> graph = simple;

                HashMap<Vertex, TupleEdge<Vertex, Vertex>> m1 = map.get(graph.getEdgeTarget(edge));

                TupleEdge<Vertex, Vertex> pair = m1.get(graph.getEdgeSource(edge));

                PlanarGraphFace<Vertex, DefaultEdge> tempFace;
                Double aDouble = flowMap.get(edge);
                if (aDouble == 0.0) {
                    tempFace = (PlanarGraphFace<Vertex, DefaultEdge>) graph.getEdgeTarget(edge);
                    tempFace.getOrthogonalRep().put(pair, 1);
                    pair.setWinkel(1);
                } else if (aDouble == 1.0) {
                    tempFace = (PlanarGraphFace<Vertex, DefaultEdge>) graph.getEdgeTarget(edge);
                    tempFace.getOrthogonalRep().put(pair, 0);
                    pair.setWinkel(0);
                } else if (aDouble == 2.0) {
                    tempFace = (PlanarGraphFace<Vertex, DefaultEdge>) graph.getEdgeTarget(edge);
                    tempFace.getOrthogonalRep().put(pair, -1);
                    pair.setWinkel(-1);
                }

                //   System.out.println("test");
            }


        }


    }


}