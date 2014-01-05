package com.yiistorm.references.ReferenceProviders;

import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.yiistorm.helpers.CommonHelper;
import com.yiistorm.references.FileReference;
import com.yiistorm.references.YiiPsiReferenceProvider;
import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: mazx
 * Date: 05.02.13
 * Time: 18:43
 * To change this template use File | Settings | File Templates.
 */
public class ControlleActionsClassReferenceProvider {
    public static PsiReference[] getReference(String path, @NotNull PsiElement element) {
        try {

            String protectedPath = CommonHelper.searchCurrentProtected(path).replace(YiiPsiReferenceProvider.projectPath, "");
            VirtualFile baseDir = YiiPsiReferenceProvider.project.getBaseDir();
            if (protectedPath != null && baseDir != null) {
                path = path.replace(YiiPsiReferenceProvider.projectPath, "");
                String str = element.getText();
                TextRange textRange = CommonHelper.getTextRange(element, str);
                String uri = str.substring(textRange.getStartOffset(), textRange.getEndOffset());

                VirtualFile protectedVirtual = baseDir.findFileByRelativePath(protectedPath);
                VirtualFile appDir = baseDir.findFileByRelativePath(path);
                if (uri.matches("^application.+")) {
                    uri = uri.replace("application.", "").replace(".", "/");
                }
                if (protectedVirtual != null) {
                    VirtualFile file = protectedVirtual.findFileByRelativePath(uri + ".php");

                    if (file == null) {
                        file = protectedVirtual.findFileByRelativePath("components/" + uri + ".php");
                    }

                    if (file != null) {

                        if (appDir != null) {
                            PsiReference ref = new FileReference(file, uri, element,
                                    textRange, YiiPsiReferenceProvider.project, protectedVirtual, appDir);
                            return new PsiReference[]{ref};
                        }
                    }
                }
            }
            return PsiReference.EMPTY_ARRAY;
        } catch (Exception e) {
            System.err.println("error" + e.getMessage());
        }
        return PsiReference.EMPTY_ARRAY;
    }
}
