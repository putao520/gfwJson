/*
 * $Id: JSONValue.java,v 1.1 2006/04/15 14:37:04 platform Exp $
 * Created on 2006-4-15
 */
package org.json.gsc;

import org.json.gsc.parser.JSONParser;
import org.json.gsc.parser.ParseException;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;


public class JSONValue {
    public static Object parse(Reader in) {
        try {
            JSONParser parser = new JSONParser();
            return parser.parse(in);
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public static Object parse(String s) {
        StringReader in = new StringReader(s);
        try {
            return parseWithException(in);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object parseWithException(Reader in) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        return parser.parse(in);
    }

    public static Object parseWithException(String s) throws ParseException {
        JSONParser parser = new JSONParser();
        return parser.parse(s);
    }

    public static void writeJSONString(Object value, Writer out) throws IOException {
        if (value == null) {
            out.write("null");
            return;
        }

        if (value instanceof String) {
            out.write('\"');
            out.write(escape((String) value));
            out.write('\"');
            return;
        }

        if (value instanceof Double) {
            if (((Double) value).isInfinite() || ((Double) value).isNaN())
                out.write("null");
            else
                out.write(value.toString());
            return;
        }

        if (value instanceof Float) {
            if (((Float) value).isInfinite() || ((Float) value).isNaN())
                out.write("null");
            else
                out.write(value.toString());
            return;
        }

        if (value instanceof Number) {
            out.write(value.toString());
            return;
        }

        if (value instanceof Boolean) {
            out.write(value.toString());
            return;
        }

        if ((value instanceof JSONStreamAware)) {
            ((JSONStreamAware) value).writeJSONString(out);
            return;
        }

        if ((value instanceof JSONAware)) {
            out.write((value).toString());
            return;
        }

        if (value instanceof Map) {
            JSONObject.writeJSONString((Map) value, out, 0);
            return;
        }

        if (value instanceof Collection) {
            JSONArray.writeJSONString((Collection) value, out, 0);
            return;
        }

        if (value instanceof byte[]) {
            JSONArray.writeJSONString((byte[]) value, out);
            return;
        }

        if (value instanceof short[]) {
            JSONArray.writeJSONString((short[]) value, out);
            return;
        }

        if (value instanceof int[]) {
            JSONArray.writeJSONString((int[]) value, out);
            return;
        }

        if (value instanceof long[]) {
            JSONArray.writeJSONString((long[]) value, out);
            return;
        }

        if (value instanceof float[]) {
            JSONArray.writeJSONString((float[]) value, out);
            return;
        }

        if (value instanceof double[]) {
            JSONArray.writeJSONString((double[]) value, out);
            return;
        }

        if (value instanceof boolean[]) {
            JSONArray.writeJSONString((boolean[]) value, out);
            return;
        }

        if (value instanceof char[]) {
            JSONArray.writeJSONString((char[]) value, out);
            return;
        }

        if (value instanceof Object[]) {
            JSONArray.writeJSONString((Object[]) value, out);
            return;
        }

        out.write(value.toString());
    }

    public static String toJSONString(Object value) {
        final StringWriter writer = new StringWriter();

        try {
            writeJSONString(value, writer);
            return writer.toString();
        } catch (IOException e) {
            // This should never happen for a StringWriter
            throw new RuntimeException(e);
        }
    }

    public static String escape(String s) {
        if (s == null)
            return null;
        StringBuffer sb = new StringBuffer();
        escape(s, sb);
        return sb.toString();
    }

    static void escape(String s, StringBuffer sb) {
        final int len = s.length();
        for (int i = 0; i < len; i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                default:
                    //Reference: http://www.unicode.org/versions/Unicode5.1.0/
                    if (ch <= '\u001F' || ch >= '\u007F' && ch <= '\u009F' || ch >= '\u2000' && ch <= '\u20FF') {
                        String ss = Integer.toHexString(ch);
                        sb.append("\\u");
                        sb.append("0".repeat(4 - ss.length()));
                        sb.append(ss.toUpperCase());
                    } else {
                        sb.append(ch);
                    }
            }
        }//for
    }

    public static int IntValue(Object val) {
        int ri = 0;
        try {
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

        } catch (Exception ignored) {
        }
        return ri;
    }

    public static double DoubleValue(Object val) {
        double ri = 0.00;
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
                ri = Double.parseDouble((String) val);
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

    public static float FloatValue(Object val) {
        float ri = 0.00f;
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

    public static long LongValue(Object val) {
        long ri = 0;
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

        } catch (Exception e) {
            ri = 0L;
        }
        return ri;
    }

    public static boolean BooleanValue(Object val) {
        boolean ri;
        try {
            ri = (boolean) val;
        } catch (Exception e) {
            if (val instanceof String v) {
                if (v.equals("0")) {
                    ri = false;
                } else if (v.equals("1")) {
                    ri = true;
                } else {
                    ri = Boolean.parseBoolean(v);
                }
            } else if (val instanceof Long l) {
                ri = l == 1L;
            } else if (val instanceof Integer l) {
                ri = l == 1;
            } else {
                ri = false;
            }
        }
        return ri;
    }

    public static JSONObject JsonValue(Object val) {
        if (val instanceof JSONObject) {
            return (JSONObject) val;
        }
        if (val instanceof String) {
            return JSONObject.toJSON((String) val);
        }
        try {
            val = JSONObject.toJSON(val.toString());
        } catch (Exception e) {
            val = null;
        }
        return (JSONObject) val;
    }

    public static <T> JSONArray<T> JsonArrayValue(Object val) {
        if (val instanceof JSONArray) {
            return (JSONArray<T>) val;
        }
        if (val instanceof String) {
            return JSONArray.toJSONArray((String) val);
        }
        if (val instanceof JSONObject) {
            return JSONArray.build((T) val);
        }
        if (val instanceof ArrayList<?>) {
            JSONArray rArray = new JSONArray<T>();
            ((List<?>) val).forEach(rArray::add);
            return rArray;
        }
        try {
            val = JSONArray.<T>toJSONArray(val.toString());
        } catch (Exception e) {
            val = JSONArray.<T>build();
        }
        return (JSONArray<T>) val;
    }
}
