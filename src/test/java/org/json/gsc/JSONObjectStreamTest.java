package org.json.gsc;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;

public class JSONObjectStreamTest extends TestCase {
    public void testPut() throws IOException {
        File file = new File("jsonTest.json");
        if (file.exists()) {
            file.delete();
        }
        // 创建
        JSONObjectStream stream = new JSONObjectStream(file);
        try (stream) {
            stream.put("foo2", "bar");
            stream.putJson("bar2", jsonStream -> {
                jsonStream.put("foo", "bar22222");
            });
            stream.put("baz2", 312);
        }
        try (stream) {
            String s = stream.toJSONString();
            System.out.println(s);
            assertEquals("{\"foo2\":\"bar\",\"bar2\":{\"foo\":\"bar22222\"},\"baz2\":312}", s);
        }
        // 补充
        try (stream) {
            stream.put("_foo2", "bar");
            stream.put("_foo2", "bar");
            stream.<String>putJsonArray("_bar2", jsonStream -> jsonStream.add("_foo").add("bar22222"));
            stream.put("_baz2", 312);
        }
        try (stream) {
            String s = JSONObject.toJSON(stream.toJSONString()).toString();
            System.out.println(s);
            assertEquals("{\"bar2\":{\"foo\":\"bar22222\"},\"_foo2\":\"bar\",\"_bar2\":[\"_foo\",\"bar22222\"],\"baz2\":312,\"foo2\":\"bar\",\"_baz2\":312}", s);
        }
        // 查找
        try (stream) {
            String s = stream.getString("_foo2");
            System.out.println(s);
            assertEquals("bar", s);
        }
    }
}
