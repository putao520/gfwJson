package org.json.gsc;

public class Data {
    private String name;
    private Integer val;
    private ChildData child;
    private ChildData[] childArr;
    private JSONObject json;
    private JSONArray jsonArray;
    private JSONArray<JSONObject> jsonArrayJson;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }

    public ChildData getChild() {
        return child;
    }

    public void setChild(ChildData child) {
        this.child = child;
    }

    public ChildData[] getChildArr() {
        return childArr;
    }

    public void setChildArr(ChildData[] childArr) {
        this.childArr = childArr;
    }

    public JSONObject getJson() {
        return json;
    }

    public void setJson(JSONObject json) {
        this.json = json;
    }

    public JSONArray getJsonArray() {
        return jsonArray;
    }

    public void setJsonArray(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    public JSONArray<JSONObject> getJsonArrayJson() {
        return jsonArrayJson;
    }

    public void setJsonArrayJson(JSONArray<JSONObject> jsonArrayJson) {
        this.jsonArrayJson = jsonArrayJson;
    }
}
