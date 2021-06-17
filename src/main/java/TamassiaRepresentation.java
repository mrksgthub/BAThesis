import org.apache.commons.lang3.tuple.MutablePair;
import org.jgrapht.alg.interfaces.MinimumCostFlowAlgorithm;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;

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

    public void run() {


        boolean tamassiaValid = true;
        MinimumCostFlowAlgorithm.MinimumCostFlow<DefaultWeightedEdge> minimumCostFlow;
        try {
            treeVertexFaceGenerator.generateFlowNetworkLayout2();
            minimumCostFlow = treeVertexFaceGenerator.generateCapacities();

            setOrthogonalRep(minimumCostFlow, treeVertexFaceGenerator.getPlanarGraphFaces());
        } catch (Exception e) {
            tamassiaValid = false;
            System.out.println("----------------------------------------Invalid Graph-----------------------------------------------------------");
        }


    }

    private void setOrthogonalRep(MinimumCostFlowAlgorithm.MinimumCostFlow<DefaultWeightedEdge> minimumCostFlow, List<PlanarGraphFace<TreeVertex, DefaultEdge>> planarGraphFaces) {



        for (PlanarGraphFace<TreeVertex, DefaultEdge> face : planarGraphFaces
        ) {
            Map<MutablePair<TreeVertex, TreeVertex>, Integer> s1 = face.getOrthogonalRep();
            for (MutablePair<TreeVertex, TreeVertex> pair :
                    face.getOrthogonalRep().keySet()) {



            }


        }


    }


}
