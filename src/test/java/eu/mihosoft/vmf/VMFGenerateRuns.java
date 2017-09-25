package eu.mihosoft.vmf;

import eu.mihosoft.vmf.testing.VMFTestShell;
import eu.mihosoft.vmftests.test1.vmfmodel.DaBean;
import eu.mihosoft.vmftests.test2.vmfmodel.Child;
import eu.mihosoft.vmftests.test2.vmfmodel.Named;
import eu.mihosoft.vmftests.test2.vmfmodel.Parent;
import org.junit.Test;

public class VMFGenerateRuns extends VMFTestShell {

    @Test
    public void testGetterSetterFeature() throws Throwable {
        setUp(DaBean.class);
        runScript("aDaBean.setName(\"testName\")");
        assertResult("aDaBean.getName()", "testName");

        String daBeanCode = findGeneratedCode("eu.mihosoft.vmftests.test1.DaBean");
        System.out.println(daBeanCode);

    }

    @Test
    public void testCloneFeature() throws Throwable {
        setUp(DaBean.class);
        runScript("aDaBean.setName(\"testName\")");
        runScript("cloneBean = aDaBean.clone()");
        assertResult("cloneBean.getName()", "testName");
    }

    @Test
    public void testReadOnlyFeature() throws Throwable {
        setUp(DaBean.class);
        runScript("roBean = aDaBean.asReadOnly()");
        assertExceptionOn("roBean.setName(\"test\")", "MissingMethodException");
    }

    @Test
    public void testContainerContainmentAddChild() throws Throwable {
        setUp(Named.class, Child.class, Parent.class);
        runScript("aParent.setName(\"Father\")");
        runScript("aParent.getChildren().add(aChild)");
        assertResult("aChild.getParent().getName()","Father");
        runScript("aChild.setName(\"Luke\")");
        assertResult("aParent.getChildren().get(0).getName()", "Luke");
        runScript("aParent.getChildren().clear()");
        assertResult("aChild.getParent()", null);
    }

    @Test
    public void testToStringFeatureSimple() throws Throwable {
        setUp(Named.class, Child.class, Parent.class);
        runScript("aParent.setName(\"Father\")");
        runScript("aParent.getChildren().add(aChild)");
        runScript("aChild.setName(\"Luke\")");
        assertResult("aParent.toString()","{\"@Type\":\"Parent\", [{\"@Type\":\"Child\", \"name\": \"Luke\"}], \"elements\": \"null\", \"name\": \"Father\"}");
    }

    @Test
    public void testDeepClone1() throws Throwable {
        setUp(Named.class, Child.class, Parent.class);
        runScript("aParent.setName(\"Father\")");
        runScript("aParent.getChildren().add(aChild)");
        runScript("aChild.setName(\"Luke\")");
        runScript("aCloneParent = aParent.vmf().content().deepCopy()");
        assertResult("java.util.Objects.equals(aParent, aCloneParent)", true);
        String str = (String) runScript("aParent.toString()");
        assertResult("aCloneParent.toString()", str);
    }


}
