package script.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ScriptHeader {
    public int unknownType;
    public int someRefsCount;
    public int refIntCount;
    public int refFloatCount;
    public int entryPointCount;
    public int jumpCount;
    public int alwaysZero1;
    public int unknownBitmask;
    public int someRefsTableOffset;
    public int intTableOffset;
    public int floatTableOffset;
    public int scriptEntryPointsOffset;
    public int jumpsOffset;
    public int alwaysZero2;
    public int unknownEventIndex;
    public int timeStampOffsetApparently;

    public ScriptJump[] entryPoints;
    public ScriptJump[] jumps;
    public long[] someRefs;
    public int[] refFloats;
    public int[] refInts;

    public String getNonCommonString() {
        List<String> list = new ArrayList<>();
        list.add("UnknownBitmask=" + formatUnknownByte(unknownBitmask));
        list.add("Entrypoints=" + entryPointCount);
        list.add("Jumps=" + jumpCount);
        list.add(unknownType != 2 ? "UnknownType=" + unknownType : ""); // always 2 in non-event scripts
        list.add(unknownEventIndex != 0 ? "UnknownEventIndex=" + formatUnknownByte(unknownEventIndex) : "");
        list.add(alwaysZero1 != 0 ? "alwaysZero1=" + alwaysZero1 : "");
        list.add(alwaysZero2 != 0 ? "alwaysZero2=" + alwaysZero2 : "");
        String full = list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining(", "));
        return "{ " + full + " }";
    }

    private static String formatUnknownByte(int bt) {
        return String.format("%02X", bt) + '=' + String.format("%03d", bt) + '(' + String.format("%8s", Integer.toBinaryString(bt)).replace(' ', '0') + ')';
    }

    /**
     * script header + 0x14 has a bunch of eight-byte descriptors with the following format:
     *
     * OO OO OO ffffaaa_ CC CC XX XX
     *
     * offset : 24
     * format : 4
     * location : 3
     * pad : 1
     * element count : 16
     * unused? : 16
     *
     * (i.e. the format info is in the high bits of the first u32)
     *
     * format:
     *   u8 = 0
     *   i8
     *   u16
     *   i16
     *   u32
     *   i32
     *   float = 6
     *
     *
     * location:
     *     0 ?
     *     1 ?
     *     2 "dataOffset" (script header + 0x28)
     *     3 callback, or "privateOffset" (script header + 0x2c)
     *     4 "sharedOffset" (script header + 0x30)
     *     5 int variables (which are followed by float variables, could in principle access other script context vars)
     *     6 event-level data (event file + 0x20)
     */
}
