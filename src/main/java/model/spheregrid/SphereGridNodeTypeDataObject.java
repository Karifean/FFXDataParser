package model.spheregrid;

import main.DataAccess;
import main.StringHelper;
import model.AbilityDataObject;
import model.Nameable;
import script.model.StackObject;

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

    public String name;
    public String dash;
    public String description;
    public String otherText;

    private int nameOffset;
    private int unknownBytes0203;
    private int dashOffset;
    private int unknownBytes0607;
    private int descriptionOffset;
    private int unknownBytes0A0B;
    private int otherTextOffset;
    private int unknownBytes0E0F;
    private int nodeEffectBitfield; // 0080 = Lock
    private int learnedMove;
    private int increaseAmount;
    private int appearanceType;

    public SphereGridNodeTypeDataObject(int[] bytes, int[] stringBytes) {
        this.bytes = bytes;
        mapBytes();
        mapStrings(stringBytes);
    }

    public SphereGridNodeTypeDataObject(int[] bytes) {
        this(bytes, null);
    }

    private void mapBytes() {
        nameOffset = read2Bytes(0x00);
        unknownBytes0203 = read2Bytes(0x02);
        dashOffset = read2Bytes(0x04);
        unknownBytes0607 = read2Bytes(0x06);
        descriptionOffset = read2Bytes(0x08);
        unknownBytes0A0B = read2Bytes(0x0A);
        otherTextOffset = read2Bytes(0x0C);
        unknownBytes0E0F = read2Bytes(0x0E);
        nodeEffectBitfield = read2Bytes(0x10);
        learnedMove = read2Bytes(0x12);
        increaseAmount = read2Bytes(0x14);
        appearanceType = read2Bytes(0x16);
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
        list.add("Effects: " + StackObject.bitfieldToString("sgNodeEffectsBitfield", nodeEffectBitfield) + " [" + String.format("%04X", nodeEffectBitfield) + "h]");
        if (learnedMove > 0) {
            list.add("Teaches Move: " + asMove(learnedMove));
        }
        if (increaseAmount > 0) {
            list.add("Increase=" + increaseAmount + " [" + String.format("%04X", increaseAmount) + "h]");
        }
        list.add("Appearance?=" + appearanceType + " [" + String.format("%04X", appearanceType) + "h]");
        String full = list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining(", "));
        String dashStr = (dashOffset > 0 && StringHelper.PRINT_DASH_STRINGS_IF_NOT_DASHES && !"-".equals(dash) ? "DH=" + dash + " / " : "");
        String descriptionStr = (descriptionOffset > 0 ? description : "");
        String soText = (otherTextOffset > 0 && StringHelper.PRINT_DASH_STRINGS_IF_NOT_DASHES && !"-".equals(otherText) ? " / OT=" + otherText : "");
        return String.format("%-20s", getName()) + " { " + full + " } " + dashStr + descriptionStr + soText;
    }

    @Override
    public String getName(String localization) {
        return name;
    }

    private int read2Bytes(int offset) {
        int val = bytes[offset];
        val += bytes[offset+1] * 0x100;
        return val;
    }

    private static String asMove(int idx) {
        AbilityDataObject move = DataAccess.getMove(idx);
        return (move != null ? move.name : "null") + " [" + String.format("%04X", idx) + "h]";
    }

    private static String formatUnknownByte(int bt) {
        return String.format("%02X", bt) + '=' + String.format("%03d", bt) + '(' + String.format("%8s", Integer.toBinaryString(bt)).replace(' ', '0') + ')';
    }
}
