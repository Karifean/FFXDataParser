package model;

import atel.model.StackObject;
import main.StringHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Part of EncounterFile
 */
public class BattlePositionsDataObject {
    private final int[] bytes;

    int countPositionings;
    BattlePositioning[] positionings;

    public BattlePositionsDataObject(int[] bytes) {
        this.bytes = bytes;
        mapBytes();
    }

    private void mapBytes() {
        BattlePositioning first = new BattlePositioning(bytes, 0);
        countPositionings = first.header.countPositionings;
        positionings = new BattlePositioning[countPositionings];
        positionings[0] = first;
        for (int i = 1; i < countPositionings; i++) {
            positionings[i] = new BattlePositioning(bytes, BattlePositioningHeader.LENGTH * i);
        }
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        list.add("Number of Positionings: " + countPositionings);
        for (int i = 0; i < countPositionings; i++) {
            list.add("--- Positioning " + StringHelper.formatHex2(i) + " ---");
            list.add(positionings[i].toString());
        }
        String full = list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining("\n"));
        return full;
    }

    private static class BattlePositioningHeader {
        private static final int LENGTH = 0x60;
        private final int[] bytes;
        int headerOffset;

        int byte00Always00;
        int countPositionings;
        int byte02Always01;
        int byte03Always04;
        int countPartyLocations;
        int countAeonLocations;
        int countMonsterLocations;
        int byte07Always00;
        int countUnknownSubstructs;
        int byte09Always04;
        int byte0AAlways00;
        int byte0BAlways00;
        int bytes0CAlways00;
        int offsetPartyLocations;
        int offsetPartySwitchLocations;
        int offsetAeonLocations;
        int offsetAeonSwitchLocations;
        int offsetMonsterLocations;
        int offsetMonsterSwitchLocations;
        int offsetUnknownSubstructs;
        int offsetFinalLocation;

        public BattlePositioningHeader(int[] bytes, int offset) {
            this.bytes = bytes;
            this.headerOffset = offset;
            mapBytes();
        }

        private void mapBytes() {
            byte00Always00 = bytes[headerOffset];
            countPositionings = bytes[0x01 + headerOffset];
            byte02Always01 = bytes[0x02 + headerOffset];
            byte03Always04 = bytes[0x03 + headerOffset];
            countPartyLocations = bytes[0x04 + headerOffset];
            countAeonLocations = bytes[0x05 + headerOffset];
            countMonsterLocations = bytes[0x06 + headerOffset];
            byte07Always00 = bytes[0x07 + headerOffset];
            countUnknownSubstructs = bytes[0x08 + headerOffset];
            byte09Always04 = bytes[0x09 + headerOffset];
            byte0AAlways00 = bytes[0x0A + headerOffset];
            byte0BAlways00 = bytes[0x0B + headerOffset];
            bytes0CAlways00 = read4Bytes(0x0C + headerOffset);
            offsetPartyLocations = read4Bytes(0x10 + headerOffset);
            offsetPartySwitchLocations = read4Bytes(0x14 + headerOffset);
            offsetAeonLocations = read4Bytes(0x18 + headerOffset);
            offsetAeonSwitchLocations = read4Bytes(0x1C + headerOffset);
            offsetMonsterLocations = read4Bytes(0x20 + headerOffset);
            offsetMonsterSwitchLocations = read4Bytes(0x24 + headerOffset);
            offsetUnknownSubstructs = read4Bytes(0x28 + headerOffset);
            offsetFinalLocation = read4Bytes(0x2C + headerOffset);
        }

        private int read4Bytes(int offset) {
            return bytes[offset] + bytes[offset+1] * 0x100 + bytes[offset+2] * 0x10000 + bytes[offset+3] * 0x1000000;
        }
    }

    private static class BattlePositioning {
        private final int[] bytes;

        BattlePositioningHeader header;
        BattleLocation[] partySwitchLocations;
        BattleLocation[] partyLocations;
        BattleLocation[] aeonSwitchLocations;
        BattleLocation[] aeonLocations;
        BattleLocation[] monsterSwitchLocations;
        BattleLocation[] monsterLocations;
        BattlePositioningUnknownSubstruct[] unknownTargetStructs;
        BattleLocation finalLocation;

        public BattlePositioning(int[] bytes, int headerOffset) {
            this.bytes = bytes;
            header = new BattlePositioningHeader(bytes, headerOffset);
            readLocations();
        }

        private void readLocations() {
            partyLocations = new BattleLocation[header.countPartyLocations];
            partySwitchLocations = new BattleLocation[header.countPartyLocations];
            for (int i = 0; i < header.countPartyLocations; i++) {
                partyLocations[i] = new BattleLocation(bytes, header.offsetPartyLocations + i * 0x10);
                partySwitchLocations[i] = new BattleLocation(bytes, header.offsetPartySwitchLocations + i * 0x10);
            }
            aeonLocations = new BattleLocation[header.countAeonLocations];
            aeonSwitchLocations = new BattleLocation[header.countAeonLocations];
            for (int i = 0; i < header.countAeonLocations; i++) {
                aeonLocations[i] = new BattleLocation(bytes, header.offsetAeonLocations + i * 0x10);
                aeonSwitchLocations[i] = new BattleLocation(bytes, header.offsetAeonSwitchLocations + i * 0x10);
            }
            monsterLocations = new BattleLocation[header.countMonsterLocations];
            monsterSwitchLocations = new BattleLocation[header.countMonsterLocations];
            for (int i = 0; i < header.countMonsterLocations; i++) {
                monsterLocations[i] = new BattleLocation(bytes, header.offsetMonsterLocations + i * 0x10);
                monsterSwitchLocations[i] = new BattleLocation(bytes, header.offsetMonsterSwitchLocations + i * 0x10);
            }
            unknownTargetStructs = new BattlePositioningUnknownSubstruct[header.countUnknownSubstructs];
            for (int i = 0; i < header.countUnknownSubstructs; i++) {
                unknownTargetStructs[i] = new BattlePositioningUnknownSubstruct(bytes, header.offsetUnknownSubstructs + i * 0x10);
            }
            finalLocation = new BattleLocation(bytes, header.offsetFinalLocation);
        }

        @Override
        public String toString() {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < header.countPartyLocations; i++) {
                list.add("Party Location " + StringHelper.formatHex2(i) + " " + partyLocations[i] + " Switch " + partySwitchLocations[i]);
            }
            for (int i = 0; i < header.countAeonLocations; i++) {
                list.add("Aeon Location " + StringHelper.formatHex2(i) + " (" + StackObject.enumToScriptField("playerChar", i + 8).getLabel() + ") " + aeonLocations[i] + " Switch " + aeonSwitchLocations[i]);
            }
            for (int i = 0; i < header.countMonsterLocations; i++) {
                list.add("Monster Location " + StringHelper.formatHex2(i) + " " + monsterLocations[i] + " Switch " + monsterSwitchLocations[i]);
            }
            for (int i = 0; i < unknownTargetStructs.length; i++) {
                list.add("Extra Struct " + StringHelper.formatHex2(i) + " " + unknownTargetStructs[i]);
            }
            list.add("Final Vector " + finalLocation);
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

        private int read4Bytes(int offset) {
            return bytes[offset] + bytes[offset+1] * 0x100 + bytes[offset+2] * 0x10000 + bytes[offset+3] * 0x1000000;
        }
    }

    private static class BattlePositioningUnknownSubstruct {
        private final int[] bytes;

        int targetOffset;
        int targetCount;
        int byte04Always01;
        int byte05Always02;
        int byte07Always00;
        int bytes08Always00;
        int bytes0CAlways00;

        BattleLocation[] targetLocations;

        public BattlePositioningUnknownSubstruct(int[] bytes, int baseOffset) {
            this.bytes = bytes;
            targetOffset = read4Bytes(baseOffset);
            byte04Always01 = bytes[baseOffset + 0x04];
            byte05Always02 = bytes[baseOffset + 0x05];
            targetCount = bytes[baseOffset + 0x06];
            byte07Always00 = bytes[baseOffset + 0x07];
            bytes08Always00 = read4Bytes(baseOffset + 0x08);
            bytes0CAlways00 = read4Bytes(baseOffset + 0x0C);
            readLocations();
        }

        private void readLocations() {
            targetLocations = new BattleLocation[targetCount];
            for (int i = 0; i < targetCount; i++) {
                targetLocations[i] = new BattleLocation(bytes, targetOffset + i * 0x10);
            }
        }

        private int read4Bytes(int offset) {
            return bytes[offset] + bytes[offset+1] * 0x100 + bytes[offset+2] * 0x10000 + bytes[offset+3] * 0x1000000;
        }

        @Override
        public String toString() {
            return "{ " + targetCount + " locs "
                    + Arrays.toString(targetLocations)
                    + (byte04Always01 != 0x01 ? ", byte04=" + StringHelper.formatHex2(byte04Always01) : "")
                    + (byte05Always02 != 0x02 ? ", byte05=" + StringHelper.formatHex2(byte05Always02) : "")
                    + (byte07Always00 != 0x00 ? ", byte07=" + StringHelper.formatHex2(byte07Always00) : "")
                    + (bytes08Always00 != 0x00 ? ", bytes08=" + StringHelper.formatHex2(bytes08Always00) : "")
                    + (bytes0CAlways00 != 0x00 ? ", bytes0C=" + StringHelper.formatHex2(bytes0CAlways00) : "")
                    + " }";
        }
    }

    private static class BattleLocation {
        float x;
        float y;
        float z;
        float w;

        public BattleLocation(int[] bytes, int offset) {
            x = Float.intBitsToFloat(read4Bytes(bytes, offset));
            y = Float.intBitsToFloat(read4Bytes(bytes, offset + 0x04));
            z = Float.intBitsToFloat(read4Bytes(bytes, offset + 0x08));
            w = Float.intBitsToFloat(read4Bytes(bytes, offset + 0x0C));
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
