package reading;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
