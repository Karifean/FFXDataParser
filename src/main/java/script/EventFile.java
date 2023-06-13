package script;

import reading.Chunk;

import java.util.List;

/**
 * jppc/event/.../.ebp
 */
public class EventFile {
    public ScriptObject eventScript;
    Chunk scriptChunk;

    public EventFile(List<Chunk> chunks) {
        mapChunks(chunks);
        mapObjects();
    }

    private void mapChunks(List<Chunk> chunks) {
        scriptChunk = chunks.get(0);
    }

    private void mapObjects() {
        eventScript = new ScriptObject(scriptChunk);
    }

    public void parseScript(List<String> strings) {
        if (eventScript != null) {
            eventScript.parseScript(strings);
        }
    }

    @Override
    public String toString() {
        StringBuilder full = new StringBuilder();
        if (eventScript != null) {
            full.append("- Script Code -").append('\n');
            full.append(eventScript.allLinesString());
            full.append("- Headers -").append('\n');
            full.append(eventScript.headersString()).append('\n');
            full.append("- Jump Table -").append('\n');
            full.append(eventScript.jumpTableString.toString()).append('\n');
        } else {
            full.append("Event Script missing");
        }
        return full.toString();
    }
}
