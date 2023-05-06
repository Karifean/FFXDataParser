package model;

import script.model.StackObject;

public class MonsterDataObject {

    private int[] stats;
    private int[] spoils;

    int hp;
    int mp;
    int overkillThreshold;
    int str;
    int def;
    int mag;
    int mdf;
    int agi;
    int lck;
    int eva;
    int acc;
    int miscProperties28;
    int miscProperties29;
    int poisonDamage;
    int elementalAbsorbFlags;
    int elementalNullFlags;
    int elementalResistFlags;
    int elementalWeakFlags;
    int statusResistanceDeath;
    int statusResistanceZombie;
    int statusResistancePetrify;
    int statusResistancePoison;
    int statusResistancePowerBreak;
    int statusResistanceMagicBreak;
    int statusResistanceArmorBreak;
    int statusResistanceMentalBreak;
    int statusResistanceConfuse;
    int statusResistanceBerserk;
    int statusResistanceProvoke;
    int statusChanceThreaten;
    int statusResistanceSleep;
    int statusResistanceSilence;
    int statusResistanceDarkness;
    int statusResistanceShell;
    int statusResistanceProtect;
    int statusResistanceReflect;
    int statusResistanceRegen;
    int statusResistanceNBlaze;
    int statusResistanceNFrost;
    int statusResistanceNShock;
    int statusResistanceNTide;
    int statusResistanceHaste;
    int statusResistanceSlow;
    int autoStatuses1;
    int autoStatuses2;
    int autoStatuses3;
    int autoStatuses4;

    int extraStatusImmunities1;
    int extraStatusImmunities2;

    int forcedAction;

    boolean armored;
    boolean immunityFractionalDamage;
    boolean immunityLife;
    boolean immunitySensor;
    boolean props28bit5;
    boolean immunityPhysicalDamage;
    boolean immunityMagicalDamage;
    boolean props28bit8;
    boolean immunityDelay;

    boolean autoDeathUhWtf;
    boolean autoZombie;
    boolean autoPetrifyUhWtf;
    boolean autoPoison;

    boolean immunityScan;
    boolean immunityDistillPower;
    boolean immunityDistillMana;
    boolean immunityDistillSpeed;
    boolean immunityUnused1;
    boolean immunityDistillAbility;
    boolean immunityShield;
    boolean immunityBoost;
    boolean immunityEject;
    boolean immunityAutoLife;
    boolean immunityCurse;
    boolean immunityDefend;
    boolean immunityGuard;
    boolean immunitySentinel;
    boolean immunityDoom;
    boolean immunityUnused2;

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

    public MonsterDataObject() {}

    public MonsterDataObject(int[] statBytes, int[] spoilsBytes) {
        this.stats = statBytes;
        this.spoils = spoilsBytes;
        mapStatBytes();
        mapSpoilsBytes();
    }

    private void mapStatBytes() {
        hp = read4Bytes(stats, 0x14);
        mp = read4Bytes(stats, 0x18);
        overkillThreshold = read4Bytes(stats, 0x1C);
        str = stats[0x20];
        def = stats[0x21];
        mag = stats[0x22];
        mdf = stats[0x23];
        agi = stats[0x24];
        lck = stats[0x25];
        eva = stats[0x26];
        acc = stats[0x27];
        miscProperties28 = stats[0x28];
        miscProperties29 = stats[0x29];
        poisonDamage = stats[0x2A];
        elementalAbsorbFlags = stats[0x2B];
        elementalNullFlags = stats[0x2C];
        elementalResistFlags = stats[0x2D];
        elementalWeakFlags = stats[0x2E];
        statusResistanceDeath = stats[0x2F];
        statusResistanceZombie = stats[0x30];
        statusResistancePetrify = stats[0x31];
        statusResistancePoison = stats[0x32];
        statusResistancePowerBreak = stats[0x33];
        statusResistanceMagicBreak = stats[0x34];
        statusResistanceArmorBreak = stats[0x35];
        statusResistanceMentalBreak = stats[0x36];
        statusResistanceConfuse = stats[0x37];
        statusResistanceBerserk = stats[0x38];
        statusResistanceProvoke = stats[0x39];
        statusChanceThreaten = stats[0x3A];
        statusResistanceSleep = stats[0x3B];
        statusResistanceSilence = stats[0x3C];
        statusResistanceDarkness = stats[0x3D];
        statusResistanceShell = stats[0x3E];
        statusResistanceProtect = stats[0x3F];
        statusResistanceReflect = stats[0x40];
        statusResistanceNTide = stats[0x41];
        statusResistanceNBlaze = stats[0x42];
        statusResistanceNShock = stats[0x43];
        statusResistanceNFrost = stats[0x44];
        statusResistanceRegen = stats[0x45];
        statusResistanceHaste = stats[0x46];
        statusResistanceSlow = stats[0x47];
        autoStatuses1 = stats[0x48];
        autoStatuses2 = stats[0x49];
        autoStatuses3 = stats[0x4A];
        autoStatuses4 = stats[0x4B];

        extraStatusImmunities1 = stats[0x4E];
        extraStatusImmunities2 = stats[0x4F];

        forcedAction = read2Bytes(stats, 0x70);

        mapStatsFlags();
    }

    private void mapStatsFlags() {
        armored = (miscProperties28 & 0x01) > 0;
        immunityFractionalDamage = (miscProperties28 & 0x02) > 0;
        immunityLife = (miscProperties28 & 0x04) > 0;
        immunitySensor = (miscProperties28 & 0x08) > 0;
        props28bit5 = (miscProperties28 & 0x10) > 0;
        immunityPhysicalDamage = (miscProperties28 & 0x20) > 0;
        immunityMagicalDamage = (miscProperties28 & 0x40) > 0;
        props28bit8 = (miscProperties28 & 0x80) > 0;
        immunityDelay = (miscProperties29 & 0x01) > 0;

        autoZombie = (autoStatuses1 & 0x02) > 0;
        autoPoison = (autoStatuses1 & 0x08) > 0;
        immunityScan = (extraStatusImmunities1 & 0x01) > 0;
        immunityDistillPower = (extraStatusImmunities1 & 0x02) > 0;
        immunityDistillMana = (extraStatusImmunities1 & 0x04) > 0;
        immunityDistillSpeed = (extraStatusImmunities1 & 0x08) > 0;
        immunityUnused1 = (extraStatusImmunities1 & 0x10) > 0;
        immunityDistillAbility = (extraStatusImmunities1 & 0x20) > 0;
        immunityShield = (extraStatusImmunities1 & 0x40) > 0;
        immunityBoost = (extraStatusImmunities1 & 0x80) > 0;
        immunityEject = (extraStatusImmunities2 & 0x01) > 0;
        immunityAutoLife = (extraStatusImmunities2 & 0x02) > 0;
        immunityCurse = (extraStatusImmunities2 & 0x04) > 0;
        immunityDefend = (extraStatusImmunities2 & 0x08) > 0;
        immunityGuard = (extraStatusImmunities2 & 0x10) > 0;
        immunitySentinel = (extraStatusImmunities2 & 0x20) > 0;
        immunityDoom = (extraStatusImmunities2 & 0x40) > 0;
        immunityUnused2 = (extraStatusImmunities2 & 0x80) > 0;
    }
    private void mapSpoilsBytes() {
        gil = read2Bytes(spoils, 0x00);
        apNormal = read2Bytes(spoils, 0x02);
        apOverkill = read2Bytes(spoils, 0x04);
        hasRonsoRage = spoils[0x06] > 0 || spoils[0x07] > 0;
        ronsoRage = readMove(spoils, 0x06);
        dropChancePrimary = spoils[0x08];
        dropChanceSecondary = spoils[0x09];
        stealChance = spoils[0x0A];
        dropChanceGear = spoils[0x0B];
        dropNormalTypePrimaryCommon = readMove(spoils, 0x0C);
        dropNormalTypePrimaryRare = readMove(spoils, 0x0E);
        dropNormalTypeSecondaryCommon = readMove(spoils, 0x10);
        dropNormalTypeSecondaryRare = readMove(spoils, 0x12);
        dropNormalQuantityPrimaryCommon = spoils[0x14];
        dropNormalQuantityPrimaryRare = spoils[0x15];
        dropNormalQuantitySecondaryCommon = spoils[0x16];
        dropNormalQuantitySecondaryRare = spoils[0x17];
        dropOverkillTypePrimaryCommon = readMove(spoils, 0x18);
        dropOverkillTypePrimaryRare = readMove(spoils, 0x1A);
        dropOverkillTypeSecondaryCommon = readMove(spoils, 0x1C);
        dropOverkillTypeSecondaryRare = readMove(spoils, 0x1E);
        dropOverkillQuantityPrimaryCommon = spoils[0x20];
        dropOverkillQuantityPrimaryRare = spoils[0x21];
        dropOverkillQuantitySecondaryCommon = spoils[0x22];
        dropOverkillQuantitySecondaryRare = spoils[0x23];
        stealItemTypeCommon = readMove(spoils, 0x24);
        stealItemTypeRare = readMove(spoils, 0x26);
        stealItemQuantityCommon = spoils[0x28];
        stealItemQuantityRare = spoils[0x29];
        bribeItem = readMove(spoils, 0x2A);
    }

    @Override
    public String toString() {
        String bigStats = "HP=" + hp + " MP=" + mp + " Overkill=" + overkillThreshold;
        String mainStats = "STR=" + str + " DEF=" + def + " MAG=" + mag + " MDF=" + mdf + "\nAGI=" + agi + " LCK=" + lck + " EVA=" + eva + " ACC=" + acc;

        String statsString = bigStats + '\n' + mainStats + '\n';

        String mainSpoils = "Gil=" + gil + "\nAP Normal=" + apNormal + " Overkill=" + apOverkill + (hasRonsoRage ? "\nRonso Rage=" + ronsoRage : "") + '\n';
        String primaryItem = "Primary Item Drop: Chance=" + dropChancePrimary + "/255\n" +
                (dropChancePrimary > 0 ? " COMMON Normal " + dropNormalQuantityPrimaryCommon + "x " + dropNormalTypePrimaryCommon + "; Overkill " + dropOverkillQuantityPrimaryCommon + "x " + dropOverkillTypePrimaryCommon + '\n' +
                "   RARE Normal " + dropNormalQuantityPrimaryRare + "x " + dropNormalTypePrimaryRare + "; Overkill " + dropOverkillQuantityPrimaryRare + "x " + dropOverkillTypePrimaryRare + '\n' : "");
        String secondaryItem = "Secondary Item Drop: Chance=" + dropChanceSecondary + "/255\n" +
                (dropChanceSecondary > 0 ? " COMMON Normal " + dropNormalQuantitySecondaryCommon + "x " + dropNormalTypeSecondaryCommon + "; Overkill " + dropOverkillQuantitySecondaryCommon + "x " + dropOverkillTypeSecondaryCommon + '\n' +
                "   RARE Normal " + dropNormalQuantitySecondaryRare + "x " + dropNormalTypeSecondaryRare + "; Overkill " + dropOverkillQuantitySecondaryRare + "x " + dropOverkillTypeSecondaryRare + '\n' : "");

        String stealItem = "Steal Item: Chance=" + stealChance + "/255\n" +
                (stealChance > 0 ? " COMMON " + stealItemQuantityCommon + "x " + stealItemTypeCommon + '\n' + "   RARE " + stealItemQuantityRare + "x " + stealItemTypeRare + '\n' : "");

        String spoilsString = mainSpoils + '\n' + primaryItem + '\n' + secondaryItem + '\n' + stealItem + '\n';
        return statsString + '\n' + spoilsString;
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
