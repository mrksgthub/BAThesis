import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.nio.dot.DOTExporter;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class SPQExporter {

    private final SPQTree tree;

    public SPQExporter(SPQTree tree) {
        this.tree = tree;

    }


    public void printTODOTSPQNode(Graph<SPQNode, DefaultEdge> jgrapthTest) {
        //Create the exporter (without ID provider)


        DOTExporter<SPQNode, DefaultEdge> exporter = new DOTExporter<>();
        exporter.setVertexIdProvider((SPQNode e) -> {
            //     return e.getName();
            return ((e.getMergedChildren().size() > 0) ? e.getName() : e.startVertex.getName() + e.sinkVertex.getName());

        });
        Writer writer = new StringWriter();
        exporter.exportGraph(jgrapthTest, writer);
        //   System.out.println(writer.toString());
    }

    public void writeTODOTSPQNode(Graph<SPQNode, DefaultEdge> jgrapthTest, String fileName) {
        //Create the exporter (without ID provider)


        DOTExporter<SPQNode, DefaultEdge> exporter = new DOTExporter<>();
        exporter.setVertexIdProvider((SPQNode e) -> {
            //     return e.getName();
            return ((e.getMergedChildren().size() > 0) ? e.getName() : e.startVertex.getName() + e.sinkVertex.getName());

        });
        Writer writer = new StringWriter();
        exporter.exportGraph(jgrapthTest, writer);
        // System.out.println(writer.toString());
        Paths.get(fileName);
        try {
            Files.write(Paths.get(fileName), writer.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public DefaultDirectedGraph<SPQNode, DefaultEdge> treeToDOT(SPQNode root, int integer) {
        DefaultDirectedGraph<SPQNode, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
        HashMap<SPQNode, Boolean> nodeHashMap = new HashMap<SPQNode, Boolean>();

        if (integer == 1) {
            dfsRun(root, nodeHashMap, graph);
        } else {
            dfsRun2(root, nodeHashMap, graph);
        }

        return graph;

    }


    public void dfsRun(SPQNode root, HashMap<SPQNode, Boolean> map, DefaultDirectedGraph<SPQNode, DefaultEdge> graph) {

        map.computeIfAbsent(root, k -> false);


        if (!map.get(root)) {
            graph.addVertex(root);
            map.put(root, true);

            for (SPQNode node :
                    root.getChildren()) {
                graph.addVertex(node);
                graph.addEdge(root, node);
                dfsRun(node, map, graph);

            }
        }
    }

    public void dfsRun2(SPQNode root, HashMap<SPQNode, Boolean> map, DefaultDirectedGraph<SPQNode, DefaultEdge> graph) {

        map.computeIfAbsent(root, k -> false);


        if (!map.get(root)) {
            graph.addVertex(root);
            map.put(root, true);

            for (SPQNode node :
                    root.mergedChildren) {
                graph.addVertex(node);
                graph.addEdge(root, node);
                dfsRun2(node, map, graph);
            }
        }
    }


    public void run(SPQNode root) {

        printTODOTSPQNode(treeToDOT(root, 2));

    }


    public void run(SPQNode root, String fileName) {

        printTODOTSPQNode(treeToDOT(root, 2));
        writeTODOTSPQNode(treeToDOT(root, 2), fileName);

    }
}
