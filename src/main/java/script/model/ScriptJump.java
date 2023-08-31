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

    private String label;
    private int purposeKind;
    private int purposeSlot;

    public ScriptJump(ScriptWorker scriptWorker, int addr, int jumpIndex, boolean isEntryPoint) {
        this.scriptWorker = scriptWorker;
        this.addr = addr;
        this.workerIndex = scriptWorker.workerIndex;
        this.jumpIndex = jumpIndex;
        this.isEntryPoint = isEntryPoint;
    }

    public String getLabel() {
        return label != null ? label : getDefaultLabel();
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
                if (scriptWorker.workerType == 1 && jumpIndex == 2) {
                    return wPrefix + "talk";
                } else if (scriptWorker.workerType == 1 && jumpIndex == 3) {
                    return wPrefix + "scout";
                } else if (scriptWorker.workerType == 1 && jumpIndex == 5) {
                    return wPrefix + "touch";
                }
            }
            String pSuffix = purposeSlot > 0 ? "p" + String.format("%02X", purposeSlot) : "";
            return wPrefix + "e" + String.format("%02X", jumpIndex) + pSuffix;
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

    public void setGenericPurpose(int slot, int kind) {
        purposeKind = kind;
        purposeSlot = slot;
        if (kind == 2) {
            setCtbPurpose();
        } else if (kind == 4) {
            setEncScript();
        } else if (kind == 6) {
            label = startEndHookPurposeSlotToString(purposeSlot);
        }
    }

    private void setCtbPurpose() {
        String purpose = ctbPurposeSlotToString(purposeSlot);
        if (purpose != null) {
            ScriptField chr = scriptWorker.purposeSlotToChar();
            String sPrefix = chr != null ? chr.getName() + "." : ("w" + String.format("%02X", workerIndex));
            label = sPrefix + purpose;
        }
    }

    private void setEncScript() {
        label = "encScript" + purposeSlot;
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
