package org.json.gsc.stream;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class JsonInputStream implements Closeable {
    private BufferedReader reader;
    private InputStreamReader r;
    private InputStream fis;

    private JsonInputStream(InputStream fis) {
        this.fis = fis;
        r = new InputStreamReader(fis, StandardCharsets.UTF_8);
        reader = new BufferedReader(r);
    }

    public static JsonInputStream build(InputStream fis) {
        return new JsonInputStream(fis);
    }

    public void close() throws IOException {
        if (reader != null) {
            reader.close();
            reader = null;
        }
        if (r != null) {
            r.close();
            r = null;
        }
        if (fis != null) {
            fis.close();
            fis = null;
        }
    }

    public BufferedReader getReader() {
        return reader;
    }
}
