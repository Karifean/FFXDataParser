package model;

import main.StringHelper;
import model.strings.LocalizedKeyedStringObject;

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

    public LocalizedKeyedStringObject name;
    public LocalizedKeyedStringObject description;

    private int nameOffset;
    private int nameKey;
    private int descriptionOffset;
    private int descriptionKey;
    int anim1;
    int anim2;

    public X2AbilityDataObject(int[] bytes, int[] stringBytes, String localization) {
        this.bytes = bytes;
        mapBytes();
        mapFlags();
        mapStrings(stringBytes, localization);
    }

    public String getName(String localization) {
        return name.getLocalizedString(localization);
    }

    private void mapBytes() {
        nameOffset = read2Bytes(0x00);
        nameKey = read2Bytes(0x02);
        descriptionOffset = read2Bytes(0x04);
        descriptionKey = read2Bytes(0x06);
        anim1 = read2Bytes(0x08);
        anim2 = read2Bytes(0x0A);
    }

    private void mapFlags() {
    }

    private void mapStrings(int[] stringBytes, String localization) {
        name.readAndSetLocalizedContent(localization, stringBytes, nameOffset, nameKey);
        description.readAndSetLocalizedContent(localization, stringBytes, descriptionOffset, descriptionKey);
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        list.add("anim=" + StringHelper.formatHex4(anim1) + "/" + StringHelper.formatHex4(anim2));
        String full = list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining(", "));
        String descriptionStr = (descriptionOffset > 0 ? description.getDefaultContent().getString() : "");
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
}
