import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.util.SupplierUtil;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GraphAusKetten2<T> {


    private final int size;
    Hashtable<Integer, List<TreeVertex>> treeVertexHashtable = new Hashtable<>();
    Hashtable<Integer, stGraph> graphHashtable = new Hashtable<>();

    private final DefaultDirectedGraph<TreeVertex, DefaultEdge> graph;
    ArrayList<KettenComponent<TreeVertex, DefaultEdge>> compList = new ArrayList<>();


    public GraphAusKetten2(int i) {

        this.size = i;
        graph = new DefaultDirectedGraph<>(TreeVertex.getvSupplier, SupplierUtil.createDefaultEdgeSupplier(), false);

        //TODO oder eine Lange Kette erzeugen und diese dann in Komponenten zerteilen?
        for (int j = 0; j < i; j++) {
            TreeVertex v1 = graph.addVertex();
            TreeVertex v2 = graph.addVertex();
            graph.addEdge(v1, v2);
            compList.add(new KettenComponent<TreeVertex, DefaultEdge>(graph, v1, v2));
        }

        System.out.println("test");

    }


    public DefaultDirectedGraph<TreeVertex, DefaultEdge> generateSPgraph() {
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



    public <V, E> void mergeSnode(KettenComponent<V, E> kettenComponent1, KettenComponent<V, E> kettenComponent2) {

        mergeVertices((TreeVertex) kettenComponent1.getEnd(), (TreeVertex) kettenComponent2.getStart());
        kettenComponent1.setEnd(kettenComponent2.getEnd());
        compList.remove(kettenComponent2);
        kettenComponent1.updateDegrees();


    }

    public <V, E> void mergeGraphsPnode(KettenComponent<V, E> kettenComponent1, KettenComponent<V, E> kettenComponent2) {

        mergeVertices((TreeVertex) kettenComponent1.getStart(), (TreeVertex) kettenComponent2.getStart());
        mergeVerticesEnd((TreeVertex) kettenComponent1.getEnd(), (TreeVertex) kettenComponent2.getEnd());
        compList.remove(kettenComponent2);
        kettenComponent1.updateDegrees();


    }



    public <T> void mergeVertices(TreeVertex a3, TreeVertex b1) {
        Set<TreeVertex> adjVertices = Graphs.neighborSetOf(graph, b1);

        for (TreeVertex adjVertex :
                adjVertices) {
            graph.addEdge(a3, adjVertex);
        }
        graph.removeVertex(b1);

    }

    public <T> void mergeVerticesEnd(TreeVertex a3, TreeVertex b1) {
        Set<DefaultEdge> adjVertices = graph.incomingEdgesOf(b1);

        for (DefaultEdge edge :
                adjVertices) {
            graph.addEdge(graph.getEdgeSource(edge),a3);
        }
        graph.removeVertex(b1);

    }


}
