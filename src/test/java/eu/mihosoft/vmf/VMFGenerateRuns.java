package eu.mihosoft.vmf;

import eu.mihosoft.vmf.testing.VMFTestShell;
import eu.mihosoft.vmftests.delegationtest.vmfmodel.DelegationTestClass;
import eu.mihosoft.vmftests.reflectiontest.vmfmodel.Node;
import eu.mihosoft.vmftests.reflectiontest.vmfmodel.ReflectionTest;
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
        } catch(Throwable ex) {
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

    @Test
    public void testReflectionSetUnsetPrimitiveWithCompiletimeDefault() throws Throwable {

        try {

            setUp(ReflectionTest.class, Node.class);

            // id must be equal to it's reflection property
            assertResult("aReflectionTest.getId()==aReflectionTest.vmf().reflect().propertyByName(\"id\").get().get()", true);

            // id must not be set
            assertResult("aReflectionTest.vmf().reflect().propertyByName(\"id\").get().isSet()", false);

            // default value of id is 23
            assertResult("aReflectionTest.getId()==23", true);

        } catch( Throwable tr ) {
            String code = findGeneratedCode("eu.mihosoft.vmftests.reflectiontest.impl.ReflectionTestImpl");
            System.err.println(code);
            throw tr;
        }

    }

    @Test
    public void testReflectionSetUnsetPrimitiveWithRuntimeDefault() throws Throwable {

        try {

            setUp(ReflectionTest.class, Node.class);

            // id2 is not set as well
            assertResult("aReflectionTest.vmf().reflect().propertyByName(\"id2\").get().isSet()", false);

            // if we set id2 ...
            runScript("aReflectionTest.setId2(\"test 123\");");

            // ... it should be set
            assertResult("aReflectionTest.vmf().reflect().propertyByName(\"id2\").get().isSet()", true);

            // if we set it to 'null' (the default value) ...
            runScript("aReflectionTest.setId2(null);");

            // ... it should not be set
            assertResult("aReflectionTest.vmf().reflect().propertyByName(\"id2\").get().isSet()", false);

            // we should check per instance default values:
            runScript("aReflectionTest.vmf().reflect().propertyByName(\"id2\").get().setDefault(\"abc\")");

            // the default value should be updated, so it should not be set
            assertResult("aReflectionTest.vmf().reflect().propertyByName(\"id2\").get().isSet()", false);

            // ... but the value should be "abc" instead of "null"
            assertResult("\"abc\".equals(aReflectionTest.getId2())", true);


        } catch( Throwable tr ) {
            String code = findGeneratedCode("eu.mihosoft.vmftests.reflectiontest.impl.ReflectionTestImpl");
            System.err.println(code);
            throw tr;
        }

    }


    @Test
    public void testReflectionSetUnsetCollectionWithCompiletimeDefault() throws Throwable {

        try {

            setUp(ReflectionTest.class, Node.class);

            // values is not set
            assertResult("aReflectionTest.vmf().reflect().propertyByName(\"values\").get().isSet()", false);

            // (but is not null because of its default value, size==3)
            assertResult("aReflectionTest.getValues().size()==3", true);

        } catch( Throwable tr ) {
            String code = findGeneratedCode("eu.mihosoft.vmftests.reflectiontest.impl.ReflectionTestImpl");
            System.err.println(code);
            throw tr;
        }

    }

    @Test
    public void testReflectionSetUnsetContainmentProperties() throws Throwable {

        try {

            setUp(ReflectionTest.class, Node.class);

            // containment properties cannot be set. we expect unset as default:
            assertResult("aNode.vmf().reflect().propertyByName(\"parent\").get().isSet()", false);


            // containment properties cannot be set. we expect an exception:
            assertExceptionOn("aNode.vmf().reflect().propertyByName(\"parent\").get().set(aNode)",
                    "RuntimeException");

            // containment properties cannot be set. we expect an exception (also for default values):
            assertExceptionOn("aNode.vmf().reflect().propertyByName(\"parent\").get().setDefault(aNode)",
                    "RuntimeException");

        } catch( Throwable tr ) {
            String code = findGeneratedCode("eu.mihosoft.vmftests.reflectiontest.impl.ReflectionTestImpl");
            System.err.println(code);
            throw tr;
        }

    }

    @Test
    public void testReflectionSetUnsetReadOnlyProperties() throws Throwable {

        try {

            setUp(ReflectionTest.class, Node.class);

            // obtain a readonly reference of the object:
            runScript("aReflectionTestRO = aReflectionTest.asReadOnly()");



            // containment properties cannot be set. we expect an exception:
            assertExceptionOn("aReflectionTestRO.vmf().reflect().propertyByName(\"id\").get().set(24)",
                    "RuntimeException");

            // containment properties cannot be set. we expect an exception (also for default values):
            assertExceptionOn("aReflectionTestRO.vmf().reflect().propertyByName(\"id\").get().setDefault(25)",
                    "RuntimeException");

        } catch( Throwable tr ) {
            String code = findGeneratedCode("eu.mihosoft.vmftests.reflectiontest.impl.ReadOnlyReflectionTestImpl");
            System.err.println(code);
            throw tr;
        }

    }


}
