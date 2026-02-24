package atel.model;

import atel.AtelScriptObject;
import main.StringHelper;

import java.util.*;
import java.util.stream.Collectors;

import static atel.model.ScriptVariable.SAVEDATA_ATEL_OFFSET;
import static reading.BytesHelper.*;

public class ScriptWorker {
    public static final int LENGTH = 0x34;

    public int workerIndex;
    public int eventWorkerType;
    public int variablesCount;
    public int refIntCount;
    public int refFloatCount;
    public int functionCount;
    public int jumpCount;
    public final int alwaysZero0C;
    public int privateDataLength;
    public int privateDataLengthPadded;
    public int variableDeclarationsOffset;
    public int refIntsOffset;
    public int refFloatsOffset;
    public int functionEntryPointsOffset;
    public int jumpsOffset;
    public final int alwaysZero28;
    public int privateDataOffset;
    public int sharedDataOffset;

    public List<ScriptJump> functions;
    public List<ScriptJump> jumps;
    public List<ScriptVariable> variableDeclarations;
    public int[] refFloats;
    public int[] refInts;
    public int[] privateDataBytes;
    public int[] functionBattleSlotArray;

    public Integer battleWorkerType;
    public Integer battleWorkerTypeSlotCount;
    public Integer purposeSlot;

    public AtelScriptObject parentScript;

    public String declaredLabel;

    public ScriptWorker(AtelScriptObject parentScript, int workerIndex, int[] headerBytes) {
        this.parentScript = parentScript;
        this.workerIndex = workerIndex;
        eventWorkerType = read2Bytes(headerBytes,0x00);
        variablesCount = read2Bytes(headerBytes,0x02);
        refIntCount = read2Bytes(headerBytes,0x04);
        refFloatCount = read2Bytes(headerBytes, 0x06);
        functionCount = read2Bytes(headerBytes, 0x08);
        jumpCount = read2Bytes(headerBytes, 0x0A);
        alwaysZero0C = read4Bytes(headerBytes, 0x0C);
        privateDataLength = read4Bytes(headerBytes, 0x10);
        variableDeclarationsOffset = read4Bytes(headerBytes, 0x14);
        refIntsOffset = read4Bytes(headerBytes, 0x18);
        refFloatsOffset = read4Bytes(headerBytes, 0x1C);
        functionEntryPointsOffset = read4Bytes(headerBytes, 0x20);
        jumpsOffset = read4Bytes(headerBytes, 0x24);
        alwaysZero28 = read4Bytes(headerBytes, 0x28);
        privateDataOffset = read4Bytes(headerBytes, 0x2C);
        sharedDataOffset = read4Bytes(headerBytes, 0x30);
    }

    public ScriptWorker(AtelScriptObject parentScript, int workerIndex, int eventWorkerType) {
        this.parentScript = parentScript;
        this.workerIndex = workerIndex;
        this.eventWorkerType = eventWorkerType;
        variablesCount = 0;
        refIntCount = parentScript.refInts.length;
        refFloatCount = parentScript.refFloats.length;
        functionCount = 0;
        jumpCount = 0;
        alwaysZero0C = 0;
        privateDataLength = 0;
        alwaysZero28 = 0;
        variableDeclarationsOffset = parentScript.variableDeclarationsOffset;
        refIntsOffset = parentScript.refIntsOffset;
        refFloatsOffset = parentScript.refFloatsOffset;
        sharedDataOffset = parentScript.sharedDataOffset;
        variableDeclarations = parentScript.variableDeclarations;
        refInts = parentScript.refInts;
        refFloats = parentScript.refFloats;
        functions = new ArrayList<>();
        jumps = new ArrayList<>();
        privateDataBytes = new int[0];
    }

    public int[] toBytes() {
        int[] headerBytes = new int[ScriptWorker.LENGTH];
        write2Bytes(headerBytes, 0x00, eventWorkerType);
        write2Bytes(headerBytes, 0x02, variablesCount);
        write2Bytes(headerBytes, 0x04, refIntCount);
        write2Bytes(headerBytes, 0x06, refFloatCount);
        write2Bytes(headerBytes, 0x08, functionCount);
        write2Bytes(headerBytes, 0x0A, jumpCount);
        write4Bytes(headerBytes, 0x0C, alwaysZero0C);
        write4Bytes(headerBytes, 0x10, privateDataLength);
        write4Bytes(headerBytes, 0x14, variableDeclarationsOffset);
        write4Bytes(headerBytes, 0x18, refIntsOffset);
        write4Bytes(headerBytes, 0x1C, refFloatsOffset);
        write4Bytes(headerBytes, 0x20, functionEntryPointsOffset);
        write4Bytes(headerBytes, 0x24, jumpsOffset);
        write4Bytes(headerBytes, 0x28, alwaysZero28);
        write4Bytes(headerBytes, 0x2C, privateDataOffset);
        write4Bytes(headerBytes, 0x30, sharedDataOffset);
        return headerBytes;
    }

    public List<ScriptLine> getLines() {
        List<ScriptLine> list = new ArrayList<>();
        Set<ScriptJump> touchedFunctions = new HashSet<>();
        List<ScriptJump> sortedFunctions = functions.stream().sorted(Comparator.comparingInt(e -> e.addr)).toList();
        for (ScriptJump function : sortedFunctions) {
            if (!touchedFunctions.contains(function)) {
                List<ScriptLine> lines = function.getLines();
                list.addAll(lines);
                touchedFunctions.add(function);
                lines.forEach(l -> touchedFunctions.addAll(l.incomingJumps));
            }
        }
        return list;
    }

    public String toString() {
        return getIndexLabel();
    }

    public void setDeclaredLabel(String label) {
        declaredLabel = label != null && !label.isEmpty() ? label : null;
    }

    public String getLabel(String localization) {
        return declaredLabel != null ? declaredLabel : getDefaultLabel(localization);
    }

    public String getDefaultLabel(String localization) {
        String indexLabel = getIndexLabel();
        String descriptor = null;
        if (purposeSlot != null) {
            descriptor = StackObject.enumToScriptField("battleWorkerSlot", purposeSlot).getLabel();
        } else if (battleWorkerType != null) {
            descriptor = StackObject.enumToScriptField("battleWorkerType", battleWorkerType).getLabel();
        } else {
            descriptor = StackObject.enumToScriptField("eventWorkerType", eventWorkerType).getLabel();
            if (eventWorkerType == 1 && !functions.isEmpty()) {
                List<ScriptLine> initLines = functions.getFirst().getLines();
                boolean isPlayer = initLines.stream().anyMatch(l -> l.lineEnder.opcode == 0xD8 && l.lineEnder.argv == 0x43);
                if (isPlayer) {
                    descriptor += " - Player";
                } else {
                    int modelId = initLines.stream().map(l -> l.lineEnder).filter(l -> l.opcode == 0xD8 && (l.argv == 0x01 || l.argv == 0x134) && l.inputs != null && !l.inputs.isEmpty() && l.inputs.getFirst().opcode == 0xAE).mapToInt(l -> l.inputs.getFirst().argv).findAny().orElse(-1);
                    if (modelId != -1) {
                        descriptor += " - " + StackObject.asString(localization, "model", null, modelId, modelId, this, false);
                    }
                }
            } else if (eventWorkerType == 2 && functions.size() >= 5) {
                List<ScriptLine> crossLines = functions.get(4).getLines();
                ScriptLine transitionLine = crossLines.stream().filter(l -> l.lineEnder.opcode == 0xD8 && l.lineEnder.argv == 0x11).findAny().orElse(null);
                if (transitionLine != null && transitionLine.lineEnder.inputs != null) {
                    ScriptInstruction firstInput = transitionLine.lineEnder.inputs.get(0);
                    if (firstInput.opcode == 0xAE) {
                        int room = firstInput.argv;
                        descriptor += " - Goto " + StackObject.asString(localization, "room", null, room, room, this, false);
                        ScriptInstruction secondInput = transitionLine.lineEnder.inputs.get(1);
                        if (secondInput.opcode == 0xAE) {
                            descriptor += " [" + secondInput.argv + "]";
                        }
                    }
                }
            }
        }
        if (descriptor != null) {
            return indexLabel + " (" + descriptor + ")";
        } else {
            return indexLabel;
        }
    }

    public String getReferenceLabel() {
        return declaredLabel != null ? declaredLabel : getIndexLabel();
    }

    public String getIndexLabel() {
        return "w" + StringHelper.formatHex2(workerIndex);
    }

    public String getNonCommonString() {
        List<String> list = new ArrayList<>();
        if (battleWorkerType != null) {
            list.add("Battle");
            list.add("Type=" + StackObject.enumToString("battleWorkerType", battleWorkerType));
            if (eventWorkerType != 2) {
                list.add("EventType (not 2?)=" + StackObject.enumToString("eventWorkerType", eventWorkerType));
            }
        } else {
            list.add("Event");
            list.add("Type=" + StackObject.enumToString("eventWorkerType", eventWorkerType));
        }
        if (purposeSlot != null) {
            list.add("PurposeSlot=" + StackObject.enumToString("battleWorkerSlot", purposeSlot));
        }
        list.add("Functions=" + StringHelper.hex2WithSuffix(functionCount));
        list.add("Jumps=" + StringHelper.hex2WithSuffix(jumpCount));
        list.add("privateData addr=" + StringHelper.formatHex4(privateDataOffset) + " len=" + StringHelper.formatHex2(privateDataLength));
        list.add(alwaysZero0C != 0 ? "alwaysZero0C=" + alwaysZero0C : "");
        list.add(alwaysZero28 != 0 ? "alwaysZero28=" + alwaysZero28 : "");
        String full = list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining(", "));
        return "{ " + full + " }";
    }

    public ScriptJump getWorkerFunction(int index) {
        return index >= 0 && index < functions.size() ? functions.get(index) : null;
    }

    public List<ScriptJump> getWorkerFunctions() {
        return functions;
    }

    public String getWorkerFunctionsLine() {
        if (functions == null || functions.isEmpty()) {
            return null;
        }
        return functions.stream().map(e -> "f" + StringHelper.formatHex2(e.jumpIndex) + "=" + StringHelper.formatHex4(e.addr)).collect(Collectors.joining(" "));
    }

    public String getJumpsLine() {
        if (jumps == null || jumps.size() == 0) {
            return null;
        }
        return jumps.stream().map(j -> "j" + StringHelper.formatHex2(j.jumpIndex) + "=" + StringHelper.formatHex4(j.addr)).collect(Collectors.joining(" "));
    }

    public ScriptVariable getVariable(int index) {
        if (variableDeclarations != null && index >= 0 && index < variableDeclarations.size()) {
            return variableDeclarations.get(index);
        }
        return null;
    }

    public String getVariableLabel(int index) {
        ScriptVariable variable = getVariable(index);
        if (variable != null) {
            return variable.getLabel(this);
        } else {
            return "var" + StringHelper.formatHex2(index);
        }
    }

    public void setJumpTargets(Map<ScriptLine, ScriptJump> jumpTargets) {
        int jumpTargetCount = jumpTargets.size();
        ScriptJump[] jumpsArray = new ScriptJump[jumpTargetCount];
        for(Map.Entry<ScriptLine, ScriptJump> entry : jumpTargets.entrySet()) {
            ScriptJump jump = entry.getValue();
            jump.addr = entry.getKey().offset;
            jumpsArray[jump.jumpIndex] = jump;
        }
        jumps = new ArrayList<>(Arrays.stream(jumpsArray).toList());
    }

    public void setBattleWorkerTypes(int battleWorkerType, int slotCount, int[] payload) {
        this.battleWorkerType = battleWorkerType;
        this.battleWorkerTypeSlotCount = slotCount;
        functionBattleSlotArray = new int[slotCount];
        for (int i = 0; i < slotCount; i++) {
            int functionIndex = read2Bytes(payload, i * 2);
            functionBattleSlotArray[i] = functionIndex;
            ScriptJump func = functionIndex != 0xFFFF ? getWorkerFunction(functionIndex) : null;
            if (func != null) {
                func.setBattleWorkerFunctionSlot(i);
            }
        }
    }

    public int[] getBattleWorkerFunctionSlotsBytes() {
        if (battleWorkerTypeSlotCount == null) {
            return new int[0];
        }
        int[] bytes = new int[battleWorkerTypeSlotCount * 2 + 2];
        write2Bytes(bytes, 0, battleWorkerTypeSlotCount);
        Map<Integer, Integer> slotToFunctionIndexMap = new HashMap<>();
        for (int i = 0; i < functions.size(); i++) {
            ScriptJump func = getWorkerFunction(i);
            if (func.battleWorkerFunctionSlot != null) {
                slotToFunctionIndexMap.put(func.battleWorkerFunctionSlot, i);
            }
        }
        for (int i = 0; i < battleWorkerTypeSlotCount; i++) {
            int val = slotToFunctionIndexMap.getOrDefault(i, 0xFFFF);
            write2Bytes(bytes, i * 2 + 2, val);
        }
        return bytes;
    }

    public void setPurposeSlot(int purposeSlot) {
        this.purposeSlot = purposeSlot;
    }

    public void setPurposeSlotAndInferType(int slot) {
        this.purposeSlot = slot;
        battleWorkerType = getTypeForSlot(slot);
        battleWorkerTypeSlotCount = getSlotCountForType(battleWorkerType);
    }

    public static Integer getTypeForSlot(int slot) {
        if (slot == 0x00) {
            return 0;
        }
        if ((slot >= 0x3 && slot <= 0x23) || slot == 0x40) {
            return 1;
        }
        if ((slot >= 0x17 && slot <= 0x3A) || slot == 0x3D) {
            return 2;
        }
        if ((slot >= 0x4F && slot <= 0x5D) || slot == 0x4D) {
            return 3;
        }
        if (slot == 0x3E || slot == 0x3F) {
            return 4;
        }
        if (slot >= 0x6D && slot <= 0x7E) {
            return 5;
        }
        if (slot == 0x6C || slot == 0x89) {
            return 6;
        }
        if (slot == 0x41 || slot == 0x42 || slot == 0x43) {
            return 7;
        }
        if (slot == 0x44 || slot == 0x45 || slot == 0x46) {
            return 8;
        }
        if (slot == 0x47 || slot == 0x48 || slot == 0x49) {
            return 9;
        }
        if (slot == 0x4A || slot == 0x4B || slot == 0x4C) {
            return 10;
        }
        return null;
    }

    public static Integer getSlotCountForType(Integer type) {
        if (type == null) {
            return null;
        }
        return switch (type) {
            case 0x0 -> 0x8D;
            case 0x1 -> 0x51;
            case 0x2 -> 0x15;
            case 0x3 -> 0xC;
            case 0x4 -> 0x18;
            case 0x5 -> 0x10;
            case 0x6 -> 0x6;
            case 0x7 -> 0x13E;
            case 0x8 -> 0x6D;
            case 0x9 -> 0x12C;
            case 0xA -> 0xF6;
            default -> null;
        };
    }

    public ScriptJump getJump(int idx) {
        if (jumps == null || idx < 0 || idx >= jumps.size()) {
            return null;
        }
        return jumps.get(idx);
    }

    public void parseReferences(int[] bytes) {
        variableDeclarations = new ArrayList<>(variablesCount);
        for (int varIdx = 0; varIdx < variablesCount; varIdx++) {
            int lb = read4Bytes(bytes, variableDeclarationsOffset + varIdx * 8);
            int hb = read4Bytes(bytes, variableDeclarationsOffset + varIdx * 8 + 4);
            ScriptVariable scriptVariable = new ScriptVariable(this, varIdx, lb, hb);
            variableDeclarations.add(scriptVariable);
            scriptVariable.inferredType = "float".equals(scriptVariable.getFormatType()) ? "float" : "unknown";
            if (scriptVariable.location == 0) {
                ScriptField entry = ScriptConstants.FFX.getEnumMap("saveData").get(scriptVariable.offset + SAVEDATA_ATEL_OFFSET);
                if (entry != null) {
                    scriptVariable.declaredType = entry.type;
                }
            } else if (scriptVariable.location == 4) {
                // sharedData
                scriptVariable.parseValues();
            } else if (scriptVariable.location == 6) {
                // eventData
                scriptVariable.parseValues();
            }
        }
        refInts = new int[refIntCount];
        for (int i = 0; i < refIntCount; i++) {
            refInts[i] = read4Bytes(bytes, refIntsOffset + i * 4);
        }
        refFloats = new int[refFloatCount];
        for (int i = 0; i < refFloatCount; i++) {
            refFloats[i] = read4Bytes(bytes, refFloatsOffset + i * 4);
        }
        functions = new ArrayList<>(functionCount);
        for (int i = 0; i < functionCount; i++) {
            int addr = read4Bytes(bytes, functionEntryPointsOffset + i * 4);
            ScriptJump func = new ScriptJump(this, addr, i, true);
            functions.add(func);
        }
        jumps = new ArrayList<>(jumpCount);
        for (int i = 0; i < jumpCount; i++) {
            int addr = read4Bytes(bytes, jumpsOffset + i * 4);
            ScriptJump jump = new ScriptJump(this, addr, i, false);
            jumps.add(jump);
        }
        if (privateDataOffset > 0 && privateDataLength > 0) {
            privateDataBytes = Arrays.copyOfRange(bytes, privateDataOffset, privateDataOffset + privateDataLength);
        } else {
            privateDataBytes = new int[0];
        }
    }

    public void parseWorkerAtelCode(int[] scriptBytes) {
        Map<Integer, List<ScriptJump>> scriptJumpsByDestination = getScriptJumpsByDestination();
        Map<Integer, ScriptLine> linesByOffset = new HashMap<>();
        Stack<Integer> offsetsToParse = new Stack<>();
        for (ScriptJump func : functions) {
            int addr = func.addr;
            if (addr < 0) {
                continue;
            }
            offsetsToParse.push(addr);
            ScriptLine predecessor = null;
            while (!offsetsToParse.isEmpty()) {
                int offset = offsetsToParse.pop();
                if (linesByOffset.containsKey(offset)) {
                    if (predecessor != null) {
                        ScriptLine scriptLine = linesByOffset.get(offset);
                        scriptLine.predecessor = predecessor;
                        predecessor.successor = scriptLine;
                        predecessor = null;
                    }
                } else {
                    int cursor = offset;
                    int opcode;
                    ScriptInstruction instruction;
                    List<ScriptInstruction> lineInstructions = new ArrayList<>();
                    List<ScriptJump> jumpsOnLine = new ArrayList<>();
                    do {
                        List<ScriptJump> jumpsOnInstruction = new ArrayList<>();
                        collectJumps(cursor, scriptJumpsByDestination, jumpsOnInstruction, false);
                        opcode = scriptBytes[cursor];
                        cursor++;
                        if (hasArgs(opcode)) {
                            collectJumps(cursor, scriptJumpsByDestination, jumpsOnInstruction, true);
                            final int arg1 = scriptBytes[cursor];
                            cursor++;
                            collectJumps(cursor, scriptJumpsByDestination, jumpsOnInstruction, true);
                            final int arg2 = scriptBytes[cursor];
                            cursor++;
                            instruction = new ScriptInstruction(offset, opcode, arg1, arg2);
                            if (opcode == 0xAD) {
                                instruction.dereferencedArg = refInts[instruction.argv];
                            } else if (opcode == 0xAF) {
                                instruction.dereferencedArg = refFloats[instruction.argv];
                            } else if ((opcode >= 0x9F && opcode <= 0xA4) || opcode == 0xA7) {
                                instruction.dereferencedVar = variableDeclarations.get(instruction.argv);
                            }
                        } else if (opcode == 0x00) {
                            int count = 0;
                            do {
                                count++;
                                collectJumps(cursor, scriptJumpsByDestination, jumpsOnInstruction, false);
                                cursor++;
                                opcode = scriptBytes[cursor];
                            } while (opcode == 0x00);
                            instruction = new ScriptInstruction(offset, 0x00, count);
                        } else {
                            instruction = new ScriptInstruction(offset, opcode);
                        }
                        instruction.incomingJumps = jumpsOnInstruction;
                        jumpsOnLine.addAll(jumpsOnInstruction);
                        lineInstructions.add(instruction);
                    } while (ScriptOpcode.OPCODES[opcode] != null && !ScriptOpcode.OPCODES[opcode].isLineEnd);
                    ScriptLine scriptLine = new ScriptLine(this, offset, lineInstructions, jumpsOnLine);
                    linesByOffset.put(offset, scriptLine);
                    if (predecessor != null) {
                        scriptLine.predecessor = predecessor;
                        predecessor.successor = scriptLine;
                    }
                    Integer branchIndex = instruction.getBranchIndex();
                    if (branchIndex != null) {
                        ScriptJump jump = getJump(branchIndex);
                        scriptLine.branch = jump;
                        if (jump != null) {
                            offsetsToParse.push(jump.addr);
                        }
                    }
                    if (scriptLine.continues()) {
                        predecessor = scriptLine;
                        offsetsToParse.push(cursor);
                    } else {
                        predecessor = null;
                    }
                }
            }
            func.targetLine = linesByOffset.get(addr);
        }
        for (ScriptJump jump : jumps) {
            jump.targetLine = linesByOffset.get(jump.addr);
        }
    }

    public void addBlankFunctions(int newType, boolean isEventWorker) {
        ScriptJump init = new ScriptJump(this, -1, 0, true);
        functions.add(init);
        ScriptInstruction initIns = new ScriptInstruction(-1, 0x3C);
        init.targetLine = new ScriptLine(this, -1, List.of(initIns), List.of(init));

        if (isEventWorker && newType == 0) {
            return;
        }

        ScriptJump main = new ScriptJump(this, -1, 1, true);
        functions.add(main);
        ScriptInstruction mainIns = new ScriptInstruction(-1, 0xD8, 0x5F, 0x00);
        main.targetLine = new ScriptLine(this, -1, List.of(mainIns), List.of(main));

        if (isEventWorker) {
            for (int i = 2; i < 8; i++) {
                ScriptJump extra = new ScriptJump(this, -1, i, true);
                functions.add(extra);
                ScriptInstruction ins = new ScriptInstruction(-1, 0x3C);
                extra.targetLine = new ScriptLine(this, -1, List.of(ins), List.of(extra));
            }
        }
    }

    public ScriptJump addBlankFunction() {
        ScriptJump extra = new ScriptJump(this, -1, functions.size(), true);
        functions.add(extra);
        ScriptInstruction ins = new ScriptInstruction(-1, 0x3C);
        extra.targetLine = new ScriptLine(this, -1, List.of(ins), List.of(extra));
        return extra;
    }

    public ScriptWorker cloneRecursively(AtelScriptObject targetScript, int clonedWorkerIndex) {
        ScriptWorker clone = new ScriptWorker(targetScript, clonedWorkerIndex, eventWorkerType);
        if (battleWorkerType != null) {
            clone.battleWorkerType = battleWorkerType;
        }
        for (ScriptJump f : functions) {
            ScriptJump clonedFunc = new ScriptJump(clone, -1, clone.functions.size(), true);
            clonedFunc.targetLine = f.targetLine.cloneRecursively(clone, new HashMap<>());
            clonedFunc.targetLine.incomingJumps.add(clonedFunc);
            clone.functions.add(clonedFunc);
        }
        return clone;
    }

    public Set<ScriptInstruction> gatherDirectWorkerReferences() {
        Set<ScriptInstruction> gathered = new HashSet<>();
        for (ScriptJump func : functions) {
            ScriptState state = new ScriptState(func);
            for (ScriptLine line : state.lines) {
                line.gatherDirectWorkerReferences(state, gathered);
            }
        }
        return gathered;
    }

    private void collectJumps(int cursor, Map<Integer, List<ScriptJump>> scriptJumpsByDestination, List<ScriptJump> jumpsOnLine, boolean isArgByte) {
        if (!scriptJumpsByDestination.containsKey(cursor)) {
            return;
        }
        List<ScriptJump> jumps = scriptJumpsByDestination.get(cursor);
        jumpsOnLine.addAll(jumps);
        if (isArgByte) {
            jumps.forEach(ScriptJump::markAsHardMisaligned);
        }
    }

    protected static boolean hasArgs(int opcode) {
        return opcode >= 0x80 && opcode != 0xFF;
    }

    private Map<Integer, List<ScriptJump>> getScriptJumpsByDestination() {
        Map<Integer, List<ScriptJump>> scriptJumpsByDestination = new HashMap<>();
        for (ScriptJump func : functions) {
            scriptJumpsByDestination.computeIfAbsent(func.addr, (x) -> new ArrayList<>()).add(func);
        }
        for (ScriptJump jump : jumps) {
            scriptJumpsByDestination.computeIfAbsent(jump.addr, (x) -> new ArrayList<>()).add(jump);
        }
        return scriptJumpsByDestination;
    }
}
