import org.jgrapht.graph.DefaultDirectedGraph;

import java.util.*;
import java.util.function.Supplier;

public class DFSTree<V, E> {


    private Map<TreeVertex, List<TreeVertex>> adjVertices = new HashMap<>();

    public Map<TreeVertex, List<TreeVertex>> getAdjVertices() {
        return this.adjVertices;
    }

    public void setAdjVertices(Map<TreeVertex, List<TreeVertex>> adjVertices) {
        this.adjVertices = adjVertices;
    }


    void addVertex(String label) {
        this.adjVertices.putIfAbsent(new TreeVertex(label), new ArrayList<>());
    }

    void removeVertex(String label) {
        TreeVertex v = new TreeVertex(label);
        this.adjVertices.values().stream().forEach((e) -> {
            e.remove(v);
        });
        this.adjVertices.remove(new TreeVertex(label));
    }


    void addEdge(V label1, V label2) {
        ((List) this.adjVertices.get(label2)).add(label1);
        ((List) this.adjVertices.get(label1)).add(label2);
    }

    void removeEdge(TreeVertex label1, TreeVertex label2) {
        List<TreeVertex> eV1 = this.adjVertices.get(label1);
        List<TreeVertex> eV2 = this.adjVertices.get(label2);
        if (eV1 != null) {
            eV1.remove(label1);
        }

        if (eV2 != null) {
            eV2.remove(label1);
        }

    }


    List<TreeVertex> getAdjVertices(TreeVertex label) {
        return this.adjVertices.get(label);
    }


    static Set<TreeVertex> depthFirstTraversal(DFSTree graph, TreeVertex root) {
        Set<TreeVertex> visited = new LinkedHashSet<>();
        Stack<TreeVertex> stack = new Stack<>();
        stack.push(root);

        while (true) {
            TreeVertex vertex;
            do {
                if (stack.isEmpty()) {
                    return visited;
                }
                vertex = stack.pop();
            } while (visited.contains(vertex));

            visited.add(vertex);
            Iterator<TreeVertex> var5 = graph.getAdjVertices(vertex).iterator();

            while (var5.hasNext()) {
                TreeVertex v = var5.next();
                stack.push(v);
            }
        }
    }


    static Set<TreeVertex> breadthFirstTraversal(DFSTree graph, TreeVertex root) {
        Set<TreeVertex> visited = new LinkedHashSet();
        Queue<TreeVertex> queue = new LinkedList();
        queue.add(root);
        visited.add(root);

        while (!queue.isEmpty()) {
            TreeVertex vertex = queue.poll();
            Iterator var5 = graph.getAdjVertices(vertex).iterator();

            while (var5.hasNext()) {
                TreeVertex v = (TreeVertex) var5.next();
                if (!visited.contains(v)) {
                    visited.add(v);
                    queue.add(v);
                }
            }
        }

        return visited;
    }


}

































