package PlanarityAndAngles.Flow;

import Datastructures.PlanarGraphFace;
import Datastructures.TupleEdge;
import Datastructures.Vertex;
import org.jgrapht.alg.flow.EdmondsKarpMFImpl;
import org.jgrapht.alg.flow.PushRelabelMFImpl;
import org.jgrapht.alg.interfaces.MaximumFlowAlgorithm;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaxFlow {


    private final List<PlanarGraphFace<Vertex>> listOfFaces;
    private final DirectedWeightedMultigraph<Vertex, DefaultWeightedEdge> flowNetwork = new DirectedWeightedMultigraph<>(DefaultWeightedEdge.class);
    private Map<DefaultWeightedEdge, Double> flowMap;
    private Vertex solverSource;
    private Vertex solverSink;
    private int counter;

    public MaxFlow(List<PlanarGraphFace<Vertex>> planarGraphFaces) {
        this.listOfFaces = planarGraphFaces;
    }

    public Map<DefaultWeightedEdge, Double> getFlowMap() {
        return flowMap;
    }

    public void runJGraptHImplementation() {


        solverSource = new Vertex("solverSource");
        solverSink = new Vertex("solverSink");

        generateFlowNetwork(listOfFaces, flowNetwork);

        // MaximumFlowAlgorithm<Datatypes.TreeVertex, DefaultWeightedEdge> test33 = new EdmondsKarpMFImpl<>(simple);
        MaximumFlowAlgorithm<Vertex, DefaultWeightedEdge> test33 = new PushRelabelMFImpl<>(flowNetwork);

        MaximumFlowAlgorithm.MaximumFlow<DefaultWeightedEdge> maxFlowValue = test33.getMaximumFlow(solverSource, solverSink);
        flowMap = test33.getFlowMap();

        if (maxFlowValue.getValue() != counter) {
            throw new IllegalArgumentException("Blub");
        }

    }

    public void runJGraptHFordFulkerson() {


        solverSource = new Vertex("solverSource");
        solverSink = new Vertex("solverSink");

        generateFlowNetwork(listOfFaces, flowNetwork);


        MaximumFlowAlgorithm<Vertex, DefaultWeightedEdge> test33 = new EdmondsKarpMFImpl<>(flowNetwork);
        //     MaximumFlowAlgorithm<Vertex, DefaultWeightedEdge> test33 = new PushRelabelMFImpl<>(flowNetwork);

        MaximumFlowAlgorithm.MaximumFlow<DefaultWeightedEdge> maxFlowValue = test33.getMaximumFlow(solverSource, solverSink);
        flowMap = test33.getFlowMap();

        if (maxFlowValue.getValue() != counter) {
            throw new IllegalArgumentException("Blub");
        }


        setOrthogonalRep();

    }


    public void runEdmondsKarp() {

        solverSource = new Vertex("solverSource");
        solverSink = new Vertex("solverSink");
        generateFlowNetwork(listOfFaces, flowNetwork);

        EdmondsKarp edmondsKarp = new EdmondsKarp(flowNetwork);

        edmondsKarp.run();


        flowMap = edmondsKarp.maxFlow;
        setOrthogonalRep();

    }


    /**
     * Erstellt Flussnetzwerk, führt Push-Relabel Maxflow Algorithmus aus und legt dann Winkel in den orthogonalen
     * Repräsentationen der Faces fest.
     *
     * @param listOfFaces
     * @return
     */
    public boolean runPushRelabel(List<PlanarGraphFace<Vertex>> listOfFaces) {

        solverSource = new Vertex("solverSource");
        solverSink = new Vertex("solverSink");
        boolean isLegalGraph = generateFlowNetwork(listOfFaces, flowNetwork);
        if (!isLegalGraph) {
            return false;
        }

        PushRelabel pushRelabel = new PushRelabel(flowNetwork);
        boolean isRectilinear = pushRelabel.run();


        flowMap = pushRelabel.maxFlow;
        //  setOrthogonalRep();
        return isRectilinear;
    }


    /**
     * Erstellt ein für den Tamassia-Algorithmus zugeschnittenes Flussnetzwerk (Garg Tamassia 96).
     * Um die benötigten lower bounds zu gewährleisten müssen die capacities und Kanten angepasst werden.
     *
     * @param listOfFaces
     * @param simple
     */
    private boolean generateFlowNetwork(List<PlanarGraphFace<Vertex>> listOfFaces, DirectedWeightedMultigraph<Vertex, DefaultWeightedEdge> simple) {
        simple.addVertex(solverSource);
        simple.addVertex(solverSink);

        // solverSource to Vertex
        int adjecentVs;
        counter = 0;

        for (PlanarGraphFace<Vertex> face : listOfFaces
        ) {

            for (TupleEdge<Vertex, Vertex> tuple : face.getEdgeList()
            ) {
                if (!simple.containsVertex(tuple.getLeft())) {
                    adjecentVs = tuple.getLeft().getAdjacentVertices().size();

                    simple.addVertex(tuple.getLeft());
                    DefaultWeightedEdge e1 = simple.addEdge(solverSource, tuple.getLeft());
                    simple.setEdgeWeight(e1, 4 - adjecentVs);
                    counter += 4 - adjecentVs;
                }
            }
        }

        int neighborOfFace;

        // OuterFace
        List<TupleEdge<Vertex, Vertex>> edgeList = listOfFaces.get(0).getEdgeList();
        // edgeList.size() Aufgrund der Struktur von vertexList: Das erste und letzte Element sind die gleichen die Formel bleibt 2*d(f) +/- 4
        neighborOfFace = 2 * (edgeList.size()) + 4 - (edgeList.size());
        if (neighborOfFace < 0) {
            System.out.println("Negative Capacity");
            return false;

        }


        // Face zu Sink
        Vertex face = listOfFaces.get(0);
        simple.addVertex(face);
        DefaultWeightedEdge edge = simple.addEdge(face, solverSink);
        simple.setEdgeWeight(edge, neighborOfFace);

        // Vertex zu Face
        for (int j = 0; j < edgeList.size(); j++) {
            Vertex vertex = edgeList.get(j).getLeft();
            adjecentVs = vertex.getAdjacentVertices().size();
            edge = simple.addEdge(vertex, face);
            simple.setEdgeWeight(edge, 4 - adjecentVs);
        }


        // Inner Faces:
        for (int i = 1; i < listOfFaces.size(); i++) {
            edgeList = listOfFaces.get(i).getEdgeList();
            neighborOfFace = 2 * (edgeList.size()) - 4 - (edgeList.size());

            if (neighborOfFace < 0) {
                System.out.println("Negative Capacity");
                return false;
            }


            // Face zu Sink
            face = listOfFaces.get(i);
            simple.addVertex(face);
            edge = simple.addEdge(face, solverSink);
            simple.setEdgeWeight(edge, neighborOfFace);

            //    System.out.println("InnerFace to Sink: " + neighborOfFace);

            // Vertex zu Face
            for (int j = 0; j < edgeList.size(); j++) {
                Vertex vertex = edgeList.get(j).getLeft();
                adjecentVs = vertex.getAdjacentVertices().size();
                edge = simple.addEdge(vertex, face);
                simple.setEdgeWeight(edge, 4 - adjecentVs);
            }

        }
        return true;
    }


    public void setOrthogonalRep() {

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

    public DirectedWeightedMultigraph<Vertex, DefaultWeightedEdge> getFlowNetwork() {
        return flowNetwork;
    }
}
