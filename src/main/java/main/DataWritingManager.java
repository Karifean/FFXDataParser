package main;

import atel.EncounterFile;
import atel.EventFile;
import model.LocalizedStringObject;
import model.StringStruct;
import model.Writable;
import reading.FileAccessorWithMods;

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

    public static int[] dataObjectsToBytes(Writable[] objects, int length, int from, int to, String localization, final boolean optimizeStrings) {
        List<String> strings = Arrays.stream(objects).flatMap(ab -> ab.getStrings(localization)).collect(Collectors.toList());
        StringStruct stringStruct = StringHelper.createStringMap(strings, localization, optimizeStrings);
        List<Integer> bytes = new ArrayList<>(List.of(1, 0, 0, 0, 0, 0, 0, 0));
        add2Bytes(bytes, from);
        add2Bytes(bytes, to);
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

    public static void writeEventStringsForAllLocalizations(String id) {
        EventFile event = DataAccess.getEvent(id);
        if (event == null) {
            return;
        }
        String path = "event/obj_ps3/" + id.substring(0, 2) + '/' + id + '/' + id + ".bin";
        writeStringFileForAllLocalizations(path, event.strings, true);
    }

    public static void writeEncounterStringsForAllLocalizations(String id) {
        EncounterFile encounter = DataAccess.getEncounter(id);
        if (encounter == null) {
            return;
        }
        String path = "battle/btl/" + id + '/' + id + ".bin";
        writeStringFileForAllLocalizations(path, encounter.strings, true);
    }

    public static void writeStringFileForAllLocalizations(String path, List<LocalizedStringObject> localizedStrings, final boolean doubleHeaders) {
        LOCALIZATIONS.forEach((key, value) -> {
            String localePath = GAME_FILES_ROOT + MODS_FOLDER + getLocalizationRoot(key) + path;
            int[] bytes = stringsToStringFileBytes(localizedStrings, key, doubleHeaders);
            FileAccessorWithMods.writeByteArrayToFile(localePath, bytes);
        });
    }

    public static int[] stringsToStringFileBytes(List<LocalizedStringObject> localizedStrings, String localization, boolean doubleHeaders) {
        List<String> strings = localizedStrings.stream().map(so -> so.getLocalizedContent(localization)).toList();
        StringStruct stringStruct = StringHelper.createStringMap(strings, localization, false);
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
