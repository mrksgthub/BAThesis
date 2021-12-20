package Visualizer;

import Datastructures.Vertex;
import org.antlr.v4.runtime.misc.Pair;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;


/**
 * Ist für das Zeichnen des Graphen zuständig. Nutzt dazu die GraphStream Bibliothek.
 *
 *
 *
 */
class GraphStreamOutput {


    private final Hashtable<Vertex, ArrayList<Vertex>> vertexToAdjListMap;
    private final Map<Vertex, Pair<Integer, Integer>> edgeToCoordMap;

    public GraphStreamOutput(Hashtable<Vertex, ArrayList<Vertex>> vertexToAdjListMap, Map<Vertex, Pair<Integer, Integer>> edgeToCoordMap) {

        this.vertexToAdjListMap = vertexToAdjListMap;
        this.edgeToCoordMap = edgeToCoordMap;
    }

    /**
     * Erstellt zum einen das Graph-Objekt von GraphStream, welches dann tatsächlich mit den angegebenen Koordinaten
     * gezeichnet wird.
     *
     */
    void run() {
        // Graphstream
        Graph graph = new SingleGraph("Graph");
        for (Vertex vertex : edgeToCoordMap.keySet()) {

            if (!vertex.isDummy()) {
                graph.addNode(vertex.getName());
                Node node = graph.getNode(vertex.getName());
                Pair<Integer, Integer> coords = edgeToCoordMap.get(vertex);
                node.setAttribute("xy", coords.a, coords.b);
            }
        }

        for (Vertex vertex : edgeToCoordMap.keySet()) {

            if (!vertex.isDummy()) {
                ArrayList<Vertex> list = vertex.getAdjacentVertices();

                for (Vertex vertex1 : list) {

                    if (graph.getEdge(vertex1.getName() + " " + vertex.getName()) == null)
                        graph.addEdge(vertex.getName() + " " + vertex1.getName(), vertex.getName(), vertex1.getName());

                }
            }


        }

        for (Node node : graph) {
            node.setAttribute("ui.label", node.getId());
        }
        String styleSheet =
                "node { text-alignment: at-right; text-color: #222; } node#B { text-alignment: at-left; } node#C { text-alignment: under; }";

        graph.setAttribute("ui.stylesheet", styleSheet);
        // https://stackoverflow.com/questions/37530756/dont-close-swing-main-app-when-closing-graphstream
        Viewer viewer = graph.display(false);
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);
    }


}







