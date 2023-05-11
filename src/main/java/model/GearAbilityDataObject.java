package model;

import main.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GearAbilityDataObject {
    int[] bytes;

    public String name;
    public String dash;
    public String description;
    public String otherText;
    public int nameOffset;
    public int dashOffset;
    public int descriptionOffset;
    public int otherTextOffset;

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

    int extraStatusInflictFlags1;
    int extraStatusInflictFlags2;
    int extraStatusResistFlags1;
    int extraStatusResistFlags2;

    int statIncreaseAmount;
    int statIncreaseFlags;

    int autoStatuses1;
    int autoStatuses2;
    int autoStatuses3;
    int autoStatuses4;

    private int abilityFlags62;
    private int abilityFlags63;
    private int abilityFlags64;
    private int abilityFlags65;
    private int abilityFlags66;

    int usually14;
    public int groupIndex;
    public int groupLevel;
    int internationalBonusIndex;

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

    public GearAbilityDataObject() {}

    public GearAbilityDataObject(int[] bytes, int[] stringBytes) {
        this.bytes = bytes;
        mapBytes();
        mapFlags();
        mapStrings(stringBytes);
    }

    public String getName() {
        return name;
    }

    private void mapBytes() {
        nameOffset = read2Bytes(0x00);
        dashOffset = read2Bytes(0x04);
        descriptionOffset = read2Bytes(0x08);
        otherTextOffset = read2Bytes(0x0C);
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

        statIncreaseFlags = bytes[0x57];
        autoStatuses1 = bytes[0x58];
        autoStatuses2 = bytes[0x59];
        autoStatuses3 = bytes[0x5A];
        autoStatuses4 = bytes[0x5B];

        extraStatusInflictFlags1 = bytes[0x5E];
        extraStatusInflictFlags2 = bytes[0x5F];
        extraStatusResistFlags1 = bytes[0x60];
        extraStatusResistFlags2 = bytes[0x61];
        abilityFlags62 = bytes[0x62];
        abilityFlags63 = bytes[0x63];
        abilityFlags64 = bytes[0x64];
        abilityFlags65 = bytes[0x65];
        abilityFlags66 = bytes[0x66];

        usually14 = bytes[0x68];
        groupIndex = bytes[0x69];
        groupLevel = bytes[0x6A];
        internationalBonusIndex = bytes[0x6B];
        // length = 0x6C
    }

    private void mapFlags() {
        sosFlag = sosFlagByte > 0;
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
        inflictScan = (extraStatusInflictFlags1 & 0x01) > 0;
        inflictDistillPower = (extraStatusInflictFlags1 & 0x02) > 0;
        inflictDistillMana = (extraStatusInflictFlags1 & 0x04) > 0;
        inflictDistillSpeed = (extraStatusInflictFlags1 & 0x08) > 0;
        inflictUnused1 = (extraStatusInflictFlags1 & 0x10) > 0;
        inflictDistillAbility = (extraStatusInflictFlags1 & 0x20) > 0;
        inflictShield = (extraStatusInflictFlags1 & 0x40) > 0;
        inflictBoost = (extraStatusInflictFlags1 & 0x80) > 0;
        inflictEject = (extraStatusInflictFlags2 & 0x01) > 0;
        inflictAutoLife = (extraStatusInflictFlags2 & 0x02) > 0;
        inflictCurse = (extraStatusInflictFlags2 & 0x04) > 0;
        inflictDefend = (extraStatusInflictFlags2 & 0x08) > 0;
        inflictGuard = (extraStatusInflictFlags2 & 0x10) > 0;
        inflictSentinel = (extraStatusInflictFlags2 & 0x20) > 0;
        inflictDoom = (extraStatusInflictFlags2 & 0x40) > 0;
        inflictUnused2 = (extraStatusInflictFlags2 & 0x80) > 0;
        resistScan = (extraStatusResistFlags1 & 0x01) > 0;
        resistDistillPower = (extraStatusResistFlags1 & 0x02) > 0;
        resistDistillMana = (extraStatusResistFlags1 & 0x04) > 0;
        resistDistillSpeed = (extraStatusResistFlags1 & 0x08) > 0;
        resistUnused1 = (extraStatusResistFlags1 & 0x10) > 0;
        resistDistillAbility = (extraStatusResistFlags1 & 0x20) > 0;
        resistShield = (extraStatusResistFlags1 & 0x40) > 0;
        resistBoost = (extraStatusResistFlags1 & 0x80) > 0;
        resistEject = (extraStatusResistFlags2 & 0x01) > 0;
        resistAutoLife = (extraStatusResistFlags2 & 0x02) > 0;
        resistCurse = (extraStatusResistFlags2 & 0x04) > 0;
        resistDefend = (extraStatusResistFlags2 & 0x08) > 0;
        resistGuard = (extraStatusResistFlags2 & 0x10) > 0;
        resistSentinel = (extraStatusResistFlags2 & 0x20) > 0;
        resistDoom = (extraStatusResistFlags2 & 0x40) > 0;
        resistUnused2 = (extraStatusResistFlags2 & 0x80) > 0;
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
        gilX2 = (abilityFlags65 & 0x40) > 0;
        hpStroll = (abilityFlags65 & 0x80) > 0;
        mpStroll = (abilityFlags66 & 0x01) > 0;
        noEncounters = (abilityFlags66 & 0x02) > 0;
        capture = (abilityFlags66 & 0x04) > 0;
    }

    private void mapStrings(int[] stringBytes) {
        name = Main.getStringAtLookupOffset(stringBytes, nameOffset);
        dash = Main.getStringAtLookupOffset(stringBytes, dashOffset);
        description = Main.getStringAtLookupOffset(stringBytes, descriptionOffset);
        otherText = Main.getStringAtLookupOffset(stringBytes, otherTextOffset);
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
        list.add(ifG0(internationalBonusIndex, "Duration?=", ""));
        if (usually14 != 0x14) {
            list.add("Byte68=" + usually14);
        }
        String full = list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining(", "));
        String dashStr = (dashOffset > 0 && !"-".equals(dash) ? "DH=" + dash + " / " : "");
        String descriptionStr = (descriptionOffset > 0 && !"-".equals(description) ? description : "");
        String soText = (otherTextOffset > 0 && !"-".equals(otherText) ? " / OT=" + otherText : "");
        return name + " { " + full + " } " + dashStr + descriptionStr + soText;
    }

    private static String ifG0(int value, String prefix, String postfix) {
        if (value > 0) {
            return prefix + value + postfix;
        } else {
            return null;
        }
    }

    private static String ifNN(String value, String prefix, String postfix) {
        if (value != null) {
            return prefix + value + postfix;
        } else {
            return "";
        }
    }

    private String grouping() {
        return "Group=" + groupIndex + " [" + String.format("%02x", groupIndex).toUpperCase() + "h] Level=" + groupLevel;
    }

    private String autoBuffs() {
        StringBuilder buffs = new StringBuilder();
        if (autoStatuses1 == 0 && autoStatuses2 == 0 && autoStatuses3 == 0 && autoStatuses4 == 0) {
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
                return withoutLastSemicolon.substring(9);
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
        StringBuilder statuses = new StringBuilder("Resist {");
        appendResistedStatus(statuses, "Death", statusResistChanceDeath);
        appendResistedStatus(statuses, "Zombie", statusResistChanceZombie);
        appendResistedStatus(statuses, "Petrify", statusResistChancePetrify);
        appendResistedStatus(statuses, "Poison", statusResistChancePoison);
        appendResistedStatus(statuses, "Confuse", statusResistChanceConfuse);
        appendResistedStatus(statuses, "Berserk", statusResistChanceBerserk);
        appendResistedStatus(statuses, "Provoke", statusResistChanceProvoke);
        appendResistedStatus(statuses, "Threaten", statusResistChanceThreaten);
        if (statusResistChancePowerBreak > 0 &&
                statusResistChancePowerBreak == statusResistChanceMagicBreak &&
                statusResistChancePowerBreak == statusResistChanceArmorBreak &&
                statusResistChancePowerBreak == statusResistChanceMentalBreak) {
            appendResistedStatus(statuses, "All Breaks", statusResistChancePowerBreak);
        } else {
            appendResistedStatus(statuses, "Power Break", statusResistChancePowerBreak);
            appendResistedStatus(statuses, "Magic Break", statusResistChanceMagicBreak);
            appendResistedStatus(statuses, "Armor Break", statusResistChanceArmorBreak);
            appendResistedStatus(statuses, "Mental Break", statusResistChanceMentalBreak);
        }
        appendResistedStatus(statuses, "Sleep", statusResistChanceSleep);
        appendResistedStatus(statuses, "Silence", statusResistChanceSilence);
        appendResistedStatus(statuses, "Darkness", statusResistChanceDarkness);
        appendResistedStatus(statuses, "Shell", statusResistChanceShell);
        appendResistedStatus(statuses, "Protect", statusResistChanceProtect);
        appendResistedStatus(statuses, "Reflect", statusResistChanceReflect);
        appendResistedStatus(statuses, "Regen", statusResistChanceRegen);
        appendResistedStatus(statuses, "Slow", statusResistChanceSlow);
        appendResistedStatus(statuses, "Haste", statusResistChanceHaste);
        if (statusInflictChanceNBlaze > 0 && statusDurationNBlaze > 0 &&
                statusInflictChanceNBlaze == statusInflictChanceNFrost &&
                statusInflictChanceNBlaze == statusInflictChanceNShock &&
                statusInflictChanceNBlaze == statusInflictChanceNTide &&
                statusDurationNBlaze == statusDurationNFrost &&
                statusDurationNBlaze == statusDurationNShock &&
                statusDurationNBlaze == statusDurationNTide) {
            appendResistedStatus(statuses, "NulAll", statusResistChanceNBlaze);
        } else {
            appendResistedStatus(statuses, "NulBlaze", statusResistChanceNBlaze);
            appendResistedStatus(statuses, "NulFrost", statusResistChanceNFrost);
            appendResistedStatus(statuses, "NulShock", statusResistChanceNShock);
            appendResistedStatus(statuses, "NulTide", statusResistChanceNTide);
        }
        if (resistScan) { statuses.append(" Scan;"); }
        if (resistShield) { statuses.append(" Shield;"); }
        if (resistBoost) { statuses.append(" Boost;"); }
        if (resistDistillPower) { statuses.append(" Distill Power;"); }
        if (resistDistillMana) { statuses.append(" Distill Mana;"); }
        if (resistDistillSpeed) { statuses.append(" Distill Speed;"); }
        if (resistDistillAbility) { statuses.append(" Distill Ability;"); }
        if (resistUnused1) { statuses.append(" Unused1;"); }
        if (resistEject) { statuses.append(" Eject;"); }
        if (resistAutoLife) { statuses.append(" Auto-Life;"); }
        if (resistCurse) { statuses.append(" Curse;"); }
        if (resistDoom) { statuses.append(" Doom;"); }
        if (resistDefend) { statuses.append(" Defend;"); }
        if (resistGuard) { statuses.append(" Guard;"); }
        if (resistSentinel) { statuses.append(" Sentinel;"); }
        if (resistUnused2) { statuses.append(" Unused2;"); }
        String conv = statuses.toString();
        if (conv.endsWith(";")) {
            String withoutLastSemicolon = conv.substring(0, conv.length() - 1);
            if (withoutLastSemicolon.contains(";")) {
                return withoutLastSemicolon + " }";
            } else {
                return "Resist " + withoutLastSemicolon.substring(9);
            }
        } else {
            return "";
        }
    }

    private void appendResistedStatus(StringBuilder builder, String name, int chance) {
        if (chance > 0) {
            builder.append(' ').append(name).append(" (").append(chance).append("%);");
        }
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
        /* if (duration == 255) {
            return "Auto";
        } else */ if (duration >= 254) {
            return "Endless";
        } else if (duration == 1) {
            return blocks ? "1 block" : "1 turn";
        } else if (duration >= 0) {
            return duration + (blocks ? " blocks" : " turns");
        } else {
            return null;
        }
    }

    private static String formatUnknownByte(int bt) {
        return String.format("%02x", bt) + '=' + String.format("%03d", bt) + '(' + String.format("%8s", Integer.toBinaryString(bt)).replace(' ', '0') + ')';
    }

    private int read2Bytes(int offset) {
        int val = bytes[offset];
        val += bytes[offset+1] * 0x100;
        return val;
    }
}
