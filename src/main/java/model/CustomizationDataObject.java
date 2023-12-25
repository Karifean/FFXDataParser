package model;

import main.DataAccess;

/**
 * takara.bin
 */
public class CustomizationDataObject implements Nameable {
    public static final int LENGTH = 0x08;
    private final int[] bytes;

    private int customizeTargetByte;
    private int customizedAbility;
    private int requiredItemType;
    private int requiredItemQuantity;
    private int requiredItemQuantityBase;

    public CustomizationDataObject(int[] bytes, int[] stringBytes) {
        this.bytes = bytes;
        mapBytes();
    }

    public CustomizationDataObject(int[] bytes) {
        this(bytes, null);
    }

    private void mapBytes() {
        customizeTargetByte = read2Bytes(0x00);
        customizedAbility = read2Bytes(0x02);
        requiredItemType = read2Bytes(0x04);
        requiredItemQuantity = bytes[0x06];
        requiredItemQuantityBase = bytes[0x07];
    }

    @Override
    public String toString() {
        String customizeTarget = customizeTargetByte == 0x01 ? "Weapon" : customizeTargetByte == 0x02 ? "Armor " : customizeTargetByte == 0x7F ? "Aeon" : "Target Unknown";
        if (customizedAbility >= 0x1000) {
            GearAbilityDataObject gearAbility = DataAccess.getGearAbility(customizedAbility);
            AbilityDataObject move = DataAccess.getMove(customizedAbility);
            String result = gearAbility != null ? gearAbility.getName() : move != null ? move.getName() : "null";
            String costString = requiredItemQuantity + "x " + asMove(requiredItemType);
            return customizeTarget + " - " + result + ": " + costString;
        } else {
            String result = asStatIncrease(customizedAbility) + " +" + requiredItemQuantity;
            String costString = requiredItemQuantityBase + "x " + asMove(requiredItemType);
            return customizeTarget + " - " + result + ": " + costString;
        }
    }

    @Override
    public String getName() {
        return this.toString();
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
        return (move != null ? move.name : "null") + " [" + String.format("%04X", idx) + "h]";
    }

    private int read2Bytes(int offset) {
        return bytes[offset] + bytes[offset+1] * 0x100;
    }
}
