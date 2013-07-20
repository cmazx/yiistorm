package com.yiistorm.completition.providers;

import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.PhpPsiElement;
import com.yiistorm.elements.Lookups.IgnoredLookupElement;
import com.yiistorm.elements.Lookups.NewFileLookupElement;
import com.yiistorm.helpers.CommonHelper;
import com.yiistorm.helpers.CompleterHelper;
import org.jetbrains.annotations.NotNull;

import java.util.regex.PatternSyntaxException;

/**
 * Created with IntelliJ IDEA.
 * User: mazx
 * Date: 19.07.13
 * Time: 2:33
 * To change this template use File | Settings | File Templates.
 */
public class ViewCompletionProvider<CompletionParameters> extends CompletionProvider {
    public static final int ABSOLUTE_LINK = 1;
    public static final int RELATIVE_LINK = 2;
    public static final int MODULE_RELATIVE_LINK = 3;

    public String[] getRenderParams(com.intellij.codeInsight.completion.CompletionParameters c) {
        PsiElement pEl = c.getLookup().getPsiElement();
        String[] names = {};
        if (pEl != null) {
            PsiElement pString = pEl.getParent();
            if (pString != null) {
                PsiElement nextSibling = pString.getNextSibling();

                while ((nextSibling != null && !nextSibling.getClass().getSimpleName().contains("ArrayCreationExpressionImpl"))) {
                    // System.err.print(nextSibling.getClass().getSimpleName());
                    nextSibling = nextSibling.getNextSibling();
                }
                if (nextSibling != null) {
                    PsiElement[] list = nextSibling.getChildren();
                    for (PsiElement el : list) {

                        PsiElement[] keyValueList = el.getChildren();
                        if (keyValueList.length == 2) {
                            String keyText = "";
                            String valueText = "";
                            String valueType = "";
                            for (PsiElement keyValueEl : keyValueList) {
                                PhpPsiElement kv = (PhpPsiElement) keyValueEl;
                                System.err.println(kv.toString());

                                if (kv.toString().equals("Array key")) {
                                    keyText = keyValueEl.getText();
                                }
                                if (kv.toString().equals("Array value")) {
                                    for (PsiElement value : kv.getChildren()) {
                                        if (value.toString().equals("String")) {
                                            valueType = "string";
                                        }
                                        if (value.toString().equals("Variable")) {
                                            //Get class from object
                                            //TODO: GET Class name
                                        }
                                    }

                                    valueText = keyValueEl.getText();
                                }

                            }
                            valueText = valueText;
                        }

                    }
                    list = list;
                }
            }
        }
        return names;
    }

    @Override
    protected void addCompletions(@NotNull com.intellij.codeInsight.completion.CompletionParameters completionParameters,
                                  ProcessingContext processingContext,
                                  @NotNull CompletionResultSet completionResultSet) {

        this.getRenderParams(completionParameters);
        PsiFile psiContainingFile = completionParameters.getPosition().getContainingFile();
        String cleanText = CommonHelper.cleanCompleterSearchString(completionParameters.getPosition().getText());
        String searchString = cleanText;
        int linkType = _getLinkType(searchString);
        VirtualFile originalFile = psiContainingFile.getOriginalFile().getVirtualFile();

        if (originalFile != null) {
            String controllerName = CommonHelper.getControllerName(originalFile.getName());
            String viewsPath = CommonHelper.getViewsPathFromControllerFile(psiContainingFile, linkType);
            String path = viewsPath;
            String resultAppend = ""; // prefix part for results
            //absolute path
            if (linkType != RELATIVE_LINK) {
                resultAppend = cleanText.replaceAll("(?si)/[a-z0-9_]+$", "/").replace("//", "").replaceAll("(?im)^/", "");
                path = path + resultAppend;   // fullpath to view folder
                searchString = searchString.replaceAll("(?si).+/", "");   //Only last part
                path = path.replaceAll("(?si)/[a-z0-9_]+$", "");    //Only path w/o last part  and
            } else {
                path = path + controllerName + "/";
            }


            String[] files = CompleterHelper.searchFiles(path, searchString);

            // completionResultSet.withPrefixMatcher("index");
            Boolean identMatch = false;
            // ArrayList<LookupElement> matches = new ArrayList<LookupElement>();
            for (String file : files) {
                String file_name = file.replace(".php", "");
                if ((resultAppend + file_name).equals(resultAppend + searchString)) {
                    identMatch = true;
                }
                completionResultSet.getPrefixMatcher().prefixMatches(resultAppend + file_name);
                //matches.add(LookupElementBuilder.create(resultAppend + file_name));

                completionResultSet.addElement(LookupElementBuilder.create(resultAppend + file_name));
            }

            if (!identMatch) {
                NewFileLookupElement n = new NewFileLookupElement(searchString, path, completionParameters.getPosition().getProject());
                completionResultSet.addElement(n);
                  /* ControllerLookupElementWeigher cl = new ControllerLookupElementWeigher(searchString, true, false);
                CompletionSorter cs = CompletionSorter.emptySorter();
                cs.weigh(cl);
                completionResultSet.withRelevanceSorter(cs);  */
                if (files.length == 0) {
                    completionResultSet.addElement(new IgnoredLookupElement(searchString));
                }
                //completionResultSet.getPrefixMatcher().prefixMatches(searchString);

            }
            //  completionResultSet.addAllElements(matches);
        }
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
