package atel.model;

import main.StringHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static reading.BytesHelper.*;

/**
 * Part of AtelScriptObject
 */
public class MapTableObject {
    public static final int LENGTH = 0x10;
    private final int[] bytes;

    int unknown00;
    int unknown02;
    int unknown04;
    int unknown06;
    int unknown08;
    int unknown0C;

    public MapTableObject(int[] bytes) {
        this.bytes = bytes;
        mapBytes();
    }

    private void mapBytes() {
        unknown00 = read4Bytes(bytes, 0x00);
        unknown02 = read2Bytes(bytes, 0x02);
        unknown04 = read2Bytes(bytes, 0x04);
        unknown06 = read2Bytes(bytes, 0x06);
        unknown08 = read4Bytes(bytes, 0x08);
        unknown0C = read4Bytes(bytes, 0x0C);
    }

    public int[] toBytes() {
        int[] array = new int[MapTableObject.LENGTH];
        write2Bytes(array, 0x00, unknown00);
        write2Bytes(array, 0x02, unknown02);
        write2Bytes(array, 0x04, unknown04);
        write2Bytes(array, 0x06, unknown06);
        write4Bytes(array, 0x08, unknown08);
        write4Bytes(array, 0x0C, unknown0C);
        return array;
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
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
        if (unknown08 != 0) {
            list.add("unknown08=" + StringHelper.hex4WithSuffix(unknown08));
        }
        if (unknown0C != 0) {
            list.add("unknown0C=" + StringHelper.hex4WithSuffix(unknown0C));
        }
        String full = list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining(", "));
        return "{ " + full + " }";
    }
}
