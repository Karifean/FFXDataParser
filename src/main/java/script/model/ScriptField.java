package script.model;

public class ScriptField {
    protected static final boolean PRINT_WITH_HEX_SUFFIX = true;

    public String name;
    public String internalName;
    public String type;
    public Integer idx;

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
        if (name == null || name.isEmpty()) {
            if (internalName == null || internalName.isEmpty()) {
                return getHexIndex();
            } else {
                return PRINT_WITH_HEX_SUFFIX ? internalName + getHexSuffix() : internalName;
            }
        }
        return PRINT_WITH_HEX_SUFFIX ? name + getHexSuffix() : name;
    }

    public String getHexIndex() {
        if (idx == null) {
            return null;
        }
        return String.format("%04X", idx);
    }

    public String getHexSuffix() {
        String hexIndex = getHexIndex();
        if (hexIndex == null) {
            return "";
        }
        return " [" + hexIndex + "h]";
    }
}