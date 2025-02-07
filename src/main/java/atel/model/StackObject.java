package atel.model;

import atel.EncounterFile;
import atel.EventFile;
import main.DataAccess;
import main.StringHelper;
import model.AbilityDataObject;
import model.LocalizedStringObject;
import model.Nameable;
import atel.MonsterFile;
import atel.AtelScriptObject;

import java.util.*;
import java.util.stream.Collectors;

public class StackObject {
    private static final boolean UNWRAP_SINGLE_NEGATED_BIT = false;

    public ScriptWorker parentWorker;
    public ScriptInstruction parentInstruction;
    public String type;
    public boolean expression;
    public String content;
    public int valueSigned;
    public int valueUnsigned;
    public boolean maybeBracketize = false;
    public Integer referenceIndex;
    public String rawType;

    public StackObject(ScriptWorker worker, ScriptInstruction instruction, String type, boolean expression, String content, int value) {
        this.parentWorker = worker;
        this.parentInstruction = instruction;
        this.type = type;
        this.expression = expression;
        this.content = content;
        this.valueSigned = value;
    }

    public StackObject(ScriptWorker worker, ScriptInstruction instruction, String rawType, int valueSigned, int valueUnsigned) {
        this.parentWorker = worker;
        this.parentInstruction = instruction;
        this.type = rawType;
        this.rawType = rawType;
        this.valueSigned = valueSigned;
        this.valueUnsigned = valueUnsigned;
    }

    public StackObject(String type, StackObject obj) {
        this.parentWorker = obj.parentWorker;
        this.parentInstruction = obj.parentInstruction;
        // direct values should never be type-cast to float as the format will just be wrong.
        this.type = (!isValidType(type) || ("float".equals(type) && !obj.expression)) ? obj.type : type;
        this.expression = obj.expression;
        this.content = obj.content;
        this.rawType = obj.rawType;
        this.valueSigned = obj.valueSigned;
        this.valueUnsigned = obj.valueUnsigned;
        this.maybeBracketize = obj.maybeBracketize;
        this.referenceIndex = obj.referenceIndex;
    }

    @Override
    public String toString() {
        if (expression) {
            return content;
        }
        String valueString = asString(this);
        if (valueString != null) {
            return valueString;
        } else {
            return asString(new StackObject(rawType, this));
        }
    }

    public static boolean isValidType(String type) {
        return type != null && !"unknown".equals(type);
    }

    public static String asString(StackObject obj) {
        return asString(obj.type, obj.rawType, obj.valueSigned, obj.valueUnsigned, obj.parentWorker);
    }

    public static String asString(String type, int value) {
        return asString(type, null, value, value, null);
    }

    public static String asString(String type, String rawType, int valueSigned, int valueUnsigned, ScriptWorker parentWorker) {
        if (!isValidType(type)) {
            return null;
        }
        String format = "int32".equals(rawType) ? "%08X" : (valueSigned >= 0x100 ? "%04X" : "%02X");
        String hex = String.format(format, valueSigned);
        if (!"int32".equals(rawType) && hex.length() == 8 && hex.startsWith("FFFF")) {
            hex = hex.substring(4);
        }
        String hexSuffix = ScriptField.PRINT_WITH_HEX_SUFFIX ? " [" + hex + "h]" : "";
        if (type.startsWith("int")) {
            return valueSigned + hexSuffix;
        }
        if (type.startsWith("uint")) {
            return valueUnsigned + hexSuffix;
        }
        if ("bool".equals(type)) {
            return (valueSigned != 0 ? "true" : "false") + hexSuffix;
        }
        if ("float".equals(type)) {
            return Float.intBitsToFloat(valueSigned) + hexSuffix;
        }
        if ("bitfield".equals(type)) {
            return bitfieldToString(null, valueUnsigned) + hexSuffix;
        }
        if ("bitfieldNegated".equals(type)) {
            return negatedBitfieldToString(null, valueUnsigned) + hexSuffix;
        }
        if ("worker".equals(type)) {
            if (valueSigned == -1) {
                return "<Self>" + hexSuffix;
            }
            AtelScriptObject parentScript = parentWorker != null ? parentWorker.parentScript : null;
            ScriptWorker header = parentScript != null ? parentScript.getWorker(valueSigned) : null;
            if (header != null) {
                return header + hexSuffix;
            } else {
                return "<w" + hex + ">" + hexSuffix;
            }
        }
        if ("var".equals(type)) {
            AtelScriptObject parentScript = parentWorker != null ? parentWorker.parentScript : null;
            return parentScript != null ? parentScript.getVariableLabel(valueSigned) : ("var" + hex);
        }
        if ("pointer".equals(type)) {
            ScriptVariable scriptVariable = new ScriptVariable(parentWorker, 0, valueSigned, 1);
            return "*" + scriptVariable.getDereference() + hexSuffix;
        }
        if ("encounter".equals(type)) {
            int field = (valueSigned & 0xFFFF0000) >> 16;
            int encIdx = valueSigned & 0x0000FFFF;
            ScriptField fieldObj = ScriptConstants.getEnumMap("field").get(field);
            if (fieldObj == null) {
                return '?' + type + ':' + valueSigned + hexSuffix;
            } else {
                return fieldObj.name + '_' + String.format("%02d", encIdx) + hexSuffix;
            }
        }
        if ("menu".equals(type)) {
            return interpretMenu(valueSigned) + hexSuffix;
        }
        if ("sphereGridNodeState".equals(type)) {
            return compositeUint16ToString("sgNodeActivationBitfield", "sgNodeType", valueUnsigned, "(Activation: %s, Content: %s)") + hexSuffix;
        }
        if ("move".equals(type)) {
            if (valueSigned == 0) {
                return "Null Move" + hexSuffix;
            } else if (valueSigned <= 0x11) {
                return "Switch/Summon:" + ScriptConstants.getEnumMap("playerChar").get(valueSigned) + hexSuffix;
            } else {
                AbilityDataObject ability = DataAccess.getMove(valueSigned);
                return (ability != null ? '"'+ability.getName()+'"' : "????") + hexSuffix;
            }
        } else if ("charMove".equals(type)) {
            AbilityDataObject ability = DataAccess.getMove(valueSigned + 0x3000);
            return (ability != null ? '"'+ability.getName()+'"' : "????") + hexSuffix;
        }
        if ("btlActor".equals(type) && valueSigned >= 0x1000 && valueSigned < 0x2000) {
            try {
                MonsterFile monster = DataAccess.getMonster(valueSigned);
                if (monster != null) {
                    return "Actors:MonsterType=m" + String.format("%03d", valueSigned - 0x1000) + " (" + monster.getName() + ")" + hexSuffix;
                }
            } catch (UnsupportedOperationException ignored) {}
        }
        if ("macroString".equals(type)) {
            return StringHelper.MACRO_LOOKUP.computeIfAbsent(valueSigned, k -> new LocalizedStringObject()).getDefaultContent();
        }
        if ("system01String".equals(type)) {
            EncounterFile system01 = DataAccess.getEncounter("system_01");
            if (system01 != null && system01.strings != null && system01.strings.size() > valueSigned) {
                String targetString = system01.strings.get(valueSigned).getDefaultContent();
                String nullSafeString = targetString != null ? targetString : "null";
                String noLineBreakString = nullSafeString.replace("\n", "\\n");
                return '"' + noLineBreakString + '"' + hexSuffix;
            }
        }
        if ("localString".equals(type)) {
            AtelScriptObject parentScript = parentWorker != null ? parentWorker.parentScript : null;
            if (parentScript != null && parentScript.strings != null && parentScript.strings.size() > valueSigned) {
                String targetString = parentScript.strings.get(valueSigned).getDefaultContent();
                String nullSafeString = targetString != null ? targetString : "null";
                String noLineBreakString = nullSafeString.replace("\n", "\\n");
                return '"' + noLineBreakString + '"' + hexSuffix;
            }
        }
        if ("map".equals(type)) {
            String mapName = EventFile.getMapNameById(valueSigned);
            if (mapName != null) {
                EventFile event = DataAccess.getEvent(mapName);
                return mapName + (event != null ? " (" + event.getName() + ")" : "") + hexSuffix;
            } else {
                return "Map#" + valueSigned + hexSuffix;
            }
        }
        Nameable object = DataAccess.getNameableObject(type, valueSigned);
        if (object != null) {
            return object.getName() + hexSuffix;
        }
        if (type.endsWith("Bitfield")) {
            return bitfieldToString(type, valueUnsigned) + hexSuffix;
        } else if (type.endsWith("BitfieldNegated")) {
            return negatedBitfieldToString(type.substring(0, type.length() - 7), valueUnsigned) + hexSuffix;
        }
        if (ScriptConstants.ENUMERATIONS.containsKey(type)) {
            return enumToString(type, valueSigned);
        }
        return null;
    }

    public static String compositeUint16ToString(String hbType, String lbType, int value, String format) {
        String hbStr = StackObject.asString(hbType, (value & 0xFF00) >> 8);
        String lbStr = StackObject.asString(lbType, value & 0x00FF);
        return String.format(format, hbStr, lbStr);
    }

    private static String interpretMenu(int value) {
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
                index = ScriptConstants.getEnumMap("playerChar").get(b4).name;
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
        List<ScriptField> bits = new ArrayList<>();
        if (value <= 0) {
            return bits;
        }
        Map<Integer, ScriptField> map = type != null ? ScriptConstants.ENUMERATIONS.getOrDefault(type, Collections.emptyMap()) : Collections.emptyMap();
        String format = value >= 0x10000 ? "b%08X" : "b%04X";
        for (int bit = 0x01; bit <= value && bit > 0; bit = bit << 1) {
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

    public static String bitfieldToString(String type, int valueUnsigned) {
        List<ScriptField> bits = bitfieldToList(type, valueUnsigned);
        return bitsToString(bits);
    }

    public static List<ScriptField> negatedBitfieldToList(String type, int valueUnsigned) {
        List<ScriptField> bits = new ArrayList<>();
        Map<Integer, ScriptField> map = type != null ? ScriptConstants.ENUMERATIONS.getOrDefault(type, Collections.emptyMap()) : Collections.emptyMap();
        String format = valueUnsigned >= 0x10000 ? "b%08X" : "b%04X";
        int max = valueUnsigned >= 0x10000 ? 0x80000000 : 0x8000;
        for (int bit = 0x01; true; bit = bit << 1) {
            if ((valueUnsigned & bit) == 0) {
                ScriptField field = map.getOrDefault(bit, new ScriptField(String.format(format, bit), type).withIdx(valueUnsigned));
                bits.add(field);
            }
            if (bit == max) {
                break;
            }
        }
        return bits;
    }

    public static String negatedBitsToString(List<ScriptField> bits) {
        if (bits.isEmpty()) {
            return "~[]";
        } else if (bits.size() == 1 && StackObject.UNWRAP_SINGLE_NEGATED_BIT) {
            return "~" + bits.get(0).getLabel();
        } else {
            return "~[" + bits.stream().map(ScriptField::getLabel).collect(Collectors.joining(", ")) + "]";
        }
    }

    public static String negatedBitfieldToString(String type, int valueUnsigned) {
        List<ScriptField> bits = negatedBitfieldToList(type, valueUnsigned);
        return negatedBitsToString(bits);
    }
}