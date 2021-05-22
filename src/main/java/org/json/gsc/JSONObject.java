/*
 * $Id: JSONObject.java,v 1.1 2006/04/15 14:10:48 platform Exp $
 * Created on 2006-4-10
 */
package org.json.gsc;

import org.json.gsc.parser.JSONParser;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class JSONObject extends HashMap<String, Object> implements Map<String, Object>, JSONAware, JSONStreamAware {

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

	public JSONObject removeAll() {
		super.clear();
		return this;
	}

	public static void writeJSONString(Map map, Writer out, int format) throws IOException {
		if (map == null) {
			out.write("null");
			return;
		}

		boolean first = true;
		Iterator iter = map.entrySet().iterator();

		String format_end;
		switch (format) {
			case 1:
				format_end = "\r\n";
				break;
			default:
				format_end = "";
		}

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

	public static String escape(String s) {
		return JSONValue.escape(s);
	}

	public JSONObject put(Map<? extends String, ?> m) {
		putAll(m);
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
		int ri = 0;
		Object val = get(key);
		try{
			if (val instanceof Integer) {
				ri = (int) val;
			} else if (val instanceof Long) {
				ri = ((Long) val).intValue();
			} else if (val instanceof Double) {
				ri = ((Double) val).intValue();
			} else if (val instanceof Float) {
				ri = ((Float) val).intValue();
			} else if (val instanceof JSONObject) {
				ri = ((JSONObject) val).getInt("$numberInt");
			} else if (val instanceof String) {
				ri = Double.valueOf((String) val).intValue();
			} else if (val instanceof BigDecimal) {
				ri = ((BigDecimal) val).intValue();
			} else if (val instanceof BigInteger) {
				ri = ((BigInteger) val).intValue();
			}

		} catch (Exception e) {
			ri = 0;
		}
		return ri;
	}

	public double getDouble(String key) {
		double ri = 0.00;
		Object val = get(key);
		try {
			if (val instanceof Integer) {
				ri = ((Integer) val).doubleValue();
			} else if (val instanceof Long) {
				ri = ((Long) val).doubleValue();
			} else if (val instanceof Double) {
				ri = (double) val;
			} else if (val instanceof Float) {
				ri = ((Float) val).doubleValue();
			} else if (val instanceof JSONObject) {
				ri = ((JSONObject) val).getDouble("$numberDouble");
			} else if (val instanceof String) {
				ri = Double.valueOf((String) val).doubleValue();
			} else if (val instanceof BigDecimal) {
				ri = ((BigDecimal) val).doubleValue();
			} else if (val instanceof BigInteger) {
				ri = ((BigInteger) val).doubleValue();
			}

		} catch (Exception e) {
			ri = 0.00;
		}
		return ri;
	}

	public float getFloat(String key) {
		float ri = 0.00f;
		Object val = get(key);
		try {
			if (val instanceof Integer) {
				ri = ((Integer) val).floatValue();
			} else if (val instanceof Long) {
				ri = ((Long) val).floatValue();
			} else if (val instanceof Double) {
				ri = ((Double) val).floatValue();
			} else if (val instanceof Float) {
				ri = (float) val;
			} else if (val instanceof JSONObject) {
				ri = ((JSONObject) val).getFloat("$numberFloat");
			} else if (val instanceof String) {
				ri = Double.valueOf((String) val).floatValue();
			} else if (val instanceof BigDecimal) {
				ri = ((BigDecimal) val).floatValue();
			} else if (val instanceof BigInteger) {
				ri = ((BigInteger) val).floatValue();
			}

		} catch (Exception e) {
			ri = 0.00f;
		}
		return ri;
	}

	public long getLong(String key) {
		long ri = 0;
		Object val = get(key);
		try {
			if (val instanceof Integer) {
				ri = ((Integer) val).longValue();
			} else if (val instanceof Long) {
				ri = (long) val;
			} else if (val instanceof Double) {
				ri = ((Double) val).longValue();
			} else if (val instanceof Float) {
				ri = ((Float) val).longValue();
			} else if (val instanceof JSONObject) {
				ri = ((JSONObject) val).getLong("$numberLong");
			} else if (val instanceof String) {
				ri = Long.getLong((String) val);
			} else if (val instanceof BigDecimal) {
				ri = ((BigDecimal) val).longValue();
			} else if (val instanceof BigInteger) {
				ri = ((BigInteger) val).longValue();
			}

		}
		catch(Exception e) {
			ri = 0L;
		}
		return ri;
	}

	public boolean getBoolean(String key){
		boolean ri;
		try {
			ri = (boolean) get(key);
		} catch (Exception e) {
			Object val = get(key);
			if (val instanceof String) {
				String v = (String) val;
				if (v.equals("0")) {
					ri = false;
				} else if (v.equals("1")) {
					ri = true;
				} else {
					ri = Boolean.valueOf(v);
				}
			} else{
				ri = false;
			}
		}
		return ri;
	}

	public JSONObject getJson(String key){
		Object val = get(key);
		if( val instanceof JSONObject ){
			return (JSONObject)val;
		}
		if( val instanceof String ){
			return JSONObject.toJSON((String) val);
		}
		try {
			val = JSONObject.toJSON(val.toString());
		} catch (Exception e) {
			val = null;
		}
		return (JSONObject) val;
	}

	public boolean check(String k, Object v) {
		var _v = (v instanceof Integer) ? Long.valueOf(v.toString()) : v;
		return has(k) && get(k).equals(_v);
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
		Object val = get(key);
		if (val instanceof JSONArray) {
			return (JSONArray<T>) val;
		}
		if (val instanceof String) {
			return JSONArray.toJSONArray((String) val);
		}
		if (val instanceof JSONObject) {
			return JSONArray.build((T) val);
		}
		if( val instanceof ArrayList<?> ) {
			JSONArray rArray = new JSONArray<T>();
			((List<?>) val).forEach(e -> rArray.add(e));
			return rArray;
		}
		try {
			val = JSONArray.<T>toJSONArray(val.toString());
		} catch (Exception e) {
			val = JSONArray.<T>build();
		}
		return (JSONArray<T>) val;
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

	public JSONObject base(){
		return base("#");
	}
	public JSONObject base(String spiltString){
		JSONObject newJson = new JSONObject();
		for( String key : this.keySet() ){
			if( !key.contains(spiltString) ){
				newJson.put(key, this.get(key));
			}
		}
		return newJson;
	}

	public JSONObject addGroup(String groupName, JSONObject input){
		return addGroup(groupName, "#", input);
	}
	public JSONObject addGroup(String groupName, String spiltString, JSONObject input){
		for(String key : input.keySet()){
			this.put( groupName + spiltString + key, input.get(key) );
		}
		return this;
	}
	public JSONObject removeGroup(String groupName, String spiltString){
		JSONObject newJson = new JSONObject();
		for( String key : this.keySet() ){
			String[] strArray = key.split(spiltString);
			if( strArray.length > 1 && !strArray[0].equals(groupName) ){
				newJson.put( unionString(strArray, spiltString), this.get(key) );
			}
		}
		return newJson;
	}

	public JSONObject groupBy(String groupName){
		return groupBy(groupName, "#");
	}
	public JSONObject groupBy(String groupName, String spiltString){
		JSONObject newJson = new JSONObject();
		for( String key : this.keySet() ){
			String[] strArray = key.split(spiltString);
			if( strArray.length > 1 && strArray[0].equals(groupName) ){
				newJson.put( unionString(strArray, spiltString), this.get(key) );
			}
		}
		return newJson;
	}
	private String unionString(String[] strArray,String splitString){
		String rs = "";
		int l = strArray.length;
		for(int i =1; i<l;i++){
			rs = rs + strArray[i] + splitString;
		}
		return l > 1 ? rs.substring(0, rs.length()-splitString.length()) : strArray[0];
	}

	/**建立key映射
	 * @param localkey 本json的key
	 * @param forgenkey 外json的对应key
	 * @return a json
	 */
	public JSONObject mapKey(String localkey,String forgenkey){
		mapTable.put(forgenkey,localkey);
		return this;
	}

	/**建立key映射
	 * @param keys 外json key to 本json key
	 * @return a json
	 */
	@SuppressWarnings("unchecked")
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

	public JSONObject link(String field, int idx) {
		JSONArray rArray = this.getJsonArray(field + "&Array");
		if (JSONArray.isInvalided(rArray)) {
			return this;
		} else {
			JSONObject o = (JSONObject) rArray.get(idx);
			if (isInvalided(o)) {
				return this;
			} else {
				Iterator var5 = o.keySet().iterator();

				while (var5.hasNext()) {
					String key = (String) var5.next();
					this.put(field + "#" + key, o.get(key));
				}

				return this;
			}
		}
	}

	private static String toUpperFirstChar(String str) {
		var arr = str.toLowerCase(Locale.ROOT).toCharArray();
		arr[0] -= 32;
		return String.valueOf(arr);
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

	private static Object toMapperJson(Object o, Field field, boolean canArray) {
		try {
			var type = field.getType();
			var val = field.get(o);
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

	private static JSONObject mapper(Object o, boolean canArray) {
		Class<?> cls = o.getClass();
		JSONObject r = JSONObject.build();
		var fields = cls.getDeclaredFields();
		for (Field field : fields) {
			/*
			if (field.getModifiers() != Modifier.PRIVATE) {
				continue;
			}
			 */
			field.setAccessible(true);
			var val = toMapperJson(o, field, canArray);
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

	private Object toMapperObject(Field field) {
		var v = get(field.getName());
		if (v == null) {
			return null;
		}
		var type = field.getType();
		// if value is Json
		if (v instanceof JSONObject) {
			return (type != JSONObject.class) ? ((JSONObject) v).mapper(field.getType()) : v;
		} else if (v instanceof JSONArray) {
			var jsonArray = (JSONArray) v;
			if (type != JSONArray.class) {
				List<Object> arrayList = new ArrayList<>();
				for (Object i : jsonArray) {
					var item = (JSONObject) i;
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
		var fields = cls.getDeclaredFields();
		try {
			// var constructor = cls.getDeclaredConstructor(null);
			var constructor = cls.getConstructor();
			var o = constructor.newInstance();
			for (Field field : fields) {
//				if (field.getModifiers() != Modifier.PRIVATE) {
//					continue;
//				}
				field.setAccessible(true);
				var result = toMapperObject(field);
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
}
