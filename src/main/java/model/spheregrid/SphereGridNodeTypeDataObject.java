package model.spheregrid;

import atel.model.StackObject;
import main.DataAccess;
import main.StringHelper;
import model.*;
import model.strings.KeyedString;
import model.strings.LocalizedKeyedStringObject;
import reading.ChunkedFileHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static reading.ChunkedFileHelper.write2Bytes;
import static reading.ChunkedFileHelper.write4Bytes;

/**
 * panel.bin
 * referenced in dat09/10/11.dat
 */
public class SphereGridNodeTypeDataObject implements Nameable, Writable {
    public static final int LENGTH = 0x18;
    private final int[] bytes;

    public LocalizedKeyedStringObject name = new LocalizedKeyedStringObject();
    public LocalizedKeyedStringObject unusedString0405 = new LocalizedKeyedStringObject();
    public LocalizedKeyedStringObject description = new LocalizedKeyedStringObject();
    public LocalizedKeyedStringObject unusedString0C0D = new LocalizedKeyedStringObject();

    private int nameOffset;
    private int nameKey;
    private int unusedString0405Offset;
    private int unusedString0405Key;
    private int descriptionOffset;
    private int descriptionKey;
    private int unusedString0C0DOffset;
    private int unusedString0C0DKey;
    private int nodeEffectBitfield; // 0080 = Lock
    private int learnedMove;
    private int increaseAmount;
    private int appearanceType;

    public SphereGridNodeTypeDataObject(int[] bytes, int[] stringBytes, String localization) {
        this.bytes = bytes;
        mapBytes();
        mapStrings(stringBytes, localization);
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
        nodeEffectBitfield = read2Bytes(0x10);
        learnedMove = read2Bytes(0x12);
        increaseAmount = read2Bytes(0x14);
        appearanceType = read2Bytes(0x16);
    }

    @Override
    public int[] toBytes(String localization) {
        int[] array = new int[SphereGridNodeDataObject.LENGTH];
        write4Bytes(array, 0x00, name.getLocalizedContent(localization).toHeaderBytes());
        write4Bytes(array, 0x04, unusedString0405.getLocalizedContent(localization).toHeaderBytes());
        write4Bytes(array, 0x08, description.getLocalizedContent(localization).toHeaderBytes());
        write4Bytes(array, 0x0C, unusedString0C0D.getLocalizedContent(localization).toHeaderBytes());
        write2Bytes(array, 0x10, nodeEffectBitfield);
        write2Bytes(array, 0x12, learnedMove);
        write2Bytes(array, 0x14, increaseAmount);
        write2Bytes(array, 0x16, appearanceType);
        return array;
    }

    private void mapStrings(int[] stringBytes, String localization) {
        if (stringBytes == null || stringBytes.length == 0) {
            return;
        }
        name.readAndSetLocalizedContent(localization, stringBytes, nameOffset, nameKey);
        unusedString0405.readAndSetLocalizedContent(localization, stringBytes, unusedString0405Offset, unusedString0405Key);
        description.readAndSetLocalizedContent(localization, stringBytes, descriptionOffset, descriptionKey);
        unusedString0C0D.readAndSetLocalizedContent(localization, stringBytes, unusedString0C0DOffset, unusedString0C0DKey);
    }

    @Override
    public LocalizedKeyedStringObject getKeyedString(String title) {
        return switch (title) {
            case "name" -> name;
            case "description" -> description;
            default -> null;
        };
    }

    public void setLocalizations(SphereGridNodeTypeDataObject localizationObject) {
        localizationObject.name.copyInto(name);
        localizationObject.unusedString0405.copyInto(unusedString0405);
        localizationObject.description.copyInto(description);
        localizationObject.unusedString0C0D.copyInto(unusedString0C0D);
    }

    @Override
    public Stream<KeyedString> streamKeyedStrings(String localization) {
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
        list.add("Effects: " + StackObject.bitfieldToString("sgNodeEffectsBitfield", nodeEffectBitfield) + StringHelper.hex4Suffix(nodeEffectBitfield));
        if (learnedMove > 0) {
            list.add("Teaches Move: " + asMove(learnedMove));
        }
        if (increaseAmount > 0) {
            list.add("Increase=" + StringHelper.hex4WithSuffix(increaseAmount));
        }
        list.add("Appearance?=" + StringHelper.hex4WithSuffix(appearanceType));
        String full = list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining(", "));
        String descriptionStr = (descriptionOffset > 0 ? description.getDefaultContent().toString() : "");
        return String.format("%-20s", getName()) + " { " + full + " } " + descriptionStr;
    }

    @Override
    public String getName(String localization) {
        return name.getLocalizedString(localization);
    }

    private int read2Bytes(int offset) {
        return ChunkedFileHelper.read2Bytes(bytes, offset);
    }

    private static String asMove(int idx) {
        AbilityDataObject move = DataAccess.getMove(idx);
        return (move != null ? move.name : "null") + StringHelper.hex4Suffix(idx);
    }

    private static String formatUnknownByte(int bt) {
        return StringHelper.formatHex2(bt) + '=' + String.format("%03d", bt) + '(' + String.format("%8s", Integer.toBinaryString(bt)).replace(' ', '0') + ')';
    }
}
