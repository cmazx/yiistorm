package com.yiistorm.elements;

import com.intellij.ide.plugins.PluginManager;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.yiistorm.forms.NewMigrationForm;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class MigrationsToolWindow implements ToolWindowFactory {
    private static final int ADD_MENUS_BACKGROUND_ACTION = 0;
    private static final int UPDATE_MIGRAITIONS_MENUS_BACKGROUND_ACTION = 1;
    static final public int CREATE_MIGRATION_BACKGROUND_ACTION = 2;
    static final public int APPLY_MIGRATIONS_BACKGROUND_ACTION = 3;
    static final public int MIGRATE_DOWN_BACKGROUND_ACTION = 4;
    private static MigrationsToolWindow toolw;
    private JTextArea migrateLog;
    private JBScrollPane scrollpane;
    private JMenuBar actionMenuBar;
    private Project _project;
    private String yiiFile;
    private String yiiPath;
    private ArrayList<String> newMigrationsList = new ArrayList<String>();
    public boolean NewFormDisplayed = false;
    private JMenu actionMenu = new JMenu();
    private NewMigrationForm newMigrationDialog;
    private JPanel buttonsPanel = new JPanel();
    private boolean MenusAdded = false;
    private Yii yii;

    public Project getProject() {
        return _project;
    }

    /**
     * Update new migrations list
     */
    private void updateNewMigrations(boolean writeLog) {
        updateNewMigrations(writeLog, false);
    }

    /**
     * Update new migrations list
     */
    private void updateNewMigrations(boolean writeLog, boolean openFirst) {
        String text;
        if (yii == null) {
            text = "Please select path to yii console in YiiStorm config.";
        } else {
            text = yii.migrateHistory();
            try {
                Pattern regex = Pattern.compile("\\s+(m\\d+?_.+?)(?:\n|$)", Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE);
                Matcher regexMatcher = regex.matcher(text);
                newMigrationsList.clear();

                while (regexMatcher.find()) {
                    newMigrationsList.add(regexMatcher.group(1));
                }
                if (openFirst) {
                    if (newMigrationsList.size() > 0) {
                        MigrationsToolWindow.toolw.openMigrationFile(newMigrationsList.get(newMigrationsList.size() - 1));
                    }
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
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        _project = project;
        toolw = this;

        PropertiesComponent properties = PropertiesComponent.getInstance(getProject());

        if (properties.getBoolean("useYiiMigrations", false)) {
            String yiiFile = properties.getValue("yiicFile");
            if (yiiFile != null) {
                yii = Yii.getInstance(yiiFile);
                yiiPath = yiiFile.replaceAll("yii[c]*.(bat|php)$", "");
            }
        }

        setMigrateLogText("");

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());

        scrollpane = new JBScrollPane();
        migrateLog = new JTextArea("Yii migrations");
        //migrateLog.setLineWrap(true);
        migrateLog.setEditable(false);
        migrateLog.setEnabled(true);
        scrollpane.setLayout(new ScrollPaneLayout());
        scrollpane.getViewport().add(migrateLog);
        contentPane.add(scrollpane, BorderLayout.CENTER);

        FlowLayout layout = new FlowLayout();
        layout.setAlignment(FlowLayout.LEFT);
        buttonsPanel.setLayout(layout);
        actionMenuBar = new JMenuBar();
        actionMenuBar.setBackground(new JBColor(new Color(0, 0, 0, 0), new Color(0, 0, 0, 0)));
        actionMenuBar.setLayout(layout);
        actionMenuBar.setBorderPainted(false);
        buttonsPanel.add(actionMenuBar);


        contentPane.add(buttonsPanel, BorderLayout.NORTH);


        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(contentPane, "", false);
        toolWindow.getContentManager().addContent(content);

        if (yii != null) {
            runBackgroundTask(ADD_MENUS_BACKGROUND_ACTION, project);
        } else {
            setMigrateLogText("Set path to yii in project settings -> YiiStorm");
        }

    }


    public void runBackgroundTask(final int Action, Project project) {
        final Task.Backgroundable task = new Task.Backgroundable(project, "Yii migrations", false) {

            @Override
            public String getProcessId() {
                return "Yii migrations";
            }

            public void run(@NotNull final ProgressIndicator indicator) {

                switch (Action) {
                    case MigrationsToolWindow.MIGRATE_DOWN_BACKGROUND_ACTION:
                        indicator.setText("Migrating 1 down");
                        indicator.setFraction(0.1);
                        MigrationsToolWindow.toolw.migrateDown();
                        indicator.setFraction(0.3);
                        MigrationsToolWindow.toolw.updateNewMigrations(true);
                        indicator.setFraction(0.5);
                        indicator.setText("Updating migrations menu");
                        MigrationsToolWindow.toolw.fillActionMenu();
                        indicator.setFraction(0.8);
                        break;
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
                        MigrationsToolWindow.toolw.updateNewMigrations(false, true);
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

    private void createMigrationByName(String name) {

        setMigrateLogText(yii.migrateCreate(name));
    }

    private void setMigrateLogText(final String text) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                migrateLog.setText(text);
            }
        });
    }

    private void fillActionMenu() {
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

    private void applyMigrations() {
        setMigrateLogText(yii.migrateUp());
    }

    private void migrateDown() {
        setMigrateLogText(yii.migrateDown());
    }

    /**
     * Add menu to contentPane
     */
    private void addMenus() {
        if (MenusAdded) {
            return;
        }
        MenusAdded = true;
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {

                actionMenu.setToolTipText("New migrations");
                actionMenu.setSize(15, 15);

                ImageIcon icon = new ImageIcon(this.getClass().getResource("/com/yiistorm/images/list.png"));
                actionMenu.setIcon(icon);
                actionMenuBar.add(actionMenu);
                fillActionMenu();

                MigrationsToolWindow.addImageButton(
                        buttonsPanel,
                        "Update list",
                        this.getClass().getResource("/com/yiistorm/images/reload.png"),
                        new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                runBackgroundTask(MigrationsToolWindow.UPDATE_MIGRAITIONS_MENUS_BACKGROUND_ACTION, _project);
                            }
                        });

                //apply migrations
                MigrationsToolWindow.addImageButton(
                        buttonsPanel,
                        "Migrate up",
                        this.getClass().getResource("/com/yiistorm/images/up.png"),
                        new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                runBackgroundTask(MigrationsToolWindow.APPLY_MIGRATIONS_BACKGROUND_ACTION, _project);
                            }
                        });

                //migrate down
                MigrationsToolWindow.addImageButton(
                        buttonsPanel,
                        "Migrate one down",
                        this.getClass().getResource("/com/yiistorm/images/down.png"),
                        new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                runBackgroundTask(MigrationsToolWindow.MIGRATE_DOWN_BACKGROUND_ACTION, _project);
                            }
                        });


                //create migration
                MigrationsToolWindow.addImageButton(
                        buttonsPanel,
                        "Create migration",
                        this.getClass().getResource("/com/yiistorm/images/add.png"),
                        new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                showCreateForm();
                            }
                        });

            }
        });
    }

    private static void addImageButton(JPanel panel, String toolTip, URL image, ActionListener listener) {
        ImageIcon updicon = new ImageIcon(image);
        JButton updateButton = new JButton(updicon);
        updateButton.setPressedIcon(updicon);
        updateButton.setFocusable(false);
        updateButton.setToolTipText(toolTip);
        //updateButton.setBorder(BorderFactory.createLineBorder(S));
        updateButton.setContentAreaFilled(false);
        panel.add(updateButton);

        if (updateButton.getActionListeners().length < 1) {
            updateButton.addActionListener(listener);
        }
    }

    private void showCreateForm() {
        final MigrationsToolWindow migrForm = this;
        if (!NewFormDisplayed) {
            newMigrationDialog = new NewMigrationForm(migrForm);
            NewFormDisplayed = true;
            newMigrationDialog.pack();
            newMigrationDialog.setVisible(true);
        }
    }

    private void openMigrationFile(final String name) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                if (_project.getBasePath() == null) {
                    return;
                }
                String migrationFilename = name;
                VirtualFile baseDir = _project.getBaseDir();
                String basePath = baseDir.getPath();
                String migrationPath = yiiPath.replace("\\", "/").replace(basePath, "");
                if (migrationFilename.contains("(")) {
                    migrationFilename = migrationFilename.replaceFirst(" \\(.+$", "");
                }

                VirtualFile migrationsFolder = baseDir.findFileByRelativePath(migrationPath + "migrations/");
                if (migrationsFolder == null) {
                    migrationsFolder = baseDir.findFileByRelativePath(migrationPath + "console/migrations");
                }
                if (migrationsFolder != null) {
                    migrationsFolder.refresh(false, true);
                    VirtualFile migrationFile = migrationsFolder.findFileByRelativePath(migrationFilename + ".php");
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
        });

    }


}
