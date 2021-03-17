
import org.apache.commons.lang3.tuple.MutablePair;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;

public class Rectangulator<E> {

    Set<PlanarGraphFace<TreeVertex, E>> planarGraphFaces = new HashSet<PlanarGraphFace<TreeVertex, E>>();

    HashMap<MutablePair<TreeVertex, TreeVertex>, MutablePair<TreeVertex, TreeVertex>> frontMap = new HashMap<>();
    HashMap<PlanarGraphFace<TreeVertex, E>, PlanarGraphFace<TreeVertex, E>> rectangularFaceMap = new LinkedHashMap<>();
    HashMap<MutablePair<TreeVertex, TreeVertex>, PlanarGraphFace<TreeVertex, E>> edgeFaceNeighbourMap = new HashMap<>();
    Map<MutablePair<TreeVertex, TreeVertex>, Integer> angleMap = new LinkedHashMap<>();
    HashMap<MutablePair<TreeVertex, TreeVertex>, PlanarGraphFace<TreeVertex, DefaultEdge>> originaledgeToFaceMap = new LinkedHashMap<>();
    PlanarGraphFace<TreeVertex, DefaultEdge> outerFace = new PlanarGraphFace<>("externalFace");

    public Set<PlanarGraphFace<TreeVertex, E>> getPlanarGraphFaces() {
        return planarGraphFaces;
    }

    public void setPlanarGraphFaces(Set<PlanarGraphFace<TreeVertex, E>> planarGraphFaces) {
        this.planarGraphFaces = planarGraphFaces;
    }

    public HashMap<MutablePair<TreeVertex, TreeVertex>, MutablePair<TreeVertex, TreeVertex>> getFrontMap() {
        return frontMap;
    }

    public void setFrontMap(HashMap<MutablePair<TreeVertex, TreeVertex>, MutablePair<TreeVertex, TreeVertex>> frontMap) {
        this.frontMap = frontMap;
    }

    public HashMap<PlanarGraphFace<TreeVertex, E>, PlanarGraphFace<TreeVertex, E>> getRectangularFaceMap() {
        return rectangularFaceMap;
    }

    public void setRectangularFaceMap(HashMap<PlanarGraphFace<TreeVertex, E>, PlanarGraphFace<TreeVertex, E>> rectangularFaceMap) {
        this.rectangularFaceMap = rectangularFaceMap;
    }

    public HashMap<MutablePair<TreeVertex, TreeVertex>, PlanarGraphFace<TreeVertex, E>> getEdgeFaceNeighbourMap() {
        return edgeFaceNeighbourMap;
    }

    public void setEdgeFaceNeighbourMap(HashMap<MutablePair<TreeVertex, TreeVertex>, PlanarGraphFace<TreeVertex, E>> edgeFaceNeighbourMap) {
        this.edgeFaceNeighbourMap = edgeFaceNeighbourMap;
    }

    public Map<MutablePair<TreeVertex, TreeVertex>, Integer> getAngleMap() {
        return angleMap;
    }

    public void setAngleMap(Map<MutablePair<TreeVertex, TreeVertex>, Integer> angleMap) {
        this.angleMap = angleMap;
    }

    public HashMap<MutablePair<TreeVertex, TreeVertex>, PlanarGraphFace<TreeVertex, DefaultEdge>> getOriginaledgeToFaceMap() {
        return originaledgeToFaceMap;
    }

    public void setOriginaledgeToFaceMap(HashMap<MutablePair<TreeVertex, TreeVertex>, PlanarGraphFace<TreeVertex, DefaultEdge>> originaledgeToFaceMap) {
        this.originaledgeToFaceMap = originaledgeToFaceMap;
    }

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
        List<PlanarGraphFace<TreeVertex, E>> faceList = new ArrayList<>(planarGraphFaces);
        Deque<PlanarGraphFace<TreeVertex, E>> dequeStack = new ArrayDeque<>(planarGraphFaces);
        PlanarGraphFace<TreeVertex, E> face = dequeStack.pop();
        ;

        while (dequeStack.size() > 0) {

            face.setOrientations();
            Map<MutablePair<TreeVertex, TreeVertex>, Integer> orthogonalRep = face.getOrthogonalRep();

            Map<MutablePair<TreeVertex, TreeVertex>, MutablePair<TreeVertex, TreeVertex>> nexts = new LinkedHashMap<>();
            Map<MutablePair<TreeVertex, TreeVertex>, MutablePair<TreeVertex, TreeVertex>> prevs = new LinkedHashMap<>();
            Map<MutablePair<TreeVertex, TreeVertex>, MutablePair<TreeVertex, TreeVertex>> fronts = new LinkedHashMap<>();
            Map<TreeVertex, MutablePair<TreeVertex, TreeVertex>> vertexToFront = new LinkedHashMap<>();
            Map<MutablePair<TreeVertex, TreeVertex>, MutablePair<TreeVertex, TreeVertex>> externalFronts = new LinkedHashMap<>();
            computeNexts(face.edgeList, nexts, prevs);


            Map<MutablePair<TreeVertex, TreeVertex>, Integer> newOrthogonalRep = new LinkedHashMap<>();
            newOrthogonalRep.putAll(face.getOrthogonalRep());


            if (face.getType() == PlanarGraphFace.FaceType.INTERAL) {
                computeFronts(orthogonalRep, fronts, nexts, prevs, face.edgeList, vertexToFront);


                List<MutablePair<TreeVertex, TreeVertex>> frontList = new ArrayList<>(fronts.values());
                for (MutablePair<TreeVertex, TreeVertex> edge : frontList
                ) {
                    projectEdge(edge, nexts, fronts, newOrthogonalRep, face, vertexToFront);
                }


            } else {

                if (!face.getName().equals("0")) {

                    // new outer Rectangle
                    TreeVertex v1 = new TreeVertex("outer1");
                    TreeVertex v2 = new TreeVertex("outer2");
                    TreeVertex v3 = new TreeVertex("outer3");
                    TreeVertex v4 = new TreeVertex("outer4");
                    MutablePair<TreeVertex, TreeVertex> edge1 = new MutablePair<>(v1, v2);
                    MutablePair<TreeVertex, TreeVertex> edge2 = new MutablePair<>(v2, v3);
                    MutablePair<TreeVertex, TreeVertex> edge3 = new MutablePair<>(v3, v4);
                    MutablePair<TreeVertex, TreeVertex> edge4 = new MutablePair<>(v4, v1);
                    MutablePair[] outerRectangle = new MutablePair[]{edge1, edge2, edge3, edge4};
                    nexts.put(edge1, edge2);
                    nexts.put(edge2, edge3);
                    nexts.put(edge3, edge4);
                    nexts.put(edge4, edge1);
                    face.getEdgeOrientationMap().put(edge1, 0);
                    face.getEdgeOrientationMap().put(edge2, 1);
                    face.getEdgeOrientationMap().put(edge3, 2);
                    face.getEdgeOrientationMap().put(edge4, 3);
                    outerFace.getEdgeOrientationMap().put(GraphHelper.reverseEdge(edge1), 0);
                    outerFace.getEdgeOrientationMap().put(GraphHelper.reverseEdge(edge2), 1);
                    outerFace.getEdgeOrientationMap().put(GraphHelper.reverseEdge(edge3), 2);
                    outerFace.getEdgeOrientationMap().put(GraphHelper.reverseEdge(edge4), 3);
                    outerFace.getEdgeList().add(GraphHelper.reverseEdge(edge1));
                    outerFace.getEdgeList().add(GraphHelper.reverseEdge(edge2));
                    outerFace.getEdgeList().add(GraphHelper.reverseEdge(edge3));
                    outerFace.getEdgeList().add(GraphHelper.reverseEdge(edge4));
                    outerFace.getOrthogonalRep().put(GraphHelper.reverseEdge(edge1), -1);
                    outerFace.getOrthogonalRep().put(GraphHelper.reverseEdge(edge2), -1);
                    outerFace.getOrthogonalRep().put(GraphHelper.reverseEdge(edge3), -1);
                    outerFace.getOrthogonalRep().put(GraphHelper.reverseEdge(edge4), -1);

                    for (MutablePair<TreeVertex, TreeVertex> edge :
                            outerFace.getEdgeList()) {
                        originaledgeToFaceMap.put(edge, outerFace);

                    }


                    newOrthogonalRep.put(edge1, 1);
                    newOrthogonalRep.put(edge2, 1);
                    newOrthogonalRep.put(edge3, 1);
                    newOrthogonalRep.put(edge4, 1);
                    face.setOrientations();
                    computeExternalFront(orthogonalRep, fronts, externalFronts, nexts, face.getEdgeOrientationMap(), outerRectangle, face.getEdgeList());

                    List<MutablePair<TreeVertex, TreeVertex>> frontList = new ArrayList<>(fronts.values());
                    for (MutablePair<TreeVertex, TreeVertex> edge : frontList
                    ) {
                        projectEdgeExternalFace(edge, nexts, fronts, newOrthogonalRep, face, vertexToFront, externalFronts);
                    }

                    List<MutablePair<TreeVertex, TreeVertex>> edgeList = new ArrayList<>(externalFronts.values());
                    if (fronts.keySet().size() == 0) {


                        MutablePair<TreeVertex, TreeVertex> edge = edgeList.get(0);
                        projectExternalEdge(edge, nexts, externalFronts, newOrthogonalRep, face.getEdgeOrientationMap(), face.sidesMap);
                        for (int i = 1; i < edgeList.size() - 1; i++) {
                            projectEdge(edgeList.get(i), nexts, externalFronts, newOrthogonalRep, face, vertexToFront);

                        }
                    }
                } else {

                    for (MutablePair<TreeVertex, TreeVertex> edge : face.edgeList
                    ) {
                        if (orthogonalRep.get(edge) == -1) {
                            findFront(edge, fronts, orthogonalRep, nexts, vertexToFront);
                        }

                    }

                    List<MutablePair<TreeVertex, TreeVertex>> frontList = new ArrayList<>(fronts.values());
                    for (MutablePair<TreeVertex, TreeVertex> edge : frontList
                    ) {
                        projectEdgeExternalFace(edge, nexts, fronts, newOrthogonalRep, face, vertexToFront, externalFronts);
                    }


                }


            }


            HashMap<MutablePair<TreeVertex, TreeVertex>, Boolean> visitedMap = new HashMap<>();
            for (MutablePair<TreeVertex, TreeVertex> pair :
                    nexts.keySet()
            ) {
                visitedMap.put(pair, false);
            }
            for (MutablePair<TreeVertex, TreeVertex> pair :
                    visitedMap.keySet()
            ) {
                if (!visitedMap.get(pair)) {
                    PlanarGraphFace<TreeVertex, E> faceObj = new PlanarGraphFace<>();

                    rectangularFaceMap.put(faceObj, face);
                    faceObj.getOrthogonalRep().put(pair, newOrthogonalRep.get(pair));
                    faceObj.edgeList.add(pair);
                    originaledgeToFaceMap.put(pair, (PlanarGraphFace<TreeVertex, DefaultEdge>) faceObj);

                    int counter = newOrthogonalRep.get(pair);
                    MutablePair<TreeVertex, TreeVertex> iterator = nexts.get(pair);

                    visitedMap.put(pair, true);
                    while (pair != iterator) {
                        assert (!visitedMap.get(iterator));
                        visitedMap.put(iterator, true);

                        faceObj.getOrthogonalRep().put(iterator, newOrthogonalRep.get(iterator));
                        faceObj.edgeList.add(iterator);
                        counter += newOrthogonalRep.get(iterator);
                        iterator = nexts.get(iterator);
                        originaledgeToFaceMap.put(iterator, (PlanarGraphFace<TreeVertex, DefaultEdge>) faceObj);

                    }
                    assert (Math.abs(counter) == 4);
                    if (counter == -4) {
                        System.out.println("here");

                        dequeStack.push(faceObj);
                        faceObj.setType(PlanarGraphFace.FaceType.EXTERNAL);


                    }
                }

            }


            System.out.println("Test");
            face = dequeStack.pop();
        }

    }

    private void projectEdgeExternalFace(MutablePair<TreeVertex, TreeVertex> front, Map<MutablePair<TreeVertex, TreeVertex>, MutablePair<TreeVertex, TreeVertex>> nexts, Map<MutablePair<TreeVertex, TreeVertex>, MutablePair<TreeVertex, TreeVertex>> fronts, Map<MutablePair<TreeVertex, TreeVertex>, Integer> newOrthogonalRep, PlanarGraphFace<TreeVertex, E> face, Map<TreeVertex, MutablePair<TreeVertex, TreeVertex>> vertexToFront, Map<MutablePair<TreeVertex, TreeVertex>, MutablePair<TreeVertex, TreeVertex>> externalFronts) {


        MutablePair<TreeVertex, TreeVertex> possibleEdge = nexts.get(front);
        MutablePair<TreeVertex, TreeVertex> beforeTempEdge = possibleEdge;
        List<MutablePair<TreeVertex, TreeVertex>> edgeList = new ArrayList<>();
        List<MutablePair<TreeVertex, TreeVertex>> newFront = new ArrayList<>();
        List<MutablePair<TreeVertex, TreeVertex>> replacerEdge = new ArrayList<>();
        MutablePair<TreeVertex, TreeVertex> originalFront = new MutablePair<TreeVertex, TreeVertex>(front.getLeft(), front.getRight());


        while (possibleEdge != front) {

            edgeList.add(possibleEdge);

            if (front == fronts.get(possibleEdge)) {
                newFront.add(new MutablePair<TreeVertex, TreeVertex>(null, front.getRight()));
                replacerEdge.add(new MutablePair<TreeVertex, TreeVertex>(front.getRight(), null));


                TreeVertex newVertex = new TreeVertex(front.getLeft().getName() + possibleEdge.getRight().getName() + " R");
                MutablePair<TreeVertex, TreeVertex> newEdge = new MutablePair<>(possibleEdge.getRight(), newVertex);

                edgeList.add(newEdge);
                MutablePair<TreeVertex, TreeVertex> newEdge2 = new MutablePair<>(newVertex, edgeList.get(0).getLeft());
                edgeList.add(newEdge2);


                newOrthogonalRep.put(possibleEdge, 0);
                newOrthogonalRep.put(newEdge, 1);
                newOrthogonalRep.put(newEdge2, newOrthogonalRep.get(front));


                // Falls die Kante, welche eine Front hat geteilt wird, daher muss die Frontsmap geupdated werden, da sonst die front nicht getroffen wird
                if (fronts.get(front) != null) {
                    fronts.put(newEdge2, fronts.get(front));

                }
                if ((externalFronts.get(front) != null)) {
                    externalFronts.put(newEdge2, fronts.get(front));
                }


                front.setRight(newVertex);


                MutablePair<TreeVertex, TreeVertex> followingEdge = nexts.get(possibleEdge);

                // Update nexts of new Face
                nexts.put(possibleEdge, newEdge);
                nexts.put(newEdge, newEdge2);
                nexts.put(newEdge2, edgeList.get(0));

                //Update the "rest" of the face


                MutablePair<TreeVertex, TreeVertex> newEdgeReverse = new MutablePair<>(front.getRight(), possibleEdge.getRight());
                newOrthogonalRep.put(newEdgeReverse, 1);
                newOrthogonalRep.put(new MutablePair<TreeVertex, TreeVertex>(front.getLeft(), front.getRight()), 1);
                newFront.get((newFront.size() - 1)).setLeft(front.getRight());
                replacerEdge.get((newFront.size() - 1)).setRight(front.getRight());

                nexts.put(front, newEdgeReverse);
                nexts.put(newEdgeReverse, followingEdge);


                possibleEdge = front;
                System.out.println("Test");


                try {
                    for (MutablePair<TreeVertex, TreeVertex> edge :
                            edgeList
                    ) {
                        newOrthogonalRep.get(edge);
                    }
                } catch (Exception e) {
                    System.out.println("Test222");
                }

                edgeList = new ArrayList<>();
            }


            beforeTempEdge = possibleEdge;
            possibleEdge = nexts.get(possibleEdge);
        }
        newFront.add(new MutablePair<TreeVertex, TreeVertex>(front.getLeft(), front.getRight()));
        //TODO orthogonalRep der Front im Benachbarten Vertex updaten
        replacerEdge.add(new MutablePair<TreeVertex, TreeVertex>(front.getRight(), (front.getLeft())));
        MutablePair<TreeVertex, TreeVertex> originalFrontReverse = GraphHelper.reverseEdge(originalFront);
        PlanarGraphFace<TreeVertex, DefaultEdge> neighbouringFace = originaledgeToFaceMap.get(originalFrontReverse);
        assert (neighbouringFace != null);

        List<MutablePair<TreeVertex, TreeVertex>> neigbouringFaceEdgeList = neighbouringFace.getEdgeList();

        for (int i = 0; i < replacerEdge.size() - 1; i++) {
            neighbouringFace.getOrthogonalRep().put(replacerEdge.get(i), 0);
            originaledgeToFaceMap.put(replacerEdge.get(i), neighbouringFace);
        }
        neighbouringFace.getOrthogonalRep().put(replacerEdge.get(replacerEdge.size() - 1), neighbouringFace.getOrthogonalRep().get(originalFrontReverse));
        originaledgeToFaceMap.put(replacerEdge.get(replacerEdge.size() - 1), neighbouringFace);

        int pos = neigbouringFaceEdgeList.indexOf(originalFrontReverse);
        neigbouringFaceEdgeList.remove(pos);
        neigbouringFaceEdgeList.addAll(pos, replacerEdge);

    }

    private void projectExternalEdge(MutablePair<TreeVertex, TreeVertex> front, Map<MutablePair<TreeVertex, TreeVertex>, MutablePair<TreeVertex, TreeVertex>> nexts, Map<MutablePair<TreeVertex, TreeVertex>, MutablePair<TreeVertex, TreeVertex>> externalFronts, Map<MutablePair<TreeVertex, TreeVertex>, Integer> newOrthogonalRep, Map<MutablePair<TreeVertex, TreeVertex>, Integer> orientations, Map<Integer, ArrayList<MutablePair<TreeVertex, TreeVertex>>> sidesMap) {

        MutablePair<TreeVertex, TreeVertex> possibleEdge = nexts.get(front);
        MutablePair<TreeVertex, TreeVertex> beforeTempEdge = possibleEdge;
        List<MutablePair<TreeVertex, TreeVertex>> edgeList = new ArrayList<>();
        List<MutablePair<TreeVertex, TreeVertex>> newFront = new ArrayList<>();
        List<MutablePair<TreeVertex, TreeVertex>> replacerEdge = new ArrayList<>();
        MutablePair<TreeVertex, TreeVertex> originalFront = new MutablePair<TreeVertex, TreeVertex>(front.getLeft(), front.getRight());


        assert (orientations.get(front) != null);
        int rectangleEdge = orientations.get(front);
        possibleEdge = sidesMap.get((rectangleEdge + 2) % 4).get(sidesMap.get((rectangleEdge + 2) % 4).size() - 1);
        MutablePair<TreeVertex, TreeVertex> startEdge = possibleEdge;

        while (possibleEdge != front) {

            edgeList.add(possibleEdge);
            //TODO Hier noch viel Mist morgen genau überlegen was zu tun ist. Vielleicht alle Kanten Sammeln die die gleiche front haben, Dann deren Reihenfolge feststellen? Dann die neuen Zyklen aufbauen?

            if (front == externalFronts.get(possibleEdge)) {
                //


                TreeVertex newVertex = new TreeVertex(front.getLeft().getName() + possibleEdge.getRight().getName() + " R");
                // die extendede Edge im neuen Rectangle
                MutablePair<TreeVertex, TreeVertex> newEdge = new MutablePair<>(possibleEdge.getRight(), newVertex);
                // die extended Edge in reverse
                MutablePair<TreeVertex, TreeVertex> newEdgeReverse = new MutablePair<>(newVertex, possibleEdge.getRight());

                edgeList.add(newEdge);
                // der Rest der alten Front
                MutablePair<TreeVertex, TreeVertex> newEdge2 = new MutablePair<>(newVertex, front.getRight());
                edgeList.add(newEdge2);
                replacerEdge.add(new MutablePair<TreeVertex, TreeVertex>(newEdge2.getRight(), newEdge2.getLeft()));

                newOrthogonalRep.put(possibleEdge, 0);
                newOrthogonalRep.put(newEdge, 1);
                newOrthogonalRep.put(newEdge2, newOrthogonalRep.get(front));

                // Falls die Kante, welche eine Front hat geteilt wird, daher muss die Frontsmap geupdated werden, da sonst die front nicht getroffen wird
                if (externalFronts.get(front) != null) {
                    externalFronts.put(newEdge2, externalFronts.get(front));
                }

                MutablePair<TreeVertex, TreeVertex> edgeThatfollowsOldFront = nexts.get(front);
                front.setRight(newVertex);

                MutablePair<TreeVertex, TreeVertex> followingEdge = nexts.get(possibleEdge);

                // Update nexts of new Face
                nexts.put(newEdgeReverse, followingEdge);
                nexts.put(front, newEdgeReverse);

                //    nexts.put(newEdge2, edgeList.get(0));

                //Update the "rest" of the face

                nexts.put(possibleEdge, newEdge);
                nexts.put(newEdge, newEdge2);
                newOrthogonalRep.put(newEdgeReverse, 1);
                newOrthogonalRep.put(new MutablePair<TreeVertex, TreeVertex>(front.getLeft(), front.getRight()), 1);
                nexts.put(newEdge2, edgeThatfollowsOldFront);
                newFront.add(new MutablePair<TreeVertex, TreeVertex>(newEdge2.getLeft(), newEdge2.getRight()));


                possibleEdge = front;
                System.out.println("Test");


                try {
                    for (MutablePair<TreeVertex, TreeVertex> edge :
                            edgeList
                    ) {
                        newOrthogonalRep.get(edge);
                    }
                } catch (Exception e) {
                    System.out.println("Test222");
                }

                edgeList = new ArrayList<>();
            }


            beforeTempEdge = possibleEdge;
            possibleEdge = nexts.get(possibleEdge);
            if (possibleEdge == startEdge) {
                break;
            }

        }


        System.out.println("Test");






        newFront.add(new MutablePair<TreeVertex, TreeVertex>(front.getLeft(), front.getRight()));
        //TODO orthogonalRep der Front im Benachbarten Vertex updaten
        replacerEdge.add(new MutablePair<TreeVertex, TreeVertex>((front.getRight()), front.getLeft()));
        MutablePair<TreeVertex, TreeVertex> originalFrontReverse = GraphHelper.reverseEdge(originalFront);
        PlanarGraphFace<TreeVertex, DefaultEdge> neighbouringFace = originaledgeToFaceMap.get(originalFrontReverse);
        assert (neighbouringFace != null);

        List<MutablePair<TreeVertex, TreeVertex>> neigbouringFaceEdgeList = neighbouringFace.getEdgeList();

        for (int i = 0; i < replacerEdge.size() - 1; i++) {
            neighbouringFace.getOrthogonalRep().put(replacerEdge.get(i), 0);
            originaledgeToFaceMap.put(replacerEdge.get(i), neighbouringFace);
        }
        neighbouringFace.getOrthogonalRep().put(replacerEdge.get(replacerEdge.size() - 1), neighbouringFace.getOrthogonalRep().get(originalFrontReverse));
        originaledgeToFaceMap.put(replacerEdge.get(replacerEdge.size() - 1), neighbouringFace);

        int pos = neigbouringFaceEdgeList.indexOf(originalFrontReverse);
        neigbouringFaceEdgeList.remove(pos);
        neigbouringFaceEdgeList.addAll(pos, replacerEdge);

















    }

    private void computeExternalFront(Map<MutablePair<TreeVertex, TreeVertex>, Integer> orthogonalRep, Map<MutablePair<TreeVertex, TreeVertex>, MutablePair<TreeVertex, TreeVertex>> fronts, Map<MutablePair<TreeVertex, TreeVertex>, MutablePair<TreeVertex, TreeVertex>> externalFronts, Map<MutablePair<TreeVertex, TreeVertex>, MutablePair<TreeVertex, TreeVertex>> nexts, Map<MutablePair<TreeVertex, TreeVertex>, Integer> orientations, MutablePair[] outerRectangle, List<MutablePair<TreeVertex, TreeVertex>> edgeList) {

        for (MutablePair<TreeVertex, TreeVertex> edge : edgeList
        ) {
            if (orthogonalRep.get(edge) == -1) {
                findexternalFront(edge, fronts, externalFronts, orthogonalRep, nexts, orientations, outerRectangle);
            }

        }

    }

    private void findexternalFront(MutablePair<TreeVertex, TreeVertex> edge, Map<MutablePair<TreeVertex, TreeVertex>, MutablePair<TreeVertex, TreeVertex>> fronts, Map<MutablePair<TreeVertex, TreeVertex>, MutablePair<TreeVertex, TreeVertex>> externalFronts, Map<MutablePair<TreeVertex, TreeVertex>, Integer> orthogonalRep, Map<MutablePair<TreeVertex, TreeVertex>, MutablePair<TreeVertex, TreeVertex>> nexts, Map<MutablePair<TreeVertex, TreeVertex>, Integer> orientations, MutablePair[] outerRectangle) {

        int counter = orthogonalRep.get(edge);
        //TODO die Seiten und deren Numerierung des äußeren Rechtecks sind gleich zu den Integers der Orientation Map (bei einer Edge Anfangen, die opposite ist?)
        Map<MutablePair<TreeVertex, TreeVertex>, Integer> prjectedEdgesCounterMap = new HashMap<>();

        MutablePair<TreeVertex, TreeVertex> tempEdge = nexts.get(edge);
        while (counter != 1 && edge != tempEdge) {

            counter += orthogonalRep.get(tempEdge);
            tempEdge = nexts.get(tempEdge);
        }
        if (edge != tempEdge) {
            fronts.put(edge, tempEdge);
        } else {
            externalFronts.put(edge, outerRectangle[Math.floorMod(orientations.get(tempEdge) - 1, 4)]);

        }


    }


    private void computeFronts(Map<MutablePair<TreeVertex, TreeVertex>, Integer> orthogonalRep, Map<MutablePair<TreeVertex, TreeVertex>, MutablePair<TreeVertex, TreeVertex>> fronts, Map<MutablePair<TreeVertex, TreeVertex>, MutablePair<TreeVertex, TreeVertex>> nexts, Map<MutablePair<TreeVertex, TreeVertex>, MutablePair<TreeVertex, TreeVertex>> prevs, List<MutablePair<TreeVertex, TreeVertex>> edgeList, Map<TreeVertex, MutablePair<TreeVertex, TreeVertex>> vertexToFront) {

        for (MutablePair<TreeVertex, TreeVertex> edge : edgeList
        ) {
            if (orthogonalRep.get(edge) == -1) {
                findFront(edge, fronts, orthogonalRep, nexts, vertexToFront);
            }

        }
    }


    private void findFront(MutablePair<TreeVertex, TreeVertex> edge, Map<MutablePair<TreeVertex, TreeVertex>, MutablePair<TreeVertex, TreeVertex>> fronts, Map<MutablePair<TreeVertex, TreeVertex>, Integer> orthogonalRep, Map<MutablePair<TreeVertex, TreeVertex>, MutablePair<TreeVertex, TreeVertex>> nexts, Map<TreeVertex, MutablePair<TreeVertex, TreeVertex>> vertexToFront) {

        int counter = orthogonalRep.get(edge);

        MutablePair<TreeVertex, TreeVertex> tempEdge = edge;
        while (counter != 1) {
            tempEdge = nexts.get(tempEdge);
            assert (tempEdge != null);
            counter += orthogonalRep.get(tempEdge);
            if (tempEdge == edge) {
                break;
            }

        }
        if (tempEdge != edge) {
            fronts.put(edge, nexts.get(tempEdge));
            vertexToFront.put(edge.getRight(), nexts.get(tempEdge));
        }
    }

    private void projectEdge(MutablePair<TreeVertex, TreeVertex> front, Map<MutablePair<TreeVertex, TreeVertex>, MutablePair<TreeVertex, TreeVertex>> nexts, Map<MutablePair<TreeVertex, TreeVertex>, MutablePair<TreeVertex, TreeVertex>> fronts, Map<MutablePair<TreeVertex, TreeVertex>, Integer> newOrthogonalRep, PlanarGraphFace<TreeVertex, E> face, Map<TreeVertex, MutablePair<TreeVertex, TreeVertex>> vertexToFront) {

        MutablePair<TreeVertex, TreeVertex> possibleEdge = nexts.get(front);
        MutablePair<TreeVertex, TreeVertex> beforeTempEdge = possibleEdge;
        List<MutablePair<TreeVertex, TreeVertex>> edgeList = new ArrayList<>();
        List<MutablePair<TreeVertex, TreeVertex>> newFront = new ArrayList<>();
        List<MutablePair<TreeVertex, TreeVertex>> replacerEdge = new ArrayList<>();
        MutablePair<TreeVertex, TreeVertex> originalFront = new MutablePair<TreeVertex, TreeVertex>(front.getLeft(), front.getRight());


        while (possibleEdge != front) {

            edgeList.add(possibleEdge);
            //TODO Hier noch viel Mist morgen genau überlegen was zu tun ist. Vielleicht alle Kanten Sammeln die die gleiche front haben, Dann deren Reihenfolge feststellen? Dann die neuen Zyklen aufbauen?


            if (front == fronts.get(possibleEdge)) {
                newFront.add(new MutablePair<TreeVertex, TreeVertex>(null, front.getRight()));
                replacerEdge.add(new MutablePair<TreeVertex, TreeVertex>(front.getRight(), null));


                TreeVertex newVertex = new TreeVertex(front.getLeft().getName() + possibleEdge.getRight().getName() + " R");
                MutablePair<TreeVertex, TreeVertex> newEdge = new MutablePair<>(possibleEdge.getRight(), newVertex);

                edgeList.add(newEdge);
                MutablePair<TreeVertex, TreeVertex> newEdge2 = new MutablePair<>(newVertex, edgeList.get(0).getLeft());
                edgeList.add(newEdge2);


                newOrthogonalRep.put(possibleEdge, 0);
                newOrthogonalRep.put(newEdge, 1);
                newOrthogonalRep.put(newEdge2, newOrthogonalRep.get(front));


                // Falls die Kante, welche eine Front hat geteilt wird, daher muss die Frontsmap geupdated werden, da sonst die front nicht getroffen wird
                if (fronts.get(front) != null) {
                    fronts.put(newEdge2, fronts.get(front));
                }


                front.setRight(newVertex);
            /*    if (face.getName().equals("0")) {
                    int pos = face.getEdgeList().indexOf(front);

                    face.getEdgeList().add(pos + 1, newEdge2);
                }
*/

                MutablePair<TreeVertex, TreeVertex> followingEdge = nexts.get(possibleEdge);

                // Update nexts of new Face
                nexts.put(possibleEdge, newEdge);
                nexts.put(newEdge, newEdge2);
                nexts.put(newEdge2, edgeList.get(0));

                //Update the "rest" of the face


                MutablePair<TreeVertex, TreeVertex> newEdgeReverse = new MutablePair<>(front.getRight(), possibleEdge.getRight());
                newOrthogonalRep.put(newEdgeReverse, 1);
                newOrthogonalRep.put(new MutablePair<TreeVertex, TreeVertex>(front.getLeft(), front.getRight()), 1);
                newFront.get((newFront.size() - 1)).setLeft(front.getRight());
                replacerEdge.get((newFront.size() - 1)).setRight(front.getRight());

                nexts.put(front, newEdgeReverse);
                nexts.put(newEdgeReverse, followingEdge);


                possibleEdge = front;
                System.out.println("Test");


                try {
                    for (MutablePair<TreeVertex, TreeVertex> edge :
                            edgeList
                    ) {
                        newOrthogonalRep.get(edge);
                    }
                } catch (Exception e) {
                    System.out.println("Test222");
                }

                edgeList = new ArrayList<>();
            }


            beforeTempEdge = possibleEdge;
            possibleEdge = nexts.get(possibleEdge);
        }
        newFront.add(new MutablePair<TreeVertex, TreeVertex>(front.getLeft(), front.getRight()));
        //TODO orthogonalRep der Front im Benachbarten Vertex updaten
        replacerEdge.add(new MutablePair<TreeVertex, TreeVertex>(front.getRight(), (front.getLeft())));
        MutablePair<TreeVertex, TreeVertex> originalFrontReverse = GraphHelper.reverseEdge(originalFront);
        PlanarGraphFace<TreeVertex, DefaultEdge> neighbouringFace = originaledgeToFaceMap.get(originalFrontReverse);
        assert (neighbouringFace != null);

        List<MutablePair<TreeVertex, TreeVertex>> neigbouringFaceEdgeList = neighbouringFace.getEdgeList();

        for (int i = 0; i < replacerEdge.size() - 1; i++) {
            neighbouringFace.getOrthogonalRep().put(replacerEdge.get(i), 0);
            originaledgeToFaceMap.put(replacerEdge.get(i), neighbouringFace);
        }
        neighbouringFace.getOrthogonalRep().put(replacerEdge.get(replacerEdge.size() - 1), neighbouringFace.getOrthogonalRep().get(originalFrontReverse));
        originaledgeToFaceMap.put(replacerEdge.get(replacerEdge.size() - 1), neighbouringFace);

        int pos = neigbouringFaceEdgeList.indexOf(originalFrontReverse);
        neigbouringFaceEdgeList.remove(pos);
        neigbouringFaceEdgeList.addAll(pos, replacerEdge);


    }


    public void computeFronts(MutablePair<TreeVertex, TreeVertex> edge, Map<MutablePair<TreeVertex, TreeVertex>, MutablePair<TreeVertex, TreeVertex>> fronts) {
        for (Map.Entry<MutablePair<TreeVertex, TreeVertex>, MutablePair<TreeVertex, TreeVertex>> entry1 : fronts.entrySet()
        ) {


        }
    }

    public void computeNexts(List<MutablePair<TreeVertex, TreeVertex>> edgeList, Map<MutablePair<TreeVertex, TreeVertex>, MutablePair<TreeVertex, TreeVertex>> nexts, Map<MutablePair<TreeVertex, TreeVertex>, MutablePair<TreeVertex, TreeVertex>> prevs) {


        for (int i = 0; i < edgeList.size(); i++) {

            nexts.put(edgeList.get(i), edgeList.get((i + 1) % edgeList.size()));
            prevs.put(edgeList.get((i + 1) % edgeList.size()), edgeList.get((i)));
        }


    }


}