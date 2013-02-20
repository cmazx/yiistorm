package com.yiistorm.ReferenceProviders;

import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.magicento.helpers.PsiPhpHelper;
import com.yiistorm.FileReference;
import com.yiistorm.YiiPsiReferenceProvider;
import com.yiistorm.helpers.CommonHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mazx
 * Date: 05.02.13
 * Time: 18:43
 * To change this template use File | Settings | File Templates.
 */
public class CActionRenderViewReferenceProvider {

    public static Hashtable<String, VirtualFile> cacheFiles = new Hashtable<String, VirtualFile>();

    public static PsiReference[] getReference(String path, @NotNull PsiElement element) {
        try {
            if (PsiPhpHelper.isString(element)) {
                //long startTime = System.nanoTime();
                String elementName = element.getText().replace("'", "").replace("\"", "");
                VirtualFile baseDir = YiiPsiReferenceProvider.project.getBaseDir();
                String inProtectedPath = path.replace(YiiPsiReferenceProvider.projectPath, "");
                String protectedPath = CommonHelper.searchCurrentProtected(inProtectedPath);
                String actionName = PsiPhpHelper.getClassName(element);
                String cacheKey = actionName + "::render_" + elementName;
                VirtualFile file = null;
                if (true) {     //!cacheFiles.containsKey(cacheKey)
                    //System.err.println("take "+cacheKey+" from filesystem");
                    PsiElement controllerPsi = getControllersUsingAction(actionName);
                    String controllerName = PsiPhpHelper.getClassIdentifierName(controllerPsi);
                    VirtualFile controllerFile = controllerPsi.getNavigationElement().getContainingFile().getVirtualFile();
                    String controllerPath = controllerFile.getPath();
                    if (elementName.matches("^/.+")) {
                        String viewPath = controllerPath.replaceAll("/controllers/[a-zA-Z0-9_]+?.php",
                                "/views/" + elementName.replace("//", "") + ".php");
                        viewPath = viewPath.replace(YiiPsiReferenceProvider.projectPath, "");
                        file = baseDir.findFileByRelativePath(viewPath);
                    } else if (elementName.matches("^//.+")) {
                        String viewPath = controllerPath.replaceAll("/controllers/[a-zA-Z0-9_]+?.php",
                                "/views/" + elementName.replace("//", "") + ".php");
                        viewPath = viewPath.replace(YiiPsiReferenceProvider.projectPath, "");
                        file = baseDir.findFileByRelativePath(viewPath);
                    } else {
                        String viewPath = controllerPath.replaceAll("/[a-zA-Z0-9_]+?.php",
                                "/" + controllerName.replace("Controller", "") + "/" + elementName + ".php");
                        viewPath = viewPath.replace("/controllers/", "/views/");
                        viewPath = viewPath.replace(YiiPsiReferenceProvider.projectPath, "");
                        file = baseDir.findFileByRelativePath(viewPath);
                    }

                    if (file == null) {
                        return PsiReference.EMPTY_ARRAY;
                    } else {
                        //System.err.println("put "+cacheKey+" to cache");
                        cacheFiles.put(cacheKey, file);
                    }
                } else {
                    //System.err.println("take "+cacheKey+" from cache");
                    file = cacheFiles.get(cacheKey);
                }

                if (file != null) {
                    VirtualFile protectedPathDir = (protectedPath != "") ? baseDir.findFileByRelativePath(protectedPath) : null;
                    String str = element.getText();
                    TextRange textRange = CommonHelper.getTextRange(element, str);
                    String uri = str.substring(textRange.getStartOffset(), textRange.getEndOffset());
                    int start = textRange.getStartOffset();
                    int len = textRange.getLength();

                    //System.out.println("Temp pour int : " + (System.nanoTime() - startTime) / 1000000 + " ms");

                    PsiReference ref = new FileReference(file, uri, element,
                            new TextRange(start, start + len), YiiPsiReferenceProvider.project, protectedPathDir, protectedPathDir);
                    return new PsiReference[]{ref};
                }
            }

            return PsiReference.EMPTY_ARRAY;

        } catch (Exception e) {
            //System.err.println("error" + e.getMessage());
        }
        return PsiReference.EMPTY_ARRAY;
    }

    public static PsiElement getControllersUsingAction(String actionClass) {
       /* List<PsiElement> elements = PsiPhpHelper.getPsiElementsFromClassName("*Controller", YiiPsiReferenceProvider.project);
        if (elements.size() > 0) {
            for (PsiElement element : elements) {
                String elementName = PsiPhpHelper.getClassIdentifierName(element);
                PsiElement navElement = element.getNavigationElement();
            }
        }   */

        //GotoClassModel2 model = new GotoClassModel2(YiiPsiReferenceProvider.project);
        ArrayList<PsiElement> controllersUsingAction = new ArrayList<PsiElement>();
        ArrayList<String> controllers = CommonHelper.searchClasses("BadCharController", YiiPsiReferenceProvider.project);
        for (String controllerClass : controllers) {

            List<PsiElement> elements = PsiPhpHelper.getPsiElementsFromClassName(controllerClass, YiiPsiReferenceProvider.project);
            if (elements.size() > 0) {
                PsiElement controllerClassPsi = elements.get(0);
                String controllerName = PsiPhpHelper.getClassName(controllerClassPsi);
                List<PsiNamedElement> methods = PsiPhpHelper.getAllMethodsFromClass(controllerClassPsi, false);
                CONTROLLER_FOR:
                for (PsiNamedElement controllerMethod : methods) {
                    PsiElement gst = PsiPhpHelper.findFirstChildOfType(controllerMethod, PsiPhpHelper.GROUP_STATEMENT);
                    PsiElement mident = PsiPhpHelper.findPrevSiblingOfType(gst, PsiPhpHelper.IDENTIFIER);
                    //public function actions()
                    if (mident.getText().matches("actions")) {
                        PsiElement actionsMethodReturn = PsiPhpHelper.findFirstChildOfType(gst, "Return");
                        PsiElement arrayCreation = PsiPhpHelper.findFirstChildOfType(actionsMethodReturn, "Array creation expression");
                        List<PsiElement> actions = PsiPhpHelper.getFullListOfChildren(arrayCreation);
                        for (PsiElement controllerAction : actions) {

                            if (PsiPhpHelper.isElementType(controllerAction, "Hash array element")) {
                                //one action
                                PsiElement ActionsListArrayValue = PsiPhpHelper.findFirstChildOfType(controllerAction, "Array value");
                                PsiElement ActionArrayCreation = PsiPhpHelper.findFirstChildOfType(ActionsListArrayValue, "Array creation expression");
                                List<PsiElement> ActionArrayChilds = PsiPhpHelper.getFullListOfChildren(ActionArrayCreation);

                                for (PsiElement ActionArrayChild : ActionArrayChilds) {
                                    //fields in one action array
                                    if (PsiPhpHelper.isElementType(ActionArrayChild, "Hash array element")) {
                                        String ActionArrayChildName = PsiPhpHelper.findFirstChildOfType(ActionArrayChild, "Array key").getText().replace("'", "");
                                        if (ActionArrayChildName.matches("^class$")) {
                                            String ControllerActionClassName = PsiPhpHelper.findFirstChildOfType(ActionArrayChild, "Array value").getText().replace("'", "");
                                            ControllerActionClassName = ControllerActionClassName;
                                            if (ControllerActionClassName.equals(actionClass)) {
                                                controllersUsingAction.add(controllerClassPsi);
                                                break CONTROLLER_FOR;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return controllersUsingAction.get(0);
    }


}
