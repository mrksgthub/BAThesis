import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SPQNode {

    List<SPQNode> children = new ArrayList<>();
    int nodes;
    SPQNode parent;
    boolean isroot = false;
    boolean visited;
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SPQNode() {

    }

    public SPQNode(int nodes) {
        this.nodes = nodes;
    }

    public SPQNode(int nodes, String name) {
        this.nodes = nodes;
        this.name = name;
    }

    public SPQNode(String name) {
        this.nodes = nodes;
        this.name = name;
    }


    public List<SPQNode> getChildren() {
        return children;
    }

    public void setChildren(List<SPQNode> children) {
        this.children = children;
    }

    public int getNodes() {
        return nodes;
    }

    public void setNodes(int nodes) {
        this.nodes = nodes;
    }

    public SPQNode getParent() {
        return parent;
    }

    public void setParent(SPQNode parent) {
        this.parent = parent;
    }

    public boolean isIsroot() {
        return isroot;
    }

    public void setIsroot(boolean isroot) {
        this.isroot = isroot;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }


    public SPQNode generateTree(int size) {
        isroot = true;
        visited = true;
        nodes = size;

        children.add(new SPQNode(nodes / 2));
        children.add(new SPQNode(nodes / 2));

        children.get(0).generateNode(nodes / 2, this);
        children.get(1).generateNode(nodes / 2, this);

        return this;

    }

    public SPQNode generateTree2(int size) {
        isroot = true;
        visited = true;
        nodes = size;

        children.add(new SPQNode(nodes - 1));
        children.add(new SPQNode(1));

        children.get(0).generateNode2(nodes - 1, this);

        return this;

    }


    public void generateNode(int size, SPQNode node) {
        visited = true;
        parent = node;

        if (size > 2) {

            Random random = new Random();
            int size2 = random.nextInt(size / 2) + 2;
            int size3 = size / size2;


            for (int i = 0; i < size2; i++) {

                children.add(new SPQNode(size3));

            }

            for (int i = 0; i < children.size(); i++) {
                children.get(i).generateNode(size3, this);
            }

        }

    }


    public void generateNode2(int size, SPQNode node) {
        visited = true;
        parent = node;

        if (size > 1) {


            int size2 = GraphHelper.getRandomNumberUsingNextInt(1, size);
            int size3 = size - size2;


            children.add(new SPQNode(size2));
            children.add(new SPQNode(size3));


            children.get(0).generateNode2(size2, this);
            ;
            children.get(1).generateNode2(size3, this);


        } else {

        }
    }


    public <T extends SPQNode> void addNodeAsChild(T node, T parent) {
        node.setParent(parent);
        parent.getChildren().add(node);

    }


    public <T extends SPQNode> void nodeUmhaengen(T node, T newnode) {
        //Abhängen
        node.getParent().getChildren().set(node.getParent().getChildren().indexOf(node), newnode);
        //neuer Knoten als Parent festlegen
        newnode.setParent(node.getParent());

        addNodeAsChild(node, newnode);

    }


}
