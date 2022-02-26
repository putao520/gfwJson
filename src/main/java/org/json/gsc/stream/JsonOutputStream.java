package org.json.gsc.stream;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class JsonOutputStream implements Closeable {
    private BufferedWriter writer;
    private OutputStreamWriter w;
    private OutputStream fos;

    private JsonOutputStream(OutputStream fos) {
        this.fos = fos;
        w = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
        writer = new BufferedWriter(w);
    }

    public static JsonOutputStream build(OutputStream fos) {
        return new JsonOutputStream(fos);
    }

    public void close() throws IOException {
        if (writer != null) {
            writer.close();
            writer = null;
        }
        if (w != null) {
            w.close();
            w = null;
        }
        if (fos != null) {
            fos.close();
            fos = null;
        }
    }

    public BufferedWriter getWriter() {
        return writer;
    }
}
