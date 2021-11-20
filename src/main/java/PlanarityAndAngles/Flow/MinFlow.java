package PlanarityAndAngles.Flow;

import PlanarityAndAngles.FaceGenerator;
import Datatypes.*;
import org.jgrapht.alg.flow.mincost.CapacityScalingMinimumCostFlow;
import org.jgrapht.alg.flow.mincost.MinimumCostFlowProblem;
import org.jgrapht.alg.interfaces.MinimumCostFlowAlgorithm;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MinFlow {

    private final SPQNode root;
    private final SPQStarTree tree;
    private final FaceGenerator<Vertex, DefaultEdge> treeVertexFaceGenerator;
    private DefaultDirectedWeightedGraph<Vertex, DefaultWeightedEdge> networkGraph;
    private final Map<Vertex, Integer> supplyMap = new HashMap<>();
    private final Map<DefaultWeightedEdge, Integer> lowerMap = new HashMap<>();
    private final Map<DefaultWeightedEdge, Integer> upperMap = new HashMap<>();


    public MinFlow(SPQStarTree tree, SPQNode root, FaceGenerator<Vertex, DefaultEdge> treeVertexFaceGenerator) {
        this.tree = tree;
        this.root = root;
        this.treeVertexFaceGenerator = treeVertexFaceGenerator;
    }

    public void run(List<PlanarGraphFace<Vertex>> planarGraphFaces) throws Exception {

        boolean tamassiaValid = true;
        MinimumCostFlowAlgorithm.MinimumCostFlow<DefaultWeightedEdge> minimumCostFlow;
        try {

           generateFlowNetworkLayout(planarGraphFaces);
            minimumCostFlow = generateCapacities();

            setOrthogonalRep(minimumCostFlow, planarGraphFaces);
        } catch (Exception e) {
            tamassiaValid = false;
            System.out.println("----------------------------------------Invalid Graph-----------------------------------------------------------");
            throw new Exception("IllegalGraph");
        }


    }

    private void setOrthogonalRep(MinimumCostFlowAlgorithm.MinimumCostFlow<DefaultWeightedEdge> minimumCostFlow, List<PlanarGraphFace<Vertex>> planarGraphFaces) {


        // Erstelle Map um die Kante y zu beommen, welche in Facette x auf Knoten z endet.
        HashMap<PlanarGraphFace<Vertex>, HashMap<Vertex, TupleEdge<Vertex, Vertex>>> map = new HashMap<>();

        for (PlanarGraphFace<Vertex> face : planarGraphFaces
        ) {
            HashMap<Vertex, TupleEdge<Vertex, Vertex>> pairVectorMap = new HashMap<>();
            map.put(face, pairVectorMap);

            for (TupleEdge<Vertex, Vertex> pair :
                    face.getOrthogonalRep().keySet()) {

                pairVectorMap.put(pair.getRight(), pair);

            }
        }


        // Weise der Kante y, aus Facette x. doe Knoten z als Endknoten hat den entsprechenden Wert zu
        for (DefaultWeightedEdge edge : minimumCostFlow.getFlowMap().keySet()
        ) {

            DefaultDirectedWeightedGraph<Vertex, DefaultWeightedEdge> graph = networkGraph;


            HashMap<Vertex, TupleEdge<Vertex, Vertex>> m1 = map.get(graph.getEdgeTarget(edge));

            TupleEdge<Vertex, Vertex> pair = m1.get(graph.getEdgeSource(edge));


            PlanarGraphFace<Vertex> tempFace;
            switch ((int) minimumCostFlow.getFlow(edge)) {


                case 1:
                    tempFace = (PlanarGraphFace<Vertex>) graph.getEdgeTarget(edge);
                    tempFace.setEdgeAngle(pair, 1);
                    break;

                case 2:
                    tempFace = (PlanarGraphFace<Vertex>) graph.getEdgeTarget(edge);
                    tempFace.setEdgeAngle(pair, 0);
                    break;

                case 3:
                    tempFace = (PlanarGraphFace<Vertex>) graph.getEdgeTarget(edge);
                    tempFace.setEdgeAngle(pair, -1);

                    break;
            }

            //    System.out.println("test");
        }


    }


    private DefaultDirectedWeightedGraph<Vertex, DefaultWeightedEdge> generateFlowNetworkLayout(List<PlanarGraphFace<Vertex>> planarGraphFaces) {
        networkGraph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);


        Vertex outerFace = planarGraphFaces.get(0);
        List<TupleEdge<Vertex, Vertex>> vertexList = planarGraphFaces.get(0).getEdgeList();
        networkGraph.addVertex(planarGraphFaces.get(0));

        supplyMap.put(outerFace, -1 * (2 * (vertexList.size()) + 4));

        // äußere Facette
        for (int j = 0; j < vertexList.size(); j++) {
            Vertex temp = vertexList.get(j).getLeft();
            networkGraph.addVertex(temp);
            supplyMap.put(temp, 4);
            DefaultWeightedEdge e = networkGraph.addEdge(temp, planarGraphFaces.get(0));
            networkGraph.setEdgeWeight(e, 1);
            upperMap.put(e, 4);
            lowerMap.put(e, 1);

        }

        // restliche Facetten
        for (int i = 1; i < planarGraphFaces.size(); i++) {

            vertexList = planarGraphFaces.get(i).getEdgeList();
            Vertex innerFace = planarGraphFaces.get(i);
            networkGraph.addVertex(planarGraphFaces.get(i));
            supplyMap.put(innerFace, -1 * (2 * (vertexList.size()) - 4));


            for (int j = 0; j < vertexList.size(); j++) {
                Vertex temp = vertexList.get(j).getLeft();
                networkGraph.addVertex(temp);
                supplyMap.put(temp, 4);
                DefaultWeightedEdge e = networkGraph.addEdge(temp, planarGraphFaces.get(i));
                networkGraph.setEdgeWeight(e, 1);
                upperMap.put(e, 4);
                lowerMap.put(e, 1);

            }


        }


        return networkGraph;
    }


    private MinimumCostFlowAlgorithm.MinimumCostFlow<DefaultWeightedEdge> generateCapacities() {


        MinimumCostFlowProblem<Vertex,
                DefaultWeightedEdge> problem = new MinimumCostFlowProblem.MinimumCostFlowProblemImpl<>(
                networkGraph, v -> supplyMap.getOrDefault(v, 0), upperMap::get,
                e -> lowerMap.getOrDefault(e, 1));

        CapacityScalingMinimumCostFlow<Vertex, DefaultWeightedEdge> minimumCostFlowAlgorithm =
                new CapacityScalingMinimumCostFlow<>();


        return minimumCostFlowAlgorithm.getMinimumCostFlow(problem);
    }


}
