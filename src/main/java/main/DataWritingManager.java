package main;

import model.LocalizedStringObject;
import model.StringStruct;
import model.Writable;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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

    public static int[] dataObjectsToBytes(Writable[] objects, int length, String localization, final boolean optimizeStrings) {
        List<String> strings = Arrays.stream(objects).flatMap(ab -> ab.getStrings(localization)).collect(Collectors.toList());
        StringStruct stringStruct = StringHelper.createStringMap(strings, optimizeStrings);
        List<Integer> bytes = new ArrayList<>(List.of(1, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        add2Bytes(bytes, objects.length - 1);
        add2Bytes(bytes, length);
        add2Bytes(bytes, objects.length * length);
        bytes.addAll(List.of(0x14, 0x00, 0x00, 0x00));
        for (Writable obj : objects) {
            int[] abilityBytes = obj.toBytes(localization, stringStruct.stringToOffsetMap);
            bytes.addAll(Arrays.stream(abilityBytes).boxed().collect(Collectors.toList()));
        }
        bytes.addAll(Arrays.stream(stringStruct.stringBytes).boxed().collect(Collectors.toList()));
        int[] fullBytes = new int[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            fullBytes[i] = bytes.get(i);
        }
        return fullBytes;
    }

    public static void writeDataObjectsInAllLocalizations(String path, Writable[] abilities, final int length, final boolean optimizeStrings) {
        LOCALIZATIONS.forEach((key, value) -> {
            String localePath = GAME_FILES_ROOT + MODS_FOLDER + getLocalizationRoot(key) + path;
            int[] bytes = dataObjectsToBytes(abilities, length, key, optimizeStrings);
            writeByteArrayToFile(localePath, bytes);
        });
    }

    public static void writeStringFileForAllLocalizations(String path, List<LocalizedStringObject> localizedStrings, final boolean doubleHeaders) {
        LOCALIZATIONS.forEach((key, value) -> {
            String localePath = GAME_FILES_ROOT + MODS_FOLDER + getLocalizationRoot(key) + path;
            int[] bytes = stringsToStringFileBytes(localizedStrings, key, doubleHeaders);
            writeByteArrayToFile(localePath, bytes);
        });
    }

    public static int[] stringsToStringFileBytes(List<LocalizedStringObject> localizedStrings, String localization, boolean doubleHeaders) {
        List<String> strings = localizedStrings.stream().map(so -> so.getLocalizedContent(localization)).toList();
        StringStruct stringStruct = StringHelper.createStringMap(strings, false);
        Map<String, Integer> headMap = stringStruct.get4ByteHeadMap();
        List<Integer> bytes = new ArrayList<>();
        strings.forEach(str -> {
            int head = headMap.get(str);
            add4Bytes(bytes, head);
            if (doubleHeaders) {
                add4Bytes(bytes, head);
            }
        });
        bytes.addAll(Arrays.stream(stringStruct.stringBytes).boxed().collect(Collectors.toList()));
        int[] fullBytes = new int[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            fullBytes[i] = bytes.get(i);
        }
        return fullBytes;
    }

    private static void add2Bytes(List<Integer> array, int value) {
        array.add(value & 0x00FF);
        array.add((value & 0xFF00) >> 8);
    }

    private static void add4Bytes(List<Integer> array, int value) {
        array.add(value & 0x00FF);
        array.add((value & 0xFF00) >> 8);
        array.add((value & 0xFF0000) >> 16);
        array.add((value & 0xFF000000) >> 24);
    }
}
