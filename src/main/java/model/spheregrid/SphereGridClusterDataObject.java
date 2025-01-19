package model.spheregrid;

import model.Nameable;

import java.util.ArrayList;
import java.util.List;

/**
 * Part of SphereGridLayoutDataObject
 */
public class SphereGridClusterDataObject implements Nameable {
    public static final int LENGTH = 0x10;
    private final int[] bytes;

    private int posX;
    private int posY;
    private int unused3;
    private int radiusType;
    private int unused5;
    private int unused6;
    private int unused7;
    private int unused8;

    public SphereGridClusterDataObject(int[] bytes, int[] stringBytes) {
        this.bytes = bytes;
        mapBytes();
    }

    public SphereGridClusterDataObject(int[] bytes) {
        this(bytes, null);
    }


    private void mapBytes() {
        posX = read2Bytes(0x00, true);
        posY = read2Bytes(0x02, true);
        unused3 = read2Bytes(0x04, false);
        radiusType = read2Bytes(0x06, false);
        unused5 = read2Bytes(0x08, false);
        unused6 = read2Bytes(0x0A, false);
        unused7 = read2Bytes(0x0C, false);
        unused8 = read2Bytes(0x0E, false);
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        list.add("Position=(" + posX + "/" + posY + ")");
        list.add("Radius/Type=" + radiusType);
        if (unused3 != 0) {
            list.add("Unknown3=" + String.format("%04X", unused3));
        }
        if (unused5 != 0) {
            list.add("Unknown5=" + String.format("%04X", unused5));
        }
        if (unused6 != 0) {
            list.add("Unknown6=" + String.format("%04X", unused6));
        }
        if (unused7 != 0) {
            list.add("Unknown7=" + String.format("%04X", unused7));
        }
        if (unused8 != 0) {
            list.add("Unknown8=" + String.format("%04X", unused8));
        }
        String full = String.join(", ", list);
        return "{ " +  full + " }";
    }

    @Override
    public String getName(String localization) {
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
