package model.spheregrid;

import model.Nameable;

import java.util.ArrayList;
import java.util.List;

public class SphereGridNodeDataObject implements Nameable {
    public static final int LENGTH = 0xC;
    private final int[] bytes;

    private int maybeX;
    private int maybeY;
    private int unused3;
    private int unknown4;
    private int cluster;
    private int unknown6;

    public SphereGridNodeDataObject(int[] bytes, int[] stringBytes) {
        this.bytes = bytes;
        mapBytes();
    }

    public SphereGridNodeDataObject(int[] bytes) {
        this(bytes, null);
    }

    private void mapBytes() {
        maybeX = read2Bytes(0x00, true);
        maybeY = read2Bytes(0x02, true);
        unused3 = read2Bytes(0x04, false);
        unknown4 = read2Bytes(0x06, false);
        cluster = read2Bytes(0x08, false);
        unknown6 = read2Bytes(0x0A, false);
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        list.add("Offset=(" + maybeX + "/" + maybeY + ")");
        list.add("Cluster#" + cluster);
        if (unused3 != 0) {
            list.add("Unknown3=" + String.format("%04X", unused3));
        }
        list.add("U4=" + String.format("%04X", unknown4));
        list.add("U6=" + String.format("%04X", unknown6));
        String full = String.join(", ", list);
        return "{ " +  full + " }";
    }

    @Override
    public String getName() {
        return this.toString();
    }

    private int read2Bytes(int offset, boolean signed) {
        int raw = bytes[offset] + bytes[offset+1] * 0x100;
        if (signed && (raw & 0x8000) != 0) {
            return raw - 0x10000;
        } else {
            return raw;
        }
    }
}
