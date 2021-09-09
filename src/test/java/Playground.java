import org.jgrapht.Graph;
import org.jgrapht.graph.AsGraphUnion;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.util.SupplierUtil;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;


public class Playground {


    public static <V, E> void replaceVertex(Graph<V, E> graph, V vertex, V replace) {
        graph.addVertex(replace);


        Set<E> edgeSet1 = new HashSet<E>(graph.incomingEdgesOf(vertex));
        Set<E> outgoingEdges = new HashSet<E>(graph.outgoingEdgesOf(vertex));


        for (E edge : edgeSet1) {
            if (vertex == graph.getEdgeTarget(edge)) {
                graph.addEdge(graph.getEdgeSource(edge), replace);
            } else if (vertex == graph.getEdgeSource(edge)) {

                graph.addEdge(replace, graph.getEdgeTarget(edge));

            }

        }

        graph.removeVertex(vertex);
    }


    // Methoden hinzfügen: coonectGraphs: verbindet 2 Graphen dadurch, dass man knoten1 von Graph1 mit Knoten2 von Graph2 verbindet
    //                     dann noch 2 stMergeP und stMergeS, bei der jeweils replaceVertex genutzt wird um start und endkoten entsprechend zu verbinden und dann einen neuen Graphen zu erzeigen

    @Test
    public void nodeMerge() {


        Supplier<String> vSupplier = new Supplier<String>() {
            private int id = 0;

            @Override
            public String get() {

                return "v" + id++;
            }
        };


        Graph<String, DefaultEdge> nodeMergeTestGraph = new DefaultUndirectedGraph<String, DefaultEdge>(vSupplier, SupplierUtil.createDefaultEdgeSupplier(), false);

        nodeMergeTestGraph.addVertex("a");
        nodeMergeTestGraph.addVertex("b");
        nodeMergeTestGraph.addEdge("a", "b");


        Graph<String, DefaultEdge> nodeMergeTestGraph2 = new DefaultUndirectedGraph<String, DefaultEdge>(vSupplier, SupplierUtil.createDefaultEdgeSupplier(), false);

        nodeMergeTestGraph2.addVertex("c");
        nodeMergeTestGraph2.addVertex("d");
        nodeMergeTestGraph2.addVertex("e");
        nodeMergeTestGraph2.addEdge("c", "d");
        nodeMergeTestGraph2.addEdge("d", "e");

        // Beispiel einer P operation in einem Graphen
        replaceVertex(nodeMergeTestGraph2, "c", "a");
        replaceVertex(nodeMergeTestGraph2, "e", "b");


        AsGraphUnion<String, DefaultEdge> union = new AsGraphUnion<>(nodeMergeTestGraph, nodeMergeTestGraph2);


/*            Angedacht: public static <V, E> void replaceVertex(Graph<V, E> graph, V Svertex, V Sreplace, V Tvertex, V Treplace){

https://stackoverflow.com/questions/8766741/changing-contents-of-vertex-with-jgrapht
        public static <V, E> void replaceVertex(Graph<V, E> graph, V vertex, V replace) {
            graph.addVertex(replace);
            for (E edge : graph.outgoingEdgesOf(vertex)) graph.addEdge(replace, graph.getEdgeTarget(edge), edge);
            for (E edge : graph.incomingEdgesOf(vertex)) graph.addEdge(graph.getEdgeSource(edge), replace, edge);
            graph.removeVertex(vertex);
            }
  */

    }

    @Test
    public void graphPMerge() {


    }

    @Test
    public void graphSMerge() {

    }

    @Test
    public void addVerticesToEdge() {

    }


    @Test
    public void mergeVertices() {


    }


}
