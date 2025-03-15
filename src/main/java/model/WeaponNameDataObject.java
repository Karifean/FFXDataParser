package model;

import atel.model.StackObject;
import main.StringHelper;
import model.strings.KeyedString;
import model.strings.LocalizedKeyedStringObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static main.DataReadingManager.DEFAULT_LOCALIZATION;
import static reading.ChunkedFileHelper.*;

/**
 * w_name.bin
 */
public class WeaponNameDataObject implements Writable, Localized<WeaponNameDataObject> {
    public static final int LENGTH = 0x48;

    private final int[] bytes;

    public LocalizedKeyedStringObject[] names = new LocalizedKeyedStringObject[7];
    public LocalizedKeyedStringObject[] unusedStrings = new LocalizedKeyedStringObject[7];
    public int[] models = new int[7];
    public int finalBytes;

    public WeaponNameDataObject(int[] bytes, int[] stringBytes, String localization) {
        this.bytes = bytes;
        mapBytes();
        mapFlags();
        mapStrings(stringBytes, localization);
    }

    private void mapBytes() {
        for (int i = 0; i < models.length; i++) {
            models[i] = read2Bytes(bytes, 0x38 + i * 0x02);
        }
        finalBytes = read2Bytes(bytes, 0x46);
    }

    private void mapFlags() {
        
    }

    private void mapStrings(int[] stringBytes, String localization) {
        if (stringBytes == null || stringBytes.length == 0) {
            return;
        }
        for (int i = 0; i < 7; i++) {
            LocalizedKeyedStringObject name = new LocalizedKeyedStringObject();
            int offset = read2Bytes(bytes, i * 0x04);
            int key = read2Bytes(bytes, i * 0x04 + 0x02);
            name.readAndSetLocalizedContent(localization, stringBytes, offset, key);
            names[i] = name;
        }
        for (int i = 0; i < 7; i++) {
            LocalizedKeyedStringObject str = new LocalizedKeyedStringObject();
            int offset = read2Bytes(bytes, i * 0x04 + 0x1C);
            int key = read2Bytes(bytes, i * 0x04 + 0x1E);
            str.readAndSetLocalizedContent(localization, stringBytes, offset, key);
            unusedStrings[i] = str;
        }
    }

    @Override
    public void setLocalizations(WeaponNameDataObject localization) {
        for (int i = 0; i < names.length; i++) {
            localization.names[i].copyInto(names[i]);
        }
        for (int i = 0; i < names.length; i++) {
            localization.unusedStrings[i].copyInto(unusedStrings[i]);
        }
    }

    @Override
    public Stream<KeyedString> streamKeyedStrings(String localization) {
        return Stream.concat(Arrays.stream(names), Arrays.stream(unusedStrings)).map(n -> n.getLocalizedContent(localization));
    }

    @Override
    public LocalizedKeyedStringObject getKeyedString(String title) {
        return switch (title) {
            case "T" -> names[0];
            case "Y" -> names[1];
            case "A" -> names[2];
            case "K" -> names[3];
            case "W" -> names[4];
            case "L" -> names[5];
            case "R" -> names[6];
            default -> null;
        };
    }

    @Override
    public int[] toBytes(String localization) {
        int[] array = new int[WeaponNameDataObject.LENGTH];

        return array;
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            list.add(StackObject.enumToScriptField("playerChar", i).name.charAt(0) + " Name: " + names[i].getDefaultString());
        }
        for (int i = 0; i < unusedStrings.length; i++) {
            list.add(StackObject.enumToScriptField("playerChar", i).name.charAt(0) + " Unused: " + unusedStrings[i].getDefaultString());
        }
        for (int i = 0; i < models.length; i++) {
            list.add(StackObject.enumToScriptField("playerChar", i).name.charAt(0) + " Model: " + StackObject.enumToString("model", models[i]));
        }
        list.add("Unknown46=" + StringHelper.formatHex4(finalBytes));
        String full = list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining("\n"));
        return full;
    }
}
