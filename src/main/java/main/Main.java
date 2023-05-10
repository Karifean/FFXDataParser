package main;

import model.AbilityDataObject;
import model.GearDataObject;
import model.KeyItemDataObject;
import model.MonsterObject;

import java.io.*;
import java.util.*;

public class Main {

    private static final int MODE_READ_TEXT_FROM_FILES = 1;
    private static final int MODE_GREP = 2;
    private static final int MODE_TRANSLATE = 3;
    private static final int MODE_READ_ALL_ABILITIES = 4;
    private static final int MODE_READ_KEY_ITEMS = 5;
    private static final int MODE_READ_MONSTER_AI = 6;
    private static final int MODE_READ_MONSTER_AI_WITH_ABILITY_NAMES = 7;
    private static final int MODE_READ_ITEM_PICKUPS = 8;
    private static final int MODE_READ_WEAPON_PICKUPS = 9;
    private static final int MODE_FIND_EQUAL_FILES = 10;
    private static final int MODE_READ_STRING_FILE = 11;
    public static final Map<Integer, Character> BIN_LOOKUP = new HashMap<>();
    public static final Map<Character, Integer> BIN_REV_LOOKUP = new HashMap<>();
    private static final String PREFIX = "src/main/resources/";
    private static final String PATH_FFX_ROOT = PREFIX + "ffx_ps2/ffx/master/";
    private static final String ORIGINALS_KERNEL_PATH_REGULAR = PATH_FFX_ROOT + "jppc/battle/kernel/";
    private static final String LOCALIZED_KERNEL_PATH_REGULAR = PATH_FFX_ROOT + "new_uspc/battle/kernel/";
    private static final String LOCALIZED_KERNEL_PATH_MODDED = PREFIX + "ffx/attacks/";
    private static final String LOCALIZED_KERNEL_PATH = LOCALIZED_KERNEL_PATH_REGULAR;
    private static final String SKILL_TABLE_A_PATH = LOCALIZED_KERNEL_PATH + "command.bin"; // "FILE07723.dat"; // "command.bin"; //
    private static final String SKILL_TABLE_B_PATH = LOCALIZED_KERNEL_PATH + "monmagic1.bin"; // "FILE07740.dat"; // "monmagic1.bin"; //
    private static final String SKILL_TABLE_C_PATH = LOCALIZED_KERNEL_PATH + "monmagic2.bin"; // "FILE07741.dat"; // "monmagic2.bin"; //
    private static final String SKILL_TABLE_D_PATH = LOCALIZED_KERNEL_PATH + "item.bin"; // "FILE07734.dat"; // "item.bin"; //
    private static final Map<String, Set<String>> ABILITY_USERS = new HashMap<>();
    private static final Map<String, AbilityDataObject[]> FILE_ABILITIES_CACHE = new HashMap<>();
    private static final AbilityDataObject[] ABILITY_CACHE = new AbilityDataObject[0x10000];

    public static void main(String[] args) {
        int mode = Integer.parseInt(args[0], 10);
        List<String> realArgs = Arrays.asList(args).subList(1, args.length);
        prepareCharMap();
        switch (mode) {
            case MODE_READ_TEXT_FROM_FILES:
                for (String filename : realArgs) {
                    parseFileText(PREFIX + filename);
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
                /* for (String hexstring : realArgs) {
                    translate(hexstring);
                } */
                break;
            case MODE_READ_ALL_ABILITIES:
            case MODE_READ_MONSTER_AI_WITH_ABILITY_NAMES:
                getAbilitiesFromFile(SKILL_TABLE_A_PATH, -1);
                getAbilitiesFromFile(SKILL_TABLE_B_PATH, -1);
                getAbilitiesFromFile(SKILL_TABLE_C_PATH, -1);
                getAbilitiesFromFile(SKILL_TABLE_D_PATH, -1);
                if (mode == MODE_READ_ALL_ABILITIES) {
                    break;
                }
            case MODE_READ_MONSTER_AI:
                for (String filename : realArgs) {
                    readMonsterAi(PREFIX + filename);
                }
                break;
            case MODE_READ_ITEM_PICKUPS:
                GearDataObject[] gear = readWeaponPickups(ORIGINALS_KERNEL_PATH_REGULAR + "buki_get.bin", false);
                KeyItemDataObject[] keyItems = readKeyItemsFromFile(LOCALIZED_KERNEL_PATH_REGULAR + "important.bin", false);
                readItemPickups(ORIGINALS_KERNEL_PATH_REGULAR + "takara.bin", gear, keyItems);
                break;
            case MODE_READ_WEAPON_PICKUPS:
                for (String filename : realArgs) {
                    readWeaponPickups(PREFIX + filename, true);
                }
                break;
            case MODE_READ_KEY_ITEMS:
                readKeyItemsFromFile(LOCALIZED_KERNEL_PATH_REGULAR + "important.bin", true);
                break;
            case MODE_FIND_EQUAL_FILES:
                findEqualFiles(PREFIX + realArgs.get(0), PREFIX + realArgs.get(1));
                break;
            case MODE_READ_STRING_FILE:
                for (String filename : realArgs) {
                    readStringFile(PREFIX + filename);
                }
                break;
            default:
                break;
        }
    }

    public static AbilityDataObject getAbility(int abilityid) {
        if (ABILITY_CACHE[abilityid] == null) {
            ABILITY_CACHE[abilityid] = Main.readAbility(abilityid);
        }
        return ABILITY_CACHE[abilityid];
    }

    private static AbilityDataObject readAbility(int abilityid) {
        if (abilityid == 0) {
            AbilityDataObject nullAbility = new AbilityDataObject();
            nullAbility.name = "No Move";
            return nullAbility;
        } else {
            int group = abilityid / 0x1000;
            int idx = abilityid & 0x0FFF;
            if (group == 3) {
                return getAbilitiesFromFile(SKILL_TABLE_A_PATH, idx);
            } else if (group == 4) {
                return getAbilitiesFromFile(SKILL_TABLE_B_PATH, idx);
            } else if (group == 6) {
                return getAbilitiesFromFile(SKILL_TABLE_C_PATH, idx);
            } else if (group == 2) {
                return getAbilitiesFromFile(SKILL_TABLE_D_PATH, idx);
            } else {
                System.out.println("invalid abilityid! " + abilityid);
                return null;
            }
        }
    }

    private static void writeGrep(String str) {
        final StringBuilder search = new StringBuilder("grep -r \"");
        str.chars().map(ch -> BIN_REV_LOOKUP.get((char) ch)).forEach(bc -> search.append("\\x").append(Integer.toHexString(bc)));
        final StringBuilder regular = new StringBuilder();
        str.chars().map(ch -> BIN_REV_LOOKUP.get((char) ch)).forEach(bc -> regular.append(Integer.toHexString(bc)));
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
                    } else if (BIN_LOOKUP.containsKey(idx)) {
                        out.append(BIN_LOOKUP.get(idx));
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
            if (BIN_LOOKUP.containsKey(idx))
                out.append(BIN_LOOKUP.get(idx));
        }
        System.out.println(out);
    }

    public static String getStringAtLookupOffset(int[] table, int offset) {
        if (offset >= table.length) {
            return "(OOB)";
        }
        StringBuilder out = new StringBuilder();
        int idx = table[offset];
        while (idx != 0x00) {
            if (BIN_LOOKUP.containsKey(idx)) {
                out.append(BIN_LOOKUP.get(idx));
            } else {
                out.append('?');
            }
            offset++;
            if (offset >= table.length) {
                return out.append("(OOB)").toString();
            }
            idx = table[offset];
        }
        return out.toString();
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
            String txt = "";
            try {
                DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
                while (inputStream.available() > 0) {
                    int i = inputStream.readUnsignedByte();
                    if (BIN_LOOKUP.containsKey(i)) {
                        txt = txt + BIN_LOOKUP.get(i);
                    }
                }
            } catch (IOException e) {}
            System.out.println(txt.trim());
        }
    }

    private static void parseScriptUsedAbilities(String filename) {
        System.out.println("--- " + filename + " ---");
        File file = new File(filename);
        if (file.isDirectory()) {
            String[] contents = file.list();
            if (contents != null) {
                Arrays.stream(contents).sorted().forEach(subfile -> parseScriptUsedAbilities(filename + '/' + subfile));
            }
        } else {
            int flen = filename.length();
            String shortFn = filename.substring(flen - 8, flen - 4);
            try {
                DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
                int length = 0x2000;
                byte[] raw = new byte[length];
                int[] b = new int[length];
                if (inputStream.read(raw) < length) {
                    throw new EOFException("Did not read all bytes");
                }
                for (int i = 0; i < length; i++) {
                    b[i] = Byte.toUnsignedInt(raw[i]);
                    if (b[i] == 0x0B && i > 4) {
                        if (b[i-1] == 0xD8 && (b[i-2] == 0x67 || b[i-2] == 0x68)) {
                            String hb = String.format("%02x", b[i - 2]);
                            String lb = String.format("%02x", b[i - 3]);
                            String skillString = hb + ' ' + lb;
                            // String lb = String.format("%-20s", Integer.toHexString(b[i - 3]));
                            if (!ABILITY_USERS.containsKey(skillString)) {
                                ABILITY_USERS.put(skillString, new HashSet<>());
                            }
                            ABILITY_USERS.get(skillString).add(shortFn);
                            System.out.println("Uses skill: " + skillString);
                        }
                    }
                }
            } catch (IOException e) {}
        }
    }

    public static AbilityDataObject getAbilitiesFromFile(String filename, int specific) {
        AbilityDataObject[] abilities = FILE_ABILITIES_CACHE.computeIfAbsent(filename, Main::readAbilitiesFromFile);
        if (abilities == null) {
            return null;
        }
        if (specific < 0) {
            System.out.println("--- " + filename + " ---");
            for (int i = 0; i < abilities.length; i++) {
                AbilityDataObject ab = abilities[i];
                String prefix = String.format("%-20s", Integer.toHexString(i) + ": " + ab.getName());
                String dash = (ab.dashOffsetComputed > 0 && !"-".equals(ab.dash) ? "DH=" + ab.dash + " / " : "");
                String description = (ab.descriptionOffsetComputed > 0 && !"-".equals(ab.description) ? ab.description : "");
                String soText = (ab.otherTextOffsetComputed > 0 && !"-".equals(ab.otherText) ? " / OT=" + ab.otherText : "");
                System.out.println(prefix + ab + ' ' + dash + description + soText);
            }
            return null;
        } else {
            return abilities[specific];
        }
    }

    private static AbilityDataObject[] readAbilitiesFromFile(String filename) {
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
                        System.out.println("Index " + i + " [" + String.format("%02x", i) + "h] (Offset " + offset + ") - " + keyItems[i]);
                    }
                }
                return keyItems;
            } catch (IOException ignored) {}
        }
        return null;
    }

    private static void readMonsterAi(String filename) {
        System.out.println("--- " + filename + " ---");
        File file = new File(filename);
        if (file.isDirectory()) {
            String[] contents = file.list();
            if (contents != null) {
                Arrays.stream(contents).filter(sf -> !sf.startsWith(".")).sorted().forEach(sf -> readMonsterAi(filename + '/' + sf));
            }
        } else {
            boolean isMonsterFile = file.getPath().contains("/mon/");
            MonsterObject aiObj = new MonsterObject(file, isMonsterFile);
            try {
                aiObj.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (isMonsterFile) {
                System.out.println(" -- " + aiObj.monsterName + " -- ");
            }
            System.out.println("- Text Script Code -");
            System.out.println(aiObj.monsterAi.textAiString);
            System.out.println("- Hex Script Code -");
            System.out.println(aiObj.monsterAi.hexAiString.toString().toUpperCase());
            if (isMonsterFile) {
                System.out.println("- Monster Data -");
                System.out.println(aiObj.monsterData);
                System.out.println("- Sensor Text -");
                System.out.println(aiObj.monsterSensorText);
                System.out.println(aiObj.monsterSensorDash);
                System.out.println("- Scan Text -");
                System.out.println(aiObj.monsterScanText);
                System.out.println(aiObj.monsterScanDash);
            }
        }
    }

    private static void readItemPickups(String filename, GearDataObject[] gear, KeyItemDataObject[] keyItems) {
        System.out.println("--- " + filename + " ---");
        File file = new File(filename);
        if (file.isDirectory()) {
            String[] contents = file.list();
            if (contents != null) {
                Arrays.stream(contents).filter(sf -> !sf.startsWith(".")).sorted().forEach(sf -> readItemPickups(filename + '/' + sf, gear, keyItems));
            }
        } else {
            try {
                DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
                inputStream.skipBytes(10);
                int entriesLow = inputStream.read();
                int entries = inputStream.read() * 256 + entriesLow + 1;
                int entrySizeLow = inputStream.read();
                int entrySize = inputStream.read() * 256 + entrySizeLow;
                int sizeLow = inputStream.read();
                int size = inputStream.read() * 256 + sizeLow;
                inputStream.skipBytes(4);
                int j = 0;
                for (int i = 0; i < entries; i++) {
                    String offset = String.format("%04x", (i * entrySize) + 20);
                    System.out.print("Index " + j + " (Offset " + offset + ") - ");
                    j++;
                    int kind = inputStream.read();
                    int quantity = inputStream.read();
                    int typeLow = inputStream.read();
                    int type = inputStream.read() * 0x100 + typeLow;
                    String typeString = String.format("%02x", type);
                    String hexSuffix = " [" + typeString + "h]";
                    if (kind == 0x02) {
                        System.out.println("Item: " + quantity + "x " + getAbility(type).name);
                    } else if (kind == 0x00) {
                        System.out.println("Gil: " + quantity * 100 + (type != 0 ? " T=" + type + hexSuffix : ""));
                    } else if (kind == 0x05) {
                        GearDataObject obj = gear != null ? gear[type] : null;
                        System.out.println("Gear: buki_get #" + type + (quantity != 1 ? " Q=" + quantity : "") + " " + obj);
                    } else if (kind == 0x0A) {
                        KeyItemDataObject obj = keyItems != null ? keyItems[typeLow] : null;
                        System.out.println("Key Item: #" + typeLow + ' ' + (obj != null ? obj.getName() : "invalid"));
                    } else {
                        System.out.println("Unknown K=" + kind + "; Q=" + quantity + "; T=" + type + hexSuffix);
                    }
                }
            } catch (IOException ignored) {}
        }
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

    private static void prepareCharMap() {
        BIN_LOOKUP.put(0x00, '\n');
        BIN_LOOKUP.put(0x03, '\n');
        BIN_LOOKUP.put(0x0A, '"');
        BIN_LOOKUP.put(0x30, '0');
        BIN_LOOKUP.put(0x31, '1');
        BIN_LOOKUP.put(0x32, '2');
        BIN_LOOKUP.put(0x33, '3');
        BIN_LOOKUP.put(0x34, '4');
        BIN_LOOKUP.put(0x35, '5');
        BIN_LOOKUP.put(0x36, '6');
        BIN_LOOKUP.put(0x37, '7');
        BIN_LOOKUP.put(0x38, '8');
        BIN_LOOKUP.put(0x39, '9');
        BIN_LOOKUP.put(0x3a, ' ');
        BIN_LOOKUP.put(0x3b, '!');
        BIN_LOOKUP.put(0x3c, '\"');
        BIN_LOOKUP.put(0x3d, '#');
        BIN_LOOKUP.put(0x3e, '$');
        BIN_LOOKUP.put(0x3f, '%');
        BIN_LOOKUP.put(0x40, '&');
        BIN_LOOKUP.put(0x41, '\'');
        BIN_LOOKUP.put(0x42, '(');
        BIN_LOOKUP.put(0x43, ')');
        BIN_LOOKUP.put(0x44, '*');
        BIN_LOOKUP.put(0x45, '+');
        BIN_LOOKUP.put(0x46, ',');
        BIN_LOOKUP.put(0x47, '-');
        BIN_LOOKUP.put(0x48, '.');
        BIN_LOOKUP.put(0x49, '/');
        BIN_LOOKUP.put(0x4a, ':');
        BIN_LOOKUP.put(0x4b, ';');
        BIN_LOOKUP.put(0x4c, '<');
        BIN_LOOKUP.put(0x4d, '=');
        BIN_LOOKUP.put(0x4e, '>');
        BIN_LOOKUP.put(0x4f, '?');

        BIN_LOOKUP.put(0x50, 'A');
        BIN_LOOKUP.put(0x51, 'B');
        BIN_LOOKUP.put(0x52, 'C');
        BIN_LOOKUP.put(0x53, 'D');
        BIN_LOOKUP.put(0x54, 'E');
        BIN_LOOKUP.put(0x55, 'F');
        BIN_LOOKUP.put(0x56, 'G');
        BIN_LOOKUP.put(0x57, 'H');
        BIN_LOOKUP.put(0x58, 'I');
        BIN_LOOKUP.put(0x59, 'J');
        BIN_LOOKUP.put(0x5A, 'K');
        BIN_LOOKUP.put(0x5B, 'L');
        BIN_LOOKUP.put(0x5C, 'M');
        BIN_LOOKUP.put(0x5D, 'N');
        BIN_LOOKUP.put(0x5E, 'O');
        BIN_LOOKUP.put(0x5F, 'P');
        BIN_LOOKUP.put(0x60, 'Q');
        BIN_LOOKUP.put(0x61, 'R');
        BIN_LOOKUP.put(0x62, 'S');
        BIN_LOOKUP.put(0x63, 'T');
        BIN_LOOKUP.put(0x64, 'U');
        BIN_LOOKUP.put(0x65, 'V');
        BIN_LOOKUP.put(0x66, 'W');
        BIN_LOOKUP.put(0x67, 'X');
        BIN_LOOKUP.put(0x68, 'Y');
        BIN_LOOKUP.put(0x69, 'Z');
        BIN_LOOKUP.put(0x6a, '[');
        BIN_LOOKUP.put(0x6b, '\\');
        BIN_LOOKUP.put(0x6c, ']');
        BIN_LOOKUP.put(0x6d, (char)(0x0361));      //  ͡ , could maybe be replaced with '^'? «https://en.wikipedia.org/wiki/Inverted_breve»
        BIN_LOOKUP.put(0x6e, '＿');                 // TODO: Replace with a normal underscore if no other (shorter) such is found in the game font
        BIN_LOOKUP.put(0x6f, '`');
        BIN_LOOKUP.put(0x70, 'a');
        BIN_LOOKUP.put(0x71, 'b');
        BIN_LOOKUP.put(0x72, 'c');
        BIN_LOOKUP.put(0x73, 'd');
        BIN_LOOKUP.put(0x74, 'e');
        BIN_LOOKUP.put(0x75, 'f');
        BIN_LOOKUP.put(0x76, 'g');
        BIN_LOOKUP.put(0x77, 'h');
        BIN_LOOKUP.put(0x78, 'i');
        BIN_LOOKUP.put(0x79, 'j');
        BIN_LOOKUP.put(0x7A, 'k');
        BIN_LOOKUP.put(0x7B, 'l');
        BIN_LOOKUP.put(0x7C, 'm');
        BIN_LOOKUP.put(0x7D, 'n');
        BIN_LOOKUP.put(0x7E, 'o');
        BIN_LOOKUP.put(0x7F, 'p');
        BIN_LOOKUP.put(0x80, 'q');
        BIN_LOOKUP.put(0x81, 'r');
        BIN_LOOKUP.put(0x82, 's');
        BIN_LOOKUP.put(0x83, 't');
        BIN_LOOKUP.put(0x84, 'u');
        BIN_LOOKUP.put(0x85, 'v');
        BIN_LOOKUP.put(0x86, 'w');
        BIN_LOOKUP.put(0x87, 'x');
        BIN_LOOKUP.put(0x88, 'y');
        BIN_LOOKUP.put(0x89, 'z');

        BIN_LOOKUP.put(0x8a, '{');
        BIN_LOOKUP.put(0x8b, '|');
        BIN_LOOKUP.put(0x8c, '}');
        BIN_LOOKUP.put(0x8d, '～');                 // Fullwidth tilde
        BIN_LOOKUP.put(0x8e, '•');
        BIN_LOOKUP.put(0x8f, '【');
        BIN_LOOKUP.put(0x90, '】');
        BIN_LOOKUP.put(0x91, '♪');
        BIN_LOOKUP.put(0x92, '♥');

        BIN_LOOKUP.put(0x94, '“');
        BIN_LOOKUP.put(0x95, '”');
        BIN_LOOKUP.put(0x96, '—');

        BIN_LOOKUP.put(0x98, '¡');
        BIN_LOOKUP.put(0x99, '↑');
        BIN_LOOKUP.put(0x9a, '↓');
        BIN_LOOKUP.put(0x9b, '←');
        BIN_LOOKUP.put(0x9c, '→');
        BIN_LOOKUP.put(0x9d, '̈');                 // TODO: Consider replacing with '¨' if this isn't represented by some other character code
        BIN_LOOKUP.put(0x9e, '«');
        BIN_LOOKUP.put(0x9f, '°');

        BIN_LOOKUP.put(0xa1, '»');
        BIN_LOOKUP.put(0xa2, '¿');
        BIN_LOOKUP.put(0xa3, 'À');
        BIN_LOOKUP.put(0xa4, 'Á');
        BIN_LOOKUP.put(0xa5, 'Â');
        BIN_LOOKUP.put(0xa6, 'Ä');
        BIN_LOOKUP.put(0xa7, 'Ç');
        BIN_LOOKUP.put(0xa8, 'È');
        BIN_LOOKUP.put(0xa9, 'É');
        BIN_LOOKUP.put(0xaa, 'Ê');
        BIN_LOOKUP.put(0xab, 'Ë');
        BIN_LOOKUP.put(0xac, 'Ì');
        BIN_LOOKUP.put(0xad, 'Í');
        BIN_LOOKUP.put(0xae, 'Î');
        BIN_LOOKUP.put(0xaf, 'Ï');
        BIN_LOOKUP.put(0xb0, 'Ñ');
        BIN_LOOKUP.put(0xb1, 'Ò');
        BIN_LOOKUP.put(0xb2, 'Ó');
        BIN_LOOKUP.put(0xb3, 'Ô');
        BIN_LOOKUP.put(0xb4, 'Ö');
        BIN_LOOKUP.put(0xb5, 'Ù');
        BIN_LOOKUP.put(0xb6, 'Ú');
        BIN_LOOKUP.put(0xb7, 'Û');
        BIN_LOOKUP.put(0xb8, 'Ü');
        BIN_LOOKUP.put(0xb9, 'ß');
        BIN_LOOKUP.put(0xba, 'à');
        BIN_LOOKUP.put(0xbb, 'á');
        BIN_LOOKUP.put(0xbc, 'â');
        BIN_LOOKUP.put(0xbd, 'ä');
        BIN_LOOKUP.put(0xbe, 'ç');
        BIN_LOOKUP.put(0xbf, 'è');
        BIN_LOOKUP.put(0xc0, 'é');
        BIN_LOOKUP.put(0xc1, 'ê');
        BIN_LOOKUP.put(0xc2, 'ë');
        BIN_LOOKUP.put(0xc3, 'ì');
        BIN_LOOKUP.put(0xc4, 'í');
        BIN_LOOKUP.put(0xc5, 'î');
        BIN_LOOKUP.put(0xc6, 'ï');
        BIN_LOOKUP.put(0xc7, 'ñ');
        BIN_LOOKUP.put(0xc8, 'ò');
        BIN_LOOKUP.put(0xc9, 'ó');
        BIN_LOOKUP.put(0xca, 'ô');
        BIN_LOOKUP.put(0xcb, 'ö');
        BIN_LOOKUP.put(0xcc, 'ù');
        BIN_LOOKUP.put(0xcd, 'ú');
        BIN_LOOKUP.put(0xce, 'û');
        BIN_LOOKUP.put(0xcf, 'ü');
        BIN_LOOKUP.put(0xd0, '，');   // This appears to be another comma, positioned about one pixel higher vertically than the 0x46 one. We'll use a fullwidth one here.
        BIN_LOOKUP.put(0xd1, 'ƒ');   // Not sure whether this is supposed to be a musical forte character or a mathematical function "f"; the former can't be represented
        BIN_LOOKUP.put(0xd2, '„');
        BIN_LOOKUP.put(0xd3, '…');
        BIN_LOOKUP.put(0xd4, '‘');
        BIN_LOOKUP.put(0xd5, '’');
        BIN_LOOKUP.put(0xd6, '▪');   // Not really what the in-game symbol looks like but it'll do I guess
        BIN_LOOKUP.put(0xd7, '–');   // Shorter dash than 0x96, but not a hyphen?
        BIN_LOOKUP.put(0xd8, '~');   // "Normal" tilde, as opposed to the fullwidth one at 0x8d
        BIN_LOOKUP.put(0xd9, '™');

// NOTE: 0x93, 0x97, 0xa0 and 0xda seem to be unused and will print nothing if used in game text

        BIN_LOOKUP.put(0xdb, '›');
        BIN_LOOKUP.put(0xdc, '§');
        BIN_LOOKUP.put(0xdd, '©');
        BIN_LOOKUP.put(0xde, 'ₐ');   // This is actually a superscripted 'a' in the game's font, but this is the closest there seems to be in the 16-bit unicode charset
        BIN_LOOKUP.put(0xdf, '®');
        BIN_LOOKUP.put(0xe0, '±');
        BIN_LOOKUP.put(0xe1, '²');
        BIN_LOOKUP.put(0xe2, '³');
        BIN_LOOKUP.put(0xe3, '¼');
        BIN_LOOKUP.put(0xe4, '½');
        BIN_LOOKUP.put(0xe5, '¾');
        BIN_LOOKUP.put(0xe6, '×');
        BIN_LOOKUP.put(0xe7, '÷');
        BIN_LOOKUP.put(0xe8, '‹');
        BIN_LOOKUP.put(0xe9, '⋯');   // Midline horizontal ellipsis as opposed to the baseline version at character 0xd3

// The characters in-between do not print anything and are presumably either not valid characters or act as
// placeholders for character names and the like in certain contexts.

        BIN_LOOKUP.put(0xfc, '\t');  // Wide space or tab, not sure which; all the remaining characters up to 0xff appears this way when typed so probably not valid

        BIN_LOOKUP.forEach((i, c) -> BIN_REV_LOOKUP.put(c, i));
    }
}
