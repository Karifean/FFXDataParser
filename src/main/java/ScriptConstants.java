import java.util.*;

public abstract class ScriptConstants {
    public static String[] FUNCGROUPS;
    public static int[] OPCODE_ARGC;
    public static int[] OPCODE_STACKPOPS;
    public static List<Integer> OPCODE_ENDLINE;
    public static Map<Integer, String> COMP_OPERATORS;
    public static Map<Integer, String> DAMAGE_FORMULAE;
    public static Map<Integer, String> DEATH_ANIMATIONS;
    public static Map<Integer, String> CONTROLLER_BUTTONS;
    public static Map<Integer, String> MUSIC_FILE_NAMES;
    public static Map<Integer, String> FIELD_ACTOR_NAMES;
    public static Map<Integer, String> BATTLE_ACTOR_NAMES;
    public static Map<Integer, String> BATTLE_END_TYPES;
    public static Map<Integer, ScriptField> ACTOR_PROPERTIES;
    public static Map<Integer, ScriptField> MOVE_PROPERTIES;

    public static void initialize() {
        if (FUNCGROUPS == null) {
            FUNCGROUPS = new String[0x10];
            FUNCGROUPS[0x0] = "Common";
            FUNCGROUPS[0x1] = "Math";
            FUNCGROUPS[0x2] = "Unknown2";
            FUNCGROUPS[0x3] = "Unknown3";
            FUNCGROUPS[0x4] = "SgEvent";
            FUNCGROUPS[0x5] = "ChEvent";
            FUNCGROUPS[0x6] = "Camera";
            FUNCGROUPS[0x7] = "Battle";
            FUNCGROUPS[0x8] = "Map";
            FUNCGROUPS[0x9] = "Mount";
            FUNCGROUPS[0xA] = "UnknownA";
            FUNCGROUPS[0xB] = "Movie";
            FUNCGROUPS[0xC] = "Debug";
            FUNCGROUPS[0xD] = "AbilityMap";
            FUNCGROUPS[0xE] = "UnknownE";
            FUNCGROUPS[0xF] = "UnknownF";
        }
        if (OPCODE_ARGC == null) {
            OPCODE_ARGC = new int[0x100];
        }
        if (OPCODE_ENDLINE == null) {
            OPCODE_ENDLINE = new ArrayList<>();
            OPCODE_ENDLINE.add(0x25);
            OPCODE_ENDLINE.add(0x2A);
            OPCODE_ENDLINE.add(0x2C);
            OPCODE_ENDLINE.add(0x34);
            OPCODE_ENDLINE.add(0x3C);
            OPCODE_ENDLINE.add(0x3D);
            OPCODE_ENDLINE.add(0x54);
            OPCODE_ENDLINE.add(0x59);
            OPCODE_ENDLINE.add(0x5A);
            OPCODE_ENDLINE.add(0x5B);
            OPCODE_ENDLINE.add(0x5C);
            OPCODE_ENDLINE.add(0x5D);
            OPCODE_ENDLINE.add(0x5E);
            OPCODE_ENDLINE.add(0x5F);
            OPCODE_ENDLINE.add(0x60);
            OPCODE_ENDLINE.add(0x77);
            OPCODE_ENDLINE.add(0x79);
            OPCODE_ENDLINE.add(0xA0);
            OPCODE_ENDLINE.add(0xA1);
            OPCODE_ENDLINE.add(0xA3);
            OPCODE_ENDLINE.add(0xA4);
            OPCODE_ENDLINE.add(0xB0);
            OPCODE_ENDLINE.add(0xB1);
            OPCODE_ENDLINE.add(0xB2);
            OPCODE_ENDLINE.add(0xB3);
            OPCODE_ENDLINE.add(0xD6);
            OPCODE_ENDLINE.add(0xD7);
            OPCODE_ENDLINE.add(0xD8);
        }
        if (OPCODE_STACKPOPS == null) {
            OPCODE_STACKPOPS = new int[0x100];
            Arrays.fill(OPCODE_STACKPOPS, -1);
            OPCODE_STACKPOPS[0x00] = 0;
            for (int i = 0x01; i <= 0x18; i++) {
                OPCODE_STACKPOPS[i] = 2;
            }
            OPCODE_STACKPOPS[0x19] = 1;
            OPCODE_STACKPOPS[0x1C] = 1;
            OPCODE_STACKPOPS[0x25] = 1;
            OPCODE_STACKPOPS[0x26] = 0;
            OPCODE_STACKPOPS[0x27] = 0;
            OPCODE_STACKPOPS[0x28] = 0;
            OPCODE_STACKPOPS[0x29] = 0;
            OPCODE_STACKPOPS[0x2A] = 1; // Seems to be "pop and ignore"?
            OPCODE_STACKPOPS[0x2B] = 1;
            OPCODE_STACKPOPS[0x2C] = 1;
            OPCODE_STACKPOPS[0x34] = 0;
            OPCODE_STACKPOPS[0x36] = 3;
            OPCODE_STACKPOPS[0x37] = 3;
            OPCODE_STACKPOPS[0x38] = 3;
            OPCODE_STACKPOPS[0x3C] = 0;
            OPCODE_STACKPOPS[0x3D] = 1;
            OPCODE_STACKPOPS[0x46] = 1;
            OPCODE_STACKPOPS[0x54] = 0;
            OPCODE_STACKPOPS[0x59] = 1;
            OPCODE_STACKPOPS[0x5A] = 1;
            OPCODE_STACKPOPS[0x5B] = 1;
            OPCODE_STACKPOPS[0x5C] = 1;
            OPCODE_STACKPOPS[0x5D] = 1;
            OPCODE_STACKPOPS[0x5E] = 1;
            OPCODE_STACKPOPS[0x5F] = 1;
            OPCODE_STACKPOPS[0x60] = 1;
            OPCODE_STACKPOPS[0x67] = 0;
            OPCODE_STACKPOPS[0x68] = 0;
            OPCODE_STACKPOPS[0x69] = 0;
            OPCODE_STACKPOPS[0x6A] = 0;
            OPCODE_STACKPOPS[0x6B] = 0;
            OPCODE_STACKPOPS[0x6C] = 0;
            OPCODE_STACKPOPS[0x6D] = 0;
            OPCODE_STACKPOPS[0x6E] = 0;
            OPCODE_STACKPOPS[0x77] = 2;
            OPCODE_STACKPOPS[0x79] = 3;
            OPCODE_STACKPOPS[0x9F] = 0;
            OPCODE_STACKPOPS[0xA0] = 1;
            OPCODE_STACKPOPS[0xA1] = 1;
            OPCODE_STACKPOPS[0xA2] = 1;
            OPCODE_STACKPOPS[0xA3] = 2;
            OPCODE_STACKPOPS[0xA7] = 1;
            OPCODE_STACKPOPS[0xAD] = 0;
            OPCODE_STACKPOPS[0xAE] = 0;
            OPCODE_STACKPOPS[0xAF] = 0;
            OPCODE_STACKPOPS[0xB0] = 0;
            OPCODE_STACKPOPS[0xB3] = 0;
            OPCODE_STACKPOPS[0xB5] = 0;
            OPCODE_STACKPOPS[0xD6] = 1;
            OPCODE_STACKPOPS[0xD7] = 1;
            OPCODE_STACKPOPS[0xD8] = 0;
            OPCODE_STACKPOPS[0xF6] = 0;
        }
        if (COMP_OPERATORS == null) {
            COMP_OPERATORS = new HashMap<>();
            COMP_OPERATORS.put(0x01, "or");
            COMP_OPERATORS.put(0x02, "and");
            COMP_OPERATORS.put(0x03, "bitOr");
            COMP_OPERATORS.put(0x04, "bitXor");
            COMP_OPERATORS.put(0x05, "bitAnd");
            COMP_OPERATORS.put(0x06, "==");
            COMP_OPERATORS.put(0x07, "!=");
            COMP_OPERATORS.put(0x08, "> (unsigned)");
            COMP_OPERATORS.put(0x09, "< (unsigned");
            COMP_OPERATORS.put(0x0A, ">");
            COMP_OPERATORS.put(0x0B, "<");
            COMP_OPERATORS.put(0x0C, ">= (unsigned)");
            COMP_OPERATORS.put(0x0D, "<= (unsigned)");
            COMP_OPERATORS.put(0x0E, ">=");
            COMP_OPERATORS.put(0x0F, "<=");
            COMP_OPERATORS.put(0x10, "OP-B-ON");
            COMP_OPERATORS.put(0x11, "OP-B-OFF");
            COMP_OPERATORS.put(0x12, "<<");
            COMP_OPERATORS.put(0x13, ">>");
            COMP_OPERATORS.put(0x14, "+");
            COMP_OPERATORS.put(0x15, "-");
            COMP_OPERATORS.put(0x16, "*");
            COMP_OPERATORS.put(0x17, "/");
            COMP_OPERATORS.put(0x18, "mod");
        }
        if (DEATH_ANIMATIONS == null) {
            DEATH_ANIMATIONS = new HashMap<>();
            DEATH_ANIMATIONS.put(0x00, "Character (Body remains and targetable)");
            DEATH_ANIMATIONS.put(0x01, "Boss (Body remains but untargetable)");
            DEATH_ANIMATIONS.put(0x02, "Humanoid (No Pyreflies, body fades out)");
            DEATH_ANIMATIONS.put(0x03, "Fiend (Pyrefly dissipation)");
            DEATH_ANIMATIONS.put(0x04, "Disintegrate-Machina (Red explosions)");
            DEATH_ANIMATIONS.put(0x05, "Steal-Machina (Same as 02 with machina SFX)");
            DEATH_ANIMATIONS.put(0x08, "YAT/YKT");
        }
        if (CONTROLLER_BUTTONS == null) {
            CONTROLLER_BUTTONS = new HashMap<>();
            CONTROLLER_BUTTONS.put(0x05, "X (Confirm)");
            CONTROLLER_BUTTONS.put(0x12, "Down?");
            CONTROLLER_BUTTONS.put(0x13, "Left?");
            CONTROLLER_BUTTONS.put(0x14, "Up?");
            CONTROLLER_BUTTONS.put(0x15, "Right?");
        }
        if (MUSIC_FILE_NAMES == null) {
            MUSIC_FILE_NAMES = new HashMap<>();
            MUSIC_FILE_NAMES.put(0x000A, "Unwavering Determination");
            MUSIC_FILE_NAMES.put(0x000B, "Secret Maneuvers");
            MUSIC_FILE_NAMES.put(0x000C, "Boss Theme");
            MUSIC_FILE_NAMES.put(0x000D, "The Summoning");
            MUSIC_FILE_NAMES.put(0x000E, "Macalania Woods");
            MUSIC_FILE_NAMES.put(0x001B, "Blitz Off!");
            MUSIC_FILE_NAMES.put(0x0082, "To Zanarkand");
            MUSIC_FILE_NAMES.put(0x0091, "Challenge?");
        }
        if (BATTLE_END_TYPES == null) {
            BATTLE_END_TYPES = new HashMap<>();
            BATTLE_END_TYPES.put(0x00, "Unknown (00)");
            BATTLE_END_TYPES.put(0x01, "?Game Over (01)");
            BATTLE_END_TYPES.put(0x02, "Unknown (02)");
            BATTLE_END_TYPES.put(0x03, "?Escape (03)");
            BATTLE_END_TYPES.put(0x04, "Unknown (04)");
        }
        if (DAMAGE_FORMULAE == null) {
            DAMAGE_FORMULAE = new HashMap<>();
            DAMAGE_FORMULAE.put(0x00, "None");
            DAMAGE_FORMULAE.put(0x01, "STR vs DEF");
            DAMAGE_FORMULAE.put(0x02, "STR (ignore DEF)");
            DAMAGE_FORMULAE.put(0x03, "MAG vs MDF");
            DAMAGE_FORMULAE.put(0x04, "MAG (ignore MDF)");
            DAMAGE_FORMULAE.put(0x05, "Current/16");
            DAMAGE_FORMULAE.put(0x06, "Fixed x50");
            DAMAGE_FORMULAE.put(0x07, "Healing");
            DAMAGE_FORMULAE.put(0x08, "Max/16");
            DAMAGE_FORMULAE.put(0x09, "Fixed x~50");
            DAMAGE_FORMULAE.put(0x0D, "Ticks/16");
            DAMAGE_FORMULAE.put(0x0F, "Special MAG (ignore MDF)");
            DAMAGE_FORMULAE.put(0x10, "Fixed x User MaxHP / 10");
            DAMAGE_FORMULAE.put(0x15, "Fixed x Gil chosen / 10");
            DAMAGE_FORMULAE.put(0x16, "Fixed xKills");
            DAMAGE_FORMULAE.put(0x17, "Fixed x9999");
        }
        if (FIELD_ACTOR_NAMES == null) {
            FIELD_ACTOR_NAMES = new HashMap<>();
            FIELD_ACTOR_NAMES.put(0x0000, "Tidus");
            FIELD_ACTOR_NAMES.put(0x0001, "Yuna");
            FIELD_ACTOR_NAMES.put(0x0002, "Auron");
            FIELD_ACTOR_NAMES.put(0x0003, "Kimahri");
            FIELD_ACTOR_NAMES.put(0x0004, "Wakka");
            FIELD_ACTOR_NAMES.put(0x0005, "Lulu");
            FIELD_ACTOR_NAMES.put(0x0006, "Rikku");
            FIELD_ACTOR_NAMES.put(0x0007, "Seymour");
            for (int i = 1; i <= 10; i++) {
                FIELD_ACTOR_NAMES.put(0x0007 + i, "FieldActor#" + i);
            }
        }
        if (BATTLE_ACTOR_NAMES == null) {
            BATTLE_ACTOR_NAMES = new HashMap<>();
            BATTLE_ACTOR_NAMES.put(0x0000, "Tidus");
            BATTLE_ACTOR_NAMES.put(0x0001, "Yuna");
            BATTLE_ACTOR_NAMES.put(0x0002, "Auron");
            BATTLE_ACTOR_NAMES.put(0x0003, "Kimahri");
            BATTLE_ACTOR_NAMES.put(0x0004, "Wakka");
            BATTLE_ACTOR_NAMES.put(0x0005, "Lulu");
            BATTLE_ACTOR_NAMES.put(0x0006, "Rikku");
            BATTLE_ACTOR_NAMES.put(0x0007, "Seymour");
            BATTLE_ACTOR_NAMES.put(0x0008, "Valefor");
            BATTLE_ACTOR_NAMES.put(0x0009, "Ifrit");
            BATTLE_ACTOR_NAMES.put(0x000A, "Ixion");
            BATTLE_ACTOR_NAMES.put(0x000B, "Shiva");
            BATTLE_ACTOR_NAMES.put(0x000C, "Bahamut");
            BATTLE_ACTOR_NAMES.put(0x000D, "Anima");
            BATTLE_ACTOR_NAMES.put(0x000E, "Yojimbo");
            BATTLE_ACTOR_NAMES.put(0x000F, "Cindy");
            BATTLE_ACTOR_NAMES.put(0x0010, "Sandy");
            BATTLE_ACTOR_NAMES.put(0x0011, "Mindy");
            for (int i = 1; i <= 10; i++) {
                BATTLE_ACTOR_NAMES.put(0x0013 + i, "Monster#" + i);
            }
            BATTLE_ACTOR_NAMES.put(0x00FF, "Actor:None");
            BATTLE_ACTOR_NAMES.put(0xFFE9, "AllCharsAndAeons");
            BATTLE_ACTOR_NAMES.put(0xFFEB, "AllChars");
            BATTLE_ACTOR_NAMES.put(0xFFEC, "AllAeons");
            BATTLE_ACTOR_NAMES.put(0xFFEF, "LastAttacker");
            BATTLE_ACTOR_NAMES.put(0xFFF0, "PredefinedGroup");
            BATTLE_ACTOR_NAMES.put(0xFFF1, "AllMonsters");
            BATTLE_ACTOR_NAMES.put(0xFFF2, "FrontlineChars");
            BATTLE_ACTOR_NAMES.put(0xFFF3, "Self");
            BATTLE_ACTOR_NAMES.put(0xFFF4, "CharacterReserve#4");
            BATTLE_ACTOR_NAMES.put(0xFFF5, "CharacterReserve#3");
            BATTLE_ACTOR_NAMES.put(0xFFF6, "CharacterReserve#2");
            BATTLE_ACTOR_NAMES.put(0xFFF7, "CharacterReserve#1");
            BATTLE_ACTOR_NAMES.put(0xFFF8, "Character#3");
            BATTLE_ACTOR_NAMES.put(0xFFF9, "Character#2");
            BATTLE_ACTOR_NAMES.put(0xFFFA, "Character#1");
            BATTLE_ACTOR_NAMES.put(0xFFFB, "AllActors");
            BATTLE_ACTOR_NAMES.put(0xFFFF, "Actor:Null");
        }
        if (ACTOR_PROPERTIES == null) {
            ACTOR_PROPERTIES = new HashMap<>();
            putActorProperty(0x0000, "HP", "ae");
            putActorProperty(0x0001, "MP", "ae");
            putActorProperty(0x0002, "maxHP", "ae");
            putActorProperty(0x0003, "maxMP", "ae");
            putActorProperty(0x0004, "isAlive/StatusDeath", "bool");
            putActorProperty(0x0005, "StatusPoison", "bool");
            putActorProperty(0x0006, "StatusPetrify", "bool");
            putActorProperty(0x0007, "StatusZombie", "bool");
            // 0008 unknown
            putActorProperty(0x0009, "STR", "ae");
            putActorProperty(0x000A, "DEF", "ae");
            putActorProperty(0x000B, "MAG", "ae");
            putActorProperty(0x000C, "MDF", "ae");
            putActorProperty(0x000D, "AGI", "ae");
            putActorProperty(0x000E, "LCK", "ae");
            putActorProperty(0x000F, "EVA", "ae");
            putActorProperty(0x0010, "ACC", "ae");
            putActorProperty(0x0011, "PoisonDamage%", "ae");
            putActorProperty(0x0012, "?ODMode", "ae");
            putActorProperty(0x0013, "OverdriveCurrent", "ae");
            putActorProperty(0x0014, "OverdriveMax", "ae");
            putActorProperty(0x0015, "?isOnFrontline", "bool");
            putActorProperty(0x001C, "?BattleRow", "ae");
            putActorProperty(0x001D, "?BattleArenaStandingPosition", "ae");
            putActorProperty(0x001E, "BattleDistance", "ae");
            putActorProperty(0x0020, "Armored", "bool");
            putActorProperty(0x0021, "?ImmuneToFractionalDmg", "bool");
            putActorProperty(0x0025, "StatusPowerBreak", "bool");
            putActorProperty(0x0026, "StatusMagicBreak", "bool");
            putActorProperty(0x0027, "StatusArmorBreak", "bool");
            putActorProperty(0x0028, "StatusMentalBreak", "bool");
            putActorProperty(0x0029, "StatusConfusion", "bool");
            putActorProperty(0x002A, "StatusBerserk", "bool");
            putActorProperty(0x002B, "StatusProvoke", "bool");
            putActorProperty(0x002C, "StatusThreaten", "bool");
            putActorProperty(0x002D, "StatusDurationSleep", "ae");
            putActorProperty(0x002E, "StatusDurationSilence", "ae");
            putActorProperty(0x002F, "StatusDurationDarkness", "ae");
            putActorProperty(0x0030, "StatusDurationShell", "ae");
            putActorProperty(0x0031, "StatusDurationProtect", "ae");
            putActorProperty(0x0032, "StatusDurationReflect", "ae");
            putActorProperty(0x0033, "StatusBlocksNulTide", "ae");
            putActorProperty(0x0034, "StatusBlocksNulBlaze", "ae");
            putActorProperty(0x0035, "StatusBlocksNulShock", "ae");
            putActorProperty(0x0036, "StatusBlocksNulFrost", "ae");
            putActorProperty(0x0037, "StatusDurationRegen", "ae");
            putActorProperty(0x0038, "StatusDurationHaste", "ae");
            putActorProperty(0x0039, "StatusDurationSlow", "ae");
            putActorProperty(0x003D, "CounterAttack", "bool");
            putActorProperty(0x003E, "?EvadeAndCounter", "bool");
            putActorProperty(0x0042, "?MagicBooster", "bool");
            putActorProperty(0x0047, "?Auto-Phoenix", "bool");
            putActorProperty(0x004F, "DeathAnimation", "deathAnim");
            putActorProperty(0x0051, "?GetsTurns", "bool");
            putActorProperty(0x0052, "?Targetable", "bool");
            putActorProperty(0x0053, "VisibleOnCTB", "bool");
            putActorProperty(0x0055, "?Location1", "ae");
            putActorProperty(0x0056, "?Location2(Tonberry)", "ae");
            putActorProperty(0x0059, "?Host", "actor");
            putActorProperty(0x005B, "?AnimationsVariant", "ae");
            putActorProperty(0x0062, "AbsorbFire", "bool");
            putActorProperty(0x0063, "AbsorbIce", "bool");
            putActorProperty(0x0064, "AbsorbThunder", "bool");
            putActorProperty(0x0065, "AbsorbWater", "bool");
            putActorProperty(0x0066, "AbsorbHoly", "bool");
            putActorProperty(0x0067, "NullFire", "bool");
            putActorProperty(0x0068, "NullIce", "bool");
            putActorProperty(0x0069, "NullThunder", "bool");
            putActorProperty(0x006A, "NullWater", "bool");
            putActorProperty(0x006B, "NullHoly", "bool");
            putActorProperty(0x006C, "ResistFire", "bool");
            putActorProperty(0x006D, "ResistIce", "bool");
            putActorProperty(0x006E, "ResistThunder", "bool");
            putActorProperty(0x006F, "ResistWater", "bool");
            putActorProperty(0x0070, "ResistHoly", "bool");
            putActorProperty(0x0071, "WeakFire", "bool");
            putActorProperty(0x0072, "WeakIce", "bool");
            putActorProperty(0x0073, "WeakThunder", "bool");
            putActorProperty(0x0074, "WeakWater", "bool");
            putActorProperty(0x0075, "WeakHoly", "bool");
            putActorProperty(0x0079, "wasStolenFrom", "bool");
            putActorProperty(0x0081, "StealItemCommonType", "move");
            putActorProperty(0x0082, "StealItemCommonAmount", "ae");
            putActorProperty(0x0083, "StealItemRareType", "move");
            putActorProperty(0x0084, "StealItemRareAmount", "ae");
            putActorProperty(0x0089, "showOverdriveBar", "bool");
            putActorProperty(0x008A, "Item1DropChance", "ae");
            putActorProperty(0x008B, "Item2DropChance", "ae");
            putActorProperty(0x008C, "GearDropChance", "ae");
            putActorProperty(0x008D, "StealChance", "ae");
            putActorProperty(0x008E, "?MustBeKilledForBattleEnd", "bool");
            putActorProperty(0x008F, "?StatusScan", "bool");
            putActorProperty(0x0090, "StatusDistillPower", "bool");
            putActorProperty(0x0091, "?StatusDistillMana", "bool");
            putActorProperty(0x0092, "?StatusDistillSpeed", "bool");
            putActorProperty(0x0093, "StatusUnusedDash", "bool");
            putActorProperty(0x0094, "?StatusDistillAbility", "bool");
            putActorProperty(0x0095, "StatusShield", "bool");
            putActorProperty(0x0096, "StatusBoost", "bool");
            putActorProperty(0x0097, "StatusEject", "bool");
            putActorProperty(0x0098, "StatusAutoLife", "bool");
            putActorProperty(0x0099, "StatusCurse", "bool");
            putActorProperty(0x009A, "StatusDefend", "bool");
            putActorProperty(0x009B, "StatusGuard", "bool");
            putActorProperty(0x009C, "StatusSentinel", "bool");
            putActorProperty(0x009D, "StatusDoom", "bool");
            putActorProperty(0x009F, "DoomCounter", "ae");
            putActorProperty(0x00A6, "DamageTaken", "ae");
            putActorProperty(0x00AE, "?Visible", "bool");
            putActorProperty(0x00AF, "?StatusResistanceDeath", "ae");
            putActorProperty(0x00B0, "?StatusResistanceZombie", "ae");
            putActorProperty(0x00B1, "?StatusResistancePetrify", "ae");
            putActorProperty(0x00B2, "?StatusResistancePoison", "ae");
            putActorProperty(0x00B3, "?StatusResistancePowerBreak", "ae");
            putActorProperty(0x00B4, "?StatusResistanceMagicBreak", "ae");
            putActorProperty(0x00B5, "?StatusResistanceArmorBreak", "ae");
            putActorProperty(0x00B6, "?StatusResistanceMentalBreak", "ae");
            putActorProperty(0x00B7, "?StatusResistanceConfusion", "ae");
            putActorProperty(0x00B8, "?StatusResistanceBerserk", "ae");
            putActorProperty(0x00B9, "?StatusResistanceProvoke", "ae");
            putActorProperty(0x00BA, "StatusChanceThreaten", "ae");
            putActorProperty(0x00BB, "StatusResistanceSleep", "ae");
            putActorProperty(0x00BC, "StatusResistanceSilence", "ae");
            putActorProperty(0x00BD, "StatusResistanceDarkness", "ae");
            putActorProperty(0x00BE, "StatusResistanceShell", "ae");
            putActorProperty(0x00BF, "StatusResistanceProtect", "ae");
            putActorProperty(0x00C0, "StatusResistanceReflect", "ae");
            putActorProperty(0x00C1, "StatusResistanceNulTide", "ae");
            putActorProperty(0x00C2, "StatusResistanceNulBlaze", "ae");
            putActorProperty(0x00C3, "StatusResistanceNulShock", "ae");
            putActorProperty(0x00C4, "StatusResistanceNulFrost", "ae");
            putActorProperty(0x00C5, "StatusResistanceRegen", "ae");
            putActorProperty(0x00C6, "StatusResistanceHaste", "ae");
            putActorProperty(0x00C7, "StatusResistanceSlow", "ae");
            putActorProperty(0x00D1, "?StatusImmunityEject", "bool");
            putActorProperty(0x00D7, "?VisibleOnFrontlinePartyList", "bool");
            putActorProperty(0x00E8, "CurrentTurnDelay", "ae");
            putActorProperty(0x0101, "ProvokerActor", "actor");
            putActorProperty(0x0103, "?CTBIcon", "ae");
            putActorProperty(0x0107, "NullDamage", "bool");
            putActorProperty(0x0108, "NullMagic", "bool");
            putActorProperty(0x0109, "NullPhysical", "bool");
            putActorProperty(0x010A, "LearnableRonsoRage", "move");
            putActorProperty(0x010C, "OverkillThreshold", "ae");
            putActorProperty(0x0111, "YojimboCompatibility", "ae");
            putActorProperty(0x0112, "YojimboGivenGil", "ae");
            putActorProperty(0x0113, "ZanmatoLevel", "ae");
            putActorProperty(0x0114, "hasTakenATurn", "bool");
            putActorProperty(0x0117, "MagusSisterMotivation", "ae");
            putActorProperty(0x0119, "?NearDeath", "bool");
            putActorProperty(0x011A, "?OverdriveAvailable", "ae");
            putActorProperty(0x012B, "APRewardNormal", "ae");
            putActorProperty(0x012C, "APRewardOverkill", "ae");
            putActorProperty(0x012D, "GilReward", "ae");
            putActorProperty(0x012E, "BonusSTR", "ae");
            putActorProperty(0x012F, "?BonusDEF", "ae");
            putActorProperty(0x0130, "?BonusMAG", "ae");
            putActorProperty(0x0131, "BonusMDF", "ae");
            putActorProperty(0x0139, "isDoublecasting", "bool");
            putActorProperty(0x014D, "?recruited (Aeon)", "bool");
            putActorProperty(0x014E, "permanentAutoLife", "bool");
        }
        if (MOVE_PROPERTIES == null) {
            MOVE_PROPERTIES = new HashMap<>();
            putMoveProperty(0x0000, "damageFormula", "damageFormula");
            putMoveProperty(0x0001, "damageType", "damageType");
            putMoveProperty(0x0002, "affectHP", "bool");
            putMoveProperty(0x0003, "affectMP", "bool");
            putMoveProperty(0x0004, "affectCTB", "bool");
            putMoveProperty(0x0005, "elementHoly", "bool");
            putMoveProperty(0x0006, "elementWater", "bool");
            putMoveProperty(0x0007, "elementThunder", "bool");
            putMoveProperty(0x0008, "elementIce", "bool");
            putMoveProperty(0x0009, "elementFire", "bool");
            // 0x000A unknown, something with the move's targeting?
        }
    }

    private static void putActorProperty(int idx, String name, String type) {
        ScriptField field = new ScriptField(name, type);
        field.idx = idx;
        ACTOR_PROPERTIES.put(idx, field);
    }

    private static void putMoveProperty(int idx, String name, String type) {
        ScriptField field = new ScriptField(name, type);
        field.idx = idx;
        MOVE_PROPERTIES.put(idx, field);
    }
}
