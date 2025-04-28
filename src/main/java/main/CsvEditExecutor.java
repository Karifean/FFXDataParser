package main;

import atel.EncounterFile;
import atel.EventFile;
import atel.MonsterFile;
import model.*;
import model.strings.LocalizedFieldStringObject;
import model.strings.LocalizedKeyedStringObject;
import reading.FileAccessorWithMods;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static main.DataReadingManager.LOCALIZATIONS;

public abstract class CsvEditExecutor {
    private static final String EDIT_CSV_ROOT = FileAccessorWithMods.RESOURCES_ROOT + "edits/";

    public static boolean editCommands(final boolean print) {
        return editItems("commands", 16, (id) -> DataAccess.getCommand(id), print);
    }

    public static boolean editAutoAbilities(final boolean print) {
        return editItems("autoAbilities", 16, (id) -> DataAccess.getAutoAbility(id), print);
    }

    public static boolean editKeyItems(final boolean print) {
        return editItems("keyItems", 16, (id) -> DataAccess.getKeyItem(id), print);
    }

    public static boolean editMonsters(final boolean print) {
        Function<Integer, Writable> monsterGetter = (id) -> {
            MonsterFile monster = DataAccess.getMonster(id + 0x1000);
            return monster != null ? monster.monsterStatData : null;
        };
        boolean sensorChanges = editItems("monsterNameSensor", 10, monsterGetter, print);
        boolean scanChanges = editItems("monsterScan", 10, monsterGetter, print);
        return sensorChanges || scanChanges;
    }

    private static boolean editItems(String file, int idRadix, Function<Integer, Writable> getter, final boolean print) {
        String path = EDIT_CSV_ROOT + file + ".csv";
        List<String[]> lines;
        try {
            lines = FileAccessorWithMods.csvToList(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        if (lines.size() <= 1) {
            return false;
        }
        String[] header = lines.get(0);
        int idCol = -1;
        int typeCol = -1;
        int copyCol = -1;
        Map<Integer, String> colToLocalization = new HashMap<>();
        for (int i = 0; i < header.length; i++) {
            String col = header[i].toLowerCase(Locale.ROOT);
            int spaceIndex = col.indexOf(' ');
            String locale = spaceIndex > 0 ? col.substring(0, spaceIndex) : "";
            if ("id".equals(col)) {
                idCol = i;
            } else if ("type".equals(col)) {
                typeCol = i;
            } else if ("direct copy".equals(col)) {
                copyCol = i;
            } else if (LOCALIZATIONS.containsKey(locale)) {
                colToLocalization.put(i, locale);
            }
        }
        if (idCol < 0 || typeCol < 0) {
            return false;
        }
        List<String[]> values = lines.subList(1, lines.size());
        for (String[] cells : values) {
            int id = Integer.parseInt(cells[idCol], idRadix);
            String type = cells[typeCol].toLowerCase(Locale.ROOT);
            Writable itemToEdit = getter.apply(id);
            if (itemToEdit != null) {
                LocalizedKeyedStringObject objToEdit = itemToEdit.getKeyedString(type);
                if (objToEdit != null) {
                    if (copyCol >= 0 && cells[copyCol] != null && !cells[copyCol].isBlank()) {
                        int copyId = Integer.parseInt(cells[copyCol], idRadix);
                        Writable itemToCopyFrom = getter.apply(copyId);
                        if (itemToCopyFrom != null) {
                            LocalizedKeyedStringObject objToCopyFrom = itemToCopyFrom.getKeyedString(type);
                            if (objToCopyFrom != null) {
                                if (print) {
                                    System.out.printf("Copying %04X into %04X%n", copyId, id);
                                }
                                objToCopyFrom.copyInto(objToEdit);
                            }
                        }
                    } else {
                        if (print) {
                            String localizedStrings = colToLocalization.keySet().stream().flatMap(i -> i < cells.length ? Stream.of(cells[i]) : Stream.empty()).collect(Collectors.joining("\",\""));
                            System.out.printf("Copying [\"%s\"] into %04X%n", localizedStrings, id);
                        }
                        colToLocalization.forEach((colIdx, localization) -> {
                            String string = colIdx < cells.length ? cells[colIdx] : null;
                            if (string != null && !string.isEmpty()) {
                                objToEdit.getLocalizedContent(localization).setString(string);
                            }
                        });
                    }
                }
            }
        }
        return true;
    }

    public static void editAndSaveEventStrings(final boolean print) {
        String path = EDIT_CSV_ROOT + "events.csv";
        List<String[]> lines;
        try {
            lines = FileAccessorWithMods.csvToList(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        if (lines.size() <= 1) {
            return;
        }
        String[] header = lines.get(0);
        int idCol = -1;
        int stringIndexCol = -1;
        Map<Integer, String> colToLocalization = new HashMap<>();
        for (int i = 0; i < header.length; i++) {
            String col = header[i].toLowerCase(Locale.ROOT);
            int spaceIndex = col.indexOf(' ');
            String locale = spaceIndex > 0 ? col.substring(0, spaceIndex) : "";
            if ("id".equals(col)) {
                idCol = i;
            } else if ("string index".equals(col)) {
                stringIndexCol = i;
            } else if (LOCALIZATIONS.containsKey(locale)) {
                colToLocalization.put(i, locale);
            }
        }
        if (idCol < 0 || stringIndexCol < 0) {
            return;
        }
        List<String[]> values = lines.subList(1, lines.size());
        Set<String> idSet = new HashSet<>();
        for (String[] cells : values) {
            String id = cells[idCol];
            int stringIndex = Integer.parseInt(cells[stringIndexCol]);
            EventFile event = DataAccess.getEvent(id);
            if (event != null) {
                idSet.add(id);
                LocalizedFieldStringObject objToEdit = event.strings.get(stringIndex);
                if (print) {
                    String localizedStrings = colToLocalization.keySet().stream().flatMap(i -> i < cells.length ? Stream.of(cells[i]) : Stream.empty()).collect(Collectors.joining("\",\""));
                    System.out.printf("Copying [\"%s\"] into %s%n", localizedStrings, id);
                }
                colToLocalization.forEach((colIdx, localization) -> {
                    String string = colIdx < cells.length ? cells[colIdx] : null;
                    if (string != null && !string.isEmpty()) {
                        objToEdit.getLocalizedContent(localization).setRegularString(string);
                    }
                });
            }
        }
        idSet.forEach(id -> DataWritingManager.writeEventStringsForAllLocalizations(id, print));
    }

    public static void editAndSaveEncounterStrings(final boolean print) {
        String path = EDIT_CSV_ROOT + "encounter.csv";
        List<String[]> lines;
        try {
            lines = FileAccessorWithMods.csvToList(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        if (lines.size() <= 1) {
            return;
        }
        String[] header = lines.get(0);
        int idCol = -1;
        int stringIndexCol = -1;
        Map<Integer, String> colToLocalization = new HashMap<>();
        for (int i = 0; i < header.length; i++) {
            String col = header[i].toLowerCase(Locale.ROOT);
            int spaceIndex = col.indexOf(' ');
            String locale = spaceIndex > 0 ? col.substring(0, spaceIndex) : "";
            if ("id".equals(col)) {
                idCol = i;
            } else if ("string index".equals(col)) {
                stringIndexCol = i;
            } else if (LOCALIZATIONS.containsKey(locale)) {
                colToLocalization.put(i, locale);
            }
        }
        if (idCol < 0 || stringIndexCol < 0) {
            return;
        }
        List<String[]> values = lines.subList(1, lines.size());
        Set<String> idSet = new HashSet<>();
        for (String[] cells : values) {
            String id = cells[idCol];
            int stringIndex = Integer.parseInt(cells[stringIndexCol]);
            EncounterFile encounter = DataAccess.getEncounter(id);
            if (encounter != null) {
                idSet.add(id);
                LocalizedFieldStringObject objToEdit = encounter.strings.get(stringIndex);
                if (print) {
                    String localizedStrings = colToLocalization.keySet().stream().flatMap(i -> i < cells.length ? Stream.of(cells[i]) : Stream.empty()).collect(Collectors.joining("\",\""));
                    System.out.printf("Copying [\"%s\"] into %s%n", localizedStrings, id);
                }
                colToLocalization.forEach((colIdx, localization) -> {
                    String string = colIdx < cells.length ? cells[colIdx] : null;
                    if (string != null && !string.isEmpty()) {
                        objToEdit.getLocalizedContent(localization).setRegularString(string);
                    }
                });
            }
        }
        idSet.forEach(id -> DataWritingManager.writeEncounterStringsForAllLocalizations(id, print));
    }
}
