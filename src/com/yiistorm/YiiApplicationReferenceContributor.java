package com.yiistorm;

import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.patterns.PhpPatterns;
import com.yiistorm.elements.ParameterReference;
import com.yiistorm.elements.PhpStringLiteralExpressionReference;

/**
 * Created with IntelliJ IDEA.
 * User: mazx
 * Date: 10.10.13
 * Time: 8:46
 * To change this template use File | Settings | File Templates.
 */
public class YiiApplicationReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(PsiReferenceRegistrar psiReferenceRegistrar) {

        psiReferenceRegistrar.registerReferenceProvider(PhpPatterns.psiElement(PhpElementTypes.PARAMETER),
                new PhpStringLiteralExpressionReference(ParameterReference.class)
                        .addCall("Yii", "getParameter")
        );


    }

}