package com.yiistorm;

import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.patterns.PhpPatterns;
import com.yiistorm.elements.ParameterReference;
import com.yiistorm.elements.PhpStringLiteralExpressionReference;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class YiiApplicationReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar psiReferenceRegistrar) {

        psiReferenceRegistrar.registerReferenceProvider(PhpPatterns.psiElement(PhpElementTypes.PARAMETER),
                new PhpStringLiteralExpressionReference(ParameterReference.class)
                        .addCall("Yii", "getParameter")
        );


    }

}