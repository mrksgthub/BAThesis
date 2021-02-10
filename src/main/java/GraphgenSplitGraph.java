import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.util.SupplierUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GraphgenSplitGraph {


    SPQNode root;

    DirectedMultigraph<TreeVertex, DefaultEdge> multigraph = new DirectedMultigraph<>(TreeVertex.getvSupplier, SupplierUtil.createDefaultEdgeSupplier(), false);
    int operations;
    List<DefaultEdge> edges = new ArrayList();
    HashMap<DefaultEdge, SPQNode> edgeSPQNodeHashMap = new HashMap();
    int counter = 0;


    public GraphgenSplitGraph(int operations) {
        this.operations = operations;
        root = new SPQPNode("Proot");
        TreeVertex vertex = new TreeVertex("source");
        TreeVertex vertex2 = new TreeVertex("sink");
        multigraph.addVertex(vertex);
        multigraph.addVertex(vertex2);
        multigraph.addEdge(vertex, vertex2);
        multigraph.addEdge(vertex, vertex2);


        root.getChildren().add(new SPQQNode("Q" + ++counter));

        SPQNode q = new SPQQNode("Q" + ++counter);
        root.getChildren().add(q);
        q.setParent(root);

        edges.add(multigraph.getEdge(vertex, vertex2));
        edgeSPQNodeHashMap.put(multigraph.getEdge(vertex, vertex2), q);


    }


    public void generateGraph() {


        DefaultEdge edge = edges.get(GraphHelper.getRandomNumberUsingNextInt(0, edges.size()));
        randomnewSNode(edge);


       // GraphHelper.printToDOT(GraphHelper.treeToDOT(root));

        for (int i = 0; i < operations; i++) {
            edge = edges.get(GraphHelper.getRandomNumberUsingNextInt(0, edges.size()));

            int degreeOfedgeSource = multigraph.outDegreeOf(multigraph.getEdgeSource(edge)) + multigraph.inDegreeOf(multigraph.getEdgeSource(edge));
            int degreeOfedgeSink = multigraph.outDegreeOf(multigraph.getEdgeTarget(edge)) + multigraph.inDegreeOf(multigraph.getEdgeTarget(edge));
            if (GraphHelper.getRandomNumberUsingNextInt(0, 2) < 1 && degreeOfedgeSource < 4 && degreeOfedgeSink < 4) {
                randomnewPNode(edge);
            } else {

                randomnewSNode(edge);
            }

        }
      //  GraphHelper.printToDOTTreeVertex(graphgenSplitGraph.getMultigraph());
      //  GraphHelper.printTODOTSPQNode(GraphHelper.treeToDOT(root));

    }

    private void randomnewSNode(DefaultEdge edge) {

        TreeVertex vertex = multigraph.addVertex();
        DefaultEdge edge1 = multigraph.addEdge(vertex, multigraph.getEdgeTarget(edge));
        DefaultEdge edge2 = multigraph.addEdge(multigraph.getEdgeSource(edge), vertex);
        multigraph.removeEdge(edge);
        edges.remove(edge);
        edges.add(edge1);
        edges.add(edge2);
        SPQNode oldQNode = edgeSPQNodeHashMap.get(edge);
        SPQNode newSnode = new SPQNode("S" + ++counter);
        SPQNode newQnode = new SPQNode("Q" + ++counter);
        edgeSPQNodeHashMap.remove(edge);
        edgeSPQNodeHashMap.put(edge1, oldQNode);
        edgeSPQNodeHashMap.put(edge2, newQnode);
        nodeUmhaengen(oldQNode, newSnode);
        addNodeAsChild(newQnode, newSnode);
    }


    private void randomnewPNode(DefaultEdge edge) {

        DefaultEdge edge1 = multigraph.addEdge(multigraph.getEdgeSource(edge), multigraph.getEdgeTarget(edge));
        edges.add(edge1);

        SPQNode oldQNode = edgeSPQNodeHashMap.get(edge);
        SPQNode newPnode = new SPQNode("P" + ++counter);
        SPQNode newQnode = new SPQNode("Q" + ++counter);
        edgeSPQNodeHashMap.put(edge1, newQnode);
        nodeUmhaengen(oldQNode, newPnode);
        addNodeAsChild(newQnode, newPnode);


    }









    public <T extends SPQNode> void addNodeAsChild(T node, T parent) {
        node.setParent(parent);
        parent.getChildren().add(node);

    }


    public <T extends SPQNode> void nodeUmhaengen(T node, T newnode) {
        //Abhängen
        node.getParent().getChildren().set(node.getParent().getChildren().indexOf(node), newnode);
        //neuer Knoten als Parent festlegen

        newnode.setParent(node.getParent());
        addNodeAsChild(node, newnode);

    }


    public SPQNode getNode() {
        return root;
    }

    public void setNode(SPQNode node) {
        this.root = node;
    }

    public DirectedMultigraph<TreeVertex, DefaultEdge> getMultigraph() {
        return multigraph;
    }

    public void setMultigraph(DirectedMultigraph<TreeVertex, DefaultEdge> multigraph) {
        this.multigraph = multigraph;
    }

    public int getOperations() {
        return operations;
    }

    public void setOperations(int operations) {
        this.operations = operations;
    }
}
