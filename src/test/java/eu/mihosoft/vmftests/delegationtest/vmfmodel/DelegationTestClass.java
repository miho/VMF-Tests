package eu.mihosoft.vmftests.delegationtest.vmfmodel;

import eu.mihosoft.vmf.core.DelegateTo;

@DelegateTo(className = "eu.mihosoft.vmftests.delegationtest.MyBehavior")
public interface DelegationTestClass {

    String getName();

    @DelegateTo(className = "eu.mihosoft.vmftests.delegationtest.MyBehavior")
    boolean nameStartsWith(String string);

    @DelegateTo(className = "eu.mihosoft.vmftests.delegationtest.MyBehavior")
    boolean constructorCalled();
}
