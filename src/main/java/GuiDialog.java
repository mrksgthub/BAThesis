import javax.swing.*;
import java.awt.event.*;

public class GuiDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textField1;
    private JTextField textField2;

    int ops;
    int chanceOfP;

    public GuiDialog() {
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

    private void onOK() {
        // add your code here

        try {
            ops = Integer.parseInt(textField1.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Nur Werte von 0-100 möglich");
            e.printStackTrace();
        }

        try {
            chanceOfP = Integer.parseInt(textField2.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Nur Werte von 0-" + Integer.MAX_VALUE +" möglich");
            e.printStackTrace();
        }
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        GuiDialog dialog = new GuiDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
