package atel.model;

import atel.AtelScriptObject;
import main.StringHelper;
import reading.BytesHelper;

import java.util.*;

public class ScriptInstruction {
    private static final boolean INFER_BITWISE_OPS_AS_BITFIELDS = false;

    public int offset;
    public int opcode;
    public boolean hasArgs;
    public int length;
    public int arg1;
    public int arg2;
    public int argv;
    public int argvSigned;
    public Integer dereferencedArg;
    public ScriptVariable dereferencedVar;

    public List<ScriptJump> incomingJumps;
    public ScriptLine parentLine;
    public ScriptWorker parentWorker;
    public AtelScriptObject parentScript;

    public List<ScriptInstruction> inputs;

    public ScriptInstruction(int addr, int opcode) {
        this(addr, opcode, 1);
    }

    public ScriptInstruction(int addr, int opcode, int length) {
        this.offset = addr;
        this.opcode = opcode;
        this.hasArgs = false;
        this.length = length;
        this.arg1 = 0;
        this.arg2 = 0;
        this.argv = 0;
        this.argvSigned = 0;
    }

    public ScriptInstruction(int addr, int opcode, int arg1, int arg2) {
        this.offset = addr;
        this.opcode = opcode;
        this.hasArgs = true;
        this.length = 3;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.argv = arg1 + arg2 * 0x100;
        this.argvSigned = argv < 0x8000 ? argv : (argv - 0x10000);
    }

    public ScriptInstruction(ScriptLine line, int opcode, int argv) {
        this.offset = -1;
        this.opcode = opcode;
        this.hasArgs = ScriptOpcode.OPCODES[opcode].hasArgs;
        this.length = hasArgs ? 3 : 1;
        setArgv(argv);
        setParentLine(line);
    }

    public ScriptInstruction(ScriptInstruction instruction) {
        this.offset = instruction.offset;
        this.opcode = instruction.opcode;
        this.hasArgs = instruction.hasArgs;
        this.length = instruction.length;
        this.arg1 = instruction.arg1;
        this.arg2 = instruction.arg2;
        this.argv = instruction.argv;
        this.argvSigned = instruction.argvSigned;
        this.dereferencedArg = instruction.dereferencedArg;
        this.dereferencedVar = instruction.dereferencedVar;
    }

    public ScriptInstruction cloneInstruction() {
        return new ScriptInstruction(this);
    }

    public void setParentLine(ScriptLine line) {
        this.parentLine = line;
        this.parentWorker = line.parentWorker;
        this.parentScript = line.parentScript;
    }

    public int rereference(int newOffset, List<ScriptVariable> variableDeclarations, List<Integer> refInts, List<Integer> refFloats) {
        if (opcode == 0xAD && dereferencedArg != null) {
            int index = BytesHelper.findOrAppend(refInts, dereferencedArg);
            setArgv(index);
        }
        if (opcode == 0xAF && dereferencedArg != null) {
            int index = BytesHelper.findOrAppend(refFloats, dereferencedArg);
            setArgv(index);
        }
        if (((opcode >= 0x9F && opcode <= 0xA4) || opcode == 0xA7) && dereferencedVar != null) {
            int index = BytesHelper.findOrAppend(variableDeclarations, dereferencedVar);
            setArgv(index);
        }
        this.offset = newOffset;
        return newOffset + length;
    }

    public void setOpcode(int newOpcode) {
        setOpcode(newOpcode, 0);
    }

    public void setOpcode(int newOpcode, int argv) {
        opcode = newOpcode;
        ScriptOpcode opcodeObj = ScriptOpcode.OPCODES[newOpcode];
        hasArgs = opcodeObj.hasArgs;
        length = hasArgs ? 3 : 1;
        setArgv(argv);
    }

    public void setArgv(int newArgv) {
        argv =  newArgv;
        argvSigned = newArgv < 0x8000 ? newArgv : (newArgv - 0x10000);
        arg1 =  newArgv & 0xFF;
        arg2 = (newArgv & 0xFF00) >> 8;
    }

    public List<Integer> toBytesList() {
        if (opcode == 0x00) {
            List<Integer> list = new ArrayList<>(length);
            for (int i = 0; i < length; i++) {
                list.add(0x00);
            }
            return list;
        }
        if (hasArgs) {
            return List.of(opcode, arg1, arg2);
        } else {
            return List.of(opcode);
        }
    }

    public String asHexString() {
        if (length <= 0) {
            return "";
        }
        if (opcode == 0x00) {
            if (length > 2) {
                return "00...00";
            } else if (length == 2) {
                return "0000";
            } else {
                return "00";
            }
        }
        return StringHelper.formatHex2(opcode) + (hasArgs ? StringHelper.formatHex2(arg1) + StringHelper.formatHex2(arg2) : "");
    }

    @Override
    public String toString() {
        return asHexString();
    }

    public String asSeparatedHexString() {
        return StringHelper.formatHex2(opcode) + (hasArgs ? ' ' + StringHelper.formatHex2(arg1) + ' ' + StringHelper.formatHex2(arg2) : "");
    }

    public String getOpcodeLabel() {
        return ScriptOpcode.OPCODES[opcode].name;
    }

    public String getArgLabel() {
        if (!hasArgs) {
            return "";
        }
        return "0x" + String.format(arg2 > 0 ? "%04X" : "%02X", argv);
    }

    public String asAsmString() {
        return String.format("%-10s", getOpcodeLabel() + ' ') + getArgLabel();
    }

    public Integer getBranchIndex() {
        if (ScriptOpcode.OPCODES[opcode] != null && ScriptOpcode.OPCODES[opcode].branches) {
            return argv;
        } else {
            return null;
        }
    }

    public int getStackPops() {
        return getStackPops(opcode, argv);
    }

    public static int getStackPops(int opcode, int argv) {
        ScriptOpcode opcodeObj = ScriptOpcode.OPCODES[opcode];
        if (opcodeObj == null) {
            return 0;
        } else if (opcodeObj.isCall) {
            ScriptFunc func = ScriptFuncLib.FFX.get(argv, null);
            return func != null && func.inputs != null ? func.inputs.size() : 0;
        } else {
            return opcodeObj.inputs.size();
        }
    }

    public ScriptInstruction getInput(int index) {
        if (inputs == null || index < 0 || index >= inputs.size()) {
            return null;
        }
        return inputs.get(index);
    }

    public String getInputType(ScriptState state, int index) {
        return getInputType(state, index, false);
    }

    public String getInputType(ScriptState state, int index, boolean preventRecursion) {
        if (index < 0 || index >= getStackPops()) {
            return null;
        }
        ScriptOpcode op = ScriptOpcode.OPCODES[opcode];
        if (op.isCall) {
            ScriptFunc func = ScriptFuncLib.FFX.get(argv, null);
            if (func == null || func.inputs == null) {
                return "unknown";
            }
            return func.getInputType(index, inputs);
        }
        if (opcode == 0xA2 || opcode == 0xA7) {
            return getVariableIndexType(argv);
        }
        if (opcode == 0xA0 || opcode == 0xA1 || opcode == 0xA3 || opcode == 0xA4) {
            if (index == 0 && (opcode == 0xA3 || opcode == 0xA4)) {
                return getVariableIndexType(argv);
            } else {
                return getVariableType(argv);
            }
        }
        if (!preventRecursion && (opcode >= 0x03 && opcode <= 0x05)) {
            int other = index == 0 ? 1 : 0;
            String otherType = inputs.get(other).getOutputType(state);
            if (otherType.startsWith("bitfield") || otherType.endsWith("Bitfield")) {
                return otherType;
            }
        }
        String declaredInputType = op.inputs.get(index).type;
        if (!declaredInputType.equals("unknown")) {
            return declaredInputType;
        }
        if (opcode == 0x26) {
            return state.rAType;
        } else if (opcode == 0x28) {
            return state.rXType;
        } else if (opcode == 0x29) {
            return state.rYType;
        }
        if (!preventRecursion && (opcode == 0x06 || opcode == 0x07)) {
            int other = index == 0 ? 1 : 0;
            return inputs.get(other).getOutputType(state);
        }
        return "int";
    }

    public String getInputLabel(int index) {
        if (index < 0 || index >= getStackPops()) {
            return null;
        }
        ScriptOpcode op = ScriptOpcode.OPCODES[opcode];
        if (op.isCall) {
            ScriptFunc func = ScriptFuncLib.FFX.get(argv, null);
            if (func == null || func.inputs == null) {
                return "unknown";
            }
            return func.inputs.get(index).getLabel();
        }
        return op.inputs.get(index).getLabel();
    }

    public void setUpInputs(Stack<ScriptInstruction> stack) {
        int stackpops = getStackPops();
        inputs = new ArrayList<>(stackpops);
        for (int i = 0; i < stackpops; i++) {
            inputs.add(0, stack.pop());
        }
    }

    public void pushOutput(Stack<ScriptInstruction> stack) {
        if (opcode == 0x2B) {
            stack.push(inputs.get(0));
        }
        ScriptOpcode opcodeObj = ScriptOpcode.OPCODES[opcode];
        String outputType = opcodeObj != null ? opcodeObj.type : null;
        if (outputType != null && !"void".equals(outputType)) {
            stack.push(this);
        }
    }

    public String getOutputType(ScriptState state) {
        if (opcode == 0x26) {
            return state.rAType;
        } else if (opcode == 0x28) {
            return state.rXType;
        } else if (opcode == 0x29) {
            return state.rYType;
        } else if (opcode == 0x2B) {
            return getInput(0).getOutputType(state);
        } else if (opcode >= 0x67 && opcode <= 0x6A) {
            return state.tempITypes.getOrDefault(opcode - 0x67, "int");
        } else if (opcode == 0x9F || opcode == 0xA2) {
            return state.parentScript.getVariableType(argv);
        } else if (opcode == 0xB5) {
            Stack<StackObject> stack = new Stack<>();
            inputsToStackObjects(state, stack);
            List<StackObject> params = popParamsForFunc(stack);
            ScriptFunc func = getFunc(params);
            return func.getType(params);
        } else {
            return ScriptOpcode.OPCODES[opcode].type;
        }
    }

    public String asString(ScriptState state) {
        Stack<StackObject> stack = new Stack<>();
        inputsToStackObjects(state, stack);
        if (opcode == 0xD8) { // CALLPOPA / FUNC
            List<StackObject> params = popParamsForFunc(stack);
            ScriptFunc func = getFunc(params);
            state.rAType = func.getType(params);
            String call = func.callD8(params, state);
            return call + ';';
        }
        StackObject p1 = null, p2 = null, p3 = null;
        switch (getStackPops()) {
            case 3: p3 = stack.pop();
            case 2: p2 = stack.pop();
            case 1: p1 = stack.pop();
            case 0:
            default:
                break;
        }
        if (opcode == 0x00) {
            return "NOP";
        } else if (opcode == 0x25) { // POPA / SET_RETURN_VALUE
            state.rAType = resolveType(state, p1);
            return p1 + ";";
        } else if (opcode == 0x2A) { // POPX / SET_TEST
            state.rXType = resolveType(state, p1);
            return "Set test = " + p1;
        } else if (opcode == 0x2C) { // POPY / SET_CASE
            state.rYType = resolveType(state, p1);
            return "switch " + p1;
        } else if (opcode == 0x34) { // RTS / RETURN
            return "return from subroutine;";
        } else if (opcode == 0x3C) { // RET / END
            return "return;";
        } else if (opcode == 0x3F) { // RETTN / CLEANUP_TO_MAIN
            return "return (RETTN): " + p1;
        } else if (opcode == 0x40) { // HALT / DYNAMIC
            return "halt";
        } else if (opcode == 0x54) { // DRET / CLEANUP_ALL_END
            return "direct return;";
        }  else if (opcode >= 0x59 && opcode <= 0x5C) { // POPI0..3 / SET_INT
            String p1t = resolveType(state, p1);
            int tempIndex = opcode - 0x59;
            String tmpIType = getTempIType(state, tempIndex);
            if (isWeakType(tmpIType)) {
                tmpIType = p1t;
            }
            String val = typed(p1, tmpIType);
            return "Set tmpI" + tempIndex + " = " + val + ";";
        } else if (opcode >= 0x5D && opcode <= 0x66) { // POPF0..9 / SET_FLOAT
            int tempIndex = opcode - 0x5D;
            return "Set tmpF" + tempIndex + " = " + p1 + ";";
        } else if (opcode == 0x77) { // REQWAIT / WAIT_DELETE
            ScriptWorker targetWorker = parentScript.getWorker(p1.valueSigned);
            ScriptJump entryPoint = targetWorker != null ? targetWorker.getEntryPoint(p2.valueSigned) : null;
            boolean direct = !p1.expression && !p2.expression && isWeakType(p1.type) && isWeakType(p2.type) && entryPoint != null;
            String w = p1.expression ? "(" + p2 + ")" : String.format((p1.valueSigned & 0xFF00) != 0 ? "%04X" : "%02X", p1.valueSigned);
            String e = p2.expression ? "(" + p3 + ")" : String.format((p2.valueSigned & 0xFF00) != 0 ? "%04X" : "%02X", p2.valueSigned);
            String scriptLabel = direct ? entryPoint.getLabel() : ("w" + w + "e" + e);
            return "await " + scriptLabel + ";";
        } else if (opcode == 0x79) { // REQCHG / EDIT_ENTRY_TABLE
            int oldIdx = p2.valueSigned + 2;
            int newIdx = p3.valueSigned;
            ScriptJump oldEntryPoint = parentWorker.getEntryPoint(oldIdx);
            ScriptJump newEntryPoint = parentWorker.getEntryPoint(newIdx);
            boolean direct = !p2.expression && !p3.expression && isWeakType(p2.type) && isWeakType(p3.type) && oldEntryPoint != null && newEntryPoint != null;
            String oldScriptLabel = direct ? oldEntryPoint.getLabel() : ("e" + (p2.expression ? "(" + p2 + ")" : String.format((oldIdx & 0xFF00) != 0 ? "%04X" : "%02X", oldIdx)));
            String newScriptLabel = direct ? newEntryPoint.getLabel() : ("e" + (p3.expression ? "(" + p3 + ")" : String.format((newIdx & 0xFF00) != 0 ? "%04X" : "%02X", newIdx)));
            String tableHolder = p1.expression ? ""+p1 : ""+p1.valueSigned;
            return "Replace script " + oldScriptLabel + " with " + newScriptLabel + " (store table at " + tableHolder + ")";
        } else if (opcode == 0xA0 || opcode == 0xA1) { // POPV(L) / SET_DATUM_(W/T)
            String val = typed(p1, getVariableType(argv));
            return "Set " + (opcode == 0xA1 ? "(limit) " : "") + getVariableLabel(argv) + " = " + val + ";";
        } else if (opcode == 0xA3 || opcode == 0xA4) { // POPAR(L) / SET_DATUM_INDEX_(W/T)
            String val = typed(p2, getVariableType(argv));
            return "Set " + (opcode == 0xA4 ? "(limit) " : "") + ensureVariableValidWithArray(state, argv, p1) + " = " + val + ";";
        } else if (opcode == 0xB0) { // JMP / JUMP
            return "Jump to " + jumpToString(state);
        } else if (opcode == 0xB3) { // JSR
            ScriptWorker worker = parentScript.getWorker(argv);
            return "Jump to subroutine " + (worker != null ? worker.getIndexLabel() : ("w" + StringHelper.formatHex2(argv)));
        } else if (opcode == 0xD6) { // POPXCJMP / SET_BNEZ
            return "(" + p1 + ") -> " + jumpToString(state);
        } else if (opcode == 0xD7) { // POPXNCJMP / SET_BEZ
            return "Check (" + p1 + ") else jump to " + jumpToString(state);
        } else if (opcode == 0xF6) { // SYSTEM
            return "System " + StringHelper.formatHex2(argv);
        }
        StackObject stackObject = toStackObject(state, new Stack<>());
        if (stackObject != null) {
            return "Stack Object: " + stackObject;
        } else {
            return null;
        }
    }

    private String jumpToString(ScriptState state) {
        ScriptJump jump = parentLine.branch;
        if (jump == null) {
            return "j" + StringHelper.formatHex2(argv);
        }
        if (state.writeJumpsAsIndexedLines) {
            if (jump.targetLine == null) {
                return "<No target line>";
            } else if (jump.targetLine.parentWorker == parentWorker) {
                int lineIndex = state.lines.indexOf(jump.targetLine);
                if (lineIndex >= 0) {
                    return "Line #" + lineIndex;
                } else {
                    return "<Unknown target line>";
                }
            } else {
                int lineIndex = jump.targetLine.parentWorker.getLines().indexOf(jump.targetLine);
                return "Script " + jump.targetLine.parentWorker.getIndexLabel() + " Line #" + lineIndex;
            }
        } else {
            return jump.getLabelWithAddr();
        }
    }

    public StackObject toStackObject(ScriptState state, Stack<StackObject> stack) {
        inputsToStackObjects(state, stack);
        if (opcode == 0xB5) { // CALL / FUNC_RET
            List<StackObject> params = popParamsForFunc(stack);
            ScriptFunc func = getFunc(params);
            StackObject stackObject = new StackObject(parentWorker, this, func.getType(params), true, func.callB5(params, state));
            stackObject.referenceIndex = argv;
            return stackObject;
        }
        StackObject p1 = null, p2 = null, p3 = null;
        switch (getStackPops()) {
            case 3: p3 = stack.pop();
            case 2: p2 = stack.pop();
            case 1: p1 = stack.pop();
            case 0:
            default:
                break;
        }
        if (opcode >= 0x01 && opcode <= 0x18) {
            ScriptOpcode op = ScriptOpcode.OPCODES[opcode];
            String resultType = op.type;
            String p1s = p1.toString();
            String p2s = p2.toString();

            if (opcode == 0x03 || opcode == 0x05 || opcode == 0x06 || opcode == 0x07) {
                String p1t = resolveType(state, p1);
                String p2t = resolveType(state, p2);
                boolean p1w = !p1.expression && isWeakType(p1t);
                boolean p2w = !p2.expression && isWeakType(p2t);
                if (INFER_BITWISE_OPS_AS_BITFIELDS && (opcode == 0x03 || opcode == 0x05)) {
                    if (p1w) {
                        p1t = opcode == 0x05 && inferIsNegationValue(p1) ? "bitfieldNegated" : "bitfield";
                        p1 = new StackObject(p1t, p1);
                        p1s = p1.toString();
                    }
                    if (p2w) {
                        p2t = opcode == 0x05 && inferIsNegationValue(p2) ? "bitfieldNegated" : "bitfield";
                        p2 = new StackObject(p2t, p2);
                        p2s = p2.toString();
                    }
                }
                if (isWeakType(p1t) && !isWeakType(p2t)) {
                    if (opcode == 0x05 && ("bitfield".equals(p2t) || p2t.endsWith("Bitfield")) && inferIsNegationValue(p1)) {
                        p1s = typed(p1, p2t + "Negated");
                    } else {
                        p1s = typed(p1, p2t);
                    }
                } else if (isWeakType(p2t) && !isWeakType(p1t)) {
                    if (opcode == 0x05 && ("bitfield".equals(p1t) || p1t.endsWith("Bitfield")) && inferIsNegationValue(p2)) {
                        p2s = typed(p2, p1t + "Negated");
                    } else {
                        p2s = typed(p2, p1t);
                    }
                }
            }
            if (p1.maybeBracketize) {
                p1s = '(' + p1s + ')';
            }
            if (p2.maybeBracketize) {
                p2s = '(' + p2s + ')';
            }
            String content = String.format(op.format, p1s, p2s);
            StackObject stackObject = new StackObject(parentWorker, this, resultType, true, content);
            stackObject.maybeBracketize = true;
            return stackObject;
        } else if (opcode == 0x19) { // OPNOT / NOT_LOGIC
            String p1s = p1.toString();
            if (p1.maybeBracketize) {
                p1s = '(' + p1s + ')';
            }
            return new StackObject(parentWorker, this, "bool", true, "!" + p1s);
        } else if (opcode == 0x1A) { // OPUMINUS / NEG
            String p1s = p1.toString();
            if (p1.maybeBracketize) {
                p1s = '(' + p1s + ')';
            }
            return new StackObject(parentWorker, this, p1.type, true, "-" + p1s);
        } else if (opcode == 0x1C) { // OPBNOT / NOT
            String p1s = p1.toString();
            if (p1.maybeBracketize) {
                p1s = '(' + p1s + ')';
            }
            return new StackObject(parentWorker, this, p1.type, true, "~" + p1s);
        } else if (opcode == 0x26) { // PUSHA / GET_RETURN_VALUE
            return new StackObject(parentWorker, this, state.rAType, true, "LastCallResult");
        } else if (opcode == 0x28) { // PUSHX / GET_TEST
            return new StackObject(parentWorker, this, state.rXType, true, "test");
        } else if (opcode == 0x29) { // PUSHY / GET_CASE
            return new StackObject(parentWorker, this, state.rYType, true, "case");
        } else if (opcode == 0x2B) { // REPUSH / COPY
            if ("float".equals(resolveType(state, p1))) {
                addWarning("Repush of float value does not work!");
            }
            stack.push(new StackObject(p1.type, p1));
            return new StackObject(p1.type, p1);
        } else if (opcode >= 0x36 && opcode <= 0x38) { // REQ / SIG_NOACK
            String cmd = "run";
            if (opcode == 0x37) { // REQSW / SIG_ONSTART
                cmd += "AndAwaitStart";
            } else if (opcode == 0x38) { // REQEW / SIG_ONEND
                cmd += "AndAwaitEnd";
            }
            String level = p1.expression ? ""+p1 : ""+p1.valueSigned;
            ScriptWorker targetWorker = parentScript.getWorker(p2.valueSigned);
            ScriptJump entryPoint = targetWorker != null ? targetWorker.getEntryPoint(p3.valueSigned) : null;
            boolean direct = !p2.expression && !p3.expression && isWeakType(p2.type) && isWeakType(p3.type) && entryPoint != null;
            String s = p2.expression ? "(" + p2 + ")" : String.format((p2.valueSigned & 0xFF00) != 0 ? "%04X" : "%02X", p2.valueSigned);
            String e = p3.expression ? "(" + p3 + ")" : String.format((p3.valueSigned & 0xFF00) != 0 ? "%04X" : "%02X", p3.valueSigned);
            String scriptLabel = direct ? entryPoint.getLabel() : ("w" + s + "e" + e);
            String content = cmd + " " + scriptLabel + " (Level " + level + ")";
            return new StackObject(parentWorker, this, "worker", true, content);
        } else if (opcode == 0x39) { // PREQ
            String content = "PREQ(" + p1 + ", " + p2 + ", " + p3 + ")";
            return new StackObject(parentWorker, this, "unknown", true, content);
        } else if (opcode == 0x46) { // TREQ
            String content = "TREQ(" + p1 + ", " + p2 + ", " + p3 + ")";
            return new StackObject(parentWorker, this, "unknown", true, content);
        } else if (opcode >= 0x67 && opcode <= 0x6A) { // PUSHI0..3 / GET_INT
            int tempIndex = opcode - 0x67;
            StackObject stackObject = new StackObject(parentWorker, this, "tmpI", true, "tmpI" + tempIndex);
            stackObject.referenceIndex = tempIndex;
            return stackObject;
        } else if (opcode >= 0x6B && opcode <= 0x74) { // PUSHF0..9 / GET_FLOAT
            int tempIndex = opcode - 0x6B;
            StackObject stackObject = new StackObject(parentWorker, this, "float", true, "tmpF" + tempIndex);
            stackObject.referenceIndex = tempIndex;
            return stackObject;
        } else if (opcode == 0x9F) { // PUSHV / GET_DATUM
            StackObject stackObject = new StackObject(parentWorker, this, "var", true, getVariableLabel(argv));
            stackObject.referenceIndex = argv;
            return stackObject;
        } else if (opcode == 0xA2) { // PUSHAR / GET_DATUM_INDEX
            StackObject stackObject = new StackObject(parentWorker, this, "var", true, ensureVariableValidWithArray(state, argv, p1));
            stackObject.referenceIndex = argv;
            return stackObject;
        } else if (opcode == 0xA7) { // PUSHARP / GET_DATUM_DESC
            StackObject stackObject = new StackObject(parentWorker, this, "pointer", true, "&" + ensureVariableValidWithArray(state, argv, p1));
            stackObject.referenceIndex = argv;
            return stackObject;
        } else if (opcode == 0xAD) { // PUSHI / CONST_INT
            int refInt = dereferencedArg != null ? dereferencedArg : parentWorker.refInts[argv];
            StackObject stackObject = new StackObject(parentWorker, this, "int32", refInt, refInt);
            stackObject.referenceIndex = argv;
            return stackObject;
        } else if (opcode == 0xAE) { // PUSHII / IMM
            return new StackObject(parentWorker, this, "int16", argvSigned, argv);
        } else if (opcode == 0xAF) { // PUSHF / CONST_FLOAT
            int refFloat = dereferencedArg != null ? dereferencedArg : parentWorker.refFloats[argv];
            StackObject stackObject = new StackObject(parentWorker, this, "float", refFloat, refFloat);
            stackObject.referenceIndex = argv;
            return stackObject;
        }
        return null;
    }

    private void inputsToStackObjects(ScriptState state, Stack<StackObject> stack) {
        for (ScriptInstruction input : inputs) {
            StackObject item = input.toStackObject(state, stack);
            if (item != null) {
                stack.push(item);
            }
        }
    }

    private List<StackObject> popParamsForFunc(Stack<StackObject> stack) {
        List<StackObject> params = new ArrayList<>();
        try {
            int functionParamCount = getStackPops();
            switch (functionParamCount) {
                case 9: params.add(0, stack.pop());
                case 8: params.add(0, stack.pop());
                case 7: params.add(0, stack.pop());
                case 6: params.add(0, stack.pop());
                case 5: params.add(0, stack.pop());
                case 4: params.add(0, stack.pop());
                case 3: params.add(0, stack.pop());
                case 2: params.add(0, stack.pop());
                case 1: params.add(0, stack.pop());
                case 0:
                default:
                    break;
            }
        } catch (EmptyStackException e) {
            addWarning("Empty stack for func " + StringHelper.formatHex4(argv));
        }
        return params;
    }

    private ScriptFunc getFunc(List<StackObject> params) {
        ScriptFunc func = ScriptFuncLib.FFX.get(argv, params);
        if (func == null) {
            func = new ScriptFunc("Unknown:" + StringHelper.formatHex4(argv), "unknown", null, false);
        }
        return func;
    }

    protected String resolveType(ScriptState state, StackObject obj) {
        if (obj == null || obj.type == null) {
            return "unknown";
        }
        if ("var".equals(obj.type)) {
            return getVariableType(obj.referenceIndex);
        }
        if ("tmpI".equals(obj.type)) {
            return getTempIType(state, obj.referenceIndex);
        }
        return obj.type;
    }

    protected static boolean isWeakType(String type) {
        return type == null || "unknown".equals(type);
    }

    protected String typed(StackObject obj, String type) {
        if (obj == null) {
            return type + ":null";
        } else {
            if (obj.expression || type == null || "unknown".equals(type)) {
                return obj.toString();
            } else {
                return new StackObject(type, obj).toString();
            }
        }
    }

    public String getVariableType(int index) {
        ScriptVariable variable = parentWorker.getVariable(index);
        if (variable != null) {
            return variable.getType();
        }
        addWarning("Variable index " + StringHelper.formatHex2(index) + " out of bounds!");
        return "unknown";
    }

    public void gatherDirectWorkerReferences(ScriptState state, Set<ScriptInstruction> gathered) {
        if (inputs == null || inputs.isEmpty()) {
            return;
        }
        for (int i = 0; i < inputs.size(); i++) {
            ScriptInstruction input = getInput(i);
            if (input.opcode == 0xAE && "worker".equals(getInputType(state, i))) {
                gathered.add(input);
            } else {
                input.gatherDirectWorkerReferences(state, gathered);
            }
        }
    }

    public String getVariableIndexType(int index) {
        ScriptVariable variable = parentWorker.getVariable(index);
        if (variable != null) {
            return variable.getArrayIndexType();
        }
        addWarning("Variable index " + StringHelper.formatHex2(index) + " out of bounds!");
        return "unknown";
    }

    public String getTempIType(ScriptState state, int index) {
        return state.tempITypes.getOrDefault(index, "unknown");
    }

    public String getVariableLabel(int index) {
        ScriptVariable variable = parentWorker.getVariable(index);
        if (variable != null) {
            return variable.getLabel(parentWorker);
        }
        String hexIdx = StringHelper.formatHex2(index);
        addWarning("Variable index " + hexIdx + " out of bounds!");
        return "var" + hexIdx;
    }

    private String ensureVariableValidWithArray(ScriptState state, int index, StackObject p1) {
        String varLabel = getVariableLabel(index);
        String indexType = resolveType(state, p1);
        ScriptVariable variable = parentWorker.getVariable(index);
        if (isWeakType(indexType) && variable != null) {
            indexType = variable.getArrayIndexType();
        }
        String arrayIndex = !p1.expression && isWeakType(indexType) ? ""+p1.valueSigned : typed(p1, indexType);
        return varLabel + '[' + arrayIndex + ']';
    }

    private boolean inferIsNegationValue(StackObject obj) {
        int inactiveBits = StackObject.negatedBitfieldToList(obj.type, obj.valueUnsigned).size();
        if (inactiveBits == 1) {
            return true;
        }
        int activeBits = StackObject.bitfieldToList(obj.type, obj.valueUnsigned).size();
        return inactiveBits < activeBits;
    }

    private void addWarning(String warn) {
        parentLine.warnings.add(warn);
    }
}
