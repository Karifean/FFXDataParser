package model;

import main.DataAccess;
import script.model.ScriptConstants;

import java.util.*;
import java.util.stream.Collectors;

public class MonsterSpoilsDataObject {
    public static final int LENGTH = 0x11C;

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
    int bribeItemQuantity;
    int gearSlotCountByte;
    int gearDamageFormula;
    int gearCritBonus;
    int gearAttackPower;
    int gearAbilityCountByte;
    int[][] gearAbilitiesOnWeaponsByChar = new int[7][8];
    int[][] gearAbilitiesOnArmorsByChar = new int[7][8];
    int zanmatoLevelByte;

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
        bribeItemQuantity = bytes[0x2C];
        gearSlotCountByte = bytes[0x2D];
        gearDamageFormula = bytes[0x2E];
        gearCritBonus = bytes[0x2F];
        gearAttackPower = bytes[0x30];
        gearAbilityCountByte = bytes[0x31];
        for (int chr = 0; chr < 7; chr++) {
            int baseOffset = 0x32 + chr * 0x20;
            for (int i = 0; i < 8; i++) {
                gearAbilitiesOnWeaponsByChar[chr][i] = read2Bytes(bytes, baseOffset + i * 2);
            }
            for (int i = 0; i < 8; i++) {
                gearAbilitiesOnArmorsByChar[chr][i] = read2Bytes(bytes, baseOffset + 0x10 + i * 2);
            }
        }
        zanmatoLevelByte = bytes[0x112];
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        list.add("Gil=" + gil);
        list.add("AP Normal=" + apNormal + " Overkill=" + apOverkill);
        if (hasRonsoRage) {
            list.add("Ronso Rage=" + asMove(ronsoRage));
        } else {
            list.add("No Ronso Rage");
        }
        list.add("Primary Item Drop: Chance=" + dropChancePrimary + "/255");
        if (dropChancePrimary > 0) {
            String primaryItemDetails = " COMMON Normal " + dropNormalQuantityPrimaryCommon + "x " + asMove(dropNormalTypePrimaryCommon) + "; Overkill " + dropOverkillQuantityPrimaryCommon + "x " + asMove(dropOverkillTypePrimaryCommon) + '\n' +
                    "   RARE Normal " + dropNormalQuantityPrimaryRare + "x " + asMove(dropNormalTypePrimaryRare) + "; Overkill " + dropOverkillQuantityPrimaryRare + "x " + asMove(dropOverkillTypePrimaryRare);
            list.add(primaryItemDetails);
        }
        list.add("Secondary Item Drop: Chance=" + dropChanceSecondary + "/255");
        if (dropChanceSecondary > 0) {
            String secondaryItemDetails = " COMMON Normal " + dropNormalQuantitySecondaryCommon + "x " + asMove(dropNormalTypeSecondaryCommon) + "; Overkill " + dropOverkillQuantitySecondaryCommon + "x " + asMove(dropOverkillTypeSecondaryCommon) + '\n' +
                    "   RARE Normal " + dropNormalQuantitySecondaryRare + "x " + asMove(dropNormalTypeSecondaryRare) + "; Overkill " + dropOverkillQuantitySecondaryRare + "x " + asMove(dropOverkillTypeSecondaryRare);
            list.add(secondaryItemDetails);
        }
        list.add("Steal Item: Chance=" + stealChance + "/255");
        if (stealChance > 0) {
            String stealDetail = " COMMON " + stealItemQuantityCommon + "x " + asMove(stealItemTypeCommon) + '\n' + "   RARE " + stealItemQuantityRare + "x " + asMove(stealItemTypeRare);
            list.add(stealDetail);
        }
        if (bribeItem != 0x0000) {
            list.add("Bribe Baseline: " + bribeItemQuantity + "x " + asMove(bribeItem));
        } else {
            list.add("No Bribe");
        }
        list.add("Gear Drop: Chance=" + dropChanceGear + "/255");
        if (dropChanceGear > 0) {
            String gearGeneral = " Formula=" + gearDamageFormula +
                    ", Power=" + gearAttackPower +
                    ", Crit=" + gearCritBonus +
                    "%, Slots=" + getRandomCountString(gearSlotCountByte, false) +
                    ", Ability Rolls=" + getRandomCountString(gearAbilityCountByte, true);
            list.add(gearGeneral);
            if (gearAbilityCountByte > 0) {
                list.add(gearAbilityString());
            }
        }
        list.add("Zanmato Level: " + (zanmatoLevelByte + 1));
        String full = list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining("\n"));
        return full;
    }

    private static String getRandomCountString(int countByte, boolean isAbilityRoll) {
        double multiplier = isAbilityRoll ? 0.125 : 0.25;
        double min = (countByte - 4) * multiplier;
        double max = (countByte + 3) * multiplier;
        if (min >= 4) {
            return "4";
        }
        if (max < 2) {
            if (!isAbilityRoll) {
                return "1";
            } else if (max < 1) {
                return "0";
            }
        }
        if ((int) min == (int) max) {
            return String.format("%s", (int) min);
        }
        return String.format("%s-%s", min, max);
    }

    private String gearAbilityString() {
        Map<String, List<Integer>> weaponAbilitiesMap = new HashMap<>();
        Map<String, List<Integer>> armorAbilitiesMap = new HashMap<>();
        for (int i = 0; i < 7; i++) {
            String weapon = singleGearAbilityString(gearAbilitiesOnWeaponsByChar[i]);
            weaponAbilitiesMap.computeIfAbsent(weapon, s -> new ArrayList<>()).add(i);
            String armor = singleGearAbilityString(gearAbilitiesOnArmorsByChar[i]);
            armorAbilitiesMap.computeIfAbsent(armor, s -> new ArrayList<>()).add(i);
        }
        String weaponStr = " Weapon " + abilityMapToString(weaponAbilitiesMap);
        String armorStr = " Armor  " + abilityMapToString(armorAbilitiesMap);
        return weaponStr + '\n' + armorStr;
    }

    private static String getCharIndicator(int chr) {
        return ScriptConstants.getEnumMap("actor").get(chr).name.substring(0, 1);
    }

    private static String abilityMapToString(Map<String, List<Integer>> map) {
        return map.entrySet().stream().map(e -> e.getValue().stream().map(c -> getCharIndicator(c)).collect(Collectors.joining()) + ": " + e.getKey()).collect(Collectors.joining("\n        "));
    }

    private String singleGearAbilityString(int[] abilityArray) {
        int guaranteedAbility = abilityArray[0];
        StringBuilder abilityStr = new StringBuilder();
        if (guaranteedAbility > 0) {
            abilityStr.append(asGearAbility(guaranteedAbility)).append(" (Forced), ");
        }
        Map<Integer, Integer> randomAbilities = new HashMap<>();
        for (int i = 1; i < 8; i++) {
            int abi = abilityArray[i];
            randomAbilities.put(abi, (randomAbilities.getOrDefault(abi, 0)) + 1);
        }
        String randomAbilityStr = randomAbilities.entrySet().stream().map(e -> asGearAbility(e.getKey()) + " (" + e.getValue() + "/7)").collect(Collectors.joining(", "));
        abilityStr.append(randomAbilityStr);
        return abilityStr.toString();
    }

    private static int read2Bytes(int[] bytes, int offset) {
        return bytes[offset] + bytes[offset+1] * 0x100;
    }

    private static String asGearAbility(int idx) {
        GearAbilityDataObject abil = DataAccess.getGearAbility(idx);
        return (abil != null ? abil.getName() : "null") + " [" + String.format("%04X", idx) + "h]";
    }

    private static String asMove(int idx) {
        AbilityDataObject move = DataAccess.getMove(idx);
        return (move != null ? move.name : "null") + " [" + String.format("%04X", idx) + "h]";
    }

    private static String formatUnknownByte(int bt) {
        return String.format("%02X", bt) + '=' + String.format("%03d", bt) + '(' + String.format("%8s", Integer.toBinaryString(bt)).replace(' ', '0') + ')';
    }

}
