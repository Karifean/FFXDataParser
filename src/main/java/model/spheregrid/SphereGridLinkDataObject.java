package model.spheregrid;

import model.Nameable;

import java.util.ArrayList;
import java.util.List;

/**
 * Part of SphereGridLayoutDataObject
 */
public class SphereGridLinkDataObject {
    public static final int LENGTH = 0x8;
    private final int[] bytes;

    private int node1;
    private int node2;
    private int anchorNode;
    private int unused;

    public SphereGridLinkDataObject(int[] bytes, int[] stringBytes) {
        this.bytes = bytes;
        mapBytes();
    }

    public SphereGridLinkDataObject(int[] bytes) {
        this(bytes, null);
    }

    private void mapBytes() {
        node1 = read2Bytes(0x00);
        node2 = read2Bytes(0x02);
        anchorNode = read2Bytes(0x04);
        unused = read2Bytes(0x06);
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        list.add("From Node N#" + node1 + " to N#" + node2);
        if (anchorNode != 0xFFFF) {
            list.add("Curved");
            list.add("Anchor=N#" + anchorNode);
        } else {
            list.add("Straight");
        }
        if (unused != 0x0000) {
            list.add("Unused=" + unused);
        }
        String full = String.join(", ", list);
        return "{ " +  full + " }";
    }

    private int read2Bytes(int offset) {
        return bytes[offset] + bytes[offset+1] * 0x100;
    }
}
