package com.yiistorm.elements.Lookups;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class IgnoredLookupElement extends LookupElement {

    private String title;
    private PsiElement psiElement = null;

    @Nullable
    private InsertHandler<LookupElement> insertHandler = null;

    public IgnoredLookupElement(String title) {

        this.title = title;
    }

    public IgnoredLookupElement(String title, String filePath, PsiElement psiElement, @Nullable InsertHandler<LookupElement> insertHandler) {
        this.title = title;
        this.insertHandler = insertHandler;
        this.psiElement = psiElement;
    }

    @NotNull
    @Override
    public String getLookupString() {
        return title;
    }

    @NotNull
    public Object getObject() {
        return this.psiElement != null ? this.psiElement : super.getObject();
    }

    public void handleInsert(InsertionContext context) {
        if (this.insertHandler != null) {
            this.insertHandler.handleInsert(context, this);
        }
    }

    public void renderElement(LookupElementPresentation presentation) {
        presentation.setItemText("");
        //presentation.setIcon(PlatformIcons.);
        //String base=getPsiElement().getProject().getBasePath();
        presentation.setTypeText("");   //ignore autocomplete
        presentation.setTypeGrayed(false);
    }

}
