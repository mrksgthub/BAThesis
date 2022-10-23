package Helperclasses;

import Datastructures.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SPQImporter {


    private HashMap<String, SPQNode> nameToNode = new HashMap<>();
    private HashMap<String, Vertex> nameToTreeVertex = new HashMap<>();
    private SPQStarTree tree;
    private SPQStarTree originalCopy;
    private String[] stringArr;

    public SPQImporter() {

    }

    public SPQStarTree getTree() {
        return tree;
    }

    public void runFromFile(String filePathString) {


        File file = new File(filePathString);
/*        try {
            Scanner scanner = new Scanner(file);
            String line = "test";

            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                proceessLine(line);
            }
*/

        try {
            readTextfileToArray(file);

            processTextArrayToTree();

        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }


    public  SPQStarTree  runFromArray() {


        processTextArrayToTree();

       return processTextArrayToTree();
    }











    private SPQStarTree processTextArrayToTree() {
        nameToNode = new HashMap<>();
        nameToTreeVertex = new HashMap<>();
        for (String line : stringArr
        ) {
            proceessLine(line);
        }


        nameToNode.get("Proot").setToRoot();
        tree = new SPQStarTree(nameToNode.get("Proot"));
        SPQPNode root = (SPQPNode) nameToNode.get("Proot");

        if (tree.getRoot() == null) {
            try {
                throw new Exception("AHHH");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        // Stelle sicher, dass die Knoten die richtigen IDs haben
        int i = 1;
        for (Vertex v : nameToTreeVertex.values()
        ) {
            if (v.getName().equals("vsource")) {
                v.setId(0);
            } else if ((v.getName().equals("vsink"))) {
                v.setId(nameToTreeVertex.values().size() - 1);
            } else {
                v.setId(i++);
            }
        }

        // Damit die nächst ID die der nächste unbenutzte Integer ist.
        Vertex.setCounter(nameToTreeVertex.values().size());
        root.setToRoot();

        // Graph muss nach dem "fixen" der ids generiert werden, da sonst die internen Hashmaps nicht mehr stimmen.

        tree.initializeSPQNodes(tree.getRoot());


/*
            Deque<SPQNode> stack = DFSIterator.buildPreOrderStack(tree.getRoot());
            Deque<SPQNode> stack2 = DFSIterator.buildPostOrderStack(tree.getRoot());*/



      //  System.out.println("Graph Imported");

        return tree;
    }

    private void readTextfileToArray(File file) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(file));
        String str;

        List<String> list = new ArrayList<String>();
        while ((str = in.readLine()) != null) {
            list.add(str);
        }
        stringArr = list.toArray(new String[0]);

    }


    private void proceessLine(String line) {

        try {
            if (line.contains("-> ")) { // Die -> Zeilen stehen immer nach der Liste der Knoten des SPQ*-Baums. Danach werden die Kinder zugewiesen.
                int i = line.indexOf(" -");

                String s1 = line.substring(0, i).trim();
                String s2 = line.substring(i + 3, line.length() - 1).trim();

                nameToNode.get(s1).getSpqChildren().add(nameToNode.get(s2));

            } else {

                if (line.length() > 2) {
                    switch (line.charAt(2)) {
                        case 'Q' -> nameToNode.put(line.substring(0, line.length() - 1).trim(), new SPQQNode(line.substring(0, line.length() - 1).trim(), true)); //QStarNodes
                        case 'v' -> { // erzeuge die Treevertexes
                            SPQQNode qNode = new SPQQNode(line.substring(0, line.length() - 1).trim(), false);
                            int i = line.lastIndexOf("v");
                            // Erzeuge Startvertex der QNode
                            nameToTreeVertex.putIfAbsent(line.substring(0, i).trim(), new Vertex(line.substring(0, i).trim()));
                            qNode.setSourceVertex(  nameToTreeVertex.get(line.substring(0, i).trim()));
                            // Erzeuge Sinkvertex der QNode
                            nameToTreeVertex.putIfAbsent(line.substring(i, line.length() - 1).trim(), new Vertex(line.substring(i, line.length() - 1).trim()));
                            qNode.setSinkVertex( nameToTreeVertex.get(line.substring(i, line.length() - 1).trim()));
                            // Qnode
                            nameToNode.put(line.substring(0, line.length() - 1).trim(), qNode);
                        }
                        case 'P' -> nameToNode.put(line.substring(0, line.length() - 1).trim(), new SPQPNode(line.substring(0, line.length() - 1).trim()));
                        case 'S' -> nameToNode.put(line.substring(0, line.length() - 1).trim(), new SPQSNode(line.substring(0, line.length() - 1).trim()));
                     //   default -> System.out.println("");
                    }

                }

            }


        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(line);
        }

    }


}
