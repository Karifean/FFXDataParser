package script.model;

import java.util.List;
import java.util.Map;

public class ScriptJump {
    public final ScriptWorker scriptWorker;
    public final int addr;
    public final int workerIndex;
    public final int jumpIndex;
    public final boolean isEntryPoint;

    public String rAType;
    public String rXType;
    public String rYType;
    public Map<Integer, String> tempITypes;
    public List<ScriptJump> reachableFrom;
    public boolean hardMisaligned = false;

    private String label;
    private int battleWorkerEntryPointType;

    public ScriptJump(ScriptWorker scriptWorker, int addr, int jumpIndex, boolean isEntryPoint) {
        this.scriptWorker = scriptWorker;
        this.addr = addr;
        this.workerIndex = scriptWorker.workerIndex;
        this.jumpIndex = jumpIndex;
        this.isEntryPoint = isEntryPoint;
    }

    public void markAsHardMisaligned() {
        this.hardMisaligned = true;
    }

    public String getLabel() {
        return label != null ? label : getDefaultLabel();
    }

    public String getLabelWithAddr() {
        return getLabel() + " (" + String.format("%04X", addr) + ")";
    }

    public String getDefaultLabel() {
        if (!isEntryPoint) {
            return "j" + String.format("%02X", jumpIndex);
        } else {
            String wPrefix = "w" + String.format("%02X", workerIndex);
            if (jumpIndex == 0) {
                return wPrefix + "init";
            } else if (jumpIndex == 1) {
                return wPrefix + "main";
            } else {
                if (scriptWorker.battleWorkerType != null) {
                    String wePrefix = wPrefix + "e" + String.format("%02X", jumpIndex);
                    return wePrefix + battleWorkerEntryPointToString(scriptWorker.battleWorkerType, battleWorkerEntryPointType);
                } else {
                    return wPrefix + eventWorkerEntryPointToString(scriptWorker.eventWorkerType, jumpIndex);
                }
            }
        }
    }

    public void setTypes(String rAType, String rXType, String rYType, Map<Integer, String> tempITypes) {
        this.rAType = rAType;
        this.rXType = rXType;
        this.rYType = rYType;
        this.tempITypes = tempITypes;
        /* if (reachableFrom != null && !reachableFrom.isEmpty()) {
            reachableFrom.forEach(rf -> rf.setTypes(rAType, rXType, rYType, tempITypes));
        } */
    }

    public void setBattleWorkerEntryPointType(int entryPointType) {
        battleWorkerEntryPointType = entryPointType;
    }

    private static String battleWorkerEntryPointToString(int battleWorkerType, int battleWorkerEntryPointType) {
        if (battleWorkerType == 2) {
            String s = ctbPurposeSlotToString(battleWorkerEntryPointType);
            if (s != null) {
                return s;
            }
        } else if (battleWorkerType == 4) {
            return "encScript" + battleWorkerEntryPointType;
        } else if (battleWorkerType == 6) {
            String s = startEndHookPurposeSlotToString(battleWorkerEntryPointType);
            if (s != null) {
                return s;
            }
        }
        return "t" + String.format("%02X", battleWorkerType) + "p" + String.format("%02X", battleWorkerEntryPointType);
    }

    private static String eventWorkerEntryPointToString(int eventWorkerType, int eventWorkerEntryPoint) {
        if (eventWorkerType == 1) {
            String label = fieldObjectSlotToString(eventWorkerEntryPoint);
            if (label != null) {
                return label;
            }
        }
        return "t" + String.format("%02X", eventWorkerType) + "e" + String.format("%02X", eventWorkerEntryPoint);
    }

    private static String fieldObjectSlotToString(int slot) {
        return switch (slot) {
            case 2 -> "talk";
            case 3 -> "scout";
            case 5 -> "touch";
            default -> null;
        };
    }

    private static String ctbPurposeSlotToString(int slot) {
        return switch (slot) {
            case 0 -> "onTurn";
            case 1 -> "preTurn";
            case 2 -> "onTargeted";
            case 3 -> "onHit";
            case 4 -> "onDeath";
            case 5 -> "onMove";
            case 6 -> "postTurn"; // prePoison
            case 7 -> "postMove?";
            case 8 -> "postPoison?";
            case 9 -> "YojiPay";
            case 0xA -> "YojiDismiss";
            case 0xB -> "YojiDeath";
            case 0xC -> "MagusTurn";
            case 0xD -> "MagusDoAsYouWill";
            case 0xE -> "MagusOneMoreTime";
            case 0xF -> "MagusFight";
            case 0x10 -> "MagusGoGo";
            case 0x11 -> "MagusHelpEachOther";
            case 0x12 -> "MagusCombinePowers";
            case 0x13 -> "MagusDefense";
            case 0x14 -> "MagusAreYouAllRight";
            default -> null;
        };
    }

    private static String startEndHookPurposeSlotToString(int slot) {
        return switch (slot) {
            case 4 -> "battleEnd";
            case 5 -> "battleStart";
            default -> null;
        };
    }
}
