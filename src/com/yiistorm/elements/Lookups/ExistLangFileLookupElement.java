package com.yiistorm.elements.Lookups;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;


public class ExistLangFileLookupElement extends LookupElement {

    private String title;
    public String createTitle = "lang category file";
    private PsiElement psiElement = null;

    @Nullable
    private InsertHandler<LookupElement> insertHandler = null;

    public ExistLangFileLookupElement(String title) {

        if (title.startsWith("//")) {
            this.title = title.replace("//", "");
        } else if (title.startsWith("/")) {
            this.title = title.replaceAll("(?im)^/", "");
        } else {
            this.title = title;
        }
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
        ImageIcon icon = new ImageIcon(this.getClass().getResource("/com/yiistorm/images/yii.png"));
        presentation.setIcon(icon);
        presentation.setTypeText(createTitle);
        presentation.setTypeGrayed(false);
    }


}
