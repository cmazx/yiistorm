package com.yiistorm;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import com.yiistorm.ReferenceProviders.*;
import com.yiistorm.helpers.YiiRefsHelper;
import org.jetbrains.annotations.NotNull;

public class YiiPsiReferenceProvider extends PsiReferenceProvider {

    public static final PsiReferenceProvider[] EMPTY_ARRAY = new PsiReferenceProvider[0];
    public static String projectPath;
    public static Project project;
    public static PropertiesComponent properties;

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
        String elname = element.getClass().getName();
        properties = PropertiesComponent.getInstance(project);
        if (elname.endsWith("StringLiteralExpressionImpl")) {

            try {
                PsiFile file = element.getContainingFile();
                VirtualFile vfile = file.getVirtualFile();
                if (vfile != null) {
                    String path = vfile.getPath();
                    String basePath = project.getBasePath();
                    if (basePath != null) {
                        projectPath = basePath.replace("\\", "/");
                        int ProviderType = YiiRefsHelper.getYiiObjectType(path, element);
                        switch (ProviderType) {
                            case YiiRefsHelper.YII_TYPE_CONTROLLER_TO_VIEW_RENDER:
                                return ControllerRenderViewReferenceProvider.getReference(path, element);
                            case YiiRefsHelper.YII_TYPE_AR_RELATION:
                                return ARRelationReferenceProvider.getReference(path, element);
                            case YiiRefsHelper.YII_TYPE_VIEW_TO_VIEW_RENDER:
                                return ViewRenderViewReferenceProvider.getReference(path, element);
                            case YiiRefsHelper.YII_TYPE_WIDGET_CALL:
                                return WidgetCallReferenceProvider.getReference(path, element);
                            case YiiRefsHelper.YII_TYPE_CACTION_TO_VIEW_RENDER:
                                return CActionRenderViewReferenceProvider.getReference(path, element);
                            case YiiRefsHelper.YII_TYPE_WIDGET_VIEW_RENDER:
                                return WidgetRenderViewReferenceProvider.getReference(path, element);
                            case YiiRefsHelper.YII_TYPE_CONTROLLER_ACTIONS_CACTION:
                                return ControlleActionsClassReferenceProvider.getReference(path, element);
                        }
                    }
                }
            } catch (Exception e) {
                //System.err.println("error" + e.getMessage());
            }
        }
        return PsiReference.EMPTY_ARRAY;
    }


}
