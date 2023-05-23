package script.model;

import java.util.Map;

public class ScriptJump {
    public int addr;
    public int scriptIndex;
    public int jumpIndex;
    public boolean isEntryPoint;

    public String rAType;
    public String rXType;
    public String rYType;
    public Map<Integer, String> tempITypes;

    public ScriptJump(int addr, int scriptIndex, int jumpIndex, boolean isEntryPoint) {
        this.addr = addr;
        this.scriptIndex = scriptIndex;
        this.jumpIndex = jumpIndex;
        this.isEntryPoint = isEntryPoint;
    }

    public String getLabel() {
        return (this.isEntryPoint ? "s" + String.format("%02X", scriptIndex) + "e" : "j") + String.format("%02X", jumpIndex);
    }

    public void setTypes(String rAType, String rXType, String rYType, Map<Integer, String> tempITypes) {
        this.rAType = rAType;
        this.rXType = rXType;
        this.rYType = rYType;
        this.tempITypes = tempITypes;
    }
}
