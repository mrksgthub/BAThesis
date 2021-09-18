import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class SPQImporter {


    private final String fileName;
    HashMap<String, SPQNode> nameToNode = new HashMap<>();
    HashMap<SPQNode, List<SPQNode>> nodeToAdjList = new HashMap<>();
    HashMap<TreeVertex, Boolean> treeMap = new HashMap<>();
    HashMap<String, TreeVertex> nameToTreeVertex = new HashMap<>();
    SPQPNode root;
    SPQTree tree;

    public SPQImporter(String s) {
        this.fileName = s;
    }

    public SPQNode run() {


        File file = new File(fileName);
        try {
            Scanner scanner = new Scanner(file);
            String line = "test";

            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                proceessLine(line);
            }

            tree = new SPQTree(nameToNode.get("Proot"));
            root = (SPQPNode) nameToNode.get("Proot");
            if (tree.getRoot() == null) {
                try {
                    throw new Exception("AHHH");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


            tree.setStartAndSinkNodesOrBuildConstructedGraph(tree.getRoot(), tree.getVisited());
            root.computeAdjecentVertices();
            root.setRoot();

/*            int i = 1;
            for (TreeVertex v: nameToTreeVertex.values()
                 ) {
                if (v.getName().equals("vsource")) {
                    v.id = 0;
                } else if ((v.getName().equals("vsink"))) {
                    v.id = nameToTreeVertex.values().size()-1;
                } else {
                    v.id = i++;
                }
            }*/



            System.out.println("Graph Imported");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        return tree.getRoot();
    }

    private void proceessLine(String line) {

        try {
            if (line.contains("-> ")) {
                int i = line.indexOf(" -");

                String s1 = line.substring(0, i).trim();
                String s2 = line.substring(i + 3, line.length() - 1).trim();

                nameToNode.get(s1).getMergedChildren().add(nameToNode.get(s2));

            } else {

                if (line.length() > 2) {
                    switch (line.charAt(2)) {
                        case 'Q' -> nameToNode.put(line.substring(0, line.length() - 1).trim(), new SPQQNode(line.substring(0, line.length() - 1).trim()));
                        case 'v' -> {
                            SPQQNode qNode = new SPQQNode(line.substring(0, line.length() - 1).trim());
                            int i = line.lastIndexOf("v");

                            TreeVertex startVertex = new TreeVertex(line.substring(0, i).trim());
                            nameToTreeVertex.putIfAbsent(line.substring(0, i).trim(), startVertex);
                            qNode.startVertex = nameToTreeVertex.get(line.substring(0, i).trim());

                            TreeVertex sinkVertex = new TreeVertex(line.substring(i, line.length() - 1).trim());
                            nameToTreeVertex.putIfAbsent(line.substring(i, line.length() - 1).trim(), sinkVertex);
                            qNode.sinkVertex = nameToTreeVertex.get(line.substring(i, line.length() - 1).trim());


                            nameToNode.put(line.substring(0, line.length() - 1).trim(), qNode);
                        }
                        case 'P' -> nameToNode.put(line.substring(0, line.length() - 1).trim(), new SPQPNode(line.substring(0, line.length() - 1).trim()));
                        case 'S' -> nameToNode.put(line.substring(0, line.length() - 1).trim(), new SPQSNode(line.substring(0, line.length() - 1).trim()));
                        default -> System.out.println("bug");
                    }

                }

            }


        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(line);
        }

    }


}
