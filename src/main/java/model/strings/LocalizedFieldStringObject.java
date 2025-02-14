package model.strings;

import main.DataReadingManager;
import main.StringHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static main.DataReadingManager.DEFAULT_LOCALIZATION;

public class LocalizedFieldStringObject {
    private final Map<String, FieldString> contents = new HashMap<>();

    public LocalizedFieldStringObject() {}

    public LocalizedFieldStringObject(String localization, FieldString content) {
        setLocalizedContent(localization, content);
    }

    public void setLocalizedContent(String localization, FieldString content) {
        contents.put(localization, content);
    }

    public void readAndSetLocalizedContent(String localization, int[] bytes, int regularHeader, int simplifiedHeader) {
        if (bytes == null || bytes.length == 0) {
            return;
        }
        contents.put(localization, new FieldString(StringHelper.localizationToCharset(localization), regularHeader, simplifiedHeader, bytes));
    }

    public String writeAllContent() {
        return DataReadingManager.LOCALIZATIONS.entrySet().stream().map((entry) -> "[" + entry.getValue() + "] " + contents.get(entry.getKey())).collect(Collectors.joining("\n"));
    }

    public FieldString getLocalizedContent(String localization) {
        return contents.get(localization);
    }

    public FieldString getDefaultContent() {
        return getLocalizedContent(DEFAULT_LOCALIZATION);
    }

    public void copyInto(LocalizedFieldStringObject other) {
        contents.forEach((localization, content) -> other.setLocalizedContent(localization, content));
    }

    @Override
    public String toString() {
        return getDefaultContent().toString();
    }
}
