import org.antlr.v4.runtime.misc.Pair;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Hashtable;


public class Test {
    private JButton drawGraphButton;
    private JButton button2;
    private JButton button3;
    private JLabel test;
    private JPanel panel1;
    private static SPQTree tree;
    private static SPQNode root;


    public Test() {

        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SPQGenerator spqGenerator = new SPQGenerator();
                spqGenerator.run(10, 30);
                tree = spqGenerator.getTree();
                root = spqGenerator.getRoot();

            }
        });


        drawGraphButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Hashtable<TreeVertex, ArrayList<TreeVertex>> embedding = new Hashtable<>();
                Embedder embedder = new Embedder(embedding, root);
                embedder.run(root);

                FaceGenerator<TreeVertex, DefaultEdge> treeVertexFaceGenerator = new FaceGenerator<>(tree.constructedGraph, root.getStartVertex(), root.getSinkVertex(), embedding);
                treeVertexFaceGenerator.generateFaces2();


                DidimoRepresentability didimoRepresentability = new DidimoRepresentability(tree, root);
                didimoRepresentability.run();

                root.getMergedChildren().get(0).computeSpirality();

                Angulator angulator = new Angulator(tree, embedding, treeVertexFaceGenerator);
                try {
                    angulator.run();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }


                //  TamassiaRepresentation tamassiaRepresentation = new TamassiaRepresentation(tree, root, treeVertexFaceGenerator);
                //  tamassiaRepresentation.run();
////////////////////////////////////////////

/////////////////////////////////////////////////////////////////////////////////////

// orthogonal rep muss gesetted werden


                Rectangulator<DefaultEdge> rectangulator = new Rectangulator<>(treeVertexFaceGenerator.planarGraphFaces);
                rectangulator.setOriginaledgeToFaceMap(treeVertexFaceGenerator.getAdjFaces2());
                rectangulator.initialize();
                rectangulator.outerFace.setOrientations();


                Orientator<DefaultEdge> orientator = new Orientator(rectangulator.getRectangularFaceMap(), rectangulator.outerFace);
                orientator.run();


                VerticalEdgeFlow verticalFlow = new VerticalEdgeFlow(orientator.originalFaceList, rectangulator.outerFace);
                DirectedWeightedMultigraph<TreeVertex, DefaultWeightedEdge> testgraph = verticalFlow.generateFlowNetworkLayout2();
                // GraphHelper.printToDOTTreeVertexWeighted(testgraph);

                verticalFlow.generateCapacities();


                HorizontalEdgeFlow horizontalFlow = new HorizontalEdgeFlow(orientator.originalFaceList, rectangulator.outerFace);
                DirectedWeightedMultigraph<TreeVertex, DefaultWeightedEdge> testgraphHor = horizontalFlow.generateFlowNetworkLayout2();
                //  GraphHelper.printToDOTTreeVertexWeighted(testgraphHor);

                horizontalFlow.generateCapacities();

                Coordinator coordinator = new Coordinator(rectangulator.outerFace, rectangulator.getRectangularFaceMap(), verticalFlow.edgeToArcMap, horizontalFlow.edgeToArcMap, verticalFlow.getMinimumCostFlow(), horizontalFlow.getMinimumCostFlow());
                coordinator.run();


                Graph graph = new SingleGraph("Tutorial 1");


                for (TreeVertex vertex : coordinator.getEdgeToCoordMap().keySet()) {

                    if (!vertex.dummy) {
                        graph.addNode(vertex.getName());
                        Node node = graph.getNode(vertex.getName());
                        Pair<Integer, Integer> coords = coordinator.getEdgeToCoordMap().get(vertex);
                        node.setAttribute("xy", coords.a, coords.b);
                    }
                }


                for (TreeVertex treeVertex : embedding.keySet()) {

                    ArrayList<TreeVertex> list = embedding.get(treeVertex);

                    for (TreeVertex vertex1 : list) {

                        if (graph.getEdge(vertex1.getName() + " " + treeVertex.getName()) == null)
                            graph.addEdge(treeVertex.getName() + " " + vertex1.getName(), treeVertex.getName(), vertex1.getName());

                    }


                }


                graph.display(false);

            }
        });
    }

    public static void main(String[] args) {


        System.setProperty("org.graphstream.ui", "swing");


        JFrame frame = new JFrame("Test");
        frame.setContentPane(new Test().panel1);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);




    }

}
