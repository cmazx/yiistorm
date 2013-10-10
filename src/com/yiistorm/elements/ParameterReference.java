package com.yiistorm.elements;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ParameterReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {

    private String parameterName;

    private boolean wrapPercent = false;

    public ParameterReference(@NotNull PsiElement element, String ParameterName) {
        super(element);
        parameterName = ParameterName;
    }

    public ParameterReference(@NotNull StringLiteralExpression element) {
        super(element);

        parameterName = element.getText().substring(
                element.getValueRange().getStartOffset(),
                element.getValueRange().getEndOffset()
        ); // Remove quotes
    }

    public ParameterReference wrapVariantsWithPercent(boolean WrapPercent) {
        this.wrapPercent = WrapPercent;
        return this;
    }


    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);

        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
    }


    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean b) {
        return new ResolveResult[0];  //To change body of implemented methods use File | Settings | File Templates.
    }
}
