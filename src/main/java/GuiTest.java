import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;


public class GuiTest extends JFrame {


    static SwingWorker worker;
    private static SPQTree tree;
    private static SPQNode root;

    int ops;
    int chanceOfP;
    private JButton drawGraphButton;
    private JButton button2;
    private JButton interrupts;
    private JLabel test;
    private JPanel panel1;
    private JPanel Texts;
    private JLabel ChanceOfP;
    private JLabel opsField;
    private JLabel status;
    private JMenuItem exportButton;
    private JMenuItem importButton;
    private JMenu filesMenu;
    private JMenuBar menuBar;
    private Boolean hasValidGraph;


    public GuiTest() {

       /* button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GuiDialog dialog1 = new GuiDialog();
                dialog1.pack();
                dialog1.setVisible(true);
                opsField.setText("Ops: " + dialog1.ops);
                ChanceOfP.setText("Chance of P: " + dialog1.chanceOfP);
                ops = dialog1.ops;
                chanceOfP = dialog1.chanceOfP;


                worker = new Thread() {


                    public void run() {

                        SPQGenerator spqGenerator = new SPQGenerator(ops, chanceOfP);
                        try {

                            Thread t1 = new Thread(spqGenerator);
                            t1.start();
                            System.out.println("Graph is being Generated");
                   *//*         while (!Thread.interrupted() && t1.isAlive()) {
                                try {
                                    Thread.sleep(Integer.MAX_VALUE);
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                    spqGenerator.shutdown();
                                    System.out.println("Unterbochen");
                                }

                            }*//*

                            //     spqGenerator.run(ops, chanceOfP);
                            t1.join();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        tree = spqGenerator.getTree();
                        root = spqGenerator.getRoot();
                        status.setText("Graph Generated");
                    }

                };
                worker.start();
            }


        });*/


        button2.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                GuiDialog dialog1 = new GuiDialog();
                dialog1.pack();
                dialog1.setVisible(true);
                opsField.setText("Ops: " + dialog1.ops);
                ChanceOfP.setText("Chance of P: " + dialog1.chanceOfP);
                ops = dialog1.ops;
                chanceOfP = dialog1.chanceOfP;
                SPQGenerator spqGenerator = new SPQGenerator(ops, chanceOfP);

                worker = new SwingWorker() {

                    @Override
                    protected Object doInBackground() throws Exception {
                        interrupts.setEnabled(true);
                        //   spqGenerator.run();

                        hasValidGraph = false;
                        Hashtable<TreeVertex, ArrayList<TreeVertex>> embedding = new Hashtable<>();

                        spqGenerator.counter = 0;
                        while (!hasValidGraph && !isCancelled()) {
                            hasValidGraph = spqGenerator.generateGraph(spqGenerator.size, chanceOfP);

                        }
                        return null;
                    }

                    @Override
                    protected void done() {
                        super.done();
                        System.out.println("In Done");
                        tree = spqGenerator.getTree();
                        root = spqGenerator.getRoot();
                        status.setText("Graph Generated");
                        drawGraphButton.setEnabled(true);
                        interrupts.setEnabled(false);
                    }
                };
                worker.execute();
            }

        });


        drawGraphButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                status.setText("Drawing Graph");
                GraphDrawOptions dialog1 = new GraphDrawOptions();
                dialog1.pack();
                dialog1.setVisible(true);

                try {
                    if (dialog1.run && tree != null && root != null) {

                        TestAndAngles angles = new TestAndAngles(tree, root);
                        angles.run(dialog1.run, dialog1.winkelAlgorithmus);
                        ////////////////////////////////////////////
                        // orthogonal rep muss gesetted werden
                        Thread t = new Thread(new GraphDrawer(angles.treeVertexFaceGenerator.planarGraphFaces, angles.embedding, angles.treeVertexFaceGenerator.adjFaces2));
                        t.start();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(panel1, "Invalid Graph", "Alert", JOptionPane.WARNING_MESSAGE);
                }
            }
        });


        interrupts.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                worker.cancel(true);
                interrupts.setEnabled(false);

            }
        });


        importButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                status.setText("Import Finished");
                JFileChooser fc = new JFileChooser();
                int returnVal = fc.showOpenDialog(panel1);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    //This is where a real application would open the file.
                    SwingWorker worker1 = new SwingWorker() {
                        @Override
                        protected Object doInBackground() throws Exception {
                            SPQImporter spqImporter = new SPQImporter(file.getAbsolutePath());
                            spqImporter.run();
                            tree = spqImporter.tree;
                            root = tree.getRoot();
                            ChanceOfP.setText("Vertices: " + tree.constructedGraph.vertexSet().size());

                            return null;
                        }

                        @Override
                        protected void done() {
                            super.done();
                            System.out.println("In Done");
                            updateText("Importing done");
                            drawGraphButton.setEnabled(true);

                        }
                    };
                    worker1.execute();

                }

                updateText("Importing done");
            }
        });

        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                status.setText("Exporting Graph");
                JFileChooser fc = new JFileChooser();
                int returnVal = fc.showSaveDialog(panel1);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    //This is where a real application would save the file.
                    SwingWorker worker1 = new SwingWorker() {
                        @Override
                        protected String doInBackground() throws Exception {
                            SPQExporter exporter = new SPQExporter(tree);
                            exporter.run(root, file.getAbsolutePath());

                            return "Test";
                        }
                    };
                    worker1.execute();
                    status.revalidate();
                    status.repaint();

                }
            }
        });


    }

    public static void main(String[] args) {

        System.setProperty("org.graphstream.ui", "swing");

        JFrame frame = new JFrame("Test");
        frame.setContentPane(new GuiTest().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }


    public void updateText(String text) {
        status.setText(text);
        status.paintImmediately(status.getVisibleRect());
    }

    public void run() {

        System.setProperty("org.graphstream.ui", "swing");

        JFrame frame = new JFrame("Test");
        frame.setContentPane(new GuiTest().panel1);
        // frame.setJMenuBar(menuBar);
        //  menuBar.setLayout(new FlowLayout(FlowLayout.LEADING));
        //   menuBar.add(filesMenu);
        //  filesMenu.add(importButton);
        //  filesMenu.add(exportButton);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }


}
