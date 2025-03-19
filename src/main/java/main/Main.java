package main;

import model.CommandDataObject;
import model.GearAbilityDataObject;
import atel.EncounterFile;
import atel.EventFile;
import atel.MonsterFile;
import model.KeyItemDataObject;
import model.MonsterStatDataObject;
import reading.FileAccessorWithMods;

import java.util.Arrays;
import java.util.List;

import static main.DataReadingManager.*;
import static reading.FileAccessorWithMods.GAME_FILES_ROOT;
import static reading.FileAccessorWithMods.MODS_FOLDER;

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
            case MODE_PARSE_MONSTER:
                for (String arg : realArgs) {
                    int idx = Integer.parseInt(arg, 10);
                    int monsterIdx = idx + 0x1000;
                    MonsterFile monster = DataAccess.getMonster(monsterIdx);
                    if (monster != null) {
                        monster.parseScript();
                        System.out.println("Printing monster #" + arg + StringHelper.hex4Suffix(monsterIdx));
                        System.out.println(monster);
                    } else {
                        System.err.println("Monster with idx " + arg + " not found");
                    }
                }
                break;
            case MODE_PARSE_ALL_MONSTERS:
                readAllMonsters(true);
                break;
            case MODE_PARSE_ENCOUNTER:
                for (String filename : realArgs) {
                    readEncounterFull(filename, true);
                }
                break;
            case MODE_PARSE_ALL_ENCOUNTERS:
                readAllEncounters(true);
                break;
            case MODE_PARSE_EVENT:
                for (String filename : realArgs) {
                    readEventFull(filename, true);
                }
                break;
            case MODE_PARSE_ALL_EVENTS:
                readAllEvents(SKIP_BLITZBALL_EVENTS_FOLDER, true);
                break;
            case MODE_PARSE_ATEL_FILE:
                for (String filename : realArgs) {
                    if (filename.contains("battle/mon")) {
                        System.out.println("Monster file: " + filename);
                        MonsterFile monsterFile = readMonsterFile(-1, filename, true);
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
                    StringHelper.readStringFile(filename, true, DEFAULT_LOCALIZATION);
                }
                break;
            case MODE_READ_MACROS:
                prepareStringMacros(getLocalizationRoot(DEFAULT_LOCALIZATION) + "menu/macrodic.dcp", DEFAULT_LOCALIZATION, true);
                break;
            case MODE_MAKE_AUTOHASTE_MOD:
                for (int monsterIndex = 0; monsterIndex < MONSTER_MAX_INDEX; monsterIndex++) {
                    String mIndexString = String.format("m%03d", monsterIndex);
                    MonsterFile monster = DataAccess.getMonster(monsterIndex  + 0x1000);
                    if (monster != null) {
                        monster.monsterStatData.autoStatusesTemporal |= 0x0800;
                        String path = GAME_FILES_ROOT + MODS_FOLDER + PATH_MONSTER_FOLDER + '_' + mIndexString + '/' + mIndexString + ".bin";
                        FileAccessorWithMods.writeByteArrayToFile(path, monster.toBytes());
                    }
                }
                break;
            case MODE_CUSTOM:
                // readPlayerCharStats("battle/kernel/ply_save.bin", true);
                // readPlayerCharRom("battle/kernel/ply_rom.bin", true);
                // readWeaponNames("battle/kernel/w_name.bin", true);
                // readCtbBase(PATH_ORIGINALS_KERNEL + "ctb_base.bin", true);
                readEncounterTables(PATH_ORIGINALS_KERNEL + "btl.bin", true);
                // readMixCombinations(PATH_ORIGINALS_KERNEL + "prepare.bin", true);
                // readDirectAtelScriptObject("menu/menumain.bin", true);
                // readX2AbilitiesFromFile("ffx_ps2/ffx2/master/new_uspc/battle/kernel/command.bin", "us", true);
                break;
            case MODE_MAKE_EDITS:
                System.out.println("--- ATTACKS ---");
                if (CsvEditExecutor.editAttacks(true)) {
                    System.out.println("--- command.bin ---");
                    CommandDataObject[] commands = Arrays.copyOfRange(DataAccess.MOVES, 0x3000, 0x3140);
                    DataWritingManager.writeDataObjectsInAllLocalizations("battle/kernel/command.bin", commands, CommandDataObject.PCCOM_LENGTH, false);
                    System.out.println("--- monmagic1.bin ---");
                    CommandDataObject[] monmagics1 = Arrays.copyOfRange(DataAccess.MOVES, 0x4000, 0x412C);
                    DataWritingManager.writeDataObjectsInAllLocalizations("battle/kernel/monmagic1.bin", monmagics1, CommandDataObject.COM_LENGTH, false);
                    System.out.println("--- monmagic2.bin ---");
                    CommandDataObject[] monmagics2 = Arrays.copyOfRange(DataAccess.MOVES, 0x6000, 0x60F7);
                    DataWritingManager.writeDataObjectsInAllLocalizations("battle/kernel/monmagic2.bin", monmagics2, CommandDataObject.COM_LENGTH, false);
                    System.out.println("--- item.bin ---");
                    CommandDataObject[] items = Arrays.copyOfRange(DataAccess.MOVES, 0x2000, 0x2070);
                    DataWritingManager.writeDataObjectsInAllLocalizations("battle/kernel/item.bin", items, CommandDataObject.PCCOM_LENGTH, false);
                }
                System.out.println("--- GEAR ABILITIES ---");
                if (CsvEditExecutor.editGearAbilities(true)) {
                    System.out.println("--- a_ability.bin ---");
                    DataWritingManager.writeDataObjectsInAllLocalizations("battle/kernel/a_ability.bin", DataAccess.GEAR_ABILITIES, GearAbilityDataObject.LENGTH, false);
                }
                System.out.println("--- KEY ITEMS ---");
                if (CsvEditExecutor.editKeyItems(true)) {
                    System.out.println("--- important.bin ---");
                    DataWritingManager.writeDataObjectsInAllLocalizations("battle/kernel/important.bin", DataAccess.KEY_ITEMS, KeyItemDataObject.LENGTH, false);
                }
                System.out.println("--- MONSTERS ---");
                if (CsvEditExecutor.editMonsters(true)) {
                    MonsterStatDataObject[] statData = (MonsterStatDataObject[]) Arrays.stream(DataAccess.MONSTERS).map(mf -> mf.monsterStatData).toArray();
                    System.out.println("--- monster1.bin ---");
                    DataWritingManager.writeDataObjectsInAllLocalizations("battle/kernel/monster1.bin", statData, MonsterStatDataObject.LENGTH, 0, 100, false);
                    System.out.println("--- monster2.bin ---");
                    DataWritingManager.writeDataObjectsInAllLocalizations("battle/kernel/monster2.bin", statData, MonsterStatDataObject.LENGTH, 101, 180, false);
                    System.out.println("--- monster3.bin ---");
                    DataWritingManager.writeDataObjectsInAllLocalizations("battle/kernel/monster3.bin", statData, MonsterStatDataObject.LENGTH, 181, 365, false);
                }
                System.out.println("--- EVENTS ---");
                CsvEditExecutor.editAndSaveEventStrings(true);
                System.out.println("--- ENCOUNTERS ---");
                CsvEditExecutor.editAndSaveEncounterStrings(true);
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
            List<Integer> charBytes = StringHelper.charToBytes(c, StringHelper.localizationToCharset(DEFAULT_LOCALIZATION));
            if (charBytes != null && !charBytes.isEmpty()) {
                spaced.append((char) c).append(' ');
                boolean first = true;
                for (int bc : charBytes) {
                    byteString.append(Integer.toHexString(bc));
                    grep.append("\\x").append(Integer.toHexString(bc));
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
        System.out.println(StringHelper.bytesToString(bytes, DEFAULT_LOCALIZATION));
    }
}
