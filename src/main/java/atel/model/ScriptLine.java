package atel.model;

import java.util.List;

public class ScriptLine {

    public int offset;
    public List<ScriptInstruction> instructions;

    public ScriptLine(int offset, List<ScriptInstruction> instructions) {
        this.offset = offset;
        this.instructions = instructions;
    }
}
