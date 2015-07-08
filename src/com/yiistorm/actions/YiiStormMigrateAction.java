package com.yiistorm.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

public class YiiStormMigrateAction extends YiiStormActionAbstract {
    public Project project;


    @Override
    public void executeAction() {
        // String text=this.runCommand();
        //JOptionPane.showMessageDialog(null,text);
        // MigrationsToolWindow mf = new MigrationsToolWindow(this);
        // mf.setText(text);
        //mf.setVisible(true);
    }


    @Override
    public Boolean isApplicable(AnActionEvent e) {
        return true;
    }


}
