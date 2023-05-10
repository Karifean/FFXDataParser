package model;

import script.model.StackObject;

public class MonsterSpoilsDataObject {

    private int[] bytes;

    int gil;
    int apNormal;
    int apOverkill;
    boolean hasRonsoRage;
    String ronsoRage;
    int dropChancePrimary;
    int dropChanceSecondary;
    int stealChance;
    int dropChanceGear;
    String dropNormalTypePrimaryCommon;
    String dropNormalTypePrimaryRare;
    String dropNormalTypeSecondaryCommon;
    String dropNormalTypeSecondaryRare;
    int dropNormalQuantityPrimaryCommon;
    int dropNormalQuantityPrimaryRare;
    int dropNormalQuantitySecondaryCommon;
    int dropNormalQuantitySecondaryRare;
    String dropOverkillTypePrimaryCommon;
    String dropOverkillTypePrimaryRare;
    String dropOverkillTypeSecondaryCommon;
    String dropOverkillTypeSecondaryRare;
    int dropOverkillQuantityPrimaryCommon;
    int dropOverkillQuantityPrimaryRare;
    int dropOverkillQuantitySecondaryCommon;
    int dropOverkillQuantitySecondaryRare;
    String stealItemTypeCommon;
    String stealItemTypeRare;
    int stealItemQuantityCommon;
    int stealItemQuantityRare;
    String bribeItem;

    public MonsterSpoilsDataObject() {}

    public MonsterSpoilsDataObject(int[] bytes) {
        this.bytes = bytes;
        mapSpoilsBytes();
    }

    private void mapSpoilsBytes() {
        gil = read2Bytes(bytes, 0x00);
        apNormal = read2Bytes(bytes, 0x02);
        apOverkill = read2Bytes(bytes, 0x04);
        hasRonsoRage = bytes[0x06] > 0 || bytes[0x07] > 0;
        ronsoRage = readMove(bytes, 0x06);
        dropChancePrimary = bytes[0x08];
        dropChanceSecondary = bytes[0x09];
        stealChance = bytes[0x0A];
        dropChanceGear = bytes[0x0B];
        dropNormalTypePrimaryCommon = readMove(bytes, 0x0C);
        dropNormalTypePrimaryRare = readMove(bytes, 0x0E);
        dropNormalTypeSecondaryCommon = readMove(bytes, 0x10);
        dropNormalTypeSecondaryRare = readMove(bytes, 0x12);
        dropNormalQuantityPrimaryCommon = bytes[0x14];
        dropNormalQuantityPrimaryRare = bytes[0x15];
        dropNormalQuantitySecondaryCommon = bytes[0x16];
        dropNormalQuantitySecondaryRare = bytes[0x17];
        dropOverkillTypePrimaryCommon = readMove(bytes, 0x18);
        dropOverkillTypePrimaryRare = readMove(bytes, 0x1A);
        dropOverkillTypeSecondaryCommon = readMove(bytes, 0x1C);
        dropOverkillTypeSecondaryRare = readMove(bytes, 0x1E);
        dropOverkillQuantityPrimaryCommon = bytes[0x20];
        dropOverkillQuantityPrimaryRare = bytes[0x21];
        dropOverkillQuantitySecondaryCommon = bytes[0x22];
        dropOverkillQuantitySecondaryRare = bytes[0x23];
        stealItemTypeCommon = readMove(bytes, 0x24);
        stealItemTypeRare = readMove(bytes, 0x26);
        stealItemQuantityCommon = bytes[0x28];
        stealItemQuantityRare = bytes[0x29];
        bribeItem = readMove(bytes, 0x2A);
    }

    @Override
    public String toString() {

        String mainSpoils = "Gil=" + gil + "\nAP Normal=" + apNormal + " Overkill=" + apOverkill + (hasRonsoRage ? "\nRonso Rage=" + ronsoRage : "") + '\n';
        String primaryItem = "Primary Item Drop: Chance=" + dropChancePrimary + "/255\n" +
                (dropChancePrimary > 0 ? " COMMON Normal " + dropNormalQuantityPrimaryCommon + "x " + dropNormalTypePrimaryCommon + "; Overkill " + dropOverkillQuantityPrimaryCommon + "x " + dropOverkillTypePrimaryCommon + '\n' +
                "   RARE Normal " + dropNormalQuantityPrimaryRare + "x " + dropNormalTypePrimaryRare + "; Overkill " + dropOverkillQuantityPrimaryRare + "x " + dropOverkillTypePrimaryRare + '\n' : "");
        String secondaryItem = "Secondary Item Drop: Chance=" + dropChanceSecondary + "/255\n" +
                (dropChanceSecondary > 0 ? " COMMON Normal " + dropNormalQuantitySecondaryCommon + "x " + dropNormalTypeSecondaryCommon + "; Overkill " + dropOverkillQuantitySecondaryCommon + "x " + dropOverkillTypeSecondaryCommon + '\n' +
                "   RARE Normal " + dropNormalQuantitySecondaryRare + "x " + dropNormalTypeSecondaryRare + "; Overkill " + dropOverkillQuantitySecondaryRare + "x " + dropOverkillTypeSecondaryRare + '\n' : "");

        String stealItem = "Steal Item: Chance=" + stealChance + "/255\n" +
                (stealChance > 0 ? " COMMON " + stealItemQuantityCommon + "x " + stealItemTypeCommon + '\n' + "   RARE " + stealItemQuantityRare + "x " + stealItemTypeRare + '\n' : "");

        return mainSpoils + '\n' + primaryItem + '\n' + secondaryItem + '\n' + stealItem + '\n';
    }

    private static int read2Bytes(int[] bytes, int offset) {
        int val = bytes[offset];
        val += bytes[offset+1] * 0x100;
        return val;
    }

    private static int read4Bytes(int[] bytes, int offset) {
        int val = bytes[offset];
        val += bytes[offset+1] * 0x100;
        val += bytes[offset+2] * 0x10000;
        val += bytes[offset+3] * 0x1000000;
        return val;
    }

    private static String readMove(int[] bytes, int offset) {
        return asMove(read2Bytes(bytes, offset));
    }

    private static String asMove(int move) {
        return new StackObject("move", false, null, move).toString();
    }

    private static String formatUnknownByte(int bt) {
        return String.format("%02x", bt) + '=' + String.format("%03d", bt) + '(' + String.format("%8s", Integer.toBinaryString(bt)).replace(' ', '0') + ')';
    }

}
