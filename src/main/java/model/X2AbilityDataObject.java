package model;

import main.StringHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ffx-2
 * command.bin
 */
public class X2AbilityDataObject implements Nameable {
    public static final int LENGTH = 0x8C;
    private final int[] bytes;

    public String name;
    public String dash;
    public String description;
    public String otherText;

    private int nameOffset;
    int unknownByte2;
    int unknownByte3;
    private int descriptionOffset;
    int unknownByte6;
    int unknownByte7;
    int anim1;
    int anim2;

    public X2AbilityDataObject(int[] bytes, int[] stringBytes, String localization) {
        this.bytes = bytes;
        mapBytes();
        mapFlags();
        mapStrings(stringBytes, localization);
    }

    public String getName(String localization) {
        return name;
    }

    private void mapBytes() {
        nameOffset = read2Bytes(0x00);
        unknownByte2 = bytes[0x02];
        unknownByte3 = bytes[0x03];
        descriptionOffset = read2Bytes(0x04);
        unknownByte6 = bytes[0x06];
        unknownByte7 = bytes[0x07];
        anim1 = read2Bytes(0x08);
        anim2 = read2Bytes(0x0A);
    }

    private void mapFlags() {
    }

    private void mapStrings(int[] stringBytes, String localization) {
        name = StringHelper.getStringAtLookupOffset(stringBytes, nameOffset);
        description = StringHelper.getStringAtLookupOffset(stringBytes, descriptionOffset);
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        list.add("anim=" + String.format("%04X", anim1) + "/" + String.format("%04X", anim2));
        String full = list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining(", "));
        String descriptionStr = (descriptionOffset > 0 ? description : "");
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
        return String.format("%02X", bt) + '=' + String.format("%03d", bt) + '(' + String.format("%8s", Integer.toBinaryString(bt)).replace(' ', '0') + ')';
    }
}
