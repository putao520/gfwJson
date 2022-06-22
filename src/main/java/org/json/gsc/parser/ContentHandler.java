package org.json.gsc.parser;

import java.io.IOException;

public interface ContentHandler {

    void startJSON() throws ParseException, IOException;

    void endJSON() throws ParseException, IOException;

    boolean startObject() throws ParseException, IOException;

    boolean endObject() throws ParseException, IOException;

    boolean startObjectEntry(String key) throws ParseException, IOException;

    boolean endObjectEntry() throws ParseException, IOException;

    boolean startArray() throws ParseException, IOException;

    boolean endArray() throws ParseException, IOException;

    /**
     * Receive notification of the JSON primitive values:
     * java.lang.String,
     * java.lang.Number,
     * java.lang.Boolean
     * null
     *
     * @param value - Instance of the following:
     *              java.lang.String,
     *              java.lang.Number,
     *              java.lang.Boolean
     *              null
     * @return false if the handler wants to stop parsing after return.
     * @throws ParseException parse error
     * @throws IOException    io error
     */
    boolean primitive(Object value) throws ParseException, IOException;

}
