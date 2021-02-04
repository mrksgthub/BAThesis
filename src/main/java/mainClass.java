import org.jbpt.graph.Edge;
import org.jbpt.hypergraph.abs.Vertex;
import org.jgrapht.Graph;
import org.jgrapht.GraphType;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;


import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;

public class mainClass {

    public static void main(String[] args) {



      Graph<Node, DefaultEdge> graph =  new DefaultUndirectedGraph(DefaultEdge.class);
      Node node1 = new Node("Bob");
      graph.addVertex(node1);

      Node node2 = new Node("Alice");
      graph.addVertex(node2);
      Node node3 = new Node("Jimmy");
      graph.addVertex(node3);
      graph.addEdge(node1, node3);
      graph.addEdge(node1, node3);
      graph.addEdge(node2, node3);

      org.jbpt.graph.Graph test = new org.jbpt.graph.Graph();
      Vertex vertex1 = new Vertex("Alice");
      Vertex vertex2 = new Vertex("Bob");
      Vertex vertex3 = new Vertex("Chris");
      Vertex vertex4 = new Vertex("Detlef");
      test.addEdge(vertex1, vertex2);
      test.addEdge(vertex2, vertex3);
      test.addEdge(vertex3, vertex4);
      test.addEdge(vertex4, vertex1);



    }
}
