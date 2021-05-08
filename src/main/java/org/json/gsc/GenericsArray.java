package org.json.gsc;

import java.lang.reflect.Array;
import java.util.List;

public class GenericsArray {
    public static <T> T[] getArray(Class<T> componentType, int length) {
        return (T[]) Array.newInstance(componentType, length);
    }

    public static <T> T[] getArray(Class<T> componentType, List<T> array) {
        var arr = (T[]) Array.newInstance(componentType, array.size());
        array.toArray(arr);
        return arr;
    }

    public static <T> T[] getArray(Class<T> componentType, Object[] array) {
        var arr = (T[]) Array.newInstance(componentType, array.length);
        for (int i = 0; i < array.length; i++) {
            arr[i] = (T) array[i];
        }
        return arr;
    }
}
