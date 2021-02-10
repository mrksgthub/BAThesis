import org.antlr.v4.runtime.tree.Tree;
import org.jbpt.algo.tree.tctree.EdgeList;
import org.jbpt.algo.tree.tctree.TCTree;
import org.jbpt.graph.Edge;
import org.jbpt.graph.MultiGraph;
import org.jbpt.hypergraph.abs.Vertex;
import org.jgrapht.*;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.planar.BoyerMyrvoldPlanarityInspector;
import org.jgrapht.generate.PruferTreeGenerator;
import org.jgrapht.graph.AsGraphUnion;
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
import org.junit.jupiter.api.Test;




public class KettenTest {

    @Test
    public void initialiseChains(){


        GraphAusKetten test = new GraphAusKetten(5);
        Graph<TreeVertex, DefaultEdge> testGraph = test.generateSPgraph();
        test.mergeSnode((KettenComponent) test.compList.get(0),(KettenComponent) test.compList.get(1));
        GraphHelper.printToDOTTreeVertex(testGraph);
        test.mergeSnode((KettenComponent) test.compList.get(0),(KettenComponent) test.compList.get(1));
        GraphHelper.printToDOTTreeVertex(testGraph);


        test.mergeGraphsPnode((KettenComponent) test.compList.get(0), (KettenComponent) test.compList.get(1));

        GraphHelper.printToDOTTreeVertex(testGraph);

        test.mergeGraphsPnode((KettenComponent) test.compList.get(1), (KettenComponent) test.compList.get(0));

        GraphHelper.printToDOTTreeVertex(testGraph);



        System.out.println("Test");
















    }

    @Test
    public void randomChainsTest() {

        GraphAusKetten test = new GraphAusKetten(9000);
        Graph<TreeVertex, DefaultEdge> testGraph = test.generateSPgraph();

        GraphHelper.printToDOTTreeVertex(testGraph);

        org.jbpt.graph.MultiGraph jbtGraph = new MultiGraph();

        org.jbpt.graph.Graph SPQRtest = new org.jbpt.graph.Graph();
        org.jbpt.graph.MultiGraph spqrtest2 = new MultiGraph();
        for (DefaultEdge edge : testGraph.edgeSet()
        ) {
           testGraph.getEdgeSource(edge);
            SPQRtest.addEdge(testGraph.getEdgeSource(edge), testGraph.getEdgeTarget(edge));

        }


        TCTree tcTree = new TCTree(SPQRtest);
        TCTree tcTree2 = new TCTree(spqrtest2);

        System.out.println(SPQRtest.toDOT());
        System.out.println(tcTree.toDOT());


        BoyerMyrvoldPlanarityInspector<TreeVertex, DefaultEdge> myrvoldPlanarityInspector = new BoyerMyrvoldPlanarityInspector<>(testGraph);



    }




}
