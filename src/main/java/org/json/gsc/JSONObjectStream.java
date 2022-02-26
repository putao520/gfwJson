package org.json.gsc;

import org.json.gsc.parser.ContainerFactory;
import org.json.gsc.parser.JSONParser;
import org.json.gsc.parser.ParseException;
import org.json.gsc.stream.JsonStream;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 文件流方式操作JSON文件，不提供完整的JSON接口
 */
public class JSONObjectStream extends JsonStream implements IJSONObject<JSONObjectStream> {

    public JSONObjectStream(File file) {
        super(file, '}', null);
    }

    public JSONObjectStream(File file, BigJsonValue bigJsonValue) {
        super(file, '}', bigJsonValue);
    }

    public JSONObjectStream(Object constValue) {
        super(constValue, '}');
    }

    public void putKey(String key, Writer out) {
        try {
            if (first) {
                out.write('{');
                first = false;
            } else {
                out.write(',');
            }
            out.write('\"');
            out.write(JSONObject.escape(String.valueOf(key)));
            out.write('\"');
            out.write(':');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JSONObjectStream putJson(String key, Consumer<JSONObjectStream> fn) {
        if (!has(key)) {
            toWriter((out) -> {
                if (fn != null) {
                    putKey(key, out);
                    var s = this.deepClone();
                    fn.accept(s);
                    s.appendEnd();
                }
            });
        }
        return this;
    }

    public <T> JSONObjectStream putJsonArray(String key, Consumer<JSONArrayStream<T>> fn) {
        if (!has(key)) {
            toWriter((out) -> {
                if (fn != null) {
                    putKey(key, out);
                    var s = this.<T>deepCloneToJSONArrayStream();
                    fn.accept(s);
                    s.appendEnd();
                }
            });
        }
        return this;
    }

    public JSONObjectStream put(String key, Object value) {
        // if (!has(key)) {
        toWriter((out) -> {
            try {
                putKey(key, out);
                JSONValue.writeJSONString(value, out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        // }
        return this;
    }

    public JSONObjectStream putAll(Map<String, Object> m) {
        for (Map.Entry<String, Object> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public void forEach(BiConsumer<String, Object> fn) {
        toReader((in) -> {
            JSONParser parser = new JSONParser(bufferSize);
            // 利用溢出时判断是否包含目标key,包含跳出解析（否则重用Map缓存）
            parser.onMapOverflow((map) -> {
                map.forEach(fn);
                return false;
            });
            try {
                ((JSONObject) parser.parse(in, (ContainerFactory) null, true)).forEach(fn);
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public Object get(String key) {
        // 重置
        return toReader((in) -> {
            JSONObject rObject = JSONObject.build();
            JSONParser parser = new JSONParser(bufferSize);
            // 利用溢出时判断是否包含目标key,包含跳出解析（否则重用Map缓存）
            parser.onMapOverflow((map) -> map.get(key) != null);
            try {
                rObject.put((JSONObject) parser.parse(in, (ContainerFactory) null, true));
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
            return rObject.get(key);
        });
    }

    public Object get(String key, Object defaultValue) {
        Object value = get(key);
        return value == null ? defaultValue : value;
    }

    // 在大文件模式下 has 开销与get 开销相同
    public boolean has(String key) {
        return get(key) != null;
    }

    public String getString(String key) {
        return getString(key, null);
    }

    public int getInt(String key) {
        return JSONValue.IntValue(get(key));
    }

    public long getLong(String key) {
        return JSONValue.LongValue(get(key));
    }

    public double getDouble(String key) {
        return JSONValue.DoubleValue(get(key));
    }

    public float getFloat(String key) {
        return JSONValue.FloatValue(get(key));
    }

    public boolean getBoolean(String key) {
        return JSONValue.BooleanValue(get(key));
    }

    public JSONObject getJson(String key) {
        return JSONValue.JsonValue(get(key));
    }

    public JSONObjectStream getJsonStream(String key) {
        Object value = get(key);
        if (value instanceof BigJsonValue bV) {
            return new JSONObjectStream(file, bV);
        } else {
            return new JSONObjectStream(value);
        }
    }

    public <T> JSONArrayStream<T> getJsonArrayStream(String key) {
        Object value = get(key);
        if (value instanceof BigJsonValue bV) {
            return new JSONArrayStream<>(file, bV);
        } else {
            return new JSONArrayStream<>(value);
        }
    }

    public <T> JSONArray<T> getJsonArray(String key) {
        return JSONValue.JsonArrayValue(get(key));
    }

    public String getString(String key, IBigJsonValueCallback fn) {
        Object value = get(key);
        if (key.equals("_id") && value instanceof JSONObject && ((JSONObject) value).has("$oid")) {
            value = ((JSONObject) value).getString("$oid");
            return value == null ? "" : value.toString();
        } else if (value instanceof String str) {
            return str;
        } else if (value instanceof BigJsonValue) {
            if (fn != null) {
                getBigJsonValueStream((BigJsonValue) value, fn);
            } else {
                throw new RuntimeException("JsonValue is a bigJsonValue,but fn is null");
            }
        }
        return null;
    }
}
