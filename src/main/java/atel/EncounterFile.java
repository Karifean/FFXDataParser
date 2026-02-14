package atel;

import atel.model.ScriptConstants;
import main.DataWritingManager;
import main.StringHelper;
import model.BattleAreasPositionsDataObject;
import model.FormationDataObject;
import model.Nameable;
import model.strings.FieldString;
import model.strings.LocalizedFieldStringObject;
import reading.Chunk;
import reading.BytesHelper;
import reading.FileAccessorWithMods;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static main.DataReadingManager.PATH_ORIGINALS_ENCOUNTER;
import static main.DataReadingManager.PATH_ORIGINALS_EVENT;
import static main.StringHelper.MACRO_LOOKUP;
import static model.FormationDataObject.writeMonster;
import static reading.BytesHelper.read4Bytes;

/**
 * jppc/battle/btl/.../.bin
 */
public class EncounterFile implements Nameable {
    public String filename;
    public String scriptId;
    public AtelScriptObject encounterScript;
    public FormationDataObject formation;
    public BattleAreasPositionsDataObject battleAreasPositions;
    public int binaryLength;
    int[] scriptBytes;
    int[] workerMappingBytes;
    int[] formationBytes;
    int[] battleAreasPositionsBytes;
    int[] japaneseTextBytes;
    int[] ftcxBytes;
    int[] englishTextBytes;
    public List<LocalizedFieldStringObject> strings;

    private final int chunkCount;
    private boolean scriptParsed = false;

    public EncounterFile(String filename, int[] bytes, boolean isInpc) {
        binaryLength = bytes.length;
        this.filename = filename;
        chunkCount = read4Bytes(bytes, 0x00) - 1;
        List<Chunk> chunks = BytesHelper.bytesToChunks(bytes, chunkCount, 4);
        if (chunkCount != chunks.size()) {
            System.err.println("mismatch!!");
        }
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
        scriptId = encounterScript.scriptId;
        if (chunkCount == 4) {
            return;
        }
        if (formationBytes != null && formationBytes.length > 0) {
            formation = new FormationDataObject(formationBytes);
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
        parseScript(false);
    }

    public void parseScript(boolean force) {
        if (encounterScript != null && (force || !scriptParsed)) {
            scriptParsed = true;
            encounterScript.setStrings(strings);
            encounterScript.parseScript();
        }
    }

    public int[] toBytes() {
        List<int[]> chunks = new ArrayList<>();
        if (AtelScriptObject.RECOMPILE_ATEL && scriptParsed) {
            AtelScriptObject.AtelScriptObjectBytes atelScriptObjectBytes = encounterScript.toBytes();
            chunks.add(atelScriptObjectBytes.bytes());
            chunks.add(atelScriptObjectBytes.battleWorkerMappingBytes());
        } else {
            chunks.add(scriptBytes);
            chunks.add(workerMappingBytes);
        }
        chunks.add(formationBytes);
        chunks.add(battleAreasPositionsBytes);
        chunks.add(DataWritingManager.stringsToStringFileBytes(strings, "jp"));
        chunks.add(ftcxBytes);
        chunks.add(DataWritingManager.stringsToStringFileBytes(strings, "us"));
        return BytesHelper.chunksToBytes(chunks, 0x08, 0x40, 0x10);
    }

    public void writeToMods(boolean writeStrings, boolean writeDeclarations) {
        String path = PATH_ORIGINALS_ENCOUNTER + scriptId + '/' + scriptId;
        int[] bytes = toBytes();
        FileAccessorWithMods.writeByteArrayToMods(path + ".bin", bytes);
        binaryLength = bytes.length;
        if (writeStrings) {
            DataWritingManager.writeEncounterStringsForAllLocalizations(this, false);
        }
        if (writeDeclarations) {
            FileAccessorWithMods.writeStringToMods(path + ".dcl.csv", encounterScript.getDeclarationsAsString());
        }
    }

    public int getIndex() {
        String[] split = scriptId.split("_");
        String map = split[0];
        Integer baseInt = ScriptConstants.FFX.MAPS_IN_REVERSE.get(map);
        if (baseInt == null) {
            return -1;
        }
        int enc = split.length > 1 ? Integer.parseInt(split[1]) : 0;
        return (baseInt << 16) | enc;
    }

    public String getName(String localization) {
        String id = scriptId != null ? scriptId : filename;
        return id + " - " + getEnemyFormationLabel(localization);
    }

    public String getEnemyFormationLabel(String localization) {
        if (formation == null) {
            return "(No Formation)";
        }
        List<String> monsterLabels = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            int monsterIndex = formation.monsters[i];
            if (monsterIndex != 0xFFFF) {
                monsterLabels.add(writeMonster(monsterIndex, localization));
            }
        }
        if (monsterLabels.isEmpty()) {
            return "(Empty)";
        } else {
            return "[" + String.join(", ", monsterLabels) + "]";
        }
    }

    @Override
    public String toString() {
        StringBuilder full = new StringBuilder();
        full.append(scriptId).append('\n');
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
