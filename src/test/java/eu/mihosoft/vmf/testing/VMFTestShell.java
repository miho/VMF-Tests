package eu.mihosoft.vmf.testing;

import eu.mihosoft.resources.MemoryResource;
import eu.mihosoft.resources.MemoryResourceSet;
import eu.mihosoft.vmf.VMF;
import groovy.lang.GroovyShell;
import org.junit.After;
import org.junit.Assert;
import org.mdkt.compiler.InMemoryJavaCompiler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.AbstractMap;
import java.util.Hashtable;
import java.util.Map;

public class VMFTestShell {
    GroovyShell shell;
    MemoryResourceSet codeField;

    public void setUp(Class... classes) throws Throwable {
        codeField = new MemoryResourceSet();
        VMF.generate(codeField, classes);
        InMemoryJavaCompiler compiler = InMemoryJavaCompiler.newInstance().ignoreWarnings();
        for (Map.Entry<String, MemoryResource> entry : codeField.getMemSet().entrySet()) {
            compiler.addSource(entry.getKey(), entry.getValue().asString());
        }
        compiler.compileAll();
        shell = new GroovyShell(compiler.getClassloader());
        for (Class cls : classes) {
            shell.setVariable("a" + cls.getSimpleName(), vmfNewInstance(compiler.getClassloader(), cls).getValue());
        }
    }

    public String findGeneratedCode(String resource) {
        if (codeField.getMemSet().containsKey(resource)) {
            return codeField.getMemSet().get(resource).asString();
        } else {
            String msg = "Unknown Resource '" + resource + ", try one of the following:\n";
            for (String key : codeField.getMemSet().keySet()) {
                msg = msg.concat("- ").concat(key).concat("\n");
            }
            throw new IllegalArgumentException(msg);
        }
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
