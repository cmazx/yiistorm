package com.yiistorm.completition.providers;

import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.util.ProcessingContext;
import com.yiistorm.DefaultSettings;
import com.yiistorm.YiiStormProjectComponent;
import com.yiistorm.elements.Lookups.ExistFileLookupElement;
import com.yiistorm.elements.Lookups.IgnoredLookupElement;
import com.yiistorm.elements.Lookups.NewFileLookupElement;
import com.yiistorm.helpers.CommonHelper;
import com.yiistorm.helpers.CompleterHelper;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

/**
 *
 *
 */
public class I18CategoryCompletionProvider extends CompletionProvider {

    protected void addCompletions(@NotNull com.intellij.codeInsight.completion.CompletionParameters completionParameters,
                                  ProcessingContext processingContext,
                                  @NotNull CompletionResultSet completionResultSet) {

        PsiFile psiContainingFile = completionParameters.getPosition().getContainingFile();
        String cleanText = CommonHelper.rmQuotes(completionParameters.getPosition().getText());
        String searchString = cleanText;
        VirtualFile originalFile = psiContainingFile.getOriginalFile().getVirtualFile();
        String lang = YiiStormProjectComponent.getInstance(psiContainingFile.getProject()).getProp("langName");
        if (lang == null) {
            lang = DefaultSettings.langName;
        }

        Boolean identMatch = false;
        if (originalFile != null) {

            String path = CommonHelper.getFilePath(psiContainingFile);
            path = CommonHelper.searchCurrentProtected(path) + "/messages/" + lang;

            if (new File(path).exists()) {


                String[] files = CompleterHelper.searchFiles(path, searchString);

                for (String file : files) {
                    String file_name = file.replace(".php", "");
                    if ((file_name).equals(searchString)) {
                        identMatch = true;
                    }
                }

                if (!identMatch && !searchString.trim().isEmpty()) {
                    ArrayList<String> phpDoc = new ArrayList<String>();
                    phpDoc.add("Localization file " + lang + "/" + cleanText);
                    NewFileLookupElement n = new NewFileLookupElement(
                            cleanText,
                            CommonHelper.getActiveTextPart(completionParameters.getPosition().getText()),
                            searchString,
                            path + "/",
                            completionParameters.getPosition().getProject(),
                            phpDoc
                    );
                    n.createTitle = "create l18n category";
                    n.fileContent = "\nreturn array(\n\n);";
                    completionResultSet.addElement(n);
                    completionResultSet.addElement(new IgnoredLookupElement(cleanText));
                }

                for (String file : files) {
                    String file_name = file.replace(".php", "");
                    completionResultSet.getPrefixMatcher().prefixMatches(cleanText);
                    ExistFileLookupElement exFL = new ExistFileLookupElement(file_name);
                    exFL.createTitle = "create l18n category";
                    completionResultSet.addElement(exFL);
                }
            }
        }


    }


}
