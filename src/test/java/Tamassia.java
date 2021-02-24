import org.jbpt.algo.tree.tctree.TCTree;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.PlanarityTestingAlgorithm;
import org.jgrapht.alg.planar.BoyerMyrvoldPlanarityInspector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.traverse.DepthFirstIterator;
import org.junit.jupiter.api.Test;


public class Tamassia {


    @Test
    public void generateFlowNetwork() {


        GraphAusKetten2 test = new GraphAusKetten2(10);
        Graph<TreeVertex, DefaultEdge> testGraph = test.generateSPgraph();

        GraphHelper.printToDOTTreeVertex(testGraph);

        DefaultDirectedGraph<TreeVertex, DefaultEdge> testDirected = new DefaultDirectedGraph<>(DefaultEdge.class);
        Graphs.addAllVertices(testDirected, testGraph.vertexSet());
        Graphs.addAllEdges(testDirected, testGraph, testGraph.edgeSet());




        org.jbpt.graph.Graph SPQRtest = new org.jbpt.graph.Graph();

        for (DefaultEdge edge : testGraph.edgeSet()
        ) {
            testGraph.getEdgeSource(edge);
            SPQRtest.addEdge(testGraph.getEdgeSource(edge), testGraph.getEdgeTarget(edge));

        }

        TreeVertex startvertex = (TreeVertex) ((KettenComponent<?, ?>) test.compList.get(0)).getStart();
        TCTree tcTree = new TCTree(SPQRtest, SPQRtest.getEdge((TreeVertex) ((KettenComponent<?, ?>) test.compList.get(0)).getStart(), SPQRtest.getAdjacent(startvertex).stream().findFirst().get()));



        System.out.println(SPQRtest.toDOT());
        System.out.println(tcTree.toDOT());


        BoyerMyrvoldPlanarityInspector<TreeVertex, DefaultEdge> myrvoldPlanarityInspector = new BoyerMyrvoldPlanarityInspector<>(testGraph);
        PlanarityTestingAlgorithm.Embedding<TreeVertex, DefaultEdge> embedding = myrvoldPlanarityInspector.getEmbedding();

        DepthFirstIterator<TreeVertex, DefaultEdge> depthFirstIterator = new DepthFirstIterator<>(testGraph);
        while (depthFirstIterator.hasNext()) {
            depthFirstIterator.next();

        }


        //FaceGenerator<TreeVertex, DefaultEdge> treeVertexFaceGenerator = new FaceGenerator<>(embedding, startvertex);
     //   treeVertexFaceGenerator.generateFaces();


    }

    @Test
    public void generateFaces() {



        Graph<TreeVertex, DefaultEdge> graph = GraphHelper.getTreeVertexDefaultEdgeDefaultUndirectedGraph();
        TreeVertex startNode = graph.addVertex();
        TreeVertex node1 = graph.addVertex();

        graph.addEdge(startNode, node1);

        TreeVertex node2 = graph.addVertex();
        graph.addEdge(node1, node2);
        TreeVertex node3 = graph.addVertex();


        graph.addEdge(node2, node3);

        TreeVertex node4 = graph.addVertex();
        graph.addEdge(node3, node4);
        graph.addEdge(node4, startNode);

        GraphHelper.printToDOTTreeVertex(graph);

        BoyerMyrvoldPlanarityInspector<TreeVertex, DefaultEdge> myrvoldPlanarityInspector = new BoyerMyrvoldPlanarityInspector<>(graph);
        PlanarityTestingAlgorithm.Embedding<TreeVertex, DefaultEdge> embedding = myrvoldPlanarityInspector.getEmbedding();


        //FaceGenerator<TreeVertex, DefaultEdge> treeVertexFaceGenerator = new FaceGenerator<>(embedding);
   //     treeVertexFaceGenerator.generateFaces();














    }


    @Test
    public void dagTest() {
        GraphAusKetten2 test = new GraphAusKetten2(9);
        Graph<TreeVertex, DefaultEdge> testGraph = test.generateSPgraph();

      //  GraphHelper.printToDOTTreeVertex(testGraph);
        DirectedAcyclicGraph<TreeVertex, DefaultEdge> asdf = new DirectedAcyclicGraph<TreeVertex, DefaultEdge>(DefaultEdge.class);
        Graphs.addAllVertices(asdf, testGraph.vertexSet());
        Graphs.addAllEdges(asdf, testGraph, testGraph.edgeSet());


        DAGgenerator<TreeVertex, DefaultEdge> daGgenerator = new DAGgenerator<TreeVertex, DefaultEdge>(testGraph, (TreeVertex) ((KettenComponent<?, ?>) test.compList.get(0)).getStart(), (TreeVertex) ((KettenComponent<?, ?>) test.compList.get(0)).getEnd());
       GraphHelper.printToDOTTreeVertex(daGgenerator.getDirectedGraph());
        System.out.println("Test");

    }













}
