package model.spheregrid;

import model.Nameable;

import java.util.ArrayList;
import java.util.List;

public class SphereGridNodeDataObject implements Nameable {
    public static final int LENGTH = 0xC;
    private final int[] bytes;

    private int posX;
    private int posY;
    private int unused3;
    private int redundantContent;
    private int cluster;
    private int unknown6;

    private Integer content;

    public SphereGridNodeDataObject(int[] bytes, int[] stringBytes) {
        this.bytes = bytes;
        mapBytes();
    }

    public SphereGridNodeDataObject(int[] bytes) {
        this(bytes, null);
    }

    private void mapBytes() {
        posX = read2Bytes(0x00, true);
        posY = read2Bytes(0x02, true);
        unused3 = read2Bytes(0x04, false);
        redundantContent = read2Bytes(0x06, false);
        cluster = read2Bytes(0x08, false);
        unknown6 = read2Bytes(0x0A, false);
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        list.add("Position=(" + posX + "/" + posY + ")");
        list.add("Cluster=C#" + cluster);
        if (hasContent() && content != redundantContent) {
            list.add("Content-Mismatch - Original=" + SphereGridLayoutDataObject.byteToNodeContent(redundantContent));
        }
        list.add("U6=" + String.format("%04X", unknown6));
        if (unused3 != 0) {
            list.add("Unknown3=" + String.format("%04X", unused3));
        }
        String full = String.join(", ", list);
        String prefix = hasContent() ? SphereGridLayoutDataObject.byteToNodeContent(content) : ("[" + SphereGridLayoutDataObject.byteToNodeContent(redundantContent) + "]");
        return prefix + " { " + full + " }";
    }

    @Override
    public String getName() {
        return this.toString();
    }

    public void setContent(int content) {
        this.content = content;
    }

    private boolean hasContent() {
        return content != null && content != 0xFF;
    }

    private int read2Bytes(int offset, boolean signed) {
        int raw = bytes[offset] + bytes[offset+1] * 0x100;
        if (signed && (raw & 0x8000) != 0) {
            return raw - 0x10000;
        } else {
            return raw;
        }
    }
}
