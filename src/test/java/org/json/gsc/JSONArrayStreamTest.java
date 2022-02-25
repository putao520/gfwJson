package org.json.gsc;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;

public class JSONArrayStreamTest extends TestCase {
    public void testAdd() {
        JSONArrayStream<Object> stream = new JSONArrayStream<>(new File("jsonArrayTest.json"));
        try (stream) {
            /*
            stream.add(1);
            stream.add("abcdefghljk1231223");
            stream.add(3);
             */
            var v = stream.getString(1, (br, bv) -> {
                String s = null;
                try {
                    char[] b = new char[bv.getLength()];
                    br.read(b, 0, b.length);
                    s = new String(b);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(s);
            });
        }
    }
}
