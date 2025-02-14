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
 * sphere.bin
 * referenced in AbilityDataObject
 */
public class SphereGridSphereTypeDataObject implements Nameable {
    public static final int LENGTH = 0x10;
    private final int[] bytes;

    public LocalizedStringObject description = new LocalizedStringObject();
    public LocalizedStringObject unusedString0405 = new LocalizedStringObject();

    private int descriptionOffset;
    private int descriptionKey;
    private int unusedString0405Offset;
    private int unusedString0405Key;
    private int actionByte;
    private int activationBitfield;
    private int rangeByte;
    private int specialRole;
    private int alwaysZero;

    private boolean isActivator;
    private boolean isShortRange;

    public SphereGridSphereTypeDataObject(int[] bytes, int[] stringBytes, String localization) {
        this.bytes = bytes;
        mapBytes();
        mapFlags();
        mapStrings(stringBytes, localization);
    }

    public SphereGridSphereTypeDataObject(int[] bytes) {
        this(bytes, null, DataReadingManager.DEFAULT_LOCALIZATION);
    }

    private void mapBytes() {
        descriptionOffset = read2Bytes(0x00);
        descriptionKey = read2Bytes(0x02);
        unusedString0405Offset = read2Bytes(0x04);
        unusedString0405Key = read2Bytes(0x06);
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

    private void mapStrings(int[] stringBytes, String localization) {
        if (stringBytes == null || stringBytes.length == 0) {
            return;
        }
        description.readAndSetLocalizedContent(localization, stringBytes, descriptionOffset);
        unusedString0405.readAndSetLocalizedContent(localization, stringBytes, unusedString0405Offset);
    }

    public void setLocalizations(SphereGridSphereTypeDataObject localizationObject) {
        localizationObject.description.copyInto(description);
        localizationObject.unusedString0405.copyInto(unusedString0405);
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        if (actionByte == 0x01) {
            list.add("Activator" + StringHelper.hex2Suffix(actionByte));
        } else if (actionByte == 0x02) {
            list.add("Mutator" + StringHelper.hex2Suffix(actionByte));
        } else {
            list.add("Unknown Action" + StringHelper.hex2Suffix(actionByte));
        }
        if (activationBitfield > 0) {
            list.add("Activates=" + StackObject.bitfieldToString("sgNodeEffectsBitfield", activationBitfield) + StringHelper.hex4Suffix(activationBitfield));
        }
        if (rangeByte == 0x01) {
            list.add("Short Range" + StringHelper.hex2Suffix(rangeByte));
        } else if (rangeByte == 0x20) {
            list.add("Long Range" + StringHelper.hex2Suffix(rangeByte));
        } else {
            list.add("Unknown Range" + StringHelper.hex2Suffix(rangeByte));
        }
        list.add("SpecialRole=" + StringHelper.hex2WithSuffix(specialRole));
        if (alwaysZero > 0) {
            list.add("Not Zero !? " + alwaysZero);
        }
        String full = list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining(", "));
        String descriptionStr = (descriptionOffset > 0 ? description.getDefaultContent() : "");
        return "{ " + full + " } " + descriptionStr;
    }

    @Override
    public String getName(String localization) {
        return this.toString();
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
        return StringHelper.formatHex2(bt) + '=' + String.format("%03d", bt) + '(' + String.format("%16s", Integer.toBinaryString(bt)).replace(' ', '0') + ')';
    }
}
