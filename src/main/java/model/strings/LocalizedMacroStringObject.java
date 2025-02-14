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
        contents.put(localization, content);
    }

    public void readAndSetLocalizedContent(String localization, int[] bytes, int regularOffset, int simplifiedOffset) {
        if (bytes == null || bytes.length == 0) {
            return;
        }
        contents.put(localization, new MacroString(StringHelper.localizationToCharset(localization), regularOffset, simplifiedOffset, bytes));
    }

    public String writeAllContent() {
        return DataReadingManager.LOCALIZATIONS.entrySet().stream().map((entry) -> "[" + entry.getValue() + "] " + contents.get(entry.getKey())).collect(Collectors.joining("\n"));
    }

    public MacroString getLocalizedContent(String localization) {
        return contents.get(localization);
    }

    public MacroString getDefaultContent() {
        return getLocalizedContent(DEFAULT_LOCALIZATION);
    }

    public void copyInto(LocalizedMacroStringObject other) {
        contents.forEach((localization, content) -> other.setLocalizedContent(localization, content));
    }

    @Override
    public String toString() {
        return getDefaultContent().toString();
    }
}
