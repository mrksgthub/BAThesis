import Datatypes.Vertex;
import Helperclasses.GraphHelper;
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


        Graph<Vertex, DefaultEdge> graph = GraphHelper.getTreeVertexDefaultEdgeDefaultUndirectedGraph();
        Vertex startNode = graph.addVertex();
        Vertex node1 = graph.addVertex();

        graph.addEdge(startNode, node1);

        Vertex node2 = graph.addVertex();
        graph.addEdge(node1, node2);
        Vertex node3 = graph.addVertex();


        graph.addEdge(node2, node3);

        Vertex node4 = graph.addVertex();
        graph.addEdge(node3, node4);
        graph.addEdge(node4, startNode);

        GraphHelper.printToDOTTreeVertex(graph);

        BoyerMyrvoldPlanarityInspector<Vertex, DefaultEdge> myrvoldPlanarityInspector = new BoyerMyrvoldPlanarityInspector<>(graph);
        PlanarityTestingAlgorithm.Embedding<Vertex, DefaultEdge> embedding = myrvoldPlanarityInspector.getEmbedding();


        //Algorithms.FaceGenerator<Datatypes.TreeVertex, DefaultEdge> treeVertexFaceGenerator = new Algorithms.FaceGenerator<>(embedding);
        //     treeVertexFaceGenerator.generateFaces();


    }


}
