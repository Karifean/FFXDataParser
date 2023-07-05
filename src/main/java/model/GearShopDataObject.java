package model;

import main.DataAccess;

import java.util.ArrayList;
import java.util.List;

/**
 * takara.bin
 */
public class GearShopDataObject implements Nameable {
    public static final int LENGTH = 0x22;
    private final int[] bytes;

    private int unusedPrices;
    private int[] offeredGearIndexes;

    public GearShopDataObject(int[] bytes, int[] stringBytes) {
        this.bytes = bytes;
        mapBytes();
    }

    public GearShopDataObject(int[] bytes) {
        this(bytes, null);
    }

    private void mapBytes() {
        unusedPrices = read2Bytes(0x00);
        offeredGearIndexes = new int[0x10];
        for (int i = 0; i < 0x10; i++) {
            offeredGearIndexes[i] = read2Bytes(i * 2 + 2);
        }
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        list.add("Price (unused): " + unusedPrices + "%");
        for (int i = 0; i < 0x10; i++) {
            int idx = offeredGearIndexes[i];
            if (idx != 0x00) {
                String idxHexSuffix = " [" + String.format("%02X", idx) + "h]";
                GearDataObject gear = DataAccess.BUYABLE_GEAR != null ? DataAccess.BUYABLE_GEAR[idx] : null;
                list.add("Slot #" + i + ": " + (gear != null ? gear.toString() : "null") + idxHexSuffix);
            }
        }
        return String.join("\n", list);
    }

    @Override
    public String getName() {
        return this.toString();
    }

    private int read2Bytes(int offset) {
        return bytes[offset] + bytes[offset+1] * 0x100;
    }
}
