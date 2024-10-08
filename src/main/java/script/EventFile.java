package script;

import main.StringHelper;
import model.Nameable;
import reading.Chunk;

import java.util.ArrayList;
import java.util.List;

import static main.StringHelper.MACRO_LOOKUP;

/**
 * jppc/event/.../.ebp
 */
public class EventFile implements Nameable {
    public ScriptObject eventScript;
    Chunk scriptChunk;
    int[] textBytes;
    List<String> originalStrings;

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
        originalStrings = StringHelper.readStringData(textBytes, false);
    }

    public void parseScript(List<String> strings) {
        if (eventScript != null) {
            List<String> combinedStrings = originalStrings != null ? new ArrayList<>(originalStrings) : new ArrayList<>();
            if (strings != null && !strings.isEmpty()) {
                for (int i = 0; i < strings.size(); i++) {
                    String str = strings.get(i);
                    if (str != null && !str.isBlank()) {
                        if (i < combinedStrings.size()) {
                            combinedStrings.set(i, str);
                        } else {
                            combinedStrings.add(str);
                        }
                    }
                }
            }
            eventScript.parseScript(combinedStrings);
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

    public String getName() {
        if (eventScript == null || eventScript.areaNameIndexes == null || eventScript.areaNameIndexes.isEmpty()) {
            return null;
        }
        return MACRO_LOOKUP.get(0xB00 + eventScript.areaNameIndexes.get(0));
    }
}
