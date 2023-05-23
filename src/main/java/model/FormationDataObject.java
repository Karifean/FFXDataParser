package model;

import main.DataAccess;
import script.MonsterFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FormationDataObject {
    public static final int LENGTH = 0x1C;

    private final int[] bytes;

    int unknownByte00;
    int unknownByte01;
    int unknownByte02;
    int inWaterByte;
    int alwaysZero04;
    int alwaysZero05;
    int alwaysZero06;
    int alwaysZero07;
    int alwaysZero08;
    int alwaysZero09;
    int alwaysZero0A;
    int alwaysZero0B;
    int[] monsters = new int[8];

    boolean byte01bit02;
    boolean byte02bit01;
    boolean byte02bit02;
    boolean byte02bit04;
    boolean byte02bit08;
    boolean byte02bit10;
    boolean isRandomEncounter;
    boolean inWater;

    public FormationDataObject(int[] bytes) {
        this.bytes = bytes;
        mapBytes();
        mapFlags();
    }

    private void mapBytes() {
        unknownByte00 = bytes[0x00];
        unknownByte01 = bytes[0x01];
        unknownByte02 = bytes[0x02];
        inWaterByte = bytes[0x03];
        alwaysZero04 = bytes[0x04];
        alwaysZero05 = bytes[0x05];
        alwaysZero06 = bytes[0x06];
        alwaysZero07 = bytes[0x07];
        alwaysZero08 = bytes[0x08];
        alwaysZero09 = bytes[0x09];
        alwaysZero0A = bytes[0x0A];
        alwaysZero0B = bytes[0x0B];
        monsters[0] = read2Bytes(0x0C);
        monsters[1] = read2Bytes(0x0E);
        monsters[2] = read2Bytes(0x10);
        monsters[3] = read2Bytes(0x12);
        monsters[4] = read2Bytes(0x14);
        monsters[5] = read2Bytes(0x16);
        monsters[6] = read2Bytes(0x18);
        monsters[7] = read2Bytes(0x1A);
    }

    private void mapFlags() {
        isRandomEncounter = unknownByte00 > 0;
        byte01bit02 = (unknownByte01 & 0x02) > 0;
        byte02bit01 = (unknownByte02 & 0x01) > 0;
        byte02bit02 = (unknownByte02 & 0x02) > 0;
        byte02bit04 = (unknownByte02 & 0x04) > 0;
        byte02bit08 = (unknownByte02 & 0x08) > 0;
        byte02bit10 = (unknownByte02 & 0x10) > 0;
        inWater = inWaterByte > 0;
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        list.add(ifG0(alwaysZero04, "byte04="));
        list.add(ifG0(alwaysZero05, "byte05="));
        list.add(ifG0(alwaysZero06, "byte06="));
        list.add(ifG0(alwaysZero07, "byte07="));
        list.add(ifG0(alwaysZero08, "byte08="));
        list.add(ifG0(alwaysZero09, "byte09="));
        list.add(ifG0(alwaysZero0A, "byte0A="));
        list.add(ifG0(alwaysZero0B, "byte0B="));
        for (int i = 0; i < 8; i++) {
            int monsterIndex = monsters[i];
            if (monsterIndex != 0xFFFF) {
                list.add("Monster #" + (i+1) + ": " + writeMonster(monsterIndex));
            }
        }
        /* list.add(isRandomEncounter ? "Byte00 YES" : "Byte00 NO");
        if (unknownByte00 > 1) {
            list.add("randomEncounterByte greater than 1!? = " + unknownByte00);
        }
        list.add(byte01bit02 ? "Byte01Bit02 YES" : "Byte01Bit02 NO");
        if (unknownByte01 != 0x00 && unknownByte01 != 0x02) {
            list.add("byte01 not 0 or 2!? = " + unknownByte01);
        }
        list.add(byte02bit01 ? "Byte02Bit01 YES" : "Byte02Bit01 NO");
        list.add(byte02bit02 ? "Byte02Bit02 YES" : "Byte02Bit02 NO");
        list.add(byte02bit04 ? "Byte02Bit04 YES" : "Byte02Bit04 NO");
        list.add(byte02bit08 ? "Byte02Bit08 YES" : "Byte02Bit08 NO");
        list.add(byte02bit10 ? "Byte02Bit10 YES" : "Byte02Bit10 NO");
        if (unknownByte02 > 0x10) {
            list.add("byte02 greater than 10!? = " + unknownByte02);
        }
        list.add(inWater ? "Underwater Battle" : null);
        if (inWaterByte > 1) {
            list.add("inWaterByte greater than 1!? = " + inWaterByte);
        } */
        String full = list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining("\n"));
        return full;
    }

    private static String ifG0(int value, String prefix) {
        if (value > 0) {
            return prefix + formatUnknownByte(value);
        } else {
            return null;
        }
    }

    private static String formatUnknownByte(int bt) {
        return String.format("%02X", bt) + '=' + String.format("%03d", bt) + '(' + String.format("%8s", Integer.toBinaryString(bt)).replace(' ', '0') + ')';
    }

    private static String writeMonster(int monsterIndex) {
        MonsterFile monster = DataAccess.getMonster(monsterIndex);
        String monsterName = monster == null ? "null" : monster.getName();
        String hexSuffix = " [" + String.format("%04X", monsterIndex) + "h]";
        return monsterName + hexSuffix;
    }

    private int read2Bytes(int offset) {
        return bytes[offset] + bytes[offset+1] * 0x100;
    }
}
