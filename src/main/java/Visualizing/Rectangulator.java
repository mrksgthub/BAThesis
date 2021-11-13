package Visualizing;

import Datatypes.PlanarGraphFace;
import Datatypes.Vertex;
import Datatypes.TupleEdge;
import Helperclasses.GraphHelper;
import org.apache.commons.lang3.time.StopWatch;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;

public class Rectangulator<E> {

    List<PlanarGraphFace<Vertex, E>> planarGraphFaces;

    HashMap<PlanarGraphFace<Vertex, E>, PlanarGraphFace<Vertex, E>> rectangularFaceMap = new HashMap<>();
    HashMap<TupleEdge<Vertex, Vertex>, PlanarGraphFace<Vertex, DefaultEdge>> originaledgeToFaceMap = new HashMap<>(2048);
    PlanarGraphFace<Vertex, DefaultEdge> outerFace = new PlanarGraphFace<>("externalFace");
    int counter = 100;
    List<TupleEdge<Vertex, Vertex>> startingEdges = new ArrayList<>();

    public Rectangulator(List<PlanarGraphFace<Vertex, E>> planarGraphFaces) {
        this.planarGraphFaces = planarGraphFaces;
    }


    public PlanarGraphFace<Vertex, DefaultEdge> getOuterFace() {
        return outerFace;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public List<PlanarGraphFace<Vertex, E>> getPlanarGraphFaces() {
        return planarGraphFaces;
    }

    public HashMap<PlanarGraphFace<Vertex, E>, PlanarGraphFace<Vertex, E>> getRectangularFaceMap() {
        return rectangularFaceMap;
    }

    public HashMap<TupleEdge<Vertex, Vertex>, PlanarGraphFace<Vertex, DefaultEdge>> getOriginaledgeToFaceMap() {
        return originaledgeToFaceMap;
    }

    public void setOriginaledgeToFaceMap(HashMap<TupleEdge<Vertex, Vertex>, PlanarGraphFace<Vertex, DefaultEdge>> originaledgeToFaceMap) {
        this.originaledgeToFaceMap = originaledgeToFaceMap;
    }

    public TupleEdge<Vertex, Vertex> next(TupleEdge<Vertex, Vertex> edge) {


        return edge;
    }


    public void initialize() {
        Deque<PlanarGraphFace<Vertex, E>> dequeStack = new ArrayDeque<>(planarGraphFaces);
        PlanarGraphFace<Vertex, E> face;


        while (dequeStack.size() > 0) {
            face = dequeStack.pop();

            int count = 0;
            for (TupleEdge<Vertex, Vertex> edge : face.getEdgeList()) {
                if ((Math.abs(face.getOrthogonalRep().get(edge)) == 1)) {
                    count++;
                }
            }
            if (count == 4) {
                if (face.getType() == PlanarGraphFace.FaceType.EXTERNAL) {
                    outerFace = (PlanarGraphFace<Vertex, DefaultEdge>) face;
                    continue;
                } else {
                    rectangularFaceMap.put(face, face);
                    continue;
                }
            }

            face.setOrientationsOuterFacette();
            Map<TupleEdge<Vertex, Vertex>, Integer> orthogonalRep = face.getOrthogonalRep();

            Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> nexts = new HashMap<>();
            Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> prevs = new HashMap<>();
            Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> fronts = new HashMap<>();
            Map<Vertex, TupleEdge<Vertex, Vertex>> vertexToFront = new HashMap<>();
            Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> externalFronts = new HashMap<>();

            //computeNexts(face.edgeList, nexts, prevs);
            computeNexts2(face.getEdgeList(), nexts, prevs);

            Map<TupleEdge<Vertex, Vertex>, Integer> newOrthogonalRep = new HashMap<>();
            newOrthogonalRep.putAll(face.getOrthogonalRep());


            if (face.getType() == PlanarGraphFace.FaceType.INTERNAL) {

                computeFronts(orthogonalRep, fronts, face.getEdgeList(), vertexToFront);

                Set<TupleEdge<Vertex, Vertex>> frontSet = new HashSet<>(fronts.values());
                List<TupleEdge<Vertex, Vertex>> frontList = new ArrayList<>(frontSet);
                for (TupleEdge<Vertex, Vertex> edge : frontList
                ) {
                    projectEdge2(edge, nexts, fronts, newOrthogonalRep);
                }


            } else {

                if (face.getType() == PlanarGraphFace.FaceType.EXTERNAL_PROCESSED) { // die ursprüngliche Äußere Facette wurde bearbeitet und ist nicht rechteckig, dann wird der rechteckige Rahmen um diese gezogen

                    // new outer Rectangle
                    Vertex v1 = new Vertex("outer1", true);
                    Vertex v2 = new Vertex("outer2", true);
                    Vertex v3 = new Vertex("outer3", true);
                    Vertex v4 = new Vertex("outer4", true);
                    TupleEdge<Vertex, Vertex> edge1 = new TupleEdge<>(v1, v2, 1);
                    TupleEdge<Vertex, Vertex> edge2 = new TupleEdge<>(v2, v3, 1);
                    TupleEdge<Vertex, Vertex> edge3 = new TupleEdge<>(v3, v4, 1);
                    TupleEdge<Vertex, Vertex> edge4 = new TupleEdge<>(v4, v1, 1);
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

                    for (TupleEdge<Vertex, Vertex> edge :
                            outerFace.getEdgeList()) {
                        originaledgeToFaceMap.put(edge, outerFace);

                    }
                  //  outerFace.computeEdgeToIndexMap();

                    newOrthogonalRep.put(edge1, 1);
                    newOrthogonalRep.put(edge2, 1);
                    newOrthogonalRep.put(edge3, 1);
                    newOrthogonalRep.put(edge4, 1);
                    face.setOrientationsOuterFacette();
            //        face.computeEdgeToIndexMap();

                    computeExternalFront(orthogonalRep, fronts, externalFronts, nexts, face.getEdgeOrientationMap(), outerRectangle, face.getEdgeList());

                    Set<TupleEdge<Vertex, Vertex>> frontSet = new LinkedHashSet<>(fronts.values());


                    Set<TupleEdge<Vertex, Vertex>> externalFrontSet = new LinkedHashSet<>(externalFronts.values());
                    List<TupleEdge<Vertex, Vertex>> edgeList = new ArrayList<>(externalFrontSet);

                    // Um den Prozess zu vereinfachen wird eine
                    if (fronts.keySet().size() == 0) {

                        TupleEdge<Vertex, Vertex> edge = edgeList.get(0);
                        projectExternalEdge(edge, nexts, externalFronts, newOrthogonalRep, face.getEdgeOrientationMap(), face.getSidesMap());
                        for (int i = 1; i < edgeList.size() - 1; i++) {
                            projectEdge2(edgeList.get(i), nexts, externalFronts, newOrthogonalRep);

                        }
                    }
                } else {

                    System.out.println("Find Fronts:");
                    StopWatch stopWatch = new StopWatch();
                    Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> fronts2 = new HashMap<>();


                    stopWatch.start();
          /*          for (Datatypes.TupleEdge<Datatypes.TreeVertex, Datatypes.TreeVertex> edge : face.edgeList
                    ) {
                        if (orthogonalRep.get(edge) == -1) {
                            findFront(edge, fronts2, orthogonalRep, nexts, vertexToFront);
                        }

                    }*/
                    stopWatch.stop();
                    System.out.println(" StopWatch findfronts1: " + stopWatch.getTime());

                    stopWatch.reset();

                    stopWatch.start();

                    findfronts2(face.getEdgeList(), fronts, orthogonalRep, vertexToFront);
                    stopWatch.stop();
                    System.out.println(" StopWatch findFronts2: " + stopWatch.getTime());


                    Set<TupleEdge<Vertex, Vertex>> frontSet = new LinkedHashSet<>(fronts.values());
                    List<TupleEdge<Vertex, Vertex>> frontList = new ArrayList<>(frontSet);
                    for (TupleEdge<Vertex, Vertex> edge : frontList
                    ) {

                        //   projectEdgeExternalFace(edge, nexts, fronts, newOrthogonalRep, face, vertexToFront, externalFronts);
                        projectEdge2(edge, nexts, fronts, newOrthogonalRep);
                    }

                }

                face.setType(PlanarGraphFace.FaceType.EXTERNAL_PROCESSED);
            }


            HashMap<TupleEdge<Vertex, Vertex>, Boolean> visitedMap = new HashMap<>();
            boolean processed = false;
            for (TupleEdge<Vertex, Vertex> pair :
                    nexts.keySet()
            ) {
                visitedMap.put(pair, false);
            }
            for (TupleEdge<Vertex, Vertex> pair :
                //  visitedMap.keySet()
                    startingEdges
            ) {
                processed = true;
                if (!visitedMap.get(pair)) {
                    PlanarGraphFace<Vertex, E> faceObj = new PlanarGraphFace<>(Integer.toString(counter++));

                    rectangularFaceMap.put(faceObj, face);
                    faceObj.getOrthogonalRep().put(pair, newOrthogonalRep.get(pair));
                    faceObj.getEdgeList().add(pair);
                    //    assert (originaledgeToFaceMap.get(Helperclasses.GraphHelper.reverseEdge(pair)) != null);


                    originaledgeToFaceMap.put(pair, (PlanarGraphFace<Vertex, DefaultEdge>) faceObj);

                    int counter = newOrthogonalRep.get(pair);
                    int counter2 = 0;
                    if ((newOrthogonalRep.get(pair) == 1)) {
                        counter2++;
                    }

                    TupleEdge<Vertex, Vertex> iterator = nexts.get(pair);

                    visitedMap.put(pair, true);
                    while (!pair.equals(iterator)) {
                        assert (!visitedMap.get(iterator));
                        visitedMap.put(iterator, true);

                        faceObj.getOrthogonalRep().put(iterator, newOrthogonalRep.get(iterator));
                        faceObj.getEdgeList().add(iterator);

                        counter += newOrthogonalRep.get(iterator);
                        if (newOrthogonalRep.get(iterator) == 1) {
                            counter2++;
                        }
                        originaledgeToFaceMap.put(iterator, (PlanarGraphFace<Vertex, DefaultEdge>) faceObj);
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
             //       faceObj.computeEdgeToIndexMap();
                    for (TupleEdge<Vertex, Vertex> edge : faceObj.getEdgeList()
                    ) {
                        originaledgeToFaceMap.put(new TupleEdge<>(edge.getLeft(), edge.getRight()), (PlanarGraphFace<Vertex, DefaultEdge>) faceObj);
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


    private void projectExternalEdge(TupleEdge<Vertex, Vertex> front, Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> nexts, Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> externalFronts, Map<TupleEdge<Vertex, Vertex>, Integer> newOrthogonalRep, Map<TupleEdge<Vertex, Vertex>, Integer> orientations, Map<Integer, ArrayList<TupleEdge<Vertex, Vertex>>> sidesMap) {
        originaledgeToFaceMap.remove(front);
        TupleEdge<Vertex, Vertex> possibleEdge = nexts.get(front);
        List<TupleEdge<Vertex, Vertex>> edgeList = new ArrayList<>();
        List<TupleEdge<Vertex, Vertex>> newFront = new ArrayList<>();
        List<TupleEdge<Vertex, Vertex>> replacerEdge = new ArrayList<>();
        TupleEdge<Vertex, Vertex> originalFront = new TupleEdge<>(front.getLeft(), front.getRight());

        assert (orientations.get(front) != null);
        int rectangleEdge = orientations.get(front);
        possibleEdge = sidesMap.get((rectangleEdge + 2) % 4).get(sidesMap.get((rectangleEdge + 2) % 4).size() - 1);
        TupleEdge<Vertex, Vertex> startEdge = possibleEdge;

        while (possibleEdge != front) {

            edgeList.add(possibleEdge);

            if (front == externalFronts.get(possibleEdge)) {
                //

                Vertex newVertex = new Vertex(front.getLeft().getName() + possibleEdge.getRight().getName() + " R", true);
                // die extendede Edge im neuen Rectangle
                TupleEdge<Vertex, Vertex> projectedEdge = new TupleEdge<>(possibleEdge.getRight(), newVertex);
                // die extended Edge in reverse
                TupleEdge<Vertex, Vertex> projectedEdgeReverse = new TupleEdge<>(newVertex, possibleEdge.getRight());

                edgeList.add(projectedEdge);
                // der Rest der alten Front
                TupleEdge<Vertex, Vertex> restOfOldFront = new TupleEdge<>(newVertex, front.getRight());
                edgeList.add(restOfOldFront);
                replacerEdge.add(new TupleEdge<>(restOfOldFront.getRight(), restOfOldFront.getLeft()));

                newOrthogonalRep.put(possibleEdge, 0);
                possibleEdge.setWinkel(0);
                newOrthogonalRep.put(projectedEdge, 1);
                projectedEdge.setWinkel(1);
                newOrthogonalRep.put(restOfOldFront, newOrthogonalRep.get(front));
                restOfOldFront.setWinkel(newOrthogonalRep.get(front));

                // Falls die Kante, welche eine Front hat geteilt wird, daher muss die Frontsmap geupdated werden, da sonst die front nicht getroffen wird
                if (externalFronts.get(front) != null) {
                    externalFronts.put(restOfOldFront, externalFronts.get(front));
                }

                TupleEdge<Vertex, Vertex> edgeThatfollowsOldFront = nexts.get(front);
                front.setRight(newVertex);

                TupleEdge<Vertex, Vertex> followingEdge = nexts.get(possibleEdge);

                // Update nexts of new Face
                nexts.put(projectedEdgeReverse, followingEdge);
                nexts.put(front, projectedEdgeReverse);

                //Update the "rest" of the face

                nexts.put(possibleEdge, projectedEdge);
                nexts.put(projectedEdge, restOfOldFront);
                newOrthogonalRep.put(projectedEdgeReverse, 1);
                projectedEdgeReverse.setWinkel(1);
                newOrthogonalRep.put(new TupleEdge<>(front.getLeft(), front.getRight()), 1);
                front.setWinkel(1);

                nexts.put(restOfOldFront, edgeThatfollowsOldFront);
                newFront.add(new TupleEdge<>(restOfOldFront.getLeft(), restOfOldFront.getRight()));


                possibleEdge = front;
                //       System.out.println("Test");


                try {
                    for (TupleEdge<Vertex, Vertex> edge :
                            edgeList
                    ) {
                        newOrthogonalRep.get(edge);
                    }
                } catch (Exception e) {
                    System.out.println("Test222");
                }

                edgeList = new ArrayList<>();
            }


            possibleEdge = nexts.get(possibleEdge);
            if (possibleEdge == startEdge) {
                break;
            }

        }


        newFront.add(new TupleEdge<>(front.getLeft(), front.getRight()));
        //TODO orthogonalRep der Front im Benachbarten Vertex updaten
        replacerEdge.add(new TupleEdge<>((front.getRight()), front.getLeft()));
        TupleEdge<Vertex, Vertex> originalFrontReverse = GraphHelper.reverseEdge(originalFront, false);
        PlanarGraphFace<Vertex, DefaultEdge> neighbouringFace = originaledgeToFaceMap.get(originalFrontReverse);
        originaledgeToFaceMap.remove(originalFrontReverse);
        assert (neighbouringFace != null);

        List<TupleEdge<Vertex, Vertex>> neigbouringFaceEdgeList = neighbouringFace.getEdgeList();

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

    private void computeExternalFront(Map<TupleEdge<Vertex, Vertex>, Integer> orthogonalRep, Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> fronts, Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> externalFronts, Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> nexts, Map<TupleEdge<Vertex, Vertex>, Integer> orientations, TupleEdge[] outerRectangle, List<TupleEdge<Vertex, Vertex>> edgeList) {

        for (TupleEdge<Vertex, Vertex> edge : edgeList
        ) {
            if (orthogonalRep.get(edge) == -1) {
                findexternalFront(edge, fronts, externalFronts, orthogonalRep, nexts, orientations, outerRectangle);
            }

        }

    }

    private void findexternalFront(TupleEdge<Vertex, Vertex> edge, Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> fronts, Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> externalFronts, Map<TupleEdge<Vertex, Vertex>, Integer> orthogonalRep, Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> nexts, Map<TupleEdge<Vertex, Vertex>, Integer> orientations, TupleEdge[] outerRectangle) {

        int counter = orthogonalRep.get(edge);
        //TODO die Seiten und deren Numerierung des äußeren Rechtecks sind gleich zu den Integers der Orientation Map (bei einer Edge Anfangen, die opposite ist?)

        TupleEdge<Vertex, Vertex> tempEdge = nexts.get(edge);
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


    private void computeFronts(Map<TupleEdge<Vertex, Vertex>, Integer> orthogonalRep, Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> fronts, List<TupleEdge<Vertex, Vertex>> edgeList, Map<Vertex, TupleEdge<Vertex, Vertex>> vertexToFront) {
        /*   for (Datatypes.TupleEdge<Datatypes.TreeVertex, Datatypes.TreeVertex> edge : edgeList
        ) {
            if (orthogonalRep.get(edge) == -1) {
                findFront(edge, fronts2, orthogonalRep, nexts, vertexToFront);
            }

        }*/

        findfronts2(edgeList, fronts, orthogonalRep, vertexToFront);
    }


    private void findfronts2(List<TupleEdge<Vertex, Vertex>> edgeList, Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> fronts, Map<TupleEdge<Vertex, Vertex>, Integer> orthogonalRep, Map<Vertex, TupleEdge<Vertex, Vertex>> vertexToFront) {


        //TODO Hier könnte man die Stacks so anpassen, dass alle Kanten auf die stacks gelegt werden (also auch die mit 180° Winkeln), aber beim Stringbuilder werden sie ignoriert. Findet man ein 011 im STrink dann peeked und poopwed man so lange bis die 0 gefunden wird und baut so das Rectangular face.
        for (TupleEdge<Vertex, Vertex> edge : edgeList) {
            edge.setWinkel(orthogonalRep.get(edge));
        }
        boolean end = false;
        int endPos = -2; // Der Initiale Wert. Ist -2, weil dieser Wert niemals beim durchlauf erreicht werden kann.

        Deque<TupleEdge<Vertex, Vertex>> edgeStack = new ArrayDeque<>();
        Deque<TupleEdge<Vertex, Vertex>> frontStack = new ArrayDeque<>();


        int counter = 0;
        StringBuilder buf = new StringBuilder();
        // Der Bitstring buf(1,2,3) enthält nur Links und Rechtsknicke, die Kanten mit 180° können in diesem Schritt ignoriert werden
        // Durchlaufe alle Kanten *2 Um einen Zyklus zu Simulieren. Finde alle Knoten Kanten an denen ein Linksknick stattfindet (deren Endknoten ist der Startpunkt eines Rechteckes), baue den Edgestack auf und Baue den Bitstring auf.

        for (int i = 0; i < edgeList.size() * 2; i++) { // TODO hier noch fixen, dass nächste Kante nicht immer geholt werden muss

            TupleEdge<Vertex, Vertex> edge = edgeList.get(i % edgeList.size());
            TupleEdge<Vertex, Vertex> startEdge;
            TupleEdge<Vertex, Vertex> front;

            if (edge.getWinkel() == -1) {
                edgeStack.push(edge);
                frontStack.push(edge);
                counter = -1;
                buf.append(0);

                if (endPos == -2 && !end) { // diese Schleife soll so lange Laufen, bis der Erste Linksknick wieder erreicht wurde.
                    end = true;
                    endPos = i;
                }

            } else if (edge.getWinkel() == 1) {
                edgeStack.push(edge);
                edge.setCounter(++counter);
                buf.append(1);
            }




            // Falls wir ein Links-Rechts-Rechtsknick Muster Finden, dann haben wir ein mögliches Rechteck gefunden. Damit haben wir die Front für das oberste Element des Frontstacks gefunden und können dann die Beiden Kanten mit rechtem Winkel herauslöschen.
            // Damit hat man ein Rechteck entfernt und in der neuen Restfacette hat pop = frontStack.pop(pop), dann einen Rechtsknick anstelle eines Linksknick
            while (buf.length() >= 3 && buf.subSequence(buf.length() - 3, buf.length()).equals("011") && !frontStack.isEmpty()) {

                TupleEdge<Vertex, Vertex> pop = frontStack.pop();
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
        Deque<TupleEdge<Vertex, Vertex>> newEdgeStack = new ArrayDeque<>();
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
            TupleEdge<Vertex, Vertex> edge = edgeStack.pollLast();
            buf3.append(str.charAt(j++));
            newEdgeStack.push(edge);

            if (edgeStack.isEmpty()) {
                edgeStack = newEdgeStack;
            }


            while (buf3.length() >= 3 && buf3.subSequence(buf3.length() - 3, buf3.length()).equals("011") && !frontStack.isEmpty()) {

                TupleEdge<Vertex, Vertex> pop = frontStack.pop();
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



        for (TupleEdge<Vertex, Vertex> edge : fronts.keySet()) {

            ArrayList<Object> list = new ArrayList<>();
            ListIterator<TupleEdge<Vertex, Vertex>> iterator = edgeList.listIterator();
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

            TupleEdge<Vertex, Vertex> edge = edgeList.get(i % edgeList.size());
            TupleEdge<Vertex, Vertex> startEdge;
            TupleEdge<Vertex, Vertex> front;

            if (edge.getWinkel() == -1) {
                edgeStack.offer(edge);
                frontStack.offer(edge);
                counter = -1;
                buf2.append(0);

                if (endPos == -2 && !end) {
                    end = true;
                    endPos = i;
                }

            } else if (edge.getWinkel() == 1) {
                edgeStack.offer(edge);
                edge.setCounter(++counter);
                buf2.append(1);
            }


        }

     //   System.out.println("test");
    }


    private void projectEdge2
            (TupleEdge<Vertex, Vertex> front, Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> nexts, Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> fronts, Map<TupleEdge<Vertex, Vertex>, Integer> newOrthogonalRep) {

        originaledgeToFaceMap.remove(front);
        TupleEdge<Vertex, Vertex> possibleEdge = nexts.get(front);
        TupleEdge<Vertex, Vertex> beforeTempEdge = possibleEdge;
        List<TupleEdge<Vertex, Vertex>> edgeList = new ArrayList<>();
        List<TupleEdge<Vertex, Vertex>> newFront = new ArrayList<>();
        List<TupleEdge<Vertex, Vertex>> replacerEdge = new ArrayList<>();
        TupleEdge<Vertex, Vertex> originalFront = new TupleEdge<>(front.getLeft(), front.getRight());


        Deque<TupleEdge<Vertex, Vertex>> stack = new ArrayDeque<>();

        while (possibleEdge != front) {
            stack.push(possibleEdge);
            possibleEdge = nexts.get(possibleEdge);

        }

        TupleEdge<Vertex, Vertex> edgeBeforeFront = stack.getFirst();

        TupleEdge<Vertex, Vertex> front2 = new TupleEdge<>(front.getLeft(), front.getRight());
        nexts.put(edgeBeforeFront, front2);

        possibleEdge = nexts.get(front);


        while (possibleEdge != front2) {

            edgeList.add(possibleEdge);
            //TODO Hier noch viel Mist morgen genau überlegen was zu tun ist. Vielleicht alle Kanten Sammeln die die gleiche front haben, Dann deren Reihenfolge feststellen? Dann die neuen Zyklen aufbauen?


            if (front == fronts.get(possibleEdge)) {
                newFront.add(new TupleEdge<>(null, front2.getRight()));
                replacerEdge.add(new TupleEdge<>(front2.getRight(), null));


                Vertex newVertex = new Vertex(front2.getLeft().getName() + possibleEdge.getRight().getName() + " R", true);
                TupleEdge<Vertex, Vertex> newEdge = new TupleEdge<>(possibleEdge.getRight(), newVertex);

                edgeList.add(newEdge);
                TupleEdge<Vertex, Vertex> newEdge2 = new TupleEdge<>(newVertex, edgeList.get(0).getLeft());
                edgeList.add(newEdge2);

                newOrthogonalRep.put(possibleEdge, 0);
                newOrthogonalRep.put(newEdge, 1);
                newOrthogonalRep.put(newEdge2, newOrthogonalRep.get(front2));


                // Falls die Kante, welche eine Front hat geteilt wird, daher muss die Frontsmap geupdated werden, da sonst die front nicht getroffen wird
                if (fronts.get(front2) != null) {
                    fronts.put(newEdge2, fronts.get(front));
                }

                front2.setRight(newVertex);

                TupleEdge<Vertex, Vertex> followingEdge = nexts.get(possibleEdge);

                // Update nexts of new Face
                nexts.put(possibleEdge, newEdge);
                nexts.put(newEdge, newEdge2);
                nexts.put(newEdge2, edgeList.get(0));

                //Update the "rest" of the face

                TupleEdge<Vertex, Vertex> newEdgeReverse = new TupleEdge<>(front2.getRight(), possibleEdge.getRight());
                newOrthogonalRep.put(newEdgeReverse, 1);
                newOrthogonalRep.put(new TupleEdge<>(front2.getLeft(), front2.getRight()), 1);
                newFront.get((newFront.size() - 1)).setLeft(front2.getRight());
                replacerEdge.get((newFront.size() - 1)).setRight(front2.getRight());


                nexts.put(new TupleEdge<>(front2.getLeft(), front2.getRight()), newEdgeReverse);
                nexts.put(newEdgeReverse, followingEdge);


                possibleEdge = front2;
                //     System.out.println("Test");

                try {
                    for (TupleEdge<Vertex, Vertex> edge :
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
        TupleEdge<Vertex, Vertex> originalFrontReverse = GraphHelper.reverseEdge(originalFront, false);
        PlanarGraphFace<Vertex, DefaultEdge> neighbouringFace = originaledgeToFaceMap.get(originalFrontReverse);
        originaledgeToFaceMap.remove(originalFrontReverse);
        assert (neighbouringFace != null);

        List<TupleEdge<Vertex, Vertex>> neigbouringFaceEdgeList = neighbouringFace.getEdgeList();

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


    public void computeNexts2
            (List<TupleEdge<Vertex, Vertex>> edgeList, Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> nexts, Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> prevs) {


        for (int i = 0; i < edgeList.size(); i++) {

            nexts.put(edgeList.get(i), edgeList.get((i + 1) % edgeList.size()));
            prevs.put(edgeList.get((i + 1) % edgeList.size()), edgeList.get((i)));
        }


    }


}