package com.yiistorm.completition;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementWeigher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created with IntelliJ IDEA.
 * User: mazx
 * Date: 20.07.13
 * Time: 3:14
 * To change this template use File | Settings | File Templates.
 */
public class ControllerLookupElementWeigher extends LookupElementWeigher {
    public ControllerLookupElementWeigher(String id, boolean negated, boolean dependsOnPrefix) {
        super(id, negated, dependsOnPrefix);
    }

    @Nullable
    @Override
    public Comparable weigh(@NotNull LookupElement lookupElement) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
