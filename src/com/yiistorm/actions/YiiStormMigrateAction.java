package com.yiistorm.actions;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.yiistorm.forms.MigrationsForm;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created with IntelliJ IDEA.
 * User: mazx
 * Date: 22.02.13
 * Time: 20:04
 * To change this template use File | Settings | File Templates.
 */
public class YiiStormMigrateAction extends YiiStormActionAbstract {
    public Project project;
    public String createMigration = " migrate create";
    public String applyMigration = " migrate --interactive=0";
    private static String OS = System.getProperty("os.name").toLowerCase();

    public String runCommand(String migrateCommand) {
        try {

            Process p = null;
            String prependPath = "";
            if (isWindows()) {
                prependPath = "cmd /c ";
            }

            PropertiesComponent properties = PropertiesComponent.getInstance(getProject());

            String path = properties.getValue("yiicFile");

            if (path == null) {
                return "Please select path to yiic in YiiStorm config.";
            }

            prependPath += path;

            p = Runtime.getRuntime().exec(prependPath + migrateCommand);   //+ "D:\\webservers\\home\\www.rest.lcl\\www\\protected\\"

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
                    lineAll += line;
                }
            }
            return lineAll;
        } catch (Exception e1) {
            return "Error! " + e1.getMessage();
        }

    }

    @Override
    public void executeAction() {
        // String text=this.runCommand();
        //JOptionPane.showMessageDialog(null,text);
        MigrationsForm mf = new MigrationsForm(this);
        // mf.setText(text);
        mf.setVisible(true);
    }


    @Override
    public Boolean isApplicable(AnActionEvent e) {
        return true;
    }

    public static boolean isWindows() {

        return (OS.indexOf("win") >= 0);

    }
}
