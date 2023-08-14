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
                list.add("C#" + i + ": " + clusterObjects[i]);
            }
        }
        if (nodeObjects != null) {
            list.add(nodeCount + " Nodes");
            for (int i = 0; i < nodeObjects.length; i++) {
                list.add("N#" + i + ": " + nodeObjects[i]);
            }
        }
        if (linkObjects != null) {
            list.add(linkCount + " Links");
            for (int i = 0; i < linkObjects.length; i++) {
                list.add("L#" + i + ": " + linkObjects[i]);
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
            case 0x2A -> asMove(0x3006); // "Delay Attack";
            case 0x2B -> asMove(0x3007); // "Delay Buster";
            case 0x2C -> asMove(0x3008); // "Sleep Attack";
            case 0x2D -> asMove(0x3009); // "Silence Attack";
            case 0x2E -> asMove(0x300A); // "Dark Attack";
            case 0x2F -> asMove(0x300B); // "Zombie Attack";
            case 0x30 -> asMove(0x300C); // "Sleep Buster";
            case 0x31 -> asMove(0x300D); // "Silence Buster";
            case 0x32 -> asMove(0x300E); // "Dark Buster";
            case 0x33 -> asMove(0x300F); // "Triple Foul";
            case 0x34 -> asMove(0x3010); // "Power Break";
            case 0x35 -> asMove(0x3011); // "Magic Break";
            case 0x36 -> asMove(0x3012); // "Armor Break";
            case 0x37 -> asMove(0x3013); // "Mental Break";
            case 0x38 -> asMove(0x3014); // "Mug";
            case 0x39 -> asMove(0x3015); // "Quick Hit";
            case 0x3A -> asMove(0x3016); // "Steal";
            case 0x3B -> asMove(0x3017); // "Use";
            case 0x3C -> asMove(0x3018); // "Flee";
            case 0x3D -> asMove(0x3019); // "Pray";
            case 0x3E -> asMove(0x301A); // "Cheer";
            case 0x3F -> asMove(0x301C); // "Focus";
            case 0x40 -> asMove(0x301D); // "Reflex";
            case 0x41 -> asMove(0x301B); // "Aim";
            case 0x42 -> asMove(0x301E); // "Luck";
            case 0x43 -> asMove(0x301F); // "Jinx";
            case 0x44 -> asMove(0x3020); // "Lancet";
            case 0x45 -> asMove(0x3022); // "Guard";
            case 0x46 -> asMove(0x3023); // "Sentinel";
            case 0x47 -> asMove(0x3024); // "Spare Change";
            case 0x48 -> asMove(0x3025); // "Threaten";
            case 0x49 -> asMove(0x3026); // "Provoke";
            case 0x4A -> asMove(0x3027); // "Entrust";
            case 0x4B -> asMove(0x3028); // "Copycat";
            case 0x4C -> asMove(0x3029); // "Doublecast";
            case 0x4D -> asMove(0x302A); // "Bribe";
            case 0x4E -> asMove(0x302B); // "Cure";
            case 0x4F -> asMove(0x302C); // "Cura";
            case 0x50 -> asMove(0x302D); // "Curaga";
            case 0x51 -> asMove(0x302E); // "NulFrost";
            case 0x52 -> asMove(0x302F); // "NulBlaze";
            case 0x53 -> asMove(0x3030); // "NulShock";
            case 0x54 -> asMove(0x3031); // "NulTide";
            case 0x55 -> asMove(0x3032); // "Scan";
            case 0x56 -> asMove(0x3033); // "Esuna";
            case 0x57 -> asMove(0x3034); // "Life";
            case 0x58 -> asMove(0x3035); // "Full-Life";
            case 0x59 -> asMove(0x3036); // "Haste";
            case 0x5A -> asMove(0x3037); // "Hastega";
            case 0x5B -> asMove(0x3038); // "Slow";
            case 0x5C -> asMove(0x3039); // "Slowga";
            case 0x5D -> asMove(0x303A); // "Shell";
            case 0x5E -> asMove(0x303B); // "Protect";
            case 0x5F -> asMove(0x303C); // "Reflect";
            case 0x60 -> asMove(0x303D); // "Dispel";
            case 0x61 -> asMove(0x303E); // "Regen";
            case 0x62 -> asMove(0x303F); // "Holy";
            case 0x63 -> asMove(0x3040); // "Auto-Life";
            case 0x64 -> asMove(0x3041); // "Blizzard";
            case 0x65 -> asMove(0x3042); // "Fire";
            case 0x66 -> asMove(0x3043); // "Thunder";
            case 0x67 -> asMove(0x3044); // "Water";
            case 0x68 -> asMove(0x3045); // "Fira";
            case 0x69 -> asMove(0x3046); // "Blizzara";
            case 0x6A -> asMove(0x3047); // "Thundara";
            case 0x6B -> asMove(0x3048); // "Watera";
            case 0x6C -> asMove(0x3049); // "Firaga";
            case 0x6D -> asMove(0x304A); // "Blizzaga";
            case 0x6E -> asMove(0x304B); // "Thundaga";
            case 0x6F -> asMove(0x304C); // "Waterga";
            case 0x70 -> asMove(0x304D); // "Bio";
            case 0x71 -> asMove(0x304E); // "Demi";
            case 0x72 -> asMove(0x304F); // "Death";
            case 0x73 -> asMove(0x3050); // "Drain";
            case 0x74 -> asMove(0x3051); // "Osmose";
            case 0x75 -> asMove(0x3052); // "Flare";
            case 0x76 -> asMove(0x3053); // "Ultima";
            case 0x77 -> asMove(0x3058); // "Pilfer Gil";
            case 0x78 -> asMove(0x3059); // "Full Break";
            case 0x79 -> asMove(0x305A); // "Extract Power";
            case 0x7A -> asMove(0x305B); // "Extract Mana";
            case 0x7B -> asMove(0x305C); // "Extract Speed";
            case 0x7C -> asMove(0x305D); // "Extract Ability";
            case 0x7D -> asMove(0x305E); // "Nab Gil";
            case 0x7E -> asMove(0x305F); // "Quick Pockets";
            default -> "Unknown:" + String.format("%02X", b);
        };
    }

    private static String asMove(int idx) {
        AbilityDataObject move = DataAccess.getMove(idx);
        return (move != null ? move.name : "null") + " [" + String.format("%04X", idx) + "h]";
    }
}
