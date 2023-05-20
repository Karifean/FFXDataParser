package script;

import main.StringHelper;
import model.MonsterSpoilsDataObject;
import model.MonsterStatDataObject;

import java.util.Arrays;
import java.util.List;

/**
 * jppc/battle/mon/.../.bin
 */
public class MonsterFile {
    public ScriptObject monsterAi;
    public MonsterStatDataObject monsterStatData;
    public MonsterSpoilsDataObject monsterSpoilsData;
    public String monsterName;
    public String monsterSensorText;
    public String monsterSensorDash;
    public String monsterScanText;
    public String monsterScanDash;
    int[] aiBytes;
    int[] statBytes = new int[0x8C];
    int[] spoilsBytes = new int[0x124];
    int[] textBytes;

    public MonsterFile(List<int[]> chunks) {
        mapChunks(chunks);
        mapObjects();
        mapStrings();
    }

    private void mapChunks(List<int[]> chunks) {
        aiBytes = chunks.get(0);
        statBytes = chunks.get(2);
        spoilsBytes = chunks.get(4);
        textBytes = chunks.get(6);
    }

    private void mapObjects() {
        monsterAi = new ScriptObject(aiBytes);
        monsterStatData = new MonsterStatDataObject(statBytes);
        monsterSpoilsData = new MonsterSpoilsDataObject(spoilsBytes);
    }

    private void mapStrings() {
        int nameOffset = textBytes[0x00] + textBytes[0x01] * 0x100;
        int sensorOffset = textBytes[0x04] + textBytes[0x05] * 0x100;
        int sensorDashOffset = textBytes[0x08] + textBytes[0x09] * 0x100;
        int scanOffset = textBytes[0x0C] + textBytes[0x0D] * 0x100;
        int scanDashOffset = textBytes[0x10] + textBytes[0x11] * 0x100;
        int[] textBytesWithOffset = Arrays.copyOfRange(textBytes, 0x80, textBytes.length);
        monsterName = StringHelper.getStringAtLookupOffset(textBytesWithOffset, nameOffset);
        monsterSensorText = StringHelper.getStringAtLookupOffset(textBytesWithOffset, sensorOffset);
        monsterSensorDash = StringHelper.getStringAtLookupOffset(textBytesWithOffset, sensorDashOffset);
        monsterScanText = StringHelper.getStringAtLookupOffset(textBytesWithOffset, scanOffset);
        monsterScanDash = StringHelper.getStringAtLookupOffset(textBytesWithOffset, scanDashOffset);
    }

    public void parseScript() {
        if (monsterAi != null) {
            monsterAi.parseScript();
        }
    }

    @Override
    public String toString() {
        StringBuilder full = new StringBuilder();
        full.append(monsterName).append('\n');
        if (monsterAi != null) {
            full.append("- Script Code -").append('\n');
            full.append(monsterAi.allLinesString());
            full.append("- Jump Table -").append('\n');
            full.append(monsterAi.jumpTableString.toString()).append('\n');
        } else {
            full.append("Monster AI missing");
        }
        full.append("- Monster Stats -").append('\n');
        full.append(monsterStatData).append('\n');
        /* full.append("- Monster Spoils -").append('\n');
        full.append(monsterSpoilsData).append('\n');
        full.append("- Sensor Text -").append('\n');
        full.append(monsterSensorText).append('\n');
        full.append(monsterSensorDash).append('\n');
        full.append("- Scan Text -").append('\n');
        full.append(monsterScanText).append('\n');
        full.append(monsterScanDash).append('\n'); */
        return full.toString();
    }

    public String getName() {
        return monsterName;
    }
}
