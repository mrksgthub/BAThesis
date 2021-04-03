import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.tree.Tree;
import org.apache.commons.lang3.tuple.MutablePair;
import org.jbpt.algo.tree.tctree.TCTree;
import org.jbpt.graph.MultiGraph;
import org.jbpt.hypergraph.abs.Vertex;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.generate.PruferTreeGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.graph.Multigraph;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;

import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Supplier;

import org.jgrapht.util.SupplierUtil;
import org.junit.jupiter.api.Assertions;


public class Test {


    @org.junit.jupiter.api.Test
    public void mutableTest() {

        MutablePair<Integer, Integer> test1 = new MutablePair<>(31, 2);
        MutablePair<Integer, Integer> test2 = new MutablePair<>(2, 31);
        MutablePair<Integer, Integer> test2One = new MutablePair<>(2, 31);

        int one = test1.hashCode();
        int two = test2.hashCode();
        test2One.setRight(44);


        int[] testArr1 = {2, 1};
        int[] testArr2 = {1, 2};
        int[] testArr3 = {1, 2};

        int one1 =  testArr1.hashCode();
        int two1 = testArr2.hashCode();
        int two2 = testArr3.hashCode();


      Pair<Integer, Integer> test12 = new Pair<>(1, 2);
        Pair<Integer, Integer> test22 = new Pair<>(2, 1);
        Pair<Integer, Integer> test32 = new Pair<>(2, 1);

        int one2 = test12.hashCode();
        int two3 = test22.hashCode();
        int two4 = test32.hashCode();


        AbstractMap.SimpleEntry<Integer, Integer> pair1 = new AbstractMap.SimpleEntry<>(1, 2);
        AbstractMap.SimpleEntry<Integer, Integer> pair2 = new AbstractMap.SimpleEntry<>(2, 1);
        AbstractMap.SimpleEntry<Integer, Integer> pair3 = new AbstractMap.SimpleEntry<>(2, 1);

        int pairV1 = pair1.hashCode();
        int pairV2 = pair2.hashCode();
        int pairV3 = pair2.hashCode();

        pair3.setValue(2);
        int pairV4 = pair3.hashCode();



        MutablePair<Integer, Integer> test6 = new Tuple<>(31, 2);
        MutablePair<Integer, Integer> test7 = new Tuple<>(2, 31);

        int oneR = test6.hashCode();
        int twoR = test7.hashCode();


















    }











    @org.junit.jupiter.api.Test
    public void testrun() {


        Graph<Node, DefaultEdge> graph = new DefaultUndirectedGraph(DefaultEdge.class);
        Node node1 = new Node("Bob");
        graph.addVertex(node1);

        Node node2 = new Node("Alice");
        graph.addVertex(node2);
        Node node3 = new Node("Jimmy");
        graph.addVertex(node3);
        graph.addEdge(node1, node3);
        graph.addEdge(node1, node3);
        graph.addEdge(node2, node3);

        Iterator<Node> iterator = new DepthFirstIterator<>(graph, node1);
        while (iterator.hasNext()) {
            Node node = iterator.next();
            System.out.println(node.name);
        }
        System.out.println("test");
    }

    @org.junit.jupiter.api.Test
    public void dfsTest() {
        Node node1 = new Node("1");
        Node node2 = new Node("2");
        Node node3 = new Node("3");
        Node node4 = new Node("4");
        node1.getAdjList().add(node2);
        node1.getAdjList().add(node3);
        node2.getAdjList().add(node1);
        node2.getAdjList().add(node4);
        node3.getAdjList().add(node1);
        node3.getAdjList().add(node4);
        node4.getAdjList().add(node2);
        node4.getAdjList().add(node3);
        DefaultUndirectedGraph dfsTree = new DefaultUndirectedGraph(DefaultEdge.class);
        Graph test = node1.makeDFSTree(node1, dfsTree);


        DOTExporter<Node, DefaultEdge> exporter =
                new DOTExporter<>(v -> v.getName().replace('.', '_'));
        exporter.setVertexAttributeProvider((v) -> {
            Map<String, Attribute> map = new LinkedHashMap<>();
            map.put("label", DefaultAttribute.createAttribute(v.getName()));
            return map;
        });
        Writer writer = new StringWriter();
        exporter.exportGraph(test, writer);
        System.out.println(writer.toString());

    }

    @org.junit.jupiter.api.Test
    public void jbptTest() {
        org.jbpt.graph.Graph test = new org.jbpt.graph.Graph();
        Vertex vertex1 = new Vertex("1");
        Vertex vertex2 = new Vertex("2");
        Vertex vertex3 = new Vertex("3");
        Vertex vertex4 = new Vertex("4");
        test.addEdge(vertex1, vertex2);
        test.addEdge(vertex2, vertex3);
        test.addEdge(vertex3, vertex4);
        test.addEdge(vertex4, vertex1);
        test.addEdge(vertex3, vertex1);

        System.out.println(test.toDOT());

        org.jbpt.algo.tree.tctree.TCTree spqr = new TCTree(test);
        System.out.println(spqr.toString());

        System.out.println("Test");
        System.out.println(spqr.getTCTreeNodes());
        System.out.println(spqr.toDOT());
        System.out.println(spqr.getGraph().toDOT());

    }


    @org.junit.jupiter.api.Test
    public void graphGenTest() {
        Graphgen graphgen = new Graphgen(10);
        org.jbpt.graph.Graph graph = graphgen.generateGraph();

        System.out.println(graph.toDOT());

    }


    @org.junit.jupiter.api.Test
    public void sequenceTest() {
        Graphgen graphgen = new Graphgen(10);
        org.jbpt.graph.Graph graph = new org.jbpt.graph.Graph();
        graphgen.generateSequence(3, 3, graph);
        System.out.println(graph.toDOT());

    }


    @org.junit.jupiter.api.Test
    public void treeTest() {
        Graphgen graphgen = new Graphgen(10);
        int size = 10;
        org.jbpt.graph.Graph graph = graphgen.generateRandomSPQTree(size);
        System.out.println(graph.toDOT());

        ReverseBFSTree reverseBFSTree = new ReverseBFSTree(graph, graphgen, size);

        reverseBFSTree.generateEdges();
        // reverseBFSTree.printGivenLevel(graphgen.getRoot(), 4);


        for (TreeVertex leaf : graphgen.leafNodes
        ) {
            Assertions.assertFalse(leaf.getFirstVertexInEdge().equals(leaf.getSecondVertexInEdge()));


        }


        // TestGraph
        Multigraph<TreeVertex, DefaultEdge> test = new Multigraph<>(DefaultEdge.class);


        for (TreeVertex vertice : reverseBFSTree.graphVertices
        ) {
            test.addVertex(vertice);
        }


        for (TreeVertex leaf : graphgen.leafNodes
        ) {
            test.addEdge(leaf.getFirstVertexInEdge(), leaf.getSecondVertexInEdge());
        }


        // Erstellt einen sauberen Graphen, ohne unverbundene Kanten
        ConnectivityInspector<TreeVertex, DefaultEdge> treeVertexDefaultEdgeConnectivityInspector = new ConnectivityInspector<TreeVertex, DefaultEdge>(test);
        Set<TreeVertex> connectedSet = treeVertexDefaultEdgeConnectivityInspector.connectedSetOf(reverseBFSTree.graphVertices.get(0));


        Multigraph<TreeVertex, DefaultEdge> test2 = new Multigraph<>(DefaultEdge.class);


        for (TreeVertex nodes : connectedSet
        ) {
            test2.addVertex(nodes);
        }

        for (TreeVertex leaf : graphgen.leafNodes
        ) {
            test2.addEdge(leaf.getFirstVertexInEdge(), leaf.getSecondVertexInEdge());
        }


        //Create the exporter (without ID provider)
        DOTExporter<TreeVertex, DefaultEdge> exporter = new DOTExporter<>();
        Writer writer = new StringWriter();
        exporter.exportGraph(test, writer);
        System.out.println(writer.toString());

//Create the exporter (with ID provider)
        DOTExporter<TreeVertex, DefaultEdge> exporter2 = new DOTExporter<>(v -> v.toString());
        writer = new StringWriter();
        exporter2.exportGraph(test2, writer);
        System.out.println(writer.toString());


        // SPQR Test
        org.jbpt.graph.Graph SPQRtest = new org.jbpt.graph.Graph();
        org.jbpt.graph.MultiGraph spqrtest2 = new MultiGraph();

        for (TreeVertex leaf : graphgen.leafNodes
        ) {
            SPQRtest.addEdge(leaf.getFirstVertexInEdge(), leaf.getSecondVertexInEdge());
            spqrtest2.addEdge(leaf.getFirstVertexInEdge(), leaf.getSecondVertexInEdge());
        }


        TCTree tcTree = new TCTree(SPQRtest);
        TCTree tcTree2 = new TCTree(spqrtest2);

        System.out.println(SPQRtest.toDOT());
        System.out.println(tcTree.toDOT());


        System.out.println(spqrtest2.toDOT());
        System.out.println(tcTree2.toDOT());
        System.out.println(

                tcTree2.getGraph().toDOT()

        );


        System.out.println("Test");


    }









    @org.junit.jupiter.api.Test
    public void kettenTest() {


        GraphAusKetten graphAusKetten = new GraphAusKetten(20);
        List<TreeVertex> testList = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            testList.add(new TreeVertex(Integer.toString(i)));
        }


        // graphAusKetten.splitList(testList, 5);
        graphAusKetten.randomChainsBottomUp(9000);


        stGraph stGraph1 = new stGraph(DefaultEdge.class);
        stGraph stGraph2 = new stGraph(DefaultEdge.class);


        graphAusKetten.mergeChains(stGraph1, stGraph2);


    }


    @org.junit.jupiter.api.Test
    public void randomTest() {

        Integer[] ints = {1, 2, 3};

        Integer[] test = pickSample(ints,2, new Random() );

        ArrayList<String> stringList = new ArrayList<>();
        stringList.add("a");
        stringList.add("b");
        stringList.add("c");
        List<String> newList = pickSample(stringList, 2, new Random());





    }

    @org.junit.jupiter.api.Test
    public void edgeMapTest() {

        HashMap<Pair<String, String>, Integer> pairIntegerHashMap = new HashMap<>();

        Pair<String, String> stringStringPair = new Pair<>("a", "b");

        pairIntegerHashMap.put(stringStringPair, 1);
        pairIntegerHashMap.get(new Pair<String, String>("a", "b"));





    }


        public static <T> T[] pickSample(T[] population, int nSamplesNeeded, Random r) {
            T[] ret = (T[]) Array.newInstance(population.getClass().getComponentType(),
                    nSamplesNeeded);
            int nPicked = 0, i = 0, nLeft = population.length;
            while (nSamplesNeeded > 0) {
                int rand = r.nextInt(nLeft);
                if (rand < nSamplesNeeded) {
                    ret[nPicked++] = population[i];
                    nSamplesNeeded--;
                }
                nLeft--;
                i++;
            }
            return ret;
        }


    public static <T> List<T> pickSample(ArrayList<T> population, int nSamplesNeeded, Random r) {
        List<T> ret = new ArrayList<T>();
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


    }




