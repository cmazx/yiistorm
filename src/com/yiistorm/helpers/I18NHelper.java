package com.yiistorm.helpers;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.yiistorm.DefaultSettings;
import com.yiistorm.YiiStormProjectComponent;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Created by mazx on 07.01.14.
 */
public class I18NHelper {
    public static String[] findMessageSource(String category, String protectedPath, Project project) {
        String[] parts = category.split("\\.");
        String catName = "";
        if (parts.length > 1) {
            catName = parts[parts.length - 1];
            parts = (String[]) ArrayUtils.remove(parts, parts.length - 1);
        }
        if (parts.length > 0) {
            if (parts[0].equals("app")) {
                parts = (String[]) ArrayUtils.remove(parts, 0);
            }

            String subpath = StringUtils.join(parts, "/");
            String subpathAlias = StringUtils.join(parts, ".");
            String lang = I18NHelper.getLang(project);
            String subpathModules = protectedPath + "/modules/" + subpath + "/messages/" + lang;
            VirtualFile subDir = project.getBaseDir().findFileByRelativePath(subpathModules);
            if (subDir != null) {
                return new String[]{subpathModules, subpathAlias, catName};
            }
            subDir = project.getBaseDir().findFileByRelativePath(
                    protectedPath + "/components/" + subpath + "/messages/" + lang);
            if (subDir != null) {
                return new String[]{protectedPath + "/components/" + subpath + "/messages/" + lang, subpathAlias, catName};
            }
        }
        return null;
    }

    public static String getLang(Project project) {
        String langName = YiiStormProjectComponent.getInstance(project).getProp("langName");
        return langName != null ? langName : DefaultSettings.langName;
    }
}
