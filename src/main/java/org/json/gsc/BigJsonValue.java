package org.json.gsc;

public class BigJsonValue {
    private final int startPos;
    private final int length;

    private BigJsonValue(int startPos, int length) {
        this.startPos = startPos;
        this.length = length;
    }

    public static BigJsonValue build(int startPos, int length) {
        return new BigJsonValue(startPos, length);
    }

    public int getStartPos() {
        return startPos;
    }

    public int getLength() {
        return length;
    }

    public String toString() {
        return "Unsupported,use JSONObjectStream.getBigJsonValueStream Get StringReader";
    }
}
