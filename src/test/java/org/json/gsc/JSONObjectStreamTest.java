package org.json.gsc;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;

public class JSONObjectStreamTest extends TestCase {
    public void testPut() throws IOException {
        // JSONObject.toJSON(":1,\"b\":2,\"c\":\"ok\"}");
        // JSONObject.toJSON("{\"a\":1,\"b\":2,\"c");


        JSONObjectStream stream = new JSONObjectStream(new File("jsonTest.json"));
        try (stream) {

            stream.put("foo2", "bar");
            stream.put("bar2", "foo");
            stream.put("baz2", 312);

            // System.out.println(stream.get("baz"));
        }
    }
}
