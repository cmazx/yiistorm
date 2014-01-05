package com.yiistorm.references.ReferenceProviders;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import com.yiistorm.DefaultSettings;
import com.yiistorm.YiiStormProjectComponent;
import com.yiistorm.helpers.CommonHelper;
import com.yiistorm.references.I18nFileReference;
import org.jetbrains.annotations.NotNull;

/**
 * @autor mazx
 */
public class I18nReferenceProvider extends PsiReferenceProvider {
    public static final PsiReferenceProvider[] EMPTY_ARRAY = new PsiReferenceProvider[0];
    public static Project project;
    public static PropertiesComponent properties;

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull final ProcessingContext context) {
        project = element.getProject();
        properties = PropertiesComponent.getInstance(project);
        PsiFile vfile = element.getContainingFile();
        String lang = YiiStormProjectComponent.getInstance(vfile.getProject()).getProp("langName");
        if (lang == null) {
            lang = DefaultSettings.langName;
        }

        try {
            String protectedPath = CommonHelper.searchCurrentProtected(CommonHelper.getFilePath(vfile));
            String relativePath = protectedPath.replace(project.getBaseDir().getCanonicalPath() + "/", "")
                    + "/messages/" + lang + "/" + CommonHelper.rmQuotes(element.getText()) + ".php";

            VirtualFile viewfile = project.getBaseDir().findFileByRelativePath(relativePath);

            if (viewfile != null) {
                PsiReference ref = new I18nFileReference(
                        viewfile,
                        element,
                        element.getTextRange(),
                        project);
                return new PsiReference[]{ref};
            }
        } catch (Exception e) {
            System.err.println("error" + e.getMessage());
        }

        return PsiReference.EMPTY_ARRAY;
    }
}
