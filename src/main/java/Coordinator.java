import org.antlr.v4.runtime.misc.Pair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.jgrapht.alg.interfaces.MinimumCostFlowAlgorithm;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Coordinator {


    private final PlanarGraphFace<TreeVertex, DefaultEdge> outerFace;
    private final HashMap<PlanarGraphFace<TreeVertex, DefaultEdge>, PlanarGraphFace<TreeVertex, DefaultEdge>> rectangularFaceMap;
    private final Map<MutablePair<TreeVertex, TreeVertex>, DefaultWeightedEdge> horzontalEdgeToArcMap;
    private final Map<MutablePair<TreeVertex, TreeVertex>, DefaultWeightedEdge> verticalEdgeToArcMap;
    private final MinimumCostFlowAlgorithm.MinimumCostFlow<DefaultWeightedEdge> horizontalMinCostFlow;
    private final MinimumCostFlowAlgorithm.MinimumCostFlow<DefaultWeightedEdge> verticalMinCostFlow;
    private Map<TreeVertex, Pair<Integer, Integer>> edgeToCoordMap = new HashMap<>();
    private HashMap<Object, Object> edgeFaceNeighbourMap = new HashMap<>();


    public Coordinator(PlanarGraphFace<TreeVertex, DefaultEdge> outerFace, HashMap<PlanarGraphFace<TreeVertex, DefaultEdge>, PlanarGraphFace<TreeVertex, DefaultEdge>> rectangularFaceMap, Map<MutablePair<TreeVertex, TreeVertex>, DefaultWeightedEdge> edgeToArcMap, Map<MutablePair<TreeVertex, TreeVertex>, DefaultWeightedEdge> edgeToArcMap1, MinimumCostFlowAlgorithm.MinimumCostFlow<DefaultWeightedEdge> minimumCostFlow, MinimumCostFlowAlgorithm.MinimumCostFlow<DefaultWeightedEdge> costFlow) {

        this.outerFace = outerFace;
        this.rectangularFaceMap = rectangularFaceMap;
        this.horzontalEdgeToArcMap = edgeToArcMap1;
        this.verticalEdgeToArcMap = edgeToArcMap;
        this.horizontalMinCostFlow = costFlow;
        this.verticalMinCostFlow = minimumCostFlow;


    }

    public Map<TreeVertex, Pair<Integer, Integer>> getEdgeToCoordMap() {
        return edgeToCoordMap;
    }

    public void setEdgeToCoordMap(Map<TreeVertex, Pair<Integer, Integer>> edgeToCoordMap) {
        this.edgeToCoordMap = edgeToCoordMap;
    }

    public void run() {

        List<PlanarGraphFace<TreeVertex, DefaultEdge>> undiscoveredFaces = new ArrayList<>(rectangularFaceMap.keySet());
        List<PlanarGraphFace<TreeVertex, DefaultEdge>> discoveredFaces = new ArrayList<>();
        edgeFaceNeighbourMap = new HashMap<>();
        HashMap<PlanarGraphFace<TreeVertex, DefaultEdge>, Boolean> visitedMap = new HashMap<>();
        visitedMap.put(outerFace, true);


        for (MutablePair<TreeVertex, TreeVertex> edge : outerFace.getEdgeList()
        ) {
            edgeFaceNeighbourMap.put(edge, outerFace);
        }

        for (PlanarGraphFace<TreeVertex, DefaultEdge> face : rectangularFaceMap.keySet()
        ) {
            for (MutablePair<TreeVertex, TreeVertex> edge : face.getEdgeList()
            ) {
                edgeFaceNeighbourMap.put(edge, face);
            }

        }


        edgeToCoordMap.put(outerFace.sidesMap.get(0).get(0).getLeft(), new Pair<>(0, 0));
        int yCoord = edgeToCoordMap.get(outerFace.sidesMap.get(0).get(0).getLeft()).b;


        ArrayList<MutablePair<TreeVertex, TreeVertex>> list = outerFace.sidesMap.get(0);
        int length = 0;
        for (MutablePair<TreeVertex, TreeVertex> edge : list) {
            length += horizontalMinCostFlow.getFlowMap().get(horzontalEdgeToArcMap.get(edge));
            edgeToCoordMap.put(edge.getRight(), new Pair<>(length, yCoord));


            MutablePair<TreeVertex, TreeVertex> reverseEdge = new TupleEdge<>(edge.getRight(), edge.getLeft());
            PlanarGraphFace<TreeVertex, DefaultEdge> face = (PlanarGraphFace<TreeVertex, DefaultEdge>) edgeFaceNeighbourMap.get(reverseEdge);
            assert (face != null);
            if (visitedMap.get(face) == null) {
                visitedMap.putIfAbsent(face, true);
                discoveredFaces.add(face);
            }


        }


        length = 0;
        list = outerFace.sidesMap.get(1);
        int xCoord = edgeToCoordMap.get(list.get(list.size() - 1).getRight()).a;
        for (int i = list.size() - 1; i > -1; i--) {
            length += verticalMinCostFlow.getFlowMap().get(verticalEdgeToArcMap.get(list.get(i)));
            edgeToCoordMap.put(list.get(i).getLeft(), new Pair<>(xCoord, length));
        }


        length = 0;
        list = outerFace.sidesMap.get(2);
        yCoord = edgeToCoordMap.get(list.get(list.size() - 1).getRight()).b;
        for (int i = list.size() - 1; i > -1; i--) {
            length += horizontalMinCostFlow.getFlowMap().get(horzontalEdgeToArcMap.get(list.get(i)));
            edgeToCoordMap.put(list.get(i).getLeft(), new Pair<>(length, yCoord));
        }

        // x-Koordinaten festlegen:
        length = 0;
        list = outerFace.sidesMap.get(3);
        xCoord = edgeToCoordMap.get(list.get(0).getLeft()).a;
        for (int i = 0; i < list.size(); i++) {
            length += verticalMinCostFlow.getFlowMap().get(verticalEdgeToArcMap.get(list.get(i)));
            assert i != list.size() - 1 || (edgeToCoordMap.get(list.get(list.size() - 1).getRight()).b == length);
            edgeToCoordMap.put(list.get(i).getRight(), new Pair<>(xCoord, length));
        }


        Pair<Integer, Integer> newCoordinates;
        while (discoveredFaces.size() > 0) {

            PlanarGraphFace<TreeVertex, DefaultEdge> currFace = discoveredFaces.get(0);
            discoveredFaces.remove(0);

            list = currFace.sidesMap.get(1);


            Pair<Integer, Integer> startVertex = edgeToCoordMap.get(list.get(0).getLeft());
            if (startVertex == null) {
                discoveredFaces.add(currFace);
                continue;
            }

            length = startVertex.b;
            xCoord = edgeToCoordMap.get(list.get(0).getLeft()).a;
            for (MutablePair<TreeVertex, TreeVertex> edge : list) {
                length += verticalMinCostFlow.getFlowMap().get(verticalEdgeToArcMap.get(edge));


                newCoordinates = new Pair<>(xCoord, length);
                Pair<Integer, Integer> vertex = edgeToCoordMap.get(edge.getRight());
                assert vertex == null || (edgeToCoordMap.get(edge.getRight()).equals(newCoordinates));
                assert (length > -1);
                edgeToCoordMap.put(edge.getRight(), new Pair<>(xCoord, length));

            }


            list = currFace.sidesMap.get(2);
            length = edgeToCoordMap.get(list.get(0).getLeft()).a;
            yCoord = edgeToCoordMap.get(list.get(0).getLeft()).b;
            for (int i = 0; i < list.size(); i++) {
                length += horizontalMinCostFlow.getFlowMap().get(horzontalEdgeToArcMap.get(list.get(i)));


                newCoordinates = new Pair<>(length, yCoord);
                assert edgeToCoordMap.get(list.get(i).getRight()) == null || (edgeToCoordMap.get(list.get(i).getRight()).equals(newCoordinates));
                assert (length > -1);
                edgeToCoordMap.put(list.get(i).getRight(), new Pair<>(length, yCoord));


                MutablePair<TreeVertex, TreeVertex> reverseEdge = new TupleEdge<>(list.get(i).getRight(), list.get(i).getLeft());
                PlanarGraphFace<TreeVertex, DefaultEdge> face = (PlanarGraphFace<TreeVertex, DefaultEdge>) edgeFaceNeighbourMap.get(reverseEdge);
                assert (face != null);
                if (visitedMap.get(face) == null) {
                    visitedMap.putIfAbsent(face, true);
                    discoveredFaces.add(face);
                }

            }


            list = currFace.sidesMap.get(3);
            length = edgeToCoordMap.get(list.get(0).getLeft()).b;
            xCoord = edgeToCoordMap.get(list.get(0).getLeft()).a;
            for (int i = 0; i < list.size(); i++) {
                length -= verticalMinCostFlow.getFlowMap().get(verticalEdgeToArcMap.get(list.get(i)));


                newCoordinates = new Pair<>(xCoord, length);
                assert edgeToCoordMap.get(list.get(i).getRight()) == null || (edgeToCoordMap.get(list.get(i).getRight()).equals(newCoordinates));

                assert (length > -1);
                edgeToCoordMap.put(list.get(i).getRight(), newCoordinates);

/*
                MutablePair<TreeVertex, TreeVertex> reverseEdge = new Tuple<>(list.get(i).getRight(), list.get(i).getLeft());
                PlanarGraphFace<TreeVertex, DefaultEdge> face = (PlanarGraphFace<TreeVertex, DefaultEdge>) edgeFaceNeighbourMap.get(reverseEdge);
                assert (face != null);
                if (visitedMap.get(face) == null) {
                    visitedMap.putIfAbsent(face, true);
                    discoveredFaces.add(face);
                }
*/

            }


            list = currFace.sidesMap.get(0);
            length = edgeToCoordMap.get(list.get(0).getLeft()).a;
            yCoord = edgeToCoordMap.get(list.get(0).getLeft()).b;
            for (int i = 0; i < list.size(); i++) {
                length -= horizontalMinCostFlow.getFlowMap().get(horzontalEdgeToArcMap.get(list.get(i)));


                newCoordinates = new Pair<>(length, yCoord);
                assert edgeToCoordMap.get(list.get(i).getRight()) == null || (edgeToCoordMap.get(list.get(i).getRight()).equals(newCoordinates));
                assert (length > -1);
                edgeToCoordMap.put(list.get(i).getRight(), new Pair<>(length, yCoord));


                MutablePair<TreeVertex, TreeVertex> reverseEdge = new TupleEdge<>(list.get(i).getRight(), list.get(i).getLeft());
                PlanarGraphFace<TreeVertex, DefaultEdge> face = (PlanarGraphFace<TreeVertex, DefaultEdge>) edgeFaceNeighbourMap.get(reverseEdge);
                assert (face != null);
                if (visitedMap.get(face) == null) {
                    visitedMap.putIfAbsent(face, true);
                    discoveredFaces.add(face);
                }

            }


        }


        System.out.println("Help");
    }


}
