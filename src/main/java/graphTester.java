import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


public class graphTester {

    private static String SAMPLE_CSV_FILE = "C:/a.csv";

    private File dataFile;
    private File[] files;


    public graphTester(File dataFile, File[] files) {
        this.dataFile = dataFile;
        this.files = files;
    }

    // https://www.callicoder.com/java-read-write-csv-file-apache-commons-csv/
    public static void main(String[] args) {


        SPQTree tree;
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
                SPQImporter spqImporter = new SPQImporter(fileName);
                spqImporter.run();


                tree = spqImporter.tree;
                root = tree.getRoot();


                Hashtable<TreeVertex, ArrayList<TreeVertex>> embedding = new Hashtable<>();
                Embedder embedder = new Embedder(embedding, root);
                embedder.run(root);

                FaceGenerator<TreeVertex, DefaultEdge> treeVertexFaceGenerator = new FaceGenerator<>(tree.constructedGraph, root.getStartVertex(), root.getSinkVertex(), embedding);
                treeVertexFaceGenerator.generateFaces2();

                DirectedMultigraph<TreeVertex, DefaultEdge> graph = spqImporter.tree.constructedGraph;
                int faces = treeVertexFaceGenerator.planarGraphFaces.size();
                int nodes = graph.vertexSet().size();


                System.out.println(fileName);
                long startTime = System.currentTimeMillis();


                DidimoRepresentability didimoRepresentability = new DidimoRepresentability(tree, root);
                didimoRepresentability.run();


                root.getMergedChildren().get(0).computeSpirality();
                Angulator angulator = new Angulator(tree, embedding, treeVertexFaceGenerator);
                angulator.run();


                long stopTime = System.currentTimeMillis();
                long elapsedTime = stopTime - startTime;
                System.out.println("Didimo Zeit: " + elapsedTime);
                long didimoTime = elapsedTime;

                startTime = System.currentTimeMillis();


                // csvPrinter.printRecord(nodes, faces, "Didimo", elapsedTime);

                // TamassiaRepresentation tamassiaRepresentation = new TamassiaRepresentation(tree, root, treeVertexFaceGenerator);
                //  tamassiaRepresentation.run();


                // MaxFlow test = new MaxFlow(tree, root, treeVertexFaceGenerator);
                //   test.run();
                stopTime = System.currentTimeMillis();
                elapsedTime = stopTime - startTime;
                long tamassiaMinFlowTime = elapsedTime;
                System.out.println("Tamassia Zeit: " + elapsedTime);


                startTime = System.currentTimeMillis();


                MaxFlow test = new MaxFlow(tree, root, treeVertexFaceGenerator);
                test.run3();
                stopTime = System.currentTimeMillis();
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
    public static void listFilesForFolder(final File folder, List<String> listOfFiles) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry, listOfFiles);
            } else {
                System.out.println(fileEntry.getAbsolutePath());
                listOfFiles.add(fileEntry.getAbsolutePath());
            }
        }
    }

    public void run() {

        SPQTree tree;
        SPQNode root;

        try (
                BufferedWriter writer = Files.newBufferedWriter(dataFile.toPath());

                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                        .withHeader("Graph", "Size", "Didimo", "Tamassia", "TamassiaPush"));
        ) {

            for (File fileName : files
            ) {

                System.out.println(fileName);
                SPQImporter spqImporter = new SPQImporter(fileName.toString());
                spqImporter.run();


                tree = spqImporter.tree;
                root = tree.getRoot();


                Hashtable<TreeVertex, ArrayList<TreeVertex>> embedding = new Hashtable<>();
                Embedder embedder = new Embedder(embedding, root);
                embedder.run(root);

                FaceGenerator<TreeVertex, DefaultEdge> treeVertexFaceGenerator = new FaceGenerator<>(tree.constructedGraph, root.getStartVertex(), root.getSinkVertex(), embedding);
                treeVertexFaceGenerator.generateFaces2();

                try {
                    GraphValidifier graphValidifier = new GraphValidifier(tree.constructedGraph, treeVertexFaceGenerator.planarGraphFaces);
                    graphValidifier.run();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Fehler in GraphValidifier");
                }

                DirectedMultigraph<TreeVertex, DefaultEdge> graph = spqImporter.tree.constructedGraph;
                int faces = treeVertexFaceGenerator.planarGraphFaces.size();
                int nodes = graph.vertexSet().size();


                System.out.println(fileName);
                long startTime = System.currentTimeMillis();
                long stopTime;
                long elapsedTime;
                long didimoTime;


                try {
                    DidimoRepresentability didimoRepresentability = new DidimoRepresentability(tree, root);
                    didimoRepresentability.run();

                    root.getMergedChildren().get(0).computeSpirality();
                    Angulator angulator = new Angulator(tree, embedding, treeVertexFaceGenerator);
                    angulator.run();

                    stopTime = System.currentTimeMillis();
                    elapsedTime = stopTime - startTime;
                    System.out.println("Didimo Zeit: " + elapsedTime);
                    didimoTime = elapsedTime;

                } catch (Exception e) {
                    e.printStackTrace();
                    didimoTime = -1;
                }

                startTime = System.currentTimeMillis();

                // csvPrinter.printRecord(nodes, faces, "Didimo", elapsedTime);

                // TamassiaRepresentation tamassiaRepresentation = new TamassiaRepresentation(tree, root, treeVertexFaceGenerator);
                //  tamassiaRepresentation.run();


                // MaxFlow test = new MaxFlow(tree, root, treeVertexFaceGenerator);
                //   test.run();


                stopTime = System.currentTimeMillis();
                elapsedTime = stopTime - startTime;
                long tamassiaMinFlowTime = elapsedTime;
                System.out.println("Tamassia Zeit: " + elapsedTime);


                long tamassiaPushTime = 0;
                try {
                    startTime = System.currentTimeMillis();
                    MaxFlow test = new MaxFlow(tree, root, treeVertexFaceGenerator);
                    test.run3();
                    stopTime = System.currentTimeMillis();
                    elapsedTime = stopTime - startTime;
                    tamassiaPushTime = elapsedTime;
                    System.out.println("TamassiaPush Zeit: " + elapsedTime);
                } catch (Exception e) {
                    e.printStackTrace();
                    tamassiaPushTime = -1;
                    System.out.println("Invalid Graph");
                }

                csvPrinter.printRecord(nodes, faces, didimoTime, tamassiaMinFlowTime, tamassiaPushTime);


            }
            csvPrinter.flush();


        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
