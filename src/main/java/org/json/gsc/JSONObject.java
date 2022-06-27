/*
 * $Id: JSONObject.java,v 1.1 2006/04/15 14:10:48 platform Exp $
 * Created on 2006-4-10
 */
package org.json.gsc;

import org.json.gsc.parser.JSONParser;

import java.io.IOException;
import java.io.Serial;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class JSONObject extends HashMap<String, Object> implements Map<String, Object>, JSONAware, JSONStreamAware, IJSONObject<JSONObject> {

    @Serial
    private static final long serialVersionUID = -503443796854799292L;

    private final HashMap<String, String> mapTable = new HashMap<>();

    public JSONObject() {
        super();
    }

    public JSONObject(Map map) {
        super(map);
    }

    public JSONObject(String key, Object value) {
        super();
        super.put(key, value);
    }

    public static JSONObject build() {
        return new JSONObject();
    }

    public static JSONObject build(Map map) {
        return new JSONObject(map);
    }

    public static JSONObject build(String key, Object value) {
        return new JSONObject(key, value);
    }

    public static JSONObject build(String json) {
        return toJSON(json);
    }

    public static void writeJSONString(Map map, Writer out, int format) throws IOException {
        if (map == null) {
            out.write("null");
            return;
        }

        boolean first = true;
        Iterator iter = map.entrySet().iterator();

        String format_end = format == 1 ? "\r\n" : "";

        out.write('{' + format_end);
        while (iter.hasNext()) {
            if (first)
                first = false;
            else
                out.write(',' + format_end);
            Map.Entry entry = (Map.Entry) iter.next();
            out.write('\"');
            out.write(escape(String.valueOf(entry.getKey())));
            out.write('\"');
            out.write(':');
            JSONValue.writeJSONString(entry.getValue(), out);
        }
        out.write('}' + format_end);
    }

    /**
     * Convert a map to JSON text. The result is a JSON object.
     * If this map is also a JSONAware, JSONAware specific behaviours will be omitted at this top level.
     *
     * @param map    map
     * @param format format
     * @return JSON text, or "null" if map is null.
     * @see org.json.gsc.JSONValue#toJSONString(Object)
     */
    public static String toJSONString(Map map, int format) {
        final StringWriter writer = new StringWriter();

        try {
            writeJSONString(map, writer, format);
            return writer.toString();
        } catch (IOException e) {
            // This should never happen with a StringWriter
            throw new RuntimeException(e);
        }
    }

    public static String toPrettyJSONString(Map map) {
        return toJSONString(map, 1);
    }

    public static String toJSONString(Map map) {
        return toJSONString(map, 0);
    }

    public static String toString(String key, Object value) {
        StringBuffer sb = new StringBuffer();
        sb.append('\"');
        if (key == null)
            sb.append("null");
        else
            JSONValue.escape(key, sb);
        sb.append('\"').append(':');

        sb.append(JSONValue.toJSONString(value));

        return sb.toString();
    }

    public static String escape(String s) {
        return JSONValue.escape(s);
    }

    private static String toUpperFirstChar(String str) {
        char[] arr = str.toLowerCase(Locale.ROOT).toCharArray();
        arr[0] -= 32;
        return String.valueOf(arr);
    }

    public static final JSONObject toJSON(String str) {
        JSONObject rObject;
        JSONParser parser = new JSONParser();
        try {
            rObject = (JSONObject) parser.parse(str);
        } catch (Exception e) {
            rObject = null;
        }
        if (rObject != null && rObject.size() == 0) {
            rObject = null;
        }
        return rObject;
    }

    public static boolean isInvalided(JSONObject object) {
        return object == null || object.size() == 0;
    }

    public static <T extends HashMap> JSONObject convert(T in) {
        JSONObject myJson = new JSONObject();
        for (Object key : in.keySet()) {
            myJson.put((String) key, in.get(key));
        }
        return myJson;
    }

    private static Object toMapperJson(Object o, Field field, boolean canArray) {
        try {
            Class<?> type = field.getType();
            Object val = field.get(o);
            if (val == null) {
                return null;
            }
            // array
            if (type.isArray()) {
                JSONArray arr = JSONArray.build();
                Object[] _val = (Object[]) val;
                for (Object v : _val) {
                    arr.put(toMapperJsonSingle(v, type.getComponentType(), false));
                }
                return arr;
            } else {
                return toMapperJsonSingle(val, type, canArray);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Field [" + field.getName() + "] IllegalAccess");
        }
    }

    private static boolean isJsonType(Object o) {
        if (o instanceof JSONArray) {
            return true;
        } else if (o instanceof JSONObject) {
            return true;
        } else if (o instanceof String) {
            return true;
        } else if (o instanceof Boolean) {
            return true;
        } else if (o instanceof Byte) {
            return true;
        } else if (o instanceof Short) {
            return true;
        } else if (o instanceof Integer) {
            return true;
        } else if (o instanceof Long) {
            return true;
        } else if (o instanceof Float) {
            return true;
        } else if (o instanceof Double) {
            return true;
        } else if (o instanceof BigDecimal) {
            return true;
        } else return o instanceof BigInteger;
    }

    private static Object toMapperJsonSingle(Object o, Class<?> cls, boolean canArray) {
        if (isJsonType(o)) {
            return o;
        }
        if (!cls.isPrimitive()) {
            return JSONObject.mapper(o, canArray);
        }
        return o;
    }

    private static JSONObject mapper(Object o, boolean canArray) {
        Class<?> cls = o.getClass();
        JSONObject r = JSONObject.build();
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
			/*
			if (field.getModifiers() != Modifier.PRIVATE) {
				continue;
			}
			 */
            field.setAccessible(true);
            Object val = toMapperJson(o, field, canArray);
            if (val != null) {
                r.put(field.getName(), val);
            }
        }
        return r;
    }

    // object->json
    public static JSONObject mapper(Object o) {
        return mapper(o, true);
    }

    public JSONObject removeAll() {
        super.clear();
        return this;
    }

    public byte[] getBytes() {
        return toString().getBytes();
    }

    public void writeJSONString(Writer out, int format) throws IOException {
        writeJSONString(this, out, format);
    }

    public void writeJSONString(Writer out) throws IOException {
        writeJSONString(this, out, 0);
    }

    public String toString() {
        return toJSONString(this);
    }

    public String toPrettyString() {
        return toPrettyJSONString(this);
    }

    public String toEscapeString() {
        return escape(toString());
    }

    public boolean compare(JSONObject json) {
        for (String key : keySet()) {
            if (!json.has(key)) {
                return false;
            }
            if (!json.get(key).equals(get(key))) {
                return false;
            }
        }
        return true;
    }

    public JSONObject mapsByKey(String key) {
        JSONObject r = new JSONObject();
        for (Object v : values()) {
            r.put(getString(key), v);
        }
        return r;
    }

    public JSONObject put(Map<? extends String, ?> m) {
        if (m != null) {
            putAll(m);
        }
        return this;
    }

    public JSONObject putIfNotNull(Map<? extends String, ?> m) {
        if (m != null) {
            this.put(m);
        }
        return this;
    }

    public Object escapeHtml(Object value) {
        return (value instanceof String) ? escape((String) value) : value;
    }

    public Object unescapeHtml(Object value) {
        return (value instanceof String) ? escape((String) value) : value;
    }

    @Override
    public JSONObject put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public JSONObject putIfNotNull(String key, Object value) {
        if (value != null) {
            this.put(key, value);
        }
        return this;
    }

    public Object escapeHtmlGet(String key) {
        return unescapeHtml(get(key));
    }

    public Object get(String key, Object defaultValue) {
        return has(key) ? get(key) : defaultValue;
    }

    public boolean has(String key) {
        return containsKey(key);
    }

    public String getString(String key) {
        Object value = get(key);
        if (key.equals("_id") && value instanceof JSONObject && ((JSONObject) value).has("$oid")) {
            value = ((JSONObject) value).getString("$oid");
        }
        return value == null ? "" : value.toString();
    }

    public int getInt(String key) {
        return JSONValue.IntValue(get(key));
    }

    public double getDouble(String key) {
        return JSONValue.DoubleValue(get(key));
    }

    public float getFloat(String key) {
        return JSONValue.FloatValue(get(key));
    }

    public long getLong(String key) {
        return JSONValue.LongValue(get(key));
    }

    public boolean getBoolean(String key) {
        return JSONValue.BooleanValue(get(key));
    }

    public JSONObject getJson(String key) {
        return JSONValue.JsonValue(get(key));
    }

    public JSONObject escapeHtmlPut(String key, Object value) {
        return put(key, escapeHtml(value));
    }

    public Object getPkValue(String key) {
        Object val = getMongoID(key);
        if (val == null) {
            val = get(key);
        }
        return val;
    }

    private String getMongoID(String key) {
        Object val = get(key);
        if (val instanceof JSONObject) {
            val = ((JSONObject) val).getString("$oid");
        }
        return (String) val;
    }

    public JSONObject link(String field) {
        return this.link(field, 0);
    }

    public <T> JSONArray<T> getJsonArray(String key) {
        return JSONValue.JsonArrayValue(get(key));

    }

    public JSONObject base() {
        return base("#");
    }

    public JSONObject base(String spiltString) {
        JSONObject newJson = new JSONObject();
        for (String key : this.keySet()) {
            if (!key.contains(spiltString)) {
                newJson.put(key, this.get(key));
            }
        }
        return newJson;
    }

    public JSONObject addGroup(String groupName, JSONObject input) {
        return addGroup(groupName, "#", input);
    }

    public JSONObject addGroup(String groupName, String spiltString, JSONObject input) {
        for (String key : input.keySet()) {
            this.put(groupName + spiltString + key, input.get(key));
        }
        return this;
    }

    public JSONObject removeGroup(String groupName, String spiltString) {
        JSONObject newJson = new JSONObject();
        for (String key : this.keySet()) {
            String[] strArray = key.split(spiltString);
            if (strArray.length > 1 && !strArray[0].equals(groupName)) {
                newJson.put(unionString(strArray, spiltString), this.get(key));
            }
        }
        return newJson;
    }

    public JSONObject groupBy(String groupName) {
        return groupBy(groupName, "#");
    }

    public JSONObject groupBy(String groupName, String spiltString) {
        JSONObject newJson = new JSONObject();
        for (String key : this.keySet()) {
            String[] strArray = key.split(spiltString);
            if (strArray.length > 1 && strArray[0].equals(groupName)) {
                newJson.put(unionString(strArray, spiltString), this.get(key));
            }
        }
        return newJson;
    }

    private String unionString(String[] strArray, String splitString) {
        StringBuilder rs = new StringBuilder();
        int l = strArray.length;
        for (int i = 1; i < l; i++) {
            rs.append(strArray[i]).append(splitString);
        }
        return l > 1 ? rs.substring(0, rs.length() - splitString.length()) : strArray[0];
    }

    /**
     * 建立key映射
     *
     * @param localkey  本json的key
     * @param forgenkey 外json的对应key
     * @return a json
     */
    public JSONObject mapKey(String localkey, String forgenkey) {
        mapTable.put(forgenkey, localkey);
        return this;
    }

    /**
     * 建立key映射
     *
     * @param keys 外json key to 本json key
     * @return a json
     */
    public JSONObject mapKey(JSONObject keys) {
        for (String key : keys.keySet()) {
            mapTable.put(key, keys.getString(key));
        }
        return this;
    }

    /**
     * k-v map to hashmap
     * json and mapTable, 2 hashmap contains fields copy to current json 里包含的自检复制 sourceData 的数据到当前Json
     *
     * @param sourceData source json
     * @return a json
     */

    public JSONObject mapReplace(JSONObject sourceData) {
        String localkey;
        for (String key : sourceData.keySet()) {
            if (mapTable.containsKey(key)) {
                localkey = mapTable.get(key);
                if (this.has(localkey)) {
                    this.put(localkey, sourceData.get(key));
                }
            }
            if (has(key)) {
                put(key, sourceData.get(key));
            }
        }
        return this;
    }

    public <T> HashMap<String, T> toHashMap() {
        HashMap<String, T> rhm = new HashMap<>();
        for (String key : this.keySet()) {
            rhm.put(key, (T) this.get(key));
        }
        return rhm;
    }

    public JSONObject link(String field, int idx) {
        JSONArray rArray = this.getJsonArray(field + "&Array");
        if (!JSONArray.isInvalided(rArray)) {
            JSONObject o = (JSONObject) rArray.get(idx);
            if (!isInvalided(o)) {

                for (String key : o.keySet()) {
                    this.put(field + "#" + key, o.get(key));
                }

            }
        }
        return this;
    }

    public boolean check(String k, Object v) {
        Long _v = (v instanceof Integer) ? Long.valueOf(v.toString()) : (Long) v;
        return has(k) && get(k).equals(_v);
    }

    private Object toMapperObject(Field field) {
        Object v = get(field.getName());
        if (v == null) {
            return null;
        }
        Class<?> type = field.getType();
        // if value is Json
        if (v instanceof JSONObject) {
            return (type != JSONObject.class) ? ((JSONObject) v).mapper(field.getType()) : v;
        } else if (v instanceof JSONArray jsonArray) {
            if (type != JSONArray.class) {
                List<Object> arrayList = new ArrayList<>();
                for (Object i : jsonArray) {
                    JSONObject item = (JSONObject) i;
                    arrayList.add(type.isPrimitive() ?
                            item :
                            item.mapper(type.getComponentType())
                    );
                }
                return arrayList.toArray();
            } else {
                return v;
            }
        } else if (type.getSimpleName().equalsIgnoreCase("int")) {
            return Integer.parseInt(v.toString());
        } else if (type.getSimpleName().equalsIgnoreCase("long")) {
            return Long.parseLong(v.toString());
        } else if (type.getSimpleName().equalsIgnoreCase("float")) {
            return Float.parseFloat(v.toString());
        } else if (type.getSimpleName().equalsIgnoreCase("double")) {
            return Double.parseDouble(v.toString());
        } else if (type.getSimpleName().equalsIgnoreCase("BigDecimal")) {
            return BigDecimal.valueOf(Long.parseLong(v.toString()));
        } else if (type.getSimpleName().equalsIgnoreCase("BigInteger")) {
            return BigInteger.valueOf(Long.parseLong(v.toString()));
        } else {
            return v;
        }
    }

    // json->object
    public <T> T mapper(Class<T> cls) {
        Field[] fields = cls.getDeclaredFields();
        try {
            // var constructor = cls.getDeclaredConstructor(null);
            Constructor<T> constructor = cls.getConstructor();
            T o = constructor.newInstance();
            for (Field field : fields) {
//				if (field.getModifiers() != Modifier.PRIVATE) {
//					continue;
//				}
                field.setAccessible(true);
                Object result = toMapperObject(field);
                if (result != null) {
                    field.set(o, (field.getType().isArray()) ?
                            GenericsArray.getArray(field.getType().getComponentType(), (Object[]) result) :
                            result
                    );
                }
            }
            return o;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Class Not has Non-Parameter constructor");
        } catch (InstantiationException e) {
            throw new RuntimeException("Instantiation");
        } catch (IllegalAccessException e) {
            throw new RuntimeException("IllegalAccess");
        } catch (InvocationTargetException e) {
            throw new RuntimeException("InvocationTarget");
        }
    }

    public Properties toProperties() {
        Object obj2;
        String key;
        Properties pro = new Properties();
        for (String obj : this.keySet()) {
            key = obj;
            obj2 = get(key);
            if (obj2 instanceof Long) {
                obj2 = ((Long) obj2).intValue();
            }
            pro.put(key, obj2);
        }
        return pro.size() > 0 ? pro : null;
    }

    /**
     * 获得2个JSON不同值的字段组
     */
    public JSONArray<String> getNeField(JSONObject o) {
        return getNeField(o, false);
    }

    public JSONArray<String> getNeField(JSONObject o, boolean struct) {
        JSONArray<String> r = new JSONArray<>();
        for (String key : keySet()) {
            if (o.has(key)) {
                var v1 = get(key);
                if (v1 == null) {
                    if (o.get(key) != null) {
                        r.put(key);
                    }
                } else if (v1 instanceof Integer v) {
                    if (v != o.getInt(key)) {
                        r.put(key);
                    }
                } else if (v1 instanceof Long v) {
                    if (v != o.getLong(key)) {
                        r.put(key);
                    }
                } else if (v1 instanceof Float v) {
                    if (v != o.getFloat(key)) {
                        r.put(key);
                    }
                } else if (v1 instanceof Double v) {
                    if (v != o.getDouble(key)) {
                        r.put(key);
                    }
                } else if (!v1.equals(o.getString(key))) {
                    r.put(key);
                }
            } else {
                if (struct) {
                    r.put(key);
                }
            }
        }
        return r;
    }
}
