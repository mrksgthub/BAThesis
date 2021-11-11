package Algorithms.Flow;

import Datatypes.Vertex;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import java.util.ArrayDeque;
import java.util.Queue;

public class EdmondsKarp extends MaxFlowImp {
// https://github.com/williamfiset/Algorithms/blob/2eed08cd0de39ce73d445e83e6aa1476741edc51/src/main/java/com/williamfiset/algorithms/graphtheory/networkflow/examples/EdmondsKarpExample.java#L154
//    https://en.wikibooks.org/wiki/Algorithm_Implementation/Graphs/Maximum_flow/Edmonds-Karp
// Wikipedia


    public EdmondsKarp(DirectedWeightedMultigraph<Vertex, DefaultWeightedEdge> networkGraph) {
        super(networkGraph);


    }

    public void initialize() {


        int maxflow = 0;
        int flowAugment;

        do {

            for (int i = 0; i < n; ++i) {
                visited[i] = false;
            }
            flowAugment = bfs();
            maxflow += flowAugment;
        } while (flowAugment != 0);

        // debugging
        double x = 0;
        for (Edge edge : outgoingEdgeLists[0]
        ) {
            x += edge.capacity;
        }

        if (maxflow != x) {
            throw new IllegalArgumentException("maxflow " + maxflow + " x " + x);
        }

        int count = 0;
        for (Edge edge : edges
        ) {
            Double flow = edge.flow;
            maxFlow.putIfAbsent(edge.edge, flow);
            count++;
        }

    }


    public int bfs() {


        visited[source] = true;
        Queue<Integer> q = new ArrayDeque<>(n);
        q.offer(source);

        Edge[] parent = new Edge[n];
        // Standard BFS Loop
        while (!q.isEmpty()) {
            int current = q.poll();
            if (current == sink) break;

            for (Edge edge : outgoingEdgeLists[current]
            ) {
                int remainingCap = (int) (edge.capacity - edge.flow);
                if (parent[edge.v] == null && edge.capacity > edge.flow && edge.v != source) {
                    visited[edge.v] = true;
                    q.offer(edge.v);
                    parent[edge.v] = edge;
                }
            }
        }


        if (parent[sink] == null) return 0;

        int bottleneck = Integer.MAX_VALUE;
        for (Edge edge = parent[sink]; edge != null; edge = parent[edge.u])
            bottleneck = (int) Math.min(bottleneck, edge.capacity - edge.flow);


        for (Edge e = parent[sink]; e != null; e = parent[e.u]) {
            e.flow += bottleneck;
            e.reverse.flow = e.reverse.flow - bottleneck;
        }

        return bottleneck;
    }


}
