package main;

import model.KeyItemDataObject;
import model.strings.FieldString;
import model.strings.LocalizedFieldStringObject;
import model.strings.LocalizedMacroStringObject;
import reading.BytesHelper;
import reading.FileAccessorWithMods;

import java.io.File;
import java.util.*;

import static main.DataReadingManager.LOCALIZATIONS;
import static main.DataReadingManager.getLocalizationRoot;

public abstract class StringHelper {
    public static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static final boolean COLORS_USE_CONSOLE_CODES = false;
    public static final boolean WRITE_LINEBREAKS_AS_COMMANDS = true;
    public static final Map<String, Map<Integer, Character>> BYTE_TO_CHAR_MAPS = new HashMap<>();
    public static final Map<String, Map<Character, Integer>> CHAR_TO_BYTE_MAPS = new HashMap<>();
    public static final Map<Integer, LocalizedMacroStringObject> MACRO_LOOKUP = new HashMap<>();

    public static String formatHex2(int twoByteValue) {
        return String.format("%02X", twoByteValue);
    }

    public static String formatHex4(int fourByteValue) {
        String formatted = String.format("%04X", fourByteValue);
        if (formatted.length() == 8 && formatted.startsWith("FFFF")) {
            return formatted.substring(4);
        }
        return formatted;
    }

    public static String formatDec3(int threeDigitValue) {
        return String.format("%03d", threeDigitValue);
    }

    public static String hex2Suffix(int oneByteValue) {
        return " [" + formatHex2(oneByteValue) + "h]";
    }

    public static String hex4Suffix(int twoByteValue) {
        return " [" + formatHex4(twoByteValue) + "h]";
    }

    public static String hex2WithSuffix(int oneByteValue) {
        return oneByteValue + hex2Suffix(oneByteValue);
    }

    public static String hex4WithSuffix(int twoByteValue) {
        return twoByteValue + hex4Suffix(twoByteValue);
    }

    public static List<Integer> charToBytes(char chr, String charset) {
        if (chr == '\n') {
            return List.of(0x03);
        }
        Integer indexValue = CHAR_TO_BYTE_MAPS.get(charset).get(chr);
        if (indexValue == null) {
            return null;
        }
        List<Integer> bytes = new ArrayList<>();
        if (indexValue > 1070) {
            indexValue -= 1040;
            bytes.add(0x04);
        }
        if (indexValue >= 0x100) {
            int byte1 = indexValue / 0xD0 + 0x2B;
            int byte2 = indexValue % 0xD0;
            bytes.add(byte1);
            bytes.add(byte2);
        } else {
            bytes.add(indexValue);
        }
        return bytes;
    }

    public static List<Integer> charToBytes(int chr, String charset) {
        return charToBytes((char) chr, charset);
    }

    public static Character byteToChar(int hex, String charset) {
        return BYTE_TO_CHAR_MAPS.get(charset).get(hex);
    }

    public static String localizationToCharset(String localization) {
        return switch (localization) {
            case "ch" -> "ch";
            case "kr" -> "kr";
            case "jp" -> "jp";
            default -> "us";
        };
    }

    public static String getColorString(int hex) {
        if (COLORS_USE_CONSOLE_CODES) {
            return byteToConsoleColor(hex);
        } else {
            return "{CLR:" + byteToColor(hex) + '}';
        }
    }

    public static String byteToColor(int hex) {
        return switch (hex) {
            case 0x41 -> "WHITE";
            case 0x43 -> "YELLOW";
            case 0x52 -> "GREY";
            case 0x88 -> "BLUE";
            case 0x94 -> "RED";
            case 0x97 -> "PINK";
            case 0xA1 -> "OL_PURPLE";
            case 0xB1 -> "OL_CYAN";
            default -> formatHex2(hex);
        };
    }

    public static int colorToByte(String color) {
        return switch (color) {
            case "WHITE" -> 0x41;
            case "YELLOW" -> 0x43;
            case "GREY" -> 0x52;
            case "BLUE" -> 0x88;
            case "RED" -> 0x94;
            case "PINK" -> 0x97;
            case "OL_PURPLE" -> 0xA1;
            case "OL_CYAN" -> 0xB1;
            default -> Integer.parseInt(color, 16);
        };
    }

    public static String byteToConsoleColor(int hex) {
        return switch (hex) {
            case 0x41 -> ANSI_RESET;
            case 0x43 -> ANSI_YELLOW;
            case 0x52 -> ANSI_BLACK;
            case 0x88 -> ANSI_BLUE;
            case 0x94 -> ANSI_RED;
            case 0xA1 -> ANSI_PURPLE;
            case 0xB1 -> ANSI_CYAN;
            default -> "";
        };
    }

    public static String getPlayerChar(int pc) {
        return switch (pc) {
            case 0x00 -> "TIDUS";
            case 0x01 -> "YUNA";
            case 0x02 -> "AURON";
            case 0x03 -> "KIMAHRI";
            case 0x04 -> "WAKKA";
            case 0x05 -> "LULU";
            case 0x06 -> "RIKKU";
            case 0x07 -> "SEYMOUR";
            case 0x08 -> "VALEFOR";
            case 0x09 -> "IFRIT";
            case 0x0A -> "IXION";
            case 0x0B -> "SHIVA";
            case 0x0C -> "BAHAMUT";
            case 0x0D -> "ANIMA";
            case 0x0E -> "YOJIMBO";
            case 0x0F -> "CINDY";
            case 0x10 -> "SANDY";
            case 0x11 -> "MINDY";
            case 0x12 -> "DUMMY";
            case 0x13 -> "DUMMY2";
            default -> "?";
        };
    }

    public static String getControllerInput(int ctrlIdx) {
        return switch (ctrlIdx) {
            case 0x20 -> "?L1 (SWITCH)";
            case 0x30 -> "TRIANGLE";
            case 0x31 -> "X";
            case 0x32 -> "CIRCLE";
            case 0x33 -> "SQUARE";
            case 0x34 -> "L1";
            case 0x35 -> "R1";
            case 0x37 -> "?R2";
            case 0x39 -> "SELECT";
            case 0x41 -> "UP";
            case 0x42 -> "RIGHT";
            case 0x44 -> "DOWN";
            case 0x48 -> "LEFT";
            default -> "?";
        };
    }

    public static String consoleColorIfEnabled(String ansiColor) {
        return COLORS_USE_CONSOLE_CODES ? ansiColor : "";
    }

    public static List<LocalizedFieldStringObject> readLocalizedStringFiles(String path) {
        List<LocalizedFieldStringObject> localized = new ArrayList<>();
        LOCALIZATIONS.forEach((key, value) -> {
            List<FieldString> localizedStrings = readStringFile(getLocalizationRoot(key) + path, false, key);
            if (localizedStrings != null) {
                for (int i = 0; i < localizedStrings.size(); i++) {
                    if (i >= localized.size()) {
                        localized.add(new LocalizedFieldStringObject());
                    }
                    localized.get(i).setLocalizedContent(key, localizedStrings.get(i));
                }
            }
        });
        return localized;
    }

    public static List<FieldString> readStringFile(String filename, boolean print, String localization) {
        File file = FileAccessorWithMods.getRealFile(filename);
        if (file.isDirectory()) {
            String[] contents = file.list();
            if (contents != null) {
                Arrays.stream(contents).filter(sf -> !sf.startsWith(".")).sorted().forEach(sf -> readStringFile(filename + '/' + sf, print, localization));
            }
            return null;
        }
        int[] bytes = BytesHelper.fileToBytes(FileAccessorWithMods.resolveFile(filename, print));
        return FieldString.fromStringData(bytes, print, StringHelper.localizationToCharset(localization));
    }

    public static void fillByteList(String string, List<Integer> byteList, String charset) {
        for (int i = 0; i < string.length(); i++) {
            char chr = string.charAt(i);
            List<Integer> cmdBytes = chr == '{' ? parseCommand(string, i) : null;
            if (cmdBytes == null) {
                List<Integer> bytesOfChar = charToBytes(chr, charset);
                if (bytesOfChar != null) {
                    byteList.addAll(bytesOfChar);
                } else {
                    System.err.printf("Unknown character %c at index %d in string %s%n", chr, i, string);
                }
            } else {
                byteList.addAll(cmdBytes);
                int endIndex = string.substring(i).indexOf('}');
                i += endIndex;
            }
        }
        byteList.add(0x00);
    }

    public static List<Integer> getInvalidCharacters(String string, String charset) {
        List<Integer> indexList = new ArrayList<>();
        for (int i = 0; i < string.length(); i++) {
            char chr = string.charAt(i);
            List<Integer> cmdBytes = chr == '{' ? parseCommand(string, i) : null;
            if (cmdBytes != null) {
                i += string.substring(i).indexOf('}');
            } else if (charToBytes(chr, charset) == null) {
                indexList.add(i);
            }
        }
        return indexList;
    }

    public static String bytesToString(int[] bytes, String localization) {
        return getStringAtLookupOffset(bytes, 0, localization);
    }

    public static int[] getStringBytesAtLookupOffset(int[] table, int offset) {
        if (table == null || offset < 0 || offset >= table.length) {
            System.err.println("Invalid bytes get, " + (table == null ? "table is null" : ("offset is " + offset)));
            return null;
        }
        int end = offset;
        while (end < table.length && table[end] != 0x00) {
            end++;
        }
        return Arrays.copyOfRange(table, offset, end);
    }

    public static String getStringAtLookupOffset(int[] table, int offset, String localization) {
        if (table == null || offset < 0 || offset >= table.length) {
            return "";
        }
        StringBuilder out = new StringBuilder();
        int idx;
        boolean anyColorization = false;
        boolean extraFiveSections = false;
        String charset = localizationToCharset(localization);
        while (offset < table.length && (idx = table[offset]) != 0x00) {
            int extraOffset = extraFiveSections ? 0x410 : 0;
            extraFiveSections = false;
            if (idx >= 0x30) {
                Character chr = byteToChar(idx + extraOffset, charset);
                if (chr != null) {
                    out.append(chr);
                } else {
                    out.append(extraOffset != 0 ? "{UNKDBLCHR:04:" : "{UNKCHR:").append(formatHex2(idx)).append('}');
                }
            } else if (idx == 0x01) {
                out.append("{PAUSE}");
            } else if (idx == 0x03) {
                out.append(WRITE_LINEBREAKS_AS_COMMANDS ? "{\\n}" : '\n');
            } else if (idx == 0x04) {
                extraFiveSections = true;
            } else if (idx == 0x07) {
                offset++;
                int pixels = table[offset] - 0x30;
                out.append("{SPACE:").append(formatHex2(pixels)).append('}');
            } else if (idx == 0x09) {
                offset++;
                int varIdx = table[offset] - 0x30;
                out.append("{TIME:").append(formatHex2(varIdx)).append('}');
            } else if (idx == 0x0A) {
                offset++;
                int clr = table[offset];
                out.append(getColorString(clr));
                anyColorization = true;
            } else if (idx == 0x0B) {
                offset++;
                int ctrlIdx = table[offset];
                out.append("{CTRL:").append(formatHex2(ctrlIdx)).append(':').append(getControllerInput(ctrlIdx)).append('}');
            } else if (idx == 0x10) {
                offset++;
                int rawValue = table[offset];
                if (rawValue == 0xFF) {
                    out.append("{CHOICE-END}");
                } else {
                    int choiceIdx = rawValue - 0x30;
                    out.append("{CHOICE:").append(formatHex2(choiceIdx)).append('}');
                }
            } else if (idx == 0x12) {
                offset++;
                int varIdx = table[offset] - 0x30;
                out.append("{VAR:").append(formatHex2(varIdx)).append('}');
            } else if (idx == 0x13 && table[offset+1] <= 0x43) {
                offset++;
                int pcIdx = table[offset] - 0x30;
                out.append("{PC:").append(formatHex2(pcIdx)).append(':').append(getPlayerChar(pcIdx)).append('}');
            } else if (idx >= 0x13 && idx <= 0x22) {
                int section = idx - 0x13;
                offset++;
                int line = table[offset] - 0x30;
                out.append("{MCR:s").append(formatHex2(section)).append('l').append(formatHex2(line));
                if (!MACRO_LOOKUP.isEmpty()) {
                    out.append(':');
                    int index = section * 0x100 + line;
                    if (MACRO_LOOKUP.containsKey(index)) {
                        out.append('"').append(MACRO_LOOKUP.get(index).getLocalizedContent(localization)).append('"');
                    } else {
                        out.append("<Missing>");
                    }
                }
                out.append('}');
            } else if (idx == 0x23) {
                offset++;
                int varIdx = table[offset] - 0x30;
                out.append("{KEY:").append(formatHex2(varIdx));
                try {
                    KeyItemDataObject keyItem = DataAccess.getKeyItem(varIdx + 0xA000);
                    if (keyItem != null) {
                        out.append(':').append('"').append(keyItem.getName(localization)).append('"');
                    }
                } catch (UnsupportedOperationException ignored) {}
                out.append('}');
            } else if (idx == 0x28) {
                offset++;
                int varIdx = table[offset] - 0x30;
                out.append("{CMD:28:").append(formatHex2(varIdx)).append('}');
            } else if (idx == 0x2A) {
                offset++;
                int varIdx = table[offset] - 0x30;
                out.append("{CMD:2A:").append(formatHex2(varIdx)).append('}');
            } else if (idx >= 0x2B) {
                int section = idx - 0x2B;
                offset++;
                int lowByte = table[offset];
                int actualIdx = section * 0xD0 + lowByte;
                Character chr = byteToChar(actualIdx + extraOffset, charset);
                if (chr != null) {
                    out.append(chr);
                } else {
                    out.append(extraOffset != 0 ? "{UNKTPLCHR:04:" : "{UNKDBLCHR:")
                            .append(formatHex2(idx)).append(':')
                            .append(formatHex2(lowByte)).append('}');
                }
            } else {
                offset++;
                int varIdx = table[offset] - 0x30;
                out.append("{CMD:").append(formatHex2(idx)).append(':').append(formatHex2(varIdx)).append('}');
            }
            offset++;
        }
        if (COLORS_USE_CONSOLE_CODES && anyColorization) {
            out.append(ANSI_RESET);
        }
        return out.toString();
    }

    public static List<Integer> stringToByteList(String string, String charset) {
        final List<Integer> byteList = new ArrayList<>();
        for (int i = 0; i < string.length(); i++) {
            char chr = string.charAt(i);
            List<Integer> cmdBytes = chr == '{' ? parseCommand(string, i) : null;
            if (cmdBytes == null) {
                List<Integer> bytesOfChar = charToBytes(chr, charset);
                if (bytesOfChar != null) {
                    byteList.addAll(bytesOfChar);
                } else {
                    System.err.printf("Unknown character %c at index %d in string %s%n", chr, i, string);
                }
            } else {
                byteList.addAll(cmdBytes);
                int endIndex = string.substring(i).indexOf('}');
                i += endIndex;
            }
        }
        return byteList;
    }

    public static int[] stringToBytes(String string, String charset) {
        List<Integer> byteList = stringToByteList(string, charset);
        final int[] stringBytes = new int[byteList.size()];
        for (int i = 0; i < byteList.size(); i++) {
            stringBytes[i] = byteList.get(i);
        }
        return stringBytes;
    }

    public static List<Integer> parseCommand(String string, int startIndex) {
        String substring = string.substring(startIndex);
        int endIndex = substring.indexOf('}');
        if (endIndex < 0) {
            return null;
        }
        String cmd = substring.substring(1, endIndex);
        if (cmd.equals("PAUSE")) {
            return List.of(0x01);
        } else if (cmd.equals("\\n")) {
            return List.of(0x03);
        } else if (cmd.equals("CMD04")) {
            return List.of(0x04);
        } else if (cmd.startsWith("SPACE:")) {
            int pixels = Integer.parseInt(cmd.substring(6), 16) + 0x30;
            return List.of(0x07, pixels);
        } else if (cmd.startsWith("TIME:")) {
            int boxType = Integer.parseInt(cmd.substring(5), 16) + 0x30;
            return List.of(0x09, boxType);
        } else if (cmd.startsWith("CLR:")) {
            return List.of(0x0A, colorToByte(cmd.substring(4)));
        } else if (cmd.startsWith("COLOR:")) {
            return List.of(0x0A, colorToByte(cmd.substring(6)));
        } else if (cmd.startsWith("CTRL:")) {
            int ctrlIdx = Integer.parseInt(cmd.substring(5, 7), 16);
            return List.of(0x0B, ctrlIdx);
        } else if (cmd.equals("CHOICE-END")) {
            return List.of(0x10, 0xFF);
        } if (cmd.startsWith("CHOICE:")) {
            int choiceIdx = Integer.parseInt(cmd.substring(7), 16) + 0x30;
            return List.of(0x10, choiceIdx);
        } else if (cmd.startsWith("VAR:")) {
            int varIdx = Integer.parseInt(cmd.substring(4), 16) + 0x30;
            return List.of(0x12, varIdx);
        } else if (cmd.startsWith("PC:")) {
            int pc = Integer.parseInt(cmd.substring(3, 5), 16) + 0x30;
            return List.of(0x13, pc);
        } else if (cmd.startsWith("MCR:")) {
            int section = Integer.parseInt(cmd.substring(5, 7), 16) + 0x13;
            int line = Integer.parseInt(cmd.substring(8, 10), 16) + 0x30;
            return List.of(section, line);
        } else if (cmd.startsWith("KEY:")) {
            int keyItemIdx = Integer.parseInt(cmd.substring(4, 6), 16) + 0x30;
            return List.of(0x23, keyItemIdx);
        } else if (cmd.startsWith("CMD:")) {
            int cmdIdx = Integer.parseInt(cmd.substring(4, 6), 16);
            int arg = Integer.parseInt(cmd.substring(7, 9), 16) + 0x30;
            return List.of(cmdIdx, arg);
        } else if (cmd.startsWith("UNKCHR:")) {
            int chr = Integer.parseInt(cmd.substring(7, 9), 16);
            return List.of(chr);
        } else if (cmd.startsWith("UNKDBLCHR:")) {
            int section = Integer.parseInt(cmd.substring(10, 12), 16);
            int idx = Integer.parseInt(cmd.substring(13, 15), 16);
            return List.of(section, idx);
        } else {
            return null;
        }
    }

    public static int getChoicesInString(String string) {
        int choices = 0;
        int choiceOffset = 0;
        while (choiceOffset != -1) {
            choiceOffset = string.indexOf("{CHOICE:" + formatHex2(choices) + "}");
            if (choiceOffset != -1) {
                choices++;
            }
        }
        return choices;
    }

    public static void setCharMap(String charset, Map<Integer, Character> byteToCharMap, Map<Character, Integer> charToByteMap) {
        BYTE_TO_CHAR_MAPS.put(charset, byteToCharMap);
        CHAR_TO_BYTE_MAPS.put(charset, charToByteMap);
    }

    public static char toLetter(int letterIndex) {
        if (letterIndex <= 0 || letterIndex >= 26) {
            return '_';
        }
        return LETTERS.charAt(letterIndex - 1);
    }
}
