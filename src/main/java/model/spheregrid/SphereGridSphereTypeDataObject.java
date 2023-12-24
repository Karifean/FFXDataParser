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
 * sphere.bin
 * referenced in AbilityDataObject
 */
public class SphereGridSphereTypeDataObject implements Nameable {
    public static final int LENGTH = 0x10;
    private final int[] bytes;

    public String description;
    public String dash;

    private int descriptionOffset;
    private int unknownBytes0203;
    private int dashOffset;
    private int unknownBytes0607;
    private int actionByte;
    private int activationBitfield;
    private int rangeByte;
    private int specialRole;
    private int alwaysZero;

    private boolean isActivator;
    private boolean isShortRange;

    public SphereGridSphereTypeDataObject(int[] bytes, int[] stringBytes) {
        this.bytes = bytes;
        mapBytes();
        mapFlags();
        mapStrings(stringBytes);
    }

    public SphereGridSphereTypeDataObject(int[] bytes) {
        this(bytes, null);
    }

    private void mapBytes() {
        descriptionOffset = read2Bytes(0x00);
        unknownBytes0203 = read2Bytes(0x02);
        dashOffset = read2Bytes(0x04);
        unknownBytes0607 = read2Bytes(0x06);
        actionByte = read2Bytes(0x08);
        activationBitfield = read2Bytes(0x0A);
        rangeByte = bytes[0x0C];
        specialRole = bytes[0x0D];
        alwaysZero = read2Bytes(0x0E);
    }

    private void mapFlags() {
        isActivator = actionByte == 0x01;
        isShortRange = rangeByte == 0x01;
    }

    private void mapStrings(int[] stringBytes) {
        description = StringHelper.getStringAtLookupOffset(stringBytes, descriptionOffset);
        dash = StringHelper.getStringAtLookupOffset(stringBytes, dashOffset);
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        if (actionByte == 0x01) {
            list.add("Activator" + " [" + String.format("%02X", actionByte) + "h]");
        } else if (actionByte == 0x02) {
            list.add("Mutator" + " [" + String.format("%02X", actionByte) + "h]");
        } else {
            list.add("Unknown Action" + " [" + String.format("%02X", actionByte) + "h]");
        }
        if (activationBitfield > 0) {
            list.add("Activates=" + StackObject.bitfieldToString("sgNodeEffectsBitfield", activationBitfield) + " [" + String.format("%04X", activationBitfield) + "h]");
        }
        if (rangeByte == 0x01) {
            list.add("Short Range" + " [" + String.format("%02X", rangeByte) + "h]");
        } else if (rangeByte == 0x20) {
            list.add("Long Range" + " [" + String.format("%02X", rangeByte) + "h]");
        } else {
            list.add("Unknown Range" + " [" + String.format("%02X", rangeByte) + "h]");
        }
        list.add("SpecialRole=" + specialRole + " [" + String.format("%02X", specialRole) + "h]");
        if (alwaysZero > 0) {
            list.add("Not Zero !? " + alwaysZero);
        }
        String full = list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining(", "));
        String dashStr = (dashOffset > 0 && StringHelper.PRINT_DASH_STRINGS_IF_NOT_DASHES && !"-".equals(dash) ? "DH=" + dash + " / " : "");
        String descriptionStr = (descriptionOffset > 0 ? description : "");
        return "{ " + full + " } " + dashStr + descriptionStr;
    }

    @Override
    public String getName() {
        return this.toString();
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
        return String.format("%02X", bt) + '=' + String.format("%03d", bt) + '(' + String.format("%16s", Integer.toBinaryString(bt)).replace(' ', '0') + ')';
    }
}
