package main;

import model.*;
import script.model.ScriptConstants;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    private static final int MODE_READ_TEXT_FROM_FILES = 1;
    private static final int MODE_GREP = 2;
    private static final int MODE_TRANSLATE = 3;
    private static final int MODE_READ_ALL_ABILITIES = 4;
    private static final int MODE_READ_KEY_ITEMS = 5;
    private static final int MODE_READ_MONSTER_AI = 6;
    private static final int MODE_RUN_SPECIFIC_MONSTER_AI = 7;
    private static final int MODE_READ_TREASURES = 8;
    private static final int MODE_READ_WEAPON_PICKUPS = 9;
    private static final int MODE_FIND_EQUAL_FILES = 10;
    private static final int MODE_READ_STRING_FILE = 11;
    private static final int MODE_READ_GEAR_ABILITIES = 12;
    private static final String RESOURCES_ROOT = "src/main/resources/";
    private static final String PATH_FFX_ROOT = RESOURCES_ROOT + "ffx_ps2/ffx/master/";
    private static final String ORIGINALS_KERNEL_PATH_REGULAR = PATH_FFX_ROOT + "jppc/battle/kernel/";
    private static final String LOCALIZED_KERNEL_PATH_REGULAR = PATH_FFX_ROOT + "new_uspc/battle/kernel/";
    private static final String ORIGINALS_KERNEL_PATH_MODDED = RESOURCES_ROOT + "ffx/battle/kernel/";
    private static final String LOCALIZED_KERNEL_PATH_MODDED = RESOURCES_ROOT + "ffx/attacks/";
    private static final String ORIGINALS_KERNEL_PATH = ORIGINALS_KERNEL_PATH_REGULAR;
    private static final String LOCALIZED_KERNEL_PATH = LOCALIZED_KERNEL_PATH_REGULAR;
    private static final String MONSTER_FOLDER_PATH = PATH_FFX_ROOT + "jppc/battle/mon/";
    private static final String SKILL_TABLE_3_PATH = LOCALIZED_KERNEL_PATH + "command.bin"; // "FILE07723.dat"; // "command.bin"; //
    private static final String SKILL_TABLE_4_PATH = LOCALIZED_KERNEL_PATH + "monmagic1.bin"; // "FILE07740.dat"; // "monmagic1.bin"; //
    private static final String SKILL_TABLE_6_PATH = LOCALIZED_KERNEL_PATH + "monmagic2.bin"; // "FILE07741.dat"; // "monmagic2.bin"; //
    private static final String SKILL_TABLE_2_PATH = LOCALIZED_KERNEL_PATH + "item.bin"; // "FILE07734.dat"; // "item.bin"; //
    private static final Map<String, Set<String>> ABILITY_USERS = new HashMap<>();
    private static final Map<String, AbilityDataObject[]> FILE_ABILITIES_CACHE = new HashMap<>();

    public static void main(String[] args) {
        int mode = Integer.parseInt(args[0], 10);
        List<String> realArgs = Arrays.asList(args).subList(1, args.length);
        readAndPrepareDataModel();
        switch (mode) {
            case MODE_READ_TEXT_FROM_FILES:
                for (String filename : realArgs) {
                    parseFileText(RESOURCES_ROOT + filename);
                }
                ABILITY_USERS.keySet().stream().sorted().forEach((skill) -> System.out.println(skill + " -> " + String.join(",", ABILITY_USERS.get(skill).stream().sorted().toList())));
                break;
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
            case MODE_RUN_SPECIFIC_MONSTER_AI:
                for (String arg : realArgs) {
                    int idx = Integer.parseInt(arg, 10);
                    int monsterIdx = idx + 0x1000;
                    MonsterObject monster = DataAccess.getMonster(monsterIdx);
                    if (monster != null) {
                        monster.parseScript();
                        System.out.println("Printing monster #" + arg + " [" + String.format("%04x", monsterIdx).toUpperCase() + "h]");
                        System.out.println(monster);
                    } else {
                        System.err.println("Monster with idx " + arg + " not found");
                    }
                }
                break;
            case MODE_READ_MONSTER_AI:
                for (String filename : realArgs) {
                    readMonsterObject(RESOURCES_ROOT + filename, true);
                }
                break;
            case MODE_READ_TREASURES:
                readTreasures(ORIGINALS_KERNEL_PATH + "takara.bin", true);
                break;
            case MODE_READ_WEAPON_PICKUPS:
                for (String filename : realArgs) {
                    readWeaponPickups(RESOURCES_ROOT + filename, true);
                }
                break;
            case MODE_READ_KEY_ITEMS:
                readKeyItemsFromFile(LOCALIZED_KERNEL_PATH + "important.bin", true);
                break;
            case MODE_READ_GEAR_ABILITIES:
                readGearAbilitiesFromFile(LOCALIZED_KERNEL_PATH + "a_ability.bin", true);
                break;
            case MODE_FIND_EQUAL_FILES:
                findEqualFiles(RESOURCES_ROOT + realArgs.get(0), RESOURCES_ROOT + realArgs.get(1));
                break;
            case MODE_READ_STRING_FILE:
                for (String filename : realArgs) {
                    readStringFile(RESOURCES_ROOT + filename);
                }
                break;
            default:
                break;
        }
    }

    private static void readAndPrepareDataModel() {
        StringHelper.initialize();
        ScriptConstants.initialize();
        prepareAbilities();
        DataAccess.GEAR_ABILITIES = readGearAbilitiesFromFile(LOCALIZED_KERNEL_PATH + "a_ability.bin", false);
        DataAccess.WEAPON_PICKUPS = readWeaponPickups(ORIGINALS_KERNEL_PATH + "buki_get.bin", false);
        DataAccess.KEY_ITEMS = readKeyItemsFromFile(LOCALIZED_KERNEL_PATH + "important.bin", false);
        DataAccess.TREASURES = readTreasures(ORIGINALS_KERNEL_PATH + "takara.bin", false);
        readMonsterObject(MONSTER_FOLDER_PATH, false);
    }

    public static void prepareAbilities() {
        prepareAbilitiesFromFile(SKILL_TABLE_3_PATH, 3);
        prepareAbilitiesFromFile(SKILL_TABLE_4_PATH, 4);
        prepareAbilitiesFromFile(SKILL_TABLE_6_PATH, 6);
        prepareAbilitiesFromFile(SKILL_TABLE_2_PATH, 2);
    }

    private static void prepareAbilitiesFromFile(String filename, int group) {
        AbilityDataObject[] abilities = FILE_ABILITIES_CACHE.computeIfAbsent(filename, (f) -> readAbilitiesFromFile(f, group, false));
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
        File file = new File(filename);
        if (file.isDirectory()) {
            String[] contents = file.list();
            if (contents != null) {
                Arrays.stream(contents).sorted().forEach(subfile -> parseFileText(filename + '/' + subfile));
            }
        } else {
            StringBuilder out = new StringBuilder();
            try {
                DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
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
        if (print) {
            System.out.println("--- " + filename + " ---");
        }
        File file = new File(filename);
        if (!file.isDirectory()) {
            try (DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
                inputStream.skipBytes(0xA);
                int count = inputStream.read();
                count += inputStream.read() * 0x100;
                AbilityDataObject[] moves = new AbilityDataObject[count+1];
                int individualLength = inputStream.read();
                individualLength += inputStream.read() * 0x100;
                int totalLength = inputStream.read();
                totalLength += inputStream.read() * 0x100;
                inputStream.skipBytes(4);
                int[] moveBytes = new int[totalLength];
                for (int i = 0; i < totalLength; i++) {
                    moveBytes[i] = inputStream.read();
                }
                byte[] stringBytes = inputStream.readAllBytes();
                int stringsLength = stringBytes.length;
                int[] allStrings = new int[stringsLength];
                for (int i = 0; i < stringsLength; i++) {
                    allStrings[i] = Byte.toUnsignedInt(stringBytes[i]);
                }
                for (int i = 0; i <= count; i++) {
                    moves[i] = new AbilityDataObject(Arrays.copyOfRange(moveBytes, i * individualLength, (i+1) * individualLength), allStrings);
                    if (print) {
                        String offset = String.format("%04x", (i * individualLength) + 20);
                        System.out.println(String.format("%04x", i + group * 0x1000).toUpperCase() + " (Offset " + offset + ") - " + moves[i]);
                    }
                }
                return moves;
            } catch (IOException ignored) {}
        }
        return null;
    }

    private static KeyItemDataObject[] readKeyItemsFromFile(String filename, boolean print) {
        File file = new File(filename);
        if (!file.isDirectory()) {
            try (DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
                inputStream.skipBytes(0xA);
                int count = inputStream.read();
                count += inputStream.read() * 0x100;
                KeyItemDataObject[] keyItems = new KeyItemDataObject[count+1];
                int individualLength = inputStream.read();
                individualLength += inputStream.read() * 0x100;
                int totalLength = inputStream.read();
                totalLength += inputStream.read() * 0x100;
                inputStream.skipBytes(4);
                int[] keyItemBytes = new int[totalLength];
                for (int i = 0; i < totalLength; i++) {
                    keyItemBytes[i] = inputStream.read();
                }
                byte[] stringBytes = inputStream.readAllBytes();
                int stringsLength = stringBytes.length;
                int[] allStrings = new int[stringsLength];
                for (int i = 0; i < stringsLength; i++) {
                    allStrings[i] = Byte.toUnsignedInt(stringBytes[i]);
                }
                for (int i = 0; i <= count; i++) {
                    keyItems[i] = new KeyItemDataObject(Arrays.copyOfRange(keyItemBytes, i * individualLength, (i+1) * individualLength), allStrings);
                    if (print) {
                        String offset = String.format("%04x", (i * individualLength) + 20);
                        System.out.println("A0" + String.format("%02x", i).toUpperCase() + " (Offset " + offset + ") - " + keyItems[i]);
                    }
                }
                return keyItems;
            } catch (IOException ignored) {}
        }
        return null;
    }

    private static GearAbilityDataObject[] readGearAbilitiesFromFile(String filename, boolean print) {
        File file = new File(filename);
        if (!file.isDirectory()) {
            try (DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
                inputStream.skipBytes(0xA);
                int count = inputStream.read();
                count += inputStream.read() * 0x100;
                GearAbilityDataObject[] gearAbilities = new GearAbilityDataObject[count+1];
                int individualLength = inputStream.read();
                individualLength += inputStream.read() * 0x100;
                int totalLength = inputStream.read();
                totalLength += inputStream.read() * 0x100;
                inputStream.skipBytes(4);
                int[] gearAbilityBytes = new int[totalLength];
                for (int i = 0; i < totalLength; i++) {
                    gearAbilityBytes[i] = inputStream.read();
                }
                byte[] stringBytes = inputStream.readAllBytes();
                int stringsLength = stringBytes.length;
                int[] allStrings = new int[stringsLength];
                for (int i = 0; i < stringsLength; i++) {
                    allStrings[i] = Byte.toUnsignedInt(stringBytes[i]);
                }
                for (int i = 0; i <= count; i++) {
                    gearAbilities[i] = new GearAbilityDataObject(Arrays.copyOfRange(gearAbilityBytes, i * individualLength, (i+1) * individualLength), allStrings);
                    if (print) {
                        String offset = String.format("%04x", (i * individualLength) + 20);
                        System.out.println("80" + String.format("%02x", i).toUpperCase() + " (Offset " + offset + ") - " + gearAbilities[i]);
                    }
                }
                if (print) {
                    Set<Integer> groups = new HashSet<>();
                    List<Integer> untakenGroups = new ArrayList<>();
                    for (GearAbilityDataObject gearAbility : gearAbilities) {
                        groups.add(gearAbility.groupIndex);
                    }
                    for (int i = 0; i <= 0x82; i++) {
                        if (!groups.contains(i)) {
                            untakenGroups.add(i);
                        }
                    }
                    System.out.println("Untaken Groups: " + untakenGroups.stream().map(i -> ""+i).collect(Collectors.joining(",")));
                }
                return gearAbilities;
            } catch (IOException ignored) {}
        }
        return null;
    }

    private static MonsterObject readMonsterObject(String filename, boolean print) {
        if (print) {
            System.out.println("--- " + filename + " ---");
        }
        File file = new File(filename);
        if (file.isDirectory()) {
            String[] contents = file.list();
            if (contents != null) {
                Arrays.stream(contents).filter(sf -> !sf.startsWith(".")).sorted().forEach(sf -> readMonsterObject(filename + '/' + sf, print));
            }
            return null;
        } else if (file.getName().endsWith(".ebp") || file.getName().endsWith(".bin")) {
            boolean isMonsterFile = file.getPath().contains("/mon/");
            MonsterObject monsterObject = new MonsterObject(file, isMonsterFile);
            if (isMonsterFile) {
                try {
                    int idx = Integer.parseInt(file.getName().substring(1, 4), 10);
                    DataAccess.MONSTERS[idx] = monsterObject;
                } catch (RuntimeException e) {
                    System.err.println("Got exception while storing monster object (fileName=" + file.getName() + ")");
                    e.printStackTrace();
                }
            }
            if (!print) {
                return monsterObject;
            }
            monsterObject.parseScript();
            if (isMonsterFile) {
                System.out.println("Monster: " + monsterObject.monsterName);
            }
            if (monsterObject.monsterAi != null) {
                System.out.println("- Script Code -");
                System.out.println(monsterObject.monsterAi.allLinesString());
                System.out.println("- Jump Table -");
                System.out.println(monsterObject.monsterAi.jumpTableString.toString());
            }
            if (isMonsterFile) {
                System.out.println("- Monster Stats -");
                System.out.println(monsterObject.monsterStatData);
                /* System.out.println("- Monster Spoils -");
                System.out.println(monsterObject.monsterSpoilsData);
                System.out.println("- Sensor Text -");
                System.out.println(monsterObject.monsterSensorText);
                System.out.println(monsterObject.monsterSensorDash);
                System.out.println("- Scan Text -");
                System.out.println(monsterObject.monsterScanText);
                System.out.println(monsterObject.monsterScanDash); */
            }
            return monsterObject;
        } else {
            System.out.println("File ignored");
            return null;
        }
    }

    private static TreasureDataObject[] readTreasures(String filename, boolean print) {
        if (print) {
            System.out.println("--- " + filename + " ---");
        }
        File file = new File(filename);
        if (file.isDirectory()) {
            String[] contents = file.list();
            if (contents != null) {
                Arrays.stream(contents).filter(sf -> !sf.startsWith(".")).sorted().forEach(sf -> readTreasures(filename + '/' + sf, print));
            }
        } else {
            try (DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
                inputStream.skipBytes(0xA);
                int count = inputStream.read();
                count += inputStream.read() * 0x100;
                TreasureDataObject[] treasures = new TreasureDataObject[count+1];
                int individualLength = inputStream.read();
                individualLength += inputStream.read() * 0x100;
                int totalLength = inputStream.read();
                totalLength += inputStream.read() * 0x100;
                inputStream.skipBytes(4);
                int[] treasureBytes = new int[totalLength];
                for (int i = 0; i < totalLength; i++) {
                    treasureBytes[i] = inputStream.read();
                }
                for (int i = 0; i <= count; i++) {
                    String offset = String.format("%04x", (i * individualLength) + 20);
                    treasures[i] = new TreasureDataObject(Arrays.copyOfRange(treasureBytes, i * individualLength, (i + 1) * individualLength));
                    if (print) {
                        System.out.print("Index " + i + " [" + String.format("%02x", i) + "h] (Offset " + offset + ") - ");
                        System.out.println(treasures[i]);
                    }
                }
                return treasures;
            } catch (IOException ignored) {}
        }
        return null;
    }

    private static GearDataObject[] readWeaponPickups(String filename, boolean print) {
        if (print) {
            System.out.println("--- " + filename + " ---");
        }
        File file = new File(filename);
        if (file.isDirectory()) {
            String[] contents = file.list();
            if (contents != null) {
                Arrays.stream(contents).filter(sf -> !sf.startsWith(".")).sorted().forEach(sf -> readWeaponPickups(filename + '/' + sf, print));
            }
        } else {
            try (DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
                inputStream.skipBytes(0xA);
                int count = inputStream.read();
                count += inputStream.read() * 0x100;
                GearDataObject[] gear = new GearDataObject[count+1];
                int individualLength = inputStream.read();
                individualLength += inputStream.read() * 0x100;
                int totalLength = inputStream.read();
                totalLength += inputStream.read() * 0x100;
                inputStream.skipBytes(4);
                int[] gearBytes = new int[totalLength];
                for (int i = 0; i < totalLength; i++) {
                    gearBytes[i] = inputStream.read();
                }
                for (int i = 0; i <= count; i++) {
                    gear[i] = new GearDataObject(Arrays.copyOfRange(gearBytes, i * individualLength, (i+1) * individualLength));
                    if (print) {
                        String offset = String.format("%04x", (i * individualLength) + 20);
                        System.out.println("Index " + i + " [" + String.format("%02x", i) + "h] (Offset " + offset + ") - " + gear[i]);
                    }
                }
                return gear;
            } catch (IOException ignored) {}
        }
        return null;
    }

    private static void readStringFile(String filename) {
        System.out.println("--- " + filename + " ---");
        File file = new File(filename);
        if (file.isDirectory()) {
            String[] contents = file.list();
            if (contents != null) {
                Arrays.stream(contents).filter(sf -> !sf.startsWith(".")).sorted().forEach(sf -> readStringFile(filename + '/' + sf));
            }
        } else {
            try {
                DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
                readStringData(inputStream, -1, true);
            } catch (IOException ignored) {}
        }
    }

    private static void findEqualFiles(final String source, final String target) {
        Map<String, byte[]> map = new HashMap<>();
        makeFileMap(target, map);
        compareFiles(source, map);
    }

    private static void compareFiles(final String source, final Map<String, byte[]> map) {
        File file = new File(source);
        if (file.isDirectory()) {
            String[] contents = file.list();
            if (contents != null) {
                Arrays.stream(contents).filter(sf -> !sf.startsWith(".")).sorted().forEach(sf -> compareFiles(source + '/' + sf, map));
            }
        } else {
            try {
                byte[] hash = fileHash(file);
                int highestMatches = 0;
                String highestKey = null;
                for (Map.Entry<String, byte[]> entry : map.entrySet()) {
                    int matches = 0;
                    byte[] val = entry.getValue();
                    int len = Math.min(val.length, hash.length);
                    for (int i = 0; i < len; i++) {
                        if (val[i] == hash[i]) {
                            matches++;
                        }
                    }
                    if (matches > highestMatches) {
                        highestMatches = matches;
                        highestKey = entry.getKey();
                    }
                }
                if (highestMatches == 0) {
                    System.out.println("No target found for " + source);
                } else {
                    System.out.println("mv " + source + " " + highestKey + " (matches=" + highestMatches + ")");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static byte[] fileHash(final File file) throws IOException {
        long fileSize = file.length();
        FileInputStream stream = new FileInputStream(file);
        byte[] fileData = new byte[(int) fileSize];
        stream.read(fileData);
        stream.close();
        return fileData;
        // return new BigInteger(1, messageDigest.digest(fileData)).toString(16);
    }

    private static void makeFileMap(final String target, final Map<String, byte[]> map) {
        File file = new File(target);
        if (file.isDirectory()) {
            String[] contents = file.list();
            if (contents != null) {
                Arrays.stream(contents).filter(sf -> !sf.startsWith(".")).sorted().forEach(sf -> makeFileMap(target + '/' + sf, map));
            }
        } else {
            try {
                map.put(target, fileHash(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
