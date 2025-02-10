package atel.model;

import model.Nameable;

import java.util.function.Function;

public class ScriptField implements Nameable {
    protected static final boolean PRINT_WITH_HEX_SUFFIX = true;

    public String name;
    public String internalName;
    public String type;
    public Integer idx;
    public String indexType;

    public ScriptField(String typeAndName) {
        this.name = typeAndName;
        this.type = typeAndName;
    }

    public ScriptField(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public ScriptField(String name, String type, String internalName) {
        this.name = name;
        this.type = type;
        this.internalName = internalName;
    }

    public ScriptField(String name, String type, String internalName, int idx) {
        this.name = name;
        this.type = type;
        this.internalName = internalName;
        this.idx = idx;
    }

    @Override
    public String toString() {
        boolean withHexSuffix = PRINT_WITH_HEX_SUFFIX || (name == null && internalName == null);
        return getLabel() + (withHexSuffix ? getHexSuffix() : "");
    }

    @Override
    public String getName(String localization) {
        return name;
    }

    public String getLabel() {
        if (name != null) {
            return name;
        } else if (internalName != null) {
            return internalName;
        } else {
            return type + ":" + idx;
        }
    }

    public String getHexIndex() {
        if (idx == null) {
            return null;
        }
        String format = idx >= 0x10000 || idx < 0 ? "%08X" : (idx >= 0x100 ? "%04X" : "%02X");
        String formatted = String.format(format, idx);
        if ("%08X".equals(format) && formatted.startsWith("FFFF")) {
            return formatted.substring(4);
        }
        return formatted;
    }

    public String getHexSuffix() {
        String hexIndex = getHexIndex();
        if (hexIndex == null) {
            return "";
        }
        return " [" + hexIndex + "h]";
    }
}