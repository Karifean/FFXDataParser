package main;

import atel.AtelScriptObject;
import atel.EncounterFile;
import atel.EventFile;
import atel.MonsterFile;
import atel.model.ScriptConstants;
import atel.model.ScriptFuncLib;
import atel.model.ScriptOpcode;
import model.*;
import model.spheregrid.SphereGridLayoutDataObject;
import model.spheregrid.SphereGridNodeTypeDataObject;
import model.spheregrid.SphereGridSphereTypeDataObject;
import model.strings.LocalizedFieldStringObject;
import model.strings.MacroDictionaryFile;
import reading.*;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static reading.BytesHelper.read4Bytes;

public class DataReadingManager {

    public static final int MONSTER_MAX_INDEX = 360;
    public static final String PATH_FFX_ROOT = "ffx_ps2/ffx/master/";
    public static final String ORIGINALS_FOLDER = "jppc/";
    public static final String PATH_ORIGINALS_ROOT = PATH_FFX_ROOT + ORIGINALS_FOLDER;
    public static final String PATH_ORIGINALS_KERNEL = PATH_ORIGINALS_ROOT + "battle/kernel/";
    public static final String PATH_MONSTER_FOLDER = PATH_ORIGINALS_ROOT + "battle/mon/";
    public static final String PATH_ORIGINALS_ENCOUNTER = PATH_ORIGINALS_ROOT + "battle/btl/";
    public static final String PATH_INTERNATIONAL_ENCOUNTER = PATH_FFX_ROOT + "inpc/battle/btl/";
    public static final String PATH_ORIGINALS_EVENT = PATH_ORIGINALS_ROOT + "event/obj/";
    public static final String PATH_ABMAP = PATH_ORIGINALS_ROOT + "menu/abmap/";
    public static final String PATH_TEXT_OUTPUT_ROOT = "target/text/";

    public static final String DEFAULT_LOCALIZATION = "us";
    public static final Map<String, String> LOCALIZATIONS = Map.of(
            "ch", "Chinese",
            "de", "German",
            "fr", "French",
            "it", "Italian",
            "jp", "Japanese",
            "kr", "Korean",
            "sp", "Spanish",
            "us", "English"
    );
    public static final List<String> CHARSETS = List.of("ch", "cn", "jp", "kr", "us");
    public static final List<String> LOCALIZATIONS_LIST = List.of("us", "de", "fr", "it", "sp", "jp", "ch", "kr");

    private static final boolean ALLOW_DAT_FILES = false;
    private static final boolean LOAD_EVENTS_AND_ENCOUNTERS = true;

    public static String getLocalizationRoot(String localization) {
        return PATH_FFX_ROOT + "new_" + localization + "pc/";
    }

    public static void initializeInternals() {
        ScriptOpcode.initialize();
        CHARSETS.forEach((charset) -> prepareCharset(charset));
        LOCALIZATIONS_LIST.forEach((key) -> prepareStringMacros(getLocalizationRoot(key) + "menu/macrodic.dcp", key, false));
        ScriptConstants.FFX.initialize();
        ScriptFuncLib.FFX.initialize();
    }

    public static void readAndPrepareDataModel() {
        DataAccess.SG_SPHERE_TYPES = readSphereGridSphereTypes("battle/kernel/sphere.bin", false);
        prepareCommands();
        DataAccess.MENUMAIN = readDirectAtelScriptObject("menu/menumain.bin", false);
        DataAccess.PLAYER_CHAR_STATS = readPlayerCharStats("battle/kernel/ply_save.bin", false);
        DataAccess.PLAYER_ROM = readPlayerCharRom("battle/kernel/ply_rom.bin", false);
        DataAccess.WEAPON_NAMES = readWeaponNames("battle/kernel/w_name.bin", false);
        DataAccess.AUTO_ABILITIES = readGearAbilities("battle/kernel/a_ability.bin", PATH_ORIGINALS_KERNEL + "arms_rate.bin", false);
        DataAccess.BUYABLE_GEAR = readWeaponPickups(PATH_ORIGINALS_KERNEL + "shop_arms.bin", false);
        DataAccess.WEAPON_PICKUPS = readWeaponPickups(PATH_ORIGINALS_KERNEL + "buki_get.bin", false);
        DataAccess.KEY_ITEMS = readKeyItems("battle/kernel/important.bin", false);
        DataAccess.GEAR_SHOPS = readWeaponShops(PATH_ORIGINALS_KERNEL + "arms_shop.bin", false);
        DataAccess.ITEM_SHOPS = readItemShops(PATH_ORIGINALS_KERNEL + "item_shop.bin", false);
        DataAccess.ARMS_TEXT = readNameDescriptionTexts("battle/kernel/arms_txt.bin", false);
        // DataAccess.BTL_TEXT = readNameDescriptionTexts("battle/kernel/btl_txt.bin", false); Wrong format
        // DataAccess.BTLEND_TEXT = readNameDescriptionTexts("battle/kernel/btlend_txt.bin", false); Wrong format
        // DataAccess.BUILD_TEXT = readNameDescriptionTexts("battle/kernel/build_txt.bin", false); Wrong format
        DataAccess.CONFIG_TEXT = readNameDescriptionTexts("battle/kernel/config_txt.bin", false);
        DataAccess.ITEM_TEXT = readNameDescriptionTexts("battle/kernel/item_txt.bin", false);
        DataAccess.MENU_TEXT = readNameDescriptionTexts("battle/kernel/menu_txt.bin", false);
        DataAccess.MMAIN_TEXT = readNameDescriptionTexts("battle/kernel/mmain_txt.bin", false);
        // DataAccess.NAME_TEXT = readNameDescriptionTexts("battle/kernel/name_txt.bin", false); Wrong format
        // DataAccess.SAVE_TEXT = readNameDescriptionTexts("battle/kernel/save_txt.bin", false); Wrong format
        DataAccess.STATS_TEXT = readNameDescriptionTexts("battle/kernel/status_txt.bin", false);
        DataAccess.SUMMON_TEXT = readNameDescriptionTexts("battle/kernel/summon_txt.bin", false);
        DataAccess.TREASURES = readTreasures(PATH_ORIGINALS_KERNEL + "takara.bin", false);
        DataAccess.MIX_COMBINATIONS = readMixCombinations(PATH_ORIGINALS_KERNEL + "prepare.bin", false);
        DataAccess.CTB_BASE = readCtbBase(PATH_ORIGINALS_KERNEL + "ctb_base.bin", false);

        ScriptCustomDeclarations customDeclarations = readScriptCustomDeclarations();
        readAllMonsters(customDeclarations.monsterMap);
        if (LOAD_EVENTS_AND_ENCOUNTERS) {
            readAllEvents(customDeclarations.eventMap, false);
            readAllEncounters(customDeclarations.encounterMap);
        }
        readEncounterTables(PATH_ORIGINALS_KERNEL + "btl.bin", false);
        addAllMonsterLocalizations();
        DataAccess.SG_NODE_TYPES = readSphereGridNodeTypes("battle/kernel/panel.bin", false);
        DataAccess.OSG_LAYOUT = readSphereGridLayout(PATH_ABMAP + "dat01.dat", PATH_ABMAP + "dat09.dat", false);
        DataAccess.SSG_LAYOUT = readSphereGridLayout(PATH_ABMAP + "dat02.dat", PATH_ABMAP + "dat10.dat", false);
        DataAccess.ESG_LAYOUT = readSphereGridLayout(PATH_ABMAP + "dat03.dat", PATH_ABMAP + "dat11.dat", false);
        DataAccess.GEAR_CUSTOMIZATIONS = readCustomizations(PATH_ORIGINALS_KERNEL + "kaizou.bin", false);
        DataAccess.AEON_CUSTOMIZATIONS = readCustomizations(PATH_ORIGINALS_KERNEL + "sum_grow.bin", false);
    }

    public static void prepareCharset(String charset) {
        String path = PATH_ORIGINALS_ROOT + "ffx_encoding/ffxsjistbl_" + charset + ".bin";
        File file = FileAccessorWithMods.resolveFile(path, false);
        byte[] allBytes = null;
        try (DataInputStream data = FileAccessorWithMods.readFile(file)) {
            allBytes = data.readAllBytes();
        } catch (IOException ignored) {}
        if (allBytes == null) {
            return;
        }
        String str = new String(allBytes, StandardCharsets.UTF_8);
        Map<Integer, Character> map = new HashMap<>();
        Map<Character, Integer> reverseMap = new HashMap<>();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            int idx = i + 0x30;
            map.put(idx, c);
            reverseMap.putIfAbsent(c, idx);
        }
        StringHelper.setCharMap(charset, map, reverseMap);
    }

    public static void prepareStringMacros(String filename, String localization, boolean print) {
        int[] bytes = BytesHelper.fileToBytes(filename, false);
        if (bytes == null) {
            return;
        }
        MacroDictionaryFile macroDictionaryFile = new MacroDictionaryFile(bytes, localization);
        macroDictionaryFile.publishStrings();
        if (print) {
            System.out.println(macroDictionaryFile);
        }
    }

    public static void prepareCommands() {
        prepareCommandsFromFile("battle/kernel/command.bin", 3);
        prepareCommandsFromFile("battle/kernel/monmagic1.bin", 4);
        prepareCommandsFromFile("battle/kernel/monmagic2.bin", 6);
        prepareCommandsFromFile("battle/kernel/item.bin", 2);

        Integer[] prices = new DataFileReader<>((b, sb, locale) -> read4Bytes(b, 0), Integer[]::new).toArray(PATH_ORIGINALS_KERNEL + "item_rate.bin", null, false);
        if (prices != null && prices.length > 0) {
            for (int i = 0; i < prices.length; i++) {
                CommandDataObject item = DataAccess.getCommand(0x2000 + i);
                if (item != null) {
                    item.gilPrice = prices[i];
                }
            }
        }
    }

    private static void prepareCommandsFromFile(String path, int group) {
        CommandDataObject[] abilities = new LocalizedDataFileReader<>((b, sb, l) -> new CommandDataObject(b, sb, l, group), CommandDataObject[]::new) {
            @Override
            public String indexWriter(int idx) {
                return StringHelper.formatHex4(idx + group * 0x1000);
            }
        }.read(path, false);
        System.arraycopy(abilities, 0, DataAccess.COMMANDS, 0x1000 * group, abilities.length);
    }

    public static X2AbilityDataObject[] readX2AbilitiesFromFile(String filename, String localization, boolean print) {
        DataFileReader<X2AbilityDataObject> reader = new DataFileReader<>(X2AbilityDataObject::new, X2AbilityDataObject[]::new) {
            @Override
            public String indexWriter(int idx) {
                return StringHelper.formatHex4(idx);
            }
        };
        List<X2AbilityDataObject> list = reader.readGenericX2DataFile(filename, localization, print);
        if (list == null) {
            return null;
        }
        X2AbilityDataObject[] array = new X2AbilityDataObject[list.size()];
        return list.toArray(array);
    }

    public static KeyItemDataObject[] readKeyItems(String path, boolean print) {
        return new LocalizedDataFileReader<>(KeyItemDataObject::new, KeyItemDataObject[]::new) {
            @Override
            public String indexWriter(int idx) {
                return "A0" + StringHelper.formatHex2(idx);
            }
        }.read(path, print);
    }

    public static PlayerCharStatDataObject[] readPlayerCharStats(String path, boolean print) {
        return new LocalizedDataFileReader<>(PlayerCharStatDataObject::new, PlayerCharStatDataObject[]::new).read(path, print);
    }

    public static PlayerRomDataObject[] readPlayerCharRom(String path, boolean print) {
        return new LocalizedDataFileReader<>(PlayerRomDataObject::new, PlayerRomDataObject[]::new).read(path, print);
    }

    public static WeaponNameDataObject[] readWeaponNames(String path, boolean print) {
        return new LocalizedDataFileReader<>(WeaponNameDataObject::new, WeaponNameDataObject[]::new).read(path, print);
    }

    public static AutoAbilityDataObject[] readGearAbilities(String path, String pricesPath, boolean print) {
        AutoAbilityDataObject[] abilities = new LocalizedDataFileReader<>(AutoAbilityDataObject::new, AutoAbilityDataObject[]::new) {
            @Override
            public String indexWriter(int idx) {
                return "80" + StringHelper.formatHex2(idx);
            }
        }.read(path, print);
        Integer[] prices = new DataFileReader<>((b, sb, locale) -> read4Bytes(b, 0), Integer[]::new).toArray(pricesPath, null, print);
        if (prices != null && prices.length > 0) {
            for (int i = 0; i < prices.length && i < abilities.length; i++) {
                abilities[i].gilPrice = prices[i];
            }
        }
        if (print) {
            Set<Integer> groups = new HashSet<>();
            List<Integer> untakenGroups = new ArrayList<>();
            for (AutoAbilityDataObject gearAbility : abilities) {
                groups.add(gearAbility.groupIndex);
            }
            for (int i = 0; i <= 0x82; i++) {
                if (!groups.contains(i)) {
                    untakenGroups.add(i);
                }
            }
            System.out.println("Untaken Groups: " + untakenGroups.stream().map(i -> ""+i).collect(Collectors.joining(",")));
        }
        return abilities;
    }

    public static NameDescriptionTextObject[] readNameDescriptionTexts(String path, boolean print) {
        return new LocalizedDataFileReader<>(NameDescriptionTextObject::new, NameDescriptionTextObject[]::new).read(path, print);
    }

    public static AtelScriptObject readDirectAtelScriptObject(String path, boolean print) {
        AtelScriptObject scriptObject = new AtelScriptObject(BytesHelper.fileToBytes(PATH_ORIGINALS_ROOT + path, print), null);
        List<LocalizedFieldStringObject> localizedStrings = StringHelper.readLocalizedStringFiles(path);
        scriptObject.setStrings(localizedStrings);
        if (print) {
            scriptObject.parseScript();
            System.out.println(scriptObject);
        }
        return scriptObject;
    }

    public static void readEncounterTables(String filename, boolean print) {
        int[] bytes = BytesHelper.fileToBytes(filename, print);
        if (bytes == null) {
            return;
        }
        List<Chunk> chunks = BytesHelper.bytesToChunks(bytes, 2, 4);
        int[] headerBytes = chunks.get(0).bytes;
        int[] payloadBytes = chunks.get(1).bytes;
        int tableCount = headerBytes.length / FieldEncounterTableDataObject.HEADER_LENGTH;
        FieldEncounterTableDataObject[] fieldTables = new FieldEncounterTableDataObject[tableCount];
        for (int i = 0; i < tableCount; i++) {
            fieldTables[i] = new FieldEncounterTableDataObject(headerBytes, payloadBytes, i * FieldEncounterTableDataObject.HEADER_LENGTH);
            if (print) {
                System.out.println("Table " + StringHelper.formatHex2(i) + ": " + fieldTables[i]);
            }
        }
        DataAccess.ENCOUNTER_TABLES = fieldTables;
    }

    public static MonsterFile readMonsterFile(Integer monsterIndex, String filename, boolean print) {
        if (!(filename.endsWith(".bin") || (ALLOW_DAT_FILES && filename.endsWith(".dat")))) {
            return null;
        }
        int[] bytes = BytesHelper.fileToBytes(filename, print);
        if (bytes == null) {
            return null;
        }
        return new MonsterFile(monsterIndex, bytes);
    }

    public static EncounterFile readEncounterFile(String encounterId, String filename, final boolean print, final boolean isInpc) {
        if (!(filename.endsWith(".bin") || (ALLOW_DAT_FILES && filename.endsWith(".dat")))) {
            return null;
        }
        int[] bytes = BytesHelper.fileToBytes(filename, print);
        if (bytes == null) {
            return null;
        }
        return new EncounterFile(encounterId, bytes, isInpc);
    }

    public static EventFile readEventFile(String eventId, String filename, final boolean print) {
        if (!(filename.endsWith(".ebp") || (ALLOW_DAT_FILES && filename.endsWith(".dat")))) {
            return null;
        }
        int[] bytes = BytesHelper.fileToBytes(filename, print);
        if (bytes == null) {
            return null;
        }
        return new EventFile(eventId, bytes);
    }

    public static void readAllMonsters(Map<String, List<String>> declarations) {
        for (int index = 0; index <= MONSTER_MAX_INDEX; index++) {
            MonsterFile monsterFile = readMonsterFull(index, declarations);
            if (monsterFile != null) {
                try {
                    if (Integer.parseInt(monsterFile.scriptId) != index) {
                        System.err.printf("mismatch! %d vs %s%n", index, monsterFile.scriptId);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                DataAccess.MONSTERS[index] = monsterFile;
            }
        }
    }

    public static MonsterFile readMonsterFull(int monsterIndex, Map<String, List<String>> declarations) {
        String mIndexString = "m" + StringHelper.formatDec3(monsterIndex);
        String midPath = '_' + mIndexString + '/' + mIndexString;
        String originalsPath = PATH_MONSTER_FOLDER + midPath + ".bin";
        MonsterFile monsterFile = readMonsterFile(monsterIndex, originalsPath, false);
        if (monsterFile == null) {
            return null;
        }
        monsterFile.monsterScript.addVariableNamings(declarations.get(monsterFile.scriptId));
        addVariableNamings(monsterFile.monsterScript, PATH_MONSTER_FOLDER + midPath + ".dcl.csv");
        return monsterFile;
    }

    public static void readAllEncounters(Map<String, List<String>> declarations) {
        File encountersFolder = FileAccessorWithMods.getRealFile(PATH_ORIGINALS_ENCOUNTER);
        if (encountersFolder.isDirectory()) {
            String[] contents = encountersFolder.list();
            if (contents != null) {
                Stream<String> encounterFiles = Arrays.stream(contents).filter(sf -> !sf.startsWith(".")).sorted();
                encounterFiles.forEach(encounterId -> {
                    EncounterFile encounterFile = readEncounterFull(encounterId, declarations);
                    if (encounterFile != null) {
                        if (!encounterId.equals(encounterFile.scriptId)) {
                            System.err.printf("mismatch! %s vs %s%n", encounterId, encounterFile.scriptId);
                        }
                        DataAccess.ENCOUNTERS.put(encounterFile.scriptId, encounterFile);
                    }
                });
            }
        }
    }

    public static EncounterFile readEncounterFull(String encounterId, Map<String, List<String>> declarations) {
        String midPath = encounterId + '/' + encounterId;
        String endPath = midPath + ".bin";
        String originalsPath = PATH_ORIGINALS_ENCOUNTER + endPath;
        EncounterFile encounterFile = readEncounterFile(encounterId, originalsPath, false, false);
        if (encounterFile == null) {
            return null;
        }
        String internationalPath = PATH_INTERNATIONAL_ENCOUNTER + endPath;
        EncounterFile internationalFile = readEncounterFile(encounterId, internationalPath, false, true);
        if (internationalFile != null) {
            encounterFile.addLocalizations(internationalFile.strings);
        }
        List<LocalizedFieldStringObject> localizedStrings = StringHelper.readLocalizedStringFiles("battle/btl/" + endPath);
        encounterFile.addLocalizations(localizedStrings);
        encounterFile.encounterScript.addVariableNamings(declarations.get(encounterFile.scriptId));
        addVariableNamings(encounterFile.encounterScript, PATH_ORIGINALS_ENCOUNTER + midPath + ".dcl.csv");
        return encounterFile;
    }

    public static void readAllEvents(Map<String, List<String>> declarations, final boolean skipBlitzballEvents) {
        File eventsFolder = FileAccessorWithMods.getRealFile(PATH_ORIGINALS_EVENT);
        if (eventsFolder.isDirectory()) {
            String[] contents = eventsFolder.list();
            if (contents != null) {
                Stream<String> eventFiles = Arrays.stream(contents)
                        .filter(sf -> !sf.startsWith(".") && (!skipBlitzballEvents || !sf.equals("bl")))
                        .map(path -> FileAccessorWithMods.getRealFile(PATH_ORIGINALS_EVENT + path))
                        .filter(f -> f.isDirectory())
                        .flatMap(f -> Arrays.stream(Objects.requireNonNullElse(f.list(), new String[0])))
                        .filter(sf -> !sf.startsWith("."))
                        .sorted();
                eventFiles.forEach(eventId -> {
                    EventFile eventFile = readEventFull(eventId, declarations);
                    if (eventFile != null) {
                        if (!eventId.equals(eventFile.scriptId)) {
                            System.err.printf("mismatch! %s vs %s%n", eventId, eventFile.scriptId);
                        }
                        DataAccess.EVENTS.put(eventFile.scriptId, eventFile);
                    }
                });
            }
        }
    }

    public static EventFile readEventFull(String eventId, Map<String, List<String>> declarations) {
        String shortened = eventId.substring(0, 2);
        String midPath = shortened + '/' + eventId + '/' + eventId;
        String originalsPath = PATH_ORIGINALS_EVENT + midPath + ".ebp";
        EventFile eventFile = readEventFile(eventId, originalsPath, false);
        if (eventFile == null) {
            return null;
        }
        List<LocalizedFieldStringObject> localizedStrings = StringHelper.readLocalizedStringFiles("event/obj_ps3/" + midPath + ".bin");
        eventFile.addLocalizations(localizedStrings);
        eventFile.eventScript.addVariableNamings(declarations.get(eventFile.scriptId));
        addVariableNamings(eventFile.eventScript, PATH_ORIGINALS_EVENT + midPath + ".dcl.csv");
        return eventFile;
    }

    public static TreasureDataObject[] readTreasures(String filename, boolean print) {
        return new DataFileReader<>(TreasureDataObject::new, TreasureDataObject[]::new).toArray(filename, null, print);
    }

    public static MixCombinationDataObject[] readMixCombinations(String filename, boolean print) {
        MixCombinationDataObject[] objects = new DataFileReader<>(MixCombinationDataObject::new, MixCombinationDataObject[]::new).toArray(filename, null, false);
        if (objects != null) {
            for (int i = 0; i < objects.length; i++) {
                objects[i].mixOrigin = i + 0x2000;
                if (print) {
                    System.out.println(objects[i]);
                }
            }
        }
        return objects;
    }

    public static CtbBaseDataObject[] readCtbBase(String filename, boolean print) {
        return new DataFileReader<>(CtbBaseDataObject::new, CtbBaseDataObject[]::new) {
            @Override
            public String indexWriter(int idx) {
                return "Agility " + (idx + 1);
            }
        }.toArray(filename, null, print);
    }

    public static GearDataObject[] readWeaponPickups(String filename, boolean print) {
        return new DataFileReader<>(GearDataObject::new, GearDataObject[]::new).toArray(filename, null, print);
    }

    public static GearShopDataObject[] readWeaponShops(String filename, boolean print) {
        return new DataFileReader<>(GearShopDataObject::new, GearShopDataObject[]::new).toArray(filename, null, print);
    }

    public static ItemShopDataObject[] readItemShops(String filename, boolean print) {
        return new DataFileReader<>(ItemShopDataObject::new, ItemShopDataObject[]::new).toArray(filename, null, print);
    }

    public static CustomizationDataObject[] readCustomizations(String filename, boolean print) {
        return new DataFileReader<>(CustomizationDataObject::new, CustomizationDataObject[]::new).toArray(filename, null, print);
    }

    public static MonsterStatDataObject[] readMonsterLocalizations(String localization, boolean print) {
        LocalizedDataFileReader<MonsterStatDataObject> reader = new LocalizedDataFileReader<>((bytes, stringBytes, loc) -> new MonsterStatDataObject(bytes, stringBytes, loc) {
            @Override
            public String toString() {
                return buildStrings(localization);
            }
        }, MonsterStatDataObject[]::new);
        int fileIndex = 0;
        File file;
        List<MonsterStatDataObject> fullList = new ArrayList<>();
        do {
            fileIndex++;
            String path = getLocalizationRoot(localization) + "battle/kernel/monster" + fileIndex + ".bin";
            file = FileAccessorWithMods.resolveFile(path, false);
            if (file.exists()) {
                List<MonsterStatDataObject> list = reader.toList(path, localization, print);
                if (list != null) {
                    fullList.addAll(list);
                }
            }
        } while (file.exists());
        MonsterStatDataObject[] array = new MonsterStatDataObject[fullList.size()];
        return fullList.toArray(array);
    }

    public static void addAllMonsterLocalizations() {
        LOCALIZATIONS.forEach((key, name) -> DataAccess.addMonsterLocalizations(readMonsterLocalizations(key,false)));
    }

    public static SphereGridSphereTypeDataObject[] readSphereGridSphereTypes(String path, boolean print) {
        return new LocalizedDataFileReader<>(SphereGridSphereTypeDataObject::new, SphereGridSphereTypeDataObject[]::new).read(path, print);
    }

    public static SphereGridNodeTypeDataObject[] readSphereGridNodeTypes(String path, boolean print) {
        return new LocalizedDataFileReader<>(SphereGridNodeTypeDataObject::new, SphereGridNodeTypeDataObject[]::new).read(path, print);
    }

    public static SphereGridLayoutDataObject readSphereGridLayout(String layout, String contents, boolean print) {
        int[] fullContentBytes = BytesHelper.fileToBytes(FileAccessorWithMods.resolveFile(contents, false));
        int[] contentBytes = fullContentBytes != null ? Arrays.copyOfRange(fullContentBytes, 0x8, fullContentBytes.length) : null;
        int[] layoutBytes = BytesHelper.fileToBytes(FileAccessorWithMods.resolveFile(layout, false));
        if (layoutBytes == null || contentBytes == null) {
            return null;
        }
        SphereGridLayoutDataObject obj = new SphereGridLayoutDataObject(layoutBytes, contentBytes);
        if (print) {
            System.out.println(obj);
        }
        return obj;
    }

    private static ScriptCustomDeclarations readScriptCustomDeclarations() {
        File declarationsFile = new File(FileAccessorWithMods.RESOURCES_ROOT + "declarations.txt");
        Map<String, List<String>> eventMap = new HashMap<>();
        Map<String, List<String>> encounterMap = new HashMap<>();
        Map<String, List<String>> monsterMap = new HashMap<>();
        ScriptCustomDeclarations declarations = new ScriptCustomDeclarations(eventMap, encounterMap, monsterMap);
        try {
            List<String> declarationLines = FileAccessorWithMods.textFileToLineList(declarationsFile);
            for (String line : declarationLines) {
                String[] split = line.split(" ");
                String type = split[0];
                Map<String, List<String>> targetMap;
                if ("event".equals(type)) {
                    targetMap = eventMap;
                } else if ("encounter".equals(type)) {
                    targetMap = encounterMap;
                } else if ("monster".equals(type)) {
                    targetMap = monsterMap;
                } else {
                    continue;
                }
                String key = split[1];
                List<String> list = targetMap.computeIfAbsent(key, (v) -> new ArrayList<>());
                list.addAll(Arrays.asList(split).subList(2, split.length));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return declarations;
    }

    private static void addVariableNamings(AtelScriptObject script, String declarationsPath) {
        File file = FileAccessorWithMods.getModdedFile(declarationsPath);
        if (!file.exists()) {
            return;
        }
        try {
            List<String[]> namingLines = FileAccessorWithMods.csvToList(file);
            script.addVariableNamingsCsv(namingLines);
        } catch (IOException | NumberFormatException e) {
            System.err.println("Got Exception in reading name declarations");
            e.printStackTrace();
        }
    }

    private record ScriptCustomDeclarations(Map<String, List<String>> eventMap, Map<String, List<String>> encounterMap, Map<String, List<String>> monsterMap) {}
}
