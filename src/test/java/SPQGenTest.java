import org.jbpt.algo.tree.tctree.TCTree;
import org.jbpt.graph.MultiGraph;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.PlanarityTestingAlgorithm;
import org.jgrapht.alg.planar.BoyerMyrvoldPlanarityInspector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.traverse.DepthFirstIterator;
import org.junit.jupiter.api.Test;




public class SPQGenTest {

@Test
    public void graphGen(){

    SPQGen2 spqGen2 = new SPQGen2(9);
    spqGen2.generate();


    DefaultDirectedGraph<SPQNode, DefaultEdge> graph = GraphHelper.treeToDOT(spqGen2.root);
    GraphHelper.printToDOT(graph);


    System.out.println("Test");

}




    @Test
    public void teilerGraphgen() {
        GraphgenSplitGraph graphgenSplitGraph = new GraphgenSplitGraph(30);
        graphgenSplitGraph.generateGraph();



        GraphHelper.printToDOTTreeVertex(graphgenSplitGraph.getMultigraph());

        DefaultDirectedGraph<SPQNode, DefaultEdge> graph = GraphHelper.treeToDOT(graphgenSplitGraph.root);
        GraphHelper.printTODOTSPQNode(graph);


        org.jbpt.graph.MultiGraph jbtGraph = new MultiGraph();

        org.jbpt.graph.Graph SPQRtest = new org.jbpt.graph.Graph();
        org.jbpt.graph.MultiGraph spqrtest2 = new MultiGraph();
        for (DefaultEdge edge : graphgenSplitGraph.getMultigraph().edgeSet()
        ) {
            graphgenSplitGraph.getMultigraph().getEdgeSource(edge);
            SPQRtest.addEdge(graphgenSplitGraph.getMultigraph().getEdgeSource(edge), graphgenSplitGraph.getMultigraph().getEdgeTarget(edge));

        }


        TCTree tcTree = new TCTree(SPQRtest);
        TCTree tcTree2 = new TCTree(spqrtest2);

        System.out.println(SPQRtest.toDOT());
        System.out.println(tcTree.toDOT());


        System.out.println(tcTree.getGraph().toDOT());



    }
}