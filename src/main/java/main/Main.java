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

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    private static final String MODE_GREP = "GREP";
    private static final String MODE_TRANSLATE = "TRANSLATE";
    private static final String MODE_READ_ALL_ABILITIES = "READ_ALL_ABILITIES";
    private static final String MODE_READ_KEY_ITEMS = "READ_KEY_ITEMS";
    private static final String MODE_READ_GEAR_ABILITIES = "READ_GEAR_ABILITIES";
    private static final String MODE_READ_TREASURES = "READ_TREASURES";
    private static final String MODE_READ_WEAPON_FILE = "READ_WEAPON_FILE";
    private static final String MODE_READ_STRING_FILE = "READ_STRING_FILE";
    private static final String MODE_PARSE_SCRIPT_FILE = "PARSE_SCRIPT_FILE";
    private static final String MODE_PARSE_ENCOUNTER = "PARSE_ENCOUNTER";
    private static final String MODE_PARSE_EVENT = "PARSE_EVENT";
    private static final String MODE_PARSE_MONSTER = "PARSE_MONSTER";

    private static final String PATH_FFX_ROOT = "ffx_ps2/ffx/master/";
    private static final String ORIGINALS_FOLDER = "jppc/";
    private static final String LOCALIZED_FOLDER = "new_uspc/";
    private static final String PATH_ORIGINALS_ROOT = PATH_FFX_ROOT + ORIGINALS_FOLDER;
    private static final String PATH_LOCALIZED_ROOT = PATH_FFX_ROOT + LOCALIZED_FOLDER;
    private static final String PATH_ORIGINALS_KERNEL = PATH_ORIGINALS_ROOT + "battle/kernel/";
    private static final String PATH_LOCALIZED_KERNEL = PATH_LOCALIZED_ROOT + "battle/kernel/";
    private static final String PATH_MONSTER_FOLDER = PATH_ORIGINALS_ROOT + "battle/mon/";
    private static final String PATH_ORIGINALS_ENCOUNTER = PATH_ORIGINALS_ROOT + "battle/btl/";
    private static final String PATH_LOCALIZED_ENCOUNTER = PATH_LOCALIZED_ROOT + "battle/btl/";
    private static final String PATH_ORIGINALS_EVENT = PATH_ORIGINALS_ROOT + "event/obj/";
    private static final String PATH_LOCALIZED_EVENT = PATH_LOCALIZED_ROOT + "event/obj_ps3/";
    private static final String PATH_SKILL_TABLE_3 = PATH_LOCALIZED_KERNEL + "command.bin"; // "FILE07723.dat"; // "command.bin"; //
    private static final String PATH_SKILL_TABLE_4 = PATH_LOCALIZED_KERNEL + "monmagic1.bin"; // "FILE07740.dat"; // "monmagic1.bin"; //
    private static final String PATH_SKILL_TABLE_6 = PATH_LOCALIZED_KERNEL + "monmagic2.bin"; // "FILE07741.dat"; // "monmagic2.bin"; //
    private static final String PATH_SKILL_TABLE_2 = PATH_LOCALIZED_KERNEL + "item.bin"; // "FILE07734.dat"; // "item.bin"; //

    private static final boolean SKIP_BLITZBALL_EVENTS = true;

    public static void main(String[] args) {
        String pathRoot = args[0];
        if (!".".equals(pathRoot)) {
            FileAccessorWithMods.GAME_FILES_ROOT = pathRoot;
        }
        String mode = args[1];
        List<String> realArgs = Arrays.asList(args).subList(2, args.length);
        readAndPrepareDataModel();
        switch (mode) {
            case MODE_GREP:
                String joined = String.join(" ", realArgs);
                writeGrep(joined);
                for (String monstername : realArgs) {
                    writeGrep(monstername);
                }
                break;
            case MODE_TRANSLATE:
                String concat = String.join("", realArgs);
                translate(concat);
                break;
            case MODE_READ_ALL_ABILITIES:
                readAbilitiesFromFile(PATH_SKILL_TABLE_3, 3, true);
                readAbilitiesFromFile(PATH_SKILL_TABLE_4, 4, true);
                readAbilitiesFromFile(PATH_SKILL_TABLE_6, 6, true);
                readAbilitiesFromFile(PATH_SKILL_TABLE_2, 2, true);
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
            case MODE_PARSE_EVENT:
                for (String filename : realArgs) {
                    readEventFull(filename, true);
                }
                break;
            case MODE_PARSE_SCRIPT_FILE:
                for (String filename : realArgs) {
                    if (filename.contains("battle/mon")) {
                        System.out.println("Monster file: " + filename);
                        readMonsterFile(filename, true);
                    } else if (filename.contains("battle/btl")) {
                        System.out.println("Encounter file: " + filename);
                        readEncounterFile(filename, true, null);
                    } else if (filename.contains("event/obj")) {
                        System.out.println("Event file: " + filename);
                        readEventFile(filename, true, null);
                    } else {
                        System.out.println("Failed to identify file: " + filename);
                    }
                }
                break;
            case MODE_READ_TREASURES:
                readTreasures(PATH_ORIGINALS_KERNEL + "takara.bin", true);
                break;
            case MODE_READ_WEAPON_FILE:
                for (String filename : realArgs) {
                    readWeaponPickups(filename, true);
                }
                break;
            case MODE_READ_KEY_ITEMS:
                readKeyItemsFromFile(PATH_LOCALIZED_KERNEL + "important.bin", true);
                break;
            case MODE_READ_GEAR_ABILITIES:
                readGearAbilitiesFromFile(PATH_LOCALIZED_KERNEL + "a_ability.bin", true);
                break;
            case MODE_READ_STRING_FILE:
                for (String filename : realArgs) {
                    StringHelper.readStringFile(filename, true);
                }
                break;
            default:
                break;
        }
    }

    private static void readAndPrepareDataModel() {
        StringHelper.initialize();
        ScriptConstants.initialize();
        ScriptFuncLib.initialize();
        prepareAbilities();
        DataAccess.GEAR_ABILITIES = readGearAbilitiesFromFile(PATH_LOCALIZED_KERNEL + "a_ability.bin", false);
        DataAccess.WEAPON_PICKUPS = readWeaponPickups(PATH_ORIGINALS_KERNEL + "buki_get.bin", false);
        DataAccess.KEY_ITEMS = readKeyItemsFromFile(PATH_LOCALIZED_KERNEL + "important.bin", false);
        DataAccess.TREASURES = readTreasures(PATH_ORIGINALS_KERNEL + "takara.bin", false);
        readMonsterFile(PATH_MONSTER_FOLDER, false);
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

    private static void writeGrep(String str) {
        final StringBuilder search = new StringBuilder("grep -r \"");
        str.chars().map(StringHelper::charToByte).forEach(bc -> search.append("\\x").append(Integer.toHexString(bc)));
        final StringBuilder regular = new StringBuilder();
        str.chars().map(StringHelper::charToByte).forEach(bc -> regular.append(Integer.toHexString(bc)));
        search.append("\" .");
        System.out.println(str);
        System.out.println(regular);
        System.out.println(search);
        System.out.println("");
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

    private static void parseFileText(String filename) {
        System.out.println("--- " + filename + " ---");
        File file = FileAccessorWithMods.getRealFile(filename);
        if (file.isDirectory()) {
            String[] contents = file.list();
            if (contents != null) {
                Arrays.stream(contents).sorted().forEach(subfile -> parseFileText(filename + '/' + subfile));
            }
        } else {
            StringBuilder out = new StringBuilder();
            try {
                DataInputStream inputStream = FileAccessorWithMods.readFile(filename);
                while (inputStream.available() > 0) {
                    int idx = inputStream.readUnsignedByte();
                    Character chr = StringHelper.byteToChar(idx);
                    if (chr != null) {
                        out.append(chr);
                    }
                }
            } catch (IOException e) {}
            System.out.println(out.toString().trim());
        }
    }

    private static AbilityDataObject[] readAbilitiesFromFile(String filename, int group, boolean print) {
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

    private static KeyItemDataObject[] readKeyItemsFromFile(String filename, boolean print) {
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

    private static GearAbilityDataObject[] readGearAbilitiesFromFile(String filename, boolean print) {
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

    private static MonsterFile readMonsterFile(String filename, boolean print) {
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

    private static EncounterFile readEncounterFile(String filename, final boolean print, final List<String> strings) {
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

    private static EventFile readEventFile(String filename, final boolean print, final List<String> strings) {
        File file = FileAccessorWithMods.getRealFile(filename);
        if (file.isDirectory()) {
            String[] contents = file.list();
            if (contents != null) {
                Arrays.stream(contents).filter(sf -> !sf.startsWith(".")).sorted().forEach(sf -> readEventFile(filename + '/' + sf, print, strings));
            }
            return null;
        } else if ((!filename.endsWith(".ebp") && !filename.endsWith(".dat")) || (SKIP_BLITZBALL_EVENTS && filename.contains("/bl/"))) {
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

    private static EncounterFile readEncounterFull(String btl, boolean print) {
        String endPath = btl + '/' + btl + ".bin";
        String originalsPath = PATH_ORIGINALS_ENCOUNTER + endPath;
        String localizedPath = PATH_LOCALIZED_ENCOUNTER + endPath;
        List<String> strings = StringHelper.readStringFile(localizedPath, false);
        return readEncounterFile(originalsPath, print, strings);
    }

    private static EventFile readEventFull(String event, boolean print) {
        String shortened = event.substring(0, 2);
        String midPath = shortened + '/' + event + '/' + event;
        String originalsPath = PATH_ORIGINALS_EVENT + midPath + ".ebp";
        String localizedPath = PATH_LOCALIZED_EVENT + midPath + ".bin";
        List<String> strings = StringHelper.readStringFile(localizedPath, false);
        return readEventFile(originalsPath, print, strings);
    }

    private static TreasureDataObject[] readTreasures(String filename, boolean print) {
        DataFileReader<TreasureDataObject> abilityReader = new DataFileReader<>() {
            @Override
            public TreasureDataObject objectCreator(int[] bytes, int[] stringBytes) {
                return new TreasureDataObject(bytes);
            }

            @Override
            public String indexWriter(int idx) {
                return "Index " + idx + " [" + String.format("%02X", idx) + "h]";
            }
        };
        List<TreasureDataObject> list = abilityReader.readGenericDataFile(filename, print);
        if (list == null) {
            return null;
        }
        TreasureDataObject[] array = new TreasureDataObject[list.size()];
        return list.toArray(array);
    }

    private static GearDataObject[] readWeaponPickups(String filename, boolean print) {
        DataFileReader<GearDataObject> abilityReader = new DataFileReader<>() {
            @Override
            public GearDataObject objectCreator(int[] bytes, int[] stringBytes) {
                return new GearDataObject(bytes);
            }

            @Override
            public String indexWriter(int idx) {
                return "Index " + idx + " [" + String.format("%02X", idx) + "h]";
            }
        };
        List<GearDataObject> list = abilityReader.readGenericDataFile(filename, print);
        if (list == null) {
            return null;
        }
        GearDataObject[] array = new GearDataObject[list.size()];
        return list.toArray(array);
    }
}
