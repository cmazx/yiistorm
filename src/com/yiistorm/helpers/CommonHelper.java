package com.yiistorm.helpers;

import com.intellij.ide.util.gotoByName.GotoClassModel2;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: mazx
 * Date: 15.02.13
 * Time: 18:55
 * To change this template use File | Settings | File Templates.
 */
public class CommonHelper {
    public static TextRange getTextRange(PsiElement element, String str) {
        Class elementClass = element.getClass();
        Method method = null;
        try {
            method = elementClass.getMethod("getValueRange");
            Object obj = null;
            obj = method.invoke(element);
            TextRange textRange = (TextRange) obj;
            String uri = str.substring(textRange.getStartOffset(), textRange.getEndOffset());
            return textRange;
        } catch (Exception e) {
            return null;
        }
    }

    public static String prepareClassName(String str) {
        return rmQuotes(str).replaceAll("^\\\\", "");
    }

    public static String rmQuotes(String str) {
        return str.replace("'", "");
    }

    public static String getCleanClassName(String str) {
        return rmQuotes(str).replaceAll("^.+\\\\", "").replace("\\", "");
    }

    public static String getRegexMatch(String str, String regexp, int groupNumber) {
        try {
            Pattern pattern = Pattern.compile(regexp);
            Matcher m = pattern.matcher(str);
            m.matches();
            m.groupCount();
            return m.group(groupNumber).toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static String searchCurrentProtected(String path) {
        String protectedPath = getRegexMatch(path, "(.+?)/controllers.+", 1);
        if (protectedPath != null) {
            return protectedPath;
        }

        protectedPath = getRegexMatch(path, "(.+?)/views.+", 1);
        if (protectedPath != null) {
            return protectedPath;
        }

        protectedPath = getRegexMatch(path, "(.+?)/components.+", 1);
        if (protectedPath != null) {
            return protectedPath;
        }

        protectedPath = getRegexMatch(path, "(.+?)/models.+", 1);
        if (protectedPath != null) {
            return protectedPath;
        }

        protectedPath = getRegexMatch(path, "(.+?)/modules.+", 1);
        if (protectedPath != null) {
            return protectedPath;
        }

        return null;
    }

    public static ArrayList<String> searchClasses(String regex, Project project) {
        List<PsiElement> psiElements = new ArrayList<PsiElement>();
        GotoClassModel2 model = new GotoClassModel2(project);
        Pattern p = Pattern.compile(regex);
        ArrayList classesNamesFounded = new ArrayList<String>();
        for (String clazz : model.getNames(true)) {
            if (p.matcher(clazz).find()) {
                classesNamesFounded.add(clazz);
            }
        }
        return classesNamesFounded;
    }


}
