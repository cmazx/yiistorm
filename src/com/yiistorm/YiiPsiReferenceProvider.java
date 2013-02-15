package com.yiistorm;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import com.yiistorm.ReferenceProviders.ARRelationReferenceProvider;
import com.yiistorm.ReferenceProviders.ControllerRenderViewReferenceProvider;
import com.yiistorm.ReferenceProviders.ViewRenderViewReferenceProvider;
import com.yiistorm.helpers.YiiRefsHelper;
import org.jetbrains.annotations.NotNull;

public class YiiPsiReferenceProvider extends PsiReferenceProvider {

    public static final PsiReferenceProvider[] EMPTY_ARRAY = new PsiReferenceProvider[0];
    public static String projectPath;
    public static Project project;

    public YiiPsiReferenceProvider() {
    }

    /**
     * Return reference or empty array
     *
     * @param element PsiElement
     * @param context ProcessingContext
     * @return PsiReference[]
     */
    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull final ProcessingContext context) {
        project = element.getProject();
        if (element.getClass().getName().endsWith("StringLiteralExpressionImpl")) {

            try {
                PsiFile file = element.getContainingFile();
                String path = file.getVirtualFile().getPath();
                projectPath = project.getBasePath().replace("\\", "/");
                int ProviderType = YiiRefsHelper.getYiiObjectType(path, element);
                switch (ProviderType) {
                    case YiiRefsHelper.YII_TYPE_CONTROLLER_TO_VIEW_RENDER:
                        return ControllerRenderViewReferenceProvider.getReference(path, element);
                    case YiiRefsHelper.YII_TYPE_AR_RELATION:
                        return ARRelationReferenceProvider.getReference(path, element);
                    case YiiRefsHelper.YII_TYPE_VIEW_TO_VIEW_RENDER:
                        return ViewRenderViewReferenceProvider.getReference(path, element);
                }
            } catch (Exception e) {
                //System.err.println("error" + e.getMessage());
            }
        }
        return PsiReference.EMPTY_ARRAY;
    }


}
