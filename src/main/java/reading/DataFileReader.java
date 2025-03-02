package reading;

import main.StringHelper;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntFunction;

public class DataFileReader<T> {
    private final DataObjectCreator<T> objectCreator;
    private final IntFunction<T[]> arrayCreator;

    public DataFileReader(DataObjectCreator<T> objectCreator, IntFunction<T[]> arrayCreator) {
        this.objectCreator = objectCreator;
        this.arrayCreator = arrayCreator;
    }

    public String indexWriter(int idx) {
        return "Index " + StringHelper.hex2WithSuffix(idx);
    }

    public List<T> toList(String filename, String localization, boolean print) {
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
                    T obj = objectCreator.create(Arrays.copyOfRange(dataBytes, i * individualLength, (i + 1) * individualLength), allStrings, localization);
                    objects.add(obj);
                    if (print) {
                        String offset = StringHelper.formatHex4((i * individualLength) + 0x14);
                        System.out.println(indexWriter(i + minIndex) + " (Offset " + offset + ") - " + obj);
                    }
                }
                return objects;
            } catch (IOException ignored) {}
        }
        return null;
    }

    public List<T> readGenericX2DataFile(String filename, String localization, boolean print) {
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
                    T obj = objectCreator.create(Arrays.copyOfRange(dataBytes, i * individualLength, (i + 1) * individualLength), allStrings, localization);
                    objects.add(obj);
                    if (print) {
                        String offset = StringHelper.formatHex4((i * individualLength) + 0x20);
                        System.out.println(indexWriter(i + minIndex) + " (Offset " + offset + ") - " + obj);
                    }
                }
                return objects;
            } catch (IOException ignored) {}
        }
        return null;
    }

    public T[] toArray(String filename, String localization, boolean print) {
        List<T> list = toList(filename, localization, print);
        if (list == null) {
            return null;
        }
        return list.toArray(arrayCreator);
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
