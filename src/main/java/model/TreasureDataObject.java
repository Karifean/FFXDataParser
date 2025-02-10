package model;

import main.DataAccess;
import main.StringHelper;

/**
 * takara.bin
 */
public class TreasureDataObject implements Nameable {
    private final int[] bytes;

    private int kind;
    private int quantity;
    private int type;

    public TreasureDataObject(int[] bytes, int[] stringBytes) {
        this.bytes = bytes;
        mapBytes();
    }

    public TreasureDataObject(int[] bytes) {
        this(bytes, null);
    }

    private void mapBytes() {
        kind = bytes[0x00];
        quantity = bytes[0x01];
        type = bytes[0x02] + bytes[0x03] * 0x100;
    }

    @Override
    public String toString() {
        String typeHexSuffix = StringHelper.hex2Suffix(type);
        String typeString = type + typeHexSuffix;
        if (kind == 0x02) {
            return "Item: " + quantity + "x " + DataAccess.getMove(type).name + typeHexSuffix;
        } else if (kind == 0x00) {
            return "Gil: " + quantity * 100 + (type != 0 ? "T=" + typeString : "") + StringHelper.hex2Suffix(quantity);
        } else if (kind == 0x05) {
            GearDataObject gear = DataAccess.WEAPON_PICKUPS != null ? DataAccess.WEAPON_PICKUPS[type] : null;
            return "Gear: buki_get #" + typeString + (quantity != 1 ? " Q=" + quantity : "") + " " + (gear != null ? gear.compactString() : "null");
        } else if (kind == 0x0A) {
            KeyItemDataObject keyItem = DataAccess.getKeyItem(type);
            return "Key Item: " + (keyItem != null ? keyItem.getName() : "null") + typeHexSuffix;
        } else {
            return "Unknown K=" + kind + "; Q=" + quantity + "; T=" + typeString;
        }
    }

    @Override
    public String getName(String localization) {
        return this.toString();
    }
}
