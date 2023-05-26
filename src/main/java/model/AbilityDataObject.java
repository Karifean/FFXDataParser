package model;

import main.StringHelper;
import script.model.ScriptConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * command.bin
 * monmagic1.bin
 * monmagic2.bin
 * item.bin
 */
public class AbilityDataObject {
    static Map<Integer, String> submenus;

    private final boolean isCharacterAbility;
    private final int[] bytes;

    public String name;
    public String dash;
    public String description;
    public String otherText;
    public int nameOffset;
    int unknownByte0B;
    public int otherTextOffset;
    int unknownByte0E;
    int unknownByte0F;
    int anim1;
    int anim2;
    int icon;
    int casterAnimation;
    int menuProperties16;
    int subsubMenuCategorization;
    // /|\ Always equal to subMenuCategorization except on nested menus in menus like Use/Quick Pockets
    //  |  and also on Requiem for some reason.
    int subMenuCategorization;
    int characterUser;
    int targetingFlags;
    int unknownProperties1B;
    int miscProperties1C;
    int miscProperties1D;
    int miscProperties1E;
    int animationProperties1F;
    int damageProperties20;
    int stealGilByte;
    int partyPreviewByte;
    int damageClass;
    int moveRank;
    int costMP;
    int costOD;
    int attackCritBonus;
    int damageFormula;
    int attackAccuracy;
    int attackPower;
    int hitCount;
    int shatterChance;
    int elementFlags;
    int statusChanceDeath;
    int statusChanceZombie;
    int statusChancePetrify;
    int statusChancePoison;
    int statusChancePowerBreak;
    int statusChanceMagicBreak;
    int statusChanceArmorBreak;
    int statusChanceMentalBreak;
    int statusChanceConfuse;
    int statusChanceBerserk;
    int statusChanceProvoke;
    int statusChanceThreaten;
    int statusChanceSleep;
    int statusChanceSilence;
    int statusChanceDarkness;
    int statusChanceShell;
    int statusChanceProtect;
    int statusChanceReflect;
    int statusChanceNTide;
    int statusChanceNBlaze;
    int statusChanceNShock;
    int statusChanceNFrost;
    int statusChanceRegen;
    int statusChanceHaste;
    int statusChanceSlow;
    int statusDurationSleep;
    int statusDurationSilence;
    int statusDurationDarkness;
    int statusDurationShell;
    int statusDurationProtect;
    int statusDurationReflect;
    int statusDurationRegen;
    int statusDurationSlow;
    int statusDurationHaste;
    int statusDurationNBlaze;
    int statusDurationNFrost;
    int statusDurationNShock;
    int statusDurationNTide;
    int extraStatusFlags1;
    int extraStatusFlags2;
    int statBuffFlags;
    int alwaysZero57;
    int overdriveCategorizationByte;
    int statBuffValue;
    int specialBuffFlags;
    int alwaysZero5B;
    int unknownByte2;
    int unknownByte3;
    public int dashOffset;
    public int descriptionOffset;
    int unknownByte6;
    int unknownByte7;
    int unknownByte0A;
    int orderingIndexInMenu;
    int sphereGridUsageRole;
    int alwaysZero5E;
    int alwaysZero5F;

    String overdriveCharacter;
    int overdriveCategory;

    boolean usableOutsideCombat;
    boolean usableInCombat;
    boolean byte1Cbit08SetOnCharAttacksAndSkillsAndValeforShivaAttack;
    boolean byte1Cbit20SetOnControllableAeonNormalAttacks; // Maybe: Force using char Accuracy formula?
    boolean affectedByDarkness;
    public boolean displayMoveName;
    boolean canMiss;
    boolean canBeReflected;
    boolean absorbDamage;
    boolean targetEnabled;
    boolean targetEnemies;
    boolean targetMulti;
    boolean targetSelfOnly;
    boolean targetFlag5;
    boolean targetEitherTeam;
    boolean targetDead;
    boolean targetFlag8LongRangeMaybe;
    boolean onTopLevelInMenu;
    boolean opensSubMenu;
    boolean byte1Fbit01;
    boolean byte1Fbit02WhichIsOnlySetOnEntrust;
    boolean useCastAnimationMaybe;
    boolean userRunsOffScreen;
    boolean showSpellAuraMaybe;
    boolean byte1Fbit20;
    boolean someFlagSetOnALLAeonOverdrives;
    boolean byte1Fbit80WhichIsOnlySetOnBribe;
    boolean partyPreviewActive;
    boolean partyPreviewHealHp;
    boolean partyPreviewHealMp;
    boolean partyPreviewHealStatuses;
    boolean randomTargets;
    boolean isPiercing;
    boolean disableWhenSilenced;
    boolean usesWeaponProperties;
    boolean isTriggerCommand;
    boolean useTier1CastAnimation;
    boolean useTier3CastAnimation;
    boolean destroyCaster;
    boolean missIfAlive;
    boolean breaksDamageLimit;
    boolean damageTypePhysical;
    boolean damageTypeMagical;
    boolean canCrit;
    boolean byte20bit08usedOnMostStrAttacks;
    boolean suppressBDLMaybe;
    boolean damageClassHP;
    boolean damageClassMP;
    boolean damageClassCTB;
    boolean damageClassUnknown;
    boolean isHealing;
    boolean isCleansingStatuses;
    boolean useInUseMenu;
    boolean useInRightMenu;
    boolean useInLeftMenu;
    boolean stealItem;
    boolean stealGil;
    boolean inflictDelayWeak;
    boolean inflictDelayStrong;
    boolean elementFire;
    boolean elementIce;
    boolean elementThunder;
    boolean elementWater;
    boolean elementHoly;
    boolean element6;
    boolean element7;
    boolean element8;
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
    boolean statBuffCheer;
    boolean statBuffFocus;
    boolean statBuffAim;
    boolean statBuffReflex;
    boolean statBuffLuck;
    boolean statBuffJinx;
    boolean statBuffUnused1;
    boolean statBuffUnused2;
    boolean specialBuffDoubleHP;
    boolean specialBuffDoubleMP;
    boolean specialBuffMPCost0;
    boolean specialBuffQuartet;
    boolean specialBuffAlwaysCrit;
    boolean specialBuffOverdrive150;
    boolean specialBuffOverdrive200;
    boolean specialBuffUnused;

    public AbilityDataObject(int[] bytes, int[] stringBytes) {
        this.bytes = bytes;
        isCharacterAbility = (bytes.length == 96);
        prepareMaps();
        mapBytes();
        mapFlags();
        mapStrings(stringBytes);
    }

    public String getName() {
        if (!displayMoveName) {
            return "[" + name + "]";
        }
        return name;
    }

    private void mapBytes() {
        nameOffset = read2Bytes(0x00);
        unknownByte2 = bytes[0x02];
        unknownByte3 = bytes[0x03];
        dashOffset = read2Bytes(0x04);
        unknownByte6 = bytes[0x06];
        unknownByte7 = bytes[0x07];
        descriptionOffset = read2Bytes(0x08);
        unknownByte0A = bytes[0x0A];
        unknownByte0B = bytes[0x0B];
        otherTextOffset = read2Bytes(0x0C);
        unknownByte0E = bytes[0x0E];
        unknownByte0F = bytes[0x0F];
        anim1 = read2Bytes(0x10);
        anim2 = read2Bytes(0x12);
        icon = bytes[0x14];
        casterAnimation = bytes[0x15];
        menuProperties16 = bytes[0x16];
        subsubMenuCategorization = bytes[0x17];
        subMenuCategorization = bytes[0x18];
        characterUser = bytes[0x19];
        targetingFlags = bytes[0x1A];
        unknownProperties1B = bytes[0x1B];
        miscProperties1C = bytes[0x1C];
        miscProperties1D = bytes[0x1D];
        miscProperties1E = bytes[0x1E];
        animationProperties1F = bytes[0x1F];
        damageProperties20 = bytes[0x20];
        stealGilByte = bytes[0x21];
        partyPreviewByte = bytes[0x22];
        damageClass = bytes[0x23];
        moveRank = bytes[0x24];
        costMP = bytes[0x25];
        costOD = bytes[0x26];
        attackCritBonus = bytes[0x27];
        damageFormula = bytes[0x28];
        attackAccuracy = bytes[0x29];
        attackPower = bytes[0x2A];
        hitCount = bytes[0x2B];
        shatterChance = bytes[0x2C];
        elementFlags = bytes[0x2D];
        statusChanceDeath = bytes[0x2E];
        statusChanceZombie = bytes[0x2F];
        statusChancePetrify = bytes[0x30];
        statusChancePoison = bytes[0x31];
        statusChancePowerBreak = bytes[0x32];
        statusChanceMagicBreak = bytes[0x33];
        statusChanceArmorBreak = bytes[0x34];
        statusChanceMentalBreak = bytes[0x35];
        statusChanceConfuse = bytes[0x36];
        statusChanceBerserk = bytes[0x37];
        statusChanceProvoke = bytes[0x38];
        statusChanceThreaten = bytes[0x39];
        statusChanceSleep = bytes[0x3A];
        statusChanceSilence = bytes[0x3B];
        statusChanceDarkness = bytes[0x3C];
        statusChanceShell = bytes[0x3D];
        statusChanceProtect = bytes[0x3E];
        statusChanceReflect = bytes[0x3F];
        statusChanceNTide = bytes[0x40];
        statusChanceNBlaze = bytes[0x41];
        statusChanceNShock = bytes[0x42];
        statusChanceNFrost = bytes[0x43];
        statusChanceRegen = bytes[0x44];
        statusChanceHaste = bytes[0x45];
        statusChanceSlow = bytes[0x46];
        statusDurationSleep = bytes[0x47];
        statusDurationSilence = bytes[0x48];
        statusDurationDarkness = bytes[0x49];
        statusDurationShell = bytes[0x4A];
        statusDurationProtect = bytes[0x4B];
        statusDurationReflect = bytes[0x4C];
        statusDurationNTide = bytes[0x4D];
        statusDurationNBlaze = bytes[0x4E];
        statusDurationNShock = bytes[0x4F];
        statusDurationNFrost = bytes[0x50];
        statusDurationRegen = bytes[0x51];
        statusDurationHaste = bytes[0x52];
        statusDurationSlow = bytes[0x53];
        extraStatusFlags1 = bytes[0x54];
        extraStatusFlags2 = bytes[0x55];
        statBuffFlags = bytes[0x56];
        alwaysZero57 = bytes[0x57];
        overdriveCategorizationByte = bytes[0x58];
        statBuffValue = bytes[0x59];
        specialBuffFlags = bytes[0x5A];
        alwaysZero5B = bytes[0x5B];
        if (isCharacterAbility) {
            orderingIndexInMenu = bytes[0x5C];
            sphereGridUsageRole = bytes[0x5D];
            alwaysZero5E = bytes[0x5E];
            alwaysZero5F = bytes[0x5F];
        }
    }

    private void mapFlags() {
        targetEnabled = (targetingFlags & 0x01) > 0;
        targetEnemies = (targetingFlags & 0x02) > 0;
        targetMulti = (targetingFlags & 0x04) > 0;
        targetSelfOnly = (targetingFlags & 0x08) > 0;
        targetFlag5 = (targetingFlags & 0x10) > 0;
        targetEitherTeam = (targetingFlags & 0x20) > 0;
        targetDead = (targetingFlags & 0x40) > 0;
        targetFlag8LongRangeMaybe = (targetingFlags & 0x80) > 0;
        onTopLevelInMenu = (menuProperties16 & 0x01) > 0;
        opensSubMenu = (menuProperties16 & 0x10) > 0;
        usableOutsideCombat = (miscProperties1C & 0x01) > 0;
        usableInCombat = (miscProperties1C & 0x02) > 0;
        displayMoveName = (miscProperties1C & 0x04) > 0;
        byte1Cbit08SetOnCharAttacksAndSkillsAndValeforShivaAttack = (miscProperties1C & 0x08) > 0;
        canMiss = (miscProperties1C & 0x10) > 0;
        byte1Cbit20SetOnControllableAeonNormalAttacks = (miscProperties1C & 0x20) > 0;
        affectedByDarkness = (miscProperties1C & 0x40) > 0;
        canBeReflected = (miscProperties1C & 0x80) > 0;
        absorbDamage = (miscProperties1D & 0x01) > 0;
        stealItem = (miscProperties1D & 0x02) > 0;
        useInUseMenu = (miscProperties1D & 0x04) > 0;
        useInRightMenu = (miscProperties1D & 0x08) > 0;
        useInLeftMenu = (miscProperties1D & 0x10) > 0;
        inflictDelayWeak = (miscProperties1D & 0x20) > 0;
        inflictDelayStrong = (miscProperties1D & 0x40) > 0;
        randomTargets = (miscProperties1D & 0x80) > 0;
        isPiercing = (miscProperties1E & 0x01) > 0;
        disableWhenSilenced = (miscProperties1E & 0x02) > 0;
        usesWeaponProperties = (miscProperties1E & 0x04) > 0;
        isTriggerCommand = (miscProperties1E & 0x08) > 0;
        useTier1CastAnimation = (miscProperties1E & 0x10) > 0;
        useTier3CastAnimation = (miscProperties1E & 0x20) > 0;
        destroyCaster = (miscProperties1E & 0x40) > 0;
        missIfAlive = (miscProperties1E & 0x80) > 0;
        byte1Fbit01 = (animationProperties1F & 0x01) > 0;
        byte1Fbit02WhichIsOnlySetOnEntrust = (animationProperties1F & 0x02) > 0;
        useCastAnimationMaybe = (animationProperties1F & 0x04) > 0;
        userRunsOffScreen = (animationProperties1F & 0x08) > 0;
        showSpellAuraMaybe = (animationProperties1F & 0x10) > 0;
        byte1Fbit20 = (animationProperties1F & 0x20) > 0;
        someFlagSetOnALLAeonOverdrives = (animationProperties1F & 0x40) > 0;
        byte1Fbit80WhichIsOnlySetOnBribe = (animationProperties1F & 0x80) > 0;
        damageTypePhysical = (damageProperties20 & 0x01) > 0;
        damageTypeMagical = (damageProperties20 & 0x02) > 0;
        canCrit = (damageProperties20 & 0x04) > 0;
        byte20bit08usedOnMostStrAttacks = (damageProperties20 & 0x08) > 0; // Seems to be needed for proper evasion?
        isHealing = (damageProperties20 & 0x10) > 0;
        isCleansingStatuses = (damageProperties20 & 0x20) > 0;
        suppressBDLMaybe = (damageProperties20 & 0x40) > 0;
        breaksDamageLimit = (damageProperties20 & 0x80) > 0;
        stealGil = (stealGilByte & 0x01) > 0;
        partyPreviewActive = (partyPreviewByte & 0x01) > 0;
        partyPreviewHealMp = (partyPreviewByte & 0x02) > 0;
        partyPreviewHealStatuses = (partyPreviewByte & 0x04) > 0;
        partyPreviewHealHp = (partyPreviewByte & 0x40) > 0;
        damageClassHP = (damageClass & 0x01) > 0;
        damageClassMP = (damageClass & 0x02) > 0;
        damageClassCTB = (damageClass & 0x04) > 0;
        damageClassUnknown = damageClass >= 0x08;
        elementFire = (elementFlags & 0x01) > 0;
        elementIce = (elementFlags & 0x02) > 0;
        elementThunder = (elementFlags & 0x04) > 0;
        elementWater = (elementFlags & 0x08) > 0;
        elementHoly = (elementFlags & 0x10) > 0;
        element6 = (elementFlags & 0x20) > 0;
        element7 = (elementFlags & 0x40) > 0;
        element8 = (elementFlags & 0x80) > 0;
        inflictScan = (extraStatusFlags1 & 0x01) > 0;
        inflictDistillPower = (extraStatusFlags1 & 0x02) > 0;
        inflictDistillMana = (extraStatusFlags1 & 0x04) > 0;
        inflictDistillSpeed = (extraStatusFlags1 & 0x08) > 0;
        inflictUnused1 = (extraStatusFlags1 & 0x10) > 0;
        inflictDistillAbility = (extraStatusFlags1 & 0x20) > 0;
        inflictShield = (extraStatusFlags1 & 0x40) > 0;
        inflictBoost = (extraStatusFlags1 & 0x80) > 0;
        inflictEject = (extraStatusFlags2 & 0x01) > 0;
        inflictAutoLife = (extraStatusFlags2 & 0x02) > 0;
        inflictCurse = (extraStatusFlags2 & 0x04) > 0;
        inflictDefend = (extraStatusFlags2 & 0x08) > 0;
        inflictGuard = (extraStatusFlags2 & 0x10) > 0;
        inflictSentinel = (extraStatusFlags2 & 0x20) > 0;
        inflictDoom = (extraStatusFlags2 & 0x40) > 0;
        inflictUnused2 = (extraStatusFlags2 & 0x80) > 0;
        statBuffCheer = (statBuffFlags & 0x01) > 0;
        statBuffAim = (statBuffFlags & 0x02) > 0;
        statBuffFocus = (statBuffFlags & 0x04) > 0;
        statBuffReflex = (statBuffFlags & 0x08) > 0;
        statBuffLuck = (statBuffFlags & 0x10) > 0;
        statBuffJinx = (statBuffFlags & 0x20) > 0;
        statBuffUnused1 = (statBuffFlags & 0x40) > 0;
        statBuffUnused2 = (statBuffFlags & 0x80) > 0;
        specialBuffDoubleHP = (specialBuffFlags & 0x01) > 0;
        specialBuffDoubleMP = (specialBuffFlags & 0x02) > 0;
        specialBuffMPCost0 = (specialBuffFlags & 0x04) > 0;
        specialBuffQuartet = (specialBuffFlags & 0x08) > 0;
        specialBuffAlwaysCrit = (specialBuffFlags & 0x10) > 0;
        specialBuffOverdrive150 = (specialBuffFlags & 0x20) > 0;
        specialBuffOverdrive200 = (specialBuffFlags & 0x40) > 0;
        specialBuffUnused = (specialBuffFlags & 0x80) > 0;
        if (overdriveCategorizationByte > 0) {
            overdriveCharacter = ScriptConstants.getEnumMap("actor").get(overdriveCategorizationByte & 0x0F).name;
            overdriveCategory = overdriveCategorizationByte / 0x10;
        }
    }

    private void mapStrings(int[] stringBytes) {
        name = StringHelper.getStringAtLookupOffset(stringBytes, nameOffset);
        dash = StringHelper.getStringAtLookupOffset(stringBytes, dashOffset);
        description = StringHelper.getStringAtLookupOffset(stringBytes, descriptionOffset);
        otherText = StringHelper.getStringAtLookupOffset(stringBytes, otherTextOffset);
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        list.add(!usableInCombat ? "Unusable" : "");
        list.add(damageKind());
        list.add(ifG0(damageFormula, "Formula=", ""));
        list.add(ifG0(hitCount, "", "-hit"));
        list.add(ifG0(attackPower, "Power=", ""));
        list.add(usesWeaponProperties ? "Uses Weapon Properties" : "");
        list.add(breaksDamageLimit ? "BDL" : "");
        list.add(suppressBDLMaybe ? "Never BDL" : "");
        list.add(targeting());
        list.add("Rank=" + moveRank);
        list.add(ifG0(costMP, "", " MP"));
        list.add(usableOutsideCombat ? "Usable outside combat" : "");
        list.add(characterUser());
        list.add(onTopLevelInMenu ? "Toplevel" : "");
        list.add(useInRightMenu ? "Topmenu=Right" : "");
        list.add(useInLeftMenu ? "Topmenu=Left" : "");
        list.add(isTriggerCommand ? "Trigger-Command" : "");
        list.add(opensSubMenu ? "Opens Submenu" : "");
        list.add(ifNN(submenus.get(subMenuCategorization), "Submenu=\"", "\""));
        list.add(subsubMenuCategorization != subMenuCategorization ? "Subsubmenu=" + submenus.get(subsubMenuCategorization) : "");
        list.add(useInUseMenu ? "In \"Use\" Menu" : "");
        list.add(ifG0(costOD, "Overdrive (", "p)"));
        list.add(ifNN(overdriveCharacter, "OD-User=", ""));
        list.add(ifG0(overdriveCategory, "OD-Choice=", ""));
        list.add(isPiercing ? "Piercing" : "");
        list.add(canMiss ? "Can miss" : "");
        list.add(ifG0(attackAccuracy, "Acc=", "%"));
        list.add(affectedByDarkness ? "Darkable" : "");
        list.add(disableWhenSilenced ? "Silenceable" : "");
        list.add(canBeReflected ? "Reflectable" : "");
        list.add(canCrit ? "Can crit" + (attackCritBonus > 0 ? " (+" + attackCritBonus + "%)" : "") : "");
        list.add(byte20bit08usedOnMostStrAttacks ? "byte20bit08" : "");
        list.add(byte1Cbit08SetOnCharAttacksAndSkillsAndValeforShivaAttack ? "byte1Cbit08" : "");
        list.add(byte1Cbit20SetOnControllableAeonNormalAttacks ? "byte1Cbit20" : "");
        list.add(elements());
        list.add(statuses());
        list.add(statBuffs());
        list.add(specialBuffs());
        list.add(destroyCaster ? "Removes Caster" : "");
        list.add(stealItem ? "Steal Item" : "");
        list.add(stealGil ? "Steal Gil" : "");
        list.add(inflictDelayWeak ? "Delay (Weak)" : "");
        list.add(inflictDelayStrong ? "Delay (Strong)" : "");
        list.add(ifG0(shatterChance, "Shatter=", "%"));
        list.add("anim=" + casterAnimation + (useTier1CastAnimation ? "L" : "") + (useTier3CastAnimation ? "H" : "") +
                "/" + String.format("%04X", anim1) +
                "/" + String.format("%04X", anim2));
        list.add(ifG0(alwaysZero57, "Byte57=", ""));
        list.add(ifG0(alwaysZero5B, "Byte5B=", ""));
        list.add(ifG0(alwaysZero5E, "Byte5E=", ""));
        list.add(ifG0(alwaysZero5F, "Byte5F=", ""));
        String full = list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining(", "));
        String dashStr = (dashOffset > 0 && !"-".equals(dash) ? "DH=" + dash + " / " : "");
        String descriptionStr = (descriptionOffset > 0 && !"-".equals(description) ? description : "");
        String soText = (otherTextOffset > 0 && !"-".equals(otherText) ? " / OT=" + otherText : "");
        return String.format("%-20s", getName()) + " { " + full + " } " + dashStr + descriptionStr + soText;
    }

    private String damageKind() {
        String damageType = damageTypePhysical ? "Physical" : (damageTypeMagical ? " Magical" : " Special");
        if (damageClassHP || damageClassMP || damageClassCTB) {
            String damageClassString = "";
            if (damageClassHP) {
                damageClassString += "HP/";
            }
            if (damageClassMP) {
                damageClassString += "MP/";
            }
            if (damageClassCTB) {
                damageClassString += "CTB/";
            }
            if (damageClassUnknown) {
                damageClassString += "Unknown(" + String.format("%02X", damageClass) + ")/";
            }
            damageClassString = damageClassString.substring(0, damageClassString.length() - 1);
            return damageType + ' ' + damageClassString + ' ' + (isHealing ? "Restore" : (absorbDamage ? "Absorb" : "Damage"));
        } else {
            return damageType;
        }
    }

    private String characterUser() {
        if (isCharacterAbility) {
            if (characterUser == 0xFF) {
                return "Usable by anyone";
            } else {
                return "Usable by " + ScriptConstants.getEnumMap("actor").get(characterUser).name;
            }
        } else {
            return "";
        }
    }

    private String statBuffs() {
        if (statBuffValue <= 0 && statBuffFlags <= 0) {
            return "";
        }
        String statBuffTypes = "";
        if (statBuffCheer) {
            statBuffTypes += "Cheer/";
        }
        if (statBuffFocus) {
            statBuffTypes += "Focus/";
        }
        if (statBuffAim) {
            statBuffTypes += "Aim/";
        }
        if (statBuffReflex) {
            statBuffTypes += "Reflex/";
        }
        if (statBuffLuck) {
            statBuffTypes += "Luck/";
        }
        if (statBuffJinx) {
            statBuffTypes += "Jinx/";
        }
        if (statBuffUnused1) {
            statBuffTypes += "UnusedBuff-40/";
        }
        if (statBuffUnused2) {
            statBuffTypes += "UnusedBuff-80/";
        }
        if (statBuffTypes.isEmpty()) {
            statBuffTypes = "NullBuff";
        } else {
            statBuffTypes = statBuffTypes.substring(0, statBuffTypes.length() - 1);
        }
        return statBuffTypes + " x" + statBuffValue;
    }

    private String elements() {
        StringBuilder elements = new StringBuilder("Multi-Element {");
        if (elementFire) { elements.append(" Fire;"); }
        if (elementIce) { elements.append(" Ice;"); }
        if (elementThunder) { elements.append(" Thunder;"); }
        if (elementWater) { elements.append(" Water;"); }
        if (elementHoly) { elements.append(" Holy;"); }
        if (element6) { elements.append(" 6;"); }
        if (element7) { elements.append(" 7;"); }
        if (element8) { elements.append(" 8;"); }
        String conv = elements.toString();
        if (conv.endsWith(";")) {
            String withoutLastSemicolon = conv.substring(0, conv.length() - 1);
            if (withoutLastSemicolon.indexOf(';') > 0) {
                return withoutLastSemicolon + " }";
            } else {
                return "Element=" + withoutLastSemicolon.substring(16);
            }
        } else {
            return "";
        }
    }

    private String targeting() {
        String target;
        if (!targetEnabled) {
            target = "Menu";
        } else if (targetSelfOnly) {
            target = (targetMulti ? "Self" : "Team") + " (Forced" + (randomTargets ? ", Random" : "") + ')';
        } else {
            target = targetEnemies ? "Enem" : "All";
            if (targetMulti) {
                target = (randomTargets ? "Random " : "All ") + target + "ies";
            } else {
                target = "1 " + (randomTargets ? "random " : "") + target + "y";
            }
            if (!targetEitherTeam) {
                target += "!!";
            }
        }
        if (targetFlag5) {
            target += "/5";
        }
        if (targetFlag8LongRangeMaybe) {
            target += "/8";
        }
        if (targetDead) {
            target += "/Dead";
        }
        return target;
    }

    private String statuses() {
        StringBuilder statuses = new StringBuilder(isCleansingStatuses ? "Remove" : "Inflict").append(" {");
        appendPermanentStatus(statuses, "Death", statusChanceDeath);
        appendPermanentStatus(statuses, "Zombie", statusChanceZombie);
        appendPermanentStatus(statuses, "Petrify", statusChancePetrify);
        appendPermanentStatus(statuses, "Poison", statusChancePoison);
        appendPermanentStatus(statuses, "Confuse", statusChanceConfuse);
        appendPermanentStatus(statuses, "Berserk", statusChanceBerserk);
        appendPermanentStatus(statuses, "Provoke", statusChanceProvoke);
        appendPermanentStatus(statuses, "Threaten", statusChanceThreaten);
        if (statusChancePowerBreak > 0 &&
            statusChancePowerBreak == statusChanceMagicBreak &&
            statusChancePowerBreak == statusChanceArmorBreak &&
            statusChancePowerBreak == statusChanceMentalBreak) {
            appendPermanentStatus(statuses, "All Breaks", statusChancePowerBreak);
        } else {
            appendPermanentStatus(statuses, "Power Break", statusChancePowerBreak);
            appendPermanentStatus(statuses, "Magic Break", statusChanceMagicBreak);
            appendPermanentStatus(statuses, "Armor Break", statusChanceArmorBreak);
            appendPermanentStatus(statuses, "Mental Break", statusChanceMentalBreak);
        }
        appendTemporaryStatus(statuses, "Sleep", statusChanceSleep, statusDurationSleep, false);
        appendTemporaryStatus(statuses, "Silence", statusChanceSilence, statusDurationSilence, false);
        appendTemporaryStatus(statuses, "Darkness", statusChanceDarkness, statusDurationDarkness, false);
        appendTemporaryStatus(statuses, "Shell", statusChanceShell, statusDurationShell, false);
        appendTemporaryStatus(statuses, "Protect", statusChanceProtect, statusDurationProtect, false);
        appendTemporaryStatus(statuses, "Reflect", statusChanceReflect, statusDurationReflect, false);
        appendTemporaryStatus(statuses, "Regen", statusChanceRegen, statusDurationRegen, false);
        appendTemporaryStatus(statuses, "Slow", statusChanceSlow, statusDurationSlow, false);
        appendTemporaryStatus(statuses, "Haste", statusChanceHaste, statusDurationHaste, false);
        if (statusChanceNBlaze > 0 && statusDurationNBlaze > 0 &&
            statusChanceNBlaze == statusChanceNFrost &&
            statusChanceNBlaze == statusChanceNShock &&
            statusChanceNBlaze == statusChanceNTide &&
            statusDurationNBlaze == statusDurationNFrost &&
            statusDurationNBlaze == statusDurationNShock &&
            statusDurationNBlaze == statusDurationNTide) {
            appendTemporaryStatus(statuses, "NulAll", statusChanceNBlaze, statusDurationNBlaze, true);
        } else {
            appendTemporaryStatus(statuses, "NulBlaze", statusChanceNBlaze, statusDurationNBlaze, true);
            appendTemporaryStatus(statuses, "NulFrost", statusChanceNFrost, statusDurationNFrost, true);
            appendTemporaryStatus(statuses, "NulShock", statusChanceNShock, statusDurationNShock, true);
            appendTemporaryStatus(statuses, "NulTide", statusChanceNTide, statusDurationNTide, true);
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
            return conv.substring(0, conv.length() - 1) + " }";
        } else {
            return "";
        }
    }

    private String specialBuffs() {
        StringBuilder buffs = new StringBuilder("Special Buffs {");
        if (specialBuffDoubleHP) { buffs.append(" Double HP;"); }
        if (specialBuffDoubleMP) { buffs.append(" Double MP;"); }
        if (specialBuffMPCost0) { buffs.append(" Spellspring;"); }
        if (specialBuffQuartet) { buffs.append(" Quartet of 9;"); }
        if (specialBuffAlwaysCrit) { buffs.append(" Always Crit;"); }
        if (specialBuffOverdrive150) { buffs.append(" Overdrive x1.5;"); }
        if (specialBuffOverdrive200) { buffs.append(" Overdrive x2;"); }
        if (specialBuffUnused) { buffs.append(" Unused;"); }
        String conv = buffs.toString();
        if (conv.endsWith(";")) {
            String withoutLastSemicolon = conv.substring(0, conv.length() - 1);
            if (withoutLastSemicolon.indexOf(';') > 0) {
                return withoutLastSemicolon + " }";
            } else {
                return "Special Buff:" + withoutLastSemicolon.substring(15);
            }
        } else {
            return "";
        }
    }

    private void appendPermanentStatus(StringBuilder builder, String name, int chance) {
        if (chance > 0) {
            builder.append(' ').append(name);
            if (!isCleansingStatuses || chance < 254) {
                builder.append(" (").append(statusChanceString(chance)).append(')');
            }
            builder.append(';');
        }
    }

    private void appendTemporaryStatus(StringBuilder builder, String name, int chance, int duration, boolean blocks) {
        if (chance > 0) {
            builder.append(' ').append(name);
            if (!isCleansingStatuses || chance < 254 || duration < 254) {
                builder.append(" (").append(statusChanceString(chance)).append(", ").append(statusDurationString(duration, blocks)).append(')');
            }
            builder.append(';');
        }
    }

    private static String ifG0(int value, String prefix, String postfix) {
        if (value > 0) {
            return prefix + value + postfix;
        } else {
            return "";
        }
    }

    private static String ifNN(String value, String prefix, String postfix) {
        if (value != null) {
            return prefix + value + postfix;
        } else {
            return "";
        }
    }

    private static String statusChanceString(int chance) {
        if (chance == 255) {
            return "Always";
        } else if (chance == 254) {
            return "Infinite%";
        } else if (chance > 0) {
            return chance + "%";
        } else {
            return null;
        }
    }

    private static String statusDurationString(int duration, boolean blocks) {
        if (duration == 255) {
            return "Auto";
        } else if (duration == 254) {
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
        return String.format("%02X", bt) + '=' + String.format("%03d", bt) + '(' + String.format("%8s", Integer.toBinaryString(bt)).replace(' ', '0') + ')';
    }

    private static void prepareMaps() {
        if (submenus == null) {
            submenus = new HashMap<>();
            submenus.put(1, "Black Magic");
            submenus.put(2, "White Magic");
            submenus.put(3, "Skill");
            submenus.put(4, "Overdrive");
            submenus.put(5, "Summon");
            submenus.put(6, "Items");
            submenus.put(7, "Weapon Change");
            submenus.put(8, "Escape");
            submenus.put(10, "Switch Character");
            submenus.put(14, "Special");
            submenus.put(15, "Armor Change");
            submenus.put(17, "Use");
            submenus.put(20, "Mix");
            submenus.put(21, "Gil (Bribe/SC)");
            submenus.put(22, "Gil (Pay Yoji)");
        }
    }

    private int read2Bytes(int offset) {
        return bytes[offset] + bytes[offset+1] * 0x100;
    }
}
