package script.model;

import main.DataAccess;
import model.AbilityDataObject;
import model.Nameable;
import script.MonsterFile;
import script.ScriptObject;

import java.util.*;
import java.util.stream.Collectors;

public class StackObject {
    public ScriptObject parentScript;
    public ScriptInstruction parentInstruction;
    public String type;
    public boolean expression;
    public String content;
    public int value;
    public boolean maybeBracketize = false;
    public Integer referenceIndex;

    public StackObject(ScriptObject script, ScriptInstruction instruction, String type, boolean expression, String content, int value) {
        this.parentScript = script;
        this.parentInstruction = instruction;
        this.type = type;
        this.expression = expression;
        this.content = content;
        this.value = value;
    }

    public StackObject(String type, StackObject obj) {
        this.parentScript = obj.parentScript;
        this.parentInstruction = obj.parentInstruction;
        // direct values should never be type-cast to float as the format will just be wrong.
        this.type = (type == null || "unknown".equals(type) || ("float".equals(type) && !obj.expression)) ? obj.type : type;
        this.expression = obj.expression;
        this.content = obj.content;
        this.value = obj.value;
        this.maybeBracketize = obj.maybeBracketize;
        this.referenceIndex = obj.referenceIndex;
    }

    @Override
    public String toString() {
        if (!expression && type != null && !"unknown".equals(type)) {
            String hex = String.format(value >= 0x10000 ? "%08X" : value >= 0x100 ? "%04X" : "%02X", value);
            String hexSuffix = ScriptField.PRINT_WITH_HEX_SUFFIX ? " [" + hex + "h]" : "";
            if ("bool".equals(type)) {
                return (value != 0 ? "true" : "false") + hexSuffix;
            }
            if ("float".equals(type)) {
                return Float.intBitsToFloat(value) + hexSuffix;
            }
            if ("bitfield".equals(type)) {
                return bitfieldToString(null, value) + hexSuffix;
            }
            if (type.startsWith("uint") || type.startsWith("int")) {
                if ("int16".equals(type) && ScriptField.PRINT_WITH_HEX_SUFFIX && hex.length() == 8 && hex.startsWith("FFFF")) {
                    return value + " [" + hex.substring(4) + "h]";
                }
                return value + hexSuffix;
            }
            if ("worker".equals(type)) {
                if (value == -1) {
                    return "<Self>";
                }
                ScriptWorker header = parentScript != null ? parentScript.getWorker(value) : null;
                if (header != null) {
                    return header + hexSuffix;
                } else {
                    return "<w" + hex + ">";
                }
            }
            if ("var".equals(type)) {
                return parentScript != null ? parentScript.getVariableLabel(value) : ("var" + hex);
            }
            if ("encounter".equals(type)) {
                int field = (value & 0xFFFF0000) >> 16;
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
                    return "Switch/Summon:" + ScriptConstants.getEnumMap("playerChar").get(value) + hexSuffix;
                } else {
                    AbilityDataObject ability = DataAccess.getMove(value);
                    return (ability != null ? '"'+ability.getName()+'"' : "????") + hexSuffix;
                }
            } else if ("charMove".equals(type)) {
                AbilityDataObject ability = DataAccess.getMove(value + 0x3000);
                return (ability != null ? '"'+ability.getName()+'"' : "????") + hexSuffix;
            }
            if ("btlActor".equals(type) && value >= 0x1000 && value < 0x2000) {
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
                String noLineBreakString = nullSafeString.replace("\n", "\\n");
                return '"' + noLineBreakString + '"' + hexSuffix;
            }
            Nameable object = DataAccess.getNameableObject(type, value);
            if (object != null) {
                return object.getName() + hexSuffix;
            }
            if (ScriptConstants.ENUMERATIONS.containsKey(type)) {
                if (type.endsWith("Bitfield")) {
                    return bitfieldToString(type, value) + hexSuffix;
                }
                return enumToString(type, value);
            }
        }
        return content;
    }

    private String interpretMenu() {
        int b1 = (value & 0xFF000000) >> 24;
        int b2 = (value & 0x00FF0000) >> 16;
        int b3 = (value & 0x0000FF00) >> 8;
        int b4 = value & 0x000000FF;
        String b1s = b1 != 0x40 ? "b1:" + b1 + '.' : "";
        String inputType = switch (b2) {
            case 0x00 -> "Menu";
            case 0x01 -> "BattleRewards.";
            case 0x02 -> "ItemShop";
            case 0x04 -> "WeaponShop";
            case 0x08 -> "EnterName.";
            case 0x10 -> "LoadGame";
            case 0x20 -> "SaveGame";
            case 0x40 -> "SphereMonitor";
            case 0x80 -> "Tutorial";
            default -> "b2:?" + b2 + ".";
        };
        String b3s = b3 != 0x00 ? "b3:" + b3 + '.' : "";
        String index = "";
        if (b2 == 0x08) {
            if (b4 == 0x20) {
                index = "AirshipPassword";
            } else if (b4 <= 0x11) {
                index = ScriptConstants.getEnumMap("btlActor").get(b4).name;
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
        } else if (b4 != 0x00 || b2 == 0x02 || b2 == 0x04) {
            index = "#" + b4;
        }
        return b1s + inputType + b3s + index;
    }

    public static String enumToString(String type, int value) {
        return enumToScriptField(type, value).toString();
    }

    public static ScriptField enumToScriptField(String type, int value) {
        if (ScriptConstants.ENUMERATIONS.containsKey(type)) {
            Map<Integer, ScriptField> map = ScriptConstants.ENUMERATIONS.get(type);
            ScriptField enumTarget = map.get(value);
            if (enumTarget != null) {
                return enumTarget;
            }
        }
        ScriptField field = new ScriptField(null, ScriptConstants.INDEX_ENUMS_ONLY.contains(type) ? "unknown" : type);
        field.idx = value;
        return field;
    }

    public static List<ScriptField> bitfieldToList(String type, int value) {
        Map<Integer, ScriptField> map = type != null ? ScriptConstants.ENUMERATIONS.getOrDefault(type, Collections.emptyMap()) : Collections.emptyMap();
        List<ScriptField> bits = new ArrayList<>();
        String format = value >= 0x10000 ? "b%08X" : "b%04X";
        for (int bit = 0x01; bit <= value; bit = bit << 1) {
            if ((value & bit) != 0) {
                ScriptField field = map.getOrDefault(bit, new ScriptField(String.format(format, bit), type).withIdx(value));
                bits.add(field);
            }
        }
        return bits;
    }

    public static String bitsToString(List<ScriptField> bits) {
        return "[" + bits.stream().map(ScriptField::getLabel).collect(Collectors.joining(", ")) + "]";
    }

    public static String bitfieldToString(String type, int value) {
        List<ScriptField> bits = bitfieldToList(type, value);
        return bitsToString(bits);
    }
}