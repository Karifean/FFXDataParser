package model;

import main.DataAccess;
import model.strings.KeyedString;
import model.strings.LocalizedKeyedStringObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static reading.BytesHelper.write2Bytes;

/**
 * item_shop.bin
 */
public class ItemShopDataObject implements Writable {
    public static final int LENGTH = 0x22;
    private final int[] bytes;

    private int unusedPrices;
    private int[] offeredItemIndexes = new int[0x10];

    public ItemShopDataObject(int[] bytes, int[] stringBytes, String localization) {
        this.bytes = bytes;
        mapBytes();
    }

    public ItemShopDataObject(int[] bytes) {
        this(bytes, null, null);
    }

    private void mapBytes() {
        unusedPrices = read2Bytes(0x00);
        for (int i = 0; i < 0x10; i++) {
            offeredItemIndexes[i] = read2Bytes(i * 2 + 2);
        }
    }

    @Override
    public Stream<KeyedString> streamKeyedStrings(String localization) {
        return Stream.of();
    }

    @Override
    public LocalizedKeyedStringObject getKeyedString(String title) {
        return null;
    }

    @Override
    public int[] toBytes(String localization) {
        int[] array = new int[ItemShopDataObject.LENGTH];
        write2Bytes(array, 0x00, unusedPrices);
        for (int i = 0; i < 0x10; i++) {
            write2Bytes(array, i * 2 + 2, offeredItemIndexes[i]);
        }
        return array;
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        list.add("Price (unused): " + unusedPrices + "%");
        for (int i = 0; i < 0x10; i++) {
            int idx = offeredItemIndexes[i];
            if (idx != 0x00) {
                CommandDataObject item = DataAccess.getCommand(idx);
                list.add("Slot #" + i + ": " + (item != null ? item.getName() : "null"));
            }
        }
        return String.join("\n", list);
    }

    private int read2Bytes(int offset) {
        return bytes[offset] + bytes[offset+1] * 0x100;
    }
}
