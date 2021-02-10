import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import java.util.HashMap;
import java.util.Set;

public class DAGgenerator<V extends TreeVertex,E> {

    Graph<V, E> graph;

    public Graph<V, E> getGraph() {
        return graph;
    }

    public void setGraph(Graph<V, E> graph) {
        this.graph = graph;
    }

    public V getStart() {
        return start;
    }

    public void setStart(V start) {
        this.start = start;
    }

    public V getEnd() {
        return end;
    }

    public void setEnd(V end) {
        this.end = end;
    }

    public HashMap<V, Integer> getvIntegerHashMap() {
        return vIntegerHashMap;
    }

    public void setvIntegerHashMap(HashMap<V, Integer> vIntegerHashMap) {
        this.vIntegerHashMap = vIntegerHashMap;
    }

    public DefaultDirectedGraph<V, DefaultEdge> getDirectedGraph() {
        return directedGraph;
    }

    public void setDirectedGraph(DefaultDirectedGraph<V, DefaultEdge> directedGraph) {
        this.directedGraph = directedGraph;
    }

    V start;
    V end;
    HashMap<V, Integer> vIntegerHashMap = new HashMap<V, Integer>();
    DefaultDirectedGraph<V, DefaultEdge> directedGraph = new DefaultDirectedGraph<V, DefaultEdge>(DefaultEdge.class);


    public DAGgenerator(Graph<V, E> graph) {
        this.graph = graph;
    }

    public DAGgenerator(Graph<V, E> testGraph, V start, V end) {
        this(testGraph);
        this.start = start;
        this.end = end;
        getTotalOrder();
        generateDAG();
    }


    private void getTotalOrder() {


        int size = graph.vertexSet().size();

        vIntegerHashMap.put(end, size);
        vIntegerHashMap.put(start, 1);

        BreadthFirstIterator<V, E> veBreadthFirstIterator = new BreadthFirstIterator<>(graph, start);
        DirectedAcyclicGraph<TreeVertex, DefaultEdge> directedAcyclicGraph = new DirectedAcyclicGraph<>(DefaultEdge.class);


        while (veBreadthFirstIterator.hasNext()) {
            V node = veBreadthFirstIterator.next();
            if (!vIntegerHashMap.containsKey(node)) {
                vIntegerHashMap.put(node, vIntegerHashMap.get(veBreadthFirstIterator.getParent(node))+1);
            }
            directedAcyclicGraph.addVertex((TreeVertex) node);
            if (node != start) {
                directedAcyclicGraph.addEdge(veBreadthFirstIterator.getParent(node), node);

            }
        }
        GraphHelper.printToDOTTreeVertex(directedAcyclicGraph);
        System.out.println("Test");

    }

    private void generateDAG() {

        Graphs.addAllVertices(directedGraph, graph.vertexSet());

        for (E edge : graph.edgeSet()
        ) {

            if ((vIntegerHashMap.get(graph.getEdgeTarget((E) edge)) > vIntegerHashMap.get(graph.getEdgeSource((E) edge)))) {
                directedGraph.addEdge(graph.getEdgeSource((E) edge), graph.getEdgeTarget((E) edge));
            } else {
                directedGraph.addEdge(graph.getEdgeTarget((E) edge), graph.getEdgeSource((E) edge));
            }


        }

    }



    private void alternativeDAG(){
        DirectedAcyclicGraph<V, DefaultEdge> directedAcyclicGraph = new DirectedAcyclicGraph<>(DefaultEdge.class);

        Graphs.addAllVertices(directedAcyclicGraph, graph.vertexSet());
        Set<V> connectedTOstart = Graphs.neighborSetOf(graph, start);
        Set<V> connectedTOend = Graphs.neighborSetOf(graph, end);

        for (V node: connectedTOstart
             ) {
            directedAcyclicGraph.addEdge(start, node);

        }
        for (V node: connectedTOend
        ) {
            directedAcyclicGraph.addEdge(node, start);

        }


        GraphHelper.printToDOTTreeVertex((Graph<TreeVertex, DefaultEdge>) directedAcyclicGraph);


    }
















}


