package eu.mihosoft.vmftests.reflectiontest.vmfmodel;

import eu.mihosoft.vmf.core.DefaultValue;

public interface InheritedDefaultValueParent {
    @DefaultValue("123")
    int getMyValue();
}