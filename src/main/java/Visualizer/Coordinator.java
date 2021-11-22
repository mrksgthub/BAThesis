package Visualizer;

import Datastructures.PlanarGraphFace;
import Datastructures.TupleEdge;
import Datastructures.Vertex;
import org.antlr.v4.runtime.misc.Pair;
import org.jgrapht.alg.interfaces.MinimumCostFlowAlgorithm;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Zuständig für die Zuweisung von x- und y-Koordinaten an die Knoten des Graphen.
 */
public class Coordinator {


    private final PlanarGraphFace<Vertex> outerFace;
    private final List<PlanarGraphFace<Vertex>> rectangularFaceMap;
    private final Map<TupleEdge<Vertex, Vertex>, DefaultWeightedEdge> horzontalEdgeToArcMap;
    private final Map<TupleEdge<Vertex, Vertex>, DefaultWeightedEdge> verticalEdgeToArcMap;
    private final MinimumCostFlowAlgorithm.MinimumCostFlow<DefaultWeightedEdge> horizontalMinCostFlow;
    private final MinimumCostFlowAlgorithm.MinimumCostFlow<DefaultWeightedEdge> verticalMinCostFlow;
    private Map<Vertex, Pair<Integer, Integer>> edgeToCoordMap = new HashMap<>();
    private HashMap<Object, Object> edgeFaceNeighbourMap = new HashMap<>();


    /**
     * @param outerFace
     * @param innerFaceList
     * @param edgeToArcMap    - horizontal Edge to Arc Map
     * @param edgeToArcMap1   - vertical Edge to Arc Map
     * @param minimumCostFlow - erzeugt von jgrapht MinimumCostFlowAlgorithm für die vertical orientieren Edges
     * @param costFlow        - - erzeugt von jgrapht MinimumCostFlowAlgorithm für die horizontal orientieren Kanten
     */
    public Coordinator(PlanarGraphFace<Vertex> outerFace, List<PlanarGraphFace<Vertex>> innerFaceList, Map<TupleEdge<Vertex, Vertex>, DefaultWeightedEdge> edgeToArcMap, Map<TupleEdge<Vertex, Vertex>, DefaultWeightedEdge> edgeToArcMap1, MinimumCostFlowAlgorithm.MinimumCostFlow<DefaultWeightedEdge> minimumCostFlow, MinimumCostFlowAlgorithm.MinimumCostFlow<DefaultWeightedEdge> costFlow) {

        this.outerFace = outerFace;
        this.rectangularFaceMap = innerFaceList;
        this.horzontalEdgeToArcMap = edgeToArcMap1;
        this.verticalEdgeToArcMap = edgeToArcMap;
        this.horizontalMinCostFlow = costFlow;
        this.verticalMinCostFlow = minimumCostFlow;


    }

    public Map<Vertex, Pair<Integer, Integer>> getEdgeToCoordMap() {
        return edgeToCoordMap;
    }


    /**
     * Berechnet die Koordinaten des Graphen dadurch, dass es einen Knoten der äußeren Facette auf (0,0) festlegt und
     * von diesem ausgehend werden alle Koordinaten der Knoten berechnet.
     */
    public void run() {

        List<PlanarGraphFace<Vertex>> undiscoveredFaces = new ArrayList<>(rectangularFaceMap);
        List<PlanarGraphFace<Vertex>> discoveredFaces = new ArrayList<>();
        edgeFaceNeighbourMap = new HashMap<>();
        HashMap<PlanarGraphFace<Vertex>, Boolean> visitedMap = new HashMap<>();
        visitedMap.put(outerFace, true);


        // füge alle Kanten zu einer Kante -> Facette Map hinzu
        for (TupleEdge<Vertex, Vertex> edge : outerFace.getEdgeList()
        ) {
            edgeFaceNeighbourMap.put(edge, outerFace);
        }

        for (PlanarGraphFace<Vertex> face : rectangularFaceMap
        ) {
            for (TupleEdge<Vertex, Vertex> edge : face.getEdgeList()
            ) {
                edgeFaceNeighbourMap.put(edge, face);
            }

        }


        edgeToCoordMap.put(outerFace.getSidesMap().get(0).get(0).getLeft(), new Pair<>(0, 0)); // bestimme den Knoten mit der (0,0) Koordinate
        int yCoord = edgeToCoordMap.get(outerFace.getSidesMap().get(0).get(0).getLeft()).b;

        // Bestimme die Koordinaten der Knoten des unteren horizontalen Randes der äußeren Facette
        // Entdecke alle Facetten, welche am unteren horizontalen Rand der äußeren Facette angrenzen.
        ArrayList<TupleEdge<Vertex, Vertex>> list = outerFace.getSidesMap().get(0);
        int length = 0;
        for (TupleEdge<Vertex, Vertex> edge : list) {
            length += horizontalMinCostFlow.getFlowMap().get(horzontalEdgeToArcMap.get(edge));
            edgeToCoordMap.put(edge.getRight(), new Pair<>(length, yCoord));

            TupleEdge<Vertex, Vertex> reverseEdge = new TupleEdge<>(edge.getRight(), edge.getLeft());
            PlanarGraphFace<Vertex> face = (PlanarGraphFace<Vertex>) edgeFaceNeighbourMap.get(reverseEdge);
            assert (face != null);
            if (visitedMap.get(face) == null) {
                visitedMap.putIfAbsent(face, true);
                discoveredFaces.add(face);
            }
        }


        // Bestimme die Koordinaten der Knoten des linken vertikalen Randes der äußeren Facette
        length = 0;
        list = outerFace.getSidesMap().get(1);
        int xCoord = edgeToCoordMap.get(list.get(list.size() - 1).getRight()).a;
        for (int i = list.size() - 1; i > -1; i--) {
            length += verticalMinCostFlow.getFlowMap().get(verticalEdgeToArcMap.get(list.get(i)));
            edgeToCoordMap.put(list.get(i).getLeft(), new Pair<>(xCoord, length));
        }

        // Bestimme die Koordinaten der Knoten des oberen horizontalen Randes der äußeren Facette
        length = 0;
        list = outerFace.getSidesMap().get(2);
        yCoord = edgeToCoordMap.get(list.get(list.size() - 1).getRight()).b;
        for (int i = list.size() - 1; i > -1; i--) {
            length += horizontalMinCostFlow.getFlowMap().get(horzontalEdgeToArcMap.get(list.get(i)));
            edgeToCoordMap.put(list.get(i).getLeft(), new Pair<>(length, yCoord));
        }

        // Bestimme die Koordinaten der Knoten des rechten vertikalen Randes der äußeren Facette
        length = 0;
        list = outerFace.getSidesMap().get(3);
        xCoord = edgeToCoordMap.get(list.get(0).getLeft()).a;
        for (int i = 0; i < list.size(); i++) {
            length += verticalMinCostFlow.getFlowMap().get(verticalEdgeToArcMap.get(list.get(i)));
            edgeToCoordMap.put(list.get(i).getRight(), new Pair<>(xCoord, length));

            assert i != list.size() - 1 || (edgeToCoordMap.get(list.get(list.size() - 1).getRight()).b == length);
        }


        // Bestimme die Koordinaten der Knoten der inneren Facetten. Dabei müssen wir die Tupel mit der Orientierung 1
        // nicht beachten (da diese in der benachbarten Facette von den Tupel mit der Orientierung 3 betrachtet werden).
        Pair<Integer, Integer> newCoordinates;
        while (discoveredFaces.size() > 0) {

            PlanarGraphFace<Vertex> currFace = discoveredFaces.get(0);
            discoveredFaces.remove(0);

            list = currFace.getSidesMap().get(1);


            Pair<Integer, Integer> startVertex = edgeToCoordMap.get(list.get(0).getLeft()); /* wir benötigen, dass der
            Knoten "links unten" (wenn wir davon ausgehen, dass "0" die Knoten sind, die links unten sind) feste
            Koordinaten hat.
            */
            if (startVertex == null) {
                discoveredFaces.add(currFace);
                continue;
            }

            length = startVertex.b;
            xCoord = edgeToCoordMap.get(list.get(0).getLeft()).a;
            // Wir legen die Koordinaten der Knoten, die die Orientierung 0 haben fest.
            for (TupleEdge<Vertex, Vertex> edge : list) {
                length += verticalMinCostFlow.getFlowMap().get(verticalEdgeToArcMap.get(edge));


                newCoordinates = new Pair<>(xCoord, length);
                Pair<Integer, Integer> vertex = edgeToCoordMap.get(edge.getRight());
                assert vertex == null || (edgeToCoordMap.get(edge.getRight()).equals(newCoordinates));
                assert (length > -1);
                edgeToCoordMap.put(edge.getRight(), new Pair<>(xCoord, length));

            }

            // Wir legen die Koordinaten der Knoten, die die Orientierung 2 haben fest.
            list = currFace.getSidesMap().get(2);
            length = edgeToCoordMap.get(list.get(0).getLeft()).a;
            yCoord = edgeToCoordMap.get(list.get(0).getLeft()).b;
            for (int i = 0; i < list.size(); i++) {
                length += horizontalMinCostFlow.getFlowMap().get(horzontalEdgeToArcMap.get(list.get(i)));


                newCoordinates = new Pair<>(length, yCoord);
                assert edgeToCoordMap.get(list.get(i).getRight()) == null || (edgeToCoordMap.get(list.get(i).getRight()).equals(newCoordinates));
                assert (length > -1);
                edgeToCoordMap.put(list.get(i).getRight(), new Pair<>(length, yCoord));


                TupleEdge<Vertex, Vertex> reverseEdge = new TupleEdge<>(list.get(i).getRight(), list.get(i).getLeft());
                PlanarGraphFace<Vertex> face = (PlanarGraphFace<Vertex>) edgeFaceNeighbourMap.get(reverseEdge);
                assert (face != null);
                if (visitedMap.get(face) == null) {
                    visitedMap.putIfAbsent(face, true);
                    discoveredFaces.add(face);
                }

            }

            // Wir legen die Koordinaten der Knoten, die die Orientierung 3 haben fest.
            list = currFace.getSidesMap().get(3);
            length = edgeToCoordMap.get(list.get(0).getLeft()).b;
            xCoord = edgeToCoordMap.get(list.get(0).getLeft()).a;
            for (int i = 0; i < list.size(); i++) {
                length -= verticalMinCostFlow.getFlowMap().get(verticalEdgeToArcMap.get(list.get(i)));
                newCoordinates = new Pair<>(xCoord, length);
                assert edgeToCoordMap.get(list.get(i).getRight()) == null || (edgeToCoordMap.get(list.get(i).getRight()).equals(newCoordinates));

                assert (length > -1);
                edgeToCoordMap.put(list.get(i).getRight(), newCoordinates);

            }

// TODO vielleicht wieder unkommentieren.

/*            list = currFace.getSidesMap().get(0);
            length = edgeToCoordMap.get(list.get(0).getLeft()).a;
            yCoord = edgeToCoordMap.get(list.get(0).getLeft()).b;
            for (int i = 0; i < list.size(); i++) {
                length -= horizontalMinCostFlow.getFlowMap().get(horzontalEdgeToArcMap.get(list.get(i)));


                newCoordinates = new Pair<>(length, yCoord);
                assert edgeToCoordMap.get(list.get(i).getRight()) == null || (edgeToCoordMap.get(list.get(i).getRight()).equals(newCoordinates));
                assert (length > -1);
                edgeToCoordMap.put(list.get(i).getRight(), new Pair<>(length, yCoord));


                TupleEdge<Vertex, Vertex> reverseEdge = new TupleEdge<>(list.get(i).getRight(), list.get(i).getLeft());
                PlanarGraphFace<Vertex> face = (PlanarGraphFace<Vertex>) edgeFaceNeighbourMap.get(reverseEdge);
                assert (face != null);
                if (visitedMap.get(face) == null) {
                    visitedMap.putIfAbsent(face, true);
                    discoveredFaces.add(face);
                }
            }*/
        }


        System.out.println("Visualizing.Coordinator finished");
    }


}
