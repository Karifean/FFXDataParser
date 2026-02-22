package atel.model;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import main.StringHelper;

import java.util.List;

public class ScriptOpcode extends ScriptField {
    public static ScriptOpcode[] OPCODES;

    public static void initialize() {
        if (OPCODES != null) {
            return;
        }
        OPCODES = new ScriptOpcode[0x100];
        putOpcode(0x00, "No Operation", "void", "NOP");
        putOpcode(0x01, "OR", "bool", "OPLOR", "%s or %s", p("a", "bool"), p("b", "bool"));
        putOpcode(0x02, "AND", "bool", "OPLAND", "%s and %s", p("a", "bool"), p("b", "bool"));
        putOpcode(0x03, "bitwise OR", "int", "OPOR", "%s | %s", p("a", "int"), p("b", "int"));
        putOpcode(0x04, "bitwise XOR", "int", "OPEOR", "%s ^ %s", p("a", "int"), p("b", "int"));
        putOpcode(0x05, "bitwise AND", "int", "OPAND", "%s & %s", p("a", "int"), p("b", "int"));
        putOpcode(0x06, "Equal", "bool", "OPEQ", "%s == %s", p("a", "unknown"), p("b", "unknown"));
        putOpcode(0x07, "Not Equal", "bool", "OPNE", "%s != %s", p("a", "unknown"), p("b", "unknown"));
        putOpcode(0x08, "Greater than (unsigned)", "bool", "OPGTU", "%s > (unsigned) %s", p("a", "int"), p("b", "int"));
        putOpcode(0x09, "Less than (unsigned)", "bool", "OPLSU", "%s < (unsigned) %s", p("a", "int"), p("b", "int"));
        putOpcode(0x0A, "Greater than", "bool", "OPGT", "%s > %s", p("a", "int"), p("b", "int"));
        putOpcode(0x0B, "Less than", "bool", "OPLS", "%s < %s", p("a", "int"), p("b", "int"));
        putOpcode(0x0C, "Greater or equal (unsigned)", "bool", "OPGTEU", "%s >= (unsigned) %s", p("a", "int"), p("b", "int"));
        putOpcode(0x0D, "Less or equal (unsigned)", "bool", "OPLSEU", "%s <= (unsigned) %s", p("a", "int"), p("b", "int"));
        putOpcode(0x0E, "Greater or equal", "bool", "OPGTE", "%s >= %s", p("a", "int"), p("b", "int"));
        putOpcode(0x0F, "Less or equal", "bool", "OPLSE", "%s <= %s", p("a", "int"), p("b", "int"));
        putOpcode(0x10, "Enable bit", "int", "OPBON", "%s B-ON %s", p("a", "int"), p("b", "int"));
        putOpcode(0x11, "Disable bit", "int", "OPBOFF", "%s B-OFF %s", p("a", "int"), p("b", "int"));
        putOpcode(0x12, "Shift left", "int", "OPSLL", "%s << %s", p("a", "int"), p("b", "int"));
        putOpcode(0x13, "Shift right", "int", "OPSRL", "%s >> %s", p("a", "int"), p("b", "int"));
        putOpcode(0x14, "Add", "int", "OPADD", "%s + %s", p("a", "int"), p("b", "int"));
        putOpcode(0x15, "Subtract", "int", "OPSUB", "%s - %s", p("a", "int"), p("b", "int"));
        putOpcode(0x16, "Multiply", "int", "OPMUL", "%s * %s", p("a", "int"), p("b", "int"));
        putOpcode(0x17, "Divide", "int", "OPDIV", "%s / %s", p("a", "int"), p("b", "int"));
        putOpcode(0x18, "Modulo", "int", "OPMOD", "%s mod %s", p("a", "int"), p("b", "int"));
        putOpcode(0x19, "NOT", "bool", "OPNOT", "!%s", p("cond", "bool"));
        putOpcode(0x1A, "Negate", "int", "OPUMINUS", "-%s", p("int", "int"));
        putOpcode(0x1B, "OPFIXADRS");
        putOpcode(0x1C, "bitwise NOT", "int", "OPBNOT", "~%s", p("a", "int"));
        putOpcode(0x1D, "LABEL");
        putOpcode(0x1E, "TAG");
        putOpcode(0x9F, "Get variable", "var", "PUSHV");
        putOpcode(0xA0, "Set variable", "void", "POPV", p("value", "unknown"));
        putOpcode(0xA1, "Set variable (limited)", "void", "POPVL", p("value", "unknown"));
        putOpcode(0xA2, "Get array item", "var", "PUSHAR", p("index", "int"));
        putOpcode(0xA3, "Set array item", "void", "POPAR", p("index", "int"), p("value", "unknown"));
        putOpcode(0xA4, "Set array item (limited)", "void", "POPARL", p("index", "int"), p("value", "unknown"));
        putOpcode(0x25, "Set LastCallResult (rA)", "void", "POPA", p("value", "unknown"));
        putOpcode(0x26, "Get LastCallResult (rA)", "unknown", "PUSHA");
        putOpcode(0xA7, "Get array pointer", "pointer", "PUSHARP", p("index", "int"));
        putOpcode(0x28, "Get test (rX)", "unknown", "PUSHX");
        putOpcode(0x29, "case (Get rY)", "unknown", "PUSHY");
        putOpcode(0x2A, "Set test (rX)", "void", "POPX", p("value", "unknown"));
        putOpcode(0x2B, "Clone", "unknown", "REPUSH", p("value", "unknown"));
        putOpcode(0x2C, "switch (Set rY)", "void", "POPY", p("value", "unknown"));
        putOpcode(0xAD, "Direct value (int32)", "unknown", "PUSHI");
        putOpcode(0xAE, "Direct value (int16)", "unknown", "PUSHII");
        putOpcode(0xAF, "Direct value (float)", "float", "PUSHF");
        putOpcode(0xB0, "Jump always", "void", "JMP");
        putOpcode(0xB1, "Jump if last test true", "void", "CJMP", p("value", "bool"));
        putOpcode(0xB2, "Jump if last test false", "void", "NCJMP", p("value", "bool"));
        putOpcode(0xB3, "Jump to subroutine (worker)", "void", "JSR");
        putOpcode(0x34, "Return from subroutine", "void", "RTS");
        putOpcode(0xB5, "Call", "unknown", "CALL");
        putOpcode(0x36, "Run worker function asynchronously", "bool", "REQ", p("level", "int"), p("w", "worker"), p("f", "int"));
        putOpcode(0x37, "Run worker function and await start", "bool", "REQSW", p("level", "int"), p("w", "worker"), p("f", "int"));
        putOpcode(0x38, "Run worker function and await end", "bool", "REQEW", p("level", "int"), p("w", "worker"), p("f", "int"));
        putOpcode(0x39, "Run party member function asynchronously", "bool", "PREQ", p("level", "int"), p("index", "int"), p("f", "int"));
        putOpcode(0x3A, "Run party member function and await start", "bool", "PREQSW", p("level", "int"), p("index", "int"), p("f", "int"));
        putOpcode(0x3B, "Run party member function and await end", "bool", "PREQEW", p("level", "int"), p("index", "int"), p("f", "int"));
        putOpcode(0x3C, "Return", "void", "RET");
        putOpcode(0x3D, "Return with cleanup", "void", "RETN", p("value", "unknown"));
        putOpcode(0x3E, "Return to main", "void", "RETT");
        putOpcode(0x3F, "Return with cleanup to main", "void", "RETTN", p("value", "unknown"));
        putOpcode(0x40, "Halt", "void", "HALT");
        putOpcode(0x41, "PUSHN");
        putOpcode(0x42, "PUSHT");
        putOpcode(0x43, "PUSHVP");
        putOpcode(0x44, "PUSHFIX");
        putOpcode(0x45, "Run worker function (forcibly) asynchronously", "bool", "FREQ", p("level", "int"), p("w", "worker"), p("f", "int"));
        putOpcode(0x46, "Run worker function (if not queued/running) asynchronously", "bool", "TREQ", p("level", "int"), p("w", "worker"), p("f", "int"));
        putOpcode(0x47, "Run worker function (conditionally) asynchronously", "bool", "BREQ", p("level", "int"), p("w", "worker"), p("f", "int"));
        putOpcode(0x48, "Run worker function (conditionally, forcibly) asynchronously", "bool", "BFREQ", p("level", "int"), p("w", "worker"), p("f", "int"));
        putOpcode(0x49, "Run worker function (conditionally, if not queued/running) asynchronously", "bool", "BTREQ", p("level", "int"), p("w", "worker"), p("f", "int"));
        putOpcode(0x4A, "Run worker function (forcibly) and await start", "bool", "FREQSW", p("level", "int"), p("w", "worker"), p("f", "int"));
        putOpcode(0x4B, "Run worker function (if not queued/running) and await start", "bool", "TREQSW", p("level", "int"), p("w", "worker"), p("f", "int"));
        putOpcode(0x4C, "Run worker function (conditionally) and await start", "bool", "BREQSW", p("level", "int"), p("w", "worker"), p("f", "int"));
        putOpcode(0x4D, "Run worker function (conditionally, forcibly) and await start", "bool", "BFREQSW", p("level", "int"), p("w", "worker"), p("f", "int"));
        putOpcode(0x4E, "Run worker function (conditionally, if not queued/running) and await start", "bool", "BTREQSW", p("level", "int"), p("w", "worker"), p("f", "int"));
        putOpcode(0x4F, "Run worker function (forcibly) and await end", "bool", "FREQEW", p("level", "int"), p("w", "worker"), p("f", "int"));
        putOpcode(0x50, "Run worker function (if not queued/running) and await end", "bool", "TREQEW", p("level", "int"), p("w", "worker"), p("f", "int"));
        putOpcode(0x51, "Run worker function (conditionally) and await end", "bool", "BREQEW", p("level", "int"), p("w", "worker"), p("f", "int"));
        putOpcode(0x52, "Run worker function (conditionally, forcibly) and await end", "bool", "BFREQEW", p("level", "int"), p("w", "worker"), p("f", "int"));
        putOpcode(0x53, "Run worker function (conditionally, if not queued/running) and await end", "bool", "BTREQEW", p("level", "int"), p("w", "worker"), p("f", "int"));
        putOpcode(0x54, "Direct return", "void", "DRET");
        putOpcode(0xD5, "Check and always jump", "void", "POPXJMP", p("condition", "bool"));
        putOpcode(0xD6, "Check and jump if true", "void", "POPXCJMP", p("condition", "bool"));
        putOpcode(0xD7, "Check and jump if false", "void", "POPXNCJMP", p("condition", "bool"));
        putOpcode(0xD8, "Call", "void", "CALLPOPA");
        putOpcode(0x59, "Set I0", "void", "POPI0", p("value", "unknown"));
        putOpcode(0x5A, "Set I1", "void", "POPI1", p("value", "unknown"));
        putOpcode(0x5B, "Set I2", "void", "POPI2", p("value", "unknown"));
        putOpcode(0x5C, "Set I3", "void", "POPI3", p("value", "unknown"));
        putOpcode(0x5D, "Set F0", "void", "POPF0", p("value", "float"));
        putOpcode(0x5E, "Set F1", "void", "POPF1", p("value", "float"));
        putOpcode(0x5F, "Set F2", "void", "POPF2", p("value", "float"));
        putOpcode(0x60, "Set F3", "void", "POPF3", p("value", "float"));
        putOpcode(0x61, "Set F4", "void", "POPF4", p("value", "float"));
        putOpcode(0x62, "Set F5", "void", "POPF5", p("value", "float"));
        putOpcode(0x63, "Set F6", "void", "POPF6", p("value", "float"));
        putOpcode(0x64, "Set F7", "void", "POPF7", p("value", "float"));
        putOpcode(0x65, "Set F8", "void", "POPF8", p("value", "float"));
        putOpcode(0x66, "Set F9", "void", "POPF9", p("value", "float"));
        putOpcode(0x67, "Get I0", "unknown", "PUSHI0");
        putOpcode(0x68, "Get I1", "unknown", "PUSHI1");
        putOpcode(0x69, "Get I2", "unknown", "PUSHI2");
        putOpcode(0x6A, "Get I3", "unknown", "PUSHI3");
        putOpcode(0x6B, "Get F0", "float", "PUSHF0");
        putOpcode(0x6C, "Get F1", "float", "PUSHF1");
        putOpcode(0x6D, "Get F2", "float", "PUSHF2");
        putOpcode(0x6E, "Get F3", "float", "PUSHF3");
        putOpcode(0x6F, "Get F4", "float", "PUSHF4");
        putOpcode(0x70, "Get F5", "float", "PUSHF5");
        putOpcode(0x71, "Get F6", "float", "PUSHF6");
        putOpcode(0x72, "Get F7", "float", "PUSHF7");
        putOpcode(0x73, "Get F8", "float", "PUSHF8");
        putOpcode(0x74, "Get F9", "float", "PUSHF9");
        putOpcode(0x75, "PUSHAINTER");
        putOpcode(0xF6, "System", "void", "SYSTEM");
        putOpcode(0x77, "await script", "void", "REQWAIT", p("w", "worker"), p("f", "int"));
        putOpcode(0x78, "await party member script", "void", "PREQWAIT", p("index", "int"), p("f", "int"));
        putOpcode(0x79, "Change interaction script", "void", "REQCHG", p("tableHolder", "pointer"), p("old", "fieldInteraction"), p("new", "int"));
        putOpcode(0x7A, "ACTREQ");
        OPCODES[0xB5].isCall = true;
        OPCODES[0xD8].isCall = true;
        OPCODES[0x34].continues = false;
        OPCODES[0x3C].continues = false;
        OPCODES[0x40].continues = false;
        OPCODES[0x54].continues = false;
        OPCODES[0xB0].continues = false;
        OPCODES[0xB0].branches = true;
        OPCODES[0xB1].branches = true;
        OPCODES[0xB2].branches = true;
        OPCODES[0xD5].branches = true;
        OPCODES[0xD6].branches = true;
        OPCODES[0xD7].branches = true;
    }

    public static void putOpcode(int hex, String name, String type, String internalName, ScriptField... inputs) {
        OPCODES[hex] = new ScriptOpcode(hex, name, type, internalName, inputs);
    }

    public static void putOpcode(int hex, String name, String type, String internalName, String format, ScriptField... inputs) {
        OPCODES[hex] = new ScriptOpcode(hex, name, type, internalName, format, inputs);
    }

    public static void putOpcode(int hex, String internalName) {
        OPCODES[hex] = new ScriptOpcode(hex, null, "void", internalName);
    }

    private static ScriptField p(String name, String type) {
        return new ScriptField(name, type);
    }

    public List<ScriptField> inputs;
    public String format;
    public boolean hasArgs = false;
    public boolean continues = true;
    public boolean branches = false;
    public boolean isCall = false;
    public boolean isLineEnd = false;
    public boolean isSignal = false;

    public ScriptOpcode(String name, String type, String internalName) {
        super(name, type, internalName);
    }

    public ScriptOpcode(int hex, String name, String type, String internalName, ScriptField... inputs) {
        super(name, type, internalName);
        this.idx = hex;
        this.hasArgs = (hex & 0x80) != 0;
        this.inputs = List.of(inputs);
        this.isLineEnd = "void".equals(type) && hex != 0;
    }

    public ScriptOpcode(int hex, String name, String type, String internalName, String format, ScriptField... inputs) {
        super(name, type, internalName);
        this.idx = hex;
        this.hasArgs = (hex & 0x80) != 0;
        this.format = format;
        this.inputs = List.of(inputs);
        this.isLineEnd = "void".equals(type) && hex != 0;
    }

    public String getType(List<StackObject> params) {
        return type;
    }

    @Override
    public String getHexIndex() {
        return idx != null ? StringHelper.formatHex2(idx) : null;
    }

    public StackObject getTypedParam(int index, List<StackObject> params, List<StackObject> earlierParams) {
        StackObject obj = params.get(index);
        String paramType = inputs.get(index).type;
        boolean doNotRetype = obj == null || obj.expression || "unknown".equals(paramType) || ("int".equals(paramType) && (obj.type.startsWith("int") || obj.type.startsWith("uint")));
        if (doNotRetype) {
            return obj;
        }
        return new StackObject(paramType, obj);
    }

    public static MenuButton getOpcodeChoices(OpcodeChoiceAction action, String type) {
        if (type == null || type.equals("void")) {
            MenuButton voidRoot = new MenuButton();
            ObservableList<MenuItem> voidRootChoices = voidRoot.getItems();
            MenuItem nop = opcodeChoice(0x00, "No operation", action);
            voidRootChoices.add(nop);
            voidRoot.setText(nop.getText());
            voidRoot.setUserData(nop.getUserData());
            voidRootChoices.add(opcodeChoice(0xD8, "Call", action));
            Menu assignmentsRoot = opcodeChoice("Assignment");
            voidRootChoices.add(assignmentsRoot);
            ObservableList<MenuItem> assigmentChoices = assignmentsRoot.getItems();
            assigmentChoices.add(opcodeChoice(0x25, "Ignore return value (Set rA)", action));
            assigmentChoices.add(opcodeChoice(0x2A, "test (Set rX)", action));
            assigmentChoices.add(opcodeChoice(0x2C, "switch (Set rY)", action));
            Menu endRoot = opcodeChoice("End");
            voidRootChoices.add(endRoot);
            ObservableList<MenuItem> endChoices = endRoot.getItems();
            endChoices.add(opcodeChoice(0x34, "Return from subroutine", action));
            endChoices.add(opcodeChoice(0x3C, "Return", action));
            endChoices.add(opcodeChoice(0x40, "Halt", action));
            endChoices.add(opcodeChoice(0x54, "Direct Return", action));
            // endChoices.add(opcodeChoice(0x3D, "RETN", action));
            // endChoices.add(opcodeChoice(0x3E, "RETT", action));
            endChoices.add(opcodeChoice(0x3F, "RETTN", action));
            Menu setTempIRoot = opcodeChoice("Set temp integer");
            assigmentChoices.add(setTempIRoot);
            ObservableList<MenuItem> tempIChildren = setTempIRoot.getItems();
            tempIChildren.add(opcodeChoice(0x59, "Set I0", action));
            tempIChildren.add(opcodeChoice(0x5A, "Set I1", action));
            tempIChildren.add(opcodeChoice(0x5B, "Set I2", action));
            tempIChildren.add(opcodeChoice(0x5C, "Set I3", action));
            Menu setTempFRoot = opcodeChoice("Set temp float");
            assigmentChoices.add(setTempFRoot);
            ObservableList<MenuItem> tempFChildren = setTempFRoot.getItems();
            tempFChildren.add(opcodeChoice(0x5D, "Set F0", action));
            tempFChildren.add(opcodeChoice(0x5E, "Set F1", action));
            tempFChildren.add(opcodeChoice(0x5F, "Set F2", action));
            tempFChildren.add(opcodeChoice(0x60, "Set F3", action));
            tempFChildren.add(opcodeChoice(0x61, "Set F4", action));
            tempFChildren.add(opcodeChoice(0x62, "Set F5", action));
            tempFChildren.add(opcodeChoice(0x63, "Set F6", action));
            tempFChildren.add(opcodeChoice(0x64, "Set F7", action));
            tempFChildren.add(opcodeChoice(0x65, "Set F8", action));
            tempFChildren.add(opcodeChoice(0x66, "Set F9", action));
            Menu scriptsRoot = opcodeChoice("Worker scripts");
            voidRootChoices.add(scriptsRoot);
            ObservableList<MenuItem> scriptsChoices = scriptsRoot.getItems();
            scriptsChoices.add(opcodeChoice(0x77, "Await script", action));
            scriptsChoices.add(opcodeChoice(0x79, "Change interaction handler", action));
            assigmentChoices.add(opcodeChoice(0xA0, "Set variable", action));
            assigmentChoices.add(opcodeChoice(0xA3, "Set array item", action));
            assigmentChoices.add(opcodeChoice(0xA1, "Set variable (type limited)", action));
            assigmentChoices.add(opcodeChoice(0xA4, "Set array item (type limited)", action));
            Menu branchRoot = opcodeChoice("Branching");
            voidRootChoices.add(branchRoot);
            ObservableList<MenuItem> branchChoices = branchRoot.getItems();
            branchChoices.add(opcodeChoice(0xB0, "Jump always", action));
            branchChoices.add(opcodeChoice(0xD6, "Jump if condition met", action));
            branchChoices.add(opcodeChoice(0xD7, "Jump if condition not met", action));
            branchChoices.add(opcodeChoice(0xB3, "Jump to subroutine (worker)", action));
            // branchChoices.add(opcodeChoice(0xB1, "Jump if last condition met", action));
            // branchChoices.add(opcodeChoice(0xB2, "Jump if last condition not met", action));
            voidRootChoices.add(opcodeChoice(0xF6, "System (Marker?)", action));
            return voidRoot;
        }

        Menu getValueRoot = opcodeChoice("Value");
        ObservableList<MenuItem> getValueChildren = getValueRoot.getItems();
        MenuItem directAE = opcodeChoice(0xAE, "Direct", action);
        getValueChildren.add(directAE);
        getValueChildren.add(opcodeChoice(0xB5, "Call", action));
        getValueChildren.add(opcodeChoice(0x9F, "Get variable", action));
        getValueChildren.add(opcodeChoice(0xA2, "Get array item", action));
        getValueChildren.add(opcodeChoice(0x29, "case (rY)", action));
        // getValueChildren.add(opcodeChoice(-2, "Convert from Integer", action));
        MenuItem cloneItem = opcodeChoice(0x2B, "Clone", action);
        cloneItem.setDisable(true);
        getValueChildren.add(cloneItem);

        if (type.equals("bool")) {
            MenuButton boolRoot = new MenuButton();
            ObservableList<MenuItem> boolRootChoices = boolRoot.getItems();
            boolRootChoices.add(getValueRoot);
            boolRoot.setText(directAE.getText());
            boolRoot.setUserData(directAE.getUserData());
            boolRootChoices.add(opcodeChoice(0x02, "AND", action));
            boolRootChoices.add(opcodeChoice(0x01, "OR", action));
            boolRootChoices.add(opcodeChoice(0x19, "NOT", action));
            Menu comparisonRoot = opcodeChoice("Comparison");
            boolRootChoices.add(comparisonRoot);
            ObservableList<MenuItem> comparisonChoices = comparisonRoot.getItems();
            comparisonChoices.add(opcodeChoice(0x06, "Equal", action));
            comparisonChoices.add(opcodeChoice(0x07, "Not equal", action));
            comparisonChoices.add(opcodeChoice(0x0A, "Greater than", action));
            comparisonChoices.add(opcodeChoice(0x0B, "Less than", action));
            comparisonChoices.add(opcodeChoice(0x0E, "Greater or equal", action));
            comparisonChoices.add(opcodeChoice(0x0F, "Less or equal", action));
            comparisonChoices.add(opcodeChoice(0x08, "Greater than (unsigned)", action));
            comparisonChoices.add(opcodeChoice(0x09, "Less than (unsigned)", action));
            comparisonChoices.add(opcodeChoice(0x0C, "Greater or equal (unsigned)", action));
            comparisonChoices.add(opcodeChoice(0x0D, "Less or equal (unsigned)", action));
            return boolRoot;
        }

        if (type.equals("float")) {
            MenuButton floatRoot = new MenuButton();
            ObservableList<MenuItem> floatRootChoices = floatRoot.getItems();
            Menu getFloatValueRoot = opcodeChoice("Value");
            floatRootChoices.add(getFloatValueRoot);
            ObservableList<MenuItem> getFloatValueChoices = getFloatValueRoot.getItems();
            MenuItem directAF = opcodeChoice(0xAF, "Direct", action);
            getFloatValueChoices.add(directAF);
            floatRoot.setText(directAF.getText());
            floatRoot.setUserData(directAF.getUserData());
            getFloatValueChoices.add(opcodeChoice(0xB5, "Call", action));
            getFloatValueChoices.add(opcodeChoice(0x9F, "Get variable", action));
            getFloatValueChoices.add(opcodeChoice(0xA2, "Get array item", action));

            Menu tempFRoot = opcodeChoice("Temp Variables");
            floatRootChoices.add(tempFRoot);
            ObservableList<MenuItem> tempFChoices = tempFRoot.getItems();
            tempFChoices.add(opcodeChoice(0x6B, "Temp float F0", action));
            tempFChoices.add(opcodeChoice(0x6C, "Temp float F1", action));
            tempFChoices.add(opcodeChoice(0x6D, "Temp float F2", action));
            tempFChoices.add(opcodeChoice(0x6E, "Temp float F3", action));
            tempFChoices.add(opcodeChoice(0x6F, "Temp float F4", action));
            tempFChoices.add(opcodeChoice(0x70, "Temp float F5", action));
            tempFChoices.add(opcodeChoice(0x71, "Temp float F6", action));
            tempFChoices.add(opcodeChoice(0x72, "Temp float F7", action));
            tempFChoices.add(opcodeChoice(0x73, "Temp float F8", action));
            tempFChoices.add(opcodeChoice(0x74, "Temp float F9", action));

            // floatRootChoices.add(opcodeChoice(-2, "Convert from Integer", action));
            return floatRoot;
        }

        if (type.equals("pointer")) {
            MenuButton pointerRoot = new MenuButton();
            ObservableList<MenuItem> pointerRootChoices = pointerRoot.getItems();
            pointerRootChoices.add(opcodeChoice(0xA7, "Get array pointer", action));
            MenuItem valueAE = opcodeChoice(0xAE, "Direct", action);
            pointerRootChoices.add(valueAE);
            pointerRoot.setText(valueAE.getText());
            pointerRoot.setUserData(valueAE.getUserData());
            pointerRootChoices.add(opcodeChoice(0xB5, "Call", action));
            return pointerRoot;
        }

        if (type.equals("exec")) {
            MenuButton runningRoot = new MenuButton();
            ObservableList<MenuItem> runningRootChoices = runningRoot.getItems();
            runningRootChoices.add(opcodeChoice(0x36, "Run wXX::fYY asynchronously", action));
            runningRootChoices.add(opcodeChoice(0x37, "Run wXX::fYY and await start", action));
            runningRootChoices.add(opcodeChoice(0x38, "Run wXX::fYY and await end", action));
            return runningRoot;
        }



        MenuButton intRoot = new MenuButton();
        ObservableList<MenuItem> intRootChoices = intRoot.getItems();
        Menu getIntValueRoot = opcodeChoice("Value");
        intRootChoices.add(getIntValueRoot);
        ObservableList<MenuItem> getIntValueChildren = getIntValueRoot.getItems();
        MenuItem valueAE = opcodeChoice(0xAE, "Direct", action);
        getIntValueChildren.add(valueAE);
        intRoot.setText(valueAE.getText());
        intRoot.setUserData(valueAE.getUserData());
        getIntValueChildren.add(opcodeChoice(0xB5, "Call", action));
        getIntValueChildren.add(opcodeChoice(0x9F, "Get variable", action));
        getIntValueChildren.add(opcodeChoice(0xA2, "Get array item", action));
        getIntValueChildren.add(opcodeChoice(0x26, "Last call result (rA)", action));
        getIntValueChildren.add(opcodeChoice(0x28, "test (rX)", action));
        getIntValueChildren.add(opcodeChoice(0x29, "case (rY)", action));
        getIntValueChildren.add(opcodeChoice(0xAF, "Float", action));
        Menu arithmeticRoot = opcodeChoice("Arithmetic");
        intRootChoices.add(arithmeticRoot);
        ObservableList<MenuItem> arithmeticChoices = arithmeticRoot.getItems();
        arithmeticChoices.add(opcodeChoice(0x14, "Add", action));
        arithmeticChoices.add(opcodeChoice(0x15, "Subtract", action));
        arithmeticChoices.add(opcodeChoice(0x16, "Multiply", action));
        arithmeticChoices.add(opcodeChoice(0x17, "Divide", action));
        arithmeticChoices.add(opcodeChoice(0x18, "Modulo", action));
        arithmeticChoices.add(opcodeChoice(0x1A, "Negate", action));
        Menu bitOpsRoot = opcodeChoice("Bitwise Operations");
        intRootChoices.add(bitOpsRoot);
        ObservableList<MenuItem> bitOpsChoices = bitOpsRoot.getItems();
        bitOpsChoices.add(opcodeChoice(0x05, "& bitwise AND", action));
        bitOpsChoices.add(opcodeChoice(0x03, "| bitwise OR", action));
        bitOpsChoices.add(opcodeChoice(0x04, "^ bitwise XOR", action));
        bitOpsChoices.add(opcodeChoice(0x1C, "~ bitwise NOT", action));
        bitOpsChoices.add(opcodeChoice(0x12, "<< Shift left by", action));
        bitOpsChoices.add(opcodeChoice(0x13, ">> Shift right by", action));
        bitOpsChoices.add(opcodeChoice(0x10, "Turn on bit", action));
        bitOpsChoices.add(opcodeChoice(0x11, "Turn off bit", action));

        Menu tempIRoot = opcodeChoice("Temp Variables");
        intRootChoices.add(tempIRoot);
        ObservableList<MenuItem> tempIChoices = tempIRoot.getItems();
        tempIChoices.add(opcodeChoice(0x67, "Temp integer I0", action));
        tempIChoices.add(opcodeChoice(0x68, "Temp integer I1", action));
        tempIChoices.add(opcodeChoice(0x69, "Temp integer I2", action));
        tempIChoices.add(opcodeChoice(0x6A, "Temp integer I3", action));
        MenuItem clone = opcodeChoice(0x2B, "Clone", action);
        clone.setDisable(true);
        intRootChoices.add(clone);
        // intRootChoices.add(opcodeChoice(-2, "Convert", action));
        return intRoot;
        /*
        OPCODE_LABELS[0x39] = "PREQ";
        OPCODE_LABELS[0x3A] = "PREQSW";
        OPCODE_LABELS[0x3B] = "PREQEW";
        OPCODE_LABELS[0x41] = "PUSHN";
        OPCODE_LABELS[0x42] = "PUSHT";
        OPCODE_LABELS[0x43] = "PUSHVP";
        OPCODE_LABELS[0x44] = "PUSHFIX";
        OPCODE_LABELS[0x45] = "FREQ";
        OPCODE_LABELS[0x46] = "TREQ";
        OPCODE_LABELS[0x47] = "BREQ";
        OPCODE_LABELS[0x48] = "BFREQ";
        OPCODE_LABELS[0x49] = "BTREQ";
        OPCODE_LABELS[0x4A] = "FREQSW";
        OPCODE_LABELS[0x4B] = "TREQSW";
        OPCODE_LABELS[0x4C] = "BREQSW";
        OPCODE_LABELS[0x4D] = "BFREQSW";
        OPCODE_LABELS[0x4E] = "BTREQSW";
        OPCODE_LABELS[0x4F] = "FREQEW";
        OPCODE_LABELS[0x50] = "TREQEW";
        OPCODE_LABELS[0x51] = "BREQEW";
        OPCODE_LABELS[0x52] = "BFREQEW";
        OPCODE_LABELS[0x53] = "BTREQEW";
        OPCODE_LABELS[0x75] = "PUSHAINTER";
        OPCODE_LABELS[0x78] = "PREQWAIT";
        OPCODE_LABELS[0x7A] = "ACTREQ";
         */
    }

    public static Menu opcodeChoice(String label) {
        OpcodeChoice opcodeChoice = new OpcodeChoice(-1, label);
        Menu menu = new Menu(label);
        menu.setUserData(opcodeChoice);
        return menu;
    }

    public static MenuItem opcodeChoice(int opcode, String label, OpcodeChoiceAction action) {
        OpcodeChoice opcodeChoice = new OpcodeChoice(opcode, label);
        MenuItem menuItem = new MenuItem(label);
        menuItem.setUserData(opcodeChoice);
        menuItem.setOnAction(actionEvent -> action.act(opcodeChoice, actionEvent));
        return menuItem;
    }

    public static record OpcodeChoice(int opcode, String label) {
        @Override
        public String toString() {
            return label;
        }
    }

    public interface OpcodeChoiceAction {
        void act(OpcodeChoice choice, ActionEvent event);
    }
}
