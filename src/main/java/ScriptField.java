public class ScriptField {
    private static final boolean PRINT_WITH_HEX_SUFFIX = false;

    public String name;
    public String internalName;
    public String type;
    public int idx;

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

    public String getNameOrHex() {
        if (name == null || name.isEmpty()) {
            return getHexIndex();
        }
        return name;
    }

    public String getHexIndex() {
        return String.format("%04x", idx).toUpperCase();
    }

    public String getHexSuffix() {
        return " [" + getHexIndex() + "h]";
    }
}