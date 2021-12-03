package PlanarityAndAngles.Flow;

import Datastructures.Vertex;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import java.util.ArrayDeque;

class PushRelabel extends MaxFlowImp {
// https://iq.opengenus.org/push-relabel-algorithm/
// https://github.com/phishman3579/java-algorithms-implementation/blob/master/src/com/jwetherell/algorithms/graph/PushRelabel.java
// https://en.wikipedia.org/wiki/Push%E2%80%93relabel_maximum_flow_algorithm
// TEST

    private int[] excessFlow;
    private int[] heights;
    private int[] nextNeighbour;
    private ArrayDeque<Integer> queue;


    public PushRelabel(DirectedWeightedMultigraph<Vertex, DefaultWeightedEdge> networkGraph) {
        super(networkGraph);


    }

    private void preflow() {
        for (int i = 1; i < excessFlow.length - 1; i++) {
            excessFlow[i] = 0;
            heights[i] = 0;
        }

        excessFlow[source] = Integer.MAX_VALUE;
        for (Edge edge : outgoingEdgeLists[source]
        ) {
            push(edge);
        }


    }


    public boolean run() {
        // initialisiere Arrays
        heights = new int[n];
        heights[source] = n;
        heights[sink] = 0;
        excessFlow = new int[n];
        nextNeighbour = new int[n];
        queue = new ArrayDeque<>();

        // Preflow (siehe Wikipedia)
        preflow();

        for (Edge edge : outgoingEdgeLists[source]
        ) {
            push(edge);
        }

        // Push Relabel durchlauf (siehe Wikiedia)
        for (int i = 1; i < n - 1; i++) {
            queue.add(i);
        }
        while (!queue.isEmpty()) {
            discharge(queue.poll());
        }


        for (Edge edge : edges
        ) {
            Double flow = edge.flow;
            maxFlow.putIfAbsent(edge.edge, flow);
        }

        double x = 0;
        double y = 0;
        for (Edge edge : outgoingEdgeLists[0]
        ) {
            x += edge.capacity;
            y += edge.flow;

        }


   /*     if (y != x || x != excessFlow[sink]) {
            throw new RuntimeException("Ungültiges Netzwerk:" + x + y + excessFlow[sink]);
        }*/

        if (y != x || x != excessFlow[sink]) {
            return false;
        } else {
            return true;
        }


    }


    private boolean push(Edge edge) {

        if (excessFlow[edge.u] > 0 && heights[edge.u] > heights[edge.v]) {
            int f = (int) (Math.min(edge.capacity - edge.flow, excessFlow[edge.u]));
            edge.flow += f;
            edge.reverse.flow -= f;

            if (excessFlow[edge.v] == 0 && edge.v != source && edge.v != sink) queue.add(edge.v);
            excessFlow[edge.u] -= f;
            excessFlow[edge.v] += f;

            return true;
        }
        return false;

    }


    private void relabel(int vertexIndex) {

        double min = Integer.MAX_VALUE;
        for (Edge edge : outgoingEdgeLists[vertexIndex]
        ) {
            // excessFlow[vertexIndex] > 0 && heights[vertexIndex] <= heights[edge.v] && was removed
            if (edge.capacity - edge.flow > 0) {
                min = Math.min(min, heights[edge.v]);
            }
            heights[vertexIndex] = (int) (1 + min);

        }
    }

    private void discharge(int vertexIndex) {

        int edgeIndex;
        while (excessFlow[vertexIndex] > 0) {

            edgeIndex = nextNeighbour[vertexIndex];
            int size = outgoingEdgeLists[vertexIndex].size(); // hat man alle Kanten in der AdjList durchgemacht, dann relabel
            if (edgeIndex < size) {
                Edge edge = outgoingEdgeLists[vertexIndex].get(edgeIndex);

                if (edge.capacity > edge.flow && heights[edge.u] > heights[edge.v]) {
                    push(edge);
                } else {
                    nextNeighbour[vertexIndex]++;
                }
            } else {
                relabel(vertexIndex);
                nextNeighbour[vertexIndex] = 0;
            }
        }
    }
}





