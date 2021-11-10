import org.apache.commons.lang3.time.StopWatch;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;

public class Rectangulator<E> {

    List<PlanarGraphFace<TreeVertex, E>> planarGraphFaces;

    HashMap<TupleEdge<TreeVertex, TreeVertex>, TupleEdge<TreeVertex, TreeVertex>> frontMap = new HashMap<>();
    HashMap<PlanarGraphFace<TreeVertex, E>, PlanarGraphFace<TreeVertex, E>> rectangularFaceMap = new HashMap<>();
    HashMap<TupleEdge<TreeVertex, TreeVertex>, PlanarGraphFace<TreeVertex, E>> edgeFaceNeighbourMap = new HashMap<>();
    Map<TupleEdge<TreeVertex, TreeVertex>, Integer> angleMap = new HashMap<>();
    HashMap<TupleEdge<TreeVertex, TreeVertex>, PlanarGraphFace<TreeVertex, DefaultEdge>> originaledgeToFaceMap = new HashMap<>(2048);
    PlanarGraphFace<TreeVertex, DefaultEdge> outerFace = new PlanarGraphFace<>("externalFace");
    int counter = 100;
    List<TupleEdge<TreeVertex, TreeVertex>> startingEdges = new ArrayList<>();

    public Rectangulator(List<PlanarGraphFace<TreeVertex, E>> planarGraphFaces) {
        this.planarGraphFaces = planarGraphFaces;
    }

    public List<PlanarGraphFace<TreeVertex, E>> getPlanarGraphFaces() {
        return planarGraphFaces;
    }

    public void setPlanarGraphFaces(List<PlanarGraphFace<TreeVertex, E>> planarGraphFaces) {
        this.planarGraphFaces = planarGraphFaces;
    }

    public HashMap<TupleEdge<TreeVertex, TreeVertex>, TupleEdge<TreeVertex, TreeVertex>> getFrontMap() {
        return frontMap;
    }

    public void setFrontMap(HashMap<TupleEdge<TreeVertex, TreeVertex>, TupleEdge<TreeVertex, TreeVertex>> frontMap) {
        this.frontMap = frontMap;
    }

    public HashMap<PlanarGraphFace<TreeVertex, E>, PlanarGraphFace<TreeVertex, E>> getRectangularFaceMap() {
        return rectangularFaceMap;
    }

    public void setRectangularFaceMap(HashMap<PlanarGraphFace<TreeVertex, E>, PlanarGraphFace<TreeVertex, E>> rectangularFaceMap) {
        this.rectangularFaceMap = rectangularFaceMap;
    }

    public HashMap<TupleEdge<TreeVertex, TreeVertex>, PlanarGraphFace<TreeVertex, E>> getEdgeFaceNeighbourMap() {
        return edgeFaceNeighbourMap;
    }

    public void setEdgeFaceNeighbourMap(HashMap<TupleEdge<TreeVertex, TreeVertex>, PlanarGraphFace<TreeVertex, E>> edgeFaceNeighbourMap) {
        this.edgeFaceNeighbourMap = edgeFaceNeighbourMap;
    }

    public Map<TupleEdge<TreeVertex, TreeVertex>, Integer> getAngleMap() {
        return angleMap;
    }

    public void setAngleMap(Map<TupleEdge<TreeVertex, TreeVertex>, Integer> angleMap) {
        this.angleMap = angleMap;
    }

    public HashMap<TupleEdge<TreeVertex, TreeVertex>, PlanarGraphFace<TreeVertex, DefaultEdge>> getOriginaledgeToFaceMap() {
        return originaledgeToFaceMap;
    }

    public void setOriginaledgeToFaceMap(HashMap<TupleEdge<TreeVertex, TreeVertex>, PlanarGraphFace<TreeVertex, DefaultEdge>> originaledgeToFaceMap) {
        this.originaledgeToFaceMap = originaledgeToFaceMap;
    }

    public TupleEdge<TreeVertex, TreeVertex> next(TupleEdge<TreeVertex, TreeVertex> edge) {


        return edge;
    }

    public TreeVertex corner(TupleEdge<TreeVertex, TreeVertex> edge) {

        return edge.getRight();
    }

    public TupleEdge<TreeVertex, TreeVertex> extend(TupleEdge<TreeVertex, TreeVertex> edge) {


        return edge;
    }


    //TODO eine Methode die zum Beispie für front(e)=(7,8) returned (r1,8), dann (r2,r1) und dann (r3,r2)
    //TODO ein paar so modifizieren, dass aus (7,8), dann wird daraus 7,r1) und dann neues pair mit (r1, r8)
    public TupleEdge<TreeVertex, TreeVertex> front(TupleEdge<TreeVertex, TreeVertex> edge, PlanarGraphFace<TreeVertex, E> planargraphFace) {
        Map<TupleEdge<TreeVertex, TreeVertex>, Integer> map = planargraphFace.getOrthogonalRep();
        Set<TupleEdge<TreeVertex, TreeVertex>> edgeSet = map.keySet();


        return edge;
    }


    public void initialize() {
        List<PlanarGraphFace<TreeVertex, E>> faceList = new ArrayList<>(planarGraphFaces);
        Deque<PlanarGraphFace<TreeVertex, E>> dequeStack = new ArrayDeque<>(planarGraphFaces);
        PlanarGraphFace<TreeVertex, E> face;


        while (dequeStack.size() > 0) {
            face = dequeStack.pop();

            int count = 0;
            for (TupleEdge<TreeVertex, TreeVertex> edge : face.getEdgeList()) {
                if ((Math.abs(face.getOrthogonalRep().get(edge)) == 1)) {
                    count++;
                }
            }
            if (count == 4) {
                if (face.getType() == PlanarGraphFace.FaceType.EXTERNAL) {
                    outerFace = (PlanarGraphFace<TreeVertex, DefaultEdge>) face;
                    continue;
                } else {
                    rectangularFaceMap.put(face, face);
                    continue;
                }
            }

            face.setOrientations();
            Map<TupleEdge<TreeVertex, TreeVertex>, Integer> orthogonalRep = face.getOrthogonalRep();

            Map<TupleEdge<TreeVertex, TreeVertex>, TupleEdge<TreeVertex, TreeVertex>> nexts = new HashMap<>();
            Map<TupleEdge<TreeVertex, TreeVertex>, TupleEdge<TreeVertex, TreeVertex>> prevs = new HashMap<>();
            Map<TupleEdge<TreeVertex, TreeVertex>, TupleEdge<TreeVertex, TreeVertex>> fronts = new HashMap<>();
            Map<TreeVertex, TupleEdge<TreeVertex, TreeVertex>> vertexToFront = new HashMap<>();
            Map<TupleEdge<TreeVertex, TreeVertex>, TupleEdge<TreeVertex, TreeVertex>> externalFronts = new HashMap<>();
            ArrayList<TupleEdge> nextsArray = new ArrayList<>();

            //computeNexts(face.edgeList, nexts, prevs);
            computeNexts2(face.edgeList, nexts, prevs, nextsArray);

            Map<TupleEdge<TreeVertex, TreeVertex>, Integer> newOrthogonalRep = new HashMap<>();
            newOrthogonalRep.putAll(face.getOrthogonalRep());


            if (face.getType() == PlanarGraphFace.FaceType.INTERNAL) {

                computeFronts(orthogonalRep, fronts, nexts, prevs, face.edgeList, vertexToFront);

                Set<TupleEdge<TreeVertex, TreeVertex>> frontSet = new HashSet<>(fronts.values());
                List<TupleEdge<TreeVertex, TreeVertex>> frontList = new ArrayList<>(frontSet);
                for (TupleEdge<TreeVertex, TreeVertex> edge : frontList
                ) {
                    projectEdge2(edge, nexts, fronts, newOrthogonalRep, face, vertexToFront);
                }


            } else {

                if (face.getType() == PlanarGraphFace.FaceType.EXTERNAL_PROCESSED) { // die ursprüngliche Äußere Facette wurde bearbeitet und ist nicht rechteckig, dann wird der rechteckige Rahmen um diese gezogen

                    // new outer Rectangle
                    TreeVertex v1 = new TreeVertex("outer1", true);
                    TreeVertex v2 = new TreeVertex("outer2", true);
                    TreeVertex v3 = new TreeVertex("outer3", true);
                    TreeVertex v4 = new TreeVertex("outer4", true);
                    TupleEdge<TreeVertex, TreeVertex> edge1 = new TupleEdge<>(v1, v2, 1);
                    TupleEdge<TreeVertex, TreeVertex> edge2 = new TupleEdge<>(v2, v3, 1);
                    TupleEdge<TreeVertex, TreeVertex> edge3 = new TupleEdge<>(v3, v4, 1);
                    TupleEdge<TreeVertex, TreeVertex> edge4 = new TupleEdge<>(v4, v1, 1);
                    TupleEdge[] outerRectangle = new TupleEdge[]{edge1, edge2, edge3, edge4};
                    nexts.put(edge1, edge2);
                    nexts.put(edge2, edge3);
                    nexts.put(edge3, edge4);
                    nexts.put(edge4, edge1);
                    face.getEdgeOrientationMap().put(edge1, 0);
                    face.getEdgeOrientationMap().put(edge2, 1);
                    face.getEdgeOrientationMap().put(edge3, 2);
                    face.getEdgeOrientationMap().put(edge4, 3);

                    outerFace.getEdgeOrientationMap().put(GraphHelper.reverseEdge(edge1, false), 0);
                    outerFace.getEdgeOrientationMap().put(GraphHelper.reverseEdge(edge2, false), 1);
                    outerFace.getEdgeOrientationMap().put(GraphHelper.reverseEdge(edge3, false), 2);
                    outerFace.getEdgeOrientationMap().put(GraphHelper.reverseEdge(edge4, false), 3);
                    outerFace.getEdgeList().add(GraphHelper.reverseEdge(edge1, true));
                    outerFace.getEdgeList().add(GraphHelper.reverseEdge(edge4, true));
                    outerFace.getEdgeList().add(GraphHelper.reverseEdge(edge3, true));
                    outerFace.getEdgeList().add(GraphHelper.reverseEdge(edge2, true));
                    outerFace.getOrthogonalRep().put(GraphHelper.reverseEdge(edge1, false), -1);
                    outerFace.getOrthogonalRep().put(GraphHelper.reverseEdge(edge2, false), -1);
                    outerFace.getOrthogonalRep().put(GraphHelper.reverseEdge(edge3, false), -1);
                    outerFace.getOrthogonalRep().put(GraphHelper.reverseEdge(edge4, false), -1);

                    for (TupleEdge<TreeVertex, TreeVertex> edge :
                            outerFace.getEdgeList()) {
                        originaledgeToFaceMap.put(edge, outerFace);

                    }
                    outerFace.computeEdgeToIndexMap();

                    newOrthogonalRep.put(edge1, 1);
                    newOrthogonalRep.put(edge2, 1);
                    newOrthogonalRep.put(edge3, 1);
                    newOrthogonalRep.put(edge4, 1);
                    face.setOrientations();
                    face.computeEdgeToIndexMap();

                    computeExternalFront(orthogonalRep, fronts, externalFronts, nexts, face.getEdgeOrientationMap(), outerRectangle, face.getEdgeList());

                    Set<TupleEdge<TreeVertex, TreeVertex>> frontSet = new LinkedHashSet<>(fronts.values());
                    List<TupleEdge<TreeVertex, TreeVertex>> frontList = new ArrayList<>(frontSet);


                    Set<TupleEdge<TreeVertex, TreeVertex>> externalFrontSet = new LinkedHashSet<>(externalFronts.values());
                    List<TupleEdge<TreeVertex, TreeVertex>> edgeList = new ArrayList<>(externalFrontSet);

                    // Um den Prozess zu vereinfachen wird eine
                    if (fronts.keySet().size() == 0) {

                        TupleEdge<TreeVertex, TreeVertex> edge = edgeList.get(0);
                        projectExternalEdge(edge, nexts, externalFronts, newOrthogonalRep, face.getEdgeOrientationMap(), face.sidesMap);
                        for (int i = 1; i < edgeList.size() - 1; i++) {
                            projectEdge2(edgeList.get(i), nexts, externalFronts, newOrthogonalRep, face, vertexToFront);

                        }
                    }
                } else {

                    System.out.println("Find Fronts:");
                    StopWatch stopWatch = new StopWatch();
                    Map<TupleEdge<TreeVertex, TreeVertex>, TupleEdge<TreeVertex, TreeVertex>> fronts2 = new HashMap<>();


                    stopWatch.start();
          /*          for (TupleEdge<TreeVertex, TreeVertex> edge : face.edgeList
                    ) {
                        if (orthogonalRep.get(edge) == -1) {
                            findFront(edge, fronts2, orthogonalRep, nexts, vertexToFront);
                        }

                    }*/
                    stopWatch.stop();
                    System.out.println(" StopWatch findfronts1: " + stopWatch.getTime());

                    stopWatch.reset();

                    stopWatch.start();

                    findfronts2(face.edgeList, fronts, orthogonalRep, vertexToFront, true);
                    stopWatch.stop();
                    System.out.println(" StopWatch findFronts2: " + stopWatch.getTime());


                    Set<TupleEdge<TreeVertex, TreeVertex>> frontSet = new LinkedHashSet<>(fronts.values());
                    List<TupleEdge<TreeVertex, TreeVertex>> frontList = new ArrayList<>(frontSet);
                    for (TupleEdge<TreeVertex, TreeVertex> edge : frontList
                    ) {

                        //   projectEdgeExternalFace(edge, nexts, fronts, newOrthogonalRep, face, vertexToFront, externalFronts);
                        projectEdge2(edge, nexts, fronts, newOrthogonalRep, face, vertexToFront);
                    }

                }

                face.setType(PlanarGraphFace.FaceType.EXTERNAL_PROCESSED);
            }


            HashMap<TupleEdge<TreeVertex, TreeVertex>, Boolean> visitedMap = new HashMap<>();
            boolean processed = false;
            for (TupleEdge<TreeVertex, TreeVertex> pair :
                    nexts.keySet()
            ) {
                visitedMap.put(pair, false);
            }
            for (TupleEdge<TreeVertex, TreeVertex> pair :
                //  visitedMap.keySet()
                    startingEdges
            ) {
                processed = true;
                if (!visitedMap.get(pair)) {
                    PlanarGraphFace<TreeVertex, E> faceObj = new PlanarGraphFace<>(Integer.toString(counter++));

                    rectangularFaceMap.put(faceObj, face);
                    faceObj.getOrthogonalRep().put(pair, newOrthogonalRep.get(pair));
                    faceObj.edgeList.add(pair);
                    //    assert (originaledgeToFaceMap.get(GraphHelper.reverseEdge(pair)) != null);


                    originaledgeToFaceMap.put(pair, (PlanarGraphFace<TreeVertex, DefaultEdge>) faceObj);

                    int counter = newOrthogonalRep.get(pair);
                    int counter2 = 0;
                    if ((newOrthogonalRep.get(pair) == 1)) {
                        counter2++;
                    }

                    TupleEdge<TreeVertex, TreeVertex> iterator = nexts.get(pair);

                    visitedMap.put(pair, true);
                    while (!pair.equals(iterator)) {
                        assert (!visitedMap.get(iterator));
                        visitedMap.put(iterator, true);

                        faceObj.getOrthogonalRep().put(iterator, newOrthogonalRep.get(iterator));
                        faceObj.edgeList.add(iterator);

                        counter += newOrthogonalRep.get(iterator);
                        if (newOrthogonalRep.get(iterator) == 1) {
                            counter2++;
                        }
                        originaledgeToFaceMap.put(iterator, (PlanarGraphFace<TreeVertex, DefaultEdge>) faceObj);
                        iterator = nexts.get(iterator);

                    }

                    assert (Math.abs(counter) == 4);
                    if (counter == -4) {
                        System.out.println("External Face Processed");

                        dequeStack.push(faceObj);
                        faceObj.setType(PlanarGraphFace.FaceType.EXTERNAL_PROCESSED);
                    }
                    if (counter2 > 4 || counter == -4) {
                        if (counter2 > 4 && counter != -4) {
                            dequeStack.push(faceObj);
                        }
                        rectangularFaceMap.remove(faceObj);
                    }
                    faceObj.computeEdgeToIndexMap();
                    for (TupleEdge<TreeVertex, TreeVertex> edge : faceObj.edgeList
                    ) {
                        originaledgeToFaceMap.put(new TupleEdge<>(edge.getLeft(), edge.getRight()), (PlanarGraphFace<TreeVertex, DefaultEdge>) faceObj);
                    }
                }
            }

            //
            if (startingEdges.size() == 0 && face.getName().equals("0") && !processed) {
                dequeStack.push(face);
            }

            // Original external face was rectangular to begin with
            if (startingEdges.size() == 0 && !face.getName().equals("0")) {
                rectangularFaceMap.put(face, face);
            }


            startingEdges = new ArrayList<>();
            //    boolean test = originaledgeToFaceMap.containsValue(face);

            //     System.out.println("Test");

        }

    }


    private void projectExternalEdge(TupleEdge<TreeVertex, TreeVertex> front, Map<TupleEdge<TreeVertex, TreeVertex>, TupleEdge<TreeVertex, TreeVertex>> nexts, Map<TupleEdge<TreeVertex, TreeVertex>, TupleEdge<TreeVertex, TreeVertex>> externalFronts, Map<TupleEdge<TreeVertex, TreeVertex>, Integer> newOrthogonalRep, Map<TupleEdge<TreeVertex, TreeVertex>, Integer> orientations, Map<Integer, ArrayList<TupleEdge<TreeVertex, TreeVertex>>> sidesMap) {
        originaledgeToFaceMap.remove(front);
        TupleEdge<TreeVertex, TreeVertex> possibleEdge = nexts.get(front);
        TupleEdge<TreeVertex, TreeVertex> beforeTempEdge = possibleEdge;
        List<TupleEdge<TreeVertex, TreeVertex>> edgeList = new ArrayList<>();
        List<TupleEdge<TreeVertex, TreeVertex>> newFront = new ArrayList<>();
        List<TupleEdge<TreeVertex, TreeVertex>> replacerEdge = new ArrayList<>();
        TupleEdge<TreeVertex, TreeVertex> originalFront = new TupleEdge<TreeVertex, TreeVertex>(front.getLeft(), front.getRight());

        assert (orientations.get(front) != null);
        int rectangleEdge = orientations.get(front);
        possibleEdge = sidesMap.get((rectangleEdge + 2) % 4).get(sidesMap.get((rectangleEdge + 2) % 4).size() - 1);
        TupleEdge<TreeVertex, TreeVertex> startEdge = possibleEdge;

        while (possibleEdge != front) {

            edgeList.add(possibleEdge);

            if (front == externalFronts.get(possibleEdge)) {
                //

                TreeVertex newVertex = new TreeVertex(front.getLeft().getName() + possibleEdge.getRight().getName() + " R", true);
                // die extendede Edge im neuen Rectangle
                TupleEdge<TreeVertex, TreeVertex> projectedEdge = new TupleEdge<>(possibleEdge.getRight(), newVertex);
                // die extended Edge in reverse
                TupleEdge<TreeVertex, TreeVertex> projectedEdgeReverse = new TupleEdge<>(newVertex, possibleEdge.getRight());

                edgeList.add(projectedEdge);
                // der Rest der alten Front
                TupleEdge<TreeVertex, TreeVertex> restOfOldFront = new TupleEdge<>(newVertex, front.getRight());
                edgeList.add(restOfOldFront);
                replacerEdge.add(new TupleEdge<TreeVertex, TreeVertex>(restOfOldFront.getRight(), restOfOldFront.getLeft()));

                newOrthogonalRep.put(possibleEdge, 0);
                possibleEdge.winkel = 0;
                newOrthogonalRep.put(projectedEdge, 1);
                projectedEdge.winkel = 1;
                newOrthogonalRep.put(restOfOldFront, newOrthogonalRep.get(front));
                restOfOldFront.winkel = newOrthogonalRep.get(front);

                // Falls die Kante, welche eine Front hat geteilt wird, daher muss die Frontsmap geupdated werden, da sonst die front nicht getroffen wird
                if (externalFronts.get(front) != null) {
                    externalFronts.put(restOfOldFront, externalFronts.get(front));
                }

                TupleEdge<TreeVertex, TreeVertex> edgeThatfollowsOldFront = nexts.get(front);
                front.setRight(newVertex);

                TupleEdge<TreeVertex, TreeVertex> followingEdge = nexts.get(possibleEdge);

                // Update nexts of new Face
                nexts.put(projectedEdgeReverse, followingEdge);
                nexts.put(front, projectedEdgeReverse);

                //Update the "rest" of the face

                nexts.put(possibleEdge, projectedEdge);
                nexts.put(projectedEdge, restOfOldFront);
                newOrthogonalRep.put(projectedEdgeReverse, 1);
                projectedEdgeReverse.winkel = 1;
                newOrthogonalRep.put(new TupleEdge<>(front.getLeft(), front.getRight()), 1);
                front.winkel = 1;

                nexts.put(restOfOldFront, edgeThatfollowsOldFront);
                newFront.add(new TupleEdge<>(restOfOldFront.getLeft(), restOfOldFront.getRight()));


                possibleEdge = front;
                //       System.out.println("Test");


                try {
                    for (TupleEdge<TreeVertex, TreeVertex> edge :
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


        newFront.add(new TupleEdge<TreeVertex, TreeVertex>(front.getLeft(), front.getRight()));
        //TODO orthogonalRep der Front im Benachbarten Vertex updaten
        replacerEdge.add(new TupleEdge<TreeVertex, TreeVertex>((front.getRight()), front.getLeft()));
        TupleEdge<TreeVertex, TreeVertex> originalFrontReverse = GraphHelper.reverseEdge(originalFront, false);
        PlanarGraphFace<TreeVertex, DefaultEdge> neighbouringFace = originaledgeToFaceMap.get(originalFrontReverse);
        originaledgeToFaceMap.remove(originalFrontReverse);
        assert (neighbouringFace != null);

        List<TupleEdge<TreeVertex, TreeVertex>> neigbouringFaceEdgeList = neighbouringFace.getEdgeList();

        for (int i = 0; i < replacerEdge.size() - 1; i++) {
            neighbouringFace.getOrthogonalRep().put(replacerEdge.get(i), 0);
            originaledgeToFaceMap.put(replacerEdge.get(i), neighbouringFace);
        }
        neighbouringFace.getOrthogonalRep().put(replacerEdge.get(replacerEdge.size() - 1), neighbouringFace.getOrthogonalRep().get(originalFrontReverse));
        originaledgeToFaceMap.put(replacerEdge.get(replacerEdge.size() - 1), neighbouringFace);

        int pos = neigbouringFaceEdgeList.indexOf(originalFrontReverse);

        neigbouringFaceEdgeList.remove(pos);
        neigbouringFaceEdgeList.addAll(pos, replacerEdge);

        startingEdges.addAll(newFront);

    }

    private void computeExternalFront(Map<TupleEdge<TreeVertex, TreeVertex>, Integer> orthogonalRep, Map<TupleEdge<TreeVertex, TreeVertex>, TupleEdge<TreeVertex, TreeVertex>> fronts, Map<TupleEdge<TreeVertex, TreeVertex>, TupleEdge<TreeVertex, TreeVertex>> externalFronts, Map<TupleEdge<TreeVertex, TreeVertex>, TupleEdge<TreeVertex, TreeVertex>> nexts, Map<TupleEdge<TreeVertex, TreeVertex>, Integer> orientations, TupleEdge[] outerRectangle, List<TupleEdge<TreeVertex, TreeVertex>> edgeList) {

        for (TupleEdge<TreeVertex, TreeVertex> edge : edgeList
        ) {
            if (orthogonalRep.get(edge) == -1) {
                findexternalFront(edge, fronts, externalFronts, orthogonalRep, nexts, orientations, outerRectangle);
            }

        }

    }

    private void findexternalFront(TupleEdge<TreeVertex, TreeVertex> edge, Map<TupleEdge<TreeVertex, TreeVertex>, TupleEdge<TreeVertex, TreeVertex>> fronts, Map<TupleEdge<TreeVertex, TreeVertex>, TupleEdge<TreeVertex, TreeVertex>> externalFronts, Map<TupleEdge<TreeVertex, TreeVertex>, Integer> orthogonalRep, Map<TupleEdge<TreeVertex, TreeVertex>, TupleEdge<TreeVertex, TreeVertex>> nexts, Map<TupleEdge<TreeVertex, TreeVertex>, Integer> orientations, TupleEdge[] outerRectangle) {

        int counter = orthogonalRep.get(edge);
        //TODO die Seiten und deren Numerierung des äußeren Rechtecks sind gleich zu den Integers der Orientation Map (bei einer Edge Anfangen, die opposite ist?)
        Map<TupleEdge<TreeVertex, TreeVertex>, Integer> prjectedEdgesCounterMap = new HashMap<>();

        TupleEdge<TreeVertex, TreeVertex> tempEdge = nexts.get(edge);
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


    private void computeFronts(Map<TupleEdge<TreeVertex, TreeVertex>, Integer> orthogonalRep, Map<TupleEdge<TreeVertex, TreeVertex>, TupleEdge<TreeVertex, TreeVertex>> fronts, Map<TupleEdge<TreeVertex, TreeVertex>, TupleEdge<TreeVertex, TreeVertex>> nexts, Map<TupleEdge<TreeVertex, TreeVertex>, TupleEdge<TreeVertex, TreeVertex>> prevs, List<TupleEdge<TreeVertex, TreeVertex>> edgeList, Map<TreeVertex, TupleEdge<TreeVertex, TreeVertex>> vertexToFront) {
        Map<TupleEdge<TreeVertex, TreeVertex>, TupleEdge<TreeVertex, TreeVertex>> fronts2 = new HashMap<>();
     /*   for (TupleEdge<TreeVertex, TreeVertex> edge : edgeList
        ) {
            if (orthogonalRep.get(edge) == -1) {
                findFront(edge, fronts2, orthogonalRep, nexts, vertexToFront);
            }

        }*/

        findfronts2(edgeList, fronts, orthogonalRep, vertexToFront, false);
    }


    private void findFront(TupleEdge<TreeVertex, TreeVertex> edge, Map<TupleEdge<TreeVertex, TreeVertex>, TupleEdge<TreeVertex, TreeVertex>> fronts, Map<TupleEdge<TreeVertex, TreeVertex>, Integer> orthogonalRep, Map<TupleEdge<TreeVertex, TreeVertex>, TupleEdge<TreeVertex, TreeVertex>> nexts, Map<TreeVertex, TupleEdge<TreeVertex, TreeVertex>> vertexToFront) {

        int counter = orthogonalRep.get(edge);

        TupleEdge<TreeVertex, TreeVertex> tempEdge = edge;
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

    private void findfronts2(List<TupleEdge<TreeVertex, TreeVertex>> edgeList, Map<TupleEdge<TreeVertex, TreeVertex>, TupleEdge<TreeVertex, TreeVertex>> fronts, Map<TupleEdge<TreeVertex, TreeVertex>, Integer> orthogonalRep, Map<TreeVertex, TupleEdge<TreeVertex, TreeVertex>> vertexToFront, boolean isExternal) {


        //TODO Hier könnte man die Stacks so anpassen, dass alle Kanten auf die stacks gelegt werden (also auch die mit 180° Winkeln), aber beim Stringbuilder werden sie ignoriert. Findet man ein 011 im STrink dann peeked und poopwed man so lange bis die 0 gefunden wird und baut so das Rectangular face.
        for (TupleEdge<TreeVertex, TreeVertex> edge : edgeList) {
            edge.winkel = orthogonalRep.get(edge);
        }
        boolean end = false;
        int endPos = -2;

        Deque<TupleEdge<TreeVertex, TreeVertex>> edgeStack = new ArrayDeque<>();
        Deque<TupleEdge<TreeVertex, TreeVertex>> frontStack = new ArrayDeque<>();

        int counter = 0;
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < edgeList.size() * 2; i++) { // TODO hier noch fixen, dass nächste Kante nicht immer geholt werden muss

            TupleEdge<TreeVertex, TreeVertex> edge = edgeList.get(i % edgeList.size());
            TupleEdge<TreeVertex, TreeVertex> startEdge;
            TupleEdge<TreeVertex, TreeVertex> front;

            if (edge.winkel == -1) {
                edgeStack.push(edge);
                frontStack.push(edge);
                counter = -1;
                buf.append(0);

                if (endPos == -2 && !end) {
                    end = true;
                    endPos = i;
                }

            } else if (edge.winkel == 1) {
                edgeStack.push(edge);
                edge.counter = ++counter;
                buf.append(1);
            }





            while (buf.length() >= 3 && buf.subSequence(buf.length() - 3, buf.length()).equals("011") && !frontStack.isEmpty()) {

                TupleEdge<TreeVertex, TreeVertex> pop = frontStack.pop();
                fronts.put(pop, edgeList.get((i + 1) % edgeList.size()));
                vertexToFront.put(pop.getRight(), edgeList.get((i + 1) % edgeList.size()));
                buf.replace(buf.length() - 3, buf.length(), "1");
                edgeStack.pop();
                edgeStack.pop();
          //      System.out.println("test");

            }

            if (i == edgeList.size()-1 && end) {
                break;
            }

        }


        StringBuilder buf3 = new StringBuilder();
        Deque<TupleEdge<TreeVertex, TreeVertex>> newEdgeStack = new ArrayDeque<>();
        Object[] toArray = edgeStack.toArray();


        /*         while (!frontStack.isEmpty()) {*/
        String str = new String();
        int j = buf.indexOf("0");
        if (j > 0) {
            str = buf.substring(j) + buf.substring(0, j);

            for (int k = 0; k < j; k++) {
                edgeStack.push(edgeStack.pollLast());
            }
        }
        j = 0;
      /*  boolean isOver = false;*/
        int size = 0;
        while (!frontStack.isEmpty() && j != str.length()) {
           /* isOver = true;*/
            size = frontStack.size();
            TupleEdge<TreeVertex, TreeVertex> edge = edgeStack.pollLast();
            buf3.append(str.charAt(j++));
            newEdgeStack.push(edge);

            if (edgeStack.isEmpty()) {
                edgeStack = newEdgeStack;
            }


            while (buf3.length() >= 3 && buf3.subSequence(buf3.length() - 3, buf3.length()).equals("011") && !frontStack.isEmpty()) {

                TupleEdge<TreeVertex, TreeVertex> pop = frontStack.pop();
                fronts.put(pop, edgeStack.peekLast());
                vertexToFront.put(pop.getRight(), edgeStack.peek());
                buf3.replace(buf3.length() - 3, buf3.length(), "1");
                newEdgeStack.pop();
                newEdgeStack.pop();
        //        System.out.println("test");
              /*  isOver = false;*/
            }



            if (j == str.length()) {
                int i = buf3.indexOf("0");
                if (i > -1) {
                    str = buf3.substring(i) + buf3.substring(0, i);
                    if (!str.contains("11")) {
                        break;
                    }

                    for (int k = 0; k < i; k++) {
                        edgeStack.push(edgeStack.pollLast());
                    }
                }






            }

        }



        for (TupleEdge<TreeVertex, TreeVertex> edge : fronts.keySet()) {

            ArrayList<Object> list = new ArrayList<>();
            ListIterator<TupleEdge<TreeVertex, TreeVertex>> iterator = edgeList.listIterator();
            while (iterator.hasNext()) {
                boolean finished = false;
                if (iterator.next() == edge) {
                    while (fronts.get(edge) != edgeList.get(iterator.nextIndex())) {
                        finished = true;
                        list.add(iterator.next());
                        if (!iterator.hasNext()) {
                            iterator = edgeList.listIterator();
                        }
                    }
                }
                if (!finished) {
                    break;
                }
            }


        }


        StringBuilder buf2 = new StringBuilder();
        for (int i = 0; i < edgeList.size(); i++) { // TODO hier noch fixen, dass nächste Kante nicht immer geholt werden muss

            TupleEdge<TreeVertex, TreeVertex> edge = edgeList.get(i % edgeList.size());
            TupleEdge<TreeVertex, TreeVertex> startEdge;
            TupleEdge<TreeVertex, TreeVertex> front;

            if (edge.winkel == -1) {
                edgeStack.offer(edge);
                frontStack.offer(edge);
                counter = -1;
                buf2.append(0);

                if (endPos == -2 && !end) {
                    end = true;
                    endPos = i;
                }

            } else if (edge.winkel == 1) {
                edgeStack.offer(edge);
                edge.counter = ++counter;
                buf2.append(1);
            }


        }

     //   System.out.println("test");
    }


    private void projectEdge2
            (TupleEdge<TreeVertex, TreeVertex> front, Map<TupleEdge<TreeVertex, TreeVertex>, TupleEdge<TreeVertex, TreeVertex>> nexts, Map<TupleEdge<TreeVertex, TreeVertex>, TupleEdge<TreeVertex, TreeVertex>> fronts, Map<TupleEdge<TreeVertex, TreeVertex>, Integer> newOrthogonalRep, PlanarGraphFace<TreeVertex, E> face, Map<TreeVertex, TupleEdge<TreeVertex, TreeVertex>> vertexToFront) {

        originaledgeToFaceMap.remove(front);
        TupleEdge<TreeVertex, TreeVertex> possibleEdge = nexts.get(front);
        TupleEdge<TreeVertex, TreeVertex> beforeTempEdge = possibleEdge;
        List<TupleEdge<TreeVertex, TreeVertex>> edgeList = new ArrayList<>();
        List<TupleEdge<TreeVertex, TreeVertex>> newFront = new ArrayList<>();
        List<TupleEdge<TreeVertex, TreeVertex>> replacerEdge = new ArrayList<>();
        TupleEdge<TreeVertex, TreeVertex> originalFront = new TupleEdge<TreeVertex, TreeVertex>(front.getLeft(), front.getRight());


        Deque<TupleEdge<TreeVertex, TreeVertex>> stack = new ArrayDeque<>();

        while (possibleEdge != front) {
            stack.push(possibleEdge);
            possibleEdge = nexts.get(possibleEdge);

        }

        TupleEdge<TreeVertex, TreeVertex> edgeBeforeFront = stack.getFirst();

        TupleEdge<TreeVertex, TreeVertex> front2 = new TupleEdge<>(front.getLeft(), front.getRight());
        nexts.put(edgeBeforeFront, front2);

        possibleEdge = nexts.get(front);


        while (possibleEdge != front2) {

            edgeList.add(possibleEdge);
            //TODO Hier noch viel Mist morgen genau überlegen was zu tun ist. Vielleicht alle Kanten Sammeln die die gleiche front haben, Dann deren Reihenfolge feststellen? Dann die neuen Zyklen aufbauen?


            if (front == fronts.get(possibleEdge)) {
                newFront.add(new TupleEdge<TreeVertex, TreeVertex>(null, front2.getRight()));
                replacerEdge.add(new TupleEdge<TreeVertex, TreeVertex>(front2.getRight(), null));


                TreeVertex newVertex = new TreeVertex(front2.getLeft().getName() + possibleEdge.getRight().getName() + " R", true);
                TupleEdge<TreeVertex, TreeVertex> newEdge = new TupleEdge<>(possibleEdge.getRight(), newVertex);

                edgeList.add(newEdge);
                TupleEdge<TreeVertex, TreeVertex> newEdge2 = new TupleEdge<>(newVertex, edgeList.get(0).getLeft());
                edgeList.add(newEdge2);

                newOrthogonalRep.put(possibleEdge, 0);
                newOrthogonalRep.put(newEdge, 1);
                newOrthogonalRep.put(newEdge2, newOrthogonalRep.get(front2));


                // Falls die Kante, welche eine Front hat geteilt wird, daher muss die Frontsmap geupdated werden, da sonst die front nicht getroffen wird
                if (fronts.get(front2) != null) {
                    fronts.put(newEdge2, fronts.get(front));
                }

                front2.setRight(newVertex);

                TupleEdge<TreeVertex, TreeVertex> followingEdge = nexts.get(possibleEdge);

                // Update nexts of new Face
                nexts.put(possibleEdge, newEdge);
                nexts.put(newEdge, newEdge2);
                nexts.put(newEdge2, edgeList.get(0));

                //Update the "rest" of the face

                TupleEdge<TreeVertex, TreeVertex> newEdgeReverse = new TupleEdge<>(front2.getRight(), possibleEdge.getRight());
                newOrthogonalRep.put(newEdgeReverse, 1);
                newOrthogonalRep.put(new TupleEdge<>(front2.getLeft(), front2.getRight()), 1);
                newFront.get((newFront.size() - 1)).setLeft(front2.getRight());
                replacerEdge.get((newFront.size() - 1)).setRight(front2.getRight());


                nexts.put(new TupleEdge<>(front2.getLeft(), front2.getRight()), newEdgeReverse);
                nexts.put(newEdgeReverse, followingEdge);


                possibleEdge = front2;
                //     System.out.println("Test");

                try {
                    for (TupleEdge<TreeVertex, TreeVertex> edge :
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
            assert (possibleEdge != null);
        }
        newFront.add(new TupleEdge<>(front2.getLeft(), front2.getRight()));
        //TODO orthogonalRep der Front im Benachbarten Vertex updaten
        replacerEdge.add(new TupleEdge<>(front2.getRight(), (front2.getLeft())));
        TupleEdge<TreeVertex, TreeVertex> originalFrontReverse = GraphHelper.reverseEdge(originalFront, false);
        PlanarGraphFace<TreeVertex, DefaultEdge> neighbouringFace = originaledgeToFaceMap.get(originalFrontReverse);
        originaledgeToFaceMap.remove(originalFrontReverse);
        assert (neighbouringFace != null);

        List<TupleEdge<TreeVertex, TreeVertex>> neigbouringFaceEdgeList = neighbouringFace.getEdgeList();

        for (int i = 0; i < replacerEdge.size() - 1; i++) {
            neighbouringFace.getOrthogonalRep().put(replacerEdge.get(i), 0);
            originaledgeToFaceMap.put(new TupleEdge<>(replacerEdge.get(i).getLeft(), replacerEdge.get(i).getRight()), neighbouringFace);
        }

        neighbouringFace.getOrthogonalRep().put(replacerEdge.get(replacerEdge.size() - 1), neighbouringFace.getOrthogonalRep().get(originalFrontReverse));
        originaledgeToFaceMap.put(new TupleEdge<>(replacerEdge.get(replacerEdge.size() - 1).getLeft(), replacerEdge.get(replacerEdge.size() - 1).getRight()), neighbouringFace);

        int pos = neigbouringFaceEdgeList.indexOf(originalFrontReverse);

        /*    neighbouringFace.computeEdgeToIndexMap();*/
        /*  int pos = neighbouringFace.edgeToIndexMap.get(originalFrontReverse);*/
        assert (pos > -1);
        neigbouringFaceEdgeList.remove(pos);
        neigbouringFaceEdgeList.addAll(pos, replacerEdge);

        nexts.remove(front);
        startingEdges.addAll(newFront);

    }


    public void computeNexts
            (List<TupleEdge<TreeVertex, TreeVertex>> edgeList, Map<TupleEdge<TreeVertex, TreeVertex>, TupleEdge<TreeVertex, TreeVertex>> nexts, Map<TupleEdge<TreeVertex, TreeVertex>, TupleEdge<TreeVertex, TreeVertex>> prevs) {


        for (int i = 0; i < edgeList.size(); i++) {
            nexts.put(edgeList.get(i), edgeList.get((i + 1) % edgeList.size()));
            prevs.put(edgeList.get((i + 1) % edgeList.size()), edgeList.get((i)));
        }


    }

    public void computeNexts2
            (List<TupleEdge<TreeVertex, TreeVertex>> edgeList, Map<TupleEdge<TreeVertex, TreeVertex>, TupleEdge<TreeVertex, TreeVertex>> nexts, Map<TupleEdge<TreeVertex, TreeVertex>, TupleEdge<TreeVertex, TreeVertex>> prevs, ArrayList<TupleEdge> nextsArray) {


        for (int i = 0; i < edgeList.size(); i++) {

            nexts.put(edgeList.get(i), edgeList.get((i + 1) % edgeList.size()));
            prevs.put(edgeList.get((i + 1) % edgeList.size()), edgeList.get((i)));
        }


    }


}