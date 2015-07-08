package com.yiistorm.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.yiistorm.forms.ConfigForm;

public class YiiStormConfigAction extends YiiStormActionAbstract {
    public Project project;

    @Override
    public void executeAction() {
        ConfigForm form = new ConfigForm(this);
        form.setVisible(true);
    }

    @Override
    public Boolean isApplicable(AnActionEvent e) {
        return true;
    }
}
