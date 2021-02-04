import org.antlr.v4.runtime.tree.Tree;
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
import java.util.*;
import java.util.function.Supplier;

import org.jgrapht.util.SupplierUtil;
import org.junit.jupiter.api.Assertions;


public class Test {

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
    public void pruferTest() {


        org.jbpt.graph.Graph graph = new org.jbpt.graph.Graph();
        int size = 20;
        PruferGen pruferGen = new PruferGen(size);


        PruferGen.generateRandomTree(size);










        // Create the VertexFactory so the generator can create vertices
        Supplier<String> vSupplier = new Supplier<String>()
        {
            private int id = 0;

            @Override
            public String get()
            {
                return "v" + id++;
            }
        };


        DefaultUndirectedGraph<String, DefaultEdge> jgrapthTest = new DefaultUndirectedGraph<String, DefaultEdge>(vSupplier, SupplierUtil.createDefaultEdgeSupplier(), false);





        int[] ints = {5,5,2,2,2,3,5};

       PruferGen.generatePruferCode(7, ints);
        Arrays.sort(ints);

        PruferTreeGenerator<String, DefaultEdge> pruferTreeGenerator = new PruferTreeGenerator<String, DefaultEdge>(9000);
        pruferTreeGenerator.generateGraph(jgrapthTest);




        //Create the exporter (without ID provider)
        DOTExporter<String, DefaultEdge> exporter = new DOTExporter<>();
        Writer writer = new StringWriter();
        exporter.exportGraph(jgrapthTest, writer);
        System.out.println(writer.toString());


        System.out.println("Test");



    }


    @org.junit.jupiter.api.Test
    public void pruferGraphgenV2(){

        // Create the VertexFactory so the generator can create vertices
        Supplier<TreeVertex> vSupplier = new Supplier<TreeVertex>()
        {
            private int id = 0;

            @Override
            public TreeVertex get()
            {
                return new TreeVertex("v" + id++);
            }
        };


        SPQTree<TreeVertex, DefaultEdge> jgrapthTest = PruferGen.getTreeVertexDefaultEdgeDefaultUndirectedGraph(vSupplier, 20);

        DefaultUndirectedGraph<String, DefaultEdge> test2 = new DefaultUndirectedGraph<String, DefaultEdge>(SupplierUtil.createStringSupplier(), SupplierUtil.createDefaultEdgeSupplier(), false);
    test2.addVertex();




         jgrapthTest.determineRoot();









        BreadthFirstIterator<TreeVertex, DefaultEdge> treeVertexDefaultEdgeBreadthFirstIterator = new BreadthFirstIterator<>(jgrapthTest, jgrapthTest.getRoot());


        //TODO jaja iteriert so nicht.
        System.out.println( treeVertexDefaultEdgeBreadthFirstIterator.next());


        System.out.println(treeVertexDefaultEdgeBreadthFirstIterator.next().getParent());



        GraphHelper.printToDOT(jgrapthTest);



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


















}
