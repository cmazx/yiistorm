package com.yiistorm.completition.providers;

import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.PhpPsiElement;
import com.yiistorm.completition.lookups.ExistFileLookupElement;
import com.yiistorm.completition.lookups.FolderLookupElement;
import com.yiistorm.completition.lookups.IgnoredLookupElement;
import com.yiistorm.completition.lookups.NewFileLookupElement;
import com.yiistorm.helpers.CommonHelper;
import com.yiistorm.helpers.CompleterHelper;
import com.yiistorm.helpers.PsiPhpHelper;
import com.yiistorm.helpers.PsiPhpTypeHelper;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.PatternSyntaxException;

public class ViewCompletionProvider<CompletionParameters> extends CompletionProvider {
    public static final int ABSOLUTE_LINK = 1;
    public static final int RELATIVE_LINK = 2;
    public static final int MODULE_RELATIVE_LINK = 3;

    public ArrayList<String> getRenderParams(com.intellij.codeInsight.completion.CompletionParameters c) {
        PsiElement pEl = c.getLookup().getPsiElement();

        ArrayList<String> names = new ArrayList<String>();
        String creatorClassName = PsiPhpHelper.getClassName(pEl);
        if (creatorClassName != null && !creatorClassName.isEmpty()) {
            names.add("@var " + creatorClassName + " $this");
        }
        if (pEl != null) {
            PsiElement pString = pEl.getParent();
            if (pString != null) {
                PsiElement nextSibling = pString.getNextSibling();

                while ((nextSibling != null && !nextSibling.getClass().getSimpleName().contains("ArrayCreationExpressionImpl"))) {
                    nextSibling = nextSibling.getNextSibling();
                }
                if (nextSibling != null) {
                    PsiElement[] list = nextSibling.getChildren();
                    for (PsiElement el : list) {

                        PsiElement[] keyValueList = el.getChildren();
                        if (keyValueList.length == 2) {
                            String keyText = "";
                            String valueType;
                            for (PsiElement keyValueEl : keyValueList) {

                                valueType = "";
                                PhpPsiElement kv = (PhpPsiElement) keyValueEl;

                                if (kv.toString().equals("Array key")) {
                                    keyText = keyValueEl.getText().replace("'", "");
                                }

                                if (kv.toString().equals("Array value")) {
                                    for (PsiElement psiElement : kv.getChildren()) {
                                        valueType = PsiPhpTypeHelper.detectType(psiElement);
                                    }
                                    //Standartize some types
                                    if (keyText != null && !valueType.equals("")) {

                                        names.add("@var " + valueType + " $" + keyText);
                                    }
                                    keyText = null;
                                }

                            }


                        }
                    }

                }
            }
        }


        return names;
    }

    @Override
    protected void addCompletions(@NotNull com.intellij.codeInsight.completion.CompletionParameters completionParameters,
                                  ProcessingContext processingContext,
                                  @NotNull CompletionResultSet completionResultSet) {

        ArrayList<String> translatingParams = this.getRenderParams(completionParameters);
        PsiFile psiContainingFile = completionParameters.getPosition().getContainingFile();
        String cleanText = CommonHelper.cleanCompleterSearchString(completionParameters.getPosition().getText());
        String searchString = cleanText;
        int linkType = _getLinkType(searchString);
        VirtualFile originalFile = psiContainingFile.getOriginalFile().getVirtualFile();

        if (originalFile != null) {

            String controllerName = getControllerName(psiContainingFile);
            String path;
            String resultAppend = ""; // prefix part for results
            if (!controllerName.isEmpty()) {   //from controller
                path = CommonHelper.getViewsPathFromControllerFile(psiContainingFile, linkType);
                if (linkType != RELATIVE_LINK) {
                    resultAppend = cleanText.replaceAll("(?si)/[a-z0-9_]+$", "/");
                    path = path.replaceAll("/$", "") + "/";   // fullpath to view folder
                    searchString = searchString.replaceAll("(?si).+/", "");   //Only last part
                    path = path.replaceAll("(?si)/[a-z0-9_]+$", "");    //Only path w/o last part  and
                    path = path + resultAppend.replaceAll("^//", "");
                } else {
                    if (!path.endsWith("/")) {
                        path += "/";
                    }
                    path += controllerName + "/";
                    if (searchString.contains("/")) {
                        resultAppend = searchString.replaceAll("(?si)/[a-z0-9_]+$", "/");
                        searchString = searchString.replaceAll("(?si).+/", "");
                        path = path + resultAppend.replaceAll("^//", "");
                    }
                }
            } else {       //not from controller
                if (linkType != RELATIVE_LINK) {   //ABSOLUTE PATH
                    path = CommonHelper.getFilePath(psiContainingFile);
                    String endPart = path.replaceAll("(?im)^.+?views/", "");
                    path = path.replace(endPart, "");
                    resultAppend = cleanText.replaceAll("(?si)/[a-z0-9_]+$", "/");
                    if (resultAppend.startsWith("//")) {
                        path += resultAppend.replaceAll("(?im)^//", "");
                    } else if (cleanText.startsWith("/")) {
                        path += resultAppend.replaceAll("(?im)^/", "");
                    }
                    searchString = searchString.replaceAll("(?si).+/", "");   //Only last part
                    path = path.replace("(?im)/[a-z0-9_]+?.php$", "");
                } else {                 //RELATIVE PATH
                    path = CommonHelper.getFilePath(psiContainingFile);
                    if (searchString.contains("/")) {
                        resultAppend = searchString.replaceAll("(?si)/[a-z0-9_]+$", "/");
                        searchString = searchString.replaceAll("(?si).+/", "");
                        path = path + resultAppend.replaceAll("^//", "");
                    }
                }
            }

            if (new File(path).exists()) {


                String[] files = CompleterHelper.searchFiles(path, searchString);
                Boolean identMatch = false;

                for (String file : files) {
                    String file_name = file.replace(".php", "");
                    if ((resultAppend + file_name).equals(resultAppend + searchString)) {
                        identMatch = true;
                    }
                    file_name = resultAppend + file_name.replace("\\", "_");

                    completionResultSet.getPrefixMatcher().prefixMatches(cleanText);
                    ExistFileLookupElement exFL = new ExistFileLookupElement(file_name);
                    completionResultSet.addElement(exFL);
                }

                //FOLDERS
                if (!identMatch) {

                    String[] folders = CompleterHelper.searchFolders(path, searchString);
                    for (String folder : folders) {
                        if ((resultAppend + folder).equals(resultAppend + searchString)) {
                            identMatch = true;
                        }
                        completionResultSet.getPrefixMatcher().prefixMatches(resultAppend + folder);
                        FolderLookupElement flEl = new FolderLookupElement(folder);
                        completionResultSet.addElement(flEl);
                    }
                }

                if (!identMatch && !searchString.trim().isEmpty()) {
                    NewFileLookupElement n = new NewFileLookupElement(
                            cleanText,
                            CommonHelper.getActiveTextPart(completionParameters.getPosition().getText()),
                            searchString, path,
                            completionParameters.getPosition().getProject(),
                            translatingParams
                    );
                    completionResultSet.addElement(n);
                    completionResultSet.addElement(new IgnoredLookupElement(cleanText));
                }
            }
        }
    }

    private String getControllerName(PsiFile file) {
        VirtualFile originalFile = file.getOriginalFile().getVirtualFile();
        if (originalFile != null) {
            String s = CommonHelper.detectPathFileType(originalFile.getPath());
            if (s.equals("controller")) {
                return CommonHelper.getControllerName(originalFile.getName());
            }
        }
        return "";
    }

    private static int _getLinkType(String path) {
        try {
            if (path.matches("(?im)^//.+")) {
                return ABSOLUTE_LINK;
            }
        } catch (PatternSyntaxException x) {
            //
        }
        try {
            if (path.matches("(?im)^/.+")) {
                return MODULE_RELATIVE_LINK;
            }
        } catch (PatternSyntaxException ex) {
            //
        }

        return RELATIVE_LINK;
    }
}
