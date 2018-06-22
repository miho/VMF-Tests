package eu.mihosoft.vmftests.reflectiontest.vmfmodel;

import eu.mihosoft.vmf.core.DefaultValue;

public interface InheritedDefaultValueParent2 {
    @DefaultValue("456")
    int getMyValue();
}