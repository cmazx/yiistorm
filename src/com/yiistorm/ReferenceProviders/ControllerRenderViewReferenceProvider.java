package com.yiistorm.ReferenceProviders;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import com.yiistorm.ViewsReference;
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
public class ControllerRenderViewReferenceProvider extends PsiReferenceProvider {
    public static final PsiReferenceProvider[] EMPTY_ARRAY = new PsiReferenceProvider[0];
    public static String projectPath;
    public static Project project;
    public static PropertiesComponent properties;

    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull final ProcessingContext context) {
        project = element.getProject();
        String elname = element.getClass().getName();
        properties = PropertiesComponent.getInstance(project);
        VirtualFile baseDir = project.getBaseDir();
        projectPath = baseDir.getCanonicalPath();
        if (elname.endsWith("StringLiteralExpressionImpl")) {

            try {
                PsiFile file = element.getContainingFile();
                VirtualFile vfile = file.getVirtualFile();
                if (vfile != null) {
                    String path = vfile.getPath();
                    String basePath = project.getBasePath();
                    if (basePath != null) {

                        String themeName = properties.getValue("themeName");
                        Class elementClass = element.getClass();
                        String protectedPath = YiiRefsHelper.getCurrentProtected(path);
                        path = path.replace(projectPath, "");

                        String viewPathTheme = YiiRefsHelper.getRenderViewPath(path, themeName);
                        String viewPath = YiiRefsHelper.getRenderViewPath(path, null);

                        protectedPath = protectedPath.replace(projectPath, "")
                                .replaceAll("/controllers/[a-zA-Z0-9_]+?.(php|tpl)+", "");

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
                            if (baseDir != null) {
                                String inThemeFullPath = viewPathTheme + controllerName + "/" + uri
                                        + (uri.endsWith(".tpl") ? "" : ".php");
                                if (baseDir.findFileByRelativePath(inThemeFullPath) != null) {
                                    viewPath = viewPathTheme;
                                }
                                VirtualFile appDir = baseDir.findFileByRelativePath(viewPath);
                                VirtualFile protectedPathDir = (!protectedPath.equals("")) ?
                                        baseDir.findFileByRelativePath(protectedPath) : null;
                                if (appDir != null) {
                                    PsiReference ref = new ViewsReference(controllerName, uri, element,
                                            new TextRange(start, start + len), project, protectedPathDir, appDir);
                                    return new PsiReference[]{ref};
                                }
                            }
                            return PsiReference.EMPTY_ARRAY;
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("error" + e.getMessage());
            }
        }
        return PsiReference.EMPTY_ARRAY;
    }
}
