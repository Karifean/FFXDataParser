package model.strings;

import main.DataReadingManager;
import main.StringHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static main.DataReadingManager.DEFAULT_LOCALIZATION;

public class LocalizedKeyedStringObject {
    private final Map<String, KeyedString> contents = new HashMap<>();

    public LocalizedKeyedStringObject() {}

    public LocalizedKeyedStringObject(String localization, KeyedString content) {
        setLocalizedContent(localization, content);
    }

    public void setLocalizedContent(String localization, KeyedString content) {
        contents.put(localization, content);
    }

    public void readAndSetLocalizedContent(String localization, int[] bytes, int offset, int key) {
        contents.put(localization, new KeyedString(StringHelper.localizationToCharset(localization), offset, key, bytes));
    }

    public String writeAllContent() {
        return DataReadingManager.LOCALIZATIONS.entrySet().stream().map((entry) -> "[" + entry.getValue() + "] " + contents.get(entry.getKey())).collect(Collectors.joining("\n"));
    }

    public KeyedString getLocalizedContent(String localization) {
        return contents.get(localization);
    }

    public KeyedString updateLocalizedContent(String localization) {
        return contents.get(localization);
    }

    public String getLocalizedString(String localization) {
        KeyedString obj = getLocalizedContent(localization);
        return obj != null ? obj.getString() : null;
    }

    public KeyedString getDefaultContent() {
        return getLocalizedContent(DEFAULT_LOCALIZATION);
    }

    public void copyInto(LocalizedKeyedStringObject other) {
        contents.forEach((localization, content) -> other.setLocalizedContent(localization, content));
    }

    @Override
    public String toString() {
        return getDefaultContent().toString();
    }
}
