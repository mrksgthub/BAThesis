import org.antlr.v4.runtime.misc.Pair;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SPQPNode extends SPQNode {

    NodeTypesEnum.NODETYPE nodeType = NodeTypesEnum.NODETYPE.P;


    public SPQPNode(String name) {
        super(name);
    }

    @Override
    public NodeTypesEnum.NODETYPE getNodeType() {
        return nodeType;
    }

    @Override
    public void setNodeType(NodeTypesEnum.NODETYPE nodeType) {
        this.nodeType = nodeType;
    }

    @Override
    public boolean calculateRepresentabilityInterval(DirectedMultigraph<TreeVertex, DefaultEdge> graph, boolean check) {

        // TODO einmal für alle Knoten zu Beginn berechnen, dann in ein Hashtable packen
        Set<TreeVertex> adjSetStart = Graphs.neighborSetOf(graph, getStartVertex());
        Set<TreeVertex> adjSetSink = Graphs.neighborSetOf(graph, getSinkVertex());


        if (mergedChildren.size() == 3) {

            // Lemma 6
            double mL = mergedChildren.get(0).getRepIntervalLowerBound();
            double mC = mergedChildren.get(1).getRepIntervalLowerBound();
            double mR = mergedChildren.get(2).getRepIntervalLowerBound();
            double ML = mergedChildren.get(0).getRepIntervalUpperBound();
            double MC = mergedChildren.get(1).getRepIntervalUpperBound();
            double MR = mergedChildren.get(2).getRepIntervalUpperBound();

            if (mL - 2 <= MR + 2 && mL - 2 <= MC && mR + 2 <= ML - 2 && mR + 2 <= MC && mC <= ML - 2 && mC <= MR + 2) {
                repIntervalLowerBound = Math.max(mL - 2, mC);
                repIntervalLowerBound = Math.max(repIntervalLowerBound, mR + 2);
                repIntervalUpperBound = Math.min(ML - 2, MC);
                repIntervalUpperBound = Math.min(repIntervalUpperBound, MC + 2);


            } else {
                System.out.println("Intervals do not overlap P has 3 Children" + " " + this.getName());
                check=false;
            }


        } else if (mergedChildren.size() == 2) {

            TreeVertex vPole;
            TreeVertex uPole;
            SPQNode leftSNode = mergedChildren.get(0);
            SPQNode rightSNode = mergedChildren.get(1);


            double mL = leftSNode.getRepIntervalLowerBound();
            double mR = rightSNode.getRepIntervalLowerBound();
            double ML = leftSNode.getRepIntervalUpperBound();
            double MR = rightSNode.getRepIntervalUpperBound();


            int inDegreeOfStartVertex = graph.inDegreeOf(startVertex);
            if (inDegreeOfStartVertex == 0) { //if starVertex is the rootvertex of the whole graph
                inDegreeOfStartVertex = 1;
            }


            int outDegreeOfSinkVertex = graph.outDegreeOf(sinkVertex);
            if (outDegreeOfSinkVertex == 0) { //if starVertex is the rootvertex of the whole graph
                outDegreeOfSinkVertex = 1;
            }
            int outdegreeOfv;
            int outdegreeOfu;

//TODO falsch, hier muss mit children gearbeitet werden

            HashSet<TreeVertex> tempSetStart = new HashSet<TreeVertex>(adjSetStart);
            tempSetStart.retainAll(nodesInCompnent);
            HashSet<TreeVertex> tempSetSink = new HashSet<TreeVertex>(adjSetSink);
            tempSetSink.retainAll(nodesInCompnent);

            int inDegreeCounterStart = 0;
            int inDegreeCounterSink = 0;
            int outDegreeCounterStart = 0;
            int outDegreeCounterSink = 0;

            for (TreeVertex node : tempSetStart
            ) {
                if (nodesInCompnent.contains(node)) {
                    inDegreeCounterStart++;
                }
            }
            for (TreeVertex node : tempSetSink
            ) {
                if (nodesInCompnent.contains(node)) {
                    inDegreeCounterSink++;
                }
            }


            int test1 = computeHowManyCommonNodesThisAndSet(tempSetStart);
            int test2 = computeHowManyCommonNodesThisAndSet(tempSetSink);


            outDegreeCounterStart = adjSetStart.size() - inDegreeCounterStart;
            outDegreeCounterSink = adjSetSink.size() - inDegreeCounterSink;


            if (inDegreeCounterStart == 2 && inDegreeCounterSink == 2) { // I_2O_alphaBeta

                // Lemmma 8
                double gamma = outDegreeCounterStart + outDegreeCounterSink - 2;

                double lBound = mL - MR;
                double upBound = ML - mR;


                if (lBound <= 4 - gamma && 2 <= upBound) {

                    repIntervalLowerBound = Math.max(mL - 2, mR) + gamma / 2;
                    repIntervalUpperBound = Math.min(ML, MR + 2) - gamma / 2;


                } else {
                    System.out.println("No rectalinear drawing possible I2Oab" + " " + this.getName());
                    check=false;
                }

            } else if ((inDegreeCounterStart == 3 && inDegreeCounterSink == 3))  // I_3dd
            {
                double pdV = 9999;
                double pdU = 9999;

                List<SPQNode> childrenOfFirstChild = mergedChildren.get(0).getMergedChildren();
                List<SPQNode> childrenOfSecondChild = mergedChildren.get(1).getMergedChildren();

                mL = leftSNode.getRepIntervalLowerBound();
                mR = rightSNode.getRepIntervalLowerBound();
                ML = leftSNode.getRepIntervalUpperBound();
                MR = rightSNode.getRepIntervalUpperBound();

                // Was tun falls erstes oder zweites child eine Q node ist
                if ((childrenOfFirstChild.get(0).getNodeType() == NodeTypesEnum.NODETYPE.P) && (childrenOfFirstChild.get(childrenOfFirstChild.size() - 1).getNodeType() == NodeTypesEnum.NODETYPE.P)) { //I3ll

                    pdU = 0;
                    pdV = 0;
                } else if ((childrenOfSecondChild.get(0).getNodeType() == NodeTypesEnum.NODETYPE.P) && (childrenOfSecondChild.get(childrenOfSecondChild.size() - 1).getNodeType() == NodeTypesEnum.NODETYPE.P)) //I3rr
                { // FIxbar indem man Q2 zu nem Qstar macht

                    pdU = 1;
                    pdV = 1;
                } else if ((childrenOfFirstChild.get(0).getNodeType() == NodeTypesEnum.NODETYPE.P) && (childrenOfFirstChild.get(childrenOfFirstChild.size() - 1).getNodeType() != NodeTypesEnum.NODETYPE.P)) { //I3lr

                    pdU = 0;
                    pdV = 1;
                } else if ((childrenOfSecondChild.get(0).getNodeType() == NodeTypesEnum.NODETYPE.P) && (childrenOfSecondChild.get(childrenOfSecondChild.size() - 1).getNodeType() != NodeTypesEnum.NODETYPE.P)) { //I3rl

                    rightSNode = mergedChildren.get(0);
                    leftSNode = mergedChildren.get(1);
                    pdU = 0;
                    pdV = 1;

                    mL = rightSNode.getRepIntervalLowerBound();
                    mR = leftSNode.getRepIntervalLowerBound();
                    ML = rightSNode.getRepIntervalUpperBound();
                    MR = leftSNode.getRepIntervalUpperBound();
                }
                double lBound = mL - MR;
                double upBound = ML - mR;

                if ((lBound <= 3) && (3 <= upBound)) {

                    repIntervalUpperBound = Math.max(mL - 1, mR + 2) - (pdU + pdV) / 2;
                    repIntervalLowerBound = Math.min(ML - 1, MR + 2) - (pdU + pdV) / 2;

                } else {
                    System.out.println("No rectalinear I3dd'" + " " + this.getName());
                    check=false;
                }


            } else { // I_3dO_alphaBeta

                double pd = 9999;
                double gamma = outDegreeCounterStart + outDegreeCounterSink - 2;

                if (inDegreeCounterStart == 3) {

                    pd = (mergedChildren.get(0).computeHowManyCommonNodesThisAndSet(tempSetStart) == 2) ? 0 : 1;
                } else { // umkehren
                    pd = (mergedChildren.get(0).computeHowManyCommonNodesThisAndSet(tempSetSink) == 2) ? 0 : 1;

                }

                double lBound = mL - MR;
                double upBound = ML - mR;

                if (lBound <= 3.5 - gamma && 2.5 <= upBound) {

                    repIntervalLowerBound = Math.max(mL - 1.5, mR + 1) + (gamma - pd) / 2;
                    repIntervalUpperBound = Math.min(ML - 0.5, MR + 2) - (gamma + pd) / 2;


                } else {
                    System.out.println("No rectalinear drawing possible I3dO" + " " + this.getName());
                    check=false;
                }


            }


        } else {
            System.out.println("Invalid number of Children for P-Node");
            check=false;
        }

        return check;
    }

    @Override
    public void computeOrthogonalRepresentation(HashMap<Pair<TreeVertex, TreeVertex>, Integer> hashMap) {

        // Für innere Facetten nur der Winkel auf der rechten Seite relevant?
        if (startNodes.size() == 3) {
            // mergedChildren 3, oder 2 sind die Fälle die unterschieden werden müssen

            // Beispiel3-4-10  Außen
            TreeVertex nextVertexStarRight = startVertex.adjecentVertices.get(Math.floorMod((startVertex.adjecentVertices.indexOf(mergedChildren.get(mergedChildren.size()-1).startNodes.get(mergedChildren.get(mergedChildren.size()-1).startNodes.size()-1)) + 1), startVertex.adjecentVertices.size()));
            hashMap.put((new Pair<TreeVertex, TreeVertex>(nextVertexStarRight, startVertex)), 1);

            //Beispiel 9-4-10
            TreeVertex nextVertexMiddle = startVertex.adjecentVertices.get(Math.floorMod((startVertex.adjecentVertices.indexOf(mergedChildren.get(mergedChildren.size()-1).startNodes.get(mergedChildren.get(mergedChildren.size()-1).startNodes.size()-1)) + 1), startVertex.adjecentVertices.size()));
            hashMap.put((new Pair<TreeVertex, TreeVertex>(startNodes.get(1), startVertex)), 1);

             // Beispiel3-4-10  Außen
            hashMap.put((new Pair<TreeVertex, TreeVertex>(startNodes.get(2), startVertex)), 1);

            // Beispiel5-4-3  Außen
            hashMap.put((new Pair<TreeVertex, TreeVertex>(startNodes.get(0), startVertex)), -1);

        } else if (startNodes.size() == 2 && startVertex.adjecentVertices.size() > 2) {
            // Beispiel 8-6-5 außen
            TreeVertex nextVertexStartLeft = startVertex.adjecentVertices.get(Math.floorMod((startVertex.adjecentVertices.indexOf(mergedChildren.get(0).startNodes.get(0)) - 1), startVertex.adjecentVertices.size()));
            hashMap.put(new Pair<TreeVertex, TreeVertex>(mergedChildren.get(0).startNodes.get(0), startVertex), alphaul);

            // Beispiel 5-6-7 Außen
            TreeVertex nextVertexStarRight = startVertex.adjecentVertices.get(Math.floorMod((startVertex.adjecentVertices.indexOf(mergedChildren.get(1).startNodes.get(0)) + 1), startVertex.adjecentVertices.size()));
            hashMap.put((new Pair<TreeVertex, TreeVertex>(nextVertexStarRight, startVertex)), alphaur);

            //Winkel zwischen der linken und rechten äußeren Kanten "innen" (Bsp. am Ende von Kante 7-6 an Knoten 6)
            hashMap.put((new Pair<TreeVertex, TreeVertex>((mergedChildren.get(1).startNodes.get(0)), startVertex)), ((alphaur + alphaul == 2) && (startVertex.adjecentVertices.size() == 3)) ? 0 : 1);
        } else if (startVertex.adjecentVertices.size() == 2) {

            hashMap.put((new Pair<TreeVertex, TreeVertex>(sinkVertex, startVertex)), 1);
            hashMap.put((new Pair<TreeVertex, TreeVertex>(startNodes.get(0), startVertex)), -1);



        }






            if (sinkNodes.size() == 3) {
                // linker Winkel an SinkVertex (außen) 14-13-8 an Knoten 13
            TreeVertex nextVertexSinkLeft = sinkVertex.adjecentVertices.get(Math.floorMod((sinkVertex.adjecentVertices.indexOf(sinkNodes.get(0)) + 1), sinkVertex.adjecentVertices.size()));
            hashMap.put((new Pair<TreeVertex, TreeVertex>(nextVertexSinkLeft, sinkVertex)), 1);

            // 8-13-14
            hashMap.put((new Pair<TreeVertex, TreeVertex>(sinkNodes.get(0), sinkVertex)), -1);

            // Beispie 7-13-12  "Zwischen Innenkanten"
            hashMap.put((new Pair<TreeVertex, TreeVertex>(sinkNodes.get(1), sinkVertex)), 1);

            // Beispie 12-13-14  "Zwischen Innenkanten"
            hashMap.put((new Pair<TreeVertex, TreeVertex>(sinkNodes.get(2), sinkVertex)), 1);

        } else if (sinkNodes.size() == 2 && startVertex.adjecentVertices.size() > 2) {
            // linker Winkel an SinkVertex (außen) 14-13-8 an Knoten 13
            TreeVertex nextVertexSinkLeft = sinkVertex.adjecentVertices.get(Math.floorMod((sinkVertex.adjecentVertices.indexOf(mergedChildren.get(0).sinkNodes.get(0)) + 1), sinkVertex.adjecentVertices.size()));
            hashMap.put((new Pair<TreeVertex, TreeVertex>(nextVertexSinkLeft, sinkVertex)), alphavl);

            // rechter Winkel an Sink Vertex (Außen)
            TreeVertex nextVertexSinkRight = sinkVertex.adjecentVertices.get(Math.floorMod((sinkVertex.adjecentVertices.indexOf(mergedChildren.get(1).sinkNodes.get(0)) - 1), sinkVertex.adjecentVertices.size()));
            hashMap.put((new Pair<TreeVertex, TreeVertex>((mergedChildren.get(1).sinkNodes.get(0)), sinkVertex)), alphavr);

            //Winkel zwischen der linken und rechten äußeren Kanten "innen" (Bsp. am Ende von Kante 9-11-10 an Knoten 11)
            hashMap.put((new Pair<TreeVertex, TreeVertex>((mergedChildren.get(0).startNodes.get(0)), sinkVertex)), (alphavr + alphavl == 2 && (sinkVertex.adjecentVertices.size() == 3)) ? 0 : 1);
        } else if (sinkVertex.adjecentVertices.size() == 2){


                hashMap.put((new Pair<TreeVertex, TreeVertex>(startVertex, sinkVertex)), -1);
                hashMap.put((new Pair<TreeVertex, TreeVertex>(sinkNodes.get(0), sinkVertex)), 1);

                System.out.println("Test");


        }


    }
}
