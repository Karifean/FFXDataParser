package model;

import main.DataAccess;
import atel.model.StackObject;
import main.StringHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * weapon.bin
 * buki_get.bin
 * shop_arms.bin
 */
public class GearDataObject {
    private final int[] bytes;

    boolean isBukiGet;
    int byte00nameIdMaybe;
    int byte01nameIdMaybe;
    int byte02existsMaybe;
    int byte03AlwaysZero;
    int modelIdx;
    int variousFlags;
    int character;
    int armorByte;
    int byte06equipperMaybe;
    int byte07equipperMaybe;
    int formula;
    int power;
    int crit;
    int slots;
    int ability1;
    int ability2;
    int ability3;
    int ability4;

    boolean armor;
    boolean flag1;
    boolean hiddenInMenu;
    boolean celestial;
    boolean brotherhood;
    boolean unknownFlagSet;

    public GearDataObject(int[] bytes) {
        this(bytes, null, null);
    }

    public GearDataObject(int[] bytes, int[] stringBytes, String localization) {
        this.bytes = bytes;
        isBukiGet = (bytes.length == 16);
        if (isBukiGet) {
            mapBytesBukiGet();
        } else {
            mapBytesNormal();
        }
        mapFlags();
    }

    private void mapBytesNormal() {
        byte00nameIdMaybe = bytes[0x00];
        byte01nameIdMaybe = bytes[0x01];
        byte02existsMaybe = bytes[0x02];
        variousFlags = bytes[0x03];
        character = bytes[0x04];
        armorByte = bytes[0x05];
        byte06equipperMaybe = bytes[0x06];
        byte07equipperMaybe = bytes[0x07];
        formula = bytes[0x08];
        power = bytes[0x09];
        crit = bytes[0x0A];
        slots = bytes[0x0B];
        modelIdx = read2Bytes(0x0C);
        ability1 = read2Bytes(0x0E);
        ability2 = read2Bytes(0x10);
        ability3 = read2Bytes(0x12);
        ability4 = read2Bytes(0x14);
    }

    private void mapBytesBukiGet() {
        variousFlags = bytes[0x00];
        character = bytes[0x01];
        armorByte = bytes[0x02];
        byte03AlwaysZero = bytes[0x03];
        formula = bytes[0x04];
        power = bytes[0x05];
        crit = bytes[0x06];
        slots = bytes[0x07];
        ability1 = read2Bytes(0x08);
        ability2 = read2Bytes(0x0A);
        ability3 = read2Bytes(0x0C);
        ability4 = read2Bytes(0x0E);
    }

    private void mapFlags() {
        armor = armorByte != 0;
        flag1 = (variousFlags & 0x01) > 0;
        hiddenInMenu = (variousFlags & 0x02) > 0;
        celestial = (variousFlags & 0x04) > 0;
        brotherhood = (variousFlags & 0x08) > 0;
        unknownFlagSet = variousFlags >= 0x10;
    }

    public String compactString() {
        return "{ " + StackObject.enumToString("playerChar", character) +
                ", " + (armor ? "Armor" : "Weapon") +
                (formula != 1 ? " Formula=" + StackObject.enumToString("damageFormula", formula) : "") +
                " " + getAbilityString() + " }";
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        list.add(StackObject.enumToString("playerChar", character));
        list.add((armor ? "Armor" : "Weapon") + StringHelper.hex2Suffix(armorByte));
        list.add("Formula=" + StackObject.enumToString("damageFormula", formula));
        list.add("Power=" + power);
        list.add("Crit=" + crit + "%");
        list.add("Slots=" + slots + " " + getAbilityString());
        list.add(flag1 ? "Flag1" : "");
        list.add(hiddenInMenu ? "Hidden in Menu" : "");
        list.add(celestial ? "Celestial" : "");
        list.add(brotherhood ? "Brotherhood" : "");
        if (unknownFlagSet) {
            list.add("UnknownFlags=" + StringHelper.formatHex2(variousFlags));
        }
        list.add(modelIdx != 0 ? "model=" + StackObject.enumToString("model", modelIdx) : "");
        if (isBukiGet) {
            list.add(byte03AlwaysZero != 0 ? "byte03=" + StringHelper.hex2WithSuffix(byte03AlwaysZero) : "");
        } else {
            list.add(byte00nameIdMaybe   != 0 ? "byte00=" + StringHelper.hex2WithSuffix(byte00nameIdMaybe) : "");
            list.add(byte01nameIdMaybe   != 0 ? "byte01=" + StringHelper.hex2WithSuffix(byte01nameIdMaybe) : "");
            list.add(byte02existsMaybe   != 0 ? "byte02=" + StringHelper.hex2WithSuffix(byte02existsMaybe) : "");
            list.add(byte06equipperMaybe != 0 ? "byte06=" + StringHelper.hex2WithSuffix(byte06equipperMaybe) : "");
            list.add(byte07equipperMaybe != 0 ? "byte07=" + StringHelper.hex2WithSuffix(byte07equipperMaybe) : "");
        }
        return "{ " + list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining(", ")) + " }";
    }

    private String getAbilityString() {
        String abilityString = "{";
        String ability1Str = getGearAbilityLabel(ability1);
        String ability2Str = getGearAbilityLabel(ability2);
        String ability3Str = getGearAbilityLabel(ability3);
        String ability4Str = getGearAbilityLabel(ability4);
        if (slots >= 1) {
            abilityString += ability1Str;
            if (slots >= 2 || !ability2Str.equals("Empty")) {
                abilityString += ", " + (slots < 2 ? "!" : "") + ability2Str;
                if (slots >= 3 || !ability3Str.equals("Empty")) {
                    abilityString += ", " + (slots < 3 ? "!" : "") + ability3Str;
                    if (slots >= 4 || !ability4Str.equals("Empty")) {
                        abilityString += ", " + (slots < 4 ? "!" : "") + ability4Str;
                    }
                }
            }
        }
        abilityString += "}";
        return abilityString;
    }

    private static String getGearAbilityLabel(int idx) {
        if (idx == 0xFF) {
            return "Empty";
        } else {
            AutoAbilityDataObject obj = DataAccess.getAutoAbility(idx);
            if (obj == null) {
                return "null";
            } else {
                return obj.getName() + StringHelper.hex4Suffix(idx);
            }
        }
    }

    private int read2Bytes(int offset) {
        return bytes[offset] + bytes[offset+1] * 0x100;
    }
}
