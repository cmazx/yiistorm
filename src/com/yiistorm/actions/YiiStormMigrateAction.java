package com.yiistorm.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

/**
 * Created with IntelliJ IDEA.
 * User: mazx
 * Date: 22.02.13
 * Time: 20:04
 * To change this template use File | Settings | File Templates.
 */
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
