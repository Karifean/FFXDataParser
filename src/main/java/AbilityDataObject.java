import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AbilityDataObject {
    static Map<Integer, String> characters;
    static Map<Integer, String> submenus;

    boolean isCharacterAbility;
    int length;
    byte[] raw;
    int[] b;

    String name;
    String dash;
    String description;
    String otherText;
    int nameOffsetLowByte;
    int nameOffsetHighByte;
    int nameOffsetComputed;
    int unknownByte0B;
    int otherTextOffsetLowByte;
    int otherTextOffsetHighByte;
    int otherTextOffsetComputed;
    int unknownByte0E;
    int unknownByte0F;
    int anim1HighByte;
    int anim1LowByte;
    int anim2HighByte;
    int anim2LowByte;
    int icon;
    int casterAnimation;
    int unknownByte16;
    int subsubMenuCategorization;
    // /|\ Always equal to subMenuCategorization except on nested menus in menus like Use/Quick Pockets
    //  |  and also on Requiem for some reason.
    int subMenuCategorization;
    int characterUser;
    int targetingFlags;
    int unknownProperties1B;
    // Bit 0x01 seems to be set on a lot of things but no clear distinction visible.
    // Bit 0x02 seems to be set on Skills as well as many enemy attacks
    // Bit 0x04 seems to be set on just about everything usable
    // Bit 0x40 seems to be set on Skills that have a "cast" animation as well as many enemy attacks
    int miscProperties1C;
    int miscProperties1D;
    int miscProperties1E;
    int unknownProperties1F;
    int damageProperties20;
    int stealGilByte;
    int miscProperties22;
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
    int statusChanceConfuse;
    int statusChanceBerserk;
    int statusChanceProvoke;
    int statusChanceThreaten;
    int statusChancePowerBreak;
    int statusChanceMagicBreak;
    int statusChanceArmorBreak;
    int statusChanceMentalBreak;
    int statusChanceSleep;
    int statusChanceSilence;
    int statusChanceDarkness;
    int statusChanceShell;
    int statusChanceProtect;
    int statusChanceReflect;
    int statusChanceRegen;
    int statusChanceSlow;
    int statusChanceHaste;
    int statusChanceNBlaze;
    int statusChanceNFrost;
    int statusChanceNShock;
    int statusChanceNTide;
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
    int alwaysNull1;
    int overdriveCategorizationByte;
    int statBuffValue;
    int specialBuffFlags;
    int alwaysNull2;
    int unknownByte2;
    int unknownByte3;
    int dashOffsetLowByte;
    int dashOffsetHighByte;
    int dashOffsetComputed;
    int descriptionOffsetLowByte;
    int descriptionOffsetHighByte;
    int descriptionOffsetComputed;
    int unknownByte6;
    int unknownByte7;
    int unknownByte0A;
    int unknownByte5C;
    int unknownByte5D;
    int unknownByte5E;
    int unknownByte5F;

    String overdriveCharacter;
    int overdriveCategory;

    boolean usableOutsideCombat;
    boolean usableInCombat;
    boolean byte28bit4; // Maybe "Get Piercing trait from Weapon or sth?)
    boolean byte28bit6; // ONLY set on all 6 of Yuna's controllable aeon normal attacks
    boolean affectedByDarkness;
    boolean displayMoveName;
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
    boolean targetFlag8;
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
    boolean byte32bit4;
    boolean byte32bit7;
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

    public AbilityDataObject() {}

    public AbilityDataObject(InputStream inputStream, int individualLength) throws IOException {
        isCharacterAbility = (individualLength == 96);
        length = individualLength;
        raw = new byte[length];
        b = new int[length];
        if (inputStream.read(raw) < length) {
            throw new EOFException("Did not read all bytes");
        }
        for (int i = 0; i < length; i++) {
            b[i] = Byte.toUnsignedInt(raw[i]);
        }
        prepareMaps();
        mapBytes();
        mapFlags();
    }

    private void mapBytes() {
        nameOffsetLowByte = b[0x00];
        nameOffsetHighByte = b[0x01];
        unknownByte2 = b[0x02];
        unknownByte3 = b[0x03];
        dashOffsetLowByte = b[0x04];
        dashOffsetHighByte = b[0x05];
        unknownByte6 = b[0x06];
        unknownByte7 = b[0x07];
        descriptionOffsetLowByte = b[0x08];
        descriptionOffsetHighByte = b[0x09];
        unknownByte0A = b[0x0A];
        unknownByte0B = b[0x0B];
        otherTextOffsetLowByte = b[0x0C];
        otherTextOffsetHighByte = b[0x0D];
        unknownByte0E = b[0x0E];
        unknownByte0F = b[0x0F];
        anim1HighByte = b[0x10];
        anim1LowByte = b[0x11];
        anim2HighByte = b[0x12];
        anim2LowByte = b[0x13];
        icon = b[0x14];
        casterAnimation = b[0x15];
        unknownByte16 = b[0x16];
        subsubMenuCategorization = b[0x17];
        subMenuCategorization = b[0x18];
        characterUser = b[0x19];
        targetingFlags = b[0x1A];
        unknownProperties1B = b[0x1B];
        miscProperties1C = b[0x1C];
        miscProperties1D = b[0x1D];
        miscProperties1E = b[0x1E];
        unknownProperties1F = b[0x1F];
        damageProperties20 = b[0x20];
        stealGilByte = b[0x21];
        // Bit 0x01 could be "affected by Alchemy" flag?
        miscProperties22 = b[0x22];
        damageClass = b[0x23];
        moveRank = b[0x24];
        costMP = b[0x25];
        costOD = b[0x26];
        attackCritBonus = b[0x27];
        damageFormula = b[0x28];
        attackAccuracy = b[0x29];
        attackPower = b[0x2A];
        hitCount = b[0x2B];
        shatterChance = b[0x2C];
        elementFlags = b[0x2D];
        statusChanceDeath = b[0x2E];
        statusChanceZombie = b[0x2F];
        statusChancePetrify = b[0x30];
        statusChancePoison = b[0x31];
        statusChancePowerBreak = b[0x32];
        statusChanceMagicBreak = b[0x33];
        statusChanceArmorBreak = b[0x34];
        statusChanceMentalBreak = b[0x35];
        statusChanceConfuse = b[0x36];
        statusChanceBerserk = b[0x37];
        statusChanceProvoke = b[0x38];
        statusChanceThreaten = b[0x39];
        statusChanceSleep = b[0x3A];
        statusChanceSilence = b[0x3B];
        statusChanceDarkness = b[0x3C];
        statusChanceShell = b[0x3D];
        statusChanceProtect = b[0x3E];
        statusChanceReflect = b[0x3F];
        statusChanceNTide = b[0x40];
        statusChanceNBlaze = b[0x41];
        statusChanceNShock = b[0x42];
        statusChanceNFrost = b[0x43];
        statusChanceRegen = b[0x44];
        statusChanceHaste = b[0x45];
        statusChanceSlow = b[0x46];
        statusDurationSleep = b[0x47];
        statusDurationSilence = b[0x48];
        statusDurationDarkness = b[0x49];
        statusDurationShell = b[0x4A];
        statusDurationProtect = b[0x4B];
        statusDurationReflect = b[0x4C];
        statusDurationNTide = b[0x4D];
        statusDurationNBlaze = b[0x4E];
        statusDurationNShock = b[0x4F];
        statusDurationNFrost = b[0x50];
        statusDurationRegen = b[0x51];
        statusDurationHaste = b[0x52];
        statusDurationSlow = b[0x53];
        extraStatusFlags1 = b[0x54];
        extraStatusFlags2 = b[0x55];
        statBuffFlags = b[0x56];
        alwaysNull1 = b[0x57];
        overdriveCategorizationByte = b[0x58];
        statBuffValue = b[0x59];
        specialBuffFlags = b[0x5A];
        alwaysNull2 = b[0x5B];
        if (length > 92) {
            unknownByte5C = b[0x5C];
            unknownByte5D = b[0x5D];
            unknownByte5E = b[0x5E];
            unknownByte5F = b[0x5F];
        }
        nameOffsetComputed = nameOffsetHighByte * 256 + nameOffsetLowByte;
        dashOffsetComputed = dashOffsetHighByte * 256 + dashOffsetLowByte;
        descriptionOffsetComputed = descriptionOffsetHighByte * 256 + descriptionOffsetLowByte;
        otherTextOffsetComputed = otherTextOffsetHighByte * 256 + otherTextOffsetLowByte;
    }

    private void mapFlags() {
        usableOutsideCombat = (miscProperties1C & 0x01) > 0;
        usableInCombat = (miscProperties1C & 0x02) > 0;
        displayMoveName = (miscProperties1C & 0x04) > 0;
        byte28bit4 = (miscProperties1C & 0x08) > 0;
        canMiss = (miscProperties1C & 0x10) > 0;
        byte28bit6 = (miscProperties1C & 0x20) > 0;
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
        stealGil = (stealGilByte & 0x01) > 0;
        damageTypePhysical = (damageProperties20 & 0x01) > 0;
        damageTypeMagical = (damageProperties20 & 0x02) > 0;
        canCrit = (damageProperties20 & 0x04) > 0;
        byte32bit4 = (damageProperties20 & 0x08) > 0; // Seems to be needed for proper evasion?
        isHealing = (damageProperties20 & 0x10) > 0;
        isCleansingStatuses = (damageProperties20 & 0x20) > 0;
        byte32bit7 = (damageProperties20 & 0x40) > 0;
        breaksDamageLimit = (damageProperties20 & 0x80) > 0;
        damageClassHP = (damageClass & 0x01) > 0;
        damageClassMP = (damageClass & 0x02) > 0;
        damageClassCTB = (damageClass & 0x04) > 0;
        damageClassUnknown = damageClass >= 0x08;
        targetEnabled = (targetingFlags & 0x01) > 0;
        targetEnemies = (targetingFlags & 0x02) > 0;
        targetMulti = (targetingFlags & 0x04) > 0;
        targetSelfOnly = (targetingFlags & 0x08) > 0;
        targetFlag5 = (targetingFlags & 0x10) > 0;
        targetEitherTeam = (targetingFlags & 0x20) > 0;
        targetDead = (targetingFlags & 0x40) > 0;
        targetFlag8 = (targetingFlags & 0x80) > 0;
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
            overdriveCharacter = characters.get(overdriveCategorizationByte % 0x10);
            overdriveCategory = overdriveCategorizationByte / 0x10;
        }
    }

    @Override
    public String toString() {
        return "{ " +
                (!usableInCombat ? "Unusable, " : "") +
                damageKind() +
                (breaksDamageLimit ? ", BDL" : "") +
                ", " + targeting() +
                ", Rank=" + moveRank +
                // (byte28bit4 ? ", 28!4" : "") +
                // (byte28bit6 ? ", 28!6" : "") +
                // ifG0(icon, "Icon#", "") +
                ifG0(costMP, "", " MP") +
                (usableOutsideCombat ? ", Usable outside combat" : "") +
                characterUser() +
                (useInRightMenu ? ", Topmenu=Right" : "") +
                (useInLeftMenu ? ", Topmenu=Left" : "") +
                (isTriggerCommand ? ", TriggerCmd" : "") +
                ifNN(submenus.get(subMenuCategorization), "Submenu=\"", "\"") +
                (subsubMenuCategorization != subMenuCategorization ? ", Subsubmenu=" + submenus.get(subsubMenuCategorization) : "" ) +
                (useInUseMenu ? ", In \"Use\" Menu" : "") +
                ifG0(costOD, "Overdrive (", "p)") +
                ifNN(overdriveCharacter, "OD-User=", "") +
                ifG0(overdriveCategory, "OD-Choice=", "") +
                (isPiercing ? ", Piercing" : "") +
                (canMiss ? ", Can miss" : "") +
                ifG0(attackAccuracy, "Acc=", "%") +
                (affectedByDarkness ? ", Darkable" : "") +
                // (canMiss || attackAccuracy > 0 ? ", " : "") +
                // (canMiss ? "Can miss" : "") + (attackAccuracy > 0 ? " (ACC+" + attackAccuracy + "%)" : "") +
                (canCrit ? ", Can crit" + (attackCritBonus > 0 ? " (+" + attackCritBonus + "%)" : "") : "") +
                (byte32bit4 ? ", byte32bit4" : "") +
                (byte32bit7 ? ", byte32bit7" : "") +
                (canBeReflected ? ", Reflectable" : "") +
                (disableWhenSilenced ? ", Silenceable" : "") +
                elements() +
                statuses() +
                statBuffs() +
                specialBuffs() +
                (destroyCaster ? ", Removes Caster" : "") +
                (stealItem ? ", Steal Item" : "") +
                (stealGil ? ", Steal Gil" : "") +
                (inflictDelayWeak ? ", Delay (Weak)" : "") +
                (inflictDelayStrong ? ", Delay (Strong)" : "") +
                ifG0(shatterChance, "Shatter=", "%") +
                ", anim=" + casterAnimation +
                (useTier1CastAnimation ? "L" : useTier3CastAnimation ? "H" : "") +
                "/" + String.format("%04x", anim1LowByte * 256 + anim1HighByte) +
                "/" + String.format("%04x", anim2LowByte * 256 + anim2HighByte) +
                " }";
    }

    private String damageKind() {
        String damageType = damageTypePhysical ? "Physical" : (damageTypeMagical ? "Magical" : "Special");
        String formula = usesWeaponProperties ? ", Formula=WPN" : ifG0(damageFormula, "Formula=", "");
        String hits = ifG0(hitCount, "", "-hit");
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
                damageClassString += "Unknown(" + String.format("%02x", damageClass) + ")";
            }
            damageClassString = damageClassString.substring(0, damageClassString.length() - 1);
            return damageType + ' ' + damageClassString + ' ' + (isHealing ? "Restore" : (absorbDamage ? "Absorb" : "Damage")) + hits + formula + ", Power=" + attackPower;
        } else {
            return damageType + hits + formula;
        }
    }

    private String characterUser() {
        if (isCharacterAbility && characterUser > 0) {
            return ", Usable by " + characters.get(characterUser);
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
        return ", " + statBuffTypes + " x" + statBuffValue;
    }

    private String elements() {
        StringBuilder elements = new StringBuilder(", Multi-Element {");
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
                return ", Element=" + withoutLastSemicolon.substring(18);
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
        if (targetFlag8) {
            target += "/8";
        }
        if (targetDead) {
            target += "/Dead";
        }
        return target;
    }

    private String statuses() {
        StringBuilder statuses = new StringBuilder(", ").append(isCleansingStatuses ? "Remove" : "Inflict").append(" {");
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
        StringBuilder buffs = new StringBuilder(", Special Buffs {");
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
                return ", Special Buff:" + withoutLastSemicolon.substring(17);
            }
        } else {
            return "";
        }
    }

    private void appendPermanentStatus(StringBuilder builder, String name, int chance) {
        if (chance > 0) {
            builder.append(' ').append(name);
            if (!isCleansingStatuses || chance < 254) {
                builder.append(": ").append(statusChanceString(chance));
            }
            builder.append(';');
        }
    }

    private void appendTemporaryStatus(StringBuilder builder, String name, int chance, int duration, boolean blocks) {
        if (chance > 0) {
            builder.append(' ').append(name);
            if (!isCleansingStatuses || chance < 254 || duration < 254) {
                builder.append(": ").append(statusChanceString(chance)).append(" (").append(statusDurationString(duration, blocks)).append(')');
            }
            builder.append(';');
        }
    }

    private static String ifG0(int value, String prefix, String postfix) {
        if (value > 0) {
            return ", " + prefix + value + postfix;
        } else {
            return "";
        }
    }

    private static String ifNN(String value, String prefix, String postfix) {
        if (value != null) {
            return ", " + prefix + value + postfix;
        } else {
            return "";
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
        return String.format("%02x", bt) + '=' + String.format("%03d", bt) + '(' + String.format("%8s", Integer.toBinaryString(bt)).replace(' ', '0') + ')';
    }

    private static void prepareMaps() {
        if (characters == null) {
            characters = new HashMap<>();
            characters.put(0, "Tidus");
            characters.put(1, "Yuna");
            characters.put(2, "Auron");
            characters.put(3, "Kimahri");
            characters.put(4, "Wakka");
            characters.put(5, "Lulu");
            characters.put(6, "Rikku");
            characters.put(7, "Seymour");
            characters.put(8, "Valefor");
            characters.put(9, "Ifrit");
            characters.put(10, "Ixion");
            characters.put(11, "Shiva");
            characters.put(12, "Bahamut");
            characters.put(13, "Anima");
            characters.put(14, "Yojimbo");
            characters.put(15, "Cindy");
            characters.put(16, "Sandy");
            characters.put(17, "Mindy");
            characters.put(255, "Everyone");
        }
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
}
