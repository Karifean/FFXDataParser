package atel.model;

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
            String type = formatToType();
            int value = 0;
            if (dataOffset > 0) {
                value += bytes[valueLocation + i * length];
                if (length > 1) {
                    value += bytes[valueLocation + 1 + i * length] * 0x100;
                    if (length > 2) {
                        value += bytes[valueLocation + 2 + i * length] * 0x10000 + bytes[valueLocation + 3 + i * length] * 0x1000000;
                    }
                }
            }
            if ("int16".equals(type) && value >= 0x8000 && value <= 0xFFFF) {
                value -= 0x10000;
            }
            if ("int8".equals(type) && value >= 0x80 && value <= 0xFF) {
                value -= 0x100;
            }
            String content = value + " [" + String.format("%04X", value) + "h]";
            StackObject obj = new StackObject(parentWorker, null, type, false, content, value);
            values.add(obj);
        }
    }

    @Override
    public String toString() {
        return "{ " +
                fullStoreLocation() +
                ", type=" + fullTypeString() +
                (!values.isEmpty() ? ", values=" + valuesString() : "") +
                " }";
    }

    private String fullStoreLocation() {
        String deref = getDereference();
        if (location == 0) {
            ScriptField scriptField = StackObject.enumToScriptField("saveData", offset);
            return Objects.requireNonNullElse(scriptField.name, "Unknown") + " (" + deref + ")";
        } else if (location == 1) {
            ScriptField scriptField = StackObject.enumToScriptField("commonVar", offset);
            return Objects.requireNonNullElse(scriptField.name, "Unknown") + " (" + deref + ")";
        }
        return deref;
    }

    private String fullTypeString() {
        String valueFormat = formatToType();
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
            } else {
                return getDereference();
            }
        }
        if (location == 1) {
            ScriptField scriptField = StackObject.enumToScriptField("commonVar", offset);
            if (scriptField.name != null) {
                return scriptField.name;
            } else {
                return getDereference();
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
            ScriptField enumTarget = ScriptConstants.getEnumMap("commonVar").get(offset);
            if (enumTarget != null) {
                return enumTarget.type;
            }
        }
        return formatToType();
    }

    public String getVarLabel() {
        return "var" + String.format("%02X", index);
    }

    public String getDereference() {
        String loc = locationToString();
        String arrayIndex = "[" + String.format("%04X", offset) + "]";
        return loc + arrayIndex;
    }

    public String initString() {
        boolean hasInit = values.stream().anyMatch(v -> v.valueSigned != 0);
        return getVarLabel() + (hasInit ? "=" + valuesString() : "");
    }

    public String valuesString() {
        if (values.isEmpty()) {
            return "";
        }
        String joined = values.stream().map(StackObject::toString).collect(Collectors.joining(", "));
        return values.size() > 1 ? "[" + joined + "]" : joined;
    }

    public String formatToType() {
        return formatToType(format);
    }

    private String locationToString() {
        return locationToString(location);
    }

    public static String locationToString(int location) {
        return switch (location) {
            case 0 -> "saveData";
            case 1 -> "commonVars";
            case 2 -> "dataOffset";
            case 3 -> "private";
            case 4 -> "sharedOffset";
            case 5 -> "int variables";
            case 6 -> "eventData";
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
