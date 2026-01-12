package atel.model;

import atel.AtelScriptObject;
import main.StringHelper;
import reading.BytesHelper;

import java.util.*;
import java.util.stream.Collectors;

import static reading.BytesHelper.*;

public class ScriptMetaStruct {
    public static final int HEADER_LENGTH = 0x40;
    public static final int TIMESTAMP_LENGTH = 0x0E;

    public AtelScriptObject parentScript;

    private int[] bytes;
    private int value00Always40;
    private int value04Always00;
    private int value08Always00;
    private int offset0CStart;
    private int timestampOffset;
    private int value14;
    private int offset18;
    private int offset1C;
    private int value20;
    private int offset24;
    private int offset28;
    private int offset2C;
    private int value30;
    private int value34;
    private int value38;
    private int offset3C;

    private int enterOffset;

    private String timestamp;
    private ScriptMetaSubObject obj0C;
    private ScriptMetaSubObject timestampObj10;
    private ScriptMetaSubObject obj18;
    private ScriptMetaSubObject obj1C;
    private ScriptMetaSubObject obj24;
    private ScriptMetaSubObject obj28;
    private ScriptMetaSubObject obj2C;
    private ScriptMetaMapPointerObject obj3C;

    public ScriptMetaStruct(AtelScriptObject parentScript, int[] fullBytes, int enterOffset) {
        this.enterOffset = enterOffset;
        this.parentScript = parentScript;
        value00Always40 = read4Bytes(fullBytes, enterOffset);
        value04Always00 = read4Bytes(fullBytes, enterOffset + 0x04);
        value08Always00 = read4Bytes(fullBytes, enterOffset + 0x08);
        offset0CStart = read4Bytes(fullBytes, enterOffset + 0x0C);
        timestampOffset = read4Bytes(fullBytes, enterOffset + 0x10);
        value14 = read4Bytes(fullBytes, enterOffset + 0x14);
        offset18 = read4Bytes(fullBytes, enterOffset + 0x18);
        offset1C = read4Bytes(fullBytes, enterOffset + 0x1C);
        value20 = read4Bytes(fullBytes, enterOffset + 0x20);
        offset24 = read4Bytes(fullBytes, enterOffset + 0x24);
        offset28 = read4Bytes(fullBytes, enterOffset + 0x28);
        offset2C = read4Bytes(fullBytes, enterOffset + 0x2C);
        value30 = read4Bytes(fullBytes, enterOffset + 0x30);
        value34 = read4Bytes(fullBytes, enterOffset + 0x34);
        value38 = read4Bytes(fullBytes, enterOffset + 0x38);
        offset3C = read4Bytes(fullBytes, enterOffset + 0x3C);
        obj0C = makeSubObject(fullBytes, offset0CStart, 0x04 + read2Bytes(fullBytes, offset0CStart) * 0x08);
        int[] timestampBytes = Arrays.copyOfRange(fullBytes, timestampOffset, timestampOffset + TIMESTAMP_LENGTH);
        timestamp = StringHelper.getUtf8String(timestampBytes);
        timestampObj10 = new ScriptMetaSubObject(timestampBytes, timestampOffset, TIMESTAMP_LENGTH);
        obj18 = makeSubObject(fullBytes, offset18, 0x04 + read2Bytes(fullBytes, offset18) * 0x08);
        obj1C = makeSubObject(fullBytes, offset1C, 0x04 + read2Bytes(fullBytes, offset1C) * 0x02);
        int offsetAfter24 = offset2C != 0 ? offset2C : (offset28 != 0 ? offset28 : timestampOffset);
        obj24 = makeSubObject(fullBytes, offset24, offsetAfter24 - offset24); // makeSillyObject?
        obj28 = makeSubObject(fullBytes, offset28, 0x02 + read2Bytes(fullBytes, offset28) * 0x02);
        obj2C = makeSubObject(fullBytes, offset2C, 0x3C);
        if (offset3C != 0) {
            obj3C = new ScriptMetaMapPointerObject(fullBytes, offset3C);
        }
        this.bytes = Arrays.copyOfRange(fullBytes, offset0CStart, enterOffset + HEADER_LENGTH);
    }

    public ScriptMetaStructBytes toBytes(int newStartOffset) {
        int[] header = new int[HEADER_LENGTH];
        write4Bytes(header, 0x00, value00Always40);
        write4Bytes(header, 0x04, value04Always00);
        write4Bytes(header, 0x08, value08Always00);
        write4Bytes(header, 0x14, value14);
        write4Bytes(header, 0x20, value20);
        write4Bytes(header, 0x30, value30);
        write4Bytes(header, 0x34, value34);
        write4Bytes(header, 0x38, value38);
        int[] timestampBytes = BytesHelper.utf8StringToBytes(timestamp);
        int timestampBytesLength = Math.max(timestampBytes.length, 0x14);
        int totalLength = lengthOf(obj0C) + lengthOf(obj24) + lengthOf(obj2C) + lengthOf(obj28, 4) + timestampBytesLength + lengthOf(obj18) + lengthOf(obj1C) + lengthOf(obj3C) + HEADER_LENGTH;
        int[] fullBytes = new int[totalLength];
        int offset = 0;
        if (obj0C != null) {
            write4Bytes(header, 0x0C, newStartOffset + offset);
            System.arraycopy(obj0C.bytes(), 0, fullBytes, offset, obj0C.length());
            offset += obj0C.length();
        }
        if (obj24 != null) {
            write4Bytes(header, 0x24, newStartOffset + offset);
            System.arraycopy(obj24.bytes(), 0, fullBytes, offset, obj24.length());
            offset += obj24.length();
        }
        if (obj2C != null) {
            write4Bytes(header, 0x2C, newStartOffset + offset);
            System.arraycopy(obj2C.bytes(), 0, fullBytes, offset, obj2C.length());
            offset += obj2C.length();
        }
        if (obj28 != null) {
            write4Bytes(header, 0x28, newStartOffset + offset);
            System.arraycopy(obj28.bytes(), 0, fullBytes, offset, obj28.length());
            offset += padLengthTo(obj28.length(), 4);
        }
        write4Bytes(header, 0x10, newStartOffset + offset);
        System.arraycopy(timestampBytes, 0, fullBytes, offset, timestampBytes.length);
        offset += timestampBytesLength;
        if (obj18 != null) {
            write4Bytes(header, 0x18, newStartOffset + offset);
            System.arraycopy(obj18.bytes(), 0, fullBytes, offset, obj18.length());
            offset += obj18.length();
        }
        if (obj1C != null) {
            write4Bytes(header, 0x1C, newStartOffset + offset);
            System.arraycopy(obj1C.bytes(), 0, fullBytes, offset, obj1C.length());
            offset += obj1C.length();
        }
        if (obj3C != null) {
            if (obj3C.obj08 != null) {
                obj3C.offset08 = newStartOffset + offset;
                System.arraycopy(obj3C.obj08, 0, fullBytes, offset, obj3C.obj08.length);
                offset += obj3C.obj08.length;
            }
            if (obj3C.obj0C != null) {
                obj3C.offset0C = newStartOffset + offset;
                System.arraycopy(obj3C.obj0C, 0, fullBytes, offset, obj3C.obj0C.length);
                offset += obj3C.obj0C.length;
            }
            write4Bytes(header, 0x3C, newStartOffset + offset);
            int[] obj3CHeaderBytes = obj3C.getHeaderBytes();
            System.arraycopy(obj3CHeaderBytes, 0, fullBytes, offset, obj3CHeaderBytes.length);
            offset += obj3CHeaderBytes.length;
        }
        System.arraycopy(header, 0, fullBytes, offset, HEADER_LENGTH);
        return new ScriptMetaStructBytes(fullBytes, newStartOffset + offset);
    }

    public int getStartOffset() {
        return offset0CStart;
    }

    private static int lengthOf(ScriptMetaSubObject obj) {
        return obj != null ? obj.length() : 0;
    }

    private static int lengthOf(ScriptMetaMapPointerObject obj) {
        return obj != null ? obj.length() : 0;
    }

    private static int lengthOf(ScriptMetaSubObject obj, int alignment) {
        return padLengthTo(lengthOf(obj), alignment);
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        list.add("Enter Offset = " + StringHelper.formatHex4(enterOffset));
        if (value00Always40 != 0x40) {
            list.add("val00=" + StringHelper.formatHex4(value00Always40));
        }
        if (value04Always00 != 0x00) {
            list.add("val04=" + StringHelper.formatHex4(value04Always00));
        }
        if (value08Always00 != 0x00) {
            list.add("val08=" + StringHelper.formatHex4(value08Always00));
        }
        list.add("val14=" + StringHelper.formatHex4(value14));
        list.add("val20=" + StringHelper.formatHex4(value20));
        list.add("val30=" + StringHelper.formatHex4(value30));
        list.add("val34=" + StringHelper.formatHex4(value34));
        list.add("val38=" + StringHelper.formatHex4(value38));
        // Order of objects: 0C, 24, 2C, 28, 10 (Timestamp), 18, 1C, 3C, "Header"
        list.add("obj0C=" + writeSubObject(obj0C, obj24));
        list.add("obj24=" + writeSubObject(obj24, obj2C));
        list.add("obj2C=" + writeSubObject(obj2C, obj28));
        list.add("obj28=" + writeSubObject(obj28, timestampObj10));
        list.add("Timestamp=" + timestamp + " (" + writeSubObject(timestampObj10, obj18) + ")");
        list.add("obj18=" + writeSubObject(obj18, obj1C));
        list.add("obj1C=" + writeSubObject(obj1C, null));
        list.add("obj3C=" + (obj3C != null ? obj3C.toString() : "null"));
        String full = list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining("\n"));
        return "{ " + full + " }";
    }

    private static ScriptMetaSubObject makeSubObject(int[] fullBytes, int offset, int length) {
        int[] payload = getSubObjectBytes(fullBytes, offset, length);
        if (payload == null) {
            return null;
        }
        return new ScriptMetaSubObject(payload, offset, length);
    }

    private static int[] getSubObjectBytes(int[] fullBytes, int offset, int length) {
        if (offset == 0x00) {
            return null;
        }
        return Arrays.copyOfRange(fullBytes, offset, offset + length);
    }

    private static String writeSubObject(ScriptMetaSubObject obj, ScriptMetaSubObject next) {
        if (obj == null) {
            return "null";
        }
        List<String> list = new ArrayList<>();
        String payload = obj.bytes() != null ? Arrays.stream(obj.bytes()).mapToObj(StringHelper::formatHex2).collect(Collectors.joining("")) : "";
        int end = obj.enterOffset() + obj.length();
        list.add("at=" + StringHelper.formatHex4(obj.enterOffset()));
        list.add("to=" + StringHelper.formatHex4(end));
        if (next != null) {
            list.add("distance=" + StringHelper.hex2WithSuffix(next.enterOffset() - end));
        }
        list.add("len="  + StringHelper.formatHex2(obj.length()));
        list.add(payload);
        return list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining("; "));
    }

    public record ScriptMetaStructBytes(int[] bytes, int enterOffset) {}
    
    public record ScriptMetaSubObject(int[] bytes, int enterOffset, int length) {}

    private static class ScriptMetaMapPointerObject {
        private int myOffset;
        private int[] myBytes;
        private int offset08;
        private int offset0C;
        private int[] obj08;
        private int[] obj0C;

        private ScriptMetaMapPointerObject(int[] fullBytes, int headerOffset) {
            myOffset = headerOffset;
            myBytes = Arrays.copyOfRange(fullBytes, headerOffset, headerOffset + 0x84);
            offset08 = read4Bytes(myBytes, 0x08);
            offset0C = read4Bytes(myBytes, 0x0C);
            if (offset08 != 0) {
                obj08 = Arrays.copyOfRange(fullBytes, offset08, offset08 + 0x04 + read4Bytes(fullBytes, offset08) * 0x02);
            }
            if (offset0C != 0) {
                obj0C = Arrays.copyOfRange(fullBytes, offset0C, offset0C + 0x40);
            }
        }

        private int[] getHeaderBytes() {
            int[] headerBytes = new int[0x84];
            System.arraycopy(myBytes, 0, headerBytes, 0, 0x84);
            write4Bytes(headerBytes, 0x08, offset08);
            write4Bytes(headerBytes, 0x0C, offset0C);
            return headerBytes;
        }

        private int length() {
            int len = 0x84;
            if (obj08 != null) {
                len += obj08.length;
            }
            if (obj0C != null) {
                len += obj0C.length;
            }
            return len;
        }

        @Override
        public String toString() {
            List<String> list = new ArrayList<>();
            list.add("at=" + StringHelper.formatHex4(myOffset));
            list.add("offset08=" + StringHelper.formatHex4(offset08));
            list.add("offset0C=" + StringHelper.formatHex4(offset0C));
            String bytes08 = obj08 != null ? Arrays.stream(obj08).mapToObj(StringHelper::formatHex2).collect(Collectors.joining("")) : "";
            list.add("obj08=" + bytes08);
            String bytes0C = obj0C != null ? Arrays.stream(obj0C).mapToObj(StringHelper::formatHex2).collect(Collectors.joining("")) : "";
            list.add("obj0C=" + bytes0C);
            return list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining("; "));
        }
    }
}
