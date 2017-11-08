package eu.mihosoft.resources;

import eu.mihosoft.vmf.core.io.MemoryResource;
import eu.mihosoft.vmf.core.io.MemoryResourceSet;
import org.junit.Assert;
import org.junit.Test;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class MemoryResourceSetTest {

    @Test
    public void testMemoryResourceSet() throws Exception {
        System.setProperty("line.separator","\n");
        MemoryResourceSet memSet = new MemoryResourceSet();
        MemoryResource res = (MemoryResource) memSet.open("dash");
        PrintWriter out = res.open();
        out.println("foo");

        Assert.assertEquals("\n" +
                "--------------------------------------------------------------------------------\n" +
                "ENTRY dash\n" +
                "--------------------------------------------------------------------------------\n" +
                "foo\n", memSet.asString());
    }

    @Test
    public void testMemoryResourceSetPrintStats() throws IOException {
        System.setProperty("line.separator","\n");
        MemoryResourceSet memSet = new MemoryResourceSet();
        MemoryResource res = (MemoryResource) memSet.open("dash");
        PrintWriter out = res.open();
        out.println("foo");

        CharArrayWriter stats = new CharArrayWriter();
        memSet.printStats(new PrintWriter(stats));
        Assert.assertEquals("Resource count: 1\n" +
                "Resource urls: \n" +
                "- dash (4)\n", stats.toString());
    }


}
