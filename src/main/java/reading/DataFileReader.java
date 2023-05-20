package reading;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class DataFileReader<T> {

    public abstract T objectCreator(int[] bytes, int[] stringBytes) throws IOException;
    public abstract String indexWriter(int idx);

    public List<T> readGenericDataFile(String filename, boolean print) {
        File file = FileAccessorWithMods.resolveFile(filename, print);
        if (!file.isDirectory()) {
            try (DataInputStream inputStream = FileAccessorWithMods.readFile(file)) {
                inputStream.skipBytes(0xA);
                int maxIndex = inputStream.read();
                maxIndex += inputStream.read() * 0x100;
                List<T> objects = new ArrayList<>(maxIndex + 1);
                int individualLength = inputStream.read();
                individualLength += inputStream.read() * 0x100;
                int totalLength = inputStream.read();
                totalLength += inputStream.read() * 0x100;
                inputStream.skipBytes(4);
                int[] moveBytes = new int[totalLength];
                for (int i = 0; i < totalLength; i++) {
                    moveBytes[i] = inputStream.read();
                }
                byte[] stringBytes = inputStream.readAllBytes();
                int stringsLength = stringBytes.length;
                int[] allStrings = new int[stringsLength];
                for (int i = 0; i < stringsLength; i++) {
                    allStrings[i] = Byte.toUnsignedInt(stringBytes[i]);
                }
                for (int i = 0; i <= maxIndex; i++) {
                    T obj = objectCreator(Arrays.copyOfRange(moveBytes, i * individualLength, (i + 1) * individualLength), allStrings);
                    objects.add(obj);
                    if (print) {
                        String offset = String.format("%04x", (i * individualLength) + 20).toUpperCase();
                        System.out.println(indexWriter(i) + " (Offset " + offset + ") - " + obj);
                    }
                }
                return objects;
            } catch (IOException ignored) {}
        }
        return null;
    }
}
