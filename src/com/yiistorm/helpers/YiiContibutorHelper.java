package com.yiistorm.helpers;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.StandardPatterns;
import com.intellij.patterns.StringPattern;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.PhpLanguage;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.patterns.PhpPatterns;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;

/**
 * Created by mazx on 05.01.14.
 */
public class YiiContibutorHelper {
    public static PsiElementPattern.Capture stringInMethod(String methodName, StringPattern className) {
        return PlatformPatterns.psiElement(PsiElement.class)
                .withParent(YiiContibutorHelper.methodLiteralExpression(methodName, className))
                .withLanguage(PhpLanguage.INSTANCE);
    }

    public static PsiElementPattern.Capture firstStringInMethod(String methodName, StringPattern className) {

        return PlatformPatterns.psiElement(PsiElement.class)
                .withParent(
                        YiiContibutorHelper.methodLiteralExpression(methodName, className)
                                .insideStarting(
                                        PlatformPatterns.psiElement().withElementType(PhpElementTypes.PARAMETER_LIST)
                                )
                )
                .withLanguage(PhpLanguage.INSTANCE);

    }

    public static com.jetbrains.php.injection.PhpElementPattern.Capture<StringLiteralExpression>
    methodLiteralExpression(String methodName, StringPattern className) {
        return PhpPatterns.phpLiteralExpression()
                .withParent(
                        methodParamsList(methodName, className)
                );
    }

    public static PsiElementPattern.Capture<PsiElement> methodParamsList(String methodName,
                                                                         StringPattern className) {
        return PlatformPatterns.psiElement().withElementType(PhpElementTypes.PARAMETER_LIST)
                .withParent(
                        PlatformPatterns.psiElement()
                                .withElementType(PhpElementTypes.METHOD_REFERENCE)
                                .referencing(
                                        PhpPatterns.psiElement().withElementType(
                                                PhpElementTypes.CLASS_METHOD
                                        ).withName(methodName)
                                                .withParent(
                                                        PhpPatterns.psiElement().withName(
                                                                className
                                                        ))
                                )

                );
    }

    public static PsiElementPattern.Capture<PsiElement> paramListInMethodWithName(String methodName) {
        return PlatformPatterns.psiElement().withElementType(PhpElementTypes.PARAMETER_LIST)
                .withParent(
                        PlatformPatterns.psiElement().withElementType(PhpElementTypes.METHOD_REFERENCE)
                                .referencing(PhpPatterns.psiElement().withElementType(PhpElementTypes.CLASS_METHOD)
                                        .withName(methodName)
                                )
                );
    }

    public static PsiElementPattern.Capture<PsiElement> arrayInMethodWithName(String methodName) {
        return PlatformPatterns.psiElement().withElementType(PhpElementTypes.ARRAY_CREATION_EXPRESSION)
                .withParent(
                        PlatformPatterns.psiElement().withElementType(PhpElementTypes.METHOD_REFERENCE)
                                .referencing(PhpPatterns.psiElement().withElementType(PhpElementTypes.CLASS_METHOD)
                                        .withName(methodName)
                                )
                );
    }


    public static PsiElementPattern.Capture firstStringInYiiMethod(String methodName) {
        return firstStringInMethod(methodName, StandardPatterns.string().oneOf("Yii", "YiiBase"));
    }

    public static PsiElementPattern.Capture stringInYiiMethod(String methodName) {
        return stringInMethod(methodName, StandardPatterns.string().oneOf("Yii", "YiiBase"));
    }
}
