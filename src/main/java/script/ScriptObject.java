package script;

import reading.Chunk;
import script.model.*;

import java.util.*;
import java.util.stream.Collectors;

import static main.StringHelper.*;

public class ScriptObject {
    private static final int JUMP_LINE_MINLENGTH = 16;
    private static final int HEX_LINE_MINLENGTH = COLORS_USE_CONSOLE_CODES ? 58 : 48;
    private static final int JUMP_PLUS_HEX_LINE_MINLENGTH = JUMP_LINE_MINLENGTH + HEX_LINE_MINLENGTH + 1;

    protected int[] bytes;
    protected int[] actualScriptCodeBytes;
    protected int absoluteOffset;
    protected int byteCursor = 0;
    protected List<ScriptHeader> headers;
    protected int[] refFloats;
    protected int[] refInts;
    protected ScriptVariable[] variableDeclarations;
    protected int floatTableOffset;
    protected int intTableOffset;
    protected int variableStructsTableOffset;
    protected int scriptCodeLength;
    protected int scriptCodeStartAddress;
    protected int scriptCodeEndAddress;
    public List<String> strings;
    public StringBuilder jumpTableString = new StringBuilder();
    Stack<StackObject> stack = new Stack<>();
    Map<Integer, String> currentTempITypes = new HashMap<>();
    Map<Integer, String> varTypes = new HashMap<>();
    Map<Integer, List<StackObject>> varEnums = new HashMap<>();
    Map<Integer, StackObject> constants = new HashMap<>();
    int currentScriptIndex = 0;
    String currentRAType = "unknown";
    String currentRXType = "unknown";
    String currentRYType = "unknown";
    boolean gatheringInfo = true;
    List<ScriptJump> scriptJumps = new ArrayList<>();
    Map<Integer, List<ScriptJump>> scriptJumpsByDestination = new HashMap<>();

    int lineCount = 0;
    String textScriptLine;
    String warnLine;
    List<String> offsetLines;
    List<String> textScriptLines;
    List<String> hexScriptLines;
    List<String> jumpLines;
    List<String> warnLines;
    List<ScriptInstruction> instructions = new ArrayList<>();

    public ScriptObject(Chunk chunk) {
        this.bytes = chunk.bytes;
        this.absoluteOffset = chunk.offset;
    }

    public ScriptObject(int[] bytes, int absoluteOffset) {
        this.bytes = bytes;
        this.absoluteOffset = absoluteOffset;
    }

    public void parseScript(List<String> strings) {
        this.strings = strings;
        byteCursor = 0;
        scriptCodeLength = read4Bytes(0x0);
        // System.out.println("Script Length: " + scriptCodeLength);
        int map_start = read4Bytes(0x4);
        int creatorTagAddress = read4Bytes(0x8);
        int event_name_start = read4Bytes(0xC);
        int jumpsEndAddress = read4Bytes(0x10);
        int obj_5b8_count = read2Bytes(0x14);
        int obj_558_count = read2Bytes(0x16);
        int main_script_index = read2Bytes(0x18);
        int unk01 = read2Bytes(0x1A);
        int obj_2e8_count = read2Bytes(0x1C);
        int zoneBytes = read2Bytes(0x1E);
        int event_data_offset = read4Bytes(0x20);
        int unk02 = read4Bytes(0x24);
        int area_offset = read4Bytes(0x28);
        int other_offset = read4Bytes(0x2C);
        scriptCodeStartAddress = read4Bytes(0x30);
        int numberOfScripts = read2Bytes(0x34);
        int numberOfScriptsWithoutSubroutines = read2Bytes(0x36);
        // System.out.println("Number of Scripts: " + numberOfScripts + " / " + numberOfScripts2);
        int[] scriptHeaderOffsets = new int[numberOfScripts];
        for (int i = 0; i < numberOfScripts; i++) {
            scriptHeaderOffsets[i] = read4Bytes(0x38 + i * 4);
        }
        int scriptIndex = 0;
        headers = new ArrayList<>();
        for (int headerStart : scriptHeaderOffsets) {
            ScriptHeader scriptHeader = parseScriptHeader(headerStart);
            parseScriptJumps(scriptHeader, scriptIndex);
            scriptIndex++;
            headers.add(scriptHeader);
        }
        parseVarIntFloatTables(headers);
        scriptCodeEndAddress = scriptCodeStartAddress + scriptCodeLength;
        actualScriptCodeBytes = Arrays.copyOfRange(bytes, scriptCodeStartAddress, scriptCodeEndAddress);
        parseScriptCode();
        if (!stack.empty()) {
            System.err.println("Stack not empty: " + stack.size() + " els: " + stack.stream().map(StackObject::toString).collect(Collectors.joining("::")));
        }
        inferEnums();
        gatheringInfo = false;
        parseScriptCode();
    }

    private ScriptHeader parseScriptHeader(int offset) {
        ScriptHeader header = new ScriptHeader();
        header.scriptType = read2Bytes(offset);
        header.variablesCount = read2Bytes(offset+0x02);
        header.refIntCount = read2Bytes(offset+0x04);
        header.refFloatCount = read2Bytes(offset+0x06);
        header.entryPointCount = read2Bytes(offset+0x08);
        header.jumpCount = read2Bytes(offset+0x0A);
        header.alwaysZero1 = read4Bytes(offset+0x0C);
        header.privateDataLength = read4Bytes(offset+0x10);
        header.variableStructsTableOffset = read4Bytes(offset+0x14);
        header.intTableOffset = read4Bytes(offset+0x18);
        header.floatTableOffset = read4Bytes(offset+0x1C);
        header.scriptEntryPointsOffset = read4Bytes(offset+0x20);
        header.jumpsOffset = read4Bytes(offset+0x24);
        header.alwaysZero2 = read4Bytes(offset+0x28);
        header.privateDataOffset = read4Bytes(offset+0x2C);
        header.sharedDataOffset = read4Bytes(offset+0x30);
        return header;
    }

    private void parseVarIntFloatTables(List<ScriptHeader> headers) {
        variableStructsTableOffset = -1;
        intTableOffset = -1;
        floatTableOffset = -1;
        for (ScriptHeader h : headers) {
            if (variableStructsTableOffset < 0) {
                variableStructsTableOffset = h.variableStructsTableOffset;
                byteCursor = h.variableStructsTableOffset;
                variableDeclarations = new ScriptVariable[h.variablesCount];
                for (int i = 0; i < h.variablesCount; i++) {
                    int lb = read4Bytes(variableStructsTableOffset + i * 8);
                    int hb = read4Bytes(variableStructsTableOffset + i * 8 + 4);
                    ScriptVariable scriptVariable = new ScriptVariable(i, lb, hb);
                    if (scriptVariable.location == 4) {
                        scriptVariable.parseValues(this, bytes, h.sharedDataOffset);
                    }
                    variableDeclarations[i] = scriptVariable;
                }
                h.variableDeclarations = variableDeclarations;
                h.setVariableInitialValues(this, bytes);
            } else if (h.variableStructsTableOffset != variableStructsTableOffset || h.variablesCount != variableDeclarations.length) {
                System.err.println("WARNING, variables table mismatch!");
            } else {
                h.variableDeclarations = variableDeclarations;
                h.setVariableInitialValues(this, bytes);
            }
            if (intTableOffset < 0) {
                intTableOffset = h.intTableOffset;
                refInts = new int[h.refIntCount];
                for (int i = 0; i < h.refIntCount; i++) {
                    refInts[i] = read4Bytes(intTableOffset + i * 4);
                }
                h.refInts = refInts;
            } else if (h.intTableOffset != intTableOffset || h.refIntCount != refInts.length) {
                System.err.println("WARNING, int table mismatch!");
            } else {
                h.refInts = refInts;
            }
            if (floatTableOffset < 0) {
                floatTableOffset = h.floatTableOffset;
                refFloats = new int[h.refFloatCount];
                for (int i = 0; i < h.refFloatCount; i++) {
                    refFloats[i] = read4Bytes(floatTableOffset + i * 4);
                }
                h.refFloats = refFloats;
            } else if (h.floatTableOffset != floatTableOffset || h.refFloatCount != refFloats.length) {
                System.err.println("WARNING, float table mismatch!");
            } else {
                h.refFloats = refFloats;
            }
        }
    }

    private void parseScriptJumps(ScriptHeader header, int scriptIndex) {
        String sPrefix = "s" + format2Or4Byte(scriptIndex);
        jumpTableString.append(sPrefix).append('\n');
        int entryPointCount = header.entryPointCount;
        ScriptJump[] entryPoints = new ScriptJump[entryPointCount];
        for (int i = 0; i < entryPointCount; i++) {
            int addr = read4Bytes(header.scriptEntryPointsOffset + i * 4);
            ScriptJump entryPoint = new ScriptJump(addr, scriptIndex, i, true);
            entryPoints[i] = entryPoint;
            scriptJumps.add(entryPoint);
            String epSuffix = "e" + format2Or4Byte(i);
            if (!scriptJumpsByDestination.containsKey(addr)) {
                scriptJumpsByDestination.put(addr, new ArrayList<>());
            }
            scriptJumpsByDestination.get(addr).add(entryPoint);
            jumpTableString.append(epSuffix).append('=').append(String.format("%04X", addr)).append(' ');
        }
        header.entryPoints = entryPoints;
        jumpTableString.append('\n');
        int jumpCount = header.jumpCount;
        ScriptJump[] jumps = new ScriptJump[jumpCount];
        for (int i = 0; i < jumpCount; i++) {
            int addr = read4Bytes(header.jumpsOffset + i * 4);
            ScriptJump jump = new ScriptJump(addr, scriptIndex, i, false);
            jumps[i] = jump;
            scriptJumps.add(jump);
            String jSuffix = "j" + format2Or4Byte(i);
            if (!scriptJumpsByDestination.containsKey(addr)) {
                scriptJumpsByDestination.put(addr, new ArrayList<>());
            }
            scriptJumpsByDestination.get(addr).add(jump);
            jumpTableString.append(jSuffix).append('=').append(String.format("%04X", addr)).append(' ');
        }
        header.jumps = jumps;
        jumpTableString.append('\n');
    }

    protected void parseScriptCode() {
        lineCount = 0;
        instructions = new ArrayList<>();
        offsetLines = new ArrayList<>();
        hexScriptLines = new ArrayList<>();
        textScriptLines = new ArrayList<>();
        jumpLines = new ArrayList<>();
        warnLines = new ArrayList<>();
        List<ScriptInstruction> lineInstructions = new ArrayList<>();
        List<ScriptJump> jumpsOnLine = new ArrayList<>();
        textScriptLine = "";
        warnLine = "";
        int opcode;
        int nextLineOffset = 0;
        byteCursor = scriptCodeStartAddress;
        while (byteCursor < scriptCodeEndAddress) {
            opcode = nextAiByte(jumpsOnLine);
            ScriptInstruction instruction;
            switch (getArgc(opcode)) {
                case 2:
                    final int arg1 = nextAiByte(jumpsOnLine);
                    final int arg2 = nextAiByte(jumpsOnLine);
                    instruction = new ScriptInstruction(opcode, arg1, arg2);
                    break;
                case 0:
                default:
                    instruction = new ScriptInstruction(opcode);
                    break;
            }
            processInstruction(instruction);
            lineInstructions.add(instruction);
            instructions.add(instruction);
            if (getLineEnd(opcode)) {
                if (!stack.empty()) {
                    warnLine += " Stack not empty (" + stack.size() + "): " + stack;
                    stack.clear();
                }
                lineCount++;
                offsetLines.add(String.format("%04X", nextLineOffset));
                nextLineOffset = byteCursor - scriptCodeStartAddress;
                textScriptLines.add(textScriptLine);
                hexScriptLines.add(getHexLine(lineInstructions));
                jumpLines.add(getJumpLine(jumpsOnLine));
                warnLines.add(warnLine);
                textScriptLine = "";
                jumpsOnLine.clear();
                lineInstructions.clear();
                warnLine = "";
            }
        }
    }

    private static String getJumpLine(List<ScriptJump> jumpsOnLine) {
        if (jumpsOnLine == null || jumpsOnLine.isEmpty()) {
            return "";
        }
        return jumpsOnLine.stream().map(j -> j.getLabel()).collect(Collectors.joining(",")) + ':';
    }

    private static String getHexLine(List<ScriptInstruction> lineInstructions) {
        Iterator<ScriptInstruction> iterator = lineInstructions.iterator();
        int zeroesInARow = 0;
        List<String> segments = new ArrayList<>();
        while (iterator.hasNext()) {
            ScriptInstruction ins = iterator.next();
            if (ins.opcode == 0x00) {
                zeroesInARow++;
                if (zeroesInARow == 1) {
                    segments.add("00");
                } else if (zeroesInARow == 2) {
                    segments.add("...");
                }
            } else {
                if (zeroesInARow > 1) {
                    segments.add("00");
                }
                zeroesInARow = 0;
                segments.add(ins.asHexString());
            }
        }
        if (zeroesInARow > 1) {
            segments.add("00");
        }
        return String.join(" ", segments);
    }

    protected int nextAiByte(List<ScriptJump> jumpsOnLine) {
        int scriptCodeByteCursor = byteCursor - scriptCodeStartAddress;
        if (scriptJumpsByDestination.containsKey(scriptCodeByteCursor)) {
            List<ScriptJump> jumps = scriptJumpsByDestination.get(scriptCodeByteCursor);
            contextualizeJumps(jumps);
            jumpsOnLine.addAll(jumps);
        }
        return readByte();
    }

    protected void contextualizeJumps(List<ScriptJump> jumps) {
        jumps.stream().filter(j -> j.isEntryPoint).findFirst().ifPresent(j -> currentScriptIndex = j.scriptIndex);
        jumps.stream().filter(j -> j.rAType != null && !"unknown".equals(j.rAType)).findFirst().ifPresent(j -> currentRAType = j.rAType);
        jumps.stream().filter(j -> j.rXType != null && !"unknown".equals(j.rXType)).findFirst().ifPresent(j -> currentRXType = j.rXType);
        jumps.stream().filter(j -> j.rYType != null && !"unknown".equals(j.rYType)).findFirst().ifPresent(j -> currentRYType = j.rYType);
        jumps.stream().filter(j -> j.tempITypes != null).flatMap(j -> j.tempITypes.entrySet().stream()).filter(s -> s.getValue() != null && !"unknown".equals(s.getValue())).forEach(s -> currentTempITypes.put(s.getKey(), s.getValue()));
    }

    protected void processInstruction(ScriptInstruction ins) {
        final int opcode = ins.opcode;
        final int argv = ins.argv;
        final String argvsh = ins.argvsh;
        StackObject p1 = null, p2 = null, p3 = null;
        try {
            switch (getStackPops(opcode)) {
                case 3: p3 = stack.pop();
                case 2: p2 = stack.pop();
                case 1: p1 = stack.pop();
                case 0:
                default:
                    break;
            }
        } catch (EmptyStackException e) {
            warnLine += " Empty stack for opcode " + String.format("%02X", opcode);
            return;
        }
        if (opcode == 0x00 || opcode == 0x1D || opcode == 0x1E) { // NOP, LABEL, TAG
            // No handling yet, they should probably be written a certain way parsed out but are never actually used
        } else if (opcode >= 0x01 && opcode <= 0x18) {
            ScriptField op = ScriptConstants.COMP_OPERATORS.get(opcode);
            String resultType = op.type;
            String p1s = p1.toString();
            String p2s = p2.toString();

            if (opcode == 0x06 || opcode == 0x07) {
                String p1t = resolveType(p1);
                String p2t = resolveType(p2);
                boolean p1w = isWeakType(p1t);
                boolean p2w = isWeakType(p2t);
                if (p1w && !p2w) {
                    p1s = typed(p1, p2t);
                } else if (p2w && !p1w) {
                    p2s = typed(p2, p1t);
                }
            }
            if (p1.maybeBracketize) {
                p1s = '(' + p1s + ')';
            }
            if (p2.maybeBracketize) {
                p2s = '(' + p2s + ')';
            }
            String content = p1s + ' ' + op.name + ' ' + p2s;
            StackObject stackObject = new StackObject(this, resultType, true, content, opcode);
            stackObject.maybeBracketize = true;
            stack.push(stackObject);
        } else if (opcode == 0x19) { // OPNOT / NOT_LOGIC
            stack.push(new StackObject(this, "bool", true, "not " + p1, 0x19));
        } else if (opcode == 0x1A) { // OPUMINUS / NEG
            stack.push(new StackObject(this, p1.type, true, "-(" + p1 + ")", 0x1A));
        } else if (opcode == 0x1C) { // OPBNOT / NOT
            stack.push(new StackObject(this, p1.type, true, "~(" + p1 + ")", 0x1C));
        } else if (opcode == 0x25) { // POPA / SET_RETURN_VALUE
            textScriptLine += p1 + ";";
            currentRAType = resolveType(p1);
        } else if (opcode == 0x26) { // PUSHA / GET_RETURN_VALUE
            stack.push(new StackObject(this, currentRAType, true, "LastCallResult", 0x26));
        } else if (opcode == 0x28) { // PUSHX / GET_TEST
            stack.push(new StackObject(this, currentRXType, true, "rX", 0x28));
        } else if (opcode == 0x29) { // PUSHY / GET_CASE
            stack.push(new StackObject(this, currentRYType, true, "case", 0x29));
        } else if (opcode == 0x2A) { // POPX / SET_TEST
            textScriptLine += "Set rX = " + p1;
            currentRXType = resolveType(p1);
        } else if (opcode == 0x2B) { // REPUSH / COPY
            stack.push(new StackObject(p1.type, p1));
            stack.push(new StackObject(p1.type, p1));
        } else if (opcode == 0x2C) { // POPY / SET_CASE
            textScriptLine += "switch " + p1;
            currentRYType = resolveType(p1);
        } else if (opcode == 0x34) { // RTS / RETURN
            textScriptLine += "return from subroutine;";
            resetRegisterTypes();
        } else if (opcode >= 0x36 && opcode <= 0x38) { // REQ / SIG_NOACK
            String cmd = "run";
            if (opcode == 0x37) { // REQSW / SIG_ONSTART
                cmd += "Async";
            } else if (opcode == 0x38) { // REQEW / SIG_ONEND
                cmd += "Sync";
            }
            String i = p1.expression ? ""+p1 : ""+p1.value;
            String s = p2.expression ? "(" + p2 + ")" : format2Or4Byte(p2.value);
            String e = p3.expression ? "(" + p3 + ")" : format2Or4Byte(p3.value);
            String sep = "s" + s + "e" + e;
            String content = cmd + " " + sep + " (" + i + ")";
            stack.push(new StackObject(this, "script", true, content, opcode));
        } else if (opcode == 0x39) { // PREQ
            String content = "PREQ(" + p1 + ", " + p2 + ", " + p3 + ")";
            stack.push(new StackObject(this, "unknown", true, content, 0x39));
        } else if (opcode == 0x3C) { // RET / END
            textScriptLine += "return;";
            resetRegisterTypes();
        } else if (opcode == 0x3D) { // Never used: RETN / CLEANUP_END
        } else if (opcode == 0x3E) { // Never used: RETT / TO_MAIN
        } else if (opcode == 0x3F) { // RETTN / CLEANUP_TO_MAIN
            textScriptLine += "return (RETTN): " + p1;
            resetRegisterTypes();
        } else if (opcode == 0x40) { // HALT / DYNAMIC
            textScriptLine += "halt";
        } else if (opcode == 0x46) { // TREQ
            String content = "TREQ(" + p1 + ", " + p2 + ", " + p3 + ")";
            stack.push(new StackObject(this, "unknown", true, content, 0x46));
        } else if (opcode == 0x54) { // DRET / CLEANUP_ALL_END
            textScriptLine += "direct return;";
            resetRegisterTypes();
        } else if (opcode >= 0x59 && opcode <= 0x5C) { // POPI0..3 / SET_INT
            currentTempITypes.put(opcode-0x59, resolveType(p1));
            textScriptLine += "tempI" + (opcode-0x59) + " = " + p1 + ";";
        } else if (opcode >= 0x5D && opcode <= 0x66) { // POPF0..9 / SET_FLOAT
            textScriptLine += "tempF" + (opcode-0x5D) + " = " + p1 + ";";
        } else if (opcode >= 0x67 && opcode <= 0x6A) { // PUSHI0..3 / GET_INT
            int tempIndex = opcode - 0x67;
            StackObject stackObject = new StackObject(this, currentTempITypes.getOrDefault(tempIndex, "unknown"), true, "tempI" + tempIndex, opcode);
            stackObject.referenceIndex = tempIndex;
            stack.push(stackObject);
        } else if (opcode >= 0x6B && opcode <= 0x74) { // PUSHF0..9 / GET_FLOAT
            int tempIndex = opcode - 0x6B;
            StackObject stackObject = new StackObject(this, "float", true, "tempF" + tempIndex, opcode);
            stackObject.referenceIndex = tempIndex;
            stack.push(stackObject);
        } else if (opcode == 0x77) { // REQWAIT / WAIT_DELETE
            String s = p1.expression ? "(" + p1 + ")" : format2Or4Byte(p1.value);
            String e = p2.expression ? "(" + p2 + ")" : format2Or4Byte(p2.value);
            String sep = "s" + s + "e" + e;
            textScriptLine += "await " + sep + ";";
        } else if (opcode == 0x78) { // Never used: PREQWAIT / WAIT_SPEC_DELETE
        } else if (opcode == 0x79) { // REQCHG / EDIT_ENTRY_TABLE
            textScriptLine += "REQCHG(" + p1 + ", " + p2 + ", " + p3 + ");";
        } else if (opcode == 0x7A) { // Never used: ACTREQ / SET_EDGE_TRIGGER
        } else if (opcode == 0x9F) { // PUSHV / GET_DATUM
            StackObject stackObject = new StackObject(this, "var", true, ensureVariableValid(argv), argv);
            stackObject.referenceIndex = argv;
            stack.push(stackObject);
        } else if (opcode == 0xA0 || opcode == 0xA1) { // POPV(L) / SET_DATUM_(W/T)
            addVarType(argv, resolveType(p1));
            if (gatheringInfo) {
                if (!varEnums.containsKey(argv)) {
                    varEnums.put(argv, new ArrayList<>());
                }
                varEnums.get(argv).add(p1);
            }
            textScriptLine += "Set ";
            if (opcode == 0xA1) {
                textScriptLine += "(limit) ";
            }
            String val = typed(p1, varTypes.get(argv));
            textScriptLine += ensureVariableValid(argv) + " = " + val;
        } else if (opcode == 0xA2) { // PUSHAR / GET_DATUM_INDEX
            String arrayIndex = '[' + ("int16".equals(p1.type) && !p1.expression ? String.format("%04X", p1.value) : p1.toString()) + ']';
            StackObject stackObject = new StackObject(this, "var", true, ensureVariableValid(argv) + arrayIndex, argv);
            stackObject.referenceIndex = argv;
            stack.push(stackObject);
        } else if (opcode == 0xA3 || opcode == 0xA4) { // POPAR(L) / SET_DATUM_INDEX_(W/T)
            String arrayIndex = '[' + ("int16".equals(p1.type) && !p1.expression ? String.format("%04X", p1.value) : p1.toString()) + ']';
            textScriptLine += "Set ";
            if (opcode == 0xA4) {
                textScriptLine += "(limit) ";
            }
            textScriptLine += ensureVariableValid(argv) + arrayIndex + " = " + p2;
        } else if (opcode == 0xA7) { // PUSHARP / GET_DATUM_DESC
            String arrayIndex = '[' + String.format("%04X", p1.value) + ']';
            StackObject stackObject = new StackObject(this, "int16", true, "ArrayPointer:var" + argvsh + arrayIndex, argv);
            stackObject.referenceIndex = argv;
            stack.push(stackObject);
        } else if (opcode == 0xAD) { // PUSHI / CONST_INT
            int refInt = refInts[argv];
            String content = "rI[" + argvsh + "]:" + refInt + " [" + String.format("%08X", refInt) + "h]";
            StackObject stackObject = new StackObject(this, "uint32", false, content, refInt);
            stackObject.referenceIndex = argv;
            stack.push(stackObject);
        } else if (opcode == 0xAE) { // PUSHII / IMM
            stack.push(new StackObject(this, "int16", false, ins.argvSigned + " [" + argvsh + "h]", argv));
        } else if (opcode == 0xAF) { // PUSHF / CONST_FLOAT
            int refFloat = refFloats[argv];
            String content = "rF[" + argvsh + "]:" + Float.intBitsToFloat(refFloat) + " [" + String.format("%08X", refFloat) + "h]";
            StackObject stackObject = new StackObject(this, "float", false, content, refFloat);
            stackObject.referenceIndex = argv;
            stack.push(stackObject);
        } else if (opcode == 0xB0) { // JMP / JUMP
            textScriptLine += "Jump to j" + String.format("%02X", argv);
            scriptJumps.stream().filter(j -> j.scriptIndex == currentScriptIndex && j.jumpIndex == argv).forEach(j -> j.setTypes(currentRAType, currentRXType, currentRYType, currentTempITypes));
            resetRegisterTypes();
        } else if (opcode == 0xB1) { // Never used: CJMP / BNEZ
        } else if (opcode == 0xB2) { // Never used: NCJMP / BEZ
        } else if (opcode == 0xB3) { // JSR
            textScriptLine += "Jump to subroutine s" + String.format("%02X", argv);
        } else if (opcode == 0xB5) { // CALL / FUNC_RET
            processB5(argv);
        } else if (opcode == 0xD6) { // POPXCJMP / SET_BNEZ
            textScriptLine += "(" + p1 + ") -> j" + String.format("%02X", argv);
            scriptJumps.stream().filter(j -> j.scriptIndex == currentScriptIndex && j.jumpIndex == argv).forEach(j -> j.setTypes(currentRAType, currentRXType, currentRYType, currentTempITypes));
        } else if (opcode == 0xD7) { // POPXNCJMP / SET_BEZ
            textScriptLine += "Check (" + p1 + ") else jump to j" + String.format("%02X", argv);
            scriptJumps.stream().filter(j -> j.scriptIndex == currentScriptIndex && j.jumpIndex == argv).forEach(j -> j.setTypes(currentRAType, currentRXType, currentRYType, currentTempITypes));
        } else if (opcode == 0xD8) { // CALLPOPA / FUNC
            processD8(argv);
        }
    }

    protected List<StackObject> popParamsForFunc(int idx) {
        List<StackObject> params = new ArrayList<>();
        try {
            int functionParamCount = getFunctionParamCount(idx);
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
            warnLine += " Empty stack for func " + String.format("%04X", idx);
        }
        return params;
    }

    protected ScriptFunc getAndTypeFuncCall(int idx, List<StackObject> params) {
        ScriptFunc func = ScriptFuncLib.get(idx, params);
        if (func == null) {
            func = new ScriptFunc("Unknown:" + String.format("%04X", idx), "unknown", null, false);
        }
        List<ScriptField> inputs = func.inputs;
        if (inputs != null) {
            for (int i = 0; i < inputs.size() && i < params.size(); i++) {
                typed(params.get(i), inputs.get(i).type);
            }
        }
        return func;
    }

    protected void processB5(int idx) {
        List<StackObject> params = popParamsForFunc(idx);
        ScriptFunc func = getAndTypeFuncCall(idx, params);
        StackObject stackObject = new StackObject(this, func.getType(params), true, func.callB5(params), idx);
        stackObject.referenceIndex = idx;
        stack.push(stackObject);
    }

    protected void processD8(int idx) {
        List<StackObject> params = popParamsForFunc(idx);
        ScriptFunc func = getAndTypeFuncCall(idx, params);
        currentRAType = func.getType(params);
        String call = func.callD8(params);
        textScriptLine += call + ';';
    }

    protected void addVarType(int var, String type) {
        if (!gatheringInfo) {
            return;
        }
        if (!varTypes.containsKey(var)) {
            varTypes.put(var, type);
        } else {
            String prevType = varTypes.get(var);
            if (isWeakType(prevType)) {
                varTypes.put(var, type);
            }
        }
    }

    protected String resolveType(StackObject obj) {
        if (obj == null) {
            return "unknown";
        }
        if ("var".equals(obj.type)) {
            return varTypes.get(obj.value);
        }
        return obj.type;
    }

    protected static boolean isWeakType(String type) {
        return type == null || "unknown".equals(type) || type.isBlank() || type.startsWith("int") || type.startsWith("uint");
    }

    protected String typed(StackObject obj, String type) {
        if (obj == null) {
            return type + ":null";
        } else {
            if ("var".equals(obj.type)) {
                addVarType(obj.value, type);
            }
            if (obj.expression || "unknown".equals(type)) {
                return obj.toString();
            } else {
                return new StackObject(type, obj).toString();
            }
        }
    }

    private String ensureVariableValid(int index) {
        if (variableDeclarations == null || variableDeclarations.length <= index) {
            String hexIdx = String.format("%02X", index);
            warnLine += "Variable index " + hexIdx + " out of bounds!";
            return "var" + hexIdx;
        }
        return variableDeclarations[index].getLabel();
    }

    protected static int getArgc(int opcode) {
        if (opcode == 0xFF) {
            return 0;
        } else if (opcode >= 0x80) {
            return 2;
        } else {
            return 0;
        }
    }

    protected int getStackPops(int opcode) {
        int stackpops = ScriptConstants.OPCODE_STACKPOPS[opcode];
        if (stackpops < 0) {
            warnLine += " Undefined stackpops for opcode " + String.format("%02X", opcode);
            return 0;
        }
        return stackpops;
    }

    protected int getFunctionParamCount(int idx) {
        ScriptFunc func = ScriptFuncLib.get(idx, null);
        if (func == null) {
            warnLine += " Undefined stackpops for func " + String.format("%04X", idx);
            return 0;
        }
        return func.inputs != null ? func.inputs.size() : 0;
    }

    protected static boolean getLineEnd(int opcode) {
        return ScriptConstants.OPCODE_ENDLINE.contains(opcode);
    }

    protected void inferEnums() {
        for (Map.Entry<Integer, String> entry : varTypes.entrySet()) {
            Integer varIdx = entry.getKey();
            if (isWeakType(entry.getValue()) && varEnums.containsKey(varIdx)) {
                List<StackObject> enums = varEnums.get(varIdx);
                if (enums.size() == 1 && !enums.get(0).expression) {
                    constants.put(varIdx, enums.get(0));
                } else if (enums.stream().noneMatch(a -> a.expression)) {
                    Set<Integer> distinctContents = enums.stream().map(a -> a.value).collect(Collectors.toSet());
                    if (distinctContents.size() == 2 && distinctContents.contains(0) && (distinctContents.contains(0x01) || distinctContents.contains(0xFF))) {
                        varTypes.put(varIdx, "bool");
                    }
                }
            }
        }
    }

    private int readByte() {
        int rd = bytes[byteCursor];
        byteCursor++;
        return rd;
    }

    private int read2Bytes(int offset) {
        return bytes[offset] + bytes[offset+1] * 0x100;
    }

    private int read4Bytes(int offset) {
        return bytes[offset] + bytes[offset+1] * 0x100 + bytes[offset+2] * 0x10000 + bytes[offset+3] * 0x1000000;
    }

    private static String format2Or4Byte(int b) {
        return String.format(b > 0x100 ? "%04X" : "%02X", b);
    }

    private void resetRegisterTypes() {
        currentRAType = "unknown";
        currentRXType = "unknown";
        currentRYType = "unknown";
        currentTempITypes.clear();
    }

    public String getScriptStartAddressLine() {
        return "Script code starts at offset " + String.format("%04X", scriptCodeStartAddress + absoluteOffset);
    }

    public String allLinesString() {
        List<String> lines = new ArrayList<>();
        lines.add(getScriptStartAddressLine());
        for (int i = 0; i < lineCount; i++) {
            lines.add(fullLineString(i));
        }
        return String.join("\n", lines) + '\n';
    }

    public String fullLineString(int line) {
        String ol = String.format("%-5s", offsetLines.get(line) + ' ');
        String jl = consoleColorIfEnabled(ANSI_PURPLE) + String.format("%-" + JUMP_LINE_MINLENGTH + "s", jumpLines.get(line)) + ' ';
        String jhl = String.format("%-" + JUMP_PLUS_HEX_LINE_MINLENGTH + "s", jl + consoleColorIfEnabled(ANSI_BLUE) +  hexScriptLines.get(line)) + ' ';
        String tl = consoleColorIfEnabled(ANSI_RESET) + textScriptLines.get(line);
        String wl = consoleColorIfEnabled(ANSI_RED) + warnLines.get(line);
        return ol + jhl + tl + wl + consoleColorIfEnabled(ANSI_RESET);
    }

    public String allInstructionsAsmString() {
        List<String> lines = new ArrayList<>();
        lines.add(getScriptStartAddressLine());
        int offset = 0;
        for (ScriptInstruction ins : instructions) {
            String ol = String.format("%-6s", String.format("%04X", offset) + ' ');
            String jl = consoleColorIfEnabled(ANSI_PURPLE) + String.format("%-11s", getJumpLine(scriptJumpsByDestination.get(offset)) + ' ');
            String hl = consoleColorIfEnabled(ANSI_BLUE) + String.format("%-11s", ins.asSeparatedHexString() + ' ');
            String asml = consoleColorIfEnabled(ANSI_GREEN) + (ins.hasArgs ? String.format("%-10s", ins.getOpcodeLabel() + ' ') + consoleColorIfEnabled(ANSI_YELLOW) + ins.getArgLabel() : ins.getOpcodeLabel());
            lines.add(ol + jl + hl + asml + consoleColorIfEnabled(ANSI_RESET));
            offset += ins.length;
        }
        return String.join("\n", lines) + '\n';
    }

    public String headersString() {
        if (headers == null || headers.isEmpty()) {
            return "No Headers";
        }
        List<String> lines = new ArrayList<>();
        lines.add(headers.size() + " Headers Total");
        for (int i = 0; i < headers.size(); i++) {
            lines.add("s" + String.format("%02X", i) + ": " + headers.get(i).getNonCommonString());
        }
        lines.add("Variables (" + variableDeclarations.length + " at offset " + String.format("%04X", variableStructsTableOffset) + ")");
        if (variableDeclarations.length > 0) {
            List<String> refsStrings = new ArrayList<>();
            for (int i = 0; i < variableDeclarations.length; i++) {
                refsStrings.add("var" + String.format("%02X", i) + ": " + variableDeclarations[i] + " [" + String.format("%016X", variableDeclarations[i].struct) + "h]");
            }
            lines.add(String.join("\n", refsStrings));
        }
        lines.add("Integers (" + refInts.length + " at offset " + String.format("%04X", intTableOffset) + ")");
        if (refInts.length > 0) {
            List<String> intStrings = new ArrayList<>();
            for (int i = 0; i < refInts.length; i++) {
                intStrings.add("refI" + String.format("%02X", i) + ": " + refInts[i] + " [" + String.format("%08X", refInts[i]) + "h]");
            }
            lines.add(String.join(", ", intStrings));
        }
        lines.add("Floats (" + refFloats.length + " at offset " + String.format("%04X", floatTableOffset) + ")");
        if (refFloats.length > 0) {
            List<String> floatStrings = new ArrayList<>();
            for (int i = 0; i < refFloats.length; i++) {
                floatStrings.add("refF" + String.format("%02X", i) + ": " + Float.intBitsToFloat(refFloats[i]) + " [" + String.format("%08X", refFloats[i]) + "h]");
            }
            lines.add(String.join(", ", floatStrings));
        }
        return String.join("\n", lines);
    }
}
