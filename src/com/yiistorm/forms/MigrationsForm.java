package com.yiistorm.forms;

import com.yiistorm.actions.YiiStormMigrateAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created with IntelliJ IDEA.
 * User: mazx
 * Date: 25.03.13
 * Time: 22:48
 * To change this template use File | Settings | File Templates.
 */
public class MigrationsForm extends JDialog {
    private JPanel contentPane;
    private JTextArea migrateLog;
    private JButton createMigration;
    private JButton applyMigration;
    private JButton closeButton;
    private YiiStormMigrateAction currentAction;
    private Cursor waitCursor = new Cursor(Cursor.WAIT_CURSOR);
    private Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);

    public MigrationsForm(YiiStormMigrateAction action) {
        final JDialog me = this;
        setCurrentAction(action);
        setContentPane(contentPane);
        setModal(true);
        setBounds(500, 500, 400, 200);

        applyMigration.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    me.setCursor(waitCursor);
                    migrateLog.setText("Migrate in process");
                    String text = currentAction.runCommand(currentAction.applyMigration);
                    migrateLog.setText(text);
                    me.setCursor(defaultCursor);
                } catch (Exception ex) {

                }

            }
        });

        createMigration.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

    }

    public void setText(String text) {
        migrateLog.setText(text);
    }

    public void setCurrentAction(YiiStormMigrateAction action) {
        currentAction = action;
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }
}
