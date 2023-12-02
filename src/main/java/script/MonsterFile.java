package script;

import model.Nameable;
import reading.Chunk;
import model.MonsterSpoilsDataObject;
import model.MonsterStatDataObject;

import java.util.Arrays;
import java.util.List;

/**
 * jppc/battle/mon/.../.bin
 */
public class MonsterFile implements Nameable {
    public ScriptObject monsterAi;
    public MonsterStatDataObject monsterStatData;
    public MonsterSpoilsDataObject monsterSpoilsData;
    public MonsterStatDataObject englishTextStatData;
    public MonsterStatDataObject monsterLocalizationData;
    Chunk scriptChunk;
    Chunk audioChunkApparently;
    int[] workerMappingBytes;
    int[] statBytes;
    int[] spoilsBytes;
    int[] englishTextBytes;

    public MonsterFile(List<Chunk> chunks) {
        mapChunks(chunks);
        mapObjects();
    }

    private void mapChunks(List<Chunk> chunks) {
        scriptChunk = chunks.get(0);
        workerMappingBytes = chunks.get(1).bytes;
        statBytes = chunks.get(2).bytes;
        spoilsBytes = chunks.get(4).bytes;
        audioChunkApparently = chunks.get(5);
        englishTextBytes = chunks.get(6).bytes;
    }

    private void mapObjects() {
        monsterAi = new ScriptObject(scriptChunk, workerMappingBytes);
        monsterStatData = new MonsterStatDataObject(statBytes, Arrays.copyOfRange(statBytes, MonsterStatDataObject.LENGTH, statBytes.length));
        monsterSpoilsData = new MonsterSpoilsDataObject(spoilsBytes);
        englishTextStatData = new MonsterStatDataObject(englishTextBytes, Arrays.copyOfRange(englishTextBytes, MonsterStatDataObject.LENGTH, englishTextBytes.length));
        // englishTextStatData = monsterStatData; For PS2
    }

    public void parseScript() {
        if (monsterAi != null) {
            monsterAi.parseScript(null);
        }
    }

    @Override
    public String toString() {
        StringBuilder full = new StringBuilder();
        full.append(getName()).append('\n');
        if (monsterAi != null) {
            full.append("- Script Code -").append('\n');
            full.append(monsterAi.allLinesString());
            full.append("- Headers -").append('\n');
            full.append(monsterAi.headersString()).append('\n');
        } else {
            full.append("Monster AI missing");
        }
        full.append("- Monster Stats -").append('\n');
        full.append(monsterStatData).append('\n');
        full.append("- Monster Spoils -").append('\n');
        full.append(monsterSpoilsData).append('\n');
        if (monsterLocalizationData != null) {
            full.append("- Localized Strings -").append('\n');
            full.append("Name: ").append(monsterLocalizationData.monsterName).append('\n');
            full.append("- Sensor Text -").append('\n');
            full.append(monsterLocalizationData.monsterSensorText).append('\n');
            if (!"-".equals(monsterLocalizationData.monsterSensorDash)) {
                full.append("DH=").append(monsterLocalizationData.monsterSensorDash).append('\n');
            }
            full.append("- Scan Text -").append('\n');
            full.append(monsterLocalizationData.monsterScanText).append('\n');
            if (!"-".equals(monsterLocalizationData.monsterScanDash)) {
                full.append("DH=").append(monsterLocalizationData.monsterScanDash).append('\n');
            }
        } else if (englishTextStatData != null) {
            full.append("- Unlocalized Strings -").append('\n');
            full.append("Name: ").append(englishTextStatData.monsterName).append('\n');
            full.append("- Sensor Text -").append('\n');
            full.append(englishTextStatData.monsterSensorText).append('\n');
            if (!"-".equals(englishTextStatData.monsterSensorDash)) {
                full.append("DH=").append(englishTextStatData.monsterSensorDash).append('\n');
            }
            full.append("- Scan Text -").append('\n');
            full.append(englishTextStatData.monsterScanText).append('\n');
            if (!"-".equals(englishTextStatData.monsterScanDash)) {
                full.append("DH=").append(englishTextStatData.monsterScanDash).append('\n');
            }
        }
        return full.toString();
    }

    public String getName() {
        return englishTextStatData != null ? englishTextStatData.monsterName : null;
    }
}
