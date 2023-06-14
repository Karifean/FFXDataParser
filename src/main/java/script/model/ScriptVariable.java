package script.model;

public class ScriptVariable {
    public final long struct;
    public final int lb;
    public final int hb;
    public final int offset;
    public final int format;
    public final int location;
    public final int elementCount;
    public final int unknownTopBytes;

    public ScriptVariable(int lb, int hb) {
        this.lb = lb;
        this.hb = hb;
        this.struct = hb * 0x100000000L + lb;
        this.offset = lb & 0xFFFFFF;
        this.format = (lb & 0xF0000000) >> 28;
        this.location = (lb & 0x0F000000) >> 25;
        this.elementCount = hb & 0xFFFF;
        this.unknownTopBytes = (hb & 0xFFFF0000) >> 16;
    }

    @Override
    public String toString() {
        return " { " +
                "offset=" + offset + " [" + String.format("%06X", offset) + "h]" +
                ", format=" + formatToString() + " [" + String.format("%02X", format) + "h]" +
                ", location=" + locationToString() + " [" + String.format("%02X", location) + "h]" +
                ", elementCount=" + elementCount + " [" + String.format("%04X", elementCount) + "h]" +
                ", utb=" + unknownTopBytes + " [" + String.format("%04X", unknownTopBytes) + "h]" +
                " } ";
    }

    private String formatToString() {
        return formatToString(format);
    }

    private String locationToString() {
        return locationToString(location);
    }

    public static String locationToString(int location) {
        return switch (location) {
            case 0 -> "unknown0";
            case 1 -> "unknown1";
            case 2 -> "dataOffset";
            case 3 -> "privateOffset";
            case 4 -> "sharedOffset";
            case 5 -> "int variables";
            case 6 -> "event-level data";
            default -> "unknown?";
        };
    }

    public static String formatToString(int format) {
        return switch (format) {
            case 0 -> "uint8";
            case 1 -> "int8";
            case 2 -> "uint16";
            case 3 -> "int16";
            case 4 -> "uint32";
            case 5 -> "int32";
            case 6 -> "float";
            default -> "unknown";
        };
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
