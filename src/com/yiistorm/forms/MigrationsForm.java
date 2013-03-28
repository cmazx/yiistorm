package com.yiistorm.forms;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
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
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private JBScrollPane scrollpane;
    private JTextField createMigrationName;
    private JMenuBar actionMenuBar;
    private JMenu actionMenu;
    private Cursor waitCursor = new Cursor(Cursor.WAIT_CURSOR);
    private Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
    private static String OS = System.getProperty("os.name").toLowerCase();
    private Project _project;
    private String yiiFile;
    private String yiiProtected;
    private ArrayList<String> newMigrationsList = new ArrayList<String>();
    public boolean NewFormDisplayed = false;
    final JMenuItem createMenu = new JMenuItem("Create new");

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


            if (yiiFile == null) {
                return "Please select path to yiic in YiiStorm config.";
            }

            prependPath += yiiFile;
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


    public void setText(String text) {
        migrateLog.setText(text);
    }


    public void updateNewMigrations(boolean writeLog) {  //yiic migrate new
        String text = "";
        if (yiiFile == null) {
            text = "Please select path to yiic in YiiStorm config.";
        } else {
            text = this.runCommand("migrate new");
            try {
                Pattern regex = Pattern.compile("\\s+(m\\d+?_.+?)(?:\n|$)", Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE);
                Matcher regexMatcher = regex.matcher(text);
                newMigrationsList.clear();

                while (regexMatcher.find()) {
                    newMigrationsList.add(regexMatcher.group(1));
                }
            } catch (Exception ex) {
                // Syntax error in the regular expression
            }

        }
        if (writeLog) {
            migrateLog.setText(text);
        }
    }

    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        _project = project;
        final MigrationsForm me = this;

        PropertiesComponent properties = PropertiesComponent.getInstance(getProject());
        yiiFile = properties.getValue("yiicFile");
        yiiProtected = yiiFile.replaceAll("yiic.(bat|php)$", "");

        updateNewMigrations(true);


        /*createMigration.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (createMigrationName.getText().trim().length() > 0) {
                    createMigration.setEnabled(false);

                    createMigration.setText("...working...");
                    migrateLog.setText("Creating new migration in process");
                    String text = me.runCommand("migrate create " + createMigrationName.getText());
                    migrateLog.setText("Creating migration '" + createMigrationName.getText() + "'\n" + text);
                    createMigration.setEnabled(true);
                    createMigration.setText("Create migration");
                    createMigrationName.setText("");
                    recreateMenus();
                    openMigrationFile(newMigrationsList.get(newMigrationsList.size() - 1));
                } else {
                    migrateLog.setText("Fill migration name field before creating new migration.");
                }
            }
        });    */


        recreateMenus();

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(contentPane, "", false);
        toolWindow.getContentManager().addContent(content);
    }

    public void recreateMenus() {
        actionMenuBar.removeAll();
        updateNewMigrations(false);
        addMenus();
    }

    public void setMigrateLogText(String text) {
        migrateLog.setText(text);
    }

    public ArrayList<String> getMigrationsList() {
        return newMigrationsList;
    }

    /**
     * Add menu to contentPane
     */
    public void addMenus() {
        final MigrationsForm me = this;
        JMenu actionMenu = new JMenu("Open migration");
        //update migrations list
        //migrations list
        if (newMigrationsList != null && newMigrationsList.size() > 0) {
            JMenu migrationsMenu = new JMenu("Open new migration");
            actionMenu.add(migrationsMenu);
            for (String migration : newMigrationsList) {

                final String migrationName = migration;
                migrationsMenu.add(new JMenuItem(migrationName));
                migrationsMenu.getItem(migrationsMenu.getItemCount() - 1).addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        openMigrationFile(migrationName);
                    }
                });

            }
        }

        actionMenu.setBackground(Color.WHITE);
        actionMenu.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        actionMenuBar.add(actionMenu);

        JMenuItem updateMenu = new JMenuItem("Update migration list");
        updateMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionMenuBar.removeAll();
                updateNewMigrations(true);
                addMenus();
            }
        });
        updateMenu.setBackground(Color.WHITE);
        updateMenu.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        actionMenuBar.add(updateMenu);

        //apply migrations
        final JMenuItem applyAllMenu = new JMenuItem("Apply all");
        applyAllMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                migrateLog.setText("Migrate in progress");
                applyAllMenu.setEnabled(false);
                applyAllMenu.setText("...applying...");
                String text = me.runCommand("migrate");
                migrateLog.setText(text);
                applyAllMenu.setEnabled(true);
                applyAllMenu.setText("Apply all migrations");
                recreateMenus();
            }
        });
        applyAllMenu.setBackground(Color.WHITE);
        applyAllMenu.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        actionMenuBar.add(applyAllMenu);

        //create migration
        createMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCreateForm();
            }
        });
        createMenu.setBackground(Color.WHITE);
        createMenu.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        actionMenuBar.add(createMenu);

    }

    public void showCreateForm() {
        final MigrationsForm migrForm = this;
        if (!NewFormDisplayed) {
            NewMigrationForm dialog = new NewMigrationForm(migrForm);
            NewFormDisplayed = true;
            dialog.pack();
            dialog.setVisible(true);
        }
    }

    public void openMigrationFile(String name) {
        String migrationPath = yiiProtected.replace(_project.getBasePath(), "").replace("\\", "/");
        _project.getBaseDir().findFileByRelativePath(migrationPath + "migrations/").refresh(false, false);
        VirtualFile migrationFile = _project.getBaseDir().findFileByRelativePath(migrationPath + "migrations/" + name + ".php");
        if (migrationFile != null) {
            new OpenFileDescriptor(_project, migrationFile, 0).navigate(true);
        }
    }
}
