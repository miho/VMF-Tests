package eu.mihosoft.resources;

import eu.mihosoft.vmf.core.Resource;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class MemoryResource implements Resource {

    CharArrayWriter mem = new CharArrayWriter();

    @Override
    public PrintWriter open() throws IOException {
        return new PrintWriter(mem);
    }

    @Override
    public void close() throws IOException {
        mem.close();
    }

    String asString()
    {
        return mem.toString();
    }
}
