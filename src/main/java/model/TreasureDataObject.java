package model;

import main.DataAccess;
import main.Main;

public class TreasureDataObject {
    private int[] bytes;

    private int kind;
    private int quantity;
    private int typeLow;
    private int type;

    public TreasureDataObject() {}

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
        if (kind == 0x02) {
            return "Item: " + quantity + "x " + Main.getAbility(type).name;
        } else if (kind == 0x00) {
            return "Gil: " + quantity * 100 + (type != 0 ? unexpectedTypeSuffix() : "");
        } else if (kind == 0x05) {
            GearDataObject obj = DataAccess.WEAPON_PICKUPS != null ? DataAccess.WEAPON_PICKUPS[type] : null;
            return "Gear: buki_get #" + type + (quantity != 1 ? " Q=" + quantity : "") + " " + obj;
        } else if (kind == 0x0A) {
            KeyItemDataObject obj = DataAccess.KEY_ITEMS != null ? DataAccess.KEY_ITEMS[typeLow] : null;
            return "Key Item: #" + typeLow + ' ' + (obj != null ? obj.getName() : "invalid");
        } else {
            return "Unknown K=" + kind + "; Q=" + quantity + ';' + unexpectedTypeSuffix();
        }
    }

    private String unexpectedTypeSuffix() {
        return " T=" + type + " [" + String.format("%02x", type) + "h]";
    }
}
