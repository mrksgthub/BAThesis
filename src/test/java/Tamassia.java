import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.PlanarityTestingAlgorithm;
import org.jgrapht.alg.planar.BoyerMyrvoldPlanarityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.junit.jupiter.api.Test;


public class Tamassia {


    @Test
    public void generateFlowNetwork() {


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


}
