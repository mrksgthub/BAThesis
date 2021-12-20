package Visualizer;

import Datastructures.PlanarGraphFace;
import Datastructures.TupleEdge;
import Datastructures.Vertex;
import org.apache.commons.lang3.time.StopWatch;

import java.util.*;

/**
 * Diese Klasse implementiert die Umwandlung der orthogonalen Repräsentation in eine orthogonale Repräsentation,
 * deren Facetten alle rechteckig sind.
 */
public class Rectangulator {

    private final List<PlanarGraphFace<Vertex>> planarGraphFaces;
    private final HashMap<PlanarGraphFace<Vertex>, PlanarGraphFace<Vertex>> rectangularFaceMap = new HashMap<>();
    private final List<PlanarGraphFace<Vertex>> rectangularInnerFaces = new ArrayList<PlanarGraphFace<Vertex>>();
    PlanarGraphFace<Vertex> outerFace = new PlanarGraphFace<>("externalFace");
    private HashMap<TupleEdge<Vertex, Vertex>, PlanarGraphFace<Vertex>> originalEdgeToFaceMap = new HashMap<>(2048);
    private int counter = 100;
    private List<TupleEdge<Vertex, Vertex>> startingEdges = new ArrayList<>();
    private TupleEdge<Vertex, Vertex> referenceEdge;


    public Rectangulator(List<PlanarGraphFace<Vertex>> planarGraphFaces) {
        this.planarGraphFaces = planarGraphFaces;
    }

    public List<PlanarGraphFace<Vertex>> getRectangularInnerFaces() {
        return rectangularInnerFaces;
    }

    public PlanarGraphFace<Vertex> getOuterFace() {
        return outerFace;
    }

    public void setOriginalTupleToFaceMap(HashMap<TupleEdge<Vertex, Vertex>, PlanarGraphFace<Vertex>> originalEdgeToFaceMap) {
        this.originalEdgeToFaceMap = originalEdgeToFaceMap;
    }

    /**
     * Wandelt die orthogonale Repräsentation, falls notwendig in eine aus rechteckigen Facetten bestehende um.
     * Das Ergebnis kann man sich mit getOuterFace() und getRectuangularInnerFaces() zurückgeben lassen.
     *
     * @throws Exception
     */
    public void run() throws Exception {
        Deque<PlanarGraphFace<Vertex>> dequeStack = new ArrayDeque<>(planarGraphFaces);
        PlanarGraphFace<Vertex> face;


        while (dequeStack.size() > 0) {
            face = dequeStack.pop();

            int count = 0;
            for (TupleEdge<Vertex, Vertex> edge : face.getEdgeList()) {
                if ((Math.abs(face.getEdgeAngle(edge)) == 1)) {
                    count++;
                }
            }
            if (count == 4) {
                if (face.getType() == PlanarGraphFace.FaceType.EXTERNAL) { // Das äußere Face ist schon rechteckig und kann gleich als äußere Facette festgelegt werden.
                    outerFace = face;
                    continue;
                } else {
                    rectangularFaceMap.put(face, face);
                    continue;
                }
            }

            //   face.setOrientationsOuterFacette();
            Map<TupleEdge<Vertex, Vertex>, Integer> orthogonalRep = face.getOrthogonalRep();
            Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> nexts = new HashMap<>();
            Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> prevs = new HashMap<>();
            Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> fronts = new HashMap<>();
            Map<Vertex, TupleEdge<Vertex, Vertex>> vertexToFront = new HashMap<>();
            Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> externalFronts = new HashMap<>();
            Map<TupleEdge<Vertex, Vertex>, Integer> newOrthogonalRep = new HashMap<>();
            computeNexts2(face.getEdgeList(), nexts, prevs);
            newOrthogonalRep.putAll(face.getOrthogonalRep());

            determineFronts(face, orthogonalRep, nexts, fronts, vertexToFront, externalFronts, newOrthogonalRep);

            buildRectangularFacesFromFace(dequeStack, face, nexts, newOrthogonalRep);

        }

        rectangularInnerFaces.addAll(rectangularFaceMap.keySet());
    }

    /**
     * Bestimme die Fronten der Facette
     *
     * @param face             Behandelte Facette
     * @param orthogonalRep    Map mit den Winkeln der Facette
     * @param nexts            Map, welche ausgibt welche das folgende TuplelEdge
     * @param fronts
     * @param vertexToFront
     * @param externalFronts
     * @param newOrthogonalRep
     */
    private void determineFronts(PlanarGraphFace<Vertex> face, Map<TupleEdge<Vertex, Vertex>, Integer> orthogonalRep, Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> nexts, Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> fronts, Map<Vertex, TupleEdge<Vertex, Vertex>> vertexToFront, Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> externalFronts, Map<TupleEdge<Vertex, Vertex>, Integer> newOrthogonalRep) throws Exception {
        if (face.getType() == PlanarGraphFace.FaceType.INTERNAL) {

            computeFronts(orthogonalRep, fronts, face.getEdgeList(), vertexToFront);
            projectFronts(nexts, fronts, newOrthogonalRep);

        } else { // Es handelt sich um die äußere Facette

            if (face.getType() == PlanarGraphFace.FaceType.EXTERNAL_PROCESSED) { // die ursprüngliche Äußere Facette wurde bearbeitet und ist nicht rechteckig, dann wird der rechteckige Rahmen um diese gezogen

                addOuterRectangleToOriginalOuterFace(face, nexts, newOrthogonalRep);
                computeExternalFront(orthogonalRep, externalFronts, nexts, face.getEdgeList());

                Set<TupleEdge<Vertex, Vertex>> externalFrontSet = new LinkedHashSet<>(externalFronts.values());
                List<TupleEdge<Vertex, Vertex>> edgeList = new ArrayList<>(externalFrontSet);

                // Um den Prozess zu vereinfachen wird nur eine externe Front, welche eine Dummykante mit der äußeren Facette bilden soll ausgewählt, dann wird diese Kante hinzugefügt und dann kann man den Rest wie eine innere Facette behandeln
                // fronts soll 0 sein, da wir das Externe Face schon einmal processed haben. So, dass die Kanten, deren Front eine andere Kante der äußeren Facette ist.

                TupleEdge<Vertex, Vertex> edge = edgeList.get(0); // die Kante, welche mit dem äußeren Rechteck verbunden werden soll
                projectExternalEdge(nexts, externalFronts, newOrthogonalRep);
                for (int i = 1; i < edgeList.size() - 1; i++) { // die anderen Kanten der
                    //      projectEdge2(edgeList.get(i), nexts, externalFronts, newOrthogonalRep);
                }
            } else { // process die äußere Facette, welche jetzt durch das äußere Rechteck eine innere Facette ist.

                System.out.println("Find Fronts:");
                StopWatch stopWatch = new StopWatch();

                stopWatch.start();

                stopWatch.stop();
                System.out.println(" StopWatch findfronts1: " + stopWatch.getTime());

                stopWatch.reset();

                stopWatch.start();

                findfronts2(face.getEdgeList(), fronts, vertexToFront);
                projectFronts(nexts, fronts, newOrthogonalRep);
                stopWatch.stop();


   /*            Set<TupleEdge<Vertex, Vertex>> frontSet = new LinkedHashSet<>(fronts.values());
                List<TupleEdge<Vertex, Vertex>> frontList = new ArrayList<>(frontSet);
                for (TupleEdge<Vertex, Vertex> edge : frontList
                ) {

                    projectEdge2(edge, nexts, fronts, newOrthogonalRep);
                }*/
                System.out.println(" StopWatch findFronts2: " + stopWatch.getTime());

                face.setType(PlanarGraphFace.FaceType.EXTERNAL_PROCESSED);
            }


        }
    }

    /**
     * Wandedlt eine Facette in eine rechteckige Facette um.
     *
     * @param dequeStack
     * @param face
     * @param nexts
     * @param newOrthogonalRep
     */
    private void buildRectangularFacesFromFace(Deque<PlanarGraphFace<Vertex>> dequeStack, PlanarGraphFace<Vertex> face, Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> nexts, Map<TupleEdge<Vertex, Vertex>, Integer> newOrthogonalRep) {
        // Nachdem die Dummykanten und Knoten hinzugefügt wurden bestimmen wir die neuen Facetten, in dem wir die
        // Kanten in nexts Ablaufen. Dazu haben wir die Startedges (die neu hinzugefügten Kanten) in startingEdges
        // gespeichert und nutzen die als Ausgangspunkt, um die neuen Facetten zu finden.


        HashMap<TupleEdge<Vertex, Vertex>, Boolean> visitedMap = new HashMap<>();

        //     boolean processed = false;
        for (TupleEdge<Vertex, Vertex> pair :
                nexts.keySet()
        ) {
            visitedMap.put(pair, false);
        }
        for (TupleEdge<Vertex, Vertex> pair :
            //  visitedMap.keySet()
                startingEdges
        ) {
            //  processed = true;
            if (!visitedMap.get(pair)) {
                PlanarGraphFace<Vertex> faceObj = new PlanarGraphFace<>(Integer.toString(counter++));

                rectangularFaceMap.put(faceObj, face);
                faceObj.setEdgeAngle(pair, newOrthogonalRep.get(pair));

                faceObj.getEdgeList().add(pair);


                originalEdgeToFaceMap.put(pair, faceObj);

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

                    faceObj.setEdgeAngle(iterator, newOrthogonalRep.get(iterator));
                    faceObj.getEdgeList().add(iterator);

                    counter += newOrthogonalRep.get(iterator);
                    if (newOrthogonalRep.get(iterator) == 1) {
                        counter2++;
                    }
                    originalEdgeToFaceMap.put(iterator, faceObj);
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

                for (TupleEdge<Vertex, Vertex> edge : faceObj.getEdgeList()
                ) {

                }
            }
        }
        if (startingEdges.size() == 0 && face.getType() == PlanarGraphFace.FaceType.EXTERNAL_PROCESSED && startingEdges.size() == 0) {
            dequeStack.push(face);
        }

        // Original inner face was rectangular to begin with
        if (startingEdges.size() == 0 && face.getType() != PlanarGraphFace.FaceType.EXTERNAL_PROCESSED) {
            rectangularFaceMap.put(face, face);
        }
 /*       if (startingEdges.size() == 0 && !face.getName().equals("0")) {
            rectangularFaceMap.put(face, face);
        }*/


        startingEdges = new ArrayList<>();
        //    boolean test = originaledgeToFaceMap.containsValue(face);

        //     System.out.println("Test");
    }

    private void projectFronts(Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> nexts, Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> fronts, Map<TupleEdge<Vertex, Vertex>, Integer> newOrthogonalRep) throws Exception {
        Set<TupleEdge<Vertex, Vertex>> frontSet = new HashSet<>(fronts.values());
        List<TupleEdge<Vertex, Vertex>> frontList = new ArrayList<>(frontSet);
        for (TupleEdge<Vertex, Vertex> edge : frontList
        ) {
            projectEdge2(edge, nexts, fronts, newOrthogonalRep);
        }
    }


    /**
     * Fügt das äußere Rechteck zur prozessierten äu0eren Facette hinzu
     *
     * @param face             - die äu0ere Facette
     * @param nexts            - die nexts()-Funktion aus dem Buch
     * @param newOrthogonalRep - die modifizierte OrthogonalRep, welche genutzt wird um die rechteckigen Facetten zu bilden.
     * @return
     */
    private TupleEdge[] addOuterRectangleToOriginalOuterFace(PlanarGraphFace<Vertex> face, Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> nexts, Map<TupleEdge<Vertex, Vertex>, Integer> newOrthogonalRep) {
        // die alte äußere Facette bekommt jetzt das Rchteck, welches die neue äußere Facette wird zur sich hinzugefügt und so wird "face" zu einer inneren Facette.
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
    /*    face.getEdgeOrientationMap().put(edge1, 0);
        face.getEdgeOrientationMap().put(edge2, 1);
        face.getEdgeOrientationMap().put(edge3, 2);
        face.getEdgeOrientationMap().put(edge4, 3);*/

        referenceEdge = edge1;

        // Die äußere Facette wird erzeugt.


        TupleEdge<Vertex, Vertex> edge1Rev = TupleEdge.reverseEdge(edge1, -1);
        TupleEdge<Vertex, Vertex> edge2Rev = TupleEdge.reverseEdge(edge2, -1);
        TupleEdge<Vertex, Vertex> edge3Rev = TupleEdge.reverseEdge(edge3, -1);
        TupleEdge<Vertex, Vertex> edge4Rev = TupleEdge.reverseEdge(edge4, -1);
/*        outerFace.getEdgeOrientationMap().put(edge1Rev, 0);
        outerFace.getEdgeOrientationMap().put(edge2Rev, 1);
        outerFace.getEdgeOrientationMap().put(edge3Rev, 2);
        outerFace.getEdgeOrientationMap().put(edge4Rev, 3);*/
        outerFace.getEdgeList().add(edge1Rev);
        outerFace.getEdgeList().add(edge4Rev);
        outerFace.getEdgeList().add(edge3Rev);
        outerFace.getEdgeList().add(edge2Rev);
        outerFace.setEdgeAngle(edge1Rev, -1);
        outerFace.setEdgeAngle(edge2Rev, -1);
        outerFace.setEdgeAngle(edge3Rev, -1);
        outerFace.setEdgeAngle(edge4Rev, -1);
        for (TupleEdge<Vertex, Vertex> edge :
                outerFace.getEdgeList()) {
            originalEdgeToFaceMap.put(edge, outerFace);

        }

        newOrthogonalRep.put(edge1, 1);
        newOrthogonalRep.put(edge2, 1);
        newOrthogonalRep.put(edge3, 1);
        newOrthogonalRep.put(edge4, 1);
        //    face.setOrientationsOuterFacette();
        return outerRectangle;
    }


    private void projectExternalEdge(Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> nexts, Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> externalFronts, Map<TupleEdge<Vertex, Vertex>, Integer> newOrthogonalRep) throws Exception {
        TupleEdge<Vertex, Vertex> front = referenceEdge;
        originalEdgeToFaceMap.remove(front);
        TupleEdge<Vertex, Vertex> possibleEdge = nexts.get(front);
        List<TupleEdge<Vertex, Vertex>> edgeList = new ArrayList<>();
        List<TupleEdge<Vertex, Vertex>> newFront = new ArrayList<>(); // Die Alte Front wird durch DummyKnoten unterteilt und die so enstehende Kette wird in newFront gespeichert.
        List<TupleEdge<Vertex, Vertex>> newFrontReverse = new ArrayList<>();
        TupleEdge<Vertex, Vertex> originalFront = new TupleEdge<>(front.getLeft(), front.getRight());

        //    assert (orientations.get(front) != null);
        //     int rectangleEdge = orientations.get(front);
        //   possibleEdge = sidesMap.get((rectangleEdge + 2) % 4).get(sidesMap.get((rectangleEdge + 2) % 4).size() - 1);
        TupleEdge<Vertex, Vertex> startEdge = possibleEdge;

        /*  while (possibleEdge != front) {*/

        edgeList.add(possibleEdge);

        /*   if (front == externalFronts.get(possibleEdge)) {*/
        //
        possibleEdge = externalFronts.keySet().iterator().next();


        Vertex newVertex = new Vertex(front.getLeft().getName() + possibleEdge.getRight().getName() + " R", true);
        // die extendede Edge im neuen Rectangle
        TupleEdge<Vertex, Vertex> projectedEdge = new TupleEdge<>(possibleEdge.getRight(), newVertex);
        // die extended Edge in reverse
        //       TupleEdge<Vertex, Vertex> projectedEdgeReverse = new TupleEdge<>(newVertex, possibleEdge.getRight());
        TupleEdge<Vertex, Vertex> projectedEdgeReverse = TupleEdge.reverseEdge(projectedEdge);

        edgeList.add(projectedEdge);
        // der Rest der alten Front
        TupleEdge<Vertex, Vertex> restOfOldFront = new TupleEdge<>(newVertex, front.getRight());
        edgeList.add(restOfOldFront);
        newFrontReverse.add(new TupleEdge<>(restOfOldFront.getRight(), restOfOldFront.getLeft()));

        newOrthogonalRep.put(possibleEdge, 0);
        //      possibleEdge.setWinkel(0);
        newOrthogonalRep.put(projectedEdge, 1);
        //       projectedEdge.setWinkel(1);
        newOrthogonalRep.put(restOfOldFront, newOrthogonalRep.get(front));
        //      restOfOldFront.setWinkel(newOrthogonalRep.get(front));

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
        newOrthogonalRep.put(new TupleEdge<>(front.getLeft(), front.getRight()), 1);

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
        /*    }*/


     /*       possibleEdge = nexts.get(possibleEdge);
            if (possibleEdge == startEdge) {
                break;
            }*/

        /*     }*/


        newFront.add(new TupleEdge<>(front.getLeft(), front.getRight()));
        //TODO orthogonalRep der Front im Benachbarten Vertex updaten
        newFrontReverse.add(new TupleEdge<>((front.getRight()), front.getLeft()));
        TupleEdge<Vertex, Vertex> originalFrontReverse = TupleEdge.reverseEdge(originalFront);
        PlanarGraphFace<Vertex> neighbouringFace = originalEdgeToFaceMap.get(originalFrontReverse);
        originalEdgeToFaceMap.remove(originalFrontReverse);
        assert (neighbouringFace != null);

        List<TupleEdge<Vertex, Vertex>> neigbouringFaceEdgeList = neighbouringFace.getEdgeList();

        for (int i = 0; i < newFrontReverse.size() - 1; i++) {
            neighbouringFace.setEdgeAngle(newFrontReverse.get(i), 0);
            originalEdgeToFaceMap.put(newFrontReverse.get(i), neighbouringFace);
        }
        neighbouringFace.setEdgeAngle(newFrontReverse.get(newFrontReverse.size() - 1), neighbouringFace.getEdgeAngle(originalFrontReverse));
        originalEdgeToFaceMap.put(newFrontReverse.get(newFrontReverse.size() - 1), neighbouringFace);

        int pos = neigbouringFaceEdgeList.indexOf(originalFrontReverse);

        neigbouringFaceEdgeList.remove(pos);
        neigbouringFaceEdgeList.addAll(pos, newFrontReverse);

        startingEdges.addAll(newFront);

        System.out.println("End of projectExternalFace");
    }

    private void projectExternalEdge2(TupleEdge<Vertex, Vertex> front, Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> nexts, Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> externalFronts, Map<TupleEdge<Vertex, Vertex>, Integer> newOrthogonalRep, Map<TupleEdge<Vertex, Vertex>, Integer> orientations, Map<Integer, ArrayList<TupleEdge<Vertex, Vertex>>> sidesMap) throws Exception {
        originalEdgeToFaceMap.remove(front);
        TupleEdge<Vertex, Vertex> possibleEdge = nexts.get(front);
        List<TupleEdge<Vertex, Vertex>> edgeList = new ArrayList<>();
        List<TupleEdge<Vertex, Vertex>> newFront = new ArrayList<>(); // Die Alte Front wird durch DummyKnoten unterteilt und die so enstehende Kette wird in newFront gespeichert.
        List<TupleEdge<Vertex, Vertex>> newFrontReverse = new ArrayList<>();
        TupleEdge<Vertex, Vertex> originalFront = new TupleEdge<>(front.getLeft(), front.getRight());

        assert (orientations.get(front) != null);
        int rectangleEdge = orientations.get(front);
        possibleEdge = sidesMap.get((rectangleEdge + 2) % 4).get(sidesMap.get((rectangleEdge + 2) % 4).size() - 1);
        TupleEdge<Vertex, Vertex> startEdge = possibleEdge;

        while (possibleEdge != front) {

            edgeList.add(possibleEdge);

            if (front == externalFronts.get(possibleEdge)) {
                //
                //  possibleEdge = externalFronts.keySet().iterator().next();
                //   front = referenceEdge;

                Vertex newVertex = new Vertex(front.getLeft().getName() + possibleEdge.getRight().getName() + " R", true);
                // die extendede Edge im neuen Rectangle
                TupleEdge<Vertex, Vertex> projectedEdge = new TupleEdge<>(possibleEdge.getRight(), newVertex);
                // die extended Edge in reverse
                //       TupleEdge<Vertex, Vertex> projectedEdgeReverse = new TupleEdge<>(newVertex, possibleEdge.getRight());
                TupleEdge<Vertex, Vertex> projectedEdgeReverse = TupleEdge.reverseEdge(projectedEdge);

                edgeList.add(projectedEdge);
                // der Rest der alten Front
                TupleEdge<Vertex, Vertex> restOfOldFront = new TupleEdge<>(newVertex, front.getRight());
                edgeList.add(restOfOldFront);
                newFrontReverse.add(new TupleEdge<>(restOfOldFront.getRight(), restOfOldFront.getLeft()));

                newOrthogonalRep.put(possibleEdge, 0);
                //      possibleEdge.setWinkel(0);
                newOrthogonalRep.put(projectedEdge, 1);
                //       projectedEdge.setWinkel(1);
                newOrthogonalRep.put(restOfOldFront, newOrthogonalRep.get(front));
                //      restOfOldFront.setWinkel(newOrthogonalRep.get(front));

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
                newOrthogonalRep.put(new TupleEdge<>(front.getLeft(), front.getRight()), 1);

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
        newFrontReverse.add(new TupleEdge<>((front.getRight()), front.getLeft()));
        TupleEdge<Vertex, Vertex> originalFrontReverse = TupleEdge.reverseEdge(originalFront);
        PlanarGraphFace<Vertex> neighbouringFace = originalEdgeToFaceMap.get(originalFrontReverse);
        originalEdgeToFaceMap.remove(originalFrontReverse);
        assert (neighbouringFace != null);

        List<TupleEdge<Vertex, Vertex>> neigbouringFaceEdgeList = neighbouringFace.getEdgeList();

        for (int i = 0; i < newFrontReverse.size() - 1; i++) {
            neighbouringFace.setEdgeAngle(newFrontReverse.get(i), 0);
            originalEdgeToFaceMap.put(newFrontReverse.get(i), neighbouringFace);
        }
        neighbouringFace.setEdgeAngle(newFrontReverse.get(newFrontReverse.size() - 1), neighbouringFace.getEdgeAngle(originalFrontReverse));
        originalEdgeToFaceMap.put(newFrontReverse.get(newFrontReverse.size() - 1), neighbouringFace);

        int pos = neigbouringFaceEdgeList.indexOf(originalFrontReverse);

        neigbouringFaceEdgeList.remove(pos);
        neigbouringFaceEdgeList.addAll(pos, newFrontReverse);

        startingEdges.addAll(newFront);

    }


    private void computeExternalFront(Map<TupleEdge<Vertex, Vertex>, Integer> orthogonalRep, Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> externalFronts, Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> nexts, List<TupleEdge<Vertex, Vertex>> edgeList) {

        for (TupleEdge<Vertex, Vertex> edge : edgeList
        ) {
            if (edge.getWinkel() == -1) {
                findexternalFront(edge, externalFronts, orthogonalRep, nexts);
            }


        }

    }

    private void findfronts2(List<TupleEdge<Vertex, Vertex>> edgeList, Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> fronts, Map<Vertex, TupleEdge<Vertex, Vertex>> vertexToFront) {


        //TODO Hier könnte man die Stacks so anpassen, dass alle Kanten auf die stacks gelegt werden (also auch die mit 180° Winkeln), aber beim Stringbuilder werden sie ignoriert. Findet man ein 011 im STrink dann peeked und poopwed man so lange bis die 0 gefunden wird und baut so das Rectangular face.
  /*      for (TupleEdge<Vertex, Vertex> edge : edgeList) {
            edge.setWinkel(orthogonalRep.get(edge));
        }*/
        boolean end = false;
        int endPos = -2; // Der Initiale Wert. Ist -2, weil dieser Wert niemals beim durchlauf erreicht werden kann.

        Deque<TupleEdge<Vertex, Vertex>> edgeStack = new ArrayDeque<>();
        Deque<TupleEdge<Vertex, Vertex>> frontStack = new ArrayDeque<>();


        int counter = 0;
        StringBuilder buf = new StringBuilder();
        // Der Bitstring buf(1,2,3) enthält nur Links und Rechtsknicke, die Kanten mit 180° können in diesem Schritt ignoriert werden
        // Durchlaufe alle Kanten *2 Um einen Zyklus zu Simulieren. Finde alle Knoten Kanten an denen ein Linksknick stattfindet (deren Endknoten ist der Startpunkt eines Rechteckes), baue den Edgestack auf und Baue den Bitstring auf.

        for (int i = 0; i < edgeList.size() * 2; i++) {  // Der erste Durchlauf, bei dem der Edgestack inilialisiert wird.

            TupleEdge<Vertex, Vertex> edge = edgeList.get(i % edgeList.size());

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

            if (i == edgeList.size() - 1 && end) {
                break;
            }

        }


        // Ab hier arbeiten wir mit 2 Stacks. Einer davon ersetzt die Liste der TupelEdge und der zweite Edgestack enthält die Kanten, der restlichen nicht rechteckigen Facette.

        StringBuilder buf3 = new StringBuilder();
        Deque<TupleEdge<Vertex, Vertex>> newEdgeStack = new ArrayDeque<>();


        /*         while (!frontStack.isEmpty()) {*/
        String str = "";
        int j = buf.indexOf("0");
        if (j > 0) {
            // Da wir den Fall von 1111110 beachten müssen, rotieren wir den Bitstring so, dass der Bitstring mit 0 beginnt.
            str = buf.substring(j) + buf.substring(0, j);

            //  die Reihenfolge von edgeStack wird an den Bitstring angepasst.
            for (int k = 0; k < j; k++) {
                edgeStack.push(edgeStack.pollLast());
            }
        }


        j = 0; // Index in str, während des Durchlaufens des while Loops
        while (!frontStack.isEmpty() && j != str.length()) {

            TupleEdge<Vertex, Vertex> edge = edgeStack.pollLast();
            buf3.append(str.charAt(j++));
            newEdgeStack.push(edge);

            if (edgeStack.isEmpty()) { // Kann so interpretiert werden, dass man wieder am Ende der Kantenliste angekommen
                // angekommen ist und man beginnt von vorne.
                edgeStack = newEdgeStack;
            }

            // while-loop, da nach finden der einer Front und dem Ersetzen von 011 durch 1 hat man möglicherweise schon die nächste front gefunden.
            while (buf3.length() >= 3 && buf3.subSequence(buf3.length() - 3, buf3.length()).equals("011") && !frontStack.isEmpty()) {

                TupleEdge<Vertex, Vertex> pop = frontStack.pop();
                fronts.put(pop, edgeStack.peekLast());
                vertexToFront.put(pop.getRight(), edgeStack.peek());
                buf3.replace(buf3.length() - 3, buf3.length(), "1");
                newEdgeStack.pop();
                newEdgeStack.pop();
            }

            // Falls man am Ende angekommen wird der edgeStack und Bitstring, wie oben beschrieben, rotiert.
            if (j == str.length()) {
                int i = buf3.indexOf("0");
                if (i > -1) {
                    str = buf3.substring(i) + buf3.substring(0, i);
                    if (!str.contains("11")) { // Falls kein 11 übrig ist, dann ist es nicht möglich eine neue Front zu finden (relevant für äußere Facetten)
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

        //    System.out.println("test");
    }


    private void findexternalFront(TupleEdge<Vertex, Vertex> edge, Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> externalFronts, Map<TupleEdge<Vertex, Vertex>, Integer> orthogonalRep, Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> nexts) {


        int counter = edge.getWinkel();


        TupleEdge<Vertex, Vertex> tempEdge = nexts.get(edge);
        while (counter != 1 && edge != tempEdge) {

            counter += tempEdge.getWinkel();
            tempEdge = nexts.get(tempEdge);
        }
        if (edge != tempEdge) {
            //       fronts.put(edge, tempEdge);
        } else {
            externalFronts.put(edge, referenceEdge);
        }


    }


    private void computeFronts(Map<TupleEdge<Vertex, Vertex>, Integer> orthogonalRep, Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> fronts, List<TupleEdge<Vertex, Vertex>> edgeList, Map<Vertex, TupleEdge<Vertex, Vertex>> vertexToFront) {

        findfronts2(edgeList, fronts, vertexToFront);
    }


    private void projectEdge2
            (TupleEdge<Vertex, Vertex> front, Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> nexts, Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> fronts, Map<TupleEdge<Vertex, Vertex>, Integer> newOrthogonalRep) throws Exception {

        originalEdgeToFaceMap.remove(front);
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
        TupleEdge<Vertex, Vertex> originalFrontReverse = TupleEdge.reverseEdge(originalFront);
        PlanarGraphFace<Vertex> neighbouringFace = originalEdgeToFaceMap.get(originalFrontReverse);
        originalEdgeToFaceMap.remove(originalFrontReverse);
        assert (neighbouringFace != null);

        List<TupleEdge<Vertex, Vertex>> neigbouringFaceEdgeList = neighbouringFace.getEdgeList();

        for (int i = 0; i < replacerEdge.size() - 1; i++) {
            neighbouringFace.setEdgeAngle(replacerEdge.get(i), 0);
            originalEdgeToFaceMap.put(new TupleEdge<>(replacerEdge.get(i).getLeft(), replacerEdge.get(i).getRight()), neighbouringFace);
        }

        neighbouringFace.setEdgeAngle(replacerEdge.get(replacerEdge.size() - 1), neighbouringFace.getEdgeAngle(originalFrontReverse));
        originalEdgeToFaceMap.put(new TupleEdge<>(replacerEdge.get(replacerEdge.size() - 1).getLeft(), replacerEdge.get(replacerEdge.size() - 1).getRight()), neighbouringFace);

        int pos = neigbouringFaceEdgeList.indexOf(originalFrontReverse);
        assert (pos > -1);
        neigbouringFaceEdgeList.remove(pos);
        neigbouringFaceEdgeList.addAll(pos, replacerEdge);

        nexts.remove(front);
        startingEdges.addAll(newFront);

    }


    private void computeNexts2
            (List<TupleEdge<Vertex, Vertex>> edgeList, Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> nexts, Map<TupleEdge<Vertex, Vertex>, TupleEdge<Vertex, Vertex>> prevs) {


        for (int i = 0; i < edgeList.size(); i++) {

            nexts.put(edgeList.get(i), edgeList.get((i + 1) % edgeList.size()));
            prevs.put(edgeList.get((i + 1) % edgeList.size()), edgeList.get((i)));
        }


    }


}