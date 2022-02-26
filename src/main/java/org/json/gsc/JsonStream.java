package org.json.gsc;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.function.Function;

public class JsonStream implements Closeable {
    protected final int bufferSize = 8192;
    private char endSign;
    protected File file;
    // 是否是空文件
    protected boolean first = true;
    // 是否最后一个字符需要替换
    protected boolean changeLast = false;
    private boolean written = false;
    private BigJsonValue bigJsonValue;

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
                changeLast = true;
            }
        }
    }

    @Override
    public void close() {
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
        }
    }

    // ===Writer===================================================
    protected void toWriter(Consumer<Writer> fn) {
        try (FileOutputStream fos = new FileOutputStream(file, true)) {
            if (changeLast) {
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                randomAccessFile.seek(randomAccessFile.length() - 1);
                randomAccessFile.write(' ');
                randomAccessFile.close();
                changeLast = false;
            }
            try (OutputStreamWriter w = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
                try (BufferedWriter bw = new BufferedWriter(w)) {
                    written = true;
                    fn.accept(bw);
                    bw.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ===Reader===================================================
    protected <T> T toReader(Function<Reader, T> fn) {
        try (InputStreamReader r = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            try (BufferedReader br = new BufferedReader(r)) {
                if (bigJsonValue != null) {
                    // 跳到文件内容区域
                    br.skip(bigJsonValue.getStartPos());
                    br.mark(bigJsonValue.getLength());
                }
                return fn.apply(br);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
}
