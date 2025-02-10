package model;

import main.StringHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * a_ability.bin
 */
public class GearAbilityDataObject implements Nameable {
    public static final int LENGTH = 0x6C;
    private final int[] bytes;

    public LocalizedStringObject name = new LocalizedStringObject();
    public LocalizedStringObject unusedString0405 = new LocalizedStringObject();
    public LocalizedStringObject description = new LocalizedStringObject();
    public LocalizedStringObject unusedString0C0D = new LocalizedStringObject();
    public int nameOffset;
    public int unusedString0405Offset;
    public int descriptionOffset;
    public int unusedString0C0DOffset;
    private int nameKey;
    private int unusedString0405Key;
    private int descriptionKey;
    private int unusedString0C0DKey;

    int sosFlagByte;
    int elementStrike;
    int elementWeak;
    int elementResist;
    int elementImmune;
    int elementAbsorb;

    int statusInflictChanceDeath;
    int statusInflictChanceZombie;
    int statusInflictChancePetrify;
    int statusInflictChancePoison;
    int statusInflictChancePowerBreak;
    int statusInflictChanceMagicBreak;
    int statusInflictChanceArmorBreak;
    int statusInflictChanceMentalBreak;
    int statusInflictChanceConfuse;
    int statusInflictChanceBerserk;
    int statusInflictChanceProvoke;
    int statusInflictChanceThreaten;
    int statusInflictChanceSleep;
    int statusInflictChanceSilence;
    int statusInflictChanceDarkness;
    int statusInflictChanceShell;
    int statusInflictChanceProtect;
    int statusInflictChanceReflect;
    int statusInflictChanceNTide;
    int statusInflictChanceNBlaze;
    int statusInflictChanceNShock;
    int statusInflictChanceNFrost;
    int statusInflictChanceRegen;
    int statusInflictChanceHaste;
    int statusInflictChanceSlow;

    int statusDurationSleep;
    int statusDurationSilence;
    int statusDurationDarkness;
    int statusDurationShell;
    int statusDurationProtect;
    int statusDurationReflect;
    int statusDurationNTide;
    int statusDurationNBlaze;
    int statusDurationNShock;
    int statusDurationNFrost;
    int statusDurationRegen;
    int statusDurationHaste;
    int statusDurationSlow;

    int statusResistChanceDeath;
    int statusResistChanceZombie;
    int statusResistChancePetrify;
    int statusResistChancePoison;
    int statusResistChanceConfuse;
    int statusResistChanceBerserk;
    int statusResistChanceProvoke;
    int statusResistChanceThreaten;
    int statusResistChancePowerBreak;
    int statusResistChanceMagicBreak;
    int statusResistChanceArmorBreak;
    int statusResistChanceMentalBreak;
    int statusResistChanceSleep;
    int statusResistChanceSilence;
    int statusResistChanceDarkness;
    int statusResistChanceShell;
    int statusResistChanceProtect;
    int statusResistChanceReflect;
    int statusResistChanceRegen;
    int statusResistChanceSlow;
    int statusResistChanceHaste;
    int statusResistChanceNBlaze;
    int statusResistChanceNFrost;
    int statusResistChanceNShock;
    int statusResistChanceNTide;

    int statIncreaseAmount;
    int unknownByte56;
    int statIncreaseFlags;
    int autoStatusesPermanent;
    int autoStatusesTemporal;
    int autoStatusesExtra;
    int extraStatusInflict;
    int extraStatusImmunities;
    private int abilityFlags62;
    private int abilityFlags63;
    private int abilityFlags64;
    private int abilityFlags65;
    private int abilityFlags66;
    int unknownByte67;

    int byte67usually14;
    public int groupIndex;
    public int groupLevel;
    int internationalBonusIndex; // 1/2/3/4 on Distil Pw/Mn/Sp/Ab, 255 on Ribbon, 254 on everything superceded by Ribbon

    boolean sosFlag;
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

    boolean inflictScan;
    boolean inflictShield;
    boolean inflictBoost;
    boolean inflictDistillPower;
    boolean inflictDistillMana;
    boolean inflictDistillSpeed;
    boolean inflictDistillAbility;
    boolean inflictUnused1;
    boolean inflictEject;
    boolean inflictAutoLife;
    boolean inflictCurse;
    boolean inflictDoom;
    boolean inflictDefend;
    boolean inflictGuard;
    boolean inflictSentinel;
    boolean inflictUnused2;

    boolean resistScan;
    boolean resistShield;
    boolean resistBoost;
    boolean resistDistillPower;
    boolean resistDistillMana;
    boolean resistDistillSpeed;
    boolean resistDistillAbility;
    boolean resistUnused1;
    boolean resistEject;
    boolean resistAutoLife;
    boolean resistCurse;
    boolean resistDoom;
    boolean resistDefend;
    boolean resistGuard;
    boolean resistSentinel;
    boolean resistUnused2;

    boolean increaseHp;
    boolean increaseMp;
    boolean increaseStr;
    boolean increaseMag;
    boolean increaseDef;
    boolean increaseMdf;

    private boolean sensor;
    private boolean firstStrike;
    private boolean initiative;
    private boolean counterattack;
    private boolean evadencounter;
    private boolean magiccounter;
    private boolean magicbooster;
    private boolean alchemy;
    private boolean autoPotion;
    private boolean autoMed;
    private boolean autoPhoenix;
    private boolean piercing;
    private boolean halfMpCost;
    private boolean oneMpCost;
    private boolean odX2;
    private boolean odX3;
    private boolean odSOS;
    private boolean odToAp;
    private boolean apX2;
    private boolean apX3;
    private boolean apX0;
    private boolean pickpocket;
    private boolean masterThief;
    private boolean breakHpLimit;
    private boolean breakMpLimit;
    private boolean breakDmgLimit;
    private boolean gilX2;
    private boolean hpStroll;
    private boolean mpStroll;
    private boolean noEncounters;
    private boolean capture;

    private boolean byte62bit80;
    private boolean byte63bit01;
    private boolean byte65bit10;
    private boolean byte65bit20;
    private boolean byte66bit08;
    private boolean byte66bit10;
    private boolean byte66bit20;
    private boolean byte66bit40;
    private boolean byte66bit80;

    public GearAbilityDataObject(int[] bytes, int[] stringBytes, String localization) {
        this.bytes = bytes;
        mapBytes();
        mapFlags();
        mapStrings(stringBytes, localization);
    }

    public String getName(String localization) {
        return name.getLocalizedContent(localization);
    }

    private void mapBytes() {
        nameOffset = read2Bytes(0x00);
        nameKey = read2Bytes(0x02);
        unusedString0405Offset = read2Bytes(0x04);
        unusedString0405Key = read2Bytes(0x06);
        descriptionOffset = read2Bytes(0x08);
        descriptionKey = read2Bytes(0x0A);
        unusedString0C0DOffset = read2Bytes(0x0C);
        unusedString0C0DKey = read2Bytes(0x0E);
        sosFlagByte = bytes[0x10];
        elementStrike = bytes[0x11];
        elementAbsorb = bytes[0x12];
        elementImmune = bytes[0x13];
        elementResist = bytes[0x14];
        elementWeak = bytes[0x15];
        statusInflictChanceDeath = bytes[0x16];
        statusInflictChanceZombie = bytes[0x17];
        statusInflictChancePetrify = bytes[0x18];
        statusInflictChancePoison = bytes[0x19];
        statusInflictChancePowerBreak = bytes[0x1A];
        statusInflictChanceMagicBreak = bytes[0x1B];
        statusInflictChanceArmorBreak = bytes[0x1C];
        statusInflictChanceMentalBreak = bytes[0x1D];
        statusInflictChanceConfuse = bytes[0x1E];
        statusInflictChanceBerserk = bytes[0x1F];
        statusInflictChanceProvoke = bytes[0x20];
        statusInflictChanceThreaten = bytes[0x21];
        statusInflictChanceSleep = bytes[0x22];
        statusInflictChanceSilence = bytes[0x23];
        statusInflictChanceDarkness = bytes[0x24];
        statusInflictChanceShell = bytes[0x25];
        statusInflictChanceProtect = bytes[0x26];
        statusInflictChanceReflect = bytes[0x27];
        statusInflictChanceNTide = bytes[0x28];
        statusInflictChanceNBlaze = bytes[0x29];
        statusInflictChanceNShock = bytes[0x2A];
        statusInflictChanceNFrost = bytes[0x2B];
        statusInflictChanceRegen = bytes[0x2C];
        statusInflictChanceHaste = bytes[0x2D];
        statusInflictChanceSlow = bytes[0x2E];
        statusDurationSleep = bytes[0x2F];
        statusDurationSilence = bytes[0x30];
        statusDurationDarkness = bytes[0x31];
        statusDurationShell = bytes[0x32];
        statusDurationProtect = bytes[0x33];
        statusDurationReflect = bytes[0x34];
        statusDurationNTide = bytes[0x35];
        statusDurationNBlaze = bytes[0x36];
        statusDurationNShock = bytes[0x37];
        statusDurationNFrost = bytes[0x38];
        statusDurationRegen = bytes[0x39];
        statusDurationHaste = bytes[0x3A];
        statusDurationSlow = bytes[0x3B];
        statusResistChanceDeath = bytes[0x3C];
        statusResistChanceZombie = bytes[0x3D];
        statusResistChancePetrify = bytes[0x3E];
        statusResistChancePoison = bytes[0x3F];
        statusResistChancePowerBreak = bytes[0x40];
        statusResistChanceMagicBreak = bytes[0x41];
        statusResistChanceArmorBreak = bytes[0x42];
        statusResistChanceMentalBreak = bytes[0x43];
        statusResistChanceConfuse = bytes[0x44];
        statusResistChanceBerserk = bytes[0x45];
        statusResistChanceProvoke = bytes[0x46];
        statusResistChanceThreaten = bytes[0x47];
        statusResistChanceSleep = bytes[0x48];
        statusResistChanceSilence = bytes[0x49];
        statusResistChanceDarkness = bytes[0x4A];
        statusResistChanceShell = bytes[0x4B];
        statusResistChanceProtect = bytes[0x4C];
        statusResistChanceReflect = bytes[0x4D];
        statusResistChanceNTide = bytes[0x4E];
        statusResistChanceNBlaze = bytes[0x4F];
        statusResistChanceNShock = bytes[0x50];
        statusResistChanceNFrost = bytes[0x51];
        statusResistChanceRegen = bytes[0x52];
        statusResistChanceHaste = bytes[0x53];
        statusResistChanceSlow = bytes[0x54];
        statIncreaseAmount = bytes[0x55];
        unknownByte56 = bytes[0x56];
        statIncreaseFlags = bytes[0x57];
        autoStatusesPermanent = read2Bytes(0x58);
        autoStatusesTemporal = read2Bytes(0x5A);
        autoStatusesExtra = read2Bytes(0x5C);
        extraStatusInflict = read2Bytes(0x5E);
        extraStatusImmunities = read2Bytes(0x60);
        abilityFlags62 = bytes[0x62];
        abilityFlags63 = bytes[0x63];
        abilityFlags64 = bytes[0x64];
        abilityFlags65 = bytes[0x65];
        abilityFlags66 = bytes[0x66];
        unknownByte67 = bytes[0x67];
        byte67usually14 = bytes[0x68];
        groupIndex = bytes[0x69];
        groupLevel = bytes[0x6A];
        internationalBonusIndex = bytes[0x6B];
    }

    private void mapFlags() {
        sosFlag = sosFlagByte > 0;
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
        inflictScan = (extraStatusInflict & 0x0001) > 0;
        inflictDistillPower = (extraStatusInflict & 0x0002) > 0;
        inflictDistillMana = (extraStatusInflict & 0x0004) > 0;
        inflictDistillSpeed = (extraStatusInflict & 0x0008) > 0;
        inflictUnused1 = (extraStatusInflict & 0x0010) > 0;
        inflictDistillAbility = (extraStatusInflict & 0x0020) > 0;
        inflictShield = (extraStatusInflict & 0x0040) > 0;
        inflictBoost = (extraStatusInflict & 0x0080) > 0;
        inflictEject = (extraStatusInflict & 0x0100) > 0;
        inflictAutoLife = (extraStatusInflict & 0x0200) > 0;
        inflictCurse = (extraStatusInflict & 0x0400) > 0;
        inflictDefend = (extraStatusInflict & 0x0800) > 0;
        inflictGuard = (extraStatusInflict & 0x1000) > 0;
        inflictSentinel = (extraStatusInflict & 0x2000) > 0;
        inflictDoom = (extraStatusInflict & 0x4000) > 0;
        inflictUnused2 = (extraStatusInflict & 0x8000) > 0;
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
        increaseHp = (statIncreaseFlags & 0x01) > 0;
        increaseMp = (statIncreaseFlags & 0x02) > 0;
        increaseStr = (statIncreaseFlags & 0x04) > 0;
        increaseMag = (statIncreaseFlags & 0x08) > 0;
        increaseDef = (statIncreaseFlags & 0x10) > 0;
        increaseMdf = (statIncreaseFlags & 0x20) > 0;
        sensor = (abilityFlags62 & 0x01) > 0;
        firstStrike = (abilityFlags62 & 0x02) > 0;
        initiative = (abilityFlags62 & 0x04) > 0;
        counterattack = (abilityFlags62 & 0x08) > 0;
        evadencounter = (abilityFlags62 & 0x10) > 0;
        magiccounter = (abilityFlags62 & 0x20) > 0;
        magicbooster = (abilityFlags62 & 0x40) > 0;
        byte62bit80 = (abilityFlags62 & 0x80) > 0;
        byte63bit01 = (abilityFlags63 & 0x01) > 0;
        alchemy = (abilityFlags63 & 0x02) > 0;
        autoPotion = (abilityFlags63 & 0x04) > 0;
        autoMed = (abilityFlags63 & 0x08) > 0;
        autoPhoenix = (abilityFlags63 & 0x10) > 0;
        piercing = (abilityFlags63 & 0x20) > 0;
        halfMpCost = (abilityFlags63 & 0x40) > 0;
        oneMpCost = (abilityFlags63 & 0x80) > 0;
        odX2 = (abilityFlags64 & 0x01) > 0;
        odX3 = (abilityFlags64 & 0x02) > 0;
        odSOS = (abilityFlags64 & 0x04) > 0;
        odToAp = (abilityFlags64 & 0x08) > 0;
        apX2 = (abilityFlags64 & 0x10) > 0;
        apX3 = (abilityFlags64 & 0x20) > 0;
        apX0 = (abilityFlags64 & 0x40) > 0;
        pickpocket = (abilityFlags64 & 0x80) > 0;
        masterThief = (abilityFlags65 & 0x01) > 0;
        breakHpLimit = (abilityFlags65 & 0x02) > 0;
        breakMpLimit = (abilityFlags65 & 0x04) > 0;
        breakDmgLimit = (abilityFlags65 & 0x08) > 0;
        byte65bit10 = (abilityFlags65 & 0x10) > 0;
        byte65bit20 = (abilityFlags65 & 0x20) > 0;
        gilX2 = (abilityFlags65 & 0x40) > 0;
        hpStroll = (abilityFlags65 & 0x80) > 0;
        mpStroll = (abilityFlags66 & 0x01) > 0;
        noEncounters = (abilityFlags66 & 0x02) > 0;
        capture = (abilityFlags66 & 0x04) > 0;
        byte66bit08 = (abilityFlags66 & 0x08) > 0;
        byte66bit10 = (abilityFlags66 & 0x10) > 0;
        byte66bit20 = (abilityFlags66 & 0x20) > 0;
        byte66bit40 = (abilityFlags66 & 0x40) > 0;
        byte66bit80 = (abilityFlags66 & 0x80) > 0;
    }

    private void mapStrings(int[] stringBytes, String localization) {
        name.setLocalizedContent(localization, StringHelper.getStringAtLookupOffset(stringBytes, nameOffset));
        unusedString0405.setLocalizedContent(localization, StringHelper.getStringAtLookupOffset(stringBytes, unusedString0405Offset));
        description.setLocalizedContent(localization, StringHelper.getStringAtLookupOffset(stringBytes, descriptionOffset));
        unusedString0C0D.setLocalizedContent(localization, StringHelper.getStringAtLookupOffset(stringBytes, unusedString0C0DOffset));
    }

    public void setLocalizations(GearAbilityDataObject localizationObject) {
        localizationObject.name.copyInto(name);
        localizationObject.unusedString0405.copyInto(unusedString0405);
        localizationObject.description.copyInto(description);
        localizationObject.unusedString0C0D.copyInto(unusedString0C0D);
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        list.add(specialAbilities());
        list.add(allElemental());
        list.add(statusInflictions());
        list.add(statusResists());
        list.add(statIncrease());
        list.add(autoBuffs());
        list.add(grouping());
        list.add(ifG0(internationalBonusIndex, "InternationalBonus?=", ""));
        if (byte67usually14 != 0x14) {
            list.add("Byte68=" + byte67usually14);
        }
        String full = list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining(", "));
        String descriptionStr = (descriptionOffset > 0 ? description.getDefaultContent() : "");
        return String.format("%-18s", getName()) + " { " + full + " } " + descriptionStr;
    }

    private static String ifG0(int value, String prefix, String postfix) {
        if (value > 0) {
            return prefix + value + postfix;
        } else {
            return null;
        }
    }

    private String grouping() {
        return "Group=" + StringHelper.hex2WithSuffix(groupIndex) + " Level=" + groupLevel;
    }

    private String autoBuffs() {
        StringBuilder buffs = new StringBuilder();
        if (autoStatusesPermanent == 0 && autoStatusesTemporal == 0 && autoStatusesExtra == 0) {
            return "";
        }
        if (sosFlag) {
            buffs.append("SOS");
        } else {
            buffs.append("Auto");
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

    private String specialAbilities() {
        StringBuilder spec = new StringBuilder();
        if (sensor) {
            spec.append(", Sensor");
        }
        if (firstStrike) {
            spec.append(", First Strike");
        }
        if (initiative) {
            spec.append(", Initiative");
        }
        if (counterattack) {
            spec.append(", Counterattack");
        }
        if (evadencounter) {
            spec.append(", Evade & Counter");
        }
        if (magiccounter) {
            spec.append(", Magic Counter");
        }
        if (magicbooster) {
            spec.append(", Magic Booster");
        }
        if (alchemy) {
            spec.append(", Alchemy");
        }
        if (autoPotion || autoMed || autoPhoenix) {
            spec.append(", Auto");
            if (autoPotion) {
                spec.append("-Potion");
            }
            if (autoMed) {
                spec.append("-Med");
            }
            if (autoPhoenix) {
                spec.append("-Phoenix");
            }
        }
        if (piercing) {
            spec.append(", Piercing");
        }
        if (halfMpCost) {
            spec.append(", MP 50%");
        }
        if (oneMpCost) {
            spec.append(", MP 1");
        }
        if (odX2) {
            spec.append(", OD x2");
        }
        if (odX3) {
            spec.append(", OD x3");
        }
        if (odSOS) {
            spec.append(", SOS-OD");
        }
        if (odToAp) {
            spec.append(", OD -> AP");
        }
        if (apX2) {
            spec.append(", AP x2");
        }
        if (apX3) {
            spec.append(", AP x3");
        }
        if (apX0) {
            spec.append(", No AP");
        }
        if (pickpocket) {
            spec.append(", Pickpocket");
        }
        if (masterThief) {
            spec.append(", Master Thief");
        }
        if (breakHpLimit || breakMpLimit || breakDmgLimit) {
            spec.append(", Break ");
            if (breakHpLimit) {
                spec.append("HP");
                if (breakMpLimit || breakDmgLimit) {
                    spec.append('/');
                }
            }
            if (breakMpLimit && breakDmgLimit) {
                spec.append("MP/DMG");
            } else if (breakMpLimit) {
                spec.append("MP");
            } else if (breakDmgLimit) {
                spec.append("DMG");
            }
            spec.append(" Limit");
        }
        if (gilX2) {
            spec.append(", Gillionaire");
        }
        if (hpStroll && mpStroll) {
            spec.append(", HP/MP Stroll");
        } else if (hpStroll) {
            spec.append(", HP Stroll");
        } else if (mpStroll) {
            spec.append(", MP Stroll");
        }
        if (noEncounters) {
            spec.append(", No Encounters");
        }
        if (capture) {
            spec.append(", Capture");
        }
        if (spec.isEmpty()) {
            return "";
        } else {
            return "Specific: " + spec.substring(2);
        }
    }

    private String statIncrease() {
        String increaseSuffix = " +" + statIncreaseAmount + "%";
        StringBuilder stats = new StringBuilder();
        if (increaseHp) {
            stats.append("HP/");
        }
        if (increaseMp) {
            stats.append("MP/");
        }
        if (increaseStr) {
            stats.append("STR/");
        }
        if (increaseMag) {
            stats.append("MAG/");
        }
        if (increaseDef) {
            stats.append("DEF/");
        }
        if (increaseMdf) {
            stats.append("MDF/");
        }
        if (stats.isEmpty() && statIncreaseAmount <= 0) {
            return "";
        }
        if (stats.isEmpty()) {
            return "Null" + increaseSuffix;
        } else {
            return stats.substring(0, stats.length() - 1) + increaseSuffix;
        }
    }

    private String allElemental() {
        String strike = elements(elementStrike);
        String weak = elements(elementWeak);
        String resist = elements(elementResist);
        String immune = elements(elementImmune);
        String absorb = elements(elementAbsorb);
        if (strike == null && weak == null && resist == null && immune == null && absorb == null) {
            return "";
        } else {
            StringBuilder elements = new StringBuilder("Element");
            if (strike != null) {
                elements.append(" Strike=").append(strike).append(';');
            }
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
        StringBuilder elements = new StringBuilder("Multi {");
        if ((elementByte & 0x01) > 0) { elements.append(" Fire;"); }
        if ((elementByte & 0x02) > 0) { elements.append(" Ice;"); }
        if ((elementByte & 0x04) > 0) { elements.append(" Thunder;"); }
        if ((elementByte & 0x08) > 0) { elements.append(" Water;"); }
        if ((elementByte & 0x10) > 0) { elements.append(" Holy;"); }
        if ((elementByte & 0x20) > 0) { elements.append(" 6;"); }
        if ((elementByte & 0x40) > 0) { elements.append(" 7;"); }
        if ((elementByte & 0x80) > 0) { elements.append(" 8;"); }
        String conv = elements.toString();
        if (conv.endsWith(";")) {
            String withoutLastSemicolon = conv.substring(0, conv.length() - 1);
            if (withoutLastSemicolon.indexOf(';') > 0) {
                return withoutLastSemicolon + " }";
            } else {
                return withoutLastSemicolon.substring(8);
            }
        } else {
            return null;
        }
    }

    private String statusInflictions() {
        StringBuilder statuses = new StringBuilder("Inflict {");
        appendPermanentStatus(statuses, "Death", statusInflictChanceDeath);
        appendPermanentStatus(statuses, "Zombie", statusInflictChanceZombie);
        appendPermanentStatus(statuses, "Petrify", statusInflictChancePetrify);
        appendPermanentStatus(statuses, "Poison", statusInflictChancePoison);
        appendPermanentStatus(statuses, "Confuse", statusInflictChanceConfuse);
        appendPermanentStatus(statuses, "Berserk", statusInflictChanceBerserk);
        appendPermanentStatus(statuses, "Provoke", statusInflictChanceProvoke);
        appendPermanentStatus(statuses, "Threaten", statusInflictChanceThreaten);
        if (statusInflictChancePowerBreak > 0 &&
                statusInflictChancePowerBreak == statusInflictChanceMagicBreak &&
                statusInflictChancePowerBreak == statusInflictChanceArmorBreak &&
                statusInflictChancePowerBreak == statusInflictChanceMentalBreak) {
            appendPermanentStatus(statuses, "All Breaks", statusInflictChancePowerBreak);
        } else {
            appendPermanentStatus(statuses, "Power Break", statusInflictChancePowerBreak);
            appendPermanentStatus(statuses, "Magic Break", statusInflictChanceMagicBreak);
            appendPermanentStatus(statuses, "Armor Break", statusInflictChanceArmorBreak);
            appendPermanentStatus(statuses, "Mental Break", statusInflictChanceMentalBreak);
        }
        appendTemporaryStatus(statuses, "Sleep", statusInflictChanceSleep, statusDurationSleep, false);
        appendTemporaryStatus(statuses, "Silence", statusInflictChanceSilence, statusDurationSilence, false);
        appendTemporaryStatus(statuses, "Darkness", statusInflictChanceDarkness, statusDurationDarkness, false);
        appendTemporaryStatus(statuses, "Shell", statusInflictChanceShell, statusDurationShell, false);
        appendTemporaryStatus(statuses, "Protect", statusInflictChanceProtect, statusDurationProtect, false);
        appendTemporaryStatus(statuses, "Reflect", statusInflictChanceReflect, statusDurationReflect, false);
        appendTemporaryStatus(statuses, "Regen", statusInflictChanceRegen, statusDurationRegen, false);
        appendTemporaryStatus(statuses, "Slow", statusInflictChanceSlow, statusDurationSlow, false);
        appendTemporaryStatus(statuses, "Haste", statusInflictChanceHaste, statusDurationHaste, false);
        if (statusInflictChanceNBlaze > 0 && statusDurationNBlaze > 0 &&
                statusInflictChanceNBlaze == statusInflictChanceNFrost &&
                statusInflictChanceNBlaze == statusInflictChanceNShock &&
                statusInflictChanceNBlaze == statusInflictChanceNTide &&
                statusDurationNBlaze == statusDurationNFrost &&
                statusDurationNBlaze == statusDurationNShock &&
                statusDurationNBlaze == statusDurationNTide) {
            appendTemporaryStatus(statuses, "NulAll", statusInflictChanceNBlaze, statusDurationNBlaze, true);
        } else {
            appendTemporaryStatus(statuses, "NulBlaze", statusInflictChanceNBlaze, statusDurationNBlaze, true);
            appendTemporaryStatus(statuses, "NulFrost", statusInflictChanceNFrost, statusDurationNFrost, true);
            appendTemporaryStatus(statuses, "NulShock", statusInflictChanceNShock, statusDurationNShock, true);
            appendTemporaryStatus(statuses, "NulTide", statusInflictChanceNTide, statusDurationNTide, true);
        }
        if (inflictScan) { statuses.append(" Scan;"); }
        if (inflictShield) { statuses.append(" Shield;"); }
        if (inflictBoost) { statuses.append(" Boost;"); }
        if (inflictDistillPower) { statuses.append(" Distill Power;"); }
        if (inflictDistillMana) { statuses.append(" Distill Mana;"); }
        if (inflictDistillSpeed) { statuses.append(" Distill Speed;"); }
        if (inflictDistillAbility) { statuses.append(" Distill Ability;"); }
        if (inflictUnused1) { statuses.append(" Unused1;"); }
        if (inflictEject) { statuses.append(" Eject;"); }
        if (inflictAutoLife) { statuses.append(" Auto-Life;"); }
        if (inflictCurse) { statuses.append(" Curse;"); }
        if (inflictDoom) { statuses.append(" Doom;"); }
        if (inflictDefend) { statuses.append(" Defend;"); }
        if (inflictGuard) { statuses.append(" Guard;"); }
        if (inflictSentinel) { statuses.append(" Sentinel;"); }
        if (inflictUnused2) { statuses.append(" Unused2;"); }
        String conv = statuses.toString();
        if (conv.endsWith(";")) {
            String withoutLastSemicolon = conv.substring(0, conv.length() - 1);
            if (withoutLastSemicolon.contains(";")) {
                return withoutLastSemicolon + " }";
            } else {
                return "Inflict " + withoutLastSemicolon.substring(10);
            }
        } else {
            return "";
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
        appendResistedStatus(statusMap, "Threaten", statusResistChanceThreaten);
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
        if (statusResistChanceNBlaze > 0 && statusDurationNBlaze > 0 &&
                statusResistChanceNBlaze == statusResistChanceNFrost &&
                statusResistChanceNBlaze == statusResistChanceNShock &&
                statusResistChanceNBlaze == statusResistChanceNTide &&
                statusDurationNBlaze == statusDurationNFrost &&
                statusDurationNBlaze == statusDurationNShock &&
                statusDurationNBlaze == statusDurationNTide) {
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
            String resistString = statusMap.entrySet().stream().map(e -> String.join("/", e.getValue()) + " (" + (e.getKey() < 255 ? (e.getKey() + "%") : "Immune") + ")").collect(Collectors.joining(", "));
            if (statusMap.size() == 1) {
                return "Resist " + resistString;
            } else {
                return "Resist { " + resistString + " }";
            }
        } else {
            return "";
        }
    }

    private void appendResistedStatus(Map<Integer, List<String>> statusMap, String name, int chance) {
        statusMap.computeIfAbsent(chance, s -> new ArrayList<>()).add(name);
    }

    private void appendPermanentStatus(StringBuilder builder, String name, int chance) {
        if (chance > 0) {
            builder.append(' ').append(name);
            if (chance < 254) {
                builder.append(" (").append(statusChanceString(chance)).append(')');
            }
            builder.append(';');
        }
    }

    private void appendTemporaryStatus(StringBuilder builder, String name, int chance, int duration, boolean blocks) {
        if (chance > 0) {
            builder.append(' ').append(name);
            if (chance < 254 || duration < 254) {
                builder.append(" (").append(statusChanceString(chance)).append(", ").append(statusDurationString(duration, blocks)).append(')');
            }
            builder.append(';');
        }
    }

    private static String statusChanceString(int chance) {
        if (chance == 255) {
            return "Always";
        } else if (chance == 254) {
            return "Infinite";
        } else if (chance > 0) {
            return chance + "%";
        } else {
            return null;
        }
    }

    private static String statusDurationString(int duration, boolean blocks) {
        if (duration >= 254) {
            return "Endless";
        } else if (duration == 1) {
            return blocks ? "1 block" : "1 turn";
        } else if (duration >= 0) {
            return duration + (blocks ? " blocks" : " turns");
        } else {
            return null;
        }
    }

    private int read2Bytes(int offset) {
        return bytes[offset] + bytes[offset+1] * 0x100;
    }
}
