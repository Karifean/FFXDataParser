package script.model;

import model.AbilityDataObject;
import main.Main;

import java.util.Map;

public class StackObject {
    public String type;
    public boolean expression;
    public String content;
    public int value;

    public StackObject(String type, boolean expression, String content, int value) {
        this.type = type;
        this.expression = expression;
        this.content = content;
        this.value = value;
    }

    public StackObject(String type, StackObject obj) {
        this.type = type;
        this.expression = obj.expression;
        this.content = obj.content;
        this.value = obj.value;
    }

    @Override
    public String toString() {
        if (!expression && !"unknown".equals(type)) {
            String hex = String.format(value >= 0x10000 ? "%08x" : value >= 0x100 ? "%04x" : "%02x", value).toUpperCase();
            String hexSuffix = " [" + hex + "h]";
            if ("bool".equals(type)) {
                return (value > 0 ? "true" : "false") + hexSuffix;
            }
            if ("float".equals(type)) {
                return Float.intBitsToFloat(value) + hexSuffix;
            }
            if ("uint".equals(type)) {
                return value + hexSuffix;
            }
            if ("int".equals(type)) {
                int signed = value < 0x8000 ? value : (value - 0x10000);
                return signed + hexSuffix;
            }
            if ("move".equals(type)) {
                if (value == 0) {
                    return "Null Move" + hexSuffix;
                } else if (value <= 0x11) {
                    return "Switch/Summon:" + ScriptConstants.getEnumMap("actor").get(value) + hexSuffix;
                } else {
                    AbilityDataObject ability = Main.getAbility(value);
                    return (ability != null ? '"'+ability.getName()+'"' : "????") + hexSuffix;
                }
            } else if ("charMove".equals(type)) {
                int adjustedValue = value + 0x3000;
                String adjustedHex = String.format("%04x", adjustedValue).toUpperCase();
                String adjustedHexSuffix = " [" + adjustedHex + "h]";
                AbilityDataObject ability = Main.getAbility(adjustedValue);
                return (ability != null ? '"'+ability.getName()+'"' : "????") + adjustedHexSuffix;
            }
            if (ScriptConstants.ENUMERATIONS.containsKey(type)) {
                Map<Integer, ScriptField> map = ScriptConstants.ENUMERATIONS.get(type);
                return map.getOrDefault(value, new ScriptField('?' + type + ':' + hex, "unknown")).toString();
            }
        }
        return content;
    }
}