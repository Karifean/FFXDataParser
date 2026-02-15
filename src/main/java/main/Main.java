package main;

import atel.AtelScriptObject;
import atel.model.*;
import gui.GuiMain;
import model.*;
import atel.EncounterFile;
import atel.EventFile;
import atel.MonsterFile;
import reading.BytesHelper;
import reading.FileAccessorWithMods;

import java.util.*;
import java.util.stream.IntStream;

import static main.DataReadingManager.*;
import static main.DataWritingManager.writeDataObjectsInAllLocalizations;
import static reading.FileAccessorWithMods.GAME_FILES_ROOT;

public class Main {

    private static final String MODE_GREP = "GREP";
    private static final String MODE_TRANSLATE = "TRANSLATE";
    private static final String MODE_READ_ALL_COMMANDS = "READ_ALL_COMMANDS";
    private static final String MODE_READ_KEY_ITEMS = "READ_KEY_ITEMS";
    private static final String MODE_READ_GEAR_ABILITIES = "READ_GEAR_ABILITIES";
    private static final String MODE_READ_TREASURES = "READ_TREASURES";
    private static final String MODE_READ_GEAR_SHOPS = "READ_GEAR_SHOPS";
    private static final String MODE_READ_ITEM_SHOPS = "READ_ITEM_SHOPS";
    private static final String MODE_READ_MISC_TEXTS = "READ_MISC_TEXTS";
    private static final String MODE_READ_MONSTER_LOCALIZATIONS = "READ_MONSTER_LOCALIZATIONS";
    private static final String MODE_READ_WEAPON_FILE = "READ_WEAPON_FILE";
    private static final String MODE_READ_STRING_FILE = "READ_STRING_FILE";
    private static final String MODE_PARSE_ATEL_FILE = "PARSE_ATEL_FILE";
    private static final String MODE_PARSE_MONSTER = "PARSE_MONSTER";
    private static final String MODE_PARSE_ALL_MONSTERS = "PARSE_ALL_MONSTERS";
    private static final String MODE_PARSE_ENCOUNTER = "PARSE_ENCOUNTER";
    private static final String MODE_PARSE_ALL_ENCOUNTERS = "PARSE_ALL_ENCOUNTERS";
    private static final String MODE_PARSE_EVENT = "PARSE_EVENT";
    private static final String MODE_PARSE_ALL_EVENTS = "PARSE_ALL_EVENTS";
    private static final String MODE_READ_SPHERE_GRID_NODE_TYPES = "READ_SPHERE_GRID_NODE_TYPES";
    private static final String MODE_READ_SPHERE_GRID_LAYOUT = "READ_SPHERE_GRID_LAYOUT";
    private static final String MODE_READ_CUSTOMIZATIONS = "READ_CUSTOMIZATIONS";
    private static final String MODE_READ_MACROS = "READ_MACROS";
    private static final String MODE_MAKE_EDITS = "MAKE_EDITS";
    private static final String MODE_MAKE_AUTOHASTE_MOD = "MAKE_AUTOHASTE_MOD";
    private static final String MODE_READ_BLITZBALL_STATS = "READ_BLITZBALL_STATS";
    private static final String MODE_READ_ENCOUNTER_TABLE = "READ_ENCOUNTER_TABLE";
    private static final String MODE_READ_MIX_TABLE = "READ_MIX_TABLE";
    private static final String MODE_READ_CTB_BASE = "READ_CTB_BASE";
    private static final String MODE_READ_PC_STATS = "READ_PC_STATS";
    private static final String MODE_READ_WEAPON_NAMES = "READ_WEAPON_NAMES";
    private static final String MODE_ADD_ATEL_SPACE = "ADD_ATEL_SPACE";
    private static final String MODE_REMAKE_SIZE_TABLE = "REMAKE_SIZE_TABLE";
    private static final String MODE_RECOMPILE = "RECOMPILE";
    private static final String MODE_GUI = "GUI";
    private static final String MODE_CUSTOM = "CUSTOM";

    private static final boolean SKIP_BLITZBALL_EVENTS_FOLDER = true;

    public static void main(String[] args) {
        String mode;
        List<String> realArgs;
        if (args.length > 1) {
            String pathRoot = args[0];
            if (!".".equals(pathRoot)) {
                GAME_FILES_ROOT = pathRoot;
            }
            mode = args[1];
            realArgs = Arrays.asList(args).subList(2, args.length);
        } else {
            GAME_FILES_ROOT = "./";
            mode = MODE_GUI;
            realArgs = List.of();
        }
        initializeInternals();
        readAndPrepareDataModel();
        switch (mode) {
            case MODE_GREP:
                String joined = String.join(" ", realArgs);
                writeGrep(joined);
                break;
            case MODE_TRANSLATE:
                String concat = String.join("", realArgs);
                translate(concat);
                break;
            case MODE_READ_ALL_COMMANDS:
                System.out.println("--- command.bin ---");
                for (int i = 0x3000; i < 0x3200; i++) {
                    CommandDataObject move = DataAccess.getCommand(i);
                    if (move != null) {
                        int offset = 0x14 + (i - 0x3000) * CommandDataObject.PCCOM_LENGTH;
                        System.out.println(String.format("%04X (Offset %04X) - ", i, offset) + move);
                    }
                }
                System.out.println("--- monmagic1.bin ---");
                for (int i = 0x4000; i < 0x4200; i++) {
                    CommandDataObject move = DataAccess.getCommand(i);
                    if (move != null) {
                        int offset = 0x14 + (i - 0x4000) * CommandDataObject.COM_LENGTH;
                        System.out.println(String.format("%04X (Offset %04X) - ", i, offset) + move);
                    }
                }
                System.out.println("--- monmagic2.bin ---");
                for (int i = 0x6000; i < 0x6200; i++) {
                    CommandDataObject move = DataAccess.getCommand(i);
                    if (move != null) {
                        int offset = 0x14 + (i - 0x6000) * CommandDataObject.COM_LENGTH;
                        System.out.println(String.format("%04X (Offset %04X) - ", i, offset) + move);
                    }
                }
                System.out.println("--- item.bin ---");
                for (int i = 0x2000; i < 0x2200; i++) {
                    CommandDataObject move = DataAccess.getCommand(i);
                    if (move != null) {
                        int offset = 0x14 + (i - 0x2000) * CommandDataObject.PCCOM_LENGTH;
                        System.out.println(String.format("%04X (Offset %04X) - ", i, offset) + move);
                    }
                }
                break;
            case MODE_PARSE_ALL_MONSTERS:
                realArgs = IntStream.range(1, 361).mapToObj(idx -> String.valueOf(idx)).toList();
            case MODE_PARSE_MONSTER:
                for (String arg : realArgs) {
                    int idx = Integer.parseInt(arg, 10);
                    int monsterIdx = idx + 0x1000;
                    String mIndexString = "m" + StringHelper.formatDec3(idx);
                    MonsterFile monsterFile = DataAccess.getMonster(monsterIdx);
                    if (monsterFile != null) {
                        monsterFile.parseScript();
                        String path = PATH_MONSTER_FOLDER + '_' + mIndexString + '/' + mIndexString + ".bin";
                        System.out.println("--- " + path + " ---");
                        String textOutputPath = PATH_TEXT_OUTPUT_ROOT + "battle/mon/" + mIndexString + ".txt";
                        String monsterFileString = monsterFile.toString();
                        FileAccessorWithMods.writeStringToFile(textOutputPath, monsterFileString);
                        System.out.println(monsterFileString);
                    } else {
                        System.err.println("Monster with idx " + arg + " not found");
                    }
                }
                break;
            case MODE_PARSE_ALL_ENCOUNTERS:
                realArgs = DataAccess.ENCOUNTERS.keySet().stream().toList();
            case MODE_PARSE_ENCOUNTER:
                for (String encounterId : realArgs) {
                    EncounterFile encounterFile = DataAccess.getEncounter(encounterId);
                    if (encounterFile != null) {
                        encounterFile.parseScript();
                        String path = PATH_ORIGINALS_ENCOUNTER + encounterId + '/' + encounterId + ".bin";
                        System.out.println("--- " + path + " ---");
                        String textOutputPath = PATH_TEXT_OUTPUT_ROOT + "battle/btl/" + encounterId + ".txt";
                        String encounterFileString = encounterFile.toString();
                        FileAccessorWithMods.writeStringToFile(textOutputPath, encounterFileString);
                        System.out.println(encounterFileString);
                    } else {
                        System.err.println("Encounter with id " + encounterId + " not found");
                    }
                }
                break;
            case MODE_PARSE_ALL_EVENTS:
                realArgs = DataAccess.EVENTS.keySet().stream().filter(eventId -> (!SKIP_BLITZBALL_EVENTS_FOLDER || !eventId.startsWith("bl"))).toList();
            case MODE_PARSE_EVENT:
                for (String eventId : realArgs) {
                    EventFile eventFile = DataAccess.getEvent(eventId);
                    if (eventFile != null) {
                        eventFile.parseScript();
                        String shortened = eventId.substring(0, 2);
                        String path = PATH_ORIGINALS_EVENT + shortened + '/' + eventId + '/' + eventId + ".ebp";
                        System.out.println("--- " + path + " ---");
                        String textOutputPath = PATH_TEXT_OUTPUT_ROOT + "event/obj/" + shortened + '/' + eventId + ".txt";
                        String encounterFileString = eventFile.toString();
                        FileAccessorWithMods.writeStringToFile(textOutputPath, encounterFileString);
                        System.out.println(encounterFileString);
                    } else {
                        System.err.println("Encounter with id " + eventId + " not found");
                    }
                }
                break;
            case MODE_PARSE_ATEL_FILE:
                for (String filename : realArgs) {
                    if (filename.contains("battle/mon")) {
                        System.out.println("Monster file: " + filename);
                        MonsterFile monsterFile = readMonsterFile(null, filename, true);
                        if (monsterFile != null) {
                            monsterFile.parseScript();
                            System.out.println(monsterFile);
                        } else {
                            System.out.println("Null");
                        }
                    } else if (filename.contains("battle/btl")) {
                        System.out.println("Encounter file: " + filename);
                        EncounterFile encounterFile = readEncounterFile(null, filename, true, filename.contains("/inpc/"));
                        if (encounterFile != null) {
                            encounterFile.parseScript();
                            System.out.println(encounterFile);
                        } else {
                            System.out.println("Null");
                        }
                    } else if (filename.contains("event/obj")) {
                        System.out.println("Event file: " + filename);
                        EventFile eventFile = readEventFile(null, filename, true);
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
            case MODE_READ_MISC_TEXTS:
                readNameDescriptionTexts("battle/kernel/arms_txt.bin", true);
                readNameDescriptionTexts("battle/kernel/config_txt.bin", true);
                readNameDescriptionTexts("battle/kernel/item_txt.bin", true);
                readNameDescriptionTexts("battle/kernel/menu_txt.bin", true);
                readNameDescriptionTexts("battle/kernel/mmain_txt.bin", true);
                readNameDescriptionTexts("battle/kernel/status_txt.bin", true);
                readNameDescriptionTexts("battle/kernel/summon_txt.bin", true);
                break;
            case MODE_READ_MONSTER_LOCALIZATIONS:
                readMonsterLocalizations(realArgs.size() > 0 ? realArgs.get(0) : DEFAULT_LOCALIZATION, true);
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
                for (int i = 0; i < DataAccess.AUTO_ABILITIES.length; i++) {
                    System.out.println(String.format("%04X (Offset %04X) - ", 0x8000 + i, 0x14 + i * AutoAbilityDataObject.LENGTH) + DataAccess.AUTO_ABILITIES[i].toString());
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
                    StringHelper.readStringFile(filename, true, DEFAULT_LOCALIZATION);
                }
                break;
            case MODE_READ_MACROS:
                prepareStringMacros(getLocalizationRoot(DEFAULT_LOCALIZATION) + "menu/macrodic.dcp", DEFAULT_LOCALIZATION, true);
                break;
            case MODE_MAKE_AUTOHASTE_MOD:
                for (int monsterIndex = 1; monsterIndex <= 346; monsterIndex++) {
                    MonsterFile monster = DataAccess.getMonster(monsterIndex + 0x1000);
                    if (monster != null) {
                        monster.monsterStatData.autoStatusesTemporal |= 0x0800;
                        monster.writeToMods(false, false);
                    }
                }
                break;
            case MODE_READ_BLITZBALL_STATS:
                EventFile event = DataAccess.getEvent("bltz0002");
                event.parseScript();
                List<StackObject> hpValues = event.eventScript.getVariable(0x126).values;
                List<StackObject> atValues = event.eventScript.getVariable(0x128).values;
                List<StackObject> enValues = event.eventScript.getVariable(0x129).values;
                List<StackObject> paValues = event.eventScript.getVariable(0x12A).values;
                List<StackObject> shValues = event.eventScript.getVariable(0x12B).values;
                List<StackObject> blValues = event.eventScript.getVariable(0x12C).values;
                List<StackObject> caValues = event.eventScript.getVariable(0x12D).values;
                List<StackObject> spValues = event.eventScript.getVariable(0x12E).values;
                for (int i = 0; i < 0x3C; i++) {
                    System.out.println("- " + StackObject.enumToString("blitzballPlayer", i) + " -");
                    int baseOffset = i * 4;
                    System.out.println("HP: " + blitzballGrowthToString(hpValues, baseOffset));
                    System.out.println("SP: " + blitzballGrowthToString(spValues, baseOffset));
                    System.out.println("AT: " + blitzballGrowthToString(atValues, baseOffset));
                    System.out.println("EN: " + blitzballGrowthToString(enValues, baseOffset));
                    System.out.println("PA: " + blitzballGrowthToString(paValues, baseOffset));
                    System.out.println("SH: " + blitzballGrowthToString(shValues, baseOffset));
                    System.out.println("BL: " + blitzballGrowthToString(blValues, baseOffset));
                    System.out.println("CA: " + blitzballGrowthToString(caValues, baseOffset));
                }
                System.out.println("Events");
                DataAccess.EVENTS.values().forEach(e -> {
                    e.parseScript();
                    Map<Integer, String> matcherMap = new HashMap<>();
                    e.eventScript.variableDeclarations.stream().forEach(d -> {
                        if (d.values.isEmpty() || d.values.size() < 100) {
                            return;
                        }
                        boolean hpEqual = true;
                        boolean spEqual = true;
                        boolean atEqual = true;
                        boolean enEqual = true;
                        boolean paEqual = true;
                        boolean shEqual = true;
                        boolean blEqual = true;
                        boolean caEqual = true;
                        int checks = 8;
                        for (int i = 0; i < 100; i++) {
                            int val = d.values.get(i).valueSigned;
                            if (hpEqual && val != hpValues.get(i).valueSigned) {
                                hpEqual = false;
                                checks--;
                            }
                            if (spEqual && val != spValues.get(i).valueSigned) {
                                spEqual = false;
                                checks--;
                            }
                            if (atEqual && val != atValues.get(i).valueSigned) {
                                atEqual = false;
                                checks--;
                            }
                            if (enEqual && val != enValues.get(i).valueSigned) {
                                enEqual = false;
                                checks--;
                            }
                            if (paEqual && val != paValues.get(i).valueSigned) {
                                paEqual = false;
                                checks--;
                            }
                            if (shEqual && val != shValues.get(i).valueSigned) {
                                shEqual = false;
                                checks--;
                            }
                            if (blEqual && val != blValues.get(i).valueSigned) {
                                blEqual = false;
                                checks--;
                            }
                            if (caEqual && val != caValues.get(i).valueSigned) {
                                caEqual = false;
                                checks--;
                            }
                            if (checks <= 0) {
                                break;
                            }
                        }
                        if (hpEqual) {
                            matcherMap.put(d.index, "BlitzballStatGrowthTable_HP");
                        }
                        if (spEqual) {
                            matcherMap.put(d.index, "BlitzballStatGrowthTable_SP");
                        }
                        if (atEqual) {
                            matcherMap.put(d.index, "BlitzballStatGrowthTable_AT");
                        }
                        if (enEqual) {
                            matcherMap.put(d.index, "BlitzballStatGrowthTable_EN");
                        }
                        if (paEqual) {
                            matcherMap.put(d.index, "BlitzballStatGrowthTable_PA");
                        }
                        if (shEqual) {
                            matcherMap.put(d.index, "BlitzballStatGrowthTable_SH");
                        }
                        if (blEqual) {
                            matcherMap.put(d.index, "BlitzballStatGrowthTable_BL");
                        }
                        if (caEqual) {
                            matcherMap.put(d.index, "BlitzballStatGrowthTable_CA");
                        }
                    });
                    if (!matcherMap.isEmpty()) {
                        StringBuilder builder = new StringBuilder("event ").append(e.scriptId);
                        matcherMap.forEach((index, stat) -> builder.append(' ').append(StringHelper.formatHex2(index)).append('=').append(stat));
                        System.out.println(builder);
                    }
                });
                break;
            case MODE_READ_ENCOUNTER_TABLE:
                readEncounterTables(PATH_ORIGINALS_KERNEL + "btl.bin", true);
                break;
            case MODE_READ_CTB_BASE:
                readCtbBase(PATH_ORIGINALS_KERNEL + "ctb_base.bin", true);
                break;
            case MODE_READ_PC_STATS:
                readPlayerCharStats("battle/kernel/ply_save.bin", true);
                readPlayerCharRom("battle/kernel/ply_rom.bin", true);
                break;
            case MODE_READ_WEAPON_NAMES:
                readWeaponNames("battle/kernel/w_name.bin", true);
                break;
            case MODE_READ_MIX_TABLE:
                readMixCombinations(PATH_ORIGINALS_KERNEL + "prepare.bin", true);
                break;
            case MODE_ADD_ATEL_SPACE:
                String type = realArgs.get(0);
                String id = realArgs.get(1);
                int workerIndex = Integer.parseInt(realArgs.get(2), 16);
                int count = Integer.parseInt(realArgs.get(3), 10);
                AtelScriptObject scriptObject;
                EventFile eventFileToSpace = null;
                EncounterFile encounterFileToSpace = null;
                MonsterFile monsterFileToSpace = null;
                if ("event".equals(type)) {
                    eventFileToSpace = DataAccess.getEvent(id);
                    eventFileToSpace.parseScript();
                    scriptObject = eventFileToSpace.eventScript;
                } else if ("encounter".equals(type)) {
                    encounterFileToSpace = DataAccess.getEncounter(id);
                    encounterFileToSpace.parseScript();
                    scriptObject = encounterFileToSpace.encounterScript;
                } else if ("monster".equals(type)) {
                    monsterFileToSpace = Objects.requireNonNull(DataAccess.getMonster(id));
                    monsterFileToSpace.parseScript();
                    scriptObject = monsterFileToSpace.monsterScript;
                } else {
                    return;
                }
                ScriptWorker worker = scriptObject.getWorker(workerIndex);
                List<ScriptJump> entryPointList = worker.entryPoints;
                int offset = scriptObject.scriptCodeLength;
                ScriptJump newEntryPoint = new ScriptJump(worker, offset, entryPointList.size(), true);
                ScriptInstruction noopInstruction = new ScriptInstruction(offset, 0x00, count);
                ScriptInstruction endInstruction = new ScriptInstruction(offset + count, 0x3C);
                newEntryPoint.targetLine = new ScriptLine(worker, offset, List.of(noopInstruction, endInstruction), List.of(newEntryPoint));
                entryPointList.add(newEntryPoint);
                if (eventFileToSpace != null) {
                    System.out.println("Added entry point " + newEntryPoint.getLabel() + " with " + count + " bytes of 00 to event " + id);
                    eventFileToSpace.writeToMods(false, false);
                } else if (encounterFileToSpace != null) {
                    System.out.println("Added entry point " + newEntryPoint.getLabel() + " with " + count + " bytes of 00 to encounter " + id);
                    encounterFileToSpace.writeToMods(false, false);
                } else {
                    System.out.println("Added entry point " + newEntryPoint.getLabel() + " with " + count + " bytes of 00 to monster " + id);
                    monsterFileToSpace.writeToMods(false, false);
                }
                break;
            case MODE_RECOMPILE:
                for (String variableId : realArgs) {
                    if (variableId.contains("_")) {
                        EncounterFile encounterFile = DataAccess.getEncounter(variableId);
                        if (encounterFile != null) {
                            encounterFile.parseScript();
                            encounterFile.writeToMods(false, false);
                        }
                    } else if (variableId.length() == 4 && variableId.startsWith("m")) {
                        int monsterIndex = Integer.parseInt(variableId.substring(1), 10);
                        MonsterFile monster = DataAccess.getMonster(monsterIndex + 0x1000);
                        if (monster != null) {
                            monster.parseScript();
                            monster.writeToMods(false, false);
                        }
                    } else {
                        EventFile eventFile = DataAccess.getEvent(variableId);
                        if (eventFile != null) {
                            eventFile.parseScript();
                            eventFile.writeToMods(false, false);
                        }
                    }
                }
                break;
            case MODE_REMAKE_SIZE_TABLE:
                DataWritingManager.remakeSizeTable();
                break;
            case MODE_CUSTOM:
                readDirectAtelScriptObject("menu/menumain.bin", true);
                // readX2AbilitiesFromFile("ffx_ps2/ffx2/master/new_uspc/battle/kernel/command.bin", "us", true);
                break;
            case MODE_MAKE_EDITS:
                writeDataObjectsInAllLocalizations("battle/kernel/ply_save.bin", DataAccess.PLAYER_CHAR_STATS, PlayerCharStatDataObject.LENGTH, false);
                System.out.println("--- ATTACKS ---");
                if (CsvEditExecutor.editCommands(true)) {
                    System.out.println("--- command.bin ---");
                    CommandDataObject[] commands = Arrays.copyOfRange(DataAccess.MOVES, 0x3000, 0x3140);
                    writeDataObjectsInAllLocalizations("battle/kernel/command.bin", commands, CommandDataObject.PCCOM_LENGTH, false);
                    System.out.println("--- monmagic1.bin ---");
                    CommandDataObject[] monmagics1 = Arrays.copyOfRange(DataAccess.MOVES, 0x4000, 0x412C);
                    writeDataObjectsInAllLocalizations("battle/kernel/monmagic1.bin", monmagics1, CommandDataObject.COM_LENGTH, false);
                    System.out.println("--- monmagic2.bin ---");
                    CommandDataObject[] monmagics2 = Arrays.copyOfRange(DataAccess.MOVES, 0x6000, 0x60F7);
                    writeDataObjectsInAllLocalizations("battle/kernel/monmagic2.bin", monmagics2, CommandDataObject.COM_LENGTH, false);
                    System.out.println("--- item.bin ---");
                    CommandDataObject[] items = Arrays.copyOfRange(DataAccess.MOVES, 0x2000, 0x2070);
                    writeDataObjectsInAllLocalizations("battle/kernel/item.bin", items, CommandDataObject.PCCOM_LENGTH, false);
                }
                System.out.println("--- AUTO ABILITIES ---");
                if (CsvEditExecutor.editAutoAbilities(true)) {
                    System.out.println("--- a_ability.bin ---");
                    writeDataObjectsInAllLocalizations("battle/kernel/a_ability.bin", DataAccess.AUTO_ABILITIES, AutoAbilityDataObject.LENGTH, false);
                }
                System.out.println("--- KEY ITEMS ---");
                if (CsvEditExecutor.editKeyItems(true)) {
                    System.out.println("--- important.bin ---");
                    writeDataObjectsInAllLocalizations("battle/kernel/important.bin", DataAccess.KEY_ITEMS, KeyItemDataObject.LENGTH, false);
                }
                System.out.println("--- MONSTERS ---");
                if (CsvEditExecutor.editMonsters(true)) {
                    DataWritingManager.writeMonsterStringsForAllLocalizations(false);
                }
                System.out.println("--- EVENTS ---");
                CsvEditExecutor.editAndSaveEventStrings(true);
                System.out.println("--- ENCOUNTERS ---");
                CsvEditExecutor.editAndSaveEncounterStrings(true);
                break;
            case MODE_GUI:
                System.out.println("UI starting");
                try {
                    GuiMain.main(args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            default:
                break;
        }
    }

    private static void writeGrep(String str) {
        final StringBuilder spaced = new StringBuilder();
        final StringBuilder byteString = new StringBuilder();
        final StringBuilder grep = new StringBuilder("grep -r \"");
        str.chars().forEach(c -> {
            List<Integer> charBytes = StringHelper.charToBytes(c, StringHelper.localizationToCharset(DEFAULT_LOCALIZATION));
            if (charBytes != null && !charBytes.isEmpty()) {
                spaced.append((char) c).append(' ');
                boolean first = true;
                for (int bc : charBytes) {
                    byteString.append(StringHelper.formatHex2(bc));
                    grep.append("\\x").append(StringHelper.formatHex2(bc));
                    if (first) {
                        first = false;
                    } else {
                        spaced.append("  ");
                    }
                }
            }
        });
        grep.append("\" .");
        System.out.println(str);
        System.out.println(spaced);
        System.out.println(byteString);
        System.out.println(grep);
    }

    private static void translate(String str) {
        int[] bytes = new int[str.length() / 2 + 1];
        for (int i = 0; i < str.length(); i += 2) {
            int idx = Integer.parseInt(str.substring(i, i+2), 16);
            bytes[i / 2] = idx == 0x00 ? 0x03 : idx;
        }
        bytes[str.length() / 2] = 0x00;
        System.out.println(StringHelper.bytesToString(bytes, DEFAULT_LOCALIZATION, true));
    }

    private static String blitzballGrowthToString(List<StackObject> values, int baseOffset) {
        float a = Float.intBitsToFloat(values.get(baseOffset).valueSigned);
        float b = Float.intBitsToFloat(values.get(baseOffset + 1).valueSigned);
        float c = Float.intBitsToFloat(values.get(baseOffset + 2).valueSigned);
        float growthType = Float.intBitsToFloat(values.get(baseOffset + 3).valueSigned);
        if (growthType == -1) {
            return "1";
        }
        List<String> parts = new ArrayList<>();
        if (a != 0) {
            parts.add(""+a);
        }
        if (b != 0) {
            String lv = growthType == 1 ? "Lv^(" + c + ")" : "Lv";
            if (b == 1.0) {
                parts.add(lv);
            } else {
                parts.add(lv + " * " + b);
            }
        }
        if (c != 0 && (growthType == 2 || growthType == 3)) {
            String sq = "Lv^2 * " + c;
            if (growthType == 3) {
                parts.add(sq);
            } else {
                return String.join(" + ", parts) + " - " + sq;
            }
        }
        return String.join(" + ", parts);
    }
}
