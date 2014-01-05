package com.yiistorm.completition.contributors;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.yiistorm.completition.providers.I18CategoryCompletionProvider;
import com.yiistorm.completition.providers.I18TitleCompletionProvider;

public class I18CompletionContributor extends CompletionContributor {
    public I18CompletionContributor() {
        extend(CompletionType.BASIC, categoryPattern(), new I18CategoryCompletionProvider());
        extend(CompletionType.BASIC, titlePattern(), new I18TitleCompletionProvider());
    }


    public static PsiElementPattern.Capture categoryPattern() {
        return YiiContibutorHelper.firstStringInYiiMethod("t");

    }

    public static PsiElementPattern.Capture titlePattern() {
        return PlatformPatterns.psiElement()
                .and(YiiContibutorHelper.stringInYiiMethod("t"))
                .andNot(YiiContibutorHelper.firstStringInYiiMethod("t"));

    }


}

