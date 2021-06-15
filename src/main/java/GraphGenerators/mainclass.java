package GraphGenerators;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.jgrapht.alg.spanning.KruskalMinimumSpanningTree;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;

public class mainclass {


    public static void main(String[] args) {

        Graph<String, DefaultWeightedEdge> stringGraph = createStringGraph();

        SpanningTreeAlgorithm<DefaultWeightedEdge> alg2;
        alg2 = new KruskalMinimumSpanningTree<>(stringGraph);
        SpanningTreeAlgorithm.SpanningTree<DefaultWeightedEdge> asdf =  alg2.getSpanningTree();


    }







    private static Graph<String, DefaultWeightedEdge> createStringGraph()
    {
        Graph<String, DefaultWeightedEdge> g = new SimpleGraph<>(DefaultWeightedEdge.class);

        String v1 = "v1";
        String v2 = "v2";
        String v3 = "v3";
        String sink = "sink";

        // add the vertices
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(sink);

        // add edges to create a circuit
        g.addEdge(v1, v2);
        g.addEdge(v2, v3);
        g.addEdge(v3, sink);
        g.addEdge(v1, sink);

        return g;
    }
}
