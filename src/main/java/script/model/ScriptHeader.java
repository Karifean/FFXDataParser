package script.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ScriptHeader {
    public int scriptType;
    public int variablesCount;
    public int refIntCount;
    public int refFloatCount;
    public int entryPointCount;
    public int jumpCount;
    public int alwaysZero1;
    public int privateDataLengthOrUnknownBitmask;
    public int variableStructsTableOffset;
    public int intTableOffset;
    public int floatTableOffset;
    public int scriptEntryPointsOffset;
    public int jumpsOffset;
    public int alwaysZero2;
    public int privateDataOffset;
    public int sharedDataOffset;

    public ScriptJump[] entryPoints;
    public ScriptJump[] jumps;
    public ScriptVariable[] variableDeclarations;
    public int[] refFloats;
    public int[] refInts;

    public String getNonCommonString() {
        List<String> list = new ArrayList<>();
        list.add("UnknownBitmask=" + formatUnknownByte(privateDataLengthOrUnknownBitmask));
        list.add("Type=" + scriptTypeToString(scriptType) + " [" + scriptType + "h]");
        list.add("Entrypoints=" + entryPointCount);
        list.add("Jumps=" + jumpCount);
        list.add(privateDataOffset != 0 ? "UnknownEventOffset=" + formatUnknownByte(privateDataOffset) : "");
        list.add(alwaysZero1 != 0 ? "alwaysZero1=" + alwaysZero1 : "");
        list.add(alwaysZero2 != 0 ? "alwaysZero2=" + alwaysZero2 : "");
        String full = list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining(", "));
        return "{ " + full + " }";
    }

    private static String formatUnknownByte(int bt) {
        return String.format("%02X", bt) + '=' + String.format("%03d", bt) + '(' + String.format("%8s", Integer.toBinaryString(bt)).replace(' ', '0') + ')';
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
}
