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
            String hex4 = String.format("%04x", value).toUpperCase();
            String hex = String.format(value >= 0x100 ? "%04x" : "%02x", value).toUpperCase();
            String hexSuffix = " [" + hex + "h]";
            if ("bool".equals(type)) {
                return (value > 0 ? "true" : "false") + hexSuffix;
            } else if ("actor".equals(type)) {
                if (ScriptConstants.BATTLE_ACTOR_NAMES.containsKey(value)) {
                    return ScriptConstants.BATTLE_ACTOR_NAMES.get(value);
                } else if (value >= 0x1000 && value <= 0x1200) {
                    return "Actors:MonsterType=" + String.format("%04x", value - 0x1000).toUpperCase();
                } else {
                    return "Actor:" + hex4;
                }
            } else if ("fieldActor".equals(type)) {
                if (ScriptConstants.FIELD_ACTOR_NAMES.containsKey(value)) {
                    return ScriptConstants.FIELD_ACTOR_NAMES.get(value);
                } else {
                    return "FieldActor:" + hex4;
                }
            } else if ("actorProperty".equals(type)) {
                if (ScriptConstants.ACTOR_PROPERTIES.containsKey(value)) {
                    return ScriptConstants.ACTOR_PROPERTIES.get(value).name;
                } else {
                    return "ActorProp:" + hex4;
                }
            } else if ("moveProperty".equals(type)) {
                if (ScriptConstants.MOVE_PROPERTIES.containsKey(value)) {
                    return ScriptConstants.MOVE_PROPERTIES.get(value).name;
                } else {
                    return "MoveProp:" + hex;
                }
            } else if ("deathAnim".equals(type)) {
                return ScriptConstants.DEATH_ANIMATIONS.getOrDefault(value, "DeathAnim?") + hexSuffix;
            } else if ("button".equals(type)) {
                return ScriptConstants.CONTROLLER_BUTTONS.getOrDefault(value, "Button?") + hexSuffix;
            } else if ("battleEndType".equals(type)) {
                return ScriptConstants.BATTLE_END_TYPES.getOrDefault(value, "BattleEndType?") + hexSuffix;
            } else if ("selector".equals(type)) {
                if (value == 0) {
                    return "Any/All" + hexSuffix;
                } else if (value == 1) {
                    return "Highest" + hexSuffix;
                } else if (value == 2) {
                    return "Lowest" + hexSuffix;
                } else if (value == 0x80) {
                    return "Not" + hexSuffix;
                } else {
                    return "Selector?" + hexSuffix;
                }
            } else if ("ambushState".equals(type)) {
                if (value == 2) {
                    return "Ambush" + hexSuffix;
                } else {
                    return "AmbushState?" + hexSuffix;
                }
            } else if ("damageType".equals(type)) {
                return (value == 1 ? "Physic" : (value == 2 ? "Magic" : "Speci")) + "alDmg" + hexSuffix;
            } else if ("damageFormula".equals(type)) {
                return "F" + value + " " + ScriptConstants.DAMAGE_FORMULAE.get(value) + hexSuffix;
            } else if ("move".equals(type)) {
                if (value == 0) {
                    return "Null Move" + hexSuffix;
                } else if (value <= 0x11) {
                    return "Switch/Summon:" + ScriptConstants.BATTLE_ACTOR_NAMES.get(value) + hexSuffix;
                } else {
                    AbilityDataObject ability = Main.getAbility(hex);
                    return (ability != null ? '"'+ability.name+'"' : "????") + hexSuffix;
                }
            } else if ("ad".equals(type)) {
                return "r:" + hex;
            } else if ("af".equals(type)) {
                return "af:" + hex;
            }
        }
        return content;
    }
}