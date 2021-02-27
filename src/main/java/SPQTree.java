import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.*;

public class SPQTree {

    SPQNode root;
    Hashtable<SPQNode, List<SPQNode>> nodeTOedgesTable = new Hashtable();
    Set<SPQNode> visited = new LinkedHashSet<SPQNode>();
    DirectedMultigraph<TreeVertex, DefaultEdge> constructedGraph = new DirectedMultigraph<TreeVertex, DefaultEdge>(DefaultEdge.class);
    Hashtable<SPQNode, HashSet<SPQNode>> vertexAdjMap = new Hashtable<>();



    public Set<SPQNode> getVisited() {
        return visited;
    }

    public void setVisited(Set<SPQNode> visited) {
        this.visited = visited;
    }

    public SPQTree(SPQNode root) {
        this.root = root;





    }

    public SPQNode getRoot() {
        return root;
    }

    public void setRoot(SPQNode root) {
        this.root = root;
    }

    public Hashtable<SPQNode, List<SPQNode>> getNodeTOedgesTable() {
        return nodeTOedgesTable;
    }

    public void setNodeTOedgesTable(Hashtable<SPQNode, List<SPQNode>> nodeTOedgesTable) {
        this.nodeTOedgesTable = nodeTOedgesTable;
    }

    public void fillNodeToEdgesTable(SPQNode root) {

        nodeTOedgesTable.putIfAbsent(root, root.mergedChildren);

        for (SPQNode node : root.mergedChildren
        ) {
            fillNodeToEdgesTable(node);
        }
    }

    public void determineSandPnodes(SPQNode root, Set<SPQNode> visited) {
        visited.add(root);


        for (SPQNode node : root.getMergedChildren()
        ) {
            determineSandPnodes(node, visited);
        }
        if (root.getNodeType() != NodeTypesEnum.NODETYPE.Q) {
            root.setStartVertex(root.getMergedChildren().get(0).getStartVertex());
            root.setSinkVertex(root.getMergedChildren().get(root.getMergedChildren().size() - 1).getSinkVertex());



          //  DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(constructedGraph);
        //    GraphPath<TreeVertex, DefaultEdge> test = dijkstraShortestPath.getPath(root.getStartVertex(), root.getSinkVertex());

         //   GraphPath<TreeVertex, DefaultEdge> test2 = dijkstraShortestPath.getPath(root.getStartVertex(), root.getSinkVertex());
        //    root.setaPathFromSourceToSink(test2.getVertexList());
        } else {
            constructedGraph.addVertex(root.getStartVertex());
            constructedGraph.addVertex(root.getSinkVertex());
            constructedGraph.addEdge(root.getStartVertex(), root.getSinkVertex());



        }

    }

    public void determineAdjecents() {

        for (TreeVertex vertex: constructedGraph.vertexSet()
             ) {
            Set<TreeVertex> adjSetStart = Graphs.neighborSetOf(constructedGraph, vertex);

        }





    }






}







