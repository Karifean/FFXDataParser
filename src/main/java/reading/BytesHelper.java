package reading;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

public abstract class BytesHelper {

    public static int[] chunksToBytes(List<int[]> chunks, int chunkCount, int chunkInitialOffset, int chunkAlignment) {
        if (chunks.size() > ((chunkInitialOffset - 0x08) / 0x04)) {
            throw new IllegalArgumentException("Too many chunks for initial offset");
        }
        int[] header = new int[chunkInitialOffset];
        boolean terminateWithFFs = false;
        if (chunkCount < 0) {
            write4Bytes(header, 0x00, 0x31305645);
            chunkCount = chunks.size();
            terminateWithFFs = true;
        } else {
            write4Bytes(header, 0x00, chunkCount);
            chunkCount--;
        }
        int endOffset = chunkInitialOffset;
        int[] paddings = new int[chunkCount];
        for (int i = 0; i < chunkCount; i++) {
            int addressTargetOffset = 0x04 + 0x04 * i;
            int[] chunk = chunks.get(i);
            int padding = 0;
            if (chunk == null || chunk.length == 0) {
                write4Bytes(header, addressTargetOffset, 0);
            } else {
                write4Bytes(header, addressTargetOffset, endOffset);
                endOffset += chunk.length;
                if (chunkAlignment > 1) {
                    int misalignment = endOffset % chunkAlignment;
                    if (misalignment > 0) {
                        padding = chunkAlignment - misalignment;
                        endOffset += padding;
                    }
                }
            }
            paddings[i] = padding;
        }
        int chunkListEndOffset = 0x04 + 0x04 * chunkCount;
        write4Bytes(header, chunkListEndOffset, endOffset);
        if (terminateWithFFs) {
            write4Bytes(header, chunkListEndOffset + 0x04, 0xFFFFFFFF);
        }
        List<Integer> bytes = new ArrayList<>(Arrays.stream(header).boxed().toList());
        for (int i = 0; i < chunkCount; i++) {
            int[] chunk = chunks.get(i);
            if (chunk != null && chunk.length > 0) {
                bytes.addAll(Arrays.stream(chunk).boxed().toList());
            }
            if (paddings[i] > 0) {
                for (int j = 0; j < paddings[i]; j++) {
                    bytes.add(0);
                }
            }
        }

        int[] fullBytes = new int[endOffset];
        for (int i = 0; i < bytes.size(); i++) {
            fullBytes[i] = bytes.get(i);
        }
        return fullBytes;
    }

    public static int[] fileToBytes(File file) {
        try (DataInputStream data = FileAccessorWithMods.readFile(file)) {
            return byteToIntArray(data.readAllBytes());
        } catch (IOException ignored) {
            return null;
        }
    }

    public static int[] byteToIntArray(byte[] array) {
        if (array == null) {
            return null;
        }
        int length = array.length;
        int[] bytes = new int[length];
        for (int i = 0; i < length; i++) {
            bytes[i] = Byte.toUnsignedInt(array[i]);
        }
        return bytes;
    }

    public static int[] fileToBytes(String path, boolean print) {
        File file = FileAccessorWithMods.resolveFile(path, print);
        return fileToBytes(file);
    }

    public static List<Chunk> bytesToChunks(int[] bytes, int assumedChunkCount, int chunkOffset) {
        if (bytes == null) {
            return null;
        }
        int chunkCount = assumedChunkCount;
        int[] offsets = new int[chunkCount + 1];
        for (int i = 0; i <= chunkCount; i++) {
            int offset = read4Bytes(bytes, i * 4 + chunkOffset);
            if (offset == 0xFFFFFFFF) {
                chunkCount = i - 1;
            } else {
                offsets[i] = offset;
            }
        }
        List<Chunk> chunks = new ArrayList<>(chunkCount);
        for (int i = 0; i < chunkCount; i++) {
            int offset = offsets[i];
            if (offset == 0) {
                chunks.add(new Chunk());
            } else {
                int to = -1;
                for (int j = i + 1; j <= chunkCount; j++) {
                    if (offsets[j] >= offset) {
                        to = offsets[j];
                        break;
                    }
                }
                if (to == -1) {
                    to = bytes.length;
                }
                chunks.add(new Chunk(bytes, offset, to));
            }
        }
        return chunks;
    }

    public static <T> int findOrAppend(List<T> list, T obj) {
        int index = list.indexOf(obj);
        if (index == -1) {
            index = list.size();
            list.add(obj);
        }
        return index;
    }

    public static <T> int findOrAppend(List<T> list, T obj, Predicate<T> predicate) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            if (predicate.test(list.get(i))) {
                return i;
            }
        }
        list.add(obj);
        return size;
    }

    public static int[] intListToArray(List<Integer> list) {
        int size = list.size();
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    public static <T> T get(T[] array, int index) {
        if (array == null || index < 0 || index >= array.length) {
            return null;
        }
        return array[index];
    }

    public static <T> T get(List<T> list, int index) {
        if (list == null || index < 0 || index >= list.size()) {
            return null;
        }
        return list.get(index);
    }

    public static int read2Bytes(int[] bytes, int offset) {
        if (bytes == null) {
            return 0;
        }
        return bytes[offset] | (bytes[offset + 1] << 8);
    }

    public static int read4Bytes(int[] bytes, int offset) {
        if (bytes == null) {
            return 0;
        }
        return bytes[offset] | (bytes[offset + 1] << 8) | (bytes[offset + 2] << 16) | (bytes[offset + 3] << 24);
    }

    public static int read2Bytes(DataInputStream stream) throws IOException {
        int x = stream.read();
        return x | (stream.read() << 8);
    }

    public static int read4Bytes(DataInputStream stream) throws IOException {
        int x = read2Bytes(stream);
        return x | (read2Bytes(stream) << 16);
    }

    public static long read8Bytes(DataInputStream stream) throws IOException {
        long x = read4Bytes(stream);
        return x | ((long) read4Bytes(stream) << 32);
    }

    public static void write2Bytes(int[] bytes, int offset, int value) {
        if (bytes == null) {
            return;
        }
        bytes[offset]     =  value & 0x00FF;
        bytes[offset + 1] = (value & 0xFF00) >> 8;
    }

    public static void write3Bytes(int[] bytes, int offset, int value) {
        if (bytes == null) {
            return;
        }
        bytes[offset]     =  value & 0x0000FF;
        bytes[offset + 1] = (value & 0x00FF00) >> 8;
        bytes[offset + 2] = (value & 0xFF0000) >> 16;
    }

    public static void write4Bytes(int[] bytes, int offset, int value) {
        if (bytes == null) {
            return;
        }
        bytes[offset]     =  value & 0x000000FF;
        bytes[offset + 1] = (value & 0x0000FF00) >> 8;
        bytes[offset + 2] = (value & 0x00FF0000) >> 16;
        bytes[offset + 3] = (value & 0xFF000000) >> 24;
    }

    public static void add2Bytes(List<Integer> bytes, int value) {
        if (bytes == null) {
            return;
        }
        bytes.add(value & 0x00FF);
        bytes.add((value & 0xFF00) >> 8);
    }

    public static void add4Bytes(List<Integer> bytes, int value) {
        if (bytes == null) {
            return;
        }
        bytes.add(value & 0x000000FF);
        bytes.add((value & 0x0000FF00) >> 8);
        bytes.add((value & 0x00FF0000) >> 16);
        bytes.add((value & 0xFF000000) >> 24);
    }

    public static void addUtf8StringBytes(List<Integer> bytes, String value) {
        if (bytes == null) {
            return;
        }
        for (byte b : value.getBytes(StandardCharsets.UTF_8)) {
            bytes.add((int) b);
        }
        bytes.add(0);
    }

    public static int[] utf8StringToBytes(String value) {
        if (value == null) {
            return new int[0];
        }
        int length = value.length();
        int[] bytes = new int[length + 1];
        byte[] stringBytes = value.getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < length; i++) {
            bytes[i] = stringBytes[i];
        }
        bytes[length] = 0;
        return bytes;
    }

    public static int padLengthTo(int length, int alignment) {
        if (alignment <= 1) {
            return length;
        }
        return (length + alignment - 1) & (-alignment);
    }
}
