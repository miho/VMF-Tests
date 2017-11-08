package eu.mihosoft.vmf.testing;


import eu.mihosoft.vmf.VMF;
import eu.mihosoft.vmf.core.TypeUtil;
import eu.mihosoft.vmf.core.io.MemoryResource;
import eu.mihosoft.vmf.core.io.MemoryResourceSet;
import eu.mihosoft.vmf.core.io.Resource;
import groovy.lang.GroovyShell;
import org.junit.After;
import org.junit.Assert;
import org.mdkt.compiler.InMemoryJavaCompiler;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class VMFTestShell {
    GroovyShell shell;
    MemoryResourceSet codeField;

    /**
     * Adds manally implemented code, such as delegated behavior. To add delegation classes, this method must be called
     * prior to setup. Otherwise the generated code does not contain the delegated behavior and will fail to compile.
     * @param className name of the class to add
     * @param code code of the class to add
     */
    public void addCode(String className, String code) {
        // register code, e.g., delegation classes which are necessary for setup
        Resource res = getCodeField().open(TypeUtil.computeFileNameFromJavaFQN(className));
        try {
            res.open().append(code).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    MemoryResourceSet getCodeField() {
        if(codeField==null) {
            codeField = new MemoryResourceSet();
        }

        return codeField;
    }

    public void setUp(Class... classes) throws Throwable {
        VMF.generate(getCodeField(), classes);
        InMemoryJavaCompiler compiler = InMemoryJavaCompiler.newInstance().ignoreWarnings();
        for (Map.Entry<String, MemoryResource> entry : getCodeField().getMemSet().entrySet()) {
            compiler.addSource(entry.getKey().replace('/','.').substring(0,entry.getKey().length()-5), entry.getValue().asString());
        }

        compiler.compileAll();
        shell = new GroovyShell(compiler.getClassloader());
        for (Class cls : classes) {
            shell.setVariable("a" + cls.getSimpleName(), vmfNewInstance(compiler.getClassloader(), cls).getValue());
        }
    }

    public String findGeneratedCode(String resource) {
        resource = resource.replace('.','/')+".java";
        if (getCodeField().getMemSet().containsKey(resource) ){
            return getCodeField().getMemSet().get(resource).asString();
        } else {
            String msg = "Unknown Resource '" + resource + ", try one of the following:\n";
            for (String key : getCodeField().getMemSet().keySet()) {
                msg = msg.concat("- ").concat(key).concat("\n");
            }
            throw new IllegalArgumentException(msg);
        }
    }

    public String getGeneratedCode() {
        return getCodeField().asString();
    }

    public Object runScript(String scriptlet) throws Throwable {
        return shell.evaluate(scriptlet);
    }

    public void assertResult(String scriptlet, Object expectedResult) {
        Object actualResult = shell.evaluate(scriptlet);
        Assert.assertEquals(expectedResult, actualResult);
    }

    public void assertExceptionOn(String scriptlet, String exceptionType) {
        boolean redFlag = false;
        try {
            shell.evaluate(scriptlet);
            redFlag = true;
        } catch (Throwable t) {
            Assert.assertEquals(exceptionType, t.getClass().getSimpleName());
        }
        if (redFlag) Assert.fail("Expected exception not thrown: " + exceptionType);
    }

    static Map.Entry<Class, Object> vmfNewInstance(ClassLoader cl, Class externalTemplate) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        String fqn;
        fqn = externalTemplate.getName().replace(".vmfmodel", ".impl").concat("Impl");
        Class implClass = cl.loadClass(fqn);
        Constructor c = implClass.getConstructor();
        c.setAccessible(true);
        Object instance = c.newInstance();

        fqn = externalTemplate.getName().replace(".vmfmodel", "");
        Class pubInterface = cl.loadClass(fqn);
        return new AbstractMap.SimpleEntry<Class, Object>(pubInterface, instance);
    }

    @After
    public void tearDown() {
        shell = null;
        codeField = null;
    }
}
