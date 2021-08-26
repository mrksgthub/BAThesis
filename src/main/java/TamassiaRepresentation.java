import org.apache.commons.lang3.tuple.MutablePair;
import org.jgrapht.alg.interfaces.MinimumCostFlowAlgorithm;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TamassiaRepresentation {

    private final SPQNode root;
    private final SPQTree tree;
    private final FaceGenerator<TreeVertex, DefaultEdge> treeVertexFaceGenerator;


    public TamassiaRepresentation(SPQTree tree, SPQNode root, FaceGenerator<TreeVertex, DefaultEdge> treeVertexFaceGenerator) {
        this.tree = tree;
        this.root = root;
        this.treeVertexFaceGenerator = treeVertexFaceGenerator;
    }

    public void run() throws Exception {

        boolean tamassiaValid = true;
        MinimumCostFlowAlgorithm.MinimumCostFlow<DefaultWeightedEdge> minimumCostFlow;
        try {
            treeVertexFaceGenerator.generateFlowNetworkLayout2();
            minimumCostFlow = treeVertexFaceGenerator.generateCapacities();

            setOrthogonalRep(minimumCostFlow, treeVertexFaceGenerator.getPlanarGraphFaces());
        } catch (Exception e) {
            tamassiaValid = false;
            System.out.println("----------------------------------------Invalid Graph-----------------------------------------------------------");
            throw new Exception("IllegalGraph");
        }


    }

    private void setOrthogonalRep(MinimumCostFlowAlgorithm.MinimumCostFlow<DefaultWeightedEdge> minimumCostFlow, List<PlanarGraphFace<TreeVertex, DefaultEdge>> planarGraphFaces) {


        // Erstelle Map um die Kante y zu beommen, welche in Facette x auf Knoten z endet.
        HashMap<PlanarGraphFace<TreeVertex, DefaultEdge>, HashMap<TreeVertex, MutablePair<TreeVertex, TreeVertex>>> map = new HashMap<>();

        for (PlanarGraphFace<TreeVertex, DefaultEdge> face : planarGraphFaces
        ) {
            HashMap<TreeVertex, MutablePair<TreeVertex, TreeVertex>> pairVectorMap = new HashMap<>();
            map.put(face, pairVectorMap);

            Map<MutablePair<TreeVertex, TreeVertex>, Integer> s1 = face.getOrthogonalRep();
            for (MutablePair<TreeVertex, TreeVertex> pair :
                    face.getOrthogonalRep().keySet()) {

                pairVectorMap.put(pair.getRight(), pair);

            }
        }


        // Weise der Kante y, aus Facette x. doe Knoten z als Endknoten hat den entsprechenden Wert zu
        for (DefaultWeightedEdge edge : minimumCostFlow.getFlowMap().keySet()
        ) {

            DefaultDirectedWeightedGraph<TreeVertex, DefaultWeightedEdge> graph = treeVertexFaceGenerator.getNetworkGraph();


            HashMap<TreeVertex, MutablePair<TreeVertex, TreeVertex>> m1 = map.get(graph.getEdgeTarget(edge));

            MutablePair<TreeVertex, TreeVertex> pair = m1.get(graph.getEdgeSource(edge));


            PlanarGraphFace<TreeVertex, DefaultEdge> tempFace;
            switch ((int) minimumCostFlow.getFlow(edge)) {


                case 1:
                    tempFace = (PlanarGraphFace<TreeVertex, DefaultEdge>) graph.getEdgeTarget(edge);
                    tempFace.getOrthogonalRep().put(pair, 1);
                    break;

                case 2:
                    tempFace = (PlanarGraphFace<TreeVertex, DefaultEdge>) graph.getEdgeTarget(edge);
                    tempFace.getOrthogonalRep().put(pair, 0);
                    break;

                case 3:
                    tempFace = (PlanarGraphFace<TreeVertex, DefaultEdge>) graph.getEdgeTarget(edge);
                    tempFace.getOrthogonalRep().put(pair, -1);

                    break;
            }

        //    System.out.println("test");
        }


    }


}
