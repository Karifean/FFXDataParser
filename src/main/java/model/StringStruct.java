package model;

import main.StringHelper;

import java.util.HashMap;
import java.util.Map;

public class StringStruct {
    public Map<String, Integer> stringToOffsetMap;
    public int[] stringBytes;
    public Map<String, Integer> stringTo4ByteHeadMap;

    public StringStruct(Map<String, Integer> stringToOffsetMap, int[] stringBytes) {
        this.stringToOffsetMap = stringToOffsetMap;
        this.stringBytes = stringBytes;
    }

    public Map<String, Integer> get4ByteHeadMap() {
        if (stringTo4ByteHeadMap == null) {
            stringTo4ByteHeadMap = new HashMap<>();
            stringToOffsetMap.forEach((string, index) -> {
                int choices = StringHelper.getChoicesInString(string);
                int totalHead = index + (choices << 24);
                stringTo4ByteHeadMap.put(string, totalHead);
            });
        }
        return stringTo4ByteHeadMap;
    }
}
