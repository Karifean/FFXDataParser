package model.strings;

import main.DataReadingManager;
import main.StringHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static main.DataReadingManager.DEFAULT_LOCALIZATION;

public class LocalizedMacroStringObject {
    private final Map<String, MacroString> contents = new HashMap<>();

    public LocalizedMacroStringObject() {}

    public LocalizedMacroStringObject(String localization, MacroString content) {
        setLocalizedContent(localization, content);
    }

    public void setLocalizedContent(String localization, MacroString content) {
        if (contents.containsKey(localization) && content.isEmpty()) {
            return;
        }
        contents.put(localization, content);
    }

    public void readAndSetLocalizedContent(String localization, int[] bytes, int regularOffset, int simplifiedOffset) {
        if (bytes == null) {
            return;
        }
        setLocalizedContent(localization, new MacroString(StringHelper.localizationToCharset(localization), regularOffset, simplifiedOffset, bytes));
    }

    public String writeAllContent() {
        return DataReadingManager.LOCALIZATIONS.entrySet().stream().map((entry) -> "[" + entry.getValue() + "] " + contents.get(entry.getKey())).collect(Collectors.joining("\n"));
    }

    public MacroString getLocalizedContent(String localization) {
        return contents.get(localization);
    }

    public String getLocalizedString(String localization) {
        MacroString obj = getLocalizedContent(localization);
        return obj != null ? obj.getString() : null;
    }

    public MacroString getDefaultContent() {
        return getLocalizedContent(DEFAULT_LOCALIZATION);
    }

    public String getDefaultString() {
        String defaultString = getLocalizedString(DEFAULT_LOCALIZATION);
        return defaultString != null ? defaultString : "";
    }

    public void copyInto(LocalizedMacroStringObject other) {
        contents.forEach((localization, content) -> other.setLocalizedContent(localization, content));
    }

    @Override
    public String toString() {
        return getDefaultContent().toString();
    }
}
