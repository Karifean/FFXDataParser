package model;

import main.DataAccess;
import main.StringHelper;
import atel.model.StackObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Part of MonsterFile
 * monster1.bin (only uses Name/Sensor/Scan text strings)
 * monster2.bin (only uses Name/Sensor/Scan text strings)
 * monster3.bin (only uses Name/Sensor/Scan text strings)
 */
public class MonsterStatDataObject {
    public static final int LENGTH = 0x80;

    private final int[] bytes;

    int nameOffset;
    int sensorOffset;
    int sensorDashOffset;
    int scanOffset;
    int scanDashOffset;
    public LocalizedStringObject monsterName = new LocalizedStringObject();
    public LocalizedStringObject monsterSensorText = new LocalizedStringObject();
    public LocalizedStringObject monsterSensorDash = new LocalizedStringObject();
    public LocalizedStringObject monsterScanText = new LocalizedStringObject();
    public LocalizedStringObject monsterScanDash = new LocalizedStringObject();

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
    int autoStatusesPermanent;
    int autoStatusesTemporal;
    int autoStatusesExtra;

    int extraStatusImmunities;

    int[] abilityList;

    int forcedAction;
    int monsterIdx;
    int modelIdx;
    int ctbIconType;
    int doomCounter;
    int monsterArenaIdx;
    int modelIdxOther;

    int alwaysZero7C;
    int alwaysZero7D;
    int alwaysZero7E;
    int alwaysZero7F;

    boolean armored;
    boolean immunityFractionalDamage;
    boolean immunityLife;
    boolean immunitySensor;
    boolean immunityScanAgainOrWhat;
    boolean immunityPhysicalDamage;
    boolean immunityMagicalDamage;
    boolean immunityAllDamage;
    boolean immunityDelay;
    boolean immunitySliceProbably;
    boolean immunityBribeProbably;

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

    public MonsterStatDataObject(int[] bytes, int[] stringBytes, String localization) {
        this.bytes = bytes;
        mapBytes();
        mapFlags();
        mapStrings(stringBytes, localization);
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
        autoStatusesPermanent = read2Bytes(bytes, 0x48);
        autoStatusesTemporal = read2Bytes(bytes, 0x4A);
        autoStatusesExtra = read2Bytes(bytes, 0x4C);
        extraStatusImmunities = read2Bytes(bytes, 0x4E);

        abilityList = new int[16];
        abilityList[0] = read2Bytes(bytes, 0x50);
        abilityList[1] = read2Bytes(bytes, 0x52);
        abilityList[2] = read2Bytes(bytes, 0x54);
        abilityList[3] = read2Bytes(bytes, 0x56);
        abilityList[4] = read2Bytes(bytes, 0x58);
        abilityList[5] = read2Bytes(bytes, 0x5A);
        abilityList[6] = read2Bytes(bytes, 0x5C);
        abilityList[7] = read2Bytes(bytes, 0x5E);
        abilityList[8] = read2Bytes(bytes, 0x60);
        abilityList[9] = read2Bytes(bytes, 0x62);
        abilityList[10] = read2Bytes(bytes, 0x64);
        abilityList[11] = read2Bytes(bytes, 0x66);
        abilityList[12] = read2Bytes(bytes, 0x68);
        abilityList[13] = read2Bytes(bytes, 0x6A);
        abilityList[14] = read2Bytes(bytes, 0x6C);
        abilityList[15] = read2Bytes(bytes, 0x6E);

        forcedAction = read2Bytes(bytes, 0x70);
        monsterIdx = read2Bytes(bytes, 0x72);
        modelIdx = read2Bytes(bytes, 0x74);
        ctbIconType = bytes[0x76];
        doomCounter = bytes[0x77];
        monsterArenaIdx = read2Bytes(bytes, 0x78);
        modelIdxOther = read2Bytes(bytes, 0x7A);
        alwaysZero7C = bytes[0x7C];
        alwaysZero7D = bytes[0x7D];
        alwaysZero7E = bytes[0x7E];
        alwaysZero7F = bytes[0x7F];
    }

    private void mapFlags() {
        armored = (miscProperties28 & 0x01) > 0;
        immunityFractionalDamage = (miscProperties28 & 0x02) > 0;
        immunityLife = (miscProperties28 & 0x04) > 0;
        immunitySensor = (miscProperties28 & 0x08) > 0;
        immunityScanAgainOrWhat = (miscProperties28 & 0x10) > 0;
        immunityPhysicalDamage = (miscProperties28 & 0x20) > 0;
        immunityMagicalDamage = (miscProperties28 & 0x40) > 0;
        immunityAllDamage = (miscProperties28 & 0x80) > 0;
        immunityDelay = (miscProperties29 & 0x01) > 0;
        immunitySliceProbably = (miscProperties29 & 0x02) > 0;
        immunityBribeProbably = (miscProperties29 & 0x04) > 0;

        autoDeath = (autoStatusesPermanent & 0x0001) > 0;
        autoZombie = (autoStatusesPermanent & 0x0002) > 0;
        autoPetrify = (autoStatusesPermanent & 0x0004) > 0;
        autoPoison = (autoStatusesPermanent & 0x0008) > 0;
        autoPowerBreak = (autoStatusesPermanent & 0x0010) > 0;
        autoMagicBreak = (autoStatusesPermanent & 0x0020) > 0;
        autoArmorBreak = (autoStatusesPermanent & 0x0040) > 0;
        autoMentalBreak = (autoStatusesPermanent & 0x0080) > 0;
        autoConfuse = (autoStatusesPermanent & 0x0100) > 0;
        autoBerserk = (autoStatusesPermanent & 0x0200) > 0;
        autoProvoke = (autoStatusesPermanent & 0x0400) > 0;
        autoThreaten = (autoStatusesPermanent & 0x0800) > 0;
        autoSleep = (autoStatusesTemporal & 0x0001) > 0;
        autoSilence = (autoStatusesTemporal & 0x0002) > 0;
        autoDarkness = (autoStatusesTemporal & 0x0004) > 0;
        autoShell = (autoStatusesTemporal & 0x0008) > 0;
        autoProtect = (autoStatusesTemporal & 0x0010) > 0;
        autoReflect = (autoStatusesTemporal & 0x0020) > 0;
        autoNTide = (autoStatusesTemporal & 0x0040) > 0;
        autoNBlaze = (autoStatusesTemporal & 0x0080) > 0;
        autoNShock = (autoStatusesTemporal & 0x0100) > 0;
        autoNFrost = (autoStatusesTemporal & 0x0200) > 0;
        autoRegen = (autoStatusesTemporal & 0x0400) > 0;
        autoHaste = (autoStatusesTemporal & 0x0800) > 0;
        autoSlow = (autoStatusesTemporal & 0x1000) > 0;
        autoScan = (autoStatusesExtra & 0x0001) > 0;
        autoDistillPower = (autoStatusesExtra & 0x0002) > 0;
        autoDistillMana = (autoStatusesExtra & 0x0004) > 0;
        autoDistillSpeed = (autoStatusesExtra & 0x0008) > 0;
        autoUnused1 = (autoStatusesExtra & 0x0010) > 0;
        autoDistillAbility = (autoStatusesExtra & 0x0020) > 0;
        autoShield = (autoStatusesExtra & 0x0040) > 0;
        autoBoost = (autoStatusesExtra & 0x0080) > 0;
        autoEject = (autoStatusesExtra & 0x0100) > 0;
        autoAutoLife = (autoStatusesExtra & 0x0200) > 0;
        autoCurse = (autoStatusesExtra & 0x0400) > 0;
        autoDefend = (autoStatusesExtra & 0x0800) > 0;
        autoGuard = (autoStatusesExtra & 0x1000) > 0;
        autoSentinel = (autoStatusesExtra & 0x2000) > 0;
        autoDoom = (autoStatusesExtra & 0x4000) > 0;
        autoUnused2 = (autoStatusesExtra & 0x8000) > 0;
        resistScan = (extraStatusImmunities & 0x0001) > 0;
        resistDistillPower = (extraStatusImmunities & 0x0002) > 0;
        resistDistillMana = (extraStatusImmunities & 0x0004) > 0;
        resistDistillSpeed = (extraStatusImmunities & 0x0008) > 0;
        resistUnused1 = (extraStatusImmunities & 0x0010) > 0;
        resistDistillAbility = (extraStatusImmunities & 0x0020) > 0;
        resistShield = (extraStatusImmunities & 0x0040) > 0;
        resistBoost = (extraStatusImmunities & 0x0080) > 0;
        resistEject = (extraStatusImmunities & 0x0100) > 0;
        resistAutoLife = (extraStatusImmunities & 0x0200) > 0;
        resistCurse = (extraStatusImmunities & 0x0400) > 0;
        resistDefend = (extraStatusImmunities & 0x0800) > 0;
        resistGuard = (extraStatusImmunities & 0x1000) > 0;
        resistSentinel = (extraStatusImmunities & 0x2000) > 0;
        resistDoom = (extraStatusImmunities & 0x4000) > 0;
        resistUnused2 = (extraStatusImmunities & 0x8000) > 0;
    }

    private void mapStrings(int[] stringBytes, String localization) {
        if (stringBytes == null || stringBytes.length == 0) {
            return;
        }
        monsterName.setLocalizedContent(localization, StringHelper.getStringAtLookupOffset(stringBytes, nameOffset));
        monsterSensorText.setLocalizedContent(localization, StringHelper.getStringAtLookupOffset(stringBytes, sensorOffset));
        monsterSensorDash.setLocalizedContent(localization, StringHelper.getStringAtLookupOffset(stringBytes, sensorDashOffset));
        monsterScanText.setLocalizedContent(localization, StringHelper.getStringAtLookupOffset(stringBytes, scanOffset));
        monsterScanDash.setLocalizedContent(localization, StringHelper.getStringAtLookupOffset(stringBytes, scanDashOffset));
    }

    public void setLocalizations(MonsterStatDataObject localizationObject) {
        localizationObject.monsterName.copyInto(monsterName);
        localizationObject.monsterSensorText.copyInto(monsterSensorText);
        localizationObject.monsterSensorDash.copyInto(monsterSensorDash);
        localizationObject.monsterScanText.copyInto(monsterScanText);
        localizationObject.monsterScanDash.copyInto(monsterScanDash);
    }

    public String getStrings(String localization) {
        List<String> list = new ArrayList<>();
        list.add("Name: " + monsterName.getLocalizedContent(localization) + " (Offset " + StringHelper.formatHex4(nameOffset) + ")");
        list.add("- Sensor Text - (Offset " + StringHelper.formatHex4(sensorOffset) + ")");
        list.add(monsterSensorText.getLocalizedContent(localization));
        list.add("- Scan Text - (Offset " + StringHelper.formatHex4(scanOffset) + ")");
        list.add(monsterScanText.getLocalizedContent(localization));
        String full = list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining("\n"));
        return full;
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        list.add("HP=" + hp + " MP=" + mp + " Overkill=" + overkillThreshold);
        list.add("STR=" + str + " DEF=" + def + " MAG=" + mag + " MDF=" + mdf);
        list.add("AGI=" + agi + " LCK=" + lck + " EVA=" + eva + " ACC=" + acc);
        if (armored) {
            list.add("Armored");
        }
        list.add(specialImmunities());
        if (miscProperties29 >= 0x08) {
            list.add("Unknown byte 29: " + StringHelper.formatHex2(miscProperties29));
        }
        list.add(allElemental());
        list.add(statusResists());
        list.add("Threaten Base Chance=" + statusChanceThreaten + "%" + (statusChanceThreaten == 0 ? " (Immune)" : ""));
        list.add("Poison Damage=" + poisonDamage + "%");
        list.add(autoBuffs());
        List<String> abilities = new ArrayList<>();
        for (int skill : abilityList) {
            if (skill > 0) {
                abilities.add(asMove(skill));
            }
        }
        if (!abilities.isEmpty()) {
            list.add("Ability List: " + String.join(", ", abilities));
        }
        if (forcedAction > 0) {
            list.add("Forced Action: " + asMove(forcedAction));
        } else {
            list.add("Forced Action: Skip Turn");
        }
        list.add("Doom Counter=" + doomCounter);
        list.add("CTB Icon Type=" + StackObject.enumToString("ctbIconType", ctbIconType));
        if (monsterArenaIdx != 0xFF) {
            list.add("Captured Monster Index=" + monsterArenaIdx + " [" + StringHelper.formatHex2(monsterArenaIdx) + "h]");
        } else {
            list.add("Cannot be Captured");
        }
        list.add("Model Base?=" + StackObject.enumToString("model", modelIdx));
        list.add("Model Texture?=" + StackObject.enumToString("model", modelIdxOther));

        if (alwaysZero7C != 0) {
            list.add("byte 7C not zero!: " + StringHelper.formatHex2(alwaysZero7C));
        }
        if (alwaysZero7D != 0) {
            list.add("byte 7D not zero!: " + StringHelper.formatHex2(alwaysZero7D));
        }
        if (alwaysZero7E != 0) {
            list.add("byte 7E not zero!: " + StringHelper.formatHex2(alwaysZero7E));
        }
        if (alwaysZero7F != 0) {
            list.add("byte 7F not zero!: " + StringHelper.formatHex2(alwaysZero7F));
        }

        String full = list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining("\n"));
        return full;
    }

    private String specialImmunities() {
        List<String> specials = new ArrayList<>();
        if (immunitySensor) {
            specials.add("Sensor");
        }
        if (immunityFractionalDamage) {
            specials.add("% Damage");
        }
        if (immunityLife) {
            specials.add("Life (Instakill by Revival when Zombied)");
        }
        if (immunityScanAgainOrWhat) {
            specials.add("Scan?");
        }
        if (immunityPhysicalDamage) {
            specials.add("Physical Damage");
        }
        if (immunityMagicalDamage) {
            specials.add("Magical Damage");
        }
        if (immunityAllDamage) {
            specials.add("All Damage");
        }
        if (immunityDelay) {
            specials.add("Delay (All CTB Heal/Damage)");
        }
        if (immunitySliceProbably) {
            specials.add("Zan/Slice");
        }
        if (immunityBribeProbably) {
            specials.add("Bribe");
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
        return elementByte > 0 ? StackObject.bitfieldToString("elementsBitfield", elementByte) : null;
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
        if (autoStatusesPermanent == 0 && autoStatusesTemporal == 0 && autoStatusesExtra == 0) {
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
        return DataAccess.getMove(move).getName() + StringHelper.hex4Suffix(move);
    }

}
