package model.spheregrid;

import main.DataAccess;
import model.AbilityDataObject;
import model.Nameable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * dat01.dat (Original Sphere Grid)
 * dat02.dat (Standard Sphere Grid)
 * dat03.dat (Expert Sphere Grid)
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
    public String getName(String localization) {
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

    private static String asMove(int idx) {
        AbilityDataObject move = DataAccess.getMove(idx);
        return (move != null ? move.name : "null") + " [" + String.format("%04X", idx) + "h]";
    }
}
