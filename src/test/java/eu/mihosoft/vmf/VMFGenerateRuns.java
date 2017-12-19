package eu.mihosoft.vmf;

import eu.mihosoft.vmf.testing.VMFTestShell;
import eu.mihosoft.vmftests.delegationtest.vmfmodel.DelegationTestClass;
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
        assertResult("aParent.toString()","{\"@type\":\"Parent\", [{\"@type\":\"Child\", \"name\": \"Luke\"}], \"elements\": \"null\", \"name\": \"Father\"}");
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

    @Test
    public void testMethodDelegation() throws Throwable {
        addCode("eu.mihosoft.vmftests.delegationtest.MyBehavior",

                "\n" +
                "package eu.mihosoft.vmftests.delegationtest;\n" +
                "public class MyBehavior implements eu.mihosoft.vmf.runtime.core.DelegatedBehavior<DelegationTestClass>{\n" +
                "\n" +
                "    private eu.mihosoft.vmftests.delegationtest.DelegationTestClass caller;\n" +
                "    private boolean constructorCalled;\n" +
                "\n" +
                "    @Override\n" +
                "    public void setCaller(DelegationTestClass caller) {\n" +
                "        this.caller = caller;\n" +
                "    }\n" +
                "\n" +
                "    public boolean nameStartsWith(String string) {\n" +
                "        if(string==null) {\n" +
                "            return false;\n" +
                "        }\n" +
                "\n" +
                "        return caller.getName().startsWith(string);\n" +
                "    }\n" +
                "\n" +
                "    public void onDelegationTestClassInstantiated() {\n" +
                "        constructorCalled = true;\n" +
                "    }\n" +
                "\n" +
                "    public boolean constructorCalled() {\n" +
                "        return this.constructorCalled;\n" +
                "    }\n" +
                "}\n");
        try {
            setUp(DelegationTestClass.class);
        } catch(Exception ex) {
            String code = findGeneratedCode("eu.mihosoft.vmftests.delegationtest.DelegationTestClass");
            System.out.println(code);
            throw ex;
        }

        assertResult("aDelegationTestClass.constructorCalled()", true);
        runScript("aDelegationTestClass.setName(\"Father\")");
        assertResult("aDelegationTestClass.nameStartsWith(\"F\")", true);
        assertResult("aDelegationTestClass.nameStartsWith(\"G\")", false);

        String code = findGeneratedCode("eu.mihosoft.vmftests.delegationtest.DelegationTestClass");
        System.out.println(code);
    }


}
