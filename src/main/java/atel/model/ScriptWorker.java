package atel.model;

import atel.AtelScriptObject;
import main.StringHelper;

import java.util.*;
import java.util.stream.Collectors;

import static atel.model.ScriptVariable.SAVEDATA_ATEL_OFFSET;
import static reading.BytesHelper.*;

public class ScriptWorker {
    public static final int LENGTH = 0x34;

    public final int workerIndex;
    public final int eventWorkerType;
    public int variablesCount;
    public int refIntCount;
    public int refFloatCount;
    public int entryPointCount;
    public int jumpCount;
    public final int alwaysZero0C;
    public int privateDataLength;
    public int privateDataLengthPadded;
    public int variableDeclarationsOffset;
    public int refIntsOffset;
    public int refFloatsOffset;
    public int scriptEntryPointsOffset;
    public int jumpsOffset;
    public final int alwaysZero28;
    public int privateDataOffset;
    public int sharedDataOffset;

    public List<ScriptJump> entryPoints;
    public ScriptJump[] jumps;
    public ScriptVariable[] variableDeclarations;
    public int[] refFloats;
    public int[] refInts;
    public int[] privateDataBytes;
    public int[] entryPointBattleSlotArray;

    public Integer battleWorkerType;
    public Integer battleWorkerTypeSlotCount;
    public Integer purposeSlot;

    public AtelScriptObject parentScript;

    public ScriptWorker(AtelScriptObject parentScript, int workerIndex, int[] headerBytes) {
        this.parentScript = parentScript;
        this.workerIndex = workerIndex;
        eventWorkerType = read2Bytes(headerBytes,0x00);
        variablesCount = read2Bytes(headerBytes,0x02);
        refIntCount = read2Bytes(headerBytes,0x04);
        refFloatCount = read2Bytes(headerBytes, 0x06);
        entryPointCount = read2Bytes(headerBytes, 0x08);
        jumpCount = read2Bytes(headerBytes, 0x0A);
        alwaysZero0C = read4Bytes(headerBytes, 0x0C);
        privateDataLength = read4Bytes(headerBytes, 0x10);
        variableDeclarationsOffset = read4Bytes(headerBytes, 0x14);
        refIntsOffset = read4Bytes(headerBytes, 0x18);
        refFloatsOffset = read4Bytes(headerBytes, 0x1C);
        scriptEntryPointsOffset = read4Bytes(headerBytes, 0x20);
        jumpsOffset = read4Bytes(headerBytes, 0x24);
        alwaysZero28 = read4Bytes(headerBytes, 0x28);
        privateDataOffset = read4Bytes(headerBytes, 0x2C);
        sharedDataOffset = read4Bytes(headerBytes, 0x30);
    }

    public ScriptWorker(AtelScriptObject parentScript, int workerIndex, ScriptWorker prototypeWorker, int eventWorkerType) {
        this.parentScript = parentScript;
        this.workerIndex = workerIndex;
        this.eventWorkerType = eventWorkerType;
        variablesCount = 0;
        refIntCount = parentScript.refInts.length;
        refFloatCount = parentScript.refFloats.length;
        entryPointCount = 0;
        jumpCount = 0;
        alwaysZero0C = 0;
        privateDataLength = 0;
        alwaysZero28 = 0;
        variableDeclarationsOffset = prototypeWorker.variableDeclarationsOffset;
        refIntsOffset = prototypeWorker.refIntsOffset;
        refFloatsOffset = prototypeWorker.refFloatsOffset;
        sharedDataOffset = prototypeWorker.sharedDataOffset;
        parseReferences(parentScript.getBytes());
    }

    public int[] toBytes() {
        int[] headerBytes = new int[ScriptWorker.LENGTH];
        write2Bytes(headerBytes, 0x00, eventWorkerType);
        write2Bytes(headerBytes, 0x02, variablesCount);
        write2Bytes(headerBytes, 0x04, refIntCount);
        write2Bytes(headerBytes, 0x06, refFloatCount);
        write2Bytes(headerBytes, 0x08, entryPointCount);
        write2Bytes(headerBytes, 0x0A, jumpCount);
        write4Bytes(headerBytes, 0x0C, alwaysZero0C);
        write4Bytes(headerBytes, 0x10, privateDataLength);
        write4Bytes(headerBytes, 0x14, variableDeclarationsOffset);
        write4Bytes(headerBytes, 0x18, refIntsOffset);
        write4Bytes(headerBytes, 0x1C, refFloatsOffset);
        write4Bytes(headerBytes, 0x20, scriptEntryPointsOffset);
        write4Bytes(headerBytes, 0x24, jumpsOffset);
        write4Bytes(headerBytes, 0x28, alwaysZero28);
        write4Bytes(headerBytes, 0x2C, privateDataOffset);
        write4Bytes(headerBytes, 0x30, sharedDataOffset);
        return headerBytes;
    }

    public List<ScriptLine> getLines() {
        List<ScriptLine> list = new ArrayList<>();
        Set<ScriptJump> touchedEntryPoints = new HashSet<>();
        List<ScriptJump> sortedEntryPoints = entryPoints.stream().sorted(Comparator.comparingInt(e -> e.addr)).toList();
        for (ScriptJump entryPoint : sortedEntryPoints) {
            if (!touchedEntryPoints.contains(entryPoint)) {
                List<ScriptLine> lines = entryPoint.getLines();
                list.addAll(lines);
                touchedEntryPoints.add(entryPoint);
                lines.forEach(l -> touchedEntryPoints.addAll(l.incomingJumps));
            }
        }
        return list;
    }

    public String toString() {
        return getIndexLabel();
    }

    public String getIndexLabel() {
        return "w" + StringHelper.formatHex2(workerIndex);
    }

    public String getNonCommonString() {
        List<String> list = new ArrayList<>();
        if (battleWorkerType != null) {
            list.add("Battle");
            list.add("Type=" + StackObject.enumToString("battleWorkerType", battleWorkerType));
        } else {
            list.add("Event");
            list.add("Type=" + StackObject.enumToString("eventWorkerType", eventWorkerType));
        }
        if (purposeSlot != null) {
            list.add("PurposeSlot=" + StackObject.enumToString("battleWorkerSlot", purposeSlot));
        }
        list.add("Entrypoints=" + StringHelper.hex2WithSuffix(entryPointCount));
        list.add("Jumps=" + StringHelper.hex2WithSuffix(jumpCount));
        list.add("privateData addr=" + StringHelper.formatHex4(privateDataOffset) + " len=" + StringHelper.formatHex2(privateDataLength));
        list.add(alwaysZero0C != 0 ? "alwaysZero0C=" + alwaysZero0C : "");
        list.add(alwaysZero28 != 0 ? "alwaysZero28=" + alwaysZero28 : "");
        String full = list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining(", "));
        return "{ " + full + " }";
    }

    public ScriptJump getEntryPoint(int index) {
        return index >= 0 && index < entryPoints.size() ? entryPoints.get(index) : null;
    }

    public List<ScriptJump> getEntryPoints() {
        return entryPoints;
    }

    public String getEntryPointsLine() {
        if (entryPoints == null || entryPoints.isEmpty()) {
            return null;
        }
        return entryPoints.stream().map(e -> "e" + StringHelper.formatHex2(e.jumpIndex) + "=" + StringHelper.formatHex4(e.addr)).collect(Collectors.joining(" "));
    }

    public String getJumpsLine() {
        if (jumps == null || jumps.length == 0) {
            return null;
        }
        return Arrays.stream(jumps).map(j -> "j" + StringHelper.formatHex2(j.jumpIndex) + "=" + StringHelper.formatHex4(j.addr)).collect(Collectors.joining(" "));
    }

    public void setJumpTargets(Map<ScriptLine, ScriptJump> jumpTargets) {
        int jumpTargetCount = jumpTargets.size();
        jumps = new ScriptJump[jumpTargetCount];
        for(Map.Entry<ScriptLine, ScriptJump> entry : jumpTargets.entrySet()) {
            ScriptJump jump = entry.getValue();
            jump.addr = entry.getKey().offset;
            jumps[jump.jumpIndex] = jump;
        }
    }

    public void setBattleWorkerTypes(int battleWorkerType, int slotCount, int[] payload) {
        this.battleWorkerType = battleWorkerType;
        this.battleWorkerTypeSlotCount = slotCount;
        entryPointBattleSlotArray = new int[slotCount];
        for (int i = 0; i < slotCount; i++) {
            int entryPointIndex = read2Bytes(payload, i * 2);
            entryPointBattleSlotArray[i] = entryPointIndex;
            ScriptJump entryPoint = entryPointIndex != 0xFFFF ? getEntryPoint(entryPointIndex) : null;
            if (entryPoint != null) {
                entryPoint.setBattleWorkerEntryPointSlot(i);
            }
        }
    }

    public int[] getBattleWorkerEntryPointSlotsBytes() {
        if (battleWorkerTypeSlotCount == null) {
            return new int[0];
        }
        int[] bytes = new int[battleWorkerTypeSlotCount * 2 + 2];
        write2Bytes(bytes, 0, battleWorkerTypeSlotCount);
        Map<Integer, Integer> slotToEntryPointIndexMap = new HashMap<>();
        for (int i = 0; i < entryPoints.size(); i++) {
            ScriptJump entryPoint = getEntryPoint(i);
            if (entryPoint.battleWorkerEntryPointSlot != null) {
                slotToEntryPointIndexMap.put(entryPoint.battleWorkerEntryPointSlot, i);
            }
        }
        for (int i = 0; i < battleWorkerTypeSlotCount; i++) {
            int val = slotToEntryPointIndexMap.getOrDefault(i, 0xFFFF);
            write2Bytes(bytes, i * 2 + 2, val);
        }
        return bytes;
    }

    public void setPurposeSlot(int purposeSlot) {
        this.purposeSlot = purposeSlot;
    }

    public ScriptJump getJump(int idx) {
        if (jumps == null || idx < 0 || idx >= jumps.length) {
            return null;
        }
        return jumps[idx];
    }

    public void parseReferences(int[] bytes) {
        variableDeclarations = new ScriptVariable[variablesCount];
        for (int varIdx = 0; varIdx < variablesCount; varIdx++) {
            int lb = read4Bytes(bytes, variableDeclarationsOffset + varIdx * 8);
            int hb = read4Bytes(bytes, variableDeclarationsOffset + varIdx * 8 + 4);
            ScriptVariable scriptVariable = new ScriptVariable(this, varIdx, lb, hb);
            variableDeclarations[varIdx] = scriptVariable;
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
        entryPoints = new ArrayList<>(entryPointCount);
        for (int i = 0; i < entryPointCount; i++) {
            int addr = read4Bytes(bytes, scriptEntryPointsOffset + i * 4);
            ScriptJump entryPoint = new ScriptJump(this, addr, i, true);
            entryPoints.add(entryPoint);
        }
        jumps = new ScriptJump[jumpCount];
        for (int i = 0; i < jumpCount; i++) {
            int addr = read4Bytes(bytes, jumpsOffset + i * 4);
            ScriptJump jump = new ScriptJump(this, addr, i, false);
            jumps[i] = jump;
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
        for (ScriptJump entryPoint : entryPoints) {
            int addr = entryPoint.addr;
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
                                instruction.dereferencedVar = variableDeclarations[instruction.argv];
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
                    } while (!ScriptConstants.OPCODE_ENDLINE.contains(opcode));
                    ScriptLine scriptLine = new ScriptLine(this, offset, lineInstructions, instruction, jumpsOnLine);
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
            entryPoint.targetLine = linesByOffset.get(addr);
        }
        for (ScriptJump jump : jumps) {
            jump.targetLine = linesByOffset.get(jump.addr);
        }
    }

    public void addBlankEntryPoints() {
        ScriptJump init = new ScriptJump(this, -1, 0, true);
        entryPoints.add(init);
        ScriptInstruction initIns = new ScriptInstruction(-1, 0x3C);
        init.targetLine = new ScriptLine(this, -1, List.of(initIns), initIns, List.of(init));

        ScriptJump main = new ScriptJump(this, -1, 1, true);
        entryPoints.add(main);
        ScriptInstruction mainIns = new ScriptInstruction(-1, 0xD8, 0x5F, 0x00);
        main.targetLine = new ScriptLine(this, -1, List.of(mainIns), mainIns, List.of(main));
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
        for (ScriptJump entryPoint : entryPoints) {
            scriptJumpsByDestination.computeIfAbsent(entryPoint.addr, (x) -> new ArrayList<>()).add(entryPoint);
        }
        for (ScriptJump jump : jumps) {
            scriptJumpsByDestination.computeIfAbsent(jump.addr, (x) -> new ArrayList<>()).add(jump);
        }
        return scriptJumpsByDestination;
    }
}
