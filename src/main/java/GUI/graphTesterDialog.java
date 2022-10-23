package GUI;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static GUI.SPQGUI.getFileChooser;
import static GUI.SPQGUI.setLastDir;

class graphTesterDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton sourceFolderButton;
    private JButton dataFolderButton;
    private JLabel sourceLabel;
    private JLabel dataLabel;
    private JTextField maxSizeField;
    private JCheckBox tamassiaMinCostErlaubenCheckBox;
    private JTextField minSizeField;
    private File dataFile;
    private File[] graphFolder;
    private File[] files;
    private int maxSize;
    private int minSize;
    private boolean tamassiaMinCostAllowed;
    private String graphData;

    public graphTesterDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);


        sourceFolderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = getFileChooser();
                fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                fc.setMultiSelectionEnabled(true);
                int returnVal = fc.showOpenDialog(contentPane);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    setLastDir(fc.getSelectedFile());
                    graphFolder = fc.getSelectedFiles();

                    if (graphFolder[0].isDirectory()) {
                        if (graphFolder.length == 1) {
                            sourceLabel.setText(graphFolder[0].getAbsolutePath());
                            files = graphFolder[0].listFiles();
                        } else {
                            JOptionPane.showMessageDialog(contentPane, "Only one folder can be selected at a time", "Alert", JOptionPane.WARNING_MESSAGE);
                            graphFolder = null;
                            files = null;
                        }

                    } else {
                        sourceLabel.setText(graphFolder[0].getAbsolutePath() + " und " + (graphFolder.length - 1) + " Files");
                        files = graphFolder;
                    }


                }


            }
        });
        dataFolderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                graphData = (graphFolder == null)?"graphData" : graphFolder[0].getPath();
                fc.setSelectedFile(new File(graphData + DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmmss").format(LocalDateTime.now()) + ".csv"));
                int returnVal = fc.showSaveDialog(contentPane);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    dataFile = fc.getSelectedFile();
                    dataLabel.setText(dataFile.getAbsolutePath());
                }

            }
        });
    }

    public static void main(String[] args) {
        graphTesterDialog dialog = new graphTesterDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    public int getMinSize() {
        return minSize;
    }

    public boolean isTamassiaMinCostAllowed() {
        return tamassiaMinCostAllowed;
    }

    public int getMaxSize() {
        return maxSize;
    }

    private void onOK() {
        // add your code here



        boolean isMinMaxValid = false;
        try {
            maxSize = Integer.parseInt(maxSizeField.getText());

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Nur Integer erlaubt");
            e.printStackTrace();
        }
        try {

            minSize = Integer.parseInt(minSizeField.getText());




        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Nur Integer erlaubt");
            e.printStackTrace();
        }


        if (minSize == -1) {
            minSize = 0;
        }
        if ((maxSize == -1)) {
            maxSize = Integer.MAX_VALUE;
        }
        if (minSize  <= maxSize) {
            isMinMaxValid = true;
        }




        tamassiaMinCostAllowed = tamassiaMinCostErlaubenCheckBox.isSelected();


        if (files == null || dataFile == null || !isMinMaxValid) {
            JOptionPane.showMessageDialog(contentPane, "Invalid inputs.", "Alert", JOptionPane.WARNING_MESSAGE);
        } else {
            dispose();
        }

    }

    private void onCancel() {
        // add your code here if necessary
        files = null;
        dataFile = null;
        dispose();
    }

    public File[] getFiles() {
        return files;
    }


    public File getDataPath() {
        return dataFile;
    }

}
