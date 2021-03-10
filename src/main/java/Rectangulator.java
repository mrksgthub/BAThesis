
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.*;

public class Rectangulator<E> {

    Set<PlanarGraphFace<TreeVertex, E>> planarGraphFaces = new HashSet<PlanarGraphFace<TreeVertex, E>>();

    HashMap<MutablePair<TreeVertex, TreeVertex>, MutablePair<TreeVertex, TreeVertex>> frontMap = new HashMap<>();


    public Rectangulator(Set<PlanarGraphFace<TreeVertex, E>> planarGraphFaces) {
        this.planarGraphFaces = planarGraphFaces;
    }

    public MutablePair<TreeVertex, TreeVertex> next(MutablePair<TreeVertex, TreeVertex> edge) {


        return edge;
    }

    public TreeVertex corner(MutablePair<TreeVertex, TreeVertex> edge) {

        return edge.getRight();
    }

    public MutablePair<TreeVertex, TreeVertex> extend(MutablePair<TreeVertex, TreeVertex> edge) {


        return edge;
    }


    //TODO eine Methode die zum Beispie für front(e)=(7,8) returned (r1,8), dann (r2,r1) und dann (r3,r2)
    //TODO ein paar so modifizieren, dass aus (7,8), dann wird daraus 7,r1) und dann neues pair mit (r1, r8)
    public MutablePair<TreeVertex, TreeVertex> front(MutablePair<TreeVertex, TreeVertex> edge, PlanarGraphFace<TreeVertex, E> planargraphFace) {
        Map<MutablePair<TreeVertex, TreeVertex>, Integer> map = planargraphFace.getOrthogonalRep();
        Set<MutablePair<TreeVertex, TreeVertex>> edgeSet = map.keySet();


        return edge;
    }


    public void initialize() {

        for (PlanarGraphFace<TreeVertex, E> face :
                planarGraphFaces) {

            Map<MutablePair<TreeVertex, TreeVertex>, Integer> orthogonalRep = face.getOrthogonalRep();

            Map<MutablePair<TreeVertex, TreeVertex>, MutablePair<TreeVertex, TreeVertex>> nexts = new LinkedHashMap<>();
            Map<MutablePair<TreeVertex, TreeVertex>, MutablePair<TreeVertex, TreeVertex>> fronts = new LinkedHashMap<>();

            computeNexts(face.getOrthogonalRep(), nexts);


            Map<MutablePair<TreeVertex, TreeVertex>, Integer> newOrthogonalRep = new LinkedHashMap<>();
            newOrthogonalRep.putAll(face.getOrthogonalRep());








            if (!face.getName().equals("0")) {
                computeFronts(orthogonalRep, fronts, nexts);


                for (MutablePair<TreeVertex, TreeVertex> edge: fronts.values()
                     ) {
                    projectEdge(edge, nexts, fronts, newOrthogonalRep);
                }


            }

            HashMap<MutablePair<TreeVertex, TreeVertex>, Boolean> visitedMap = new HashMap<>();
            for (MutablePair<TreeVertex, TreeVertex> pair:
                    nexts.keySet()
                 ) {
                visitedMap.put(pair, false);
            }
            for (MutablePair<TreeVertex, TreeVertex> pair:
                    visitedMap.keySet()
            ) {
                if (!visitedMap.get(pair)) {
                    int counter = newOrthogonalRep.get(pair);
                    MutablePair<TreeVertex, TreeVertex> iterator = nexts.get(pair);

                    visitedMap.put(pair, true);
                    visitedMap.put(iterator, true);
                    while (pair != iterator) {
                        visitedMap.put(iterator, true);
                        counter += newOrthogonalRep.get(iterator);
                        iterator = nexts.get(iterator);

                    }
                    assert (Math.abs(counter) == 4);
                }





            }




            System.out.println("Test");
        }

    }

    private void computeFronts(Map<MutablePair<TreeVertex, TreeVertex>, Integer> orthogonalRep, Map<MutablePair<TreeVertex, TreeVertex>, MutablePair<TreeVertex, TreeVertex>> fronts, Map<MutablePair<TreeVertex, TreeVertex>, MutablePair<TreeVertex, TreeVertex>> nexts) {

        for (MutablePair<TreeVertex, TreeVertex> edge : orthogonalRep.keySet()
        ) {
            if (orthogonalRep.get(edge) == -1) {
                findFront(edge, fronts, orthogonalRep, nexts);
            }

        }
    }


    private void findFront(MutablePair<TreeVertex, TreeVertex> edge, Map<MutablePair<TreeVertex, TreeVertex>, MutablePair<TreeVertex, TreeVertex>> fronts, Map<MutablePair<TreeVertex, TreeVertex>, Integer> orthogonalRep, Map<MutablePair<TreeVertex, TreeVertex>, MutablePair<TreeVertex, TreeVertex>> nexts) {

        int counter = orthogonalRep.get(edge);

        MutablePair<TreeVertex, TreeVertex> tempEdge = edge;
        while (counter != 1) {
            tempEdge = nexts.get(tempEdge);
            counter += orthogonalRep.get(tempEdge);



        }
        fronts.put(edge,  nexts.get(tempEdge));



    }

    private void projectEdge(MutablePair<TreeVertex, TreeVertex> front, Map<MutablePair<TreeVertex, TreeVertex>, MutablePair<TreeVertex, TreeVertex>> nexts, Map<MutablePair<TreeVertex, TreeVertex>, MutablePair<TreeVertex, TreeVertex>> fronts, Map<MutablePair<TreeVertex, TreeVertex>, Integer> newOrthogonalRep) {





        MutablePair<TreeVertex, TreeVertex> possibleEdge = nexts.get(front);
        MutablePair<TreeVertex, TreeVertex> beforeTempEdge = possibleEdge;
        List<MutablePair<TreeVertex, TreeVertex>> edgeList = new ArrayList<>();



        while (possibleEdge != front) {


            edgeList.add(possibleEdge);
            //TODO Hier noch viel Mist morgen genau überlegen was zu tun ist. Vielleicht alle Kanten Sammeln die die gleiche front haben, Dann deren Reihenfolge feststellen? Dann die neuen Zyklen aufbauen?

            if (front == fronts.get(possibleEdge)) {
                TreeVertex newVertex = new TreeVertex(front.getLeft().getName() + possibleEdge.getRight().getName() + " R");
                MutablePair<TreeVertex, TreeVertex> newEdge = new MutablePair<>(possibleEdge.getRight(), newVertex);

                edgeList.add(newEdge);
                MutablePair<TreeVertex, TreeVertex> newEdge2 = new MutablePair<>(newVertex, edgeList.get(0).getLeft());
                edgeList.add(newEdge2);


                newOrthogonalRep.put(possibleEdge, 0);
                newOrthogonalRep.put(newEdge, 1);
                // newOrthogonalRep.put(newEdge2, 1);

                front.setRight(newVertex);

                MutablePair<TreeVertex, TreeVertex> followingEdge = nexts.get(possibleEdge);

                // Update nexts of new Face
                nexts.put(possibleEdge, newEdge);
                nexts.put(newEdge, newEdge2);
                nexts.put(newEdge2, edgeList.get(0));

                //Update the "rest" of the face


                MutablePair<TreeVertex, TreeVertex> newEdgeReverse = new MutablePair<>(front.getRight(), possibleEdge.getRight());
                newOrthogonalRep.put(newEdgeReverse, 1);
                newOrthogonalRep.put(front, 1);
                nexts.put(front, newEdgeReverse);
                nexts.put(newEdgeReverse, followingEdge);


                possibleEdge = front;
                System.out.println("Test");
                edgeList = new ArrayList<>();
            }



            beforeTempEdge = possibleEdge;
            possibleEdge = nexts.get(possibleEdge);
        }




    }


    public void computeFronts(MutablePair<TreeVertex, TreeVertex> edge, Map<MutablePair<TreeVertex, TreeVertex>, MutablePair<TreeVertex, TreeVertex>> fronts) {
        for (Map.Entry<MutablePair<TreeVertex, TreeVertex>, MutablePair<TreeVertex, TreeVertex>> entry1 : fronts.entrySet()
        ) {


        }
    }

    public void computeNexts(Map<MutablePair<TreeVertex, TreeVertex>, Integer> orthogonalRep, Map<MutablePair<TreeVertex, TreeVertex>, MutablePair<TreeVertex, TreeVertex>> nexts) {

        ArrayList<MutablePair<TreeVertex, TreeVertex>> arrList = new ArrayList<>(orthogonalRep.keySet());


        for (int i = 0; i < arrList.size(); i++) {

            nexts.put(arrList.get(i), arrList.get((i + 1) % arrList.size()));

        }


    }


}
