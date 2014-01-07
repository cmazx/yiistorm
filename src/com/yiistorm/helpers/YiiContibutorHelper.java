package com.yiistorm.helpers;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.PhpLanguage;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.patterns.PhpElementPattern;
import com.jetbrains.php.lang.patterns.PhpPatterns;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;

/**
 * Created by mazx on 05.01.14.
 */
public class YiiContibutorHelper {
    public static PsiElementPattern.Capture stringInMethod(String methodName, String className, int superLevels) {
        return PlatformPatterns.psiElement(PsiElement.class)
                .withParent(YiiContibutorHelper.methodLiteralExpression(methodName, className, superLevels))
                .withLanguage(PhpLanguage.INSTANCE);
    }

    public static PsiElementPattern.Capture firstStringInMethod(String methodName, String className, int superLevels) {

        return PlatformPatterns.psiElement(PsiElement.class)
                .withParent(
                        YiiContibutorHelper.methodLiteralExpression(methodName, className, superLevels)
                                .insideStarting(
                                        PlatformPatterns.psiElement().withElementType(PhpElementTypes.PARAMETER_LIST)
                                )
                )
                .withLanguage(PhpLanguage.INSTANCE);

    }

    public static PhpElementPattern.Capture<StringLiteralExpression> methodLiteralExpression(String methodName, String className, int superLevels) {
        return PhpPatterns.phpLiteralExpression()
                .withParent(
                        methodParamsList(methodName, className, superLevels)
                );
    }

    public static PsiElementPattern.Capture<PsiElement> methodParamsList(String methodName, String className,
                                                                         int superLevels) {
        return PlatformPatterns.psiElement().withElementType(PhpElementTypes.PARAMETER_LIST)
                .withParent(
                        PlatformPatterns.psiElement()
                                .withElementType(PhpElementTypes.METHOD_REFERENCE)
                                .referencing(
                                        PhpPatterns.psiElement().withElementType(
                                                PhpElementTypes.CLASS_METHOD
                                        ).withName(methodName)
                                                .withSuperParent(superLevels,
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

    public static PsiElementPattern.Capture firstStringInYiiMethod(String methodName) {
        return firstStringInMethod(methodName, "Yii", 5);
    }

    public static PsiElementPattern.Capture stringInYiiMethod(String methodName) {
        return stringInMethod(methodName, "Yii", 5);
    }
}
