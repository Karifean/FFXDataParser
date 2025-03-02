package model.strings;

import main.StringHelper;
import reading.ChunkedFileHelper;

import java.util.*;
import java.util.stream.Stream;

public class FieldString {

    public static List<FieldString> fromStringData(int[] bytes, boolean print, String charset) {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        int first = bytes[0x00] + bytes[0x01] * 0x100;
        int count = first / 0x08;
        List<FieldString> strings = new ArrayList<>(count);
        try {
            for (int i = 0; i < count; i++) {
                int regularHeader = ChunkedFileHelper.read4Bytes(bytes, i * 0x08);
                int simplifiedHeader = ChunkedFileHelper.read4Bytes(bytes, i * 0x08 + 0x04);
                FieldString out = new FieldString(charset, regularHeader, simplifiedHeader, bytes);
                if (print) {
                    System.out.printf("String %s: %s%n", StringHelper.hex2WithSuffix(i), out);
                }
                strings.add(out);
            }
        } catch (Exception e) {
            System.err.println("Exception during string data reading. (" + e.getLocalizedMessage() + ")");
        }
        return strings;
    }

    public static int[] rebuildFieldStrings(List<FieldString> strings, String charset, final boolean optimize) {
        final Map<String, Integer> offsetMap = new HashMap<>();
        final List<Integer> byteList = new ArrayList<>();
        byteList.add(0);
        offsetMap.put("", 0);
        Stream<FieldString> stringStream = strings.stream();
        stringStream.forEach((fieldString) -> {
            String regularString = fieldString.getRegularString();
            fieldString.regularChoices = StringHelper.getChoicesInString(regularString);
            if (regularString == null || regularString.isEmpty()) {
                fieldString.regularOffset = 0;
            } else if (offsetMap.containsKey(regularString)) {
                fieldString.regularOffset = offsetMap.get(regularString);
            } else {
                fieldString.regularOffset = byteList.size();
                offsetMap.put(regularString, byteList.size());
                StringHelper.fillByteList(regularString, byteList, charset);
            }
            String simplifiedString = fieldString.getSimplifiedString();
            fieldString.simplifiedChoices = StringHelper.getChoicesInString(simplifiedString);
            if (simplifiedString == null || simplifiedString.isEmpty()) {
                fieldString.simplifiedOffset = 0;
            } else if (offsetMap.containsKey(simplifiedString)) {
                fieldString.simplifiedOffset = offsetMap.get(simplifiedString);
            } else {
                fieldString.simplifiedOffset = byteList.size();
                offsetMap.put(simplifiedString, byteList.size());
                StringHelper.fillByteList(simplifiedString, byteList, charset);
            }
        });
        final int[] stringBytes = new int[byteList.size()];
        for (int i = 0; i < byteList.size(); i++) {
            stringBytes[i] = byteList.get(i);
        }
        return stringBytes;
    }

    String charset;
    int regularOffset;
    int regularFlags;
    int regularChoices;
    int simplifiedOffset;
    int simplifiedFlags;
    int simplifiedChoices;
    int[] regularBytes;
    int[] simplifiedBytes;

    public FieldString(String charset, int regularHeader, int simplifiedHeader, int[] bytes) {
        this.charset = charset;
        this.regularOffset = regularHeader & 0x0000FFFF;
        this.regularFlags = regularHeader & 0x00FF0000 >> 16;
        this.regularChoices = regularHeader & 0xFF000000 >> 24;
        this.simplifiedOffset = simplifiedHeader & 0x0000FFFF;
        this.simplifiedFlags = simplifiedHeader & 0x00FF0000 >> 16;
        this.simplifiedChoices = simplifiedHeader & 0xFF000000 >> 24;
        this.regularBytes = StringHelper.getStringBytesAtLookupOffset(bytes, regularOffset);
        this.simplifiedBytes = regularOffset == simplifiedOffset ? regularBytes : StringHelper.getStringBytesAtLookupOffset(bytes, simplifiedOffset);
    }

    public int toRegularHeaderBytes() {
        return regularOffset | (regularFlags << 16) | (regularChoices << 24);
    }

    public int toSimplifiedHeaderBytes() {
        return simplifiedOffset | (simplifiedFlags << 16) | (simplifiedChoices << 24);
    }

    @Override
    public String toString() {
        return getString();
    }

    public String getString() {
        if (hasDistinctSimplified()) {
            return getRegularString() + " (Simplified: " + getSimplifiedString() + ")";
        }
        return getRegularString();
    }

    public boolean isEmpty() {
        return getRegularString().isEmpty() && getSimplifiedString().isEmpty();
    }

    public String getRegularString() {
        return StringHelper.bytesToString(regularBytes, charset);
    }

    public String getSimplifiedString() {
        return StringHelper.bytesToString(simplifiedBytes, charset);
    }

    public boolean hasDistinctSimplified() {
        return regularBytes != simplifiedBytes;
    }

    public void setRegularString(String str, String newCharset) {
        setCharset(newCharset);
        boolean keepSimplifiedSynced = !hasDistinctSimplified();
        regularBytes = StringHelper.stringToBytes(str, charset);
        if (keepSimplifiedSynced) {
            simplifiedBytes = regularBytes;
        }
    }

    public void setSimplifiedString(String str, String newCharset) {
        setCharset(newCharset);
        simplifiedBytes = StringHelper.stringToBytes(str, charset);
    }

    public void setRegularString(String str) {
        setRegularString(str, null);
    }

    public void setSimplifiedString(String str) {
        setSimplifiedString(str, null);
    }

    public void setCharset(String newCharset) {
        if (newCharset != null && !newCharset.equals(charset)) {
            this.charset = newCharset;
        }
    }

}
