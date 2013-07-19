package com.yiistorm;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.yiistorm.helpers.MigrationsCondition;
import org.jetbrains.annotations.Nls;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class YiiStormSettingsPage implements Configurable {

    private JCheckBox enableYiiStorm;
    private JTextField themeNameField;
    private JTextField yiicFileField;
    private JButton yiicPathSelect;
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

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JPanel panel1 = new JPanel();
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

        enableYiiStorm = new JCheckBox("Enable Yii Storm for this project");
        panel1.add(enableYiiStorm);
        panel1.add(Box.createHorizontalGlue());

        panel.add(panel1);
        panel.add(Box.createVerticalStrut(8));

        JPanel panel2 = new JPanel();
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));

        panel.add(panel2);

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


        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        enableYiiStorm.setSelected(properties.getBoolean("enableYiiStorm", true));
        themeNameField.setText(properties.getValue("themeName", DefaultSettings.themeName));


        JPanel yiicPanel = new JPanel();
        yiicPanel.setLayout(new BorderLayout());
        yiicPanel.setMaximumSize(new Dimension(5000, 25));
        JLabel yiicFileFieldLabel = new JLabel("Yiic manager path:");
        yiicFileFieldLabel.setSize(new Dimension(200, 20));
        yiicFileField = new JTextField(15);
        yiicFileFieldLabel.setLabelFor(yiicFileField);
        yiicFileField.setMaximumSize(new Dimension(500, 20));
        yiicPathSelect = new JButton("Select yiic file");


        yiicPanel.add(yiicFileFieldLabel, BorderLayout.WEST);
        yiicPanel.add(yiicFileField, BorderLayout.CENTER);
        yiicPanel.add(yiicPathSelect, BorderLayout.EAST);
        panel.add(Box.createVerticalStrut(4));
        panel.add(yiicPanel);

        panel.add(Box.createVerticalGlue());


        String yiicFile = properties.getValue("yiicFile");
        if (yiicFile != null) {
            yiicFileField.setText(yiicFile);
        }

        yiicPathSelect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                VirtualFile baseDir = project.getBaseDir();
                fileChooser.setCurrentDirectory(new File(baseDir.getPath()));
                int ret = fileChooser.showDialog(null, "Открыть файл");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    yiicFileField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            }
        });

        return panel;
    }

    @Override
    public void apply() throws ConfigurationException {
        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        properties.setValue("enableYiiStorm", String.valueOf(enableYiiStorm.isSelected()));
        properties.setValue("themeName", themeNameField.getText());
        properties.setValue("yiicFile", yiicFileField.getText());

        final ToolWindowManager manager = ToolWindowManager.getInstance(project);
        final ToolWindow tw = manager.getToolWindow("Migrations");
        if (tw != null) {
            tw.setAvailable(MigrationsCondition.makeCondition(project), null);
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
