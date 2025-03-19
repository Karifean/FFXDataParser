package model;

import main.DataAccess;
import main.StringHelper;
import model.strings.KeyedString;
import model.strings.LocalizedKeyedStringObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static reading.BytesHelper.write2Bytes;

/**
 * prepare.bin
 */
public class MixCombinationDataObject implements Writable {
    public static final int LENGTH = 0xE0;
    private final int[] bytes;

    public int mixOrigin;
    private final int[] mixResults = new int[0x70];

    public MixCombinationDataObject(int[] bytes, int[] stringBytes, String localization) {
        this.bytes = bytes;
        mapBytes();
    }

    public MixCombinationDataObject(int[] bytes) {
        this(bytes, null, null);
    }

    private void mapBytes() {
        for (int i = 0; i < 0x70; i++) {
            mixResults[i] = read2Bytes(i * 2);
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
        int[] array = new int[MixCombinationDataObject.LENGTH];
        for (int i = 0; i < 0x70; i++) {
            write2Bytes(array, i * 2, mixResults[i]);
        }
        return array;
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 0x70; i++) {
            int result = mixResults[i];
            if (result != 0x00) {
                list.add(asMove(mixOrigin) + " + " + asMove(i + 0x2000) + " = " + asMove(result));
            }
        }
        return String.join("\n", list);
    }

    private static String asMove(int idx) {
        CommandDataObject move = DataAccess.getCommand(idx);
        return (move != null ? move.name : "null") + StringHelper.hex4Suffix(idx);
    }

    private int read2Bytes(int offset) {
        return bytes[offset] + bytes[offset+1] * 0x100;
    }
}
