import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.*;

public class graphBuilder {


    public static int CHANCE_OF_P = 60;
    private static int OPS = 200;

    public graphBuilder(int CHANCE_OF_P, int OPS) {

        graphBuilder.CHANCE_OF_P = CHANCE_OF_P;
        graphBuilder.OPS = OPS;
    }

    public static void main(String[] args) throws Exception {


        ExecutorService executorService =
                new ThreadPoolExecutor(8, 8, 0L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<Runnable>());
        List<Callable<Object>> callableList = new ArrayList<>();


        SPQTree tree;
        SPQNode root;

        BlockingQueue<SPQGenerator> blockingQueue = new ArrayBlockingQueue<>(1024);

        //  SPQGenerator spqGenerator = new SPQGenerator(60000, 30, blockingQueue);
        //   SPQGenerator spqGen = spqGenerator.call();


        ArrayList<Callable<Object>> arrList = new ArrayList<>();
        arrList.add(new SPQGenerator(OPS, CHANCE_OF_P, blockingQueue));
        arrList.add(new SPQGenerator(OPS, CHANCE_OF_P, blockingQueue));
        arrList.add(new SPQGenerator(OPS, CHANCE_OF_P, blockingQueue));
        arrList.add(new SPQGenerator(OPS, CHANCE_OF_P, blockingQueue));
        arrList.add(new SPQGenerator(OPS, CHANCE_OF_P, blockingQueue));
        arrList.add(new SPQGenerator(OPS, CHANCE_OF_P, blockingQueue));
        arrList.add(new SPQGenerator(OPS, CHANCE_OF_P, blockingQueue));
        arrList.add(new SPQGenerator(OPS, CHANCE_OF_P, blockingQueue));
        arrList.add(new Consumer(blockingQueue));


        executorService.invokeAll(arrList);
        executorService.shutdown();


    }


    public static class Consumer implements Callable {

        SPQTree tree;
        SPQNode root;
        SPQGenerator spqGenerator;
        BlockingQueue<SPQGenerator> blockingQueue;

        public Consumer(BlockingQueue<SPQGenerator> blockingQueue) {
            this.blockingQueue = blockingQueue;

        }

        public Object call() {

            // call web service


            try {
                while (true) {
                    this.spqGenerator = blockingQueue.take();

                    tree = spqGenerator.getTree();
                    root = spqGenerator.getRoot();


                    SPQExporter spqExporter = new SPQExporter(tree);
                    spqExporter.run(root);
                    spqExporter.run(root, "C:/a.txt");

                    SPQImporter spqImporter = new SPQImporter("C:/a.txt");
                    spqImporter.run();

                    DirectedMultigraph<TreeVertex, DefaultEdge> graph = spqImporter.tree.constructedGraph;

                    tree = spqImporter.tree;
                    root = tree.getRoot();


                    Hashtable<TreeVertex, ArrayList<TreeVertex>> embedding = new Hashtable<>();
                    Embedder embedder = new Embedder(embedding, root);
                    embedder.run(root);

                    FaceGenerator<TreeVertex, DefaultEdge> treeVertexFaceGenerator = new FaceGenerator<>(tree.constructedGraph, root.getStartVertex(), root.getSinkVertex(), embedding);
                    treeVertexFaceGenerator.generateFaces2();


                    System.out.println("Anzahl Faces:" + treeVertexFaceGenerator.planarGraphFaces.size());


                    int faces = treeVertexFaceGenerator.planarGraphFaces.size();
                    int nodes = graph.vertexSet().size();

                    try {
                        Files.copy(Paths.get("C:/a.txt"), Paths.get("C:/" + nodes + "N" + faces + "F.txt"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Thread.sleep(5000);


                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            return null;
        }


    }


}

