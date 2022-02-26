package org.json.gsc;

import org.json.gsc.parser.ContainerFactory;
import org.json.gsc.parser.JSONParser;
import org.json.gsc.parser.ParseException;
import org.json.gsc.stream.JsonStream;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.function.Consumer;

public class JSONArrayStream<T> extends JsonStream implements IJSONArray<JSONArrayStream<T>> {
    public JSONArrayStream(File file) {
        super(file, ']', null);
    }

    public JSONArrayStream(File file, BigJsonValue bigJsonValue) {
        super(file, ']', bigJsonValue);
    }

    public JSONArrayStream(Object constValue) {
        super(constValue, ']');
    }

    private void prefixItem(Writer out) {
        try {
            if (first) {
                out.write('[');
                first = false;
            } else {
                out.write(',');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JSONArrayStream<T> addJson(Consumer<JSONObjectStream> fn) {
        toWriter((out) -> {
            if (fn != null) {
                prefixItem(out);
                var s = this.deepClone();
                fn.accept(s);
                s.appendEnd();
            }
        });
        return this;
    }

    public <S> JSONArrayStream<T> addJsonArray(Consumer<JSONArrayStream<S>> fn) {
        toWriter((out) -> {
            if (fn != null) {
                prefixItem(out);
                var s = this.<S>deepCloneToJSONArrayStream();
                fn.accept(s);
                s.appendEnd();
            }
        });
        return this;
    }

    public JSONArrayStream<T> add(T value) {
        toWriter((out) -> {
            try {
                prefixItem(out);
                JSONValue.writeJSONString(value, out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return this;
    }

    public JSONArrayStream<T> addAll(Collection<? extends T> c) {
        for (T t : c) {
            add(t);
        }
        return this;
    }

    public void forEach(Consumer<T> fn) {
        toReader((in) -> {
            JSONParser parser = new JSONParser(bufferSize);
            // 利用溢出时判断是否包含目标key,包含跳出解析（否则重用Map缓存）
            parser.onListOverflow((list) -> {
                list.forEach(fn);
                return false;
            });
            try {
                ((JSONArray<T>) parser.parse(in, (ContainerFactory) null, true)).forEach(fn);
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public JSONObjectStream getJsonStream(int idx) {
        Object value = get(idx);
        if (value instanceof BigJsonValue bV) {
            return new JSONObjectStream(file, bV);
        } else {
            return new JSONObjectStream(value);
        }
    }

    public <T> JSONArrayStream<T> getJsonArrayStream(int idx) {
        Object value = get(idx);
        if (value instanceof BigJsonValue bV) {
            return new JSONArrayStream<>(file, bV);
        } else {
            return new JSONArrayStream<>(value);
        }
    }

    public T get(int idx) {
        return toReader((in) -> {
            JSONArray<T> rArray = JSONArray.build();
            JSONParser parser = new JSONParser(bufferSize);
            // JSONParser parser = new JSONParser(10);
            // 利用溢出时判断是否包含目标key,包含跳出解析（否则重用Map缓存）
            parser.onListOverflow((list) -> {
                int pl = parser.getPreloadListSize();
                return (idx > pl && idx < (pl + list.size()));
            });
            try {
                rArray.addAll((JSONArray<T>) parser.parse(in, (ContainerFactory) null, true));
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
            int _idx = idx - parser.getPreloadListSize();
            return rArray.get(_idx);
        });
    }

    public String getString(int idx) {
        return getString(idx, null);
    }

    public String getString(int idx, IBigJsonValueCallback fn) {
        Object value = get(idx);
        if (value instanceof BigJsonValue) {
            if (fn != null) {
                getBigJsonValueStream((BigJsonValue) value, fn);
            } else {
                throw new RuntimeException("JsonValue is a bigJsonValue,but fn is null");
            }
        } else if (value instanceof String str) {
            return str;
        } else {
            try {
                return value.toString();
            } catch (Exception e) {
                try {
                    return String.valueOf(value);
                } catch (Exception e2) {
                    return null;
                }
            }
        }
        return null;
    }

    public int getInt(int idx) {
        return JSONValue.IntValue(get(idx));
    }

    public double getDouble(int idx) {
        return JSONValue.DoubleValue(get(idx));
    }

    public boolean getBoolean(int idx) {
        return JSONValue.BooleanValue(get(idx));
    }

    public long getLong(int idx) {
        return JSONValue.LongValue(get(idx));
    }

    public float getFloat(int idx) {
        return JSONValue.FloatValue(get(idx));
    }

    public JSONObject getJson(int idx) {
        return JSONValue.JsonValue(get(idx));
    }

    public <T> JSONArray<T> getJsonArray(int idx) {
        return JSONValue.JsonArrayValue(get(idx));
    }
}
