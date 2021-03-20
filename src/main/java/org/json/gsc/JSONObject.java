/*
 * $Id: JSONObject.java,v 1.1 2006/04/15 14:10:48 platform Exp $
 * Created on 2006-4-10
 */
package org.json.gsc;

import org.json.gsc.parser.JSONParser;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class JSONObject extends HashMap<String, Object> implements Map<String, Object>, JSONAware, JSONStreamAware {

	private static final long serialVersionUID = -503443796854799292L;

	private final HashMap<String, String> mapTable = new HashMap<>();

	public JSONObject() {
		super();
	}

	/**
	 * Allows creation of a JSONObject from a Map. After that, both the
	 * generated JSONObject and the Map can be modified independently.
	 *
	 * @param map
	 */
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

	/**
	 * Encode a map into JSON text and write it to out.
	 * If this map is also a JSONAware or JSONStreamAware, JSONAware or JSONStreamAware specific behaviours will be ignored at this top level.
	 *
	 * @param map
	 * @param out
	 * @see org.json.gsc.JSONValue#writeJSONString(Object, Writer)
	 */
	public static void writeJSONString(Map map, Writer out) throws IOException {
		if (map == null) {
			out.write("null");
			return;
		}

		boolean first = true;
		Iterator iter = map.entrySet().iterator();

		out.write('{');
		while (iter.hasNext()) {
			if (first)
				first = false;
			else
				out.write(',');
			Map.Entry entry = (Map.Entry) iter.next();
			out.write('\"');
			out.write(escape(String.valueOf(entry.getKey())));
			out.write('\"');
			out.write(':');
			JSONValue.writeJSONString(entry.getValue(), out);
		}
		out.write('}');
	}

	public void writeJSONString(Writer out) throws IOException{
		writeJSONString(this, out);
	}

	/**
	 * Convert a map to JSON text. The result is a JSON object. 
	 * If this map is also a JSONAware, JSONAware specific behaviours will be omitted at this top level.
	 *
	 * @see org.json.gsc.JSONValue#toJSONString(Object)
	 *
	 * @param map
	 * @return JSON text, or "null" if map is null.
	 */
	public static String toJSONString(Map map){
		final StringWriter writer = new StringWriter();

		try {
			writeJSONString(map, writer);
			return writer.toString();
		} catch (IOException e) {
			// This should never happen with a StringWriter
			throw new RuntimeException(e);
		}
	}

	public String toJSONString(){
		return toJSONString(this);
	}

	public String toString(){
		return toJSONString();
	}

	public String toEscapeString() {
		return escape(toJSONString());
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
			if (!json.containsKey(key)) {    // 判断是否包含对应key
				return false;
			}
			if (!json.get(key).equals(get(key))) {    // 比较对应值是否一致
				return false;
			}
		}
		return true;
	}

	public JSONObject putAlls(Map<? extends String, ?> m) {
		putAll(m);
		return this;
	}

	public JSONObject mapsByKey(String key) {
		JSONObject r = new JSONObject();
		for (Object v : values()) {
			r.put(getString(key), v);
		}
		return r;
	}

	/**
	 * Escape quotes, \, /, \r, \n, \b, \f, \t and other control characters (U+0000 through U+001F).
	 * It's the same as JSONValue.escape() only for compatibility here.
	 *
	 * @param s
	 * @return
	 * @see org.json.gsc.JSONValue#escape(String)
	 */
	public static String escape(String s) {
		return JSONValue.escape(s);
	}

	public JSONObject puts(String key,Object value){
		put(key,value);
		return this;
	}
	public Object escapeHtml(Object value){
		return ( value instanceof String ) ? escape((String)value) : value;
	}

	public Object unescapeHtml(Object value){
		return ( value instanceof String ) ? escape((String)value) : value;
	}

	public Object escapeHtmlPut(String key,Object value){
		return put(key, escapeHtml(value));
	}

	public JSONObject escapeHtmlPuts(String key,Object value){
		return puts(key, escapeHtml(value));
	}

	public Object escapeHtmlGet(String key){
		return unescapeHtml( get(key) );
	}

	public Object get(String key,Object defaultValue){
		return containsKey(key) ? get(key) : defaultValue;
	}

	public String getString(String key){
		Object value = get(key);
		if( key.equals("_id") && value instanceof JSONObject && ((JSONObject) value).containsKey("$oid") ){
			value = ((JSONObject) value).getString("$oid");
		}
		else if( value instanceof JSONObject ){
			value = ((JSONObject) value).toJSONString();
		}
		else if( value instanceof JSONArray ){
			value = ((JSONArray) value).toJSONString();
		}
		return  value==null ? "" : value.toString();
	}

	public int getInt(String key){
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
		boolean ri = false;
		try{
			ri = (boolean)get(key);
		}
		catch(Exception e){
			ri = false;
			Object val = get(key);
			if( val instanceof String ){
				ri = Boolean.getBoolean((String) val);
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
			return JSONObject.toJSON((String)val);
		}
		try{
			val = JSONObject.toJSON(val.toString());
		}
		catch(Exception e){
			val = null;
		}
		return (JSONObject)val;
	}

	public JSONArray getJsonArray(String key){
		Object val = get(key);
		if( val instanceof JSONArray ){
			return (JSONArray)val;
		}
		if( val instanceof String ){
			return JSONArray.toJSONArray((String)val);
		}
		if( val instanceof JSONObject){
			return JSONArray.addx(val);
		}
		if( val instanceof ArrayList<?> ){
			JSONArray rArray = new JSONArray();
			((List<?>)val).forEach( e->rArray.add(e) );
			return rArray;
		}
		try{
			val = JSONArray.toJSONArray(val.toString());
		}
		catch(Exception e){
			val = null;
		}
		return (JSONArray)val;
	}

	public Object getPkValue(String key){
		Object val = getMongoID(key);
		if( val == null){
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

	@Deprecated
	public static JSONObject putx(String key, Object val) {
		return (new JSONObject()).puts(key, val);
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
	/**
	 * 获得不包含分组的json
	 * */
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
	/**
	 * 新增组
	 * */
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
	/**
	 * 将spiltString分割的字符串的左边看做一组
	 * */
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
	 * @return
	 */
	public JSONObject mapKey(String localkey,String forgenkey){
		mapTable.put(forgenkey,localkey);
		return this;
	}
	/**建立key映射
	 * @param keys 外json key =>本json key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONObject mapKey(JSONObject keys){
		for(String key : keys.keySet()){
			mapTable.put(key, keys.getString(key));
		}
		return this;
	}

	/**根据k-v映射表复制数据
	 * 按当前JSON和mapTable 2个hashmap 里所包含的字段复制sourceData的数据到当前json
	 * @param sourceData
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONObject mapReplace(JSONObject sourceData){
		String localkey;
		for(String key : sourceData.keySet()){
			if(mapTable.containsKey(key)){
				localkey = mapTable.get(key);
				if( this.containsKey(localkey) ){
					this.put(localkey, sourceData.get(key));
				}
			}
			if( containsKey(key) ){
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
}
