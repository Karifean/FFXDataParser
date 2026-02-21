package model;

import atel.model.StackObject;
import main.StringHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Part of BattleFile
 */
public class BattleAreasPositionsDataObject {
    int areaCount;
    BattleArea[] areas;

    public BattleAreasPositionsDataObject(int[] bytes) {
        mapBytes(bytes);
    }

    private void mapBytes(int[] bytes) {
        BattleArea first = new BattleArea(bytes, 0);
        areaCount = first.header.areaCount;
        areas = new BattleArea[areaCount];
        areas[0] = first;
        for (int i = 1; i < areaCount; i++) {
            areas[i] = new BattleArea(bytes, i * BattleAreaHeader.LENGTH);
        }
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        list.add("Number of Areas: " + areaCount);
        for (int i = 0; i < areaCount; i++) {
            list.add("--- Area " + StringHelper.formatHex2(i) + " ---");
            list.add(areas[i].toString());
        }
        String full = list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining("\n"));
        return full;
    }

    private static class BattleArea {
        private final int[] bytes;

        BattleAreaHeader header;
        BattlePosition[] partyPositions;
        BattlePosition[] partySwitchPositions;
        BattlePosition[] aeonPositions;
        BattlePosition[] aeonSwitchPositions;
        BattlePosition[] monsterPositions;
        BattlePosition[] monsterSwitchPositions;
        BattleAreaUnknownSubstruct[] unknownTargetStructs;
        BattlePosition finalPosition;

        public BattleArea(int[] bytes, int headerOffset) {
            this.bytes = bytes;
            header = new BattleAreaHeader(bytes, headerOffset);
            readPositions();
        }

        private void readPositions() {
            partyPositions = new BattlePosition[header.partyPositionCount];
            partySwitchPositions = new BattlePosition[header.partyPositionCount];
            for (int i = 0; i < header.partyPositionCount; i++) {
                partyPositions[i] = new BattlePosition(bytes, header.offsetPartyPositions + i * BattlePosition.LENGTH);
                partySwitchPositions[i] = new BattlePosition(bytes, header.offsetPartySwitchPositions + i * BattlePosition.LENGTH);
            }
            aeonPositions = new BattlePosition[header.aeonPositionCount];
            aeonSwitchPositions = new BattlePosition[header.aeonPositionCount];
            for (int i = 0; i < header.aeonPositionCount; i++) {
                aeonPositions[i] = new BattlePosition(bytes, header.offsetAeonPositions + i * BattlePosition.LENGTH);
                aeonSwitchPositions[i] = new BattlePosition(bytes, header.offsetAeonSwitchPositions + i * BattlePosition.LENGTH);
            }
            monsterPositions = new BattlePosition[header.monsterPositionCount];
            monsterSwitchPositions = new BattlePosition[header.monsterPositionCount];
            for (int i = 0; i < header.monsterPositionCount; i++) {
                monsterPositions[i] = new BattlePosition(bytes, header.offsetMonsterPositions + i * BattlePosition.LENGTH);
                monsterSwitchPositions[i] = new BattlePosition(bytes, header.offsetMonsterSwitchPositions + i * BattlePosition.LENGTH);
            }
            unknownTargetStructs = new BattleAreaUnknownSubstruct[header.unknownSubstructCount];
            for (int i = 0; i < header.unknownSubstructCount; i++) {
                unknownTargetStructs[i] = new BattleAreaUnknownSubstruct(bytes, header.offsetUnknownSubstructs + i * BattleAreaUnknownSubstruct.LENGTH);
            }
            finalPosition = new BattlePosition(bytes, header.offsetFinalLocation);
        }

        @Override
        public String toString() {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < header.partyPositionCount; i++) {
                list.add("Party Position " + StringHelper.formatHex2(i) + " " + partyPositions[i] + " Switch " + partySwitchPositions[i]);
            }
            for (int i = 0; i < header.aeonPositionCount; i++) {
                list.add("Aeon Position " + StringHelper.formatHex2(i) + " (" + StackObject.enumToScriptField("playerChar", i + 8).getLabel() + ") " + aeonPositions[i] + " Switch " + aeonSwitchPositions[i]);
            }
            for (int i = 0; i < header.monsterPositionCount; i++) {
                list.add("Monster Position " + StringHelper.formatHex2(i) + " " + monsterPositions[i] + " Switch " + monsterSwitchPositions[i]);
            }
            for (int i = 0; i < unknownTargetStructs.length; i++) {
                list.add("Extra Struct " + StringHelper.formatHex2(i) + " " + unknownTargetStructs[i]);
            }
            list.add("Final Position " + finalPosition);
            if (header.extraPosition1.notNothing()) {
                list.add("Extra Position 1 " + header.extraPosition1);
            }
            if (header.extraPosition2.notNothing()) {
                list.add("Extra Position 2 " + header.extraPosition2);
            }
            if (header.extraPosition3.notNothing()) {
                list.add("Extra Position 3 " + header.extraPosition3);
            }
            if (header.byte00Always00 != 0x00) {
                list.add("headerbyte00=" + StringHelper.formatHex2(header.byte00Always00));
            }
            if (header.byte02Always01 != 0x01) {
                list.add("headerbyte02=" + StringHelper.formatHex2(header.byte02Always01));
            }
            if (header.byte03Always04 != 0x04) {
                list.add("headerbyte03=" + StringHelper.formatHex2(header.byte03Always04));
            }
            if (header.byte07Always00 != 0x00) {
                list.add("headerbyte07=" + StringHelper.formatHex2(header.byte07Always00));
            }
            if (header.byte09Always04 != 0x04) {
                list.add("headerbyte09=" + StringHelper.formatHex2(header.byte09Always04));
            }
            if (header.byte0AAlways00 != 0x00) {
                list.add("headerbyte0A=" + StringHelper.formatHex2(header.byte0AAlways00));
            }
            if (header.byte0BAlways00 != 0x00) {
                list.add("headerbyte0B=" + StringHelper.formatHex2(header.byte0BAlways00));
            }
            if (header.bytes0CAlways00 != 0x00) {
                list.add("headerbytes0C=" + StringHelper.formatHex2(header.bytes0CAlways00));
            }
            String full = list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining("\n"));
            return full;
        }
    }

    private static class BattleAreaHeader {
        public static final int LENGTH = 0x60;
        private final int[] bytes;
        int headerOffset;

        int byte00Always00;
        int areaCount;
        int byte02Always01;
        int byte03Always04;
        int partyPositionCount;
        int aeonPositionCount;
        int monsterPositionCount;
        int byte07Always00;
        int unknownSubstructCount;
        int byte09Always04;
        int byte0AAlways00;
        int byte0BAlways00;
        int bytes0CAlways00;
        int offsetPartyPositions;
        int offsetPartySwitchPositions;
        int offsetAeonPositions;
        int offsetAeonSwitchPositions;
        int offsetMonsterPositions;
        int offsetMonsterSwitchPositions;
        int offsetUnknownSubstructs;
        int offsetFinalLocation;
        BattlePosition extraPosition1;
        BattlePosition extraPosition2;
        BattlePosition extraPosition3;

        public BattleAreaHeader(int[] bytes, int offset) {
            this.bytes = bytes;
            this.headerOffset = offset;
            mapBytes();
        }

        private void mapBytes() {
            byte00Always00 = bytes[headerOffset];
            areaCount = bytes[0x01 + headerOffset];
            byte02Always01 = bytes[0x02 + headerOffset];
            byte03Always04 = bytes[0x03 + headerOffset];
            partyPositionCount = bytes[0x04 + headerOffset];
            aeonPositionCount = bytes[0x05 + headerOffset];
            monsterPositionCount = bytes[0x06 + headerOffset];
            byte07Always00 = bytes[0x07 + headerOffset];
            unknownSubstructCount = bytes[0x08 + headerOffset];
            byte09Always04 = bytes[0x09 + headerOffset];
            byte0AAlways00 = bytes[0x0A + headerOffset];
            byte0BAlways00 = bytes[0x0B + headerOffset];
            bytes0CAlways00 = read4Bytes(0x0C + headerOffset);
            offsetPartyPositions = read4Bytes(0x10 + headerOffset);
            offsetPartySwitchPositions = read4Bytes(0x14 + headerOffset);
            offsetAeonPositions = read4Bytes(0x18 + headerOffset);
            offsetAeonSwitchPositions = read4Bytes(0x1C + headerOffset);
            offsetMonsterPositions = read4Bytes(0x20 + headerOffset);
            offsetMonsterSwitchPositions = read4Bytes(0x24 + headerOffset);
            offsetUnknownSubstructs = read4Bytes(0x28 + headerOffset);
            offsetFinalLocation = read4Bytes(0x2C + headerOffset);
            extraPosition1 = new BattlePosition(bytes, 0x30 + headerOffset);
            extraPosition2 = new BattlePosition(bytes, 0x40 + headerOffset);
            extraPosition3 = new BattlePosition(bytes, 0x50 + headerOffset);
        }

        private int read4Bytes(int offset) {
            return bytes[offset] + bytes[offset+1] * 0x100 + bytes[offset+2] * 0x10000 + bytes[offset+3] * 0x1000000;
        }
    }

    private static class BattleAreaUnknownSubstruct {
        public static final int LENGTH = 0x10;
        private final int[] bytes;
        int structOffset;

        int targetOffset;
        int targetCount;
        int byte04Always01;
        int byte05Always02;
        int byte07Always00;
        int bytes08Always00;
        int bytes0CAlways00;

        BattlePosition[] targetPositions;

        public BattleAreaUnknownSubstruct(int[] bytes, int structOffset) {
            this.bytes = bytes;
            this.structOffset = structOffset;
            mapBytes();
            readPositions();
        }

        private void mapBytes() {
            targetOffset = read4Bytes(structOffset);
            byte04Always01 = bytes[0x04 + structOffset];
            byte05Always02 = bytes[0x05 + structOffset];
            targetCount = bytes[0x06 + structOffset];
            byte07Always00 = bytes[0x07 + structOffset];
            bytes08Always00 = read4Bytes(0x08 + structOffset);
            bytes0CAlways00 = read4Bytes(0x0C + structOffset);
        }

        private void readPositions() {
            targetPositions = new BattlePosition[targetCount];
            for (int i = 0; i < targetCount; i++) {
                targetPositions[i] = new BattlePosition(bytes, targetOffset + i * BattlePosition.LENGTH);
            }
        }

        private int read4Bytes(int offset) {
            return bytes[offset] + bytes[offset+1] * 0x100 + bytes[offset+2] * 0x10000 + bytes[offset+3] * 0x1000000;
        }

        @Override
        public String toString() {
            return "{ " + targetCount + " positions "
                    + Arrays.toString(targetPositions)
                    + (byte04Always01 != 0x01 ? ", byte04=" + StringHelper.formatHex2(byte04Always01) : "")
                    + (byte05Always02 != 0x02 ? ", byte05=" + StringHelper.formatHex2(byte05Always02) : "")
                    + (byte07Always00 != 0x00 ? ", byte07=" + StringHelper.formatHex2(byte07Always00) : "")
                    + (bytes08Always00 != 0x00 ? ", bytes08=" + StringHelper.formatHex2(bytes08Always00) : "")
                    + (bytes0CAlways00 != 0x00 ? ", bytes0C=" + StringHelper.formatHex2(bytes0CAlways00) : "")
                    + " }";
        }
    }

    private static class BattlePosition {
        public static final int LENGTH = 0x10;
        float x;
        float y;
        float z;
        float w;

        public BattlePosition(int[] bytes, int offset) {
            x = Float.intBitsToFloat(read4Bytes(bytes, offset));
            y = Float.intBitsToFloat(read4Bytes(bytes, offset + 0x04));
            z = Float.intBitsToFloat(read4Bytes(bytes, offset + 0x08));
            w = Float.intBitsToFloat(read4Bytes(bytes, offset + 0x0C));
        }

        public boolean notNothing() {
            return x != 0 || y != 0 || z != 0 || w != 0;
        }

        @Override
        public String toString() {
            return "{ " +
                    "x=" + x +
                    ", y=" + y +
                    ", z=" + z +
                    (w != 0 ? ", w=" + w : "") +
                    " }";
        }

        private int read4Bytes(int[] bytes, int offset) {
            return bytes[offset] + bytes[offset+1] * 0x100 + bytes[offset+2] * 0x10000 + bytes[offset+3] * 0x1000000;
        }
    }
}
