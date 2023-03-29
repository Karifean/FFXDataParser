import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class MonsterAiObject {
    private static int[] OPCODE_ARGC;
    private static int[] OPCODE_STACKPOPS;
    private static List<Integer> OPCODE_ENDLINE;
    private static Map<Integer, String> COMP_OPERATORS;
    File file;
    DataInputStream data;
    Stack<StackObject> stack = new Stack<>();
    StringBuilder hexAiString = new StringBuilder();
    StringBuilder textAiString = new StringBuilder();
    StringBuilder monsterText = new StringBuilder();
    Map<Integer, String> lastTempTypes = new HashMap<>();
    Map<Integer, String> varTypes = new HashMap<>();
    Map<Integer, List<StackObject>> varEnums = new HashMap<>();
    Map<Integer, StackObject> constants = new HashMap<>();
    boolean gatheringInfo = true;
    boolean writeJumps = true;
    int byteCursor = 0;
    int firstEarlierJump;
    int[] aiBytes;
    List<Integer> jumpDestinations = new ArrayList<>();
    Map<Integer, List<Integer>> reverseJumpDestinations = new HashMap<>();

    public MonsterAiObject(File file) {
        this.file = file;
        prepare();
    }

    private void newDataStream() throws FileNotFoundException {
        data = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
    }

    public void run() throws IOException {
        newDataStream();
        int absoluteBeginAddress = read4Bytes();
        int addressListLength = read4Bytes();
        data.mark(addressListLength);
        int unknownChunkAddress = read4Bytes();
        int valuesBeginAddress = read4Bytes();
        int nullAddress1 = read4Bytes();
        int dropsBeginAddress = read4Bytes();
        int dropsEndAddress = read4Bytes();
        int textPartAddress = read4Bytes();
        int totalFileBytes = read4Bytes();
        data.reset();
        data.skipNBytes(addressListLength - 8);
        data.mark(52);
        int scriptLength = read4Bytes();
        int nullAddress2 = read4Bytes();
        int creatorTagAddress = read4Bytes();
        int numberPartAddress = read4Bytes();
        int jumpsEndAddress = read4Bytes();
        data.skipBytes(20);
        int jumpsStartAddress = read4Bytes();
        data.skipBytes(4);
        int scriptStartAddress = read4Bytes();
        data.reset();
        data.mark(scriptStartAddress + scriptLength);
        data.skipBytes(scriptStartAddress);
        aiBytes = new int[scriptLength];
        for (int i = 0; i < scriptLength; i++) {
            aiBytes[i] = data.read();
        }
        data.reset();
        data.mark(jumpsEndAddress);
        data.skipNBytes(jumpsStartAddress);
        parseJumps((jumpsEndAddress - jumpsStartAddress) / 4);
        parseAiLinear(scriptLength);
        if (!stack.empty()) {
            System.err.println("Stack not empty: " + stack.size() + " els: " + stack.stream().map(StackObject::toString).collect(Collectors.joining("::")));
        }
        inferEnums();
        gatheringInfo = false;
        parseAiLinear(scriptLength);
        /* newDataStream();
        data.skipNBytes(textPartAddress + 0x80);
        try {
            readRemainingText();
        } catch (IOException ignored) {} */
    }

    private void parseJumps(int amount) throws IOException {
        firstEarlierJump = -1;
        int lastJump = -1;
        int thisJump;
        for (int i = 0; i < amount; i++) {
            thisJump = read4Bytes();
            if (firstEarlierJump == -1 && thisJump < lastJump) {
                firstEarlierJump = jumpDestinations.size();
            }
            jumpDestinations.add(thisJump);
            lastJump = thisJump;
        }
        for (int i = 0; i < jumpDestinations.size(); i++) {
            int destination = jumpDestinations.get(i);
            if (!reverseJumpDestinations.containsKey(destination)) {
                reverseJumpDestinations.put(destination, new ArrayList<>());
            }
            reverseJumpDestinations.get(destination).add(i);
        }
    }

    private void parseAiLinear(int scriptLength) {
        byteCursor = 0;
        hexAiString = new StringBuilder();
        textAiString = new StringBuilder();
        int opcode = 0;
        while (opcode != 0xFF && byteCursor < scriptLength) {
            opcode = nextAiByte();
            int arg1 = 0;
            int arg2 = 0;
            int arg3 = 0;
            switch (getArgc(opcode)) {
                case 3:
                    arg1 = nextAiByte();
                    arg2 = nextAiByte();
                    arg3 = nextAiByte();
                    break;
                case 2:
                    arg1 = nextAiByte();
                    arg2 = nextAiByte();
                    break;
                case 1:
                    arg1 = nextAiByte();
                    break;
                case 0:
                default:
                    break;
            }
            process(opcode, arg1, arg2, arg3);
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

    private int nextAiByte() {
        if (reverseJumpDestinations.containsKey(byteCursor) && (hexAiString.isEmpty() || hexAiString.charAt(hexAiString.length() - 1) == '\n')) {
            String jumpLabels = reverseJumpDestinations.get(byteCursor).stream().map(j -> getJLabel(j)).collect(Collectors.joining(",")) + ":\n";
            textAiString.append(jumpLabels);
            hexAiString.append(jumpLabels);
        }
        int rd = aiBytes[byteCursor];
        byteCursor++;
        hexAiString.append(String.format("%02x", rd));
        return rd;
    }

    private void process(int opcode, int arg1, int arg2, int arg3) {
        int argv = arg1 + arg2 * 256 + arg3 * 65536;
        int argvSigned = argv < 0x8000 ? argv : (argv - 0x10000);
        String argvsd = ""+argvSigned;
        String argvsh = String.format(arg3 > 0 ? "%06x" : (arg2 > 0 ? "%04x" : "%02x"), argv).toUpperCase();
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
            String op = COMP_OPERATORS.get(opcode);
            String type = opcode <= 0x0F ? "bool" : (p1.type.equals(p2.type) ? p1.type : (p1.type+"/"+p2.type));
            String asType = p1.type;
            if ("var".equals(asType)) {
                asType = varTypes.containsKey(p1.value) ? varTypes.get(p1.value) : p2.type;
            }
            String content = "(" + p1 + " " + op + " " + typed(p2, asType) + ")";
            stack.push(new StackObject(type, true, content, opcode));
        } else if (opcode == 0x19) {
            stack.push(new StackObject("bool", true, "not " + p1, 0x19));
        } else if (opcode == 0x25) {
            textAiString.append("Opcode:25(").append(p1).append(", ").append(p2).append(")\n");
        } else if (opcode == 0x26) {
            stack.push(new StackObject("unknown", true, "opcode:26", 0x26));
        } else if (opcode == 0x29) {
            stack.push(new StackObject("case", true, "case", 0x29));
        } else if (opcode == 0x2A) {
            textAiString.append("pop and ?ignore ").append(p1).append('\n');
        } else if (opcode == 0x2B) {
            stack.push(new StackObject(p1.type, p1));
            stack.push(new StackObject(p1.type, p1));
        } else if (opcode == 0x2C) {
            textAiString.append("switch ").append(p1).append(' ');
        } else if (opcode == 0x34) {
            textAiString.append("hard cut?\n");
        } else if (opcode == 0x3C) {
            textAiString.append("return\n");
        } else if (opcode >= 0x36 && opcode <= 0x38) {
            String prefix = typed(p1, "fieldActor") + "?.";
            String suffix = "[" + p2.value + "]";
            if (opcode == 0x37) {
                String content = prefix + "animation?" + suffix;
                stack.push(new StackObject("fieldAnimation", true, content, opcode));
            } else {
                String content = prefix + "opcode:" + String.format("%02x", opcode) + suffix;
                stack.push(new StackObject("unknown", true, content, opcode));
            }
        } else if (opcode >= 0x59 && opcode <= 0x60) {
            textAiString.append("temp").append(opcode-0x59).append(" = ").append(p1).append('\n');
            lastTempTypes.put(opcode-0x59, p1.type);
        } else if (opcode >= 0x67 && opcode <= 0x6E) {
            stack.push(new StackObject(lastTempTypes.getOrDefault(opcode-0x67, "unknown"), true, "temp"+(opcode-0x67), opcode));
        } else if (opcode == 0x77) {
            textAiString.append("Opcode:77(").append(p1).append(',').append(p2).append(")\n");
        } else if (opcode == 0x79) {
            textAiString.append("Opcode:79(").append(p1).append(',').append(p2).append(',').append(p3).append(")\n");
        } else if (opcode == 0x9F) {
            boolean solo = !gatheringInfo && varEnums.containsKey(argv) && varEnums.get(argv).size() == 1;
            String vrAppend = argvsh + (solo ? "[" + varEnums.get(argv).get(0) + "]" : "");
            if (constants.containsKey(argv)) {
                stack.push(new StackObject(constants.get(argv).type, true, "const"+vrAppend, argv));
            } else {
                stack.push(new StackObject("var", true, "var"+vrAppend, argv));
            }
        } else if (opcode == 0xA0) {
            addVarType(argv, "var".equals(p1.type) ? varTypes.get(p1.value) : p1.type);
            if (gatheringInfo) {
                if (!varEnums.containsKey(argv)) {
                    varEnums.put(argv, new ArrayList<>());
                }
                varEnums.get(argv).add(p1);
            }
            textAiString.append("Set ").append(constants.containsKey(argv) ? "const" : "var").append(argvsh).append(" = ").append(typed(p1, varTypes.get(argv))).append('\n');
        } else if (opcode == 0xA2) {
            String globalIdx = argvsh + String.format("%04x", p1.value);
            stack.push(new StackObject("global", true, "global:"+globalIdx, argv));
        } else if (opcode == 0xA3) {
            String globalIdx = argvsh + String.format("%04x", p1.value);
            textAiString.append("Set global:").append(globalIdx).append(" = ").append(p2).append('\n');
        } else if (opcode == 0xA7) {
            String globalIdx = argvsh + String.format("%04x", p1.value);
            stack.push(new StackObject("a7", true, "a7:"+globalIdx, argv));
        } else if (opcode == 0xAD || opcode == 0xAE || opcode == 0xAF) {
            stack.push(new StackObject(String.format("%02x", opcode), false, argvsd + " [" + argvsh + "h]", argv));
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
        // } else if (opcode == 0xB3) {
            // stack.push(new StackObject("B3", true, "opcode:b3(" + argvsh + ")", argv));
        } else if (opcode != 0x00 && opcode != 0xFF) {
            textAiString.append("Opcode:").append(String.format("%02x", opcode)).append('.').append(argvsh).append('\n');
        }
    }

    private void processB5(int opcode, int group) {
        List<StackObject> params = new ArrayList<>();
        try {
            switch (getFunctionParamCount(opcode, group)) {
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
            hexAiString.append("\nEmpty stack at byteCursor ").append(byteCursor).append(" for B5 opcode ").append(String.format("%02x", opcode)).append(" group ").append(String.format("%02x", group)).append('\n');
            return;
        }
        int idx = opcode + group * 256;
        ScriptFunc func = ScriptFuncLib.get(idx, params);
        if (func == null) {
            func = new ScriptFunc("Unknown:" + String.format("%04x", idx), "unknown", false);
        } else if (func.inputs != null) {
            for (int i = 0; i < func.inputs.size(); i++) {
                typed(params.get(i), func.inputs.get(i).type);
            }
        }
        stack.push(new StackObject(func.getType(params), true, func.callB5(params), idx));
    }

    private void processD8(int opcode, int group) {
        List<StackObject> params = new ArrayList<>();
        try {
            switch (getFunctionParamCount(opcode, group)) {
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
            hexAiString.append("\nEmpty stack at byteCursor ").append(byteCursor).append(" for D8 opcode ").append(String.format("%02x", opcode)).append(" group ").append(String.format("%02x", group)).append('\n');
            return;
        }
        int idx = opcode + group * 256;
        ScriptFunc func = ScriptFuncLib.get(idx, params);
        if (func == null) {
            func = new ScriptFunc("Unknown:" + String.format("%04x", idx), "unknown", false);
        } else if (func.inputs != null) {
            for (int i = 0; i < func.inputs.size(); i++) {
                typed(params.get(i), func.inputs.get(i).type);
            }
        }
        textAiString.append(func.callD8(params)).append(";\n");
    }

    private void addVarType(int var, String type) {
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

    private static boolean isWeakType(String type) {
        return type == null || "ae".equals(type) || "af".equals(type);
    }

    private int[] skipUntil(int[] untilA, int[] untilB) throws IOException {
        int count = 0;
        int rd;
        int finding = 0;
        while (data.available() > 0) {
            rd = data.read();
            if (finding == 1) {
                if (rd == untilA[count]) {
                    count++;
                    if (count >= untilA.length) {
                        return untilA;
                    }
                } else {
                    finding = 0;
                    count = 0;
                }
            }
            if (finding == 2) {
                if (rd == untilB[count]) {
                    count++;
                    if (count >= untilB.length) {
                        return untilB;
                    }
                } else {
                    finding = 0;
                    count = 0;
                }
            }
            if (finding == 0) {
                if (untilA != null && rd == untilA[count]) {
                    count = 1;
                    finding = 1;
                } else if (untilB != null && rd == untilB[count]) {
                    count = 1;
                    finding = 2;
                }
            }
        }
        throw new IOException();
    }

    private String typed(StackObject obj, String type) {
        if (obj == null) {
            return type + ":null";
        } else {
            if ("var".equals(obj.type)) {
                addVarType(obj.value, type);
            }
            if (obj.expression) {
                return obj.toString();
            } else {
                return new StackObject(type, obj).toString();
            }
        }
    }

    private String getJLabel(int j) {
        if (j >= firstEarlierJump) {
            return "j" + String.format("%02x", j - firstEarlierJump).toUpperCase();
        } else {
            return "p" + String.format("%02x", j).toUpperCase();
        }
        /* if (j > 10) {
        } else if (j == 0) {
            return "Begin0";
        } else if (j == 1) {
            return "Begin1";
        } else if (j == 2) {
            return "Begin2";
        } else if (j == 3) {
            return "Begin3";
        } else if (j == 4) {
            return "Begin4";
        } else if (j == 5) {
            return "BeforeTurn";
        } else if (j == 6) {
            return "Turn";
        } else if (j == 7) {
            return "TurnMid";
        } else if (j == 8) {
            return "PreCounter";
        } else if (j == 9) {
            return "Counter";
        } else if (j == 10) {
            return "Death";
        }
        return "Unknown" + j; */
    }

    private static int getArgc(int opcode) {
        if (opcode == 0xff) {
            return 0;
        } else if (opcode >= 0x80) {
            return 2;
        } else {
            return 0;
        }
    }

    private int getStackPops(int opcode) {
        int stackpops = OPCODE_STACKPOPS[opcode];
        if (stackpops < 0) {
            hexAiString.append("\nundefined stackpops for opcode ").append(String.format("%02x", opcode)).append('\n');
            return 0;
        }
        return stackpops;
    }

    private int getFunctionParamCount(int opcode, int group) {
        ScriptFunc func = ScriptFuncLib.get(opcode + group * 256, null);
        if (func == null) {
            hexAiString.append("\nundefined stackpops for func ").append(String.format("%02x", opcode)).append(" ").append(String.format("%02x", group)).append('\n');
            return 0;
        }
        return func.inputs != null ? func.inputs.size() : 0;
    }

    private static boolean getLineEnd(int opcode) {
        return OPCODE_ENDLINE.contains(opcode);
    }

    private void inferEnums() {
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

    private static void prepare() {
        ScriptConstants.initialize();
        ScriptFuncLib.initialize();
        if (OPCODE_ARGC == null) {
            OPCODE_ARGC = new int[0x100];
        }
        if (OPCODE_ENDLINE == null) {
            OPCODE_ENDLINE = new ArrayList<>();
            OPCODE_ENDLINE.add(0x25);
            OPCODE_ENDLINE.add(0x2A);
            OPCODE_ENDLINE.add(0x34);
            OPCODE_ENDLINE.add(0x3C);
            OPCODE_ENDLINE.add(0x3D);
            OPCODE_ENDLINE.add(0x54);
            OPCODE_ENDLINE.add(0x59);
            OPCODE_ENDLINE.add(0x5A);
            OPCODE_ENDLINE.add(0x5B);
            OPCODE_ENDLINE.add(0x5C);
            OPCODE_ENDLINE.add(0x5D);
            OPCODE_ENDLINE.add(0x5E);
            OPCODE_ENDLINE.add(0x5F);
            OPCODE_ENDLINE.add(0x60);
            OPCODE_ENDLINE.add(0x77);
            OPCODE_ENDLINE.add(0x79);
            OPCODE_ENDLINE.add(0xA0);
            OPCODE_ENDLINE.add(0xA3);
            OPCODE_ENDLINE.add(0xB0);
            OPCODE_ENDLINE.add(0xB3);
            OPCODE_ENDLINE.add(0xD6);
            OPCODE_ENDLINE.add(0xD7);
            OPCODE_ENDLINE.add(0xD8);
        }
        if (OPCODE_STACKPOPS == null) {
            OPCODE_STACKPOPS = new int[0x100];
            Arrays.fill(OPCODE_STACKPOPS, -1);
            OPCODE_STACKPOPS[0x00] = 0;
            for (int i = 0x01; i <= 0x18; i++) {
                OPCODE_STACKPOPS[i] = 2;
            }
            OPCODE_STACKPOPS[0x19] = 1;
            OPCODE_STACKPOPS[0x25] = 2;
            OPCODE_STACKPOPS[0x26] = 0;
            OPCODE_STACKPOPS[0x29] = 0;
            OPCODE_STACKPOPS[0x2A] = 1; // Seems to be "pop and ignore"?
            OPCODE_STACKPOPS[0x2B] = 1; // Seems to be "duplicate"
            OPCODE_STACKPOPS[0x2C] = 1;
            OPCODE_STACKPOPS[0x34] = 0;
            OPCODE_STACKPOPS[0x36] = 2; // Almost CERTAINLY wrong, used together with 0x25
            OPCODE_STACKPOPS[0x37] = 2; // Almost CERTAINLY wrong, used together with 0x25
            OPCODE_STACKPOPS[0x38] = 2; // Almost CERTAINLY wrong, used together with 0x25
            OPCODE_STACKPOPS[0x3C] = 0;
            OPCODE_STACKPOPS[0x3D] = 1;
            OPCODE_STACKPOPS[0x54] = 0;
            OPCODE_STACKPOPS[0x59] = 1;
            OPCODE_STACKPOPS[0x5A] = 1;
            OPCODE_STACKPOPS[0x5B] = 1;
            OPCODE_STACKPOPS[0x5C] = 1;
            OPCODE_STACKPOPS[0x5D] = 1;
            OPCODE_STACKPOPS[0x5E] = 1;
            OPCODE_STACKPOPS[0x5F] = 1;
            OPCODE_STACKPOPS[0x60] = 1;
            OPCODE_STACKPOPS[0x67] = 0;
            OPCODE_STACKPOPS[0x68] = 0;
            OPCODE_STACKPOPS[0x69] = 0;
            OPCODE_STACKPOPS[0x6A] = 0;
            OPCODE_STACKPOPS[0x6B] = 0;
            OPCODE_STACKPOPS[0x6C] = 0;
            OPCODE_STACKPOPS[0x6D] = 0;
            OPCODE_STACKPOPS[0x6E] = 0;
            OPCODE_STACKPOPS[0x77] = 2;
            OPCODE_STACKPOPS[0x79] = 3;
            OPCODE_STACKPOPS[0x9F] = 0;
            OPCODE_STACKPOPS[0xA0] = 1;
            OPCODE_STACKPOPS[0xA2] = 1; // Unknown if accurate, could work differently.
            OPCODE_STACKPOPS[0xA3] = 2; // This doesn't seem right, it uses 2 (sometimes?)
            OPCODE_STACKPOPS[0xA7] = 1;
            OPCODE_STACKPOPS[0xAD] = 0;
            OPCODE_STACKPOPS[0xAE] = 0;
            OPCODE_STACKPOPS[0xAF] = 0;
            OPCODE_STACKPOPS[0xB0] = 0;
            OPCODE_STACKPOPS[0xB3] = 0;
            OPCODE_STACKPOPS[0xB5] = 0;
            OPCODE_STACKPOPS[0xD6] = 1;
            OPCODE_STACKPOPS[0xD7] = 1;
            OPCODE_STACKPOPS[0xD8] = 0;
            OPCODE_STACKPOPS[0xF6] = 0;
        }
        if (COMP_OPERATORS == null) {
            COMP_OPERATORS = new HashMap<>();
            COMP_OPERATORS.put(0x01, "or");
            COMP_OPERATORS.put(0x02, "and");
            COMP_OPERATORS.put(0x03, "bitOr");
            COMP_OPERATORS.put(0x04, "?04?");
            COMP_OPERATORS.put(0x05, "bitAnd");
            COMP_OPERATORS.put(0x06, "==");
            COMP_OPERATORS.put(0x07, "!=");
            COMP_OPERATORS.put(0x08, "?08?");
            COMP_OPERATORS.put(0x09, "?09?");
            COMP_OPERATORS.put(0x0A, ">");
            COMP_OPERATORS.put(0x0B, "<");
            COMP_OPERATORS.put(0x0C, "?0C?");
            COMP_OPERATORS.put(0x0D, "?0D?");
            COMP_OPERATORS.put(0x0E, ">=");
            COMP_OPERATORS.put(0x0F, "<=");
            COMP_OPERATORS.put(0x12, "?12?");
            COMP_OPERATORS.put(0x14, "+");
            COMP_OPERATORS.put(0x15, "-");
            COMP_OPERATORS.put(0x16, "*");
            COMP_OPERATORS.put(0x17, "/");
            COMP_OPERATORS.put(0x18, "mod");
        }
    }

    private void readRemainingText() throws IOException {
        while (data.available() > 0) {
            int character = data.read();
            if (Main.BIN_LOOKUP.containsKey(character)) {
                monsterText.append(Main.BIN_LOOKUP.get(character));
            }
        }
    }

    private int read2Bytes() throws IOException {
        int val = data.read();
        val += data.read() * 0x100;
        return val;
    }

    private int read4Bytes() throws IOException {
        int val = data.read();
        val += data.read() * 0x100;
        val += data.read() * 0x10000;
        val += data.read() * 0x1000000;
        return val;
    }
}
