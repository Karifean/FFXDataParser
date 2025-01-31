package model;

import main.DataAccess;
import main.StringHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ply_save.bin
 */
public class PlayerCharStatDataObject implements Nameable {
    public static final int LENGTH = 0x9C;

    private final int[] bytes;

    public LocalizedStringObject characterName = new LocalizedStringObject();
    int nameOffset;
    private int nameKey;

    int baseHp;
    int baseMp;
    int baseStr;
    int baseDef;
    int baseMag;
    int baseMdf;
    int baseAgi;
    int baseLck;
    int baseEva;
    int baseAcc;
    int currentAp;
    int currentHp;
    int currentMp;
    int maxHp;
    int maxMp;
    int unknownBytes1415;
    int unknownBytes1617;
    int miscFlags;
    int equippedWeaponIndex;
    int equippedArmorIndex;
    int hp;
    int mp;
    int str;
    int def;
    int mag;
    int mdf;
    int agi;
    int lck;
    int eva;
    int acc;
    int poisonDamage;
    int overdriveMode;
    int overdriveCurrent;
    int overdriveMax;
    int slvAvailable;
    int slvTotal;

    int unknownByte3D;
    int abilityByte3E;
    int abilityByte3F;
    int abilityByte40;
    int abilityByte41;
    int abilityByte42;
    int abilityByte43;
    int abilityByte44;
    int abilityByte45;
    int abilityByte46;
    int abilityByte47;
    int abilityByte48;
    int abilityByte49;

    int encounterCount;
    int killCount;

    public PlayerCharStatDataObject(int[] bytes, int[] stringBytes, String localization) {
        this.bytes = bytes;
        mapBytes();
        mapFlags();
        mapStrings(stringBytes, localization);
    }

    private void mapBytes() {
        nameOffset = read2Bytes(bytes, 0x00);
        nameKey = read2Bytes(bytes, 0x02);
        baseHp = read4Bytes(bytes, 0x04);
        baseMp = read4Bytes(bytes, 0x08);
        baseStr = bytes[0x0C];
        baseDef = bytes[0x0D];
        baseMag = bytes[0x0E];
        baseMdf = bytes[0x0F];
        baseAgi = bytes[0x10];
        baseLck = bytes[0x11];
        baseEva = bytes[0x12];
        baseAcc = bytes[0x13];

        currentAp = read4Bytes(bytes, 0x18);
        currentHp = read4Bytes(bytes, 0x1C);
        currentMp = read4Bytes(bytes, 0x20);
        maxHp = read4Bytes(bytes, 0x24);
        maxMp = read4Bytes(bytes, 0x28);

        unknownBytes1415 = read2Bytes(bytes, 0x14);
        unknownBytes1617 = read2Bytes(bytes, 0x16);

        miscFlags = bytes[0x2C];
        equippedWeaponIndex = bytes[0x2D];
        equippedArmorIndex = bytes[0x2E];
        str = bytes[0x2F];
        def = bytes[0x30];
        mag = bytes[0x31];
        mdf = bytes[0x32];
        agi = bytes[0x33];
        lck = bytes[0x34];
        eva = bytes[0x35];
        acc = bytes[0x36];

        poisonDamage = bytes[0x37];
        overdriveMode = bytes[0x38];
        overdriveCurrent = bytes[0x39];
        overdriveMax = bytes[0x3A];
        slvAvailable = bytes[0x3B];
        slvTotal = bytes[0x3C];

        unknownByte3D = bytes[0x3D];
        abilityByte3E = bytes[0x3E];
        abilityByte3F = bytes[0x3F];
        abilityByte40 = bytes[0x40];
        abilityByte41 = bytes[0x41];
        abilityByte42 = bytes[0x42];
        abilityByte43 = bytes[0x43];
        abilityByte44 = bytes[0x44];
        abilityByte45 = bytes[0x45];
        abilityByte46 = bytes[0x46];
        abilityByte47 = bytes[0x47];
        abilityByte48 = bytes[0x48];
        abilityByte49 = bytes[0x49];

        encounterCount = read4Bytes(bytes, 0x50);
        killCount = read4Bytes(bytes, 0x54);
    }

    private void mapFlags() {
        // TODO
    }

    private void mapStrings(int[] stringBytes, String localization) {
        if (stringBytes == null || stringBytes.length == 0) {
            return;
        }
        characterName.setLocalizedContent(localization, StringHelper.getStringAtLookupOffset(stringBytes, nameOffset));
    }

    public void setLocalizations(PlayerCharStatDataObject localizationObject) {
        localizationObject.characterName.copyInto(characterName);
    }

    public String getStrings() {
        List<String> list = new ArrayList<>();
        list.add("Name at Offset " + String.format("%04X", nameOffset) + ":");
        list.add(characterName.getAllContent());
        // TODO
        String full = list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining("\n"));
        return full;
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        list.add("HP=" + hp + " MP=" + mp);
        list.add("STR=" + str + " DEF=" + def + " MAG=" + mag + " MDF=" + mdf);
        list.add("AGI=" + agi + " LCK=" + lck + " EVA=" + eva + " ACC=" + acc);
        list.add("Poison Damage=" + poisonDamage + "%");

        String full = list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining("\n"));
        return full;
    }

    private static int read2Bytes(int[] bytes, int offset) {
        return bytes[offset] + bytes[offset+1] * 0x100;
    }

    private static int read4Bytes(int[] bytes, int offset) {
        return bytes[offset] + bytes[offset+1] * 0x100 + bytes[offset+2] * 0x10000 + bytes[offset+3] * 0x1000000;
    }

    private static String asMove(int move) {
        return DataAccess.getMove(move).getName() + " [" + String.format("%04X", move) + "h]";
    }

    @Override
    public String getName(String localization) {
        return characterName.getLocalizedContent(localization);
    }
}
