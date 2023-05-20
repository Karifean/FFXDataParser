package script;

import java.util.List;

/**
 * jppc/battle/btl/.../.bin
 */
public class EncounterFile {
    public ScriptObject encounterScript;
    int[] scriptBytes;

    public EncounterFile(List<int[]> chunks) {
        mapChunks(chunks);
        mapObjects();
    }

    private void mapChunks(List<int[]> chunks) {
        scriptBytes = chunks.get(0);
    }

    private void mapObjects() {
        encounterScript = new ScriptObject(scriptBytes);
    }

    public void parseScript() {
        if (encounterScript != null) {
            encounterScript.parseScript();
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
