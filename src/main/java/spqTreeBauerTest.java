import org.jgrapht.Graph;
import org.jgrapht.Graphs;
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
        DirectedMultigraph<TreeVertex, SPQEdge> graphSPQNodeEdges = new DirectedMultigraph<>(SPQEdge.class);
        DirectedMultigraph<SPQNode, DefaultEdge> SPQTree = new DirectedMultigraph<>(DefaultEdge.class);


        Graphs.addGraph(graph, tree.constructedGraph);

        graph.removeEdge(root.startVertex, root.sinkVertex);

        for (TreeVertex vertex : graph.vertexSet()) {
            graphSPQNodeEdges.addVertex(vertex);
        }
        for (DefaultEdge edge : graph.edgeSet()) {
            SPQQNode subNode = new SPQQNode(graph.getEdgeSource(edge), graph.getEdgeTarget(edge), false);
            SPQQNode node = new SPQQNode(graph.getEdgeSource(edge), graph.getEdgeTarget(edge), true);
            graphSPQNodeEdges.addEdge(graph.getEdgeSource(edge), graph.getEdgeTarget(edge), new SPQEdge(node));
            SPQTree.addVertex(node);
            SPQTree.addVertex(subNode);
            SPQTree.addEdge(node, subNode);
        }


        TreeVertex prev = root.startVertex;
        ArrayDeque<TreeVertex> stack = new ArrayDeque<>();
        //  stack.add(prev);

        List<ArrayDeque<TreeVertex>> qNodes = new ArrayList<>();
        SPQDepthiterator<TreeVertex, SPQEdge> depthFirstIterator = new SPQDepthiterator(graphSPQNodeEdges, root.startVertex, stack, prev);

        while (depthFirstIterator.hasNext()) {


            TreeVertex vertex = depthFirstIterator.next();
            stack.add(vertex);

            if (graphSPQNodeEdges.degreeOf(vertex) != 2) {
                qNodes.add(stack);
                stack = new ArrayDeque<>();
                stack.add(vertex);
            }


            prev = vertex;

        }






/*        DirectedMultigraph<SPQNode, SPQEdge> spqTree = new DirectedMultigraph<>(SPQEdge.class);
        DirectedMultigraph<Graph<TreeVertex, SPQEdge>, SPQEdge> graphTree = new DirectedMultigraph<>(SPQEdge.class);
        BiconnectivityInspector<TreeVertex, SPQEdge> biconnectivityInspector = new BiconnectivityInspector<>(graphSPQNodeEdges);
        biconnectivityInspector.getCutpoints();


        Set<Graph<TreeVertex, SPQEdge>> blocks = biconnectivityInspector.getBlocks();

        ArrayDeque<Graph<TreeVertex, SPQEdge>> spqQueue = new ArrayDeque<>();
        spqQueue.offer(graphSPQNodeEdges);
        int counter = 0;

        while (!spqQueue.isEmpty()) {

            Graph<TreeVertex, SPQEdge> tempGraph = spqQueue.poll();
            BiconnectivityInspector<TreeVertex, SPQEdge> biconnectivityInspector2 = new BiconnectivityInspector<>(tempGraph);
            Set<Graph<TreeVertex, SPQEdge>> blocks2 = biconnectivityInspector2.getBlocks();

            if (blocks2.size() > 1) {
                //  spqTree.addVertex(new SPQSNode());
                graphTree.addVertex(tempGraph);
                for (Graph<TreeVertex, SPQEdge> graph3 : blocks2
                ) {
                    graphTree.addVertex(graph3);
                    graphTree.addEdge(tempGraph, graph3);
                }

                spqQueue.addAll(blocks2);
            } else {
                spqTree.addVertex(new SPQPNode("v" + counter++));

            }

        }*/


        ConnectivityInspector<TreeVertex, SPQEdge> connectivityInspector = new ConnectivityInspector<>(graphSPQNodeEdges);
        ArrayDeque<TreeVertex> arrayDeque = new ArrayDeque<>(graph.vertexSet());
        connectivityInspector.connectedSets();

        arrayDeque.remove(root.startVertex);
        arrayDeque.remove(root.sinkVertex);


        SPQNode rootNode = null;


        while (!arrayDeque.isEmpty()) {

            TreeVertex vertex = arrayDeque.pop();


            Set<SPQEdge> inmcomingEdges = graphSPQNodeEdges.incomingEdgesOf(vertex);
            Set<SPQEdge> outgoingEdges = graphSPQNodeEdges.outgoingEdgesOf(vertex);

            Iterator<SPQEdge> iterator = inmcomingEdges.iterator();

            SPQEdge prevEdge;
            if (inmcomingEdges.size() > 1) { // P-Node

                prevEdge = iterator.next();
                while (iterator.hasNext() && inmcomingEdges.size() > 1) { // Stack bessere Lösung
                    SPQEdge edge = iterator.next();

                    if (graphSPQNodeEdges.getEdgeSource(prevEdge) == graphSPQNodeEdges.getEdgeSource(edge)) {
                        SPQNode nextNode = edge.node;
                        edge.node = new SPQPNode(graphSPQNodeEdges.getEdgeSource(edge), graphSPQNodeEdges.getEdgeTarget(edge));
                        SPQTree.addVertex(edge.node);
                        SPQTree.addEdge(edge.node, prevEdge.node);
                        SPQTree.addEdge(edge.node, nextNode);

                        graphSPQNodeEdges.removeEdge(prevEdge);
                        iterator = inmcomingEdges.iterator();
                    }
                    prevEdge = edge;

                }
            }

            iterator = outgoingEdges.iterator();
            prevEdge = iterator.next();
            if (outgoingEdges.size() > 1) { // P-Node
                while (iterator.hasNext() && outgoingEdges.size() > 1) {

                    SPQEdge edge = iterator.next();
                    if (graphSPQNodeEdges.getEdgeTarget(prevEdge) == graphSPQNodeEdges.getEdgeTarget(edge)) {
                        SPQNode nextNode = edge.node;
                        edge.node = new SPQPNode(graphSPQNodeEdges.getEdgeSource(edge), graphSPQNodeEdges.getEdgeTarget(edge));


                        SPQTree.addVertex(edge.node);
                        SPQTree.addEdge(edge.node, prevEdge.node);
                        SPQTree.addEdge(edge.node, nextNode);

                        graphSPQNodeEdges.removeEdge(prevEdge);
                        iterator = outgoingEdges.iterator();

                    }
                    prevEdge = edge;

                }
            }


            inmcomingEdges = graphSPQNodeEdges.incomingEdgesOf(vertex);
            outgoingEdges = graphSPQNodeEdges.outgoingEdgesOf(vertex);


            if (inmcomingEdges.size() == 1 && outgoingEdges.size() == 1) { // new SNode

                SPQEdge incomingEdge = inmcomingEdges.iterator().next();
                TreeVertex edgeSource = graphSPQNodeEdges.getEdgeSource(incomingEdge);
                SPQEdge outgoingEdge = outgoingEdges.iterator().next();
                TreeVertex edgeTarget = graphSPQNodeEdges.getEdgeTarget(outgoingEdge);


                SPQSNode node = new SPQSNode(edgeSource, edgeTarget);
                graphSPQNodeEdges.addEdge(edgeSource, edgeTarget, new SPQEdge(node));
                SPQTree.addVertex(node);
                SPQTree.addEdge(node, incomingEdge.node);
                SPQTree.addEdge(node, outgoingEdge.node);

                graphSPQNodeEdges.removeVertex(vertex);
                arrayDeque.remove(vertex);

            } else {
                arrayDeque.offerLast(vertex);
            }

        }


        GraphHelper.printTODOTSPQNode(SPQTree);


        for (SPQNode spqNode : SPQTree.vertexSet()) {
            if (SPQTree.inDegreeOf(spqNode) == 0) {
                rootNode = spqNode;
                break;
            }
        }


        // DFS für den contract schritt
        List<ArrayDeque<SPQNode>> qNodes2 = new ArrayList<>();
        // create an empty stack and push the root node
        Stack<SPQNode> stack2 = new Stack();
        stack2.push(rootNode);

        // create another stack to store postorder traversal
        Stack<SPQNode> out = new Stack();

        // loop till stack is empty
        while (!stack2.empty()) {

            SPQNode current = stack2.pop();
            out.push(current);


            for (DefaultEdge edge : SPQTree.outgoingEdgesOf(current)) {

                stack2.push(SPQTree.getEdgeTarget(edge));
            }


        }



        while (!out.isEmpty()) {
            SPQNode vertex = out.pop();

            if (SPQTree.containsVertex(vertex) && SPQTree.incomingEdgesOf(vertex).size() == 1) {

                DefaultEdge edge = SPQTree.incomingEdgesOf(vertex).stream().iterator().next();
                SPQNode parentNode = SPQTree.getEdgeSource(edge);
                ArrayList<SPQNode> parentSuccessors = new ArrayList<>(Graphs.successorListOf(SPQTree, parentNode));

                if (vertex.getNodeType() == parentNode.getNodeType() && (vertex.getNodeType() == NodeTypesEnum.NODETYPE.S)) {
                    List<DefaultEdge> childrenSuccesors = new ArrayList<>();


          /*          childrenSuccesors.addAll(SPQTree.outgoingEdgesOf(SPQTree.getEdgeTarget(edge)));
                    for (DefaultEdge outEdge : childrenSuccesors) {
                        SPQNode edgeTarget = SPQTree.getEdgeTarget(outEdge);
                        SPQTree.addEdge(parentNode, edgeTarget);
                    }*/


                    List<DefaultEdge> outEdges = new ArrayList<>(SPQTree.outgoingEdgesOf(parentNode));
                    SPQTree.removeAllEdges(outEdges);
                    int index = parentSuccessors.indexOf(vertex);
                    parentSuccessors.remove(index);
                    parentSuccessors.addAll(index, Graphs.successorListOf(SPQTree, vertex));
                    Graphs.addOutgoingEdges(SPQTree, parentNode, parentSuccessors);

                    SPQTree.removeVertex(vertex);


                    ArrayDeque<SPQNode> childrenList = new ArrayDeque<>(Graphs.successorListOf(SPQTree, parentNode));


                    while (childrenList.size() > 1) {

                        SPQNode pop = childrenList.pop();
                        SPQNode peek = childrenList.peek();

                        if (peek.getNodeType() == NodeTypesEnum.NODETYPE.Q && pop.getNodeType() == NodeTypesEnum.NODETYPE.Q) {
                            GraphHelper.mergeQVertices(SPQTree, pop, peek);
                            childrenList.pop(); // remove the merged node
                            childrenList.push(pop);

                        }

                    }



                    // if S node only has one Child remove it and connect child to parent
                    if (SPQTree.containsVertex(vertex) && SPQTree.outgoingEdgesOf(vertex).size() == 1) {
                        GraphHelper.mergeVerticeWithParent(SPQTree, vertex,  Graphs.successorListOf(SPQTree, vertex).get(0));

                    }


                } else if (vertex.getNodeType() == parentNode.getNodeType() && (vertex.getNodeType() == NodeTypesEnum.NODETYPE.P)) {

                    if (vertex.getSinkVertex() == parentNode.getSinkVertex() && vertex.getStartVertex() == parentNode.getStartVertex()) {


                        List<DefaultEdge> childrenSuccesors = new ArrayList<>();


               /*         childrenSuccesors.addAll(SPQTree.outgoingEdgesOf(SPQTree.getEdgeTarget(edge)));
                        for (DefaultEdge outEdge : childrenSuccesors) {
                            SPQNode edgeTarget = SPQTree.getEdgeTarget(outEdge);
                            SPQTree.addEdge(parentNode, edgeTarget);
                        }*/

                        List<DefaultEdge> outEdges = new ArrayList<>(SPQTree.outgoingEdgesOf(parentNode));
                        SPQTree.removeAllEdges(outEdges);
                        int index = parentSuccessors.indexOf(vertex);
                        parentSuccessors.remove(index);
                        parentSuccessors.addAll(index, Graphs.successorListOf(SPQTree, vertex));
                        Graphs.addOutgoingEdges(SPQTree, parentNode, parentSuccessors);

                        SPQTree.removeVertex(SPQTree.getEdgeTarget(edge));


                    } else {

                    }
                }
            }

        }


        SPQQNode sourceSink = new SPQQNode(rootNode.startVertex, rootNode.sinkVertex, false);
        SPQQNode node = new SPQQNode(rootNode.startVertex, rootNode.sinkVertex, true);
        SPQTree.addVertex(node);
        SPQTree.addVertex(sourceSink);
        SPQTree.addEdge(node, sourceSink);

        SPQNode pRoot = new SPQNode("Proot");
        SPQTree.addVertex(pRoot);
        SPQTree.addEdge(pRoot, rootNode);
        SPQTree.addEdge(pRoot, node);

        GraphHelper.printTODOTSPQNode(SPQTree);
        GraphHelper.writeTODOTSPQNode(SPQTree, "C:/b.txt");




        SPQImporter spqImporter2 = new SPQImporter("C:/testGraph.txt");
        spqImporter2.run();







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

class SPQEdge extends DefaultEdge {

    SPQNode node;

    public SPQEdge(SPQNode node) {
        this.node = node;
    }

    public SPQNode getNode() {
        return node;
    }

    @Override
    public String toString() {
        return "(" + getSource() + " : " + getTarget() + " : )";
    }

}