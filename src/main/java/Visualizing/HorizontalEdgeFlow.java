package Visualizing;

import Datatypes.PlanarGraphFace;
import Datatypes.TupleEdge;
import Datatypes.Vertex;
import Helperclasses.GraphHelper;
import org.jgrapht.alg.flow.mincost.CapacityScalingMinimumCostFlow;
import org.jgrapht.alg.flow.mincost.MinimumCostFlowProblem;
import org.jgrapht.alg.interfaces.MinimumCostFlowAlgorithm;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HorizontalEdgeFlow implements Runnable {

    HashMap<TupleEdge<Vertex, Vertex>, PlanarGraphFace<Vertex, DefaultEdge>> edgeToFAceMap = new HashMap<>();
    Map<Vertex, Integer> supplyMap = new HashMap<>();
    Map<DefaultWeightedEdge, Integer> lowerMap = new HashMap<>();
    Map<DefaultWeightedEdge, Integer> upperMap = new HashMap<>();
    List<PlanarGraphFace<Vertex, DefaultEdge>> rectangleList;
    PlanarGraphFace<Vertex, DefaultEdge> outerFace;
    Map<TupleEdge<Vertex, Vertex>, DefaultWeightedEdge> edgeToArcMap = new HashMap<>();
    private Thread t;
    private String threadName = "horizontal";
    private DirectedWeightedMultigraph<Vertex, DefaultWeightedEdge> networkGraph;
    private MinimumCostFlowAlgorithm.MinimumCostFlow<DefaultWeightedEdge> minimumCostFlow;

    public HorizontalEdgeFlow(List<PlanarGraphFace<Vertex, DefaultEdge>> rectangleList, PlanarGraphFace<Vertex, DefaultEdge> outerFace) {
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

    public Map<TupleEdge<Vertex, Vertex>, DefaultWeightedEdge> getEdgeToArcMap() {
        return edgeToArcMap;
    }

    public MinimumCostFlowAlgorithm.MinimumCostFlow<DefaultWeightedEdge> getMinimumCostFlow() {
        return minimumCostFlow;
    }

    public DirectedWeightedMultigraph<Vertex, DefaultWeightedEdge> generateFlowNetworkLayout2() {
        networkGraph = new DirectedWeightedMultigraph<>(DefaultWeightedEdge.class);

        List<PlanarGraphFace<Vertex, DefaultEdge>> rectangleList = this.rectangleList;
        Vertex outerFace = this.outerFace;
        networkGraph.addVertex(outerFace);


        supplyMap.put(outerFace, 0);
        // Erstellt die Bögen vom outer Face zu den inneren Faces am unteren Rand der äußeren Facette
        for (TupleEdge<Vertex, Vertex> edge :
                this.outerFace.getSidesMap().get(0)) {
            PlanarGraphFace<Vertex, DefaultEdge> neighbour = edgeToFAceMap.get(GraphHelper.reverseEdge(edge, false));
            networkGraph.addVertex(neighbour);

            DefaultWeightedEdge e = networkGraph.addEdge(this.outerFace, neighbour);
            edgeToArcMap.put(edge, e);
            edgeToArcMap.put(GraphHelper.reverseEdge(edge, false), e);

            networkGraph.setEdgeWeight(e, 1);
            upperMap.put(e, Integer.MAX_VALUE);
            lowerMap.put(e, 1);
        }

        // Gehe alle inneren Facetten durch, um die Bögen von der Facette face zur darüberliegenden Facetten
        for (int j = 0; j < rectangleList.size(); j++) {
            Vertex face = rectangleList.get(j);
            networkGraph.addVertex(face);
            supplyMap.put(face, 0);

            for (TupleEdge<Vertex, Vertex> edge :
                    rectangleList.get(j).getSidesMap().get(2)) {
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


        HashMap<DefaultWeightedEdge, Double> costMap = new HashMap<>();
        for (DefaultWeightedEdge edge :
                networkGraph.edgeSet()) {

        }


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
