package main;

import model.*;
import model.spheregrid.SphereGridLayoutDataObject;
import model.spheregrid.SphereGridNodeTypeDataObject;
import model.spheregrid.SphereGridSphereTypeDataObject;
import reading.Chunk;
import reading.ChunkedFileHelper;
import reading.DataFileReader;
import reading.FileAccessorWithMods;
import atel.EncounterFile;
import atel.EventFile;
import atel.MonsterFile;
import atel.model.ScriptConstants;
import atel.model.ScriptFuncLib;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class DataReadingManager {

    public static final String PATH_FFX_ROOT = "ffx_ps2/ffx/master/";
    public static final String ORIGINALS_FOLDER = "jppc/";
    public static final String PATH_ORIGINALS_ROOT = PATH_FFX_ROOT + ORIGINALS_FOLDER;
    public static final String PATH_ORIGINALS_KERNEL = PATH_ORIGINALS_ROOT + "battle/kernel/";
    public static final String PATH_MONSTER_FOLDER = PATH_ORIGINALS_ROOT + "battle/mon/";
    public static final String PATH_ORIGINALS_ENCOUNTER = PATH_ORIGINALS_ROOT + "battle/btl/";
    public static final String PATH_INTERNATIONAL_ENCOUNTER = PATH_FFX_ROOT + "inpc/battle/btl/";
    public static final String PATH_ORIGINALS_EVENT = PATH_ORIGINALS_ROOT + "event/obj/";
    public static final String PATH_ABMAP = PATH_ORIGINALS_ROOT + "menu/abmap/";

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

    private static final boolean ALLOW_DAT_FILES = true;
    private static final boolean LOAD_EVENTS_AND_ENCOUNTERS = false;

    public static String getDefaultLocalization() {
        return DEFAULT_LOCALIZATION;
    }

    public static String getLocalizationRoot(String localization) {
        return PATH_FFX_ROOT + "new_" + localization + "pc/";
    }

    public static void initializeInternals() {
        StringHelper.initialize();
        LOCALIZATIONS.forEach((key, value) -> prepareStringMacros(getLocalizationRoot(key) + "menu/macrodic.dcp", key, false));
        ScriptConstants.initialize();
        ScriptFuncLib.initialize();
    }

    public static void readAndPrepareDataModel() {
        DataAccess.SG_SPHERE_TYPES = readSphereGridSphereTypes("battle/kernel/sphere.bin", false);
        prepareAbilities();
        DataAccess.PLAYER_CHAR_STATS = readPlayerCharStats("battle/kernel/ply_save.bin", false);
        DataAccess.GEAR_ABILITIES = readGearAbilities("battle/kernel/a_ability.bin", false);
        DataAccess.BUYABLE_GEAR = readWeaponPickups(PATH_ORIGINALS_KERNEL + "shop_arms.bin", false);
        DataAccess.WEAPON_PICKUPS = readWeaponPickups(PATH_ORIGINALS_KERNEL + "buki_get.bin", false);
        DataAccess.KEY_ITEMS = readKeyItems("battle/kernel/important.bin", false);
        DataAccess.GEAR_SHOPS = readWeaponShops(PATH_ORIGINALS_KERNEL + "arms_shop.bin", false);
        DataAccess.ITEM_SHOPS = readItemShops(PATH_ORIGINALS_KERNEL + "item_shop.bin", false);
        DataAccess.TREASURES = readTreasures(PATH_ORIGINALS_KERNEL + "takara.bin", false);
        readAllMonsters(false);
        if (LOAD_EVENTS_AND_ENCOUNTERS) {
            readAllEvents(false, false);
            readAllEncounters(false);
        }
        LOCALIZATIONS.forEach((key, name) -> DataAccess.addMonsterLocalizations(readMonsterLocalizations(key,false)));
        DataAccess.SG_NODE_TYPES = readSphereGridNodeTypes("battle/kernel/panel.bin", false);
        DataAccess.OSG_LAYOUT = readSphereGridLayout(PATH_ABMAP + "dat01.dat", PATH_ABMAP + "dat09.dat", false);
        DataAccess.SSG_LAYOUT = readSphereGridLayout(PATH_ABMAP + "dat02.dat", PATH_ABMAP + "dat10.dat", false);
        DataAccess.ESG_LAYOUT = readSphereGridLayout(PATH_ABMAP + "dat03.dat", PATH_ABMAP + "dat11.dat", false);
        DataAccess.GEAR_CUSTOMIZATIONS = readCustomizations(PATH_ORIGINALS_KERNEL + "kaizou.bin", false);
        DataAccess.AEON_CUSTOMIZATIONS = readCustomizations(PATH_ORIGINALS_KERNEL + "sum_grow.bin", false);
    }

    public static void prepareStringMacros(String filename, String localization, boolean print) {
        List<Chunk> chunks = ChunkedFileHelper.readGenericChunkedFile(filename, false, null, 16);
        if (chunks == null) {
            return;
        }
        MacroDictionaryFile macroDictionaryFile = new MacroDictionaryFile(chunks, localization);
        macroDictionaryFile.publishStrings();
        if (print) {
            System.out.println(macroDictionaryFile);
        }
    }

    public static void prepareAbilities() {
        prepareAbilitiesFromFile("battle/kernel/command.bin", 3);
        prepareAbilitiesFromFile("battle/kernel/monmagic1.bin", 4);
        prepareAbilitiesFromFile("battle/kernel/monmagic2.bin", 6);
        prepareAbilitiesFromFile("battle/kernel/item.bin", 2);
    }

    private static void prepareAbilitiesFromFile(String path, int group) {
        AbilityDataObject[] abilities = readAbilitiesFromFile(getLocalizationRoot(getDefaultLocalization()) + path, group, getDefaultLocalization(), false);
        if (abilities == null) {
            System.err.println("Failed to load abilities from " + path + " (group " + group + ')');
            return;
        }
        LOCALIZATIONS.forEach((key, name) -> {
            AbilityDataObject[] localizations = readAbilitiesFromFile(getLocalizationRoot(key) + path, group, key, false);
            if (localizations != null) {
                for (int i = 0; i < localizations.length && i < abilities.length; i++) {
                    if (abilities[i] != null && localizations[i] != null) {
                        abilities[i].setLocalizations(localizations[i]);
                    }
                }
            }
        });
        System.arraycopy(abilities, 0, DataAccess.MOVES, 0x1000 * group, abilities.length);
    }

    public static AbilityDataObject[] readAbilitiesFromFile(String filename, int group, String localization, boolean print) {
        DataFileReader<AbilityDataObject> reader = new DataFileReader<>((bytes, stringBytes) -> new AbilityDataObject(bytes, stringBytes, localization, group)) {
            @Override
            public String indexWriter(int idx) {
                return StringHelper.formatHex4(idx + group * 0x1000);
            }
        };
        List<AbilityDataObject> list = reader.readGenericDataFile(filename, print);
        if (list == null) {
            return null;
        }
        AbilityDataObject[] array = new AbilityDataObject[list.size()];
        return list.toArray(array);
    }

    public static X2AbilityDataObject[] readX2AbilitiesFromFile(String filename, String localization, boolean print) {
        DataFileReader<X2AbilityDataObject> reader = new DataFileReader<>((b, sb) -> new X2AbilityDataObject(b, sb, localization)) {
            @Override
            public String indexWriter(int idx) {
                return StringHelper.formatHex4(idx);
            }
        };
        List<X2AbilityDataObject> list = reader.readGenericX2DataFile(filename, print);
        if (list == null) {
            return null;
        }
        X2AbilityDataObject[] array = new X2AbilityDataObject[list.size()];
        return list.toArray(array);
    }

    public static KeyItemDataObject[] readKeyItems(String path, boolean print) {
        KeyItemDataObject[] items = readKeyItemsFromFile(getLocalizationRoot(getDefaultLocalization()) + path, getDefaultLocalization(), print);
        if (items == null) {
            return null;
        }
        LOCALIZATIONS.forEach((key, name) -> {
            KeyItemDataObject[] localizations = readKeyItemsFromFile(getLocalizationRoot(key) + path, key, false);
            if (localizations != null) {
                for (int i = 0; i < localizations.length && i < items.length; i++) {
                    if (items[i] != null && localizations[i] != null) {
                        items[i].setLocalizations(localizations[i]);
                    }
                }
            }
        });
        return items;
    }

    public static KeyItemDataObject[] readKeyItemsFromFile(String filename, String localization, boolean print) {
        DataFileReader<KeyItemDataObject> reader = new DataFileReader<>((int[] bytes, int[] stringBytes) -> new KeyItemDataObject(bytes, stringBytes, localization)) {
            @Override
            public String indexWriter(int idx) {
                return "A0" + StringHelper.formatHex2(idx);
            }
        };
        List<KeyItemDataObject> list = reader.readGenericDataFile(filename, print);
        if (list == null) {
            return null;
        }
        KeyItemDataObject[] array = new KeyItemDataObject[list.size()];
        return list.toArray(array);
    }

    public static PlayerCharStatDataObject[] readPlayerCharStats(String path, boolean print) {
        PlayerCharStatDataObject[] stats = readPlayerCharStatsFromFile(getLocalizationRoot(getDefaultLocalization()) + path, getDefaultLocalization(), print);
        if (stats == null) {
            return null;
        }
        LOCALIZATIONS.forEach((key, name) -> {
            PlayerCharStatDataObject[] localizations = readPlayerCharStatsFromFile(getLocalizationRoot(key) + path, key, false);
            if (localizations != null) {
                for (int i = 0; i < localizations.length && i < stats.length; i++) {
                    if (stats[i] != null && localizations[i] != null) {
                        stats[i].setLocalizations(localizations[i]);
                    }
                }
            }
        });
        return stats;
    }

    public static PlayerCharStatDataObject[] readPlayerCharStatsFromFile(String filename, String localization, boolean print) {
        DataFileReader<PlayerCharStatDataObject> reader = new DataFileReader<>((b, sb) -> new PlayerCharStatDataObject(b, sb, localization));
        List<PlayerCharStatDataObject> list = reader.readGenericDataFile(filename, print);
        if (list == null) {
            return null;
        }
        PlayerCharStatDataObject[] array = new PlayerCharStatDataObject[list.size()];
        return list.toArray(array);
    }

    public static GearAbilityDataObject[] readGearAbilities(String path, boolean print) {
        GearAbilityDataObject[] abilities = readGearAbilitiesFromFile(getLocalizationRoot(getDefaultLocalization()) + path, getDefaultLocalization(), print);
        if (abilities == null) {
            return null;
        }
        LOCALIZATIONS.forEach((key, name) -> {
            GearAbilityDataObject[] localizations = readGearAbilitiesFromFile(getLocalizationRoot(key) + path, key, false);
            if (localizations != null) {
                for (int i = 0; i < localizations.length && i < abilities.length; i++) {
                    if (abilities[i] != null && localizations[i] != null) {
                        abilities[i].setLocalizations(localizations[i]);
                    }
                }
            }
        });
        if (print) {
            Set<Integer> groups = new HashSet<>();
            List<Integer> untakenGroups = new ArrayList<>();
            for (GearAbilityDataObject gearAbility : abilities) {
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

    public static GearAbilityDataObject[] readGearAbilitiesFromFile(String filename, String localization, boolean print) {
        DataFileReader<GearAbilityDataObject> reader = new DataFileReader<>((int[] bytes, int[] stringBytes) -> new GearAbilityDataObject(bytes, stringBytes, localization)) {
            @Override
            public String indexWriter(int idx) {
                return "80" + StringHelper.formatHex2(idx);
            }
        };
        List<GearAbilityDataObject> list = reader.readGenericDataFile(filename, print);
        if (list == null) {
            return null;
        }
        GearAbilityDataObject[] array = new GearAbilityDataObject[list.size()];
        return list.toArray(array);
    }

    public static MonsterFile readMonsterFile(String filename, boolean print) {
        if (!(filename.endsWith(".bin") || (ALLOW_DAT_FILES && filename.endsWith(".dat")))) {
            return null;
        }
        List<Chunk> chunks = ChunkedFileHelper.readGenericChunkedFile(filename, print, null, true);
        if (chunks == null) {
            return null;
        }
        return new MonsterFile(chunks);
    }

    public static EncounterFile readEncounterFile(String filename, final boolean print, final boolean isInpc) {
        if (!(filename.endsWith(".bin") || (ALLOW_DAT_FILES && filename.endsWith(".dat")))) {
            return null;
        }
        List<Integer> knownLengths = new ArrayList<>();
        knownLengths.add(null);
        knownLengths.add(null);
        knownLengths.add(FormationDataObject.LENGTH);
        List<Chunk> chunks = ChunkedFileHelper.readGenericChunkedFile(filename, print, knownLengths, true);
        if (chunks == null) {
            return null;
        }
        return new EncounterFile(chunks, isInpc);
    }

    public static EventFile readEventFile(String filename, final boolean print) {
        if (!(filename.endsWith(".ebp") || (ALLOW_DAT_FILES && filename.endsWith(".dat")))) {
            return null;
        }
        /* List<Integer> knownLengths = new ArrayList<>();
        knownLengths.add(null);
        knownLengths.add(null);
        knownLengths.add(0x8C);
        knownLengths.add(null);
        knownLengths.add(0x12C); */
        List<Chunk> chunks = ChunkedFileHelper.readGenericChunkedFile(filename, print, null, false);
        if (chunks == null) {
            return null;
        }
        return new EventFile(chunks);
    }

    public static MonsterFile readMonsterFull(int index, boolean print) {
        String mIndex = String.format("m%03d", index);
        String midPath = '_' + mIndex + '/' + mIndex;
        String originalsPath = PATH_MONSTER_FOLDER + midPath + ".bin";
        MonsterFile monsterFile = readMonsterFile(originalsPath, print);
        if (monsterFile == null) {
            return null;
        }
        if (print) {
            monsterFile.parseScript();
            System.out.println(monsterFile);
        }
        return monsterFile;
    }

    public static void readAllMonsters(final boolean print) {
        for (int index = 0; index <= 360; index++) {
            MonsterFile monsterFile = readMonsterFull(index, print);
            if (monsterFile != null) {
                DataAccess.MONSTERS[index] = monsterFile;
            }
        }
    }

    public static void readAllEncounters(final boolean print) {
        File encountersFolder = FileAccessorWithMods.getRealFile(PATH_ORIGINALS_ENCOUNTER);
        if (encountersFolder.isDirectory()) {
            String[] contents = encountersFolder.list();
            if (contents != null) {
                /* if (print) {
                    System.out.println("Found encounters: " + String.join(", ", contents));
                } */
                Arrays.stream(contents).filter(sf -> !sf.startsWith(".")).sorted().forEach(sf -> {
                    EncounterFile encounterFile = readEncounterFull(sf, print);
                    if (encounterFile != null) {
                        DataAccess.ENCOUNTERS.put(sf, encounterFile);
                    }
                });
            } else {
                if (print) {
                    System.out.println("Cannot list encounters");
                }
            }
        } else {
            if (print) {
                System.out.println("Cannot locate encounters");
            }
        }
    }

    public static EncounterFile readEncounterFull(String btl, boolean print) {
        String endPath = btl + '/' + btl + ".bin";
        String originalsPath = PATH_ORIGINALS_ENCOUNTER + endPath;
        EncounterFile encounterFile = readEncounterFile(originalsPath, print, false);
        if (encounterFile == null) {
            return null;
        }
        String internationalPath = PATH_INTERNATIONAL_ENCOUNTER + endPath;
        EncounterFile internationalFile = readEncounterFile(internationalPath, false, true);
        if (internationalFile != null && internationalFile.strings != null) {
            if (encounterFile.strings == null) {
                encounterFile.strings = new ArrayList<>();
            }
            for (int i = 0; i < internationalFile.strings.size(); i++) {
                if (i < encounterFile.strings.size()) {
                    internationalFile.strings.get(i).copyInto(encounterFile.strings.get(i));
                } else {
                    encounterFile.strings.add(internationalFile.strings.get(i));
                }
            }
        }
        List<LocalizedStringObject> localizedStrings = StringHelper.readLocalizedStringFiles("battle/btl/" + endPath);
        encounterFile.addLocalizations(localizedStrings);
        if (print) {
            encounterFile.parseScript();
            System.out.println(encounterFile);
        }
        return encounterFile;
    }

    public static void readAllEvents(final boolean skipBlitzballEvents, final boolean print) {
        File eventsFolder = FileAccessorWithMods.getRealFile(PATH_ORIGINALS_EVENT);
        if (eventsFolder.isDirectory()) {
            String[] contents = eventsFolder.list();
            if (contents != null) {
                /* if (print) {
                    System.out.println("Found folders: " + String.join(", ", contents));
                } */
                List<String> eventFiles = Arrays.stream(contents)
                        .filter(sf -> !sf.startsWith(".") && (!skipBlitzballEvents || !sf.equals("bl")))
                        .sorted()
                        .map(path -> FileAccessorWithMods.getRealFile(PATH_ORIGINALS_EVENT + path))
                        .filter(f -> f.isDirectory())
                        .flatMap(f -> Arrays.stream(Objects.requireNonNull(f.list())))
                        .filter(sf -> !sf.startsWith("."))
                        .sorted()
                        .collect(Collectors.toList());
                /* if (print) {
                    System.out.println("Found events: " + String.join(", ", eventFiles));
                } */
                eventFiles.forEach(ev -> {
                    EventFile eventFile = readEventFull(ev, print);
                    DataAccess.EVENTS.put(ev, eventFile);
                });
            } else {
                if (print) {
                    System.out.println("Cannot list events");
                }
            }
        } else {
            if (print) {
                System.out.println("Cannot locate events");
            }
        }
    }

    public static EventFile readEventFull(String event, boolean print) {
        String shortened = event.substring(0, 2);
        String midPath = shortened + '/' + event + '/' + event;
        String originalsPath = PATH_ORIGINALS_EVENT + midPath + ".ebp";
        EventFile eventFile = readEventFile(originalsPath, print);
        if (eventFile == null) {
            return null;
        }
        List<LocalizedStringObject> localizedStrings = StringHelper.readLocalizedStringFiles("event/obj_ps3/" + midPath + ".bin");
        eventFile.addLocalizations(localizedStrings);
        if (print) {
            eventFile.parseScript();
            System.out.println(eventFile);
        }
        return eventFile;
    }

    public static TreasureDataObject[] readTreasures(String filename, boolean print) {
        DataFileReader<TreasureDataObject> reader = new DataFileReader<>(TreasureDataObject::new);
        List<TreasureDataObject> list = reader.readGenericDataFile(filename, print);
        if (list == null) {
            return null;
        }
        TreasureDataObject[] array = new TreasureDataObject[list.size()];
        return list.toArray(array);
    }

    public static GearDataObject[] readWeaponPickups(String filename, boolean print) {
        DataFileReader<GearDataObject> reader = new DataFileReader<>(GearDataObject::new);
        List<GearDataObject> list = reader.readGenericDataFile(filename, print);
        if (list == null) {
            return null;
        }
        GearDataObject[] array = new GearDataObject[list.size()];
        return list.toArray(array);
    }

    public static GearShopDataObject[] readWeaponShops(String filename, boolean print) {
        DataFileReader<GearShopDataObject> reader = new DataFileReader<>(GearShopDataObject::new);
        List<GearShopDataObject> list = reader.readGenericDataFile(filename, print);
        if (list == null) {
            return null;
        }
        GearShopDataObject[] array = new GearShopDataObject[list.size()];
        return list.toArray(array);
    }

    public static ItemShopDataObject[] readItemShops(String filename, boolean print) {
        DataFileReader<ItemShopDataObject> reader = new DataFileReader<>(ItemShopDataObject::new);
        List<ItemShopDataObject> list = reader.readGenericDataFile(filename, print);
        if (list == null) {
            return null;
        }
        ItemShopDataObject[] array = new ItemShopDataObject[list.size()];
        return list.toArray(array);
    }

    public static CustomizationDataObject[] readCustomizations(String filename, boolean print) {
        DataFileReader<CustomizationDataObject> reader = new DataFileReader<>(CustomizationDataObject::new);
        List<CustomizationDataObject> list = reader.readGenericDataFile(filename, print);
        if (list == null) {
            return null;
        }
        CustomizationDataObject[] array = new CustomizationDataObject[list.size()];
        return list.toArray(array);
    }

    public static MonsterLocalizationDataObject[] readMonsterLocalizations(String localization, boolean print) {
        DataFileReader<MonsterLocalizationDataObject> reader = new DataFileReader<>((b, sb) -> new MonsterLocalizationDataObject(b, sb, localization));
        int fileIndex = 0;
        File file;
        List<MonsterLocalizationDataObject> fullList = new ArrayList<>();
        do {
            fileIndex++;
            String path = getLocalizationRoot(localization) + "battle/kernel/monster" + fileIndex + ".bin";
            file = FileAccessorWithMods.resolveFile(path, false);
            if (file.exists()) {
                List<MonsterLocalizationDataObject> list = reader.readGenericDataFile(path, print);
                if (list != null) {
                    fullList.addAll(list);
                }
            }
        } while (file.exists());
        MonsterLocalizationDataObject[] array = new MonsterLocalizationDataObject[fullList.size()];
        return fullList.toArray(array);
    }

    public static SphereGridSphereTypeDataObject[] readSphereGridSphereTypes(String path, boolean print) {
        SphereGridSphereTypeDataObject[] sphereTypes = readSphereGridSphereTypesFromFile(getLocalizationRoot(getDefaultLocalization()) + path, getDefaultLocalization(), print);
        if (sphereTypes == null) {
            return null;
        }
        LOCALIZATIONS.forEach((key, name) -> {
            SphereGridSphereTypeDataObject[] localizations = readSphereGridSphereTypesFromFile(getLocalizationRoot(key) + path, key, false);
            if (localizations != null) {
                for (int i = 0; i < localizations.length && i < sphereTypes.length; i++) {
                    if (sphereTypes[i] != null && localizations[i] != null) {
                        sphereTypes[i].setLocalizations(localizations[i]);
                    }
                }
            }
        });
        return sphereTypes;
    }

    public static SphereGridSphereTypeDataObject[] readSphereGridSphereTypesFromFile(String filename, String localization, boolean print) {
        DataFileReader<SphereGridSphereTypeDataObject> reader = new DataFileReader<>((b, sb) -> new SphereGridSphereTypeDataObject(b, sb, localization));
        List<SphereGridSphereTypeDataObject> list = reader.readGenericDataFile(filename, print);
        if (list == null) {
            return null;
        }
        SphereGridSphereTypeDataObject[] array = new SphereGridSphereTypeDataObject[list.size()];
        return list.toArray(array);
    }

    public static SphereGridNodeTypeDataObject[] readSphereGridNodeTypes(String path, boolean print) {
        SphereGridNodeTypeDataObject[] nodeTypes = readSphereGridNodeTypesFromFile(getLocalizationRoot(getDefaultLocalization()) + path, getDefaultLocalization(), print);
        if (nodeTypes == null) {
            return null;
        }
        LOCALIZATIONS.forEach((key, name) -> {
            SphereGridNodeTypeDataObject[] localizations = readSphereGridNodeTypesFromFile(getLocalizationRoot(key) + path, key, false);
            if (localizations != null) {
                for (int i = 0; i < localizations.length && i < nodeTypes.length; i++) {
                    if (nodeTypes[i] != null && localizations[i] != null) {
                        nodeTypes[i].setLocalizations(localizations[i]);
                    }
                }
            }
        });
        return nodeTypes;
    }

    public static SphereGridNodeTypeDataObject[] readSphereGridNodeTypesFromFile(String filename, String localization, boolean print) {
        DataFileReader<SphereGridNodeTypeDataObject> reader = new DataFileReader<>((b, sb) -> new SphereGridNodeTypeDataObject(b, sb, localization));
        List<SphereGridNodeTypeDataObject> list = reader.readGenericDataFile(filename, print);
        if (list == null) {
            return null;
        }
        SphereGridNodeTypeDataObject[] array = new SphereGridNodeTypeDataObject[list.size()];
        return list.toArray(array);
    }

    public static SphereGridLayoutDataObject readSphereGridLayout(String layout, String contents, boolean print) {
        int[] fullContentBytes = ChunkedFileHelper.fileToBytes(FileAccessorWithMods.resolveFile(contents, false));
        int[] contentBytes = fullContentBytes != null ? Arrays.copyOfRange(fullContentBytes, 0x8, fullContentBytes.length) : null;
        int[] layoutBytes = ChunkedFileHelper.fileToBytes(FileAccessorWithMods.resolveFile(layout, false));
        if (layoutBytes == null || contentBytes == null) {
            return null;
        }
        SphereGridLayoutDataObject obj = new SphereGridLayoutDataObject(layoutBytes, contentBytes);
        if (print) {
            System.out.println(obj);
        }
        return obj;
    }
}
