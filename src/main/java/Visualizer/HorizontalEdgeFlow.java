package Visualizer;

import Datastructures.PlanarGraphFace;
import Datastructures.Vertex;
import Datastructures.TupleEdge;
import org.jgrapht.alg.flow.mincost.CapacityScalingMinimumCostFlow;
import org.jgrapht.alg.flow.mincost.MinimumCostFlowProblem;
import org.jgrapht.alg.interfaces.MinimumCostFlowAlgorithm;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HorizontalEdgeFlow implements Runnable {

    private final HashMap<TupleEdge<Vertex, Vertex>, PlanarGraphFace<Vertex>> edgeToFAceMap = new HashMap<>();
    private final Map<Vertex, Integer> supplyMap = new HashMap<>();
    private final Map<DefaultWeightedEdge, Integer> lowerMap = new HashMap<>();
    private final Map<DefaultWeightedEdge, Integer> upperMap = new HashMap<>();
    private List<PlanarGraphFace<Vertex>> innerFaceList = new ArrayList<>();
    private final PlanarGraphFace<Vertex> outerFace;
    Map<TupleEdge<Vertex, Vertex>, DefaultWeightedEdge> edgeToArcMap = new HashMap<>();
    private Thread t;
    private final String threadName = "horizontal";
    private DirectedWeightedMultigraph<Vertex, DefaultWeightedEdge> networkGraph;
    private MinimumCostFlowAlgorithm.MinimumCostFlow<DefaultWeightedEdge> minimumCostFlow;

    public HorizontalEdgeFlow(List<PlanarGraphFace<Vertex>> innerFaceList, PlanarGraphFace<Vertex> outerFace) {
        this.innerFaceList = innerFaceList;
        this.outerFace = outerFace;

        for (TupleEdge<Vertex, Vertex> edge : outerFace.getEdgeList()
        ) {
            edgeToFAceMap.put(edge, outerFace);
        }


        for (PlanarGraphFace<Vertex> face : innerFaceList
        ) {
            for (TupleEdge<Vertex, Vertex> edge : face.getEdgeList()
            ) {
                edgeToFAceMap.put(edge, face);
            }

        }


    }

    public Map<TupleEdge<Vertex, Vertex>, DefaultWeightedEdge> getEdgeToArcMap() {
        return edgeToArcMap;
    }

    public MinimumCostFlowAlgorithm.MinimumCostFlow<DefaultWeightedEdge> getMinimumCostFlow() {
        return minimumCostFlow;
    }

    public DirectedWeightedMultigraph<Vertex, DefaultWeightedEdge> generateFlowNetworkLayout2() {
        networkGraph = new DirectedWeightedMultigraph<>(DefaultWeightedEdge.class);

        List<PlanarGraphFace<Vertex>> rectangleList = this.innerFaceList;
        Vertex outerFace = this.outerFace;
        networkGraph.addVertex(outerFace);


        supplyMap.put(outerFace, 0);
        for (TupleEdge<Vertex, Vertex> edge :
                this.outerFace.getSidesMap().get(0)) {
            PlanarGraphFace<Vertex> neighbour = edgeToFAceMap.get(TupleEdge.reverseEdge(edge));
            networkGraph.addVertex(neighbour);

            DefaultWeightedEdge e = networkGraph.addEdge(this.outerFace, neighbour);
            edgeToArcMap.put(edge, e);
            edgeToArcMap.put(TupleEdge.reverseEdge(edge), e);

            networkGraph.setEdgeWeight(e, 1);
            upperMap.put(e, Integer.MAX_VALUE);
            lowerMap.put(e, 1);
        }


        for (int j = 0; j < rectangleList.size(); j++) {
            Vertex face = rectangleList.get(j);
            networkGraph.addVertex(face);
            supplyMap.put(face, 0);

            for (TupleEdge<Vertex, Vertex> edge :
                    rectangleList.get(j).getSidesMap().get(2)) {
                PlanarGraphFace<Vertex> neighbour = edgeToFAceMap.get(TupleEdge.reverseEdge(edge));
                networkGraph.addVertex(neighbour);
                DefaultWeightedEdge e = networkGraph.addEdge(face, neighbour);


                edgeToArcMap.put(edge, e);
                edgeToArcMap.put(TupleEdge.reverseEdge(edge), e);
                networkGraph.setEdgeWeight(e, 1);
                upperMap.put(e, Integer.MAX_VALUE);
                lowerMap.put(e, 1);
            }

        }


        return networkGraph;
    }

    private void generateCapacities() {


        MinimumCostFlowProblem<Vertex,
                DefaultWeightedEdge> problem = new MinimumCostFlowProblem.MinimumCostFlowProblemImpl<>(
                networkGraph, v -> supplyMap.getOrDefault(v, 0), upperMap::get,
                e -> lowerMap.getOrDefault(e, 1));

        CapacityScalingMinimumCostFlow<Vertex, DefaultWeightedEdge> minimumCostFlowAlgorithm =
                new CapacityScalingMinimumCostFlow<>();



        minimumCostFlow = minimumCostFlowAlgorithm.getMinimumCostFlow(problem);


    }


    @Override
    public void run() {
        System.out.println("Horizontal Edge Flow");
        generateCapacities();
    }

    public void start() {
        System.out.println("Starting " + threadName);
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }


}
