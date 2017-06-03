package com.yiistorm.helpers;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.yiistorm.YiiStormProjectComponent;
import com.yiistorm.elements.Yii;

/**
 * Created with IntelliJ IDEA.
 * User: mazx
 * Date: 05.04.13
 * Time: 19:09
 * To change this template use File | Settings | File Templates.
 */
public class MigrationsCondition implements Condition {
    @Override
    public boolean value(Object o) {
        YiiStormProjectComponent component = YiiStormProjectComponent.getInstance((Project) o);

        Notifications.Bus.register("yiicnotfound", NotificationDisplayType.BALLOON);
        if (component.getBooleanProp("useYiiMigrations")) {
            boolean phpOk = CommonHelper.phpVersionCheck();
            if (component.getProp("yiicFile").length() < 1) {
                Notifications.Bus.notify(new Notification("yiistormMigration",
                        "YiiStorm migrations",
                        "Yii not selected ",
                        NotificationType.WARNING));
                return false;
            }
            String yiiConsolePath = component.getProp("yiicFile");
            Boolean yiiReady = yiiConsolePath != null && phpOk;
            if (yiiReady && Yii.getInstance(yiiConsolePath) != null) {
                return true;
            } else {
                Notifications.Bus.notify(new Notification("yiistormMigration",
                        "YiiStorm migrations",
                        phpOk ? "Yii file not configured." : "Can't run php. Check your system configuration. ",
                        NotificationType.WARNING));
            }
        }
        return false;
    }

    public static boolean makeCondition(Project p) {
        MigrationsCondition condition = new MigrationsCondition();
        return condition.value(p);
    }
}
