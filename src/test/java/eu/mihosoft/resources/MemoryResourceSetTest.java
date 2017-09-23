package eu.mihosoft.resources;

import org.junit.Assert;
import org.junit.Test;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class MemoryResourceSetTest {

    @Test
    public void testMemoryResourceSet() throws Exception {
        MemoryResourceSet memSet = new MemoryResourceSet();
        MemoryResource res = (MemoryResource) memSet.open("dash");
        PrintWriter out = res.open();
        out.println("foo");

        Assert.assertEquals("dash : foo\n", memSet.asString());
    }

    @Test
    public void testMemoryResourceSetPrintStats() throws IOException {
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
