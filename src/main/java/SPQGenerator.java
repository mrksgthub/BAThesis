import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.ListIterator;
import java.util.Set;

public class SPQGenerator {

    SPQNode root;
    SPQTree tree;


    public void run(int size, int chanceOfP) {


        Boolean check = false;
        Hashtable<TreeVertex, ArrayList<TreeVertex>> embedding = new Hashtable<>();

        int counter = 0;
        while (!check) {
            counter++;
            check = true;

            GraphgenSplitGraph graphgenSplitGraph = new GraphgenSplitGraph(size, chanceOfP);
            graphgenSplitGraph.generateGraph();


            root = graphgenSplitGraph.getRoot();
            root.compactTree();
            System.out.println("SPQ-Trees");

            tree = new SPQTree(root);


            tree.setStartAndSinkNodesOrBuildConstructedGraph(tree.getRoot(), tree.getVisited());

            // normale repräsentation
            root.generateQstarNodes();

            root.computeAdjecentVertices();


            embedding = erstelleHashtablefuerFacegenerator(tree);


            check = root.computeRepresentability(check);
            if (check) {
                check = (tree.computeNofRoot()) ? check : false;
                if (!check) {
                    System.out.println("Didimo rejected at source Node");
                }
            }

            if (!check) {
                continue;
            }

            boolean tamassiaValid = true;
            try {

                //    Hashtable<TreeVertex, ArrayList<TreeVertex>> embedding = erstelleHashtablefuerFacegenerator(tree);
                FaceGenerator<TreeVertex, DefaultEdge> treeVertexFaceGenerator = new FaceGenerator<TreeVertex, DefaultEdge>(tree.constructedGraph, root.getStartVertex(), root.getSinkVertex(), embedding);
                treeVertexFaceGenerator.generateFaces2(); // counterclockwise = inner, clockwise = outerFacette

                DefaultDirectedWeightedGraph<TreeVertex, DefaultWeightedEdge> treeVertexDefaultEdgeDefaultDirectedWeightedGraph = treeVertexFaceGenerator.generateFlowNetworkLayout2();
                treeVertexFaceGenerator.generateCapacities();

            } catch (Exception e) {
                tamassiaValid = false;
                System.out.println("----------------------------------------Invalid Graph-----------------------------------------------------------");
            }


            assert (tamassiaValid == check);


        }


    }



    public Hashtable<TreeVertex, ArrayList<TreeVertex>> erstelleHashtablefuerFacegenerator(SPQTree tree) {
        Hashtable<TreeVertex, ArrayList<TreeVertex>> embedding = new Hashtable<>();

        for (TreeVertex vertex :
                tree.constructedGraph.vertexSet()) {

            ArrayList<TreeVertex> arrList = new ArrayList<>();

            Set<DefaultEdge> tempIn = tree.constructedGraph.incomingEdgesOf(vertex);
            Set<DefaultEdge> tempOut = tree.constructedGraph.outgoingEdgesOf(vertex);


            ArrayList<DefaultEdge> inEdgesList = new ArrayList<>(tempIn);
            ListIterator<DefaultEdge> tempInListIterator = inEdgesList.listIterator(inEdgesList.size());
            ListIterator<DefaultEdge> tempOutListIterator = new ArrayList<>(tempOut).listIterator();

            while (tempInListIterator.hasPrevious()) {
                arrList.add(tree.constructedGraph.getEdgeSource(tempInListIterator.previous()));
            }
            while (tempOutListIterator.hasNext()) {
                arrList.add(tree.constructedGraph.getEdgeTarget(tempOutListIterator.next()));
            }

            embedding.put(vertex, arrList);
        }
        return embedding;
    }

    public SPQNode getRoot() {
        return root;
    }

    public void setRoot(SPQNode root) {
        this.root = root;
    }

    public SPQTree getTree() {
        return tree;
    }

    public void setTree(SPQTree tree) {
        this.tree = tree;
    }
}

