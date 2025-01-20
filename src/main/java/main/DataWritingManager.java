package main;

import model.StringStruct;
import model.Writable;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static main.DataReadingManager.LOCALIZATIONS;
import static main.DataReadingManager.getLocalizationRoot;
import static reading.FileAccessorWithMods.GAME_FILES_ROOT;
import static reading.FileAccessorWithMods.MODS_FOLDER;

public class DataWritingManager {

    public static void writeByteArrayToFile(String path, int[] bytes) {
        try {
            Files.createDirectories(Paths.get(path.substring(0, path.lastIndexOf('/'))));
        } catch (IOException e) {
            System.err.println("Failed to create directories");
            e.printStackTrace();
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(path)) {
            for (int aByte : bytes) {
                fileOutputStream.write(aByte);
            }
            fileOutputStream.flush();
        } catch (IOException e) {
            System.err.println("Failed to write file");
            e.printStackTrace();
        }
    }

    public static int[] dataObjectsToBytes(Writable[] objects, int length, String localization) {
        List<String> strings = Arrays.stream(objects).flatMap(ab -> ab.getStrings(localization)).collect(Collectors.toList());
        StringStruct stringStruct = StringHelper.createStringMap(strings);
        List<Integer> bytes = new ArrayList<>(List.of(1, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        add2Bytes(bytes, objects.length - 1);
        add2Bytes(bytes, length);
        add2Bytes(bytes, objects.length * length);
        bytes.addAll(List.of(0x14, 0x00, 0x00, 0x00));
        for (Writable obj : objects) {
            int[] abilityBytes = obj.toBytes(localization, stringStruct.stringMap);
            bytes.addAll(Arrays.stream(abilityBytes).boxed().collect(Collectors.toList()));
        }
        bytes.addAll(Arrays.stream(stringStruct.stringBytes).boxed().collect(Collectors.toList()));
        int[] fullBytes = new int[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            fullBytes[i] = bytes.get(i);
        }
        return fullBytes;
    }

    public static void writeDataObjectsInAllLocalizations(String path, Writable[] abilities, int length) {
        LOCALIZATIONS.forEach((key, value) -> {
            String localePath = GAME_FILES_ROOT + MODS_FOLDER + getLocalizationRoot(key) + path;
            int[] bytes = dataObjectsToBytes(abilities, length, key);
            writeByteArrayToFile(localePath, bytes);
        });
    }

    private static void add2Bytes(List<Integer> array, int value) {
        array.add(value & 0x00FF);
        array.add((value & 0xFF00) / 0x100);
    }
}
