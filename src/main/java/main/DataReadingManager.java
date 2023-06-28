package main;

import model.*;
import reading.Chunk;
import reading.ChunkedFileHelper;
import reading.DataFileReader;
import reading.FileAccessorWithMods;
import script.EncounterFile;
import script.EventFile;
import script.MonsterFile;
import script.model.ScriptConstants;
import script.model.ScriptFuncLib;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class DataReadingManager {

    public static final String PATH_FFX_ROOT = "ffx_ps2/ffx/master/";
    public static final String ORIGINALS_FOLDER = "jppc/";
    public static final String LOCALIZED_FOLDER = "new_uspc/";
    public static final String PATH_ORIGINALS_ROOT = PATH_FFX_ROOT + ORIGINALS_FOLDER;
    public static final String PATH_LOCALIZED_ROOT = PATH_FFX_ROOT + LOCALIZED_FOLDER;
    public static final String PATH_ORIGINALS_KERNEL = PATH_ORIGINALS_ROOT + "battle/kernel/";
    public static final String PATH_LOCALIZED_KERNEL = PATH_LOCALIZED_ROOT + "battle/kernel/";
    public static final String PATH_MONSTER_FOLDER = PATH_ORIGINALS_ROOT + "battle/mon/";
    public static final String PATH_ORIGINALS_ENCOUNTER = PATH_ORIGINALS_ROOT + "battle/btl/";
    public static final String PATH_LOCALIZED_ENCOUNTER = PATH_LOCALIZED_ROOT + "battle/btl/";
    public static final String PATH_ORIGINALS_EVENT = PATH_ORIGINALS_ROOT + "event/obj/";
    public static final String PATH_LOCALIZED_EVENT = PATH_LOCALIZED_ROOT + "event/obj_ps3/";
    public static final String PATH_SKILL_TABLE_3 = PATH_LOCALIZED_KERNEL + "command.bin"; // "FILE07723.dat"; // "command.bin"; //
    public static final String PATH_SKILL_TABLE_4 = PATH_LOCALIZED_KERNEL + "monmagic1.bin"; // "FILE07740.dat"; // "monmagic1.bin"; //
    public static final String PATH_SKILL_TABLE_6 = PATH_LOCALIZED_KERNEL + "monmagic2.bin"; // "FILE07741.dat"; // "monmagic2.bin"; //
    public static final String PATH_SKILL_TABLE_2 = PATH_LOCALIZED_KERNEL + "item.bin"; // "FILE07734.dat"; // "item.bin"; //

    private static final boolean SKIP_BLITZBALL_EVENTS_FOLDER = true;

    public static void readAndPrepareDataModel() {
        StringHelper.initialize();
        ScriptConstants.initialize();
        ScriptFuncLib.initialize();
        prepareAbilities();
        DataAccess.GEAR_ABILITIES = readGearAbilitiesFromFile(PATH_LOCALIZED_KERNEL + "a_ability.bin", false);
        DataAccess.BUYABLE_GEAR = readWeaponPickups(PATH_ORIGINALS_KERNEL + "shop_arms.bin", false);
        DataAccess.WEAPON_PICKUPS = readWeaponPickups(PATH_ORIGINALS_KERNEL + "buki_get.bin", false);
        DataAccess.KEY_ITEMS = readKeyItemsFromFile(PATH_LOCALIZED_KERNEL + "important.bin", false);
        DataAccess.GEAR_SHOPS = readWeaponShops(PATH_ORIGINALS_KERNEL + "arms_shop.bin", false);
        DataAccess.ITEM_SHOPS = readItemShops(PATH_ORIGINALS_KERNEL + "item_shop.bin", false);
        DataAccess.TREASURES = readTreasures(PATH_ORIGINALS_KERNEL + "takara.bin", false);
        readMonsterFile(PATH_MONSTER_FOLDER, false);
        DataAccess.addMonsterLocalizations(readMonsterLocalizations(false));
    }

    public static void prepareAbilities() {
        prepareAbilitiesFromFile(PATH_SKILL_TABLE_3, 3);
        prepareAbilitiesFromFile(PATH_SKILL_TABLE_4, 4);
        prepareAbilitiesFromFile(PATH_SKILL_TABLE_6, 6);
        prepareAbilitiesFromFile(PATH_SKILL_TABLE_2, 2);
    }

    private static void prepareAbilitiesFromFile(String filename, int group) {
        AbilityDataObject[] abilities = readAbilitiesFromFile(filename, group, false);
        if (abilities == null) {
            System.err.println("Failed to load abilities from " + filename + " (group " + group + ')');
            return;
        }
        System.arraycopy(abilities, 0, DataAccess.MOVES, 0x1000 * group, abilities.length);
    }

    public static AbilityDataObject[] readAbilitiesFromFile(String filename, int group, boolean print) {
        DataFileReader<AbilityDataObject> reader = new DataFileReader<>() {
            @Override
            public AbilityDataObject objectCreator(int[] bytes, int[] stringBytes) {
                return new AbilityDataObject(bytes, stringBytes);
            }

            @Override
            public String indexWriter(int idx) {
                return String.format("%04X", idx + group * 0x1000);
            }
        };
        List<AbilityDataObject> list = reader.readGenericDataFile(filename, print);
        if (list == null) {
            return null;
        }
        AbilityDataObject[] array = new AbilityDataObject[list.size()];
        return list.toArray(array);
    }

    public static KeyItemDataObject[] readKeyItemsFromFile(String filename, boolean print) {
        DataFileReader<KeyItemDataObject> reader = new DataFileReader<>() {
            @Override
            public KeyItemDataObject objectCreator(int[] bytes, int[] stringBytes) {
                return new KeyItemDataObject(bytes, stringBytes);
            }

            @Override
            public String indexWriter(int idx) {
                return "A0" + String.format("%02X", idx);
            }
        };
        List<KeyItemDataObject> list = reader.readGenericDataFile(filename, print);
        if (list == null) {
            return null;
        }
        KeyItemDataObject[] array = new KeyItemDataObject[list.size()];
        return list.toArray(array);
    }

    public static GearAbilityDataObject[] readGearAbilitiesFromFile(String filename, boolean print) {
        DataFileReader<GearAbilityDataObject> reader = new DataFileReader<>() {
            @Override
            public GearAbilityDataObject objectCreator(int[] bytes, int[] stringBytes) {
                return new GearAbilityDataObject(bytes, stringBytes);
            }

            @Override
            public String indexWriter(int idx) {
                return "80" + String.format("%02X", idx);
            }
        };
        List<GearAbilityDataObject> list = reader.readGenericDataFile(filename, print);
        if (list == null) {
            return null;
        }
        GearAbilityDataObject[] array = new GearAbilityDataObject[list.size()];
        if (print) {
            Set<Integer> groups = new HashSet<>();
            List<Integer> untakenGroups = new ArrayList<>();
            for (GearAbilityDataObject gearAbility : list) {
                groups.add(gearAbility.groupIndex);
            }
            for (int i = 0; i <= 0x82; i++) {
                if (!groups.contains(i)) {
                    untakenGroups.add(i);
                }
            }
            System.out.println("Untaken Groups: " + untakenGroups.stream().map(i -> ""+i).collect(Collectors.joining(",")));
        }
        return list.toArray(array);
    }

    public static MonsterFile readMonsterFile(String filename, boolean print) {
        File file = FileAccessorWithMods.getRealFile(filename);
        if (file.isDirectory()) {
            String[] contents = file.list();
            if (contents != null) {
                Arrays.stream(contents).filter(sf -> !sf.startsWith(".")).sorted().forEach(sf -> readMonsterFile(filename + '/' + sf, print));
            }
            return null;
        } else if (!filename.endsWith(".bin") && !filename.endsWith(".dat")) {
            return null;
        }
        List<Integer> knownLengths = new ArrayList<>();
        knownLengths.add(null);
        knownLengths.add(null);
        knownLengths.add(MonsterStatDataObject.LENGTH);
        knownLengths.add(null);
        knownLengths.add(MonsterSpoilsDataObject.LENGTH);
        List<Chunk> chunks = ChunkedFileHelper.readGenericChunkedFile(filename, print, knownLengths, true);
        MonsterFile monsterFile = new MonsterFile(chunks);
        try {
            int idx = Integer.parseInt(filename.substring(filename.length() - 7, filename.length() - 4), 10);
            DataAccess.MONSTERS[idx] = monsterFile;
        } catch (RuntimeException e) {
            System.err.println("Got exception while storing monster object (" + filename + ")");
            e.printStackTrace();
        }
        if (print) {
            monsterFile.parseScript();
            System.out.println(monsterFile);
        }
        return monsterFile;
    }

    public static EncounterFile readEncounterFile(String filename, final boolean print, final List<String> strings) {
        File file = FileAccessorWithMods.getRealFile(filename);
        if (file.isDirectory()) {
            String[] contents = file.list();
            if (contents != null) {
                Arrays.stream(contents).filter(sf -> !sf.startsWith(".")).sorted().forEach(sf -> readEncounterFile(filename + '/' + sf, print, strings));
            }
            return null;
        } else if (!filename.endsWith(".bin") && !filename.endsWith(".dat")) {
            return null;
        }
        List<String> actualStrings = strings;
        if (strings == null) {
            String stringFilePath = filename.replace(ORIGINALS_FOLDER, LOCALIZED_FOLDER);
            actualStrings = StringHelper.readStringFile(stringFilePath, false);
        }
        List<Integer> knownLengths = new ArrayList<>();
        knownLengths.add(null);
        knownLengths.add(null);
        knownLengths.add(FormationDataObject.LENGTH);
        List<Chunk> chunks = ChunkedFileHelper.readGenericChunkedFile(filename, print, knownLengths, true);
        EncounterFile encounterFile = new EncounterFile(chunks);
        try {
            // DataAccess.ENCOUNTERS[idx] = encounterFile;
        } catch (RuntimeException e) {
            System.err.println("Got exception while storing encounter object (" + filename + ")");
            e.printStackTrace();
        }
        if (print) {
            encounterFile.parseScript(actualStrings);
            System.out.println(encounterFile);
        }
        return encounterFile;
    }

    public static EventFile readEventFile(String filename, final boolean print, final List<String> strings) {
        File file = FileAccessorWithMods.getRealFile(filename);
        if (file.isDirectory()) {
            String[] contents = file.list();
            if (contents != null) {
                Arrays.stream(contents).filter(sf -> !sf.startsWith(".") && (!SKIP_BLITZBALL_EVENTS_FOLDER || !sf.equals("bl"))).sorted().forEach(sf -> readEventFile(filename + '/' + sf, print, strings));
            }
            return null;
        } else if (!filename.endsWith(".ebp") && !filename.endsWith(".dat")) {
            return null;
        }
        List<String> actualStrings = strings;
        if (strings == null) {
            String stringFilePath = filename.replace(ORIGINALS_FOLDER, LOCALIZED_FOLDER).replace("obj/", "obj_ps3/").replace(".ebp", ".bin");
            try {
                actualStrings = StringHelper.readStringFile(stringFilePath, false);
            } catch (Exception e) {
                System.out.println("Got exception while trying to parse strings: " + e.getLocalizedMessage());
            }
        }
        /* List<Integer> knownLengths = new ArrayList<>();
        knownLengths.add(null);
        knownLengths.add(null);
        knownLengths.add(0x8C);
        knownLengths.add(null);
        knownLengths.add(0x12C); */
        List<Chunk> chunks = ChunkedFileHelper.readGenericChunkedFile(filename, print, null, false);
        EventFile eventFile = new EventFile(chunks);
        try {
            // DataAccess.ENCOUNTERS[idx] = eventFile;
        } catch (RuntimeException e) {
            System.err.println("Got exception while storing encounter object (" + filename + ")");
            e.printStackTrace();
        }
        if (print) {
            eventFile.parseScript(actualStrings);
            System.out.println(eventFile);
        }
        return eventFile;
    }

    public static EncounterFile readEncounterFull(String btl, boolean print) {
        String endPath = btl + '/' + btl + ".bin";
        String originalsPath = PATH_ORIGINALS_ENCOUNTER + endPath;
        String localizedPath = PATH_LOCALIZED_ENCOUNTER + endPath;
        List<String> strings = StringHelper.readStringFile(localizedPath, false);
        return readEncounterFile(originalsPath, print, strings);
    }

    public static EventFile readEventFull(String event, boolean print) {
        String shortened = event.substring(0, 2);
        String midPath = shortened + '/' + event + '/' + event;
        String originalsPath = PATH_ORIGINALS_EVENT + midPath + ".ebp";
        String localizedPath = PATH_LOCALIZED_EVENT + midPath + ".bin";
        List<String> strings = StringHelper.readStringFile(localizedPath, false);
        return readEventFile(originalsPath, print, strings);
    }

    public static TreasureDataObject[] readTreasures(String filename, boolean print) {
        DataFileReader<TreasureDataObject> reader = new DataFileReader<>() {
            @Override
            public TreasureDataObject objectCreator(int[] bytes, int[] stringBytes) {
                return new TreasureDataObject(bytes);
            }

            @Override
            public String indexWriter(int idx) {
                return "Index " + idx + " [" + String.format("%02X", idx) + "h]";
            }
        };
        List<TreasureDataObject> list = reader.readGenericDataFile(filename, print);
        if (list == null) {
            return null;
        }
        TreasureDataObject[] array = new TreasureDataObject[list.size()];
        return list.toArray(array);
    }

    public static GearDataObject[] readWeaponPickups(String filename, boolean print) {
        DataFileReader<GearDataObject> reader = new DataFileReader<>() {
            @Override
            public GearDataObject objectCreator(int[] bytes, int[] stringBytes) {
                return new GearDataObject(bytes);
            }

            @Override
            public String indexWriter(int idx) {
                return "Index " + idx + " [" + String.format("%02X", idx) + "h]";
            }
        };
        List<GearDataObject> list = reader.readGenericDataFile(filename, print);
        if (list == null) {
            return null;
        }
        GearDataObject[] array = new GearDataObject[list.size()];
        return list.toArray(array);
    }

    public static GearShopDataObject[] readWeaponShops(String filename, boolean print) {
        DataFileReader<GearShopDataObject> reader = new DataFileReader<>() {
            @Override
            public GearShopDataObject objectCreator(int[] bytes, int[] stringBytes) {
                return new GearShopDataObject(bytes);
            }

            @Override
            public String indexWriter(int idx) {
                return "Index " + idx + " [" + String.format("%02X", idx) + "h]";
            }
        };
        List<GearShopDataObject> list = reader.readGenericDataFile(filename, print);
        if (list == null) {
            return null;
        }
        GearShopDataObject[] array = new GearShopDataObject[list.size()];
        return list.toArray(array);
    }

    public static ItemShopDataObject[] readItemShops(String filename, boolean print) {
        DataFileReader<ItemShopDataObject> reader = new DataFileReader<>() {
            @Override
            public ItemShopDataObject objectCreator(int[] bytes, int[] stringBytes) {
                return new ItemShopDataObject(bytes);
            }

            @Override
            public String indexWriter(int idx) {
                return "Index " + idx + " [" + String.format("%02X", idx) + "h]";
            }
        };
        List<ItemShopDataObject> list = reader.readGenericDataFile(filename, print);
        if (list == null) {
            return null;
        }
        ItemShopDataObject[] array = new ItemShopDataObject[list.size()];
        return list.toArray(array);
    }

    public static MonsterStatDataObject[] readMonsterLocalizations(boolean print) {
        class MonsterLocalizationReader extends DataFileReader<MonsterStatDataObject> {
            int offset = 0;
            @Override
            public MonsterStatDataObject objectCreator(int[] bytes, int[] stringBytes) {
                return new MonsterStatDataObject(bytes, stringBytes);
            }

            @Override
            public String indexWriter(int idx) {
                return "Index " + (idx) + " [" + String.format("%02X", idx) + "h]";
            }
        };
        MonsterLocalizationReader reader = new MonsterLocalizationReader();
        int fileIndex = 0;
        File file;
        List<MonsterStatDataObject> fullList = new ArrayList<>();
        do {
            fileIndex++;
            String path = PATH_LOCALIZED_KERNEL + "monster" + fileIndex + ".bin";
            file = FileAccessorWithMods.resolveFile(path, false);
            if (file.exists()) {
                List<MonsterStatDataObject> list = reader.readGenericDataFile(path, print);
                if (list != null) {
                    fullList.addAll(list);
                }
                reader.offset = fullList.size();
            }
        } while (file.exists());
        MonsterStatDataObject[] array = new MonsterStatDataObject[fullList.size()];
        return fullList.toArray(array);
    }
}
