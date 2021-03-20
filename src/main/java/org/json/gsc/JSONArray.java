/*
 * $Id: JSONArray.java,v 1.1 2006/04/15 14:10:48 platform Exp $
 * Created on 2006-4-10
 */
package org.json.gsc;

import org.json.gsc.parser.JSONParser;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;
import java.util.function.Function;

/**
 * A JSON array. JSONObject supports java.util.List interface.
 *
 * @author FangYidong<fangyidong @ yahoo.com.cn>
 */
public class JSONArray<V extends Object> extends ArrayList<V> implements JSONAware, JSONStreamAware {
	private static final long serialVersionUID = 3957988303675231981L;
	private LinkedHashMap<Object, JSONArray> groups;

	/**
	 * Constructs an empty JSONArray.
	 */
	public JSONArray() {
		super();
	}

	/**
	 * Constructs a JSONArray containing the elements of the specified
	 * collection, in the order they are returned by the collection's iterator.
	 *
	 * @param c the collection whose elements are to be placed into this JSONArray
	 */
	public JSONArray(Collection c) {
		super(c);
	}

	public static JSONArray build() {
		return new JSONArray();
	}

	public static JSONArray build(Collection c) {
		return new JSONArray(c);
	}

	public static JSONArray build(String s) {
		return toJSONArray(s);
	}

	/**
	 * Encode a list into JSON text and write it to out.
	 * If this list is also a JSONStreamAware or a JSONAware, JSONStreamAware and JSONAware specific behaviours will be ignored at this top level.
	 *
	 * @param collection
	 * @param out
	 * @see org.json.gsc.JSONValue#writeJSONString(Object, Writer)
	 */
	public static void writeJSONString(Collection collection, Writer out) throws IOException {
		if (collection == null) {
			out.write("null");
			return;
		}

		boolean first = true;
		Iterator iter = collection.iterator();

		out.write('[');
		while (iter.hasNext()) {
			if (first) {
				first = false;
			} else {
				out.write(',');
			}
			Object value = iter.next();
			if (value == null) {
				out.write("null");
				continue;
			}

			JSONValue.writeJSONString(value, out);
		}
		out.write(']');
	}

	public JSONObject getJson(int idx){
		Object val = this.get(idx);
		JSONObject rs = null;
		if( val instanceof String ){
			rs = JSONObject.toJSON((String)val);
		}
		else if( val instanceof JSONObject ){
			rs = (JSONObject)val;
		}
		if( rs == null ){
			throw new RuntimeException("数据行:" + idx + " ->不是JSON数据");
		}
		return rs;
	}

	public JSONArray getJsonArray(int idx){
		Object val = this.get(idx);
		JSONArray rs = null;
		if( val instanceof String ){
			rs = JSONArray.toJSONArray((String)val);
		}
		else if( val instanceof JSONArray ){
			rs = (JSONArray)val;
		}
		if( rs == null ){
			throw new RuntimeException("数据行:" + idx + " ->不是JSONArray数据");
		}
		return rs;
	}

	public String getString(int idx){
		Object val = this.get(idx);
		String rs = null;
		if( !(val instanceof String )){
			try{
				rs = val.toString();
			}
			catch (Exception e){
				try{
					rs = String.valueOf(val);
				}
				catch (Exception e2){
					rs = null;
				}
			}
		}
		else {
			rs = (String)val;
		}
		return rs;
	}

	public int getInt(int idx){
		return (int)this.get(idx);
	}
	public long getLong(int idx){
		return (long)this.get(idx);
	}
	public float getFloat(int idx){
		return (float)this.get(idx);
	}
	public double getDouble(int idx){
		return (double)this.get(idx);
	}

	@Override
	public void writeJSONString(Writer out) throws IOException{
		writeJSONString(this, out);
	}

	/**
	 * Convert a list to JSON text. The result is a JSON array. 
	 * If this list is also a JSONAware, JSONAware specific behaviours will be omitted at this top level.
	 *
	 * @see org.json.gsc.JSONValue#toJSONString(Object)
	 *
	 * @param collection
	 * @return JSON text, or "null" if list is null.
	 */
	public static String toJSONString(Collection collection){
		final StringWriter writer = new StringWriter();

		try {
			writeJSONString(collection, writer);
			return writer.toString();
		} catch(IOException e){
			// This should never happen for a StringWriter
			throw new RuntimeException(e);
		}
	}

	public static void writeJSONString(byte[] array, Writer out) throws IOException{
		if(array == null){
			out.write("null");
		} else if(array.length == 0) {
			out.write("[]");
		} else {
			out.write("[");
			out.write(String.valueOf(array[0]));

			for(int i = 1; i < array.length; i++){
				out.write(",");
				out.write(String.valueOf(array[i]));
			}

			out.write("]");
		}
	}

	public static String toJSONString(byte[] array){
		final StringWriter writer = new StringWriter();

		try {
			writeJSONString(array, writer);
			return writer.toString();
		} catch(IOException e){
			// This should never happen for a StringWriter
			throw new RuntimeException(e);
		}
	}

	public static void writeJSONString(short[] array, Writer out) throws IOException{
		if(array == null){
			out.write("null");
		} else if(array.length == 0) {
			out.write("[]");
		} else {
			out.write("[");
			out.write(String.valueOf(array[0]));

			for(int i = 1; i < array.length; i++){
				out.write(",");
				out.write(String.valueOf(array[i]));
			}

			out.write("]");
		}
	}

	public static String toJSONString(short[] array){
		final StringWriter writer = new StringWriter();

		try {
			writeJSONString(array, writer);
			return writer.toString();
		} catch(IOException e){
			// This should never happen for a StringWriter
			throw new RuntimeException(e);
		}
	}

	public static void writeJSONString(int[] array, Writer out) throws IOException{
		if(array == null){
			out.write("null");
		} else if(array.length == 0) {
			out.write("[]");
		} else {
			out.write("[");
			out.write(String.valueOf(array[0]));

			for(int i = 1; i < array.length; i++){
				out.write(",");
				out.write(String.valueOf(array[i]));
			}

			out.write("]");
		}
	}

	public static String toJSONString(int[] array){
		final StringWriter writer = new StringWriter();

		try {
			writeJSONString(array, writer);
			return writer.toString();
		} catch(IOException e){
			// This should never happen for a StringWriter
			throw new RuntimeException(e);
		}
	}

	public static void writeJSONString(long[] array, Writer out) throws IOException{
		if(array == null){
			out.write("null");
		} else if(array.length == 0) {
			out.write("[]");
		} else {
			out.write("[");
			out.write(String.valueOf(array[0]));

			for(int i = 1; i < array.length; i++){
				out.write(",");
				out.write(String.valueOf(array[i]));
			}

			out.write("]");
		}
	}

	public static String toJSONString(long[] array){
		final StringWriter writer = new StringWriter();

		try {
			writeJSONString(array, writer);
			return writer.toString();
		} catch(IOException e){
			// This should never happen for a StringWriter
			throw new RuntimeException(e);
		}
	}

	public static void writeJSONString(float[] array, Writer out) throws IOException{
		if(array == null){
			out.write("null");
		} else if(array.length == 0) {
			out.write("[]");
		} else {
			out.write("[");
			out.write(String.valueOf(array[0]));

			for(int i = 1; i < array.length; i++){
				out.write(",");
				out.write(String.valueOf(array[i]));
			}

			out.write("]");
		}
	}

	public static String toJSONString(float[] array){
		final StringWriter writer = new StringWriter();

		try {
			writeJSONString(array, writer);
			return writer.toString();
		} catch(IOException e){
			// This should never happen for a StringWriter
			throw new RuntimeException(e);
		}
	}

	public static void writeJSONString(double[] array, Writer out) throws IOException{
		if(array == null){
			out.write("null");
		} else if(array.length == 0) {
			out.write("[]");
		} else {
			out.write("[");
			out.write(String.valueOf(array[0]));

			for(int i = 1; i < array.length; i++){
				out.write(",");
				out.write(String.valueOf(array[i]));
			}

			out.write("]");
		}
	}

	public static String toJSONString(double[] array){
		final StringWriter writer = new StringWriter();

		try {
			writeJSONString(array, writer);
			return writer.toString();
		} catch(IOException e){
			// This should never happen for a StringWriter
			throw new RuntimeException(e);
		}
	}

	public static void writeJSONString(boolean[] array, Writer out) throws IOException{
		if(array == null){
			out.write("null");
		} else if(array.length == 0) {
			out.write("[]");
		} else {
			out.write("[");
			out.write(String.valueOf(array[0]));

			for(int i = 1; i < array.length; i++){
				out.write(",");
				out.write(String.valueOf(array[i]));
			}

			out.write("]");
		}
	}

	public static String toJSONString(boolean[] array){
		final StringWriter writer = new StringWriter();

		try {
			writeJSONString(array, writer);
			return writer.toString();
		} catch(IOException e){
			// This should never happen for a StringWriter
			throw new RuntimeException(e);
		}
	}

	public static void writeJSONString(char[] array, Writer out) throws IOException{
		if(array == null){
			out.write("null");
		} else if(array.length == 0) {
			out.write("[]");
		} else {
			out.write("[\"");
			out.write(String.valueOf(array[0]));

			for(int i = 1; i < array.length; i++){
				out.write("\",\"");
				out.write(String.valueOf(array[i]));
			}

			out.write("\"]");
		}
	}

	public static String toJSONString(char[] array){
		final StringWriter writer = new StringWriter();

		try {
			writeJSONString(array, writer);
			return writer.toString();
		} catch(IOException e){
			// This should never happen for a StringWriter
			throw new RuntimeException(e);
		}
	}

	public static void writeJSONString(Object[] array, Writer out) throws IOException{
		if(array == null){
			out.write("null");
		} else if(array.length == 0) {
			out.write("[]");
		} else {
			out.write("[");
			JSONValue.writeJSONString(array[0], out);

			for(int i = 1; i < array.length; i++){
				out.write(",");
				JSONValue.writeJSONString(array[i], out);
			}

			out.write("]");
		}
	}

	public static String toJSONString(Object[] array){
		final StringWriter writer = new StringWriter();

		try {
			writeJSONString(array, writer);
			return writer.toString();
		} catch(IOException e){
			// This should never happen for a StringWriter
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toJSONString(){
		return toJSONString(this);
	}

	/**
	 * Returns a string representation of this array. This is equivalent to
	 * calling {@link JSONArray#toJSONString()}.
	 */
	@Override
	public String toString() {
		return toJSONString();
	}


	public JSONArray adds(V obj) {
		add(obj);
		return this;
	}

	public <T> List<T> toArrayList() {
		List<T> list = new ArrayList<>();
		forEach(item -> {
			list.add((T) item);
		});
		return list;
	}

	/**
	 * 根据一个某特定字段的值,映射行数据到JSON
	 */
	public JSONObject mapsByKey(String keyName){
		JSONObject rJson = new JSONObject();
		forEach( item->{
			String vKey = ((JSONObject)item).getString(keyName);
			if( vKey != null ){
				rJson.put(vKey, item);
			}
		});
		return rJson;
	}

	/**
	 * 设置连接交集
	 */
	public JSONArray joinOn(String fieldName, JSONArray foreignArray) {
		return this.joinOn(fieldName, fieldName, foreignArray);
	}

	public JSONArray joinOn(String ownFieldName, String foreignFieldName, JSONArray foreignArray) {
		return this.joinOn(ownFieldName, foreignFieldName, foreignArray, false);
	}

	public JSONArray joinOn(String ownFieldName, String foreignFieldName, JSONArray foreignArray, boolean save_null_item) {
		if (foreignArray == null) {
			this.clear();
			return this;
		} else {
			HashMap<Object, JSONObject> quickMap = new HashMap();
			Iterator it = foreignArray.iterator();

			while (it.hasNext()) {
				Object obj = it.next();
				JSONObject item = (JSONObject) obj;
				if (item.containsKey(foreignFieldName)) {
					quickMap.put(item.getString(foreignFieldName), item);
				}
			}

			it = this.iterator();

			while (true) {
				while (it.hasNext()) {
					JSONObject item = (JSONObject) it.next();
					if (!item.containsKey(ownFieldName)) {
						it.remove();
					} else {
						List<String> keyValueArray = new ArrayList(Arrays.asList(item.getString(ownFieldName).split(",")));
						Iterator<String> child_it = keyValueArray.iterator();
						JSONArray rArray = new JSONArray();

						while (child_it.hasNext()) {
							String keyValue = child_it.next();
							if (!quickMap.containsKey(keyValue)) {
								if (!save_null_item) {
									child_it.remove();
								}
							} else {
								rArray.add(quickMap.get(keyValue));
							}
						}

						item.put(ownFieldName + "&Array", rArray);
						if (rArray.size() > 0) {
							item.link(ownFieldName);
						}
					}
				}

				return this;
			}
		}
	}

	@Deprecated
	public static final JSONArray addx(Object obj) {
		return (new JSONArray()).adds(obj);
	}

	public static final JSONArray toJSONArray(List<Object> arrayList) {
		JSONArray arrayJson = new JSONArray();
		for (Object object : arrayList) {
			arrayJson.add(object);
		}
		return arrayJson;
	}

	public static final JSONArray toJSONArray(String str) {
		JSONArray rObject;
		JSONParser parser = new JSONParser();
		try {
			rObject = (JSONArray) parser.parse(str);
		} catch (Exception e) {
			rObject = null;
		}
		return rObject;
	}

	public static final boolean isInvalided(JSONArray array) {
		return array == null || array.size() == 0;
	}

	public static final <T extends Iterable> JSONArray convert(T in) {
		JSONArray myJsonArray = new JSONArray();
		for (Object obj : in) {
			myJsonArray.add(obj);
		}
		return myJsonArray;
	}

	private JSONArray sortJsonArray(String field, int sort) {
		JSONArray newArray = new JSONArray();
		Iterator<V> it = this.iterator();
		Iterator<V> it2;
		JSONObject item, item2;
		long sortValue, sortValue2;
		while (it.hasNext()) {
			item = (JSONObject) it.next();
			sortValue = item.getLong(field);
			// 找到当前最大排序值对象
			it2 = this.iterator();
			while (it2.hasNext()) {
				item2 = (JSONObject) it2.next();
				sortValue2 = item2.getLong(field);
				// 发现更大的排序值了，替换当前指标量
				if (sort == 1) { // 升序
					if (sortValue2 < sortValue) {
						item = item2;
						sortValue = sortValue2;
					}
				} else {
					if (sortValue2 > sortValue){
						item = item2;
						sortValue = sortValue2;
					}
				}
			}
			// 删除最大对象再老数组
			this.remove(item);
			it = this.iterator();
			// 添加最大对象到新数组
			newArray.add(item);
		}
		return newArray;
	}

	public JSONArray desc(String field) {
		return sortJsonArray(field, 0);
	}

	public JSONArray asc(String field) {
		return sortJsonArray(field, 1);
	}

	public JSONArray limit(int no) {
		Iterator<V> it = this.iterator();
		int i = 0;
		while (it.hasNext()) {
			if (i > no) {
				it.remove();
			}
			i++;
		}
		return this;
	}

	// 比较jsonObject构成的jsonArray,内容是否一致(不管顺序)
	public boolean compareJson(JSONArray<JSONObject> jsonArray) {
		for (JSONObject json : jsonArray) {
			for (int i = 0, l = this.size(); i < l; i++) {
				if (!json.compare(getJson(i))) {
					return false;
				}
			}
		}
		return true;
	}

	public JSONArray step(int no) {
		JSONArray outArray = new JSONArray();
		for (int i = no, l = this.size(); i < l; i++) {
			outArray.add(this.get(i));
		}
		return outArray;
	}

	public JSONArray filter(String field, Function<Object, Boolean> cb) {
		Iterator<V> it = this.iterator();
		while (it.hasNext()) {
			// 不符合条件删除
			if (!cb.apply(((JSONObject) it.next()).get(field))) {
				it.remove();
			}
		}
		return this;
	}

	// 深度克隆
	public JSONArray<V> clone() {
		JSONArray<V> result = JSONArray.build();
		for (V v : this) {
			result.add(v);
		}
		return result;
	}

	// 修改Field内对应的值
	public JSONArray mapValue(String field, Function<Object, Object> func) {
		for (Object v : this) {
			JSONObject item = (JSONObject) v;
			item.put(field, func.apply(item.get(field)));
		}
		return this;
	}

	// 修改Field
	public JSONArray mapKey( String field , Function<String,String> func){
		JSONObject item;
		Object val;
		for(Object o : this){
			item = (JSONObject)o;
			val = item.get(field);
			item.remove(field);
			item.put(func.apply( field ), val);
		}
		return this;
	}

	// 按照field字段，树状展开
	public JSONArray flatMap(String field) {
		groups = new LinkedHashMap<>();
		Iterator<V> it = this.iterator();
		JSONObject item;
		Object groupValue;
		while (it.hasNext()) {
			item = (JSONObject) it.next();
			groupValue = item.get(field);
			if (!groups.containsKey(groupValue)) {
				groups.put(groupValue, new JSONArray());
			}
			groups.get(groupValue).add(item);
		}
		return this;
	}
	public JSONArray then(Function<JSONArray, JSONArray> func){
		// 异常处理
		if( groups != null ){
			// 过滤后替换值（中间过程）
			for(Object key : groups.keySet()){
				groups.put(key, func.apply(groups.get(key)));
			}
		}
		return this;
	}
	public JSONArray reduce(){
		JSONArray outArray = new JSONArray();
		// 异常处理
		if( groups != null ){
			// 整合最后的hash（合并数据）
			for(Object key : groups.keySet()){
				outArray.addAll( groups.get(key) );
			}
		}
		return outArray;
	}

	public JSONArray mapReduce(String field, Function<JSONArray, JSONArray> reduceFunc){
		return this.flatMap(field).then(reduceFunc).reduce();
	}
}
