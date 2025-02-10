package atel.model;

import main.StringHelper;

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
    public ScriptLine targetLine;

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
        return getLabel() + " (" + StringHelper.formatHex4(addr) + ")";
    }

    public String getDefaultLabel() {
        if (!isEntryPoint) {
            return "j" + StringHelper.formatHex2(jumpIndex);
        } else {
            String wPrefix = "w" + StringHelper.formatHex2(scriptWorker.workerIndex);
            if (jumpIndex == 0) {
                return wPrefix + "init";
            } else if (jumpIndex == 1) {
                return wPrefix + "main";
            } else {
                if (scriptWorker.battleWorkerType != null) {
                    String wePrefix = wPrefix + "e" + StringHelper.formatHex2(jumpIndex);
                    return wePrefix + battleWorkerEntryPointToString();
                } else {
                    return wPrefix + eventWorkerEntryPointToStringWithFallback(scriptWorker.eventWorkerType, jumpIndex);
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

    private String battleWorkerEntryPointToString() {
        int battleWorkerType = scriptWorker.battleWorkerType;
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
        String strBattleWorkerType = "t" + StringHelper.formatHex2(battleWorkerType);
        String strBattleWorkerEntryPointType = "p" + StringHelper.formatHex2(battleWorkerEntryPointType);
        String strWorkerPurposeSlot = "s" + StringHelper.formatHex2(scriptWorker.purposeSlot);
        return strWorkerPurposeSlot + strBattleWorkerType + strBattleWorkerEntryPointType;
    }

    private static String eventWorkerEntryPointToStringWithFallback(int eventWorkerType, int eventWorkerEntryPoint) {
        String label = eventWorkerEntryPointToString(eventWorkerType, eventWorkerEntryPoint);
        if (label != null) {
            return label;
        }
        return "e" + StringHelper.formatHex2(eventWorkerEntryPoint);
    }

    private static String eventWorkerEntryPointToString(int eventWorkerType, int eventWorkerEntryPoint) {
        return switch (eventWorkerType) {
            case 1 -> // FieldObject
                    switch (eventWorkerEntryPoint) {
                        case 2 -> "talk";
                        case 3 -> "scout";
                        case 4 -> "fo?c";
                        case 5 -> "touch";
                        default -> null;
                    };
            case 2 -> // PlayerEdge
                    switch (eventWorkerEntryPoint) {
                        case 2 -> "pe?t";
                        case 3 -> "pe?s";
                        case 4 -> "cross";
                        case 5 -> "touch";
                        case 6 -> "pe?e";
                        case 7 -> "pe?l";
                        default -> null;
                    };
            case 3 -> // PlayerZone
                    switch (eventWorkerEntryPoint) {
                        case 2 -> "talk";
                        case 3 -> "scout";
                        case 4 -> "cross";
                        case 5 -> "touch";
                        case 6 -> "enter";
                        case 7 -> "leave";
                        default -> null;
                    };
            case 5 -> // Edge
                    switch (eventWorkerEntryPoint) {
                        case 2 -> "cross";
                        default -> null;
                    };
            case 6 -> // Zone
                    switch (eventWorkerEntryPoint) {
                        case 2 -> "enter";
                        case 3 -> "leave";
                        default -> null;
                    };
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
