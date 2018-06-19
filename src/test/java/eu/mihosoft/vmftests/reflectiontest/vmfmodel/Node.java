package eu.mihosoft.vmftests.reflectiontest.vmfmodel;

import eu.mihosoft.vmf.core.Container;
import eu.mihosoft.vmf.core.Contains;

public interface Node {

    @Contains(opposite = "parent")
    Node[] getChildren();

    @Container(opposite = "children")
    Node getParent();

}