package com.yiistorm.completition.providers;

import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import com.yiistorm.DefaultSettings;
import com.yiistorm.YiiStormProjectComponent;
import com.yiistorm.elements.Lookups.MessageLookupElement;
import com.yiistorm.helpers.CommonHelper;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * Created by mazx on 05.01.14.
 */
public class I18TitleCompletionProvider extends CompletionProvider {

    protected void addCompletions(@NotNull com.intellij.codeInsight.completion.CompletionParameters completionParameters,
                                  ProcessingContext processingContext,
                                  @NotNull CompletionResultSet completionResultSet) {

        PsiFile psiContainingFile = completionParameters.getPosition().getContainingFile();
        String lang = YiiStormProjectComponent.getInstance(psiContainingFile.getProject()).getProp("langName");
        if (lang == null) {
            lang = DefaultSettings.langName;
        }

        PsiElement position = completionParameters.getPosition();
        PsiElement list = (ParameterList) position.getParent().getParent();
        PsiElement[] children = list.getChildren();
        if (children.length < 2) {
            return;
        }
        if (!children[0].toString().equals("String") || children[1] != position.getParent()) {
            return;
        }
        String fileName = CommonHelper.rmQuotes(children[0].getFirstChild().getText());
        String cleanText = CommonHelper.rmQuotes(completionParameters.getPosition().getText());
        VirtualFile originalFile = psiContainingFile.getOriginalFile().getVirtualFile();

        Boolean identMatch = false;
        if (originalFile != null) {

            String path = CommonHelper.getFilePath(psiContainingFile);
            path = CommonHelper.searchCurrentProtected(path) + "/messages/" + lang + "/";
            HashMap<String, String> map = CommonHelper.parsePhpArrayConfig(psiContainingFile.getProject(),
                    path + fileName + ".php");

            completionResultSet.caseInsensitive();
            if (map.size() > 0) {
                for (String key : map.keySet()) {
                    if (key.equals(cleanText)) {
                        identMatch = true;
                    }
                    completionResultSet.getPrefixMatcher().prefixMatches(key.toLowerCase());
                    MessageLookupElement exFL = new MessageLookupElement(key, map.get(key),
                            completionParameters.getPosition().getProject());
                    completionResultSet.addElement(exFL);
                }
            }


            /*
            AUTOCREATE NEW LINES
            @FIXME: file sync bug

            if (!identMatch) {
                ArrayList<String> phpDoc = new ArrayList<String>();
                phpDoc.add("Localization file " + lang + "/" + cleanText);
                NewArrayValueLookupElement n = new NewArrayValueLookupElement(
                        cleanText,
                        path,
                        fileName,
                        completionParameters.getPosition().getProject()
                );
                completionResultSet.addElement(n);
                completionResultSet.addElement(new IgnoredLookupElement(cleanText));
            } */
        }

    }


}
