package eu.mihosoft.vmftests.reflectiontest.vmfmodel;

import eu.mihosoft.vmf.core.DefaultValue;

public interface InheritedDefaultValueOverride2 extends InheritedDefaultValueParent{
    // should default to 0 (the default for int)
    int getMyValue();
}