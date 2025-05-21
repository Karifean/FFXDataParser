package atel.model;

import main.DataAccess;
import main.StringHelper;

import java.util.HashMap;
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
    public Map<Integer, String> tempITypes = new HashMap<>();
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
        // this.tempITypes = tempITypes;
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
        if (battleWorkerType == 0) {
            ScriptField s = ScriptConstants.getEnumMap("cameraHandlerTag").get(battleWorkerEntryPointSlot);
            if (s != null) {
                return "Cam" + s.getLabel();
            }
        } else if (battleWorkerType == 1) {
            ScriptField s = ScriptConstants.getEnumMap("motionHandlerTag").get(battleWorkerEntryPointSlot);
            if (s != null) {
                return "Motion" + s.getLabel();
            }
        } else if (battleWorkerType == 2) {
            ScriptField s = ScriptConstants.getEnumMap("combatHandlerTag").get(battleWorkerEntryPointSlot);
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
                return prefix + s.getLabel();
            }
        } else if (battleWorkerType == 3) {
            ScriptField s = ScriptConstants.getEnumMap("battleGruntHandlerTag").get(battleWorkerEntryPointSlot);
            if (s != null) {
                return "Grunt" + s.getLabel();
            }
        } else if (battleWorkerType == 4) {
            return "btlScene" + StringHelper.formatHex2(battleWorkerEntryPointSlot);
        } else if (battleWorkerType == 5) {
            if (battleWorkerSlot >= 0x6D && battleWorkerSlot <= 0x7E) {
                ScriptField s = ScriptConstants.getEnumMap("voiceHandlerTag").get(battleWorkerEntryPointSlot);
                if (s != null) {
                    String chr = StackObject.enumToScriptField("playerChar", battleWorkerSlot - 0x6D).getName();
                    return chr + "Voice" + s.getLabel();
                }
            }
        } else if (battleWorkerType == 6) {
            ScriptField s = ScriptConstants.getEnumMap("battleStartEndHookTag").get(battleWorkerEntryPointSlot);
            if (s != null) {
                return "Hook" + s.getLabel();
            }
        }
        if (battleWorkerSlot == 0x00) {
            return "?BattleCam" + StringHelper.formatHex2(battleWorkerEntryPointSlot);
        }
        if (battleWorkerSlot >= 0x41 && battleWorkerSlot <= 0x4C) {
            int cmd;
            if (battleWorkerSlot == 0x41 || battleWorkerSlot == 0x42 || battleWorkerSlot == 0x43) {
                cmd = 0x3000 + battleWorkerEntryPointSlot;
            } else if (battleWorkerSlot == 0x44 || battleWorkerSlot == 0x45 || battleWorkerSlot == 0x46) {
                cmd = 0x2000 + battleWorkerEntryPointSlot;
            } else if (battleWorkerSlot == 0x47 || battleWorkerSlot == 0x48 || battleWorkerSlot == 0x49) {
                cmd = 0x4000 + battleWorkerEntryPointSlot;
            } else {
                cmd = 0x6000 + battleWorkerEntryPointSlot;
            }
            return "MagicCam" + StringHelper.formatHex4(cmd) + "(" + DataAccess.getCommand(cmd).getName() + ")";
        }
        String strWorkerPurposeSlot = "s" + StringHelper.formatHex2(battleWorkerSlot);
        String strWorkerType = "t" + StringHelper.formatHex2(battleWorkerType);
        String strEntryPointPurposeSlot = "p" + StringHelper.formatHex2(battleWorkerEntryPointSlot);
        return strWorkerPurposeSlot + strWorkerType + strEntryPointPurposeSlot;
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
}
