import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.util.SupplierUtil;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GraphAusKetten {


    private final int size;
    Hashtable<Integer, List<TreeVertex>> treeVertexHashtable = new Hashtable<>();
    Hashtable<Integer, stGraph> graphHashtable = new Hashtable<>();

    private final DefaultUndirectedGraph<TreeVertex, DefaultEdge> graph;



    public GraphAusKetten(int i) {

        this.size = i;
        graph = new DefaultUndirectedGraph<>(TreeVertex.getvSupplier, SupplierUtil.createDefaultEdgeSupplier(), false);
        List<TreeVertex> vertexList = new ArrayList<>();

        for (int j = 0; j < i; j++) {
                TreeVertex v1 = graph.addVertex();
                TreeVertex v2 = graph.addVertex();
            graph.addEdge(v1, v2);


        }

        int iter=0;
        for (TreeVertex vertex: graph.vertexSet()
             ) {
            vertexList.add(vertex);


        }


        System.out.println("test");


    }


    void generateChains() {


        // von Stackoverflow: finde die Splitpoints um size in zufällig viele Teile zu zerteilen
        List<Integer> splitPoints =
                IntStream.rangeClosed(2, size)
                        .boxed().collect(Collectors.toList());
        Collections.shuffle(splitPoints);
        splitPoints.subList(4, splitPoints.size()).clear();
        Collections.sort(splitPoints);


    }


    public void splitList(List<TreeVertex> testList, int size) {
        Random random = new Random();

        int j = 0;


        int divider = random.ints(2, (size + 1)).findFirst().getAsInt();
        System.out.println(divider);

        List<TreeVertex> vertexList1 = testList.subList(0, divider);
        List<TreeVertex> vertexList2 = testList.subList(divider, testList.size());

        treeVertexHashtable.put(j, vertexList1);
        treeVertexHashtable.put(j++, vertexList2);

        List<TreeVertex> vertexList = new LinkedList<>();

        for (int i = 0; i < 1; i++) {


            vertexList = treeVertexHashtable.get(i);
            divider = random.ints(2, (vertexList.size() + 1)).findFirst().getAsInt();
            System.out.println(divider);

            vertexList1 = testList.subList(0, divider);
            vertexList2 = testList.subList(divider, testList.size());

            treeVertexHashtable.put(j, vertexList1);
            treeVertexHashtable.put(j++, vertexList2);

        }


        System.out.println("Test");

    }


















    public void randomChainsBottomUp(int i) {


        Random random = new Random();
        int chainsAmount = getChooseChain(2, i/2);



        List<TreeVertex> vertexList1;



        // initialize chains
        int u = 0;
        for (int j = 1; j < chainsAmount+1; j++) {

            vertexList1 = new LinkedList<>();
            vertexList1.add(new TreeVertex(Integer.toString(u++)));
            vertexList1.add(new TreeVertex(Integer.toString(u++)));

            treeVertexHashtable.put(j, vertexList1);


        }


        int chooseChain;

        for (int j = 0; j < (i-chainsAmount*2); j++) {
            chooseChain = getChooseChain(1, treeVertexHashtable.size()); //beginnt mit index 0
            vertexList1 = treeVertexHashtable.get(chooseChain);
            vertexList1.add(new TreeVertex(Integer.toString(u++)));
        }
















        System.out.println("test");

    }

    private int getChooseChain(int min, int max) {

        Random random = new Random();
        return (random.nextInt(max + 1 - min) + min);
    }

    public void mergeChains(stGraph stGraph1, stGraph stGraph2) {










    }
}
