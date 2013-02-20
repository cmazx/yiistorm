package com.yiistorm.ReferenceProviders;

import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.yiistorm.FileReference;
import com.yiistorm.YiiPsiReferenceProvider;
import com.yiistorm.helpers.CommonHelper;
import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: mazx
 * Date: 05.02.13
 * Time: 18:43
 * To change this template use File | Settings | File Templates.
 */
public class WidgetRenderViewReferenceProvider {

    public static PsiReference[] getReference(String path, @NotNull PsiElement element) {
        try {
            String currentPath = path.replace(YiiPsiReferenceProvider.projectPath, "").replaceAll("/[a-zA-Z0-9_]+?.(php|tpl)+$", "");
            String protectedPath = CommonHelper.searchCurrentProtected(path).replace(YiiPsiReferenceProvider.projectPath, "");
            String viewAbsolutePath = protectedPath + "/views";
            String viewPath = currentPath + "/views";

            String str = element.getText();
            TextRange textRange = CommonHelper.getTextRange(element, str);
            String uri = str.substring(textRange.getStartOffset(), textRange.getEndOffset());
            int start = textRange.getStartOffset();
            int len = textRange.getLength();

            if (!uri.endsWith(".tpl") && !uri.startsWith("smarty:")) {
                uri += ".php";
            }

            VirtualFile baseDir = YiiPsiReferenceProvider.project.getBaseDir();
            VirtualFile appDir = baseDir.findFileByRelativePath(viewPath);
            VirtualFile protectedPathDir = (protectedPath != "") ? baseDir.findFileByRelativePath(protectedPath) : null;

            String filepath = viewPath + "/" + uri;
            if (uri.matches("^//.+")) {
                filepath = viewAbsolutePath + "/" + uri.replace("//", "");
            }
            VirtualFile file = baseDir.findFileByRelativePath(filepath);

            if (file != null && appDir != null) {
                PsiReference ref = new FileReference(file, uri, element,
                        new TextRange(start, start + len), YiiPsiReferenceProvider.project, protectedPathDir, appDir);
                return new PsiReference[]{ref};
            }
            return PsiReference.EMPTY_ARRAY;

        } catch (Exception e) {
            System.err.println("error" + e.getMessage());
        }
        return PsiReference.EMPTY_ARRAY;
    }
}
