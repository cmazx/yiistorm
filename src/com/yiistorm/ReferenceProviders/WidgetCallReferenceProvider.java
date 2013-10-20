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
public class WidgetCallReferenceProvider {


    public static PsiReference[] getReference(String path, @NotNull PsiElement element) {
        try {
            VirtualFile baseDir = YiiPsiReferenceProvider.project.getBaseDir();
            if (baseDir != null) {
                String inProtectedPath = path.replace(YiiPsiReferenceProvider.projectPath, "");
                String protectedPath = CommonHelper.searchCurrentProtected(inProtectedPath);
                String widgetPath = element.getText().replace("'", "");
                String widgetFilePath = "";
                if (widgetPath.matches("^components.+")) {
                    widgetFilePath = protectedPath + "/" + widgetPath.replace(".", "/") + ".php";
                } else if (widgetPath.matches("^ext\\..+")) {
                    widgetFilePath = (protectedPath + "/" + widgetPath.replace(".", "/")).replace("/ext/", "/extensions/") + ".php";
                } else if (widgetPath.matches("^app\\..+")) {
                    widgetFilePath = widgetPath.replace(".", "/").replace("app", protectedPath) + ".php";
                } else if (widgetPath.matches("^application.+")) {
                    widgetFilePath = widgetPath.replace(".", "/").replace("application", protectedPath) + ".php";
                } else {
                    if (!widgetPath.contains(".")) {
                        String currentFolder = inProtectedPath.replaceAll("[a-z0-9A-Z_]+?.php", "");
                        VirtualFile existsNear = baseDir.findFileByRelativePath(currentFolder + widgetPath + ".php");
                        if (existsNear == null) {
                            VirtualFile existsInParentDir = baseDir.findFileByRelativePath(currentFolder + ".." + "/"
                                    + widgetPath + ".php");
                            if (existsInParentDir != null) {
                                widgetFilePath = currentFolder + ".." + "/" + widgetPath + ".php";
                            } else {
                                VirtualFile existsInProtectedComponents = baseDir.findFileByRelativePath(protectedPath
                                        + "/" + "components" + "/" + widgetPath + ".php");
                                if (existsInProtectedComponents != null) {
                                    widgetFilePath = protectedPath + "/" + "components" + "/" + widgetPath + ".php";
                                }
                            }
                        } else {
                            widgetFilePath = currentFolder + widgetPath + ".php";
                        }
                    }
                }
                VirtualFile file = baseDir.findFileByRelativePath(widgetFilePath);


                VirtualFile protectedPathDir = (!protectedPath.equals("")) ? baseDir.findFileByRelativePath(protectedPath) : null;


                String str = element.getText();
                TextRange textRange = CommonHelper.getTextRange(element, str);
                String uri = str.substring(textRange.getStartOffset(), textRange.getEndOffset());
                int start = textRange.getStartOffset();
                int len = textRange.getLength();

                if (file != null) {
                    PsiReference ref = new FileReference(file, uri, element,
                            new TextRange(start, start + len), YiiPsiReferenceProvider.project, protectedPathDir, protectedPathDir);
                    return new PsiReference[]{ref};
                }

                return PsiReference.EMPTY_ARRAY;
            }
        } catch (Exception e) {
            System.err.println("error" + e.getMessage());
        }


        return PsiReference.EMPTY_ARRAY;
    }
}
