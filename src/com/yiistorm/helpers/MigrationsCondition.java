package com.yiistorm.helpers;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.yiistorm.YiiStormProjectComponent;
import com.yiistorm.elements.Yiic;

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
        if (component.getBooleanProp("useYiiMigrations") && component.getProp("yiicFile") != null
                && Yiic.yiicIsRunnable(component.getProp("yiicFile"))) {
            return true;
        }
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public static boolean makeCondition(Project p) {
        MigrationsCondition condition = new MigrationsCondition();
        return condition.value(p);
    }
}
