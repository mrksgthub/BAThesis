package GraphGenerators;

import Algorithms.FaceGenerator;
import Algorithms.Flow.MaxFlow;
import Datatypes.SPQNode;
import Datatypes.SPQTree;
import Datatypes.Vertex;
import Helperclasses.SPQExporter;
import org.jgrapht.graph.DefaultEdge;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.ListIterator;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

public class SPQGenerator implements Callable, Runnable {

    SPQNode root;
    SPQTree tree;
    int size;
    int chanceOfP;
    private BlockingQueue<SPQGenerator> blockingQueue;
    private long elapsedTime2;
    private GraphgenSplitGraph graphgenSplitGraph;
    private volatile boolean shutdown = false;
     int counter;

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

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setChanceOfP(int chanceOfP) {
        this.chanceOfP = chanceOfP;
    }

    public BlockingQueue<SPQGenerator> getBlockingQueue() {
        return blockingQueue;
    }

    public void setBlockingQueue(BlockingQueue<SPQGenerator> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    public long getElapsedTime2() {
        return elapsedTime2;
    }

    public void setElapsedTime2(long elapsedTime2) {
        this.elapsedTime2 = elapsedTime2;
    }

    public boolean isShutdown() {
        return shutdown;
    }

    public void setShutdown(boolean shutdown) {
        this.shutdown = shutdown;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public int getCounter() {
        return counter;
    }

    public GraphgenSplitGraph getGraphgenSplitGraph() {
        return graphgenSplitGraph;
    }

    public void shutdown() {
        shutdown = true;
    }


    public void run() {
        run(size, chanceOfP);
    }


    public void run(int size, int chanceOfP) {

        // Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        // logger.setLevel(Level.ALL);
        // Handler handler = new ConsoleHandler();
        // handler.setLevel(Level.ALL);
        //  logger.addHandler(handler);


        Boolean check = false;
        Hashtable<Vertex, ArrayList<Vertex>> embedding = new Hashtable<>();

        counter = 0;
        while (!check && !shutdown) {
            check = generateGraph(size, chanceOfP);


        }
        System.out.println("Generator zu Ende");


    }

     public Boolean generateGraph(int size, int chanceOfP) {
        Hashtable<Vertex, ArrayList<Vertex>> embedding;
        Boolean check;
        counter++;
        check = true;

        graphgenSplitGraph = new GraphgenSplitGraph(size, chanceOfP);
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
        for (Vertex vertex : tree.getConstructedGraph().vertexSet()
        ) {
            int i = tree.getConstructedGraph().degreeOf(vertex);
            if (i > 4) {
                continue;
            }
        }


        // Zeit:
        long startTime = System.currentTimeMillis();

        check = root.computeRepresentability(check);
        if (check) {
            check = (tree.computeNofRoot()) ? check : false;
            if (!check) {
                System.out.println("Didimo rejected at source Node");
            }
        }

        if (!check) {
            // continue;
        }

        // Zeit:
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        // logger.info("Didimo Zeit: " + elapsedTime);

        System.out.println("Knotenanzahl: " + graphgenSplitGraph.getMultigraph().vertexSet().size());


        boolean tamassiaValid = true;

        try {
            //    Hashtable<Datatypes.TreeVertex, ArrayList<Datatypes.TreeVertex>> embedding = erstelleHashtablefuerFacegenerator(tree);
            FaceGenerator<Vertex, DefaultEdge> treeVertexFaceGenerator = new FaceGenerator<Vertex, DefaultEdge>(tree.getConstructedGraph(), root.getStartVertex(), root.getSinkVertex(), embedding);
            treeVertexFaceGenerator.generateFaces2(); // counterclockwise = inner, clockwise = outerFacette
            // Zeit2:
            long startTime2 = System.currentTimeMillis();
         //   DefaultDirectedWeightedGraph<Datatypes.TreeVertex, DefaultWeightedEdge> treeVertexDefaultEdgeDefaultDirectedWeightedGraph = treeVertexFaceGenerator.generateFlowNetworkLayout2();
         //   treeVertexFaceGenerator.generateCapacities();
            MaxFlow test = new MaxFlow(tree, treeVertexFaceGenerator);
            test.run3();

            // Zeit2:
            long stopTime2 = System.currentTimeMillis();
            long elapsedTime2 = stopTime2 - startTime2;
            // logger.info("Tamassia Zeit: " + elapsedTime2);
        } catch (Exception e) {
            tamassiaValid = false;
            System.out.println("----------------------------------------Invalid Graph-----------------------------------------------------------");
        }

        assert (tamassiaValid == check);
        // assert(false);
       // tamassiaValid = false;
        if (tamassiaValid != check) {
            try {
                SPQExporter spqExporter = new SPQExporter(tree);
                //      spqExporter.run(root);
                spqExporter.run(root, "C:/bug.txt");


                throw new Exception("AHHHH");
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        return check;
    }


    public Hashtable<Vertex, ArrayList<Vertex>> erstelleHashtablefuerFacegenerator(SPQTree tree) {
        Hashtable<Vertex, ArrayList<Vertex>> embedding = new Hashtable<>();

        for (Vertex vertex :
                tree.getConstructedGraph().vertexSet()) {

            ArrayList<Vertex> arrList = new ArrayList<>();

            Set<DefaultEdge> tempIn = tree.getConstructedGraph().incomingEdgesOf(vertex);
            Set<DefaultEdge> tempOut = tree.getConstructedGraph().outgoingEdgesOf(vertex);


            ArrayList<DefaultEdge> inEdgesList = new ArrayList<>(tempIn);
            ListIterator<DefaultEdge> tempInListIterator = inEdgesList.listIterator(inEdgesList.size());
            ListIterator<DefaultEdge> tempOutListIterator = new ArrayList<>(tempOut).listIterator();

            while (tempInListIterator.hasPrevious()) {
                arrList.add(tree.getConstructedGraph().getEdgeSource(tempInListIterator.previous()));
            }
            while (tempOutListIterator.hasNext()) {
                arrList.add(tree.getConstructedGraph().getEdgeTarget(tempOutListIterator.next()));
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


    @Override
    public SPQGenerator call() throws Exception {
        run();
        blockingQueue.put(this);
        return this;
    }
}

