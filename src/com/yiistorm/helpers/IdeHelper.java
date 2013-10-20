package com.yiistorm.helpers;

import com.intellij.codeInsight.TargetElementUtilBase;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import com.intellij.psi.formatter.xml.XmlCodeStyleSettings;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Helper for working easily with the IDE
 *
 * @author Enrique Piatti
 */
public class IdeHelper {

    public static final String INTELLIJ_IDEA_RULEZZZ = "IntellijIdeaRulezzz ";

    public static void showDialog(final Project project, final String message, final String title, final Icon icon) {

        UIUtil.invokeAndWaitIfNeeded(new Runnable() {
            @Override
            public void run() {
                Messages.showMessageDialog(project, message, title, icon);
            }
        });
    }

    public static void showDialog(Project project, String message, String title) {
        showDialog(project, message, title);
    }

    // TODO: this log doesn't work
    public static void log(String message) {
        Logger.getInstance("").info(message);
    }

    public static void logError(String message) {
        Logger.getInstance("").error(message);
    }

    // TODO: this log doesn't work
    public static void logWarning(String message) {
        Logger.getInstance("").warn(message);
    }

    public static void navigateToPsiElement(PsiElement psiElement) {
        Project project = psiElement.getProject();
        PsiElement navElement = psiElement.getNavigationElement();
        navElement = TargetElementUtilBase.getInstance().getGotoDeclarationTarget(psiElement, navElement);
        if (navElement instanceof Navigatable) {
            if (((Navigatable) navElement).canNavigate()) {
                ((Navigatable) navElement).navigate(true);
            }
        } else if (navElement != null) {
            int navOffset = navElement.getTextOffset();
            VirtualFile virtualFile = PsiUtilCore.getVirtualFile(navElement);
            if (virtualFile != null) {
                new OpenFileDescriptor(project, virtualFile, navOffset).navigate(true);
            }
        }
    }

    public static boolean prompt(String message, String title) {
        return Messages.showOkCancelDialog(message, title, Messages.getQuestionIcon()) == 0;
    }


    public static CodeStyleSettings getSettings(Project project) {
        if (project != null) {
            CodeStyleSettingsManager manager = CodeStyleSettingsManager.getInstance(project);
            if (manager != null) {
                return manager.getCurrentSettings();
            }
        }
        return null;
    }

    public static XmlCodeStyleSettings getXmlSettings(Project project) {
        CodeStyleSettings settings = getSettings(project);
        if (settings != null) {
            return settings.getCustomSettings(XmlCodeStyleSettings.class);
        }
        return null;
    }


    public static void showNotification(String message, NotificationType type, @Nullable Project project) {
        final MessageBus messageBus = project == null ? ApplicationManager.getApplication().getMessageBus() : project.getMessageBus();
        final Notification notification = new Notification("Notification", "Notification", message, type, null);

        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                messageBus.syncPublisher(Notifications.TOPIC).notify(notification);
            }
        });
    }


}
