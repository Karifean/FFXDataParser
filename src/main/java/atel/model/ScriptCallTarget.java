package atel.model;

import main.StringHelper;

import java.util.ArrayList;
import java.util.List;

public class ScriptCallTarget extends ScriptOpcode {
    public int funcspace;
    public boolean canCallAsVoid = false;

    public ScriptCallTarget(String name, String type, String internalName, boolean brackets) {
        super(name, type, internalName);
        this.inputs = brackets ? new ArrayList<>() : null;
    }

    public ScriptCallTarget(String name, String type, String internalName, ScriptField... inputs) {
        super(name, type, internalName);
        this.inputs = List.of(inputs);
    }

    public ScriptCallTarget(String name, ScriptField... inputs) {
        super(name, "unknown", null);
        this.inputs = List.of(inputs);
    }

    public ScriptCallTarget(ScriptField... inputs) {
        super(null, "unknown", null);
        this.inputs = List.of(inputs);
    }

    @Override
    public String getHexIndex() {
        return idx != null ? StringHelper.formatHex4(idx) : null;
    }

    @Override
    public String toString() {
        String groupStr = idx != null ? ScriptConstants.FFX.FUNCSPACES[idx / 0x1000] + '.' : "";
        if (isNameless()) {
            return groupStr + getHexIndex();
        }
        return groupStr + super.toString();
    }

    public String getOptionLabel() {
        String hexIndex = getHexIndex();
        if (isNameless()) {
            return hexIndex;
        }
        return hexIndex + ": " + getLabel();
    }

    public String callB5(List<StackObject> params, ScriptState state) {
        int len = params.size();
        if (len != (inputs == null ? 0 : inputs.size())) {
            return "ERROR, call target " + this + " called with " + len + " params but needs " + (inputs == null ? 0 : inputs.size()) + "!";
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
        List<StackObject> typedParams = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            StackObject typed = getTypedParam(i, params, typedParams);
            typedParams.add(typed);
            str.append(inputs.get(i).name).append('=').append(typed.asString(state)).append(", ");
        }
        return str.substring(0, str.length() - 2) + ')';
    }

    public String callD8(List<StackObject> params, ScriptState state) {
        return "call " + callB5(params, state);
    }

    public String getInputType(int index, List<ScriptInstruction> instructionInputs) {
        return inputs.get(index).type;
    }

    protected boolean isNameless() {
        return (name == null || name.isEmpty()) && (internalName == null || internalName.isEmpty());
    }
}
