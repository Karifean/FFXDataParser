public class ScriptField {
    public String name;
    public String type;

    public ScriptField(String typeAndName) {
        this.name = typeAndName;
        this.type = typeAndName;
    }

    public ScriptField(String name, String type) {
        this.name = name;
        this.type = type;
    }
    @Override public String toString() { return name; }
}