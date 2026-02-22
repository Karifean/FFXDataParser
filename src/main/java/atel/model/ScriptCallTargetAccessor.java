package atel.model;

import reading.BytesHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ScriptCallTargetAccessor extends ScriptCallTarget {

    private final Map<Integer, ScriptField> accessMap;
    private final String subjectType;
    private final String predicateType;
    private final ScriptField fixedPredicate;
    private final boolean self;
    private final String write;
    private int subjectParamIndex;
    private int predicateParamIndex;
    private int valueParamIndex;
    private int extraParams;

    public ScriptCallTargetAccessor(String name, String subjectType, String internalName, String write, ScriptField predicate, ScriptField... extras) {
        super(name, "accessor", internalName, true);
        setInputs(subjectType, write != null, false, extras);
        this.subjectType = subjectType;
        this.predicateType = predicate.type;
        this.self = subjectType == null;
        this.write = write;
        this.fixedPredicate = predicate;
        this.accessMap = null;
    }

    public ScriptCallTargetAccessor(String name, String subjectType, String internalName, String write, String predicateType, ScriptField... extras) {
        super(name, "accessor", internalName, true);
        setInputs(subjectType, write != null, true, extras);
        this.subjectType = subjectType;
        this.predicateType = predicateType;
        this.self = subjectType == null;
        this.write = write;
        this.fixedPredicate = null;
        this.accessMap = ScriptConstants.FFX.getEnumMap(predicateType);
    }

    public String getType(List<StackObject> params) {
        int predIdx = self ? 0 : 1;
        StackObject predParam = predIdx < params.size() ? params.get(predIdx) : null;
        ScriptField predicate = fixedPredicate != null ? fixedPredicate : (predParam == null || predParam.expression) ? null : accessMap.get(predParam.valueSigned);
        return predicate != null ? predicate.type : "unknown";
    }

    @Override
    public String callB5(List<StackObject> params, ScriptState state) {
        int len = params.size();
        if (len != (inputs == null ? 0 : inputs.size())) {
            return "ERROR, call target " + name + " called with " + len + " params but needs " + (inputs == null ? 0 : inputs.size()) + "!";
        }
        StringBuilder str = new StringBuilder();
        if (PRINT_WITH_HEX_SUFFIX && !isNameless()) {
            str.append(getHexSuffix().substring(1)).append(' ');
        }
        if (self) {
            str.append("Self");
        } else {
            StackObject subjectParam = params.get(subjectParamIndex);
            StackObject typed = subjectParam.expression || "unknown".equals(subjectType) ? subjectParam : new StackObject(subjectType, subjectParam);
            str.append(typed.asString(state));
        }
        str.append('.');
        StackObject predParam = predicateParamIndex >= 0 && predicateParamIndex < params.size() ? params.get(predicateParamIndex) : null;
        ScriptField predicate = fixedPredicate != null ? fixedPredicate : (predParam == null || predParam.expression) ? null : accessMap.get(predParam.valueSigned);
        if (predicate != null) {
            str.append(predicate);
        } else {
            StackObject typed = predParam.expression || "unknown".equals(predicateType) ? predParam : new StackObject(predicateType, predParam);
            String typedString = typed.asString(state);
            if (typed.maybeBracketize) {
                typedString = '(' + typedString + ')';
            }
            str.append(typedString);
        }
        if (extraParams > 0) {
            str.append('(');
            for (int i = 0; i < len; i++) {
                if (i != subjectParamIndex && i != predicateParamIndex && i != valueParamIndex) {
                    StackObject obj = params.get(i);
                    String paramType = inputs.get(i).type;
                    StackObject typed = obj.expression || "unknown".equals(paramType) ? obj : new StackObject(paramType, obj);
                    str.append(inputs.get(i)).append('=').append(typed.asString(state)).append(", ");
                }
            }
            return str.substring(0, str.length() - 2) + ')';
        } else {
            return str.toString();
        }
    }

    @Override
    public String callD8(List<StackObject> params, ScriptState state) {
        StackObject valParam = valueParamIndex >= 0 && valueParamIndex < params.size() ? params.get(valueParamIndex) : null;
        String paramType = getType(params);
        StackObject typed = valParam == null || valParam.expression || "unknown".equals(paramType) ? valParam : new StackObject(paramType, valParam);
        return "Set " + callB5(params, state) + ' ' + write + ' ' + typed;
    }

    @Override
    public String getInputType(int index, List<ScriptInstruction> instructionInputs) {
        if (index == valueParamIndex) {
            ScriptInstruction predParam = BytesHelper.get(instructionInputs, predicateParamIndex);
            ScriptField predicate = fixedPredicate != null ? fixedPredicate : (predParam == null || predParam.opcode != 0xAE) ? null : accessMap.get(predParam.argvSigned);
            return predicate != null ? predicate.type : "unknown";
        }
        if (index == predicateParamIndex) {
            return predicateType;
        }
        if (index == subjectParamIndex) {
            return subjectType;
        }
        return super.getInputType(index, instructionInputs);
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
