package atel.model;

import atel.BattleFile;
import atel.EventFile;
import main.DataAccess;
import main.StringHelper;
import model.CommandDataObject;
import model.strings.LocalizedMacroStringObject;
import model.Nameable;
import atel.MonsterFile;
import atel.AtelScriptObject;

import java.util.*;
import java.util.stream.Collectors;

import static main.DataReadingManager.DEFAULT_LOCALIZATION;

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

    public StackObject(ScriptWorker worker, ScriptInstruction instruction, String type, boolean expression, String content) {
        this.parentWorker = worker;
        this.parentInstruction = instruction;
        this.type = type;
        this.expression = expression;
        this.content = content;
    }

    public StackObject(ScriptWorker worker, ScriptInstruction instruction, String rawType, int valueSigned, int valueUnsigned) {
        this.parentWorker = worker;
        this.parentInstruction = instruction;
        this.type = "float".equals(rawType) ? "float" : "unknown";
        this.rawType = rawType;
        this.valueSigned = valueSigned;
        this.valueUnsigned = valueUnsigned;
    }

    public StackObject(String type, StackObject obj) {
        this.parentWorker = obj.parentWorker;
        this.parentInstruction = obj.parentInstruction;
        // float type should be preserved, as the format will just be wrong if typecast to something else.
        this.type = (isWeakType(type) || "float".equals(type) || "float".equals(obj.rawType)) ? obj.type : type;
        this.expression = obj.expression;
        this.content = obj.content;
        this.rawType = obj.rawType;
        this.valueSigned = obj.valueSigned;
        this.valueUnsigned = obj.valueUnsigned;
        this.maybeBracketize = obj.maybeBracketize;
        this.referenceIndex = obj.referenceIndex;
    }

    public String asString(ScriptState state) {
        if (expression) {
            return content;
        }
        String localization = state != null && state.localization != null ? state.localization : DEFAULT_LOCALIZATION;
        String valueString = asString(localization, this);
        if (valueString != null) {
            return valueString;
        } else {
            return asString(localization, new StackObject(rawType, this));
        }
    }

    @Override
    public String toString() {
        if (expression) {
            return content;
        }
        String valueString = asString(DEFAULT_LOCALIZATION, this);
        if (valueString != null) {
            return valueString;
        } else {
            return asString(DEFAULT_LOCALIZATION, new StackObject(rawType, this));
        }
    }

    public static boolean isWeakType(String type) {
        return type == null || "unknown".equals(type);
    }

    public static String asString(String localization, StackObject obj) {
        return asString(localization, obj.type, obj.rawType, obj.valueSigned, obj.valueUnsigned, obj.parentWorker, ScriptField.PRINT_WITH_HEX_SUFFIX);
    }

    public static String asString(String localization, String type, int value) {
        return asString(localization, type, null, value, value, null, ScriptField.PRINT_WITH_HEX_SUFFIX);
    }

    public static String asString(String localization, String type, String rawType, int valueSigned, int valueUnsigned, ScriptWorker parentWorker, boolean withHexSuffix) {
        String format = "int32".equals(rawType) ? "%08X" : ((valueSigned & 0xFFFFFF00) != 0 ? "%04X" : "%02X");
        String hex = String.format(format, valueSigned);
        if (!"int32".equals(rawType) && hex.length() == 8 && hex.startsWith("FFFF")) {
            hex = hex.substring(4);
        }
        String hexSuffix = withHexSuffix ? " [" + hex + "h]" : "";
        if (type == null || "unknown".equals(type) || type.startsWith("int")) {
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
        if ("worker".equals(type) || "workerOrSelf".equals(type)) {
            if ("workerOrSelf".equals(type) && valueSigned == -1) {
                return "Self" + hexSuffix;
            }
            AtelScriptObject parentScript = parentWorker != null ? parentWorker.parentScript : null;
            ScriptWorker worker = parentScript != null ? parentScript.getWorker(valueSigned) : null;
            if (worker != null) {
                return worker.getLabel(localization) + hexSuffix;
            } else {
                return "w" + hex + hexSuffix;
            }
        }
        if ("var".equals(type)) {
            return parentWorker != null ? parentWorker.getVariableLabel(valueSigned) : ("var" + hex);
        }
        if ("pointer".equals(type)) {
            ScriptVariable scriptVariable = new ScriptVariable(parentWorker, 0, valueSigned, 1);
            return "&" + scriptVariable.getLabel(parentWorker) + hexSuffix;
        }
        if ("battle".equals(type)) {
            int map = (valueSigned & 0xFFFF0000) >> 16;
            int encIdx = valueSigned & 0x0000FFFF;
            ScriptField mapObj = ScriptConstants.FFX.getEnumMap("map").get(map);
            if (mapObj == null) {
                return '?' + type + ':' + valueSigned + hexSuffix;
            } else {
                String battleId = mapObj.name + '_' + String.format("%02d", encIdx);
                return battleId + hexSuffix;
            }
        }
        if ("menu".equals(type)) {
            return interpretMenu(valueSigned) + hexSuffix;
        }
        if ("sphereGridNodeState".equals(type)) {
            return compositeUint16ToString(localization, "bitfieldFrom_playerChar", "sgNodeType", valueUnsigned, "(Activation: %s, Content: %s)") + hexSuffix;
        }
        if ("command".equals(type)) {
            if (valueSigned == 0) {
                return "Null Command" + hexSuffix;
            } else if (valueSigned <= 0x11) {
                return "Switch/Summon:" + ScriptConstants.FFX.getEnumMap("playerChar").get(valueSigned) + hexSuffix;
            } else {
                CommandDataObject ability = DataAccess.getCommand(valueSigned);
                return (ability != null ? '"'+ability.getName(localization)+'"' : "NullCmd") + hexSuffix;
            }
        } else if ("charCommand".equals(type)) {
            CommandDataObject ability = DataAccess.getCommand(valueSigned + 0x3000);
            return (ability != null ? '"'+ability.getName(localization)+'"' : "NullCmd") + hexSuffix;
        }
        if (("btlChr".equals(type) || "monster".equals(type)) && valueSigned >= 0x1000 && valueSigned < 0x2000) {
            try {
                MonsterFile monster = DataAccess.getMonster(valueSigned);
                if (monster != null) {
                    String prefix = "btlChr".equals(type) ? "Actors:MonsterType=" : "";
                    return prefix + "m" + StringHelper.formatDec3(valueSigned & 0x0FFF) + " (" + monster.getName(localization) + ")" + hexSuffix;
                }
            } catch (UnsupportedOperationException ignored) {}
        }
        if ("macroString".equals(type)) {
            return StringHelper.MACRO_LOOKUP.computeIfAbsent(valueSigned, k -> new LocalizedMacroStringObject()).getLocalizedString(localization);
        }
        if ("system01String".equals(type)) {
            BattleFile system01 = DataAccess.getBattle("system_01");
            if (system01 != null && system01.strings != null && system01.strings.size() > valueSigned) {
                String fieldString = system01.strings.get(valueSigned).getLocalizedString(localization);
                String noLineBreakString = fieldString != null ? fieldString.replace("\n", "{\\n}") : "null";
                return '"' + noLineBreakString + '"' + hexSuffix;
            }
        }
        if ("localString".equals(type)) {
            AtelScriptObject parentScript = parentWorker != null ? parentWorker.parentScript : null;
            if (parentScript != null && parentScript.strings != null && parentScript.strings.size() > valueSigned) {
                String fieldString = parentScript.strings.get(valueSigned).getLocalizedString(localization);
                String noLineBreakString = fieldString != null ? fieldString.replace("\n", "{\\n}") : "null";
                return '"' + noLineBreakString + '"' + hexSuffix;
            }
        }
        if ("room".equals(type)) {
            String roomId = EventFile.getRoomNameById(valueSigned);
            if (roomId != null) {
                EventFile event = DataAccess.getEvent(roomId);
                return (event != null ? event.getName(localization) : (roomId + " (Unknown)")) + hexSuffix;
            } else {
                return "Room#" + valueSigned + hexSuffix;
            }
        }
        if ("blitzballPlayer".equals(type)) {
            if (valueSigned == 0x3C) {
                return "<Empty>";
            }
            if (StringHelper.MACRO_LOOKUP.containsKey(0x700 + valueSigned)) {
                String str = StringHelper.MACRO_LOOKUP.get(0x700 + valueSigned).getLocalizedString(localization);
                return "\"" + str + "\"" + hexSuffix;
            } else {
                return enumToString(type, valueSigned);
            }
        }
        if ("model".equals(type)) {
            ScriptField enumValue = enumToScriptField(type, valueSigned);
            if (valueSigned == 0) {
                return enumValue.toString();
            }
            String filePrefix = getModelFilePrefix(valueSigned);
            if (filePrefix != null) {
                String enumLabel = enumValue.getName(localization) != null ? " (" + enumValue.getLabel() + ")" : "";
                return filePrefix + StringHelper.formatDec3(valueSigned & 0x0FFF) + enumLabel + hexSuffix;
            } else {
                return enumValue.toString();
            }
        }
        if ("voiceFile".equals(type)) {
            int fileNumber = valueSigned >> 12;
            int firstLetterIndex = (valueSigned >> 6) & 0x3F;
            int secondLetterIndex = valueSigned & 0x3F;
            String tag = StringHelper.toLetter(firstLetterIndex) + "" + StringHelper.toLetter(secondLetterIndex);
            return fileNumber + tag + hexSuffix;
        }
        if ("magicFile".equals(type)) {
            if (valueSigned == 0) {
                return "None" + hexSuffix;
            }
            return String.format("magic_%04d", valueSigned) + hexSuffix;
        }
        if ("btlScene".equals(type)) {
            return String.format("btlScene%02X", valueSigned) + hexSuffix;
        }
        if ("blitzTech".equals(type) || "blitzTechP1".equals(type)) {
            return StringHelper.MACRO_LOOKUP.get(0x800 + valueSigned).getLocalizedString(localization) + hexSuffix;
        } else if ("blitzTechP2".equals(type)) {
            return StringHelper.MACRO_LOOKUP.get(0x81E + valueSigned).getLocalizedString(localization) + hexSuffix;
        }
        Nameable object = DataAccess.getNameableObject(type, valueSigned);
        if (object != null) {
            return object.getName(localization) + hexSuffix;
        }
        if (type.startsWith("bitfieldFrom_")) {
            String enumType = type.substring(13);
            return "[" + bitfieldToIntList(valueUnsigned).stream().map(i -> StackObject.asString(localization, enumType, i)).collect(Collectors.joining(", ")) + "]" + hexSuffix;
        }
        if (type.endsWith("Bitfield")) {
            return bitfieldToString(type, valueUnsigned) + hexSuffix;
        } else if (type.endsWith("BitfieldNegated")) {
            return negatedBitfieldToString(type.substring(0, type.length() - 7), valueUnsigned) + hexSuffix;
        }
        if (ScriptConstants.FFX.ENUMERATIONS.containsKey(type)) {
            return enumToString(type, valueSigned);
        }
        return null;
    }

    public static String compositeUint16ToString(String localization, String hbType, String lbType, int value, String format) {
        String hbStr = StackObject.asString(localization, hbType, (value & 0xFF00) >> 8);
        String lbStr = StackObject.asString(localization, lbType, value & 0x00FF);
        return String.format(format, hbStr, lbStr);
    }

    private static String interpretMenu(int value) {
        int hb = (value & 0xFFFF0000) >> 16;
        int lb = value & 0x0000FFFF;
        String hbs = switch (hb) {
            case 0x4000 -> "Menu";
            case 0x4001 -> "BattleRewards";
            case 0x4002 -> "ItemShop";
            case 0x4004 -> "WeaponShop";
            case 0x4008 -> "EnterName";
            case 0x4010 -> "LoadGame";
            case 0x4020 -> "SaveGame";
            case 0x4040 -> "SphereMonitor";
            case 0x4080 -> "Tutorial";
            default -> "hb:?" + hb + ".";
        };
        String lbs;
        if (hb == 0x4008) {
            if (lb == 0x20) {
                lbs = "_AirshipPassword";
            } else if (lb <= 0x11) {
                lbs = "_" + ScriptConstants.FFX.getEnumMap("playerChar").get(lb).name;
            } else {
                lbs = "?" + lb;
            }
        } else if (hb == 0x4080) {
            lbs = switch (lb) {
                case 0x01 -> "_SphereGrid";
                case 0x02 -> "_SphereGridLockNodes";
                case 0x03 -> "_Customize";
                case 0x04 -> "_AeonAbilities";
                case 0x05 -> "_AeonAttributes";
                default -> "?" + lb;
            };
        } else if (lb != 0x00 || hb == 0x4002 || hb == 0x4004) {
            lbs = "#" + lb;
        } else {
            lbs = "";
        }
        return hbs + lbs;
    }

    public String asIntValue(boolean withHexSuffix) {
        if (!withHexSuffix) {
            return String.valueOf(valueSigned);
        }
        String format = "int32".equals(rawType) ? "%08X" : ((valueSigned & 0xFFFFFF00) != 0 ? "%04X" : "%02X");
        String hex = String.format(format, valueSigned);
        if (!"int32".equals(rawType) && hex.length() == 8 && hex.startsWith("FFFF")) {
            hex = hex.substring(4);
        }
        return valueSigned + " [" + hex + "h]";
    }

    public static String enumToString(String type, int value) {
        return enumToScriptField(type, value).toString();
    }

    public static ScriptField enumToScriptField(String type, int value) {
        if (ScriptConstants.FFX.ENUMERATIONS.containsKey(type)) {
            Map<Integer, ScriptField> map = ScriptConstants.FFX.ENUMERATIONS.get(type);
            ScriptField enumTarget = map.get(value);
            if (enumTarget != null) {
                return enumTarget;
            }
        }
        ScriptField field = new ScriptField(null, ScriptConstants.INDEX_ENUMS_ONLY.contains(type) ? "unknown" : type);
        field.idx = value;
        return field;
    }

    public static List<Integer> bitfieldToIntList(int value) {
        List<Integer> bits = new ArrayList<>();
        if (value == 0) {
            return bits;
        }
        int max = (value & 0xFFFF0000) != 0 ? 0x20 : 0x10;
        for (int i = 0; i < max; i++) {
            int bit = 1 << i;
            if ((value & bit) != 0) {
                bits.add(i);
            }
        }
        return bits;
    }

    public static List<ScriptField> bitfieldToList(String type, int value) {
        List<ScriptField> bits = new ArrayList<>();
        if (value == 0) {
            return bits;
        }
        Map<Integer, ScriptField> map = type != null ? ScriptConstants.FFX.ENUMERATIONS.getOrDefault(type, Collections.emptyMap()) : Collections.emptyMap();
        String format = (value & 0xFFFF0000) != 0 ? "b%08X" : "b%04X";
        for (int bit = 0x01; bit <= value && bit > 0; bit = bit << 1) {
            if ((value & bit) != 0) {
                ScriptField field = map.getOrDefault(bit, new ScriptField(String.format(format, bit), type, null, value));
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
        Map<Integer, ScriptField> map = type != null ? ScriptConstants.FFX.ENUMERATIONS.getOrDefault(type, Collections.emptyMap()) : Collections.emptyMap();
        String format = (valueUnsigned & 0xFFFF0000) != 0 ? "b%08X" : "b%04X";
        int max = (valueUnsigned & 0xFFFF0000) != 0 ? 0x80000000 : 0x8000;
        for (int bit = 0x01; true; bit = bit << 1) {
            if ((valueUnsigned & bit) == 0) {
                ScriptField field = map.getOrDefault(bit, new ScriptField(String.format(format, bit), type, null, valueUnsigned));
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

    public static String getModelFilePrefix(int value) {
        return switch (value >> 12) {
            case 0 -> "pc/c";
            case 1 -> "mon/m";
            case 2 -> "npc/n";
            case 3 -> "sum/s";
            case 4 -> "wep/w";
            case 5 -> "obj/f";
            case 6 -> "skl/k";
            default -> null;
        };
    }
}