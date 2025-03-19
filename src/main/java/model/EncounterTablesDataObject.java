package model;

import main.StringHelper;
import reading.Chunk;
import reading.BytesHelper;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static reading.BytesHelper.read2Bytes;

/**
 * btl.bin
 */
public class EncounterTablesDataObject {
    int[] chunk1Bytes;
    int[] chunk2Bytes;

    FieldTable[] fieldTables;

    public EncounterTablesDataObject(int[] bytes) {
        List<Chunk> chunks = BytesHelper.bytesToChunks(bytes, 2, 4);
        mapChunks(chunks);
        mapObjects();
    }

    private void mapChunks(List<Chunk> chunks) {
        chunk1Bytes = chunks.get(0).bytes;
        chunk2Bytes = chunks.get(1).bytes;
    }

    private void mapObjects() {
        int fieldCount = chunk1Bytes.length / FieldTable.LENGTH;
        fieldTables = new FieldTable[fieldCount];
        for (int i = 0; i < fieldCount; i++) {
            fieldTables[i] = new FieldTable(chunk1Bytes, chunk2Bytes, i * FieldTable.LENGTH);
        }
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        list.add(fieldTables.length + " Tables Total");
        for (int i = 0; i < fieldTables.length; i++) {
            list.add("Table " + StringHelper.formatHex2(i) + ": " + fieldTables[i].toString());
        }
        if (chunk1Bytes.length % FieldTable.LENGTH != 0) {
            list.add("Leftover bytes=" + (chunk1Bytes.length % FieldTable.LENGTH));
        }
        return String.join("\n", list);
    }

    private static class FieldTable {
        public static final int LENGTH = 0x0E;

        int id;
        int dataOffset;
        int formationOffset;
        String field;
        int unknown0C;

        int totalFormationCount;
        int groupCount;
        FieldFormationGroup[] groups;

        public FieldTable(int[] bytes, int[] dataBytes, int offset) {
            id = read2Bytes(bytes, offset);
            dataOffset = read2Bytes(bytes, offset + 0x02);
            formationOffset = read2Bytes(bytes, offset + 0x04);
            byte[] stringBytes = new byte[6];
            for (int i = 0; i < 6; i++) {
                stringBytes[i] = (byte) bytes[offset + 0x06 + i];
            }
            field = new String(stringBytes, StandardCharsets.UTF_8);
            unknown0C = read2Bytes(bytes, offset + 0x0C);

            totalFormationCount = dataBytes[dataOffset];
            groupCount = dataBytes[dataOffset + 0x01];
            groups = new FieldFormationGroup[groupCount];
            int groupOffset = 2;
            for (int i = 0; i < groupCount; i++) {
                FieldFormationGroup group = new FieldFormationGroup(this, dataBytes, dataOffset + groupOffset);
                groups[i] = group;
                groupOffset += group.getLength();
            }
        }

        @Override
        public String toString() {
            return "FieldTable {" +
                    "id=" + id +
                    ", field=" + field +
                    ", fmOffset?=" + formationOffset +
                    (unknown0C != 0 ? ", unknown0C=" + unknown0C : "") +
                    ", fmCount=" + totalFormationCount +
                    ", groups(" + groupCount + ")=" + Arrays.toString(groups) +
                    '}';
        }
    }

    private static class FieldFormationGroup {
        public static final int LENGTH = 0x0E;

        int formationCount;
        int battlefield;
        int danger;
        int totalWeight;

        FieldTable parentTable;
        FieldFormation[] formations;

        public FieldFormationGroup(FieldTable table, int[] bytes, int offset) {
            parentTable = table;
            formationCount = bytes[offset];
            battlefield = read2Bytes(bytes, offset + 0x01);
            danger = bytes[offset + 0x03];
            totalWeight = bytes[offset + 0x04];
            formations = new FieldFormation[formationCount];
            for (int i = 0; i < formationCount; i++) {
                formations[i] = new FieldFormation(table, this, bytes, offset + 0x05 + i * FieldFormation.LENGTH);
            }
        }

        public int getLength() {
            return 0x05 + formationCount * FieldFormation.LENGTH;
        }

        @Override
        public String toString() {
            return "Group {battlefield=" + battlefield +
                    ", danger=" + danger +
                    ", formations(" + formationCount + ")=" + Arrays.toString(formations) +
                    "}";
        }
    }

    private static class FieldFormation {
        public static final int LENGTH = 0x02;

        int id;
        int weight;

        String encounter;
        FieldTable parentTable;
        FieldFormationGroup parentGroup;

        public FieldFormation(FieldTable table, FieldFormationGroup group, int[] bytes, int offset) {
            parentTable = table;
            parentGroup = group;
            id = bytes[offset];
            weight = bytes[offset + 1];
            encounter = String.format("%s_%02d", table.field, id);
        }

        @Override
        public String toString() {
            int totalWeight = parentGroup.totalWeight;
            return encounter +
                    (weight > 0 || totalWeight > 0 ? " (" + (totalWeight > 0 ? weight + "/" + totalWeight : weight) + ")" : "");
        }
    }
}
