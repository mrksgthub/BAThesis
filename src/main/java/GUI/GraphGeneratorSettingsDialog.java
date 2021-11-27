package GUI;

import javax.swing.*;
import java.awt.event.*;

class GraphGeneratorSettingsDialog extends JDialog {
    int ops = -1;
    int chanceOfP = -1;
    boolean validSettings = false;
    ButtonGroup buttonGroup1;
    private int mode;
    private int maxDeg = -1;
    private int einfachheit = -1;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JTextField textField4;
    private JPanel randomButton;
    private JRadioButton mixDeg3Deg4RadioButton;
    private JRadioButton onlyDeg3RadioButton;
    private JRadioButton onlyDeg4RadioButton;
    private JRadioButton randomRadioButton;
    private JCheckBox allowInvalidGraphsCheckbox;
    private boolean allowInvalidGraphs;

    public boolean isAllowInvalidGraphs() {
        return allowInvalidGraphs;
    }

    public GraphGeneratorSettingsDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);


        randomRadioButton.setMnemonic(KeyEvent.VK_0);
        mixDeg3Deg4RadioButton.setMnemonic(KeyEvent.VK_1);
        onlyDeg3RadioButton.setMnemonic(KeyEvent.VK_2);
        onlyDeg4RadioButton.setMnemonic(KeyEvent.VK_3);


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

    public int getOps() {
        return ops;
    }

    public int getChanceOfP() {
        return chanceOfP;
    }

    public int getMaxDeg() {
        return maxDeg;
    }

    public int getEinfachheit() {
        return einfachheit;
    }

    public int getMode() {
        return mode;
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
            chanceOfP = (int) Double.parseDouble(textField2.getText())*100000;
            if (chanceOfP < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Nur Werte von 0,000-100,0 möglich");
            e.printStackTrace();
            chanceOfP = -1;
        }

        try {
            maxDeg = Integer.parseInt(textField3.getText());
            if (maxDeg < 2 || maxDeg > 4) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Nur Werte von 2-4 möglich");
            e.printStackTrace();
            maxDeg = -1;
        }

        try {
            einfachheit = Integer.parseInt(textField4.getText());
            if (einfachheit < 1) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Nur Werte von 1+ möglich");
            e.printStackTrace();
            einfachheit = -1;
        }

        allowInvalidGraphs = allowInvalidGraphsCheckbox.isSelected();


        if (chanceOfP >= 0 && ops >= 0 && (maxDeg >= 2 && maxDeg <= 4) && (einfachheit >= 1)) {
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
