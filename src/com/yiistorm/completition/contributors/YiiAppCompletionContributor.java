package com.yiistorm.completition.contributors;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.StandardPatterns;
import com.jetbrains.php.lang.PhpLanguage;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.patterns.PhpPatterns;
import com.yiistorm.completition.providers.YiiAppCompletionProvider;

/**
 *
 */
public class YiiAppCompletionContributor extends CompletionContributor {
    public YiiAppCompletionContributor() {
        extend(CompletionType.BASIC, appFieldPattern(), new YiiAppCompletionProvider());
    }


    private static PsiElementPattern.Capture appFieldPattern() {
        return PlatformPatterns.psiElement()
                .withParent(PlatformPatterns.psiElement().withElementType(PhpElementTypes.FIELD_REFERENCE)
                                .withChild(
                                        PlatformPatterns.psiElement().withElementType(PhpElementTypes.METHOD_REFERENCE)
                                                .referencing(
                                                        PhpPatterns.psiElement().withElementType(PhpElementTypes.CLASS_METHOD)
                                                                .withName("app").withParent(
                                                                PhpPatterns.psiElement()
                                                                        .withElementType(PhpElementTypes.CLASS)
                                                                        .withName(StandardPatterns.string().oneOfIgnoreCase("Yii", "YiiBase"))
                                                        )
                                                )
                                )
                ).withLanguage(PhpLanguage.INSTANCE);

    }
}
