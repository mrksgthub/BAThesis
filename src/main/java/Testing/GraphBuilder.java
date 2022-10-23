package Testing;

import Datastructures.SPQNode;
import Datastructures.SPQStarTree;
import Datastructures.Vertex;
import GraphGenerators.SPQGenerator;
import Helperclasses.SPQExporter;
import Helperclasses.SPQImporter;
import PlanarityAndAngles.FaceGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Ist verantworlich dafür, dass eine  SPQ*-Bäumen mit den gewählten Paramtern erzeugt werden und dann mit einem
 * generierten Namen an der richtigen Stelle abgespeichert werden.
 *
 *
 */
public class GraphBuilder {

    private final String filePathString;
    private static int counter = 0;

    /**
     * Implementiert die Methode einen SPQ*-Bäum zu generieren und in das
     * angegebene Verzeichnis im DOT-Format zu exportieren.
     *
     *
     * @param filePathString Zielverzeichnis
     */
    public GraphBuilder(String filePathString) {

        this.filePathString = filePathString;
    }

    /**
     * Führt die Erzeugung und das exportieren des SPQ*-Baums mit den entsprechenden Parametern durch
     *
     *
     * @param CHANCE_OF_P Wahrscheinlichkeit einen P-Knoten hinzuzufügen
     * @param nodes maximale Knotenanzahl
     * @param maxDeg maximaler Knotengrad
     * @param mode Modus
     * @param isInvalidAllowed Dürfen nicht rektilinear planare Graphen gespeichert werden.
     * @return ist der Graph rektilinear planar.
     */
    public boolean run(int CHANCE_OF_P, int nodes, int maxDeg, int mode, boolean isInvalidAllowed) {


        SPQStarTree tree;
        SPQNode root;


        SPQGenerator spqGenerator = new SPQGenerator();
        boolean valid = spqGenerator.generateGraph(nodes, CHANCE_OF_P, maxDeg, mode);


        if (valid || isInvalidAllowed) {
            tree = spqGenerator.getTree();
            root = spqGenerator.getRoot();

            DirectedMultigraph<Vertex, DefaultEdge> graph = tree.getConstructedGraph();
            FaceGenerator<Vertex, DefaultEdge> treeVertexFaceGenerator = new FaceGenerator<>(tree.getConstructedGraph(), root.getSourceVertex(), root.getSinkVertex());
            treeVertexFaceGenerator.generateFaces();

       //     System.out.println("Anzahl Faces:" + treeVertexFaceGenerator.getPlanarGraphFaces().size());

            int faces = treeVertexFaceGenerator.getPlanarGraphFaces().size();
            int numberOfNodes = graph.vertexSet().size();


            SPQExporter spqExporter = new SPQExporter();
            File filePath = new File(filePathString,
                    numberOfNodes + "N" + faces + "F"+ "D"+counter++ +".dot");

            spqExporter.run(root, filePath.toString());
            return true;
        } else {
            return false;
        }



    }


}
