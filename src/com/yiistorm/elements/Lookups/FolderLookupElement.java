package com.yiistorm.elements.Lookups;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class FolderLookupElement extends LookupElement {

    private String title;
    private PsiElement psiElement = null;

    @Nullable
    private InsertHandler<LookupElement> insertHandler = null;

    public FolderLookupElement(String title) {

        this.title = title;
    }

    public FolderLookupElement(String title, String filePath, PsiElement psiElement, @Nullable InsertHandler<LookupElement> insertHandler) {
        this.title = title;
        this.insertHandler = insertHandler;
        this.psiElement = psiElement;
    }

    @NotNull
    @Override
    public String getLookupString() {
        return title + "/";
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
        presentation.setItemText(getLookupString());
        presentation.setIcon(PlatformIcons.FOLDER_ICON);
        presentation.setTypeText("Folder");
        presentation.setTypeGrayed(false);
    }

}
