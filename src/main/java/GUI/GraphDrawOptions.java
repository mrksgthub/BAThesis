package GUI;

import javax.swing.*;
import java.awt.event.*;

public class GraphDrawOptions extends JDialog {
    public boolean run = false;
    WinkelAlgorithmus winkelAlgorithmus;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JRadioButton tamassiaPushRelabelRadioButton;
    private JRadioButton didimoRadioButton;
    private ButtonGroup buttonGroup1;

    public GraphDrawOptions() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);


        tamassiaPushRelabelRadioButton.setMnemonic(KeyEvent.VK_0);
        didimoRadioButton.setMnemonic(KeyEvent.VK_1);


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
        GraphDrawOptions dialog = new GraphDrawOptions();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    private void onOK() {
        // add your code here
        int selection = buttonGroup1.getSelection().getMnemonic();

        switch (selection) {
            case KeyEvent.VK_1:
                winkelAlgorithmus = WinkelAlgorithmus.DIDIMO;
                break;
            case KeyEvent.VK_0:
                winkelAlgorithmus = WinkelAlgorithmus.PUSH_RELABEL;
                break;
            default:
                throw new IllegalStateException("Button doesn't exist");
        }
        run = true;
        dispose();


    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public enum WinkelAlgorithmus            // Enum-Typ
    {
        PUSH_RELABEL, DIDIMO  // Enumerationskonstanten
    }
}
