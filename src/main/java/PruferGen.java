import org.jbpt.graph.Graph;
import org.jgrapht.generate.PruferTreeGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.util.SupplierUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.function.Supplier;


/**
 * Klasse von geekforgeeks.com übernommen
 */
public class PruferGen {

    private static org.jbpt.graph.Graph graph = new org.jbpt.graph.Graph();
    int size;
    static ArrayList<TreeVertex> nodeList = new ArrayList<>();


    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public PruferGen(int size) {

        this.size = size;
        for (int i = 0; i < size; i++) {

            nodeList.add(new TreeVertex(Integer.toString(i)));

        }


    }

    // Function to Generate Random Tree
    static void generateRandomTree(int n) {



        int length = n - 2;
        int[] arr = new int[length];

        generatePruferCode(length, arr);

        printTreeEdges(arr, length);
        System.out.println(graph.toDOT());

    }

    public static void generatePruferCode(int length, int[] arr) {
        Random rand = new Random();
        // Loop to Generate Random Array
        for (int i = 0; i < length; i++) {
            arr[i] = rand.nextInt(length + 1) + 1;
        }
    }


    // Prints edges of tree
    // represented by give Prufer code
    static void printTreeEdges(int prufer[], int m) {

        int vertices = m + 2;
        int vertex_set[] = new int[vertices];

        // Initialize the array of vertices
        for (int i = 0; i < vertices; i++)
            vertex_set[i] = 0;

        // Number of occurrences of vertex in code
        for (int i = 0; i < vertices - 2; i++)
            vertex_set[prufer[i] - 1] += 1;

        System.out.print("\nThe edge set E(G) is:\n");

        int j = 0;

        // Find the smallest label not present in
        // prufer[].
        for (int i = 0; i < vertices - 2; i++) {
            for (j = 0; j < vertices; j++) {

                // If j+1 is not present in prufer set
                if (vertex_set[j] == 0) {

                    // Remove from Prufer set and print
                    // pair.
                    vertex_set[j] = -1;
                    System.out.print("(" + (j + 1) + ", "
                            + prufer[i] + ") ");

                    graph.addEdge(nodeList.get(j + 1 - 1), nodeList.get((prufer[i] - 1)));


                    vertex_set[prufer[i] - 1]--;

                    break;
                }
            }
        }

        j = 0;

        // For the last element

        int start = 0;
        int end = 0;


        for (int i = 0; i < vertices; i++) {
            if (vertex_set[i] == 0 && j == 0) {

                System.out.print("(" + (i + 1) + ", ");
                start = (i + 1);

                j++;


            } else if (vertex_set[i] == 0 && j == 1)
                System.out.print((i + 1) + ")\n");
            end = (i + 1);


        }

        graph.addEdge(nodeList.get(start - 1), nodeList.get(end - 1));


    }


    public static <V> SPQTree getTreeVertexDefaultEdgeDefaultUndirectedGraph(Supplier<V> vSupplier, int size) {
        SPQTree<V, DefaultEdge> jgrapthTest = new SPQTree<V, DefaultEdge>(vSupplier, SupplierUtil.createDefaultEdgeSupplier(), false);
        PruferTreeGenerator<V, DefaultEdge> pruferTreeGenerator = new PruferTreeGenerator<V, DefaultEdge>(size);
        pruferTreeGenerator.generateGraph(jgrapthTest);

        Iterator<V> treeVertexIterator = jgrapthTest.vertexSet().iterator();

        while (treeVertexIterator.hasNext()
        ) {

            V treeVertex = treeVertexIterator.next();

            // System.out.println(jgrapthTest.outDegreeOf(treeVertex) + " " + treeVertex.getName());

        }
        return jgrapthTest;
    }




}





