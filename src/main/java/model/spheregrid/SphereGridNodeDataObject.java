package model.spheregrid;

import main.DataAccess;
import main.StringHelper;
import model.Nameable;

import java.util.ArrayList;
import java.util.List;

/**
 * Part of SphereGridLayoutDataObject
 */
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
            list.add("Content-Mismatch - Original=" + getContentLabel(redundantContent));
        }
        list.add("U6=" + StringHelper.formatHex4(unknown6) + "=" + unknown6);
        if (unused3 != 0) {
            list.add("Unknown3=" + StringHelper.formatHex4(unused3));
        }
        String full = String.join(", ", list);
        String prefix = hasContent() ? getContentLabel(content) : ("[" + getContentLabel(redundantContent) + "]");
        return prefix + " { " + full + " }";
    }

    @Override
    public String getName(String localization) {
        return this.toString();
    }

    public void setContent(int content) {
        this.content = content;
    }

    private boolean hasContent() {
        return content != null && content != 0xFF;
    }

    private String getContentLabel(int content) {
        SphereGridNodeTypeDataObject nodeType = DataAccess.getSgNodeType(content);
        String label = nodeType != null ? nodeType.getName() : "null";
        return label + " [" + String.format(content >= 0x100 ? "%04X" : "%02X", content) + "h]";
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
