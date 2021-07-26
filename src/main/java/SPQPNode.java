import org.apache.commons.lang3.tuple.MutablePair;

import java.util.HashMap;

public class SPQPNode extends SPQNode {

    NodeTypesEnum.NODETYPE nodeType = NodeTypesEnum.NODETYPE.P;
    private int inDegreeCounterStart;
    private int inDegreeCounterSink;
    private int outDegreeCounterStart;
    private int outDegreeCounterSink;
    private boolean isRoot = false;


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
    public boolean calculateRepresentabilityInterval() {


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
                repIntervalUpperBound = Math.min(repIntervalUpperBound, MR + 2);
                System.out.println("3 Children" + " " + this.getName());


            } else {
                System.out.println("Intervals do not overlap P has 3 Children" + " " + this.getName());
                return false;
            }


        } else if (mergedChildren.size() == 2) {

            SPQNode leftSNode = mergedChildren.get(0);
            SPQNode rightSNode = mergedChildren.get(1);


            double mL = leftSNode.getRepIntervalLowerBound();
            double mR = rightSNode.getRepIntervalLowerBound();
            double ML = leftSNode.getRepIntervalUpperBound();
            double MR = rightSNode.getRepIntervalUpperBound();


            //TODO wurde geändert von dem auskommentierten zu diesem hier
            inDegreeCounterStart = startNodes.size();
            inDegreeCounterSink = sinkNodes.size();

            outDegreeCounterStart = startVertex.adjecentVertices.size() - inDegreeCounterStart;
            outDegreeCounterSink = sinkVertex.adjecentVertices.size() - inDegreeCounterSink;


            if (inDegreeCounterStart == 2 && inDegreeCounterSink == 2) { // I_2O_alphaBeta

                // Lemmma 8
                double gamma = outDegreeCounterStart + outDegreeCounterSink - 2;

                double lBound = mL - MR;
                double upBound = ML - mR;


                if (lBound <= 4 - gamma && 2 <= upBound) {

                    repIntervalLowerBound = Math.max(mL - 2, mR) + gamma / 2;
                    repIntervalUpperBound = Math.min(ML, MR + 2) - gamma / 2;

                    System.out.println("I2Oab" + " " + this.getName());

                } else {
                    System.out.println("No rectalinear drawing possible I2Oab" + " " + this.getName());
                    return false;
                }

            } else if ((inDegreeCounterStart == 3 && inDegreeCounterSink == 3))  // I_3dd
            {
                double pdV = 9999;
                double pdU = 9999;

                mL = leftSNode.getRepIntervalLowerBound();
                mR = rightSNode.getRepIntervalLowerBound();
                ML = leftSNode.getRepIntervalUpperBound();
                MR = rightSNode.getRepIntervalUpperBound();

                // Was tun falls erstes oder zweites child eine Q node ist
                if ((mergedChildren.get(0).startNodes.size() == 2) && (mergedChildren.get(0).sinkNodes.size() == 2)) { //I3ll


                    pdU = 0;
                    pdV = 0;
                } else if ((mergedChildren.get(1).startNodes.size() == 2) && (mergedChildren.get(1).sinkNodes.size() == 2)) //I3rr
                { // FIxbar indem man Q2 zu nem Qstar macht

                    pdU = 1;
                    pdV = 1;
                } else if ((mergedChildren.get(1).startNodes.size() == 2) && (mergedChildren.get(0).sinkNodes.size() == 2)) { //I3lr

                    pdU = 0;
                    pdV = 1;

                } else if ((mergedChildren.get(0).startNodes.size() == 2) && (mergedChildren.get(1).sinkNodes.size() == 2)) { //I3rl

                    pdU = 1;
                    pdV = 0;

                    System.out.println("I_3rl reverse" + this.getName());

                }
                double lBound = mL - MR;
                double upBound = ML - mR;

                if ((lBound <= 3) && (3 <= upBound)) {

                    repIntervalLowerBound = Math.max(mL - 1, mR + 2) - (pdU + pdV) / 2;
                    repIntervalUpperBound = Math.min(ML - 1, MR + 2) - (pdU + pdV) / 2;

                    System.out.println("I3_dd" + " " + this.getName());

                } else {
                    System.out.println("No rectalinear I3dd'" + " " + this.getName());
                    return false;
                }

            } else { // I_3dO_alphaBeta

                double pd = 9999;
                double gamma = outDegreeCounterStart + outDegreeCounterSink - 2;

                if (inDegreeCounterStart == 3) {

                    //TODO wurde geändert
                    pd = (mergedChildren.get(0).startNodes.size() == 2) ? 0 : 1;


                    System.out.println("I_3dOab reverse" + this.getName());

                } else { // check Sink
                    pd = (mergedChildren.get(0).sinkNodes.size() == 2) ? 0 : 1;
                    System.out.println("NI_3dOab normal" + " " + this.getName());

                }

                double lBound = mL - MR;
                double upBound = ML - mR;

                if (lBound <= 3.5 - gamma && 2.5 <= upBound) {

                    repIntervalLowerBound = Math.max(mL - 1.5, mR + 1) + (gamma - pd) / 2;
                    repIntervalUpperBound = Math.min(ML - 0.5, MR + 2) - (gamma + pd) / 2;
                } else {
                    System.out.println("No rectalinear drawing possible I3dO" + " " + this.getName());
                    return false;
                }

            }


        } else {
            System.out.println("Invalid number of Children for P-Node");
            return false;
        }

        return true;
    }

    @Override
    public void computeOrthogonalRepresentation(HashMap<MutablePair<TreeVertex, TreeVertex>, Integer> hashMap) {

        // Für innere Facetten nur der Winkel auf der rechten Seite relevant?
        TreeVertex vertex1 = mergedChildren.get(0).startNodes.get(0);
        if (startNodes.size() == 3 && !this.isRoot) {
            // mergedChildren 3, oder 2 sind die Fälle die unterschieden werden müssen

            // Beispiel3-4-10  Außen
            TreeVertex nextVertexStarRight = startVertex.adjecentVertices.get(Math.floorMod((startVertex.adjecentVertices.indexOf(mergedChildren.get(mergedChildren.size() - 1).startNodes.get(mergedChildren.get(mergedChildren.size() - 1).startNodes.size() - 1)) + 1), startVertex.adjecentVertices.size()));
            hashMap.put((new Tuple<>(nextVertexStarRight, startVertex)), 1);

            //Beispiel 9-4-10
            TreeVertex nextVertexMiddle = startVertex.adjecentVertices.get(Math.floorMod((startVertex.adjecentVertices.indexOf(mergedChildren.get(mergedChildren.size() - 1).startNodes.get(mergedChildren.get(mergedChildren.size() - 1).startNodes.size() - 1)) + 1), startVertex.adjecentVertices.size()));
            hashMap.put((new Tuple<>(startNodes.get(1), startVertex)), 1);

            // Beispiel3-4-10  Außen
            hashMap.put((new Tuple<>(startNodes.get(2), startVertex)), 1);

            // Beispiel5-4-3  Außen
            hashMap.put((new Tuple<>(startNodes.get(0), startVertex)), 1);

        } else if (startNodes.size() == 2 && startVertex.adjecentVertices.size() > 2 && !this.isRoot) {
            // Beispiel 8-6-5 außen
            TreeVertex nextVertexStartLeft = startVertex.adjecentVertices.get(Math.floorMod((startVertex.adjecentVertices.indexOf(vertex1) - 1), startVertex.adjecentVertices.size()));
            hashMap.put(new Tuple<>(vertex1, startVertex), alphaul);

            // Beispiel 5-6-7 Außen
            TreeVertex vertex2 = mergedChildren.get(1).startNodes.get(0);
            TreeVertex nextVertexStarRight = startVertex.adjecentVertices.get(Math.floorMod((startVertex.adjecentVertices.indexOf(vertex2) + 1), startVertex.adjecentVertices.size()));
            hashMap.put((new Tuple<>(nextVertexStarRight, startVertex)), alphaur);

            //Winkel zwischen der linken und rechten äußeren Kanten "innen" (Bsp. am Ende von Kante 7-6 an Knoten 6)
            hashMap.put((new Tuple<>(vertex2, startVertex)), ((alphaur + alphaul == 2) && (startVertex.adjecentVertices.size() == 3)) ? 0 : 1);

        } else if (startVertex.adjecentVertices.size() == 2 && this.isRoot) {

            hashMap.put((new Tuple<>(sinkVertex, startVertex)), 1);
            hashMap.put((new Tuple<>(startNodes.get(0), startVertex)), -1);


        }


        if (sinkNodes.size() == 3 && !this.isRoot) {
            // linker Winkel an SinkVertex (außen) 14-13-8 an Knoten 13
            TreeVertex nextVertexSinkLeft = sinkVertex.adjecentVertices.get(Math.floorMod((sinkVertex.adjecentVertices.indexOf(sinkNodes.get(0)) + 1), sinkVertex.adjecentVertices.size()));
            hashMap.put((new Tuple<>(nextVertexSinkLeft, sinkVertex)), 1);

            // 8-13-7
            hashMap.put((new Tuple<>(sinkNodes.get(0), sinkVertex)), 1);

            // Beispie 7-13-12  "Zwischen Innenkanten"
            hashMap.put((new Tuple<>(sinkNodes.get(1), sinkVertex)), 1);

            // Beispie 12-13-14  "Zwischen Innenkanten"
            hashMap.put((new Tuple<>(sinkNodes.get(2), sinkVertex)), 1);

        } else if (sinkNodes.size() == 2 && sinkVertex.adjecentVertices.size() > 2 && !this.isRoot) {
            // linker Winkel an SinkVertex (außen) 14-13-8 an Knoten 13
            TreeVertex nextVertexSinkLeft = sinkVertex.adjecentVertices.get(Math.floorMod((sinkVertex.adjecentVertices.indexOf(mergedChildren.get(0).sinkNodes.get(0)) + 1), sinkVertex.adjecentVertices.size()));
            hashMap.put((new Tuple<>(nextVertexSinkLeft, sinkVertex)), alphavl);

            // rechter Winkel an Sink Vertex (Außen)
            TreeVertex nextVertexSinkRight = sinkVertex.adjecentVertices.get(Math.floorMod((sinkVertex.adjecentVertices.indexOf(mergedChildren.get(1).sinkNodes.get(0)) - 1), sinkVertex.adjecentVertices.size()));
            hashMap.put((new Tuple<>((mergedChildren.get(1).sinkNodes.get(0)), sinkVertex)), alphavr);

            //Winkel zwischen der linken und rechten äußeren Kanten "innen" (Bsp. am Ende von Kante 9-11-10 an Knoten 11)
            hashMap.put((new Tuple<>(sinkNodes.get(0), sinkVertex)), (alphavr + alphavl == 2 && (sinkVertex.adjecentVertices.size() == 3)) ? 0 : 1);

        } else if (sinkVertex.adjecentVertices.size() == 2 && this.isRoot) {

            hashMap.put((new Tuple<>(startVertex, sinkVertex)), -1);
            hashMap.put((new Tuple<>(sinkNodes.get(0), sinkVertex)), 1);

          //  System.out.println("Test");


        }


    }

    public boolean isRoot() {
        return this.isRoot;
    }

    @Override
    public void setRoot() {
        super.setRoot();
        isRoot = true;

    }
}
