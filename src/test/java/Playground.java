import org.antlr.v4.runtime.tree.Tree;
import org.jbpt.algo.tree.tctree.EdgeList;
import org.jbpt.algo.tree.tctree.TCTree;
import org.jbpt.graph.Edge;
import org.jbpt.graph.MultiGraph;
import org.jbpt.hypergraph.abs.Vertex;
import org.jgrapht.*;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.generate.PruferTreeGenerator;
import org.jgrapht.graph.AsGraphUnion;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.graph.Multigraph;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;

import java.io.StringWriter;
import java.io.Writer;
import java.util.*;
import java.util.function.Supplier;

import org.jgrapht.util.SupplierUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class Playground {


    @Test
    public void nodeMerge() {


        Supplier<String> vSupplier = new Supplier<String>() {
            private int id = 0;

            @Override
            public String get() {

                return "v" + id++;
            }
        };


        Graph<String, DefaultEdge> nodeMergeTestGraph = new DefaultUndirectedGraph<String, DefaultEdge>(vSupplier, SupplierUtil.createDefaultEdgeSupplier(), false);

        nodeMergeTestGraph.addVertex("a");
        nodeMergeTestGraph.addVertex("b");
        nodeMergeTestGraph.addEdge("a", "b");


        Graph<String, DefaultEdge> nodeMergeTestGraph2 = new DefaultUndirectedGraph<String, DefaultEdge>(vSupplier, SupplierUtil.createDefaultEdgeSupplier(), false);

        nodeMergeTestGraph2.addVertex("c");
        nodeMergeTestGraph2.addVertex("d");
        nodeMergeTestGraph2.addVertex("e");
        nodeMergeTestGraph2.addEdge("c", "d");
        nodeMergeTestGraph2.addEdge("d", "e");

        // Beispiel einer P operation in einem Graphen
        replaceVertex(nodeMergeTestGraph2, "c", "a");
        replaceVertex(nodeMergeTestGraph2, "e", "b");




        AsGraphUnion<String, DefaultEdge> union = new AsGraphUnion<>(nodeMergeTestGraph, nodeMergeTestGraph2);


/*            Angedacht: public static <V, E> void replaceVertex(Graph<V, E> graph, V Svertex, V Sreplace, V Tvertex, V Treplace){

https://stackoverflow.com/questions/8766741/changing-contents-of-vertex-with-jgrapht
        public static <V, E> void replaceVertex(Graph<V, E> graph, V vertex, V replace) {
            graph.addVertex(replace);
            for (E edge : graph.outgoingEdgesOf(vertex)) graph.addEdge(replace, graph.getEdgeTarget(edge), edge);
            for (E edge : graph.incomingEdgesOf(vertex)) graph.addEdge(graph.getEdgeSource(edge), replace, edge);
            graph.removeVertex(vertex);
            }
  */

    }






    // Methoden hinzfügen: coonectGraphs: verbindet 2 Graphen dadurch, dass man knoten1 von Graph1 mit Knoten2 von Graph2 verbindet
    //                     dann noch 2 stMergeP und stMergeS, bei der jeweils replaceVertex genutzt wird um start und endkoten entsprechend zu verbinden und dann einen neuen Graphen zu erzeigen



    public static <V,E> void replaceVertex(Graph<V, E> graph, V vertex, V replace) {
        graph.addVertex(replace);



       Set<E> edgeSet1 =  new HashSet<E>(graph.incomingEdgesOf(vertex));
       Set<E> outgoingEdges = new HashSet<E>(graph.outgoingEdgesOf(vertex));


        for (E edge : edgeSet1) {
            if (vertex == graph.getEdgeTarget(edge)) {
                graph.addEdge(graph.getEdgeSource(edge), replace);
            }
            else if (vertex == graph.getEdgeSource(edge)) {

                graph.addEdge(replace, graph.getEdgeTarget(edge));

            }

        }

        graph.removeVertex(vertex);
    }


    @Test
    public void graphPMerge(){


        stGraph test1 = new stGraph(DefaultEdge.class);
        stGraph test2 = new stGraph(DefaultEdge.class);


        test1.addVertex(new TreeVertex("A1"));
        test1.addVertex(new TreeVertex("A2"));
        test1.addVertex(new TreeVertex("A3"));


        Iterator<TreeVertex> test1Iterator = test1.vertexSet().stream().iterator();
        TreeVertex startA = test1Iterator.next();
        test1Iterator.next();
        TreeVertex endA = test1Iterator.next();

        test1.addEdge(test1.findVertex("A1"), test1.findVertex("A2"));
        test1.addEdge(test1.findVertex("A2"), test1.findVertex("A3"));


        test2.addVertex(new TreeVertex("B1"));
        test2.addVertex(new TreeVertex("B2"));


        Iterator<TreeVertex> test2Iterator = test2.vertexSet().stream().iterator();
        TreeVertex startB = test2Iterator.next();
        TreeVertex endB = test2Iterator.next();
        test2.addEdge(startB, endB);

        test1.setsNode(startA);
        test1.settNode(endA);
        test2.setsNode(startB);
        test2.settNode(endB);



    test1.mergeGraphsPnode(test2);


    }

    @Test
    public void graphSMerge(){



        Supplier<String> vSupplier = new Supplier<String>() {
            private int id = 0;

            @Override
            public String get() {

                return "v" + id++;
            }
        };







        stGraph test1 = new stGraph(DefaultEdge.class);
        stGraph test2 = new stGraph(DefaultEdge.class);


        test1.addVertex(new TreeVertex("A1"));
        test1.addVertex(new TreeVertex("A2"));
        test1.addVertex(new TreeVertex("A3"));


        Iterator<TreeVertex> test1Iterator = test1.vertexSet().stream().iterator();
        TreeVertex startA = test1Iterator.next();
        test1Iterator.next();
        TreeVertex endA = test1Iterator.next();

        test1.addEdge(test1.findVertex("A1"), test1.findVertex("A2"));
        test1.addEdge(test1.findVertex("A2"), test1.findVertex("A3"));


        test2.addVertex(new TreeVertex("B1"));
        test2.addVertex(new TreeVertex("B2"));


        Iterator<TreeVertex> test2Iterator = test2.vertexSet().stream().iterator();
        TreeVertex startB = test2Iterator.next();
        TreeVertex endB = test2Iterator.next();
         test2.addEdge(startB, endB);
        test1.getEdge(startB, endB);

        test1.setsNode(startA);
        test1.settNode(endA);
        test2.setsNode(startB);
        test2.settNode(endB);



        test1.mergeGraphsSnode(test2);


        GraphHelper.addVerticesToEdge(test1, test1.getEdge(startB, endB), 3);


    }

    @Test
    public void addVerticesToEdge() {


        stGraph test1 = new stGraph(TreeVertex.getvSupplier, SupplierUtil.createDefaultEdgeSupplier(), false);
        stGraph test2 = new stGraph(TreeVertex.getvSupplier, SupplierUtil.createDefaultEdgeSupplier(), false);


        test1.addVertex(new TreeVertex("A1"));
        test1.addVertex(new TreeVertex("A2"));
        test1.addVertex(new TreeVertex("A3"));


        Iterator<TreeVertex> test1Iterator = test1.vertexSet().stream().iterator();
        TreeVertex startA = test1Iterator.next();

        TreeVertex endA = test1Iterator.next();

        test1.addEdge(test1.findVertex("A1"), test1.findVertex("A2"));
        test1.addEdge(test1.findVertex("A2"), test1.findVertex("A3"));


        test2.addVertex(new TreeVertex("B1"));
        test2.addVertex(new TreeVertex("B2"));


        Iterator<TreeVertex> test2Iterator = test2.vertexSet().stream().iterator();
        TreeVertex startB = test2Iterator.next();
        TreeVertex endB = test2Iterator.next();

        test2.addEdge(startB, endB);



        GraphHelper.addVerticesToEdge(test1, test1.getEdge(startA, endA), 3);

    }



}
