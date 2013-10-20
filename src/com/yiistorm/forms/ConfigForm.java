package com.yiistorm.forms;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.vfs.VirtualFile;
import com.yiistorm.actions.YiiStormActionAbstract;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;

public class ConfigForm extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField themeNameField;
    private JPanel fileChooserPanel;
    private JTextField yiicFileField;
    private JButton yiicPathSelect;
    private JFileChooser fileChooser;
    private static boolean showed = false;
    private YiiStormActionAbstract currentAction;


    public ConfigForm(YiiStormActionAbstract action) {
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

        String yiicFile = properties.getValue("yiicFile");
        if (yiicFile != null) {
            yiicFileField.setText(yiicFile);
        }

        yiicPathSelect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                VirtualFile baseDir = currentAction.getProject().getBaseDir();
                if (baseDir != null) {
                    fileChooser.setCurrentDirectory(new File(baseDir.getPath()));
                    int ret = fileChooser.showDialog(null, "Открыть файл");
                    if (ret == JFileChooser.APPROVE_OPTION) {
                        yiicFileField.setText(fileChooser.getSelectedFile().getAbsolutePath());
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

        String selectedFile = yiicFileField.getText();
        if (selectedFile != null) {
            properties.setValue("yiicFile", selectedFile);
        }
        ConfigForm.showed = false;
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        ConfigForm.showed = false;
        dispose();
    }

    public static void main() {
        //System.exit(0);
    }
}
