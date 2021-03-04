import org.antlr.v4.runtime.misc.Pair;
import org.jbpt.graph.MultiGraph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.flow.EdmondsKarpMFImpl;
import org.jgrapht.alg.flow.mincost.MinimumCostFlowProblem;
import org.jgrapht.alg.interfaces.MaximumFlowAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.*;

import org.jgrapht.traverse.DepthFirstIterator;
import org.junit.jupiter.api.Test;

import java.util.*;


public class SPQGenTest {

    @Test
    public void graphGen() {

        SPQGen2 spqGen2 = new SPQGen2(1000);
        spqGen2.generate();


        DefaultDirectedGraph<SPQNode, DefaultEdge> graph = GraphHelper.treeToDOT(spqGen2.root, 1);
        GraphHelper.printToDOT(graph);


        System.out.println("Test");

    }


    @Test
    public void teilerGraphgen() {


        SPQNode root = new SPQNode();
        SPQTree tree = new SPQTree(root);

        Boolean check = false;

        int counter = 0;
        while (!check) {
            counter++;
            check = true;

            GraphgenSplitGraph graphgenSplitGraph = new GraphgenSplitGraph(100, 20);
            graphgenSplitGraph.generateGraph();


            root = graphgenSplitGraph.getRoot();
            root.compactTree();
         //   DefaultDirectedGraph<SPQNode, DefaultEdge> graph2 = GraphHelper.treeToDOT(root, 2);
     //       GraphHelper.printTODOTSPQNode(graph2);
            tree = new SPQTree(root);
            tree.fillNodeToEdgesTable(tree.getRoot());
            tree.determineSandPnodes(tree.getRoot(), tree.getVisited());

            // normale repräsentation
            root.compactTree2();

            root.computeNodesInComponent();

      //    graph2 = GraphHelper.treeToDOT(root, 2);
      //      GraphHelper.printTODOTSPQNode(graph2);
     //       GraphHelper.printToDOTTreeVertex(tree.constructedGraph);
//TODO falls Degree = 4, dann Problem?
          check =  root.computeRepresentability(tree.constructedGraph, check);
        }


        tree.computeNofRoot();










        root.getMergedChildren().get(0).computeSpirality();


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


        DepthFirstIterator<TreeVertex, DefaultEdge> depthFirstIterator = new DepthFirstIterator<>(tree.constructedGraph);
        while (depthFirstIterator.hasNext()) {
            depthFirstIterator.next();

        }

        FaceGenerator<TreeVertex, DefaultEdge> treeVertexFaceGenerator = new FaceGenerator<TreeVertex, DefaultEdge>(tree.constructedGraph, root.getStartVertex(), root.getSinkVertex(), embedding);
        treeVertexFaceGenerator.generateFaces2(); // counterclockwise = inner, clockwise = outerFacette
        HashMap<Pair<TreeVertex, TreeVertex>, Integer> pairIntegerMap = new HashMap<>();

        for (Pair<TreeVertex, TreeVertex> pair :
                treeVertexFaceGenerator.adjFaces2.keySet()) {
            pairIntegerMap.put(pair, 0);
        }






        winkelHinzufügen(root, pairIntegerMap);


        List<PlanarGraphFace<TreeVertex, DefaultEdge>> test = new ArrayList<>();
        for (PlanarGraphFace<TreeVertex, DefaultEdge> face: treeVertexFaceGenerator.planarGraphFaces
             ) {
           int edgeCount = 0;
            for (Pair<TreeVertex, TreeVertex> pair:
           face.getOrthogonalRep().keySet() ){
                face.getOrthogonalRep().put(pair, pairIntegerMap.get(pair));
                edgeCount += pairIntegerMap.get(pair);
            }

            if (Math.abs(edgeCount) != 4) {
            //    assert(Math.abs(edgeCount) == 4);
                test.add(face);
                assert (Math.abs(edgeCount) != 4);
            }






        }

        DefaultDirectedWeightedGraph<TreeVertex, DefaultWeightedEdge> treeVertexDefaultEdgeDefaultDirectedWeightedGraph = treeVertexFaceGenerator.generateFlowNetworkLayout2();
        treeVertexFaceGenerator.generateCapacities();


        GraphHelper.printToDOTTreeVertexWeighted(treeVertexDefaultEdgeDefaultDirectedWeightedGraph);




  //     MaximumFlowAlgorithm<TreeVertex, DefaultWeightedEdge> test33 = new EdmondsKarpMFImpl<>(treeVertexDefaultEdgeDefaultDirectedWeightedGraph);


    //    test33.getMaximumFlow(treeVertexFaceGenerator.source, treeVertexFaceGenerator.sink);
   //    test33.getFlowMap();


    //    MinimumCostFlowProblem<TreeVertex, DefaultWeightedEdge> minimumCostFlowProblem = new MinimumCostFlowProblem.MinimumCostFlowProblemImpl<TreeVertex, DefaultWeightedEdge>(treeVertexDefaultEdgeDefaultDirectedWeightedGraph);



        System.out.println("Test");











    }


    @Test
    public void teilergraphMassTest() {

        for (int i = 0; i < 1000; i++) {
            teilerGraphgen();

        }

    }


    @Test
    public void didimoTest() {

        int counter = 0;
        int vertexcounter = 0;
        SPQPNode root = new SPQPNode("Proot");
        TreeVertex source = new TreeVertex("source");
        TreeVertex sink = new TreeVertex("sink");

        ArrayList<TreeVertex> vertexList = new ArrayList<>();
        vertexList = generateVertices(34);


        SPQSNode sN = addSNode(root, counter++);

        int[] array = {1, 33};
        counter = addQStar(root, counter++, array, vertexList);
        int[] array2 = {1, 2, 3};
        counter = addQStar(sN, counter++, array2, vertexList);
        SPQPNode pV1 = addPNode(sN, counter++);

        SPQSNode sV2 = addSNode(pV1, counter++);

        int[] array3 = {3, 4};
        counter = addQStar(sV2, counter++, array3, vertexList);
        SPQPNode pV4 = addPNode(sV2, counter++);
        int[] array4 = {13, 14, 27, 28, 29, 33};
        counter = addQStar(sV2, counter++, array4, vertexList);

        SPQSNode sV8 = addSNode(pV4, counter++);
        SPQSNode sV9 = addSNode(pV4, counter++);
        int[] array5 = {4, 5, 6};
        counter = addQStar(sV8, counter++, array5, vertexList);
        SPQPNode pV10 = addPNode(sV8, counter++);

        int[] array6 = {6, 8, 13};
        counter = addQStar(pV10, counter++, array6, vertexList);
        int[] array7 = {6, 7, 13};
        counter = addQStar(pV10, counter++, array7, vertexList);


        SPQPNode pV11 = addPNode(sV9, counter++);
        int[] array8 = {11, 12, 13};
        counter = addQStar(sV9, counter++, array8, vertexList);

        int[] array9 = {4, 9, 11};
        counter = addQStar(pV11, counter++, array9, vertexList);
        int[] array10 = {4, 10, 11};
        counter = addQStar(pV11, counter++, array10, vertexList);


        SPQSNode sV3 = addSNode(pV1, counter++);
        int[] array11 = {3, 15};
        counter = addQStar(sV3, counter++, array11, vertexList);
        SPQPNode pV5 = addPNode(sV3, counter++);

        int[] array12 = {15, 16, 17, 22};
        counter = addQStar(pV5, counter++, array12, vertexList);
        int[] array13 = {15, 18, 22};
        counter = addQStar(pV5, counter++, array13, vertexList);
        int[] array14 = {15, 19, 20, 21, 22};
        counter = addQStar(pV5, counter++, array14, vertexList);


        int[] array15 = {22, 23};
        counter = addQStar(sV3, counter++, array15, vertexList);
        SPQPNode pV6 = addPNode(sV3, counter++);
        int[] array16 = {23, 24, 30};
        counter = addQStar(pV6, counter++, array16, vertexList);
        int[] array17 = {23, 25, 26, 30};
        counter = addQStar(pV6, counter++, array17, vertexList);


        SPQPNode pV7 = addPNode(sV3, counter++);
        int[] array18 = {30, 31, 33};
        counter = addQStar(pV7, counter++, array18, vertexList);
        int[] array19 = {30, 32, 33};
        counter = addQStar(pV7, counter++, array19, vertexList);


        SPQTree tree = new SPQTree(root);
        root.compactTree();
        tree.determineSandPnodes(tree.getRoot(), tree.getVisited());

        root.compactTree2();


        root.computeNodesInComponent();
        boolean check = true;
        root.computeRepresentability(tree.constructedGraph, check);


        DefaultDirectedGraph<SPQNode, DefaultEdge> graph2 = GraphHelper.treeToDOT(root, 2);
        GraphHelper.printTODOTSPQNode(graph2);
        GraphHelper.printToDOTTreeVertex(tree.constructedGraph);


        HashMap<SPQNode, ArrayList<Double>> list = new HashMap<>();
        getIntervals(root, list);

        HashMap<TreeVertex, HashSet<TreeVertex>> adjMap = new HashMap<>();
        getAdjecentsMap(root, adjMap, tree.constructedGraph);


        tree.computeNofRoot();
        root.getMergedChildren().get(0).setSpirality(3);
        root.getMergedChildren().get(0).computeSpirality();

        // TODO visited map ist outdated
       // checkSpiralitiesWithinBounds(tree);


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


        DepthFirstIterator<TreeVertex, DefaultEdge> depthFirstIterator = new DepthFirstIterator<>(tree.constructedGraph);
        while (depthFirstIterator.hasNext()) {
            depthFirstIterator.next();

        }

        FaceGenerator<TreeVertex, DefaultEdge> treeVertexFaceGenerator = new FaceGenerator<TreeVertex, DefaultEdge>(tree.constructedGraph, root.getStartVertex(), root.getSinkVertex(), embedding);
        treeVertexFaceGenerator.generateFaces2(); // counterclockwise = inner, clockwise = outerFacette
        HashMap<Pair<TreeVertex, TreeVertex>, Integer> pairIntegerMap = new HashMap<>();
        for (Pair<TreeVertex, TreeVertex> pair :
                treeVertexFaceGenerator.adjFaces2.keySet()) {
            pairIntegerMap.put(pair, 0);
        }

        winkelHinzufügen(root, pairIntegerMap);


        List<PlanarGraphFace<TreeVertex, DefaultEdge>> test = new ArrayList<>();
        for (PlanarGraphFace<TreeVertex, DefaultEdge> face : treeVertexFaceGenerator.planarGraphFaces
        ) {
            int edgeCount = 0;
            for (Pair<TreeVertex, TreeVertex> pair :
                    face.getOrthogonalRep().keySet()) {
                face.getOrthogonalRep().put(pair, pairIntegerMap.get(pair));
                edgeCount += pairIntegerMap.get(pair);
            }

            if (Math.abs(edgeCount) != 4) {
                //    assert(Math.abs(edgeCount) == 4);
                test.add(face);
            }


        }




        System.out.println("Test");
    }

    private void checkSpiralitiesWithinBounds(SPQTree tree) {
        for (SPQNode node :
                tree.visited) {
            if (node.mergedChildren.size() > 0 && !node.getName().equals("Proot")) {
                assert (node.getRepIntervalLowerBound() <= node.getSpirality() && node.getSpirality() <= node.getRepIntervalUpperBound());
            }
        }
    }


    public void winkelHinzufügen(SPQNode root, HashMap<Pair<TreeVertex, TreeVertex>, Integer> hashmap) {

        for (SPQNode node :
                root.getMergedChildren()) {
            winkelHinzufügen(node, hashmap);
        }
        if (root.getMergedChildren().size() > 1 && !root.isIsroot()) {
            root.computeOrthogonalRepresentation(hashmap);
        }

    }


    public SPQPNode addPNode(SPQNode parentNode, int counter) {
        SPQPNode node = new SPQPNode("P" + counter);
        node.setParent(parentNode);
        parentNode.getChildren().add(node);
        return node;
    }

    public SPQSNode addSNode(SPQNode parentNode, int counter) {
        SPQSNode node = new SPQSNode("S" + counter);
        node.setParent(parentNode);
        parentNode.getChildren().add(node);
        return node;
    }


    public SPQQNode addQNode(SPQNode parentNode, int counter, TreeVertex start, TreeVertex sink) {
        SPQQNode node = new SPQQNode("Q" + counter++);
        node.setParent(parentNode);
        parentNode.getChildren().add(node);
        node.setStartVertex(start);
        node.setSinkVertex(sink);
        return node;
    }

    public ArrayList<TreeVertex> generateVertices(int counter) {
        ArrayList<TreeVertex> list = new ArrayList<>();

        for (int i = 0; i < counter; i++) {
            TreeVertex vertex = new TreeVertex("v" + i);
            list.add(vertex);
        }

        return list;
    }


    public int addQStar(SPQNode parentNode, int counter, int[] nodes, ArrayList<TreeVertex> vertexList) {
        SPQSNode node = new SPQSNode("S" + counter);

        for (int i = 0; i < nodes.length - 1; i++) {
            addQNode(node, counter++, vertexList.get(nodes[i]), vertexList.get(nodes[i + 1]));
        }
        node.setParent(parentNode);
        parentNode.getChildren().add(node);
        return counter;
    }


    public HashMap<SPQNode, ArrayList<Double>> getIntervals(SPQNode root, HashMap<SPQNode, ArrayList<Double>> intervalsMap) {

        for (SPQNode node :
                root.getMergedChildren()) {
            getIntervals(node, intervalsMap);
        }
        if (root.getMergedChildren().size() > 1 && root.getNodeType() != NodeTypesEnum.NODETYPE.Q) {
            ArrayList<Double> array = new ArrayList<>();
            array.add(root.getRepIntervalLowerBound());
            array.add(root.getRepIntervalUpperBound());
            intervalsMap.put(root, array);
        }

        return intervalsMap;
    }


    /**
     * Ist counterclockwise
     *
     * @param root
     * @param adjecentsMap
     * @param constructedGraph
     * @return
     */
    public HashMap<TreeVertex, HashSet<TreeVertex>> getAdjecentsMap(SPQNode root, HashMap<TreeVertex, HashSet<TreeVertex>> adjecentsMap, DirectedMultigraph<TreeVertex, DefaultEdge> constructedGraph) {

        for (SPQNode node :
                root.getMergedChildren()) {
            getAdjecentsMap(node, adjecentsMap, constructedGraph);
        }
        if (root.getMergedChildren().size() > 0) {


            Set<TreeVertex> adjSetStart = Graphs.neighborSetOf(constructedGraph, root.getStartVertex());
            Set<TreeVertex> adjSetSink = Graphs.neighborSetOf(constructedGraph, root.getSinkVertex());
            HashSet<TreeVertex> tempSetStart = new LinkedHashSet<>(adjSetStart);
            tempSetStart.retainAll(root.getNodesInCompnent());
            HashSet<TreeVertex> tempSetSink = new LinkedHashSet<>(adjSetSink);
            tempSetSink.retainAll(root.getNodesInCompnent());

            HashSet<TreeVertex> outDegreeAdjSink = new LinkedHashSet<>();
            HashSet<TreeVertex> outDegreeAdjStart = new LinkedHashSet<>();

            for (TreeVertex vertex :
                    adjSetSink) {
                if (!tempSetSink.contains(vertex)) {
                    outDegreeAdjSink.add(vertex);
                }
            }
            for (TreeVertex vertex :
                    adjSetStart) {
                if (!tempSetStart.contains(vertex)) {
                    outDegreeAdjStart.add(vertex);
                }
            }


            root.setOutDegreeSinkVertex(outDegreeAdjSink.size());
            root.setOutDegreeSinkVertexSet(outDegreeAdjSink);

            root.setInDegreeSinkVertex(tempSetSink.size());
            root.setInDegreeSinkVertexSet(tempSetSink);

            root.setOutDegreeStartVertex(outDegreeAdjStart.size());
            root.setOutDegreeStartVertexSet(outDegreeAdjStart);

            root.setInDegreeStartVertex(tempSetStart.size());
            root.setInDegreeStarVertexSet(tempSetStart);


            adjecentsMap.put(root.getStartVertex(), (LinkedHashSet<TreeVertex>) adjSetSink);
            adjecentsMap.put(root.getSinkVertex(), (LinkedHashSet<TreeVertex>) adjSetSink);
        }

        return adjecentsMap;
    }


}