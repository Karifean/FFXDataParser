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

    private static final boolean VERBOSE_HEADER_OUTPUT = false;
    private static final boolean PRINT_REF_INTS_FLOATS = false;
    private static final boolean PRINT_JUMP_TABLE = false;

    protected final int[] bytes;
    protected final int absoluteOffset;
    protected final int[] battleWorkerMappingBytes;

    protected int[] actualScriptCodeBytes;
    protected ScriptWorker[] workers;
    protected int[] refFloats;
    protected int[] refInts;
    protected ScriptVariable[] variableDeclarations;
    protected int floatTableOffset;
    protected int intTableOffset;
    protected int variableStructsTableOffset;
    protected int map_start;
    protected int creatorTagAddress;
    protected int event_name_start;
    protected int jumpsEndAddress;
    protected int amountOfType2or3Scripts;
    protected int amountOfType4Scripts;
    protected int amountOfType5Scripts;
    protected int areaNameBytes;
    protected int areaNameIndexesOffset;
    protected int other_offset;
    protected int mainScriptIndex;
    protected int unknown1A;
    protected int unknown24;
    public int eventDataOffset;
    protected int scriptCodeLength;
    protected int scriptCodeStartAddress;
    protected int scriptCodeEndAddress;
    protected int numberOfScripts;
    protected int numberOfScriptsWithoutSubroutines;
    public List<String> strings;
    public List<Integer> areaNameIndexes;
    Stack<StackObject> stack = new Stack<>();
    Map<Integer, String> currentTempITypes = new HashMap<>();
    Map<Integer, String> varTypes = new HashMap<>();
    Map<Integer, List<StackObject>> varEnums = new HashMap<>();
    Map<Integer, StackObject> constants = new HashMap<>();
    int currentWorkerIndex = 0;
    String currentRAType = "unknown";
    String currentRXType = "unknown";
    String currentRYType = "unknown";
    boolean gatheringInfo = true;
    List<ScriptJump> scriptJumps;
    Map<Integer, List<ScriptJump>> scriptJumpsByDestination;
    List<ScriptJump> currentExecutionLines;

    int lineCount = 0;
    String textScriptLine;
    List<String> warningsOnLine;
    List<String> offsetLines;
    List<String> textScriptLines;
    List<String> hexScriptLines;
    List<String> jumpLines;
    List<String> warnLines;
    List<ScriptInstruction> instructions = new ArrayList<>();

    public ScriptObject(Chunk chunk, int[] battleWorkerMappingBytes) {
        this(chunk.bytes, chunk.offset, battleWorkerMappingBytes);
    }

    public ScriptObject(int[] bytes, int absoluteOffset, int[] battleWorkerMappingBytes) {
        this.bytes = bytes;
        this.absoluteOffset = absoluteOffset;
        this.battleWorkerMappingBytes = battleWorkerMappingBytes;
        mapFields();
        parseWorkers();
    }

    private void mapFields() {
        scriptCodeLength = read4Bytes(0x00);
        map_start = read4Bytes(0x04);
        creatorTagAddress = read4Bytes(0x08);
        event_name_start = read4Bytes(0x0C);
        jumpsEndAddress = read4Bytes(0x10);
        amountOfType2or3Scripts = read2Bytes(0x14);
        amountOfType4Scripts = read2Bytes(0x16);
        mainScriptIndex = read2Bytes(0x18);
        unknown1A = read2Bytes(0x1A);
        amountOfType5Scripts = read2Bytes(0x1C);
        areaNameBytes = read2Bytes(0x1E);
        eventDataOffset = read4Bytes(0x20);
        unknown24 = read4Bytes(0x24);
        areaNameIndexesOffset = read4Bytes(0x28);
        other_offset = read4Bytes(0x2C);
        scriptCodeStartAddress = read4Bytes(0x30);
        numberOfScripts = read2Bytes(0x34);
        numberOfScriptsWithoutSubroutines = read2Bytes(0x36);

        if (areaNameBytes > 0 && areaNameIndexesOffset > 0) {
            areaNameIndexes = new ArrayList<>();
            if ((areaNameBytes & 0x8000) > 0) {
                int count = areaNameBytes & 0x7FFF;
                for (int i = 0; i < count; i++) {
                    areaNameIndexes.add(read2Bytes(areaNameIndexesOffset + 2 * i));
                }
            } else {
                areaNameIndexes.add(areaNameBytes);
            }
        }
    }

    private void parseWorkers() {
        scriptJumps = new ArrayList<>();
        scriptJumpsByDestination = new HashMap<>();
        workers = new ScriptWorker[numberOfScripts];
        for (int i = 0; i < numberOfScripts; i++) {
            int offset = read4Bytes(0x38 + i * 4);
            ScriptWorker scriptWorker = parseScriptWorker(offset, i);
            parseScriptJumps(scriptWorker);
            workers[i] = scriptWorker;
        }
        parseVarIntFloatTables();
        parseBattleWorkerTypes();
    }

    public void parseScript(List<String> strings) {
        this.strings = strings;
        scriptCodeEndAddress = scriptCodeStartAddress + scriptCodeLength;
        actualScriptCodeBytes = Arrays.copyOfRange(bytes, scriptCodeStartAddress, scriptCodeEndAddress);
        syntacticParseScriptCode();

        gatheringInfo = true;
        semanticParseScriptCode();
        inferBooleans();

        gatheringInfo = false;
        semanticParseScriptCode();
    }

    private ScriptWorker parseScriptWorker(int offset, int scriptIndex) {
        return new ScriptWorker(this, scriptIndex, Arrays.copyOfRange(bytes, offset, offset + ScriptWorker.LENGTH));
    }

    public ScriptWorker getWorker(int workerIndex) {
        return workerIndex >= 0 && workerIndex < workers.length ? workers[workerIndex] : null;
    }

    private void parseVarIntFloatTables() {
        variableStructsTableOffset = -1;
        intTableOffset = -1;
        floatTableOffset = -1;
        for (ScriptWorker w : workers) {
            if (variableStructsTableOffset < 0) {
                variableStructsTableOffset = w.variableStructsTableOffset;
                variableDeclarations = new ScriptVariable[w.variablesCount];
                for (int i = 0; i < w.variablesCount; i++) {
                    int lb = read4Bytes(variableStructsTableOffset + i * 8);
                    int hb = read4Bytes(variableStructsTableOffset + i * 8 + 4);
                    ScriptVariable scriptVariable = new ScriptVariable(w, i, lb, hb);
                    if (scriptVariable.location == 0) {
                        ScriptField entry = ScriptConstants.getEnumMap("saveData").get(scriptVariable.offset);
                        if (entry != null) {
                            varTypes.put(i, entry.type);
                        }
                    } else if (scriptVariable.location == 4) {
                        scriptVariable.parseValues();
                    } else if (scriptVariable.location == 6) {
                        scriptVariable.parseValues();
                    }
                    variableDeclarations[i] = scriptVariable;
                }
                w.variableDeclarations = variableDeclarations;
                w.setVariableInitialValues();
            } else if (w.variableStructsTableOffset != variableStructsTableOffset || w.variablesCount != variableDeclarations.length) {
                System.err.println("WARNING, variables table mismatch!");
            } else {
                w.variableDeclarations = variableDeclarations;
                w.setVariableInitialValues();
            }
            if (intTableOffset < 0) {
                intTableOffset = w.intTableOffset;
                refInts = new int[w.refIntCount];
                for (int i = 0; i < w.refIntCount; i++) {
                    refInts[i] = read4Bytes(intTableOffset + i * 4);
                }
                w.refInts = refInts;
            } else if (w.intTableOffset != intTableOffset || w.refIntCount != refInts.length) {
                System.err.println("WARNING, int table mismatch!");
            } else {
                w.refInts = refInts;
            }
            if (floatTableOffset < 0) {
                floatTableOffset = w.floatTableOffset;
                refFloats = new int[w.refFloatCount];
                for (int i = 0; i < w.refFloatCount; i++) {
                    refFloats[i] = read4Bytes(floatTableOffset + i * 4);
                }
                w.refFloats = refFloats;
            } else if (w.floatTableOffset != floatTableOffset || w.refFloatCount != refFloats.length) {
                System.err.println("WARNING, float table mismatch!");
            } else {
                w.refFloats = refFloats;
            }
        }
    }

    private void parseBattleWorkerTypes() {
        if (battleWorkerMappingBytes == null || battleWorkerMappingBytes.length == 0) {
            return;
        }
        int notActuallySectionCount = battleWorkerMappingBytes[0];
        int preSectionLength = battleWorkerMappingBytes[1];
        // map from section index to purpose slot
        HashMap<Integer,Integer> slotMap = new HashMap();
        for (int i = 2; i < preSectionLength+2; i++) {
            if (battleWorkerMappingBytes[i] != 0xFF) {
                slotMap.put(battleWorkerMappingBytes[i], i-2);
            }
        }
        int sectionsLineOffset = preSectionLength + (preSectionLength % 2 == 0 ? 2 : 3);
        Integer firstOffset = null;
        for (int i = 0; i < notActuallySectionCount; i++) {
            int offset = sectionsLineOffset + i * 4;
            int header = battleWorkerMappingBytes[offset];
            int scriptKind = battleWorkerMappingBytes[offset + 1];
            int sectionOffset = battleWorkerMappingBytes[offset + 2] + battleWorkerMappingBytes[offset + 3] * 0x100;
            if (i == 0) {
                firstOffset = sectionOffset;
            } else if (offset >= firstOffset) {
                System.err.println("WARNING - Offset number mismatch at index " + i + " expected " + notActuallySectionCount);
                break;
            }
            int sectionValueCount = battleWorkerMappingBytes[sectionOffset] + battleWorkerMappingBytes[sectionOffset + 1] * 0x100;
            int sectionPayloadOffset = sectionOffset + 2;
            if (slotMap.containsKey(i)) {
                workers[header].setPurposeSlot(slotMap.get(i));
            }
            workers[header].setBattleWorkerTypes(scriptKind, sectionValueCount, Arrays.copyOfRange(battleWorkerMappingBytes, sectionPayloadOffset, sectionPayloadOffset + sectionValueCount * 2));
        }
    }

    private void parseScriptJumps(ScriptWorker worker) {
        int entryPointCount = worker.entryPointCount;
        ScriptJump[] entryPoints = new ScriptJump[entryPointCount];
        for (int i = 0; i < entryPointCount; i++) {
            int addr = read4Bytes(worker.scriptEntryPointsOffset + i * 4);
            ScriptJump entryPoint = new ScriptJump(worker, addr, i, true);
            entryPoints[i] = entryPoint;
            scriptJumps.add(entryPoint);
            if (!scriptJumpsByDestination.containsKey(addr)) {
                scriptJumpsByDestination.put(addr, new ArrayList<>());
            }
            scriptJumpsByDestination.get(addr).add(entryPoint);
        }
        worker.entryPoints = entryPoints;
        int jumpCount = worker.jumpCount;
        ScriptJump[] jumps = new ScriptJump[jumpCount];
        for (int i = 0; i < jumpCount; i++) {
            int addr = read4Bytes(worker.jumpsOffset + i * 4);
            ScriptJump jump = new ScriptJump(worker, addr, i, false);
            jumps[i] = jump;
            scriptJumps.add(jump);
            if (!scriptJumpsByDestination.containsKey(addr)) {
                scriptJumpsByDestination.put(addr, new ArrayList<>());
            }
            scriptJumpsByDestination.get(addr).add(jump);
        }
        worker.jumps = jumps;
    }

    protected void syntacticParseScriptCode() {
        lineCount = 0;
        instructions = new ArrayList<>();
        offsetLines = new ArrayList<>();
        hexScriptLines = new ArrayList<>();
        jumpLines = new ArrayList<>();
        List<ScriptInstruction> lineInstructions = new ArrayList<>();
        List<ScriptJump> jumpsOnLine = new ArrayList<>();
        int nextLineOffset = 0;
        int cursor = 0;
        while (cursor < scriptCodeLength) {
            List<ScriptJump> jumpsOnInstruction = new ArrayList<>();
            int offset = cursor;
            int opcode = nextAiByte(cursor, jumpsOnInstruction, false);
            cursor++;
            ScriptInstruction instruction;
            if (hasArgs(opcode)) {
                final int arg1 = nextAiByte(cursor, jumpsOnInstruction, true);
                cursor++;
                final int arg2 = nextAiByte(cursor, jumpsOnInstruction, true);
                cursor++;
                instruction = new ScriptInstruction(offset, opcode, arg1, arg2);
            } else {
                instruction = new ScriptInstruction(offset, opcode);
            }
            instruction.jumps = jumpsOnInstruction;
            jumpsOnLine.addAll(jumpsOnInstruction);
            lineInstructions.add(instruction);
            instructions.add(instruction);
            if (getLineEnd(opcode)) {
                lineCount++;
                offsetLines.add(String.format("%04X", nextLineOffset));
                nextLineOffset = cursor;
                hexScriptLines.add(getHexLine(lineInstructions));
                jumpLines.add(getJumpLine(jumpsOnLine));
                textScriptLine = "";
                jumpsOnLine.clear();
                lineInstructions.clear();
            }
        }
    }

    private void semanticParseScriptCode() {
        currentExecutionLines = new ArrayList<>();
        textScriptLines = new ArrayList<>();
        warnLines = new ArrayList<>();
        textScriptLine = "";
        warningsOnLine = new ArrayList<>();
        List<ScriptInstruction> nonNullInstructionsOnLine = new ArrayList<>();
        List<ScriptJump> softMisalignedOnLine = new ArrayList<>();
        List<ScriptJump> hardMisalignedOnLine = new ArrayList<>();
        for (ScriptInstruction instruction : instructions) {
            if (!instruction.jumps.isEmpty()) {
                restoreTypingsFromJumps(instruction.jumps);
                instruction.jumps.forEach(j -> j.reachableFrom = currentExecutionLines);
                currentExecutionLines = new ArrayList<>(currentExecutionLines);
                currentExecutionLines.addAll(instruction.jumps);
                List<ScriptJump> hardMisaligned = instruction.jumps.stream().filter(j -> j.hardMisaligned).collect(Collectors.toList());
                if (!hardMisaligned.isEmpty()) {
                    hardMisalignedOnLine.addAll(hardMisaligned);
                }
                if (!nonNullInstructionsOnLine.isEmpty()) {
                    softMisalignedOnLine.addAll(instruction.jumps);
                }
            }
            if (instruction.opcode != 0x00) {
                nonNullInstructionsOnLine.add(instruction);
            }
            instruction.reachableFrom = currentExecutionLines;
            processInstruction(instruction);
            if (getLineEnd(instruction.opcode)) {
                if (!stack.empty()) {
                    warningsOnLine.add("Stack not empty (" + stack.size() + "): " + stack);
                    stack.clear();
                }
                if (!hardMisalignedOnLine.isEmpty()) {
                    softMisalignedOnLine.removeAll(hardMisalignedOnLine);
                    warningsOnLine.add("Broken jumps: " + hardMisalignedOnLine.stream().map(j -> j.getLabelWithAddr()).collect(Collectors.joining(",")));
                    hardMisalignedOnLine.clear();
                }
                if (!softMisalignedOnLine.isEmpty()) {
                    warningsOnLine.add("Soft-broken jumps: " + softMisalignedOnLine.stream().map(j -> j.getLabelWithAddr()).collect(Collectors.joining(",")));
                    softMisalignedOnLine.clear();
                }
                textScriptLines.add(textScriptLine);
                warnLines.add(warningsOnLine.isEmpty() ? null : (" " + String.join("; ", warningsOnLine)));
                textScriptLine = "";
                warningsOnLine = new ArrayList<>();
                nonNullInstructionsOnLine.clear();
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

    protected int nextAiByte(int cursor, List<ScriptJump> jumpsOnLine, boolean isArgByte) {
        if (scriptJumpsByDestination.containsKey(cursor)) {
            List<ScriptJump> jumps = scriptJumpsByDestination.get(cursor);
            restoreTypingsFromJumps(jumps);
            jumpsOnLine.addAll(jumps);
            if (isArgByte) {
                jumps.forEach(ScriptJump::markAsHardMisaligned);
            }
        }
        return actualScriptCodeBytes[cursor];
    }

    protected void restoreTypingsFromJumps(List<ScriptJump> jumps) {
        if (jumps == null || jumps.isEmpty()) {
            return;
        }
        jumps.stream().filter(j -> j.isEntryPoint).findFirst().ifPresent(j -> currentWorkerIndex = j.workerIndex);
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
            warningsOnLine.add("Empty stack for opcode " + String.format("%02X", opcode));
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
            StackObject stackObject = new StackObject(workers[currentWorkerIndex], ins, resultType, true, content, opcode);
            stackObject.maybeBracketize = true;
            stack.push(stackObject);
        } else if (opcode == 0x19) { // OPNOT / NOT_LOGIC
            String p1s = p1.toString();
            if (p1.maybeBracketize) {
                p1s = '(' + p1s + ')';
            }
            stack.push(new StackObject(workers[currentWorkerIndex], ins, "bool", true, "!" + p1s, 0x19));
        } else if (opcode == 0x1A) { // OPUMINUS / NEG
            String p1s = p1.toString();
            if (p1.maybeBracketize) {
                p1s = '(' + p1s + ')';
            }
            stack.push(new StackObject(workers[currentWorkerIndex], ins, p1.type, true, "-" + p1s, 0x1A));
        } else if (opcode == 0x1C) { // OPBNOT / NOT
            String p1s = p1.toString();
            if (p1.maybeBracketize) {
                p1s = '(' + p1s + ')';
            }
            stack.push(new StackObject(workers[currentWorkerIndex], ins, p1.type, true, "~" + p1s, 0x1C));
        } else if (opcode == 0x25) { // POPA / SET_RETURN_VALUE
            textScriptLine += p1 + ";";
            currentRAType = resolveType(p1);
        } else if (opcode == 0x26) { // PUSHA / GET_RETURN_VALUE
            stack.push(new StackObject(workers[currentWorkerIndex], ins, currentRAType, true, "LastCallResult", 0x26));
        } else if (opcode == 0x28) { // PUSHX / GET_TEST
            stack.push(new StackObject(workers[currentWorkerIndex], ins, currentRXType, true, "rX", 0x28));
        } else if (opcode == 0x29) { // PUSHY / GET_CASE
            stack.push(new StackObject(workers[currentWorkerIndex], ins, currentRYType, true, "case", 0x29));
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
                cmd += "AndAwaitStart";
            } else if (opcode == 0x38) { // REQEW / SIG_ONEND
                cmd += "AndAwaitEnd";
            }
            String level = p1.expression ? ""+p1 : ""+p1.value;
            boolean direct = !p2.expression && !p3.expression && isWeakType(p2.type) && isWeakType(p3.type) && p2.value < workers.length && p3.value < workers[p2.value].entryPoints.length;
            String s = p2.expression ? "(" + p2 + ")" : format2Or4Byte(p2.value);
            String e = p3.expression ? "(" + p3 + ")" : format2Or4Byte(p3.value);
            String scriptLabel = direct ? workers[p2.value].entryPoints[p3.value].getLabel() : ("w" + s + "e" + e);
            String content = cmd + " " + scriptLabel + " (Level " + level + ")";
            stack.push(new StackObject(workers[currentWorkerIndex], ins, "worker", true, content, opcode));
        } else if (opcode == 0x39) { // PREQ
            String content = "PREQ(" + p1 + ", " + p2 + ", " + p3 + ")";
            stack.push(new StackObject(workers[currentWorkerIndex], ins, "unknown", true, content, 0x39));
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
            resetRegisterTypes();
        } else if (opcode == 0x46) { // TREQ
            String content = "TREQ(" + p1 + ", " + p2 + ", " + p3 + ")";
            stack.push(new StackObject(workers[currentWorkerIndex], ins, "unknown", true, content, 0x46));
        } else if (opcode == 0x54) { // DRET / CLEANUP_ALL_END
            textScriptLine += "direct return;";
            resetRegisterTypes();
        } else if (opcode >= 0x59 && opcode <= 0x5C) { // POPI0..3 / SET_INT
            String p1t = resolveType(p1);
            int tempIndex = opcode - 0x59;
            if (p1t != null && !"unknown".equals(p1t)) {
                currentTempITypes.put(tempIndex, p1t);
            } else {
                currentTempITypes.remove(tempIndex);
            }
            String val = typed(p1, currentTempITypes.get(tempIndex));
            textScriptLine += "Set tmpI" + tempIndex + " = " + val + ";";
        } else if (opcode >= 0x5D && opcode <= 0x66) { // POPF0..9 / SET_FLOAT
            int tempIndex = opcode - 0x5D;
            textScriptLine += "Set tmpF" + tempIndex + " = " + p1 + ";";
        } else if (opcode >= 0x67 && opcode <= 0x6A) { // PUSHI0..3 / GET_INT
            int tempIndex = opcode - 0x67;
            StackObject stackObject = new StackObject(workers[currentWorkerIndex], ins, "tmpI", true, "tmpI" + tempIndex, tempIndex);
            stackObject.referenceIndex = tempIndex;
            stack.push(stackObject);
        } else if (opcode >= 0x6B && opcode <= 0x74) { // PUSHF0..9 / GET_FLOAT
            int tempIndex = opcode - 0x6B;
            StackObject stackObject = new StackObject(workers[currentWorkerIndex], ins, "float", true, "tmpF" + tempIndex, opcode);
            stackObject.referenceIndex = tempIndex;
            stack.push(stackObject);
        } else if (opcode == 0x77) { // REQWAIT / WAIT_DELETE
            boolean direct = !p1.expression && !p2.expression && isWeakType(p1.type) && isWeakType(p2.type) && p1.value < workers.length && p2.value < workers[p1.value].entryPoints.length;
            String w = p1.expression ? "(" + p1 + ")" : format2Or4Byte(p1.value);
            String e = p2.expression ? "(" + p2 + ")" : format2Or4Byte(p2.value);
            String scriptLabel = direct ? workers[p1.value].entryPoints[p2.value].getLabel() : ("w" + w + "e" + e);
            textScriptLine += "await " + scriptLabel + ";";
        } else if (opcode == 0x78) { // Never used: PREQWAIT / WAIT_SPEC_DELETE
        } else if (opcode == 0x79) { // REQCHG / EDIT_ENTRY_TABLE
            ScriptJump[] entryPoints = workers[currentWorkerIndex].entryPoints;
            int oldIdx = p2.value + 2;
            int newIdx = p3.value;
            boolean direct = !p2.expression && !p3.expression && isWeakType(p2.type) && isWeakType(p3.type) && oldIdx < entryPoints.length && newIdx < entryPoints.length;
            String oldScriptLabel = direct ? entryPoints[oldIdx].getLabel() : ("e" + (p2.expression ? "(" + p2 + ")" : format2Or4Byte(oldIdx)));
            String newScriptLabel = direct ? entryPoints[newIdx].getLabel() : ("e" + (p3.expression ? "(" + p3 + ")" : format2Or4Byte(newIdx)));
            String i = p1.expression ? ""+p1 : ""+p1.value;
            textScriptLine += "Replace script " + oldScriptLabel + " with " + newScriptLabel + " (" + i + ")";;
            // textScriptLine += "REQCHG(" + p1 + ", " + p2 + ", " + p3 + ");";
        } else if (opcode == 0x7A) { // Never used: ACTREQ / SET_EDGE_TRIGGER
        } else if (opcode == 0x9F) { // PUSHV / GET_DATUM
            StackObject stackObject = new StackObject(workers[currentWorkerIndex], ins, "var", true, getVariableLabel(argv), argv);
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
            textScriptLine += getVariableLabel(argv) + " = " + val + ";";
        } else if (opcode == 0xA2) { // PUSHAR / GET_DATUM_INDEX
            StackObject stackObject = new StackObject(workers[currentWorkerIndex], ins, "var", true, ensureVariableValidWithArray(argv, p1), argv);
            stackObject.referenceIndex = argv;
            stack.push(stackObject);
        } else if (opcode == 0xA3 || opcode == 0xA4) { // POPAR(L) / SET_DATUM_INDEX_(W/T)
            addVarType(argv, resolveType(p2));
            textScriptLine += "Set ";
            if (opcode == 0xA4) {
                textScriptLine += "(limit) ";
            }
            String val = typed(p2, varTypes.get(argv));
            textScriptLine += ensureVariableValidWithArray(argv, p1) + " = " + val + ";";
        } else if (opcode == 0xA7) { // PUSHARP / GET_DATUM_DESC
            String arrayIndex = '[' + String.format("%04X", p1.value) + ']';
            StackObject stackObject = new StackObject(workers[currentWorkerIndex], ins, "int16", true, "ArrayPointer:var" + argvsh + arrayIndex, argv);
            stackObject.referenceIndex = argv;
            stack.push(stackObject);
        } else if (opcode == 0xAD) { // PUSHI / CONST_INT
            int refInt = refInts[argv];
            String content = "rI[" + argvsh + "]:" + refInt + " [" + String.format("%08X", refInt) + "h]";
            StackObject stackObject = new StackObject(workers[currentWorkerIndex], ins, "uint32", false, content, refInt);
            stackObject.referenceIndex = argv;
            stack.push(stackObject);
        } else if (opcode == 0xAE) { // PUSHII / IMM
            stack.push(new StackObject(workers[currentWorkerIndex], ins, "int16", false, ins.argvSigned + " [" + argvsh + "h]", ins.argvSigned));
        } else if (opcode == 0xAF) { // PUSHF / CONST_FLOAT
            int refFloat = refFloats[argv];
            String content = "rF[" + argvsh + "]:" + Float.intBitsToFloat(refFloat) + " [" + String.format("%08X", refFloat) + "h]";
            StackObject stackObject = new StackObject(workers[currentWorkerIndex], ins, "float", false, content, refFloat);
            stackObject.referenceIndex = argv;
            stack.push(stackObject);
        } else if (opcode == 0xB0) { // JMP / JUMP
            ScriptJump jump = referenceJump(argv);
            textScriptLine += "Jump to " + (jump != null ? jump.getLabelWithAddr() : ("j" + String.format("%02X", argv)));
            resetRegisterTypes();
        } else if (opcode == 0xB1) { // Never used: CJMP / BNEZ
        } else if (opcode == 0xB2) { // Never used: NCJMP / BEZ
        } else if (opcode == 0xB3) { // JSR
            ScriptWorker worker = workers == null || workers.length <= argv ? null : workers[argv];
            textScriptLine += "Jump to subroutine " + (worker != null ? worker.getIndexLabel() : ("w" + String.format("%02X", argv)));
        } else if (opcode == 0xB5) { // CALL / FUNC_RET
            List<StackObject> params = popParamsForFunc(argv);
            ScriptFunc func = getAndTypeFuncCall(argv, params);
            StackObject stackObject = new StackObject(workers[currentWorkerIndex], ins, func.getType(params), true, func.callB5(params), argv);
            stackObject.referenceIndex = argv;
            stack.push(stackObject);
        } else if (opcode == 0xD6) { // POPXCJMP / SET_BNEZ
            ScriptJump jump = referenceJump(argv);
            textScriptLine += "(" + p1 + ") -> " + (jump != null ? jump.getLabelWithAddr() : ("j" + String.format("%02X", argv)));
        } else if (opcode == 0xD7) { // POPXNCJMP / SET_BEZ
            ScriptJump jump = referenceJump(argv);
            textScriptLine += "Check (" + p1 + ") else jump to " + (jump != null ? jump.getLabelWithAddr() : ("j" + String.format("%02X", argv)));
        } else if (opcode == 0xD8) { // CALLPOPA / FUNC
            List<StackObject> params = popParamsForFunc(argv);
            ScriptFunc func = getAndTypeFuncCall(argv, params);
            currentRAType = func.getType(params);
            String call = func.callD8(params);
            textScriptLine += call + ';';
        }
    }

    private ScriptJump referenceJump(int argv) {
        if (workers == null || workers.length <= currentWorkerIndex) {
            return null;
        }
        ScriptWorker worker = workers[currentWorkerIndex];
        if (worker == null || worker.jumps == null || worker.jumps.length <= argv) {
            return null;
        }
        return referenceJump(worker.jumps[argv]);
    }

    private ScriptJump referenceJump(ScriptJump jump) {
        if (jump == null) {
            return null;
        }
        if (jump.hardMisaligned) {
            warningsOnLine.add("Referencing broken jump: " + jump.getLabelWithAddr());
        }
        setJumpTypes(jump);
        return jump;
    }

    private void setJumpTypes(ScriptJump jump) {
        if (jump == null) {
            return;
        }
        jump.setTypes(currentRAType, currentRXType, currentRYType, currentTempITypes);
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
            warningsOnLine.add("Empty stack for func " + String.format("%04X", idx));
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

    protected void addVarType(int var, String type) {
        if (!gatheringInfo) {
            return;
        }
        if (!varTypes.containsKey(var) && variableDeclarations != null && var < variableDeclarations.length) {
            varTypes.put(var, variableDeclarations[var].getType());
        }
        String prevType = varTypes.get(var);
        if (isWeakType(prevType)) {
            varTypes.put(var, type);
        }
    }

    protected String resolveType(StackObject obj) {
        if (obj == null) {
            return "unknown";
        }
        if ("var".equals(obj.type)) {
            return varTypes.get(obj.value);
        }
        if ("tmpI".equals(obj.type)) {
            return currentTempITypes.getOrDefault(obj.value, "unknown");
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
            if ("tmpI".equals(obj.type) && type != null && !"unknown".equals(type)) {
                currentTempITypes.put(obj.value, type);
            }
            if (obj.expression || type == null || "unknown".equals(type)) {
                return obj.toString();
            } else {
                return new StackObject(type, obj).toString();
            }
        }
    }

    public String getVariableLabel(int index) {
        if (variableDeclarations != null && index >= 0 && index < variableDeclarations.length) {
            return variableDeclarations[index].getLabel();
        }
        String hexIdx = String.format("%02X", index);
        warningsOnLine.add("Variable index " + hexIdx + " out of bounds!");
        return "var" + hexIdx;
    }

    private String ensureVariableValidWithArray(int index, StackObject p1) {
        String varLabel = getVariableLabel(index);
        String indexType = resolveType(p1);
        if (isWeakType(indexType) && (variableDeclarations != null && index >= 0 && index < variableDeclarations.length)) {
            indexType = variableDeclarations[index].getArrayIndexType();
        }
        String arrayIndex = !p1.expression && isWeakType(indexType) ? String.format("%04X", p1.value) : typed(p1, indexType);
        return varLabel + '[' + arrayIndex + ']';
    }

    protected static boolean hasArgs(int opcode) {
        if (opcode == 0xFF) {
            return false;
        } else if (opcode >= 0x80) {
            return true;
        } else {
            return false;
        }
    }

    protected int getStackPops(int opcode) {
        int stackpops = ScriptConstants.OPCODE_STACKPOPS[opcode];
        if (stackpops < 0) {
            warningsOnLine.add("Undefined stackpops for opcode " + String.format("%02X", opcode));
            return 0;
        }
        return stackpops;
    }

    protected int getFunctionParamCount(int idx) {
        ScriptFunc func = ScriptFuncLib.get(idx, null);
        if (func == null) {
            warningsOnLine.add("Undefined stackpops for func " + String.format("%04X", idx));
            return 0;
        }
        return func.inputs != null ? func.inputs.size() : 0;
    }

    protected static boolean getLineEnd(int opcode) {
        return ScriptConstants.OPCODE_ENDLINE.contains(opcode);
    }

    protected void inferBooleans() {
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
        if (!currentExecutionLines.isEmpty()) {
            // currentExecutionLines.forEach(this::setJumpTypes);
            currentExecutionLines = new ArrayList<>();
        }
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
        String warnLine = warnLines.get(line);
        String wl = warnLine == null || warnLine.isEmpty() ? "" : (consoleColorIfEnabled(ANSI_RED) + warnLine);
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
        if (workers == null || workers.length == 0) {
            return "No Workers";
        }
        List<String> lines = new ArrayList<>();
        if (mainScriptIndex != 0xFFFF) {
            lines.add("Main Worker: " + mainScriptIndex + " [" + String.format("%02X", mainScriptIndex) + "h]");
        }
        if (VERBOSE_HEADER_OUTPUT) {
            lines.add("map_start = " + String.format("%04X", map_start));
            lines.add("unk1 = " + String.format("%04X", unknown1A));
            lines.add("unk2 = " + String.format("%04X", unknown24));
            lines.add("other_offset = " + String.format("%06X", other_offset));
        }
        if (areaNameIndexes != null) {
            lines.add("Area Names");
            areaNameIndexes.forEach(i -> lines.add(MACRO_LOOKUP.get(0xB00 + i)));
        }
        lines.add(numberOfScripts + " Workers Total");
        for (int i = 0; i < numberOfScripts; i++) {
            lines.add("w" + String.format("%02X", i) + ": " + workers[i].getNonCommonString());
        }
        lines.add("Variables (" + variableDeclarations.length + " at offset " + String.format("%04X", variableStructsTableOffset) + ")");
        if (variableDeclarations.length > 0) {
            List<String> refsStrings = new ArrayList<>();
            for (int i = 0; i < variableDeclarations.length; i++) {
                refsStrings.add("var" + String.format("%02X", i) + ": " + variableDeclarations[i] + " [" + String.format("%016X", variableDeclarations[i].fullBytes) + "h] - Inferred " + varTypes.get(i));
            }
            lines.add(String.join("\n", refsStrings));
        }
        if (PRINT_REF_INTS_FLOATS) {
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
        }
        if (PRINT_JUMP_TABLE) {
            lines.add("- Jump Table -");
            for (int i = 0; i < numberOfScripts; i++) {
                lines.add("w" + String.format("%02X", i));
                ScriptWorker h = workers[i];
                lines.add(h.getEntryPointsLine());
                lines.add(h.getJumpsLine());
            }
        }
        return lines.stream().filter(s -> s != null && !s.isEmpty()).collect(Collectors.joining("\n"));
    }

    public int[] getBytes() {
        return bytes;
    }
}
