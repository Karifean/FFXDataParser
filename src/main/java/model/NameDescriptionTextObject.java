package model;

import model.strings.KeyedString;
import model.strings.LocalizedKeyedStringObject;

import java.util.stream.Stream;

import static reading.ChunkedFileHelper.write4Bytes;

/**
 * status_txt.bin
 */
public class NameDescriptionTextObject implements Nameable, Writable, Localized<NameDescriptionTextObject> {
    public static final int LENGTH = 0x10;
    private final int[] bytes;

    public LocalizedKeyedStringObject name = new LocalizedKeyedStringObject();
    public LocalizedKeyedStringObject unusedString0405 = new LocalizedKeyedStringObject();
    public LocalizedKeyedStringObject description = new LocalizedKeyedStringObject();
    public LocalizedKeyedStringObject unusedString0C0D = new LocalizedKeyedStringObject();

    private int nameOffset;
    private int nameKey;
    private int unusedString0405Offset;
    private int unusedString0405Key;
    private int descriptionOffset;
    private int descriptionKey;
    private int unusedString0C0DOffset;
    private int unusedString0C0DKey;

    public NameDescriptionTextObject(int[] bytes, int[] stringBytes, String localization) {
        this.bytes = bytes;
        mapBytes();
        mapStrings(stringBytes, localization);
    }
    public String getName(String localization) {
        return name.getLocalizedString(localization);
    }

    private void mapBytes() {
        nameOffset = read2Bytes(0x00);
        nameKey = read2Bytes(0x02);
        unusedString0405Offset = read2Bytes(0x04);
        unusedString0405Key = read2Bytes(0x06);
        descriptionOffset = read2Bytes(0x08);
        descriptionKey = read2Bytes(0x0A);
        unusedString0C0DOffset = read2Bytes(0x0C);
        unusedString0C0DKey = read2Bytes(0x0E);
    }

    @Override
    public int[] toBytes(String localization) {
        int[] array = new int[NameDescriptionTextObject.LENGTH];
        write4Bytes(array, 0x00, name.getLocalizedContent(localization).toHeaderBytes());
        write4Bytes(array, 0x04, unusedString0405.getLocalizedContent(localization).toHeaderBytes());
        write4Bytes(array, 0x08, description.getLocalizedContent(localization).toHeaderBytes());
        write4Bytes(array, 0x0C, unusedString0C0D.getLocalizedContent(localization).toHeaderBytes());
        return array;
    }

    private void mapStrings(int[] stringBytes, String localization) {
        name.readAndSetLocalizedContent(localization, stringBytes, nameOffset, nameKey);
        unusedString0405.readAndSetLocalizedContent(localization, stringBytes, unusedString0405Offset, unusedString0405Key);
        description.readAndSetLocalizedContent(localization, stringBytes, descriptionOffset, descriptionKey);
        unusedString0C0D.readAndSetLocalizedContent(localization, stringBytes, unusedString0C0DOffset, unusedString0C0DKey);
    }

    @Override
    public void setLocalizations(NameDescriptionTextObject localizationObject) {
        localizationObject.name.copyInto(name);
        localizationObject.unusedString0405.copyInto(unusedString0405);
        localizationObject.description.copyInto(description);
        localizationObject.unusedString0C0D.copyInto(unusedString0C0D);
    }

    @Override
    public Stream<KeyedString> streamKeyedStrings(String localization) {
        return Stream.of(
                name.getLocalizedContent(localization),
                unusedString0405.getLocalizedContent(localization),
                description.getLocalizedContent(localization),
                unusedString0C0D.getLocalizedContent(localization)
        );
    }

    @Override
    public LocalizedKeyedStringObject getKeyedString(String title) {
        return switch (title) {
            case "name" -> name;
            case "description" -> description;
            default -> null;
        };
    }

    @Override
    public String toString() {
        String descriptionStr = (descriptionOffset > 0 ? description.getDefaultContent().toString() : "");
        return String.format("%-20s", getName()) + " - " + descriptionStr;
    }

    private int read2Bytes(int offset) {
        return bytes[offset] + bytes[offset+1] * 0x100;
    }
}
