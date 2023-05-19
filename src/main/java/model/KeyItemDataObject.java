package model;

import main.StringHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class KeyItemDataObject {
    private int[] bytes;

    public String name;
    public String dash;
    public String description;
    public String otherText;

    private int nameOffset;
    int unknownByte2;
    int unknownByte3;
    private int dashOffset;
    int unknownByte6;
    int unknownByte7;
    private int descriptionOffset;
    int unknownByte0A;
    int unknownByte0B;
    private int otherTextOffset;
    int unknownByte0E;
    int unknownByte0F;

    int isAlBhedPrimer;
    int alwaysZero;
    int unknownByte12;
    int ordering;

    public KeyItemDataObject() {}

    public KeyItemDataObject(int[] bytes, int[] stringBytes) {
        this.bytes = bytes;
        mapBytes();
        mapFlags();
        mapStrings(stringBytes);
    }

    public String getName() {
        return name;
    }

    private void mapBytes() {
        nameOffset = read2Bytes(0x00);
        unknownByte2 = bytes[0x02];
        unknownByte3 = bytes[0x03];
        dashOffset = read2Bytes(0x04);
        unknownByte6 = bytes[0x06];
        unknownByte7 = bytes[0x07];
        descriptionOffset = read2Bytes(0x08);
        unknownByte0A = bytes[0x0A];
        unknownByte0B = bytes[0x0B];
        otherTextOffset = read2Bytes(0x0C);
        unknownByte0E = bytes[0x0E];
        unknownByte0F = bytes[0x0F];
        isAlBhedPrimer = bytes[0x10];
        alwaysZero = bytes[0x11];
        unknownByte12 = bytes[0x12];
        ordering = bytes[0x13];
    }

    private void mapFlags() {
    }

    private void mapStrings(int[] stringBytes) {
        name = StringHelper.getStringAtLookupOffset(stringBytes, nameOffset);
        dash = StringHelper.getStringAtLookupOffset(stringBytes, dashOffset);
        description = StringHelper.getStringAtLookupOffset(stringBytes, descriptionOffset);
        otherText = StringHelper.getStringAtLookupOffset(stringBytes, otherTextOffset);
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        list.add("byte12=" + formatUnknownByte(unknownByte12));
        list.add(ifG0(isAlBhedPrimer, "isPrimerByte=", ""));
        list.add(ifG0(alwaysZero, "byte11 not Zero!=", ""));
        list.add("Ordering: " + ordering + " [" + String.format("%02x", ordering) + "h]");
        String full = list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining(", "));
        String dashStr = (dashOffset > 0 && !"-".equals(dash) ? "DH=" + dash + " / " : "");
        String descriptionStr = (descriptionOffset > 0 && !"-".equals(description) ? description : "");
        String soText = (otherTextOffset > 0 && !"-".equals(otherText) ? " / OT=" + otherText : "");
        return String.format("%-20s", getName()) + " { " + full + " } " + dashStr + descriptionStr + soText;
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

    private static String ifNN(String value, String prefix, String postfix) {
        if (value != null) {
            return prefix + value + postfix;
        } else {
            return "";
        }
    }

    private static String formatUnknownByte(int bt) {
        return String.format("%02x", bt) + '=' + String.format("%03d", bt) + '(' + String.format("%8s", Integer.toBinaryString(bt)).replace(' ', '0') + ')';
    }
}
