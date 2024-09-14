package model;

import main.DataAccess;

import java.util.ArrayList;
import java.util.List;

/**
 * Used in scripts usually in shared_data
 */
public class SpriteDataObject implements Nameable {
    public static final int LENGTH = 0x60;
    private final int[] bytes;

    int flags;
    int someCount;
    int offsetToColors;
    long texLow;
    long texHigh;
    int left;
    int bottom;
    int right;
    int top;
    int uMin;
    int vMin;
    int uWidth;
    int vWidth;

    public SpriteDataObject(int[] bytes, int[] stringBytes) {
        this.bytes = bytes;
        mapBytes();
    }

    public SpriteDataObject(int[] bytes) {
        this(bytes, null);
    }

    private void mapBytes() {
        flags = read2Bytes(0x00);
        someCount = read2Bytes(0x02);
        offsetToColors = read2Bytes(0x04);
        texLow = read4Bytes(0x0E);
        texHigh = read4Bytes(0x16);
        left = read2Bytes(0x1E);
        bottom = read2Bytes(0x20);
        right = read2Bytes(0x22);
        top = read2Bytes(0x24);
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        return String.join("\n", list);
    }

    @Override
    public String getName() {
        return this.toString();
    }

    private int read2Bytes(int offset) {
        return bytes[offset] + bytes[offset+1] * 0x100;
    }

    private int read4Bytes(int offset) {
        return bytes[offset] + bytes[offset+1] * 0x100 + bytes[offset+2] * 0x10000 + bytes[offset+3] * 0x1000000;
    }
}
