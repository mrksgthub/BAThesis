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


/**
 * Implementiert die Durchführung des Netzwerkflussansatzes.
 * 1. die Transformation der Einbettung in ein Flussnetzwerk.
 * 2. die Durchführung des Maximalkostenflusses.
 * 3. die Festlegung der Winkel in der orthogonalen Repräsentation
 *
 */
public class MaxFlow {


    private final List<PlanarGraphFace<Vertex>> listOfFaces;
    public final DirectedWeightedMultigraph<Vertex, DefaultWeightedEdge> flowNetwork = new DirectedWeightedMultigraph<>(DefaultWeightedEdge.class);
    private Map<DefaultWeightedEdge, Double> flowMap;
    private final Vertex solverSource;
    private final Vertex solverSink;
    private int counter;

    public MaxFlow(List<PlanarGraphFace<Vertex>> planarGraphFaces) {
        this.listOfFaces = planarGraphFaces;
        solverSource = new Vertex("solverSource");
        solverSink = new Vertex("solverSink");
    }

    public Map<DefaultWeightedEdge, Double> getFlowMap() {
        return flowMap;
    }


    /**
     * Erstellt das Flussnetzwerk und führt den Maximalkostenfluss aus
     *
     *
     * @return Ist der Fluss gültig?
     */
    public boolean runJGraphTPushRelabelImplementation() {

       boolean isValid = generateFlowNetwork();
        if (isValid) {
            return runJGraphTPushRelabelPlanarityTest();
        } else {
            return false;
        }
    }

    /**
     * Führt den Maximalkostenflussalgorithmus aus und prüft, ob der Fluss ein Maximalfluss ist und somit eine
     * der rektilineare Planaritätstest bestanden wurde.
     *
     * @return WUrde der rektilineare Planaritätstest bestanden?
     */
    public boolean runJGraphTPushRelabelPlanarityTest() {
        MaximumFlowAlgorithm<Vertex, DefaultWeightedEdge> test33 = new PushRelabelMFImpl<>(flowNetwork);
        MaximumFlowAlgorithm.MaximumFlow<DefaultWeightedEdge> maxFlowValue = test33.getMaximumFlow(solverSource, solverSink);
        flowMap = test33.getFlowMap();
        return maxFlowValue.getValue() == counter;
    }

    /**
     * Transformiert die Einbettung des Graphen in das entsprechende Flussnetzwerk.
     *
     *
     * @return
     */
    public boolean generateFlowNetwork() {


        flowNetwork.addVertex(solverSource);
        flowNetwork.addVertex(solverSink);

        // solverSource to Vertex
        int adjecentVs;
        counter = 0;

        for (PlanarGraphFace<Vertex> face : listOfFaces
        ) {

            for (TupleEdge<Vertex, Vertex> tuple : face.getEdgeList()
            ) {
                if (!flowNetwork.containsVertex(tuple.getLeft())) {
                    adjecentVs = tuple.getLeft().getAdjacentVertices().size();

                    flowNetwork.addVertex(tuple.getLeft());
                    DefaultWeightedEdge e1 = flowNetwork.addEdge(solverSource, tuple.getLeft());
                    flowNetwork.setEdgeWeight(e1, 4 - adjecentVs);
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
        flowNetwork.addVertex(face);
        DefaultWeightedEdge edge = flowNetwork.addEdge(face, solverSink);
        flowNetwork.setEdgeWeight(edge, neighborOfFace);

        // Vertex zu Face
        for (int j = 0; j < edgeList.size(); j++) {
            Vertex vertex = edgeList.get(j).getLeft();
            adjecentVs = vertex.getAdjacentVertices().size();
            edge = flowNetwork.addEdge(vertex, face);
            flowNetwork.setEdgeWeight(edge, 4 - adjecentVs);
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
            flowNetwork.addVertex(face);
            edge = flowNetwork.addEdge(face, solverSink);
            flowNetwork.setEdgeWeight(edge, neighborOfFace);

            //    System.out.println("InnerFace to Sink: " + neighborOfFace);

            // Vertex zu Face
            for (int j = 0; j < edgeList.size(); j++) {
                Vertex vertex = edgeList.get(j).getLeft();
                adjecentVs = vertex.getAdjacentVertices().size();
                edge = flowNetwork.addEdge(vertex, face);
                flowNetwork.setEdgeWeight(edge, 4 - adjecentVs);
            }

        }

        return true;
    }

/*    public boolean runJGraphTFordFulkersonImplementation() {


        boolean isValid = generateFlowNetwork();
        // MaximumFlowAlgorithm<Datatypes.TreeVertex, DefaultWeightedEdge> test33 = new EdmondsKarpMFImpl<>(simple);
        if (isValid) {
            return runJGrapthTFordFulkersonPlanarityTest();
        } else {
            return false;
        }

    }*/

/*    private boolean runJGrapthTFordFulkersonPlanarityTest() {
        MaximumFlowAlgorithm<Vertex, DefaultWeightedEdge> test33 = new EdmondsKarpMFImpl<>(flowNetwork);
        //     MaximumFlowAlgorithm<Vertex, DefaultWeightedEdge> test33 = new PushRelabelMFImpl<>(flowNetwork);
        MaximumFlowAlgorithm.MaximumFlow<DefaultWeightedEdge> maxFlowValue = test33.getMaximumFlow(solverSource, solverSink);
        flowMap = test33.getFlowMap();
        return maxFlowValue.getValue() == counter;

    }*/


    /**
     * Nutzt den berechneten Fluss und nutzt diesen um die orthogonale Repräsentation festzulegen.
     *
     *
     *
     */
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

}
