package script.model;

import java.util.ArrayList;
import java.util.List;

public class ScriptFunc extends ScriptField {
    public List<ScriptField> inputs;
    public int funcspace;

    public ScriptFunc(String name, String type, String internalName, boolean brackets) {
        super(name, type, internalName);
        this.inputs = brackets ? new ArrayList<>() : null;
        this.hexFormat = "%04X";
    }

    public ScriptFunc(String name, String type, String internalName, ScriptField... inputs) {
        super(name, type, internalName);
        this.inputs = List.of(inputs);
        this.hexFormat = "%04X";
    }

    public ScriptFunc(ScriptField... inputs) {
        super(null, "unknown");
        this.inputs = List.of(inputs);
        this.hexFormat = "%04X";
    }

    public String getType(List<StackObject> params) {
        return type;
    }

    @Override
    public String toString() {
        String groupStr = idx != null ? ScriptConstants.FUNCSPACES[idx / 0x1000] + '.' : "";
        if (isNameless()) {
            return groupStr + getHexIndex();
        }
        return groupStr + super.toString();
    }

    public String callB5(List<StackObject> params) {
        int len = params.size();
        if (len != (inputs == null ? 0 : inputs.size())) {
            return "ERROR, func " + this + " called with " + len + " params but needs " + (inputs == null ? 0 : inputs.size()) + "!";
        }
        StringBuilder str = new StringBuilder();
        str.append(this);
        if (inputs == null) {
            return str.toString();
        }
        str.append('(');
        if (len == 0) {
            return str.toString() + ')';
        }
        for (int i = 0; i < len; i++) {
            StackObject obj = params.get(i);
            String paramType = inputs.get(i).type;
            StackObject typed = obj == null || obj.expression || "unknown".equals(paramType) ? obj : new StackObject(paramType, obj);
            str.append(inputs.get(i).name).append('=').append(typed).append(", ");
        }
        return str.substring(0, str.length() - 2) + ')';
    }

    public String callD8(List<StackObject> params) {
        return "call " + callB5(params);
    }

    protected boolean isNameless() {
        return (name == null || name.isEmpty()) && (internalName == null || internalName.isEmpty());
    }
}
