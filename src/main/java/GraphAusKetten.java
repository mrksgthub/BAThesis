import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.util.SupplierUtil;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GraphAusKetten<T> {


    private final int size;
    Hashtable<Integer, List<TreeVertex>> treeVertexHashtable = new Hashtable<>();
    Hashtable<Integer, stGraph> graphHashtable = new Hashtable<>();

    private final DefaultUndirectedGraph<TreeVertex, DefaultEdge> graph;
    ArrayList<KettenComponent<TreeVertex, DefaultEdge>> compList = new ArrayList<>();


    public GraphAusKetten(int i) {

        this.size = i;
        graph = new DefaultUndirectedGraph<>(TreeVertex.getvSupplier, SupplierUtil.createDefaultEdgeSupplier(), false);

        //TODO oder eine Lange Kette erzeugen und diese dann in Komponenten zerteilen?
        for (int j = 0; j < i; j++) {
            TreeVertex v1 = graph.addVertex();
            TreeVertex v2 = graph.addVertex();
            graph.addEdge(v1, v2);
            compList.add(new KettenComponent<TreeVertex, DefaultEdge>(graph, v1, v2));
        }

        System.out.println("test");

    }


    public DefaultUndirectedGraph<TreeVertex, DefaultEdge> generateSPgraph() {
        List<KettenComponent<TreeVertex, DefaultEdge>> toBeMergedComponents = new ArrayList<>();

        Random random = new Random();
        int runsWithoutChange = 0;

        //    while (compList.size() > 2 || (toBeMergedComponents.get(0).getStartNodeDegree() + toBeMergedComponents.get(1).getStartNodeDegree() <= 4) &&  toBeMergedComponents.get(0).getEndNodeDegree() + toBeMergedComponents.get(1).getEndNodeDegree() <= 4) {

        while (compList.size() > 2) {


            //    toBeMergedComponents =  GraphHelper.pickSample(compList, 2, random);
            toBeMergedComponents = compList;
            Collections.shuffle(toBeMergedComponents);

            runsWithoutChange++;

            //TODO vielleicht sollte falls kein passender component in "1" steht die geshuffelte Liste durchgegangen werden bis man einen component findet,
            // welcher für die S, oder P operation passt (methode schreiben public KettenComponent findComponent(toBeMergedComponents.get(0))
            if (random.nextInt(2) > 0 && toBeMergedComponents.get(0).getStartNodeDegree() + toBeMergedComponents.get(1).getStartNodeDegree() < 4 && toBeMergedComponents.get(0).getEndNodeDegree() + toBeMergedComponents.get(1).getEndNodeDegree() < 4 &&
                    !(graph.containsEdge(toBeMergedComponents.get(0).getStart(), toBeMergedComponents.get(0).getEnd()) && graph.containsEdge(toBeMergedComponents.get(1).getStart(), toBeMergedComponents.get(1).getEnd()))) {

                //TODO Incase the Graph already contains an edge going from s-t of component1 (Multiedges verhindern)
                mergeGraphsPnode(toBeMergedComponents.get(0), toBeMergedComponents.get(1));
                runsWithoutChange = 0;


            } else if (toBeMergedComponents.get(0).getEndNodeDegree() + toBeMergedComponents.get(1).getStartNodeDegree() <= 4) {

                mergeSnode(toBeMergedComponents.get(0), toBeMergedComponents.get(1));
                runsWithoutChange = 0;
            }

            // curing component from high degree
            if (toBeMergedComponents.get(0).getStartNodeDegree() > 3) {
                TreeVertex newNode = graph.addVertex();
                graph.addEdge(newNode, toBeMergedComponents.get(0).getStart());
                toBeMergedComponents.get(0).setStart(newNode);
                toBeMergedComponents.get(0).updateDegrees();
            }
            if (toBeMergedComponents.get(0).getEndNodeDegree() > 3) {
                TreeVertex newNode = graph.addVertex();
                graph.addEdge(toBeMergedComponents.get(0).getEnd(), newNode);
                toBeMergedComponents.get(0).setEnd(newNode);
                toBeMergedComponents.get(0).updateDegrees();
            }

            // add new edges if needed
            if (runsWithoutChange > 2) {
                TreeVertex v1 = graph.addVertex();
                TreeVertex v2 = graph.addVertex();
                graph.addEdge(v1, v2);
                compList.add(new KettenComponent<TreeVertex, DefaultEdge>(graph, v1, v2));
                runsWithoutChange = 0;
            }


        }
        //TODO alternativ ein s und ein t "capstone" vertices einfügen und diese dann mit 2 bzw 4 komponenten verbinden?
        toBeMergedComponents = GraphHelper.pickSample(compList, 2, random);
        mergeGraphsPnode(toBeMergedComponents.get(0), toBeMergedComponents.get(1));


        return graph;
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
        int chainsAmount = getChooseChain(2, i / 2);

        List<TreeVertex> vertexList1;

        // initialize chains
        int u = 0;
        for (int j = 1; j < chainsAmount + 1; j++) {

            vertexList1 = new LinkedList<>();
            vertexList1.add(new TreeVertex(Integer.toString(u++)));
            vertexList1.add(new TreeVertex(Integer.toString(u++)));
            treeVertexHashtable.put(j, vertexList1);

        }


        int chooseChain;

        for (int j = 0; j < (i - chainsAmount * 2); j++) {
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


    public <V, E> void mergeSnode(KettenComponent<V, E> kettenComponent1, KettenComponent<V, E> kettenComponent2) {

        mergeVertices((TreeVertex) kettenComponent1.getEnd(), (TreeVertex) kettenComponent2.getStart());
        kettenComponent1.setEnd(kettenComponent2.getEnd());
        compList.remove(kettenComponent2);
        kettenComponent1.updateDegrees();


    }

    public <V, E> void mergeGraphsPnode(KettenComponent<V, E> kettenComponent1, KettenComponent<V, E> kettenComponent2) {

        mergeVertices((TreeVertex) kettenComponent1.getStart(), (TreeVertex) kettenComponent2.getStart());
        mergeVertices((TreeVertex) kettenComponent1.getEnd(), (TreeVertex) kettenComponent2.getEnd());
        compList.remove(kettenComponent2);
        kettenComponent1.updateDegrees();


    }


    /**
     * Replaces a specific Vertex in a jgraphT Undirected Graph with another Vertex
     *
     * @param vertex
     * @param replace
     * @param <V>
     * @param <E>
     */
    public <V, E> void replaceVertex(Graph<V, E> graph, V vertex, V replace) {

        Set<E> edgeSet1 = new HashSet<E>(graph.incomingEdgesOf(vertex));
        Set<E> outgoingEdges = new HashSet<E>(graph.outgoingEdgesOf(vertex));


        for (E edge : edgeSet1) {
            if (vertex == graph.getEdgeTarget(edge)) {
                graph.addEdge(graph.getEdgeSource(edge), replace);
            } else if (vertex == graph.getEdgeSource(edge)) {

                graph.addEdge(replace, graph.getEdgeTarget(edge));

            }

        }

        graph.removeVertex(vertex);
    }


    public TreeVertex findVertex(String name) {

        Set<TreeVertex> vertexSet = this.graph.vertexSet();


        for (TreeVertex node : vertexSet
        ) {
            if (name.equals(node.getName())) {
                return node;
            }
        }

        System.out.println("Node not found");
        return null;

    }

    public <T> void mergeVertices(TreeVertex a3, TreeVertex b1) {
        Set<TreeVertex> adjVertices = Graphs.neighborSetOf(graph, b1);

        for (TreeVertex adjVertex :
                adjVertices) {
            graph.addEdge(a3, adjVertex);
        }
        graph.removeVertex(b1);

    }


}
