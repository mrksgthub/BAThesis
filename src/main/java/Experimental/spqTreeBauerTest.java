package Experimental;

import Datastructures.*;
import Datastructures.Vertex;
import Helperclasses.GraphHelper;
import Helperclasses.SPQImporter;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.traverse.DepthFirstIterator;

import java.util.*;

class spqTreeBauerTest {


    public static void main(String[] args) {

        SPQStarTree tree;
        SPQNode root;

        SPQImporter spqImporter = new SPQImporter();
        spqImporter.runFromFile("C:/a.txt");


        tree = spqImporter.getTree();
        root = tree.getRoot();

        DefaultDirectedGraph<SPQNode, DefaultEdge> graph2 = GraphHelper.treeToDOT(root, 2);
        GraphHelper.printTODOTSPQNode(graph2);


        DirectedMultigraph<Vertex, DefaultEdge> graph = new DirectedMultigraph<>(DefaultEdge.class);
        DirectedMultigraph<Vertex, SPQEdge> graphSPQNodeEdges = new DirectedMultigraph<>(SPQEdge.class);
        DirectedMultigraph<SPQNode, DefaultEdge> SPQTree = new DirectedMultigraph<>(DefaultEdge.class);


        Graphs.addGraph(graph, tree.getConstructedGraph());

        graph.removeEdge(root.getStartVertex(), root.getSinkVertex());

        for (Vertex vertex : graph.vertexSet()) {
            graphSPQNodeEdges.addVertex(vertex);
        }
        for (DefaultEdge edge : graph.edgeSet()) {
            SPQQNode subNode = new SPQQNode(graph.getEdgeSource(edge), graph.getEdgeTarget(edge), true);
            SPQQNode node = new SPQQNode(graph.getEdgeSource(edge), graph.getEdgeTarget(edge), true);
            graphSPQNodeEdges.addEdge(graph.getEdgeSource(edge), graph.getEdgeTarget(edge), new SPQEdge(node));
            SPQTree.addVertex(node);
            SPQTree.addVertex(subNode);
            SPQTree.addEdge(node, subNode);
        }


        Vertex prev = root.getStartVertex();
        ArrayDeque<Vertex> stack = new ArrayDeque<>();
        //  stack.add(prev);

        List<ArrayDeque<Vertex>> qNodes = new ArrayList<>();
        SPQDepthiterator<Vertex, SPQEdge> depthFirstIterator = new SPQDepthiterator(graphSPQNodeEdges, root.getStartVertex(), stack, prev);

        while (depthFirstIterator.hasNext()) {


            Vertex vertex = depthFirstIterator.next();
            stack.add(vertex);

            if (graphSPQNodeEdges.degreeOf(vertex) != 2) {
                qNodes.add(stack);
                stack = new ArrayDeque<>();
                stack.add(vertex);
            }


            prev = vertex;

        }


        ConnectivityInspector<Vertex, SPQEdge> connectivityInspector = new ConnectivityInspector<>(graphSPQNodeEdges);
        ArrayDeque<Vertex> arrayDeque = new ArrayDeque<>(graph.vertexSet());
        connectivityInspector.connectedSets();

        arrayDeque.remove(root.getStartVertex());
        arrayDeque.remove(root.getSinkVertex());


        SPQNode rootNode = null;

        // Valdes Tarjan 82 DAG -> SPQ-Baum
        while (!arrayDeque.isEmpty()) {

            Vertex vertex = arrayDeque.pop();


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
                Vertex edgeSource = graphSPQNodeEdges.getEdgeSource(incomingEdge);
                SPQEdge outgoingEdge = outgoingEdges.iterator().next();
                Vertex edgeTarget = graphSPQNodeEdges.getEdgeTarget(outgoingEdge);


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


        // Kontraktion des SPQ-Baums in einen SPQ*-Baum
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

                if (vertex.getNodeType() == parentNode.getNodeType() && (vertex.getNodeType() == SPQNode.NodeTypesEnum.NODETYPE.S)) {
                    List<DefaultEdge> childrenSuccesors = new ArrayList<>();


          /*          childrenSuccesors.addAll(Datatypes.SPQTree.outgoingEdgesOf(Datatypes.SPQTree.getEdgeTarget(edge)));
                    for (DefaultEdge outEdge : childrenSuccesors) {
                        Datatypes.SPQNode edgeTarget = Datatypes.SPQTree.getEdgeTarget(outEdge);
                        Datatypes.SPQTree.addEdge(parentNode, edgeTarget);
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

                        if (peek.getNodeType() == SPQNode.NodeTypesEnum.NODETYPE.Q && pop.getNodeType() == SPQNode.NodeTypesEnum.NODETYPE.Q) {
                            GraphHelper.mergeQVertices(SPQTree, pop, peek);
                            childrenList.pop(); // remove the merged node
                            childrenList.push(pop);

                        }

                    }



                    // if S node only has one Child remove it and connect child to parent
                    if (SPQTree.containsVertex(vertex) && SPQTree.outgoingEdgesOf(vertex).size() == 1) {
                        GraphHelper.mergeVerticeWithParent(SPQTree, vertex,  Graphs.successorListOf(SPQTree, vertex).get(0));

                    }


                } else if (vertex.getNodeType() == parentNode.getNodeType() && (vertex.getNodeType() == SPQNode.NodeTypesEnum.NODETYPE.P)) {

                    if (vertex.getSinkVertex() == parentNode.getSinkVertex() && vertex.getStartVertex() == parentNode.getStartVertex()) {


                        List<DefaultEdge> childrenSuccesors = new ArrayList<>();


               /*         childrenSuccesors.addAll(Datatypes.SPQTree.outgoingEdgesOf(Datatypes.SPQTree.getEdgeTarget(edge)));
                        for (DefaultEdge outEdge : childrenSuccesors) {
                            Datatypes.SPQNode edgeTarget = Datatypes.SPQTree.getEdgeTarget(outEdge);
                            Datatypes.SPQTree.addEdge(parentNode, edgeTarget);
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


        SPQQNode sourceSink = new SPQQNode(rootNode.getStartVertex(), rootNode.getSinkVertex(), false);
        SPQQNode node = new SPQQNode(rootNode.getStartVertex(), rootNode.getSinkVertex(), true);
        SPQTree.addVertex(node);
        SPQTree.addVertex(sourceSink);
        SPQTree.addEdge(node, sourceSink);

        SPQNode pRoot = new SPQPNode("Proot");
        SPQTree.addVertex(pRoot);
        SPQTree.addEdge(pRoot, rootNode);
        SPQTree.addEdge(pRoot, node);

        GraphHelper.printTODOTSPQNode(SPQTree);
        GraphHelper.writeTODOTSPQNode(SPQTree, "C:/b.txt");




        SPQImporter spqImporter2 = new SPQImporter();
        spqImporter2.runFromFile("C:/testGraph.txt");







    }

    public void checkIncomingEdge(DefaultEdge vertexA, DefaultWeightedEdge vertexB) {


        // VTL82 3.3b)

        // VTL82. 3.3c

    }


}

class SPQDepthiterator<V, E> extends DepthFirstIterator<V, E> {


    private V backVertex;
    private E backEdge;
    private V previous;
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