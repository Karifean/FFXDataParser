package atel.model;

import java.util.List;

public class ScriptLine {

    public final ScriptWorker scriptWorker;
    public int offset;
    public List<ScriptInstruction> instructions;
    public List<ScriptJump> jumps;

    public ScriptJump branch;

    public ScriptLine(ScriptWorker scriptWorker, int offset, List<ScriptInstruction> instructions) {
        this.scriptWorker = scriptWorker;
        this.offset = offset;
        this.instructions = instructions;
    }

    public List<ScriptJump> getProperJumps() {
        return jumps != null ? jumps.stream().filter(j -> j.addr == offset).toList() : List.of();
    }

    public List<ScriptJump> getSoftMisalignedJumps() {
        return jumps != null ? jumps.stream().filter(j -> j.addr != offset && !j.hardMisaligned).toList() : List.of();
    }

    public List<ScriptJump> getHardMisalignedJumps() {
        return jumps != null ? jumps.stream().filter(j -> j.hardMisaligned).toList() : List.of();
    }
}
