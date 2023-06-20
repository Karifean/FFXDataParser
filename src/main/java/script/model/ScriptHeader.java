package script.model;

import script.ScriptObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ScriptHeader {
    public static final int LENGTH = 0x34;

    public final int scriptIndex;
    public final int scriptType;
    public final int variablesCount;
    public final int refIntCount;
    public final int refFloatCount;
    public final int entryPointCount;
    public final int jumpCount;
    public final int alwaysZero1;
    public final int privateDataLength;
    public final int variableStructsTableOffset;
    public final int intTableOffset;
    public final int floatTableOffset;
    public final int scriptEntryPointsOffset;
    public final int jumpsOffset;
    public final int alwaysZero2;
    public final int privateDataOffset;
    public final int sharedDataOffset;

    public ScriptJump[] entryPoints;
    public ScriptJump[] jumps;
    public ScriptVariable[] variableDeclarations;
    public int[] refFloats;
    public int[] refInts;
    public List<ScriptVariable> privateVars;
    public List<ScriptVariable> sharedVars;

    private Integer purpose;
    private int[] purposeBytes;

    public ScriptHeader(int scriptIndex, int[] bytes) {
        this.scriptIndex = scriptIndex;
        scriptType = read2Bytes(bytes,0x00);
        variablesCount = read2Bytes(bytes,0x02);
        refIntCount = read2Bytes(bytes,0x04);
        refFloatCount = read2Bytes(bytes, 0x06);
        entryPointCount = read2Bytes(bytes, 0x08);
        jumpCount = read2Bytes(bytes, 0x0A);
        alwaysZero1 = read4Bytes(bytes, 0x0C);
        privateDataLength = read4Bytes(bytes, 0x10);
        variableStructsTableOffset = read4Bytes(bytes, 0x14);
        intTableOffset = read4Bytes(bytes, 0x18);
        floatTableOffset = read4Bytes(bytes, 0x1C);
        scriptEntryPointsOffset = read4Bytes(bytes, 0x20);
        jumpsOffset = read4Bytes(bytes, 0x24);
        alwaysZero2 = read4Bytes(bytes, 0x28);
        privateDataOffset = read4Bytes(bytes, 0x2C);
        sharedDataOffset = read4Bytes(bytes, 0x30);
    }

    public String getNonCommonString() {
        List<String> list = new ArrayList<>();
        list.add("Type=" + scriptTypeToString(scriptType) + " [" + String.format("%02X", scriptType) + "h]");
        if (purpose != null) {
            list.add("Purpose=" + purposeToString(purpose) + " [" + String.format("%02X", purpose) + "h]");
        }
        list.add("Entrypoints=" + entryPointCount);
        list.add("Jumps=" + jumpCount);
        list.add(privateValuesString());
        /* list.add(privateDataOffset != 0 ? "privateDataOffset=" + String.format("%04X", privateDataOffset) : "");
        list.add(privateDataLength != 0 ? "privateDataLength=" + String.format("%04X", privateDataLength) : ""); */
        list.add(alwaysZero1 != 0 ? "alwaysZero1=" + alwaysZero1 : "");
        list.add(alwaysZero2 != 0 ? "alwaysZero2=" + alwaysZero2 : "");
        String full = list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining(", "));
        return "{ " + full + " }";
    }

    public String getEntryPointsLine() {
        if (entryPoints == null || entryPoints.length == 0) {
            return null;
        }
        return Arrays.stream(entryPoints).map(e -> "e" + String.format("%02X", e.jumpIndex) + "=" + String.format("%04X", e.addr)).collect(Collectors.joining(" "));
    }

    public String getJumpsLine() {
        if (jumps == null || jumps.length == 0) {
            return null;
        }
        return Arrays.stream(jumps).map(j -> "j" + String.format("%02X", j.jumpIndex) + "=" + String.format("%04X", j.addr)).collect(Collectors.joining(" "));
    }

    public void setVariableInitialValues(ScriptObject script, int[] bytes) {
        if (variableDeclarations == null || variableDeclarations.length == 0) {
            return;
        }
        privateVars = new ArrayList<>();
        sharedVars = new ArrayList<>();
        final int maxPrivate = privateDataOffset + privateDataLength;
        for (ScriptVariable vr : variableDeclarations) {
            if (vr.location == 3) {
                int max = privateDataOffset + vr.offset + vr.getLength();
                if (max <= maxPrivate) {
                    privateVars.add(new ScriptVariable(vr));
                }
            } else if (vr.location == 4) {
                sharedVars.add(new ScriptVariable(vr));
            }
        }
        privateVars.forEach(p -> p.parseValues(script, bytes, privateDataOffset));
        sharedVars.forEach(s -> s.parseValues(script, bytes, sharedDataOffset));
    }

    public void setPurpose(int purpose, int valueCount, int[] payload) {
        this.purpose = purpose;
        this.purposeBytes = payload;
        for (int i = 0; i < valueCount; i++) {
            int val = read2Bytes(payload, i * 2);
            if (purpose == 2) {
                if (val < entryPoints.length) {
                    entryPoints[val].setCtbPurpose(i);
                }
            }
        }
    }

    public static String scriptTypeToString(int scriptType) {
        return switch (scriptType) {
            case 0 -> "Subroutine";
            case 1 -> "FieldObject";
            case 2 -> "BattleObject";
            case 4 -> "Cutscene";
            default -> "unknown?";
        };
    }

    public static String purposeToString(int purpose) {
        return switch (purpose) {
            case 0 -> "CameraControl";
            case 1 -> "MotionControl";
            case 2 -> "CombatControl";
            default -> "unknown?";
        };
    }

    private String privateValuesString() {
        if (privateVars == null || privateVars.isEmpty()) {
            return "";
        }
        String joined = privateVars.stream().filter(v -> !v.values.isEmpty()).map(v -> v.initString()).collect(Collectors.joining(", "));
        return "privateVars=[" + joined + "]";
    }

    private String sharedValuesString() {
        if (sharedVars == null || sharedVars.isEmpty()) {
            return "";
        }
        String joined = sharedVars.stream().filter(v -> !v.values.isEmpty()).map(ScriptVariable::valuesString).collect(Collectors.joining(", "));
        return "sharedVars=[" + joined + "]";
    }

    private static int read2Bytes(int[] bytes, int offset) {
        return bytes[offset] + bytes[offset+1] * 0x100;
    }

    private static int read4Bytes(int[] bytes, int offset) {
        return bytes[offset] + bytes[offset+1] * 0x100 + bytes[offset+2] * 0x10000 + bytes[offset+3] * 0x1000000;
    }
}
