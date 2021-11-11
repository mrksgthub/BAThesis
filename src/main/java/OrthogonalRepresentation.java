import Datatypes.Vertex;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.ArrayList;
import java.util.List;


public class OrthogonalRepresentation {

    DefaultDirectedGraph<Vertex, DefaultEdge> graph;
    List<Vertex> vertices = new ArrayList<Vertex>();


    public OrthogonalRepresentation(DefaultDirectedGraph<Vertex, DefaultEdge> graph, List<Vertex> vertices) {
        this.graph = graph;
        this.vertices = vertices;
    }
}
