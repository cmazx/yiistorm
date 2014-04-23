package com.yiistorm.completition.lookups;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.yiistorm.DefaultSettings;
import com.yiistorm.YiiStormProjectComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.net.URL;


public class MessageLookupElement extends LookupElement {

    private String title;
    private String langTitle;
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

    public MessageLookupElement(String title, String langTitle, Project project) {
        this.title = title;
        this.langTitle = langTitle;
        String lang = YiiStormProjectComponent.getInstance(project).getProp("langName");
        URL icoUrl = this.getClass().getResource("/com/yiistorm/images/lang/"
                + (lang != null ? lang : DefaultSettings.langName) + ".png");
        if (icoUrl == null) {
            icoUrl = this.getClass().getResource("/com/yiistorm/images/yii.png");
        }
        icon = new ImageIcon(icoUrl);
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
        presentation.setTypeText(langTitle);
        presentation.setTypeGrayed(false);
    }

}
