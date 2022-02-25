package org.json.gsc;

public interface IJSONArray<S> {
    Object get(int idx);

    String getString(int idx);

    int getInt(int idx);

    double getDouble(int idx);

    float getFloat(int idx);

    long getLong(int idx);

    boolean getBoolean(int idx);

    JSONObject getJson(int idx);

    <T> JSONArray<T> getJsonArray(int idx);
}
