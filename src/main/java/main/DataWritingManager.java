package main;

import atel.EncounterFile;
import atel.EventFile;
import model.strings.*;
import model.Writable;
import reading.FileAccessorWithMods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static main.DataReadingManager.LOCALIZATIONS;
import static main.DataReadingManager.getLocalizationRoot;
import static reading.FileAccessorWithMods.GAME_FILES_ROOT;
import static reading.FileAccessorWithMods.MODS_FOLDER;

public class DataWritingManager {

    public static int[] dataObjectsToBytes(Writable[] objects, int length, int from, int to, String localization, final boolean optimizeStrings) {
        Stream<KeyedString> stringStream = Arrays.stream(objects).flatMap(ab -> ab.streamKeyedStrings(localization));
        int[] stringBytes = KeyedString.rebuildKeyedStrings(stringStream, StringHelper.localizationToCharset(localization), optimizeStrings);
        List<Integer> bytes = new ArrayList<>(List.of(1, 0, 0, 0, 0, 0, 0, 0));
        add2Bytes(bytes, from);
        add2Bytes(bytes, to - 1);
        add2Bytes(bytes, length);
        add2Bytes(bytes, objects.length * length);
        bytes.addAll(List.of(0x14, 0x00, 0x00, 0x00));
        for (Writable obj : objects) {
            int[] objectBytes = obj.toBytes(localization);
            bytes.addAll(Arrays.stream(objectBytes).boxed().toList());
        }
        bytes.addAll(Arrays.stream(stringBytes).boxed().toList());
        int[] fullBytes = new int[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            fullBytes[i] = bytes.get(i);
        }
        return fullBytes;
    }

    public static int[] dataObjectWithStringsToBytes(Writable object, String localization) {
        Stream<KeyedString> stringStream = object.streamKeyedStrings(localization);
        int[] stringBytes = KeyedString.rebuildKeyedStrings(stringStream, StringHelper.localizationToCharset(localization), false);
        List<Integer> bytes = new ArrayList<>(Arrays.stream(object.toBytes(localization)).boxed().toList());
        bytes.addAll(Arrays.stream(stringBytes).boxed().toList());
        int[] fullBytes = new int[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            fullBytes[i] = bytes.get(i);
        }
        return fullBytes;
    }

    public static void writeDataObjectsInAllLocalizations(String path, Writable[] objects, final int length, final int from, final int to, final boolean optimizeStrings) {
        final Writable[] subArray = from == 0 && to == objects.length ? objects : Arrays.copyOfRange(objects, from, to);
        LOCALIZATIONS.forEach((key, value) -> {
            String localePath = GAME_FILES_ROOT + MODS_FOLDER + getLocalizationRoot(key) + path;
            int[] bytes = dataObjectsToBytes(subArray, length, from, to, key, optimizeStrings);
            FileAccessorWithMods.writeByteArrayToFile(localePath, bytes);
        });
    }

    public static void writeDataObjectsInAllLocalizations(String path, Writable[] objects, final int length, final boolean optimizeStrings) {
        writeDataObjectsInAllLocalizations(path, objects, length, 0, objects.length, optimizeStrings);
    }

    public static void writeEventStringsForAllLocalizations(String id, final boolean print) {
        EventFile event = DataAccess.getEvent(id);
        if (event == null) {
            return;
        }
        String path = "event/obj_ps3/" + id.substring(0, 2) + '/' + id + '/' + id + ".bin";
        writeStringFileForAllLocalizations(path, event.strings, print);
    }

    public static void writeEncounterStringsForAllLocalizations(String id, final boolean print) {
        EncounterFile encounter = DataAccess.getEncounter(id);
        if (encounter == null) {
            return;
        }
        String path = "battle/btl/" + id + '/' + id + ".bin";
        writeStringFileForAllLocalizations(path, encounter.strings, print);
    }

    public static void writeStringFileForAllLocalizations(String path, List<LocalizedFieldStringObject> localizedStrings, final boolean print) {
        if (print) {
            System.out.printf("Writing string file: %s%n", path);
        }
        LOCALIZATIONS.forEach((key, value) -> {
            String localePath = GAME_FILES_ROOT + MODS_FOLDER + getLocalizationRoot(key) + path;
            int[] bytes = stringsToStringFileBytes(localizedStrings, key);
            FileAccessorWithMods.writeByteArrayToFile(localePath, bytes);
        });
    }

    public static int[] stringsToStringFileBytes(List<LocalizedFieldStringObject> localizedStrings, String localization) {
        List<FieldString> strings = localizedStrings.stream().map(so -> {
            FieldString localizedContent = so.getLocalizedContent(localization);
            if (localizedContent == null) {
                System.err.println("null content");
            }
            return localizedContent;
        }).toList();
        int[] stringBytes = FieldString.rebuildFieldStrings(strings, StringHelper.localizationToCharset(localization), false);
        List<Integer> bytes = new ArrayList<>();
        strings.forEach(str -> {
            add4Bytes(bytes, str.toRegularHeaderBytes());
            add4Bytes(bytes, str.toSimplifiedHeaderBytes());
        });
        bytes.addAll(Arrays.stream(stringBytes).boxed().collect(Collectors.toList()));
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
