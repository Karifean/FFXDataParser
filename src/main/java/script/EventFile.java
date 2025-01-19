package script;

import main.StringHelper;
import model.LocalizedStringObject;
import model.Nameable;
import reading.Chunk;

import java.util.List;
import java.util.stream.Collectors;

import static main.DataReadingManager.DEFAULT_LOCALIZATION;
import static main.StringHelper.MACRO_LOOKUP;

/**
 * jppc/event/.../.ebp
 */
public class EventFile implements Nameable {
    public ScriptObject eventScript;
    Chunk scriptChunk;
    int[] textBytes;
    List<LocalizedStringObject> strings;

    public EventFile(List<Chunk> chunks) {
        mapChunks(chunks);
        mapObjects();
        mapStrings();
    }

    private void mapChunks(List<Chunk> chunks) {
        scriptChunk = chunks.get(0);
        textBytes = chunks.size() > 4 ? chunks.get(4).bytes : null;
    }

    private void mapObjects() {
        eventScript = new ScriptObject(scriptChunk, null);
    }

    private void mapStrings() {
        List<String> rawStrings = StringHelper.readStringData(textBytes, false);
        if (rawStrings != null) {
            this.strings = rawStrings.stream().map(str -> new LocalizedStringObject(DEFAULT_LOCALIZATION, str)).collect(Collectors.toList());
        }
    }

    public void addLocalizations(List<LocalizedStringObject> strings) {
        if (this.strings == null) {
            this.strings = strings;
            return;
        }
        for (int i = 0; i < strings.size(); i++) {
            LocalizedStringObject localizationStringObject = strings.get(i);
            if (i < this.strings.size()) {
                LocalizedStringObject stringObject = this.strings.get(i);
                if (stringObject != null && localizationStringObject != null) {
                    localizationStringObject.copyInto(stringObject);
                }
            } else {
                this.strings.add(localizationStringObject);
            }
        }
    }

    public void parseScript() {
        if (eventScript != null) {
            eventScript.setStrings(strings);
            eventScript.parseScript();
        }
    }

    @Override
    public String toString() {
        StringBuilder full = new StringBuilder();
        full.append(getName()).append('\n');
        if (eventScript != null) {
            full.append("- Script Code -").append('\n');
            full.append(eventScript.allLinesString());
            full.append("- Headers -").append('\n');
            full.append(eventScript.headersString()).append('\n');
        } else {
            full.append("Event Script missing");
        }
        return full.toString();
    }

    public String getName(String localization) {
        if (eventScript == null || eventScript.areaNameIndexes == null || eventScript.areaNameIndexes.isEmpty()) {
            return null;
        }
        return MACRO_LOOKUP.get(0xB00 + eventScript.areaNameIndexes.get(0)).getLocalizedContent(localization);
    }
}
