package com.yiistorm.completition.providers;

import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.ProcessingContext;
import com.yiistorm.helpers.CommonHelper;
import com.yiistorm.helpers.I18NHelper;
import org.jetbrains.annotations.NotNull;

/**
 *
 *
 */
public class YiiAppCompletionProvider extends CompletionProvider {

    protected void addCompletions(@NotNull com.intellij.codeInsight.completion.CompletionParameters completionParameters,
                                  ProcessingContext processingContext,
                                  @NotNull CompletionResultSet completionResultSet) {

        PsiFile currentFile = completionParameters.getPosition().getContainingFile();
        Project project = currentFile.getProject();
        String lang = I18NHelper.getLang(project);
        if (currentFile.getOriginalFile().getVirtualFile() != null) {

            String searchStringFull = CommonHelper.cleanCompleterSearchString(completionParameters.getPosition().getText());

            String path = CommonHelper.getFilePath(currentFile);
            String protectedPath = CommonHelper.searchCurrentProtected(path);
            if (protectedPath == null) {
                return;
            }
            path = CommonHelper.getRelativePath(project, protectedPath).replaceFirst("\\/", "");
            if (path == null) {
                return;
            }

            completionResultSet.addElement(LookupElementBuilder.create(searchStringFull + " test"));
        }


    }


}
