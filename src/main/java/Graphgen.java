import org.antlr.v4.runtime.tree.Tree;
import org.jbpt.graph.Graph;
import org.jbpt.hypergraph.abs.Vertex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Stream;








/*
Ideen:

Wahrscheinlichkeit für p und s Knoten zufällig generieren?





 */

public class Graphgen {

    private int vertices;
    private Vertex[] vertexArr;
    String[] nodeTypes = {"S", "P", "N"};
    ArrayList<TreeVertex> leafNodes = new ArrayList<>();
    ArrayList<TreeVertex> rest = new ArrayList<>();
    TreeVertex root;

    public TreeVertex getRoot() {
        return root;
    }

    public ArrayList<TreeVertex> getLeafNodes() {
        return leafNodes;
    }

    public void setLeafNodes(ArrayList<TreeVertex> leafNodes) {
        this.leafNodes = leafNodes;
    }

    public void setRoot(TreeVertex root) {
        this.root = root;
    }

    public Graphgen(int size) {
        vertices = size;
        vertexArr = new Vertex[vertices];

        for (int i = 0; i < vertices; i++) {
            Vertex lastVertex = new Vertex(Integer.toString(i));
            vertexArr[i] = lastVertex;
        }


    }


    public Graph generateGraph() {
        Graph graph = new Graph();

        for (int i = 0; i < vertices - 1; i++) {
            graph.addEdge(vertexArr[i], vertexArr[i + 1]);
        }

        graph.addEdge(vertexArr[0], vertexArr[vertices - 1]);


        return graph;

    }


    public int generateSequence(int length, int initialVertex, Graph graph) {

        for (int i = 0; i < length - 1; i++) {
            graph.addEdge(vertexArr[i + initialVertex], vertexArr[i + initialVertex + 1]);
        }

        return initialVertex + length - 1;
    }


    public int generateParallel(int length, int initialVertex, Graph graph) {

        return initialVertex + length - 1;
    }


    public Graph generateRandomSPQTree(int size) {
        Graph spqTree = new Graph();
        ArrayList<TreeVertex> nodes = new ArrayList<TreeVertex>();
        for (int i = 0; i < size-2; i++) {
            TreeVertex lastVertex = new TreeVertex(Integer.toString(i));
            nodes.add(lastVertex);
        }


        generateRoot(nodes, spqTree);

        for (int i = 0; i < size-2; i++) {
            addRandomTreeNode(nodes, spqTree);
        }


        // spqTree.getEdges().stream().iterator().next();

        return spqTree;
    }

    private void addRandomTreeNode(ArrayList<TreeVertex> nodes, Graph spqTree) {

        // Vertex parent =  spqTree.getVertices().stream().skip(new Random().nextInt(spqTree.getVertices().size())).findFirst();


        // newChild is not the new child per se, but it determines if the child is either just a new single edge, or if it forms a P or it causes a new series, or parallel structure ot emerge.
        TreeVertex newChild = new TreeVertex(nodeTypes[new Random().nextInt(nodeTypes.length)]);

        //


        if (newChild.getName().equals("N")) {


            TreeVertex parent = rest.get(new Random().nextInt(rest.size()));

            spqTree.addEdge(parent, newChild);
            newChild.setParent(parent);
            leafNodes.add(newChild);

        } else {

            TreeVertex movedNode = leafNodes.get(new Random().nextInt(rest.size()));

            if (newChild.getName().equals("P") || movedNode.getParent().getName().equals("P") || movedNode.getParent().getName().equals("root")) {

                TreeVertex newN = new TreeVertex("N", newChild);
                //Listen auffüllen
                leafNodes.add(newN);
                rest.add(newChild);
                // Neuen Knoten und Kante zwischen P/S - N  hinzufügen
                spqTree.addEdge(newChild, newN);


                // Alte Kante zwischen N und dessen parent entfernen
                spqTree.removeEdge(spqTree.getEdge(movedNode, movedNode.getParent()));

                // den neuen P/S Knoten einhängen und das alte N umhängen
                spqTree.addEdge(newChild, movedNode.getParent());
                spqTree.addEdge(movedNode, newChild);
                newChild.setParent(movedNode.getParent());

                // altes N an neuen Parent umlegen
                movedNode.setParent(newChild);

            } else { // newChild ist S node und parentnode ist entweder root, oder S-Node

                TreeVertex newN = new TreeVertex("N", movedNode.getParent());

                spqTree.addEdge(movedNode.getParent(), newN);
                leafNodes.add(newN);

                //Höhe setzen
                newN.setDepth(newN.parent.getDepth()+1);

            }


        }


    }


    /**
     * Generates the root
     *
     * @param nodes
     * @param spqTree
     */
    private void generateRoot(ArrayList<TreeVertex> nodes, Graph spqTree) {
        TreeVertex p = new TreeVertex("root");
        spqTree.addVertex(p);
        this.root = p;
        TreeVertex n1 = new TreeVertex("N");
        spqTree.addVertex(n1);
        TreeVertex n2 = new TreeVertex("N");
        spqTree.addVertex(n2);
        spqTree.addEdge(p, n1);
        spqTree.addEdge(p, n2);

        n1.setParent(p);
        n2.setParent(p);

        leafNodes.add(n1);
        leafNodes.add(n2);
        rest.add(p);

        n1.setDepth(n1.getDepth() + 1);
        n2.setDepth(n2.getDepth() + 1);

    }

    public int getVertices() {
        return vertices;
    }

    public void setVertices(int vertices) {
        this.vertices = vertices;
    }
}
