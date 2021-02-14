import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DFSTreeGenerator {

    DirectedMultigraph<TreeVertex, DefaultEdge> backedendGraph;
    DFSNode start;
    HashMap<TreeVertex, List<TreeVertex>> adjecencyMap = new HashMap<>();
    HashMap<TreeVertex, List<TreeVertex>> backEdges = new HashMap<TreeVertex, List<TreeVertex>>();
    HashMap<TreeVertex, TreeVertex> parentMap = new HashMap<TreeVertex, TreeVertex>();

    DirectedMultigraph<TreeVertex, DefaultEdge> dfsTree = new DirectedMultigraph<TreeVertex, DefaultEdge>(DefaultEdge.class);
    List<DefaultEdge> backEdgelist = new ArrayList<>();


    public DFSTreeGenerator(DirectedMultigraph<TreeVertex, DefaultEdge> backedendGraph) {
        this.backedendGraph = backedendGraph;

        for (TreeVertex vertex :
                backedendGraph.vertexSet()) {
            adjecencyMap.put(vertex, Graphs.neighborListOf(backedendGraph, vertex));
        }


        for (TreeVertex vertex : backedendGraph.vertexSet()
        ) {
            dfsTree.addVertex(vertex);
            backEdges.put(vertex, new ArrayList<TreeVertex>());
            vertex.setVisited(false);
        }


        generateDFSTreeNode(backedendGraph.vertexSet().iterator().next());


        for (DefaultEdge edge :
                backedendGraph.edgeSet()) {
            if (!dfsTree.containsEdge(backedendGraph.getEdgeSource(edge), backedendGraph.getEdgeTarget(edge))) {
                backEdgelist.add(edge);
            }
        }

    }

    public void generateDFSTreeNode(TreeVertex root) {

        root.setVisited(true);

        for (TreeVertex node :
                adjecencyMap.get(root)) {
            if (node.isVisited()) {
                //backedges geht über ancestors
                backEdges.get(node).add(root);
            } else {

                generateDFSTreeNode(node);
                dfsTree.addEdge(root, node);
            }


        }

    }


}
