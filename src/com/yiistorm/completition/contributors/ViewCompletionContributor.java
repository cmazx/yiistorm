package com.yiistorm.completition.contributors;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.StandardPatterns;
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
        PsiElementPattern.Capture<PsiElement> $patterns = PlatformPatterns
                .psiElement(PsiElement.class)
                .withParent(
                        PlatformPatterns.or(
                                PlatformPatterns.psiElement(StringLiteralExpression.class)
                                        .withParent(
                                                PlatformPatterns.psiElement(PhpElementTypes.PARAMETER_LIST)
                                                        .withParent(
                                                                PlatformPatterns.psiElement(PhpElementTypes.METHOD_REFERENCE)
                                                                        .withText(StandardPatterns.string().contains("renderPartial("))
                                                        )

                                        ),

                                PlatformPatterns.psiElement(StringLiteralExpression.class)
                                        .withParent(
                                                PlatformPatterns.psiElement(PhpElementTypes.PARAMETER_LIST)
                                                        .withParent(
                                                                PlatformPatterns.psiElement(PhpElementTypes.METHOD_REFERENCE)
                                                                        .withText(StandardPatterns.string().contains("render("))
                                                        )

                                        ),

                                PlatformPatterns.psiElement(StringLiteralExpression.class)
                                        .withParent(
                                                PlatformPatterns.psiElement(PhpElementTypes.PARAMETER_LIST)
                                                        .withParent(
                                                                PlatformPatterns.psiElement(PhpElementTypes.METHOD_REFERENCE)
                                                                        .withText(StandardPatterns.string().contains("renderAjax("))
                                                        )

                                        )
                        )
                )
                .withLanguage(PhpLanguage.INSTANCE);
        return $patterns;
    }


}

