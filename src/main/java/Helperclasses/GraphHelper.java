package Helperclasses;

import PlanarityAndAngles.FaceGenerator;
import Datastructures.SPQNode;
import Datastructures.Vertex;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.*;
import org.jgrapht.nio.dot.DOTExporter;
import org.jgrapht.util.SupplierUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class GraphHelper<V extends Vertex, E> {


    public static <V> void printToDOT(Graph<V, DefaultEdge> jgrapthTest) {
        //Create the exporter (without ID provider)
        DOTExporter<V, DefaultEdge> exporter = new DOTExporter<>();
        Writer writer = new StringWriter();
        exporter.exportGraph(jgrapthTest, writer);
        System.out.println(writer.toString());
    }

    public static DefaultUndirectedGraph<Vertex, DefaultEdge> getTreeVertexDefaultEdgeDefaultUndirectedGraph() {
        return new DefaultUndirectedGraph<Vertex, DefaultEdge>(Vertex.getvSupplier, SupplierUtil.createDefaultEdgeSupplier(), false);
    }

    public static void printToDOTTreeVertex(Graph<Vertex, DefaultEdge> jgrapthTest) {
        //Create the exporter (without ID provider)


        DOTExporter<Vertex, DefaultEdge> exporter = new DOTExporter<>();
        exporter.setVertexIdProvider((Vertex e) -> {
            return e.getName();
        });
        Writer writer = new StringWriter();
        exporter.exportGraph(jgrapthTest, writer);
        System.out.println(writer.toString());
    }

    public static void printToDOTTreeVertexWeighted(Graph<Vertex, DefaultWeightedEdge> jgrapthTest) {
        //Create the exporter (without ID provider)


        DOTExporter<Vertex, DefaultWeightedEdge> exporter = new DOTExporter<>();
        exporter.setVertexIdProvider((Vertex e) -> {
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



    public static void writeTODOTSPQNode(Graph<SPQNode, DefaultEdge> jgrapthTest, String stringPath) {
        //Create the exporter (without ID provider)


        DOTExporter<SPQNode, DefaultEdge> exporter = new DOTExporter<>();
        exporter.setVertexIdProvider((SPQNode e) -> {
            return e.getName();
        });
        Writer writer = new StringWriter();
        exporter.exportGraph(jgrapthTest, writer);
        Paths.get(stringPath);
        try {
            Files.write(Paths.get(stringPath), writer.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void writeTODOTTreeVertex(Graph<Vertex, DefaultEdge> jgrapthTest, String stringPath) {
        //Create the exporter (without ID provider)

        DOTExporter<Vertex, DefaultEdge> exporter = new DOTExporter<>();
        exporter.setVertexIdProvider((Vertex e) -> {
            return e.getName();
        });
        Writer writer = new StringWriter();
        exporter.exportGraph(jgrapthTest, writer);
        Paths.get(stringPath);
        try {
            Files.write(Paths.get(stringPath), writer.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void writeGraphStreamToFIle(org.graphstream.graph.Graph graph, String directory) throws IOException {
        FileSinkDOTTreeVertex fs = new FileSinkDOTTreeVertex();
        fs.writeAll(graph, directory);
    }


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
     * @param <V>
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


    public static DefaultDirectedGraph<SPQNode, DefaultEdge> treeToDOT(SPQNode root, int integer) {
        DefaultDirectedGraph<SPQNode, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
        HashMap<SPQNode, Boolean> nodeHashMap = new HashMap<SPQNode, Boolean>();

        if (integer == 1) {
            dfsRun(root, nodeHashMap, graph);
        } else {
            dfsRun2(root, nodeHashMap, graph);
        }

        return graph;

    }


    private static void dfsRun(SPQNode root, HashMap<SPQNode, Boolean> map, DefaultDirectedGraph<SPQNode, DefaultEdge> graph) {

        map.computeIfAbsent(root, k -> false);


        if (!map.get(root)) {
            graph.addVertex(root);
            map.put(root, true);

            for (SPQNode node :
                    root.getSpqChildren()) {
                graph.addVertex(node);
                graph.addEdge(root, node);
                dfsRun(node, map, graph);

            }
        }
    }

    private static void dfsRun2(SPQNode root, HashMap<SPQNode, Boolean> map, DefaultDirectedGraph<SPQNode, DefaultEdge> graph) {

        map.computeIfAbsent(root, k -> false);


        if (!map.get(root)) {
            graph.addVertex(root);
            map.put(root, true);

            for (SPQNode node :
                    root.getSpqChildren()) {
                graph.addVertex(node);
                graph.addEdge(root, node);
                dfsRun2(node, map, graph);
            }
        }
    }


    public static void writeFaceGeneatorToFile(FaceGenerator<Vertex, DefaultEdge> faceGeneator, String filepath) throws IOException {


        ObjectOutputStream oos = null;
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(filepath, false);
            oos = new ObjectOutputStream(fout);
            oos.writeObject(faceGeneator);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (oos != null) {
                oos.close();
            }
        }

    }


    public static void writeObjectToFile(Object o, String filepath) {


        ObjectOutputStream oos = null;
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(filepath, false);
            oos = new ObjectOutputStream(fout);
            oos.writeObject(o);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static Object readObjectFromFile(String filepath) {
        try {

            FileInputStream fileIn = new FileInputStream(filepath);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);

            Object obj = objectIn.readObject();

            System.out.println("The Object has been read from the file");
            objectIn.close();
            return obj;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }


    public static FaceGenerator<Vertex, DefaultEdge> ReadFaceGeneratorFromFile(String filepath) {
        //  filepath = "C:\\graph.ser";


        try {

            FileInputStream fileIn = new FileInputStream(filepath);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);

            FaceGenerator<Vertex, DefaultEdge> obj = (FaceGenerator<Vertex, DefaultEdge>) objectIn.readObject();

            System.out.println("The Object has been read from the file");
            objectIn.close();
            return obj;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }


    public static <V,E>void mergeVertices(DirectedMultigraph<V, E> spqTree, V prevChild, V child) {

        List<V> vs = Graphs.successorListOf(spqTree, child);
        Graphs.addOutgoingEdges(spqTree, prevChild, vs);
        spqTree.removeVertex(child);


    }

    public static void mergeQVertices(DirectedMultigraph<SPQNode, DefaultEdge> spqTree, SPQNode prevChild, SPQNode child) {

        List<SPQNode> vs = Graphs.successorListOf(spqTree, child);
        Graphs.addOutgoingEdges(spqTree, prevChild, vs);
        spqTree.removeVertex(child);

    }

    public static void mergeVerticeWithParent(DirectedMultigraph<SPQNode, DefaultEdge> spqTree, SPQNode parent, SPQNode child) {


        List<SPQNode> vs = Graphs.predecessorListOf(spqTree, child);
        spqTree.addEdge(vs.get(0), child);
        spqTree.removeVertex(parent);

    }
}
