import org.jbpt.graph.Graph;
import org.jbpt.hypergraph.abs.Vertex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;

public class ReverseBFSTree {

    Graph inputGraph;
    TreeVertex root;
    private int size;
    Hashtable<Vertex, Vertex[]> vertexHashtable = new Hashtable<>();
    ArrayList<TreeVertex> graphVertices = new ArrayList<TreeVertex>();
    private int iterator = 0;

    public ReverseBFSTree(Graph inputGraph, Graphgen graphgen, int size) {
        this.inputGraph = inputGraph;
        this.root = graphgen.getRoot();
        //graphVertices = graphgen.getLeafNodes();
        this.size = size;


        Collection<Vertex> vertices = inputGraph.getVertices();
        Vertex[] vertexArray = vertices.toArray(new Vertex[0]);


        for (Vertex vertex : vertexArray) {
            vertexHashtable.put(vertex, inputGraph.getAdjacent(vertex).toArray(new Vertex[0]));
            TreeVertex tempVertex = (TreeVertex) vertex;
            tempVertex.setChildren(new ArrayList(inputGraph.getAdjacent(vertex)));
            // parent aus der Liste entfernen
            tempVertex.getChildren().remove(tempVertex.getParent());

        }


    }


    void generateEdges(){
        for (int i = 0; i < size; i++) {
            graphVertices.add(new TreeVertex(Integer.toString(i)));
        }
       preOrderDFS(root);


    }


    /*
    Test DFS construction of tree
     */
    void preOrderDFS(TreeVertex node) {
        if (node == null) {
            return;
        }
        // determine the edges of the children of the Graph
        if (node.visited == false) {
            determineStartAndEndnodesOfTheGraph(node);
            node.visited = true;
        }
        //recusive steps
        for (TreeVertex child : node.getChildren()
        ) {
            preOrderDFS(child);
        }


    }



    private void determineStartAndEndnodesOfTheGraph(TreeVertex node) {
        if (node.getName().equals("S")) {
            TreeVertex firstNode = node.getChildren().get(0);
            TreeVertex mitteVertex = node.getChildren().get(0);
            TreeVertex lastVertex = node.getChildren().get(node.getChildren().size() - 1);

            //first node
            firstNode.setFirstVertexInEdge(node.getParent().getFirstVertexInEdge());
            firstNode.setSecondVertexInEdge(graphVertices.get(++iterator));
            // second until second to last node

            for (int i = 1; i < node.getChildren().size() - 1; i++) {
                mitteVertex = node.getChildren().get(i);
                mitteVertex.setFirstVertexInEdge(node.getChildren().get(i - 1).getSecondVertexInEdge());
                mitteVertex.setSecondVertexInEdge(graphVertices.get(++iterator));
            }
            //last node
            lastVertex.setFirstVertexInEdge(mitteVertex.getSecondVertexInEdge());
            lastVertex.setSecondVertexInEdge(node.getParent().getSecondVertexInEdge());
        }

        if (node.getName().equals("P" ) ) {
            for (TreeVertex child : node.getChildren()
            ) {
                child.setFirstVertexInEdge(node.getFirstVertexInEdge());
                child.setSecondVertexInEdge(node.getSecondVertexInEdge());
            }

        }
        if (node.getName().equals("root") && node.getFirstVertexInEdge() == null) {
            node.setFirstVertexInEdge(graphVertices.get(iterator));
            node.setSecondVertexInEdge(graphVertices.get(++iterator));

            for (TreeVertex child : node.getChildren()
            ) {
                child.setFirstVertexInEdge(node.getFirstVertexInEdge());
                child.setSecondVertexInEdge(node.getSecondVertexInEdge());
            }


        } else if ((node.getParent().getName().equals("P") || node.getParent().getName().equals("root")) && node.getFirstVertexInEdge() == null) {
            node.setFirstVertexInEdge(node.getParent().getFirstVertexInEdge());
            node.setSecondVertexInEdge(node.getParent().getSecondVertexInEdge());
        } else {

        }
    }


    /* Print nodes at a given level */
    void printGivenLevel(Vertex node, int level) {
        if (node == null)
            return;
        if (level == 1)
            System.out.print(node.getName() + " ");
        else if (level > 1) {
            for (Vertex vertex : vertexHashtable.get(node)
            ) {
                printGivenLevel(vertex, level - 1);

            }

            vertexHashtable.size();


            // Einfach eine neue NodeMap erstellen indem man eine eigene Map mit get Adject baut?


        }
    }


}
