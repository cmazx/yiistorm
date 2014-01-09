package com.yiistorm;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.JBColor;
import com.yiistorm.elements.ConfigParser;
import com.yiistorm.helpers.MigrationsCondition;
import org.jetbrains.annotations.Nls;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

public class YiiStormSettingsPage implements Configurable {

    private JCheckBox enableYiiStorm;
    private JTextField themeNameField;
    private JTextField langField;
    private JTextField yiicFileField;
    private JTextField yiiConfigPath;
    private JTextField yiiLitePath;
    private JCheckBox useMigrationsCheckbox;
    private JCheckBox useYiiCompleter;
    private PropertiesComponent properties;
    private JPanel panel;
    Project project;

    public YiiStormSettingsPage(Project project) {
        this.project = project;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "YiiStorm";
    }

    @Override
    public JComponent createComponent() {

        properties = PropertiesComponent.getInstance(project);
        panel = new JPanel();

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JPanel panel1 = new JPanel();
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

        enableYiiStorm = new JCheckBox("Enable Yii Storm for this project");
        panel1.add(enableYiiStorm);
        panel1.add(Box.createHorizontalGlue());
        panel.add(panel1);

        //chbox migrations
        JPanel panel12 = new JPanel();
        panel12.setLayout(new BoxLayout(panel12, BoxLayout.X_AXIS));

        useMigrationsCheckbox = new JCheckBox("Use migrations");
        useMigrationsCheckbox.setSelected(properties.getBoolean("useYiiMigrations", true));

        panel12.add(useMigrationsCheckbox);
        panel12.add(Box.createHorizontalGlue());
        panel.add(panel12);


        //strut
        panel.add(Box.createVerticalStrut(8));

        JPanel panel2 = new JPanel();
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));

        panel.add(panel2);


        //default lang name


        JPanel themePanellang = new JPanel();
        themePanellang.setLayout(new BorderLayout());
        themePanellang.setMaximumSize(new Dimension(5000, 25));

        JLabel langLabel = new JLabel("Default lang abbr:");
        langLabel.setSize(new Dimension(200, 20));
        langField = new JTextField(15);
        langLabel.setLabelFor(langField);
        langField.setMaximumSize(new Dimension(500, 20));
        themePanellang.add(langLabel, BorderLayout.WEST);
        themePanellang.add(langField, BorderLayout.CENTER);

        panel.add(Box.createVerticalStrut(8));
        panel.add(themePanellang);

        //theme name

        JPanel themePanel = new JPanel();
        themePanel.setLayout(new BorderLayout());
        themePanel.setMaximumSize(new Dimension(5000, 25));

        JLabel themeNameLabel = new JLabel("Project theme name:");
        themeNameLabel.setSize(new Dimension(200, 20));
        themeNameField = new JTextField(15);
        themeNameLabel.setLabelFor(themeNameField);
        themeNameField.setMaximumSize(new Dimension(500, 20));
        themePanel.add(themeNameLabel, BorderLayout.WEST);
        themePanel.add(themeNameField, BorderLayout.CENTER);

        panel.add(Box.createVerticalStrut(8));
        panel.add(themePanel);


        enableYiiStorm.setSelected(properties.getBoolean("enableYiiStorm", true));
        themeNameField.setText(properties.getValue("themeName", DefaultSettings.themeName));
        langField.setText(properties.getValue("langName", DefaultSettings.langName));


        initYiicPath();
        initYiiAppPanel();

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    public void initYiiAppPanel() {

        KeyListener toggleListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                useYiiCompleterDisabledToggle();
            }
        };

        JPanel panelYiiApp = new JPanel();
        panelYiiApp.setLayout(new BoxLayout(panelYiiApp, BoxLayout.Y_AXIS));
        panelYiiApp.setBorder(BorderFactory.createTitledBorder("Yii application completing"));

        //chbox migrations
        JPanel panelUseYiiiCompleter = new JPanel();
        panelUseYiiiCompleter.setLayout(new BoxLayout(panelUseYiiiCompleter, BoxLayout.X_AXIS));

        useYiiCompleter = new JCheckBox("Use Yii::app() completer");
        useYiiCompleter.setSelected(properties.getBoolean("useYiiCompleter", false));

        panelUseYiiiCompleter.add(useYiiCompleter);
        panelUseYiiiCompleter.add(Box.createHorizontalGlue());
        panelYiiApp.add(panelUseYiiiCompleter);

//YiiLite SELECT

        JPanel yiicPanel = new JPanel();
        yiicPanel.setLayout(new BorderLayout());
        yiicPanel.setMaximumSize(new Dimension(5000, 25));
        JLabel label = new JLabel("YiiLite.php file path:");
        label.setSize(new Dimension(200, 20));
        yiiLitePath = new JTextField(15);
        label.setLabelFor(yiiLitePath);
        yiiLitePath.setMaximumSize(new Dimension(500, 20));
        yiiLitePath.addKeyListener(toggleListener);
        JButton yiiLitePathSelect = new JButton("Select file");

        JPanel lpan = new JPanel();
        lpan.add(label).setSize(200, 20);
        yiicPanel.add(lpan, BorderLayout.WEST);
        yiicPanel.add(yiiLitePath, BorderLayout.CENTER);
        yiicPanel.add(yiiLitePathSelect, BorderLayout.EAST);
        panelYiiApp.add(Box.createVerticalStrut(4));
        panelYiiApp.add(yiicPanel);


        String yiicFile = properties.getValue("yiiLitePath");
        if (yiicFile != null) {
            yiiLitePath.setText(yiicFile);
        }

        yiiLitePathSelect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                VirtualFile baseDir = project.getBaseDir();
                if (baseDir != null) {
                    fileChooser.setCurrentDirectory(new File(baseDir.getPath()));
                    int ret = fileChooser.showDialog(null, "Открыть файл");
                    if (ret == JFileChooser.APPROVE_OPTION) {
                        yiiLitePath.setText(fileChooser.getSelectedFile().getAbsolutePath());
                        useYiiCompleterDisabledToggle();
                    }
                }
            }
        });


//CONFIG SELECT
        JPanel yiicConfigPanel = new JPanel();
        yiicConfigPanel.setLayout(new BorderLayout());
        yiicConfigPanel.setMaximumSize(new Dimension(5000, 25));
        JLabel ConfigLabel = new JLabel("Yii current config path:");
        ConfigLabel.setSize(new Dimension(200, 20));
        yiiConfigPath = new JTextField(15);
        yiiConfigPath.addKeyListener(toggleListener);
        ConfigLabel.setLabelFor(yiiConfigPath);
        yiiConfigPath.setMaximumSize(new Dimension(500, 20));
        JButton yiiConfigPathSelect = new JButton("Select file");

        JPanel ConfigLabelpan = new JPanel();
        ConfigLabelpan.add(ConfigLabel).setSize(200, 20);
        yiicConfigPanel.add(ConfigLabelpan, BorderLayout.WEST);
        yiicConfigPanel.add(yiiConfigPath, BorderLayout.CENTER);
        yiicConfigPanel.add(yiiConfigPathSelect, BorderLayout.EAST);
        panelYiiApp.add(Box.createVerticalStrut(4));
        panelYiiApp.add(yiicConfigPanel);


        String yiicConfigFile = properties.getValue("yiiConfigPath");
        if (yiicConfigFile != null) {
            yiiConfigPath.setText(yiicConfigFile);
        }

        yiiConfigPathSelect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                VirtualFile baseDir = project.getBaseDir();
                if (baseDir != null) {
                    fileChooser.setCurrentDirectory(new File(baseDir.getPath()));
                    int ret = fileChooser.showDialog(null, "Открыть файл");
                    if (ret == JFileChooser.APPROVE_OPTION) {
                        yiiConfigPath.setText(fileChooser.getSelectedFile().getAbsolutePath());
                        useYiiCompleterDisabledToggle();
                    }
                }
            }
        });
        boolean empty = properties.getValue("yiiLitePath") == null || properties.getValue("yiiConfigPath") == null;
        if (!empty) {
            empty = properties.getValue("yiiLitePath").isEmpty() || properties.getValue("yiiConfigPath").isEmpty();
        }
        if (empty) {
            useYiiCompleter.setEnabled(false);
        } else {
            useYiiCompleterDisabledToggle();
        }

        panel.add(panelYiiApp);
    }

    public void useYiiCompleterDisabledToggle() {
        if (yiiLitePath.getText().length() > 0) {
            checkYiiAppParams();
        }
        boolean filled = !yiiLitePath.getText().isEmpty() && !yiiConfigPath.getText().isEmpty();
        if (filled) {
            filled = yiiLitePath.getBackground().equals(JBColor.GREEN) && yiiConfigPath.getBackground().equals(JBColor.GREEN);
        }
        useYiiCompleter.setEnabled(filled);
        if (!filled) {
            useYiiCompleter.setSelected(false);
        }
    }

    public void initYiicPath() {
        JPanel yiicPanel = new JPanel();
        yiicPanel.setLayout(new BorderLayout());
        yiicPanel.setMaximumSize(new Dimension(5000, 25));
        JLabel yiicFileFieldLabel = new JLabel("Yiic.php path:");
        yiicFileFieldLabel.setSize(new Dimension(200, 20));
        yiicFileField = new JTextField(15);
        yiicFileFieldLabel.setLabelFor(yiicFileField);
        yiicFileField.setMaximumSize(new Dimension(500, 20));
        JButton yiicPathSelect = new JButton("Select file");


        yiicPanel.add(yiicFileFieldLabel, BorderLayout.WEST);
        yiicPanel.add(yiicFileField, BorderLayout.CENTER);
        yiicPanel.add(yiicPathSelect, BorderLayout.EAST);
        panel.add(Box.createVerticalStrut(4));
        panel.add(yiicPanel);


        String yiicFile = properties.getValue("yiicFile");
        if (yiicFile != null) {
            yiicFileField.setText(yiicFile);
        }

        yiicPathSelect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                VirtualFile baseDir = project.getBaseDir();
                if (baseDir != null) {
                    fileChooser.setCurrentDirectory(new File(baseDir.getPath()));
                    int ret = fileChooser.showDialog(null, "Открыть файл");
                    if (ret == JFileChooser.APPROVE_OPTION) {
                        yiicFileField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                    }
                }
            }
        });
    }

    public void checkYiiAppParams() {

        ConfigParser parser = new ConfigParser(YiiStormProjectComponent.getInstance(project));
        if (yiiLitePath.getText().length() > 0) {
            if (parser.testYiiLitePath(yiiLitePath.getText())) {
                yiiLitePath.setBackground(JBColor.GREEN);
                if (yiiConfigPath.getText().length() > 0) {
                    if (parser.testYiiConfigPath(yiiConfigPath.getText())) {
                        yiiConfigPath.setBackground(JBColor.GREEN);
                    } else {
                        yiiConfigPath.setBackground(JBColor.PINK);
                    }
                } else {
                    yiiConfigPath.setBackground(JBColor.background());
                }
            } else {
                yiiLitePath.setBackground(JBColor.PINK);
            }
        } else {
            yiiLitePath.setBackground(JBColor.background());
        }
    }

    @Override
    public void apply() throws ConfigurationException {
        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        properties.setValue("enableYiiStorm", String.valueOf(enableYiiStorm.isSelected()));
        properties.setValue("themeName", themeNameField.getText());
        properties.setValue("langName", langField.getText());
        properties.setValue("yiicFile", yiicFileField.getText());
        properties.setValue("yiiConfigPath", yiiConfigPath.getText());
        properties.setValue("yiiLitePath", yiiLitePath.getText());
        properties.setValue("useYiiCompleter", String.valueOf(useYiiCompleter.isSelected()));
        properties.setValue("useYiiMigrations", String.valueOf(useMigrationsCheckbox.isSelected()));

        final ToolWindowManager manager = ToolWindowManager.getInstance(project);
        final ToolWindow tw = manager.getToolWindow("Migrations");
        if (tw != null) {
            tw.setAvailable(MigrationsCondition.makeCondition(project), null);
        }
        if (properties.getBoolean("useYiiCompleter", false)) {
            YiiStormProjectComponent.getInstance(project).loadConfigParser();
        } else {
            YiiStormProjectComponent.getInstance(project).clearConfigParser();
        }

    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public String getHelpTopic() {
        return null;
    }

    @Override
    public void disposeUIResources() {

    }

    @Override
    public void reset() {

    }
}
