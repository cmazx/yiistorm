package com.yiistorm.references.referenceContributors;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.jetbrains.php.lang.PhpLanguage;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.yiistorm.helpers.YiiContibutorHelper;
import com.yiistorm.references.ReferenceProviders.I18nReferenceProvider;

public class I18nReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(categoryPattern(), new I18nReferenceProvider());

    }

    public PsiElementPattern.Capture categoryPattern() {
        return PlatformPatterns.psiElement(PsiElement.class)
                .withElementType(PhpElementTypes.STRING)
                .withParent(YiiContibutorHelper.methodParamsList("t", StandardPatterns.string().oneOf("Yii", "YiiBase")))
                .insideStarting(
                        PlatformPatterns.psiElement().withElementType(PhpElementTypes.PARAMETER_LIST)
                )
                .withLanguage(PhpLanguage.INSTANCE);
    }

}
