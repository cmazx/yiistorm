package com.yiistorm.completition.providers;

import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.util.ProcessingContext;
import com.yiistorm.completition.lookups.ExistFileLookupElement;
import com.yiistorm.completition.lookups.IgnoredLookupElement;
import com.yiistorm.completition.lookups.NewFileLookupElement;
import com.yiistorm.helpers.CommonHelper;
import com.yiistorm.helpers.CompleterHelper;
import com.yiistorm.helpers.I18NHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class I18CategoryCompletionProvider extends CompletionProvider {

    protected void addCompletions(@NotNull com.intellij.codeInsight.completion.CompletionParameters completionParameters,
                                  ProcessingContext processingContext,
                                  @NotNull CompletionResultSet completionResultSet) {

        PsiFile currentFile = completionParameters.getPosition().getContainingFile();
        Project project = currentFile.getProject();
        String lang = I18NHelper.getLang(project);
        if (currentFile.getOriginalFile().getVirtualFile() != null) {

            String searchString = CommonHelper.cleanCompleterSearchString(completionParameters.getPosition().getText());

            String subpathAlias = "";
            String path = CommonHelper.getFilePath(currentFile);
            String protectedPath = CommonHelper.searchCurrentProtected(path);
            if (protectedPath == null) {
                return;
            }
            path = CommonHelper.getRelativePath(project, protectedPath).replaceFirst("\\/", "");
            if (path == null) {
                return;
            }
            if (searchString.contains(".")) {
                String[] result = I18NHelper.findMessageSource(searchString, path, project);
                if (result != null) {
                    path = result[0];
                    subpathAlias = !result[1].isEmpty() ? (result[1] + ".") : "";
                    searchString = result[2];
                } else {
                    path += "/messages/" + lang;
                }
            } else {
                path += "/messages/" + lang;
            }
            if (path == null) {
                return;
            }
            Boolean identMatch = false;
            VirtualFile fv = project.getBaseDir().findFileByRelativePath(path);
            if (fv != null) {

                String[] files = CompleterHelper.searchFiles(fv.getCanonicalPath(), searchString);

                for (String file : files) {
                    String file_name = file.replace(".php", "");
                    if ((file_name).equals(searchString)) {
                        identMatch = true;
                    }
                }

                if (!identMatch && !searchString.trim().isEmpty() && !searchString.isEmpty()) {
                    ArrayList<String> phpDoc = new ArrayList<String>();
                    phpDoc.add("Localization file " + lang + "/" + searchString);
                    NewFileLookupElement n = new NewFileLookupElement(
                            subpathAlias + searchString,
                            CommonHelper.getActiveTextPart(completionParameters.getPosition().getText()),
                            searchString,
                            fv.getCanonicalPath() + "/",
                            completionParameters.getPosition().getProject(),
                            phpDoc
                    );
                    n.createTitle = "create l18n category";
                    n.fileContent = "\nreturn array(\n\n);";
                    completionResultSet.addElement(n);
                    completionResultSet.addElement(new IgnoredLookupElement(searchString));
                }

                for (String file : files) {
                    String file_name = file.replace(".php", "");
                    completionResultSet.getPrefixMatcher().prefixMatches(subpathAlias + file_name);
                    ExistFileLookupElement exFL = new ExistFileLookupElement(subpathAlias + file_name);
                    exFL.createTitle = "create l18n category";
                    completionResultSet.addElement(exFL);
                }
            }
        }


    }


}
