package org.json.gsc;

import junit.framework.TestCase;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class JSONValueTest extends TestCase {
	public void testByteArrayToString() throws IOException {
		assertEquals("null", JSONValue.toJSONString(null));
		assertEquals("[]", JSONValue.toJSONString(new byte[0]));
		assertEquals("[12]", JSONValue.toJSONString(new byte[]{12}));
		assertEquals("[-7,22,86,-99]", JSONValue.toJSONString(new byte[]{-7, 22, 86, -99}));

		StringWriter writer;

		writer = new StringWriter();
		JSONValue.writeJSONString(null, writer);
		assertEquals("null", writer.toString());

		writer = new StringWriter();
		JSONValue.writeJSONString(new byte[0], writer);
		assertEquals("[]", writer.toString());

		writer = new StringWriter();
		JSONValue.writeJSONString(new byte[]{12}, writer);
		assertEquals("[12]", writer.toString());

		writer = new StringWriter();
		JSONValue.writeJSONString(new byte[]{-7, 22, 86, -99}, writer);
		assertEquals("[-7,22,86,-99]", writer.toString());
	}
	
	public void testShortArrayToString() throws IOException {
		assertEquals("null", JSONValue.toJSONString(null));
		assertEquals("[]", JSONValue.toJSONString(new short[0]));
		assertEquals("[12]", JSONValue.toJSONString(new short[]{12}));
		assertEquals("[-7,22,86,-99]", JSONValue.toJSONString(new short[]{-7, 22, 86, -99}));

		StringWriter writer;

		writer = new StringWriter();
		JSONValue.writeJSONString(null, writer);
		assertEquals("null", writer.toString());

		writer = new StringWriter();
		JSONValue.writeJSONString(new short[0], writer);
		assertEquals("[]", writer.toString());

		writer = new StringWriter();
		JSONValue.writeJSONString(new short[]{12}, writer);
		assertEquals("[12]", writer.toString());

		writer = new StringWriter();
		JSONValue.writeJSONString(new short[]{-7, 22, 86, -99}, writer);
		assertEquals("[-7,22,86,-99]", writer.toString());
	}
	
	public void testIntArrayToString() throws IOException {
		assertEquals("null", JSONValue.toJSONString(null));
		assertEquals("[]", JSONValue.toJSONString(new int[0]));
		assertEquals("[12]", JSONValue.toJSONString(new int[]{12}));
		assertEquals("[-7,22,86,-99]", JSONValue.toJSONString(new int[]{-7, 22, 86, -99}));

		StringWriter writer;

		writer = new StringWriter();
		JSONValue.writeJSONString(null, writer);
		assertEquals("null", writer.toString());

		writer = new StringWriter();
		JSONValue.writeJSONString(new int[0], writer);
		assertEquals("[]", writer.toString());

		writer = new StringWriter();
		JSONValue.writeJSONString(new int[]{12}, writer);
		assertEquals("[12]", writer.toString());

		writer = new StringWriter();
		JSONValue.writeJSONString(new int[]{-7, 22, 86, -99}, writer);
		assertEquals("[-7,22,86,-99]", writer.toString());
	}
	
	public void testLongArrayToString() throws IOException {
		assertEquals("null", JSONValue.toJSONString(null));
		assertEquals("[]", JSONValue.toJSONString(new long[0]));
		assertEquals("[12]", JSONValue.toJSONString(new long[]{12}));
		assertEquals("[-7,22,9223372036854775807,-99]", JSONValue.toJSONString(new long[]{-7, 22, 9223372036854775807L, -99}));

		StringWriter writer;

		writer = new StringWriter();
		JSONValue.writeJSONString(null, writer);
		assertEquals("null", writer.toString());

		writer = new StringWriter();
		JSONValue.writeJSONString(new long[0], writer);
		assertEquals("[]", writer.toString());

		writer = new StringWriter();
		JSONValue.writeJSONString(new long[]{12}, writer);
		assertEquals("[12]", writer.toString());

		writer = new StringWriter();
		JSONValue.writeJSONString(new long[]{-7, 22, 86, -99}, writer);
		assertEquals("[-7,22,86,-99]", writer.toString());
	}
	
	public void testFloatArrayToString() throws IOException {
		assertEquals("null", JSONValue.toJSONString(null));
		assertEquals("[]", JSONValue.toJSONString(new float[0]));
		assertEquals("[12.8]", JSONValue.toJSONString(new float[]{12.8f}));
		assertEquals("[-7.1,22.234,86.7,-99.02]", JSONValue.toJSONString(new float[]{-7.1f, 22.234f, 86.7f, -99.02f}));

		StringWriter writer;

		writer = new StringWriter();
		JSONValue.writeJSONString(null, writer);
		assertEquals("null", writer.toString());

		writer = new StringWriter();
		JSONValue.writeJSONString(new float[0], writer);
		assertEquals("[]", writer.toString());

		writer = new StringWriter();
		JSONValue.writeJSONString(new float[]{12.8f}, writer);
		assertEquals("[12.8]", writer.toString());

		writer = new StringWriter();
		JSONValue.writeJSONString(new float[]{-7.1f, 22.234f, 86.7f, -99.02f}, writer);
		assertEquals("[-7.1,22.234,86.7,-99.02]", writer.toString());
	}
	
	public void testDoubleArrayToString() throws IOException {
		assertEquals("null", JSONValue.toJSONString(null));
		assertEquals("[]", JSONValue.toJSONString(new double[0]));
		assertEquals("[12.8]", JSONValue.toJSONString(new double[]{12.8}));
		assertEquals("[-7.1,22.234,86.7,-99.02]", JSONValue.toJSONString(new double[]{-7.1, 22.234, 86.7, -99.02}));

		StringWriter writer;

		writer = new StringWriter();
		JSONValue.writeJSONString(null, writer);
		assertEquals("null", writer.toString());

		writer = new StringWriter();
		JSONValue.writeJSONString(new double[0], writer);
		assertEquals("[]", writer.toString());

		writer = new StringWriter();
		JSONValue.writeJSONString(new double[]{12.8}, writer);
		assertEquals("[12.8]", writer.toString());

		writer = new StringWriter();
		JSONValue.writeJSONString(new double[]{-7.1, 22.234, 86.7, -99.02}, writer);
		assertEquals("[-7.1,22.234,86.7,-99.02]", writer.toString());
	}
	
	public void testBooleanArrayToString() throws IOException {
		assertEquals("null", JSONValue.toJSONString(null));
		assertEquals("[]", JSONValue.toJSONString(new boolean[0]));
		assertEquals("[true]", JSONValue.toJSONString(new boolean[]{true}));
		assertEquals("[true,false,true]", JSONValue.toJSONString(new boolean[]{true, false, true}));

		StringWriter writer;

		writer = new StringWriter();
		JSONValue.writeJSONString(null, writer);
		assertEquals("null", writer.toString());

		writer = new StringWriter();
		JSONValue.writeJSONString(new boolean[0], writer);
		assertEquals("[]", writer.toString());

		writer = new StringWriter();
		JSONValue.writeJSONString(new boolean[]{true}, writer);
		assertEquals("[true]", writer.toString());

		writer = new StringWriter();
		JSONValue.writeJSONString(new boolean[]{true, false, true}, writer);
		assertEquals("[true,false,true]", writer.toString());
	}
	
	public void testCharArrayToString() throws IOException {
		assertEquals("null", JSONValue.toJSONString(null));
		assertEquals("[]", JSONValue.toJSONString(new char[0]));
		assertEquals("[\"a\"]", JSONValue.toJSONString(new char[]{'a'}));
		assertEquals("[\"a\",\"b\",\"c\"]", JSONValue.toJSONString(new char[]{'a', 'b', 'c'}));

		StringWriter writer;

		writer = new StringWriter();
		JSONValue.writeJSONString(null, writer);
		assertEquals("null", writer.toString());

		writer = new StringWriter();
		JSONValue.writeJSONString(new char[0], writer);
		assertEquals("[]", writer.toString());

		writer = new StringWriter();
		JSONValue.writeJSONString(new char[]{'a'}, writer);
		assertEquals("[\"a\"]", writer.toString());

		writer = new StringWriter();
		JSONValue.writeJSONString(new char[]{'a', 'b', 'c'}, writer);
		assertEquals("[\"a\",\"b\",\"c\"]", writer.toString());
	}
	
	public void testObjectArrayToString() throws IOException {
		assertEquals("null", JSONValue.toJSONString(null));
		assertEquals("[]", JSONValue.toJSONString(new Object[0]));
		assertEquals("[\"Hello\"]", JSONValue.toJSONString(new Object[]{"Hello"}));
		assertEquals("[\"Hello\",12,[1,2,3]]", JSONValue.toJSONString(new Object[]{"Hello", new Integer(12), new int[]{1, 2, 3}}));

		StringWriter writer;

		writer = new StringWriter();
		JSONValue.writeJSONString(null, writer);
		assertEquals("null", writer.toString());

		writer = new StringWriter();
		JSONValue.writeJSONString(new Object[0], writer);
		assertEquals("[]", writer.toString());

		writer = new StringWriter();
		JSONValue.writeJSONString(new Object[]{"Hello"}, writer);
		assertEquals("[\"Hello\"]", writer.toString());

		writer = new StringWriter();
		JSONValue.writeJSONString(new Object[]{"Hello", new Integer(12), new int[]{1, 2, 3}}, writer);
		assertEquals("[\"Hello\",12,[1,2,3]]", writer.toString());
	}
	
	public void testArraysOfArrays() throws IOException {
		
		StringWriter writer;
		
		final int[][][] nestedIntArray = new int[][][]{{{1}, {5}}, {{2}, {6}}};
		final String expectedNestedIntString = "[[[1],[5]],[[2],[6]]]";
		
		assertEquals(expectedNestedIntString, JSONValue.toJSONString(nestedIntArray));
		
		writer = new StringWriter();
		JSONValue.writeJSONString(nestedIntArray, writer);
		assertEquals(expectedNestedIntString, writer.toString());

		final String[][] nestedStringArray = new String[][]{{"a", "b"}, {"c", "d"}};
		final String expectedNestedStringString = "[[\"a\",\"b\"],[\"c\",\"d\"]]";

		assertEquals(expectedNestedStringString, JSONValue.toJSONString(nestedStringArray));

		writer = new StringWriter();
		JSONValue.writeJSONString(nestedStringArray, writer);
		assertEquals(expectedNestedStringString, writer.toString());
	}

	public void testSimpleJsonToObject() {
		// json->object
		JSONObject json = JSONObject.build();
		ChildData o = json.put("d", 12.d).put("f", 16.f).mapper(ChildData.class);
		assertEquals(o.getD(), 12.d);
		assertEquals(o.getF(), 16.f);
	}

	public void testJsonToObject() {
		JSONArray cdArr = JSONArray.<JSONObject>build();
		for (int i = 0; i < 3; i++) {
			cdArr.put(JSONObject.build("d", 55.d + i).put("f", 66.f + i));
		}
		// json->object
		JSONObject json = JSONObject.build();
		Data o = json
				.put("name", "putao520")
				.put("val", 16)
				.put("child", JSONObject.build("d", 55.d).put("f", 66.f))
				.put("childArr", cdArr)
				.put("json", JSONObject.build("first", "yu").put("second", "yue"))
				.put("jsonArray", JSONArray.build().put("1").put("2").put("3"))
				.put("jsonArrayJson", JSONArray.<JSONObject>build()
						.put(JSONObject.build("id", 1))
						.put(JSONObject.build("id", 2))
						.put(JSONObject.build("id", 3))
				)
				.mapper(Data.class);
		assertEquals(o.getName(), "putao520");
		assertEquals(o.getVal(), 16);
		ChildData rCd = o.getChild();
		assertEquals(rCd.getD(), 55.d);
		assertEquals(rCd.getF(), 66.f);
		ChildData[] rCdArr = o.getChildArr();
		for (int i = 0; i < 3; i++) {
			ChildData _cd = rCdArr[i];
			assertEquals(_cd.getD(), 55.d + i);
			assertEquals(_cd.getF(), 66.f + i);
		}
		JSONObject j = o.getJson();
		assertEquals(j.getString("first"), "yu");
		assertEquals(j.getString("second"), "yue");
		JSONArray<String> ja = o.getJsonArray();
		for (int i = 0; i < 3; i++) {
			assertTrue(ja.get(i).equals(String.valueOf(i + 1)));
		}
		JSONArray<JSONObject> jaj = o.getJsonArrayJson();
		System.out.println(jaj);
		for (JSONObject v : jaj) {
			assertTrue(v.containsKey("id"));
		}
	}

	public void testSimpleObjectToJson() {
		// object->json
		ChildData cd = new ChildData();
		cd.setD(12.d);
		cd.setF(16.f);
		JSONObject j = JSONObject.mapper(cd);
		assertEquals(j.getDouble("d"), 12.d);
		assertEquals(j.getFloat("f"), 16.f);
	}

	public void testObjectToJson() {
		ChildData cd = new ChildData();
		cd.setD(55.d);
		cd.setF(66.f);

		List<ChildData> cdArr = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			ChildData _cd = new ChildData();
			_cd.setD(55.d + i);
			_cd.setF(66.f + i);
			cdArr.add(_cd);
		}
		ChildData[] cds = new ChildData[cdArr.size()];
		cdArr.toArray(cds);

		// object->json
		Data da = new Data();
		da.setName("putao520");
		da.setVal(16);
		da.setChild(cd);
		da.setChildArr(cds);
		da.setJson(JSONObject.build("first", "yu").put("second", "yue"));
		da.setJsonArray(JSONArray.build().put("1").put("2").put("3"));
		da.setJsonArrayJson(JSONArray.<JSONObject>build()
				.put(JSONObject.build("id", 1))
				.put(JSONObject.build("id", 2))
				.put(JSONObject.build("id", 3))
		);
		JSONObject o = JSONObject.mapper(da);
		System.out.println("test name");
		assertEquals(o.getString("name"), "putao520");
		System.out.println("test val");
		assertEquals(o.getInt("val"), 16);
		System.out.println("test child");
		JSONObject j = o.getJson("child");
		assertEquals(j.getDouble("d"), 55.d);
		assertEquals(j.getFloat("f"), 66.f);
		JSONArray<JSONObject> jArr = o.getJsonArray("childArr");
		System.out.println(jArr);
		for (JSONObject v : jArr) {
			assertTrue(v.containsKey("d"));
			assertTrue(v.containsKey("f"));
		}
		JSONObject rj = o.getJson("json");
		assertEquals(rj.getString("first"), "yu");
		assertEquals(rj.getString("second"), "yue");
		JSONArray<String> ra = o.getJsonArray("jsonArray");
		for (int i = 0; i < 3; i++) {
			assertTrue(ra.getString(i).equals(String.valueOf(i + 1)));
		}
		JSONArray<JSONObject> raj = o.getJsonArray("jsonArrayJson");
		for (int i = 0; i < 3; i++) {
			assertEquals(raj.getJson(i).getInt("id"), (i + 1));
		}
	}
}
