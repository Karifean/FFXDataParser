package model;

import java.util.Map;
import java.util.stream.Stream;

public interface Writable {
    Stream<String> getStrings(String localization);
    int[] toBytes(String localization, Map<String, Integer> stringMap);
}
