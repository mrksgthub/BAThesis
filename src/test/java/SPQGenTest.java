import org.jbpt.graph.MultiGraph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import org.junit.jupiter.api.Test;




public class SPQGenTest {

@Test
    public void graphGen(){

    SPQGen2 spqGen2 = new SPQGen2(9);
    spqGen2.generate();


    DefaultDirectedGraph<SPQNode, DefaultEdge> graph = GraphHelper.treeToDOT(spqGen2.root, 1);
    GraphHelper.printToDOT(graph);


    System.out.println("Test");

}




    @Test
    public void teilerGraphgen() {
        GraphgenSplitGraph graphgenSplitGraph = new GraphgenSplitGraph(9);
        graphgenSplitGraph.generateGraph();



        GraphHelper.printToDOTTreeVertex(graphgenSplitGraph.getMultigraph());

        DefaultDirectedGraph<SPQNode, DefaultEdge> graph = GraphHelper.treeToDOT(graphgenSplitGraph.root,1);
        GraphHelper.printTODOTSPQNode(graph);


        org.jbpt.graph.MultiGraph jbtGraph = new MultiGraph();

        org.jbpt.graph.Graph SPQRtest = new org.jbpt.graph.Graph();
        org.jbpt.graph.MultiGraph spqrtest2 = new MultiGraph();
        for (DefaultEdge edge : graphgenSplitGraph.getMultigraph().edgeSet()
        ) {
            graphgenSplitGraph.getMultigraph().getEdgeSource(edge);
            SPQRtest.addEdge(graphgenSplitGraph.getMultigraph().getEdgeSource(edge), graphgenSplitGraph.getMultigraph().getEdgeTarget(edge));

        }


      //  TCTree tcTree = new TCTree(SPQRtest);
       // TCTree tcTree2 = new TCTree(spqrtest2);

   //     System.out.println(SPQRtest.toDOT());
    //    System.out.println(tcTree.toDOT());


        SPQNode root = graphgenSplitGraph.getRoot();
        root.compachtTree();



        DefaultDirectedGraph<SPQNode, DefaultEdge> graph2 = GraphHelper.treeToDOT(root, 2);
       GraphHelper.printTODOTSPQNode(graph2);


        SPQTree tree = new SPQTree(root);
        tree.fillNodeToEdgesTable(tree.getRoot());
        tree.deterimeSandPnodeStartEndVertices(tree.getRoot(), tree.getVisited());






        GraphHelper.printToDOTTreeVertex(tree.constructedGraph);
       // System.out.println(tcTree.getGraph().toDOT());
        DFSTreeGenerator dfsTreeGenerator = new DFSTreeGenerator(tree.constructedGraph);

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(tree.constructedGraph);
       GraphPath<TreeVertex, DefaultEdge> test = dijkstraShortestPath.getPath(root.getStartVertex(), root.getSinkVertex());

        GraphPath<TreeVertex, DefaultEdge> test2 = dijkstraShortestPath.getPath(root.getMergedChildren().get(0).getStartVertex(), root.getSinkVertex());




        // normale repräsentation
        root.compachtTree2();

        root.computeNodesInComponent();

        graph2 = GraphHelper.treeToDOT(root, 2);
        GraphHelper.printTODOTSPQNode(graph2);


        root.computeRepresentability(tree.constructedGraph);








    }

    @Test
    public void didimoTest() {


        SPQNode root = new SPQPNode("Proot");

        TreeVertex source = new TreeVertex("source");
        root.setStartVertex(source);
        TreeVertex sink = new TreeVertex("sink");
        root.setSinkVertex(sink);
        SPQQNode q1 = new SPQQNode("Q1");
        SPQNode q2 = new SPQQNode("Q2");
        q1.setStartVertex(source);
        q1.setSinkVertex(sink);
        q2.setStartVertex(source);
        q2.setSinkVertex(sink);



        root.getChildren().add(q1);
        root.getChildren().add(q2);




        SPQTree tree = new SPQTree(root);
        root.compachtTree();
        root.compachtTree2();
        tree.deterimeSandPnodeStartEndVertices(tree.getRoot(), tree.getVisited());















    }














}