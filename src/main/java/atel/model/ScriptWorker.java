package atel.model;

import atel.AtelScriptObject;
import main.StringHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static reading.BytesHelper.read2Bytes;
import static reading.BytesHelper.read4Bytes;

public class ScriptWorker {
    public static final int LENGTH = 0x34;

    public final int workerIndex;
    public final int eventWorkerType;
    public final int variablesCount;
    public final int refIntCount;
    public final int refFloatCount;
    public final int entryPointCount;
    public final int jumpCount;
    public final int alwaysZero0C;
    public final int privateDataLength;
    public final int variableStructsTableOffset;
    public final int intTableOffset;
    public final int floatTableOffset;
    public final int scriptEntryPointsOffset;
    public final int jumpsOffset;
    public final int alwaysZero28;
    public final int privateDataOffset;
    public final int sharedDataOffset;

    public ScriptJump[] entryPoints;
    public ScriptJump[] jumps;
    public ScriptVariable[] variableDeclarations;
    public int[] refFloats;
    public int[] refInts;
    public List<ScriptVariable> privateVars;
    public List<ScriptVariable> sharedVars;
    public ScriptJump[] entryPointBattleSlotArray;

    public Integer battleWorkerType;
    public Integer purposeSlot;

    public AtelScriptObject parentScript;

    public ScriptWorker(AtelScriptObject parentScript, int workerIndex, int[] bytes) {
        this.parentScript = parentScript;
        this.workerIndex = workerIndex;
        eventWorkerType = read2Bytes(bytes,0x00);
        variablesCount = read2Bytes(bytes,0x02);
        refIntCount = read2Bytes(bytes,0x04);
        refFloatCount = read2Bytes(bytes, 0x06);
        entryPointCount = read2Bytes(bytes, 0x08);
        jumpCount = read2Bytes(bytes, 0x0A);
        alwaysZero0C = read4Bytes(bytes, 0x0C);
        privateDataLength = read4Bytes(bytes, 0x10);
        variableStructsTableOffset = read4Bytes(bytes, 0x14);
        intTableOffset = read4Bytes(bytes, 0x18);
        floatTableOffset = read4Bytes(bytes, 0x1C);
        scriptEntryPointsOffset = read4Bytes(bytes, 0x20);
        jumpsOffset = read4Bytes(bytes, 0x24);
        alwaysZero28 = read4Bytes(bytes, 0x28);
        privateDataOffset = read4Bytes(bytes, 0x2C);
        sharedDataOffset = read4Bytes(bytes, 0x30);
    }

    public String toString() {
        return getIndexLabel();
    }

    public String getIndexLabel() {
        return "w" + StringHelper.formatHex2(workerIndex);
    }

    public String getNonCommonString() {
        List<String> list = new ArrayList<>();
        if (battleWorkerType != null) {
            list.add("Battle");
            list.add("Type=" + StackObject.enumToString("battleWorkerType", battleWorkerType));
        } else {
            list.add("Event");
            list.add("Type=" + StackObject.enumToString("eventWorkerType", eventWorkerType));
        }
        if (purposeSlot != null) {
            list.add("PurposeSlot=" + purposeSlotToString(purposeSlot) + StringHelper.hex2Suffix(purposeSlot));
        }
        list.add("Entrypoints=" + StringHelper.hex2WithSuffix(entryPointCount));
        list.add("Jumps=" + StringHelper.hex2WithSuffix(jumpCount));
        list.add("privateData addr=" + StringHelper.formatHex4(privateDataOffset) + " len=" + StringHelper.formatHex2(privateDataLength));
        list.add(alwaysZero0C != 0 ? "alwaysZero0C=" + alwaysZero0C : "");
        list.add(alwaysZero28 != 0 ? "alwaysZero28=" + alwaysZero28 : "");
        String full = list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining(", "));
        return "{ " + full + " }";
    }

    public String getEntryPointsLine() {
        if (entryPoints == null || entryPoints.length == 0) {
            return null;
        }
        return Arrays.stream(entryPoints).map(e -> "e" + StringHelper.formatHex2(e.jumpIndex) + "=" + StringHelper.formatHex4(e.addr)).collect(Collectors.joining(" "));
    }

    public String getJumpsLine() {
        if (jumps == null || jumps.length == 0) {
            return null;
        }
        return Arrays.stream(jumps).map(j -> "j" + StringHelper.formatHex2(j.jumpIndex) + "=" + StringHelper.formatHex4(j.addr)).collect(Collectors.joining(" "));
    }

    public void setVariableInitialValues() {
        if (variableDeclarations == null || variableDeclarations.length == 0) {
            return;
        }
        privateVars = new ArrayList<>();
        sharedVars = new ArrayList<>();
        final int maxPrivate = privateDataOffset + privateDataLength;
        for (ScriptVariable vr : variableDeclarations) {
            if (vr.location == 3) {
                int max = privateDataOffset + vr.offset + vr.getLength();
                if (max <= maxPrivate) {
                    privateVars.add(new ScriptVariable(vr));
                }
            } else if (vr.location == 4) {
                sharedVars.add(new ScriptVariable(vr));
            }
        }
        privateVars.forEach(p -> p.parseValues());
        sharedVars.forEach(s -> s.parseValues());
    }

    public void setBattleWorkerTypes(int battleWorkerType, int slotCount, int[] payload) {
        this.battleWorkerType = battleWorkerType;
        entryPointBattleSlotArray = new ScriptJump[slotCount];
        for (int i = 0; i < slotCount; i++) {
            int entryPointIndex = read2Bytes(payload, i * 2);
            ScriptJump entryPoint = entryPointIndex != 0xFFFF && entryPointIndex < entryPoints.length ? entryPoints[entryPointIndex] : null;
            entryPointBattleSlotArray[i] = entryPoint;
            if (entryPoint != null) {
                entryPoint.setBattleWorkerEntryPointSlot(i);
            }
        }
    }

    public void setPurposeSlot(int purposeSlot) {
        this.purposeSlot = purposeSlot;
    }

    public static String purposeSlotToString(int battleWorkerSlot) {
        if (battleWorkerSlot == 0x00) {
            return "?BattleCameras";
        }
        if (battleWorkerSlot >= 0x25 && battleWorkerSlot <= 0x28) {
            String chr = StackObject.enumToScriptField("playerChar", battleWorkerSlot - 0x17).getName();
            return "BasicHandlesFor" + chr;
        }
        if (battleWorkerSlot >= 0x29 && battleWorkerSlot <= 0x3A) {
            String chr = StackObject.enumToScriptField("playerChar", battleWorkerSlot - 0x29).getName();
            return "ExtraHandlesFor" + chr;
        }
        if (battleWorkerSlot == 0x3D) {
            return "MonsterAi";
        }
        if (battleWorkerSlot == 0x3E) {
            return "BtlScene0-7";
        } else if (battleWorkerSlot == 0x3F) {
            return "BtlScene8+";
        }
        if (battleWorkerSlot == 0x41 || battleWorkerSlot == 0x42 || battleWorkerSlot == 0x43) {
            return "MagicCam3-" + (battleWorkerSlot - 0x41);
        } else if (battleWorkerSlot == 0x44 || battleWorkerSlot == 0x45 || battleWorkerSlot == 0x46) {
            return "MagicCam2-" + (battleWorkerSlot - 0x44);
        } else if (battleWorkerSlot == 0x47 || battleWorkerSlot == 0x48 || battleWorkerSlot == 0x49) {
            return "MagicCam4-" + (battleWorkerSlot - 0x47);
        } else if (battleWorkerSlot == 0x4A || battleWorkerSlot == 0x4B || battleWorkerSlot == 0x4C) {
            return "MagicCam6-" + (battleWorkerSlot - 0x4A);
        }
        return StringHelper.formatHex2(battleWorkerSlot);
    }
}
