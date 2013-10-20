package com.yiistorm.ReferenceProviders;

import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.yiistorm.ViewsReference;
import com.yiistorm.YiiPsiReferenceProvider;
import com.yiistorm.helpers.YiiRefsHelper;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * User: mazx
 * Date: 05.02.13
 * Time: 18:43
 * To change this template use File | Settings | File Templates.
 */
public class ControllerRenderViewReferenceProvider {
    public static PsiReference[] getReference(String path, @NotNull PsiElement element) {
        try {
            String themeName = YiiPsiReferenceProvider.properties.getValue("themeName");
            Class elementClass = element.getClass();
            String protectedPath = YiiRefsHelper.getCurrentProtected(path);
            path = path.replace(YiiPsiReferenceProvider.projectPath, "");

            String viewPathTheme = YiiRefsHelper.getRenderViewPath(path, themeName);
            String viewPath = YiiRefsHelper.getRenderViewPath(path, null);

            protectedPath = protectedPath.replace(YiiPsiReferenceProvider.projectPath, "").replaceAll("/controllers/[a-zA-Z0-9_]+?.(php|tpl)+", "");

            Method method = elementClass.getMethod("getValueRange");
            Object obj = method.invoke(element);
            TextRange textRange = (TextRange) obj;
            Class _PhpPsiElement = elementClass.getSuperclass().getSuperclass().getSuperclass();
            Method phpPsiElementGetText = _PhpPsiElement.getMethod("getText");
            Object obj2 = phpPsiElementGetText.invoke(element);
            String str = obj2.toString();
            String uri = str.substring(textRange.getStartOffset(), textRange.getEndOffset());
            int start = textRange.getStartOffset();
            int len = textRange.getLength();
            String controllerName = YiiRefsHelper.getControllerClassName(path);


            if (controllerName != null) {
                VirtualFile baseDir = YiiPsiReferenceProvider.project.getBaseDir();
                if (baseDir != null) {
                    String inThemeFullPath = viewPathTheme + controllerName + "/" + uri + (uri.endsWith(".tpl") ? "" : ".php");
                    if (baseDir.findFileByRelativePath(inThemeFullPath) != null) {
                        viewPath = viewPathTheme;
                    }
                    VirtualFile appDir = baseDir.findFileByRelativePath(viewPath);
                    VirtualFile protectedPathDir = (!protectedPath.equals("")) ?
                            baseDir.findFileByRelativePath(protectedPath) : null;
                    if (appDir != null) {
                        PsiReference ref = new ViewsReference(controllerName, uri, element,
                                new TextRange(start, start + len), YiiPsiReferenceProvider.project, protectedPathDir, appDir);
                        return new PsiReference[]{ref};
                    }
                }
                return PsiReference.EMPTY_ARRAY;
            }
        } catch (Exception e) {
            System.err.println("error" + e.getMessage());
        }
        return PsiReference.EMPTY_ARRAY;
    }
}
