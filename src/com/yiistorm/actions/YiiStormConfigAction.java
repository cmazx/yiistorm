package com.yiistorm.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.yiistorm.forms.ConfigForm;

/**
 * Created with IntelliJ IDEA.
 * User: mazx
 * Date: 22.02.13
 * Time: 20:04
 * To change this template use File | Settings | File Templates.
 */
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
