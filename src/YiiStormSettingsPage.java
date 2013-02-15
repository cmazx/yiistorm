import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;

import javax.swing.*;

import com.intellij.ide.util.PropertiesComponent;

import java.awt.*;

public class YiiStormSettingsPage implements Configurable {

    private JCheckBox enableYiiStorm;
    private JTextField secretKeyTextField;
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
        panel.setLayout(new BoxLayout
                (panel, BoxLayout.Y_AXIS));
        JPanel panel1 = new JPanel();
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

        enableYiiStorm = new JCheckBox("Enable Yii Storm for this project");
        panel1.add(enableYiiStorm);
        panel1.add(Box.createHorizontalGlue());

        JPanel panel2 = new JPanel();
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));


        JPanel panel3 = new JPanel();
        panel3.setLayout(new BoxLayout(panel3, BoxLayout.X_AXIS));

        JLabel label2 = new JLabel("Secret key:");

        secretKeyTextField = new JTextField(15);
        label2.setLabelFor(secretKeyTextField);
        panel3.add(label2);
        panel3.add(secretKeyTextField);
        panel3.add(Box.createHorizontalGlue());
        secretKeyTextField.setMaximumSize(new Dimension(50,20));

        panel.add(panel1);
        panel.add(Box.createVerticalStrut(8));

        panel.add(panel2);
        panel.add(Box.createVerticalStrut(8));
        panel.add(panel3);
        panel.add(Box.createVerticalGlue());
        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        enableYiiStorm.setSelected(properties.getBoolean("enableYiiStorm", true));
        secretKeyTextField.setText(properties.getValue("yiiStormSecretKey", DefaultSettings.secretKey));

        return panel;
    }

    @Override
    public void apply() throws ConfigurationException {
        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        properties.setValue("enableYiiStorm", String.valueOf(enableYiiStorm.isSelected()));
        properties.setValue("yiiStormSecretKey", secretKeyTextField.getText());

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
