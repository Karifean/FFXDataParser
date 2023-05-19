package model;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class DataFileReader<T> {

    public  abstract T objectCreator(int[] bytes, int[] stringBytes) throws IOException;
    public abstract String indexWriter(int idx);

    public List<T> readGenericDataFile(String filename, boolean print) {
        if (print) {
            System.out.println("--- " + filename + " ---");
        }
        File file = new File(filename);
        if (!file.isDirectory()) {
            try (DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
                inputStream.skipBytes(0xA);
                int count = inputStream.read();
                count += inputStream.read() * 0x100;
                List<T> objects = new ArrayList<>(count + 1);
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
                for (int i = 0; i <= count; i++) {
                    T obj = objectCreator(Arrays.copyOfRange(moveBytes, i * individualLength, (i + 1) * individualLength), allStrings);
                    objects.add(obj);
                    if (print) {
                        String offset = String.format("%04x", (i * individualLength) + 20);
                        System.out.println(indexWriter(i) + " (Offset " + offset + ") - " + obj);
                    }
                }
                return objects;
            } catch (IOException ignored) {}
        }
        return null;
    }
}
