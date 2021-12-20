package Datastructures;

import Helperclasses.DFSIterator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.*;

/**
 *Ist eine Containerklasse für die WUrzel des SPQ*-baums und wird genutzt, um einen SPQ*-Graphen zu initialisieren,
 *oder einen SPQ-Baum in einen SPQ*-Baum zu transformieren.
 *
 *
 */
public class SPQStarTree {

    private SPQNode root;
    private final Set<SPQNode> visited = new LinkedHashSet<>();
    private final DirectedMultigraph<Vertex, DefaultEdge> constructedGraph = new DirectedMultigraph<>(DefaultEdge.class);
    private final Hashtable<Vertex, ArrayList<Vertex>> vertexToAdjecencyListMap = new Hashtable<>();

    public SPQStarTree(SPQNode root) {
        this.root = root;


    }

    public DirectedMultigraph<Vertex, DefaultEdge> getConstructedGraph() {
        return constructedGraph;
    }

    public SPQNode getRoot() {
        return root;
    }

    public void setRoot(SPQNode root) {
        this.root = root;
    }

    public Hashtable<Vertex, ArrayList<Vertex>> getVertexToAdjecencyListMap() {
        return vertexToAdjecencyListMap;
    }


    /**
     * Legt füllt die adjazenten start- und sinkNodes arrays auf und erzeugt auch einen JGraphT gerichteten Graphen, um
     * die planare Einbettung der Graphen, dessen Wurzel in root übrgeben wurde, in einen gerichteten Graphen umwandelt.
     *
     *
     * @param root Wurzel des Baumes
     */
    private void setStartAndSinkNodesAndBuildConstructedGraph(SPQNode root) {

        Deque<SPQNode> stack = DFSIterator.buildPostOrderStack(root);
        while (!stack.isEmpty()) {
            SPQNode node = stack.pop();
            if (node.getNodeType() != SPQNode.NodeTypesEnum.NODETYPE.Q || node.getSpqChildren().size() > 0) {
                node.setSourceVertex(node.getSpqChildren().get(0).getSourceVertex());
                node.setSinkVertex(node.getSpqChildren().get(node.getSpqChildren().size() - 1).getSinkVertex());

            } else {
                constructedGraph.addVertex(node.getSourceVertex());
                constructedGraph.addVertex(node.getSinkVertex());
                constructedGraph.addEdge(node.getSourceVertex(), node.getSinkVertex());
            }
        }

    }


    /**
     * Merged Eltern und Kinder, bei  der Transformation in einen SPQ-Baum -> SPQ*-Baum
     *
     *
     * @param root Wirzeö des Baums.
     */
    private void compactTree(SPQNode root) {

        Deque<SPQNode> stack = DFSIterator.buildPostOrderStack(root);
        while (!stack.isEmpty()) {
            SPQNode node = stack.pop();
            if (node.getParent() != null && node.getNodeType() == node.getParent().getNodeType() && !node.getParent().isRoot()) {
                node.mergeNodeWithParent(node, node.getParent());
            }
        }
    }


    /**
     * Erzeugt die Q*-Knoten, bei der Transformation in SOQ-Baum -> SPQ*-Baum.
     *
     *
     * @param root  Wurzel des Baums
     */
    private void generateQStarNodes(SPQNode root) {

        Deque<SPQNode> stack = DFSIterator.buildPostOrderStack(root);
        while (!stack.isEmpty()) {
            SPQNode node = stack.pop();
            node.generateQstarChildren();
        }

    }

    /**
     * Started und führt die Transformation von SOQ-Baum -> SPQ*-Baum durch.
     *
     *
     * @param root  Wurzel des Baums
     */
    public void addValidSPQStarTreeRepresentation(SPQNode root) {
        compactTree(root);
        generateQStarNodes(root);
    }

    /**
     * Initialisiert die Knoten des SPQ*-Baums, nach dem Laden. Dabei werden nur drei Methonden ausgeführt, die in
     * dieser Klasse implementeiert sind.
     *
     *
     * @param root  Wurzel des Baums
     */
    public void initializeSPQNodes(SPQNode root) {
        setStartAndSinkNodesAndBuildConstructedGraph(root);
        calculateAdjaecencyListsOfSinkAndSource(root);
        determineInnerOuterAdjecentsOfSinkAndSource(root);
    }


    /**
     * Berechnet die korrekten Adjazentenlisten der Knoten des Graphen (nicht des Baumes).
     *
     *
     * @param root Wurzel des Baums
     */
    private void calculateAdjaecencyListsOfSinkAndSource(SPQNode root) {

        Deque<SPQNode> stack = DFSIterator.buildPreOrderStack(root);
        while (!stack.isEmpty()) {
            SPQNode node = stack.pop();
            node.addToAdjacencyListsSinkAndSource();
        }
    }

    /**
     * Legt die inneren und äußeren adjazenten Knoten an den Polen fest. Dabei ist dafür gesorgt, dass sie in der
     * richtigen Reihenfolge einfügt.
     *
     *
     * @param root  Wurzel des Baums
     */
    private void determineInnerOuterAdjecentsOfSinkAndSource(SPQNode root) {

        Deque<SPQNode> stack = DFSIterator.buildPostOrderStack(root);
        while (!stack.isEmpty()) {
            SPQNode node = stack.pop();
            if (node.getSpqChildren().size() > 0) {
                for (SPQNode nodes :
                        node.getSpqChildren()) {
                    node.addToSourceAndSinkLists(nodes); //innere adjazente Knoten
                }
            }
        }
    }


}
























