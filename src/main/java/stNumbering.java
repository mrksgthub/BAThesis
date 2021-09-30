import org.jgrapht.Graphs;
import org.jgrapht.graph.AsUndirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.util.DoublyLinkedList;

import java.util.*;

public class stNumbering {


    static boolean[] visited;
    static int[] childOf;
    static int[] incomingEdge;
    static int[] OrientationOfIncomingEdge;
    static int[] listPositionOfVertex;
    static Edge[] nextDependentEdge;
    static int[] dfs;
    static int i = 0;
    static ArrayList<DefaultEdge> Edges = new ArrayList<>();
    static int[] edgeInt;
    static int[] dfiArray;
    static TreeVertex[] vertices;
    private static DirectedMultigraph<TreeVertex, DefaultEdge> graph;
    private static AsUndirectedGraph<TreeVertex, DefaultEdge> graph3;
    DoublyLinkedList<TreeVertex> L;

    public static void main(String[] args) {
        SPQTree tree;
        SPQNode root;

        SPQImporter spqImporter = new SPQImporter("C:/a.txt");
        spqImporter.run();


        tree = spqImporter.tree;
        root = tree.getRoot();

        DefaultDirectedGraph<SPQNode, DefaultEdge> graph2 = GraphHelper.treeToDOT(root, 2);
        GraphHelper.printTODOTSPQNode(graph2);


        graph = new DirectedMultigraph<>(DefaultEdge.class);

        graph3 = new AsUndirectedGraph<>(graph);

        Graphs.addGraph(graph, tree.constructedGraph);
        visited = new boolean[graph3.vertexSet().size()];
        childOf = new int[graph3.vertexSet().size()];
        dfs = new int[graph3.vertexSet().size()];
        edgeInt = new int[graph3.vertexSet().size()];
        Edges.addAll(graph3.edgeSet());
        dfiArray = new int[graph3.vertexSet().size()];
        vertices = new TreeVertex[graph3.vertexSet().size()];

        int i = 0;
        for (TreeVertex v : graph3.vertexSet()
        ) {
            vertices[v.id] = v;
        }


        DFTransversal dFTransversal = new DFTransversal(graph3);
        dFTransversal.run(vertices[0]);


    }


    public void dfs(TreeVertex root) {
        visited[root.id] = true;

        for (TreeVertex v : Graphs.neighborListOf(graph3, root)
        ) {

            if (visited[v.id]) {
                dfs(v);
            } else {

            }

        }

    }


    public void dfs2(TreeVertex v) {
        dfs[v.id] = ++i;


    }


}

class Vertex {
    TreeVertex v;
    int id;
    int orientation;

    public Vertex(TreeVertex v) {
        this.v = v;
        id = v.id;
    }
}

class Edge {


}


class DFTransversal {

    AsUndirectedGraph<TreeVertex, DefaultEdge> graph3;
    DirectedMultigraph<TreeVertex, DefaultEdge> dfsTree = new DirectedMultigraph<TreeVertex, DefaultEdge>(DefaultEdge.class);
    boolean[] dfsVisited;
    TreeVertex[] childOf;
    List<Stack<TreeVertex>> stackList = new LinkedList<>();
    HashMap<DefaultEdge, Boolean> edgesVisited = new HashMap<>();


    public DFTransversal(AsUndirectedGraph<TreeVertex, DefaultEdge> graph3) {
        this.graph3 = graph3;
        dfsVisited = new boolean[graph3.vertexSet().size() * 3];
        childOf = new TreeVertex[graph3.vertexSet().size() * 3];

        // Arrays.fill(childOf, -1);
    }

    public void run(TreeVertex vertex) {

        dfs(vertex);


        GraphHelper.printToDOTTreeVertex(dfsTree);


    }

    public void dfs(TreeVertex vertex) { // https://www.techiedelight.com/depth-first-search/ für die Implementation des itarativen DFS algorithmus

        int v;

        // create a stack used to do iterative DFS
        Stack<TreeVertex> stack = new Stack<>();
        Deque<TreeVertex> ear = new ArrayDeque<>();

        // push the source node into the stack
        stack.push(vertex);

        // loop till stack is empty
        while (!stack.empty()) {
            // Pop a vertex from the stack
            vertex = stack.pop();


            // if the vertex is already discovered yet, ignore it
            if (dfsVisited[vertex.id]) {
                if (childOf[vertex.id] != null) {
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
            dfsVisited[vertex.id] = true;
            System.out.print(vertex.id + " ");
            dfsTree.addVertex(vertex);
            if (childOf[vertex.id] != null) {
                dfsTree.addEdge(childOf[vertex.id], vertex);
            }

            // do for every edge `v —> u`
            List<TreeVertex> adj = Graphs.neighborListOf(graph3, vertex);
            for (int i = adj.size() - 1; i >= 0; i--) {
                TreeVertex u = adj.get(i);
                if (!dfsVisited[u.id]) {
                    stack.push(u);
                    childOf[u.id] = vertex;
                }
            }
        }


        boolean[] visited = new boolean[graph3.vertexSet().size() * 3];

        while (!ear.isEmpty()) {

            TreeVertex vertex1 = ear.pop();

            List<TreeVertex> adj = Graphs.neighborListOf(graph3, vertex1);
            for (int i = adj.size() - 1; i >= 0; i--) {
                if (adj.get(i) == childOf[vertex1.id] || childOf[adj.get(i).id] == vertex1 || edgesVisited.containsKey(graph3.getEdge(adj.get(i), vertex1))        ) {
                    continue;
                }



                //backedge found
                Stack<TreeVertex> newEar = new Stack<>();
                newEar.push(vertex1);
                visited[vertex1.id] = true;

                TreeVertex vertex2 = adj.get(i);


                edgesVisited.putIfAbsent(graph3.getEdge(vertex1, vertex2), true);

                while (newEar.firstElement() != vertex2 && vertex2 != null) {


                    newEar.push(vertex2);

                    if (visited[vertex2.id]) {
                        break;
                    }

                    visited[vertex2.id] = true;

                    vertex2 = childOf[vertex2.id];

                }
                stackList.add(newEar);

            }
        }


    }


}