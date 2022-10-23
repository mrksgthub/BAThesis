package GraphGenerators;

import PlanarityAndAngles.Didimo.DidimoRepresentability;
import Datastructures.SPQNode;
import Datastructures.SPQStarTree;
import Datastructures.Vertex;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

/**
 * Implementiert die Methode, um mit GraphgenSplitGraph einen Graphen zu erzeugen und ihn diesen dann auf rektilineare
 * Planarität zu testen.
 *
 *
 *
 */
public class SPQGenerator  {

    private SPQNode root;
    private SPQStarTree tree;
    private int size;
    private int chanceOfP;
    private int maxDeg =4;
    private int counter;
    private BlockingQueue<SPQGenerator> blockingQueue;
    private final boolean shutdown = false;
    private final int mode = 0;

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

    public SPQGenerator(int nodes, int chanceOfP, int maxDeg) {
        this.size = nodes;
        this.chanceOfP = chanceOfP;

        this.maxDeg = maxDeg;
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
            check = generateGraph(size, chanceOfP, maxDeg, mode);

        }
        System.out.println("Generator zu Ende");


    }

    /**
     * Diese Methode ist dafür zuständig ein GraphGenSplitGraph Objekt zu erzeugen, welches einen SP-Graphen erzeugt.
     * Dieser wird dann auf rektilineare Planarität getestet.
     *
     *
     * @param nodes Maximale Anzahl von Operationen, welche der Graphengenerator ausführen kann und gleichzeitig die maximale
     *            Kantenanzahl des SP-Graphen
     * @param chanceOfP Wahrscheintlichkeit dafür, dass der Graphengenerator versuchen wird einen Q-Knoten durch einen
     *                  P-Knoten zu ersetzen.
     * @param maxDeg maximaler Knotengrad.
     * @param mode Welche Art von SP-Graph erzeugt werden soll 0 = random 1 = MixDeg3Deg4 2 = Deg3 3 = Deg4.
     * @return Ist rektilineare Zeuchnung möglich ja = true, sonst = false-
     */
    public Boolean generateGraph(int nodes, int chanceOfP, int maxDeg, int mode) {
        boolean check;
        counter++;
        check = true;
        Vertex.resetIdCounter();
        GraphgenSplitGraph graphgenSplitGraph = new GraphgenSplitGraph(nodes, chanceOfP, maxDeg, mode);
        graphgenSplitGraph.generateGraph();


        root = graphgenSplitGraph.getRoot();
      //  System.out.println("SPQ-Trees");

        tree = new SPQStarTree(root);



        tree.addValidSPQStarTreeRepresentation(root);
        tree.initializeSPQNodes(root);

     //   System.out.println("SPQ-Trees Done");
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

      //  System.out.println("Knotenanzahl: " + graphgenSplitGraph.getMultigraph().vertexSet().size());







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


}

