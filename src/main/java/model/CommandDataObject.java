package model;

import atel.model.StackObject;
import main.DataAccess;
import main.DataReadingManager;
import main.StringHelper;
import model.spheregrid.SphereGridSphereTypeDataObject;
import model.strings.LocalizedKeyedStringObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static reading.BytesHelper.write2Bytes;
import static reading.BytesHelper.read2Bytes;

/**
 * command.bin
 * monmagic1.bin
 * monmagic2.bin
 * item.bin
 */
public class CommandDataObject extends NameDescriptionTextObject implements Nameable, Writable {
    public static final int COM_LENGTH = 0x5C;
    public static final int PCCOM_LENGTH = 0x60;
    static Map<Integer, String> submenus;

    private final boolean isCharacterAbility;

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
    int targetsAllowedApparently;
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
    int extraStatusInflict;
    int statBuffFlags;
    int overdriveCategorizationByte;
    int statBuffValue;
    int specialBuffInflict;
    int orderingIndexInMenu;
    int sphereTypeForSphereGrid;
    int alwaysZero5E;
    int alwaysZero5F;

    String overdriveCharacter;
    int overdriveCategory;

    boolean usableOutsideCombat;
    boolean usableInCombat;
    int hitCalcType;
    boolean hitCalcUsesTable;
    boolean affectedByDarkness;
    public boolean displayMoveName;
    boolean canBeReflected;
    boolean absorbDamage;
    boolean targetEnabled;
    boolean targetEnemies;
    boolean targetMulti;
    boolean targetSelfOnly;
    boolean targetFlag5MaybeAbleToChooseWithinTeam;
    boolean targetEitherTeam;
    boolean targetDead;
    boolean targetFlagLongRange;
    boolean onTopLevelInMenu;
    boolean opensSubMenu;
    boolean canChargeOverdriveViaWarriorOrHealerProbably;
    boolean emptiesOverdriveBar;
    boolean showSpellcastAura;
    boolean userRunsOffScreen;
    boolean canBeCopycatted;
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
    boolean usesWeaponProperties; // Gear overwrites Formula and Power, while element and status data are merged
    boolean isTriggerCommand;
    boolean useTier1CastAnimation;
    boolean useTier3CastAnimation;
    boolean destroyCaster;
    boolean missIfAlive;
    boolean breaksDamageLimit;
    boolean damageTypePhysical;
    boolean damageTypeMagical;
    boolean canCrit;
    boolean useGearCritBonus;
    boolean suppressBDL;
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
    boolean statBuffUnused;
    boolean specialBuffDoubleHP;
    boolean specialBuffDoubleMP;
    boolean specialBuffMPCost0;
    boolean specialBuffQuartet;
    boolean specialBuffAlwaysCrit;
    boolean specialBuffOverdrive150;
    boolean specialBuffOverdrive200;
    boolean specialBuffUnused;
    boolean considerSphereType;

    public int gilPrice;

    public CommandDataObject(int[] bytes, int[] stringBytes, String localization, int group) {
        super(bytes, stringBytes, localization);
        isCharacterAbility = group <= 3;
        considerSphereType = group == 2;
        prepareMaps();
        mapBytes(bytes);
        mapFlags();
    }

    @Override
    public String getName(String localization) {
        if (!displayMoveName) {
            return "[" + name.getLocalizedString(localization) + "]";
        }
        return name.getLocalizedString(localization);
    }

    private void mapBytes(int[] bytes) {
        anim1 = read2Bytes(bytes, 0x10);
        anim2 = read2Bytes(bytes, 0x12);
        icon = bytes[0x14];
        casterAnimation = bytes[0x15];
        menuProperties16 = bytes[0x16];
        subsubMenuCategorization = bytes[0x17];
        subMenuCategorization = bytes[0x18];
        characterUser = bytes[0x19];
        targetingFlags = bytes[0x1A];
        targetsAllowedApparently = bytes[0x1B];
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
        extraStatusInflict = read2Bytes(bytes, 0x54);
        statBuffFlags = read2Bytes(bytes, 0x56);
        overdriveCategorizationByte = bytes[0x58];
        statBuffValue = bytes[0x59];
        specialBuffInflict = read2Bytes(bytes, 0x5A);
        if (isCharacterAbility) {
            orderingIndexInMenu = bytes[0x5C];
            sphereTypeForSphereGrid = bytes[0x5D];
            alwaysZero5E = bytes[0x5E];
            alwaysZero5F = bytes[0x5F];
        }
    }

    public int[] toBytes(String localization) {
        int[] array = new int[isCharacterAbility ? CommandDataObject.PCCOM_LENGTH : CommandDataObject.COM_LENGTH];
        System.arraycopy(super.toBytes(localization), 0, array, 0, 0x10);
        write2Bytes(array, 0x10, anim1);
        write2Bytes(array, 0x12, anim2);
        array[0x14] = icon;
        array[0x15] = casterAnimation;
        array[0x16] = menuProperties16;
        array[0x17] = subsubMenuCategorization;
        array[0x18] = subMenuCategorization;
        array[0x19] = characterUser;
        array[0x1A] = targetingFlags;
        array[0x1B] = targetsAllowedApparently;
        array[0x1C] = miscProperties1C;
        array[0x1D] = miscProperties1D;
        array[0x1E] = miscProperties1E;
        array[0x1F] = animationProperties1F;
        array[0x20] = damageProperties20;
        array[0x21] = stealGil ? 1 : 0;
        array[0x22] = partyPreviewByte;
        array[0x23] = damageClass;
        array[0x24] = moveRank;
        array[0x25] = costMP;
        array[0x26] = costOD;
        array[0x27] = attackCritBonus;
        array[0x28] = damageFormula;
        array[0x29] = attackAccuracy;
        array[0x2A] = attackPower;
        array[0x2B] = hitCount;
        array[0x2C] = shatterChance;
        array[0x2D] = elementFlags;
        array[0x2E] = statusChanceDeath;
        array[0x2F] = statusChanceZombie;
        array[0x30] = statusChancePetrify;
        array[0x31] = statusChancePoison;
        array[0x32] = statusChancePowerBreak;
        array[0x33] = statusChanceMagicBreak;
        array[0x34] = statusChanceArmorBreak;
        array[0x35] = statusChanceMentalBreak;
        array[0x36] = statusChanceConfuse;
        array[0x37] = statusChanceBerserk;
        array[0x38] = statusChanceProvoke;
        array[0x39] = statusChanceThreaten;
        array[0x3A] = statusChanceSleep;
        array[0x3B] = statusChanceSilence;
        array[0x3C] = statusChanceDarkness;
        array[0x3D] = statusChanceShell;
        array[0x3E] = statusChanceProtect;
        array[0x3F] = statusChanceReflect;
        array[0x40] = statusChanceNTide;
        array[0x41] = statusChanceNBlaze;
        array[0x42] = statusChanceNShock;
        array[0x43] = statusChanceNFrost;
        array[0x44] = statusChanceRegen;
        array[0x45] = statusChanceHaste;
        array[0x46] = statusChanceSlow;
        array[0x47] = statusDurationSleep;
        array[0x48] = statusDurationSilence;
        array[0x49] = statusDurationDarkness;
        array[0x4A] = statusDurationShell;
        array[0x4B] = statusDurationProtect;
        array[0x4C] = statusDurationReflect;
        array[0x4D] = statusDurationNTide;
        array[0x4E] = statusDurationNBlaze;
        array[0x4F] = statusDurationNShock;
        array[0x50] = statusDurationNFrost;
        array[0x51] = statusDurationRegen;
        array[0x52] = statusDurationHaste;
        array[0x53] = statusDurationSlow;
        write2Bytes(array, 0x54, extraStatusInflict);
        write2Bytes(array, 0x56, statBuffFlags);
        array[0x58] = overdriveCategorizationByte;
        array[0x59] = statBuffValue;
        write2Bytes(array, 0x5A, specialBuffInflict);
        if (isCharacterAbility) {
            array[0x5C] = orderingIndexInMenu;
            array[0x5D] = sphereTypeForSphereGrid;
            array[0x5E] = alwaysZero5E;
            array[0x5F] = alwaysZero5F;
        }
        return array;
    }

    private void mapFlags() {
        targetEnabled = (targetingFlags & 0x01) > 0;
        targetEnemies = (targetingFlags & 0x02) > 0;
        targetMulti = (targetingFlags & 0x04) > 0;
        targetSelfOnly = (targetingFlags & 0x08) > 0;
        targetFlag5MaybeAbleToChooseWithinTeam = (targetingFlags & 0x10) > 0;
        targetEitherTeam = (targetingFlags & 0x20) > 0;
        targetDead = (targetingFlags & 0x40) > 0;
        targetFlagLongRange = (targetingFlags & 0x80) > 0;
        onTopLevelInMenu = (menuProperties16 & 0x01) > 0;
        opensSubMenu = (menuProperties16 & 0x10) > 0;
        usableOutsideCombat = (miscProperties1C & 0x01) > 0;
        usableInCombat = (miscProperties1C & 0x02) > 0;
        displayMoveName = (miscProperties1C & 0x04) > 0;
        hitCalcType = (miscProperties1C / 0x08) % 8;
        hitCalcUsesTable = (miscProperties1C & 0x08) > 0 || hitCalcType == 6;
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
        canChargeOverdriveViaWarriorOrHealerProbably = (animationProperties1F & 0x01) > 0;
        emptiesOverdriveBar = (animationProperties1F & 0x02) > 0;
        showSpellcastAura = (animationProperties1F & 0x04) > 0;
        userRunsOffScreen = (animationProperties1F & 0x08) > 0;
        canBeCopycatted = (animationProperties1F & 0x10) > 0;
        byte1Fbit20 = (animationProperties1F & 0x20) > 0;
        someFlagSetOnALLAeonOverdrives = (animationProperties1F & 0x40) > 0;
        byte1Fbit80WhichIsOnlySetOnBribe = (animationProperties1F & 0x80) > 0;
        damageTypePhysical = (damageProperties20 & 0x01) > 0;
        damageTypeMagical = (damageProperties20 & 0x02) > 0;
        canCrit = (damageProperties20 & 0x04) > 0;
        useGearCritBonus = (damageProperties20 & 0x08) > 0;
        isHealing = (damageProperties20 & 0x10) > 0;
        isCleansingStatuses = (damageProperties20 & 0x20) > 0;
        suppressBDL = (damageProperties20 & 0x40) > 0;
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
        statBuffCheer = (statBuffFlags & 0x01) > 0;
        statBuffAim = (statBuffFlags & 0x02) > 0;
        statBuffFocus = (statBuffFlags & 0x04) > 0;
        statBuffReflex = (statBuffFlags & 0x08) > 0;
        statBuffLuck = (statBuffFlags & 0x10) > 0;
        statBuffJinx = (statBuffFlags & 0x20) > 0;
        statBuffUnused = statBuffFlags >= 0x40;
        specialBuffDoubleHP = (specialBuffInflict & 0x01) > 0;
        specialBuffDoubleMP = (specialBuffInflict & 0x02) > 0;
        specialBuffMPCost0 = (specialBuffInflict & 0x04) > 0;
        specialBuffQuartet = (specialBuffInflict & 0x08) > 0;
        specialBuffAlwaysCrit = (specialBuffInflict & 0x10) > 0;
        specialBuffOverdrive150 = (specialBuffInflict & 0x20) > 0;
        specialBuffOverdrive200 = (specialBuffInflict & 0x40) > 0;
        specialBuffUnused = specialBuffInflict >= 0x80;
        if (overdriveCategorizationByte > 0) {
            overdriveCharacter = StackObject.enumToScriptField("playerChar", overdriveCategorizationByte & 0x0F).name;
            overdriveCategory = overdriveCategorizationByte >> 4;
        }
    }

    public String nameInAllLanguages() {
        return List.of("us", "de", "fr", "sp", "it", "jp", "ch", "kr").stream().map(l -> getName(l)).collect(Collectors.joining(" / "));
    }

    @Override
    public LocalizedKeyedStringObject getKeyedString(String title) {
        return switch (title) {
            case "name" -> name;
            case "description" -> description;
            default -> null;
        };
    }

    @Override
    public String toString() {
        return toString(DataReadingManager.DEFAULT_LOCALIZATION);
    }

    public String toString(String localization) {
        List<String> list = new ArrayList<>();
        list.add(!usableInCombat ? "Unusable" : "");
        list.add(damageKind());
        list.add(damageFormula > 0 ? "Formula=" + StackObject.enumToString("damageFormula", damageFormula) : "");
        list.add(ifG0(hitCount, "", "-hit"));
        list.add(ifG0(attackPower, "Power=", ""));
        list.add(usesWeaponProperties ? "Uses Weapon Properties" : "");
        list.add(breaksDamageLimit ? "BDL" : "");
        list.add(suppressBDL ? "Never BDL" : "");
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
        list.add(emptiesOverdriveBar ? "Empties OD gauge" : "");
        list.add(canChargeOverdriveViaWarriorOrHealerProbably ? "Charges OD (Warrior/Healer)" : "");
        list.add(isPiercing ? "Piercing" : "");
        list.add("Hit%=" + hitChance() + (hitCalcUsesTable ? " (uses Table)" : ""));
        list.add(missIfAlive ? "Only hits Dead" : "");
        list.add(affectedByDarkness ? "Darkable" : "");
        list.add(disableWhenSilenced ? "Silenceable" : "");
        list.add(canBeReflected ? "Reflectable" : "");
        list.add(canBeCopycatted ? "Copycattable" : "");
        if (canCrit) {
            String bonus = useGearCritBonus ? " (+% from gear)" : (attackCritBonus > 0 ? " (+" + attackCritBonus + "%)" : "");
            list.add("Can crit" + bonus);
        }
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
        list.add(showSpellcastAura ? "Spellcast-Aura" : "");
        list.add("anim=" + casterAnimation + (useTier1CastAnimation ? "L" : "") + (useTier3CastAnimation ? "H" : "") +
                "/" + StringHelper.formatHex4(anim1) +
                "/" + StringHelper.formatHex4(anim2));
        list.add(ifG0(alwaysZero5E, "Byte5E=", ""));
        list.add(ifG0(alwaysZero5F, "Byte5F=", ""));
        list.add(ifG0(gilPrice, "Price=", " gil"));
        if (considerSphereType && sphereTypeForSphereGrid != 0xFF) {
            SphereGridSphereTypeDataObject sgSphereType = DataAccess.SG_SPHERE_TYPES[sphereTypeForSphereGrid];
            list.add("SphereGridRole=" + StringHelper.hex2WithSuffix(sphereTypeForSphereGrid) + " " + (sgSphereType != null ? sgSphereType : "null"));
        }
        String full = list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining(", "));
        String descriptionStr = description.getDefaultString();
        return String.format("%-22s", getName(localization)) + " { " + full + " } " + descriptionStr;
    }

    private String damageKind() {
        String damageType = damageTypePhysical ? (damageTypeMagical ? "  Hybrid" : "Physical") : (damageTypeMagical ? " Magical" : " Special");
        if (damageClass > 0) {
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
                damageClassString += "Unknown(" + StringHelper.formatHex2(damageClass) + ")/";
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
                return "Usable by " + StackObject.enumToString("playerChar", characterUser);
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
        if (statBuffUnused) {
            statBuffTypes += "UnusedBuffsByte-" + StringHelper.formatHex2(statBuffFlags) + "/";
        }
        if (statBuffTypes.isEmpty()) {
            statBuffTypes = "NullBuff";
        } else {
            statBuffTypes = statBuffTypes.substring(0, statBuffTypes.length() - 1);
        }
        return statBuffTypes + " x" + statBuffValue;
    }

    private String elements() {
        // return elementFlags > 0 ? StackObject.bitfieldToString("elementsBitfield", elementFlags) : null;
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
                target = (targetFlag5MaybeAbleToChooseWithinTeam ? "Choose" : "Fixed") + " " + (randomTargets ? "random " : "") + target + "y";
            }
            if (!targetEitherTeam) {
                target += "!!";
            }
        }
        if (targetFlagLongRange) {
            target += "/Ranged";
        }
        if (targetDead) {
            target += "/Dead";
        }
        return target;
    }

    private String hitChance() {
        return switch (hitCalcType) {
            case 0 -> "Always";
            case 1, 2 -> attackAccuracy + "%";
            case 3, 4 -> "ACC";
            case 5 -> "ACC*2.5";
            case 6 -> "ACC*1.5";
            case 7 -> "ACC*0.5";
            default -> "Unknown";
        } + " [0" + hitCalcType + "h]";
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
        if (specialBuffUnused) { buffs.append(" Unused=").append(StringHelper.formatHex2(specialBuffInflict)).append(";"); }
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
        return StringHelper.formatHex2(bt) + '=' + String.format("%03d", bt) + '(' + String.format("%8s", Integer.toBinaryString(bt)).replace(' ', '0') + ')';
    }

    private static void prepareMaps() {
        if (submenus == null) {
            submenus = new HashMap<>();
            submenus.put(0x00, "Normal Menu");
            submenus.put(0x01, "Black Magic");
            submenus.put(0x02, "White Magic");
            submenus.put(0x03, "Skill");
            submenus.put(0x04, "Overdrive");
            submenus.put(0x05, "Summon");
            submenus.put(0x06, "Items");
            submenus.put(0x07, "Weapon Change");
            submenus.put(0x08, "Escape");
            submenus.put(0x0A, "Switch Character");
            submenus.put(0x0C, "Left Menu");
            submenus.put(0x0D, "Right Menu");
            submenus.put(0x0E, "Special");
            submenus.put(0x0F, "Armor Change");
            submenus.put(0x11, "Use");
            submenus.put(0x14, "Mix");
            submenus.put(0x15, "Gil (Bribe/SC)");
            submenus.put(0x16, "Gil (Pay Yoji)");
        }
    }
}
