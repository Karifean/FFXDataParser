package script.model;

import main.DataAccess;
import model.AbilityDataObject;
import model.KeyItemDataObject;
import model.TreasureDataObject;
import script.MonsterFile;
import script.ScriptObject;

import java.util.Map;

public class StackObject {
    public ScriptObject parentScript;
    public String type;
    public boolean expression;
    public String content;
    public int value;

    public StackObject(ScriptObject script, String type, boolean expression, String content, int value) {
        this.parentScript = script;
        this.type = type;
        this.expression = expression;
        this.content = content;
        this.value = value;
    }

    public StackObject(String type, StackObject obj) {
        this.parentScript = obj.parentScript;
        this.type = "unknown".equals(type) ? obj.type : type;
        this.expression = obj.expression;
        this.content = obj.content;
        this.value = obj.value;
    }

    @Override
    public String toString() {
        if (!expression && !"unknown".equals(type)) {
            String hex = String.format(value >= 0x10000 ? "%08X" : value >= 0x100 ? "%04X" : "%02X", value);
            String hexSuffix = " [" + hex + "h]";
            if ("bool".equals(type)) {
                return (value > 0 ? "true" : "false") + hexSuffix;
            }
            if ("float".equals(type)) {
                return Float.intBitsToFloat(value) + hexSuffix;
            }
            if ("uint".equals(type)) {
                return value + hexSuffix;
            }
            if ("int".equals(type)) {
                int signed = value < 0x8000 ? value : (value - 0x10000);
                return signed + hexSuffix;
            }
            if ("treasure".equals(type)) {
                TreasureDataObject obj = DataAccess.getTreasure(value);
                if (obj != null) {
                    return obj + hexSuffix;
                }
            }
            if ("keyItem".equals(type)) {
                KeyItemDataObject obj = DataAccess.getKeyItem(value);
                if (obj != null) {
                    return obj.getName() + hexSuffix;
                }
            }
            if ("encounter".equals(type)) {
                int field = (value & 0xFFFF0000) / 0x10000;
                int encIdx = value & 0x0000FFFF;
                ScriptField fieldObj = ScriptConstants.getEnumMap("field").get(field);
                if (fieldObj == null) {
                    return '?' + type + ':' + value + hexSuffix;
                } else {
                    return fieldObj.name + '_' + String.format("%02d", encIdx) + hexSuffix;
                }
            }
            if ("menu".equals(type)) {
                return interpretMenu() + hexSuffix;
            }
            if ("move".equals(type)) {
                if (value == 0) {
                    return "Null Move" + hexSuffix;
                } else if (value <= 0x11) {
                    return "Switch/Summon:" + ScriptConstants.getEnumMap("actor").get(value) + hexSuffix;
                } else {
                    AbilityDataObject ability = DataAccess.getMove(value);
                    return (ability != null ? '"'+ability.getName()+'"' : "????") + hexSuffix;
                }
            } else if ("charMove".equals(type)) {
                AbilityDataObject ability = DataAccess.getMove(value + 0x3000);
                return (ability != null ? '"'+ability.getName()+'"' : "????") + hexSuffix;
            }
            if ("actor".equals(type) && value >= 0x1000 && value < 0x2000) {
                try {
                    MonsterFile monster = DataAccess.getMonster(value);
                    if (monster != null) {
                        return "Actors:MonsterType=" + monster.getName() + hexSuffix;
                    }
                } catch (UnsupportedOperationException ignored) {}
            }
            if ("string".equals(type) && parentScript != null && parentScript.strings != null && parentScript.strings.size() > value) {
                String targetString = parentScript.strings.get(value);
                String nullSafeString = targetString != null ? targetString : "null";
                String noLineBreakString = nullSafeString.replace('\n', ' ');
                return '"' + noLineBreakString + '"' + hexSuffix;
            }
            if (ScriptConstants.ENUMERATIONS.containsKey(type)) {
                Map<Integer, ScriptField> map = ScriptConstants.ENUMERATIONS.get(type);
                ScriptField enumTarget = map.get(value);
                if (enumTarget != null) {
                    return enumTarget.toString();
                } else {
                    return '?' + type + ':' + value + hexSuffix;
                }
            }
        }
        return content;
    }

    private String interpretMenu() {
        int b1 = (value & 0xFF000000) / 0x1000000;
        int b2 = (value & 0x00FF0000) / 0x10000;
        int b3 = (value & 0x0000FF00) / 0x100;
        int b4 = value & 0x000000FF;
        String b1s = b1 != 0x40 ? "b1:" + b1 + '.' : "";
        String inputType = switch (b2) {
            case 0x01 -> "BattleRewards.";
            case 0x02 -> "ItemShop";
            case 0x04 -> "WeaponShop";
            case 0x08 -> "EnterName.";
            case 0x20 -> "Saving";
            case 0x80 -> "Tutorial";
            default -> "b2:?" + b2 + ".";
        };
        String b3s = b3 != 0x00 ? "b3:" + b3 + '.' : "";
        String index = "";
        if (b2 == 0x08) {
            if (b4 == 0x20) {
                index = "AirshipPassword";
            } else if (b4 <= 0x11) {
                index = ScriptConstants.getEnumMap("actor").get(b4).name;
            } else {
                index = "?" + b4;
            }
        } else if (b2 == 0x80) {
            index = switch (b4) {
                case 0x01 -> "SphereGrid";
                case 0x03 -> "Customize";
                case 0x04 -> "AeonAbilities";
                case 0x05 -> "AeonAttributes";
                default -> "b4:?" + b4;
            };
        } else if (b4 != 0x00) {
            index = "#" + b4;
        }
        return b1s + inputType + b3s + index;
    }
}