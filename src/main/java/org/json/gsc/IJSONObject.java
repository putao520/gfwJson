package org.json.gsc;

public interface IJSONObject<S> {
    Object get(String key, Object defaultValue);

    String getString(String key);

    int getInt(String key);

    double getDouble(String key);

    float getFloat(String key);

    long getLong(String key);

    boolean getBoolean(String key);

    JSONObject getJson(String key);

    <T> JSONArray<T> getJsonArray(String key);

    boolean has(String key);
}
