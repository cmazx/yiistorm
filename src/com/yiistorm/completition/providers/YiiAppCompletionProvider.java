package com.yiistorm.completition.providers;

import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.ProcessingContext;
import com.yiistorm.YiiStormProjectComponent;
import com.yiistorm.completition.lookups.ConfigComponentLookupElement;
import com.yiistorm.elements.ConfigParser;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

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
        ConfigParser config = YiiStormProjectComponent.getInstance(project).getYiiConfig();
        if (config != null) {
            HashMap<String, String> classMap = config.getComponentsClassMap();
            if (classMap != null && classMap.size() > 0) {
                for (String componentName : classMap.keySet()) {
                    completionResultSet.addElement(new ConfigComponentLookupElement(componentName, project));
                }
            }
        }

    }


}
