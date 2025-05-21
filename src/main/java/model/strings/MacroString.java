package model.strings;

import main.StringHelper;

import java.util.ArrayList;
import java.util.List;

public class MacroString {

    public static List<MacroString> fromStringData(int[] bytes, String charset) {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        int first = bytes[0x00] + bytes[0x01] * 0x100;
        int count = first / 0x04;
        List<MacroString> strings = new ArrayList<>(count);
        try {
            for (int i = 0; i < count; i++) {
                int headerOffset = i * 0x04;
                int regularOffset = bytes[headerOffset] + bytes[headerOffset + 0x01] * 0x100;
                int simplifiedOffset = bytes[headerOffset + 0x02] + bytes[headerOffset + 0x03] * 0x100;
                MacroString out = new MacroString(charset, regularOffset, simplifiedOffset, bytes);
                strings.add(out);
            }
        } catch (Exception e) {
            System.err.println("Exception during string data reading. (" + e.getLocalizedMessage() + ")");
        }
        return strings;
    }

    String charset;
    int regularOffset;
    int simplifiedOffset;
    int[] regularBytes;
    int[] simplifiedBytes;

    public MacroString(String charset, int regularOffset, int simplifiedOffset, int[] bytes) {
        this.charset = charset;
        this.regularOffset = regularOffset;
        this.simplifiedOffset = simplifiedOffset;
        this.regularBytes = StringHelper.getStringBytesAtLookupOffset(bytes, regularOffset);
        this.simplifiedBytes = regularOffset == simplifiedOffset ? regularBytes : StringHelper.getStringBytesAtLookupOffset(bytes, simplifiedOffset);
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
