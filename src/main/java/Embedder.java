import java.util.ArrayList;
import java.util.Hashtable;

public class Embedder {


    private final SPQNode root;
    private final Hashtable<TreeVertex, ArrayList<TreeVertex>> embedding;

    public Embedder(Hashtable<TreeVertex, ArrayList<TreeVertex>> embedding, SPQNode root) {
        this.embedding = embedding;
        this.root = root;

    }

    public void run(SPQNode node) {

        embedding.putIfAbsent(node.getStartVertex(), node.getStartVertex().adjecentVertices);
        embedding.putIfAbsent(node.getSinkVertex(), node.getSinkVertex().adjecentVertices);

        assert (node.getStartVertex() != null);
        assert (node.getSinkVertex() != null);

        for (SPQNode child: node.getMergedChildren()
             ) {
            run(child);
        }


    }



}
