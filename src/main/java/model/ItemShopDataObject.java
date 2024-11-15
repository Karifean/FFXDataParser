package model;

import main.DataAccess;

import java.util.ArrayList;
import java.util.List;

/**
 * item_shop.bin
 */
public class ItemShopDataObject implements Nameable {
    public static final int LENGTH = 0x22;
    private final int[] bytes;

    private int unusedPrices;
    private int[] offeredItemIndexes = new int[0x10];

    public ItemShopDataObject(int[] bytes, int[] stringBytes) {
        this.bytes = bytes;
        mapBytes();
    }

    public ItemShopDataObject(int[] bytes) {
        this(bytes, null);
    }

    private void mapBytes() {
        unusedPrices = read2Bytes(0x00);
        for (int i = 0; i < 0x10; i++) {
            offeredItemIndexes[i] = read2Bytes(i * 2 + 2);
        }
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        list.add("Price (unused): " + unusedPrices + "%");
        for (int i = 0; i < 0x10; i++) {
            int idx = offeredItemIndexes[i];
            if (idx != 0x00) {
                AbilityDataObject item = DataAccess.getMove(idx);
                list.add("Slot #" + i + ": " + (item != null ? item.getName() : "null"));
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
