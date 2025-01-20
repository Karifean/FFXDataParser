package main;

import model.AbilityDataObject;
import model.GearAbilityDataObject;
import reading.FileAccessorWithMods;
import script.EncounterFile;
import script.EventFile;
import script.MonsterFile;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static main.DataReadingManager.*;
import static reading.FileAccessorWithMods.GAME_FILES_ROOT;

public class Main {

    private static final String MODE_GREP = "GREP";
    private static final String MODE_TRANSLATE = "TRANSLATE";
    private static final String MODE_READ_ALL_ABILITIES = "READ_ALL_ABILITIES";
    private static final String MODE_READ_KEY_ITEMS = "READ_KEY_ITEMS";
    private static final String MODE_READ_GEAR_ABILITIES = "READ_GEAR_ABILITIES";
    private static final String MODE_READ_TREASURES = "READ_TREASURES";
    private static final String MODE_READ_GEAR_SHOPS = "READ_GEAR_SHOPS";
    private static final String MODE_READ_ITEM_SHOPS = "READ_ITEM_SHOPS";
    private static final String MODE_READ_MONSTER_LOCALIZATIONS = "READ_MONSTER_LOCALIZATIONS";
    private static final String MODE_READ_WEAPON_FILE = "READ_WEAPON_FILE";
    private static final String MODE_READ_STRING_FILE = "READ_STRING_FILE";
    private static final String MODE_PARSE_SCRIPT_FILE = "PARSE_SCRIPT_FILE";
    private static final String MODE_PARSE_ENCOUNTER = "PARSE_ENCOUNTER";
    private static final String MODE_PARSE_ALL_ENCOUNTERS = "PARSE_ALL_ENCOUNTERS";
    private static final String MODE_PARSE_EVENT = "PARSE_EVENT";
    private static final String MODE_PARSE_ALL_EVENTS = "PARSE_ALL_EVENTS";
    private static final String MODE_PARSE_MONSTER = "PARSE_MONSTER";
    private static final String MODE_READ_SPHERE_GRID_NODE_TYPES = "READ_SPHERE_GRID_NODE_TYPES";
    private static final String MODE_READ_SPHERE_GRID_LAYOUT = "READ_SPHERE_GRID_LAYOUT";
    private static final String MODE_READ_CUSTOMIZATIONS = "READ_CUSTOMIZATIONS";
    private static final String MODE_READ_MACROS = "READ_MACROS";
    private static final String MODE_CUSTOM = "CUSTOM";

    private static final boolean SKIP_BLITZBALL_EVENTS_FOLDER = true;

    public static void main(String[] args) {
        String pathRoot = args[0];
        if (!".".equals(pathRoot)) {
            GAME_FILES_ROOT = pathRoot;
        }
        String mode = args[1];
        List<String> realArgs = Arrays.asList(args).subList(2, args.length);
        initializeInternals();
        readAndPrepareDataModel();
        switch (mode) {
            case MODE_GREP:
                String joined = String.join(" ", realArgs);
                writeGrep(joined);
                /* for (String substr : realArgs) {
                    writeGrep(substr);
                } */
                break;
            case MODE_TRANSLATE:
                String concat = String.join("", realArgs);
                translate(concat);
                break;
            case MODE_READ_ALL_ABILITIES:
                System.out.println("--- command.bin ---");
                for (int i = 0x3000; i < 0x3200; i++) {
                    AbilityDataObject move = DataAccess.getMove(i);
                    if (move != null) {
                        int offset = 0x14 + (i - 0x3000) * 0x60;
                        System.out.println(String.format("%04X (Offset %04X) - ", i, offset) + move);
                    }
                }
                System.out.println("--- monmagic1.bin ---");
                for (int i = 0x4000; i < 0x4200; i++) {
                    AbilityDataObject move = DataAccess.getMove(i);
                    if (move != null) {
                        int offset = 0x14 + (i - 0x4000) * 0x5C;
                        System.out.println(String.format("%04X (Offset %04X) - ", i, offset) + move);
                    }
                }
                System.out.println("--- monmagic2.bin ---");
                for (int i = 0x6000; i < 0x6200; i++) {
                    AbilityDataObject move = DataAccess.getMove(i);
                    if (move != null) {
                        int offset = 0x14 + (i - 0x6000) * 0x5C;
                        System.out.println(String.format("%04X (Offset %04X) - ", i, offset) + move);
                    }
                }
                System.out.println("--- item.bin ---");
                for (int i = 0x2000; i < 0x2200; i++) {
                    AbilityDataObject move = DataAccess.getMove(i);
                    if (move != null) {
                        int offset = 0x14 + (i - 0x2000) * 0x60;
                        System.out.println(String.format("%04X (Offset %04X) - ", i, offset) + move);
                    }
                }
                break;
            case MODE_PARSE_MONSTER:
                for (String arg : realArgs) {
                    int idx = Integer.parseInt(arg, 10);
                    int monsterIdx = idx + 0x1000;
                    MonsterFile monster = DataAccess.getMonster(monsterIdx);
                    if (monster != null) {
                        monster.parseScript();
                        System.out.println("Printing monster #" + arg + " [" + String.format("%04X", monsterIdx) + "h]");
                        System.out.println(monster);
                    } else {
                        System.err.println("Monster with idx " + arg + " not found");
                    }
                }
                break;
            case MODE_PARSE_ENCOUNTER:
                for (String filename : realArgs) {
                    readEncounterFull(filename, true);
                }
                break;
            case MODE_PARSE_ALL_ENCOUNTERS:
                File encountersFolder = FileAccessorWithMods.getRealFile(PATH_ORIGINALS_ENCOUNTER);
                if (encountersFolder.isDirectory()) {
                    String[] contents = encountersFolder.list();
                    if (contents != null) {
                        System.out.println("Found encounters: " + String.join(", ", contents));
                        Arrays.stream(contents).filter(sf -> !sf.startsWith(".")).sorted().forEach(sf -> readEncounterFull(sf, true));
                    } else {
                        System.out.println("Cannot list encounters");
                    }
                } else {
                    System.out.println("Cannot locate encounters");
                }
                break;
            case MODE_PARSE_EVENT:
                for (String filename : realArgs) {
                    readEventFull(filename, true);
                }
                break;
            case MODE_PARSE_ALL_EVENTS:
                File eventsFolder = FileAccessorWithMods.getRealFile(PATH_ORIGINALS_EVENT);
                if (eventsFolder.isDirectory()) {
                    String[] contents = eventsFolder.list();
                    if (contents != null) {
                        System.out.println("Found folders: " + String.join(", ", contents));
                        List<String> eventFiles = Arrays.stream(contents)
                                .filter(sf -> !sf.startsWith(".") && (!SKIP_BLITZBALL_EVENTS_FOLDER || !sf.equals("bl")))
                                .sorted()
                                .map(path -> FileAccessorWithMods.getRealFile(PATH_ORIGINALS_EVENT + path))
                                .filter(f -> f.isDirectory())
                                .flatMap(f -> Arrays.stream(Objects.requireNonNull(f.list())))
                                .filter(sf -> !sf.startsWith("."))
                                .collect(Collectors.toList());
                        System.out.println("Found events: " + String.join(", ", eventFiles));
                        eventFiles.forEach(ev -> readEventFull(ev, true));
                    } else {
                        System.out.println("Cannot list events");
                    }
                } else {
                    System.out.println("Cannot locate events");
                }
                break;
            case MODE_PARSE_SCRIPT_FILE:
                for (String filename : realArgs) {
                    if (filename.contains("battle/mon")) {
                        System.out.println("Monster file: " + filename);
                        readMonsterFile(filename, true);
                    } else if (filename.contains("battle/btl")) {
                        System.out.println("Encounter file: " + filename);
                        EncounterFile encounterFile = readEncounterFile(filename, true);
                        if (encounterFile != null) {
                            encounterFile.parseScript();
                            System.out.println(encounterFile);
                        } else {
                            System.out.println("Null");
                        }
                    } else if (filename.contains("event/obj")) {
                        System.out.println("Event file: " + filename);
                        EventFile eventFile = readEventFile(filename, true);
                        if (eventFile != null) {
                            eventFile.parseScript();
                            System.out.println(eventFile);
                        } else {
                            System.out.println("Null");
                        }
                    } else {
                        System.out.println("Failed to identify file: " + filename);
                    }
                }
                break;
            case MODE_READ_TREASURES:
                readTreasures(PATH_ORIGINALS_KERNEL + "takara.bin", true);
                break;
            case MODE_READ_GEAR_SHOPS:
                readWeaponShops(PATH_ORIGINALS_KERNEL + "arms_shop.bin", true);
                break;
            case MODE_READ_ITEM_SHOPS:
                readItemShops(PATH_ORIGINALS_KERNEL + "item_shop.bin", true);
                break;
            case MODE_READ_MONSTER_LOCALIZATIONS:
                readMonsterLocalizations(realArgs.size() > 0 ? realArgs.get(0) : DEFAULT_LOCALIZATION, true);
                /*
                for (int i = 0; i < DataAccess.MONSTERS.length; i++) {
                    MonsterFile monster = DataAccess.MONSTERS[i];
                    if (monster != null) {
                        System.out.println("--- Name ---");
                        System.out.println(monster.monsterStatData.monsterName.getAllContent());
                        System.out.println("--- Sensor Text ---");
                        System.out.println(monster.monsterStatData.monsterSensorText.getAllContent());
                        System.out.println("--- Scan Text ---");
                        System.out.println(monster.monsterStatData.monsterScanText.getAllContent());
                    }
                }
                 */
                break;
            case MODE_READ_WEAPON_FILE:
                for (String filename : realArgs) {
                    readWeaponPickups(filename, true);
                }
                break;
            case MODE_READ_KEY_ITEMS:
                readKeyItems("battle/kernel/important.bin", true);
                break;
            case MODE_READ_GEAR_ABILITIES:
                // readGearAbilitiesFromFile(PATH_LOCALIZED_KERNEL + "a_ability.bin", "us", true);
                for (int i = 0; i < DataAccess.GEAR_ABILITIES.length; i++) {
                    System.out.println(String.format("%04X (Offset %04X) - ", 0x8000 + i, 0x14 + i * GearAbilityDataObject.LENGTH) + DataAccess.GEAR_ABILITIES[i].toString());
                }
                break;
            case MODE_READ_SPHERE_GRID_NODE_TYPES:
                readSphereGridSphereTypes("battle/kernel/sphere.bin", true);
                readSphereGridNodeTypes("battle/kernel/panel.bin", true);
                break;
            case MODE_READ_SPHERE_GRID_LAYOUT:
                readSphereGridLayout(realArgs.get(0), realArgs.get(1), true);
                break;
            case MODE_READ_CUSTOMIZATIONS:
                readCustomizations(PATH_ORIGINALS_KERNEL + "kaizou.bin", true);
                readCustomizations(PATH_ORIGINALS_KERNEL + "sum_grow.bin", true);
                break;
            case MODE_READ_STRING_FILE:
                for (String filename : realArgs) {
                    StringHelper.readStringFile(filename, true);
                }
                break;
            case MODE_READ_MACROS:
                prepareStringMacros(PATH_FFX_ROOT + "new_uspc/menu/macrodic.dcp", "us", true);
                break;
            case MODE_CUSTOM:
                // readX2AbilitiesFromFile("ffx_ps2/ffx2/master/new_uspc/battle/kernel/command.bin", "us", true);
                System.out.println("--- command.bin ---");
                AbilityDataObject[] commands = Arrays.copyOfRange(DataAccess.MOVES, 0x3000, 0x3140);
                DataWritingManager.writeDataObjectsInAllLocalizations("battle/kernel/command.bin", commands, 0x60);
                System.out.println("--- monmagic1.bin ---");
                AbilityDataObject[] monmagics1 = Arrays.copyOfRange(DataAccess.MOVES, 0x4000, 0x412C);
                DataWritingManager.writeDataObjectsInAllLocalizations("battle/kernel/monmagic1.bin", monmagics1, 0x5C);
                System.out.println("--- monmagic2.bin ---");
                AbilityDataObject[] monmagics2 = Arrays.copyOfRange(DataAccess.MOVES, 0x6000, 0x60F7);
                DataWritingManager.writeDataObjectsInAllLocalizations("battle/kernel/monmagic2.bin", monmagics2, 0x5C);
                System.out.println("--- item.bin ---");
                AbilityDataObject[] items = Arrays.copyOfRange(DataAccess.MOVES, 0x2000, 0x2070);
                DataWritingManager.writeDataObjectsInAllLocalizations("battle/kernel/item.bin", items, 0x60);
                break;
            default:
                break;
        }
    }

    private static void writeGrep(String str) {
        final StringBuilder spaced = new StringBuilder();
        final StringBuilder byteString = new StringBuilder();
        final StringBuilder grep = new StringBuilder("grep -r \"");
        str.chars().forEach(c -> {
            int bc = StringHelper.charToByte(c);
            spaced.append((char) c).append(' ');
            byteString.append(Integer.toHexString(bc));
            grep.append("\\x").append(Integer.toHexString(bc));
        });
        grep.append("\" .");
        System.out.println(str);
        System.out.println(spaced);
        System.out.println(byteString);
        System.out.println(grep);
    }

    private static void translate(String str) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < str.length(); i += 2) {
            int idx = Integer.parseInt(str.substring(i, i+2), 16);
            Character chr = StringHelper.byteToChar(idx);
            if (chr != null) {
                out.append(chr);
            }
        }
        System.out.println(out);
    }
}
