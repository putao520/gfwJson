package org.json.gsc;

import java.io.BufferedReader;

@FunctionalInterface
public interface IBigJsonValueCallback {
    void run(BufferedReader reader, BigJsonValue bigJsonValue);
}
