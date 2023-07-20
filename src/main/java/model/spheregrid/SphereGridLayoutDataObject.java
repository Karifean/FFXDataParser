package model.spheregrid;

import main.DataAccess;
import model.AbilityDataObject;
import model.Nameable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * dat01.dat
 * dat02.dat
 * dat03.dat
 */
public class SphereGridLayoutDataObject implements Nameable {
    private final int[] bytes;

    private int unknown1;
    private int clusterCount;
    private int nodeCount;
    private int linkCount;
    private int unknown5;
    private int unknown6;
    private int unknown7;
    private int unknown8;
    private int[] clusterBytes;
    private int[] nodesBytes;
    private int[] linkBytes;

    private SphereGridClusterDataObject[] clusterObjects;
    private SphereGridNodeDataObject[] nodeObjects;
    private SphereGridLinkDataObject[] linkObjects;
    int[] nodeContents;

    public SphereGridLayoutDataObject(int[] bytes, int[] contentBytes) {
        this.bytes = bytes;
        mapBytes();
        mapObjects();
        setNodeContents(contentBytes);
    }

    public SphereGridLayoutDataObject(int[] bytes) {
        this(bytes, null);
    }

    private void mapBytes() {
        unknown1 = read2Bytes(0x00);
        clusterCount = read2Bytes(0x02);
        nodeCount = read2Bytes(0x04);
        linkCount = read2Bytes(0x06);
        unknown5 = read2Bytes(0x08);
        unknown6 = read2Bytes(0x0A);
        unknown7 = read2Bytes(0x0C);
        unknown8 = read2Bytes(0x0E);
        int endOfClusterBytes = 0x10 + clusterCount * SphereGridClusterDataObject.LENGTH;
        clusterBytes = Arrays.copyOfRange(bytes, 0x10, endOfClusterBytes);
        int endOfNodesBytes = endOfClusterBytes + nodeCount * 0x0C;
        nodesBytes = Arrays.copyOfRange(bytes, endOfClusterBytes, endOfNodesBytes);
        int endOfLinksBytes = endOfNodesBytes + linkCount * 0x08;
        linkBytes = Arrays.copyOfRange(bytes, endOfNodesBytes, endOfLinksBytes);
    }

    private void mapObjects() {
        clusterObjects = new SphereGridClusterDataObject[clusterCount];
        for (int i = 0; i < clusterCount; i++) {
            clusterObjects[i] = new SphereGridClusterDataObject(Arrays.copyOfRange(clusterBytes, i * SphereGridClusterDataObject.LENGTH, (i + 1) * SphereGridClusterDataObject.LENGTH));
        }
        nodeObjects = new SphereGridNodeDataObject[nodeCount];
        for (int i = 0; i < nodeCount; i++) {
            nodeObjects[i] = new SphereGridNodeDataObject(Arrays.copyOfRange(nodesBytes, i * SphereGridNodeDataObject.LENGTH, (i + 1) * SphereGridNodeDataObject.LENGTH));
        }
        linkObjects = new SphereGridLinkDataObject[linkCount];
        for (int i = 0; i < linkCount; i++) {
            linkObjects[i] = new SphereGridLinkDataObject(Arrays.copyOfRange(linkBytes, i * SphereGridLinkDataObject.LENGTH, (i + 1) * SphereGridLinkDataObject.LENGTH));
        }
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        if (clusterObjects != null) {
            list.add(clusterCount + " Clusters");
            for (int i = 0; i < clusterObjects.length; i++) {
                list.add("#" + i + ": " + clusterObjects[i]);
            }
        }
        if (nodeObjects != null) {
            list.add(nodeCount + " Nodes");
            for (int i = 0; i < nodeObjects.length; i++) {
                list.add("#" + i + ": " + nodeObjects[i]);
            }
        }
        if (linkObjects != null) {
            list.add(linkCount + " Links");
            for (int i = 0; i < linkObjects.length; i++) {
                list.add("#" + i + ": " + linkObjects[i]);
            }
        }
        return String.join("\n", list);
    }

    @Override
    public String getName() {
        return this.toString();
    }

    public void setNodeContents(int[] nodeContents) {
        this.nodeContents = nodeContents;
        if (nodeObjects == null || nodeContents == null) {
            return;
        }
        for (int i = 0; i < nodeContents.length && i < nodeObjects.length; i++) {
            nodeObjects[i].setContent(nodeContents[i]);
        }
    }

    private int read2Bytes(int offset) {
        return bytes[offset] + bytes[offset+1] * 0x100;
    }

    public static String byteToNodeContent(int b) {
        return switch (b) {
            case 0x00 -> "Lv 3 Lock";
            case 0x01 -> "Empty";
            case 0x02 -> "STR+1";
            case 0x03 -> "STR+2";
            case 0x04 -> "STR+3";
            case 0x05 -> "STR+4";
            case 0x06 -> "DEF+1";
            case 0x07 -> "DEF+2";
            case 0x08 -> "DEF+3";
            case 0x09 -> "DEF+4";
            case 0x0A -> "MAG+1";
            case 0x0B -> "MAG+2";
            case 0x0C -> "MAG+3";
            case 0x0D -> "MAG+4";
            case 0x0E -> "MDF+1";
            case 0x0F -> "MDF+2";
            case 0x10 -> "MDF+3";
            case 0x11 -> "MDF+4";
            case 0x12 -> "AGI+1";
            case 0x13 -> "AGI+2";
            case 0x14 -> "AGI+3";
            case 0x15 -> "AGI+4";
            case 0x16 -> "LCK+1";
            case 0x17 -> "LCK+2";
            case 0x18 -> "LCK+3";
            case 0x19 -> "LCK+4";
            case 0x1A -> "EVA+1";
            case 0x1B -> "EVA+2";
            case 0x1C -> "EVA+3";
            case 0x1D -> "EVA+4";
            case 0x1E -> "ACC+1";
            case 0x1F -> "ACC+2";
            case 0x20 -> "ACC+3";
            case 0x21 -> "ACC+4";
            case 0x22 -> "HP+200";
            case 0x23 -> "HP+300";
            case 0x24 -> "MP+40";
            case 0x25 -> "MP+20";
            case 0x26 -> "MP+10";
            case 0x27 -> "Lv 1 Lock";
            case 0x28 -> "Lv 2 Lock";
            case 0x29 -> "Lv 4 Lock";
            case 0x2A -> "Delay Attack";
            case 0x2B -> "Delay Buster";
            case 0x2C -> "Sleep Attack";
            case 0x2D -> "Silence Attack";
            case 0x2E -> "Dark Attack";
            case 0x2F -> "Zombie Attack";
            case 0x30 -> "Sleep Buster";
            case 0x31 -> "Silence Buster";
            case 0x32 -> "Dark Buster";
            case 0x33 -> "Triple Foul";
            case 0x34 -> "Power Break";
            case 0x35 -> "Magic Break";
            case 0x36 -> "Armor Break";
            case 0x37 -> "Mental Break";
            case 0x38 -> "Mug";
            case 0x39 -> "Quick Hit";
            case 0x3A -> "Steal";
            case 0x3B -> "Use";
            case 0x3C -> "Flee";
            case 0x3D -> "Pray";
            case 0x3E -> "Cheer";
            case 0x3F -> "Focus";
            case 0x40 -> "Reflex";
            case 0x41 -> "Aim";
            case 0x42 -> "Luck";
            case 0x43 -> "Jinx";
            case 0x44 -> "Lancet";
            case 0x45 -> "Guard";
            case 0x46 -> "Sentinel";
            case 0x47 -> "Spare Change";
            case 0x48 -> "Threaten";
            case 0x49 -> "Provoke";
            case 0x4A -> "Entrust";
            case 0x4B -> "Copycat";
            case 0x4C -> "Doublecast";
            case 0x4D -> "Bribe";
            case 0x4E -> "Cure";
            case 0x4F -> "Cura";
            case 0x50 -> "Curaga";
            case 0x51 -> "NulFrost";
            case 0x52 -> "NulBlaze";
            case 0x53 -> "NulShock";
            case 0x54 -> "NulTide";
            case 0x55 -> "Scan";
            case 0x56 -> "Esuna";
            case 0x57 -> "Life";
            case 0x58 -> "Full-Life";
            case 0x59 -> "Haste";
            case 0x5A -> "Hastega";
            case 0x5B -> "Slow";
            case 0x5C -> "Slowga";
            case 0x5D -> "Shell";
            case 0x5E -> "Protect";
            case 0x5F -> "Reflect";
            case 0x60 -> "Dispel";
            case 0x61 -> "Regen";
            case 0x62 -> "Holy";
            case 0x63 -> "Auto-Life";
            case 0x64 -> "Blizzard";
            case 0x65 -> "Fire";
            case 0x66 -> "Thunder";
            case 0x67 -> "Water";
            case 0x68 -> "Fira";
            case 0x69 -> "Blizzara";
            case 0x6A -> "Thundara";
            case 0x6B -> "Watera";
            case 0x6C -> "Firaga";
            case 0x6D -> "Blizzaga";
            case 0x6E -> "Thundaga";
            case 0x6F -> "Waterga";
            case 0x70 -> "Bio";
            case 0x71 -> "Demi";
            case 0x72 -> "Death";
            case 0x73 -> "Drain";
            case 0x74 -> "Osmose";
            case 0x75 -> "Flare";
            case 0x76 -> "Ultima";
            case 0x77 -> "Pilfer Gil";
            case 0x78 -> "Full Break";
            case 0x79 -> "Extract Power";
            case 0x7A -> "Extract Mana";
            case 0x7B -> "Extract Speed";
            case 0x7C -> "Extract Ability";
            case 0x7D -> "Nab Gil";
            case 0x7E -> "Quick Pockets";
            default -> "Unknown:" + String.format("%02X", b);
        };
    }

    private static String asMove(int idx) {
        AbilityDataObject move = DataAccess.getMove(idx);
        return (move != null ? move.name : "null") + " [" + String.format("%04X", idx) + "h]";
    }
}
