package GUI;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;

class GenerateTestDataDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton graphFolderButton;
    private JTextField minimumOpsField;
    private JTextField chanceOfPIncrField;
    private JTextField opsIncrementField;
    private JTextField chanceOfPField;
    private JLabel graphFolderLabel;
    private JTextField maxDegreeText;

    private JTextField chainLengthText;
    private JLabel maxDegreeLabel;
    private JLabel chainLengthLabel;
    private JTextField NumChanceOfPIncrementsField;
    private JTextField NumOpsIncreaseField;
    private JRadioButton mixDeg3Deg4RadioButton;
    private JRadioButton onlyDeg3RadioButton;
    private JRadioButton onlyDeg4RadioButton;
    private JRadioButton randomRadioButton;
    
    private JPanel randomButton;
    private JCheckBox allowInvalidGraphsCheckbox;
    private JTextField counterText;


    private int chanceOfPIncr;
    private int minOps;
    private int opsIncrement;
    private int chanceOfP;
    private boolean validSettings;
    private File[] graphFolder;
    private String filePath;
    private int maxDegree;
    private int counter;
    private int chainLength;
    private int numOfOpsIncrease;
    private int numOfChanceOfPIncrease;
    private ButtonGroup buttonGroup1;

    public boolean isAllowInvalidGraphs() {
        return allowInvalidGraphs;
    }

    private boolean allowInvalidGraphs;

    public int getCounter() {
        return counter;
    }

    public int getMode() {
        return mode;
    }

    private int mode;

    public GenerateTestDataDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);




        randomRadioButton.setMnemonic(KeyEvent.VK_0);
        mixDeg3Deg4RadioButton.setMnemonic(KeyEvent.VK_1);
        onlyDeg3RadioButton.setMnemonic(KeyEvent.VK_2);
        onlyDeg4RadioButton.setMnemonic(KeyEvent.VK_3);



        graphFolderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                fc.setMultiSelectionEnabled(true);
                int returnVal = fc.showOpenDialog(contentPane);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    graphFolder = fc.getSelectedFiles();

                    if (graphFolder[0].isDirectory()) {
                        if (graphFolder.length == 1) {
                            graphFolderLabel.setText(graphFolder[0].getAbsolutePath());
                            filePath = graphFolder[0].getAbsolutePath().toString();
                        } else {
                            JOptionPane.showMessageDialog(contentPane, "Only one folder can be selected at a time", "Alert", JOptionPane.WARNING_MESSAGE);
                            graphFolder = null;
                            filePath = null;
                        }

                    } else {
                        JOptionPane.showMessageDialog(contentPane, "Only one folder can be selected at a time", "Alert", JOptionPane.WARNING_MESSAGE);
                        graphFolder = null;
                        filePath = null;


                    }


                }
            }
        });


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
    }

    public static void main(String[] args) {
        GenerateTestDataDialog dialog = new GenerateTestDataDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    public int getNumOfOpsIncrease() {
        return numOfOpsIncrease;
    }

    public int getNumOfChanceOfPIncrease() {
        return numOfChanceOfPIncrease;
    }

    public int getMaxDegree() {
        return maxDegree;
    }

    public int getChainLength() {
        return chainLength;
    }

    public int getChanceOfPIncr() {
        return chanceOfPIncr;
    }

    public int getMinOps() {
        return minOps;
    }

    public int getOpsIncrement() {
        return opsIncrement;
    }

    public int getChanceOfP() {
        return chanceOfP;
    }

    public String getFilePath() {
        return filePath;
    }

    private void onOK() {
        // add your code here


        int selection = buttonGroup1.getSelection().getMnemonic();

        switch (selection) {
            case KeyEvent.VK_0 -> mode = 0;
            case KeyEvent.VK_1 -> mode = 1;
            case KeyEvent.VK_2 -> mode = 2;
            case KeyEvent.VK_3 -> mode = 3;
            default -> throw new IllegalStateException("Button doesn't exist");
        }

        try {
            counter = Integer.parseInt(counterText.getText());
            if (counter < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Nur Werte von 0+" + Integer.MAX_VALUE + " möglich");
            e.printStackTrace();
            counter = -1;
        }




        try {
            minOps = Integer.parseInt(minimumOpsField.getText());
            if (minOps < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Nur Werte von 0+" + Integer.MAX_VALUE + " möglich");
            e.printStackTrace();
            minOps = -1;
        }


        try {
            numOfChanceOfPIncrease = Integer.parseInt( NumChanceOfPIncrementsField.getText());
            if (numOfChanceOfPIncrease < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Nur Werte von 0+ möglich");
            e.printStackTrace();
            numOfChanceOfPIncrease = -1;
        }


        try {
            numOfOpsIncrease = Integer.parseInt(NumOpsIncreaseField.getText());
            if (numOfOpsIncrease < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Nur Werte von 0+ möglich");
            e.printStackTrace();
            numOfOpsIncrease = -1;
        }


        try {
            opsIncrement = Integer.parseInt(opsIncrementField.getText());
            if (opsIncrement < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Nur Werte von 0+ möglich");
            e.printStackTrace();
            opsIncrement = -1;
        }

        try {

            double v = Double.parseDouble(chanceOfPField.getText()) * 1000;
            chanceOfP = (int) v;
            if (chanceOfP < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Nur Werte von 0.0000+ möglich");
            e.printStackTrace();
            chanceOfP = -1;
        }


        try {
            double v = Double.parseDouble(chanceOfPIncrField.getText()) * 1000;
            chanceOfPIncr = (int) v;;
            if (chanceOfPIncr < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Nur Werte von 0.0000+ möglich");
            e.printStackTrace();
            chanceOfPIncr = -1;
        }


        try {
            maxDegree = Integer.parseInt(maxDegreeText.getText());
            if (maxDegree < 2 || maxDegree > 4) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Nur Werte von 2-4 möglich");
            e.printStackTrace();
            maxDegree = -1;
        }

        try {
            chainLength = Integer.parseInt(chainLengthText.getText());
            if (chainLength < 1) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Nur Werte von 1+ möglich");
            e.printStackTrace();
            chainLength = -1;
        }

        allowInvalidGraphs = allowInvalidGraphsCheckbox.isSelected();

        if (chanceOfP >= 0 && chanceOfPIncr >= 0 && minOps >= 0 && opsIncrement >= 0 && maxDegree > 1 && maxDegree < 5 && chainLength > 0 && numOfChanceOfPIncrease > 0 && numOfOpsIncrease > 0 && counter >0) {
            validSettings = true;
            dispose();

        }


      //  dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}

