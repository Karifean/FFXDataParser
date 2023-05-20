package main;

import model.*;
import script.EncounterFile;
import script.EventFile;
import script.MonsterFile;
import script.model.ScriptConstants;
import script.model.ScriptFuncLib;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    private static final int MODE_GREP = 1;
    private static final int MODE_TRANSLATE = 2;
    private static final int MODE_READ_ALL_ABILITIES = 4;
    private static final int MODE_READ_KEY_ITEMS = 5;
    private static final int MODE_READ_GEAR_ABILITIES = 6;
    private static final int MODE_READ_SPECIFIC_MONSTER_WITH_AI = 7;
    private static final int MODE_READ_TREASURES = 8;
    private static final int MODE_READ_WEAPON_PICKUPS = 9;
    private static final int MODE_READ_STRING_FILE = 10;
    private static final int MODE_PARSE_GENERIC_SCRIPT_FILE = 14;
    private static final int MODE_PARSE_MONSTER_FILE = 15;
    private static final int MODE_PARSE_ENCOUNTER_FILE = 16;
    private static final int MODE_PARSE_EVENT_FILE = 17;
    private static final String PATH_FFX_ROOT = "ffx_ps2/ffx/master/";
    private static final String ORIGINALS_KERNEL_PATH = PATH_FFX_ROOT + "jppc/battle/kernel/";
    private static final String LOCALIZED_KERNEL_PATH = PATH_FFX_ROOT + "new_uspc/battle/kernel/";
    private static final String MONSTER_FOLDER_PATH = PATH_FFX_ROOT + "jppc/battle/mon/";
    private static final String SKILL_TABLE_3_PATH = LOCALIZED_KERNEL_PATH + "command.bin"; // "FILE07723.dat"; // "command.bin"; //
    private static final String SKILL_TABLE_4_PATH = LOCALIZED_KERNEL_PATH + "monmagic1.bin"; // "FILE07740.dat"; // "monmagic1.bin"; //
    private static final String SKILL_TABLE_6_PATH = LOCALIZED_KERNEL_PATH + "monmagic2.bin"; // "FILE07741.dat"; // "monmagic2.bin"; //
    private static final String SKILL_TABLE_2_PATH = LOCALIZED_KERNEL_PATH + "item.bin"; // "FILE07734.dat"; // "item.bin"; //

    public static void main(String[] args) {
        int mode = Integer.parseInt(args[0], 10);
        List<String> realArgs = Arrays.asList(args).subList(1, args.length);
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
                readAbilitiesFromFile(SKILL_TABLE_3_PATH, 3, true);
                readAbilitiesFromFile(SKILL_TABLE_4_PATH, 4, true);
                readAbilitiesFromFile(SKILL_TABLE_6_PATH, 6, true);
                readAbilitiesFromFile(SKILL_TABLE_2_PATH, 2, true);
                break;
            case MODE_READ_SPECIFIC_MONSTER_WITH_AI:
                for (String arg : realArgs) {
                    int idx = Integer.parseInt(arg, 10);
                    int monsterIdx = idx + 0x1000;
                    MonsterFile monster = DataAccess.getMonster(monsterIdx);
                    if (monster != null) {
                        monster.parseScript();
                        System.out.println("Printing monster #" + arg + " [" + String.format("%04x", monsterIdx).toUpperCase() + "h]");
                        System.out.println(monster);
                    } else {
                        System.err.println("Monster with idx " + arg + " not found");
                    }
                }
                break;
            case MODE_PARSE_MONSTER_FILE:
                for (String filename : realArgs) {
                    readMonsterFile(filename, true);
                }
                break;
            case MODE_PARSE_ENCOUNTER_FILE:
                for (String filename : realArgs) {
                    readEncounterFile(filename, true);
                }
                break;
            case MODE_PARSE_EVENT_FILE:
                for (String filename : realArgs) {
                    readEventFile(filename, true);
                }
                break;
            case MODE_PARSE_GENERIC_SCRIPT_FILE:
                for (String filename : realArgs) {
                    if (filename.contains("battle/mon")) {
                        System.out.println("Monster file: " + filename);
                        readMonsterFile(filename, true);
                    } else if (filename.contains("battle/btl")) {
                        System.out.println("Encounter file: " + filename);
                        readEncounterFile(filename, true);
                    } else if (filename.contains("event/obj")) {
                        System.out.println("Event file: " + filename);
                        readEventFile(filename, true);
                    } else {
                        System.out.println("Failed to identify file: " + filename);
                    }
                }
                break;
            case MODE_READ_TREASURES:
                readTreasures(ORIGINALS_KERNEL_PATH + "takara.bin", true);
                break;
            case MODE_READ_WEAPON_PICKUPS:
                for (String filename : realArgs) {
                    readWeaponPickups(filename, true);
                }
                break;
            case MODE_READ_KEY_ITEMS:
                readKeyItemsFromFile(LOCALIZED_KERNEL_PATH + "important.bin", true);
                break;
            case MODE_READ_GEAR_ABILITIES:
                readGearAbilitiesFromFile(LOCALIZED_KERNEL_PATH + "a_ability.bin", true);
                break;
            case MODE_READ_STRING_FILE:
                for (String filename : realArgs) {
                    readStringFile(filename);
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
        DataAccess.GEAR_ABILITIES = readGearAbilitiesFromFile(LOCALIZED_KERNEL_PATH + "a_ability.bin", false);
        DataAccess.WEAPON_PICKUPS = readWeaponPickups(ORIGINALS_KERNEL_PATH + "buki_get.bin", false);
        DataAccess.KEY_ITEMS = readKeyItemsFromFile(LOCALIZED_KERNEL_PATH + "important.bin", false);
        DataAccess.TREASURES = readTreasures(ORIGINALS_KERNEL_PATH + "takara.bin", false);
        readMonsterFile(MONSTER_FOLDER_PATH, false);
    }

    public static void prepareAbilities() {
        prepareAbilitiesFromFile(SKILL_TABLE_3_PATH, 3);
        prepareAbilitiesFromFile(SKILL_TABLE_4_PATH, 4);
        prepareAbilitiesFromFile(SKILL_TABLE_6_PATH, 6);
        prepareAbilitiesFromFile(SKILL_TABLE_2_PATH, 2);
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

    private static List<String> readStringData(DataInputStream data, int count, boolean print) throws IOException {
        final boolean untilStrings = count < 0;
        if (untilStrings) {
            data.mark(4);
            int first = data.read();
            first += data.read() * 0x100;
            first += data.read() * 0x10000;
            first += data.read() * 0x1000000;
            data.reset();
            count = first / 8;
        }
        List<Integer> offsets = new ArrayList<>(count);
        Map<Integer, Integer> optionCount = new HashMap<>();
        data.mark(count * 8);
        for (int i = 0; i < count; i++) {
            int offset = data.read();
            offset += data.read() * 0x100;
            offset += data.read() * 0x10000;
            int choosableOptions = data.read();
            offsets.add(offset);
            optionCount.put(i, choosableOptions);
            int clonedOffset = data.read();
            clonedOffset += data.read() * 0x100;
            clonedOffset += data.read() * 0x10000;
            int clonedChoosableOptions = data.read();
            if (offset != clonedOffset) {
                System.err.println("offset not cloned: offset " + offset + "; other " + clonedOffset);
            }
            if (choosableOptions != clonedChoosableOptions) {
                System.err.println("choosableOptions not cloned: original " + choosableOptions + "; other " + clonedChoosableOptions);
            }
        }
        data.reset();
        return readStringsAtOffsets(count, offsets, data, print, optionCount);
    }

    public static List<String> readStringsAtOffsets(int count, List<Integer> offsets, DataInputStream data, boolean print, Map<Integer, Integer> optionCount) {
        List<String> strings = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int offset = offsets.get(i);
            StringBuilder out = new StringBuilder();
            data.mark(offset + 0xFFFF);
            if (print) {
                int options = optionCount != null ? optionCount.get(i) : 0;
                String choosable = options > 0 ? " (" + options + " selectable)" : "";
                System.out.print("String " + i + " [" + String.format("%04x", offset) + "]" + choosable + ":");
            }
            try {
                data.skipNBytes(offset);
                while (data.available() > 0) {
                    int idx = data.read();
                    if (idx == 0x00) {
                        break;
                    } else {
                        Character chr = StringHelper.byteToChar(idx);
                        if (chr != null) {
                            out.append(chr);
                        }
                    }
                }
                if (print) {
                    System.out.println(out);
                }
                strings.add(out.toString());
            } catch (IOException e) {
                // e.printStackTrace();
                if (print) {
                    System.out.println(e.getLocalizedMessage());
                }
                strings.add(e.toString());
            } finally {
                try {
                    data.reset();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return strings;
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
                return String.format("%04x", idx + group * 0x1000).toUpperCase();
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
                return "A0" + String.format("%02x", idx).toUpperCase();
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
                return "80" + String.format("%02x", idx).toUpperCase();
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
        } else if (!filename.endsWith(".bin")) {
            return null;
        }
        List<Integer> knownLengths = new ArrayList<>();
        knownLengths.add(null);
        knownLengths.add(null);
        knownLengths.add(0x8C);
        knownLengths.add(null);
        knownLengths.add(0x12C);
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

    private static EncounterFile readEncounterFile(String filename, boolean print) {
        File file = FileAccessorWithMods.getRealFile(filename);
        if (file.isDirectory()) {
            String[] contents = file.list();
            if (contents != null) {
                Arrays.stream(contents).filter(sf -> !sf.startsWith(".")).sorted().forEach(sf -> readEncounterFile(filename + '/' + sf, print));
            }
            return null;
        } else if (!filename.endsWith(".bin")) {
            return null;
        }
        /* List<Integer> knownLengths = new ArrayList<>();
        knownLengths.add(null);
        knownLengths.add(null);
        knownLengths.add(0x8C);
        knownLengths.add(null);
        knownLengths.add(0x12C); */
        List<Chunk> chunks = ChunkedFileHelper.readGenericChunkedFile(filename, print, null, true);
        EncounterFile encounterFile = new EncounterFile(chunks);
        try {
            // DataAccess.ENCOUNTERS[idx] = encounterFile;
        } catch (RuntimeException e) {
            System.err.println("Got exception while storing encounter object (" + filename + ")");
            e.printStackTrace();
        }
        if (print) {
            encounterFile.parseScript();
            System.out.println(encounterFile);
        }
        return encounterFile;
    }

    private static EventFile readEventFile(String filename, boolean print) {
        File file = FileAccessorWithMods.getRealFile(filename);
        if (file.isDirectory()) {
            String[] contents = file.list();
            if (contents != null) {
                Arrays.stream(contents).filter(sf -> !sf.startsWith(".")).sorted().forEach(sf -> readEventFile(filename + '/' + sf, print));
            }
            return null;
        } else if (!filename.endsWith(".ebp")) {
            return null;
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
            eventFile.parseScript();
            System.out.println(eventFile);
        }
        return eventFile;
    }

    private static TreasureDataObject[] readTreasures(String filename, boolean print) {
        DataFileReader<TreasureDataObject> abilityReader = new DataFileReader<>() {
            @Override
            public TreasureDataObject objectCreator(int[] bytes, int[] stringBytes) {
                return new TreasureDataObject(bytes);
            }

            @Override
            public String indexWriter(int idx) {
                return "Index " + idx + " [" + String.format("%02x", idx) + "h]";
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
                return "Index " + idx + " [" + String.format("%02x", idx) + "h]";
            }
        };
        List<GearDataObject> list = abilityReader.readGenericDataFile(filename, print);
        if (list == null) {
            return null;
        }
        GearDataObject[] array = new GearDataObject[list.size()];
        return list.toArray(array);
    }

    private static void readStringFile(String filename) {
        System.out.println("--- " + filename + " ---");
        File file = FileAccessorWithMods.getRealFile(filename);
        if (file.isDirectory()) {
            String[] contents = file.list();
            if (contents != null) {
                Arrays.stream(contents).filter(sf -> !sf.startsWith(".")).sorted().forEach(sf -> readStringFile(filename + '/' + sf));
            }
        } else {
            try {
                DataInputStream inputStream = FileAccessorWithMods.readFile(filename);
                readStringData(inputStream, -1, true);
            } catch (IOException ignored) {}
        }
    }
}
