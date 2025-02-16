package main;

import atel.EncounterFile;
import atel.EventFile;
import atel.MonsterFile;
import atel.model.ScriptConstants;
import atel.model.ScriptFuncLib;
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
    public static final List<String> CHARSETS = List.of("ch", "cn", "jp", "kr", "us");

    private static final boolean ALLOW_DAT_FILES = true;
    private static final boolean LOAD_EVENTS_AND_ENCOUNTERS = false;

    public static String getDefaultLocalization() {
        return DEFAULT_LOCALIZATION;
    }

    public static String getLocalizationRoot(String localization) {
        return PATH_FFX_ROOT + "new_" + localization + "pc/";
    }

    public static void initializeInternals() {
        CHARSETS.forEach((charset) -> prepareCharset(charset));
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
        AbilityDataObject[] abilities = new LocalizedDataFileReader<>((b, sb, l) -> new AbilityDataObject(b, sb, l, group), i -> new AbilityDataObject[i]) {
            @Override
            public String indexWriter(int idx) {
                return StringHelper.formatHex4(idx + group * 0x1000);
            }
        }.read(path, false);
        System.arraycopy(abilities, 0, DataAccess.MOVES, 0x1000 * group, abilities.length);
    }

    public static X2AbilityDataObject[] readX2AbilitiesFromFile(String filename, String localization, boolean print) {
        DataFileReader<X2AbilityDataObject> reader = new DataFileReader<>(X2AbilityDataObject::new, i -> new X2AbilityDataObject[i]) {
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
        return new LocalizedDataFileReader<>(KeyItemDataObject::new, i -> new KeyItemDataObject[i]) {
            @Override
            public String indexWriter(int idx) {
                return "A0" + StringHelper.formatHex2(idx);
            }
        }.read(path, print);
    }

    public static PlayerCharStatDataObject[] readPlayerCharStats(String path, boolean print) {
        return new LocalizedDataFileReader<>(PlayerCharStatDataObject::new, i -> new PlayerCharStatDataObject[i]).read(path, print);
    }

    public static GearAbilityDataObject[] readGearAbilities(String path, boolean print) {
        GearAbilityDataObject[] abilities = new LocalizedDataFileReader<>(GearAbilityDataObject::new, i -> new GearAbilityDataObject[i]) {
            @Override
            public String indexWriter(int idx) {
                return "80" + StringHelper.formatHex2(idx);
            }
        }.read(path, print);
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

    public static NameDescriptionTextObject[] readNameDescriptionTexts(String path, boolean print) {
        return new LocalizedDataFileReader<>(NameDescriptionTextObject::new, i -> new NameDescriptionTextObject[i]).read(path, print);
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
        List<LocalizedFieldStringObject> localizedStrings = StringHelper.readLocalizedStringFiles("battle/btl/" + endPath);
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
        List<LocalizedFieldStringObject> localizedStrings = StringHelper.readLocalizedStringFiles("event/obj_ps3/" + midPath + ".bin");
        eventFile.addLocalizations(localizedStrings);
        if (print) {
            eventFile.parseScript();
            System.out.println(eventFile);
        }
        return eventFile;
    }

    public static TreasureDataObject[] readTreasures(String filename, boolean print) {
        return new DataFileReader<>(TreasureDataObject::new, i -> new TreasureDataObject[i]).toArray(filename, null, print);
    }

    public static GearDataObject[] readWeaponPickups(String filename, boolean print) {
        return new DataFileReader<>(GearDataObject::new, i -> new GearDataObject[i]).toArray(filename, null, print);
    }

    public static GearShopDataObject[] readWeaponShops(String filename, boolean print) {
        return new DataFileReader<>(GearShopDataObject::new, i -> new GearShopDataObject[i]).toArray(filename, null, print);
    }

    public static ItemShopDataObject[] readItemShops(String filename, boolean print) {
        return new DataFileReader<>(ItemShopDataObject::new, i -> new ItemShopDataObject[i]).toArray(filename, null, print);
    }

    public static CustomizationDataObject[] readCustomizations(String filename, boolean print) {
        return new DataFileReader<>(CustomizationDataObject::new, i -> new CustomizationDataObject[i]).toArray(filename, null, print);
    }

    public static MonsterStatDataObject[] readMonsterLocalizations(String localization, boolean print) {
        LocalizedDataFileReader<MonsterStatDataObject> reader = new LocalizedDataFileReader<>((bytes, stringBytes, loc) -> new MonsterStatDataObject(bytes, stringBytes, loc) {
            @Override
            public String toString() {
                return buildStrings(localization);
            }
        }, i -> new MonsterStatDataObject[i]);
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

    public static SphereGridSphereTypeDataObject[] readSphereGridSphereTypes(String path, boolean print) {
        return new LocalizedDataFileReader<>(SphereGridSphereTypeDataObject::new, i -> new SphereGridSphereTypeDataObject[i]).read(path, print);
    }

    public static SphereGridNodeTypeDataObject[] readSphereGridNodeTypes(String path, boolean print) {
        return new LocalizedDataFileReader<>(SphereGridNodeTypeDataObject::new, i -> new SphereGridNodeTypeDataObject[i]).read(path, print);
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
