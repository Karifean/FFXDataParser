package model;

import atel.model.StackObject;
import main.DataAccess;
import main.StringHelper;
import model.strings.KeyedString;
import model.strings.LocalizedKeyedStringObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static reading.ChunkedFileHelper.*;

/**
 * ply_save.bin
 */
public class PlayerCharStatDataObject implements Nameable, Writable, Localized<PlayerCharStatDataObject> {
    public static final int LENGTH = 0x9C;

    private final int[] bytes;

    public LocalizedKeyedStringObject name = new LocalizedKeyedStringObject();
    private int nameOffset;
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
    int slvUsed;

    int unknownByte3D;
    int abilityField3E;
    int abilityField42;
    int abilityField46;

    int encounterCount;
    int killCount;

    List<Integer> commandIds;

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
        slvUsed = bytes[0x3C];

        unknownByte3D = bytes[0x3D];
        abilityField3E = read4Bytes(bytes, 0x3E);
        abilityField42 = read4Bytes(bytes, 0x42);
        abilityField46 = read4Bytes(bytes, 0x46);

        encounterCount = read4Bytes(bytes, 0x50);
        killCount = read4Bytes(bytes, 0x54);
    }

    private void mapFlags() {
        commandIds = new ArrayList<>();
        commandIds.addAll(StackObject.bitfieldToIntList(abilityField3E).stream().map(i -> i + 0x3000).toList());
        commandIds.addAll(StackObject.bitfieldToIntList(abilityField42).stream().map(i -> i + 0x3020).toList());
        commandIds.addAll(StackObject.bitfieldToIntList(abilityField46).stream().map(i -> i + 0x3040).toList());
    }

    private void mapStrings(int[] stringBytes, String localization) {
        if (stringBytes == null || stringBytes.length == 0) {
            return;
        }
        name.readAndSetLocalizedContent(localization, stringBytes, nameOffset, nameKey);
    }

    public void setLocalizations(PlayerCharStatDataObject localizationObject) {
        localizationObject.name.copyInto(name);
    }

    @Override
    public LocalizedKeyedStringObject getKeyedString(String title) {
        return switch (title) {
            case "name" -> name;
            default -> null;
        };
    }

    @Override
    public int[] toBytes(String localization) {
        int[] array = new int[PlayerCharStatDataObject.LENGTH];
        write4Bytes(array, 0x00, name.getLocalizedContent(localization).toHeaderBytes());
        write4Bytes(array, 0x04, baseHp);
        write4Bytes(array, 0x08, baseMp);

        array[0x0C] = baseStr;
        array[0x0D] = baseDef;
        array[0x0E] = baseMag;
        array[0x0F] = baseMdf;
        array[0x10] = baseAgi;
        array[0x11] = baseLck;
        array[0x12] = baseEva;
        array[0x13] = baseAcc;

        write4Bytes(array, 0x18, currentAp);
        write4Bytes(array, 0x1C, currentHp);
        write4Bytes(array, 0x20, currentMp);
        write4Bytes(array, 0x24, maxHp);
        write4Bytes(array, 0x28, maxMp);

        write2Bytes(array, 0x14, unknownBytes1415);
        write2Bytes(array, 0x16, unknownBytes1617);

        array[0x2C] = miscFlags;
        array[0x2D] = equippedWeaponIndex;
        array[0x2E] = equippedArmorIndex;
        array[0x2F] = str;
        array[0x30] = def;
        array[0x31] = mag;
        array[0x32] = mdf;
        array[0x33] = agi;
        array[0x34] = lck;
        array[0x35] = eva;
        array[0x36] = acc;

        array[0x37] = poisonDamage;
        array[0x38] = overdriveMode;
        array[0x39] = overdriveCurrent;
        array[0x3A] = overdriveMax;
        array[0x3B] = slvAvailable;
        array[0x3C] = slvUsed;

        array[0x3D] = unknownByte3D;
        write4Bytes(array, 0x3E, abilityField3E);
        write4Bytes(array, 0x42, abilityField42);
        write4Bytes(array, 0x46, abilityField46);

        write4Bytes(array, 0x50, encounterCount);
        write4Bytes(array, 0x54, killCount);
        // TODO
        return array;
    }

    @Override
    public Stream<KeyedString> streamKeyedStrings(String localization) {
        return Stream.of(name.getLocalizedContent(localization));
    }

    public String buildStrings() {
        List<String> list = new ArrayList<>();
        list.add("Name at Offset " + StringHelper.formatHex4(nameOffset) + ":");
        list.add(name.getDefaultString());
        // TODO
        String full = list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining("\n"));
        return full;
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        list.add(name.getDefaultString());
        list.add("--- Base ---");
        list.add("HP=" + baseHp + " MP=" + baseMp);
        list.add("STR=" + baseStr + " DEF=" + baseDef + " MAG=" + baseMag + " MDF=" + baseMdf);
        list.add("AGI=" + baseAgi + " LCK=" + baseLck + " EVA=" + baseEva + " ACC=" + baseAcc);
        list.add("--- Current ---");
        list.add("HP=" + hp + " MP=" + mp);
        list.add("STR=" + str + " DEF=" + def + " MAG=" + mag + " MDF=" + mdf);
        list.add("AGI=" + agi + " LCK=" + lck + " EVA=" + eva + " ACC=" + acc);
        list.add("Poison Damage=" + poisonDamage + "%");
        list.add("Abilities=[" + commandIds.stream().map(a -> asMove(a)).collect(Collectors.joining(", ")) + "]");

        String full = list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining("\n"));
        return full;
    }

    private static String asMove(int move) {
        return DataAccess.getCommand(move).getName() + StringHelper.hex4Suffix(move);
    }

    @Override
    public String getName(String localization) {
        return name.getLocalizedString(localization);
    }
}
