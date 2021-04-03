import org.apache.commons.lang3.tuple.MutablePair;
import org.jgrapht.alg.flow.mincost.CapacityScalingMinimumCostFlow;
import org.jgrapht.alg.flow.mincost.MinimumCostFlowProblem;
import org.jgrapht.alg.interfaces.MinimumCostFlowAlgorithm;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HorizontalEdgeFlow {


    HashMap<MutablePair<TreeVertex, TreeVertex>, PlanarGraphFace<TreeVertex, DefaultEdge>> edgeToFAceMap = new HashMap<>();
    Map<TreeVertex, Integer> supplyMap = new HashMap<>();
    Map<DefaultWeightedEdge, Integer> lowerMap = new HashMap<>();
    Map<DefaultWeightedEdge, Integer> upperMap = new HashMap<>();
    List<PlanarGraphFace<TreeVertex, DefaultEdge>> rectangleList = new ArrayList<>();
    PlanarGraphFace<TreeVertex, DefaultEdge> outerFace;
    private DirectedWeightedMultigraph<TreeVertex, DefaultWeightedEdge> networkGraph;
    private MinimumCostFlowAlgorithm.MinimumCostFlow<DefaultWeightedEdge> minimumCostFlow;
    Map<MutablePair<TreeVertex, TreeVertex>, DefaultWeightedEdge> edgeToArcMap = new HashMap<>();

    public HorizontalEdgeFlow(List<PlanarGraphFace<TreeVertex, DefaultEdge>> rectangleList, PlanarGraphFace<TreeVertex, DefaultEdge> outerFace) {
        this.rectangleList = rectangleList;
        this.outerFace = outerFace;

        for (MutablePair<TreeVertex, TreeVertex> edge : outerFace.getEdgeList()
        ) {
            edgeToFAceMap.put(edge, outerFace);
        }


        for (PlanarGraphFace<TreeVertex, DefaultEdge> face : rectangleList
        ) {
            for (MutablePair<TreeVertex, TreeVertex> edge : face.getEdgeList()
            ) {
                edgeToFAceMap.put(edge, face);
            }

        }


    }

    public MinimumCostFlowAlgorithm.MinimumCostFlow<DefaultWeightedEdge> getMinimumCostFlow() {
        return minimumCostFlow;
    }

    public void setMinimumCostFlow(MinimumCostFlowAlgorithm.MinimumCostFlow<DefaultWeightedEdge> minimumCostFlow) {
        this.minimumCostFlow = minimumCostFlow;
    }

    public HashMap<MutablePair<TreeVertex, TreeVertex>, PlanarGraphFace<TreeVertex, DefaultEdge>> getEdgeToFAceMap() {
        return edgeToFAceMap;
    }

    public void setEdgeToFAceMap(HashMap<MutablePair<TreeVertex, TreeVertex>, PlanarGraphFace<TreeVertex, DefaultEdge>> edgeToFAceMap) {
        this.edgeToFAceMap = edgeToFAceMap;
    }

    public DirectedWeightedMultigraph<TreeVertex, DefaultWeightedEdge> generateFlowNetworkLayout2() {
        networkGraph = new DirectedWeightedMultigraph<>(DefaultWeightedEdge.class);

        List<PlanarGraphFace<TreeVertex, DefaultEdge>> rectangleList = this.rectangleList;
        TreeVertex outerFace = this.outerFace;
        networkGraph.addVertex(outerFace);


        supplyMap.put(outerFace, 0);
        for (MutablePair<TreeVertex, TreeVertex> edge :
                this.outerFace.sidesMap.get(0)) {
            PlanarGraphFace<TreeVertex, DefaultEdge> neighbour = edgeToFAceMap.get(GraphHelper.reverseEdge(edge));
            networkGraph.addVertex(neighbour);

            DefaultWeightedEdge e = networkGraph.addEdge(this.outerFace, neighbour);
            edgeToArcMap.put(edge, e);
            edgeToArcMap.put(GraphHelper.reverseEdge(edge), e);

            networkGraph.setEdgeWeight(e, 1);
            upperMap.put(e, Integer.MAX_VALUE);
            lowerMap.put(e, 1);
        }




        for (int j = 0; j < rectangleList.size(); j++) {
            TreeVertex face = rectangleList.get(j);
            networkGraph.addVertex(face);
            supplyMap.put(face, 0);

            for (MutablePair<TreeVertex, TreeVertex> edge :
                    rectangleList.get(j).sidesMap.get(2)) {
                PlanarGraphFace<TreeVertex, DefaultEdge> neighbour = edgeToFAceMap.get(GraphHelper.reverseEdge(edge));
                networkGraph.addVertex(neighbour);
                DefaultWeightedEdge e = networkGraph.addEdge(face, neighbour);


                edgeToArcMap.put(edge, e);
                edgeToArcMap.put(GraphHelper.reverseEdge(edge), e);
                networkGraph.setEdgeWeight(e, 1);
                upperMap.put(e, Integer.MAX_VALUE);
                lowerMap.put(e, 1);
            }

        }


        return networkGraph;
    }

    public void generateCapacities() {


        MinimumCostFlowProblem<TreeVertex,
                DefaultWeightedEdge> problem = new MinimumCostFlowProblem.MinimumCostFlowProblemImpl<>(
                networkGraph, v -> supplyMap.getOrDefault(v, 0), upperMap::get,
                e -> lowerMap.getOrDefault(e, 1));

        CapacityScalingMinimumCostFlow<TreeVertex, DefaultWeightedEdge> minimumCostFlowAlgorithm =
                new CapacityScalingMinimumCostFlow<>();


        HashMap<DefaultWeightedEdge, Double> costMap = new HashMap<>();
        for (DefaultWeightedEdge edge :
                networkGraph.edgeSet()) {

        }


        minimumCostFlow = minimumCostFlowAlgorithm.getMinimumCostFlow(problem);


    }


}
