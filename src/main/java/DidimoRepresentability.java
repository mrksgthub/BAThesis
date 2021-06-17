public class DidimoRepresentability {


    private final SPQTree tree;
    private final SPQNode root
            ;

    public DidimoRepresentability(SPQTree tree, SPQNode root) {
        this.tree = tree;
        this.root = root;
    }

    public void run() {
        boolean check = true;

     check = root.computeRepresentability(check);
        if (check) {
            check = (tree.computeNofRoot()) ? check : false;
            if (!check) {
                System.out.println("Didimo rejected at source Node");
            }
        }



    }









}
