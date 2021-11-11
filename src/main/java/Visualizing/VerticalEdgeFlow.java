package Visualizing;

import Datatypes.PlanarGraphFace;
import Datatypes.Vertex;
import Datatypes.TupleEdge;
import Helperclasses.GraphHelper;
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

public class VerticalEdgeFlow implements Runnable {

    HashMap<TupleEdge<Vertex, Vertex>, PlanarGraphFace<Vertex, DefaultEdge>> edgeToFAceMap = new HashMap<>();
    Map<Vertex, Integer> supplyMap = new HashMap<>();
    Map<DefaultWeightedEdge, Integer> lowerMap = new HashMap<>();
    Map<DefaultWeightedEdge, Integer> upperMap = new HashMap<>();
    Map<TupleEdge<Vertex, Vertex>, DefaultWeightedEdge> edgeToArcMap = new HashMap<>();
    List<PlanarGraphFace<Vertex, DefaultEdge>> rectangleList = new ArrayList<>();
    PlanarGraphFace<Vertex, DefaultEdge> outerFace;
    private Thread t;
    private String threadName = "vertical";
    private DirectedWeightedMultigraph<Vertex, DefaultWeightedEdge> networkGraph;
    private MinimumCostFlowAlgorithm.MinimumCostFlow<DefaultWeightedEdge> minimumCostFlow;


    public VerticalEdgeFlow(List<PlanarGraphFace<Vertex, DefaultEdge>> rectangleList, PlanarGraphFace<Vertex, DefaultEdge> outerFace) {
        this.rectangleList = rectangleList;
        this.outerFace = outerFace;

        for (TupleEdge<Vertex, Vertex> edge : outerFace.getEdgeList()
        ) {
            edgeToFAceMap.put(edge, outerFace);
        }


        for (PlanarGraphFace<Vertex, DefaultEdge> face : rectangleList
        ) {
            for (TupleEdge<Vertex, Vertex> edge : face.getEdgeList()
            ) {
                edgeToFAceMap.put(edge, face);
            }

        }


    }

    public HashMap<TupleEdge<Vertex, Vertex>, PlanarGraphFace<Vertex, DefaultEdge>> getEdgeToFAceMap() {
        return edgeToFAceMap;
    }

    public void setEdgeToFAceMap(HashMap<TupleEdge<Vertex, Vertex>, PlanarGraphFace<Vertex, DefaultEdge>> edgeToFAceMap) {
        this.edgeToFAceMap = edgeToFAceMap;
    }

    public Map<Vertex, Integer> getSupplyMap() {
        return supplyMap;
    }

    public void setSupplyMap(Map<Vertex, Integer> supplyMap) {
        this.supplyMap = supplyMap;
    }

    public Map<DefaultWeightedEdge, Integer> getLowerMap() {
        return lowerMap;
    }

    public void setLowerMap(Map<DefaultWeightedEdge, Integer> lowerMap) {
        this.lowerMap = lowerMap;
    }

    public Map<DefaultWeightedEdge, Integer> getUpperMap() {
        return upperMap;
    }

    public void setUpperMap(Map<DefaultWeightedEdge, Integer> upperMap) {
        this.upperMap = upperMap;
    }

    public Map<TupleEdge<Vertex, Vertex>, DefaultWeightedEdge> getEdgeToArcMap() {
        return edgeToArcMap;
    }

    public void setEdgeToArcMap(Map<TupleEdge<Vertex, Vertex>, DefaultWeightedEdge> edgeToArcMap) {
        this.edgeToArcMap = edgeToArcMap;
    }

    public List<PlanarGraphFace<Vertex, DefaultEdge>> getRectangleList() {
        return rectangleList;
    }

    public void setRectangleList(List<PlanarGraphFace<Vertex, DefaultEdge>> rectangleList) {
        this.rectangleList = rectangleList;
    }

    public PlanarGraphFace<Vertex, DefaultEdge> getOuterFace() {
        return outerFace;
    }

    public void setOuterFace(PlanarGraphFace<Vertex, DefaultEdge> outerFace) {
        this.outerFace = outerFace;
    }

    public MinimumCostFlowAlgorithm.MinimumCostFlow<DefaultWeightedEdge> getMinimumCostFlow() {
        return minimumCostFlow;
    }

    public void setMinimumCostFlow(MinimumCostFlowAlgorithm.MinimumCostFlow<DefaultWeightedEdge> minimumCostFlow) {
        this.minimumCostFlow = minimumCostFlow;
    }

    public DirectedWeightedMultigraph<Vertex, DefaultWeightedEdge> generateFlowNetworkLayout2() {
        networkGraph = new DirectedWeightedMultigraph<>(DefaultWeightedEdge.class);

        List<PlanarGraphFace<Vertex, DefaultEdge>> rectangleList = this.rectangleList;
        Vertex outerFace = this.outerFace;
        networkGraph.addVertex(outerFace);


        supplyMap.put(outerFace, 0);
        for (TupleEdge<Vertex, Vertex> edge :
                this.outerFace.getSidesMap().get(1)) {
            PlanarGraphFace<Vertex, DefaultEdge> neighbour = edgeToFAceMap.get(GraphHelper.reverseEdge(edge, false));
            networkGraph.addVertex(neighbour);

            DefaultWeightedEdge e = networkGraph.addEdge(this.outerFace, neighbour);

            edgeToArcMap.put(edge, e);
            edgeToArcMap.put(GraphHelper.reverseEdge(edge, false), e);

            networkGraph.setEdgeWeight(e, 1);
            upperMap.put(e, Integer.MAX_VALUE);
            lowerMap.put(e, 1);
        }


        for (int j = 0; j < rectangleList.size(); j++) {
            Vertex face = rectangleList.get(j);
            networkGraph.addVertex(face);
            supplyMap.put(face, 0);

            for (TupleEdge<Vertex, Vertex> edge :
                    rectangleList.get(j).getSidesMap().get(3)) {
                PlanarGraphFace<Vertex, DefaultEdge> neighbour = edgeToFAceMap.get(GraphHelper.reverseEdge(edge, false));
                networkGraph.addVertex(neighbour);

                DefaultWeightedEdge e = networkGraph.addEdge(face, neighbour);
                edgeToArcMap.put(edge, e);
                edgeToArcMap.put(GraphHelper.reverseEdge(edge, false), e);

                networkGraph.setEdgeWeight(e, 1);
                upperMap.put(e, Integer.MAX_VALUE);
                lowerMap.put(e, 1);
            }

        }


        return networkGraph;
    }

    public void generateCapacities() {


        MinimumCostFlowProblem<Vertex,
                DefaultWeightedEdge> problem = new MinimumCostFlowProblem.MinimumCostFlowProblemImpl<>(
                networkGraph, v -> supplyMap.getOrDefault(v, 0), upperMap::get,
                e -> lowerMap.getOrDefault(e, 1));

        CapacityScalingMinimumCostFlow<Vertex, DefaultWeightedEdge> minimumCostFlowAlgorithm =
                new CapacityScalingMinimumCostFlow<>();


        HashMap<DefaultWeightedEdge, Double> costMap = new HashMap();
        for (DefaultWeightedEdge edge :
                networkGraph.edgeSet()) {

        }


        minimumCostFlow = minimumCostFlowAlgorithm.getMinimumCostFlow(problem);


    }


    @Override
    public void run() {
        System.out.println("Vertical Edge Flow");
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
