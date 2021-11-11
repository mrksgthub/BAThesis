package Visualizing;

import Datatypes.Vertex;
import Helperclasses.GraphHelper;
import org.antlr.v4.runtime.misc.Pair;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class GraphFrame {


    private final Map<Vertex, Pair<Integer, Integer>> edgeToCoordMap;
    private final Hashtable<Vertex, ArrayList<Vertex>> adjecentVerticsMap;

    public GraphFrame(Map<Vertex, Pair<Integer, Integer>> edgeToCoordMap, Hashtable<Vertex, ArrayList<Vertex>> adjecentVerticesMap) {

        this.edgeToCoordMap = edgeToCoordMap;
        this.adjecentVerticsMap = adjecentVerticesMap;
    }

    public static void main(String[] args) {
        System.setProperty("org.graphstream.ui", "swing");

        Graph graph = new SingleGraph("Tutorial 1");

        /*
        graph.addNode("A");
        Node node = graph.getNode("A");
        node.setAttribute("xy", 0, 0);
        graph.addNode("B");
        node = graph.getNode("B");
        node.setAttribute("xy", 0, 10);

        graph.addNode("C");
        node = graph.getNode("C");
        node.setAttribute("xy", 10, 10);

        graph.addNode("D");
        node = graph.getNode("D");
        node.setAttribute("xy", 5, 5);

        graph.addEdge("AB", "A", "B");
        graph.addEdge("BC", "B", "C");
        graph.addEdge("CA", "C", "A");
*/
        Map<Vertex, Pair<Integer, Integer>> coord = (Map<Vertex, Pair<Integer, Integer>>) GraphHelper.readObjectFromFile("C:\\hashMap.ser");
        Hashtable<Vertex, ArrayList<Vertex>> embed = (Hashtable<Vertex, ArrayList<Vertex>>) GraphHelper.readObjectFromFile("C:\\adjecency.ser");


        for (Vertex vertex : coord.keySet()) {

            assert embed != null;
            if (!vertex.isDummy()) {
                graph.addNode(vertex.getName());
                Node node = graph.getNode(vertex.getName());
                Pair<Integer, Integer> coords = coord.get(vertex);
                node.setAttribute("xy", coords.a, coords.b);
            }

        }
        for (Vertex treeVertex : embed.keySet()) {

            ArrayList<Vertex> list = embed.get(treeVertex);

            for (Vertex vertex1 : list) {


                if (graph.getEdge(vertex1.getName() + " " + treeVertex.getName()) == null)
                    graph.addEdge(treeVertex.getName() + " " + vertex1.getName(), treeVertex.getName(), vertex1.getName());

            }


        }


        graph.display(false);


    }


    public void displayGraph() {
        System.setProperty("org.graphstream.ui", "swing");


        Graph graph = new SingleGraph("Tutorial 1");


        for (Vertex vertex : edgeToCoordMap.keySet()) {

            graph.addNode(vertex.getName());
            Node node = graph.getNode(vertex.getName());
            Pair<Integer, Integer> coords = edgeToCoordMap.get(vertex);
            node.setAttribute("xy", coords.a, coords.b);
        }


        graph.display(false);


    }


}