package com.yiistorm.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * @author Enrique Piatti
 */
public interface IAction {

    Boolean isApplicable(AnActionEvent e);

}