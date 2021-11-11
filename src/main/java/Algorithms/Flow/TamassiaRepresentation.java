package Algorithms.Flow;

import Algorithms.FaceGenerator;
import Datatypes.*;
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
    private final FaceGenerator<Vertex, DefaultEdge> treeVertexFaceGenerator;


    public TamassiaRepresentation(SPQTree tree, SPQNode root, FaceGenerator<Vertex, DefaultEdge> treeVertexFaceGenerator) {
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

    private void setOrthogonalRep(MinimumCostFlowAlgorithm.MinimumCostFlow<DefaultWeightedEdge> minimumCostFlow, List<PlanarGraphFace<Vertex, DefaultEdge>> planarGraphFaces) {


        // Erstelle Map um die Kante y zu beommen, welche in Facette x auf Knoten z endet.
        HashMap<PlanarGraphFace<Vertex, DefaultEdge>, HashMap<Vertex, TupleEdge<Vertex, Vertex>>> map = new HashMap<>();

        for (PlanarGraphFace<Vertex, DefaultEdge> face : planarGraphFaces
        ) {
            HashMap<Vertex, TupleEdge<Vertex, Vertex>> pairVectorMap = new HashMap<>();
            map.put(face, pairVectorMap);

            Map<TupleEdge<Vertex, Vertex>, Integer> s1 = face.getOrthogonalRep();
            for (TupleEdge<Vertex, Vertex> pair :
                    face.getOrthogonalRep().keySet()) {

                pairVectorMap.put(pair.getRight(), pair);

            }
        }


        // Weise der Kante y, aus Facette x. doe Knoten z als Endknoten hat den entsprechenden Wert zu
        for (DefaultWeightedEdge edge : minimumCostFlow.getFlowMap().keySet()
        ) {

            DefaultDirectedWeightedGraph<Vertex, DefaultWeightedEdge> graph = treeVertexFaceGenerator.getNetworkGraph();


            HashMap<Vertex, TupleEdge<Vertex, Vertex>> m1 = map.get(graph.getEdgeTarget(edge));

            TupleEdge<Vertex, Vertex> pair = m1.get(graph.getEdgeSource(edge));


            PlanarGraphFace<Vertex, DefaultEdge> tempFace;
            switch ((int) minimumCostFlow.getFlow(edge)) {


                case 1:
                    tempFace = (PlanarGraphFace<Vertex, DefaultEdge>) graph.getEdgeTarget(edge);
                    tempFace.getOrthogonalRep().put(pair, 1);
                    break;

                case 2:
                    tempFace = (PlanarGraphFace<Vertex, DefaultEdge>) graph.getEdgeTarget(edge);
                    tempFace.getOrthogonalRep().put(pair, 0);
                    break;

                case 3:
                    tempFace = (PlanarGraphFace<Vertex, DefaultEdge>) graph.getEdgeTarget(edge);
                    tempFace.getOrthogonalRep().put(pair, -1);

                    break;
            }

            //    System.out.println("test");
        }


    }


}
