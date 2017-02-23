package com.yiistorm.references.referenceContributors;

import com.intellij.patterns.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.patterns.PhpPatterns;
import com.yiistorm.references.ReferenceProviders.ControllerRenderViewReferenceProvider;
import com.yiistorm.references.ReferenceProviders.ViewRenderViewReferenceProvider;
import com.yiistorm.references.ReferenceProviders.WidgetCallReferenceProvider;
import com.yiistorm.references.YiiPsiReferenceProvider;

public class YiiReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(
                PhpPatterns.phpElement()
                        .withElementType(PhpElementTypes.STRING),
                new YiiPsiReferenceProvider()
        );
        registrar.registerReferenceProvider(
                PhpPatterns.phpElement()
                        .withElementType(PhpElementTypes.STRING)
                        .withParent(isParamListInMethodWithName(".+?widget\\(.+"))
                        .and(inFile(PlatformPatterns.string().endsWith("Controller.php"))),
                new WidgetCallReferenceProvider()
        );
        //View-to-view
        registrar.registerReferenceProvider(
                PhpPatterns.phpElement()
                        .withElementType(PhpElementTypes.STRING)
                        .withParent(isParamListInMethodWithName(".+?render(Partial|Ajax)*\\(.+"))
                        .andNot(inFile(PlatformPatterns.string().endsWith("Controller.php"))),
                new ViewRenderViewReferenceProvider()
        );
        //Controller-to-view
        registrar.registerReferenceProvider(
                PhpPatterns.phpElement()
                        .withElementType(PhpElementTypes.STRING)
                        .withTreeParent(isParamListInMethodWithName("(?sim).+?render(Partial|Ajax)*\\(.+")),
                new ControllerRenderViewReferenceProvider()
        );
    }

    /**
     * Check element is param is parameterList in method reference
     */
    private TreeElementPattern isParamListInMethodWithName(String name) {

        return PhpPatterns.phpElement()
                .withElementType(PhpElementTypes.PARAMETER_LIST)
                .withTreeParent(
                        PhpPatterns.phpElement()
                                .withElementType(PhpElementTypes.METHOD_REFERENCE)
                                .withText(StandardPatterns.string().matches(name))
                );
    }

    /**
     * Check file name
     */
    private PsiElementPattern.Capture<PsiElement> inFile(StringPattern namePattern) {
        return PlatformPatterns.psiElement(PsiElement.class).inFile(PlatformPatterns.psiFile().withName(namePattern));
    }
}
