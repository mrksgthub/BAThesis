import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.AsGraphUnion;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Supplier;

public class stGraph extends Multigraph {


    TreeVertex sNode;
    TreeVertex tNode;


    public stGraph(Class edgeClass) {


        super(edgeClass);
    }

    public stGraph(Supplier vertexSupplier, Supplier edgeSupplier, boolean weighted) {
        super(vertexSupplier, edgeSupplier, weighted);
    }


    public TreeVertex getsNode() {
        return sNode;
    }

    public void setsNode(TreeVertex sNode) {
        this.sNode = sNode;
    }

    public TreeVertex gettNode() {
        return tNode;
    }

    public void settNode(TreeVertex tNode) {
        this.tNode = tNode;
    }


    public <V> void connectGraph(stGraph graph, V vertex1, V vertex2) {



    }


    public <V> void mergeGraphsSnode(stGraph graph) {
        replaceVertex(this, tNode, graph.getsNode());
        this.settNode(graph.gettNode());


        Graphs.addGraph(this, graph);



    }

    public <V> void mergeGraphsPnode(stGraph graph) {
        replaceVertex(this, sNode, graph.getsNode());
        replaceVertex(this, tNode, graph.gettNode());
        this.setsNode(graph.getsNode());
        this.settNode(graph.gettNode());

     //   AsGraphUnion<TreeVertex, DefaultEdge> union = new AsGraphUnion<>(this, graph);

        Graphs.addGraph(this, graph);




    }

    /**
     * Replaces a specific Vertex in a jgraphT Undirected Graph with another Vertex
     * @param graph
     * @param vertex
     * @param replace
     * @param <V>
     * @param <E>
     */
    public <V,E> void replaceVertex(Graph<V, E> graph, V vertex, V replace) {
        graph.addVertex(replace);

        Set<E> edgeSet1 =  new HashSet<E>(graph.incomingEdgesOf(vertex));
        Set<E> outgoingEdges = new HashSet<E>(graph.outgoingEdgesOf(vertex));


        for (E edge : edgeSet1) {
            if (vertex == graph.getEdgeTarget(edge)) {
                graph.addEdge(graph.getEdgeSource(edge), replace);
            }
            else if (vertex == graph.getEdgeSource(edge)) {

                graph.addEdge(replace, graph.getEdgeTarget(edge));

            }

        }

        graph.removeVertex(vertex);
    }


    public TreeVertex findVertex(String name){

        Set<TreeVertex> vertexSet= this.vertexSet();


        for (TreeVertex node:vertexSet
             ) {
            if (name.equals(node.getName())) {
                return node;
            }
        }

        System.out.println("Node not found");
        return null;

    }



}
