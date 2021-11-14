package GUI;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;

public class GenerateTestDataDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton graphFolderButton;
    private JTextField minimumOpsField;
    private JTextField chanceOfPIncrField;
    private JTextField opsIncrementField;
    private JTextField chanceOfPField;
    private JLabel graphFolderLabel;
    private int chanceOfPIncr;
    private int minOps;
    private int opsIncrement;
    private int chanceOfP;
    private boolean validSettings;
    private File[] graphFolder;
    private String filePath;


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




    public GenerateTestDataDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);


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

    private void onOK() {
        // add your code here

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
            chanceOfP = Integer.parseInt(chanceOfPField.getText());
            if (chanceOfP < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Nur Werte von 0+ möglich");
            e.printStackTrace();
            chanceOfP = -1;
        }



        try {
            chanceOfPIncr = Integer.parseInt(chanceOfPIncrField.getText());
            if (chanceOfPIncr < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Nur Werte von 0+ möglich");
            e.printStackTrace();
            chanceOfPIncr = -1;
        }









        if (chanceOfP >= 0 && chanceOfPIncr >= 0 && minOps >= 0 &&   opsIncrement   >= 0 )  {
            validSettings = true;
            dispose();

        }
































        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        GenerateTestDataDialog dialog = new GenerateTestDataDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}

