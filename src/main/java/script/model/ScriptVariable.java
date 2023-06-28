package script.model;

import script.ScriptObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ScriptVariable {
    public final int index;
    public final long struct;
    public final int lb;
    public final int hb;
    public final int offset;
    public final int format;
    public final int location;
    public final int elementCount;
    public final int unknownTopBytes;
    public final List<StackObject> values = new ArrayList<>();

    public ScriptVariable(int index, int lb, int hb) {
        this.index = index;
        this.lb = lb;
        this.hb = hb;
        this.struct = hb * 0x100000000L + lb;
        this.offset = lb & 0xFFFFFF;
        this.format = (lb & 0xF0000000) >> 28;
        this.location = (lb & 0x0F000000) >> 25;
        this.elementCount = hb & 0xFFFF;
        this.unknownTopBytes = (hb & 0xFFFF0000) >> 16;
    }

    public ScriptVariable(ScriptVariable vr) {
        this.index = vr.index;
        this.lb = vr.lb;
        this.hb = vr.hb;
        this.struct = vr.struct;
        this.offset = vr.offset;
        this.format = vr.format;
        this.location = vr.location;
        this.elementCount = vr.elementCount;
        this.unknownTopBytes = vr.unknownTopBytes;
    }

    public int getLength() {
        return format < 2 ? 1 : (format < 4 ? 2 : 4);
    }

    public void parseValues(ScriptObject script, int[] bytes, int outerOffset) {
        if (location != 3 && location != 4) {
            return;
        }
        int valueLocation = outerOffset + offset;
        int length = getLength();
        if (bytes.length < valueLocation + length) {
            return;
        }
        for (int i = 0; i < elementCount; i++) {
            String type = formatToType();
            int value = 0;
            if (outerOffset > 0) {
                value += bytes[valueLocation + i * length];
                if (length > 1) {
                    value += bytes[valueLocation + 1 + i * length] * 0x100;
                    if (length > 2) {
                        value += bytes[valueLocation + 2 + i * length] * 0x10000 + bytes[valueLocation + 3 + i * length] * 0x1000000;
                    }
                }
            }
            StackObject obj = new StackObject(script, null, type, false, null, value);
            values.add(obj);
        }
    }

    @Override
    public String toString() {
        return "{ " +
                fullStoreLocation() +
                ", type=" + fullTypeString() +
                (!values.isEmpty() ? ", values=" + valuesString() : "") +
                (unknownTopBytes > 0 ? ", utb=" + unknownTopBytes + " [" + String.format("%04X", unknownTopBytes) + "h]" : "") +
                " }";
    }

    private String fullStoreLocation() {
        String deref = getDereference();
        if (location == 0) {
            ScriptField scriptField = StackObject.enumToScriptField("globalVar", offset);
            return Objects.requireNonNullElse(scriptField.name, "Unknown") + " (" + deref + ")";
        }
        return deref;
    }

    private String fullTypeString() {
        String valueFormat = formatToType();
        if (elementCount <= 1) {
            return valueFormat;
        }
        String arrayIndex = "[" + elementCount + "]";
        return valueFormat + arrayIndex;
    }

    public String getLabel() {
        if (location == 0) {
            ScriptField scriptField = StackObject.enumToScriptField("globalVar", offset);
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
            ScriptField scriptField = StackObject.enumToScriptField("globalVar", offset);
            if (scriptField.indexType != null) {
                return scriptField.indexType;
            }
        }
        return "unknown";
    }

    public String getType() {
        if (location == 0) {
            ScriptField enumTarget = ScriptConstants.ENUMERATIONS.get("globalVar").get(offset);
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
        boolean hasInit = values.stream().anyMatch(v -> v.value != 0);
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
            case 0 -> "global";
            case 1 -> "shared1";
            case 2 -> "dataOffset";
            case 3 -> "private";
            case 4 -> "sharedOffset";
            case 5 -> "int variables";
            case 6 -> "eventData";
            default -> "unknown?";
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
