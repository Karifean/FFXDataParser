package script;

import script.model.*;

import java.util.*;
import java.util.stream.Collectors;

public class ScriptObject {
    private static final boolean WRITE_SCRIPT_PREFIX_BEFORE_JUMPS = false;

    protected int[] bytes;
    protected int[] actualScriptCodeBytes;
    protected int byteCursor = 0;
    protected int[] refFloats;
    protected int[] refInts;
    protected int scriptCodeLength;
    protected int scriptCodeStartAddress;
    protected int scriptCodeEndAddress;
    public StringBuilder hexAiString = new StringBuilder();
    public StringBuilder textAiString = new StringBuilder();
    Stack<StackObject> stack = new Stack<>();
    Map<Integer, String> lastTempTypes = new HashMap<>();
    Map<Integer, String> varTypes = new HashMap<>();
    Map<Integer, List<StackObject>> varEnums = new HashMap<>();
    Map<Integer, StackObject> constants = new HashMap<>();
    String lastCallType = "unknown";
    boolean gatheringInfo = true;
    List<Integer> jumpDestinations = new ArrayList<>();
    Map<Integer, List<String>> reverseJumpDestinations = new HashMap<>();

    public ScriptObject(int[] bytes) {
        this.bytes = bytes;
        prepare();
    }

    public void run() {
        byteCursor = 0;
        scriptCodeLength = read4Bytes();
        // System.out.println("Script Length: " + scriptCodeLength);
        int nullAddress2 = read4Bytes();
        int creatorTagAddress = read4Bytes();
        int numberPartAddress = read4Bytes();
        int jumpsEndAddress = read4Bytes();
        skipBytes(0x14);
        int jumpsStartAddress = read4Bytes();
        int weirdRandomFlagsAddress = read4Bytes();
        scriptCodeStartAddress = read4Bytes();
        int numberOfScripts = read2Bytes();
        int numberOfScripts2 = read2Bytes();
        // System.out.println("Number of Scripts: " + numberOfScripts + " / " + numberOfScripts2);
        int[] scriptHeaderOffsets = new int[numberOfScripts];
        for (int i = 0; i < numberOfScripts; i++) {
            scriptHeaderOffsets[i] = read4Bytes();
        }
        int scriptIndex = 0;
        List<ScriptHeader> headers = new ArrayList<>();
        for (int headerStart : scriptHeaderOffsets) {
            byteCursor = headerStart;
            ScriptHeader scriptHeader = parseScriptHeader();
            parseScriptJumps(scriptHeader, scriptIndex);
            scriptIndex++;
            headers.add(scriptHeader);
        }
        parseReferenceFloatsAndInts(headers);
        byteCursor = jumpsStartAddress;
        // parseJumps((jumpsEndAddress - jumpsStartAddress) / 4);
        scriptCodeEndAddress = scriptCodeStartAddress + scriptCodeLength;
        actualScriptCodeBytes = Arrays.copyOfRange(bytes, scriptCodeStartAddress, scriptCodeEndAddress);
        parseScriptCode();
        if (!stack.empty()) {
            System.err.println("Stack not empty: " + stack.size() + " els: " + stack.stream().map(StackObject::toString).collect(Collectors.joining("::")));
        }
        inferEnums();
        gatheringInfo = false;
        parseScriptCode();
    }

    private ScriptHeader parseScriptHeader() {
        ScriptHeader header = new ScriptHeader();
        header.count1 = read2Bytes();
        header.someRefsCount = read2Bytes();
        header.refIntCount = read2Bytes();
        header.refFloatCount = read2Bytes();
        header.entryPointCount = read2Bytes();
        header.maybeJumpCount = read2Bytes();
        header.alwaysZero1 = read4Bytes();
        header.alwaysZero2 = read4Bytes();
        header.someRefsOffset = read4Bytes();
        header.intTableOffset = read4Bytes();
        header.floatTableOffset = read4Bytes();
        header.scriptEntryPointsOffset = read4Bytes();
        header.jumpsOffset = read4Bytes();
        header.alwaysZero3 = read4Bytes();
        header.alwaysZero4 = read4Bytes();
        header.weirdoOffset = read4Bytes();
        return header;
    }

    private void parseReferenceFloatsAndInts(List<ScriptHeader> headers) {
        int lastRefIntTableOffset = -1;
        int lastRefFloatTableOffset = -1;
        for (ScriptHeader h : headers) {
            if (lastRefFloatTableOffset < 0) {
                lastRefFloatTableOffset = h.floatTableOffset;
                byteCursor = h.floatTableOffset;
                refFloats = new int[h.refFloatCount];
                for (int i = 0; i < h.refFloatCount; i++) {
                    refFloats[i] = read4Bytes();
                }
            } else if (h.floatTableOffset != lastRefFloatTableOffset) {
                System.err.println("WARNING, float table mismatch!");
            }
            if (lastRefIntTableOffset < 0) {
                lastRefIntTableOffset = h.intTableOffset;
                byteCursor = h.intTableOffset;
                refInts = new int[h.refIntCount];
                for (int i = 0; i < h.refIntCount; i++) {
                    refInts[i] = read4Bytes();
                }
            } else if (h.intTableOffset != lastRefIntTableOffset) {
                System.err.println("WARNING, int table mismatch!");
            }
        }
    }

    private void parseScriptJumps(ScriptHeader header, int scriptIndex) {
        String sPrefix = "s" + format2Or4Byte(scriptIndex);
        byteCursor = header.scriptEntryPointsOffset;
        int entryPointCount = header.entryPointCount;
        int[] entryPoints = new int[entryPointCount];
        for (int i = 0; i < entryPointCount; i++) {
            int entryPoint = read4Bytes();
            entryPoints[i] = entryPoint;
            jumpDestinations.add(entryPoint);
            String epSuffix = "e" + format2Or4Byte(i);
            if (!reverseJumpDestinations.containsKey(entryPoint)) {
                reverseJumpDestinations.put(entryPoint, new ArrayList<>());
            }
            reverseJumpDestinations.get(entryPoint).add(sPrefix + epSuffix);
        }
        byteCursor = header.jumpsOffset;
        int jumpCount = header.maybeJumpCount;
        int[] jumps = new int[jumpCount];
        for (int i = 0; i < jumpCount; i++) {
            int jump = read4Bytes();
            jumps[i] = jump;
            jumpDestinations.add(jump);
            String jSuffix = "j" + format2Or4Byte(i);
            if (!reverseJumpDestinations.containsKey(jump)) {
                reverseJumpDestinations.put(jump, new ArrayList<>());
            }
            reverseJumpDestinations.get(jump).add(WRITE_SCRIPT_PREFIX_BEFORE_JUMPS ? sPrefix + jSuffix : jSuffix);
        }
    }

    protected void parseScriptCode() {
        byteCursor = scriptCodeStartAddress;
        hexAiString = new StringBuilder();
        textAiString = new StringBuilder();
        int opcode;
        while (byteCursor < scriptCodeEndAddress) {
            opcode = nextAiByte();
            int arg1 = 0;
            int arg2 = 0;
            switch (getArgc(opcode)) {
                case 2:
                    arg1 = nextAiByte();
                    arg2 = nextAiByte();
                case 0:
                default:
                    break;
            }
            process(opcode, arg1, arg2);
            if (getLineEnd(opcode)) {
                if (!stack.empty()) {
                    hexAiString.append(" STACK NOT EMPTY: ").append(stack);
                    stack.clear();
                }
                hexAiString.append('\n');
            } else {
                hexAiString.append(' ');
            }
        }
    }

    protected int nextAiByte() {
        int scriptCodeByteCursor = byteCursor - scriptCodeStartAddress;
        if (reverseJumpDestinations.containsKey(scriptCodeByteCursor) && (hexAiString.isEmpty() || hexAiString.charAt(hexAiString.length() - 1) == '\n')) {
            List<String> jumps = reverseJumpDestinations.get(scriptCodeByteCursor);
            String jumpLabels = String.join(",", jumps) + ":\n";
            textAiString.append(jumpLabels);
            hexAiString.append(jumpLabels);
        }
        int rd = readByte();
        hexAiString.append(String.format("%02x", rd));
        return rd;
    }

    protected void process(int opcode, int arg1, int arg2) {
        int argv = arg1 + arg2 * 0x100;
        int argvSigned = argv < 0x8000 ? argv : (argv - 0x10000);
        String argvsd = ""+argvSigned;
        String argvsh = format2Or4Byte(argv);
        StackObject p1 = null, p2 = null, p3 = null, p4 = null, p5 = null, p6 = null, p7 = null, p8 = null;
        try {
            switch (getStackPops(opcode)) {
                case 8: p8 = stack.pop();
                case 7: p7 = stack.pop();
                case 6: p6 = stack.pop();
                case 5: p5 = stack.pop();
                case 4: p4 = stack.pop();
                case 3: p3 = stack.pop();
                case 2:
                    p2 = stack.pop();
                case 1:
                    p1 = stack.pop();
                case 0:
                default:
                    break;
            }
        } catch (EmptyStackException e) {
            hexAiString.append("\nEmpty stack at byteCursor ").append(byteCursor).append(" for opcode ").append(String.format("%02x", opcode)).append('\n');
            return;
        }
        if (opcode >= 0x01 && opcode <= 0x18) {
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
            String content = "(" + p1s + " " + op.name + " " + p2s + ")";
            stack.push(new StackObject(resultType, true, content, opcode));
        } else if (opcode == 0x19) {
            stack.push(new StackObject("bool", true, "not " + p1, 0x19));
        } else if (opcode == 0x1A) {
            stack.push(new StackObject("unknown", true, "OPUMINUS", 0x1A));
        } else if (opcode == 0x1C) {
            stack.push(new StackObject(p1.type, true, "bitNot " + p1, 0x1C));
        } else if (opcode == 0x25) {
            lastCallType = p1.type;
            textAiString.append("(call) ").append(p1).append(";\n");
        } else if (opcode == 0x26) {
            stack.push(new StackObject(lastCallType, true, "LastCallResult", 0x26));
        } else if (opcode == 0x28) {
            stack.push(new StackObject("unknown", true, "rX", 0x28));
        } else if (opcode == 0x29) {
            stack.push(new StackObject("unknown", true, "case", 0x29));
        } else if (opcode == 0x2A) {
            textAiString.append("Set rX = ").append(p1).append('\n');
        } else if (opcode == 0x2B) {
            stack.push(new StackObject(p1.type, p1));
            stack.push(new StackObject(p1.type, p1));
        } else if (opcode == 0x2C) {
            textAiString.append("switch ").append(p1).append('\n');
        } else if (opcode == 0x34) {
            textAiString.append("return from subroutine\n");
        } else if (opcode >= 0x36 && opcode <= 0x38) {
            String type = "queueScript";
            if (opcode == 0x37) {
                type += "Sync";
            } else if (opcode == 0x38) {
                type += "Async";
            }
            String sep = "s" + format2Or4Byte(p2.value) + "e" + format2Or4Byte(p3.value);
            String content = "(" + p1 + ", " + sep + ")";
            stack.push(new StackObject(type, true, type + content, opcode));
        } else if (opcode == 0x3C) {
            textAiString.append("return\n");
        } else if (opcode == 0x54) {
            textAiString.append("direct return?\n");
        } else if (opcode >= 0x59 && opcode <= 0x5C) {
            textAiString.append("tempI").append(opcode-0x59).append(" = ").append(p1).append('\n');
            lastTempTypes.put(opcode-0x59, p1.type);
        } else if (opcode >= 0x5D && opcode <= 0x66) {
            textAiString.append("tempF").append(opcode-0x5D).append(" = ").append(p1).append('\n');
        } else if (opcode >= 0x67 && opcode <= 0x6A) {
            stack.push(new StackObject(lastTempTypes.getOrDefault(opcode-0x67, "unknown"), true, "tempI"+(opcode-0x67), opcode));
        } else if (opcode >= 0x6B && opcode <= 0x74) {
            stack.push(new StackObject("float", true, "tempF"+(opcode-0x6B), opcode));
        } else if (opcode == 0x77) {
            //textAiString.append("await script(").append(p1).append(", ").append(p2).append(")\n");
            String sep = "s" + format2Or4Byte(p1.value) + "e" + format2Or4Byte(p2.value);
            textAiString.append("await ").append(sep).append(";\n");
        } else if (opcode == 0x79) {
            textAiString.append("REQCHG(").append(p1).append(", ").append(p2).append(", ").append(p3).append(")\n");
        } else if (opcode == 0x9F) {
            stack.push(new StackObject("var", true, "var"+argvsh, argv));
        } else if (opcode == 0xA0 || opcode == 0xA1) {
            addVarType(argv, resolveType(p1));
            if (gatheringInfo) {
                if (!varEnums.containsKey(argv)) {
                    varEnums.put(argv, new ArrayList<>());
                }
                varEnums.get(argv).add(p1);
            }
            textAiString.append("Set ");
            if (opcode == 0xA1) {
                textAiString.append("(limit) ");
            }
            textAiString.append("var").append(argvsh).append(" = ").append(typed(p1, varTypes.get(argv))).append('\n');
        } else if (opcode == 0xA2) {
            String arrayIndex = '[' + String.format("%04x", p1.value) + ']';
            stack.push(new StackObject("int", true, "var"+argvsh+arrayIndex, argv));
        } else if (opcode == 0xA3 || opcode == 0xA4) {
            String arrayIndex = '[' + String.format("%04x", p1.value) + ']';
            textAiString.append("Set ");
            if (opcode == 0xA4) {
                textAiString.append("(limit) ");
            }
            textAiString.append("var").append(argvsh);
            textAiString.append(arrayIndex).append(" = ").append(p2).append('\n');
        } else if (opcode == 0xA7) {
            String arrayIndex = '[' + String.format("%04x", p1.value) + ']';
            stack.push(new StackObject("int", true, "ArrayPointer:var"+argvsh+arrayIndex, argv));
        } else if (opcode == 0xAD) {
            int refInt = refInts[argv];
            String content = "rI[" + argvsh + "]:" + refInt + " [" + String.format("%08x", refInt).toUpperCase() + "h]";
            stack.push(new StackObject("int", false, content, refInt));
        } else if (opcode == 0xAE) {
            stack.push(new StackObject("int", false, argvsd + " [" + argvsh + "h]", argv));
        } else if (opcode == 0xAF) {
            int refFloat = refFloats[argv];
            String content = "rF[" + argvsh + "]:" + Float.intBitsToFloat(refFloat) + " [" + String.format("%08x", refFloat).toUpperCase() + "h]";
            stack.push(new StackObject("float", false, content, refFloat));
        } else if (opcode == 0xB5) {
            processB5(arg1, arg2);
        } else if (opcode == 0xD6) {
            textAiString.append(p1).append(" -> j").append(argvsh).append('\n');
        } else if (opcode == 0xD7) {
            textAiString.append("Check ").append(p1).append(" else jump to j").append(argvsh).append('\n');
        } else if (opcode == 0xD8) {
            processD8(arg1, arg2);
        } else if (opcode == 0xB0) {
            textAiString.append("Jump to j").append(argvsh).append('\n');
        } else if (opcode == 0xB3) {
            textAiString.append("Jump to subroutine s").append(argvsh).append('\n');
        } else if (opcode != 0x00 && opcode != 0xFF) {
            textAiString.append("Opcode:").append(String.format("%02x", opcode)).append('.').append(argvsh).append('\n');
        }
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
            hexAiString.append("\nEmpty stack at byteCursor ").append(byteCursor).append(" for func ").append(String.format("%04x", idx)).append('\n');
        }
        return params;
    }

    protected ScriptFunc getAndTypeFuncCall(int idx, List<StackObject> params) {
        ScriptFunc func = ScriptFuncLib.get(idx, params);
        if (func == null) {
            func = new ScriptFunc("Unknown:" + String.format("%04x", idx), "unknown", null, false);
        } else if (func.inputs != null) {
            for (int i = 0; i < func.inputs.size(); i++) {
                typed(params.get(i), func.inputs.get(i).type);
            }
        }
        return func;
    }

    protected void processB5(int opcode, int group) {
        int idx = opcode + group * 0x100;
        List<StackObject> params = popParamsForFunc(idx);
        ScriptFunc func = getAndTypeFuncCall(idx, params);
        stack.push(new StackObject(func.getType(params), true, func.callB5(params), idx));
    }

    protected void processD8(int opcode, int group) {
        int idx = opcode + group * 0x100;
        List<StackObject> params = popParamsForFunc(idx);
        ScriptFunc func = getAndTypeFuncCall(idx, params);
        lastCallType = func.getType(params);
        textAiString.append(func.callD8(params)).append(";\n");
    }

    protected void addVarType(int var, String type) {
        if (!gatheringInfo) {
            return;
        }
        if (!varTypes.containsKey(var)) {
            varTypes.put(var, type);
        } else {
            String prevType = varTypes.get(var);
            if (isWeakType(prevType)) {
                varTypes.put(var, type);
            }
        }
    }

    protected String resolveType(StackObject obj) {
        if (obj == null) {
            return "unknown";
        }
        if ("var".equals(obj.type)) {
            return varTypes.get(obj.value);
        }
        return obj.type;
    }

    protected static boolean isWeakType(String type) {
        return type == null || "int".equals(type) || "unknown".equals(type) || type.isBlank();
    }

    protected String typed(StackObject obj, String type) {
        if (obj == null) {
            return type + ":null";
        } else {
            if ("var".equals(obj.type)) {
                addVarType(obj.value, type);
            }
            if (obj.expression || "unknown".equals(type)) {
                return obj.toString();
            } else {
                return new StackObject(type, obj).toString();
            }
        }
    }

    protected static int getArgc(int opcode) {
        if (opcode == 0xff) {
            return 0;
        } else if (opcode >= 0x80) {
            return 2;
        } else {
            return 0;
        }
    }

    protected int getStackPops(int opcode) {
        int stackpops = ScriptConstants.OPCODE_STACKPOPS[opcode];
        if (stackpops < 0) {
            hexAiString.append("\nundefined stackpops for opcode ").append(String.format("%02x", opcode)).append('\n');
            return 0;
        }
        return stackpops;
    }

    protected int getFunctionParamCount(int idx) {
        ScriptFunc func = ScriptFuncLib.get(idx, null);
        if (func == null) {
            hexAiString.append("\nundefined stackpops for func ").append(String.format("%04x", idx)).append('\n');
            return 0;
        }
        return func.inputs != null ? func.inputs.size() : 0;
    }

    protected static boolean getLineEnd(int opcode) {
        return ScriptConstants.OPCODE_ENDLINE.contains(opcode);
    }

    protected void inferEnums() {
        for (Map.Entry<Integer, String> entry : varTypes.entrySet()) {
            Integer varIdx = entry.getKey();
            if (isWeakType(entry.getValue()) && varEnums.containsKey(varIdx)) {
                List<StackObject> enums = varEnums.get(varIdx);
                if (enums.size() == 1 && !enums.get(0).expression) {
                    constants.put(varIdx, enums.get(0));
                } else if (enums.stream().noneMatch(a -> a.expression)) {
                    Set<Integer> distinctContents = enums.stream().map(a -> a.value).collect(Collectors.toSet());
                    if (distinctContents.size() == 2 && distinctContents.contains(0) && (distinctContents.contains(1) || distinctContents.contains(255))) {
                        varTypes.put(varIdx, "bool");
                    }
                }
            }
        }
    }

    protected static void prepare() {
        ScriptConstants.initialize();
        ScriptFuncLib.initialize();
    }

    private int readByte() {
        int rd = bytes[byteCursor];
        byteCursor++;
        return rd;
    }

    private int read2Bytes() {
        int a = readByte();
        int b = readByte() * 0x100;
        return a + b;
    }

    private int read4Bytes() {
        int a = read2Bytes();
        int b = read2Bytes() * 0x10000;
        return a + b;
    }

    private static String format2Or4Byte(int b) {
        return String.format(b > 0x100 ? "%04x" : "%02x", b).toUpperCase();
    }

    private long read8Bytes() {
        long a = read4Bytes();
        long b = read4Bytes() * 0x100000000L;
        return a + b;
    }

    private void skipBytes(int amount) {
        byteCursor += amount;
    }

    private static class ScriptHeader {
        int count1;
        int someRefsCount;
        int refIntCount;
        int refFloatCount;
        int entryPointCount;
        int maybeJumpCount;
        int alwaysZero1;
        int alwaysZero2;
        int someRefsOffset;
        int intTableOffset;
        int floatTableOffset;
        int scriptEntryPointsOffset;
        int jumpsOffset;
        int alwaysZero3;
        int alwaysZero4;
        int weirdoOffset;
    }
}
