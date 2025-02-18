package atel;

import model.BattleAreasPositionsDataObject;
import model.FormationDataObject;
import model.strings.FieldString;
import model.strings.LocalizedFieldStringObject;
import reading.Chunk;

import java.util.List;
import java.util.stream.Collectors;

/**
 * jppc/battle/btl/.../.bin
 */
public class EncounterFile {

    public String id;
    public AtelScriptObject encounterScript;
    public FormationDataObject formation;
    public BattleAreasPositionsDataObject battleAreasPositions;
    int[] scriptBytes;
    int[] workerMappingBytes;
    int[] formationBytes;
    int[] battleAreasPositionsBytes;
    int[] japaneseTextBytes;
    int[] ftcxBytes;
    int[] englishTextBytes;
    public List<LocalizedFieldStringObject> strings;

    private int chunkCount;

    public EncounterFile(String id, List<Chunk> chunks, boolean isInpc) {
        this.id = id;
        chunkCount = chunks.size();
        mapChunks(chunks, isInpc);
        mapObjects();
        mapStrings();
    }

    private void mapChunks(List<Chunk> chunks, boolean isInpc) {
        scriptBytes = chunks.get(0).bytes;
        workerMappingBytes = chunks.get(1).bytes;
        formationBytes = chunks.get(2).bytes;
        battleAreasPositionsBytes = chunks.get(3).bytes;
        if (chunks.size() > 4 && chunks.get(4).offset != 0) {
            if (isInpc) {
                englishTextBytes = chunks.get(4).bytes;
            } else {
                japaneseTextBytes = chunks.get(4).bytes;
            }
        }
        if (chunks.size() > 5 && chunks.get(5).offset != 0) {
            ftcxBytes = chunks.get(5).bytes;
        }
        if (chunks.size() > 6 && chunks.get(6).offset != 0) {
            // Yes, in inpc/.../test00_12.bin, chunks 4 and 6 are apparently identical, both the englishTextBytes
            englishTextBytes = chunks.get(6).bytes;
        }
    }

    private void mapObjects() {
        encounterScript = new AtelScriptObject(scriptBytes, workerMappingBytes);
        if (formationBytes != null && formationBytes.length > 0) {
            formation = new FormationDataObject(formationBytes);
        }
        if (chunkCount == 4) {
            return;
        }
        if (battleAreasPositionsBytes != null && battleAreasPositionsBytes.length > 0) {
            battleAreasPositions = new BattleAreasPositionsDataObject(battleAreasPositionsBytes);
        }
    }

    private void mapStrings() {
        List<FieldString> japaneseStrings = FieldString.fromStringData(japaneseTextBytes, false, "jp");
        if (japaneseStrings != null) {
            List<LocalizedFieldStringObject> localizedJpStringObjects = japaneseStrings.stream().map(str -> new LocalizedFieldStringObject("jp", str)).collect(Collectors.toList());
            addLocalizations(localizedJpStringObjects);
        }
        List<FieldString> englishStrings = FieldString.fromStringData(englishTextBytes, false, "us");
        if (englishStrings != null) {
            List<LocalizedFieldStringObject> localizedUsStringObjects = englishStrings.stream().map(str -> new LocalizedFieldStringObject("us", str)).collect(Collectors.toList());
            addLocalizations(localizedUsStringObjects);
        }
    }

    public void addLocalizations(List<LocalizedFieldStringObject> strings) {
        if (strings == null) {
            return;
        }
        if (this.strings == null) {
            this.strings = strings;
            return;
        }
        for (int i = 0; i < strings.size(); i++) {
            LocalizedFieldStringObject localizationStringObject = strings.get(i);
            if (i < this.strings.size()) {
                LocalizedFieldStringObject stringObject = this.strings.get(i);
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
        if (chunkCount == 4) {
            full.append("Unsafe format - ChunkCount is ").append(chunkCount).append('\n');
        }
        if (formation != null) {
            full.append("- Encounter Formation -\n").append(formation).append('\n');
        }
        if (battleAreasPositions != null) {
            full.append("- Encounter Areas/Positions -\n").append(battleAreasPositions).append('\n');
        }
        if (encounterScript != null) {
            full.append("- Script Code -").append('\n');
            full.append(encounterScript.allLinesString());
            full.append("- Script Workers -").append('\n');
            full.append(encounterScript.workersString()).append('\n');
        } else {
            full.append("Encounter Script missing");
        }
        return full.toString();
    }
}
