package PlanarityAndAngles.Flow;

import PlanarityAndAngles.FaceGenerator;
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


    private Map<DefaultWeightedEdge, Double> flowMap;
    private Map<DefaultWeightedEdge, Double> flowMap2;
    private Vertex solverSource;
    private Vertex solverSink;
    private final SPQStarTree tree;
    private FaceGenerator<Vertex, DefaultEdge> treeVertexFaceGenerator;
    private List<PlanarGraphFace<Vertex>> planarGraphFaces;
    private int counter;
    private final DirectedWeightedMultigraph<Vertex, DefaultWeightedEdge> simple = new DirectedWeightedMultigraph<>(DefaultWeightedEdge.class);

    public MaxFlow(SPQStarTree tree, List<PlanarGraphFace<Vertex>> planarGraphFaces) {
        this.tree = tree;
        this.planarGraphFaces = planarGraphFaces;
    }

    public FaceGenerator<Vertex, DefaultEdge> getTreeVertexFaceGenerator() {
        return treeVertexFaceGenerator;
    }

    public void setTreeVertexFaceGenerator( List<PlanarGraphFace<Vertex>> planarGraphFaces) {
        this.planarGraphFaces = planarGraphFaces;
    }

    public void run() {


        solverSource = new Vertex("solverSource");
        solverSink = new Vertex("solverSink");

        generateFlowGraph(tree, planarGraphFaces, simple);


        // MaximumFlowAlgorithm<Datatypes.TreeVertex, DefaultWeightedEdge> test33 = new EdmondsKarpMFImpl<>(simple);
        MaximumFlowAlgorithm<Vertex, DefaultWeightedEdge> test33 = new PushRelabelMFImpl<>(simple);

        MaximumFlowAlgorithm.MaximumFlow<DefaultWeightedEdge> maxFlowValue = test33.getMaximumFlow(solverSource, solverSink);
        flowMap = test33.getFlowMap();

        if (maxFlowValue.getValue() != counter) {
            throw new IllegalArgumentException("Blub");
        }


        setOrthogonalRep(flowMap, planarGraphFaces);

    }


    public void run2() {

        solverSource = new Vertex("solverSource");
        solverSink = new Vertex("solverSink");
        generateFlowGraph(tree, planarGraphFaces, simple);

        EdmondsKarp edmondsKarp = new EdmondsKarp(simple);

        edmondsKarp.initialize();


        flowMap2 = edmondsKarp.maxFlow;
        setOrthogonalRep(edmondsKarp.maxFlow, planarGraphFaces);

    }


    /**
     * Erstellt Flussnetzwerk, führt Push-Relabel Maxflow Algorithmus aus und legt dann Winkel in den orthogonalen
     * Repräsentationen der Faces fest.
     */
    public void run3() {

        solverSource = new Vertex("solverSource");
        solverSink = new Vertex("solverSink");
        generateFlowGraph(tree, planarGraphFaces, simple);

        PushRelabel pushRelabel = new PushRelabel(simple);

        pushRelabel.initialize();


        flowMap2 = pushRelabel.maxFlow;
        setOrthogonalRep(pushRelabel.maxFlow, planarGraphFaces);

    }


    /**
     * Erstellt ein für den Tamassia-Algorithmus zugeschnittenes Flussnetzwerk (Garg Tamassia 96).
     * Um die benötigten lower bounds zu gewährleisten müssen die capacities und Kanten angepasst werden.
     *
     *
     *
     * @param tree
     * @param planarGraphFaces
     * @param simple
     */
    private void generateFlowGraph(SPQStarTree tree, List<PlanarGraphFace<Vertex>> planarGraphFaces, DirectedWeightedMultigraph<Vertex, DefaultWeightedEdge> simple) {
        simple.addVertex(solverSource);
        simple.addVertex(solverSink);

        // solverSource to Vertex
        int neighbors;
        counter = 0;
        for (Vertex vertex : tree.getConstructedGraph().vertexSet()
        ) {
            neighbors = tree.getConstructedGraph().outDegreeOf(vertex) + tree.getConstructedGraph().inDegreeOf(vertex);
            simple.addVertex(vertex);
            DefaultWeightedEdge e1 = simple.addEdge(solverSource, vertex);
            simple.setEdgeWeight(e1, 4 - neighbors);
            counter += 4 - neighbors;
        }


        int neighborOfFace;

        // OuterFace
        List<TupleEdge<Vertex,Vertex>> edgeList = planarGraphFaces.get(0).getEdgeList();
/*        for (TupleEdge<Vertex, Vertex> edge: treeVertexFaceGenerator.getPlanarGraphFaces().get(0).getEdgeList()
        ) {
            vertexList2.add(edge.getLeft());
        }*/
      // List<Vertex> vertexList = treeVertexFaceGenerator.getListOfFaces2().get(0);

        // edgeList.size() Aufgrund der Struktur von vertexList: Das erste und letzte Element sind die gleichen die Formel bleibt 2*d(f) +/- 4
        neighborOfFace = 2 * (edgeList.size()) + 4 - (edgeList.size());
        if (neighborOfFace < 0) {
            throw new IllegalArgumentException("Negative Capacity");
        }


        // Face zu Sink
        Vertex face = planarGraphFaces.get(0);
        simple.addVertex(face);
        DefaultWeightedEdge edge = simple.addEdge(face, solverSink);
        simple.setEdgeWeight(edge, neighborOfFace);

        // Vertex zu Face
        for (int j = 0; j < edgeList.size() ; j++) {
            Vertex vertex = edgeList.get(j).getLeft();
            neighbors = tree.getConstructedGraph().outDegreeOf(vertex) + tree.getConstructedGraph().inDegreeOf(vertex);
            edge = simple.addEdge(vertex, face);
            simple.setEdgeWeight(edge, 4 - neighbors);
        }


        // Inner Faces:
        for (int i = 1; i < planarGraphFaces.size(); i++) {

            edgeList = planarGraphFaces.get(i).getEdgeList();

            neighborOfFace = 2 * (edgeList.size()) - 4 - (edgeList.size());

            if (neighborOfFace < 0) {
                throw new IllegalArgumentException("Negative Capacity");
            }


            // Face zu Sink
            face = planarGraphFaces.get(i);
            simple.addVertex(face);
            edge = simple.addEdge(face, solverSink);
            simple.setEdgeWeight(edge, neighborOfFace);

            //    System.out.println("InnerFace to Sink: " + neighborOfFace);

            // Vertex zu Face
            for (int j = 0; j < edgeList.size() ; j++) {
                Vertex vertex = edgeList.get(j).getLeft();
                neighbors = tree.getConstructedGraph().outDegreeOf(vertex) + tree.getConstructedGraph().inDegreeOf(vertex);
                edge = simple.addEdge(vertex, face);
                simple.setEdgeWeight(edge, 4 - neighbors);
            }

        }
    }



/*    private void generateFlowGraph2(SPQTree tree, FaceGenerator<Vertex, DefaultEdge> treeVertexFaceGenerator, DirectedWeightedMultigraph<Vertex, DefaultWeightedEdge> simple) {
        simple.addVertex(solverSource);
        simple.addVertex(solverSink);


   *//*     ArrayList<Vertex> vertexList = new ArrayList<>();
        for (TupleEdge<Vertex, Vertex> edge: treeVertexFaceGenerator.getPlanarGraphFaces().get(0).getEdgeList()
             ) {
            vertexList.add(edge.getLeft());
        }

        *//*






        // solverSource to Vertex
        int neighbors;
        counter = 0;
        for (Vertex vertex : tree.getConstructedGraph().vertexSet()
        ) {
            neighbors = tree.getConstructedGraph().outDegreeOf(vertex) + tree.getConstructedGraph().inDegreeOf(vertex);
            simple.addVertex(vertex);
            DefaultWeightedEdge e1 = simple.addEdge(solverSource, vertex);
            simple.setEdgeWeight(e1, 4 - neighbors);
            counter += 4 - neighbors;
        }


        int neighborOfFace;

        // OuterFace
        List<Vertex> vertexList2 = new ArrayList<>();
        for (TupleEdge<Vertex, Vertex> edge: treeVertexFaceGenerator.getPlanarGraphFaces().get(0).getEdgeList()
        ) {
            vertexList2.add(edge.getLeft());
        }
        List<Vertex> vertexList = treeVertexFaceGenerator.getListOfFaces2().get(0);

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
        for (int j = 0; j < vertexList2.size() ; j++) {
            Vertex vertex = vertexList2.get(j);
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
    }*/




























    private void setOrthogonalRep(Map<DefaultWeightedEdge, Double> flowMap, List<PlanarGraphFace<Vertex>> planarGraphFaces) {

        // Erstelle Map um die Kante (y,z) zu beommen, welche in Facette x auf Knoten z endet.
        HashMap<PlanarGraphFace<Vertex>, HashMap<Vertex, TupleEdge<Vertex, Vertex>>> map = new HashMap<>(); // Facette -> Map (Vertex x -> Kante (y,x) in Facette


        for (PlanarGraphFace<Vertex> face : planarGraphFaces
        ) {
            HashMap<Vertex, TupleEdge<Vertex, Vertex>> pairVectorMap = new HashMap<>();
            map.put(face, pairVectorMap);

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

                PlanarGraphFace<Vertex> tempFace;
                Double aDouble = flowMap.get(edge);
                if (aDouble == 0.0) {
                    tempFace = (PlanarGraphFace<Vertex>) graph.getEdgeTarget(edge);
                    tempFace.setEdgeAngle(pair, 1);
                //   pair.setWinkel(1);
                } else if (aDouble == 1.0) {
                    tempFace = (PlanarGraphFace<Vertex>) graph.getEdgeTarget(edge);
                    tempFace.setEdgeAngle(pair, 0);
                 // pair.setWinkel(0);
                } else if (aDouble == 2.0) {
                    tempFace = (PlanarGraphFace<Vertex>) graph.getEdgeTarget(edge);
                    tempFace.setEdgeAngle(pair, -1);
                 //   pair.setWinkel(-1);
                }

                //   System.out.println("test");
            }


        }


    }


}
