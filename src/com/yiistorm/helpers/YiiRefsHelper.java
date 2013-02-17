package com.yiistorm.helpers;

import com.intellij.psi.PsiElement;
import com.magicento.helpers.PsiPhpHelper;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created with IntelliJ IDEA.
 * User: mazx
 * Date: 05.02.13
 * Time: 18:44
 * To change this template use File | Settings | File Templates.
 */
public class YiiRefsHelper {
    public final static int YII_TYPE_UNKNOWN = -1;
    public final static int YII_TYPE_CONTROLLER_TO_VIEW_RENDER = 1;
    public final static int YII_TYPE_AR_RELATION = 2;
    public final static int YII_TYPE_VIEW_TO_VIEW_RENDER = 3;
    public final static int YII_TYPE_WIDGET_CALL = 4;
    public final static int YII_TYPE_CACTION_TO_VIEW_RENDER = 5;


    public static String getCurrentProtected(String path) {
        if (path.matches("(.+?/)modules.+")) {
            try {
                Pattern regex = Pattern.compile("(.+?/)modules.+", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
                Matcher regexMatcher = regex.matcher(path);
                regexMatcher.matches();
                regexMatcher.groupCount();
                return regexMatcher.group(1).toString();
            } catch (PatternSyntaxException ex) {
                // Syntax error in the regular expression
            }
        }
        return path;
    }

    /**
     * @param path
     * @return
     */
    public static int getYiiObjectType(String path, PsiElement el) {

        if (isWidgetCall(el)) {
            return YII_TYPE_WIDGET_CALL;
        }

        if (isCActionRenderView(el)) {
            return YII_TYPE_CACTION_TO_VIEW_RENDER;
        }

        if (isExtendCActiveRecord(el)) {
            return YII_TYPE_AR_RELATION;
        }

        if (isYiiViewToViewRenderCall(el)) {
            return YII_TYPE_VIEW_TO_VIEW_RENDER;
        }

        if (isYiiControllerToViewRenderCall(path, el)) {
            return YII_TYPE_CONTROLLER_TO_VIEW_RENDER;
        }


        return YII_TYPE_UNKNOWN;
    }

    public static boolean isExtendCActiveRecord(PsiElement el) {
        try {
            PsiElement PsiClass = PsiPhpHelper.getClassElement(el);
            boolean extendsCActiveRecord = PsiPhpHelper.isExtendsSuperclass(PsiClass, "CActiveRecord");
            if (extendsCActiveRecord) {
                return true;
            }
        } catch (Exception ex) {
        }
        return false;
    }

    public static boolean isCActionRenderView(PsiElement el) {
        try {
            PsiElement classEl = PsiPhpHelper.getClassElement(el);
            if (classEl != null) {
                if (ExtendedPsiPhpHelper.parentMethodNameMatch(el, "^(renderPartial|render)$")) {
                    boolean extendsCActiveRecord = PsiPhpHelper.isExtendsSuperclass(classEl, "CAction");
                    if (extendsCActiveRecord) {
                        return true;
                    }
                }
            }
        } catch (Exception ex) {
        }
        return false;
    }

    public static boolean isWidgetCall(PsiElement el) {
        PsiElement method = PsiPhpHelper.findFirstParentOfType(el, PsiPhpHelper.METHOD_REFERENCE);
        String methodName = PsiPhpHelper.getMethodName(method);
        if (methodName.matches("^widget$")) {
            return true;
        }
        return false;
    }

    public static boolean isYiiControllerToViewRenderCall(String path, PsiElement el) {
        if (path.contains("Controller.php")) {
            PsiElement classEl = PsiPhpHelper.getClassElement(el);
            if (classEl.toString().contains("Controller")) {
                PsiElement parameter_list = PsiPhpHelper.findFirstParentOfType(el, PsiPhpHelper.METHOD_REFERENCE);
                if (parameter_list != null) {
                    String mrefchild = PsiPhpHelper.getMethodName(parameter_list);
                    if (mrefchild.matches("^(renderPartial|render)$")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isYiiViewToViewRenderCall(PsiElement el) {
        if (PsiPhpHelper.getClassElement(el) == null) {
            PsiElement parameter_list = PsiPhpHelper.findFirstParentOfType(el, PsiPhpHelper.METHOD_REFERENCE);
            if (parameter_list != null) {
                String mrefchild = PsiPhpHelper.getMethodName(parameter_list);
                if (mrefchild.matches("^(renderPartial|render)$")) {
                    return true;
                }
            }
        }

        return false;
    }

    public static String getViewParentPath(String path) {
        try {
            Pattern regex = Pattern.compile("(.+views)/.+", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher regexMatcher = regex.matcher(path);
            regexMatcher.matches();
            regexMatcher.groupCount();
            return regexMatcher.group(1);
        } catch (PatternSyntaxException ex) {
            System.err.println(ex.getMessage());
            // Syntax error in the regular expression
        }
        return null;
    }

    public static String getRenderViewPath(String path) {
        try {
            Pattern regex = Pattern.compile("(.+?)controllers/(.+?)Controller\\.php", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher regexMatcher = regex.matcher(path);
            regexMatcher.matches();
            regexMatcher.groupCount();
            return regexMatcher.group(1) + "views/";
        } catch (PatternSyntaxException ex) {
            System.err.println(ex.getMessage());
            // Syntax error in the regular expression
        }
        return "";
    }

    /**
     * Controller name
     *
     * @param element PsiElement obj
     * @return String Controller name
     */
    public static String getControllerClassName(PsiElement element) {
        PsiElement prevEl = element.getParent();
        if (prevEl != null) {
            prevEl = prevEl.getParent();
            if (prevEl != null) {
                String elClassName = prevEl.getClass().getName();  //Method under cursor
                if (elClassName.endsWith("MethodReferenceImpl")) {
                    try {
                        Method phpPsiElementGetName = prevEl.getClass().getMethod("getName");
                        Method phpPsiElementGetClassReference = prevEl.getClass().getMethod("getClassReference");
                        String name = (String) phpPsiElementGetName.invoke(prevEl);
                        //Method equal $controller->render() or $controller->renderPartial();
                        if (name.toLowerCase().equals("render") || name.toLowerCase().equals("renderpartial")) {
                            Method getClassReference = prevEl.getClass().getMethod("getClassReference");
                            Object classRef = getClassReference.invoke(prevEl);

                            if (classRef.toString().matches("Variable")) {
                                //Get class from object
                                PsiElement classRef2 = (PsiElement) phpPsiElementGetClassReference.invoke(prevEl);
                                Method resolveMethod = classRef2.getClass().getMethod("resolve");
                                Object resolveMethodObj = resolveMethod.invoke(classRef2);
                                String className = resolveMethodObj.toString().toLowerCase();
                                ///Check class name
                                if (className.startsWith("phpclass") && className.matches(".+?controller")) {
                                    Pattern pattern = Pattern.compile(".+? (.+?)controller");
                                    Matcher m = pattern.matcher(className);
                                    m.matches();
                                    m.groupCount();
                                    return m.group(1).toString();
                                }
                            } else {
                            /*
                                Static
                             */
                            }
                        }
                    } catch (Exception ex) {
                        System.err.println("prevEl error:" + ex.getMessage());
                    }
                } else {
                    //System.err.println("prevEl not MethodReferenceImpl!!!" );
                }
            } else {
                // System.err.println("prevEl IS NULL!!!!" );
            }
        }
        return "";
    }

}
