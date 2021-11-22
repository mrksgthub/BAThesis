package Experimental;

import Datastructures.SPQNode;
import Datastructures.SPQStarTree;
import Helperclasses.GraphHelper;
import Helperclasses.SPQImporter;
import org.jgrapht.Graphs;
import org.jgrapht.graph.AsUndirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.util.DoublyLinkedList;

import java.util.*;

class stNumbering {


    private static boolean[] visited;
    private static int[] childOf;
    static int[] incomingEdge;
    static int[] OrientationOfIncomingEdge;
    static int[] listPositionOfVertex;
    static Edge[] nextDependentEdge;
    private static int[] dfs;
    private static int i = 0;
    private static ArrayList<DefaultEdge> Edges = new ArrayList<>();
    private static int[] edgeInt;
    private static int[] dfiArray;
    private static Datastructures.Vertex[] vertices;
    private static DirectedMultigraph<Datastructures.Vertex, DefaultEdge> graph;
    private static AsUndirectedGraph<Datastructures.Vertex, DefaultEdge> graph3;
    DoublyLinkedList<Datastructures.Vertex> L;

    public static void main(String[] args) {

        /*
        Andere Methode
        counter = vertexSet.size()-1
        Weise s node wert 0 zu
              t node wert vertexSet.size()-1 und auf stack S
              while (S.notempty)
             {
              alle Kinder von t node auf stackm falls sie keinen Wert bis jetzt haben und weise ihnen counter-- zu.
             }


         */

        SPQStarTree tree;
        SPQNode root;

        SPQImporter spqImporter = new SPQImporter();
        spqImporter.run("C:/a.txt");


        tree = spqImporter.getTree();
        root = tree.getRoot();

        DefaultDirectedGraph<SPQNode, DefaultEdge> graph2 = GraphHelper.treeToDOT(root, 2);
        GraphHelper.printTODOTSPQNode(graph2);


        graph = new DirectedMultigraph<>(DefaultEdge.class);

        graph3 = new AsUndirectedGraph<>(graph);

        Graphs.addGraph(graph, tree.getConstructedGraph());
        visited = new boolean[graph3.vertexSet().size()];
        childOf = new int[graph3.vertexSet().size()];
        dfs = new int[graph3.vertexSet().size()];
        edgeInt = new int[graph3.vertexSet().size()];
        Edges.addAll(graph3.edgeSet());
        dfiArray = new int[graph3.vertexSet().size()];
        vertices = new Datastructures.Vertex[graph3.vertexSet().size()];

        int i = 0;
        for (Datastructures.Vertex v : graph3.vertexSet()
        ) {
            vertices[v.getId()] = v;
        }


        DFTransversal dFTransversal = new DFTransversal(graph3);
        dFTransversal.run(vertices[0]);


    }


    private void dfs(Datastructures.Vertex root) {
        visited[root.getId()] = true;

        for (Datastructures.Vertex v : Graphs.neighborListOf(graph3, root)
        ) {

            if (visited[v.getId()]) {
                dfs(v);
            } else {

            }

        }

    }


    public void dfs2(Datastructures.Vertex v) {
        dfs[v.getId()] = ++i;


    }


}

class Vertex {

}

class Edge {


}


class DFTransversal {

    private AsUndirectedGraph<Datastructures.Vertex, DefaultEdge> graph3;
    private DirectedMultigraph<Datastructures.Vertex, DefaultEdge> dfsTree = new DirectedMultigraph<Datastructures.Vertex, DefaultEdge>(DefaultEdge.class);
    private boolean[] dfsVisited;
    private Datastructures.Vertex[] childOf;
    private List<Stack<Datastructures.Vertex>> stackList = new LinkedList<>();
    private HashMap<DefaultEdge, Boolean> edgesVisited = new HashMap<>();


    public DFTransversal(AsUndirectedGraph<Datastructures.Vertex, DefaultEdge> graph3) {
        this.graph3 = graph3;
        dfsVisited = new boolean[graph3.vertexSet().size() * 3];
        childOf = new Datastructures.Vertex[graph3.vertexSet().size() * 3];

        // Arrays.fill(childOf, -1);
    }

    public void run(Datastructures.Vertex vertex) {

        dfs(vertex);


        GraphHelper.printToDOTTreeVertex(dfsTree);


    }

    private void dfs(Datastructures.Vertex vertex) { // https://www.techiedelight.com/depth-first-search/ für die Implementation des itarativen DFS algorithmus

        int v;

        // create a stack used to do iterative DFS
        Stack<Datastructures.Vertex> stack = new Stack<>();
        Deque<Datastructures.Vertex> ear = new ArrayDeque<>();

        // push the source node into the stack
        stack.push(vertex);

        // loop till stack is empty
        while (!stack.empty()) {
            // Pop a vertex from the stack
            vertex = stack.pop();


            // if the vertex is already discovered yet, ignore it
            if (dfsVisited[vertex.getId()]) {
                if (childOf[vertex.getId()] != null) {
                    //  dfsTree.addEdge(vertex, ear.firstElement());
                    //  stackList.add(ear);
                    //  ear = new Stack<>();
                }
                continue;
            }
            ear.addLast(vertex);

            // we will reach here if the popped vertex `v`
            // is not discovered yet; print it and process
            // its undiscovered adjacent nodes into the stack
            dfsVisited[vertex.getId()] = true;
            System.out.print(vertex.getId() + " ");
            dfsTree.addVertex(vertex);
            if (childOf[vertex.getId()] != null) {
                dfsTree.addEdge(childOf[vertex.getId()], vertex);
            }

            // do for every edge `v —> u`
            List<Datastructures.Vertex> adj = Graphs.neighborListOf(graph3, vertex);
            for (int i = adj.size() - 1; i >= 0; i--) {
                Datastructures.Vertex u = adj.get(i);
                if (!dfsVisited[u.getId()]) {
                    stack.push(u);
                    childOf[u.getId()] = vertex;
                }
            }
        }


        boolean[] visited = new boolean[graph3.vertexSet().size() * 3];

        while (!ear.isEmpty()) {

            Datastructures.Vertex vertex1 = ear.pop();

            List<Datastructures.Vertex> adj = Graphs.neighborListOf(graph3, vertex1);
            for (int i = adj.size() - 1; i >= 0; i--) {
                if (adj.get(i) == childOf[vertex1.getId()] || childOf[adj.get(i).getId()] == vertex1 || edgesVisited.containsKey(graph3.getEdge(adj.get(i), vertex1))        ) {
                    continue;
                }



                //backedge found
                Stack<Datastructures.Vertex> newEar = new Stack<>();
                newEar.push(vertex1);
                visited[vertex1.getId()] = true;

                Datastructures.Vertex vertex2 = adj.get(i);


                edgesVisited.putIfAbsent(graph3.getEdge(vertex1, vertex2), true);

                while (newEar.firstElement() != vertex2 && vertex2 != null) {


                    newEar.push(vertex2);

                    if (visited[vertex2.getId()]) {
                        break;
                    }

                    visited[vertex2.getId()] = true;

                    vertex2 = childOf[vertex2.getId()];

                }
                stackList.add(newEar);

            }
        }


    }


}