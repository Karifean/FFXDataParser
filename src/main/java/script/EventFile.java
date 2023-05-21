package script;

import reading.Chunk;

import java.util.List;

/**
 * jppc/event/.../.ebp
 */
public class EventFile {
    public ScriptObject encounterScript;
    Chunk scriptChunk;

    public EventFile(List<Chunk> chunks) {
        mapChunks(chunks);
        mapObjects();
    }

    private void mapChunks(List<Chunk> chunks) {
        scriptChunk = chunks.get(0);
    }

    private void mapObjects() {
        encounterScript = new ScriptObject(scriptChunk);
    }

    public void parseScript(List<String> strings) {
        if (encounterScript != null) {
            encounterScript.parseScript(strings);
        }
    }

    @Override
    public String toString() {
        StringBuilder full = new StringBuilder();
        if (encounterScript != null) {
            full.append("- Script Code -").append('\n');
            full.append(encounterScript.allLinesString());
            full.append("- Jump Table -").append('\n');
            full.append(encounterScript.jumpTableString.toString()).append('\n');
        } else {
            full.append("Encounter Script missing");
        }
        return full.toString();
    }
}
