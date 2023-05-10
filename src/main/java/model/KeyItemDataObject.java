package model;

import main.Main;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class KeyItemDataObject {
    private int[] bytes;

    public String name;
    public String dash;
    public String description;
    public String otherText;

    private int nameOffset;

    public KeyItemDataObject() {}

    public KeyItemDataObject(int[] bytes, int[] stringBytes) {
        this.bytes = bytes;
        mapBytes();
        mapFlags();
        mapStrings(stringBytes);
    }

    public String getName() {
        return name;
    }

    private void mapBytes() {
        nameOffset = read2Bytes(0x00);
    }

    private void mapFlags() {
    }

    private void mapStrings(int[] stringBytes) {
        name = Main.getStringAtLookupOffset(stringBytes, nameOffset);
    }

    @Override
    public String toString() {
        return "Name: " + getName();
    }

    private int read2Bytes(int offset) {
        int val = bytes[offset];
        val += bytes[offset+1] * 0x100;
        return val;
    }
}
