package model;

import main.StringHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * important.bin
 */
public class KeyItemDataObject implements Nameable, Writable {
    public static final int LENGTH = 0x14;
    private final int[] bytes;

    public LocalizedStringObject name = new LocalizedStringObject();
    public LocalizedStringObject unusedString0405 = new LocalizedStringObject();
    public LocalizedStringObject description = new LocalizedStringObject();
    public LocalizedStringObject unusedString0C0D = new LocalizedStringObject();

    private int nameOffset;
    private int nameKey;
    private int unusedString0405Offset;
    private int unusedString0405Key;
    private int descriptionOffset;
    private int descriptionKey;
    private int unusedString0C0DOffset;
    private int unusedString0C0DKey;

    int isAlBhedPrimer;
    int alwaysZero;
    int unknownByte12;
    int ordering;

    public KeyItemDataObject(int[] bytes, int[] stringBytes, String localization) {
        this.bytes = bytes;
        mapBytes();
        mapFlags();
        mapStrings(stringBytes, localization);
    }

    public String getName(String localization) {
        return name.getLocalizedContent(localization);
    }

    private void mapBytes() {
        nameOffset = read2Bytes(0x00);
        nameKey = read2Bytes(0x02);
        unusedString0405Offset = read2Bytes(0x04);
        unusedString0405Key = read2Bytes(0x06);
        descriptionOffset = read2Bytes(0x08);
        descriptionKey = read2Bytes(0x0A);
        unusedString0C0DOffset = read2Bytes(0x0C);
        unusedString0C0DKey = read2Bytes(0x0E);
        isAlBhedPrimer = bytes[0x10];
        alwaysZero = bytes[0x11];
        unknownByte12 = bytes[0x12];
        ordering = bytes[0x13];
    }

    @Override
    public int[] toBytes(String localization, Map<String, Integer> stringMap) {
        int[] array = new int[KeyItemDataObject.LENGTH];
        write2Bytes(array, 0x00, stringMap.get(name.getLocalizedContent(localization)));
        write2Bytes(array, 0x02, nameKey);
        write2Bytes(array, 0x04, stringMap.get(unusedString0405.getLocalizedContent(localization)));
        write2Bytes(array, 0x06, unusedString0405Key);
        write2Bytes(array, 0x08, stringMap.get(description.getLocalizedContent(localization)));
        write2Bytes(array, 0x0A, descriptionKey);
        write2Bytes(array, 0x0C, stringMap.get(unusedString0C0D.getLocalizedContent(localization)));
        write2Bytes(array, 0x0E, unusedString0C0DKey);
        array[0x10] = isAlBhedPrimer;
        array[0x11] = alwaysZero;
        array[0x12] = unknownByte12;
        array[0x13] = ordering;
        return array;
    }

    private void mapFlags() {
    }

    private void mapStrings(int[] stringBytes, String localization) {
        name.readAndSetLocalizedContent(localization, stringBytes, nameOffset);
        unusedString0405.readAndSetLocalizedContent(localization, stringBytes, unusedString0405Offset);
        description.readAndSetLocalizedContent(localization, stringBytes, descriptionOffset);
        unusedString0C0D.readAndSetLocalizedContent(localization, stringBytes, unusedString0C0DOffset);
    }

    public void setLocalizations(KeyItemDataObject localizationObject) {
        localizationObject.name.copyInto(name);
        localizationObject.unusedString0405.copyInto(unusedString0405);
        localizationObject.description.copyInto(description);
        localizationObject.unusedString0C0D.copyInto(unusedString0C0D);
    }

    @Override
    public Stream<String> getStrings(String localization) {
        return Stream.of(
                name.getLocalizedContent(localization),
                unusedString0405.getLocalizedContent(localization),
                description.getLocalizedContent(localization),
                unusedString0C0D.getLocalizedContent(localization)
        );
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        list.add("byte12=" + formatUnknownByte(unknownByte12));
        list.add(ifG0(isAlBhedPrimer, "isPrimerByte=", ""));
        list.add(ifG0(alwaysZero, "byte11 not Zero!=", ""));
        list.add("Ordering: " + StringHelper.hex2WithSuffix(ordering));
        String full = list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining(", "));
        String descriptionStr = (descriptionOffset > 0 ? description.getDefaultContent() : "");
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
