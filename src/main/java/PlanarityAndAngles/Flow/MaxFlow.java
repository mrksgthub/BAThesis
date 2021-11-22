package PlanarityAndAngles.Flow;

import PlanarityAndAngles.FaceGenerator;
import Datastructures.*;
import org.jgrapht.alg.flow.PushRelabelMFImpl;
import org.jgrapht.alg.interfaces.MaximumFlowAlgorithm;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public  class  MaxFlow {


    private Map<DefaultWeightedEdge, Double> flowMap;
    private Map<DefaultWeightedEdge, Double> flowMap2;
    private Vertex solverSource;
    private Vertex solverSink;
    private final SPQStarTree tree;
    private FaceGenerator<Vertex, DefaultEdge> treeVertexFaceGenerator;
    private List<PlanarGraphFace<Vertex>> listOfFaces;
    private int counter;
    private final DirectedWeightedMultigraph<Vertex, DefaultWeightedEdge> flowNetwork = new DirectedWeightedMultigraph<>(DefaultWeightedEdge.class);

    public MaxFlow(SPQStarTree tree, List<PlanarGraphFace<Vertex>> planarGraphFaces) {
        this.tree = tree;
        this.listOfFaces = planarGraphFaces;
    }

    public FaceGenerator<Vertex, DefaultEdge> getTreeVertexFaceGenerator() {
        return treeVertexFaceGenerator;
    }

    public void setTreeVertexFaceGenerator( List<PlanarGraphFace<Vertex>> planarGraphFaces) {
        this.listOfFaces = planarGraphFaces;
    }

    public void runJGraptHImplementation() {


        solverSource = new Vertex("solverSource");
        solverSink = new Vertex("solverSink");

        generateFlowGraph(listOfFaces, flowNetwork);



        // MaximumFlowAlgorithm<Datatypes.TreeVertex, DefaultWeightedEdge> test33 = new EdmondsKarpMFImpl<>(simple);
        MaximumFlowAlgorithm<Vertex, DefaultWeightedEdge> test33 = new PushRelabelMFImpl<>(flowNetwork);

        MaximumFlowAlgorithm.MaximumFlow<DefaultWeightedEdge> maxFlowValue = test33.getMaximumFlow(solverSource, solverSink);
        flowMap = test33.getFlowMap();

        if (maxFlowValue.getValue() != counter) {
            throw new IllegalArgumentException("Blub");
        }


        setOrthogonalRep(flowMap, listOfFaces);

    }


    public void runEdmondsKarp() {

        solverSource = new Vertex("solverSource");
        solverSink = new Vertex("solverSink");
        generateFlowGraph(listOfFaces, flowNetwork);

        EdmondsKarp edmondsKarp = new EdmondsKarp(flowNetwork);

        edmondsKarp.run();


        flowMap2 = edmondsKarp.maxFlow;
        setOrthogonalRep(edmondsKarp.maxFlow, listOfFaces);

    }


    /**
     * Erstellt Flussnetzwerk, führt Push-Relabel Maxflow Algorithmus aus und legt dann Winkel in den orthogonalen
     * Repräsentationen der Faces fest.
     * @param listOfFaces
     * @param constructedGraph
     */
    public void runPushRelabel(List<PlanarGraphFace<Vertex>> listOfFaces, DirectedMultigraph<Vertex, DefaultEdge> constructedGraph) {

        solverSource = new Vertex("solverSource");
        solverSink = new Vertex("solverSink");
        generateFlowGraph(listOfFaces, flowNetwork);

        PushRelabel pushRelabel = new PushRelabel(flowNetwork);

        pushRelabel.run();


        flowMap2 = pushRelabel.maxFlow;
        setOrthogonalRep(pushRelabel.maxFlow, this.listOfFaces);

    }


    /**
     * Erstellt ein für den Tamassia-Algorithmus zugeschnittenes Flussnetzwerk (Garg Tamassia 96).
     * Um die benötigten lower bounds zu gewährleisten müssen die capacities und Kanten angepasst werden.
     *
     * @param listOfFaces
     * @param simple
     *
     */
    private void generateFlowGraph(List<PlanarGraphFace<Vertex>> listOfFaces, DirectedWeightedMultigraph<Vertex, DefaultWeightedEdge> simple) {
        simple.addVertex(solverSource);
        simple.addVertex(solverSink);

        // solverSource to Vertex
        int neighbors;
        counter = 0;

        for (PlanarGraphFace<Vertex> face: listOfFaces
             ) {

            for (TupleEdge<Vertex, Vertex> tuple : face.getEdgeList()
            ) {
                if (!simple.containsVertex(tuple.getLeft())) {
                    neighbors = tuple.getLeft().getAdjacentVertices().size();

                    simple.addVertex(tuple.getLeft());
                    DefaultWeightedEdge e1 = simple.addEdge(solverSource, tuple.getLeft());
                    simple.setEdgeWeight(e1, 4 - neighbors);
                    counter += 4 - neighbors;
                }
            }
        }

        int neighborOfFace;

        // OuterFace
        List<TupleEdge<Vertex,Vertex>> edgeList = listOfFaces.get(0).getEdgeList();
        // edgeList.size() Aufgrund der Struktur von vertexList: Das erste und letzte Element sind die gleichen die Formel bleibt 2*d(f) +/- 4
        neighborOfFace = 2 * (edgeList.size()) + 4 - (edgeList.size());
        if (neighborOfFace < 0) {
            throw new IllegalArgumentException("Negative Capacity");
        }


        // Face zu Sink
        Vertex face = listOfFaces.get(0);
        simple.addVertex(face);
        DefaultWeightedEdge edge = simple.addEdge(face, solverSink);
        simple.setEdgeWeight(edge, neighborOfFace);

        // Vertex zu Face
        for (int j = 0; j < edgeList.size() ; j++) {
            Vertex vertex = edgeList.get(j).getLeft();
            neighbors = vertex.getAdjacentVertices().size();
            edge = simple.addEdge(vertex, face);
            simple.setEdgeWeight(edge, 4 - neighbors);
        }


        // Inner Faces:
        for (int i = 1; i < listOfFaces.size(); i++) {
            edgeList = listOfFaces.get(i).getEdgeList();
            neighborOfFace = 2 * (edgeList.size()) - 4 - (edgeList.size());

            if (neighborOfFace < 0) {
                throw new IllegalArgumentException("Negative Capacity");
            }


            // Face zu Sink
            face = listOfFaces.get(i);
            simple.addVertex(face);
            edge = simple.addEdge(face, solverSink);
            simple.setEdgeWeight(edge, neighborOfFace);

            //    System.out.println("InnerFace to Sink: " + neighborOfFace);

            // Vertex zu Face
            for (int j = 0; j < edgeList.size() ; j++) {
                Vertex vertex = edgeList.get(j).getLeft();
                neighbors = vertex.getAdjacentVertices().size();
                edge = simple.addEdge(vertex, face);
                simple.setEdgeWeight(edge, 4 - neighbors);
            }

        }
    }


    private void setOrthogonalRep(Map<DefaultWeightedEdge, Double> flowMap, List<PlanarGraphFace<Vertex>> listOfFaces) {

        // Erstelle Map um die Kante (y,z) zu beommen, welche in Facette x auf Knoten z endet.
        HashMap<PlanarGraphFace<Vertex>, HashMap<Vertex, TupleEdge<Vertex, Vertex>>> map = new HashMap<>(); // Facette -> Map (Vertex x -> Kante (y,x) in Facette


        for (PlanarGraphFace<Vertex> face : listOfFaces
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

            if (flowNetwork.getEdgeSource(edge) != solverSink && flowNetwork.getEdgeSource(edge) != solverSource && flowNetwork.getEdgeTarget(edge) != solverSink && flowNetwork.getEdgeTarget(edge) != solverSource) {
                DirectedWeightedMultigraph<Vertex, DefaultWeightedEdge> graph = flowNetwork;

                HashMap<Vertex, TupleEdge<Vertex, Vertex>> m1 = map.get(graph.getEdgeTarget(edge));

                TupleEdge<Vertex, Vertex> pair = m1.get(graph.getEdgeSource(edge));

                PlanarGraphFace<Vertex> tempFace;
                Double aDouble = flowMap.get(edge);
                if (aDouble == 0.0) {
                    tempFace = (PlanarGraphFace<Vertex>) graph.getEdgeTarget(edge);
                    tempFace.setEdgeAngle(pair, 1);
                } else if (aDouble == 1.0) {
                    tempFace = (PlanarGraphFace<Vertex>) graph.getEdgeTarget(edge);
                    tempFace.setEdgeAngle(pair, 0);
                } else if (aDouble == 2.0) {
                    tempFace = (PlanarGraphFace<Vertex>) graph.getEdgeTarget(edge);
                    tempFace.setEdgeAngle(pair, -1);
                }

                //   System.out.println("test");
            }


        }


    }


}
