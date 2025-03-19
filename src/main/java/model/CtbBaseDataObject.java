package model;

import model.strings.KeyedString;
import model.strings.LocalizedKeyedStringObject;

import java.util.stream.Stream;

/**
 * ctb_base.bin
 */
public class CtbBaseDataObject implements Writable {
    public static final int LENGTH = 0x02;

    int tickspeed;
    int icvBonus;

    public CtbBaseDataObject(int[] bytes, int[] stringBytes, String localization) {
        mapBytes(bytes);
    }

    public CtbBaseDataObject(int[] bytes) {
        this(bytes, null, null);
    }

    private void mapBytes(int[] bytes) {
        tickspeed = bytes[0x00];
        icvBonus = bytes[0x01];
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
        int[] array = new int[CtbBaseDataObject.LENGTH];
        array[0x00] = tickspeed;
        array[0x01] = icvBonus;
        return array;
    }

    @Override
    public String toString() {
        int maxIcv = tickspeed * 3;
        int minIcv = maxIcv - icvBonus;
        return String.format("Tickspeed %d, ICV Bonus %d (ICV Range %d - %d)", tickspeed, icvBonus, minIcv, maxIcv);
    }
}
