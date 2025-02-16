package model;

import main.StringHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * important.bin
 */
public class KeyItemDataObject extends NameDescriptionTextObject implements Nameable, Writable {
    public static final int LENGTH = 0x14;
    private final int[] bytes;

    int isAlBhedPrimer;
    int alwaysZero;
    int unknownByte12;
    int ordering;

    public KeyItemDataObject(int[] bytes, int[] stringBytes, String localization) {
        super(bytes, stringBytes, localization);
        this.bytes = bytes;
        mapBytes();
        mapFlags();
    }

    public String getName(String localization) {
        return name.getLocalizedString(localization);
    }

    private void mapBytes() {
        isAlBhedPrimer = bytes[0x10];
        alwaysZero = bytes[0x11];
        unknownByte12 = bytes[0x12];
        ordering = bytes[0x13];
    }

    @Override
    public int[] toBytes(String localization) {
        int[] array = new int[KeyItemDataObject.LENGTH];
        System.arraycopy(super.toBytes(localization), 0, array, 0, 0x10);
        array[0x10] = isAlBhedPrimer;
        array[0x11] = alwaysZero;
        array[0x12] = unknownByte12;
        array[0x13] = ordering;
        return array;
    }

    private void mapFlags() {
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        list.add("byte12=" + formatUnknownByte(unknownByte12));
        list.add(ifG0(isAlBhedPrimer, "isPrimerByte=", ""));
        list.add(ifG0(alwaysZero, "byte11 not Zero!=", ""));
        list.add("Ordering: " + StringHelper.hex2WithSuffix(ordering));
        String full = list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining(", "));
        String descriptionStr = description.getDefaultString();
        return String.format("%-20s", getName()) + " { " + full + " } " + descriptionStr;
    }

    private int read2Bytes(int offset) {
        int val = bytes[offset];
        val += bytes[offset+1] * 0x100;
        return val;
    }

    private static String ifG0(int value, String prefix, String postfix) {
        if (value > 0) {
            return prefix + value + postfix;
        } else {
            return null;
        }
    }

    private static String formatUnknownByte(int bt) {
        return StringHelper.formatHex2(bt) + '=' + String.format("%03d", bt) + '(' + String.format("%8s", Integer.toBinaryString(bt)).replace(' ', '0') + ')';
    }

    private static void write2Bytes(int[] array, int offset, int value) {
        array[offset]     =  value & 0x00FF;
        array[offset + 1] = (value & 0xFF00) >> 8;
    }
}
