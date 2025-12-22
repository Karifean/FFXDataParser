package model;

import main.StringHelper;

import java.util.Arrays;

import static reading.BytesHelper.read2Bytes;

/**
 * btl.bin
 */
public class FieldEncounterTableDataObject {
    public static final int HEADER_LENGTH = 0x0E;

    int id;
    int dataOffset;
    int formationOffset;
    String map;
    int unknown0C;

    int totalFormationCount;
    int groupCount;
    FieldFormationGroup[] groups;

    public FieldEncounterTableDataObject(int[] headerBytes, int[] dataBytes, int headerOffset) {
        id = read2Bytes(headerBytes, headerOffset);
        dataOffset = read2Bytes(headerBytes, headerOffset + 0x02);
        formationOffset = read2Bytes(headerBytes, headerOffset + 0x04);
        map = StringHelper.getUtf8String(headerBytes, headerOffset + 0x06, 0x06);
        unknown0C = read2Bytes(headerBytes, headerOffset + 0x0C);

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
        return "{" +
                String.format("id=%3d", id) +
                ", map=" + map +
                ", fmOffset?=" + formationOffset +
                (unknown0C != 0 ? ", unknown0C=" + unknown0C : "") +
                ", fmCount=" + totalFormationCount +
                ", groups(" + groupCount + ")=" + Arrays.toString(groups) +
                '}';
    }

    private static class FieldFormationGroup {
        public static final int LENGTH = 0x0E;

        int formationCount;
        int battlefield;
        int danger;
        int totalWeight;

        FieldEncounterTableDataObject parentTable;
        FieldFormation[] formations;

        public FieldFormationGroup(FieldEncounterTableDataObject table, int[] bytes, int offset) {
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
        FieldEncounterTableDataObject parentTable;
        FieldFormationGroup parentGroup;

        public FieldFormation(FieldEncounterTableDataObject table, FieldFormationGroup group, int[] bytes, int offset) {
            parentTable = table;
            parentGroup = group;
            id = bytes[offset];
            weight = bytes[offset + 1];
            encounter = String.format("%s_%02d", table.map, id);
        }

        @Override
        public String toString() {
            int totalWeight = parentGroup.totalWeight;
            return encounter +
                    (weight > 0 || totalWeight > 0 ? " (" + (totalWeight > 0 ? weight + "/" + totalWeight : weight) + ")" : "");
        }
    }
}
