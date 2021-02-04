import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Node {


    public Node(String name) {
        this.name = name;


    }

    public String name;

    public void setName(String name) {
        this.name = name;
    }

    public void setAdjList(List<Node> adjList) {
        this.adjList = adjList;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public String getName() {
        return name;
    }

    public List<Node> getAdjList() {
        return adjList;
    }

    public boolean isVisited() {
        return visited;
    }

    List<Node> adjList = new ArrayList<>();
    boolean visited;

    public void addVertex(Node node) {
        adjList.add(node);
    }

    List<Node> backEdgeList = new ArrayList<>();

    public List<Node> getBackEdgeList() {
        return backEdgeList;
    }

    public void setBackEdgeList(List<Node> backEdgeList) {
        this.backEdgeList = backEdgeList;
    }

    public Graph<Node, DefaultEdge> makeDFSTree(Node root, Graph<Node, DefaultEdge> dfsTree) {


        root.setVisited(true);
        dfsTree.addVertex(root);

        for (Node node : root.adjList
        ) {
            if (!node.isVisited()) {
                dfsTree.addVertex(node);
                dfsTree.addEdge(root, node);
                System.out.println("Added " + root.getName() + " und " + node.getName() + " als Kante.");
                //node.setVisited(true);
                makeDFSTree(node, dfsTree);
            }

        }

        return dfsTree;
    }


}
