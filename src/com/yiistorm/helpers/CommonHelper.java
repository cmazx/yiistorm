package com.yiistorm.helpers;

import com.intellij.codeInsight.TargetElementUtilBase;
import com.intellij.ide.util.gotoByName.GotoClassModel2;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiUtilCore;
import com.magicento.helpers.PsiPhpHelper;

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

    public static String getViewsPathFromControllerFile(VirtualFile originalFile) {
        return originalFile.getParent().getCanonicalPath() + "/../views/";
    }

    public static String getControllerName(String fullname) {
        String controllerName = fullname.replace("Controller.php", "");
        if (controllerName.length() < 1) {
            return null;
        }
        return controllerName.substring(0, 1).toLowerCase() + controllerName.substring(1);
    }

    public static String cleanCompleterSearchString(String s) {
        String searchString = s.replace("IntellijIdeaRulezzz", "");
        searchString = searchString.replace("IntellijIdeaRulezzz ", "");
        return searchString.replaceAll("['\"]+", "").trim();
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

    public static String searchModulePath(String path) {
        String protectedPath = getRegexMatch(path, "(.+)/controllers.*", 1);
        if (protectedPath != null) {
            return protectedPath;
        }

        protectedPath = getRegexMatch(path, "(.+)/views.*", 1);
        if (protectedPath != null) {
            return protectedPath;
        }

        protectedPath = getRegexMatch(path, "(.+)/components.*", 1);
        if (protectedPath != null) {
            return protectedPath;
        }

        protectedPath = getRegexMatch(path, "(.+)/models.*", 1);
        if (protectedPath != null) {
            return protectedPath;
        }

        protectedPath = getRegexMatch(path, "(.+)/modules.*", 1);
        if (protectedPath != null) {
            return protectedPath;
        }

        return null;
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
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        ArrayList classesNamesFounded = new ArrayList<String>();
        String[] models = model.getNames(true);
        for (String clazz : models) {

            if (p.matcher(clazz).find()) {
                classesNamesFounded.add(clazz);
            }
        }
        return classesNamesFounded;
    }

    public static PsiElement getFileByClass(String actionClass, Project project) {
        if (actionClass == null) {
            return null;
        }
        List<PsiElement> elements = PsiPhpHelper.getPsiElementsFromClassName(actionClass, project);
        if (elements.size() > 0) {
            PsiElement element = elements.get(0);
            PsiElement navElement = element.getNavigationElement();
            navElement = TargetElementUtilBase.getInstance().getGotoDeclarationTarget(element, navElement);
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
        return null;
    }


}
