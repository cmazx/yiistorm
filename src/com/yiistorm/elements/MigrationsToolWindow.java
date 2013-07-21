package com.yiistorm.elements;

import com.intellij.ide.plugins.PluginManager;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.util.ProgressIndicatorBase;
import com.intellij.openapi.project.DumbModeAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ex.ProgressIndicatorEx;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.yiistorm.forms.NewMigrationForm;
import com.yiistorm.helpers.CommonHelper;

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


public class MigrationsToolWindow implements ToolWindowFactory {
    private Yiic yiic;
    static final public int ADD_MENUS_BACKGROUND_ACTION = 0;
    static final public int UPDATE_MIGRAITIONS_MENUS_BACKGROUND_ACTION = 1;
    static final public int CREATE_MIGRATION_BACKGROUND_ACTION = 2;
    static final public int APPLY_MIGRATIONS_BACKGROUND_ACTION = 3;
    public static MigrationsToolWindow toolw;
    private JPanel contentPane;
    private JTextArea migrateLog;
    private JBScrollPane scrollpane;
    private JTextField createMigrationName;
    private VerticalMenuBar actionMenuBar;
    private Cursor waitCursor = new Cursor(Cursor.WAIT_CURSOR);
    private Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
    private static String OS = System.getProperty("os.name").toLowerCase();
    private Project _project;
    private String yiiFile;
    private String yiiProtected;
    private ArrayList<String> newMigrationsList = new ArrayList<String>();
    public boolean NewFormDisplayed = false;
    final JMenuItem createMenu = new JMenuItem("Create new");
    JMenu actionMenu = new JMenu();
    NewMigrationForm newMigrationDialog;

    public Project getProject() {
        return _project;
    }


    public String runCommand(String migrateCommand) {
        try {

            Process p = null;
            String prependPath = CommonHelper.getCommandPrepend();


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
            setMigrateLogText(text);
        }

    }

    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {


        _project = project;
        toolw = this;
        yiic = new Yiic();

        PropertiesComponent properties = PropertiesComponent.getInstance(getProject());
        yiiFile = properties.getValue("yiicFile");
        setMigrateLogText("");

        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());

        scrollpane = new JBScrollPane();
        migrateLog = new JTextArea("Yii migrations");
        //migrateLog.setLineWrap(true);
        migrateLog.setEditable(false);
        migrateLog.setEnabled(true);
        scrollpane.setLayout(new ScrollPaneLayout());
        scrollpane.getViewport().add(migrateLog);
        contentPane.add(scrollpane, BorderLayout.CENTER);


        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout());
        actionMenuBar = new VerticalMenuBar();
        buttonsPanel.add(actionMenuBar);

        contentPane.add(buttonsPanel, BorderLayout.WEST);

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(contentPane, "", false);
        toolWindow.getContentManager().addContent(content);
        if (yiiFile != null && yiic.yiicIsRunnable(yiiFile)) {
            yiiProtected = yiiFile.replaceAll("yiic.(bat|php)$", "");
            runBackgroundTask(this.ADD_MENUS_BACKGROUND_ACTION, project);
        } else {
            setMigrateLogText("Set path to yiic in project settings -> YiiStorm");
        }

    }


    public void runBackgroundTask(final int Action, Project project) {
        final Task.Backgroundable task = new Task.Backgroundable(project, "Yii migrations", false) {

            @Override
            public String getProcessId() {
                return "Yii migrations";
            }

            @Override
            public DumbModeAction getDumbModeAction() {
                return DumbModeAction.CANCEL;
            }

            public void run(final ProgressIndicator indicator) {
                final Task.Backgroundable this_task = this;
                ((ProgressIndicatorEx) indicator).addStateDelegate(new ProgressIndicatorBase() {
                    @Override
                    public void cancel() {
                        this_task.onCancel();
                    }
                });
                switch (Action) {
                    case MigrationsToolWindow.ADD_MENUS_BACKGROUND_ACTION:
                        indicator.setText("Updating migrations list");
                        indicator.setFraction(0.1);
                        MigrationsToolWindow.toolw.updateNewMigrations(true);
                        indicator.setFraction(0.5);
                        MigrationsToolWindow.toolw.addMenus();
                        indicator.setFraction(0.8);
                        break;
                    case MigrationsToolWindow.UPDATE_MIGRAITIONS_MENUS_BACKGROUND_ACTION:
                        indicator.setText("Updating migrations list");
                        indicator.setFraction(0.1);
                        MigrationsToolWindow.toolw.updateNewMigrations(true);
                        indicator.setFraction(0.5);
                        indicator.setText("Updating migrations menu");
                        MigrationsToolWindow.toolw.fillActionMenu();
                        indicator.setFraction(0.8);
                        break;
                    case MigrationsToolWindow.APPLY_MIGRATIONS_BACKGROUND_ACTION:
                        indicator.setText("Applying migrations list");
                        indicator.setFraction(0.1);
                        MigrationsToolWindow.toolw.applyMigrations();
                        indicator.setFraction(0.3);
                        MigrationsToolWindow.toolw.updateNewMigrations(false);
                        indicator.setFraction(0.5);
                        indicator.setText("Updating migrations menu");
                        MigrationsToolWindow.toolw.fillActionMenu();
                        indicator.setFraction(0.8);
                        break;
                    case MigrationsToolWindow.CREATE_MIGRATION_BACKGROUND_ACTION:
                        indicator.setText("Creating migration: " + newMigrationDialog.getMigrationName());
                        indicator.setFraction(0.1);
                        MigrationsToolWindow.toolw.createMigrationByName(newMigrationDialog.getMigrationName());
                        indicator.setFraction(0.3);
                        MigrationsToolWindow.toolw.updateNewMigrations(false);
                        ArrayList<String> migrationsList = MigrationsToolWindow.toolw.getMigrationsList();
                        MigrationsToolWindow.toolw.openMigrationFile(migrationsList.get(migrationsList.size() - 1));
                        indicator.setFraction(0.5);
                        indicator.setText("Updating migrations menu");
                        MigrationsToolWindow.toolw.fillActionMenu();
                        indicator.setFraction(0.8);

                        break;
                }

                indicator.stop();
            }

        };
        task.setCancelText("Stop processing").queue();
    }

    public void createMigrationByName(String name) {
        setMigrateLogText(this.runCommand("migrate create " + name));
    }

    public void setMigrateLogText(final String text) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                migrateLog.setText(text);
            }
        });
    }

    public ArrayList<String> getMigrationsList() {
        return newMigrationsList;
    }

    public void fillActionMenu() {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                actionMenu.removeAll();

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
                } else {
                    JMenuItem noMigration = new JMenuItem("Where no new migrations");
                    noMigration.setEnabled(false);
                    actionMenu.add(noMigration);
                }
            }
        });
    }

    public void applyMigrations() {
        String text = this.runCommand("migrate");
        setMigrateLogText(text);
    }

    /**
     * Add menu to contentPane
     */
    public void addMenus() {
        final MigrationsToolWindow me = this;
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                actionMenu.setText("Actions");
                actionMenu.setBackground(Color.WHITE);
                actionMenu.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                actionMenuBar.add(actionMenu);
                fillActionMenu();
                JMenuItem updateMenu = new JMenuItem("Update list");
                if (updateMenu.getActionListeners().length < 1) {
                    updateMenu.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            updateNewMigrations(true);
                            fillActionMenu();
                        }
                    });
                }
                updateMenu.setBackground(Color.WHITE);
                updateMenu.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                actionMenuBar.add(updateMenu);

                //apply migrations
                final JMenuItem applyAllMenu = new JMenuItem("Apply all");
                if (applyAllMenu.getActionListeners().length < 1) {
                    applyAllMenu.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            runBackgroundTask(MigrationsToolWindow.APPLY_MIGRATIONS_BACKGROUND_ACTION, _project);
                        }
                    });
                }
                applyAllMenu.setBackground(Color.WHITE);
                applyAllMenu.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                actionMenuBar.add(applyAllMenu);

                //create migration
                if (createMenu.getActionListeners().length < 1) {
                    createMenu.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            showCreateForm();
                        }
                    });
                }
                createMenu.setBackground(Color.WHITE);
                createMenu.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                actionMenuBar.add(createMenu);
            }
        });
    }

    public void showCreateForm() {
        final MigrationsToolWindow migrForm = this;
        if (!NewFormDisplayed) {
            newMigrationDialog = new NewMigrationForm(migrForm);
            NewFormDisplayed = true;
            newMigrationDialog.pack();
            newMigrationDialog.setVisible(true);
        }
    }

    public void openMigrationFile(final String name) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                String migrationPath = yiiProtected.replace(_project.getBasePath(), "").replace("\\", "/");
                VirtualFile baseDir = _project.getBaseDir();
                if (baseDir != null) {
                    VirtualFile migrationsFolder = baseDir.findFileByRelativePath(migrationPath + "migrations/");
                    if (migrationsFolder != null) {
                        migrationsFolder.refresh(false, true);
                        VirtualFile migrationFile = migrationsFolder.findFileByRelativePath(name + ".php"); //migrationPath + "migrations/" +
                        if (migrationFile != null) {
                            OpenFileDescriptor of = new OpenFileDescriptor(_project, migrationFile);
                            if (of.canNavigate()) {
                                of.navigate(true);
                            }
                        } else {
                            PluginManager.getLogger().error("Migrations file not founded");
                        }
                    } else {
                        PluginManager.getLogger().error("Migrations folder not founded");
                    }
                }
            }
        });

    }
}
