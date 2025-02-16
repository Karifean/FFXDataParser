package model;

import main.DataAccess;
import main.StringHelper;
import model.strings.KeyedString;
import model.strings.LocalizedKeyedStringObject;

import java.util.stream.Stream;

import static reading.ChunkedFileHelper.write2Bytes;

/**
 * kaizou.bin
 * sum_grow.bin
 */
public class CustomizationDataObject implements Writable {
    public static final int LENGTH = 0x08;
    private final int[] bytes;

    private int customizeTargetByte;
    private int customizedAbility;
    private int requiredItemType;
    private int requiredItemQuantity;
    private int requiredItemQuantityBase;

    public CustomizationDataObject(int[] bytes, int[] stringBytes, String localization) {
        this.bytes = bytes;
        mapBytes();
    }

    private void mapBytes() {
        customizeTargetByte = read2Bytes(0x00);
        customizedAbility = read2Bytes(0x02);
        requiredItemType = read2Bytes(0x04);
        requiredItemQuantity = bytes[0x06];
        requiredItemQuantityBase = bytes[0x07];
    }

    @Override
    public Stream<KeyedString> streamKeyedStrings(String localization) {
        return Stream.of();
    }

    @Override
    public LocalizedKeyedStringObject getKeyedString(String title) {
        return null;
    }

    @Override
    public int[] toBytes(String localization) {
        int[] array = new int[CustomizationDataObject.LENGTH];
        write2Bytes(array, 0x00, customizeTargetByte);
        write2Bytes(array, 0x02, customizedAbility);
        write2Bytes(array, 0x04, requiredItemType);
        array[0x06] = requiredItemQuantity;
        array[0x07] = requiredItemQuantityBase;
        return array;
    }

    @Override
    public String toString() {
        String customizeTarget = customizeTargetByte == 0x01 ? "Weapon" : customizeTargetByte == 0x02 ? "Armor " : customizeTargetByte == 0x7F ? "Aeon" : "Target Unknown";
        if (customizedAbility >= 0x1000) {
            GearAbilityDataObject gearAbility = DataAccess.getGearAbility(customizedAbility);
            AbilityDataObject move = DataAccess.getMove(customizedAbility);
            Nameable relevantNameable = gearAbility != null ? gearAbility : move != null ? move : (l) -> "null";
            String result = relevantNameable.getName() + StringHelper.hex4Suffix(customizedAbility);
            String costString = requiredItemQuantity + "x " + asMove(requiredItemType);
            return customizeTarget + " - " + result + ": " + costString;
        } else {
            String result = asStatIncrease(customizedAbility) + " +" + requiredItemQuantity;
            String costString = requiredItemQuantityBase + "x " + asMove(requiredItemType);
            return customizeTarget + " - " + result + ": " + costString;
        }
    }

    private static String asStatIncrease(int idx) {
        return switch (idx) {
            case 0 -> "HP";
            case 1 -> "MP";
            case 2 -> "STR";
            case 3 -> "DEF";
            case 4 -> "MAG";
            case 5 -> "MDF";
            case 6 -> "AGI";
            case 7 -> "LCK";
            case 8 -> "EVA";
            case 9 -> "ACC";
            default -> "null";
        };
    }

    private static String asMove(int idx) {
        AbilityDataObject move = DataAccess.getMove(idx);
        return (move != null ? move.name : "null") + StringHelper.hex4Suffix(idx);
    }

    private int read2Bytes(int offset) {
        return bytes[offset] + bytes[offset+1] * 0x100;
    }
}
