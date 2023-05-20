package script;

import model.FormationDataObject;
import reading.Chunk;

import java.util.List;

/**
 * jppc/battle/btl/.../.bin
 */
public class EncounterFile {
    public ScriptObject encounterScript;
    public FormationDataObject formation;
    Chunk scriptChunk;
    int[] formationBytes;

    public EncounterFile(List<Chunk> chunks) {
        mapChunks(chunks);
        mapObjects(chunks.size());
    }

    private void mapChunks(List<Chunk> chunks) {
        scriptChunk = chunks.get(0);
        formationBytes = chunks.get(2).bytes;
    }

    private void mapObjects(int chunkCount) {
        encounterScript = new ScriptObject(scriptChunk);
        formation = new FormationDataObject(formationBytes);
    }

    public void parseScript() {
        if (encounterScript != null) {
            encounterScript.parseScript();
        }
    }

    @Override
    public String toString() {
        StringBuilder full = new StringBuilder();
        if (formation != null) {
            full.append("- Encounter Formation -\n").append(formation).append('\n');
        }
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
