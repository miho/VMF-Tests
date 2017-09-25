package eu.mihosoft.vmftests.test2.vmfmodel;

import eu.mihosoft.vmf.core.Container;

public interface Child extends Named {
    @Container(opposite="children")
    Parent getParent();
}
