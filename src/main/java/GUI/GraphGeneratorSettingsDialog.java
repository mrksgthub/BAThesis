package GUI;

import javax.swing.*;
import java.awt.event.*;

public class GraphGeneratorSettingsDialog extends JDialog {
    int ops = -1;
    int chanceOfP = -1;
    boolean validSettings = false;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textField1;
    private JTextField textField2;

    public GraphGeneratorSettingsDialog() {
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
    }

    public static void main(String[] args) {
        GraphGeneratorSettingsDialog dialog = new GraphGeneratorSettingsDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    private void onOK() {
        // add your code here

        try {
            ops = Integer.parseInt(textField1.getText());
            if (ops < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Nur Werte von 1-" + Integer.MAX_VALUE + " möglich");
            e.printStackTrace();
            ops = -1;
        }

        try {
            chanceOfP = Integer.parseInt(textField2.getText());
            if (chanceOfP < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Nur Werte von 0-100 möglich");
            e.printStackTrace();
            chanceOfP = -1;
        }
        if (chanceOfP >= 0 && ops >= 0) {
            validSettings = true;
            dispose();

        }

    }

    private void onCancel() {
        // add your code here if necessary
        validSettings = false;
        dispose();
    }

}
