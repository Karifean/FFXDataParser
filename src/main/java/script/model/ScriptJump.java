package script.model;

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

    private String label;

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
        if (isEntryPoint) {
            String sPrefix = "s" + String.format("%02X", scriptIndex);
            if (jumpIndex == 0) {
                return sPrefix + "init";
            } else if (jumpIndex == 1) {
                return sPrefix + "step";
            } else {
                return sPrefix + "e" + String.format("%02X", jumpIndex);
            }
        } else {
            return "j" + String.format("%02X", jumpIndex);
        }
    }

    public void setTypes(String rAType, String rXType, String rYType, Map<Integer, String> tempITypes) {
        this.rAType = rAType;
        this.rXType = rXType;
        this.rYType = rYType;
        this.tempITypes = tempITypes;
    }

    public void setCtbPurpose(int slot) {
        String purpose = ctbPurposeSlotToString(slot);
        if (purpose != null) {
            label = "s" + String.format("%02X", scriptIndex) + purpose;
        }
    }

    public void setEncScript(int slot) {
        label = "encScript" + slot;
    }

    private static String ctbPurposeSlotToString(int slot) {
        return switch (slot) {
            case 0 -> "onTurn";
            case 1 -> "preTurn";
            case 2 -> "onTargeted";
            case 3 -> "onHit";
            case 4 -> "onDeath";
            case 5 -> "onMove";
            case 6 -> "postTurn";
            case 7 -> "postMove";
            default -> null;
        };
    }
}
