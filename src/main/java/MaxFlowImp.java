import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MaxFlowImp {


    private final DirectedWeightedMultigraph<TreeVertex, DefaultWeightedEdge> networkGraph;
    int n;
    HashMap<TreeVertex, Integer> treeVertexIntergerHashMap = new HashMap<>();
    TreeVertex[] treeVertexArr;
    ArrayList<EdmondsKarp.Edge>[] outgoingEdgeLists;
    boolean[] visited;
    int source = 0;
    int sink;
    HashMap<DefaultWeightedEdge, Double> maxFlow = new HashMap<>();
    List<EdmondsKarp.Edge> edges = new ArrayList<>();


    /**
     * Objekt in welchem der MaxFlow-Algorithmus durchgeführt wird
     *
     *
     *
     * @param networkGraph
     */
    public MaxFlowImp(DirectedWeightedMultigraph<TreeVertex, DefaultWeightedEdge> networkGraph) {
        this.networkGraph = networkGraph;
        n = networkGraph.vertexSet().size();
        sink = n - 1;
        treeVertexArr = new TreeVertex[n];
        outgoingEdgeLists = new ArrayList[n];
        visited = new boolean[n];

        // initialise Arrays
        int counter = 1;
        for (TreeVertex vertex : networkGraph.vertexSet()
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
            TreeVertex v = treeVertexArr[i];
            List<TreeVertex> neighbors = Graphs.successorListOf(networkGraph, v);
            if (outgoingEdgeLists[i] == null) outgoingEdgeLists[i] = new ArrayList<>();
            for (TreeVertex vertex : neighbors
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
        TreeVertex source;
        TreeVertex target;
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
