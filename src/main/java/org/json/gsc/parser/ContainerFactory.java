package org.json.gsc.parser;

import java.util.List;
import java.util.Map;

public interface ContainerFactory {
    /**
     * @return A Map instance to store JSON object, or null if you want to use org.json.simple.JSONObject.
     */
    Map createObjectContainer();

    /**
     * @return A List instance to store JSON array, or null if you want to use org.json.simple.JSONArray.
     */
    List creatArrayContainer();
}
