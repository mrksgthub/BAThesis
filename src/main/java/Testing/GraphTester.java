package Testing;

import Datastructures.SPQNode;
import Datastructures.SPQStarTree;
import Datastructures.Vertex;
import Helperclasses.DFSIterator;
import Helperclasses.GraphValidifier;
import Helperclasses.SPQImporter;
import PlanarityAndAngles.Angulator;
import PlanarityAndAngles.Didimo.DidimoRepresentability;
import PlanarityAndAngles.FaceGenerator;
import PlanarityAndAngles.Flow.MaxFlow;
import PlanarityAndAngles.Flow.MinFlow;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedMultigraph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;


/**
 * Diese Klasse stellt die Fähigkeit zum batch-Testen von SPQ*-Baum .dot Dateien, oder eines ganzen Verzeichnisses
 * zur Verfügung. Speichert das Ergebnis des Tests in einer .csv Datei.
 *
 */
public class GraphTester {

    private static final String SAMPLE_CSV_FILE = "C:/a.csv";

    private final File outputFile;
    private final File[] inputFiles;
    private final int runs = 15;


    public GraphTester(File outputFile, File[] inputFiles) {
        this.outputFile = outputFile;
        this.inputFiles = inputFiles;
    }



    /**
    https://stackoverflow.com/questions/1844688/how-to-read-all-files-in-a-folder-from-java
     */
    private static void listFilesForFolder(final File folder, List<String> listOfFiles) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry, listOfFiles);
            } else {
                System.out.println(fileEntry.getAbsolutePath());
                listOfFiles.add(fileEntry.getAbsolutePath());
            }
        }
    }

    /**
     * Führt den Test auf den dem GraphTester-Objekt übergebenen Graphen aus. (Entweder Dateien, oder Verzeichnis).
     * minSize und maxSize legen die Knotengröße fest, die der Graph haben darf.
     * tamassiaMinCostAllowed überprüft, ob der Capacity-Scaling Ansatz getestet werden soll.
     *
     * @param minSize                Falls auf <0 gesetzt, dann gibt es keine minimale Größe
     * @param maxSize                Falls auf <0 gesetzt, dann gibt es keine maximale Größe
     * @param tamassiaMinCostAllowed
     */
    public void run(int minSize, int maxSize, boolean tamassiaMinCostAllowed) {

        SPQStarTree tree;
        SPQNode root;

        try (
                BufferedWriter writer = Files.newBufferedWriter(
                        outputFile.toPath(),
                        StandardOpenOption.APPEND,
                        StandardOpenOption.CREATE);

                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                        .withHeader("Graph", "Size", "Degree3Vertices", "Degree4Vertices", "FlownetworkKantenanzahl",
                                "SPQ*Knoten", "DidimoMean", "DidimoMedian", "DidimoStdev", "Tamassia", "TamassiaMedian", "TamassiaStdDev", "TamassiaPush", "TamassiaPushMedian",
                                "TamassiaPushStdev", "FordFulkerson", "FordFulkersonStdev", "rectilinear planar", "SnodeChildren"))
        ) {

            for (File fileName : inputFiles
            ) {

             //   System.out.println(fileName);
                SPQImporter spqImporter = new SPQImporter();
                spqImporter.runFromFile(fileName.toString());

                tree = spqImporter.getTree();
                root = tree.getRoot();


                FaceGenerator<Vertex, DefaultEdge> treeVertexFaceGenerator = new FaceGenerator<>(tree.getConstructedGraph(), root.getSourceVertex(), root.getSinkVertex());
                treeVertexFaceGenerator.generateFaces();

                if (maxSize < 0 || tree.getConstructedGraph().vertexSet().size() <= maxSize) {
                    if (minSize < 0 || tree.getConstructedGraph().vertexSet().size() >= minSize) {
                     //   System.gc();
                        runAllTests(tree, root, csvPrinter, fileName, spqImporter, treeVertexFaceGenerator, tamassiaMinCostAllowed);
                    }
                }

            }
            csvPrinter.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /**
     * Führt die Tests für den SPQ*-Baum, der als Eingabe dient aus und speichert die Messwerte dann in csvPrinter.
     *
     *
     * @param tree Der SPQ*-Baum und Graph, der gemessen werden soll
     * @param root Wurzel des SPQ*-Baums
     * @param csvPrinter speichert die Messergebnisse.
     * @param fileName
     * @param spqImporter
     * @param treeVertexFaceGenerator
     * @param tamassiaMinCostAllowed - soll der Minimalkostenalgorithmus ausgeführt werden.
     * @throws Exception
     */
    private void runAllTests(SPQStarTree tree, SPQNode root, CSVPrinter csvPrinter, File fileName, SPQImporter spqImporter, FaceGenerator<Vertex, DefaultEdge> treeVertexFaceGenerator, boolean tamassiaMinCostAllowed) throws Exception {
        try {
            GraphValidifier graphValidifier = new GraphValidifier(tree.getConstructedGraph(), treeVertexFaceGenerator.getPlanarGraphFaces());
            graphValidifier.run();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Fehler in Helperclasses.GraphValidifier");
        }

        DirectedMultigraph<Vertex, DefaultEdge> graph = spqImporter.getTree().getConstructedGraph();
        int faces = treeVertexFaceGenerator.getPlanarGraphFaces().size();
        int nodes = graph.vertexSet().size();
       // System.out.println(fileName);
        long startTime = System.nanoTime();
        long stopTime;
        long elapsedTime;
        double didimoTime;
        double didimoMedian = 0;
        double didimoStdev = 0;
        double tamassiaMinFlowTime;
        double tamassiaMinFlowMedian = 0;
        double tamassiaMinFlowStdDev = 0;
        double FordFulkerson = 0;
        double FordFulkersonStdev = 0;
        double tamassiaPushTime = 0;
        double tamassiaPushMedian = 0;
        double tamassiaPushStdev = 0;
        int flowNetWorkEdges = 0;
        int degree3Vertices = 0;
        int degree4Vertices = 0;
        int notQnodeCount = 0;
        int sNodeCountChildren = 0;
        boolean isRectilinear = false;
        Deque<SPQNode> s = DFSIterator.buildPostOrderStack(root);

        // Informationen über den Graphen sammeln
        while (!s.isEmpty()) {
            SPQNode node = s.pop();
            notQnodeCount += (node.getSpqChildren().size() != 0) ? 1 : 0;

            if (node.getNodeType() == SPQNode.NodeTypesEnum.NODETYPE.S) {
                sNodeCountChildren += node.getSpqChildren().size();
            }
        }

        // notQnodeCount = s.size();


        for (Vertex v : tree.getConstructedGraph().vertexSet()
        ) {
            if (v.getAdjacentVertices().size() == 3) {
                degree3Vertices++;
            }
            if (v.getAdjacentVertices().size() == 4) {
                degree4Vertices++;
            }
        }


        try {
            DescriptiveStatistics statsDidimo = new DescriptiveStatistics();
            // Didimo Test
            for (int i = 0; i < runs; i++) {

                tree = spqImporter.runFromArray();
                treeVertexFaceGenerator = new FaceGenerator<>(tree.getConstructedGraph(), root.getSourceVertex(), root.getSinkVertex());
                treeVertexFaceGenerator.generateFaces();
                startTime = System.nanoTime();
                DidimoRepresentability didimoRepresentability = new DidimoRepresentability();
                isRectilinear = didimoRepresentability.run(tree.getRoot());
                stopTime = System.nanoTime();
                elapsedTime = stopTime - startTime;
                statsDidimo.addValue(elapsedTime);
            }
            didimoTime = statsDidimo.getMean();
            didimoStdev = statsDidimo.getStandardDeviation();
            didimoMedian = statsDidimo.getPercentile(50);


        } catch (Exception e) {
            e.printStackTrace();
            didimoTime = -1;
        }

        // Netzwerkflussansatz MinCostTest
        if (tamassiaMinCostAllowed && (nodes < 20000 || faces > 2)) {
            tree = spqImporter.runFromArray();

            DescriptiveStatistics statsTamassiaMinFlow = new DescriptiveStatistics();

            for (int i = 0; i < runs; i++) {
                MinFlow tamassiaRepresentation = new MinFlow(treeVertexFaceGenerator.getPlanarGraphFaces());
                tamassiaRepresentation.generateFlowNetwork();
                startTime = System.nanoTime();
                tamassiaRepresentation.runJGraphTMinCostFlowPlanarityTest();
                stopTime = System.nanoTime();
                elapsedTime = stopTime - startTime;
                statsTamassiaMinFlow.addValue(elapsedTime);

            }
            tamassiaMinFlowTime = statsTamassiaMinFlow.getMean();
            tamassiaMinFlowStdDev = statsTamassiaMinFlow.getStandardDeviation();
            tamassiaMinFlowMedian = statsTamassiaMinFlow.getPercentile(50);
        } else {
            tamassiaMinFlowTime = -1;
            tamassiaMinFlowStdDev = -1;

        }


        try {
            DescriptiveStatistics statsTamassiaPush = new DescriptiveStatistics();
            // PushRelabel Test
            for (int i = 0; i < runs; i++) {

                tree = spqImporter.runFromArray();

                MaxFlow test = new MaxFlow(treeVertexFaceGenerator.getPlanarGraphFaces());
                test.generateFlowNetwork();
                startTime = System.nanoTime();
                test.runJGraphTPushRelabelPlanarityTest();
                stopTime = System.nanoTime();
                elapsedTime = stopTime - startTime;
                statsTamassiaPush.addValue(elapsedTime);
                flowNetWorkEdges = test.getFlowMap().keySet().size();
                int count = 0;
                for (DefaultWeightedEdge edge : test.getFlowMap().keySet()
                ) {
                    if (test.flowNetwork.getEdgeWeight(edge) != 0) {
                        count++;
                    }

                }

            }
            tamassiaPushTime = statsTamassiaPush.getMean();
            tamassiaPushStdev = statsTamassiaPush.getStandardDeviation();
            tamassiaPushMedian = statsTamassiaPush.getPercentile(50);

        } catch (Exception e) {
            e.printStackTrace();
            tamassiaPushTime = -1;
            System.out.println("Invalid Graph");
        }

/*
        // Falls man den Ford Fulkerson Test einfügen will
        try {
            DescriptiveStatistics statsFordFulkerson = new DescriptiveStatistics();

            for (int i = 0; i < runs; i++) {

                tree = spqImporter.runFromArray();
                treeVertexFaceGenerator = new FaceGenerator<>(tree.getConstructedGraph(), root.getStartVertex(), root.getSinkVertex());
                treeVertexFaceGenerator.generateFaces();
                startTime = System.nanoTime();
                MaxFlow test = new MaxFlow(tree, treeVertexFaceGenerator.getPlanarGraphFaces());
                test.runJGraptHFordFulkerson();
             //   test.runEdmondsKarp();
                stopTime = System.nanoTime();
                elapsedTime = stopTime - startTime;
                statsFordFulkerson.addValue(elapsedTime);
            //    flowNetWorkEdges = test.flowMap2.keySet().size();
                System.out.println("TamassiaPush Zeit: " + elapsedTime);
            }
            FordFulkerson = statsFordFulkerson.getMean();
            FordFulkersonStdev = statsFordFulkerson.getStandardDeviation();
            System.out.println("TamassiaStdev" + tamassiaPushStdev);

        } catch (Exception e) {
            e.printStackTrace();
            FordFulkerson = -1;
            System.out.println("Invalid Graph");
        }
*/


        csvPrinter.printRecord(nodes, faces, degree3Vertices, degree4Vertices, flowNetWorkEdges, notQnodeCount, didimoTime, didimoMedian, didimoStdev, tamassiaMinFlowTime, tamassiaMinFlowMedian, tamassiaMinFlowStdDev, tamassiaPushTime, tamassiaPushMedian, tamassiaPushStdev, FordFulkerson, FordFulkersonStdev, isRectilinear, sNodeCountChildren);
    }


}
