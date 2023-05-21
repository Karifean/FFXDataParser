package model;

import main.DataAccess;
import script.model.StackObject;

public class MonsterSpoilsDataObject {

    private final int[] bytes;

    int gil;
    int apNormal;
    int apOverkill;
    boolean hasRonsoRage;
    int ronsoRage;
    int dropChancePrimary;
    int dropChanceSecondary;
    int stealChance;
    int dropChanceGear;
    int dropNormalTypePrimaryCommon;
    int dropNormalTypePrimaryRare;
    int dropNormalTypeSecondaryCommon;
    int dropNormalTypeSecondaryRare;
    int dropNormalQuantityPrimaryCommon;
    int dropNormalQuantityPrimaryRare;
    int dropNormalQuantitySecondaryCommon;
    int dropNormalQuantitySecondaryRare;
    int dropOverkillTypePrimaryCommon;
    int dropOverkillTypePrimaryRare;
    int dropOverkillTypeSecondaryCommon;
    int dropOverkillTypeSecondaryRare;
    int dropOverkillQuantityPrimaryCommon;
    int dropOverkillQuantityPrimaryRare;
    int dropOverkillQuantitySecondaryCommon;
    int dropOverkillQuantitySecondaryRare;
    int stealItemTypeCommon;
    int stealItemTypeRare;
    int stealItemQuantityCommon;
    int stealItemQuantityRare;
    int bribeItem;

    public MonsterSpoilsDataObject(int[] bytes) {
        this.bytes = bytes;
        mapSpoilsBytes();
    }

    private void mapSpoilsBytes() {
        gil = read2Bytes(bytes, 0x00);
        apNormal = read2Bytes(bytes, 0x02);
        apOverkill = read2Bytes(bytes, 0x04);
        hasRonsoRage = bytes[0x06] > 0 || bytes[0x07] > 0;
        ronsoRage = read2Bytes(bytes, 0x06);
        dropChancePrimary = bytes[0x08];
        dropChanceSecondary = bytes[0x09];
        stealChance = bytes[0x0A];
        dropChanceGear = bytes[0x0B];
        dropNormalTypePrimaryCommon = read2Bytes(bytes, 0x0C);
        dropNormalTypePrimaryRare = read2Bytes(bytes, 0x0E);
        dropNormalTypeSecondaryCommon = read2Bytes(bytes, 0x10);
        dropNormalTypeSecondaryRare = read2Bytes(bytes, 0x12);
        dropNormalQuantityPrimaryCommon = bytes[0x14];
        dropNormalQuantityPrimaryRare = bytes[0x15];
        dropNormalQuantitySecondaryCommon = bytes[0x16];
        dropNormalQuantitySecondaryRare = bytes[0x17];
        dropOverkillTypePrimaryCommon = read2Bytes(bytes, 0x18);
        dropOverkillTypePrimaryRare = read2Bytes(bytes, 0x1A);
        dropOverkillTypeSecondaryCommon = read2Bytes(bytes, 0x1C);
        dropOverkillTypeSecondaryRare = read2Bytes(bytes, 0x1E);
        dropOverkillQuantityPrimaryCommon = bytes[0x20];
        dropOverkillQuantityPrimaryRare = bytes[0x21];
        dropOverkillQuantitySecondaryCommon = bytes[0x22];
        dropOverkillQuantitySecondaryRare = bytes[0x23];
        stealItemTypeCommon = read2Bytes(bytes, 0x24);
        stealItemTypeRare = read2Bytes(bytes, 0x26);
        stealItemQuantityCommon = bytes[0x28];
        stealItemQuantityRare = bytes[0x29];
        bribeItem = read2Bytes(bytes, 0x2A);
    }

    @Override
    public String toString() {
        String mainSpoils = "Gil=" + gil + "\nAP Normal=" + apNormal + " Overkill=" + apOverkill + (hasRonsoRage ? "\nRonso Rage=" + asMove(ronsoRage) : "") + '\n';
        String primaryItem = "Primary Item Drop: Chance=" + dropChancePrimary + "/255\n" +
                (dropChancePrimary > 0 ? " COMMON Normal " + dropNormalQuantityPrimaryCommon + "x " + asMove(dropNormalTypePrimaryCommon) + "; Overkill " + dropOverkillQuantityPrimaryCommon + "x " + asMove(dropOverkillTypePrimaryCommon) + '\n' +
                "   RARE Normal " + dropNormalQuantityPrimaryRare + "x " + asMove(dropNormalTypePrimaryRare) + "; Overkill " + dropOverkillQuantityPrimaryRare + "x " + asMove(dropOverkillTypePrimaryRare) + '\n' : "");
        String secondaryItem = "Secondary Item Drop: Chance=" + dropChanceSecondary + "/255\n" +
                (dropChanceSecondary > 0 ? " COMMON Normal " + dropNormalQuantitySecondaryCommon + "x " + asMove(dropNormalTypeSecondaryCommon) + "; Overkill " + dropOverkillQuantitySecondaryCommon + "x " + asMove(dropOverkillTypeSecondaryCommon) + '\n' +
                "   RARE Normal " + dropNormalQuantitySecondaryRare + "x " + asMove(dropNormalTypeSecondaryRare) + "; Overkill " + dropOverkillQuantitySecondaryRare + "x " + asMove(dropOverkillTypeSecondaryRare) + '\n' : "");

        String stealItem = "Steal Item: Chance=" + stealChance + "/255\n" +
                (stealChance > 0 ? " COMMON " + stealItemQuantityCommon + "x " + asMove(stealItemTypeCommon) + '\n' + "   RARE " + stealItemQuantityRare + "x " + asMove(stealItemTypeRare) + '\n' : "");

        return mainSpoils + '\n' + primaryItem + '\n' + secondaryItem + '\n' + stealItem + '\n';
    }

    private static int read2Bytes(int[] bytes, int offset) {
        return bytes[offset] + bytes[offset+1] * 0x100;
    }

    private static String asMove(int move) {
        return DataAccess.getMove(move).getName() + " [" + String.format("%04X", move) + "h]";
    }

    private static String formatUnknownByte(int bt) {
        return String.format("%02X", bt) + '=' + String.format("%03d", bt) + '(' + String.format("%8s", Integer.toBinaryString(bt)).replace(' ', '0') + ')';
    }

}
