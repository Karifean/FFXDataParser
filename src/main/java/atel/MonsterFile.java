package atel;

import main.DataWritingManager;
import main.StringHelper;
import model.Nameable;
import reading.Chunk;
import model.MonsterLootDataObject;
import model.MonsterStatDataObject;
import reading.BytesHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static reading.BytesHelper.read4Bytes;

/**
 * jppc/battle/mon/.../.bin
 */
public class MonsterFile implements Nameable {
    public Integer monsterIndex;
    public String scriptId;
    public AtelScriptObject monsterScript;
    public MonsterStatDataObject monsterStatData;
    public MonsterLootDataObject monsterLootData;
    int[] scriptBytes;
    int[] audioBytesApparently;
    int[] workerMappingBytes;
    int[] statBytes;
    int[] chunk3Bytes;
    int[] lootBytes;
    int[] englishTextBytes;

    private boolean scriptParsed = false;

    public MonsterFile(Integer monsterIndex, int[] bytes) {
        this.monsterIndex = monsterIndex;
        int chunkCount = read4Bytes(bytes, 0x00) - 1;
        List<Chunk> chunks = BytesHelper.bytesToChunks(bytes, chunkCount, 4);
        mapChunks(chunks);
        mapObjects();
    }

    private void mapChunks(List<Chunk> chunks) {
        scriptBytes = chunks.get(0).bytes;
        workerMappingBytes = chunks.get(1).bytes;
        statBytes = chunks.get(2).bytes;
        chunk3Bytes = chunks.get(3).bytes;
        lootBytes = chunks.get(4).bytes;
        audioBytesApparently = chunks.get(5).bytes;
        englishTextBytes = chunks.get(6).bytes;
    }

    private void mapObjects() {
        monsterScript = new AtelScriptObject(scriptBytes, workerMappingBytes);
        scriptId = monsterScript.scriptId;
        monsterStatData = new MonsterStatDataObject(statBytes, Arrays.copyOfRange(statBytes, MonsterStatDataObject.LENGTH, statBytes.length), "jp");
        monsterLootData = new MonsterLootDataObject(lootBytes);
        MonsterStatDataObject englishTextStatData = new MonsterStatDataObject(englishTextBytes, Arrays.copyOfRange(englishTextBytes, MonsterStatDataObject.LENGTH, englishTextBytes.length), "us");
        monsterStatData.setLocalizations(englishTextStatData);
    }

    public void parseScript() {
        if (monsterScript != null) {
            scriptParsed = true;
            monsterScript.parseScript();
        }
    }

    public int[] toBytes() {
        List<int[]> chunks = new ArrayList<>();
        if (AtelScriptObject.RECOMPILE_ATEL && scriptParsed) {
            AtelScriptObject.AtelScriptObjectBytes atelScriptObjectBytes = monsterScript.toBytes();
            chunks.add(atelScriptObjectBytes.bytes());
            chunks.add(atelScriptObjectBytes.battleWorkerMappingBytes());
        } else {
            chunks.add(scriptBytes);
            chunks.add(workerMappingBytes);
        }
        chunks.add(DataWritingManager.dataObjectWithStringsToBytes(monsterStatData, "jp"));
        chunks.add(chunk3Bytes);
        chunks.add(monsterLootData.toBytes(null));
        chunks.add(audioBytesApparently);
        chunks.add(DataWritingManager.dataObjectWithStringsToBytes(monsterStatData, "us"));
        return BytesHelper.chunksToBytes(chunks, 0x08, 0x30, 0x10);
    }

    @Override
    public String toString() {
        StringBuilder full = new StringBuilder();
        full.append(getName()).append(" (m").append(scriptId).append(')');
        if (monsterIndex != null) {
            full.append(StringHelper.hex4Suffix(monsterIndex + 0x1000));
        }
        full.append('\n');
        if (monsterScript != null) {
            full.append("- Script Code -").append('\n');
            full.append(monsterScript.allLinesString());
            full.append("- Script Workers -").append('\n');
            full.append(monsterScript.workersString()).append('\n');
        } else {
            full.append("Monster AI missing");
        }
        full.append("- Monster Stats -").append('\n');
        full.append(monsterStatData).append('\n');
        full.append("- Monster Loot -").append('\n');
        full.append(monsterLootData).append('\n');
        if (monsterStatData != null) {
            full.append("- Localized Strings -").append('\n');
            full.append("Name: ").append(monsterStatData.name.getDefaultContent()).append('\n');
            full.append("- Sensor Text -\n");
            full.append(monsterStatData.sensorText.getDefaultContent()).append('\n');
            full.append("- Scan Text -").append('\n');
            full.append(monsterStatData.scanText.getDefaultContent()).append('\n');
        }
        return full.toString();
    }

    public String getName(String localization) {
        return monsterStatData != null ? monsterStatData.getName(localization) : null;
    }
}
