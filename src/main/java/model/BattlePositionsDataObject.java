package model;

import atel.model.StackObject;

import java.util.ArrayList;
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
        list.add("Positionings: " + countPositionings);
        for (int i = 0; i < countPositionings; i++) {
            list.add("--- Positioning #" + (i + 1) + " ---");
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
        int byte08Always01;
        int byte09Always04;
        int byte0AAlways00;
        int byte0BAlways00;
        int bytes0CAlways00;
        int offsetPartyLocations;
        int offsetPartySomethings;
        int offsetAeonLocations;
        int offsetAeonSomethings;
        int offsetMonsterLocations;
        int offsetMonsterSomethings;
        int offset28WeirdOne;
        int offset2C;

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
            byte08Always01 = bytes[0x08 + headerOffset];
            byte09Always04 = bytes[0x09 + headerOffset];
            byte0AAlways00 = bytes[0x0A + headerOffset];
            byte0BAlways00 = bytes[0x0B + headerOffset];
            bytes0CAlways00 = read4Bytes(0x0C + headerOffset);
            offsetPartyLocations = read4Bytes(0x10 + headerOffset);
            offsetPartySomethings = read4Bytes(0x14 + headerOffset);
            offsetAeonLocations = read4Bytes(0x18 + headerOffset);
            offsetAeonSomethings = read4Bytes(0x1C + headerOffset);
            offsetMonsterLocations = read4Bytes(0x20 + headerOffset);
            offsetMonsterSomethings = read4Bytes(0x24 + headerOffset);
            offset28WeirdOne = read4Bytes(0x28 + headerOffset);
            offset2C = read4Bytes(0x2C + headerOffset);
        }

        private int read4Bytes(int offset) {
            return bytes[offset] + bytes[offset+1] * 0x100 + bytes[offset+2] * 0x10000 + bytes[offset+3] * 0x1000000;
        }
    }

    private static class BattlePositioning {
        private final int[] bytes;

        BattlePositioningHeader header;
        BattleLocation[] partySomethings;
        BattleLocation[] partyLocations;
        BattleLocation[] aeonSomethings;
        BattleLocation[] aeonLocations;
        BattleLocation[] monsterSomethings;
        BattleLocation[] monsterLocations;
        BattleLocation[] weirdoTargetLocations;
        BattleLocation finalLocation;

        public BattlePositioning(int[] bytes, int headerOffset) {
            this.bytes = bytes;
            header = new BattlePositioningHeader(bytes, headerOffset);
            readLocations();
        }

        private void readLocations() {
            partyLocations = new BattleLocation[header.countPartyLocations];
            partySomethings = new BattleLocation[header.countPartyLocations];
            for (int i = 0; i < header.countPartyLocations; i++) {
                partyLocations[i] = new BattleLocation(bytes, header.offsetPartyLocations + i * 0x10);
                partySomethings[i] = new BattleLocation(bytes, header.offsetPartySomethings + i * 0x10);
            }
            aeonLocations = new BattleLocation[header.countAeonLocations];
            aeonSomethings = new BattleLocation[header.countAeonLocations];
            for (int i = 0; i < header.countAeonLocations; i++) {
                aeonLocations[i] = new BattleLocation(bytes, header.offsetAeonLocations + i * 0x10);
                aeonSomethings[i] = new BattleLocation(bytes, header.offsetAeonSomethings + i * 0x10);
            }
            monsterLocations = new BattleLocation[header.countMonsterLocations];
            monsterSomethings = new BattleLocation[header.countMonsterLocations];
            for (int i = 0; i < header.countMonsterLocations; i++) {
                monsterLocations[i] = new BattleLocation(bytes, header.offsetMonsterLocations + i * 0x10);
                monsterSomethings[i] = new BattleLocation(bytes, header.offsetMonsterSomethings + i * 0x10);
            }
            int weirdoTargetOffset = read4Bytes(header.offset28WeirdOne);
            int weirdoTargetLocationCount = bytes[header.offset28WeirdOne + 0x05];
            weirdoTargetLocations = new BattleLocation[weirdoTargetLocationCount];
            for (int i = 0; i < weirdoTargetLocationCount; i++) {
                weirdoTargetLocations[i] = new BattleLocation(bytes, weirdoTargetOffset + i * 0x10);
            }
            finalLocation = new BattleLocation(bytes, header.offset2C);
        }

        @Override
        public String toString() {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < header.countPartyLocations; i++) {
                list.add("Party Location #" + (i + 1) + ": " + partyLocations[i]);
            }
            for (int i = 0; i < header.countPartyLocations; i++) {
                if (partySomethings[i].notNothing()) {
                    list.add("Party Unknown Vector #" + (i + 1) + ": " + partySomethings[i]);
                }
            }
            for (int i = 0; i < header.countAeonLocations; i++) {
                list.add("Aeon Location #" + (i + 1) + " (" + StackObject.enumToScriptField("playerChar", i + 8).getName() + "?): " + aeonLocations[i]);
            }
            for (int i = 0; i < header.countAeonLocations; i++) {
                if (aeonSomethings[i].notNothing()) {
                    list.add("Aeon Unknown Vector #" + (i + 1) + ": " + aeonSomethings[i]);
                }
            }
            for (int i = 0; i < header.countMonsterLocations; i++) {
                list.add("Monster Location #" + (i + 1) + ": " + monsterLocations[i]);
            }
            for (int i = 0; i < header.countMonsterLocations; i++) {
                if (monsterSomethings[i].notNothing()) {
                    list.add("Monster Unknown Vector #" + (i + 1) + ": " + monsterSomethings[i]);
                }
            }
            for (int i = 0; i < weirdoTargetLocations.length; i++) {
                list.add("Weirdo Vector #" + (i + 1) + ": " + weirdoTargetLocations[i]);
            }
            if (finalLocation.notNothing()) {
                list.add("Final Vector: " + finalLocation);
            }
            String full = list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining("\n"));
            return full;
        }

        private int read4Bytes(int offset) {
            return bytes[offset] + bytes[offset+1] * 0x100 + bytes[offset+2] * 0x10000 + bytes[offset+3] * 0x1000000;
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
