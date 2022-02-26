package org.json.gsc;

import org.json.gsc.parser.ContainerFactory;
import org.json.gsc.parser.JSONParser;
import org.json.gsc.parser.ParseException;

import java.io.File;
import java.io.IOException;

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

    public JSONObjectStream put(String key, Object value) {
        if (!has(key)) {
            toWriter((out) -> {
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
                    JSONValue.writeJSONString(value, out);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        return this;
    }

    public Object get(String key) {
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
