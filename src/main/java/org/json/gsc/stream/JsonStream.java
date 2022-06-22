package org.json.gsc.stream;

import org.json.gsc.BigJsonValue;
import org.json.gsc.IBigJsonValueCallback;
import org.json.gsc.JSONArrayStream;
import org.json.gsc.JSONObjectStream;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.function.Function;

public class JsonStream implements Closeable {
    protected final int bufferSize = 8192;
    private final char endSign;
    private final BigJsonValue bigJsonValue;
    protected File file;
    protected Object constValue;
    // 是否是空文件
    protected boolean first = true;
    protected JsonInputStream jis;
    protected JsonOutputStream jos;
    // 是否最后一个字符需要替换
    private boolean written = false;

    public JsonStream(Object constValue, char endSign) {
        this.constValue = constValue;
        this.endSign = endSign;
        this.bigJsonValue = null;
    }

    public JsonStream(File file, char endSign, BigJsonValue bigJsonValue) {
        this.file = file;
        this.endSign = endSign;
        this.bigJsonValue = bigJsonValue;
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (file.length() > 0) {
                first = false;
            }
        }
    }

    // 读取流
    protected BufferedReader getReader() {
        if (jis == null) {
            try {
                jis = JsonInputStream.build(file != null ?
                        new FileInputStream(file) :
                        new ByteArrayInputStream(constValue.toString().getBytes(StandardCharsets.UTF_8)));
                jis.getReader().mark(bufferSize);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jis.getReader();
    }

    public void setReader(JsonInputStream reader) {
        jis = reader;
    }

    // 写入流
    protected BufferedWriter getWriter() {
        if (jos == null) {
            try {
                var fos = new FileOutputStream(file, true);
                if (file.length() > 0) {
                    fos.getChannel().write(ByteBuffer.wrap(new byte[]{(byte) '\n'}), file.length() - 1);
                }
                jos = JsonOutputStream.build(fos);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jos.getWriter();
    }

    public void setWriter(JsonOutputStream writer) {
        jos = writer;
    }

    public void setFirstFlag(boolean first) {
        this.first = first;
    }

    public void appendEnd() {
        if (written) {
            toWriter((out) -> {
                try {
                    if (!first) {
                        out.write(endSign);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            written = false;
        }
    }

    @Override
    public void close() {
        appendEnd();
        try {
            if (jis != null) {
                jis.close();
                jis = null;
            }
            if (jos != null) {
                jos.close();
                jos = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ===Writer===================================================
    protected void toWriter(Consumer<BufferedWriter> fn) {
        try {
            var bw = getWriter();
            written = true;
            fn.accept(bw);
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ===Reader===================================================
    protected <T> T toReader(Function<BufferedReader, T> fn) {
        T r;
        var br = getReader();
        try {
            if (bigJsonValue != null) {
                // 跳到文件内容区域
                br.skip(bigJsonValue.getStartPos());
                br.mark(bigJsonValue.getLength());
            }
            r = fn.apply(br);
            br.reset();

        } catch (IOException e) {
            // e.printStackTrace();
            r = null;
        }
        return r;
    }

    // =============================================================
    public JSONObjectStream deepClone() {
        JSONObjectStream jjs = new JSONObjectStream(file);
        jjs.setReader(this.jis);
        jjs.setWriter(this.jos);
        jjs.setFirstFlag(true);
        return jjs;
    }

    public <T> JSONArrayStream<T> deepCloneToJSONArrayStream() {
        JSONArrayStream<T> jas = new JSONArrayStream<>(file);
        jas.setReader(this.jis);
        jas.setWriter(this.jos);
        jas.setFirstFlag(true);
        return jas;
    }

    /**
     * @param bigJsonValue 超大Json值对象
     * @param fn           接收超大Json值对象的读取流的回调函数
     */
    public void getBigJsonValueStream(BigJsonValue bigJsonValue, IBigJsonValueCallback fn) {
        try (InputStreamReader r = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            try (BufferedReader br = new BufferedReader(r)) {
                br.skip(bigJsonValue.getStartPos());
                br.mark(bigJsonValue.getLength());
                fn.run(br, bigJsonValue);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String toJSONString() {
        return toReader((in) -> {
            try {
                in.reset();
                StringBuilder sb = new StringBuilder();
                int c;
                do {
                    c = in.read();
                    if (c != -1) {
                        sb.append((char) c);
                    }
                } while (c != -1);
                return sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
    }
}
