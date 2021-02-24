import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;

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
    public void calculateRepresentabilityInterval(DirectedMultigraph<TreeVertex, DefaultEdge> graph) {

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

            if (mL <= MR && mL <= MC && mR <= ML && mR <= MC && mC <= ML && mC <= MR) {
                repIntervalLowerBound = Math.max(mL - 2, mC);
                repIntervalLowerBound = Math.max( repIntervalLowerBound, mR + 2);
                repIntervalUpperBound = Math.min(ML - 2, MC);
                repIntervalUpperBound = Math.min(repIntervalUpperBound, MC + 2);


            } else {
                System.out.println("Intervals do not overlap P has 3 Children" + " " + this.getName() );
            }


        } else if (mergedChildren.size() == 2) {

            TreeVertex vPole;
            TreeVertex uPole;
            SPQNode leftSNode = mergedChildren.get(0);
            SPQNode rightSNode = mergedChildren.get(1);

            /*
            SPQNode firstChildeofLeftSNode = leftSNode.getMergedChildren().get(0);
            SPQNode lastChildeofLeftSNode = leftSNode.getMergedChildren().get(leftSNode.getMergedChildren().size() - 1);
            SPQNode firstChildeofRightSNode = rightSNode.getMergedChildren().get(0);
            SPQNode lastChildeofRightSNode = rightSNode.getMergedChildren().get(leftSNode.getMergedChildren().size() - 1);
*/

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


    /*            if (outDegreeCounterSink == 2 && outDegreeCounterStart == 1) { // Fall I2O21
                    double tempmL = mL;
                    double tempmR = mR;
                    double tempML = ML;
                    double tempMR = MR;
                    mL = tempmR;
                    mR = tempmL;
                    MR = tempML;
                    ML = tempMR;
                }

     */


                double lBound = mL - MR;
                double upBound = ML - mR;


                if (lBound <= 4 - gamma && 2 <= upBound) {

                    repIntervalLowerBound = Math.max(mL - 2, mR) + gamma / 2;
                    repIntervalUpperBound = Math.min(ML, MR + 2) - gamma / 2;


                } else {
                    System.out.println("No rectalinear drawing possible I2Oab" + " " + this.getName() );
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


                if ((childrenOfFirstChild.get(0).getNodeType() == NodeTypesEnum.NODETYPE.P) && (childrenOfFirstChild.get(childrenOfFirstChild.size() - 1).getNodeType() == NodeTypesEnum.NODETYPE.P)) { //I3ll
                    vPole = startVertex;
                    uPole = sinkVertex;
                    pdU = 0;
                    pdV = 0;
                } else if ((childrenOfSecondChild.get(0).getNodeType() == NodeTypesEnum.NODETYPE.P) && (childrenOfSecondChild.get(childrenOfSecondChild.size() - 1).getNodeType() == NodeTypesEnum.NODETYPE.P)) //I3rr
                {
                    vPole = startVertex;
                    uPole = sinkVertex;
                    pdU = 1;
                    pdV = 1;
                } else if ((childrenOfFirstChild.get(0).getNodeType() == NodeTypesEnum.NODETYPE.P) && (childrenOfFirstChild.get(childrenOfFirstChild.size() - 1).getNodeType() != NodeTypesEnum.NODETYPE.P)) { //I3lr
                    vPole = startVertex;
                    uPole = sinkVertex;
                    pdU = 0;
                    pdV = 1;
                } else if ((childrenOfSecondChild.get(0).getNodeType() == NodeTypesEnum.NODETYPE.P) && (childrenOfSecondChild.get(childrenOfSecondChild.size() - 1).getNodeType() != NodeTypesEnum.NODETYPE.P)) { //I3rl
                    vPole = sinkVertex;
                    uPole = startVertex;
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
                    System.out.println("No rectalinear I3dd'" + " " + this.getName() );
                }


            } else { // I_3dO_alphaBeta

                double pd = 9999;
                double gamma = outDegreeCounterStart + outDegreeCounterSink - 2;

                if (inDegreeCounterStart == 3) {

                    pd = (mergedChildren.get(0).computeHowManyCommonNodesThisAndSet(tempSetStart) == 2) ? 0 : 1;
                } else { // umkehren
                    pd = (mergedChildren.get(0).computeHowManyCommonNodesThisAndSet(tempSetSink) == 2) ? 0 : 1;

                    /*
                    double tempmL = mL;
                    double tempmR = mR;
                    double tempML = ML;
                    double tempMR = MR;
                    mL = tempmR;
                    mR = tempmL;
                    MR = tempML;
                    ML = tempMR;

                     */
                }

                double lBound = mL - MR;
                double upBound = ML - mR;

                if (lBound <= 3.5 - gamma && 2.5 <= upBound) {

                    repIntervalLowerBound = Math.max(mL - 1.5, mR + 1) + (gamma - pd) / 2;
                    repIntervalUpperBound = Math.min(ML -0.5, MR + 2) - (gamma + pd) / 2;


                } else {
                    System.out.println("No rectalinear drawing possible I3dO" + " " + this.getName() );
                }


            }


        } else {
            System.out.println("Invalid number of Children for P-Node");
        }




    }



}
