package com.yiistorm.references.ReferenceProviders;

import com.intellij.codeInsight.TargetElementUtilBase;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiUtilCore;
import com.yiistorm.helpers.CommonHelper;
import com.yiistorm.helpers.ExtendedPsiPhpHelper;
import com.yiistorm.helpers.PsiPhpHelper;
import com.yiistorm.helpers.YiiRefsHelper;
import com.yiistorm.references.FileReference;
import com.yiistorm.references.YiiPsiReferenceProvider;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class ARRelationReferenceProvider {

    public static PsiReference[] getReference(String path, @NotNull PsiElement element) {
        try {

            String viewPath = path.replace(YiiPsiReferenceProvider.projectPath, "");
            String protectedPath = YiiRefsHelper.getCurrentProtected(path);
            protectedPath = protectedPath.replace(YiiPsiReferenceProvider.projectPath, "");
            if (ARRelationReferenceProvider.isARRelationClassName(element)) {

                String str = element.getText();
                TextRange textRange = CommonHelper.getTextRange(element, str);
                if (textRange != null) {
                    VirtualFile baseDir = YiiPsiReferenceProvider.project.getBaseDir();
                    if (baseDir != null) {
                        String className = element.getText();
                        VirtualFile v = ARRelationReferenceProvider.getClassFile(className);
                        VirtualFile appDir = baseDir.findFileByRelativePath(viewPath);
                        VirtualFile protectedPathDir = (!protectedPath.equals("")) ? baseDir.findFileByRelativePath(protectedPath) : null;
                        if (appDir != null) {
                            PsiReference ref = new FileReference(v, str.substring(textRange.getStartOffset(), textRange.getEndOffset())
                                    , element,
                                    textRange, YiiPsiReferenceProvider.project, protectedPathDir, appDir);
                            return new PsiReference[]{ref};
                        }
                    }
                }
            }
            return PsiReference.EMPTY_ARRAY;
        } catch (Exception e) {
            //System.err.println("error" + e.getMessage());
        }
        return PsiReference.EMPTY_ARRAY;
    }

    /**
     * Check what PsiElement is Array value in CActiveRecord relations array
     *
     * @param el
     * @return
     */
    protected static boolean isARRelationClassName(PsiElement el) {
        if (PsiPhpHelper.isElementType(el.getParent(), "Array value")) {
            PsiElement leftEl = PsiPhpHelper.findPrevSiblingOfType(el.getParent(), "Array value");
            String text = leftEl.getText();

            if (text.matches("(self::HAS_MANY|self::MANY_MANY|self::HAS_ONE|self::BELONGS_TO)")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Search file by Class name
     *
     * @param className Name of class
     * @return VirtualFile
     */
    protected static VirtualFile getClassFile(String className) {
        String namespacedName = CommonHelper.prepareClassName(className);
        String cleanName = CommonHelper.getCleanClassName(className);
        List<PsiElement> elements = PsiPhpHelper.getPsiElementsFromClassName(cleanName, YiiPsiReferenceProvider.project);
        if (elements.size() > 0) {
            for (PsiElement element : elements) {
                String elementName = "";
                PsiElement namespaceElement = ExtendedPsiPhpHelper.getNamespaceElement(element);
                if (namespaceElement != null) {
                    elementName = ExtendedPsiPhpHelper.getNamespaceFullName(namespaceElement) + "\\";
                }
                elementName += PsiPhpHelper.getClassIdentifierName(element);
                PsiElement navElement = element.getNavigationElement();
                if (namespacedName.equals(elementName)) {
                    navElement = TargetElementUtilBase.getInstance().getGotoDeclarationTarget(element, navElement);
                    if (navElement != null) {
                        VirtualFile virtualFile = PsiUtilCore.getVirtualFile(navElement);
                        return virtualFile;
                    }
                }
            }
        }
        return null;
    }
}
