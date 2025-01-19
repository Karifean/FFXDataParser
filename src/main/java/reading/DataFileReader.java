package reading;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataFileReader<T> {
    private final DataObjectCreator<T> objectCreator;

    public DataFileReader(DataObjectCreator<T> objectCreator) {
        this.objectCreator = objectCreator;
    }

    public String indexWriter(int idx) {
        return "Index " + idx + " [" + String.format("%02X", idx) + "h]";
    }

    public List<T> readGenericDataFile(String filename, boolean print) {
        File file = FileAccessorWithMods.resolveFile(filename, print);
        if (!file.isDirectory()) {
            try (DataInputStream inputStream = FileAccessorWithMods.readFile(file)) {
                inputStream.skipBytes(0x8);
                final int minIndex = read2Bytes(inputStream);
                final int maxIndex = read2Bytes(inputStream);
                final List<T> objects = new ArrayList<>(maxIndex + 1 - minIndex);
                final int individualLength = read2Bytes(inputStream);
                final int totalLength = read2Bytes(inputStream);
                inputStream.skipBytes(4);
                final int[] dataBytes = new int[totalLength];
                for (int i = 0; i < totalLength; i++) {
                    dataBytes[i] = inputStream.read();
                }
                final byte[] stringBytes = inputStream.readAllBytes();
                final int stringsLength = stringBytes.length;
                final int[] allStrings = new int[stringsLength];
                for (int i = 0; i < stringsLength; i++) {
                    allStrings[i] = Byte.toUnsignedInt(stringBytes[i]);
                }
                final int j = maxIndex - minIndex;
                for (int i = 0; i <= j; i++) {
                    T obj = objectCreator.create(Arrays.copyOfRange(dataBytes, i * individualLength, (i + 1) * individualLength), allStrings);
                    objects.add(obj);
                    if (print) {
                        String offset = String.format("%04X", (i * individualLength) + 0x14);
                        System.out.println(indexWriter(i + minIndex) + " (Offset " + offset + ") - " + obj);
                    }
                }
                return objects;
            } catch (IOException ignored) {}
        }
        return null;
    }

    public List<T> readGenericX2DataFile(String filename, boolean print) {
        File file = FileAccessorWithMods.resolveFile(filename, print);
        if (!file.isDirectory()) {
            try (DataInputStream inputStream = FileAccessorWithMods.readFile(file)) {
                inputStream.skipBytes(0xC);
                final int minIndex = read4Bytes(inputStream);
                final int maxIndex = read4Bytes(inputStream);
                final List<T> objects = new ArrayList<>(maxIndex + 1 - minIndex);
                final int individualLength = read4Bytes(inputStream);
                final int totalLength = read4Bytes(inputStream);
                inputStream.skipBytes(4);
                final int[] dataBytes = new int[totalLength];
                for (int i = 0; i < totalLength; i++) {
                    dataBytes[i] = inputStream.read();
                }
                final byte[] stringBytes = inputStream.readAllBytes();
                final int stringsLength = stringBytes.length;
                final int[] allStrings = new int[stringsLength];
                for (int i = 0; i < stringsLength; i++) {
                    allStrings[i] = Byte.toUnsignedInt(stringBytes[i]);
                }
                final int j = maxIndex - minIndex;
                for (int i = 0; i <= j; i++) {
                    T obj = objectCreator.create(Arrays.copyOfRange(dataBytes, i * individualLength, (i + 1) * individualLength), allStrings);
                    objects.add(obj);
                    if (print) {
                        String offset = String.format("%04X", (i * individualLength) + 0x20);
                        System.out.println(indexWriter(i + minIndex) + " (Offset " + offset + ") - " + obj);
                    }
                }
                return objects;
            } catch (IOException ignored) {}
        }
        return null;
    }

    private int read2Bytes(DataInputStream stream) throws IOException {
        int x = stream.read();
        return x + stream.read() * 0x100;
    }

    private int read4Bytes(DataInputStream stream) throws IOException {
        int x = read2Bytes(stream);
        return x + read2Bytes(stream) * 0x10000;
    }
}
