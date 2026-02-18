package model.strings;

import main.StringHelper;

import java.util.ArrayList;
import java.util.List;

import static reading.BytesHelper.read2Bytes;

public class MacroString {

    public static List<MacroString> fromStringData(int[] bytes, String charset) {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        int first = read2Bytes(bytes, 0);
        int count = first / 0x04;
        List<MacroString> strings = new ArrayList<>(count);
        try {
            for (int i = 0; i < count; i++) {
                int headerOffset = i * 0x04;
                int regularOffset = read2Bytes(bytes, headerOffset);
                int simplifiedOffset = read2Bytes(bytes, headerOffset + 0x02);
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
        return StringHelper.bytesToString(regularBytes, charset, true);
    }

    public String getSimplifiedString() {
        return StringHelper.bytesToString(simplifiedBytes, charset, true);
    }


    public String getRegularStringMultiline() {
        return StringHelper.bytesToString(regularBytes, charset, false);
    }

    public String getSimplifiedStringMultiline() {
        return StringHelper.bytesToString(simplifiedBytes, charset, false);
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
