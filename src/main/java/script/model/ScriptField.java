package script.model;

import model.Nameable;

public class ScriptField implements Nameable {
    protected static final boolean PRINT_WITH_HEX_SUFFIX = true;

    public String name;
    public String internalName;
    public String type;
    public Integer idx;
    public String hexFormat;
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

    @Override
    public String toString() {
        boolean withHexSuffix = PRINT_WITH_HEX_SUFFIX || (name == null && internalName == null);
        return getLabel() + (withHexSuffix ? getHexSuffix() : "");
    }

    @Override
    public String getName() {
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
        return String.format(hexFormat != null ? hexFormat : (idx >= 0x10000 ? "%08X" : (idx >= 0x100 ? "%04X" : "%02X")), idx);
    }

    public String getHexSuffix() {
        String hexIndex = getHexIndex();
        if (hexIndex == null) {
            return "";
        }
        return " [" + hexIndex + "h]";
    }
}