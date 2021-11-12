package Algorithms.Flow;

import Datatypes.Vertex;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MaxFlowImp {


    private final DirectedWeightedMultigraph<Vertex, DefaultWeightedEdge> networkGraph;
    int n;
    HashMap<Vertex, Integer> treeVertexIntergerHashMap = new HashMap<>();
    Vertex[] treeVertexArr;
    ArrayList<Edge>[] outgoingEdgeLists;
    boolean[] visited;
    int source = 0;
    int sink;
    HashMap<DefaultWeightedEdge, Double> maxFlow = new HashMap<>();
    List<Edge> edges = new ArrayList<>();


    /**
     * Objekt in welchem der Algorithms.Flow.MaxFlow-Algorithmus durchgeführt wird
     *
     *
     *
     * @param networkGraph
     */
    public MaxFlowImp(DirectedWeightedMultigraph<Vertex, DefaultWeightedEdge> networkGraph) {
        this.networkGraph = networkGraph;
        n = networkGraph.vertexSet().size();
        sink = n - 1;
        treeVertexArr = new Vertex[n];
        outgoingEdgeLists = (ArrayList<Edge>[]) new ArrayList[n];
        visited = new boolean[n];

        // initialise Arrays
        int counter = 1;
        for (Vertex vertex : networkGraph.vertexSet()
        ) {
            if (vertex.getName().equals("solverSource")) {
                treeVertexArr[0] = vertex;
                treeVertexIntergerHashMap.put(vertex, 0);
            } else if (vertex.getName().equals("solverSink")) {
                treeVertexArr[n - 1] = vertex;
                treeVertexIntergerHashMap.put(vertex, n - 1);
            } else {
                treeVertexArr[counter] = vertex;
                treeVertexIntergerHashMap.put(vertex, counter++);
            }
        }

        // initilise AdjLists
        for (int i = 0; i < n; i++) {
            Vertex v = treeVertexArr[i];
            List<Vertex> neighbors = Graphs.successorListOf(networkGraph, v);
            if (outgoingEdgeLists[i] == null) outgoingEdgeLists[i] = new ArrayList<>();
            for (Vertex vertex : neighbors
            ) {
                EdmondsKarp.Edge e = new EdmondsKarp.Edge(i, treeVertexIntergerHashMap.get(vertex));
                outgoingEdgeLists[i].add(e);
                edges.add(e);
            }
        }


    }


    class Edge {
        DefaultWeightedEdge edge;
        Edge reverse;
        int u;
        int v;
        Vertex source;
        Vertex target;
        double capacity;
        Double flow = 0.0;

        public Edge(int u, int v) {
            this.u = u;
            this.v = v;
            source = treeVertexArr[u];
            target = treeVertexArr[v];
            if (networkGraph.containsEdge(source, target)) {
                edge = networkGraph.getEdge(source, target);
                capacity = networkGraph.getEdgeWeight(edge);
                reverse = new Edge(v, u, this);
            }


        }

        public Edge(int u, int v, Edge edge) {
            this.u = u;
            this.v = v;
            source = treeVertexArr[u];
            target = treeVertexArr[v];
            capacity = 0;
            if (outgoingEdgeLists[u] == null) outgoingEdgeLists[u] = new ArrayList<>();
            outgoingEdgeLists[u].add(this);
            reverse = edge;

        }
    }


}
