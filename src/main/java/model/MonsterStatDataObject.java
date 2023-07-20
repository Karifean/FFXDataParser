package model;

import main.DataAccess;
import main.StringHelper;
import script.model.StackObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MonsterStatDataObject {
    public static final int LENGTH = 0x80;

    private final int[] bytes;

    int nameOffset;
    int sensorOffset;
    int sensorDashOffset;
    int scanOffset;
    int scanDashOffset;
    public String monsterName;
    public String monsterSensorText;
    public String monsterSensorDash;
    public String monsterScanText;
    public String monsterScanDash;
    public boolean isLocalizationData = false;

    int hp;
    int mp;
    int overkillThreshold;
    int str;
    int def;
    int mag;
    int mdf;
    int agi;
    int lck;
    int eva;
    int acc;
    int miscProperties28;
    int miscProperties29;
    int poisonDamage;
    int elementAbsorb;
    int elementImmune;
    int elementResist;
    int elementWeak;
    int statusResistChanceDeath;
    int statusResistChanceZombie;
    int statusResistChancePetrify;
    int statusResistChancePoison;
    int statusResistChancePowerBreak;
    int statusResistChanceMagicBreak;
    int statusResistChanceArmorBreak;
    int statusResistChanceMentalBreak;
    int statusResistChanceConfuse;
    int statusResistChanceBerserk;
    int statusResistChanceProvoke;
    int statusChanceThreaten;
    int statusResistChanceSleep;
    int statusResistChanceSilence;
    int statusResistChanceDarkness;
    int statusResistChanceShell;
    int statusResistChanceProtect;
    int statusResistChanceReflect;
    int statusResistChanceRegen;
    int statusResistChanceNBlaze;
    int statusResistChanceNFrost;
    int statusResistChanceNShock;
    int statusResistChanceNTide;
    int statusResistChanceHaste;
    int statusResistChanceSlow;
    int autoStatuses1;
    int autoStatuses2;
    int autoStatuses3;
    int autoStatuses4;
    int autoStatuses5;
    int autoStatuses6;

    int extraStatusImmunities1;
    int extraStatusImmunities2;

    int forcedAction;
    int monsterIdx;
    int modelIdx;
    int doomCounter;
    int monsterArenaIdx;

    boolean armored;
    boolean immunityFractionalDamage;
    boolean immunityLife;
    boolean immunitySensor;
    boolean props28bit10;
    boolean immunityPhysicalDamage;
    boolean immunityMagicalDamage;
    boolean props28bit80;
    boolean immunityDelay;

    boolean autoDeath;
    boolean autoZombie;
    boolean autoPetrify;
    boolean autoPoison;
    boolean autoPowerBreak;
    boolean autoMagicBreak;
    boolean autoArmorBreak;
    boolean autoMentalBreak;
    boolean autoConfuse;
    boolean autoBerserk;
    boolean autoProvoke;
    boolean autoThreaten;
    boolean autoSleep;
    boolean autoSilence;
    boolean autoDarkness;
    boolean autoUnknown280;
    boolean autoUnknown301;
    boolean autoUnknown302;
    boolean autoUnknown304;
    boolean autoShell;
    boolean autoProtect;
    boolean autoReflect;
    boolean autoNTide;
    boolean autoNBlaze;
    boolean autoNShock;
    boolean autoNFrost;
    boolean autoRegen;
    boolean autoHaste;
    boolean autoSlow;
    boolean autoUnknown4;
    boolean autoScan;
    boolean autoShield;
    boolean autoBoost;
    boolean autoDistillPower;
    boolean autoDistillMana;
    boolean autoDistillSpeed;
    boolean autoDistillAbility;
    boolean autoUnused1;
    boolean autoEject;
    boolean autoAutoLife;
    boolean autoCurse;
    boolean autoDoom;
    boolean autoDefend;
    boolean autoGuard;
    boolean autoSentinel;
    boolean autoUnused2;

    boolean resistScan;
    boolean resistDistillPower;
    boolean resistDistillMana;
    boolean resistDistillSpeed;
    boolean resistUnused1;
    boolean resistDistillAbility;
    boolean resistShield;
    boolean resistBoost;
    boolean resistEject;
    boolean resistAutoLife;
    boolean resistCurse;
    boolean resistDefend;
    boolean resistGuard;
    boolean resistSentinel;
    boolean resistDoom;
    boolean resistUnused2;

    public MonsterStatDataObject(int[] bytes, int[] stringBytes) {
        this.bytes = bytes;
        mapBytes();
        mapFlags();
        mapStrings(stringBytes);
    }

    private void mapBytes() {
        nameOffset = read2Bytes(bytes, 0x00);
        sensorOffset = read2Bytes(bytes, 0x04);
        sensorDashOffset = read2Bytes(bytes, 0x08);
        scanOffset = read2Bytes(bytes, 0x0C);
        scanDashOffset = read2Bytes(bytes, 0x10);
        hp = read4Bytes(bytes, 0x14);
        mp = read4Bytes(bytes, 0x18);
        overkillThreshold = read4Bytes(bytes, 0x1C);
        str = bytes[0x20];
        def = bytes[0x21];
        mag = bytes[0x22];
        mdf = bytes[0x23];
        agi = bytes[0x24];
        lck = bytes[0x25];
        eva = bytes[0x26];
        acc = bytes[0x27];
        miscProperties28 = bytes[0x28];
        miscProperties29 = bytes[0x29];
        poisonDamage = bytes[0x2A];
        elementAbsorb = bytes[0x2B];
        elementImmune = bytes[0x2C];
        elementResist = bytes[0x2D];
        elementWeak = bytes[0x2E];
        statusResistChanceDeath = bytes[0x2F];
        statusResistChanceZombie = bytes[0x30];
        statusResistChancePetrify = bytes[0x31];
        statusResistChancePoison = bytes[0x32];
        statusResistChancePowerBreak = bytes[0x33];
        statusResistChanceMagicBreak = bytes[0x34];
        statusResistChanceArmorBreak = bytes[0x35];
        statusResistChanceMentalBreak = bytes[0x36];
        statusResistChanceConfuse = bytes[0x37];
        statusResistChanceBerserk = bytes[0x38];
        statusResistChanceProvoke = bytes[0x39];
        statusChanceThreaten = bytes[0x3A];
        statusResistChanceSleep = bytes[0x3B];
        statusResistChanceSilence = bytes[0x3C];
        statusResistChanceDarkness = bytes[0x3D];
        statusResistChanceShell = bytes[0x3E];
        statusResistChanceProtect = bytes[0x3F];
        statusResistChanceReflect = bytes[0x40];
        statusResistChanceNTide = bytes[0x41];
        statusResistChanceNBlaze = bytes[0x42];
        statusResistChanceNShock = bytes[0x43];
        statusResistChanceNFrost = bytes[0x44];
        statusResistChanceRegen = bytes[0x45];
        statusResistChanceHaste = bytes[0x46];
        statusResistChanceSlow = bytes[0x47];
        autoStatuses1 = bytes[0x48];
        autoStatuses2 = bytes[0x49];
        autoStatuses3 = bytes[0x4A];
        autoStatuses4 = bytes[0x4B];
        autoStatuses5 = bytes[0x4C];
        autoStatuses6 = bytes[0x4D];

        extraStatusImmunities1 = bytes[0x4E];
        extraStatusImmunities2 = bytes[0x4F];

        forcedAction = read2Bytes(bytes, 0x70);
        monsterIdx = read2Bytes(bytes, 0x72);
        modelIdx = read2Bytes(bytes, 0x74);
        doomCounter = bytes[0x77];
        monsterArenaIdx = read2Bytes(bytes, 0x78);
    }

    private void mapFlags() {
        armored = (miscProperties28 & 0x01) > 0;
        immunityFractionalDamage = (miscProperties28 & 0x02) > 0;
        immunityLife = (miscProperties28 & 0x04) > 0;
        immunitySensor = (miscProperties28 & 0x08) > 0;
        props28bit10 = (miscProperties28 & 0x10) > 0;
        immunityPhysicalDamage = (miscProperties28 & 0x20) > 0;
        immunityMagicalDamage = (miscProperties28 & 0x40) > 0;
        props28bit80 = (miscProperties28 & 0x80) > 0;
        immunityDelay = (miscProperties29 & 0x01) > 0;

        autoDeath = (autoStatuses1 & 0x01) > 0;
        autoZombie = (autoStatuses1 & 0x02) > 0;
        autoPetrify = (autoStatuses1 & 0x04) > 0;
        autoPoison = (autoStatuses1 & 0x08) > 0;
        autoPowerBreak = (autoStatuses1 & 0x10) > 0;
        autoMagicBreak = (autoStatuses1 & 0x20) > 0;
        autoArmorBreak = (autoStatuses1 & 0x40) > 0;
        autoMentalBreak = (autoStatuses1 & 0x80) > 0;
        autoConfuse = (autoStatuses2 & 0x01) > 0;
        autoBerserk = (autoStatuses2 & 0x02) > 0;
        autoProvoke = (autoStatuses2 & 0x04) > 0;
        autoThreaten = (autoStatuses2 & 0x08) > 0;
        autoSleep = (autoStatuses2 & 0x10) > 0;
        autoSilence = (autoStatuses2 & 0x20) > 0;
        autoDarkness = (autoStatuses2 & 0x40) > 0;
        autoUnknown280 = (autoStatuses2 & 0x80) > 0;
        autoUnknown301 = (autoStatuses3 & 0x01) > 0;
        autoUnknown302 = (autoStatuses3 & 0x02) > 0;
        autoUnknown304 = (autoStatuses3 & 0x04) > 0;
        autoShell = (autoStatuses3 & 0x08) > 0;
        autoProtect = (autoStatuses3 & 0x10) > 0;
        autoReflect = (autoStatuses3 & 0x20) > 0;
        autoNTide = (autoStatuses3 & 0x40) > 0;
        autoNBlaze = (autoStatuses3 & 0x80) > 0;
        autoNShock = (autoStatuses4 & 0x01) > 0;
        autoNFrost = (autoStatuses4 & 0x02) > 0;
        autoRegen = (autoStatuses4 & 0x04) > 0;
        autoHaste = (autoStatuses4 & 0x08) > 0;
        autoSlow = (autoStatuses4 & 0x10) > 0;
        autoUnknown4 = autoStatuses4 >= 0x20;
        autoScan = (autoStatuses5 & 0x01) > 0;
        autoDistillPower = (autoStatuses5 & 0x02) > 0;
        autoDistillMana = (autoStatuses5 & 0x04) > 0;
        autoDistillSpeed = (autoStatuses5 & 0x08) > 0;
        autoUnused1 = (autoStatuses5 & 0x10) > 0;
        autoDistillAbility = (autoStatuses5 & 0x20) > 0;
        autoShield = (autoStatuses5 & 0x40) > 0;
        autoBoost = (autoStatuses5 & 0x80) > 0;
        autoEject = (autoStatuses6 & 0x01) > 0;
        autoAutoLife = (autoStatuses6 & 0x02) > 0;
        autoCurse = (autoStatuses6 & 0x04) > 0;
        autoDefend = (autoStatuses6 & 0x08) > 0;
        autoGuard = (autoStatuses6 & 0x10) > 0;
        autoSentinel = (autoStatuses6 & 0x20) > 0;
        autoDoom = (autoStatuses6 & 0x40) > 0;
        autoUnused2 = (autoStatuses6 & 0x80) > 0;
        resistScan = (extraStatusImmunities1 & 0x01) > 0;
        resistDistillPower = (extraStatusImmunities1 & 0x02) > 0;
        resistDistillMana = (extraStatusImmunities1 & 0x04) > 0;
        resistDistillSpeed = (extraStatusImmunities1 & 0x08) > 0;
        resistUnused1 = (extraStatusImmunities1 & 0x10) > 0;
        resistDistillAbility = (extraStatusImmunities1 & 0x20) > 0;
        resistShield = (extraStatusImmunities1 & 0x40) > 0;
        resistBoost = (extraStatusImmunities1 & 0x80) > 0;
        resistEject = (extraStatusImmunities2 & 0x01) > 0;
        resistAutoLife = (extraStatusImmunities2 & 0x02) > 0;
        resistCurse = (extraStatusImmunities2 & 0x04) > 0;
        resistDefend = (extraStatusImmunities2 & 0x08) > 0;
        resistGuard = (extraStatusImmunities2 & 0x10) > 0;
        resistSentinel = (extraStatusImmunities2 & 0x20) > 0;
        resistDoom = (extraStatusImmunities2 & 0x40) > 0;
        resistUnused2 = (extraStatusImmunities2 & 0x80) > 0;
    }

    private void mapStrings(int[] stringBytes) {
        if (stringBytes == null || stringBytes.length == 0) {
            return;
        }
        monsterName = StringHelper.getStringAtLookupOffset(stringBytes, nameOffset);
        monsterSensorText = StringHelper.getStringAtLookupOffset(stringBytes, sensorOffset);
        monsterSensorDash = StringHelper.getStringAtLookupOffset(stringBytes, sensorDashOffset);
        monsterScanText = StringHelper.getStringAtLookupOffset(stringBytes, scanOffset);
        monsterScanDash = StringHelper.getStringAtLookupOffset(stringBytes, scanDashOffset);
    }

    public String getStrings() {
        List<String> list = new ArrayList<>();
        list.add("Name: " + monsterName + " (Offset " + String.format("%04X", nameOffset) + ")");
        list.add("- Sensor Text - (Offset " + String.format("%04X", sensorOffset) + ")");
        list.add(monsterSensorText);
        list.add("- Scan Text - (Offset " + String.format("%04X", scanOffset) + ")");
        list.add(monsterScanText);
        String full = list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining("\n"));
        return full;
    }

    @Override
    public String toString() {
        if (isLocalizationData) {
            return getStrings();
        }
        List<String> list = new ArrayList<>();
        list.add("HP=" + hp + " MP=" + mp + " Overkill=" + overkillThreshold);
        list.add("STR=" + str + " DEF=" + def + " MAG=" + mag + " MDF=" + mdf);
        list.add("AGI=" + agi + " LCK=" + lck + " EVA=" + eva + " ACC=" + acc);
        if (armored) {
            list.add("Armored");
        }
        list.add(specialImmunities());
        list.add(allElemental());
        list.add(statusResists());
        list.add("Threaten Base Chance=" + statusChanceThreaten + "%");
        list.add("Poison Damage=" + poisonDamage + "%");
        list.add(autoBuffs());
        if (forcedAction > 0) {
            list.add("Forced Action: " + asMove(forcedAction));
        } else {
            list.add("No Forced Action");
        }
        list.add("Doom Counter=" + doomCounter);
        list.add("Model=" + StackObject.enumToString("model", modelIdx));

        String full = list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining("\n"));
        return full;
    }

    private String specialImmunities() {
        List<String> specials = new ArrayList<>();
        if (immunitySensor) {
            specials.add("Sensor");
        }
        if (immunityFractionalDamage) {
            specials.add("Fractional Damage");
        }
        if (immunityLife) {
            specials.add("Life (Instakill by Revival when Zombied)");
        }
        if (props28bit10) {
            specials.add("Unknown:b28b10");
        }
        if (immunityPhysicalDamage) {
            specials.add("Physical Damage");
        }
        if (immunityMagicalDamage) {
            specials.add("Magical Damage");
        }
        if (props28bit80) {
            specials.add("Unknown:b28b80");
        }
        if (immunityDelay) {
            specials.add("Delay (All CTB Heal/Damage)");
        }
        if (specials.isEmpty()) {
            return "";
        } else {
            return "Immune to " + String.join(", ", specials);
        }
    }

    private String allElemental() {
        String weak = elements(elementWeak);
        String resist = elements(elementResist);
        String immune = elements(elementImmune);
        String absorb = elements(elementAbsorb);
        if (weak == null && resist == null && immune == null && absorb == null) {
            return "No Elemental Affinities";
        } else {
            StringBuilder elements = new StringBuilder("Element Affinities:");
            if (weak != null) {
                elements.append(" Weak=").append(weak).append(';');
            }
            if (resist != null) {
                elements.append(" Resist=").append(resist).append(';');
            }
            if (immune != null) {
                elements.append(" Immune=").append(immune).append(';');
            }
            if (absorb != null) {
                elements.append(" Absorb=").append(absorb).append(';');
            }
            return elements.substring(0, elements.length() - 1);
        }
    }

    private String elements(int elementByte) {
        StringBuilder elements = new StringBuilder("{");
        if ((elementByte & 0x01) > 0) { elements.append(" Fire,"); }
        if ((elementByte & 0x02) > 0) { elements.append(" Ice,"); }
        if ((elementByte & 0x04) > 0) { elements.append(" Thunder,"); }
        if ((elementByte & 0x08) > 0) { elements.append(" Water,"); }
        if ((elementByte & 0x10) > 0) { elements.append(" Holy,"); }
        if ((elementByte & 0x20) > 0) { elements.append(" 6,"); }
        if ((elementByte & 0x40) > 0) { elements.append(" 7,"); }
        if ((elementByte & 0x80) > 0) { elements.append(" 8,"); }
        String conv = elements.toString();
        if (conv.endsWith(",")) {
            String withoutLastSemicolon = conv.substring(0, conv.length() - 1);
            if (withoutLastSemicolon.indexOf(',') > 0) {
                return withoutLastSemicolon + " }";
            } else {
                return withoutLastSemicolon.substring(2);
            }
        } else {
            return null;
        }
    }

    private String statusResists() {
        Map<Integer, List<String>> statusMap = new HashMap<>();
        appendResistedStatus(statusMap, "Death", statusResistChanceDeath);
        appendResistedStatus(statusMap, "Zombie", statusResistChanceZombie);
        appendResistedStatus(statusMap, "Petrify", statusResistChancePetrify);
        appendResistedStatus(statusMap, "Poison", statusResistChancePoison);
        appendResistedStatus(statusMap, "Confuse", statusResistChanceConfuse);
        appendResistedStatus(statusMap, "Berserk", statusResistChanceBerserk);
        appendResistedStatus(statusMap, "Provoke", statusResistChanceProvoke);
        if (statusResistChancePowerBreak > 0 &&
                statusResistChancePowerBreak == statusResistChanceMagicBreak &&
                statusResistChancePowerBreak == statusResistChanceArmorBreak &&
                statusResistChancePowerBreak == statusResistChanceMentalBreak) {
            appendResistedStatus(statusMap, "All Breaks", statusResistChancePowerBreak);
        } else {
            appendResistedStatus(statusMap, "Power Break", statusResistChancePowerBreak);
            appendResistedStatus(statusMap, "Magic Break", statusResistChanceMagicBreak);
            appendResistedStatus(statusMap, "Armor Break", statusResistChanceArmorBreak);
            appendResistedStatus(statusMap, "Mental Break", statusResistChanceMentalBreak);
        }
        appendResistedStatus(statusMap, "Sleep", statusResistChanceSleep);
        appendResistedStatus(statusMap, "Silence", statusResistChanceSilence);
        appendResistedStatus(statusMap, "Darkness", statusResistChanceDarkness);
        appendResistedStatus(statusMap, "Shell", statusResistChanceShell);
        appendResistedStatus(statusMap, "Protect", statusResistChanceProtect);
        appendResistedStatus(statusMap, "Reflect", statusResistChanceReflect);
        appendResistedStatus(statusMap, "Regen", statusResistChanceRegen);
        appendResistedStatus(statusMap, "Slow", statusResistChanceSlow);
        appendResistedStatus(statusMap, "Haste", statusResistChanceHaste);
        if (statusResistChanceNBlaze > 0 &&
                statusResistChanceNBlaze == statusResistChanceNFrost &&
                statusResistChanceNBlaze == statusResistChanceNShock &&
                statusResistChanceNBlaze == statusResistChanceNTide) {
            appendResistedStatus(statusMap, "NulAll", statusResistChanceNBlaze);
        } else {
            appendResistedStatus(statusMap, "NulBlaze", statusResistChanceNBlaze);
            appendResistedStatus(statusMap, "NulFrost", statusResistChanceNFrost);
            appendResistedStatus(statusMap, "NulShock", statusResistChanceNShock);
            appendResistedStatus(statusMap, "NulTide", statusResistChanceNTide);
        }
        if (resistScan) { appendResistedStatus(statusMap, "Scan", 0xFF); }
        if (resistShield) { appendResistedStatus(statusMap, "Shield", 0xFF); }
        if (resistBoost) { appendResistedStatus(statusMap, "Boost", 0xFF); }
        if (resistDistillPower && resistDistillMana && resistDistillSpeed && resistDistillAbility) {
            appendResistedStatus(statusMap, "All Distills", 0xFF);
        } else {
            if (resistDistillPower) {
                appendResistedStatus(statusMap, "Distill Power", 0xFF);
            }
            if (resistDistillMana) {
                appendResistedStatus(statusMap, "Distill Mana", 0xFF);
            }
            if (resistDistillSpeed) {
                appendResistedStatus(statusMap, "Distill Speed", 0xFF);
            }
            if (resistDistillAbility) {
                appendResistedStatus(statusMap, "Distill Ability", 0xFF);
            }
        }
        if (resistUnused1) { appendResistedStatus(statusMap, "Unused1", 0xFF); }
        if (resistEject) { appendResistedStatus(statusMap, "Eject", 0xFF); }
        if (resistAutoLife) { appendResistedStatus(statusMap, "Auto-Life", 0xFF); }
        if (resistCurse) { appendResistedStatus(statusMap, "Curse", 0xFF); }
        if (resistDoom) { appendResistedStatus(statusMap, "Doom", 0xFF); }
        if (resistDefend) { appendResistedStatus(statusMap, "Defend", 0xFF); }
        if (resistGuard) { appendResistedStatus(statusMap, "Guard", 0xFF); }
        if (resistSentinel) { appendResistedStatus(statusMap, "Sentinel", 0xFF); }
        if (resistUnused2) { appendResistedStatus(statusMap, "Unused2", 0xFF); }
        statusMap.remove(0);
        if (!statusMap.isEmpty()) {
            return "Status Resists: " + statusMap.entrySet().stream().map(e -> String.join("/", e.getValue()) + " (" + (e.getKey() < 255 ? (e.getKey() + "%") : "Immune") + ")").collect(Collectors.joining(", "));
        } else {
            return "";
        }
    }

    private void appendResistedStatus(Map<Integer, List<String>> statusMap, String name, int chance) {
        statusMap.computeIfAbsent(chance, s -> new ArrayList<>()).add(name);
    }

    private String autoBuffs() {
        StringBuilder buffs = new StringBuilder("Auto");
        if (autoStatuses1 == 0 && autoStatuses2 == 0 && autoStatuses3 == 0 && autoStatuses4 == 0 && autoStatuses5 == 0 && autoStatuses6 == 0) {
            return "";
        }
        if (autoDeath) {
            buffs.append("-Death??");
        }
        if (autoZombie) {
            buffs.append("-Zombie");
        }
        if (autoPetrify) {
            buffs.append("-Petrify??");
        }
        if (autoPoison) {
            buffs.append("-Poison");
        }
        if (autoPowerBreak && autoMagicBreak && autoArmorBreak && autoMentalBreak) {
            buffs.append("-FullBreak");
        } else {
            if (autoPowerBreak) {
                buffs.append("-PowerBreak");
            }
            if (autoMagicBreak) {
                buffs.append("-MagicBreak");
            }
            if (autoArmorBreak) {
                buffs.append("-ArmorBreak");
            }
            if (autoMentalBreak) {
                buffs.append("-MentalBreak");
            }
        }
        if (autoConfuse) {
            buffs.append("-Confuse??");
        }
        if (autoBerserk) {
            buffs.append("-Berserk??");
        }
        if (autoProvoke) {
            buffs.append("-Provoke??");
        }
        if (autoThreaten) {
            buffs.append("-Threaten??");
        }
        if (autoSleep) {
            buffs.append("-Sleep??");
        }
        if (autoSilence) {
            buffs.append("-Silence");
        }
        if (autoDarkness) {
            buffs.append("-Darkness");
        }
        if (autoUnknown280) {
            buffs.append("-uk280");
        }
        if (autoUnknown301) {
            buffs.append("-uk301");
        }
        if (autoUnknown302) {
            buffs.append("-uk302");
        }
        if (autoUnknown304) {
            buffs.append("-uk304");
        }
        if (autoShell) {
            buffs.append("-Shell");
        }
        if (autoProtect) {
            buffs.append("-Protect");
        }
        if (autoReflect) {
            buffs.append("-Reflect");
        }
        if (autoNTide && autoNBlaze && autoNShock && autoNFrost) {
            buffs.append("-NulAll");
        } else {
            if (autoNBlaze) {
                buffs.append("-NulBlaze");
            }
            if (autoNFrost) {
                buffs.append("-NulFrost");
            }
            if (autoNShock) {
                buffs.append("-NulShock");
            }
            if (autoNTide) {
                buffs.append("-NulTide");
            }
        }
        if (autoRegen) {
            buffs.append("-Regen");
        }
        if (autoHaste) {
            buffs.append("-Haste");
        }
        if (autoSlow) {
            buffs.append("-Slow");
        }
        if (autoUnknown4) {
            buffs.append("-uk4");
        }
        if (autoScan) {
            buffs.append("-Scan");
        }
        if (autoShield) {
            buffs.append("-Shield");
        }
        if (autoBoost) {
            buffs.append("-Boost");
        }
        if (autoDistillPower) {
            buffs.append("-DistillPower");
        }
        if (autoDistillMana) {
            buffs.append("-DistillMana");
        }
        if (autoDistillSpeed) {
            buffs.append("-DistillSpeed");
        }
        if (autoDistillAbility) {
            buffs.append("-DistillAbility");
        }
        if (autoUnused1) {
            buffs.append("-Unused1");
        }
        if (autoEject) {
            buffs.append("-Eject??");
        }
        if (autoAutoLife) {
            buffs.append("-AutoLife");
        }
        if (autoCurse) {
            buffs.append("-Curse");
        }
        if (autoDoom) {
            buffs.append("-Doom");
        }
        if (autoDefend) {
            buffs.append("-Defend");
        }
        if (autoGuard) {
            buffs.append("-Guard");
        }
        if (autoSentinel) {
            buffs.append("-Sentinel");
        }
        if (autoUnused2) {
            buffs.append("-Unused2");
        }
        return buffs.toString();
    }

    private static int read2Bytes(int[] bytes, int offset) {
        return bytes[offset] + bytes[offset+1] * 0x100;
    }

    private static int read4Bytes(int[] bytes, int offset) {
        return bytes[offset] + bytes[offset+1] * 0x100 + bytes[offset+2] * 0x10000 + bytes[offset+3] * 0x1000000;
    }

    private static String asMove(int move) {
        return DataAccess.getMove(move).getName() + " [" + String.format("%04X", move) + "h]";
    }

}
