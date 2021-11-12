package Algorithms.Flow;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import java.util.ArrayDeque;

public class PushRelabel extends MaxFlowImp {
// https://iq.opengenus.org/push-relabel-algorithm/
// https://github.com/phishman3579/java-algorithms-implementation/blob/master/src/com/jwetherell/algorithms/graph/PushRelabel.java
// https://en.wikipedia.org/wiki/Push%E2%80%93relabel_maximum_flow_algorithm


    int[] excessFlow;
    int[] heights;
    Vertex[] vertices;
    int[] nextNeighbour;
    private ArrayDeque<Integer> queue;


    public PushRelabel(DirectedWeightedMultigraph<Datatypes.Vertex, DefaultWeightedEdge> networkGraph) {
        super(networkGraph);

        vertices = new Vertex[n];
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] = new Vertex(i);
        }


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


    public void initialize() {
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


        if (y != x || x != excessFlow[sink]) {
            throw new RuntimeException("Ungültiges Netzwerk:" + x + y + excessFlow[sink]);
        }


    }


    public boolean push(Edge edge) {

        if (excessFlow[edge.u] > 0 && heights[edge.u] > heights[edge.v]) {
            double f = Math.min(edge.capacity - edge.flow, excessFlow[edge.u]);
            edge.flow += f;
            edge.reverse.flow -= f;

            if (excessFlow[edge.v] == 0 && edge.v != source && edge.v != sink) queue.add(edge.v);

            excessFlow[edge.u] -= f;
            excessFlow[edge.v] += f;
            vertices[edge.u].excessFlow -= f;
            vertices[edge.v].excessFlow += f;
            return true;
        }
        return false;

    }


    public void relabel(int vertexIndex) {

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

    public void discharge(int vertexIndex) {

        int edgeIndex;
        while (excessFlow[vertexIndex] > 0) {

            edgeIndex = nextNeighbour[vertexIndex];
            int size = outgoingEdgeLists[vertexIndex].size();
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


    class Vertex {

        int id;
        int excessFlow = 0;
        int height = -1;
        int flowIn = 0;
        int flowOut = 0;

        public Vertex(int i) {
            id = i;
        }


    }


}





