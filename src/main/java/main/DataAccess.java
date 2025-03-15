package main;

import atel.AtelScriptObject;
import model.*;
import model.spheregrid.SphereGridLayoutDataObject;
import model.spheregrid.SphereGridNodeTypeDataObject;
import model.spheregrid.SphereGridSphereTypeDataObject;
import atel.EncounterFile;
import atel.EventFile;
import atel.MonsterFile;

import java.util.HashMap;
import java.util.Map;

public abstract class DataAccess {
    public static CommandDataObject[] MOVES = new CommandDataObject[0x10000];
    public static Map<String, EventFile> EVENTS = new HashMap<>();
    public static Map<String, EncounterFile> ENCOUNTERS = new HashMap<>();
    public static MonsterFile[] MONSTERS = new MonsterFile[0x1000];
    public static AtelScriptObject MENUMAIN;
    public static PlayerCharStatDataObject[] PLAYER_CHAR_STATS;
    public static PlayerRomDataObject[] PLAYER_ROM;
    public static WeaponNameDataObject[] WEAPON_NAMES;
    public static GearAbilityDataObject[] GEAR_ABILITIES;
    public static KeyItemDataObject[] KEY_ITEMS;
    public static TreasureDataObject[] TREASURES;
    public static MixCombinationDataObject[] MIX_COMBINATIONS;
    public static GearDataObject[] WEAPON_PICKUPS;
    public static GearDataObject[] BUYABLE_GEAR;
    public static GearShopDataObject[] GEAR_SHOPS;
    public static ItemShopDataObject[] ITEM_SHOPS;
    public static SphereGridNodeTypeDataObject[] SG_NODE_TYPES;
    public static SphereGridSphereTypeDataObject[] SG_SPHERE_TYPES;
    public static SphereGridLayoutDataObject OSG_LAYOUT;
    public static SphereGridLayoutDataObject SSG_LAYOUT;
    public static SphereGridLayoutDataObject ESG_LAYOUT;
    public static CustomizationDataObject[] GEAR_CUSTOMIZATIONS;
    public static CustomizationDataObject[] AEON_CUSTOMIZATIONS;

    public static NameDescriptionTextObject[] ARMS_TEXT;
    // public static NameDescriptionTextObject[] BTL_TEXT;
    public static NameDescriptionTextObject[] BTLEND_TEXT;
    public static NameDescriptionTextObject[] BUILD_TEXT;
    public static NameDescriptionTextObject[] CONFIG_TEXT;
    public static NameDescriptionTextObject[] ITEM_TEXT;
    public static NameDescriptionTextObject[] MENU_TEXT;
    public static NameDescriptionTextObject[] MMAIN_TEXT;
    public static NameDescriptionTextObject[] NAME_TEXT;
    public static NameDescriptionTextObject[] SAVE_TEXT;
    public static NameDescriptionTextObject[] STATS_TEXT;
    public static NameDescriptionTextObject[] SUMMON_TEXT;

    private final static Nameable DUMMY_OBJECT = (l) -> "null";

    public static Nameable getNameableObject(String type, int idx) {
        Nameable object = switch (type) {
            case "move" -> getCommand(idx);
            case "monster" -> getMonster(idx);
            case "keyItem" -> getKeyItem(idx);
            case "treasure" -> getTreasure(idx);
            case "sgNodeType" -> getSgNodeType(idx);
            default -> DUMMY_OBJECT;
        };
        if (object == DUMMY_OBJECT) {
            return null;
        }
        if (object == null) {
            return DUMMY_OBJECT;
        }
        return object;
    }

    public static EventFile getEvent(String id) {
        return EVENTS.get(id);
    }

    public static EncounterFile getEncounter(String id) {
        return ENCOUNTERS.get(id);
    }

    public static CommandDataObject getCommand(int idx) {
        if (MOVES == null) {
            throw new UnsupportedOperationException();
        }
        return MOVES[idx];
    }

    public static GearAbilityDataObject getGearAbility(int idx) {
        if (idx == 0x00FF) {
            return null;
        }
        if (GEAR_ABILITIES == null) {
            throw new UnsupportedOperationException();
        }
        int actual = idx - 0x8000;
        if (actual >= 0 && actual < GEAR_ABILITIES.length) {
            return GEAR_ABILITIES[actual];
        } else {
            return null;
        }
    }

    public static SphereGridNodeTypeDataObject getSgNodeType(int idx) {
        if (SG_NODE_TYPES == null) {
            throw new UnsupportedOperationException();
        }
        if (idx >= 0 && idx < SG_NODE_TYPES.length) {
            return SG_NODE_TYPES[idx];
        } else {
            return null;
        }
    }

    public static KeyItemDataObject getKeyItem(int idx) {
        if (KEY_ITEMS == null) {
            throw new UnsupportedOperationException();
        }
        int actual = idx - 0xA000;
        if (actual >= 0 && actual < KEY_ITEMS.length) {
            return KEY_ITEMS[actual];
        } else {
            return null;
        }
    }

    public static TreasureDataObject getTreasure(int idx) {
        if (TREASURES == null) {
            throw new UnsupportedOperationException();
        }
        return TREASURES[idx];
    }

    public static void addMonsterLocalizations(MonsterStatDataObject[] localizations) {
        if (localizations == null) {
            return;
        }
        for (int i = 0; i < localizations.length && i < MONSTERS.length; i++) {
            if (MONSTERS[i] != null) {
                MONSTERS[i].monsterStatData.setLocalizations(localizations[i]);
            }
        }
    }

    public static MonsterFile getMonster(int idx) {
        if (MONSTERS == null) {
            throw new UnsupportedOperationException();
        }
        int actual = idx - 0x1000;
        if (actual >= 0 && actual < MONSTERS.length) {
            return MONSTERS[actual];
        } else {
            return null;
        }
    }
}
