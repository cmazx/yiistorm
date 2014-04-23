package com.yiistorm.completition.lookups;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;


public class ConfigComponentLookupElement extends LookupElement {

    private String title;
    private PsiElement psiElement = null;
    private ImageIcon icon;

    @Nullable
    private InsertHandler<LookupElement> insertHandler = new InsertHandler<LookupElement>() {
        @Override
        public void handleInsert(InsertionContext insertionContext, LookupElement lookupElement) {
            String lookup = lookupElement.getLookupString();
            int lookup2 = insertionContext.getStartOffset();
            insertionContext.getEditor().getDocument().replaceString(lookup2, lookup2 + lookup.length(), lookup);
        }
    };

    public ConfigComponentLookupElement(String title, Project project) {
        this.title = title;
        icon = new ImageIcon(this.getClass().getResource("/com/yiistorm/images/yii.png"));
    }

    public boolean isCaseSensitive() {
        return false;
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
        presentation.setItemText(title);
        presentation.setIcon(icon);
        presentation.setTypeText("application component");
        presentation.setTypeGrayed(false);
    }

}
