package main;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ChunkedFileHelper {

    public static List<int[]> readGenericChunkedFile(String filename, boolean print, List<Integer> knownLengths) {
        if (print) {
            System.out.println("--- " + filename + " ---");
        }
        File file = new File(filename);
        if (!file.isDirectory()) {
            byte[] allBytes = null;
            try (DataInputStream data = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
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
            int chunkCount = read4Bytes(bytes, 0x00);
            if (chunkCount <= 0) {
                return new ArrayList<>();
            }
            int[] offsets = new int[chunkCount];
            for (int i = 0; i < chunkCount; i++) {
                offsets[i] = read4Bytes(bytes, (i + 1) * 4);
            }
            List<int[]> chunks = new ArrayList<>(chunkCount);
            for (int i = 0; i < chunkCount; i++) {
                int offset = offsets[i];
                if (offset <= 0) {
                    chunks.add(new int[0]);
                } else {
                    if (knownLengths != null && knownLengths.size() > i && knownLengths.get(i) != null) {
                        int length = knownLengths.get(i);
                        chunks.add(Arrays.copyOfRange(bytes, offset, offset + length));
                    } else {
                        int to = 0;
                        for (int j = i + 1; j < chunkCount; j++) {
                            if (offsets[j] > offset) {
                                to = offsets[j];
                                break;
                            }
                        }
                        if (to == 0) {
                            to = byteCount;
                        }
                        chunks.add(Arrays.copyOfRange(bytes, offset, to));
                    }
                }
            }
            return chunks;
        }
        return null;
    }

    private static int read4Bytes(int[] bytes, int offset) {
        return bytes[offset] + bytes[offset+1] * 0x100 + bytes[offset+2] * 0x10000 + bytes[offset+3] * 0x1000000;
    }
}
