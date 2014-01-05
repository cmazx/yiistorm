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
import com.yiistorm.helpers.CommonHelper;
import com.yiistorm.helpers.YiiRefsHelper;
import com.yiistorm.references.FileReference;
import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: mazx
 * Date: 05.02.13
 * Time: 18:43
 * To change this template use File | Settings | File Templates.
 */
public class ViewRenderViewReferenceProvider extends PsiReferenceProvider {
    public static final PsiReferenceProvider[] EMPTY_ARRAY = new PsiReferenceProvider[0];
    public static Project project;
    public static PropertiesComponent properties;

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull final ProcessingContext context) {
        project = element.getProject();
        String elname = element.getClass().getName();
        properties = PropertiesComponent.getInstance(project);
        if (elname.endsWith("StringLiteralExpressionImpl")) {
            try {
                PsiFile file = element.getContainingFile();
                VirtualFile vfile = file.getVirtualFile();
                if (vfile != null) {
                    String path = vfile.getPath();
                    VirtualFile baseDir = project.getBaseDir();
                    if (baseDir == null) {
                        return PsiReference.EMPTY_ARRAY;
                    }
                    String basePath = baseDir.getCanonicalPath();
                    if (basePath != null) {
                        String viewPath = path.replace(basePath, "")
                                .replaceAll("/[a-zA-Z0-9_]+?.(php|tpl)+", "");
                        String viewAbsolutePath = YiiRefsHelper.getViewParentPath(path
                                .replace(basePath, ""));
                        String protectedPath = YiiRefsHelper.getCurrentProtected(path);
                        protectedPath = protectedPath.replace(basePath, "");

                        String str = element.getText();
                        TextRange textRange = CommonHelper.getTextRange(element, str);
                        String uri = str.substring(textRange.getStartOffset(), textRange.getEndOffset());
                        int start = textRange.getStartOffset();
                        int len = textRange.getLength();

                        if (!uri.endsWith(".tpl") && !uri.startsWith("smarty:")) {
                            uri += ".php";
                        }

                        VirtualFile appDir = baseDir.findFileByRelativePath(viewPath);
                        VirtualFile protectedPathDir = (!protectedPath.equals(""))
                                ? baseDir.findFileByRelativePath(protectedPath) : null;

                        String filepath = viewPath + "/" + uri;
                        if (uri.matches("^//.+")) {
                            filepath = viewAbsolutePath + "/" + uri.replace("//", "");
                        }
                        VirtualFile viewfile = baseDir.findFileByRelativePath(filepath);

                        if (viewfile != null && appDir != null) {
                            PsiReference ref = new FileReference(
                                    viewfile,
                                    uri,
                                    element,
                                    new TextRange(start, start + len),
                                    project,
                                    protectedPathDir,
                                    appDir);
                            return new PsiReference[]{ref};
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
