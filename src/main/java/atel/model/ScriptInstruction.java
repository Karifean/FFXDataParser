package atel.model;

import main.StringHelper;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

public class ScriptInstruction {
    public final int offset;
    public final int opcode;
    public final boolean hasArgs;
    public final int length;
    public final int arg1;
    public final int arg2;
    public final int argv;
    public final int argvSigned;

    public List<ScriptJump> jumps;
    public List<ScriptJump> reachableFrom;

    public ScriptInstruction(int addr, int opcode) {
        this.offset = addr;
        this.opcode = opcode;
        this.hasArgs = false;
        this.length = 1;
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

    public String asHexString() {
        return StringHelper.formatHex2(opcode) + (hasArgs ? StringHelper.formatHex2(arg1) + StringHelper.formatHex2(arg2) : "");
    }

    public String asSeparatedHexString() {
        return StringHelper.formatHex2(opcode) + (hasArgs ? ' ' + StringHelper.formatHex2(arg1) + ' ' + StringHelper.formatHex2(arg2) : "");
    }

    public String getOpcodeLabel() {
        return ScriptConstants.OPCODE_LABELS[opcode];
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
}
