package eu.mihosoft.vmftests.test2.vmfmodel;

import eu.mihosoft.vmf.core.Contains;

public interface Parent extends Named  {
    @Contains(opposite = "parent")
    Child[] getChildren();

    Named[] getElements();
}
