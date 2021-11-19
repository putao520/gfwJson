/*
 * $Id: JSONArray.java,v 1.1 2006/04/15 14:10:48 platform Exp $
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
import java.util.function.Function;

public class JSONArray<V> extends ArrayList<V> implements JSONAware, JSONStreamAware {
	private static final long serialVersionUID = 3957988303675231981L;
	private LinkedHashMap<Object, JSONArray<JSONObject>> groups;

	public JSONArray() {
		super();
	}

	public JSONArray(Collection c) {
		super(c);
	}

	public static <T> JSONArray<T> build() {
		return new JSONArray<>();
	}

	public static <T> JSONArray<T> build(Collection c) {
		return new JSONArray<>(c);
	}

	public static <T> JSONArray<T> build(String s) {
		return toJSONArray(s);
	}

	public static <T> JSONArray<T> build(T s) {
		return (new JSONArray<T>()).put(s);
	}

	public JSONArray removeAll() {
		super.clear();
		return this;
	}

	private static String getAppend(int format) {
		if (format == 1) {
			return "\r\n";
		}
		return "";
	}

	public boolean isJsonValue() {
		if (size() > 0) {
			try {
				return JSONObject.build(get(0).toString()) != null;
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	public boolean isJsonArrayValue() {
		if (size() > 0) {
			try {
				return JSONArray.build(get(0).toString()) != null;
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	public static void writeJSONString(Collection collection, Writer out, int format) throws IOException {
		if (collection == null) {
			out.write("null");
			return;
		}

		boolean first = true;
		Iterator iter = collection.iterator();

		String format_end = getAppend(format);

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
		out.write(']' + format_end);
	}

	public static <T> JSONArray<T> toJSONArray(List<T> arrayList) {
		JSONArray<T> arrayJson = new JSONArray<>();
		for (T object : arrayList) {
			arrayJson.put(object);
		}
		return arrayJson;
	}

	public JSONObject getJson(int idx) {
		Object val = this.get(idx);
		JSONObject rs = null;
		if (val instanceof String) {
			rs = JSONObject.toJSON((String) val);
		} else if (val instanceof JSONObject) {
			rs = (JSONObject) val;
		}
		if (rs == null) {
			rs = JSONObject.build();
		}
		return rs;
	}

	public <T> JSONArray<T> getJsonArray(int idx) {
		Object val = this.get(idx);
		JSONArray rs = null;
		if (val instanceof String) {
			rs = JSONArray.<T>toJSONArray((String) val);
		} else if (val instanceof JSONArray) {
			rs = (JSONArray<T>) val;
		}
		if (rs == null) {
			rs = JSONArray.<T>build();
		}
		return rs;
	}

	public int getInt(int idx) {
		int r = 0;
		Object in = get(idx);
		if (in == null) {
			return r;
		}
		try {
			if (in instanceof Long) {
				r = ((Long) in).intValue();
			} else if (in instanceof Integer) {
				r = (Integer) in;
			} else if (in instanceof String) {
				r = Integer.valueOf(((String) in).trim());
			} else if (in instanceof Double) {
				r = ((Double) in).intValue();
			} else if (in instanceof Float) {
				r = ((Float) in).intValue();
			} else if (in instanceof Boolean) {
				r = (Boolean) in ? 0 : 1;
			} else if (in instanceof BigInteger) {
				r = ((BigInteger) in).intValue();
			} else if (in instanceof BigDecimal) {
				r = ((BigDecimal) in).intValue();
			}
		} catch (Exception e) {
			r = 0;
		}
		return r;
	}

	public long getLong(int idx) {
		long r = 0;
		Object in = get(idx);
		if (in == null) {
			return r;
		}
		try {
			if (in instanceof Long) {
				r = (Long) in;
			} else if (in instanceof Integer) {
				r = ((Integer) in).longValue();
			} else if (in instanceof String) {
				r = Long.valueOf(((String) in).trim());
			} else if (in instanceof Double) {
				r = ((Double) in).longValue();
			} else if (in instanceof Float) {
				r = ((Float) in).longValue();
			} else if (in instanceof Boolean) {
				r = (Boolean) in ? 0 : 1;
			} else if (in instanceof BigInteger) {
				r = ((BigInteger) in).longValue();
			} else if (in instanceof BigDecimal) {
				r = ((BigDecimal) in).longValue();
			}
		} catch (Exception e) {
			r = 0;
		}
		return r;
	}

	public float getFloat(int idx) {
		Object in = get(idx);
		float r = 0.f;
		if (in == null) {
			return r;
		}
		try {
			if (in instanceof Long) {
				r = ((Long) in).floatValue();
			} else if (in instanceof Integer) {
				r = ((Integer) in).floatValue();
			} else if (in instanceof String) {
				r = Float.valueOf(((String) in).trim());
			} else if (in instanceof Double) {
				r = ((Double) in).floatValue();
			} else if (in instanceof Float) {
				r = (Float) in;
			} else if (in instanceof Boolean) {
				r = (Boolean) in ? 0.0f : 1.0f;
			} else if (in instanceof BigInteger) {
				r = ((BigInteger) in).floatValue();
			} else if (in instanceof BigDecimal) {
				r = ((BigDecimal) in).floatValue();
			}
		} catch (Exception e) {
			r = 0.f;
		}
		return r;
	}
	public double getDouble(int idx) {
		double r = 0.d;
		Object in = get(idx);
		if (in == null) {
			return r;
		}
		try {
			if (in instanceof Long) {
				r = ((Long) in).doubleValue();
			} else if (in instanceof Integer) {
				r = ((Integer) in).doubleValue();
			} else if (in instanceof String) {
				r = Double.valueOf(((String) in).trim());
			} else if (in instanceof Double) {
				r = ((Double) in);
			} else if (in instanceof Float) {
				r = ((Float) in).doubleValue();
			} else if (in instanceof Boolean) {
				r = (Boolean) in ? 0.0d : 1.0d;
			} else if (in instanceof BigInteger) {
				r = ((BigInteger) in).doubleValue();
			} else if (in instanceof BigDecimal) {
				r = ((BigDecimal) in).doubleValue();
			}
		} catch (Exception e) {
			r = 0.d;
		}
		return r;
	}

	public static String toJSONString(Collection collection) {
		return toJSONString(collection, 0);
	}

	public static String toJSONString(Collection collection, int format) {
		final StringWriter writer = new StringWriter();

		try {
			writeJSONString(collection, writer, format);
			return writer.toString();
		} catch (IOException e) {
			// This should never happen for a StringWriter
			throw new RuntimeException(e);
		}
	}

	public static void writeJSONString(byte[] array, Writer out) throws IOException {
		writeJSONString(array, out, 0);
	}

	public static void writeJSONString(byte[] array, Writer out, int format) throws IOException {
		if (array == null) {
			out.write("null");
		} else if (array.length == 0) {
			out.write("[]");
		} else {
			String format_end = getAppend(format);
			out.write("[");
			out.write(String.valueOf(array[0]));

			for (int i = 1; i < array.length; i++) {
				out.write(",");
				out.write(String.valueOf(array[i]));
			}

			out.write("]" + format_end);
		}
	}

	public static String toJSONString(byte[] array) {
		return toJSONString(array, 0);
	}

	public static String toJSONString(byte[] array, int format) {
		final StringWriter writer = new StringWriter();

		try {
			writeJSONString(array, writer, format);
			return writer.toString();
		} catch (IOException e) {
			// This should never happen for a StringWriter
			throw new RuntimeException(e);
		}
	}

	public static void writeJSONString(short[] array, Writer out) throws IOException {
		writeJSONString(array, out, 0);
	}

	public static void writeJSONString(short[] array, Writer out, int format) throws IOException {
		if (array == null) {
			out.write("null");
		} else if (array.length == 0) {
			out.write("[]");
		} else {
			String format_end = getAppend(format);
			out.write("[");
			out.write(String.valueOf(array[0]));

			for (int i = 1; i < array.length; i++) {
				out.write(",");
				out.write(String.valueOf(array[i]));
			}

			out.write("]" + format_end);
		}
	}

	public static String toJSONString(short[] array) {
		return toJSONString(array, 0);
	}

	public static String toJSONString(short[] array, int format) {
		final StringWriter writer = new StringWriter();

		try {
			writeJSONString(array, writer, format);
			return writer.toString();
		} catch (IOException e) {
			// This should never happen for a StringWriter
			throw new RuntimeException(e);
		}
	}

	public static void writeJSONString(int[] array, Writer out) throws IOException {
		writeJSONString(array, out, 0);
	}

	public static void writeJSONString(int[] array, Writer out, int format) throws IOException {
		if (array == null) {
			out.write("null");
		} else if (array.length == 0) {
			out.write("[]");
		} else {
			String format_end = getAppend(format);
			out.write("[");
			out.write(String.valueOf(array[0]));

			for (int i = 1; i < array.length; i++) {
				out.write(",");
				out.write(String.valueOf(array[i]));
			}

			out.write("]" + format_end);
		}
	}

	public static String toJSONString(int[] array) {
		return toJSONString(array, 0);
	}

	public static String toJSONString(int[] array, int format) {
		final StringWriter writer = new StringWriter();

		try {
			writeJSONString(array, writer, format);
			return writer.toString();
		} catch (IOException e) {
			// This should never happen for a StringWriter
			throw new RuntimeException(e);
		}
	}

	public static void writeJSONString(long[] array, Writer out) throws IOException {
		writeJSONString(array, out, 0);
	}

	public static void writeJSONString(long[] array, Writer out, int format) throws IOException {
		if (array == null) {
			out.write("null");
		} else if (array.length == 0) {
			out.write("[]");
		} else {
			String format_end = getAppend(format);
			out.write("[");
			out.write(String.valueOf(array[0]));

			for (int i = 1; i < array.length; i++) {
				out.write(",");
				out.write(String.valueOf(array[i]));
			}

			out.write("]" + format_end);
		}
	}

	public static String toJSONString(long[] array) {
		return toJSONString(array, 0);
	}

	public static String toJSONString(long[] array, int format) {
		final StringWriter writer = new StringWriter();

		try {
			writeJSONString(array, writer, format);
			return writer.toString();
		} catch (IOException e) {
			// This should never happen for a StringWriter
			throw new RuntimeException(e);
		}
	}

	public static void writeJSONString(float[] array, Writer out) throws IOException {
		writeJSONString(array, out, 0);
	}

	public static void writeJSONString(float[] array, Writer out, int format) throws IOException {
		if (array == null) {
			out.write("null");
		} else if (array.length == 0) {
			out.write("[]");
		} else {
			String format_end = getAppend(format);
			out.write("[");
			out.write(String.valueOf(array[0]));

			for (int i = 1; i < array.length; i++) {
				out.write(",");
				out.write(String.valueOf(array[i]));
			}

			out.write("]" + format_end);
		}
	}

	public static String toJSONString(float[] array) {
		return toJSONString(array, 0);
	}

	public static String toJSONString(float[] array, int format) {
		final StringWriter writer = new StringWriter();

		try {
			writeJSONString(array, writer, format);
			return writer.toString();
		} catch (IOException e) {
			// This should never happen for a StringWriter
			throw new RuntimeException(e);
		}
	}

	public static void writeJSONString(double[] array, Writer out) throws IOException {
		writeJSONString(array, out, 0);
	}

	public static void writeJSONString(double[] array, Writer out, int format) throws IOException {
		if (array == null) {
			out.write("null");
		} else if (array.length == 0) {
			out.write("[]");
		} else {
			String format_end = getAppend(format);
			out.write("[");
			out.write(String.valueOf(array[0]));

			for (int i = 1; i < array.length; i++) {
				out.write(",");
				out.write(String.valueOf(array[i]));
			}

			out.write("]" + format_end);
		}
	}

	public static String toJSONString(double[] array) {
		return toJSONString(array, 0);
	}

	public static String toJSONString(double[] array, int format) {
		final StringWriter writer = new StringWriter();

		try {
			writeJSONString(array, writer, format);
			return writer.toString();
		} catch (IOException e) {
			// This should never happen for a StringWriter
			throw new RuntimeException(e);
		}
	}

	public static void writeJSONString(boolean[] array, Writer out) throws IOException {
		writeJSONString(array, out, 0);
	}

	public static void writeJSONString(boolean[] array, Writer out, int format) throws IOException {
		if (array == null) {
			out.write("null");
		} else if (array.length == 0) {
			out.write("[]");
		} else {
			String format_end = getAppend(format);
			out.write("[");
			out.write(String.valueOf(array[0]));

			for (int i = 1; i < array.length; i++) {
				out.write(",");
				out.write(String.valueOf(array[i]));
			}

			out.write("]" + format_end);
		}
	}

	public static String toJSONString(boolean[] array) {
		return toJSONString(array, 0);
	}

	public static String toJSONString(boolean[] array, int format) {
		final StringWriter writer = new StringWriter();

		try {
			writeJSONString(array, writer, format);
			return writer.toString();
		} catch (IOException e) {
			// This should never happen for a StringWriter
			throw new RuntimeException(e);
		}
	}

	public static void writeJSONString(char[] array, Writer out) throws IOException {
		writeJSONString(array, out, 0);
	}

	public static void writeJSONString(char[] array, Writer out, int format) throws IOException {
		if (array == null) {
			out.write("null");
		} else if (array.length == 0) {
			out.write("[]");
		} else {
			String format_end = getAppend(format);
			out.write("[\"");
			out.write(String.valueOf(array[0]));

			for (int i = 1; i < array.length; i++) {
				out.write("\",\"");
				out.write(String.valueOf(array[i]));
			}

			out.write("\"]" + format_end);
		}
	}

	public static String toJSONString(char[] array) {
		return toJSONString(array, 0);
	}

	public static String toJSONString(char[] array, int format) {
		final StringWriter writer = new StringWriter();

		try {
			writeJSONString(array, writer, format);
			return writer.toString();
		} catch (IOException e) {
			// This should never happen for a StringWriter
			throw new RuntimeException(e);
		}
	}

	public static void writeJSONString(Object[] array, Writer out) throws IOException {
		writeJSONString(array, out, 0);
	}

	public static void writeJSONString(Object[] array, Writer out, int format) throws IOException {
		if (array == null) {
			out.write("null");
		} else if (array.length == 0) {
			out.write("[]");
		} else {
			String format_end = getAppend(format);
			out.write("[");
			JSONValue.writeJSONString(array[0], out);

			for (int i = 1; i < array.length; i++) {
				out.write(",");
				JSONValue.writeJSONString(array[i], out);
			}

			out.write("]" + format_end);
		}
	}

	public static String toJSONString(Object[] array) {
		return toJSONString(array, 0);
	}

	public static String toJSONString(Object[] array, int format) {
		final StringWriter writer = new StringWriter();

		try {
			writeJSONString(array, writer, format);
			return writer.toString();
		} catch (IOException e) {
			// This should never happen for a StringWriter
			throw new RuntimeException(e);
		}
	}

	public void writeJSONString(Writer out, int format) throws IOException {
		writeJSONString(this, out, format);
	}

	@Override
	public void writeJSONString(Writer out) throws IOException {
		writeJSONString(this, out, 0);
	}

	@Override
	public String toString() {
		return toJSONString(this);
	}

	public String toPrettyString() {
		return toJSONString(this, 1);
	}

	public static <T extends Iterable> JSONArray<Object> convert(T in) {
		JSONArray<Object> myJsonArray = new JSONArray<>();
		for (Object obj : in) {
			myJsonArray.put(obj);
		}
		return myJsonArray;
	}

	public static <T> JSONArray<T> toJSONArray(String str) {
		JSONArray<T> rObject;
		JSONParser parser = new JSONParser();
		try {
			rObject = (JSONArray<T>) parser.parse(str);
		} catch (Exception e) {
			rObject = null;
		}
		return rObject;
	}

	public JSONObject mapsByKey(String keyName) {
		JSONObject rJson = new JSONObject();
		forEach(item -> {
			String vKey = ((JSONObject) item).getString(keyName);
			if( vKey != null ){
				rJson.put(vKey, item);
			}
		});
		return rJson;
	}

	public JSONArray joinOn(String fieldName, JSONArray foreignArray) {
		return this.joinOn(fieldName, fieldName, foreignArray);
	}

	public JSONArray joinOn(String ownFieldName, String foreignFieldName, JSONArray foreignArray) {
		return this.joinOn(ownFieldName, foreignFieldName, foreignArray, false);
	}

	public static boolean isInvalided(JSONArray array) {
		return array == null || array.size() == 0;
	}

	public JSONArray put(V obj) {
		add(obj);
		return this;
	}

	public JSONArray putIfNotNull(V obj) {
		if (obj != null) {
			this.put(obj);
		}
		return this;
	}

	public String getString(int idx) {
		Object val = this.get(idx);
		String rs;
		if (!(val instanceof String)) {
			try {
				rs = val.toString();
			} catch (Exception e) {
				try {
					rs = String.valueOf(val);
				} catch (Exception e2) {
					rs = null;
				}
			}
		} else {
			rs = (String) val;
		}
		return rs;
	}

	public <T> List<T> toArrayList() {
		List<T> list = new ArrayList<>();
		forEach(item -> list.add((T) item));
		return list;
	}

	public boolean has(V v) {
		return super.contains(v);
	}

	public JSONArray joinOn(String ownFieldName, String foreignFieldName, JSONArray foreignArray, boolean save_null_item) {
		if (foreignArray == null) {
			this.clear();
			return this;
		} else {
			HashMap<Object, JSONObject> quickMap = new HashMap<>();
			Iterator it = foreignArray.iterator();

			while (it.hasNext()) {
				Object obj = it.next();
				JSONObject item = (JSONObject) obj;
				if (item.containsKey(foreignFieldName)) {
					quickMap.put(item.getString(foreignFieldName), item);
				}
			}

			it = this.iterator();

			while (it.hasNext()) {
				JSONObject item = (JSONObject) it.next();
				if (!item.containsKey(ownFieldName)) {
					it.remove();
				} else {
					List<String> keyValueArray = new ArrayList<>(Arrays.asList(item.getString(ownFieldName).split(",")));
					Iterator<String> child_it = keyValueArray.iterator();
					JSONArray<JSONObject> rArray = new JSONArray<>();

					while (child_it.hasNext()) {
						String keyValue = child_it.next();
						if (!quickMap.containsKey(keyValue)) {
							if (!save_null_item) {
								child_it.remove();
							}
						} else {
							rArray.put(quickMap.get(keyValue));
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

	private JSONArray<JSONObject> sortJsonArray(String field, int sort) {
		JSONArray<JSONObject> newArray = new JSONArray<JSONObject>();
		Iterator<V> it = this.iterator();
		Iterator<V> it2;
		JSONObject item, item2;
		long sortValue, sortValue2;
		while (it.hasNext()) {
			item = (JSONObject) it.next();
			sortValue = item.getLong(field);

			it2 = this.iterator();
			while (it2.hasNext()) {
				item2 = (JSONObject) it2.next();
				sortValue2 = item2.getLong(field);
				if (sort == 1) {
					if (sortValue2 < sortValue) {
						item = item2;
						sortValue = sortValue2;
					}
				} else {
					if (sortValue2 > sortValue) {
						item = item2;
						sortValue = sortValue2;
					}
				}
			}

			this.remove(item);
			it = this.iterator();
			newArray.put(item);
		}
		return newArray;
	}

	public JSONArray<JSONObject> desc(String field) {
		return sortJsonArray(field, 0);
	}

	public JSONArray<JSONObject> asc(String field) {
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

	public JSONArray<V> step(int no) {
		JSONArray<V> outArray = new JSONArray<>();
		for (int i = no, l = this.size(); i < l; i++) {
			outArray.put(this.get(i));
		}
		return outArray;
	}

	public <O> JSONArray<V> filter(String field, Function<O, Boolean> cb) {

		this.removeIf(v -> !cb.apply((O) ((JSONObject) v).get(field)));
		return this;
	}


	public JSONArray<V> clone() {
		JSONArray<V> result = JSONArray.build();
		for (V v : this) {
			result.put(v);
		}
		return result;
	}


	public <O> JSONArray mapValue(String field, Function<O, Object> func) {
		for (Object v : this) {
			JSONObject item = (JSONObject) v;
			item.put(field, func.apply((O) item.get(field)));
		}
		return this;
	}


	public JSONArray mapKey(String field, Function<String, String> func) {
		JSONObject item;
		Object val;
		for (Object o : this) {
			item = (JSONObject) o;
			val = item.get(field);
			item.remove(field);
			item.put(func.apply(field), val);
		}
		return this;
	}

	public JSONArray flatMap(String field) {
		groups = new LinkedHashMap<>();
		Iterator<V> it = this.iterator();
		JSONObject item;
		Object groupValue;
		while (it.hasNext()) {
			item = (JSONObject) it.next();
			groupValue = item.get(field);
			if (!groups.containsKey(groupValue)) {
				groups.put(groupValue, new JSONArray<>());
			}
			groups.get(groupValue).put(item);
		}
		return this;
	}

	public JSONArray then(Function<JSONArray<JSONObject>, JSONArray<JSONObject>> func) {
		// 异常处理
		if (groups != null) {
			groups.replaceAll((k, v) -> func.apply(groups.get(k)));
		}
		return this;
	}

	public JSONArray<JSONObject> reduce() {
		JSONArray<JSONObject> outArray = new JSONArray<>();

		if (groups != null) {
			for (Object key : groups.keySet()) {
				outArray.put(groups.get(key));
			}
		}
		return outArray;
	}

	public JSONArray put(Collection<? extends V> c) {
		this.addAll(c);
		return this;
	}

	public JSONArray putIfNotNull(Collection<? extends V> c) {
		if (c != null) {
			this.put(c);
		}
		return this;
	}

	public JSONArray<JSONObject> mapReduce(String field, Function<JSONArray<JSONObject>, JSONArray<JSONObject>> reduceFunc) {
		return this.flatMap(field).then(reduceFunc).reduce();
	}
}
