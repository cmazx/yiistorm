package com.yiistorm.completition.providers;

import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.PhpPsiElement;
import com.jetbrains.php.lang.psi.elements.impl.ClassConstantReferenceImpl;
import com.jetbrains.php.lang.psi.elements.impl.ConstantReferenceImpl;
import com.jetbrains.php.lang.psi.elements.impl.PhpExpressionImpl;
import com.jetbrains.php.lang.psi.elements.impl.VariableImpl;
import com.yiistorm.elements.Lookups.IgnoredLookupElement;
import com.yiistorm.elements.Lookups.NewFileLookupElement;
import com.yiistorm.helpers.CommonHelper;
import com.yiistorm.helpers.CompleterHelper;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
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

    public HashMap<String, String> getRenderParams(com.intellij.codeInsight.completion.CompletionParameters c) {
        PsiElement pEl = c.getLookup().getPsiElement();
        HashMap<String, String> names = new HashMap<String, String>();
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
                            String valueType = "";
                            for (PsiElement keyValueEl : keyValueList) {


                                valueType = "";
                                PhpPsiElement kv = (PhpPsiElement) keyValueEl;

                                if (kv.toString().equals("Array key")) {
                                    keyText = keyValueEl.getText().replace("'", "");
                                }

                                if (kv.toString().equals("Array value")) {
                                    for (PsiElement value : kv.getChildren()) {

                                        if (value.toString().equals("Number")) {
                                            PhpExpressionImpl composite_value = (PhpExpressionImpl) value;
                                            valueType = composite_value.getType().toStringResolved();
                                        }
                                        //Class::CONST
                                        else if (value.toString().equals("Class constant reference")) {
                                            ClassConstantReferenceImpl value_cri = (ClassConstantReferenceImpl) value;
                                            valueType = value_cri.getText();
                                        }
                                        //null,bool,etc..
                                        else if (value.toString().equals("Constant reference")) {
                                            ConstantReferenceImpl value_cri = (ConstantReferenceImpl) value;

                                            valueType = value_cri.getType().toStringResolved();
                                        }
                                        // new Class();
                                        else if (value.toString().equals("New expression")) {
                                            PsiElement[] value_new_exr = value.getChildren();
                                            if (value_new_exr.length > 0) {
                                                PsiElement classref = value_new_exr[0];
                                                if (classref.toString().equals("Class reference")) {
                                                    valueType = classref.getText();
                                                } else {   //can't detect class
                                                    valueType = "";
                                                    //System.err.println("Bad 'New expression' founded by phpstorm :" + value_new_exr.toString());
                                                }
                                            }
                                        }
                                        // $var
                                        else if (value.toString().equals("Variable")) {

                                            VariableImpl psi = (VariableImpl) value;
                                            valueType = psi.getType().toStringResolved();
                                            if (valueType.startsWith("#")) {
                                                //FIXME: add type process
                                                valueType = " ";
                                            }
                                        }
                                        //all over types
                                        else {

                                            try {
                                                VariableImpl psi = (VariableImpl) value;
                                                valueType = psi.getType().toStringResolved();
                                            } catch (Exception e) {
                                                valueType = value.toString();
                                            }
                                        }
                                    }

                                    //Standartize some types
                                    if (valueType.startsWith("#F") || valueType.equals("Function call")) {
                                        //FIXME: try add return type from standard functions
                                        valueType = "resource";
                                    }
                                    if (valueType.equals("Array creation expression")) {
                                        valueType = "mixed";
                                    }

                                    if (keyText != null && valueType != "") {

                                        names.put(keyText, valueType);
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

        HashMap<String, String> translatingParams = this.getRenderParams(completionParameters);
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
                NewFileLookupElement n = new NewFileLookupElement(searchString, path, completionParameters.getPosition().getProject(), translatingParams);
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
