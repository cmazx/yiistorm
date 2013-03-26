package com.yiistorm;

/**
 * Created with IntelliJ IDEA.
 * User: mazx
 * Date: 22.02.13
 * Time: 20:17
 * To change this template use File | Settings | File Templates.
 */

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.magicento.helpers.IdeHelper;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;


public class YiiStormProjectComponent implements ProjectComponent {

    private Project _project;
    private boolean _isCacheConfigXmlUpdated = false;
    private boolean _isCacheLayoutXmlUpdated = false;

    public YiiStormProjectComponent(Project project) {
        _project = project;
    }

    public static YiiStormProjectComponent getInstance(Project project) {
        return project.getComponent(YiiStormProjectComponent.class);
    }

    public void initComponent() {

    }


    public void disposeComponent() {
        // TODO: insert component disposal logic here
    }

    @NotNull
    public String getComponentName() {
        return "YiiStormProjectComponent";
    }

    public void projectOpened() {
        // called when project is opened
    }

    public void projectClosed() {
        // called when project is being closed
    }

    public boolean isDisabled() {
        return !isEnabled();
    }

    public boolean isEnabled() {
        return isEnabled(_project);
    }

    public static boolean isEnabled(Project project) {
        return true;//TODO: work with config
    }

    public void showMessage(String message, String title, Icon icon) {
        IdeHelper.showDialog(_project, message, title, icon);
    }

    public void showMessageError(String message) {
        showMessage(message, "Error", Messages.getErrorIcon());
    }

    public void showMessageInfo(String message) {
        showMessage(message, "Info", Messages.getInformationIcon());
    }


}
