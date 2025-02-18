package atel.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ScriptLine {
    private static final Set<Integer> UNCONTINUING_OPCODES = Set.of(0x30, 0x34, 0x3C, 0x40, 0xB0);

    public final ScriptWorker scriptWorker;
    public int offset;
    public List<ScriptInstruction> instructions;
    public ScriptInstruction lineEnder;
    public List<ScriptJump> incomingJumps;

    public ScriptLine predecessor;
    public ScriptLine successor;
    public ScriptJump branch;

    public ScriptLine(ScriptWorker scriptWorker, int offset, List<ScriptInstruction> instructions, ScriptInstruction lineEnder) {
        this.scriptWorker = scriptWorker;
        this.offset = offset;
        this.instructions = instructions;
        this.lineEnder = lineEnder;
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
        return !UNCONTINUING_OPCODES.contains(lineEnder.opcode);
    }

    public List<ScriptLine> getPotentialPreviousLines() {
        List<ScriptLine> list = new ArrayList<>();
        if (predecessor != null && predecessor.continues()) {
            list.add(predecessor);
        }
        List<ScriptJump> properJumps = getProperJumps();
        if (!properJumps.isEmpty()) {
            list.addAll(properJumps.stream().map(j -> j.targetLine).toList());
        }
        return list;
    }

    public List<ScriptLine> getPotentialNextLines() {
        List<ScriptLine> list = new ArrayList<>();
        if (continues() && successor != null) {
            list.add(successor);
        }
        if (branch != null) {
            list.add(branch.targetLine);
        }
        return list;
    }
}
