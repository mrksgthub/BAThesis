import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.nio.dot.DOTExporter;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.util.SupplierUtil;

import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

public class GraphHelper<V extends TreeVertex, E> {


    public static <V> void printToDOT(Graph<V, DefaultEdge> jgrapthTest) {
        //Create the exporter (without ID provider)
        DOTExporter<V, DefaultEdge> exporter = new DOTExporter<>();
        Writer writer = new StringWriter();
        exporter.exportGraph(jgrapthTest, writer);
        System.out.println(writer.toString());
    }

    public static DefaultUndirectedGraph<TreeVertex, DefaultEdge> getTreeVertexDefaultEdgeDefaultUndirectedGraph() {
        return new DefaultUndirectedGraph<TreeVertex, DefaultEdge>(TreeVertex.getvSupplier, SupplierUtil.createDefaultEdgeSupplier(), false);
    }

    public static void printToDOTTreeVertex(Graph<TreeVertex, DefaultEdge> jgrapthTest) {
        //Create the exporter (without ID provider)


        DOTExporter<TreeVertex, DefaultEdge> exporter = new DOTExporter<>();
        exporter.setVertexIdProvider((TreeVertex e) -> {
            return e.getName();
        });
        Writer writer = new StringWriter();
        exporter.exportGraph(jgrapthTest, writer);
        System.out.println(writer.toString());
    }

    public static void printTODOTSPQNode(Graph<SPQNode, DefaultEdge> jgrapthTest) {
        //Create the exporter (without ID provider)


        DOTExporter<SPQNode, DefaultEdge> exporter = new DOTExporter<>();
        exporter.setVertexIdProvider((SPQNode e) -> {
            return e.getName();
        });
        Writer writer = new StringWriter();
        exporter.exportGraph(jgrapthTest, writer);
        System.out.println(writer.toString());
    }


















    /**
     * Replaces the subtree rooted in root2 in graph g2 with the subtree rooted in root1 in graph g1. Graph g1 is left
     * unchanged.
     *
     * @param g1    first graph
     * @param g2    second graph
     * @param root1 root of subtree in first graph
     * @param root2 root of subtree in second graph
     * @param <V>   vertex type
     * @param <E>   edge type
     *              <p>
     *              https://stackoverflow.com/questions/60606453/jgrapht-replacing-subtree-in-a-directed-acyclic-graph-by-another-subtree
     */
    public static <V, E> void replaceSubtree(Graph<V, E> g1, Graph<V, E> g2, V root1, V root2) {
        //1. Add subtree under root1 to graph g2 as a disconnected component
        BreadthFirstIterator<V, E> bfs = new BreadthFirstIterator<>(g1, root1);
        g2.addVertex(bfs.next());
        while (bfs.hasNext()) {
            V vertex = bfs.next();
            V parent = bfs.getParent(vertex);
            g2.addVertex(vertex);
            g2.addEdge(parent, vertex, bfs.getSpanningTreeEdge(vertex));
        }

        //2. Get the edge (object) between root2 and its parent. A special case occurs if root2 is also the root of g2
        // in which case it does not have a parent.
        E treeEdge = (g2.incomingEdgesOf(root2).isEmpty() ? null : g2.incomingEdgesOf(root2).iterator().next());
        V parent = (treeEdge == null ? null : Graphs.getOppositeVertex(g2, treeEdge, root2));

        //3. Remove subtree rooted in vertex k
        bfs = new BreadthFirstIterator<>(g2, root2);
        while (bfs.hasNext())
            g2.removeVertex(bfs.next());

        //4. Reconnect the two components
        if (parent != null)
            g2.addEdge(parent, root1, treeEdge);
    }

    //TODO nützliche Funktionen Knoten in Kanten einfügen?!, edge contraction?

    /**
     * Replaces testEdge=(v1, v2) with a series of Edges of length i the first vertex in this series is going to be v1, the last is v2.
     *
     * @param graph
     * @param edge
     * @param i
     */
    public static <V, E> void addVerticesToEdge(Graph<V, E> graph, E edge, int i) {

        V edgeSource = graph.getEdgeSource(edge);

        V vertex = edgeSource;

        List<V> vertexList = new LinkedList<>();
        for (int j = 0; j < i - 1; j++) {
            vertex = graph.getVertexSupplier().get();
            vertexList.add(vertex);
            graph.addVertex(vertex);
            graph.addEdge(edgeSource, vertex);
            edgeSource = vertex;

        }

        graph.addEdge(vertex, graph.getEdgeTarget(edge));
        graph.removeEdge(edge);


        System.out.println("test");
    }


    /**
     * Picks nSamplesNeeded Samples out of an ArrayList. (Stackoverflow)
     *
     * @param <T>
     * @param population
     * @param nSamplesNeeded
     * @param r
     * @return
     */
    public static <V, E> List<V> pickSample(ArrayList<V> population, int nSamplesNeeded, Random r) {
        List<V> ret = new ArrayList<V>();
        int nPicked = 0, i = 0, nLeft = population.size();
        while (nSamplesNeeded > 0) {
            int rand = r.nextInt(nLeft);
            if (rand < nSamplesNeeded) {
                ret.add(population.get(i));
                nSamplesNeeded--;
            }
            nLeft--;
            i++;
        }
        return ret;
    }


    public static DefaultDirectedGraph<SPQNode, DefaultEdge> treeToDOT(SPQNode root) {
        DefaultDirectedGraph<SPQNode, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
        HashMap<SPQNode, Boolean> nodeHashMap = new HashMap<SPQNode, Boolean>();


        dfsRun(root, nodeHashMap, graph);

        return graph;









    }


    public static void dfsRun(SPQNode root, HashMap<SPQNode, Boolean> map, DefaultDirectedGraph<SPQNode, DefaultEdge> graph) {

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



    public static int getRandomNumberUsingNextInt(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }


}
