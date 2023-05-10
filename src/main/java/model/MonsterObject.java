package model;

import main.Main;
import script.ScriptObject;

import java.io.*;
import java.util.*;

public class MonsterObject {
    File file;
    DataInputStream data;
    public ScriptObject monsterAi;
    public MonsterDataObject monsterData;
    public boolean isMonsterFile;
    public String monsterName;
    public String monsterSensorText;
    public String monsterSensorDash;
    public String monsterScanText;
    public String monsterScanDash;
    public StringBuilder monsterText = new StringBuilder();
    int[] aiBytes;
    int[] statBytes = new int[0x8C];
    int[] spoilsBytes;

    public MonsterObject(File file, boolean isMonsterFile) {
        this.file = file;
        this.isMonsterFile = isMonsterFile;
    }

    private void newDataStream() throws FileNotFoundException {
        data = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
    }

    public void run() throws IOException {
        newDataStream();
        data.mark(40);
        int absoluteBeginAddress = read4Bytes();
        int scriptBeginAddress = read4Bytes();
        int unknownChunkAddress = read4Bytes();
        int valuesBeginAddress = read4Bytes();
        int nullAddress1 = read4Bytes();
        int spoilsBeginAddress = read4Bytes();
        int spoilsEndAddress = read4Bytes();
        int textPartAddress = read4Bytes();
        int totalFileBytes = read4Bytes();
        data.reset();
        data.skipNBytes(scriptBeginAddress);
        int nextChunkAfterScript = unknownChunkAddress > 0 ? unknownChunkAddress : valuesBeginAddress;
        int totalScriptLength = nextChunkAfterScript - scriptBeginAddress;
        aiBytes = new int[totalScriptLength];
        for (int i = 0; i < totalScriptLength; i++) {
            aiBytes[i] = data.read();
        }
        monsterAi = new ScriptObject(aiBytes);
        monsterAi.run();
        if (isMonsterFile) {
            newDataStream();
            data.mark(valuesBeginAddress + 0x8C);
            data.skipNBytes(valuesBeginAddress);
            for (int i = 0; i < 0x8C; i++) {
                statBytes[i] = data.read();
            }
            data.reset();
            data.mark(spoilsEndAddress);
            data.skipNBytes(spoilsBeginAddress);
            int spoilsLength = spoilsEndAddress - spoilsBeginAddress;
            spoilsBytes = new int[spoilsLength];
            for (int i = 0; i < spoilsLength; i++) {
                spoilsBytes[i] = data.read();
            }
            monsterData = new MonsterDataObject(statBytes, spoilsBytes);
            data.reset();
            data.skipNBytes(textPartAddress);
            int nameOffset = read2Bytes();
            data.skipNBytes(2);
            int sensorOffset = read2Bytes();
            data.skipNBytes(2);
            int sensorDashOffset = read2Bytes();
            data.skipNBytes(2);
            int scanOffset = read2Bytes();
            data.skipNBytes(2);
            int scanDashOffset = read2Bytes();
            data.skipNBytes(0x6E);
            List<Integer> offsets = List.of(nameOffset, sensorOffset, sensorDashOffset, scanOffset, scanDashOffset);
            List<String> strings = Main.readStringsAtOffsets(5, offsets, data, false, null);
            monsterName = strings.get(0);
            monsterSensorText = strings.get(1);
            monsterSensorDash = strings.get(2);
            monsterScanText = strings.get(3);
            monsterScanDash = strings.get(4);
            strings.forEach(s -> monsterText.append(s).append('\n'));
        }
    }

    private void readRemainingText() throws IOException {
        while (data.available() > 0) {
            int character = data.read();
            if (Main.BIN_LOOKUP.containsKey(character)) {
                monsterText.append(Main.BIN_LOOKUP.get(character));
            }
        }
    }

    private int read2Bytes() throws IOException {
        int val = data.read();
        val += data.read() * 0x100;
        return val;
    }

    private int read4Bytes() throws IOException {
        int val = data.read();
        val += data.read() * 0x100;
        val += data.read() * 0x10000;
        val += data.read() * 0x1000000;
        return val;
    }
}
