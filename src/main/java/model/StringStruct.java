package model;

import java.util.Map;

public class StringStruct {
    public Map<String, Integer> stringMap;
    public int[] stringBytes;

    public StringStruct(Map<String, Integer> stringMap, int[] stringBytes) {
        this.stringMap = stringMap;
        this.stringBytes = stringBytes;
    }
}
