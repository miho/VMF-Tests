package eu.mihosoft.vmftests.reflectiontest.vmfmodel;

import eu.mihosoft.vmf.core.DefaultValue;

public interface InheritedDefaultValueOverride extends InheritedDefaultValueParent{
    @DefaultValue("-123")
    int getMyValue();
}