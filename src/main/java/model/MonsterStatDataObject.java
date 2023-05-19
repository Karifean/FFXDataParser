package model;

import script.model.StackObject;

public class MonsterStatDataObject {

    private final int[] bytes;

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

    public MonsterStatDataObject(int[] bytes) {
        this.bytes = bytes;
        mapStatBytes();
    }

    private void mapStatBytes() {
        hp = read4Bytes(bytes, 0x14);
        mp = read4Bytes(bytes, 0x18);
        overkillThreshold = read4Bytes(bytes, 0x1C);
        str = bytes[0x20];
        def = bytes[0x21];
        mag = bytes[0x22];
        mdf = bytes[0x23];
        agi = bytes[0x24];
        lck = bytes[0x25];
        eva = bytes[0x26];
        acc = bytes[0x27];
        miscProperties28 = bytes[0x28];
        miscProperties29 = bytes[0x29];
        poisonDamage = bytes[0x2A];
        elementalAbsorbFlags = bytes[0x2B];
        elementalNullFlags = bytes[0x2C];
        elementalResistFlags = bytes[0x2D];
        elementalWeakFlags = bytes[0x2E];
        statusResistanceDeath = bytes[0x2F];
        statusResistanceZombie = bytes[0x30];
        statusResistancePetrify = bytes[0x31];
        statusResistancePoison = bytes[0x32];
        statusResistancePowerBreak = bytes[0x33];
        statusResistanceMagicBreak = bytes[0x34];
        statusResistanceArmorBreak = bytes[0x35];
        statusResistanceMentalBreak = bytes[0x36];
        statusResistanceConfuse = bytes[0x37];
        statusResistanceBerserk = bytes[0x38];
        statusResistanceProvoke = bytes[0x39];
        statusChanceThreaten = bytes[0x3A];
        statusResistanceSleep = bytes[0x3B];
        statusResistanceSilence = bytes[0x3C];
        statusResistanceDarkness = bytes[0x3D];
        statusResistanceShell = bytes[0x3E];
        statusResistanceProtect = bytes[0x3F];
        statusResistanceReflect = bytes[0x40];
        statusResistanceNTide = bytes[0x41];
        statusResistanceNBlaze = bytes[0x42];
        statusResistanceNShock = bytes[0x43];
        statusResistanceNFrost = bytes[0x44];
        statusResistanceRegen = bytes[0x45];
        statusResistanceHaste = bytes[0x46];
        statusResistanceSlow = bytes[0x47];
        autoStatuses1 = bytes[0x48];
        autoStatuses2 = bytes[0x49];
        autoStatuses3 = bytes[0x4A];
        autoStatuses4 = bytes[0x4B];

        extraStatusImmunities1 = bytes[0x4E];
        extraStatusImmunities2 = bytes[0x4F];

        forcedAction = read2Bytes(bytes, 0x70);

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

    @Override
    public String toString() {
        String bigStats = "HP=" + hp + " MP=" + mp + " Overkill=" + overkillThreshold;
        String mainStats = "STR=" + str + " DEF=" + def + " MAG=" + mag + " MDF=" + mdf + "\nAGI=" + agi + " LCK=" + lck + " EVA=" + eva + " ACC=" + acc;

        return bigStats + '\n' + mainStats + '\n';
    }

    private static int read2Bytes(int[] bytes, int offset) {
        return bytes[offset] + bytes[offset+1] * 0x100;
    }

    private static int read4Bytes(int[] bytes, int offset) {
        return bytes[offset] + bytes[offset+1] * 0x100 + bytes[offset+2] * 0x10000 + bytes[offset+3] * 0x1000000;
    }

    private static String asMove(int move) {
        return new StackObject("move", false, null, move).toString();
    }

}
