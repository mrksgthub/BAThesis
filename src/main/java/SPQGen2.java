public class SPQGen2 {


    int nodes;
    SPQNode root;

    public SPQGen2(int nodes) {

        if (nodes < 3) {
            throw new RuntimeException("nö");
        }

        this.nodes = nodes;














    }

    public void generate() {

        SPQNode node = new SPQNode(nodes);
        root =  node.generateTree2(nodes);



    }



}
