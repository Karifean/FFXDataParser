package model.spheregrid;

import main.DataAccess;
import main.StringHelper;
import model.*;
import atel.model.StackObject;
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
 * sphere.bin
 * referenced in AbilityDataObject
 */
public class SphereGridSphereTypeDataObject implements Writable, Localized<SphereGridSphereTypeDataObject> {
    public static final int LENGTH = 0x10;
    private final int[] bytes;

    public LocalizedKeyedStringObject description = new LocalizedKeyedStringObject();
    public LocalizedKeyedStringObject unusedString0405 = new LocalizedKeyedStringObject();

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
        description.readAndSetLocalizedContent(localization, stringBytes, descriptionOffset, descriptionKey);
        unusedString0405.readAndSetLocalizedContent(localization, stringBytes, unusedString0405Offset, unusedString0405Key);
    }

    @Override
    public LocalizedKeyedStringObject getKeyedString(String title) {
        return switch (title) {
            case "description" -> description;
            default -> null;
        };
    }

    @Override
    public Stream<KeyedString> streamKeyedStrings(String localization) {
        return Stream.of(
                description.getLocalizedContent(localization),
                unusedString0405.getLocalizedContent(localization)
        );
    }

    @Override
    public int[] toBytes(String localization) {
        int[] array = new int[SphereGridSphereTypeDataObject.LENGTH];
        write4Bytes(array, 0x00, description.getLocalizedContent(localization).toHeaderBytes());
        write4Bytes(array, 0x04, unusedString0405.getLocalizedContent(localization).toHeaderBytes());
        write2Bytes(array, 0x08, actionByte);
        write2Bytes(array, 0x0A, activationBitfield);
        array[0x0C] = rangeByte;
        array[0x0D] = specialRole;
        write2Bytes(array, 0x0E, alwaysZero);
        return array;
    }

    @Override
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
        String descriptionStr = (descriptionOffset > 0 ? description.getDefaultContent().toString() : "");
        return "{ " + full + " } " + descriptionStr;
    }

    private int read2Bytes(int offset) {
        return ChunkedFileHelper.read2Bytes(bytes, offset);
    }

    private static String asMove(int idx) {
        AbilityDataObject move = DataAccess.getMove(idx);
        return (move != null ? move.name : "null") + StringHelper.hex4Suffix(idx);
    }

    private static String formatUnknownByte(int bt) {
        return StringHelper.formatHex2(bt) + '=' + String.format("%03d", bt) + '(' + String.format("%16s", Integer.toBinaryString(bt)).replace(' ', '0') + ')';
    }
}
