package com.yiistorm.completition.providers;

import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.util.ProcessingContext;
import com.yiistorm.helpers.CommonHelper;
import com.yiistorm.helpers.CompleterHelper;
import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: mazx
 * Date: 19.07.13
 * Time: 2:33
 * To change this template use File | Settings | File Templates.
 */
public class ViewCompletionProvider<CompletionParameters> extends CompletionProvider {
    @Override
    protected void addCompletions(@NotNull com.intellij.codeInsight.completion.CompletionParameters completionParameters,
                                  ProcessingContext processingContext,
                                  @NotNull CompletionResultSet completionResultSet) {

        PsiFile psiContainingFile = completionParameters.getPosition().getContainingFile();
        String cleanText = CommonHelper.cleanCompleterSearchString(completionParameters.getPosition().getText());
        String searchString = cleanText;
        VirtualFile originalFile = psiContainingFile.getOriginalFile().getVirtualFile();

        if (originalFile != null) {
            String controllerName = CommonHelper.getControllerName(originalFile.getName());
            String path = CommonHelper.getViewsPathFromControllerFile(originalFile);
            String resultAppend = ""; // prefix part for results
            //absolute path
            if (searchString.contains("//")) {
                resultAppend = cleanText.replaceAll("(?si)/[a-z0-9_]+$", "/").replace("//", "");
                path = path + resultAppend;   // no double slash
                searchString = searchString.replaceAll("(?si).+/", "");   //Only last part
                path = path.replaceAll("(?si)/[a-z0-9_]+$", "");    //Only path w/o last part  and
            } else {
                path = path + controllerName + "/";
            }

            String[] files = CompleterHelper.searchFiles(path, searchString);

            completionResultSet.withPrefixMatcher("index");
            for (String file : files) {
                String file_name = file.replace(".php", "");
                completionResultSet.getPrefixMatcher().prefixMatches(resultAppend + file_name);
                completionResultSet.addElement(LookupElementBuilder.create(resultAppend + file_name));
            }
        }
    }


}
