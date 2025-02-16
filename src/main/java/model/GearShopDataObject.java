package model;

import main.DataAccess;
import main.StringHelper;
import model.strings.KeyedString;
import model.strings.LocalizedKeyedStringObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static reading.ChunkedFileHelper.write2Bytes;

/**
 * arms_shop.bin
 */
public class GearShopDataObject implements Writable {
    public static final int LENGTH = 0x22;
    private final int[] bytes;

    private int unusedPrices;
    private int[] offeredGearIndexes;

    public GearShopDataObject(int[] bytes, int[] stringBytes, String localization) {
        this.bytes = bytes;
        mapBytes();
    }

    public GearShopDataObject(int[] bytes) {
        this(bytes, null, null);
    }

    private void mapBytes() {
        unusedPrices = read2Bytes(0x00);
        offeredGearIndexes = new int[0x10];
        for (int i = 0; i < 0x10; i++) {
            offeredGearIndexes[i] = read2Bytes(i * 2 + 2);
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
        int[] array = new int[GearShopDataObject.LENGTH];
        write2Bytes(array, 0x00, unusedPrices);
        for (int i = 0; i < 0x10; i++) {
            write2Bytes(array, i * 2 + 2, offeredGearIndexes[i]);
        }
        return array;
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        list.add("Price (unused): " + unusedPrices + "%");
        for (int i = 0; i < 0x10; i++) {
            int idx = offeredGearIndexes[i];
            if (idx != 0x00) {
                String idxHexSuffix = StringHelper.hex2Suffix(idx);
                GearDataObject gear = DataAccess.BUYABLE_GEAR != null ? DataAccess.BUYABLE_GEAR[idx] : null;
                list.add("Slot #" + i + ": " + (gear != null ? gear.toString() : "null") + idxHexSuffix);
            }
        }
        return String.join("\n", list);
    }

    private int read2Bytes(int offset) {
        return bytes[offset] + bytes[offset+1] * 0x100;
    }
}
