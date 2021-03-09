import org.antlr.v4.runtime.misc.Pair;

import java.util.*;

public class Rectangulator<E> {

    Set<PlanarGraphFace<TreeVertex, E>> planarGraphFaces = new HashSet<PlanarGraphFace<TreeVertex, E>>();

    HashMap<Pair<TreeVertex, TreeVertex>, Pair<TreeVertex, TreeVertex>> frontMap = new HashMap<>();


    public Rectangulator(Set<PlanarGraphFace<TreeVertex, E>> planarGraphFaces) {
        this.planarGraphFaces = planarGraphFaces;
    }

    public Pair<TreeVertex, TreeVertex> next(Pair<TreeVertex, TreeVertex> edge) {


        return edge;
    }

    public TreeVertex corner(Pair<TreeVertex, TreeVertex> edge) {

        return edge.b;
    }

    public Pair<TreeVertex, TreeVertex> extend(Pair<TreeVertex, TreeVertex> edge) {


        return edge;
    }


    //TODO eine Methode die zum Beispie für front(e)=(7,8) returned (r1,8), dann (r2,r1) und dann (r3,r2)
    //TODO ein paar so modifizieren, dass aus (7,8), dann wird daraus 7,r1) und dann neues pair mit (r1, r8)
    public Pair<TreeVertex, TreeVertex> front(Pair<TreeVertex, TreeVertex> edge, PlanarGraphFace<TreeVertex, E> planargraphFace) {
        Map<Pair<TreeVertex, TreeVertex>, Integer> map = planargraphFace.getOrthogonalRep();
        Set<Pair<TreeVertex, TreeVertex>> edgeSet = map.keySet();


        return edge;
    }


    public void initialize() {

        for (PlanarGraphFace<TreeVertex, E> face :
                planarGraphFaces) {

            Map<Pair<TreeVertex, TreeVertex>, Integer> orthogonalRep = face.getOrthogonalRep();

            Map<Pair<TreeVertex, TreeVertex>, Pair<TreeVertex, TreeVertex>> nexts = new LinkedHashMap<>();
            Map<Pair<TreeVertex, TreeVertex>, Pair<TreeVertex, TreeVertex>> fronts = new LinkedHashMap<>();

            computeNexts(face.getOrthogonalRep(), nexts);

            if (!face.getName().equals("0")) {
                computeFronts(orthogonalRep, fronts, nexts);


                for (Pair<TreeVertex, TreeVertex> edge: fronts.values()
                     ) {
                    projectEdge(edge, nexts, fronts);
                }


            }



            System.out.println("Test");
        }

    }

    private void computeFronts(Map<Pair<TreeVertex, TreeVertex>, Integer> orthogonalRep, Map<Pair<TreeVertex, TreeVertex>, Pair<TreeVertex, TreeVertex>> fronts, Map<Pair<TreeVertex, TreeVertex>, Pair<TreeVertex, TreeVertex>> nexts) {

        for (Pair<TreeVertex, TreeVertex> edge : orthogonalRep.keySet()
        ) {
            if (orthogonalRep.get(edge) == -1) {
                findFront(edge, fronts, orthogonalRep, nexts);
            }

        }
    }


    private void findFront(Pair<TreeVertex, TreeVertex> edge, Map<Pair<TreeVertex, TreeVertex>, Pair<TreeVertex, TreeVertex>> fronts, Map<Pair<TreeVertex, TreeVertex>, Integer> orthogonalRep, Map<Pair<TreeVertex, TreeVertex>, Pair<TreeVertex, TreeVertex>> nexts) {

        int counter = orthogonalRep.get(edge);

        Pair<TreeVertex, TreeVertex> tempEdge = edge;
        while (counter != 1) {
            tempEdge = nexts.get(tempEdge);
            counter += orthogonalRep.get(tempEdge);



        }
        fronts.put(edge,  nexts.get(tempEdge));



    }

    private void projectEdge(Pair<TreeVertex, TreeVertex> front, Map<Pair<TreeVertex, TreeVertex>, Pair<TreeVertex, TreeVertex>> nexts, Map<Pair<TreeVertex, TreeVertex>, Pair<TreeVertex, TreeVertex>> fronts) {



        Pair<TreeVertex, TreeVertex> possibleEdge = nexts.get(front);
        Pair<TreeVertex, TreeVertex> beforeTempEdge = possibleEdge;

        while (possibleEdge != front) {

            //TODO Hier noch viel Mist morgen genau überlegen was zu tun ist. Vielleicht alle Kanten Sammeln die die gleiche front haben, Dann deren Reihenfolge feststellen? Dann die neuen Zyklen aufbauen?

            if (front == fronts.get(possibleEdge)) {
                Pair<TreeVertex, TreeVertex> newEdge = new Pair<>(new TreeVertex(front.a.getName() + front.b.getName() + " R"), front.b);
                Pair<TreeVertex, TreeVertex> followingEdge = nexts.get(possibleEdge);

                nexts.put(beforeTempEdge, newEdge);
                nexts.put(newEdge, beforeTempEdge);




                System.out.println("Test");
            }

            beforeTempEdge = possibleEdge;
            possibleEdge = nexts.get(possibleEdge);
        }




    }


    public void computeFronts(Pair<TreeVertex, TreeVertex> edge, Map<Pair<TreeVertex, TreeVertex>, Pair<TreeVertex, TreeVertex>> fronts) {
        for (Map.Entry<Pair<TreeVertex, TreeVertex>, Pair<TreeVertex, TreeVertex>> entry1 : fronts.entrySet()
        ) {


        }
    }

    public void computeNexts(Map<Pair<TreeVertex, TreeVertex>, Integer> orthogonalRep, Map<Pair<TreeVertex, TreeVertex>, Pair<TreeVertex, TreeVertex>> nexts) {

        ArrayList<Pair<TreeVertex, TreeVertex>> arrList = new ArrayList<>(orthogonalRep.keySet());


        for (int i = 0; i < arrList.size(); i++) {

            nexts.put(arrList.get(i), arrList.get((i + 1) % arrList.size()));

        }


    }


}
