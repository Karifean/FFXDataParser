package atel.model;

import atel.AtelScriptObject;
import main.StringHelper;

import java.util.*;
import java.util.stream.Collectors;

import static main.StringHelper.*;

public class ScriptLine {
    private static final int JUMP_LINE_MINLENGTH = 16;
    private static final int HEX_LINE_MINLENGTH = COLORS_USE_CONSOLE_CODES ? 58 : 48;
    private static final int JUMP_PLUS_HEX_LINE_MINLENGTH = JUMP_LINE_MINLENGTH + HEX_LINE_MINLENGTH + 1;

    public ScriptWorker parentWorker;
    public AtelScriptObject parentScript;
    public int offset;
    public List<ScriptInstruction> instructions;
    public ScriptInstruction lineEnder;
    public List<ScriptJump> incomingJumps;

    public ScriptLine predecessor;
    public ScriptLine successor;
    public ScriptJump branch;
    public List<String> warnings = new ArrayList<>();

    private boolean malformed = false;

    public ScriptLine(ScriptWorker parentWorker, int offset, List<ScriptInstruction> instructions, ScriptInstruction lineEnder, List<ScriptJump> incomingJumps) {
        this.parentWorker = parentWorker;
        this.parentScript = parentWorker != null ? parentWorker.parentScript : null;
        this.offset = offset;
        this.instructions = instructions != null ? new ArrayList<>(instructions) : new ArrayList<>();
        this.lineEnder = lineEnder;
        this.incomingJumps = new ArrayList<>(incomingJumps);
        if (instructions != null && !instructions.isEmpty()) {
            instructions.forEach(ins -> ins.setParentLine(this));
            setUpInputs();
        }
    }

    public List<Integer> toBytesList() {
        List<Integer> list = new ArrayList<>();
        instructions.forEach(ins -> list.addAll(ins.toBytesList()));
        return list;
    }

    public void rereference(int newOffset, Map<ScriptLine, ScriptJump> workerJumpTargets, List<ScriptVariable> variableDeclarations, List<Integer> refInts, List<Integer> refFloats) {
        offset = newOffset;
        incomingJumps.stream().filter(j -> j.isEntryPoint).forEach(j -> j.addr = newOffset);
        int cursor = newOffset;
        for (ScriptInstruction ins : instructions) {
            cursor = ins.rereference(cursor, variableDeclarations, refInts, refFloats);
        }
        if (branch != null) {
            int index;
            if (workerJumpTargets.containsKey(branch.targetLine)) {
                ScriptJump existing = workerJumpTargets.get(branch.targetLine);
                index = existing.jumpIndex;
                branch = existing;
            } else {
                index = workerJumpTargets.size();
                workerJumpTargets.put(branch.targetLine, branch);
                branch.jumpIndex = index;
            }
            lineEnder.setArgv(index);
        }
    }

    public List<ScriptJump> getProperJumps() {
        return incomingJumps != null ? incomingJumps.stream().filter(j -> j.addr == offset).toList() : List.of();
    }

    public List<ScriptJump> getSoftMisalignedJumps() {
        return incomingJumps != null ? incomingJumps.stream().filter(j -> j.addr != offset && !j.hardMisaligned).toList() : List.of();
    }

    public List<ScriptJump> getHardMisalignedJumps() {
        return incomingJumps != null ? incomingJumps.stream().filter(j -> j.hardMisaligned).toList() : List.of();
    }

    public boolean continues() {
        if (lineEnder.opcode == 0xD8 && lineEnder.argv == 0x5F) {
            return false;
        }
        return !ScriptConstants.OPCODES_UNCONTINUING.contains(lineEnder.opcode);
    }

    public void setInstructions(List<ScriptInstruction> instructions, ScriptLine nextInList) {
        malformed = instructions.isEmpty();
        this.instructions = instructions;
        if (malformed) {
            return;
        }
        instructions.forEach(ins -> ins.setParentLine(this));
        lineEnder = instructions.getLast();
        setUpInputs();
        if (continues()) {
            if (nextInList != null) {
                nextInList.predecessor = this;
            }
            successor = nextInList;
        } else {
            if (nextInList != null) {
                nextInList.predecessor = null;
            }
            successor = null;
        }
    }

    public void setUpInputs() {
        try {
            Stack<ScriptInstruction> stack = new Stack<>();
            for (ScriptInstruction instruction : instructions) {
                instruction.setUpInputs(stack);
                instruction.pushOutput(stack);
            }
        } catch (EmptyStackException e) {
            malformed = true;
            warnings.add("Malformed (EmptyStackException)");
        }
    }

    public String asString(ScriptState state) {
        String ol = String.format("%-5s", StringHelper.formatHex4(offset) + ' ');
        String jl = consoleColorIfEnabled(ANSI_PURPLE) + String.format("%-" + JUMP_LINE_MINLENGTH + "s", getJumpsString(state)) + ' ';
        String jhl = String.format("%-" + JUMP_PLUS_HEX_LINE_MINLENGTH + "s", jl + consoleColorIfEnabled(ANSI_BLUE) + asHexString()) + ' ';
        String tl = consoleColorIfEnabled(ANSI_RESET) + lineEnder.asString(state);
        String wl = warnings.isEmpty() ? "" : (consoleColorIfEnabled(ANSI_RED) + String.join(" ", warnings));
        return ol + jhl + tl + wl + consoleColorIfEnabled(ANSI_RESET);
    }

    public String getJumpsString(ScriptState state) {
        List<String> jumps = incomingJumps.stream().filter(j -> !j.isEntryPoint).map(j -> j.getLabel()).toList();
        if (jumps.isEmpty()) {
            return "";
        }
        return String.join(",", jumps) + ":";
    }

    public String asHexString() {
        return instructions.stream().map(ins -> ins.asHexString()).collect(Collectors.joining(" "));
    }

    @Override
    public String toString() {
        return asHexString();
    }
}
