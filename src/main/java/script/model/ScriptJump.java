package script.model;

import java.util.List;
import java.util.Map;

public class ScriptJump {
    public final ScriptHeader scriptHeader;
    public final int addr;
    public final int scriptIndex;
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

    public ScriptJump(ScriptHeader scriptHeader, int addr, int jumpIndex, boolean isEntryPoint) {
        this.scriptHeader = scriptHeader;
        this.addr = addr;
        this.scriptIndex = scriptHeader.scriptIndex;
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
            String sPrefix = "s" + String.format("%02X", scriptIndex);
            if (jumpIndex == 0) {
                return sPrefix + "init";
            } else if (jumpIndex == 1) {
                return sPrefix + "main";
            } else {
                if (scriptHeader.scriptType == 1 && jumpIndex == 2) {
                    return sPrefix + "talk";
                } else if (scriptHeader.scriptType == 1 && jumpIndex == 3) {
                    return sPrefix + "scout";
                }
            }
            String pSuffix = purposeSlot > 0 ? "p" + String.format("%02X", purposeSlot) : "";
            return sPrefix + "e" + String.format("%02X", jumpIndex) + pSuffix;
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
        }
    }

    private void setCtbPurpose() {
        String purpose = ctbPurposeSlotToString(purposeSlot);
        if (purpose != null) {
            label = "s" + String.format("%02X", scriptIndex) + purpose;
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
}
