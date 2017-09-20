package eu.mihosoft.vmf;

import eu.mihosoft.resources.MemoryResourceSet;
import eu.mihosoft.vmftests.test1.vmfmodel.DaBean;
import org.junit.Assert;
import org.junit.Test;

import java.io.PrintWriter;

public class VMFGenerateRuns {

    @Test
    public void testGeneration1() throws Throwable
    {
        MemoryResourceSet res = new MemoryResourceSet();
        VMF.generate(res, DaBean.class);
        PrintWriter out = new PrintWriter(System.out);
        res.printStats(out);
        out.flush();
        Assert.assertEquals("", res.asString());
    }


}
