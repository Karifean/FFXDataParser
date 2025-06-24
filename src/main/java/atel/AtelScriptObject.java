package atel;

import atel.model.*;
import main.StringHelper;
import model.strings.LocalizedFieldStringObject;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static main.StringHelper.*;

public class AtelScriptObject {
    private static final int JUMP_LINE_MINLENGTH = 16;
    private static final int HEX_LINE_MINLENGTH = COLORS_USE_CONSOLE_CODES ? 58 : 48;
    private static final int JUMP_PLUS_HEX_LINE_MINLENGTH = JUMP_LINE_MINLENGTH + HEX_LINE_MINLENGTH + 1;

    private static final boolean PRINT_UNKNOWN_HEADER_VALS = false;
    private static final boolean PRINT_REF_INTS_FLOATS = false;
    private static final boolean PRINT_JUMP_TABLE = false;
    private static final boolean INFER_BITWISE_OPS_AS_BITFIELDS = false;

    protected final int[] bytes;
    protected final int[] battleWorkerMappingBytes;

    protected int[] actualScriptCodeBytes;
    protected ScriptWorker[] workers;
    public int[] refFloats;
    public int[] refInts;
    public ScriptVariable[] variableDeclarations;
    protected int variableStructsTableOffset;
    protected int intTableOffset;
    protected int floatTableOffset;
    protected int map_start;
    protected int creatorTagAddress;
    protected int event_name_start;
    protected int jumpsEndAddress;
    protected int amountOfType2or3Scripts;
    protected int amountOfType4Scripts;
    protected int amountOfType5Scripts;
    protected int areaNameBytes;
    protected int areaNameIndexesOffset;
    protected int unknownSize40StructOffset;
    protected int mainScriptIndex;
    protected int unknown1A;
    protected int unknownOffset24;
    public int eventDataOffset;
    protected int scriptCodeLength;
    protected int scriptCodeStartAddress;
    protected int scriptCodeEndAddress;
    protected int namespaceCount;  // Total number of workers
    protected int actorCount; // Total number of workers except subroutines
    public List<LocalizedFieldStringObject> strings;
    public List<Integer> areaNameIndexes;
    public MapEntranceObject[] mapEntrances;
    public String creatorTag;
    Stack<StackObject> stack = new Stack<>();
    Map<Integer, String> currentTempITypes = new HashMap<>();
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
    List<ScriptLine> scriptLines = new ArrayList<>();

    public AtelScriptObject(int[] bytes, int[] battleWorkerMappingBytes) {
        this.bytes = bytes;
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
        unknownOffset24 = read4Bytes(0x24);
        areaNameIndexesOffset = read4Bytes(0x28);
        unknownSize40StructOffset = read4Bytes(0x2C);
        scriptCodeStartAddress = read4Bytes(0x30);
        namespaceCount = read2Bytes(0x34);
        actorCount = read2Bytes(0x36);

        int[] creatorTagSubBytes;
        if (creatorTagAddress > 0 && (creatorTagSubBytes = getStringBytesAtLookupOffset(bytes, creatorTagAddress)) != null) {
            byte[] creatorStringBytes = new byte[creatorTagSubBytes.length];
            for (int i = 0; i < creatorTagSubBytes.length; i++) {
                creatorStringBytes[i] = (byte) creatorTagSubBytes[i];
            }
            creatorTag = new String(creatorStringBytes, StandardCharsets.UTF_8);
        }

        if (map_start > 0 && map_start < creatorTagAddress) {
            int mapStartLength = (creatorTagAddress - map_start);
            int mapStartCount = mapStartLength / 0x20;
            mapEntrances = new MapEntranceObject[mapStartCount];
            for (int i = 0; i < mapStartCount; i++) {
                mapEntrances[i] = new MapEntranceObject(Arrays.copyOfRange(bytes, map_start + i * MapEntranceObject.LENGTH, map_start + (i + 1) * MapEntranceObject.LENGTH));
            }
        }

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
        workers = new ScriptWorker[namespaceCount];
        for (int i = 0; i < namespaceCount; i++) {
            int offset = read4Bytes(0x38 + i * 4);
            ScriptWorker scriptWorker = parseScriptWorker(offset, i);
            parseScriptJumps(scriptWorker);
            workers[i] = scriptWorker;
        }
        parseVarIntFloatTables();
        parseBattleWorkerTypes();
    }

    public void addLocalizations(List<LocalizedFieldStringObject> strings) {
        if (strings == null) {
            return;
        }
        if (this.strings == null) {
            this.strings = strings;
            return;
        }
        for (int i = 0; i < strings.size(); i++) {
            LocalizedFieldStringObject localizationStringObject = strings.get(i);
            if (i < this.strings.size()) {
                LocalizedFieldStringObject stringObject = this.strings.get(i);
                if (stringObject != null && localizationStringObject != null) {
                    localizationStringObject.copyInto(stringObject);
                }
            } else {
                this.strings.add(localizationStringObject);
            }
        }
    }

    public void setStrings(List<LocalizedFieldStringObject> strings) {
        this.strings = strings;
    }

    public void parseScript() {
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
        for (ScriptWorker worker : workers) {
            if (variableStructsTableOffset < 0) {
                variableStructsTableOffset = worker.variableStructsTableOffset;
                variableDeclarations = new ScriptVariable[worker.variablesCount];
                for (int varIdx = 0; varIdx < worker.variablesCount; varIdx++) {
                    int lb = read4Bytes(variableStructsTableOffset + varIdx * 8);
                    int hb = read4Bytes(variableStructsTableOffset + varIdx * 8 + 4);
                    ScriptVariable scriptVariable = new ScriptVariable(worker, varIdx, lb, hb);
                    variableDeclarations[varIdx] = scriptVariable;
                    scriptVariable.inferredType = "float".equals(scriptVariable.getType()) ? "float" : "unknown";
                    if (scriptVariable.location == 0) {
                        ScriptField entry = ScriptConstants.getEnumMap("saveData").get(scriptVariable.offset);
                        if (entry != null) {
                            scriptVariable.inferredType = entry.type;
                        }
                    } else if (scriptVariable.location == 4) {
                        scriptVariable.parseValues();
                    } else if (scriptVariable.location == 6) {
                        scriptVariable.parseValues();
                    }
                }
                worker.variableDeclarations = variableDeclarations;
                worker.setVariableInitialValues();
            } else if (worker.variableStructsTableOffset != variableStructsTableOffset || worker.variablesCount != variableDeclarations.length) {
                System.err.println("WARNING, variables table mismatch!");
            } else {
                worker.variableDeclarations = variableDeclarations;
                worker.setVariableInitialValues();
            }
            if (intTableOffset < 0) {
                intTableOffset = worker.intTableOffset;
                refInts = new int[worker.refIntCount];
                for (int i = 0; i < worker.refIntCount; i++) {
                    refInts[i] = read4Bytes(intTableOffset + i * 4);
                }
                worker.refInts = refInts;
            } else if (worker.intTableOffset != intTableOffset || worker.refIntCount != refInts.length) {
                System.err.println("WARNING, int table mismatch!");
            } else {
                worker.refInts = refInts;
            }
            if (floatTableOffset < 0) {
                floatTableOffset = worker.floatTableOffset;
                refFloats = new int[worker.refFloatCount];
                for (int i = 0; i < worker.refFloatCount; i++) {
                    refFloats[i] = read4Bytes(floatTableOffset + i * 4);
                }
                worker.refFloats = refFloats;
            } else if (worker.floatTableOffset != floatTableOffset || worker.refFloatCount != refFloats.length) {
                System.err.println("WARNING, float table mismatch!");
            } else {
                worker.refFloats = refFloats;
            }
        }
    }

    private void parseBattleWorkerTypes() {
        if (battleWorkerMappingBytes == null || battleWorkerMappingBytes.length == 0) {
            return;
        }
        int workersToMapSupposedly = battleWorkerMappingBytes[0];
        int workerSlotCount = battleWorkerMappingBytes[1];
        // map from section index to purpose slot
        Map<Integer, Integer> slotMap = new HashMap<>();
        for (int i = 0; i < workerSlotCount; i++) {
            if (battleWorkerMappingBytes[i + 2] != 0xFF) {
                slotMap.put(battleWorkerMappingBytes[i + 2], i);
            }
        }
        int sectionsLineOffset = workerSlotCount + 2 + (workerSlotCount % 2);
        Integer firstOffset = null;
        for (int i = 0; i < workersToMapSupposedly; i++) {
            int offset = sectionsLineOffset + i * 4;
            int workerIndex = battleWorkerMappingBytes[offset];
            int battleWorkerType = battleWorkerMappingBytes[offset + 1];
            int sectionOffset = battleWorkerMappingBytes[offset + 2] + battleWorkerMappingBytes[offset + 3] * 0x100;
            if (i == 0) {
                firstOffset = sectionOffset;
            } else if (offset >= firstOffset) {
                // System.err.println("WARNING - Offset number mismatch at index " + i + " expected " + workersToMapSupposedly);
                break;
            }
            int entryPointSlotCount = battleWorkerMappingBytes[sectionOffset] + battleWorkerMappingBytes[sectionOffset + 1] * 0x100;
            int sectionPayloadOffset = sectionOffset + 2;
            ScriptWorker worker = getWorker(workerIndex);
            if (worker != null) {
                if (slotMap.containsKey(i)) {
                    worker.setPurposeSlot(slotMap.get(i));
                }
                worker.setBattleWorkerTypes(battleWorkerType, entryPointSlotCount, Arrays.copyOfRange(battleWorkerMappingBytes, sectionPayloadOffset, sectionPayloadOffset + entryPointSlotCount * 2));
            } else {
                System.err.println("WARNING - no worker with index " + workerIndex + " at section " + i + "!");
            }
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
        scriptLines = new ArrayList<>();
        instructions = new ArrayList<>();
        offsetLines = new ArrayList<>();
        hexScriptLines = new ArrayList<>();
        jumpLines = new ArrayList<>();
        List<ScriptInstruction> lineInstructions = new ArrayList<>();
        List<ScriptJump> jumpsOnLine = new ArrayList<>();
        int currentScriptLineOffset = 0;
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
                ScriptLine scriptLine = new ScriptLine(null, currentScriptLineOffset, lineInstructions, instruction);
                scriptLine.incomingJumps = jumpsOnLine;
                jumpsOnLine.forEach(j -> j.targetLine = scriptLine);
                scriptLines.add(scriptLine);
                lineCount++;
                offsetLines.add(StringHelper.formatHex4(currentScriptLineOffset));
                currentScriptLineOffset = cursor;
                hexScriptLines.add(getHexLine(lineInstructions));
                jumpLines.add(getJumpLine(jumpsOnLine));
                textScriptLine = "";
                jumpsOnLine.clear();
                lineInstructions = new ArrayList<>();
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
        jumps.stream().filter(j -> j.isEntryPoint).findFirst().ifPresent(j -> {
            currentWorkerIndex = j.workerIndex;
            currentTempITypes = j.tempITypes;
        });
        jumps.stream().filter(j -> j.rAType != null && !"unknown".equals(j.rAType)).findFirst().ifPresent(j -> currentRAType = j.rAType);
        jumps.stream().filter(j -> j.rXType != null && !"unknown".equals(j.rXType)).findFirst().ifPresent(j -> currentRXType = j.rXType);
        jumps.stream().filter(j -> j.rYType != null && !"unknown".equals(j.rYType)).findFirst().ifPresent(j -> currentRYType = j.rYType);
    }

    protected void processInstruction(ScriptInstruction ins) {
        final int opcode = ins.opcode;
        final int argv = ins.argv;
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
            warningsOnLine.add("Empty stack for opcode " + StringHelper.formatHex2(opcode));
            return;
        }
        if (opcode == 0x00 || opcode == 0x1D || opcode == 0x1E) { // NOP, LABEL, TAG
            // No handling yet, they should probably be written a certain way parsed out but are never actually used
        } else {
            ScriptWorker currentWorker = workers[currentWorkerIndex];
            if (opcode >= 0x01 && opcode <= 0x18) {
                ScriptField op = ScriptConstants.COMP_OPERATORS.get(opcode);
                String resultType = op.type;
                String p1s = p1.toString();
                String p2s = p2.toString();

                if (opcode == 0x03 || opcode == 0x05 || opcode == 0x06 || opcode == 0x07) {
                    String p1t = resolveType(p1);
                    String p2t = resolveType(p2);
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
                String content = p1s + ' ' + op.name + ' ' + p2s;
                StackObject stackObject = new StackObject(currentWorker, ins, resultType, true, content);
                stackObject.maybeBracketize = true;
                stack.push(stackObject);
            } else if (opcode == 0x19) { // OPNOT / NOT_LOGIC
                String p1s = p1.toString();
                if (p1.maybeBracketize) {
                    p1s = '(' + p1s + ')';
                }
                stack.push(new StackObject(currentWorker, ins, "bool", true, "!" + p1s));
            } else if (opcode == 0x1A) { // OPUMINUS / NEG
                String p1s = p1.toString();
                if (p1.maybeBracketize) {
                    p1s = '(' + p1s + ')';
                }
                stack.push(new StackObject(currentWorker, ins, p1.type, true, "-" + p1s));
            } else if (opcode == 0x1C) { // OPBNOT / NOT
                String p1s = p1.toString();
                if (p1.maybeBracketize) {
                    p1s = '(' + p1s + ')';
                }
                stack.push(new StackObject(currentWorker, ins, p1.type, true, "~" + p1s));
            } else if (opcode == 0x25) { // POPA / SET_RETURN_VALUE
                textScriptLine += p1 + ";";
                currentRAType = resolveType(p1);
            } else if (opcode == 0x26) { // PUSHA / GET_RETURN_VALUE
                stack.push(new StackObject(currentWorker, ins, currentRAType, true, "LastCallResult"));
            } else if (opcode == 0x28) { // PUSHX / GET_TEST
                stack.push(new StackObject(currentWorker, ins, currentRXType, true, "test"));
            } else if (opcode == 0x29) { // PUSHY / GET_CASE
                stack.push(new StackObject(currentWorker, ins, currentRYType, true, "case"));
            } else if (opcode == 0x2A) { // POPX / SET_TEST
                textScriptLine += "Set test = " + p1;
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
                String level = p1.expression ? ""+p1 : ""+p1.valueSigned;
                boolean direct = !p2.expression && !p3.expression && isWeakType(p2.type) && isWeakType(p3.type) && p2.valueSigned < workers.length && p3.valueSigned < workers[p2.valueSigned].entryPoints.length;
                String s = p2.expression ? "(" + p2 + ")" : format2Or4Byte(p2.valueSigned);
                String e = p3.expression ? "(" + p3 + ")" : format2Or4Byte(p3.valueSigned);
                String scriptLabel = direct ? workers[p2.valueSigned].entryPoints[p3.valueSigned].getLabel() : ("w" + s + "e" + e);
                String content = cmd + " " + scriptLabel + " (Level " + level + ")";
                stack.push(new StackObject(currentWorker, ins, "worker", true, content));
            } else if (opcode == 0x39) { // PREQ
                String content = "PREQ(" + p1 + ", " + p2 + ", " + p3 + ")";
                stack.push(new StackObject(currentWorker, ins, "unknown", true, content));
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
                stack.push(new StackObject(currentWorker, ins, "unknown", true, content));
            } else if (opcode == 0x54) { // DRET / CLEANUP_ALL_END
                textScriptLine += "direct return;";
                resetRegisterTypes();
            } else if (opcode >= 0x59 && opcode <= 0x5C) { // POPI0..3 / SET_INT
                String p1t = resolveType(p1);
                int tempIndex = opcode - 0x59;
                String tmpIType = currentTempITypes.get(tempIndex);
                if (isWeakType(tmpIType)) {
                    tmpIType = p1t;
                    currentTempITypes.put(tempIndex, p1t);
                }
                String val = typed(p1, tmpIType);
                textScriptLine += "Set tmpI" + tempIndex + " = " + val + ";";
            } else if (opcode >= 0x5D && opcode <= 0x66) { // POPF0..9 / SET_FLOAT
                int tempIndex = opcode - 0x5D;
                textScriptLine += "Set tmpF" + tempIndex + " = " + p1 + ";";
            } else if (opcode >= 0x67 && opcode <= 0x6A) { // PUSHI0..3 / GET_INT
                int tempIndex = opcode - 0x67;
                StackObject stackObject = new StackObject(currentWorker, ins, "tmpI", true, "tmpI" + tempIndex);
                stackObject.referenceIndex = tempIndex;
                stack.push(stackObject);
            } else if (opcode >= 0x6B && opcode <= 0x74) { // PUSHF0..9 / GET_FLOAT
                int tempIndex = opcode - 0x6B;
                StackObject stackObject = new StackObject(currentWorker, ins, "float", true, "tmpF" + tempIndex);
                stackObject.referenceIndex = tempIndex;
                stack.push(stackObject);
            } else if (opcode == 0x77) { // REQWAIT / WAIT_DELETE
                boolean direct = !p1.expression && !p2.expression && isWeakType(p1.type) && isWeakType(p2.type) && p1.valueSigned < workers.length && p2.valueSigned < workers[p1.valueSigned].entryPoints.length;
                String w = p1.expression ? "(" + p1 + ")" : format2Or4Byte(p1.valueSigned);
                String e = p2.expression ? "(" + p2 + ")" : format2Or4Byte(p2.valueSigned);
                String scriptLabel = direct ? workers[p1.valueSigned].entryPoints[p2.valueSigned].getLabel() : ("w" + w + "e" + e);
                textScriptLine += "await " + scriptLabel + ";";
            } else if (opcode == 0x78) { // Never used: PREQWAIT / WAIT_SPEC_DELETE
            } else if (opcode == 0x79) { // REQCHG / EDIT_ENTRY_TABLE
                ScriptJump[] entryPoints = currentWorker.entryPoints;
                int oldIdx = p2.valueSigned + 2;
                int newIdx = p3.valueSigned;
                boolean direct = !p2.expression && !p3.expression && isWeakType(p2.type) && isWeakType(p3.type) && oldIdx < entryPoints.length && newIdx < entryPoints.length;
                String oldScriptLabel = direct ? entryPoints[oldIdx].getLabel() : ("e" + (p2.expression ? "(" + p2 + ")" : format2Or4Byte(oldIdx)));
                String newScriptLabel = direct ? entryPoints[newIdx].getLabel() : ("e" + (p3.expression ? "(" + p3 + ")" : format2Or4Byte(newIdx)));
                String tableHolder = p1.expression ? ""+p1 : ""+p1.valueSigned;
                if (p1.parentInstruction.opcode == 0xA7) {
                    addVarType(p1.valueSigned, "workerEventTable");
                }
                textScriptLine += "Replace script " + oldScriptLabel + " with " + newScriptLabel + " (store table at " + tableHolder + ")";;
                // textScriptLine += "REQCHG(" + p1 + ", " + p2 + ", " + p3 + ");";
            } else if (opcode == 0x7A) { // Never used: ACTREQ / SET_EDGE_TRIGGER
            } else if (opcode == 0x9F) { // PUSHV / GET_DATUM
                StackObject stackObject = new StackObject(currentWorker, ins, "var", true, getVariableLabel(argv));
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
                String val = typed(p1, getVariableType(argv));
                textScriptLine += getVariableLabel(argv) + " = " + val + ";";
            } else if (opcode == 0xA2) { // PUSHAR / GET_DATUM_INDEX
                StackObject stackObject = new StackObject(currentWorker, ins, "var", true, ensureVariableValidWithArray(argv, p1));
                stackObject.referenceIndex = argv;
                stack.push(stackObject);
            } else if (opcode == 0xA3 || opcode == 0xA4) { // POPAR(L) / SET_DATUM_INDEX_(W/T)
                addVarType(argv, resolveType(p2));
                textScriptLine += "Set ";
                if (opcode == 0xA4) {
                    textScriptLine += "(limit) ";
                }
                String val = typed(p2, getVariableType(argv));
                textScriptLine += ensureVariableValidWithArray(argv, p1) + " = " + val + ";";
            } else if (opcode == 0xA7) { // PUSHARP / GET_DATUM_DESC
                StackObject stackObject = new StackObject(currentWorker, ins, "pointer", true, "&" + ensureVariableValidWithArray(argv, p1));
                stackObject.referenceIndex = argv;
                stack.push(stackObject);
            } else if (opcode == 0xAD) { // PUSHI / CONST_INT
                int refInt = refInts[argv];
                StackObject stackObject = new StackObject(currentWorker, ins, "int32", refInt, refInt);
                stackObject.referenceIndex = argv;
                stack.push(stackObject);
            } else if (opcode == 0xAE) { // PUSHII / IMM
                StackObject stackObject = new StackObject(currentWorker, ins, "int16", ins.argvSigned, ins.argv);
                stack.push(stackObject);
            } else if (opcode == 0xAF) { // PUSHF / CONST_FLOAT
                int refFloat = refFloats[argv];
                StackObject stackObject = new StackObject(currentWorker, ins, "float", refFloat, refFloat);
                stackObject.referenceIndex = argv;
                stack.push(stackObject);
            } else if (opcode == 0xB0) { // JMP / JUMP
                ScriptJump jump = referenceJump(argv);
                textScriptLine += "Jump to " + (jump != null ? jump.getLabelWithAddr() : ("j" + StringHelper.formatHex2(argv)));
                resetRegisterTypes();
            } else if (opcode == 0xB1) { // Never used: CJMP / BNEZ
            } else if (opcode == 0xB2) { // Never used: NCJMP / BEZ
            } else if (opcode == 0xB3) { // JSR
                ScriptWorker worker = getWorker(argv);
                textScriptLine += "Jump to subroutine " + (worker != null ? worker.getIndexLabel() : ("w" + StringHelper.formatHex2(argv)));
            } else if (opcode == 0xB5) { // CALL / FUNC_RET
                List<StackObject> params = popParamsForFunc(argv);
                ScriptFunc func = getAndTypeFuncCall(argv, params);
                StackObject stackObject = new StackObject(currentWorker, ins, func.getType(params), true, func.callB5(params));
                stackObject.referenceIndex = argv;
                stack.push(stackObject);
            } else if (opcode == 0xD6) { // POPXCJMP / SET_BNEZ
                ScriptJump jump = referenceJump(argv);
                textScriptLine += "(" + p1 + ") -> " + (jump != null ? jump.getLabelWithAddr() : ("j" + StringHelper.formatHex2(argv)));
            } else if (opcode == 0xD7) { // POPXNCJMP / SET_BEZ
                ScriptJump jump = referenceJump(argv);
                textScriptLine += "Check (" + p1 + ") else jump to " + (jump != null ? jump.getLabelWithAddr() : ("j" + StringHelper.formatHex2(argv)));
            } else if (opcode == 0xD8) { // CALLPOPA / FUNC
                List<StackObject> params = popParamsForFunc(argv);
                ScriptFunc func = getAndTypeFuncCall(argv, params);
                currentRAType = func.getType(params);
                String call = func.callD8(params);
                textScriptLine += call + ';';
            } else if (opcode == 0xF6) { // SYSTEM
                textScriptLine += "System " + StringHelper.formatHex2(argv);
            }
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
            warningsOnLine.add("Empty stack for func " + StringHelper.formatHex4(idx));
        }
        return params;
    }

    protected ScriptFunc getAndTypeFuncCall(int idx, List<StackObject> params) {
        ScriptFunc func = ScriptFuncLib.get(idx, params);
        if (func == null) {
            func = new ScriptFunc("Unknown:" + StringHelper.formatHex4(idx), "unknown", null, false);
        }
        List<ScriptField> inputs = func.inputs;
        if (inputs != null && !inputs.isEmpty() && !params.isEmpty()) {
            int len = Math.min(inputs.size(), params.size());
            for (int i = 0; i < len; i++) {
                typed(params.get(i), inputs.get(i).type);
            }
        }
        return func;
    }

    protected void addVarType(int var, String type) {
        if (!gatheringInfo || variableDeclarations == null || var < 0 || var > variableDeclarations.length) {
            return;
        }
        variableDeclarations[var].inferType(type);
    }

    protected String resolveType(StackObject obj) {
        if (obj == null || obj.type == null) {
            return "unknown";
        }
        if ("var".equals(obj.type)) {
            return getVariableType(obj.referenceIndex);
        }
        if ("tmpI".equals(obj.type)) {
            return currentTempITypes.getOrDefault(obj.referenceIndex, "unknown");
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
            if ("var".equals(obj.type)) {
                addVarType(obj.referenceIndex, type);
            }
            if ("tmpI".equals(obj.type) && type != null && !"unknown".equals(type)) {
                currentTempITypes.put(obj.referenceIndex, type);
            }
            if (obj.expression || type == null || "unknown".equals(type)) {
                return obj.toString();
            } else {
                return new StackObject(type, obj).toString();
            }
        }
    }

    public String getVariableType(int index) {
        if (variableDeclarations != null && index >= 0 && index < variableDeclarations.length) {
            return variableDeclarations[index].inferredType;
        }
        warningsOnLine.add("Variable index " + StringHelper.formatHex2(index) + " out of bounds!");
        return "unknown";
    }

    public String getVariableLabel(int index) {
        if (variableDeclarations != null && index >= 0 && index < variableDeclarations.length) {
            return variableDeclarations[index].getLabel(getWorker(currentWorkerIndex));
        }
        String hexIdx = StringHelper.formatHex2(index);
        warningsOnLine.add("Variable index " + hexIdx + " out of bounds!");
        return "var" + hexIdx;
    }

    private String ensureVariableValidWithArray(int index, StackObject p1) {
        String varLabel = getVariableLabel(index);
        String indexType = resolveType(p1);
        if (isWeakType(indexType) && (variableDeclarations != null && index >= 0 && index < variableDeclarations.length)) {
            indexType = variableDeclarations[index].getArrayIndexType();
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

    protected static boolean hasArgs(int opcode) {
        return opcode >= 0x80 && opcode != 0xFF;
    }

    protected int getStackPops(int opcode) {
        int stackpops = ScriptConstants.OPCODE_STACKPOPS[opcode];
        if (stackpops < 0) {
            warningsOnLine.add("Undefined stackpops for opcode " + StringHelper.formatHex2(opcode));
            return 0;
        }
        return stackpops;
    }

    protected int getFunctionParamCount(int idx) {
        ScriptFunc func = ScriptFuncLib.get(idx, null);
        if (func == null) {
            warningsOnLine.add("Undefined stackpops for func " + StringHelper.formatHex4(idx));
            return 0;
        }
        return func.inputs != null ? func.inputs.size() : 0;
    }

    protected static boolean getLineEnd(int opcode) {
        return ScriptConstants.OPCODE_ENDLINE.contains(opcode);
    }

    protected void inferBooleans() {
        if (variableDeclarations == null) {
            return;
        }
        for (int varIdx = 0; varIdx < variableDeclarations.length; varIdx++) {
            ScriptVariable var = variableDeclarations[varIdx];
            if (isWeakType(var.inferredType) && varEnums.containsKey(varIdx)) {
                List<StackObject> enums = varEnums.get(varIdx);
                if (enums.size() == 1 && !enums.get(0).expression) {
                    constants.put(varIdx, enums.get(0));
                } else if (enums.stream().noneMatch(a -> a.expression)) {
                    Set<Integer> distinctContents = enums.stream().map(a -> a.valueSigned).collect(Collectors.toSet());
                    if (distinctContents.size() == 2 && distinctContents.contains(0) && (distinctContents.contains(0x01) || distinctContents.contains(0xFF))) {
                        var.inferType("bool");
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
    }

    @Override
    public String toString() {
        return "- Script Code -" + '\n' +
                allLinesString() +
                "- Script Workers -" + '\n' +
                workersString() + '\n';
    }

    public String allLinesString() {
        List<String> lines = new ArrayList<>();
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
        int offset = 0;
        for (ScriptInstruction ins : instructions) {
            String ol = String.format("%-6s", StringHelper.formatHex4(offset) + ' ');
            String jl = consoleColorIfEnabled(ANSI_PURPLE) + String.format("%-11s", getJumpLine(scriptJumpsByDestination.get(offset)) + ' ');
            String hl = consoleColorIfEnabled(ANSI_BLUE) + String.format("%-11s", ins.asSeparatedHexString() + ' ');
            String asml = consoleColorIfEnabled(ANSI_GREEN) + (ins.hasArgs ? String.format("%-10s", ins.getOpcodeLabel() + ' ') + consoleColorIfEnabled(ANSI_YELLOW) + ins.getArgLabel() : ins.getOpcodeLabel());
            lines.add(ol + jl + hl + asml + consoleColorIfEnabled(ANSI_RESET));
            offset += ins.length;
        }
        return String.join("\n", lines) + '\n';
    }

    public String workersString() {
        if (workers == null || workers.length == 0) {
            return "No Workers";
        }
        List<String> lines = new ArrayList<>();
        lines.add("Script Code Start Address: " + StringHelper.formatHex4(scriptCodeStartAddress));
        if (creatorTag != null) {
            lines.add("Creator: " + creatorTag);
        }
        if (mainScriptIndex != 0xFFFF) {
            lines.add("Main Worker: w" + StringHelper.formatHex2(mainScriptIndex));
        }
        if (mapEntrances != null) {
            lines.add(mapEntrances.length + " Map Entrances");
            for (int i = 0; i < mapEntrances.length; i++) {
                lines.add("Entrance " + StringHelper.formatHex2(i) + " " + mapEntrances[i].toString());
            }
        }
        if (PRINT_UNKNOWN_HEADER_VALS) {
            if (unknown1A != 0) {
                lines.add("unknown1A=" + StringHelper.formatHex4(unknown1A));
            }
            if (unknownOffset24 != 0) {
                lines.add("unknownOffset24=" + StringHelper.formatHex4(unknownOffset24) + "; size=" + StringHelper.formatHex4(areaNameIndexesOffset - unknownOffset24));
            }
            if (unknownSize40StructOffset != 0) {
                int[] other_offset_target = Arrays.copyOfRange(bytes, unknownSize40StructOffset, unknownSize40StructOffset + 0x40);
                lines.add("Struct pointed to at 0x2C");
                lines.add(Arrays.stream(other_offset_target).mapToObj((i) -> StringHelper.formatHex2(i)).collect(Collectors.joining(" ")));
            }
            // Order of offsets seems to be:
            // other_offset - eventData - areaNameIndexes - unknownOffset24
        }
        if (areaNameIndexes != null) {
            int firstAreaNameIndex = areaNameIndexes.get(0);
            List<Integer> differentAreaNameIndexes = areaNameIndexes.stream().filter((id) -> id != firstAreaNameIndex).toList();
            lines.add("Area Names");
            lines.add(MACRO_LOOKUP.get(0xB00 + firstAreaNameIndex).toString());
            differentAreaNameIndexes.forEach(i -> lines.add(MACRO_LOOKUP.get(0xB00 + i).toString()));
        }
        lines.add(namespaceCount > 1 ? namespaceCount + " Workers Total" : "1 Worker Total");
        for (int i = 0; i < namespaceCount; i++) {
            lines.add("w" + StringHelper.formatHex2(i) + ": " + workers[i].getNonCommonString());
        }
        lines.add("Variables (" + variableDeclarations.length + " at offset " + StringHelper.formatHex4(variableStructsTableOffset) + ")");
        if (variableDeclarations.length > 0) {
            List<String> refsStrings = new ArrayList<>();
            for (int i = 0; i < variableDeclarations.length; i++) {
                refsStrings.add("Variable " + StringHelper.formatHex2(i) + ": " + variableDeclarations[i] + " [" + String.format("%016X", variableDeclarations[i].fullBytes) + "h]");
            }
            lines.add(String.join("\n", refsStrings));
        }
        if (PRINT_REF_INTS_FLOATS) {
            lines.add("Integers (" + refInts.length + " at offset " + StringHelper.formatHex4(intTableOffset) + ")");
            if (refInts.length > 0) {
                List<String> intStrings = new ArrayList<>();
                for (int i = 0; i < refInts.length; i++) {
                    intStrings.add("refI" + StringHelper.formatHex2(i) + ": " + refInts[i] + " [" + String.format("%08X", refInts[i]) + "h]");
                }
                lines.add(String.join(", ", intStrings));
            }
            lines.add("Floats (" + refFloats.length + " at offset " + StringHelper.formatHex4(floatTableOffset) + ")");
            if (refFloats.length > 0) {
                List<String> floatStrings = new ArrayList<>();
                for (int i = 0; i < refFloats.length; i++) {
                    floatStrings.add("refF" + StringHelper.formatHex2(i) + ": " + Float.intBitsToFloat(refFloats[i]) + " [" + String.format("%08X", refFloats[i]) + "h]");
                }
                lines.add(String.join(", ", floatStrings));
            }
        }
        if (PRINT_JUMP_TABLE) {
            lines.add("- Jump Table -");
            for (int i = 0; i < namespaceCount; i++) {
                lines.add("w" + StringHelper.formatHex2(i));
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
