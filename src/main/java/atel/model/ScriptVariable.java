package atel.model;

import main.StringHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ScriptVariable {
    public static final int SAVEDATA_ATEL_OFFSET = 0x1EC;
    public final int index;
    public final long fullBytes;
    public final int lb;
    public final int hb;
    public final int offset;
    public final int format;
    public final int location;
    public final int elementCount;
    public final int elementSize;
    public final List<StackObject> values = new ArrayList<>();

    public ScriptWorker parentWorker;
    public String inferredType = "unknown";

    public ScriptVariable(ScriptWorker parentWorker, int index, int lb, int hb) {
        this.parentWorker = parentWorker;
        this.index = index;
        this.lb = lb;
        this.hb = hb;
        this.fullBytes = hb * 0x100000000L + lb;
        this.offset = lb & 0x00FFFFFF;
        this.format = (lb & 0xF0000000) >> 28;
        this.location = (lb & 0x0F000000) >> 25;
        this.elementCount = hb & 0xFFFF;
        this.elementSize = (hb & 0xFFFF0000) >> 16;
    }

    public ScriptVariable(ScriptVariable vr) {
        this.parentWorker = vr.parentWorker;
        this.inferredType = vr.inferredType;
        this.index = vr.index;
        this.lb = vr.lb;
        this.hb = vr.hb;
        this.fullBytes = vr.fullBytes;
        this.offset = vr.offset;
        this.format = vr.format;
        this.location = vr.location;
        this.elementCount = vr.elementCount;
        this.elementSize = vr.elementSize;
    }

    public int getLength() {
        return format < 2 ? 1 : (format < 4 ? 2 : 4);
    }

    public int getDataOffset() {
        if (location == 3) {
            return parentWorker.privateDataOffset;
        } else if (location == 4) {
            return parentWorker.sharedDataOffset;
        } else if (location == 6) {
            return parentWorker.parentScript.eventDataOffset;
        }
        return -1;
    }

    public void inferType(String type) {
        if (isWeakType(inferredType) && !"var".equals(type)) {
            inferredType = type;
        }
    }

    public void parseValues() {
        if (location != 3 && location != 4 && location != 6) {
            return;
        }
        int dataOffset = getDataOffset();
        int[] bytes = parentWorker.parentScript.getBytes();
        int valueLocation = dataOffset + offset;
        int length = getLength();
        if (bytes.length < valueLocation + length) {
            return;
        }
        for (int i = 0; i < elementCount; i++) {
            String type = formatToType(format);
            int valueUnsigned = 0;
            if (dataOffset > 0) {
                int valueOffset = valueLocation + i * length;
                valueUnsigned |= bytes[valueOffset];
                if (length > 1) {
                    valueUnsigned |= bytes[valueOffset + 1] << 8;
                    if (length > 2) {
                        valueUnsigned |= (bytes[valueOffset + 2] << 16);
                        valueUnsigned |= (bytes[valueOffset + 3] << 24);
                    }
                }
            }
            int valueSigned;
            if ("int16".equals(type) && (valueUnsigned & 0x8000) != 0) {
                valueSigned = valueUnsigned - 0x10000;
            } else if ("int8".equals(type) && (valueUnsigned & 0x80) != 0) {
                valueSigned = valueUnsigned - 0x100;
            } else {
                valueSigned = valueUnsigned;
            }
            StackObject obj = new StackObject(parentWorker, null, type, valueSigned, valueUnsigned);
            values.add(obj);
        }
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        list.add(getDereference());
        list.add("type=" + fullTypeString());
        list.add(valuesString());
        String full = list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining(", "));
        return getLabel(null) + " { " + full + " }";
    }

    private String fullTypeString() {
        String rawType = formatToType(format);
        String rawTypeSuffix = !rawType.equals(inferredType) ? " (" + rawType + ")" : "";
        if (elementCount <= 1) {
            return inferredType + rawTypeSuffix;
        }
        String elements = elementSize > 1 ? elementCount + "=" + (elementCount / elementSize) + "*" + elementSize + "bytes" : ""+elementCount;
        String arrayIndex = "[" + elements + "]";
        return inferredType + arrayIndex + rawTypeSuffix;
    }

    public String getLabel(ScriptWorker worker) {
        if (location == 0) {
            ScriptField scriptField = StackObject.enumToScriptField("saveData", offset + SAVEDATA_ATEL_OFFSET);
            if (scriptField.name != null) {
                return scriptField.name;
            }
        }
        if (location == 1) {
            ScriptField scriptField = StackObject.enumToScriptField("battleVar", offset);
            if (scriptField.name != null) {
                return scriptField.name;
            }
        }
        return getVarLabel(worker);
    }

    public String getArrayIndexType() {
        if (location == 0) {
            ScriptField scriptField = StackObject.enumToScriptField("saveData", offset + SAVEDATA_ATEL_OFFSET);
            if (scriptField.indexType != null) {
                return scriptField.indexType;
            }
        }
        if (location == 1) {
            ScriptField scriptField = StackObject.enumToScriptField("battleVar", offset);
            if (scriptField.indexType != null) {
                return scriptField.indexType;
            }
        }
        return "unknown";
    }

    public String getType() {
        if (location == 0) {
            ScriptField enumTarget = ScriptConstants.ENUMERATIONS.get("saveData").get(offset + SAVEDATA_ATEL_OFFSET);
            if (enumTarget != null) {
                return enumTarget.type;
            }
        }
        if (location == 1) {
            ScriptField enumTarget = ScriptConstants.getEnumMap("battleVar").get(offset);
            if (enumTarget != null) {
                return enumTarget.type;
            }
        }
        return formatToType(format);
    }

    public String getVarLabel(ScriptWorker worker) {
        int offsetBonus = 0;
        String prefix = "";
        if (location == 0) {
            offsetBonus = SAVEDATA_ATEL_OFFSET;
        } else if (location == 3) {
            if (worker != null) {
                offsetBonus = worker.privateDataOffset;
            } else {
                prefix = "+";
            }
        }
        String offsetStr = prefix + StringHelper.formatHex4(offset + offsetBonus);
        return locationToLabel(location) + offsetStr;
    }

    public String getDereference() {
        String offsetStr = StringHelper.formatHex4(offset);
        if (location == 0) {
            offsetStr = StringHelper.formatHex4(offset + SAVEDATA_ATEL_OFFSET) + "=" + offsetStr + "+" + StringHelper.formatHex4(SAVEDATA_ATEL_OFFSET);
        }
        return locationToString(location) + "[" + offsetStr + "]";
    }

    public String valuesString() {
        if (values.isEmpty() || values.stream().allMatch(o -> o.valueSigned == 0)) {
            return "";
        }
        String joined = values.stream().map(StackObject::toString).collect(Collectors.joining(", "));
        return values.size() > 1 ? "values=[" + joined + "]" : ("value=" + joined);
    }

    public static String locationToString(int location) {
        return switch (location) {
            case 0 -> "saveData";
            case 1 -> "battleVars";
            case 2 -> "dataOffset";
            case 3 -> "private";
            case 4 -> "sharedOffset";
            case 5 -> "rI";
            case 6 -> "eventData";
            default -> "unknown:" + location;
        };
    }

    public static String locationToLabel(int location) {
        return switch (location) {
            case 0 -> "saveData";
            case 1 -> "battleVar";
            case 3 -> "priv";
            case 4 -> "sharedObj";
            case 6 -> "eventVar";
            default -> "unknown:" + location;
        };
    }

    public static String formatToType(int format) {
        return switch (format) {
            case 0 -> "uint8";
            case 1 -> "int8";
            case 2 -> "uint16";
            case 3 -> "int16";
            case 4 -> "uint32";
            case 5 -> "int32";
            case 6 -> "float";
            default -> "unknown";
        };
    }

    private static boolean isWeakType(String type) {
        return type == null || "unknown".equals(type);
    }
}
