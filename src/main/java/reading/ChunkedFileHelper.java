package reading;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public abstract class ChunkedFileHelper {
    public static final int DEFAULT_ASSUMED_CHUNK_COUNT = 10;
    public static final int DEFAULT_ASSUMED_CHUNK_OFFSET = 4;

    public static List<Chunk> readGenericChunkedFile(String filename, boolean print, boolean readChunkCount) {
        File file = FileAccessorWithMods.resolveFile(filename, print);
        if (!file.isDirectory()) {
            int[] bytes = fileToBytes(file);
            return bytesToChunks(bytes, readChunkCount ? read4Bytes(bytes, 0x00) - 1 : DEFAULT_ASSUMED_CHUNK_COUNT, DEFAULT_ASSUMED_CHUNK_OFFSET);
        }
        return null;
    }

    public static List<Chunk> readGenericChunkedFile(String filename, boolean print, int chunkCount) {
        File file = FileAccessorWithMods.resolveFile(filename, print);
        if (!file.isDirectory()) {
            int[] bytes = fileToBytes(file);
            return bytesToChunks(bytes, chunkCount, 0);
        }
        return null;
    }

    public static int[] fileToBytes(File file) {
        byte[] allBytes = null;
        try (DataInputStream data = FileAccessorWithMods.readFile(file)) {
            allBytes = data.readAllBytes();
        } catch (IOException ignored) {}
        if (allBytes == null) {
            return null;
        }
        int byteCount = allBytes.length;
        int[] bytes = new int[byteCount];
        for (int j = 0; j < byteCount; j++) {
            bytes[j] = Byte.toUnsignedInt(allBytes[j]);
        }
        return bytes;
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

    public static int read2Bytes(int[] bytes, int offset) {
        if (bytes == null) {
            return 0;
        }
        return bytes[offset] + bytes[offset+1] * 0x100;
    }

    public static int read4Bytes(int[] bytes, int offset) {
        if (bytes == null) {
            return 0;
        }
        return bytes[offset] + bytes[offset+1] * 0x100 + bytes[offset+2] * 0x10000 + bytes[offset+3] * 0x1000000;
    }

    public static void write2Bytes(int[] bytes, int offset, int value) {
        if (bytes == null) {
            return;
        }
        bytes[offset]     =  value & 0x00FF;
        bytes[offset + 1] = (value & 0xFF00) >> 8;
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
}
