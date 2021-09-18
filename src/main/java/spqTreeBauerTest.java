import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.BiconnectivityInspector;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.traverse.DepthFirstIterator;

import java.util.*;

public class spqTreeBauerTest {


    public static void main(String[] args) {

        SPQTree tree;
        SPQNode root;

        SPQImporter spqImporter = new SPQImporter("C:/a.txt");
        spqImporter.run();


        tree = spqImporter.tree;
        root = tree.getRoot();

        DefaultDirectedGraph<SPQNode, DefaultEdge> graph2 = GraphHelper.treeToDOT(root, 2);
        GraphHelper.printTODOTSPQNode(graph2);


        DirectedMultigraph<TreeVertex, DefaultEdge> graph = new DirectedMultigraph<>(DefaultEdge.class);

        Graphs.addGraph(graph, tree.constructedGraph);

        graph.removeEdge(root.startVertex, root.sinkVertex);


        TreeVertex prev = root.startVertex;
        ArrayDeque<TreeVertex> stack = new ArrayDeque<>();
        //  stack.add(prev);

        List<ArrayDeque<TreeVertex>> qNodes = new ArrayList<>();
        SPQDepthiterator<TreeVertex, DefaultEdge> depthFirstIterator = new SPQDepthiterator(graph, root.startVertex, stack, prev);

        while (depthFirstIterator.hasNext()) {


            TreeVertex vertex = depthFirstIterator.next();
            stack.add(vertex);

            if (graph.degreeOf(vertex) != 2) {
                qNodes.add(stack);
                stack = new ArrayDeque<>();
                stack.add(vertex);
            }




            prev = vertex;

        }






        DirectedMultigraph<SPQNode, DefaultEdge> spqTree = new DirectedMultigraph<>(DefaultEdge.class);
        DirectedMultigraph<Graph<TreeVertex, DefaultEdge>, DefaultEdge> graphTree = new DirectedMultigraph<>(DefaultEdge.class);
        BiconnectivityInspector<TreeVertex, DefaultEdge> biconnectivityInspector = new BiconnectivityInspector<>(graph);
        biconnectivityInspector.getCutpoints();


        Set<Graph<TreeVertex, DefaultEdge>> blocks = biconnectivityInspector.getBlocks();

        ArrayDeque<Graph<TreeVertex, DefaultEdge>> spqQueue = new ArrayDeque<>();
        spqQueue.offer(graph);
        int counter = 0;

        while (!spqQueue.isEmpty()) {

            Graph<TreeVertex, DefaultEdge> tempGraph = spqQueue.poll();
            BiconnectivityInspector<TreeVertex, DefaultEdge> biconnectivityInspector2 = new BiconnectivityInspector<>(tempGraph);
            Set<Graph<TreeVertex, DefaultEdge>> blocks2 = biconnectivityInspector2.getBlocks();

            if (blocks2.size() > 1) {
                //  spqTree.addVertex(new SPQSNode());
                graphTree.addVertex(tempGraph);
                for (Graph<TreeVertex, DefaultEdge> graph3 : blocks2
                ) {
                    graphTree.addVertex(graph3);
                    graphTree.addEdge(tempGraph, graph3);
                }

                spqQueue.addAll(blocks2);
            } else {
                spqTree.addVertex(new SPQPNode("v" + counter++));



            }





        }


        ConnectivityInspector<TreeVertex, DefaultEdge> connectivityInspector = new ConnectivityInspector<>(graph);
        ArrayDeque<TreeVertex> arrayDeque = new ArrayDeque<>(graph.vertexSet());
        connectivityInspector.connectedSets();

        arrayDeque.remove(root.startVertex);
        arrayDeque.remove(root.sinkVertex);




        while (!arrayDeque.isEmpty()) {

            TreeVertex vertex = arrayDeque.pop();


            Set<DefaultEdge> inmcomingEdges = graph.incomingEdgesOf(vertex);
            Set<DefaultEdge> outgoingEdges = graph.outgoingEdgesOf(vertex);

            Iterator<DefaultEdge> iterator = inmcomingEdges.iterator();

            DefaultEdge prevEdge;
            if (inmcomingEdges.size() > 1) {

                prevEdge = iterator.next();
                while (iterator.hasNext() && inmcomingEdges.size() > 1) {
                    DefaultEdge edge = iterator.next();

                    if (graph.getEdgeSource(prevEdge) == graph.getEdgeSource(edge)) {

                        graph.removeEdge(prevEdge);
                        iterator = inmcomingEdges.iterator();
                    }
                    prevEdge = edge;

                }
            }

            iterator = outgoingEdges.iterator();
            prevEdge = iterator.next();
            if (outgoingEdges.size() > 1) {
                while (iterator.hasNext() && outgoingEdges.size() > 1) {

                    DefaultEdge edge = iterator.next();
                    if (graph.getEdgeTarget(prevEdge) == graph.getEdgeTarget(edge)) {
                        graph.removeEdge(prevEdge);
                        iterator = outgoingEdges.iterator();

                    }
                    prevEdge = edge;

                }
            }


            inmcomingEdges = graph.incomingEdgesOf(vertex);
            outgoingEdges = graph.outgoingEdgesOf(vertex);


            if (inmcomingEdges.size() == 1 && outgoingEdges.size() == 1) {

                graph.addEdge(graph.getEdgeSource(inmcomingEdges.iterator().next()), graph.getEdgeTarget(outgoingEdges.iterator().next()));
                graph.removeVertex(vertex);
                arrayDeque.remove(vertex);

            } else {
                arrayDeque.offerLast(vertex);
            }


        }



    }

    public void checkIncomingEdge(DefaultEdge vertexA, DefaultWeightedEdge vertexB) {


        // VTL82 3.3b)

        // VTL82. 3.3c

    }


}

class SPQDepthiterator<V, E> extends DepthFirstIterator<V, E> {


    V backVertex;
    E backEdge;
    V previous;
    ArrayDeque<V> stack;
    private ArrayDeque<V> queue;

    public SPQDepthiterator(Graph<V, E> g) {
        super(g);
    }

    public SPQDepthiterator(Graph<V, E> g, V startVertex) {
        super(g, startVertex);
    }

    public SPQDepthiterator(Graph<V, E> g, Iterable<V> startVertices) {
        super(g, startVertices);
    }

    public SPQDepthiterator(Graph<V, E> g, V startVertex, ArrayDeque<V> queue, V previous) {
        super(g, startVertex);
        this.queue = queue;
        this.previous = previous;
    }


    @Override
    protected void encounterVertexAgain(V vertex, E edge) {
        super.encounterVertexAgain(vertex, edge);
        backVertex = vertex;
        backEdge = edge;
    }


}