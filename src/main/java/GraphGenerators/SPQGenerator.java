package GraphGenerators;

import PlanarityAndAngles.Didimo.DidimoRepresentability;
import Datastructures.SPQNode;
import Datastructures.SPQStarTree;
import Datastructures.Vertex;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

public class SPQGenerator implements Callable, Runnable {

    private SPQNode root;
    private SPQStarTree tree;
    private int size;
    private int chanceOfP;
    private int maxDeg =4;
    private int einfachheit = 1;
    private int counter;
    private BlockingQueue<SPQGenerator> blockingQueue;
    private final boolean shutdown = false;
    private int mode = 0;

    public SPQGenerator(int size, int chanceOfP) {
        this.size = size;
        this.chanceOfP = chanceOfP;
    }

    public SPQGenerator(int size, int chanceOfP, BlockingQueue<SPQGenerator> blockingQueue) {
        this.size = size;
        this.chanceOfP = chanceOfP;
        this.blockingQueue = blockingQueue;
    }

    public SPQGenerator() {
    }

    public SPQGenerator(int ops, int chanceOfP, int maxDeg, int einfachheit) {
        this.size = ops;
        this.chanceOfP = chanceOfP;

        this.maxDeg = maxDeg;
        this.einfachheit = einfachheit;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }


    public void run() {
        run(size, chanceOfP);
    }


    private void run(int size, int chanceOfP) {

        Boolean check = false;

        counter = 0;
        while (!check && !shutdown) {
            check = generateGraph(size, chanceOfP, maxDeg, einfachheit, mode);

        }
        System.out.println("Generator zu Ende");


    }

    public Boolean generateGraph(int ops, int chanceOfP, int maxDeg, int einfachheit, int mode) {
        Boolean check;
        counter++;
        check = true;

        GraphgenSplitGraph graphgenSplitGraph = new GraphgenSplitGraph(ops, chanceOfP, maxDeg, einfachheit, mode);
        graphgenSplitGraph.generateGraph();


        root = graphgenSplitGraph.getRoot();
        System.out.println("SPQ-Trees");

        tree = new SPQStarTree(root);



        tree.addValidSPQStarTreeRepresentation(root);
        tree.initializeSPQNodes(root);

        System.out.println("SPQ-Trees Done");
        for (Vertex vertex : tree.getConstructedGraph().vertexSet()
        ) {
            int i = tree.getConstructedGraph().degreeOf(vertex);
            if (i > 4) {
                continue;
            }
        }


        // Zeit:
        long startTime = System.currentTimeMillis();

        DidimoRepresentability didimoRepresentability = new DidimoRepresentability();
        check = didimoRepresentability.run(tree.getRoot());



        // Zeit:
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        // logger.info("Didimo Zeit: " + elapsedTime);

        System.out.println("Knotenanzahl: " + graphgenSplitGraph.getMultigraph().vertexSet().size());



/*

        boolean tamassiaValid = true;

    */
/*    try {*//*

            //    Hashtable<Datatypes.TreeVertex, ArrayList<Datatypes.TreeVertex>> embedding = erstelleHashtablefuerFacegenerator(tree);
            FaceGenerator<Vertex, DefaultEdge> treeVertexFaceGenerator = new FaceGenerator<Vertex, DefaultEdge>(tree.getConstructedGraph(), root.getStartVertex(), root.getSinkVertex());
            treeVertexFaceGenerator.generateFaces(); // counterclockwise = inner, clockwise = outerFacette
            // Zeit2:
            long startTime2 = System.currentTimeMillis();
            //   DefaultDirectedWeightedGraph<Datatypes.TreeVertex, DefaultWeightedEdge> treeVertexDefaultEdgeDefaultDirectedWeightedGraph = treeVertexFaceGenerator.generateFlowNetworkLayout2();
            //   treeVertexFaceGenerator.generateCapacities();

            System.out.println("Tamassia Start");
            MaxFlow test = new MaxFlow(treeVertexFaceGenerator.getPlanarGraphFaces());
       //   tamassiaValid = test.runPushRelabel(treeVertexFaceGenerator.getPlanarGraphFaces());
            tamassiaValid = test.runJGraptHImplementation();

            // Zeit2:
            long stopTime2 = System.currentTimeMillis();
            long elapsedTime2 = stopTime2 - startTime2;
            // logger.info("Tamassia Zeit: " + elapsedTime2);
  */
/*      } catch (Exception e) {
            tamassiaValid = false;
            System.out.println("----------------------------------------Invalid Graph-----------------------------------------------------------");
        }*//*


        assert (tamassiaValid == check);
        // assert(false);
        // tamassiaValid = false;
        if (tamassiaValid != check) {
            try {
                SPQExporter spqExporter = new SPQExporter();
                //      spqExporter.run(root);
                spqExporter.run(root, "C:/bug.txt");


                throw new Exception("AHHHH");
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
*/



        return check;
    }


    public SPQNode getRoot() {
        return root;
    }

    public void setRoot(SPQNode root) {
        this.root = root;
    }

    public SPQStarTree getTree() {
        return tree;
    }

    public void setTree(SPQStarTree tree) {
        this.tree = tree;
    }


    @Override
    public SPQGenerator call() throws Exception {
        run();
        blockingQueue.put(this);
        return this;
    }
}

