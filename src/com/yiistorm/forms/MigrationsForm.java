package com.yiistorm.forms;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created with IntelliJ IDEA.
 * User: mazx
 * Date: 25.03.13
 * Time: 22:48
 * To change this template use File | Settings | File Templates.
 */
public class MigrationsForm implements ToolWindowFactory {
    private JPanel contentPane;
    private JTextArea migrateLog;
    private JButton createMigration;
    private JButton applyMigration;
    private JBScrollPane scrollpane;
    private JTextField createMigrationName;
    private Cursor waitCursor = new Cursor(Cursor.WAIT_CURSOR);
    private Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
    private static String OS = System.getProperty("os.name").toLowerCase();
    private Project _project;

    public Project getProject() {
        return _project;
    }

    public String runCommand(String migrateCommand) {
        try {

            Process p = null;
            String prependPath = "";
            if (isWindows()) {
                prependPath = "cmd /c ";
            }

            PropertiesComponent properties = PropertiesComponent.getInstance(getProject());

            String path = properties.getValue("yiicFile");

            if (path == null) {
                return "Please select path to yiic in YiiStorm config.";
            }

            prependPath += path;
            p = Runtime.getRuntime().exec(prependPath + " " + migrateCommand + " --interactive=0");   //+ "D:\\webservers\\home\\www.rest.lcl\\www\\protected\\"

            String lineAll = "";
            if (p != null) {
                //p.waitFor();
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line = reader.readLine();
                while (line != null) {
                    line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    lineAll += line + "\n";
                }
            }
            return lineAll;
        } catch (Exception e1) {
            return "Error! " + e1.getMessage();
        }

    }

    public static boolean isWindows() {

        return (OS.indexOf("win") >= 0);

    }
    //public MigrationsForm(YiiStormMigrateAction action) {
    // final JDialog me = this;
    // setTitle("YiiStorm migrations");
    // setCurrentAction(action);
    //  setContentPane(contentPane);
    //  setModal(true);
    //  setBounds(500, 500, 400, 200);
       /* JScrollPane sp = new JBScrollPane(migrateLog);


                */
    //}

    public void setText(String text) {
        migrateLog.setText(text);
    }


    private void onCancel() {
// add your code here if necessary
        //  dispose();
    }

    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        _project = project;
        final MigrationsForm me = this;
        applyMigration.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                migrateLog.setText("Migrate in process");
                applyMigration.setEnabled(false);
                applyMigration.setText("...working...");
                String text = me.runCommand("migrate");
                migrateLog.setText(text);
                applyMigration.setEnabled(true);
                applyMigration.setText("Apply all migrations");
            }
        });

        createMigration.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.err.println(createMigrationName.getText().isEmpty());
                System.err.println(createMigrationName.getText().length());
                if (createMigrationName.getText().trim().length() > 0) {
                    createMigration.setEnabled(false);

                    createMigration.setText("...working...");
                    migrateLog.setText("Creating new migration in process");
                    String text = me.runCommand("migrate create " + createMigrationName.getText());
                    migrateLog.setText("Creating migration '" + createMigrationName.getText() + "'\n" + text);
                    createMigration.setEnabled(true);
                    createMigration.setText("Create migration");
                } else {
                    migrateLog.setText("Fill migration name field before creating new migration.");
                }
            }
        });

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(contentPane, "", false);
        toolWindow.getContentManager().addContent(content);

    }
}
