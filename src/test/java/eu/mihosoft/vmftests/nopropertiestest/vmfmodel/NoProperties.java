package eu.mihosoft.vmftests.nopropertiestest.vmfmodel;

import eu.mihosoft.vmf.core.DelegateTo;

public interface NoProperties {
    @DelegateTo(className="eu.mihosoft.vmftests.nopropertiestest.DelegatedBehavior")
    public void testDelegation();
}