import org.jgrapht.Graph;
import org.jgrapht.Graphs;

import java.util.HashSet;
import java.util.Set;

public class KettenComponent<V,E> {

    V start;
    V end;
    Graph<V,E> graph;
    int startNodeDegree = 0;
    int endNodeDegree = 0;


    public KettenComponent(V start, V end) {
        this.start = start;
        this.end = end;

    }
    public KettenComponent(Graph<V,E> graph, V start, V end) {
        this.start = start;
        this.end = end;
        this.graph = graph;
    }



    public V getStart() {
        startNodeDegree = graph.degreeOf(start);
        return start;

    }


    public void setStart(V start) {
        this.start = start;
        startNodeDegree = graph.degreeOf(start);



    }

    public V getEnd() {
        endNodeDegree = graph.degreeOf(end);


        return end;

    }

    public void setEnd(V end) {

        endNodeDegree = graph.degreeOf(end);
        this.end = end;
    }

    public Graph<V, E> getGraph() {
        return graph;
    }

    public void setGraph(Graph<V, E> graph) {
        this.graph = graph;
    }

    public int getStartNodeDegree() {
        return startNodeDegree;
    }

    public void setStartNodeDegree(int startNodeDegree) {
        this.startNodeDegree = startNodeDegree;
    }

    public int getEndNodeDegree() {
        return endNodeDegree;
    }

    public void setEndNodeDegree(int endNodeDegree) {
        this.endNodeDegree = endNodeDegree;
    }

    public void updateDegrees() {
        startNodeDegree = graph.degreeOf(start);
        endNodeDegree = graph.degreeOf(end);
    }
}
