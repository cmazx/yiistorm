package com.yiistorm.helpers;

import com.intellij.patterns.PlatformPatterns; 
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.yiistorm.YiiPsiReferenceProvider; 
import com.yiistorm.references.YiiPsiReferenceProvider;

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
    public final static int YII_TYPE_WIDGET_VIEW_RENDER = 6;
    public final static int YII_TYPE_CONTROLLER_ACTIONS_CACTION = 7;


    public static String getCurrentProtected(String path) {
        if (path.matches("(.+?/)modules.+")) {
            try {
                Pattern regex = Pattern.compile("(.+?/)modules.+", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
                Matcher regexMatcher = regex.matcher(path);
                regexMatcher.matches();
                regexMatcher.groupCount();
                return regexMatcher.group(1);
            } catch (PatternSyntaxException ex) {
                // Syntax error in the regular expression
            }
        }
        return path;
    }

    /**
     * @param path Path
     * @return int
     */
    public static int getYiiObjectType(String path, PsiElement el) {

        if (isWidgetRenderView(path, el)) {
            return YII_TYPE_WIDGET_VIEW_RENDER;
        }

        if (isExtendCActiveRecord(el)) {
            return YII_TYPE_AR_RELATION;
        }

        if (isYiiControllerToViewRenderCall(path, el)) {
            return YII_TYPE_CONTROLLER_TO_VIEW_RENDER;
        }

        if (isYiiControllerCActionClassName(path, el)) {
            return YII_TYPE_CONTROLLER_ACTIONS_CACTION;
        }

        if (isCActionRenderView(el)) {
            return YII_TYPE_CACTION_TO_VIEW_RENDER;
        }

        return YII_TYPE_UNKNOWN;
    }

    public static boolean isYiiApplication(PsiElement el) {

        //System.err.println(el.getText().toString());
        return PlatformPatterns
                .psiElement(PhpElementTypes.EXPRESSION)
                        // .withParent(PlatformPatterns
                        //         .psiElement(PhpElementTypes.METHOD_REFERENCE) )
                .accepts(el);
    }

    public static boolean isExtendCActiveRecord(PsiElement el) {
        try {
            PsiElement PsiClass = PsiPhpHelper.getClassElement(el);
            boolean extendsCActiveRecord = PsiPhpHelper.isExtendsSuperclass(PsiClass, "CActiveRecord");
            if (extendsCActiveRecord) {
                return true;
            }
        } catch (Exception ex) {
            //
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
            //
        }
        return false;
    }


    public static boolean isWidgetRenderView(String path, PsiElement el) {
        if (!path.contains("Controller.php")) {
            PsiElementPattern.Capture p = PlatformPatterns.psiElement().withElementType(PhpElementTypes.CLASS)
                    .withSuperParent(5, PlatformPatterns.psiElement().withName("CWidget"));

            PsiElementPattern.Capture renderMethod = PlatformPatterns.psiElement().withParent(
                    YiiContibutorHelper.paramListInMethodWithName("render")
            );
            PsiElementPattern.Capture renderPartialMethod = PlatformPatterns.psiElement().withParent(
                    YiiContibutorHelper.paramListInMethodWithName("renderPartial")
            );
            PsiElement[] elc = el.getContainingFile().getChildren();
            if (elc.length > 0 && elc[0] != null && elc[0].getChildren().length > 0) {
                for (PsiElement element : elc[0].getChildren()) {
                    if (p.accepts(element) && (renderMethod.accepts(el) || renderPartialMethod.accepts(el))) {
                        return true;
                    }
                }
            }
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

    public static boolean isYiiControllerCActionClassName(String path, PsiElement el) {
        if (path.contains("Controller.php")) {
            PsiElement classEl = PsiPhpHelper.getClassElement(el);
            if (classEl.toString().contains("Controller")) {
                PsiElement group_state = PsiPhpHelper.findFirstParentOfType(el, PsiPhpHelper.GROUP_STATEMENT);
                PsiElement method_identifier = PsiPhpHelper.findPrevSiblingOfType(group_state, PsiPhpHelper.IDENTIFIER);
                if (method_identifier != null) {
                    String identifierName = method_identifier.getText();
                    if (identifierName.matches("^actions$")) {
                        return true;
                    }
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

    public static String getRenderViewPath(String path, String themeName) {
        try {
            Pattern regex = Pattern.compile("(.+?)controllers/(.+?)Controller\\.php", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher regexMatcher = regex.matcher(path);
            regexMatcher.matches();
            regexMatcher.groupCount();
            if (themeName == null) {
                themeName = "";
            } else {
                themeName = "themes/" + themeName.replaceAll("^/", "").replaceAll("/$", "").trim() + "/";
            }

            return regexMatcher.group(1) + themeName + "views/";
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
    public static String getRendererClassName(PsiElement element) {
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
                                    return m.group(1);
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

    public static String getControllerClassName(String path) {
        path = path.replace(YiiPsiReferenceProvider.projectPath, "");
        try {
            Pattern regex = Pattern.compile(".+controllers/(.+?)Controller.php", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher regexMatcher = regex.matcher(path);
            regexMatcher.matches();
            regexMatcher.groupCount();
            String name = regexMatcher.group(1);
            if (name.contains("/")) {//insubfolder
                name = regexMatcher.group(1);
                try {
                    Pattern regex2 = Pattern.compile("(.+/)*([a-zA-Z0-9_]+)$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
                    Matcher regexMatcher2 = regex2.matcher(name);
                    regexMatcher2.matches();
                    regexMatcher2.groupCount();
                    String subname = regexMatcher2.group(2);
                    name = regexMatcher2.group(1) + subname.substring(0, 1).toLowerCase() + subname.substring(1);
                } catch (PatternSyntaxException ex) {
                    System.err.println(ex.getMessage());
                    // Syntax error in the regular expression
                }
            } else {
                name = name.substring(0, 1).toLowerCase() + name.substring(1);
            }


            return name;
        } catch (PatternSyntaxException ex) {
            System.err.println(ex.getMessage());
            // Syntax error in the regular expression
        }
        return null;
    }

}
