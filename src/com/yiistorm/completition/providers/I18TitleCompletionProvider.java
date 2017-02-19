package com.yiistorm.completition.providers;

import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import com.yiistorm.completition.lookups.MessageLookupElement;
import com.yiistorm.helpers.CommonHelper;
import com.yiistorm.helpers.I18NHelper;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 *
 */
public class I18TitleCompletionProvider extends CompletionProvider {

    protected void addCompletions(@NotNull com.intellij.codeInsight.completion.CompletionParameters completionParameters,
                                  ProcessingContext processingContext,
                                  @NotNull CompletionResultSet completionResultSet) {

        PsiFile currentFile = completionParameters.getPosition().getContainingFile();
        Project project = currentFile.getProject();

        String lang = I18NHelper.getLang(project);

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
        String searchString = CommonHelper.cleanCompleterSearchString(completionParameters.getPosition().getText());
        VirtualFile originalFile = currentFile.getOriginalFile().getVirtualFile();

        Boolean identMatch = false;
        if (originalFile != null) {

            String path = CommonHelper.getFilePath(currentFile);
            String protectedPath = CommonHelper.searchCurrentProtected(path);
            protectedPath = CommonHelper.getRelativePath(project, protectedPath);
            if (fileName.contains(".")) {
                String[] result = I18NHelper.findMessageSource(fileName, protectedPath, project);
                if (result != null) {
                    protectedPath = result[0];
                    fileName = result[2];
                } else {
                    protectedPath += "/messages/" + lang;
                }
            } else {
                protectedPath += "/messages/" + lang;
            }

            VirtualFile file = project.getBaseDir().findFileByRelativePath(protectedPath + "/" + fileName + ".php");
            if (file == null) {
                return;
            }
            HashMap<String, String> map = CommonHelper.parsePhpArrayConfig(project, file.getCanonicalPath());

            completionResultSet = completionResultSet.caseInsensitive();
            if (map.size() > 0) {
                for (String key : map.keySet()) {
                    if (key.equals(searchString)) {
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
