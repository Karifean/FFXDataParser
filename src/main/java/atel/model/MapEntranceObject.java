package atel.model;

import main.StringHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static reading.ChunkedFileHelper.read2Bytes;
import static reading.ChunkedFileHelper.read4Bytes;

/**
 * Part of AtelScriptObject
 */
public class MapEntranceObject {
    public static final int LENGTH = 0x20;
    private final int[] bytes;

    int unknown00;
    int unknown02;
    int unknown04;
    int unknown06;
    float rotation;
    float x;
    float y;
    float z;
    int unknown18;
    int unknown1C;

    public MapEntranceObject(int[] bytes) {
        this.bytes = bytes;
        mapBytes();
    }

    private void mapBytes() {
        unknown00 = read4Bytes(bytes, 0x00);
        unknown00 = read2Bytes(bytes, 0x02);
        unknown04 = read2Bytes(bytes, 0x04);
        unknown06 = read2Bytes(bytes, 0x06);
        rotation = readFloat(0x08);
        x = readFloat(0x0C);
        y = readFloat(0x10);
        z = readFloat(0x14);
        unknown18 = read4Bytes(bytes, 0x18);
        unknown1C = read4Bytes(bytes, 0x1C);
    }

    private float readFloat(int offset) {
        return Float.intBitsToFloat(read4Bytes(bytes, offset));
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        list.add("x=" + x);
        list.add("y=" + y);
        list.add("z=" + z);
        list.add("rotation=" + rotation);
        if (unknown00 != 0) {
            list.add("unknown00=" + StringHelper.hex2WithSuffix(unknown00));
        }
        if (unknown02 != 0) {
            list.add("unknown02=" + StringHelper.hex2WithSuffix(unknown02));
        }
        if (unknown04 != 0) {
            list.add("unknown04=" + StringHelper.hex2WithSuffix(unknown04));
        }
        if (unknown06 != 0) {
            list.add("unknown06=" + StringHelper.hex2WithSuffix(unknown06));
        }
        if (unknown18 != 0) {
            list.add("unknown18=" + StringHelper.hex2WithSuffix(unknown18));
        }
        if (unknown1C != 0) {
            list.add("unknown1C=" + StringHelper.hex2WithSuffix(unknown1C));
        }
        String full = list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining(", "));
        return "{ " + full + " }";
    }
}
