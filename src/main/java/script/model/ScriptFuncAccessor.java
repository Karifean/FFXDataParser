package script.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ScriptFuncAccessor extends ScriptFunc {

    private Map<Integer, ScriptField> accessMap;
    private String subjectType;
    private String predicateType;
    private ScriptField fixedPredicate;
    private boolean self;
    private String write;
    private int subjectParamIndex;
    private int predicateParamIndex;
    private int valueParamIndex;
    private int extraParams;

    public ScriptFuncAccessor(String name, String subjectType, String internalName, String write, ScriptField predicate, ScriptField... extras) {
        super(name, "accessor", internalName, true);
        setInputs(subjectType, write != null, false, extras);
        this.subjectType = subjectType;
        this.predicateType = predicate.type;
        this.self = subjectType == null;
        this.write = write;
        this.fixedPredicate = predicate;
    }

    public ScriptFuncAccessor(String name, String subjectType, String internalName, String write, Map<Integer, ScriptField> accessMap, String predicateType, ScriptField... extras) {
        super(name, "accessor", internalName, true);
        setInputs(subjectType, write != null, true, extras);
        this.subjectType = subjectType;
        this.predicateType = predicateType;
        this.self = subjectType == null;
        this.write = write;
        this.accessMap = accessMap;
    }

    public String getType(List<StackObject> params) {
        int predIdx = self ? 0 : 1;
        ScriptField predicate = fixedPredicate != null ? fixedPredicate : accessMap.get(params.get(predIdx).value);
        return predicate != null ? predicate.type : null;
    }

    public String callB5(List<StackObject> params) {
        int len = params.size();
        if (len != (inputs == null ? 0 : inputs.size())) {
            return "ERROR, func " + name + " called with " + len + " params but needs " + (inputs == null ? 0 : inputs.size()) + "!";
        }
        StringBuilder str = new StringBuilder();
        if (SHOW_FULL_TAG) {
            str.append(getHexSuffix().substring(1)).append(' ');
        }
        if (self) {
            str.append("Self");
        } else {
            StackObject subjectParam = params.get(subjectParamIndex);
            StackObject typed = subjectParam.expression ? subjectParam : new StackObject(subjectType, subjectParam);
            str.append(typed);
        }
        str.append('.');
        ScriptField predicate = fixedPredicate != null ? fixedPredicate : accessMap.get(params.get(predicateParamIndex).value);
        if (predicate != null) {
            str.append(predicate);
        } else {
            StackObject predParam = params.get(predicateParamIndex);
            StackObject typed = predParam.expression ? predParam : new StackObject(predicateType, predParam);
            str.append(typed);
        }
        if (extraParams > 0) {
            str.append('(');
            for (int i = 0; i < len; i++) {
                if (i != subjectParamIndex && i != predicateParamIndex && i != valueParamIndex) {
                    StackObject obj = params.get(i);
                    StackObject typed = obj.expression ? obj : new StackObject(inputs.get(i).type, obj);
                    str.append(inputs.get(i)).append('=').append(typed).append(", ");
                }
            }
            return str.substring(0, str.length() - 2) + ')';
        } else {
            return str.toString();
        }
    }

    public String callD8(List<StackObject> params) {
        StackObject valParam = params.get(valueParamIndex);
        StackObject typed = valParam.expression ? valParam : new StackObject(getType(params), valParam);
        return "Set " + callB5(params) + ' ' + write + ' ' + typed;
    }

    private void setInputs(String subjectType, boolean write, boolean propAsInput, ScriptField[] extras) {
        inputs = new ArrayList<>();
        ScriptField propertyField = new ScriptField("property");
        if (propAsInput) {
            inputs.add(propertyField);
        }
        ScriptField subjectField = new ScriptField("subject", subjectType);
        if (subjectType != null) {
            inputs.add(0, subjectField);
        }
        ScriptField valueField = new ScriptField("value", "unknown");
        if (write) {
            inputs.add(valueField);
        }
        if (extras != null && extras.length > 0) {
            extraParams = extras.length;
            inputs.addAll(0, Arrays.asList(extras));
        } else {
            extraParams = 0;
        }
        subjectParamIndex = inputs.indexOf(subjectField);
        predicateParamIndex = inputs.indexOf(propertyField);
        valueParamIndex = inputs.indexOf(valueField);
    }
}
