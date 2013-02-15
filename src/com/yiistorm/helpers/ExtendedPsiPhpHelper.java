package com.yiistorm.helpers;

import com.intellij.psi.PsiElement;
import com.magicento.helpers.PsiPhpHelper;

/**
 * Created with IntelliJ IDEA.
 * User: mazx
 * Date: 15.02.13
 * Time: 19:10
 * To change this template use File | Settings | File Templates.
 */
public class ExtendedPsiPhpHelper extends PsiPhpHelper {

    public static PsiElement getNamespaceElement(PsiElement classElement) {
        PsiElement el = classElement.getParent().getParent();
        if (isNamespace(el)) {
            return el;
        } else {
            return null;
        }
    }

    public static String getNamespaceFullName(PsiElement namespaceElement) {
        String elementName = "";
        if (isNamespace(namespaceElement)) {
            elementName = PsiPhpHelper.findNextSiblingOfType(namespaceElement.getFirstChild(), "NSReference").getText();
            elementName += PsiPhpHelper.findNextSiblingOfType(namespaceElement.getFirstChild(), "identifier").getText();
        } else {

        }
        return elementName;
    }

    public static boolean isNamespace(PsiElement element) {
        return (element.toString() == "Namespace" && element.getFirstChild() != null);
    }

}
