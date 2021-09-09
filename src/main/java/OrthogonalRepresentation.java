import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.ArrayList;
import java.util.List;


public class OrthogonalRepresentation {

    DefaultDirectedGraph<TreeVertex, DefaultEdge> graph;
    List<TreeVertex> vertices = new ArrayList<TreeVertex>();


    public OrthogonalRepresentation(DefaultDirectedGraph<TreeVertex, DefaultEdge> graph, List<TreeVertex> vertices) {
        this.graph = graph;
        this.vertices = vertices;
    }
}
