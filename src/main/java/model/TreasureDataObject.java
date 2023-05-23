package model;

import main.DataAccess;

/**
 * takara.bin
 */
public class TreasureDataObject {
    private final int[] bytes;

    private int kind;
    private int quantity;
    private int typeLow;
    private int type;

    public TreasureDataObject(int[] bytes) {
        this.bytes = bytes;
        mapBytes();
    }

    private void mapBytes() {
        kind = bytes[0x00];
        quantity = bytes[0x01];
        typeLow = bytes[0x02];
        type = bytes[0x03] * 0x100 + typeLow;
    }

    @Override
    public String toString() {
        String typeString = type + " [" + String.format("%02X", type) + "h]";
        if (kind == 0x02) {
            return "Item: " + quantity + "x " + DataAccess.getMove(type).name + " [" + String.format("%04X", type) + "h]";
        } else if (kind == 0x00) {
            return "Gil: " + quantity * 100 + (type != 0 ? "T=" + typeString : "") + " [" + String.format("%02X", quantity) + "h]";
        } else if (kind == 0x05) {
            GearDataObject obj = DataAccess.WEAPON_PICKUPS != null ? DataAccess.WEAPON_PICKUPS[type] : null;
            return "Gear: buki_get #" + typeString + (quantity != 1 ? " Q=" + quantity : "") + " " + obj;
        } else if (kind == 0x0A) {
            KeyItemDataObject obj = DataAccess.getKeyItem(type);
            return "Key Item: #" + typeLow + " [" + String.format("%04X", type) + "h]" + ' ' + (obj != null ? obj.getName() : "invalid");
        } else {
            return "Unknown K=" + kind + "; Q=" + quantity + "; T=" + typeString;
        }
    }
}
