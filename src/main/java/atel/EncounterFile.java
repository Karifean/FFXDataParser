package atel;

import main.StringHelper;
import model.BattlePositionsDataObject;
import model.FormationDataObject;
import model.LocalizedStringObject;
import reading.Chunk;

import java.util.List;
import java.util.stream.Collectors;

import static main.DataReadingManager.DEFAULT_LOCALIZATION;

/**
 * jppc/battle/btl/.../.bin
 */
public class EncounterFile {
    public AtelScriptObject encounterScript;
    public FormationDataObject formation;
    public BattlePositionsDataObject battlePositions;
    Chunk scriptChunk;
    int[] workerMappingBytes;
    int[] formationBytes;
    int[] battlePositionsBytes;
    int[] textBytes;
    public List<LocalizedStringObject> strings;

    private int chunkCount;

    public EncounterFile(List<Chunk> chunks) {
        chunkCount = chunks.size();
        mapChunks(chunks);
        mapObjects();
        mapStrings();
    }

    private void mapChunks(List<Chunk> chunks) {
        scriptChunk = chunks.get(0);
        workerMappingBytes = chunks.get(1).bytes;
        formationBytes = chunks.get(2).bytes;
        battlePositionsBytes = chunks.get(3).bytes;
        if (chunks.size() > 6 && chunks.get(6).offset != 0) {
            textBytes = chunks.get(6).bytes;
        } else if (chunks.size() > 4 && chunks.get(4).offset != 0) {
            textBytes = chunks.get(4).bytes;
        }
    }

    private void mapObjects() {
        if (chunkCount == 5) {
            return;
        }
        encounterScript = new AtelScriptObject(scriptChunk, workerMappingBytes);
        if (formationBytes != null && formationBytes.length > 0) {
            formation = new FormationDataObject(formationBytes);
        }
        if (battlePositionsBytes != null && battlePositionsBytes.length > 0) {
            battlePositions = new BattlePositionsDataObject(battlePositionsBytes);
        }
    }

    private void mapStrings() {
        List<String> rawStrings = StringHelper.readStringData(textBytes, false);
        if (rawStrings != null) {
            strings = rawStrings.stream().map(str -> new LocalizedStringObject(DEFAULT_LOCALIZATION, str)).collect(Collectors.toList());
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
        if (encounterScript != null) {
            encounterScript.setStrings(strings);
            encounterScript.parseScript();
        }
    }

    @Override
    public String toString() {
        StringBuilder full = new StringBuilder();
        if (chunkCount != 8) {
            full.append("Unsafe format - ChunkCount is ").append(chunkCount).append('\n');
        }
        if (formation != null) {
            full.append("- Encounter Formation -\n").append(formation).append('\n');
        }
        if (battlePositions != null) {
            full.append("- Encounter Positions -\n").append(battlePositions).append('\n');
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
