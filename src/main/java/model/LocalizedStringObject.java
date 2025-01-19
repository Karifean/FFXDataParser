package model;

import main.DataReadingManager;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static main.DataReadingManager.DEFAULT_LOCALIZATION;

/**
 * ply_save.bin
 */
public class LocalizedStringObject {
    public static final String MISSING_STRING = "<Unset>";

    private final Map<String, String> contents = new HashMap<>();

    public LocalizedStringObject() {}

    public LocalizedStringObject(String localization, String content) {
        setLocalizedContent(localization, content);
    }

    public void setLocalizedContent(String localization, String content) {
        contents.put(localization, content);
    }

    public String getAllContent() {
        return DataReadingManager.LOCALIZATIONS.entrySet().stream().map((entry) -> "[" + entry.getValue() + "] " + contents.get(entry.getKey())).collect(Collectors.joining("\n"));
    }

    public String getLocalizedContent(String localization) {
        return contents.getOrDefault(localization, MISSING_STRING);
    }

    public String getDefaultContent() {
        return getLocalizedContent(DEFAULT_LOCALIZATION);
    }

    public void copyInto(LocalizedStringObject other) {
        contents.forEach((localization, content) -> other.setLocalizedContent(localization, content));
    }

    @Override
    public String toString() {
        return getDefaultContent();
    }
}
