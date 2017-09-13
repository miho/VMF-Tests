package eu.mihosoft.vmf.tutorial01;

import eu.mihosoft.vcollections.VList;
import eu.mihosoft.vmf.runtime.core.DelegatedBehavior;

import java.util.List;
import java.util.stream.Collectors;

public class MyNodeDelegate implements DelegatedBehavior<MyNode>{

    private MyNode caller;

    @Override
    public void setCaller(MyNode caller) {
        this.caller = caller;
    }

    public VList<ImmutableElement> elementsWithIdLessThan(int value) {
        return VList.newInstance(caller.getElements().stream().
                filter(immutableElement -> immutableElement.getId() < value).
                collect(Collectors.toList()));
    }
}
