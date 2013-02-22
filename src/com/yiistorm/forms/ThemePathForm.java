package com.yiistorm.forms;

import com.intellij.ide.util.PropertiesComponent;
import com.yiistorm.actions.YiiStormActionAbstract;

import javax.swing.*;
import java.awt.event.*;

public class ThemePathForm extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField themeNameField;
    private static boolean showed = false;
    private YiiStormActionAbstract currentAction;


    public ThemePathForm(YiiStormActionAbstract action) {
        setCurrentAction(action);
        setContentPane(contentPane);
        setModal(true);
        setBounds(500, 500, 400, 200);
        getRootPane().setDefaultButton(buttonOK);

        PropertiesComponent properties = PropertiesComponent.getInstance(currentAction.getProject());
        String themeName = properties.getValue("themeName");
        if (themeName != null) {
            themeNameField.setText(themeName);
        }

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


    public void setCurrentAction(YiiStormActionAbstract action) {
        currentAction = action;
    }

    private void onOK() {
// add your code here

        PropertiesComponent properties = PropertiesComponent.getInstance(currentAction.getProject());
        String themeName = themeNameField.getText();
        if (themeName != null) {
            properties.setValue("themeName", themeName);
        }
        ThemePathForm.showed = false;
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        ThemePathForm.showed = false;
        dispose();
    }

    public static void main() {
        //System.exit(0);
    }
}
