import java.util.ArrayList;
import java.util.List;

public class ScriptFunc extends ScriptField {

    public List<ScriptField> inputs;

    public ScriptFunc(String name, String type, boolean brackets) {
        super(name, type);
        this.inputs = brackets ? new ArrayList<>() : null;
    }

    public ScriptFunc(String name, String type, List<ScriptField> inputs) {
        super(name, type);
        this.inputs = inputs;
    }

    public ScriptFunc(String name, String type, ScriptField i1) {
        super(name, type);
        this.inputs = List.of(i1);
    }

    public ScriptFunc(String name, String type, ScriptField i1, ScriptField i2) {
        super(name, type);
        this.inputs = List.of(i1, i2);
    }

    public ScriptFunc(String name, String type, ScriptField i1, ScriptField i2, ScriptField i3) {
        super(name, type);
        this.inputs = List.of(i1, i2, i3);
    }

    public ScriptFunc(String name, String type, ScriptField i1, ScriptField i2, ScriptField i3, ScriptField i4) {
        super(name, type);
        this.inputs = List.of(i1, i2, i3, i4);
    }

    public ScriptFunc(String name, String type, ScriptField i1, ScriptField i2, ScriptField i3, ScriptField i4, ScriptField i5) {
        super(name, type);
        this.inputs = List.of(i1, i2, i3, i4, i5);
    }

    public ScriptFunc(String name, String type, ScriptField i1, ScriptField i2, ScriptField i3, ScriptField i4, ScriptField i5, ScriptField i6) {
        super(name, type);
        this.inputs = List.of(i1, i2, i3, i4, i5, i6);
    }

    public ScriptFunc(String name, String type, ScriptField i1, ScriptField i2, ScriptField i3, ScriptField i4, ScriptField i5, ScriptField i6, ScriptField i7) {
        super(name, type);
        this.inputs = List.of(i1, i2, i3, i4, i5, i6, i7);
    }

    public ScriptFunc(String name, String type, ScriptField i1, ScriptField i2, ScriptField i3, ScriptField i4, ScriptField i5, ScriptField i6, ScriptField i7, ScriptField i8) {
        super(name, type);
        this.inputs = List.of(i1, i2, i3, i4, i5, i6, i7, i8);
    }

    public ScriptFunc(String name, String type, ScriptField i1, ScriptField i2, ScriptField i3, ScriptField i4, ScriptField i5, ScriptField i6, ScriptField i7, ScriptField i8, ScriptField i9) {
        super(name, type);
        this.inputs = List.of(i1, i2, i3, i4, i5, i6, i7, i8, i9);
    }

    public String getType(List<StackObject> params) {
        return type;
    }

    @Override
    public String toString() {
        return name;
    }

    public String callB5(List<StackObject> params) {
        int len = params.size();
        if (len != (inputs == null ? 0 : inputs.size())) {
            return "ERROR, func " + name + " called with " + len + " params but needs " + (inputs == null ? 0 : inputs.size()) + "!";
        }
        if (inputs == null) {
            return name;
        }
        StringBuilder str = new StringBuilder(name);
        str.append('(');
        if (len == 0) {
            return str.toString() + ')';
        }
        for (int i = 0; i < len; i++) {
            StackObject obj = params.get(i);
            StackObject typed = obj.expression ? obj : new StackObject(inputs.get(i).type, false, obj.content, obj.value);
            str.append(inputs.get(i)).append('=').append(typed).append(", ");
        }
        return str.substring(0, str.length() - 2) + ')';
    }

    public String callD8(List<StackObject> params) {
        return "call " + callB5(params);
    }
}
