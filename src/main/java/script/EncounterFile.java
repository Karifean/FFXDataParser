package script;

import main.StringHelper;
import model.FormationDataObject;
import reading.Chunk;

import java.util.ArrayList;
import java.util.List;

/**
 * jppc/battle/btl/.../.bin
 */
public class EncounterFile {
    public ScriptObject encounterScript;
    public FormationDataObject formation;
    Chunk scriptChunk;
    int[] workerMappingBytes;
    int[] formationBytes;
    int[] textBytes;
    List<String> originalStrings;

    public EncounterFile(List<Chunk> chunks) {
        mapChunks(chunks);
        mapObjects(chunks.size());
        mapStrings();
    }

    private void mapChunks(List<Chunk> chunks) {
        scriptChunk = chunks.get(0);
        workerMappingBytes = chunks.get(1).bytes;
        formationBytes = chunks.get(2).bytes;
        textBytes = chunks.size() > 6 ? chunks.get(6).bytes : null;
    }

    private void mapObjects(int chunkCount) {
        encounterScript = new ScriptObject(scriptChunk, workerMappingBytes);
        if (formationBytes != null && formationBytes.length > 0) {
            formation = new FormationDataObject(formationBytes);
        }
    }

    private void mapStrings() {
        originalStrings = StringHelper.readStringData(textBytes, false);
    }

    public void parseScript(List<String> strings) {
        if (encounterScript != null) {
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
            encounterScript.parseScript(combinedStrings);
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
            full.append("- Headers -").append('\n');
            full.append(encounterScript.headersString()).append('\n');
        } else {
            full.append("Encounter Script missing");
        }
        return full.toString();
    }
}
