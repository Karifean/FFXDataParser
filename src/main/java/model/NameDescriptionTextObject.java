package model;

import model.strings.KeyedString;
import model.strings.LocalizedKeyedStringObject;

import java.util.stream.Stream;

import static reading.BytesHelper.write4Bytes;

/**
 * status_txt.bin
 */
public class NameDescriptionTextObject implements Nameable, Writable, Localized<NameDescriptionTextObject> {
    public static final int LENGTH = 0x10;
    private final int[] bytes;

    public LocalizedKeyedStringObject name = new LocalizedKeyedStringObject();
    public LocalizedKeyedStringObject simplifiedName = new LocalizedKeyedStringObject();
    public LocalizedKeyedStringObject description = new LocalizedKeyedStringObject();
    public LocalizedKeyedStringObject simplifiedDescription = new LocalizedKeyedStringObject();

    private int nameOffset;
    private int nameKey;
    private int simplifiedNameOffset;
    private int simplifiedNameKey;
    private int descriptionOffset;
    private int descriptionKey;
    private int simplifiedDescriptionOffset;
    private int simplifiedDescriptionKey;

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
        simplifiedNameOffset = read2Bytes(0x04);
        simplifiedNameKey = read2Bytes(0x06);
        descriptionOffset = read2Bytes(0x08);
        descriptionKey = read2Bytes(0x0A);
        simplifiedDescriptionOffset = read2Bytes(0x0C);
        simplifiedDescriptionKey = read2Bytes(0x0E);
    }

    @Override
    public int[] toBytes(String localization) {
        int[] array = new int[NameDescriptionTextObject.LENGTH];
        write4Bytes(array, 0x00, name.getLocalizedContent(localization).toHeaderBytes());
        write4Bytes(array, 0x04, simplifiedName.getLocalizedContent(localization).toHeaderBytes());
        write4Bytes(array, 0x08, description.getLocalizedContent(localization).toHeaderBytes());
        write4Bytes(array, 0x0C, simplifiedDescription.getLocalizedContent(localization).toHeaderBytes());
        return array;
    }

    private void mapStrings(int[] stringBytes, String localization) {
        name.readAndSetLocalizedContent(localization, stringBytes, nameOffset, nameKey);
        simplifiedName.readAndSetLocalizedContent(localization, stringBytes, simplifiedNameOffset, simplifiedNameKey);
        description.readAndSetLocalizedContent(localization, stringBytes, descriptionOffset, descriptionKey);
        simplifiedDescription.readAndSetLocalizedContent(localization, stringBytes, simplifiedDescriptionOffset, simplifiedDescriptionKey);
    }

    @Override
    public void setLocalizations(NameDescriptionTextObject localizationObject) {
        localizationObject.name.copyInto(name);
        localizationObject.simplifiedName.copyInto(simplifiedName);
        localizationObject.description.copyInto(description);
        localizationObject.simplifiedDescription.copyInto(simplifiedDescription);
    }

    @Override
    public Stream<KeyedString> streamKeyedStrings(String localization) {
        return Stream.of(
                name.getLocalizedContent(localization),
                simplifiedName.getLocalizedContent(localization),
                description.getLocalizedContent(localization),
                simplifiedDescription.getLocalizedContent(localization)
        );
    }

    @Override
    public LocalizedKeyedStringObject getKeyedString(String title) {
        return switch (title) {
            case "name" -> name;
            case "simplifiedName" -> simplifiedName;
            case "description" -> description;
            case "simplifiedDescription" -> simplifiedDescription;
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
