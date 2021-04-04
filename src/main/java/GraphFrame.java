import org.antlr.v4.runtime.misc.Pair;
import org.graphstream.graph.*;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class GraphFrame {


    private final Map<TreeVertex, Pair<Integer, Integer>> edgeToCoordMap;
    private final Hashtable<TreeVertex, ArrayList<TreeVertex>> adjecentVerticsMap;

    public GraphFrame(Map<TreeVertex, Pair<Integer, Integer>> edgeToCoordMap, Hashtable<TreeVertex, ArrayList<TreeVertex>> adjecentVerticesMap) {

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
        Map<TreeVertex, Pair<Integer, Integer>> coord = (Map<TreeVertex, Pair<Integer, Integer>>) GraphHelper.readObjectFromFile("C:\\hashMap.ser");
        Hashtable<TreeVertex, ArrayList<TreeVertex>> embed = (Hashtable<TreeVertex, ArrayList<TreeVertex>>) GraphHelper.readObjectFromFile("C:\\adjecency.ser");




        for (TreeVertex vertex : coord.keySet()) {

            assert embed != null;
            if (!vertex.dummy) {
                graph.addNode(vertex.getName());
                Node node = graph.getNode(vertex.getName());
                Pair<Integer, Integer> coords = coord.get(vertex);
                node.setAttribute("xy", coords.a, coords.b);
            }

        }
        for (TreeVertex treeVertex : embed.keySet()) {

            ArrayList<TreeVertex> list = embed.get(treeVertex);

            for (TreeVertex vertex1 : list) {


                if(graph.getEdge(vertex1.getName()+" "+treeVertex.getName()) == null)
                    graph.addEdge(  treeVertex.getName()+" "+vertex1.getName() ,treeVertex.getName(), vertex1.getName());

            }



        }



        graph.display(false);






    }




    public void displayGraph() {
        System.setProperty("org.graphstream.ui", "swing");


        Graph graph = new SingleGraph("Tutorial 1");




        for (TreeVertex vertex : edgeToCoordMap.keySet()) {

            graph.addNode(vertex.getName());
            Node node = graph.getNode(vertex.getName());
            Pair<Integer, Integer> coords = edgeToCoordMap.get(vertex);
            node.setAttribute("xy", coords.a, coords.b);
        }




      graph.display(false);



    }


}