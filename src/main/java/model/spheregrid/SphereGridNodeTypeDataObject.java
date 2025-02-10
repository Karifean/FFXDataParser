package model.spheregrid;

import main.DataAccess;
import main.DataReadingManager;
import main.StringHelper;
import model.AbilityDataObject;
import model.LocalizedStringObject;
import model.Nameable;
import atel.model.StackObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * panel.bin
 * referenced in dat09/10/11.dat
 */
public class SphereGridNodeTypeDataObject implements Nameable {
    public static final int LENGTH = 0x18;
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
    private int nodeEffectBitfield; // 0080 = Lock
    private int learnedMove;
    private int increaseAmount;
    private int appearanceType;

    public SphereGridNodeTypeDataObject(int[] bytes, int[] stringBytes, String localization) {
        this.bytes = bytes;
        mapBytes();
        mapStrings(stringBytes, localization);
    }

    public SphereGridNodeTypeDataObject(int[] bytes) {
        this(bytes, null, DataReadingManager.DEFAULT_LOCALIZATION);
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

    private void mapStrings(int[] stringBytes, String localization) {
        name.setLocalizedContent(localization, StringHelper.getStringAtLookupOffset(stringBytes, nameOffset));
        unusedString0405.setLocalizedContent(localization, StringHelper.getStringAtLookupOffset(stringBytes, unusedString0405Offset));
        description.setLocalizedContent(localization, StringHelper.getStringAtLookupOffset(stringBytes, descriptionOffset));
        unusedString0C0D.setLocalizedContent(localization, StringHelper.getStringAtLookupOffset(stringBytes, unusedString0C0DOffset));
    }

    public void setLocalizations(SphereGridNodeTypeDataObject localizationObject) {
        localizationObject.name.copyInto(name);
        localizationObject.unusedString0405.copyInto(unusedString0405);
        localizationObject.description.copyInto(description);
        localizationObject.unusedString0C0D.copyInto(unusedString0C0D);
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
        String descriptionStr = (descriptionOffset > 0 ? description.getDefaultContent() : "");
        return String.format("%-20s", getName()) + " { " + full + " } " + descriptionStr;
    }

    @Override
    public String getName(String localization) {
        return name.getLocalizedContent(localization);
    }

    private int read2Bytes(int offset) {
        int val = bytes[offset];
        val += bytes[offset+1] * 0x100;
        return val;
    }

    private static String asMove(int idx) {
        AbilityDataObject move = DataAccess.getMove(idx);
        return (move != null ? move.name : "null") + StringHelper.hex4Suffix(idx);
    }

    private static String formatUnknownByte(int bt) {
        return StringHelper.formatHex2(bt) + '=' + String.format("%03d", bt) + '(' + String.format("%8s", Integer.toBinaryString(bt)).replace(' ', '0') + ')';
    }
}
