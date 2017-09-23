package eu.mihosoft.vmf;

import eu.mihosoft.vmf.testing.VMFTestShell;
import eu.mihosoft.vmftests.test1.vmfmodel.DaBean;
import org.junit.Test;

public class VMFGenerateRuns {


    VMFTestShell sh = new VMFTestShell();

    @Test
    public void testGetterSetterFeature() throws Throwable {
        sh.setUp(DaBean.class);
        sh.runScript("aDaBean.setName(\"testName\")");
        sh.assertResult("aDaBean.getName()", "testName");

        String daBeanCode = sh.findGeneratedCode("eu.mihosoft.vmftests.test1.DaBean");
        System.out.println(daBeanCode);

        sh.tearDown();
    }

    @Test
    public void testCloneFeature() throws Throwable {
        sh.setUp(DaBean.class);
        sh.runScript("aDaBean.setName(\"testName\")");
        sh.runScript("cloneBean = aDaBean.clone()");
        sh.assertResult("cloneBean.getName()", "testName");
        sh.tearDown();
    }

    @Test
    public void testReadOnlyFeature() throws Throwable {
        sh.setUp(DaBean.class);
        sh.runScript("roBean = aDaBean.asReadOnly()");
        sh.assertExceptionOn("roBean.setName(\"test\")", "MissingMethodException");
        sh.tearDown();
    }

}
