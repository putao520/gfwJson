/*
 * $Id: JSONParser.java,v 1.1 2006/04/15 14:10:48 platform Exp $
 * Created on 2006-4-15
 */
package org.json.gsc.parser;

import org.json.gsc.JSONArray;
import org.json.gsc.JSONObject;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class JSONParser {
    // public static final int MAX_ITEM = 1;
    public static final int MAX_ITEM = 5000;
    public static final int S_INIT = 0;
    public static final int S_IN_FINISHED_VALUE = 1;//string,number,boolean,null,object,array
    public static final int S_IN_OBJECT = 2;
    public static final int S_IN_ARRAY = 3;
    public static final int S_PASSED_PAIR_KEY = 4;
    public static final int S_IN_PAIR_VALUE = 5;
    public static final int S_END = 6;
    public static final int S_IN_ERROR = -1;
    private final Yylex lexer = new Yylex((Reader) null);
    private Function<Map, Boolean> onMapOverflow;
    private Function<List, Boolean> onListOverflow;
    private int preloadListSize = 0;
    private LinkedList<?> handlerStatusStack;
    private Yytoken token = null;
    private int status = S_INIT;
    private int nearPosition = 0;

    public JSONParser() {
    }

    // 最大字符串值，大于它时会触发读错误，导致不返回字符串，而是返回字符索引号
    public JSONParser(int maxSize) {
        lexer.setMaxSize(maxSize);
    }

    public JSONParser onMapOverflow(Function<Map, Boolean> fn) {
        this.onMapOverflow = fn;
        return this;
    }

    public JSONParser onListOverflow(Function<List, Boolean> fn) {
        this.onListOverflow = fn;
        return this;
    }

    private int peekStatus(LinkedList<?> statusStack) {
        if (statusStack.size() == 0)
            return -1;
        return (Integer) statusStack.getFirst();
    }

    /**
     * Reset the parser to the initial state without resetting the underlying reader.
     */
    public void reset() {
        preloadListSize = 0;
        nearPosition = 0;
        token = null;
        status = S_INIT;
        handlerStatusStack = null;
    }

    /**
     * Reset the parser to the initial state with a new character reader.
     *
     * @param in - The new character reader.
     */
    public void reset(Reader in) {
        lexer.yyreset(in);
        reset();
    }

    /**
     * @return The position of the beginning of the current token.
     */
    public int getPosition() {
        return lexer.getPosition();
    }

    /**
     * @return 返回可用有效的字节在块中的位置
     */
    public int availablePosition() {
        return nearPosition;
    }

    public Object parse(String s) throws ParseException {
        return parse(s, (ContainerFactory) null);
    }

    public Object parse(String s, ContainerFactory containerFactory) throws ParseException {
        StringReader in = new StringReader(s);
        try {
            return parse(in, containerFactory);
        } catch (IOException ie) {
            /*
             * Actually it will never happen.
             */
            throw new ParseException(-1, ParseException.ERROR_UNEXPECTED_EXCEPTION, ie);
        }
    }

    public Object parse(Reader in) throws IOException, ParseException {
        return parse(in, (ContainerFactory) null);
    }

    public Object parse(Reader in, ContainerFactory containerFactory) throws IOException, ParseException {
        return parse(in, containerFactory, true);
    }

    // true 继续执行
    private boolean pushMap(Map m, String key, Object value) {
        if (m.size() >= MAX_ITEM && onMapOverflow != null) {
            if (onMapOverflow.apply(m)) {
                return false;
            }
            m.clear();
        }
        m.put(key, value);
        return true;
    }

    // true 继续执行
    private boolean addList(List l, Object value) {
        if (l.size() >= MAX_ITEM && onListOverflow != null) {
            if (onListOverflow.apply(l)) {
                return false;
            }
            preloadListSize += l.size();
            l.clear();
        }
        l.add(value);
        return true;
    }

    // 获得已经读的列表长度
    public int getPreloadListSize() {
        return preloadListSize;
    }

    /**
     * Parse JSON text into java object from the input source.
     *
     * @param in               input
     * @param containerFactory - Use this factory to createyour own JSON object and JSON array containers.
     * @param compatibility    - 支持不完整的json结构
     * @return Instance of the following:
     * org.json.simple.JSONObject,
     * org.json.simple.JSONArray,
     * java.lang.String,
     * java.lang.Number,
     * java.lang.Boolean,
     * null
     * @throws IOException    io error
     * @throws ParseException parse error
     */
    public Object parse(Reader in, ContainerFactory containerFactory, boolean compatibility) throws IOException, ParseException {
        reset(in);
        LinkedList<Integer> statusStack = new LinkedList<>();
        LinkedList<Object> valueStack = new LinkedList<>();

        try {
            do {
                nearPosition = getPosition();
                nextToken();
                switch (status) {
                    // 开始字符串
                    case S_INIT:
                        switch (token.type) {
                            // 值
                            case Yytoken.TYPE_VALUE -> {
                                status = S_IN_FINISHED_VALUE;
                                statusStack.addFirst(status);
                                valueStack.addFirst(token.value);
                            }
                            // 左花括号 {
                            case Yytoken.TYPE_LEFT_BRACE -> {
                                status = S_IN_OBJECT;
                                statusStack.addFirst(status);
                                valueStack.addFirst(createObjectContainer(containerFactory));
                            }
                            // 左中括号 [
                            case Yytoken.TYPE_LEFT_SQUARE -> {
                                status = S_IN_ARRAY;
                                statusStack.addFirst(status);
                                valueStack.addFirst(createArrayContainer(containerFactory));
                            }
                            default -> status = S_IN_ERROR;
                        }//inner switch
                        break;
                    // 结束值
                    case S_IN_FINISHED_VALUE:
                        if (token.type == Yytoken.TYPE_EOF)
                            return valueStack.removeFirst();
                        else {
                            if (compatibility) {
                                return valueStack.removeFirst();
                            } else {
                                throw new ParseException(getPosition(), ParseException.ERROR_UNEXPECTED_TOKEN, token);
                            }
                        }
                        // 是 JsonObject
                    case S_IN_OBJECT:
                        switch (token.type) {
                            case Yytoken.TYPE_COMMA:
                                break;
                            case Yytoken.TYPE_VALUE:
                                if (token.value instanceof String key) {
                                    valueStack.addFirst(key);
                                    status = S_PASSED_PAIR_KEY;
                                    statusStack.addFirst(status);
                                } else {
                                    status = S_IN_ERROR;
                                }
                                break;
                            case Yytoken.TYPE_RIGHT_BRACE:
                                if (valueStack.size() > 1) {
                                    statusStack.removeFirst();
                                    valueStack.removeFirst();
                                    status = peekStatus(statusStack);
                                } else {
                                    status = S_IN_FINISHED_VALUE;
                                }
                                break;
                            default:
                                status = S_IN_ERROR;
                                break;
                        }//inner switch
                        break;
                    // 解析key与value分离
                    case S_PASSED_PAIR_KEY:
                        switch (token.type) {
                            // 冒号:
                            case Yytoken.TYPE_COLON:
                                break;
                            // 值
                            case Yytoken.TYPE_VALUE:
                                statusStack.removeFirst();
                                String key = (String) valueStack.removeFirst();
                                Map parent = (Map) valueStack.getFirst();
                                // 这里插入 parent 溢出处理
                                if (!pushMap(parent, key, token.value)) {
                                    // 故意制造错误
                                    status = S_IN_ERROR;
                                } else {
                                    status = peekStatus(statusStack);
                                }
                                break;
                            // 左[
                            case Yytoken.TYPE_LEFT_SQUARE:
                                statusStack.removeFirst();
                                key = (String) valueStack.removeFirst();
                                parent = (Map) valueStack.getFirst();
                                List newArray = createArrayContainer(containerFactory);
                                if (!pushMap(parent, key, newArray)) {
                                    status = S_IN_ERROR;
                                } else {
                                    status = S_IN_ARRAY;
                                }
                                statusStack.addFirst(status);
                                valueStack.addFirst(newArray);
                                break;
                            // 左 {
                            case Yytoken.TYPE_LEFT_BRACE:
                                statusStack.removeFirst();
                                key = (String) valueStack.removeFirst();
                                parent = (Map) valueStack.getFirst();
                                Map newObject = createObjectContainer(containerFactory);
                                if (!pushMap(parent, key, newObject)) {
                                    status = S_IN_ERROR;
                                } else {
                                    status = S_IN_OBJECT;
                                }
                                statusStack.addFirst(status);
                                valueStack.addFirst(newObject);
                                break;
                            default:
                                status = S_IN_ERROR;
                        }
                        break;
                    // JsonArray
                    case S_IN_ARRAY:
                        switch (token.type) {
                            // ,
                            case Yytoken.TYPE_COMMA:
                                break;
                            // 值
                            case Yytoken.TYPE_VALUE:
                                List val = (List) valueStack.getFirst();
                                if (!addList(val, token.value)) {
                                    status = S_IN_ERROR;
                                }
                                break;
                            // [
                            case Yytoken.TYPE_RIGHT_SQUARE:
                                if (valueStack.size() > 1) {
                                    statusStack.removeFirst();
                                    valueStack.removeFirst();
                                    status = peekStatus(statusStack);
                                } else {
                                    status = S_IN_FINISHED_VALUE;
                                }
                                break;
                            // {
                            case Yytoken.TYPE_LEFT_BRACE:
                                val = (List) valueStack.getFirst();
                                Map newObject = createObjectContainer(containerFactory);
                                if (!addList(val, newObject)) {
                                    status = S_IN_ERROR;
                                } else {
                                    status = S_IN_OBJECT;
                                }
                                statusStack.addFirst(status);
                                valueStack.addFirst(newObject);
                                break;
                            // [
                            case Yytoken.TYPE_LEFT_SQUARE:
                                val = (List) valueStack.getFirst();
                                List newArray = createArrayContainer(containerFactory);
                                if (!addList(val, newArray)) {
                                    status = S_IN_ERROR;
                                } else {
                                    status = S_IN_ARRAY;
                                }
                                statusStack.addFirst(status);
                                valueStack.addFirst(newArray);
                                break;
                            default:
                                status = S_IN_ERROR;
                        }//inner switch
                        break;
                    case S_IN_ERROR:
                        throw new ParseException(getPosition(), ParseException.ERROR_UNEXPECTED_TOKEN, token);
                }//switch
                if (status == S_IN_ERROR) {
                    if (compatibility) {
                        return valueStack.size() > 0 ? valueStack.removeFirst() : null;
                    } else {
                        throw new ParseException(getPosition(), ParseException.ERROR_UNEXPECTED_TOKEN, token);
                    }
                }
            } while (token.type != Yytoken.TYPE_EOF);
        } catch (IOException ie) {
            throw ie;
        }

        throw new ParseException(getPosition(), ParseException.ERROR_UNEXPECTED_TOKEN, token);
    }

    private void nextToken() throws ParseException, IOException {
        token = lexer.yylex();
        if (token == null)
            token = new Yytoken(Yytoken.TYPE_EOF, null);
    }

    private Map createObjectContainer(ContainerFactory containerFactory) {
        if (containerFactory == null)
            return new JSONObject();
        Map m = containerFactory.createObjectContainer();

        if (m == null)
            return new JSONObject();
        return m;
    }

    private List createArrayContainer(ContainerFactory containerFactory) {
        if (containerFactory == null)
            return new JSONArray();
        List l = containerFactory.creatArrayContainer();

        if (l == null)
            return new JSONArray();
        return l;
    }

    public void parse(String s, ContentHandler contentHandler) throws ParseException {
        parse(s, contentHandler, false);
    }

    public void parse(String s, ContentHandler contentHandler, boolean isResume) throws ParseException {
        StringReader in = new StringReader(s);
        try {
            parse(in, contentHandler, isResume);
        } catch (IOException ie) {
            /*
             * Actually it will never happen.
             */
            throw new ParseException(-1, ParseException.ERROR_UNEXPECTED_EXCEPTION, ie);
        }
    }

    public void parse(Reader in, ContentHandler contentHandler) throws IOException, ParseException {
        parse(in, contentHandler, false);
    }

    /**
     * Stream processing of JSON text.
     *
     * @param in             input
     * @param contentHandler content
     * @param isResume       - Indicates if it continues previous parsing operation.
     *                       If set to true, resume parsing the old stream, and parameter 'in' will be ignored.
     *                       If this method is called for the first time in this instance, isResume will be ignored.
     * @throws IOException    io
     * @throws ParseException parse
     * @see ContentHandler
     */
    public void parse(Reader in, ContentHandler contentHandler, boolean isResume) throws IOException, ParseException {
        if (!isResume) {
            reset(in);
            handlerStatusStack = new LinkedList();
        } else {
            if (handlerStatusStack == null) {
                isResume = false;
                reset(in);
                handlerStatusStack = new LinkedList();
            }
        }

        LinkedList statusStack = handlerStatusStack;

        try {
            do {
                switch (status) {
                    case S_INIT:
                        contentHandler.startJSON();
                        nextToken();
                        switch (token.type) {
                            case Yytoken.TYPE_VALUE -> {
                                status = S_IN_FINISHED_VALUE;
                                statusStack.addFirst(status);
                                if (!contentHandler.primitive(token.value))
                                    return;
                            }
                            case Yytoken.TYPE_LEFT_BRACE -> {
                                status = S_IN_OBJECT;
                                statusStack.addFirst(status);
                                if (!contentHandler.startObject())
                                    return;
                            }
                            case Yytoken.TYPE_LEFT_SQUARE -> {
                                status = S_IN_ARRAY;
                                statusStack.addFirst(status);
                                if (!contentHandler.startArray())
                                    return;
                            }
                            default -> status = S_IN_ERROR;
                        }//inner switch
                        break;

                    case S_IN_FINISHED_VALUE:
                        nextToken();
                        if (token.type == Yytoken.TYPE_EOF) {
                            contentHandler.endJSON();
                            status = S_END;
                            return;
                        } else {
                            status = S_IN_ERROR;
                            throw new ParseException(getPosition(), ParseException.ERROR_UNEXPECTED_TOKEN, token);
                        }

                    case S_IN_OBJECT:
                        nextToken();
                        switch (token.type) {
                            case Yytoken.TYPE_COMMA:
                                break;
                            case Yytoken.TYPE_VALUE:
                                if (token.value instanceof String key) {
                                    status = S_PASSED_PAIR_KEY;
                                    statusStack.addFirst(status);
                                    if (!contentHandler.startObjectEntry(key))
                                        return;
                                } else {
                                    status = S_IN_ERROR;
                                }
                                break;
                            case Yytoken.TYPE_RIGHT_BRACE:
                                if (statusStack.size() > 1) {
                                    statusStack.removeFirst();
                                    status = peekStatus(statusStack);
                                } else {
                                    status = S_IN_FINISHED_VALUE;
                                }
                                if (!contentHandler.endObject())
                                    return;
                                break;
                            default:
                                status = S_IN_ERROR;
                                break;
                        }//inner switch
                        break;

                    case S_PASSED_PAIR_KEY:
                        nextToken();
                        switch (token.type) {
                            case Yytoken.TYPE_COLON:
                                break;
                            case Yytoken.TYPE_VALUE:
                                statusStack.removeFirst();
                                status = peekStatus(statusStack);
                                if (!contentHandler.primitive(token.value))
                                    return;
                                if (!contentHandler.endObjectEntry())
                                    return;
                                break;
                            case Yytoken.TYPE_LEFT_SQUARE:
                                statusStack.removeFirst();
                                statusStack.addFirst(S_IN_PAIR_VALUE);
                                status = S_IN_ARRAY;
                                statusStack.addFirst(status);
                                if (!contentHandler.startArray())
                                    return;
                                break;
                            case Yytoken.TYPE_LEFT_BRACE:
                                statusStack.removeFirst();
                                statusStack.addFirst(S_IN_PAIR_VALUE);
                                status = S_IN_OBJECT;
                                statusStack.addFirst(status);
                                if (!contentHandler.startObject())
                                    return;
                                break;
                            default:
                                status = S_IN_ERROR;
                        }
                        break;

                    case S_IN_PAIR_VALUE:
                        /*
                         * S_IN_PAIR_VALUE is just a marker to indicate the end of an object entry, it doesn't proccess any token,
                         * therefore delay consuming token until next round.
                         */
                        statusStack.removeFirst();
                        status = peekStatus(statusStack);
                        if (!contentHandler.endObjectEntry())
                            return;
                        break;

                    case S_IN_ARRAY:
                        nextToken();
                        switch (token.type) {
                            case Yytoken.TYPE_COMMA:
                                break;
                            case Yytoken.TYPE_VALUE:
                                if (!contentHandler.primitive(token.value))
                                    return;
                                break;
                            case Yytoken.TYPE_RIGHT_SQUARE:
                                if (statusStack.size() > 1) {
                                    statusStack.removeFirst();
                                    status = peekStatus(statusStack);
                                } else {
                                    status = S_IN_FINISHED_VALUE;
                                }
                                if (!contentHandler.endArray())
                                    return;
                                break;
                            case Yytoken.TYPE_LEFT_BRACE:
                                status = S_IN_OBJECT;
                                statusStack.addFirst(status);
                                if (!contentHandler.startObject())
                                    return;
                                break;
                            case Yytoken.TYPE_LEFT_SQUARE:
                                status = S_IN_ARRAY;
                                statusStack.addFirst(status);
                                if (!contentHandler.startArray())
                                    return;
                                break;
                            default:
                                status = S_IN_ERROR;
                        }//inner switch
                        break;

                    case S_END:
                        return;

                    case S_IN_ERROR:
                        throw new ParseException(getPosition(), ParseException.ERROR_UNEXPECTED_TOKEN, token);
                }//switch
                if (status == S_IN_ERROR) {
                    throw new ParseException(getPosition(), ParseException.ERROR_UNEXPECTED_TOKEN, token);
                }
            } while (token.type != Yytoken.TYPE_EOF);
        } catch (IOException | Error | RuntimeException | ParseException ie) {
            status = S_IN_ERROR;
            throw ie;
        }

        status = S_IN_ERROR;
        throw new ParseException(getPosition(), ParseException.ERROR_UNEXPECTED_TOKEN, token);
    }
}
