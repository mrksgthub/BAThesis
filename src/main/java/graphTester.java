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

    private static final String SAMPLE_CSV_FILE = "C:/a.csv";


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
                        .withHeader("Graph", "Size", "Method", "Time in s"));
        ) {

            for (String fileName: listOfFiles
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


            long startTime = System.currentTimeMillis();


            DidimoRepresentability didimoRepresentability = new DidimoRepresentability(tree, root);
            didimoRepresentability.run();


            root.getMergedChildren().get(0).computeSpirality();
            Angulator angulator = new Angulator(tree, embedding, treeVertexFaceGenerator);
            angulator.run();


            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            System.out.println("Didimo Zeit: " + elapsedTime);


            startTime = System.currentTimeMillis();


            csvPrinter.printRecord(nodes, faces, "Didimo", elapsedTime);

            TamassiaRepresentation tamassiaRepresentation = new TamassiaRepresentation(tree, root, treeVertexFaceGenerator);
            tamassiaRepresentation.run();


            // MaxFlow test = new MaxFlow(tree, root, treeVertexFaceGenerator);
            //   test.run();
            stopTime = System.currentTimeMillis();
            elapsedTime = stopTime - startTime;
            System.out.println("Tamassia Zeit: " + elapsedTime);

            csvPrinter.printRecord(nodes, faces, "Tamassia", elapsedTime);





            }
            csvPrinter.flush();


        } catch (IOException e) {
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



}
