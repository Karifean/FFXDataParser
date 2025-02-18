package atel.model;

import main.StringHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ScriptVariable {
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

    public static int[] byteStructFromDescriptor(ScriptWorker parentWorker, int value, int length) {
        if (parentWorker == null || parentWorker.parentScript == null) {
            return null;
        }
        ScriptVariable scriptVariable = new ScriptVariable(parentWorker, 0, value, 1);
        int offset = scriptVariable.getDataOffset();
        if (offset < 0) {
            return null;
        }
        return Arrays.copyOfRange(parentWorker.parentScript.getBytes(), offset, offset + length);
    }

    public ScriptVariable(ScriptWorker parentWorker, int index, int lb, int hb) {
        this.parentWorker = parentWorker;
        this.index = index;
        this.lb = lb;
        this.hb = hb;
        this.fullBytes = hb * 0x100000000L + lb;
        this.offset = lb & 0xFFFFFF;
        this.format = (lb & 0xF0000000) >> 28;
        this.location = (lb & 0x0F000000) >> 25;
        this.elementCount = hb & 0xFFFF;
        this.elementSize = (hb & 0xFFFF0000) >> 16;
    }

    public ScriptVariable(ScriptVariable vr) {
        this.parentWorker = vr.parentWorker;
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
            int valueSigned = 0;
            if (dataOffset > 0) {
                int valueOffset = valueLocation + i * length;
                valueSigned += bytes[valueOffset];
                if (length > 1) {
                    valueSigned += bytes[valueOffset + 1] << 8;
                    if (length > 2) {
                        valueSigned += (bytes[valueOffset + 2] << 16) + (bytes[valueOffset + 3] << 24);
                    }
                }
            }
            int valueUnsigned = valueSigned;
            if ("int16".equals(type) && valueSigned >= 0x8000 && valueSigned <= 0xFFFF) {
                valueUnsigned = valueSigned - 0x10000;
            }
            if ("int8".equals(type) && valueSigned >= 0x80 && valueSigned <= 0xFF) {
                valueUnsigned = valueSigned - 0x100;
            }
            StackObject obj = new StackObject(parentWorker, null, type, valueSigned, valueUnsigned);
            values.add(obj);
        }
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        list.add(fullStoreLocation());
        list.add("type=" + fullTypeString());
        list.add(valuesString());
        String full = list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining(", "));
        return "{ " + full + " }";
    }

    private String fullStoreLocation() {
        String deref = getDereference();
        if (location == 0) {
            ScriptField scriptField = StackObject.enumToScriptField("saveData", offset);
            return Objects.requireNonNullElse(scriptField.name, "Unknown") + " (" + deref + ")";
        } else if (location == 1) {
            ScriptField scriptField = StackObject.enumToScriptField("battleVar", offset);
            return Objects.requireNonNullElse(scriptField.name, "Unknown") + " (" + deref + ")";
        }
        return deref;
    }

    private String fullTypeString() {
        String valueFormat = formatToType(format);
        if (elementCount <= 1) {
            return valueFormat;
        }
        String elements = elementSize > 1 ? elementCount + "=" + (elementCount / elementSize) + "*" + elementSize + "bytes" : ""+elementCount;
        String arrayIndex = "[" + elements + "]";
        return valueFormat + arrayIndex;
    }

    public String getLabel() {
        if (location == 0) {
            ScriptField scriptField = StackObject.enumToScriptField("saveData", offset);
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
        return getVarLabel();
    }

    public String getArrayIndexType() {
        if (location == 0) {
            ScriptField scriptField = StackObject.enumToScriptField("saveData", offset);
            if (scriptField.indexType != null) {
                return scriptField.indexType;
            }
        }
        return "unknown";
    }

    public String getType() {
        if (location == 0) {
            ScriptField enumTarget = ScriptConstants.ENUMERATIONS.get("saveData").get(offset);
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

    public String getVarLabel() {
        return locationToLabel(location) + StringHelper.formatHex2(location == 3 ? index : offset);
    }

    public String getDereference() {
        return locationToString(location) + "[" + StringHelper.formatHex4(offset) + "]";
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
            case 1 -> "battleVar";
            case 2 -> "dataOffset";
            case 3 -> "private";
            case 4 -> "sharedOffset";
            case 5 -> "int variables";
            case 6 -> "eventData";
            default -> "unknown:" + location;
        };
    }

    public static String locationToLabel(int location) {
        return switch (location) {
            case 0 -> "saveData";
            case 1 -> "battleVar";
            case 3 -> "var";
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
}
