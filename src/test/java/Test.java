import org.antlr.v4.runtime.misc.Pair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.jbpt.algo.tree.tctree.TCTree;
import org.jbpt.hypergraph.abs.Vertex;

import java.lang.reflect.Array;
import java.util.*;


public class Test {


    public static <T> T[] pickSample(T[] population, int nSamplesNeeded, Random r) {
        T[] ret = (T[]) Array.newInstance(population.getClass().getComponentType(),
                nSamplesNeeded);
        int nPicked = 0, i = 0, nLeft = population.length;
        while (nSamplesNeeded > 0) {
            int rand = r.nextInt(nLeft);
            if (rand < nSamplesNeeded) {
                ret[nPicked++] = population[i];
                nSamplesNeeded--;
            }
            nLeft--;
            i++;
        }
        return ret;
    }

    public static <T> List<T> pickSample(ArrayList<T> population, int nSamplesNeeded, Random r) {
        List<T> ret = new ArrayList<T>();
        int nPicked = 0, i = 0, nLeft = population.size();
        while (nSamplesNeeded > 0) {
            int rand = r.nextInt(nLeft);
            if (rand < nSamplesNeeded) {
                ret.add(population.get(i));
                nSamplesNeeded--;
            }
            nLeft--;
            i++;
        }
        return ret;
    }

    @org.junit.jupiter.api.Test
    public void mutableTest() {

        MutablePair<Integer, Integer> test1 = new MutablePair<>(31, 2);
        MutablePair<Integer, Integer> test2 = new MutablePair<>(2, 31);
        MutablePair<Integer, Integer> test2One = new MutablePair<>(2, 31);

        int one = test1.hashCode();
        int two = test2.hashCode();
        test2One.setRight(44);


        int[] testArr1 = {2, 1};
        int[] testArr2 = {1, 2};
        int[] testArr3 = {1, 2};

        int one1 = testArr1.hashCode();
        int two1 = testArr2.hashCode();
        int two2 = testArr3.hashCode();


        Pair<Integer, Integer> test12 = new Pair<>(1, 2);
        Pair<Integer, Integer> test22 = new Pair<>(2, 1);
        Pair<Integer, Integer> test32 = new Pair<>(2, 1);

        int one2 = test12.hashCode();
        int two3 = test22.hashCode();
        int two4 = test32.hashCode();


        AbstractMap.SimpleEntry<Integer, Integer> pair1 = new AbstractMap.SimpleEntry<>(1, 2);
        AbstractMap.SimpleEntry<Integer, Integer> pair2 = new AbstractMap.SimpleEntry<>(2, 1);
        AbstractMap.SimpleEntry<Integer, Integer> pair3 = new AbstractMap.SimpleEntry<>(2, 1);

        int pairV1 = pair1.hashCode();
        int pairV2 = pair2.hashCode();
        int pairV3 = pair2.hashCode();

        pair3.setValue(2);
        int pairV4 = pair3.hashCode();


        MutablePair<Integer, Integer> test6 = new TupleEdge<>(31, 2);
        MutablePair<Integer, Integer> test7 = new TupleEdge<>(2, 31);

        int oneR = test6.hashCode();
        int twoR = test7.hashCode();

    }

    @org.junit.jupiter.api.Test
    public void jbptTest() {
        org.jbpt.graph.Graph test = new org.jbpt.graph.Graph();
        Vertex vertex1 = new Vertex("1");
        Vertex vertex2 = new Vertex("2");
        Vertex vertex3 = new Vertex("3");
        Vertex vertex4 = new Vertex("4");
        test.addEdge(vertex1, vertex2);
        test.addEdge(vertex2, vertex3);
        test.addEdge(vertex3, vertex4);
        test.addEdge(vertex4, vertex1);
        test.addEdge(vertex3, vertex1);

        System.out.println(test.toDOT());

        org.jbpt.algo.tree.tctree.TCTree spqr = new TCTree(test);
        System.out.println(spqr.toString());

        System.out.println("Test");
        System.out.println(spqr.getTCTreeNodes());
        System.out.println(spqr.toDOT());
        System.out.println(spqr.getGraph().toDOT());

    }

    @org.junit.jupiter.api.Test
    public void randomTest() {

        Integer[] ints = {1, 2, 3};

        Integer[] test = pickSample(ints, 2, new Random());

        ArrayList<String> stringList = new ArrayList<>();
        stringList.add("a");
        stringList.add("b");
        stringList.add("c");
        List<String> newList = pickSample(stringList, 2, new Random());


    }

    @org.junit.jupiter.api.Test
    public void edgeMapTest() {

        HashMap<Pair<String, String>, Integer> pairIntegerHashMap = new HashMap<>();

        Pair<String, String> stringStringPair = new Pair<>("a", "b");

        pairIntegerHashMap.put(stringStringPair, 1);
        pairIntegerHashMap.get(new Pair<String, String>("a", "b"));


    }










}




