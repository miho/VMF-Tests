package eu.mihosoft.vmftests.reflectiontest.vmfmodel;

import eu.mihosoft.vcollections.VList;
import eu.mihosoft.vmf.core.DefaultValue;

import java.util.Arrays;
import java.util.List;

public interface ReflectionTest {

    @DefaultValue("23")
    int getId();

    @DefaultValue("eu.mihosoft.vcollections.VList.newInstance(java.util.Arrays.asList(\"a\",\"b\", \"c\"))")
    String[] getValues();

    int getId2();

}
