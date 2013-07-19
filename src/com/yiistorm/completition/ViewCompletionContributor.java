package com.yiistorm.completition;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.PhpLanguage;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import com.yiistorm.completition.providers.ViewCompletionProvider;


public class ViewCompletionContributor extends CompletionContributor {
    public ViewCompletionContributor() {
        extend(CompletionType.BASIC, viewsPattern(), new ViewCompletionProvider<CompletionParameters>());
    }


    public static PsiElementPattern.Capture viewsPattern() {
        return PlatformPatterns
                .psiElement(PsiElement.class)
                        //.psiElement(StringLiteralExpression.class)
                .withParent(
                        PlatformPatterns.psiElement(StringLiteralExpression.class)
                                .withParent(
                                        PlatformPatterns.psiElement(PhpElementTypes.PARAMETER_LIST)
                                                .withParent(
                                                        PlatformPatterns.psiElement(PhpElementTypes.METHOD_REFERENCE)
                                                )
                                )
                )
                .withLanguage(PhpLanguage.INSTANCE);
    }


}

