package Testing;

import Datastructures.SPQNode;
import Datastructures.SPQStarTree;
import Datastructures.Vertex;
import Helperclasses.DFSIterator;
import Helperclasses.GraphValidifier;
import Helperclasses.SPQImporter;
import PlanarityAndAngles.Didimo.Angulator;
import PlanarityAndAngles.Didimo.DidimoRepresentability;
import PlanarityAndAngles.FaceGenerator;
import PlanarityAndAngles.Flow.MaxFlow;
import PlanarityAndAngles.Flow.MinFlow;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.jgrapht.graph.DefaultEdge;
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


public class GraphTester {

    private static String SAMPLE_CSV_FILE = "C:/a.csv";

    private File dataFile;
    private File[] files;
    private int runs = 5;


    public GraphTester(File dataFile, File[] files) {
        this.dataFile = dataFile;
        this.files = files;
    }

    // https://www.callicoder.com/java-read-write-csv-file-apache-commons-csv/
    public static void main(String[] args) {


        SPQStarTree tree;
        SPQNode root;

        final File folder = new File("C:\\Graphs");
        ArrayList<String> listOfFiles = new ArrayList<>();
        listFilesForFolder(folder, listOfFiles);


        try (
                BufferedWriter writer = Files.newBufferedWriter(Paths.get(SAMPLE_CSV_FILE));

                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                        .withHeader("Graph", "Size", "Didimo", "Tamassia", "TamassiaPush"));
        ) {

            for (String fileName : listOfFiles
            ) {

                System.out.println(fileName);
                SPQImporter spqImporter = new SPQImporter();
                spqImporter.runFromFile(fileName);


                tree = spqImporter.getTree();
                root = tree.getRoot();


                /* Embedder embedder = new Embedder(embedding);
                embedder.run(root);
*/
                FaceGenerator<Vertex, DefaultEdge> treeVertexFaceGenerator = new FaceGenerator<>(tree.getConstructedGraph(), root.getStartVertex(), root.getSinkVertex());
                treeVertexFaceGenerator.generateFaces();

                DirectedMultigraph<Vertex, DefaultEdge> graph = spqImporter.getTree().getConstructedGraph();
                int faces = treeVertexFaceGenerator.getPlanarGraphFaces().size();
                int nodes = graph.vertexSet().size();


                System.out.println(fileName);
                long startTime = System.nanoTime();


                DidimoRepresentability didimoRepresentability = new DidimoRepresentability();
                didimoRepresentability.run(tree.getRoot());

                Angulator angulator = new Angulator();
                angulator.run(tree.getRoot(), treeVertexFaceGenerator.getPlanarGraphFaces());


                long stopTime = System.nanoTime();
                long elapsedTime = stopTime - startTime;
                System.out.println("Didimo Zeit: " + elapsedTime);
                long didimoTime = elapsedTime;

                startTime = System.nanoTime();


                // csvPrinter.printRecord(nodes, faces, "Didimo", elapsedTime);

                // Algorithms.Flow.TamassiaRepresentation tamassiaRepresentation = new Algorithms.Flow.TamassiaRepresentation(tree, root, treeVertexFaceGenerator);
                //  tamassiaRepresentation.run();


                // Algorithms.Flow.MaxFlow test = new Algorithms.Flow.MaxFlow(tree, root, treeVertexFaceGenerator);
                //   test.run();
                stopTime = System.nanoTime();
                elapsedTime = stopTime - startTime;
                long tamassiaMinFlowTime = elapsedTime;
                System.out.println("Tamassia Zeit: " + elapsedTime);



                startTime = System.nanoTime();
                MaxFlow test = new MaxFlow(tree, treeVertexFaceGenerator.getPlanarGraphFaces());
                test.runPushRelabel(treeVertexFaceGenerator.getPlanarGraphFaces(), tree.getConstructedGraph());
                stopTime = System.nanoTime();
                elapsedTime = stopTime - startTime;
                long tamassiaPushTime = elapsedTime;
                System.out.println("TamassiaPush Zeit: " + elapsedTime);








                csvPrinter.printRecord(nodes, faces, didimoTime, tamassiaMinFlowTime, tamassiaPushTime);


            }
            csvPrinter.flush();


        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /*
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
     * @param minSize Falls auf <0 gesetzt, dann gibt es keine minimale Größe
     * @param maxSize Falls auf <0 gesetzt, dann gibt es keine maximale Größe
     * @param tamassiaMinCostAllowed
     */
    public void run(int minSize, int maxSize, boolean tamassiaMinCostAllowed) {

        SPQStarTree tree;
        SPQNode root;

        try (
                /*BufferedWriter writer = Files.newBufferedWriter(dataFile.toPath());*/
                BufferedWriter writer = Files.newBufferedWriter(
                        dataFile.toPath(),
                        StandardOpenOption.APPEND,
                        StandardOpenOption.CREATE);

                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                        .withHeader("Graph", "Size", "Degree 3 Vertices", "FlownetworkKantenanzahl", "SPQ*Knoten", "DidimoMean", "DidimoStdev", "Tamassia", "TamassiaStdDev", "TamassiaPush", "TamassiaPushStdev", "FordFulkerson", "FordFulkersonStdev"));
        ) {

            for (File fileName : files
            ) {

                System.out.println(fileName);
                SPQImporter spqImporter = new SPQImporter();
                spqImporter.runFromFile(fileName.toString());


                tree = spqImporter.getTree();
                root = tree.getRoot();


                FaceGenerator<Vertex, DefaultEdge> treeVertexFaceGenerator = new FaceGenerator<>(tree.getConstructedGraph(), root.getStartVertex(), root.getSinkVertex());
                treeVertexFaceGenerator.generateFaces();


                if (maxSize < 0 || tree.getConstructedGraph().vertexSet().size() <= maxSize) {
                    if (minSize < 0 || tree.getConstructedGraph().vertexSet().size() >= minSize) {
                        runAllTests(tree, root, csvPrinter, fileName, spqImporter, treeVertexFaceGenerator, tamassiaMinCostAllowed);
                    }
                }



            }
            csvPrinter.flush();


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

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
        System.out.println(fileName);
        long startTime = System.nanoTime();
        long stopTime;
        long elapsedTime;
        double didimoTime;
        double didimoStdev = 0;
        double tamassiaMinFlowTime;
        double tamassiaMinFlowStdDev = 0;
        double FordFulkerson=0;
        double FordFulkersonStdev=0;
        double tamassiaPushTime = 0;
        double tamassiaPushStdev = 0;
        int flowNetWorkEdges = 0;
        int degree3Vertices = 0;
        int notQnodeCount = 0;
        // TODO EINPFLEGEN
        Deque<SPQNode> s = DFSIterator.buildPostOrderStack(root);

        while (!s.isEmpty()) {
            notQnodeCount += (s.pop().getSpqChildren().size() != 0) ? 1 : 0;
        }

        for (Vertex v : tree.getConstructedGraph().vertexSet()
        ) {
            if (v.getAdjacentVertices().size() == 3) {
                degree3Vertices++;
            }
        }


        try {
            DescriptiveStatistics statsDidimo = new DescriptiveStatistics();

            for (int i = 0; i < runs; i++) {


                tree = spqImporter.runFromArray();
                startTime = System.nanoTime();
                DidimoRepresentability didimoRepresentability = new DidimoRepresentability();
                didimoRepresentability.run(tree.getRoot());

                // root.getMergedChildren().get(0).computeSpirality();

                Angulator angulator = new Angulator();
                angulator.run(tree.getRoot(), treeVertexFaceGenerator.getPlanarGraphFaces());
                stopTime = System.nanoTime();
                elapsedTime = stopTime - startTime;
                statsDidimo.addValue(elapsedTime);
                System.out.println("Didimo Zeit: " + elapsedTime);
            }
            didimoTime = statsDidimo.getMean();
            didimoStdev = statsDidimo.getStandardDeviation();
            System.out.println("DidimoStdev" + didimoStdev);

        } catch (Exception e) {
            e.printStackTrace();
            didimoTime = -1;
        }

        if (tamassiaMinCostAllowed) {
            tree = spqImporter.runFromArray();
            DescriptiveStatistics statsTamassiaMinFlow = new DescriptiveStatistics();

            for (int i = 0; i < runs; i++) {
                startTime = System.nanoTime();

                MinFlow tamassiaRepresentation = new MinFlow(tree, root, treeVertexFaceGenerator);
                tamassiaRepresentation.run(treeVertexFaceGenerator.getPlanarGraphFaces());

                stopTime = System.nanoTime();
                elapsedTime = stopTime - startTime;
                statsTamassiaMinFlow.addValue(elapsedTime);
                System.out.println("Tamassia Zeit: " + elapsedTime);
            }
            tamassiaMinFlowTime = statsTamassiaMinFlow.getMean();
            tamassiaMinFlowStdDev = statsTamassiaMinFlow.getStandardDeviation();
            System.out.println("TamassaMinStdDev" + tamassiaMinFlowStdDev);
        } else {
            tamassiaMinFlowTime = -1;
            tamassiaMinFlowStdDev = -1;

        }


        try {
            DescriptiveStatistics statsTamassiaPush = new DescriptiveStatistics();

            for (int i = 0; i < runs; i++) {

                tree = spqImporter.runFromArray();
                startTime = System.nanoTime();
                MaxFlow test = new MaxFlow(tree, treeVertexFaceGenerator.getPlanarGraphFaces());
                test.runPushRelabel(treeVertexFaceGenerator.getPlanarGraphFaces(), tree.getConstructedGraph());
                stopTime = System.nanoTime();
                elapsedTime = stopTime - startTime;
                // tamassiaPushTime= elapsedTime;
                statsTamassiaPush.addValue(elapsedTime);
                //flowNetWorkEdges = test.getFlowMap().keySet().size()-test.getFlowNetwork().vertexSet().size()-2;
                flowNetWorkEdges = test.flowMap2.keySet().size();
                System.out.println("TamassiaPush Zeit: " + elapsedTime);
            }
            tamassiaPushTime = statsTamassiaPush.getMean();
            tamassiaPushStdev = statsTamassiaPush.getStandardDeviation();
            System.out.println("TamassiaStdev" + tamassiaPushStdev);

        } catch (Exception e) {
            e.printStackTrace();
            tamassiaPushTime = -1;
            System.out.println("Invalid Graph");
        }


        try {
            DescriptiveStatistics statsFordFulkerson = new DescriptiveStatistics();

            for (int i = 0; i < runs; i++) {

                tree = spqImporter.runFromArray();
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


























        csvPrinter.printRecord(nodes, faces, degree3Vertices, flowNetWorkEdges, notQnodeCount, didimoTime, didimoStdev, tamassiaMinFlowTime, tamassiaMinFlowStdDev, tamassiaPushTime, tamassiaPushStdev, FordFulkerson, FordFulkersonStdev);
    }


}
