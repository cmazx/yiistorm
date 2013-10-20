package com.yiistorm.elements.Lookups;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class ExistFileLookupElement extends LookupElement {

    private String title;
    private String prependSlashes = "";
    private PsiElement psiElement = null;

    @Nullable
    private InsertHandler<LookupElement> insertHandler = null;

    public ExistFileLookupElement(String title) {

        if (title.startsWith("//")) {
            prependSlashes = "//";
            this.title = title.replace("//", "");
        } else if (title.startsWith("/")) {
            prependSlashes = "/";
            this.title = title.replaceAll("(?im)^/", "");
        } else {
            this.title = title;
        }
    }

    public ExistFileLookupElement(String title, String filePath, PsiElement psiElement, @Nullable InsertHandler<LookupElement> insertHandler) {
        if (title.startsWith("//")) {
            prependSlashes = "//";
            this.title = title.replace("//", "");
        } else if (title.startsWith("/")) {
            prependSlashes = "/";
            this.title = title.replaceAll("(?im)^/", "");
        } else {
            this.title = title;
        }


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
        presentation.setItemText(title);
        presentation.setIcon(PhpIcons.PHP_FILE);
        presentation.setTypeText("view file");
        presentation.setTypeGrayed(false);
    }

}
