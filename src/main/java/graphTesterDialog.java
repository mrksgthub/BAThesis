import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class graphTesterDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton sourceFolderButton;
    private JButton dataFolderButton;
    private JLabel sourceLabel;
    private JLabel dataLabel;
    private File dataFile;
    private File graphFolder[];
    private File[] files;


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

                JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                fc.setMultiSelectionEnabled(true);
                int returnVal = fc.showOpenDialog(contentPane);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
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
                fc.setSelectedFile(new File("graphData" + DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmmss").format(LocalDateTime.now()) + ".csv"));
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

    private void onOK() {
        // add your code here
        if (files == null || dataFile == null) {
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
