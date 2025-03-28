package atel.model;

import main.DataAccess;
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
    private int battleWorkerEntryPointSlot;

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

    public void setBattleWorkerEntryPointSlot(int entryPointSlot) {
        battleWorkerEntryPointSlot = entryPointSlot;
    }

    private String battleWorkerEntryPointToString() {
        int battleWorkerType = scriptWorker.battleWorkerType != null ? scriptWorker.battleWorkerType : -1;
        int battleWorkerSlot = scriptWorker.purposeSlot != null ? scriptWorker.purposeSlot : -1;
        if (battleWorkerType == 2) {
            String s = ctbPurposeSlotToString(battleWorkerEntryPointSlot);
            if (s != null) {
                String prefix = "";
                if (battleWorkerSlot >= 0x25 && battleWorkerSlot <= 0x28) {
                    String chr = StackObject.enumToScriptField("playerChar", battleWorkerSlot - 0x17).getName();
                    prefix = "Base" + chr + ".";
                }
                if (battleWorkerSlot >= 0x29 && battleWorkerSlot <= 0x3A) {
                    String chr = StackObject.enumToScriptField("playerChar", battleWorkerSlot - 0x29).getName();
                    prefix = chr + ".";
                }
                return prefix + s;
            }
        } else if (battleWorkerType == 4) {
            return "btlScene" + StringHelper.hex2WithSuffix(battleWorkerEntryPointSlot);
        } else if (battleWorkerType == 6) {
            String s = startEndHookPurposeSlotToString(battleWorkerEntryPointSlot);
            if (s != null) {
                return s;
            }
        }
        if (battleWorkerSlot == 0x00) {
            return "?BattleCam" + StringHelper.formatHex2(battleWorkerEntryPointSlot);
        } else if (battleWorkerSlot == 0x41 || battleWorkerSlot == 0x42 || battleWorkerSlot == 0x43) {
            int cmd = 0x3000 + battleWorkerEntryPointSlot;
            return "MagicCam:" + DataAccess.getCommand(cmd).getName() + StringHelper.hex4Suffix(cmd);
        } else if (battleWorkerSlot == 0x44 || battleWorkerSlot == 0x45 || battleWorkerSlot == 0x46) {
            int cmd = 0x2000 + battleWorkerEntryPointSlot;
            return "MagicCam:" + DataAccess.getCommand(cmd).getName() + StringHelper.hex4Suffix(cmd);
        } else if (battleWorkerSlot == 0x47 || battleWorkerSlot == 0x48 || battleWorkerSlot == 0x49) {
            int cmd = 0x4000 + battleWorkerEntryPointSlot;
            return "MagicCam:" + DataAccess.getCommand(cmd).getName() + StringHelper.hex4Suffix(cmd);
        } else if (battleWorkerSlot == 0x4A || battleWorkerSlot == 0x4B || battleWorkerSlot == 0x4C) {
            int cmd = 0x6000 + battleWorkerEntryPointSlot;
            return "MagicCam:" + DataAccess.getCommand(cmd).getName() + StringHelper.hex4Suffix(cmd);
        }
        String strBattleWorkerType = "t" + StringHelper.formatHex2(battleWorkerType);
        String strBattleWorkerEntryPointType = "p" + StringHelper.formatHex2(battleWorkerEntryPointSlot);
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
            case 8 -> "postPoison";
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
