package com.yiistorm.helpers;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * User: mazx
 * Date: 15.02.13
 * Time: 18:55
 * To change this template use File | Settings | File Templates.
 */
public class CommonHelper {
    public static TextRange getTextRange(PsiElement element, String str) {
        Class elementClass = element.getClass();
        Method method = null;
        try {
            method = elementClass.getMethod("getValueRange");
            Object obj = null;
            obj = method.invoke(element);
            TextRange textRange = (TextRange) obj;
            String uri = str.substring(textRange.getStartOffset(), textRange.getEndOffset());
            return textRange;
        } catch (Exception e) {
            return null;
        }
    }

    public static String prepareClassName(String str)
    {
        return   rmQuotes(str).replaceAll("^\\\\", "");
    }

    public static String rmQuotes(String str)
    {
        return   str.replace("'", "");
    }

    public static String getCleanClassName(String str)
    {
        return  rmQuotes(str).replaceAll("^.+\\\\", "").replace("\\", "");
    }


}
