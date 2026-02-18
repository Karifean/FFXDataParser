package model.strings;

import main.StringHelper;
import reading.BytesHelper;

import java.util.*;
import java.util.stream.Stream;

public class KeyedString {

    public static int[] rebuildKeyedStrings(Stream<KeyedString> stringStream, String charset, final boolean optimize) {
        final Map<String, KeyedString> map = new HashMap<>();
        final List<Integer> byteList = new ArrayList<>();
        final List<KeyedString> list = stringStream.sorted(Comparator.comparingInt(s -> s.bytes.length)).toList();
        for (KeyedString keyedString : list) {
            String actualString = keyedString.getString();
            if (map.containsKey(actualString)) {
                keyedString.offset = map.get(actualString).offset;
                keyedString.key = map.get(actualString).key;
            } else {
                keyedString.offset = byteList.size();
                keyedString.key = map.size();
                map.put(actualString, keyedString);
                StringHelper.fillByteList(actualString, byteList, charset);
            }
        }
        return BytesHelper.intListToArray(byteList);
    }

    String charset;
    int offset;
    int key;
    int[] bytes;

    public KeyedString(String charset, int offset, int key, int[] bytes) {
        this.charset = charset;
        this.offset = offset;
        this.key = key;
        this.bytes = StringHelper.getStringBytesAtLookupOffset(bytes, offset);
    }

    public int toHeaderBytes() {
        return offset | (key << 16);
    }

    @Override
    public String toString() {
        return getString();
    }

    public String getString() {
        return StringHelper.bytesToString(bytes, charset, true);
    }

    public String getStringMultiline() {
        return StringHelper.bytesToString(bytes, charset, false);
    }

    public boolean isEmpty() {
        return getString().isEmpty();
    }

    public void setString(String str, String newCharset) {
        setCharset(newCharset);
        bytes = StringHelper.stringToBytes(str, charset);
    }

    public void setString(String str) {
        setString(str, null);
    }

    public void setCharset(String newCharset) {
        if (newCharset != null && !newCharset.equals(charset)) {
            this.charset = newCharset;
        }
    }

}
