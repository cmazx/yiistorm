package com.yiistorm.helpers;

import com.intellij.psi.PsiElement;

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
        return (element.toString().equals("Namespace") && element.getFirstChild() != null);
    }

    public static boolean parentMethodNameMatch(PsiElement el, String regexp) {
        PsiElement parameter_list = PsiPhpHelper.findFirstParentOfType(el, PsiPhpHelper.METHOD_REFERENCE);
        if (parameter_list != null) {
            String mrefchild = PsiPhpHelper.getMethodName(parameter_list);
            try {
                if (mrefchild.matches(regexp)) {
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public static boolean parentClassNameMatch(PsiElement el, String regexp) {
        PsiElement classEl = PsiPhpHelper.getClassElement(el);
        if (classEl.toString().matches(regexp)) {
            return true;
        }
        return false;
    }


}
