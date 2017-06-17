package com.yiistorm.references.ReferenceProviders;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import com.yiistorm.helpers.YiiRefsHelper;
import com.yiistorm.references.ViewsReference;
import com.yiistorm.references.YiiPsiReferenceProvider;
import org.jetbrains.annotations.NotNull;

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
    public VirtualFile baseDir;

    @NotNull
    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull final ProcessingContext context) {
        project = element.getProject();
        String elname = element.getClass().getName();
        properties = PropertiesComponent.getInstance(project);
        baseDir = project.getBaseDir();

        if (baseDir == null) {
            return PsiReference.EMPTY_ARRAY;
        }

        projectPath = baseDir.getCanonicalPath();
        YiiPsiReferenceProvider.projectPath = projectPath;
        if (elname.endsWith("StringLiteralExpressionImpl")) {

            try {
                PsiFile file = element.getContainingFile();
                VirtualFile vfile = file.getVirtualFile();
                if (vfile != null) {
                    String path = vfile.getPath();
                    String basePath = project.getBasePath();
                    if (basePath != null) {
                        String themeName = properties.getValue("themeName");
                        String uri = element.getText();
                        int start = 0;
                        int len = uri.length();
                        uri = uri.substring(1, len - 1);
                        String protectedPath = YiiRefsHelper.getCurrentProtected(path);
                        path = path.replace(projectPath, "");

                        String viewPathTheme = YiiRefsHelper.getRenderViewPath(path, themeName);
                        String viewPath = YiiRefsHelper.getRenderViewPath(path, null);

                        if (protectedPath == null) {
                            return PsiReference.EMPTY_ARRAY;
                        }
                        protectedPath = protectedPath.replace(projectPath, "")
                                .replaceAll("/controllers/[a-zA-Z0-9_]+?.(php|tpl)+", "");

                        String controllerName = YiiRefsHelper.getControllerClassName(path);
                        if (controllerName != null) {
                            if (baseDir != null) {
                                if (uri.startsWith("//")) {
                                    controllerName = "";
                                    uri = uri.replace("//", "");

                                    if (viewPathTheme == null) {
                                        return PsiReference.EMPTY_ARRAY;
                                    }

                                    if (viewPathTheme.contains("/modules/")) {
                                        viewPathTheme = "/protected/views";
                                    }
                                }
                                viewPath = getThemedPath(viewPathTheme, controllerName, uri, viewPath);
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
                StackTraceElement[] t = e.getStackTrace();
                System.err.println(e.getMessage());
            }
        }

        return PsiReference.EMPTY_ARRAY;
    }

    private String getThemedPath(String viewPathTheme, String controllerName, String uri, String viewPath) {


        String inThemeFullPath = viewPathTheme + controllerName + "/" + uri
                + (uri.endsWith(".tpl") ? "" : ".php");
        if (baseDir.findFileByRelativePath(inThemeFullPath) != null) {
            return viewPathTheme;
        }
        return viewPath;
    }


}
