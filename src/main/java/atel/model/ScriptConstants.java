package atel.model;

import main.StringHelper;
import reading.FileAccessorWithMods;

import java.io.*;
import java.util.*;

public class ScriptConstants {
    public static ScriptConstants FFX = new ScriptConstants();
    private static final String ENUM_CSV_ROOT = FileAccessorWithMods.RESOURCES_ROOT + "enums";
    public String[] FUNCSPACES;
    public static String[] OPCODE_LABELS;
    public static int[] OPCODE_STACKPOPS;
    public static List<Integer> OPCODE_ENDLINE;
    public static final Set<Integer> OPCODE_CALLING = Set.of(0xB5, 0xD8);
    public static final Set<Integer> OPCODE_BRANCHING = Set.of(0xB0, 0xB1, 0xB2, 0xD5, 0xD6, 0xD7);
    public static final Set<Integer> OPCODES_UNCONTINUING = Set.of(0x34, 0x3C, 0x40, 0xB0);
    public static final Map<Integer, ScriptField> COMP_OPERATORS = new HashMap<>();
    public static final List<String> INDEX_ENUMS_ONLY = List.of("var", "saveData", "battleVar");
    public final Map<String, Map<Integer, ScriptField>> ENUMERATIONS = new HashMap<>();

    private static boolean initializedStatics = false;

    private ScriptConstants() {}

    public static void staticInitialize() {
        if (initializedStatics) {
            return;
        }
        initializedStatics = true;

        OPCODE_LABELS = new String[0x100];
        OPCODE_LABELS[0x00] = "NOP";
        OPCODE_LABELS[0x01] = "OPLOR";
        OPCODE_LABELS[0x02] = "OPLAND";
        OPCODE_LABELS[0x03] = "OPOR";
        OPCODE_LABELS[0x04] = "OPEOR";
        OPCODE_LABELS[0x05] = "OPAND";
        OPCODE_LABELS[0x06] = "OPEQ";
        OPCODE_LABELS[0x07] = "OPNE";
        OPCODE_LABELS[0x08] = "OPGTU";
        OPCODE_LABELS[0x09] = "OPLSU";
        OPCODE_LABELS[0x0A] = "OPGT";
        OPCODE_LABELS[0x0B] = "OPLS";
        OPCODE_LABELS[0x0C] = "OPGTEU";
        OPCODE_LABELS[0x0D] = "OPLSEU";
        OPCODE_LABELS[0x0E] = "OPGTE";
        OPCODE_LABELS[0x0F] = "OPLSE";
        OPCODE_LABELS[0x10] = "OPBON";
        OPCODE_LABELS[0x11] = "OPBOFF";
        OPCODE_LABELS[0x12] = "OPSLL";
        OPCODE_LABELS[0x13] = "OPSRL";
        OPCODE_LABELS[0x14] = "OPADD";
        OPCODE_LABELS[0x15] = "OPSUB";
        OPCODE_LABELS[0x16] = "OPMUL";
        OPCODE_LABELS[0x17] = "OPDIV";
        OPCODE_LABELS[0x18] = "OPMOD";
        OPCODE_LABELS[0x19] = "OPNOT";
        OPCODE_LABELS[0x1A] = "OPUMINUS";
        OPCODE_LABELS[0x1B] = "OPFIXADRS";
        OPCODE_LABELS[0x1C] = "OPBNOT";
        OPCODE_LABELS[0x1D] = "LABEL";
        OPCODE_LABELS[0x1E] = "TAG";
        OPCODE_LABELS[0x1F] = "PUSHV";
        OPCODE_LABELS[0x20] = "POPV";
        OPCODE_LABELS[0x21] = "POPVL";
        OPCODE_LABELS[0x22] = "PUSHAR";
        OPCODE_LABELS[0x23] = "POPAR";
        OPCODE_LABELS[0x24] = "POPARL";
        OPCODE_LABELS[0x25] = "POPA";
        OPCODE_LABELS[0x26] = "PUSHA";
        OPCODE_LABELS[0x27] = "PUSHARP";
        OPCODE_LABELS[0x28] = "PUSHX";
        OPCODE_LABELS[0x29] = "PUSHY";
        OPCODE_LABELS[0x2A] = "POPX";
        OPCODE_LABELS[0x2B] = "REPUSH";
        OPCODE_LABELS[0x2C] = "POPY";
        OPCODE_LABELS[0x2D] = "PUSHI";
        OPCODE_LABELS[0x2E] = "PUSHII";
        OPCODE_LABELS[0x2F] = "PUSHF";
        OPCODE_LABELS[0x30] = "JMP";
        OPCODE_LABELS[0x31] = "CJMP";
        OPCODE_LABELS[0x32] = "NCJMP";
        OPCODE_LABELS[0x33] = "JSR";
        OPCODE_LABELS[0x34] = "RTS";
        OPCODE_LABELS[0x35] = "CALL";
        OPCODE_LABELS[0x36] = "REQ";
        OPCODE_LABELS[0x37] = "REQSW";
        OPCODE_LABELS[0x38] = "REQEW";
        OPCODE_LABELS[0x39] = "PREQ";
        OPCODE_LABELS[0x3A] = "PREQSW";
        OPCODE_LABELS[0x3B] = "PREQEW";
        OPCODE_LABELS[0x3C] = "RET";
        OPCODE_LABELS[0x3D] = "RETN";
        OPCODE_LABELS[0x3E] = "RETT";
        OPCODE_LABELS[0x3F] = "RETTN";
        OPCODE_LABELS[0x40] = "HALT";
        OPCODE_LABELS[0x41] = "PUSHN";
        OPCODE_LABELS[0x42] = "PUSHT";
        OPCODE_LABELS[0x43] = "PUSHVP";
        OPCODE_LABELS[0x44] = "PUSHFIX";
        OPCODE_LABELS[0x45] = "FREQ";
        OPCODE_LABELS[0x46] = "TREQ";
        OPCODE_LABELS[0x47] = "BREQ";
        OPCODE_LABELS[0x48] = "BFREQ";
        OPCODE_LABELS[0x49] = "BTREQ";
        OPCODE_LABELS[0x4A] = "FREQSW";
        OPCODE_LABELS[0x4B] = "TREQSW";
        OPCODE_LABELS[0x4C] = "BREQSW";
        OPCODE_LABELS[0x4D] = "BFREQSW";
        OPCODE_LABELS[0x4E] = "BTREQSW";
        OPCODE_LABELS[0x4F] = "FREQEW";
        OPCODE_LABELS[0x50] = "TREQEW";
        OPCODE_LABELS[0x51] = "BREQEW";
        OPCODE_LABELS[0x52] = "BFREQEW";
        OPCODE_LABELS[0x53] = "BTREQEW";
        OPCODE_LABELS[0x54] = "DRET";
        OPCODE_LABELS[0x55] = "POPXJMP";
        OPCODE_LABELS[0x56] = "POPXCJMP";
        OPCODE_LABELS[0x57] = "POPXNCJMP";
        OPCODE_LABELS[0x58] = "CALLPOPA";
        OPCODE_LABELS[0x59] = "POPI0";
        OPCODE_LABELS[0x5A] = "POPI1";
        OPCODE_LABELS[0x5B] = "POPI2";
        OPCODE_LABELS[0x5C] = "POPI3";
        OPCODE_LABELS[0x5D] = "POPF0";
        OPCODE_LABELS[0x5E] = "POPF1";
        OPCODE_LABELS[0x5F] = "POPF2";
        OPCODE_LABELS[0x60] = "POPF3";
        OPCODE_LABELS[0x61] = "POPF4";
        OPCODE_LABELS[0x62] = "POPF5";
        OPCODE_LABELS[0x63] = "POPF6";
        OPCODE_LABELS[0x64] = "POPF7";
        OPCODE_LABELS[0x65] = "POPF8";
        OPCODE_LABELS[0x66] = "POPF9";
        OPCODE_LABELS[0x67] = "PUSHI0";
        OPCODE_LABELS[0x68] = "PUSHI1";
        OPCODE_LABELS[0x69] = "PUSHI2";
        OPCODE_LABELS[0x6A] = "PUSHI3";
        OPCODE_LABELS[0x6B] = "PUSHF0";
        OPCODE_LABELS[0x6C] = "PUSHF1";
        OPCODE_LABELS[0x6D] = "PUSHF2";
        OPCODE_LABELS[0x6E] = "PUSHF3";
        OPCODE_LABELS[0x6F] = "PUSHF4";
        OPCODE_LABELS[0x70] = "PUSHF5";
        OPCODE_LABELS[0x71] = "PUSHF6";
        OPCODE_LABELS[0x72] = "PUSHF7";
        OPCODE_LABELS[0x73] = "PUSHF8";
        OPCODE_LABELS[0x74] = "PUSHF9";
        OPCODE_LABELS[0x75] = "PUSHAINTER";
        OPCODE_LABELS[0x76] = "SYSTEM";
        OPCODE_LABELS[0x77] = "REQWAIT";
        OPCODE_LABELS[0x78] = "PREQWAIT";
        OPCODE_LABELS[0x79] = "REQCHG";
        OPCODE_LABELS[0x7A] = "ACTREQ";
        System.arraycopy(OPCODE_LABELS, 0, OPCODE_LABELS, 0x80, 0x7F);

        OPCODE_ENDLINE = new ArrayList<>();
        OPCODE_ENDLINE.add(0x25);
        OPCODE_ENDLINE.add(0x2A);
        OPCODE_ENDLINE.add(0x2C);
        OPCODE_ENDLINE.add(0x34);
        OPCODE_ENDLINE.add(0x3C);
        OPCODE_ENDLINE.add(0x3D);
        OPCODE_ENDLINE.add(0x3E);
        OPCODE_ENDLINE.add(0x3F);
        OPCODE_ENDLINE.add(0x40);
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
        OPCODE_ENDLINE.add(0xF6);

        OPCODE_STACKPOPS = new int[0x100];
        Arrays.fill(OPCODE_STACKPOPS, -1);
        OPCODE_STACKPOPS[0x00] = 0; // NOP
        Arrays.fill(OPCODE_STACKPOPS, 0x01, 0x19, 2); // Comparison operators
        OPCODE_STACKPOPS[0x19] = 1; // OPNOT / NOT_LOGIC
        OPCODE_STACKPOPS[0x1A] = 1; // OPUMINUS / NEG
        OPCODE_STACKPOPS[0x1C] = 1; // OPBNOT / NOT
        OPCODE_STACKPOPS[0x25] = 1; // POPA / SET_RETURN_VALUE
        OPCODE_STACKPOPS[0x26] = 0; // PUSHA / GET_RETURN_VALUE
        OPCODE_STACKPOPS[0x28] = 0; // PUSHX / GET_TEST
        OPCODE_STACKPOPS[0x29] = 0; // PUSHY / GET_CASE
        OPCODE_STACKPOPS[0x2A] = 1; // POPX / SET_TEST
        OPCODE_STACKPOPS[0x2B] = 1; // REPUSH / COPY
        OPCODE_STACKPOPS[0x2C] = 1; // POPY / SET_CASE
        OPCODE_STACKPOPS[0x34] = 0; // RTS / RETURN
        OPCODE_STACKPOPS[0x36] = 3; // REQ / SIG_NOACK
        OPCODE_STACKPOPS[0x37] = 3; // REQSW / SIG_ONSTART
        OPCODE_STACKPOPS[0x38] = 3; // REQEW / SIG_ONEND
        OPCODE_STACKPOPS[0x39] = 3; // PREQ (need to check what this even does)
        OPCODE_STACKPOPS[0x3C] = 0; // RET / END
        OPCODE_STACKPOPS[0x3D] = 1; // RETN / CLEANUP_END
        OPCODE_STACKPOPS[0x3F] = 1; // RETTN / CLEANUP_TO_MAIN
        OPCODE_STACKPOPS[0x40] = 0; // HALT / DYNAMIC
        OPCODE_STACKPOPS[0x46] = 3; // TREQ (need to check what this even does)
        OPCODE_STACKPOPS[0x54] = 0; // DRET / CLEANUP_ALL_END
        Arrays.fill(OPCODE_STACKPOPS, 0x59, 0x67, 1); // all POPI/F / SET_INT/FLOAT
        Arrays.fill(OPCODE_STACKPOPS, 0x67, 0x75, 0); // all PUSHI/F / GET_INT/FLOAT
        OPCODE_STACKPOPS[0x77] = 2; // REQWAIT / WAIT_DELETE
        OPCODE_STACKPOPS[0x79] = 3; // REQCHG / EDIT_ENTRY_TABLE (need to check what this even does)
        OPCODE_STACKPOPS[0x9F] = 0; // PUSHV / GET_DATUM
        OPCODE_STACKPOPS[0xA0] = 1; // POPV / SET_DATUM_W
        OPCODE_STACKPOPS[0xA1] = 1; // POPVL / SET_DATUM_T
        OPCODE_STACKPOPS[0xA2] = 1; // PUSHAR / GET_DATUM_INDEX
        OPCODE_STACKPOPS[0xA3] = 2; // POPAR / SET_DATUM_INDEX_W
        OPCODE_STACKPOPS[0xA4] = 2; // POPARL / SET_DATUM_INDEX_T
        OPCODE_STACKPOPS[0xA7] = 1; // PUSHARP / GET_DATUM_DESC
        OPCODE_STACKPOPS[0xAD] = 0; // PUSHI / CONST_INT
        OPCODE_STACKPOPS[0xAE] = 0; // PUSHII / IMM
        OPCODE_STACKPOPS[0xAF] = 0; // PUSHF / CONST_FLOAT
        OPCODE_STACKPOPS[0xB0] = 0; // JMP / JUMP
        OPCODE_STACKPOPS[0xB1] = 1; // CJMP / BNEZ
        OPCODE_STACKPOPS[0xB2] = 1; // NCJMP / BEZ
        OPCODE_STACKPOPS[0xB3] = 0; // JSR
        OPCODE_STACKPOPS[0xB5] = 0; // CALL / FUNC_RET
        OPCODE_STACKPOPS[0xD6] = 1; // POPXCJMP / SET_BNEZ
        OPCODE_STACKPOPS[0xD7] = 1; // POPXNCJMP / SET_BEZ
        OPCODE_STACKPOPS[0xD8] = 0; // CALLPOPA / FUNC
        OPCODE_STACKPOPS[0xF6] = 0; // SYSTEM

        putCompOperator(0x01, "or", "bool", "OPLOR");
        putCompOperator(0x02, "and", "bool", "OPLAND");
        putCompOperator(0x03, "|", "int", "OPOR");
        putCompOperator(0x04, "^", "int", "OPEOR");
        putCompOperator(0x05, "&", "int", "OPAND");
        putCompOperator(0x06, "==", "bool", "OPEQ");
        putCompOperator(0x07, "!=", "bool", "OPNE");
        putCompOperator(0x08, "> (unsigned)", "bool", "OPGTU");
        putCompOperator(0x09, "< (unsigned)", "bool", "OPLSU");
        putCompOperator(0x0A, ">", "bool", "OPGT");
        putCompOperator(0x0B, "<", "bool", "OPLS");
        putCompOperator(0x0C, ">= (unsigned)", "bool", "OPGTEU");
        putCompOperator(0x0D, "<= (unsigned)", "bool", "OPLSEU");
        putCompOperator(0x0E, ">=", "bool", "OPGTE");
        putCompOperator(0x0F, "<=", "bool", "OPLSE");
        putCompOperator(0x10, "OP-B-ON", "int", "OPBON");
        putCompOperator(0x11, "OP-B-OFF", "int", "OPBOFF");
        putCompOperator(0x12, "<<", "int", "OPSLL");
        putCompOperator(0x13, ">>", "int", "OPSRL");
        putCompOperator(0x14, "+", "int", "OPADD");
        putCompOperator(0x15, "-", "int", "OPSUB");
        putCompOperator(0x16, "*", "int", "OPMUL");
        putCompOperator(0x17, "/", "int", "OPDIV");
        putCompOperator(0x18, "mod", "int", "OPMOD");
    }

    private static void putCompOperator(int idx, String name, String type, String internalName) {
        ScriptField field = new ScriptField(name, type, internalName, idx);
        COMP_OPERATORS.put(idx, field);
    }

    public void initialize() {
        FUNCSPACES = new String[0x10];
        FUNCSPACES[0x0] = "Common";
        FUNCSPACES[0x1] = "Math";
        FUNCSPACES[0x2] = "Unknown2";
        FUNCSPACES[0x3] = "Unknown3";
        FUNCSPACES[0x4] = "SgEvent";
        FUNCSPACES[0x5] = "ChEvent";
        FUNCSPACES[0x6] = "Camera";
        FUNCSPACES[0x7] = "Battle";
        FUNCSPACES[0x8] = "Map";
        FUNCSPACES[0x9] = "Mount";
        FUNCSPACES[0xA] = "UnknownA";
        FUNCSPACES[0xB] = "Movie";
        FUNCSPACES[0xC] = "Debug";
        FUNCSPACES[0xD] = "AbilityMap";
        FUNCSPACES[0xE] = "UnknownE";
        FUNCSPACES[0xF] = "UnknownF";

        addEnumsFromAllCsvsInFolder(new File(ENUM_CSV_ROOT));

        putSaveDataVariable(0x1EF, "MoonflowFlags1", "int");
        putSaveDataVariable(0x1F0, "MoonflowFlags2", "int");
        putSaveDataVariable(0x1F1, "ViaPurificoFlags1", "int");
        putSaveDataVariable(0x1F2, "ViaPurificoFlags2", "int"); // Also Besaid Falls treasure at &0x80 for some reason
        putSaveDataVariable(0x1F3, "MoonflowFlags3", "int");
        putSaveDataVariable(0x26C, "RidingChocoboFlags", "int");
        putSaveDataVariable(0x279, "CalmLandsQuestProgressionFlags", "int");
        putSaveDataVariable(0x27E, "MushroomRockRoadFlags", "int");
        putSaveDataVariable(0x281, "CalmLandsTreasureFlags", "int");
        putSaveDataVariable(0x287, "BelgemineFightProgressionFlags", "int");
        putSaveDataVariable(0x288, "YojimboHireAnswerNumber", "int"); // Only used/referenced within nagi0600
        putSaveDataVariable(0x28B, "MonsterArenaUnlockQuestProgressionFlags", "int");
        putSaveDataVariable(0x28C, "MonsterArenaAreaSpeciesConquestUnlockFlags", "int");
        putSaveDataVariable(0x290, "MonsterArenaOriginalCreationUnlockFlags", "int");
        putSaveDataVariable(0x294, "WobblyChocoboRecordMinutes", "int");
        putSaveDataVariable(0x295, "WobblyChocoboRecordSeconds", "int");
        putSaveDataVariable(0x296, "WobblyChocoboRecordTenths", "int");
        putSaveDataVariable(0x297, "DodgerChocoboRecordMinutes", "int");
        putSaveDataVariable(0x298, "DodgerChocoboRecordSeconds", "int");
        putSaveDataVariable(0x299, "DodgerChocoboRecordTenths", "int");
        putSaveDataVariable(0x29A, "HyperDodgerChocoboRecordMinutes", "int");
        putSaveDataVariable(0x29B, "HyperDodgerChocoboRecordSeconds", "int");
        putSaveDataVariable(0x29C, "HyperDodgerChocoboRecordTenths", "int");
        putSaveDataVariable(0x29D, "CatcherChocoboRecordMinutes", "int");
        putSaveDataVariable(0x29E, "CatcherChocoboRecordSeconds", "int");
        putSaveDataVariable(0x29F, "CatcherChocoboRecordTenths", "int");
        putSaveDataVariable(0x2BA, "BikanelTreasureFlags1", "int");
        putSaveDataVariable(0x2BB, "BikanelTreasureFlags2", "int");
        putSaveDataVariable(0x2BC, "BikanelTreasureFlags3", "int");
        putSaveDataVariable(0x2F0, "EnergyBlastProgressionFlags", "int");
        putSaveDataVariable(0x300, "RemiemTempleProgressionFlags", "int");
        putSaveDataVariable(0x301, "BesaidVillageTreasureFlags", "int");
        putSaveDataVariable(0x33F, "MusicSpheresOwnedCount", "int");
        putSaveDataVariable(0x340, "MovieSpheresOwnedCount", "int");
        putSaveDataVariable(0x37F, "DebugSkipJechtIntroScenes", "bool");
        putSaveDataVariable(0x337, "ControllableCharacterInLuca", "playerChar");
        putSaveDataVariable(0x3AC, "KilikaForestTreasureFlags", "int");
        putSaveDataVariable(0x3B1, "BesaidTreasureFlags", "int");
        putSaveDataVariable(0x3B9, "MacalaniaTreasureFlags", "int");
        putSaveDataVariable(0x3Be, "OmegaRuinsProgressionFlags", "int");
        putSaveDataVariable(0x3C0, "HomeProgressionFlags", "int");
        putSaveDataVariable(0x3F1, "ThunderPlainsProgressionFlags", "int");
        putSaveDataVariable(0x3F4, "LightningDodgingRewardsToPickUpFlags", "int");
        putSaveDataVariable(0x3Fc, "LightningDodgingTotalBolts", "int");
        putSaveDataVariable(0x3Fe, "LightningDodgingTotalDodges", "int");
        putSaveDataVariable(0x400, "LightningDodgingHighestConsecutiveDodges", "int"); // 440
        putSaveDataVariable(0x438, "BlitzballWakkaPowerProgress", "int");
        putSaveDataVariable(0x440, "BlitzballMovementType", "blitzballMovementType");
        putSaveDataVariable(0x451, "AirshipLocationsVisitedFlags", "int");
        putSaveDataVariable(0xBEC, "GameMoment", "int");
        putSaveDataVariable(0xC20, "GilLentToOAka", "int");
        putSaveDataVariable(0xC24, "MacalaniaPricesChosenForOAka", "int");
        putSaveDataVariable(0xC28, "JechtShotMinigameCleared", "bool");
        putSaveDataVariable(0xC29, "BelgemineFamiliarity", "int");
        putSaveDataVariable(0xC30, "RemiemAeonsDefeated", "int");
        putSaveDataVariable(0xC35, "WorldChampionPrerequisitesCleared", "bool");
        putSaveDataVariable(0xC36, "SaveSphereInstructionsSeen", "int");
        putSaveDataVariable(0xC38, "CelestialMirrorObtained", "bool");
        putSaveDataVariable(0xC39, "CelestialsObtainedFlags", "int");
        putSaveDataVariable(0xC3C, "CalmLandsChocoboPositionX", "float");
        putSaveDataVariable(0xC40, "CalmLandsChocoboPositionY", "float");
        putSaveDataVariable(0xC44, "CalmLandsChocoboPositionZ", "float");
        putSaveDataVariable(0xC4C, "AlBhedPrimersCollectedCount", "int");
        putSaveDataVariable(0xC54, "BlitzballMenuReturnMap", "int");
        putSaveDataVariable(0xC58, "BlitzballMenuReturnEntrance", "int");
        putSaveDataVariable(0xC5C, "AnimaSealsUnlocked", "int");
        putSaveDataVariable(0xC68, "BattleInfoStorage", "int"); // Some usages: Evrae Altana's remaining HP, which Sahagin remains alive before Geosgaeno.
        putSaveDataVariable(0xC6C, "CelestialsHalfPoweredFlags", "int");
        putSaveDataVariable(0xC6D, "CelestialsFullPoweredFlags", "int");
        putSaveDataVariable(0xC6F, "isRedButterflyEncounter", "bool");
        putSaveDataVariable(0xC70, "MusicTempStorageForResting", "bgm");
        putSaveDataVariable(0xC74, "BlitzballTeamPlayerCount", "int", "blitzballTeam");
        putSaveDataVariable(0xC7B, "OptionalAeonsRecruitedFlags", "int");
        putSaveDataVariable(0xC7F, "JechtSpheresCollectedCount", "int");
        putSaveDataVariable(0xC80, "AeonBoostingKeyItemsObtainedFlags", "int");
        putSaveDataVariable(0xC81, "AirshipDestinationUnlocks", "int");
        putSaveDataVariable(0xC83, "?CactuarGuardiansQuestProgress", "int");
        putSaveDataVariable(0xC84, "IsBetweenDjoseFaythTalkAndAirshipBoarding", "bool");
        putSaveDataVariable(0xC85, "CactuarGuardiansBeaten", "int");
        putSaveDataVariable(0xC86, "AlBhedPrimersInstructionsSeen", "bool");
        putSaveDataVariable(0xC87, "RemiemRaceTreasureFlags", "int");
        putSaveDataVariable(0xC89, "DarkValeforCompletionFlags", "int");
        putSaveDataVariable(0xC8A, "DarkIfritCompletionFlags", "int");
        putSaveDataVariable(0xC8B, "DarkIxionCompletionFlags", "int");
        putSaveDataVariable(0xC8C, "DarkShivaCompletionFlags", "int");
        putSaveDataVariable(0xC8D, "DarkBahamutCompletionFlags", "int");
        putSaveDataVariable(0xC8E, "DarkYojimboCompletionFlags", "int");
        putSaveDataVariable(0xC8F, "DarkAnimaCompletionFlags", "int");
        putSaveDataVariable(0xC90, "DarkMagusSistersCompletionFlags", "int");
        putSaveDataVariable(0xC91, "PenanceUnlockState", "int");
        putSaveDataVariable(0xCD7, "BattleDialogLinesSeenFlags", "int");
        putSaveDataVariable(0xD4f, "?TidusBattleTalkativeness", "int");
        putSaveDataVariable(0xD50, "?WakkaBattleTalkativeness", "int");
        putSaveDataVariable(0xD51, "?YunaBattleTalkativeness", "int");
        putSaveDataVariable(0xD52, "?LuluBattleTalkativeness", "int");
        putSaveDataVariable(0xD53, "?RikkuBattleTalkativeness", "int");
        putSaveDataVariable(0xD54, "?AuronBattleTalkativeness", "int");
        putSaveDataVariable(0xD55, "Unused?KimahriBattleTalkativeness", "int");
        putSaveDataVariable(0x11EC, "BlitzballPlayerLearnedTechsPage1", "bitfieldFrom_blitzTechP1", "blitzballPlayer");
        putSaveDataVariable(0x12DC, "BlitzballPlayerLearnedTechsPage2", "bitfieldFrom_blitzTechP2", "blitzballPlayer");
        putSaveDataVariable(0x13CC, "BlitzballPlayerPendingLearningTechsPage1?", "bitfieldFrom_blitzTechP1", "blitzballPlayer");
        putSaveDataVariable(0x140C, "BlitzballPlayerPendingLearningTechsPage2?", "bitfieldFrom_blitzTechP2", "blitzballPlayer");
        putSaveDataVariable(0x1452, "BlitzballPlayerEquippedTechs", "blitzTech"); // index = blitzballPlayer * 5 + 0..4
        putSaveDataVariable(0x157E, "BlitzballPlayerUnlockedTechSlots", "int", "blitzballPlayer");
        putSaveDataVariable(0x15BA, "BlitzballPlayerCurrentLevel", "int", "blitzballPlayer");
        putSaveDataVariable(0x15F6, "MatchPlayerPendingEXP", "int", "int");
        putSaveDataVariable(0x1754, "BlitzballPlayerCurrentEXP", "int", "blitzballPlayer");
        putSaveDataVariable(0x1606, "BlitzballTeamPlayers", "blitzballPlayer");
        putSaveDataVariable(0x163E, "BlitzballMatchAurochsScore", "int");
        putSaveDataVariable(0x163F, "BlitzballMatchOpponentScore", "int");
        putSaveDataVariable(0x1640, "BlitzballIsSecondHalf", "bool");
        putSaveDataVariable(0x1641, "BlitzballMatchPlayerMarkedIndex", "int", "int");
        putSaveDataVariable(0x1651, "BlitzballEnemyTeam", "blitzballTeam");
        putSaveDataVariable(0x1652, "BlitzballCurrentLeagueTeamSeedings", "blitzballTeam", "int");
        putSaveDataVariable(0x1658, "BlitzballCurrentTournamentTeamSlot", "blitzballTeam", "int");
        putSaveDataVariable(0x1666, "BlitzballCurrentLeagueTeamWins", "int", "blitzballTeam");
        putSaveDataVariable(0x166C, "BlitzballCurrentLeagueTeamLosses", "int", "blitzballTeam");
        putSaveDataVariable(0x1672, "BlitzballCurrentLeagueRound", "int");
        putSaveDataVariable(0x1673, "BlitzballCurrentLeagueMatchTeamSlot", "blitzballTeam", "int");
        putSaveDataVariable(0x1679, "BlitzballAurochsMatchupLine", "int");
        putSaveDataVariable(0x167A, "BlitzballGameMode", "blitzballGameMode");
        putSaveDataVariable(0x169F, "BlitzballCurrentTournamentState", "int");
        putSaveDataVariable(0x16A0, null, "int");
        putSaveDataVariable(0x16A1, "BlitzballCurrentGameGoalsScored", "int");
        putSaveDataVariable(0x16A2, "BlitzballCurrentGameGoalScorer", "blitzballPlayer", "int");
        putSaveDataVariable(0x1714, "BlitzballAurochsMatchupLineLeftSide", "bool");
        putSaveDataVariable(0x1716, "BlitzballPlayerContractDurations", "int", "blitzballPlayer");
        putSaveDataVariable(0x1752, "BlitzballTotalGamesWon", "int");
        putSaveDataVariable(0x17CC, "BlitzballCurrentGameGoalTimestamp", "int", "int");
        putSaveDataVariable(0x1984, "BlitzballPlayerCostPerGame", "int", "blitzballPlayer");
        putSaveDataVariable(0x19FC, "BlitzballLeaguePrizeIndex", "int");
        putSaveDataVariable(0x1A02, "BlitzballTournamentPrizeIndex", "int");
        putSaveDataVariable(0x1A08, "BlitzballLeagueTopScorerPrizeIndex", "int");
        putSaveDataVariable(0x1A0A, "BlitzballTournamentTopScorerPrizeIndex", "int");
        putSaveDataVariable(0x1A0C, "?BlitzballPlayerUncoveredTechsPage1", "bitfieldFrom_blitzTechP1", "blitzballPlayer");
        putSaveDataVariable(0x1AFC, "?BlitzballPlayerUncoveredTechsPage2", "bitfieldFrom_blitzTechP2", "blitzballPlayer");
        putSaveDataVariable(0x21EC, "SphereGridNodeState", "sphereGridNodeState", "int");

        putBattleVariable(0x0170, "BattleDialogLineVoiceFile", "voiceFile");
        putBattleVariable(0x028C, "BattleDialogLineString", "system01String");

        putEnum("playerChar", 0x0000, "Tidus", "PC_TIDUS");
        putEnum("playerChar", 0x0001, "Yuna", "PC_YUNA");
        putEnum("playerChar", 0x0002, "Auron", "PC_AURON");
        putEnum("playerChar", 0x0003, "Kimahri", "PC_KIMAHRI");
        putEnum("playerChar", 0x0004, "Wakka", "PC_WAKKA");
        putEnum("playerChar", 0x0005, "Lulu", "PC_LULU");
        putEnum("playerChar", 0x0006, "Rikku", "PC_RIKKU");
        putEnum("playerChar", 0x0007, "Seymour", "PC_SEYMOUR");
        putEnum("playerChar", 0x0008, "Valefor", "PC_VALEFOR");
        putEnum("playerChar", 0x0009, "Ifrit", "PC_IFRIT");
        putEnum("playerChar", 0x000A, "Ixion", "PC_IXION");
        putEnum("playerChar", 0x000B, "Shiva", "PC_SHIVA");
        putEnum("playerChar", 0x000C, "Bahamut", "PC_BAHAMUT");
        putEnum("playerChar", 0x000D, "Anima", "PC_ANIMA");
        putEnum("playerChar", 0x000E, "Yojimbo", "PC_YOJIMBO");
        putEnum("playerChar", 0x000F, "Cindy", "PC_MAGUS1");
        putEnum("playerChar", 0x0010, "Sandy", "PC_MAGUS2");
        putEnum("playerChar", 0x0011, "Mindy", "PC_MAGUS3");
        putEnum("playerChar", 0x0012, null, "PC_DUMMY");
        putEnum("playerChar", 0x0013, null, "PC_DUMMY2");
        putEnum("playerChar", -1, "Empty", null); // 0xFFFF

        putEnum("deathAnimation", 0x00, "Character (Body remains and targetable)", "death_normal");
        putEnum("deathAnimation", 0x01, "Boss (Body remains but untargetable)", "death_nop");
        putEnum("deathAnimation", 0x02, "Humanoid (No Pyreflies, body fades out)", "death_fadeout");
        putEnum("deathAnimation", 0x03, "Fiend (Pyrefly dissipation)", "death_phantom");
        putEnum("deathAnimation", 0x04, "Disintegrate-Machina (Red explosions)", "death_exp");
        putEnum("deathAnimation", 0x05, "Steal-Machina (Same as 02 with machina SFX)", "death_break");
        putEnum("deathAnimation", 0x08, "YAT/YKT", "death_break2");

        putEnum("selector", 0x00, "Any/All", "nop");
        putEnum("selector", 0x01, "Highest", "max");
        putEnum("selector", 0x02, "Lowest", "min");
        putEnum("selector", 0x80, "Not", "not");

        putEnum("ambushState", 0x00, "Normal (00)", "first_attack_normal");
        putEnum("ambushState", 0x01, "Preemptive", "first_attack_player");
        putEnum("ambushState", 0x02, "Ambushed", "first_attack_monster");
        putEnum("ambushState", 0x03, "Normal (03)", "first_attack_random_off");

        putEnum("yojimboReaction", 0x00, "NullReaction");
        putEnum("yojimboReaction", 0x01, "Regular", "youjinbou_consent_pay");
        putEnum("yojimboReaction", 0x02, "Nod", "youjinbou_consent_pay_ok");
        putEnum("yojimboReaction", 0x03, "Headshake", "youjinbou_consent_pay_ng");

        putEnum("targetType", 0x00, "Single", "target_type_single");
        putEnum("targetType", 0x01, "Multi", "target_type_group");
        putEnum("targetType", 0x02, "AllActors?", "target_type_all");
        putEnum("targetType", 0x03, "Self?", "target_type_own");

        putEnum("eventWorkerType", 0x00, "Subroutine");
        putEnum("eventWorkerType", 0x01, "FieldObject");
        putEnum("eventWorkerType", 0x02, "PlayerEdge");
        putEnum("eventWorkerType", 0x03, "PlayerZone");
        putEnum("eventWorkerType", 0x04, "Scenario");
        putEnum("eventWorkerType", 0x05, "Edge");
        putEnum("eventWorkerType", 0x06, "Zone");

        putEnum("battleWorkerType", 0x00, "CameraHandler");
        putEnum("battleWorkerType", 0x01, "MotionHandler");
        putEnum("battleWorkerType", 0x02, "CombatHandler");
        putEnum("battleWorkerType", 0x03, "BattleGruntHandler");
        putEnum("battleWorkerType", 0x04, "EncounterScripts");
        putEnum("battleWorkerType", 0x05, "VoiceHandler");
        putEnum("battleWorkerType", 0x06, "StartEndHooks");
        putEnum("battleWorkerType", 0x07, "MagicCameraHandler-Command");
        putEnum("battleWorkerType", 0x08, "MagicCameraHandler-Item");
        putEnum("battleWorkerType", 0x09, "MagicCameraHandler-Monmagic1");
        putEnum("battleWorkerType", 0x0A, "MagicCameraHandler-Monmagic2");

        putEnum("battleWorkerSlot", 0x00, "?BattleCameras");
        for (int i = 0; i <= 0x11; i++) {
            String chr = StackObject.enumToScriptField("playerChar", i).getName();
            if (i >= 0x0E) {
                putEnum("battleWorkerSlot", i + 0x17, "BasicHandlesFor" + chr);
            }
            putEnum("battleWorkerSlot", i + 0x29, "ExtraHandlesFor" + chr);
            putEnum("battleWorkerSlot", i + 0x6D, "VoiceHandlesFor" + chr);
        }
        putEnum("battleWorkerSlot", 0x3D, "MonsterAi");
        putEnum("battleWorkerSlot", 0x3E, "BtlScene0-7");
        putEnum("battleWorkerSlot", 0x3F, "BtlScene8+ (Voice)");
        for (int i = 0; i < 3; i++) {
            putEnum("battleWorkerSlot", 0x41 + i, "MagicCam3-" + i);
            putEnum("battleWorkerSlot", 0x44 + i, "MagicCam2-" + i);
            putEnum("battleWorkerSlot", 0x47 + i, "MagicCam4-" + i);
            putEnum("battleWorkerSlot", 0x4A + i, "MagicCam6-" + i);
        }

        putEnum("combatHandlerTag", 0x00, "onTurn");
        putEnum("combatHandlerTag", 0x01, "preTurn");
        putEnum("combatHandlerTag", 0x02, "onTargeted");
        putEnum("combatHandlerTag", 0x03, "onHit");
        putEnum("combatHandlerTag", 0x04, "onDeath");
        putEnum("combatHandlerTag", 0x05, "onMove");
        putEnum("combatHandlerTag", 0x06, "postTurn"); // prePoison
        putEnum("combatHandlerTag", 0x07, "postMove?");
        putEnum("combatHandlerTag", 0x08, "postPoison");
        putEnum("combatHandlerTag", 0x09, "YojiPay");
        putEnum("combatHandlerTag", 0x0A, "YojiDismiss");
        putEnum("combatHandlerTag", 0x0B, "YojiDeath");
        putEnum("combatHandlerTag", 0x0C, "MagusTurn");
        putEnum("combatHandlerTag", 0x0D, "MagusDoAsYouWill");
        putEnum("combatHandlerTag", 0x0E, "MagusOneMoreTime");
        putEnum("combatHandlerTag", 0x0F, "MagusFight");
        putEnum("combatHandlerTag", 0x10, "MagusGoGo");
        putEnum("combatHandlerTag", 0x11, "MagusHelpEachOther");
        putEnum("combatHandlerTag", 0x12, "MagusCombinePowers");
        putEnum("combatHandlerTag", 0x13, "MagusDefense");
        putEnum("combatHandlerTag", 0x14, "MagusAreYouAllRight");

        putEnum("cameraHandlerTag", 0x18, "Enter");
        putEnum("cameraHandlerTag", 0x19, "Select");
        putEnum("cameraHandlerTag", 0x1B, "MagicStart");
        putEnum("cameraHandlerTag", 0x1C, "Normal");
        putEnum("cameraHandlerTag", 0x2C, "MonMagicStart");
        putEnum("cameraHandlerTag", 0x2D, "MonMagicLaunch");
        putEnum("cameraHandlerTag", 0x2E, "MonItemStart");
        putEnum("cameraHandlerTag", 0x2F, "MonItemLaunch");
        putEnum("cameraHandlerTag", 0x33, "ItemLaunch");
        putEnum("cameraHandlerTag", 0x34, "MagicLaunch");
        putEnum("cameraHandlerTag", 0x36, "Swap");
        putEnum("cameraHandlerTag", 0x3C, "SkillActivation");
        putEnum("cameraHandlerTag", 0x42, "PlayerVictory");
        putEnum("cameraHandlerTag", 0x43, "PlayerDefeat");
        putEnum("cameraHandlerTag", 0x79, "SummonMagicFiring");
        putEnum("cameraHandlerTag", 0x83, "Summon");

        putEnum("battleGruntHandlerTag", 0x09, "OnAttack");
        putEnum("battleGruntHandlerTag", 0x0A, "AfterAttack");
        putEnum("battleGruntHandlerTag", 0x0B, "OnDamaged");

        putEnum("motionHandlerTag", 0x00, "Wait", "_tag_motion_wait");
        putEnum("motionHandlerTag", 0x04, "Magic", "_tag_motion_magic");
        putEnum("motionHandlerTag", 0x05, "MagicThrow", "_tag_motion_magic_throw");
        putEnum("motionHandlerTag", 0x13, "SP1", "_tag_motion_sp1");
        putEnum("motionHandlerTag", 0x14, "SP2", "_tag_motion_sp2");
        putEnum("motionHandlerTag", 0x15, "SP3", "_tag_motion_sp3");
        putEnum("motionHandlerTag", 0x16, "SP4", "_tag_motion_sp4");
        putEnum("motionHandlerTag", 0x17, "SP5", "_tag_motion_sp5");
        putEnum("motionHandlerTag", 0x18, "SP6", "_tag_motion_sp6");
        putEnum("motionHandlerTag", 0x19, "SP7", "_tag_motion_sp7");
        putEnum("motionHandlerTag", 0x1A, "SP8", "_tag_motion_sp8");
        putEnum("motionHandlerTag", 0x41, "AttackEnd", "_tag_motion_attack_end");
        putEnum("motionHandlerTag", 0x4E, "Pay", "_tag_motion_pay");

        putEnum("battleStartEndHookTag", 0x04, "End");
        putEnum("battleStartEndHookTag", 0x05, "Start");

        putEnum("voiceHandlerTag", 0x09, "Command");
        putEnum("voiceHandlerTag", 0x0A, "Revived");
        putEnum("voiceHandlerTag", 0x0B, "Switched");
        putEnum("voiceHandlerTag", 0x0C, "Summoned");
        putEnum("voiceHandlerTag", 0x0D, "Provoke");
        putEnum("voiceHandlerTag", 0x0E, "Threaten");
        putEnum("voiceHandlerTag", 0x0F, "Flee?");

        putEnum("fieldInteraction", 0x00, "talk");
        putEnum("fieldInteraction", 0x01, "scout");
        putEnum("fieldInteraction", 0x02, "cross");
        putEnum("fieldInteraction", 0x03, "touch");
        putEnum("fieldInteraction", 0x04, "enter");
        putEnum("fieldInteraction", 0x05, "leave");

        putEnum("controllerButton", 0x00, "?L2 (Scroll Up)");
        putEnum("controllerButton", 0x01, "?R2 (Scroll Down)");
        putEnum("controllerButton", 0x02, "?L1 (Switch)");
        putEnum("controllerButton", 0x03, "?R1 (CTB Preview Down)");
        putEnum("controllerButton", 0x04, "Triangle (Menu/Defend)");
        putEnum("controllerButton", 0x05, "X (Confirm)");
        putEnum("controllerButton", 0x06, "Circle (Cancel)");
        putEnum("controllerButton", 0x07, "Square (Scout)");
        putEnum("controllerButton", 0x08, "Select");
        putEnum("controllerButton", 0x0B, "?Start (Pause)");
        putEnum("controllerButton", 0x0C, "?Up");
        putEnum("controllerButton", 0x0D, "?Right");
        putEnum("controllerButton", 0x0E, "?Down");
        putEnum("controllerButton", 0x0F, "?Left");

        putEnum("stringVarType", 0x00, "?Resolved String");
        putEnum("stringVarType", 0x01, "Immediate Integer");
        for (int i = 0; i < 16; i++) {
            putEnum("stringVarType", 0x13 + i, "MacroDict#" + i);
        }
        putEnum("stringVarType", 0x2B, "Immediate Integer (1 Digit?)");
        putEnum("stringVarType", 0x2C, "Immediate Integer (2 Digits?)");
        putEnum("stringVarType", 0x2D, "Immediate Integer (3 Digits?)");
        putEnum("stringVarType", 0x2E, "Immediate Integer (4 Digits?)");
        putEnum("stringVarType", 0x33, "Item Label");
        putEnum("stringVarType", 0x34, "Gear Label");
        putEnum("stringVarType", 0x35, "Command Label");
        putEnum("stringVarType", 0x36, "Auto-Ability Label");
        putEnum("stringVarType", 0x37, "Treasure Label");

        putEnum("textFlagBitfield", 0x0002, "?HasSpeakerLine");
        putEnum("textFlagBitfield", 0x4000, "Al Bhed");

        putEnum("blitzTechPassiveFamilyBitfield", 0x0002, "Anti-Venom");
        putEnum("blitzTechPassiveFamilyBitfield", 0x0004, "Anti-Nap");
        putEnum("blitzTechPassiveFamilyBitfield", 0x0008, "Anti-Wither");
        putEnum("blitzTechPassiveFamilyBitfield", 0x0010, "Anti-Drain");
        putEnum("blitzTechPassiveFamilyBitfield", 0x0080, "Tackle Slip");
        putEnum("blitzTechPassiveFamilyBitfield", 0x0200, "Volley");
        putEnum("blitzTechPassiveFamilyBitfield", 0x0400, "Elite Defense");
        putEnum("blitzTechPassiveFamilyBitfield", 0x0800, "Brawler");
        putEnum("blitzTechPassiveFamilyBitfield", 0x1000, "Good Morning!");
        putEnum("blitzTechPassiveFamilyBitfield", 0x2000, "Grip Gloves");

        putEnum("blitzballTechcopyTiming", 1, "Too early");
        putEnum("blitzballTechcopyTiming", 2, "Correct");
        putEnum("blitzballTechcopyTiming", 3, "Too late");

        /*
        Note: Now handles directly in StackObject as an indirection into macro dict 7
        putEnum("blitzballPlayer", 0x00, "Tidus");
        putEnum("blitzballPlayer", 0x01, "Wakka");
        putEnum("blitzballPlayer", 0x02, "Datto");
        putEnum("blitzballPlayer", 0x03, "Letty");
        putEnum("blitzballPlayer", 0x04, "Jassu");
        putEnum("blitzballPlayer", 0x05, "Botta");
        putEnum("blitzballPlayer", 0x06, "Keepa");
        putEnum("blitzballPlayer", 0x07, "Bickson");
        putEnum("blitzballPlayer", 0x08, "Abus");
        putEnum("blitzballPlayer", 0x09, "Graav");
        putEnum("blitzballPlayer", 0x0A, "?Doram");
        putEnum("blitzballPlayer", 0x0B, "?Balgerda");
        putEnum("blitzballPlayer", 0x0C, "?Raudy");
        putEnum("blitzballPlayer", 0x0D, "?Larbeight");
        putEnum("blitzballPlayer", 0x0E, "?Isken");
        putEnum("blitzballPlayer", 0x0F, "?Vuroja");
        putEnum("blitzballPlayer", 0x10, "?Kulukan");
        putEnum("blitzballPlayer", 0x11, "?Deim");
        putEnum("blitzballPlayer", 0x12, "?Nizarut");
        putEnum("blitzballPlayer", 0x13, "?Eigaar");
        putEnum("blitzballPlayer", 0x14, "?Blappa");
        putEnum("blitzballPlayer", 0x15, "?Berrik");
        putEnum("blitzballPlayer", 0x16, "?Judda");
        putEnum("blitzballPlayer", 0x17, "?Lakkam");
        putEnum("blitzballPlayer", 0x18, "?Nimrook");
        putEnum("blitzballPlayer", 0x19, "Basik Ronso");
        putEnum("blitzballPlayer", 0x1A, "Argai Ronso");
        putEnum("blitzballPlayer", 0x1B, "Gazna Ronso");
        putEnum("blitzballPlayer", 0x1C, "?Nuvy Ronso");
        putEnum("blitzballPlayer", 0x1D, "?Irga Ronso");
        putEnum("blitzballPlayer", 0x1E, "?Zamzi Ronso");
        putEnum("blitzballPlayer", 0x1F, "?Giera Guado");
        putEnum("blitzballPlayer", 0x20, "?Zazi Guado");
        putEnum("blitzballPlayer", 0x21, "?Navara Guado");
        putEnum("blitzballPlayer", 0x22, "?Auda Guado");
        putEnum("blitzballPlayer", 0x23, "?Pah Guado");
        putEnum("blitzballPlayer", 0x24, "?Noy Guado");
        putEnum("blitzballPlayer", 0x25, "Rin");
        putEnum("blitzballPlayer", 0x26, "Tatts");
        putEnum("blitzballPlayer", 0x27, "Kyou");
        putEnum("blitzballPlayer", 0x28, "Shuu");
        putEnum("blitzballPlayer", 0x29, "Nedus");
        putEnum("blitzballPlayer", 0x2A, "Biggs");
        putEnum("blitzballPlayer", 0x2B, "Wedge");
        putEnum("blitzballPlayer", 0x2C, "Ropp");
        putEnum("blitzballPlayer", 0x2D, "Linna");
        putEnum("blitzballPlayer", 0x2E, "Mep");
        putEnum("blitzballPlayer", 0x2F, "Zalitz");
        putEnum("blitzballPlayer", 0x30, "Naida");
        putEnum("blitzballPlayer", 0x31, "Durren");
        putEnum("blitzballPlayer", 0x32, "Jumal");
        putEnum("blitzballPlayer", 0x33, "Svanda");
        putEnum("blitzballPlayer", 0x34, "Vilucha");
        putEnum("blitzballPlayer", 0x35, "Shaami");
        putEnum("blitzballPlayer", 0x36, "Zev Ronso");
        putEnum("blitzballPlayer", 0x37, "Yuma Guado");
        putEnum("blitzballPlayer", 0x38, "Kiyuri");
        putEnum("blitzballPlayer", 0x39, "Brother");
        putEnum("blitzballPlayer", 0x3A, "Mifurey");
        putEnum("blitzballPlayer", 0x3B, "Miyu");
        putEnum("blitzballPlayer", 0x3C, "<Empty>"); */

        putEnum("blitzballTeam", 0x00, "Luca Goers");
        putEnum("blitzballTeam", 0x01, "Kilika Beasts");
        putEnum("blitzballTeam", 0x02, "Al Bhed Psyches");
        putEnum("blitzballTeam", 0x03, "Ronso Fangs");
        putEnum("blitzballTeam", 0x04, "Guado Glories");
        putEnum("blitzballTeam", 0x05, "Besaid Aurochs");

        putEnum("blitzballMovementType", 0x00, "Auto");
        putEnum("blitzballMovementType", 0x01, "Manual A");
        putEnum("blitzballMovementType", 0x02, "Manual B");

        putEnum("blitzballGameMode", 0x00, "League");
        putEnum("blitzballGameMode", 0x01, "Tournament");
        putEnum("blitzballGameMode", 0x02, "Exhibition");
        putEnum("blitzballGameMode", 0x03, "Tutorial");
        putEnum("blitzballGameMode", 0x04, "Team Data");
        putEnum("blitzballGameMode", 0x05, "Cancel");
        putEnum("blitzballGameMode", 0x06, "Reset Data");

        putEnum("battleDebugFlag", 0x00, "?FullItems");
        putEnum("battleDebugFlag", 0x01, "?PlayersInvincible");
        putEnum("battleDebugFlag", 0x02, "?AllInvincible");
        putEnum("battleDebugFlag", 0x03, "ControlMonsters");
        putEnum("battleDebugFlag", 0x04, "?NoMpCost");
        putEnum("battleDebugFlag", 0x05, "?AlwaysOverdrive");
        putEnum("battleDebugFlag", 0x06, "?NoDamageVariance");
        putEnum("battleDebugFlag", 0x07, "NeverCrit");
        putEnum("battleDebugFlag", 0x08, "?AttacksStatusesAlwaysHit");
        putEnum("battleDebugFlag", 0x09, "?LogBattleInformation");
        putEnum("battleDebugFlag", 0x0A, "?FullSpGil");
        putEnum("battleDebugFlag", 0x0B, "?FullWeapons");
        putEnum("battleDebugFlag", 0x0C, "?MonstersInvincible");
        putEnum("battleDebugFlag", 0x0D, "?AlwaysCrit");
        putEnum("battleDebugFlag", 0x0E, "?DmgAlways1");
        putEnum("battleDebugFlag", 0x0F, "?DmgAlways9999");
        putEnum("battleDebugFlag", 0x10, "?DmgAlways99999");
        putEnum("battleDebugFlag", 0x11, "?AlwaysRareDrop");
        putEnum("battleDebugFlag", 0x12, "?APx100");
        putEnum("battleDebugFlag", 0x13, "?Gilx100");
        putEnum("battleDebugFlag", 0x14, "?NoOverkills");
        putEnum("battleDebugFlag", 0x15, "?FullCommands");
        putEnum("battleDebugFlag", 0x16, "?FullSummons");
        putEnum("battleDebugFlag", 0x17, "?CommandSkip");
        putEnum("battleDebugFlag", 0x18, "?PermanentSensor");
        putEnum("battleDebugFlag", 0x19, "?AlwaysPreemptive");

        putEnum("messageWindowState", 0x01, "Printing?");
        putEnum("messageWindowState", 0x02, "Fully printed?");
        putEnum("messageWindowState", 0x03, "Closed");

        putEnum("textAlignment", 0x00, "Top Left", "MESWIN_POS_LEFTUP");
        putEnum("textAlignment", 0x01, "Bottom Left", "MESWIN_POS_LEFTDOWN");
        putEnum("textAlignment", 0x02, "Top Right", "MESWIN_POS_RIGHTUP");
        putEnum("textAlignment", 0x03, "Bottom Right", "MESWIN_POS_RIGHTDOWN");
        putEnum("textAlignment", 0x04, "Center", "MESWIN_POS_CENTER");

        putEnum("effectType", 0x00, "Position");
        putEnum("effectType", 0x01, "Rotation");
        putEnum("effectType", 0x02, "RenderParameter");
        putEnum("effectType", 0x03, "Texture");
        putEnum("effectType", 0x05, "PositionAndRotation");
        putEnum("effectType", -1, "All");

        putEnum("stdmotion", -1, null, "MOT_ALL");
        putEnum("stdmotion", 0, null, "MOT_NONE");
        putEnum("stdmotion", 255, null, "MOT_ALL");

        putEnum("map", 0x0000, "system");
        putEnum("map", 0x0002, "test00");
        putEnum("map", 0x0005, "test10");
        putEnum("map", 0x0006, "test11");
        putMaps(0x000A, "znkd", 14);
        putMaps(0x001E, "bjyt", 12);
        putMaps(0x0032, "cdsp", 8);
        putMaps(0x0041, "bsil", 7);
        putMaps(0x005F, "slik", 11);
        putMaps(0x0083, "klyt", 12);
        putMaps(0x00A5, "lchb", 18);
        putMaps(0x00D2, "mihn", 8);
        putMaps(0x00DC, "kino", 9);
        putMaps(0x00F5, "genk", 16);
        putMaps(0x012C, "kami", 4);
        putMaps(0x0136, "mcfr", 14);
        putMaps(0x014A, "maca", 5);
        putMaps(0x0154, "mcyt", 7);
        putMaps(0x015E, "bika", 4);
        putMaps(0x0168, "azit", 7);
        putMaps(0x017C, "hiku", 21);
        putMaps(0x0195, "stbv", 1);
        putMaps(0x019A, "bvyt", 13);
        putMaps(0x01A9, "nagi", 7);
        putMaps(0x01BD, "lmyt", 2);
        putMaps(0x01E5, "mtgz", 11);
        putMaps(0x01F4, "zkrn", 6);
        putMaps(0x0203, "dome", 7);
        putMaps(0x0235, "ssbt", 4);
        putMaps(0x0244, "sins", 9);
        putMaps(0x024E, "omeg", 1);
        putMaps(0x0259, "zzzz", 3);
        putMaps(0x0262, "tori", 3);

        putEnum("sfx", 0x80000005, "?NewGameChime");
        putEnum("sfx", 0x80000009, "TreasureChestOpening");
        putEnum("sfx", 0x8000000E, "GilPaid");
        putEnum("sfx", 0x80000010, "PuzzlePrompt");
        putEnum("sfx", 0x80000012, "TakeSphereP1");
        putEnum("sfx", 0x80000013, "InsertSphere");
        putEnum("sfx", 0x80000014, "TakeSphereP2");
        putEnum("sfx", 0x80000016, "PuzzleSolvedChime");
        putEnum("sfx", 0x80000017, "LevelGainedChime");
        putEnum("sfx", 0x80000026, "TreasureObtainedChime");
        putEnum("sfx", 0x80000030, "AlBhedPrimerObtained");
        putEnum("sfx", 0x80000032, "SaveSphere");
        putEnum("sfx", 0x8000003D, "?ShineCelestialMirror");
        putEnum("sfx", 0x80000047, "AirshipCursorMove");
        putEnum("sfx", 0x80000048, "BoardAirship");
        putEnum("sfx", 0x8000004A, "InstructionsChime");
        putEnum("sfx", 0x00014C19, "BelgemineHealAeons");
        putEnum("sfx", 0x00015403, "LightningBoltHit");
        putEnum("sfx", 0x00015C01, "DestructionSphereChestSealUnlock");
        putEnum("sfx", 0x00016411, "SphereGridNodeActivation");

        putEnum("bgm", 0x000A, "Unwavering Determination");
        putEnum("bgm", 0x000B, "Secret Maneuvers");
        putEnum("bgm", 0x000C, "Boss Theme");
        putEnum("bgm", 0x000D, "The Summoning");
        putEnum("bgm", 0x000E, "Macalania Woods");
        putEnum("bgm", 0x0010, "?Battle Theme");
        putEnum("bgm", 0x001A, "Seymour's Ambition");
        putEnum("bgm", 0x001B, "Blitz Off!");
        putEnum("bgm", 0x001D, "Thunder Plains");
        putEnum("bgm", 0x001F, "?Underwater Ruins");
        putEnum("bgm", 0x0025, "People of the North Pole");
        putEnum("bgm", 0x0026, "Brass de Chocobo");
        putEnum("bgm", 0x0029, "Truth Revealed");
        putEnum("bgm", 0x002A, "Seymour Battle");
        putEnum("bgm", 0x002B, "Pursuit");
        putEnum("bgm", 0x002C, "?Eerie");
        putEnum("bgm", 0x0030, "Prelude");
        putEnum("bgm", 0x0031, "Otherworld (BFA)");
        putEnum("bgm", 0x0032, "Decisive Battle");
        putEnum("bgm", 0x0082, "To Zanarkand");
        putEnum("bgm", 0x0086, "?Yuna's Decision");
        putEnum("bgm", 0x008A, "?Victory Fanfare");
        putEnum("bgm", 0x008D, "?A Fleeting Dream");
        putEnum("bgm", 0x0088, "?Yuna's Theme");
        putEnum("bgm", 0x008F, "Good Night");
        putEnum("bgm", 0x0091, "Challenge");
        putEnum("bgm", 0x00A5, "Run!!");
        putEnum("bgm", 0x00AB, "Lulu's Theme");
        putEnum("bgm", 0x00B0, "A Contest of Aeons");

        putEnum("battleTransition", 0x00, "Screen Shatter");
        putEnum("battleTransition", 0x01, "Fade");

        putEnum("shadowMode", 0x00, null, "shadow_mode_none");
        putEnum("shadowMode", 0x01, null, "shadow_mode_easy");
        putEnum("shadowMode", 0x02, null, "shadow_mode_drop");

        putEnum("reverbeStatus", 0x00, null, "reverbe_stat_off");
        putEnum("reverbeStatus", 0x01, null, "reverbe_stat_on");
        putEnum("reverbeStatus", 0x02, null, "reverbe_stat_script");

        putEnum("specialBattleSetting", 0x01, "Sin Fin");
        putEnum("specialBattleSetting", 0x02, "Sin Arm");
        putEnum("specialBattleSetting", 0x03, "Evrae");

        putEnum("battleEndType", 0x01, "Defeat");
        putEnum("battleEndType", 0x02, "Victory");
        putEnum("battleEndType", 0x03, "PlayerEscaped");
        putEnum("battleEndType", 0x04, "MonsterEscaped");

        putEnum("appearMotion", 0x00, "No Animation", "appear_motion_off");
        putEnum("appearMotion", 0x01, null, "appear_motion_off_trans");
        putEnum("appearMotion", 0x02, null, "appear_motion_on");
        putEnum("appearMotion", 0x03, null, "appear_motion_on_trans");
        putEnum("appearMotion", 0x04, "Hidden", "appear_motion_off_hide");

        putEnum("btlCharVoice", 0x00, "Damage");
        putEnum("btlCharVoice", 0x01, "Attack");
        putEnum("btlCharVoice", 0x02, "Finisher1");
        putEnum("btlCharVoice", 0x03, "Finisher2");
        putEnum("btlCharVoice", 0x04, "Finisher3");
        putEnum("btlCharVoice", 0x05, "Frustrated");
        putEnum("btlCharVoice", 0x06, "BigSuccess");
        putEnum("btlCharVoice", 0x07, "BigDamage");

        putEnum("btlSoundEffect", 0x00, null, "btl_se_critical_hit");
        putEnum("btlSoundEffect", 0x01, null, "btl_se_normal_hit");
        putEnum("btlSoundEffect", 0x02, null, "btl_se_weak_hit");
        putEnum("btlSoundEffect", 0x03, null, "btl_se_not_hit");
        putEnum("btlSoundEffect", 0x04, null, "btl_se_add_attack");
        putEnum("btlSoundEffect", 0x05, null, "btl_se_start_attack");
        putEnum("btlSoundEffect", 0x06, null, "btl_se_foot");
        putEnum("btlSoundEffect", 0x07, null, "btl_se_jump");
        putEnum("btlSoundEffect", 0x08, null, "btl_se_land");
        putEnum("btlSoundEffect", 0x09, null, "btl_se_death");
        putEnum("btlSoundEffect", 0x0A, null, "btl_se_appear");
        putEnum("btlSoundEffect", 0x0B, null, "btl_se_normal_damage");
        putEnum("btlSoundEffect", 0x0C, null, "btl_se_critical_damage");
        putEnum("btlSoundEffect", 0x0D, null, "btl_se_weak_damage");
        putEnum("btlSoundEffect", 0x0E, null, "btl_se_chant_magic");
        putEnum("btlSoundEffect", 0x0F, null, "btl_se_chant_sp0");
        putEnum("btlSoundEffect", 0x10, null, "btl_se_chant_sp1");
        putEnum("btlSoundEffect", 0x11, null, "btl_se_chant_sp2");
        putEnum("btlSoundEffect", 0x12, null, "btl_se_chant_sp3");
        putEnum("btlSoundEffect", 0x13, null, "btl_se_sp0");
        putEnum("btlSoundEffect", 0x14, null, "btl_se_sp1");
        putEnum("btlSoundEffect", 0x15, null, "btl_se_sp2");
        putEnum("btlSoundEffect", 0x16, null, "btl_se_sp3");
        putEnum("btlSoundEffect", 0x17, null, "btl_se_sp4");
        putEnum("btlSoundEffect", 0x18, null, "btl_se_sp5");
        putEnum("btlSoundEffect", 0x19, null, "btl_se_sp6");
        putEnum("btlSoundEffect", 0x1A, null, "btl_se_chant_summon");
        putEnum("btlSoundEffect", 0x1B, null, "btl_se_fall_down");
        putEnum("btlSoundEffect", 0x1C, null, "btl_se_win");
        putEnum("btlSoundEffect", 0x1D, null, "btl_se_item");
        putEnum("btlSoundEffect", 0x1E, null, "btl_se_item_search");
        putEnum("btlSoundEffect", 0x1F, null, "btl_se_not_hit_near");
        putEnum("btlSoundEffect", 0x20, null, "btl_se_start_attack_miss");

        putEnum("damageFormula", 0x00, "None");
        putEnum("damageFormula", 0x01, "STR vs DEF", "calc_physic");
        putEnum("damageFormula", 0x02, "STR (ignore DEF)", "calc_ig_physic");
        putEnum("damageFormula", 0x03, "MAG vs MDF", "calc_magic");
        putEnum("damageFormula", 0x04, "MAG (ignore MDF)", "calc_ig_magic");
        putEnum("damageFormula", 0x05, "Current/16", "calc_ratio");
        putEnum("damageFormula", 0x06, "Fixed x50", "calc_fix");
        putEnum("damageFormula", 0x07, "Healing", "calc_rc_magic");
        putEnum("damageFormula", 0x08, "Max/16");
        putEnum("damageFormula", 0x09, "Fixed x46~53");
        putEnum("damageFormula", 0x0D, "Ticks/16");
        putEnum("damageFormula", 0x0F, "Special MAG (ignore MDF)");
        putEnum("damageFormula", 0x10, "Fixed x User MaxHP / 10");
        putEnum("damageFormula", 0x11, "Celestial HP-based");
        putEnum("damageFormula", 0x12, "Celestial MP-based");
        putEnum("damageFormula", 0x13, "Celestial Auron");
        putEnum("damageFormula", 0x15, "Fixed x Gil chosen / 10");
        putEnum("damageFormula", 0x16, "Fixed xKills");
        putEnum("damageFormula", 0x17, "Fixed x9999");

        putEnum("overdriveMode", 0x00, "Warrior", "limit_type_toushi");
        putEnum("overdriveMode", 0x01, "Comrade", "limit_type_fundo");
        putEnum("overdriveMode", 0x02, "Stoic", "limit_type_kuniku");
        putEnum("overdriveMode", 0x03, "Healer", "limit_type_jiai");
        putEnum("overdriveMode", 0x04, "Tactician", "limit_type_sakuryaku");
        putEnum("overdriveMode", 0x05, "?Victim", "limit_type_kyuuti");
        putEnum("overdriveMode", 0x06, "Dancer", "limit_type_karei");
        putEnum("overdriveMode", 0x07, "Avenger", "limit_type_hiai");
        putEnum("overdriveMode", 0x08, "Slayer", "limit_type_gaika");
        putEnum("overdriveMode", 0x09, "Hero", "limit_type_eiyuu");
        putEnum("overdriveMode", 0x0A, "Rook", "limit_type_banjyaku");
        putEnum("overdriveMode", 0x0B, "Victor", "limit_type_syouri");
        putEnum("overdriveMode", 0x0C, "Coward", "limit_type_tijyoku");
        putEnum("overdriveMode", 0x0D, "Ally", "limit_type_taiji");
        putEnum("overdriveMode", 0x0E, "?Sufferer", "limit_type_kugyou");
        putEnum("overdriveMode", 0x0F, "Daredevil", "limit_type_kiki");
        putEnum("overdriveMode", 0x10, "Loner", "limit_type_kokou");
        putEnum("overdriveMode", 0x13, "Aeons Only", "limit_type_syoukan");

        putEnum("damageType", 0x00, "Special", "atc_type_not");
        putEnum("damageType", 0x01, "Physical", "atc_type_physic");
        putEnum("damageType", 0x02, "Magical", "atc_type_magic");

        putEnum("cmdSubmenu", 0x00, "Normal Menu");
        putEnum("cmdSubmenu", 0x01, "Black Magic");
        putEnum("cmdSubmenu", 0x02, "White Magic");
        putEnum("cmdSubmenu", 0x03, "Skill");
        putEnum("cmdSubmenu", 0x04, "Overdrive");
        putEnum("cmdSubmenu", 0x05, "Summon");
        putEnum("cmdSubmenu", 0x06, "Items");
        putEnum("cmdSubmenu", 0x07, "Weapon Change");
        putEnum("cmdSubmenu", 0x08, "Escape");
        putEnum("cmdSubmenu", 0x0A, "Switch Character");
        putEnum("cmdSubmenu", 0x0C, "Left Menu");
        putEnum("cmdSubmenu", 0x0D, "Right Menu");
        putEnum("cmdSubmenu", 0x0E, "Special");
        putEnum("cmdSubmenu", 0x0F, "Armor Change");
        putEnum("cmdSubmenu", 0x11, "Use");
        putEnum("cmdSubmenu", 0x14, "Mix");
        putEnum("cmdSubmenu", 0x15, "Gil (Bribe/SC)");
        putEnum("cmdSubmenu", 0x16, "Gil (Pay Yoji)");

        putEnum("sphereGrid", 0x00, "Original (JP/NTSC) Sphere Grid");
        putEnum("sphereGrid", 0x01, "Standard Sphere Grid");
        putEnum("sphereGrid", 0x02, "Expert Sphere Grid");

        putEnum("weakState", 0x00, "Normal", "chr_idle_normal");
        putEnum("weakState", 0x01, "Slightly Weak", "chr_idle_weak");
        putEnum("weakState", 0x02, "Very Weak", "chr_idle_dying");
        putEnum("weakState", 0x03, "Dead", "chr_idle_dead");

        putEnum("achievement", 0x00, "Completion of FFX", "ACH_COMPLETION_OF_FFX");
        putEnum("achievement", 0x02, "Teamwork!", "ACH_TEAMWORK_OF_FFX");
        putEnum("achievement", 0x07, "Show Off!", "ACH_SHOW_OFF");
        putEnum("achievement", 0x08, "Striker", "ACH_STRIKER");
        putEnum("achievement", 0x09, "Chocobo License", "ACH_CHOCOBO_LICENSE");
        putEnum("achievement", 0x0C, "Lightning Dancer", "ACH_LIGHTNING_DANCER");
        putEnum("achievement", 0x11, "Chocobo Rider", "ACH_CHOCOBO_RIDER");
        putEnum("achievement", 0x17, "Chocobo Master (Redirect)", "ACH_CHOCOBO_MASTER");
        putEnum("achievement", 0x19, "Blitzball Master (Redirect)", "ACH_BLITZBALL_MASTER");

        putEnum("monsterArenaUnlock", 0x01, "Area Conquest unlocked");
        putEnum("monsterArenaUnlock", 0x02, "Species Conquest unlocked");
        putEnum("monsterArenaUnlock", 0x03, "Original Creations unlocked");
        for (int i = 0x300; i < 0x323; i++) {
            putEnum("monsterArenaUnlock", i, "Creation #" + (i - 0x2FF) + " defeated");
        }

        for (int i = 0; i <= 0x0013; i++) {
            getEnumMap("btlChr").put(i, getEnumMap("playerChar").get(i));
        }
        for (int i = 0; i < 8; i++) {
            putEnum("btlChr", 0x14 + i, "Monster#" + StringHelper.formatHex2(i));
        }
        putEnum("btlChr", 0x00FF, "Actor:None");

        for (int i = 0x1000; i <= 0x1200; i++) {
            putEnum("btlChr", i, "Actors:MonsterType=m" + StringHelper.formatDec3(i & 0x0FFF));
        }
        putEnum("btlChr", -26, null, "CHR_OWN_TARGET0"); // 0xFFE6
        putEnum("btlChr", -25, null, "CHR_ALL_PLY3"); // 0xFFE7
        putEnum("btlChr", -24, null, "CHR_ALL_PLAYER2"); // 0xFFE8
        putEnum("btlChr", -23, "AllCharsAndAeons", "CHR_ALL_PLAYER"); // 0xFFE9
        putEnum("btlChr", -22, null, "CHR_PARENT"); // 0xFFEA
        putEnum("btlChr", -21, "AllChrs?", "CHR_ALL2"); // 0xFFEB
        putEnum("btlChr", -20, "AllAeons", "CHR_ALL_SUMMON"); // 0xFFEC
        putEnum("btlChr", -19, null, "CHR_ALL_PLY2"); // 0xFFED
        putEnum("btlChr", -18, null, "CHR_INPUT"); // 0xFFEE
        putEnum("btlChr", -17, "LastAttacker", "CHR_REACTION"); // 0xFFEF
        putEnum("btlChr", -16, "MatchingGroup", "CHR_OWN_TARGET"); // 0xFFF0
        putEnum("btlChr", -15, "AllMonsters", "CHR_ALL_MON"); // 0xFFF1
        putEnum("btlChr", -14, "FrontlineChars", "CHR_ALL_PLY"); // 0xFFF2
        putEnum("btlChr", -13, "Self", "CHR_OWN"); // 0xFFF3
        putEnum("btlChr", -12, "CharacterReserve#4", "CHR_PARTY7"); // 0xFFF4
        putEnum("btlChr", -11, "CharacterReserve#3", "CHR_PARTY6"); // 0xFFF5
        putEnum("btlChr", -10, "CharacterReserve#2", "CHR_PARTY5"); // 0xFFF6
        putEnum("btlChr", -9, "CharacterReserve#1", "CHR_PARTY4"); // 0xFFF7
        putEnum("btlChr", -8, "Character#3", "CHR_PARTY3"); // 0xFFF8
        putEnum("btlChr", -7, "Character#2", "CHR_PARTY2"); // 0xFFF9
        putEnum("btlChr", -6, "Character#1", "CHR_PARTY1"); // 0xFFFA
        putEnum("btlChr", -5, "AllActors", "CHR_ALL"); // 0xFFFB
        putEnum("btlChr", -4, "?TargetChrsImmediate", "CHR_TARGET_NOW"); // 0xFFFC
        putEnum("btlChr", -3, "TargetChrs", "CHR_TARGET"); // 0xFFFD
        putEnum("btlChr", -2, "ActiveChrs", "CHR_ACTIVE"); // 0xFFFE
        putEnum("btlChr", -1, "Actor:Null", "CHR_NOP"); // 0xFFFF

        for (int i = 0; i <= 0x0013; i++) {
            getEnumMap("ctbIconType").put(i, getEnumMap("playerChar").get(i));
        }
        putEnum("ctbIconType", 0x14, "Monster");
        putEnum("ctbIconType", 0x15, "Boss (not numbered)");
        putEnum("ctbIconType", 0x16, "Boss (numbered)");
        putEnum("ctbIconType", 0x17, "Cid");

        putEnum("magusTarget", 0x00, "AllMonsters", "command_target_enemy");
        putEnum("magusTarget", 0x01, "FrontlineChars", "command_target_friend");
        putEnum("magusTarget", 0x101, "FrontlineChars except self");
        for (int i = 0x0F; i <= 0x11; i++) {
            getEnumMap("magusTarget").put(i + 0x0200, getEnumMap("playerChar").get(i));
        }

        putEnum("invisWallState", 0x00, "Intangible");
        putEnum("invisWallState", 0x01, "Block All");
        putEnum("invisWallState", 0x02, "Block NPC Only");
        putEnum("invisWallState", 0x0E, "Block Player Only");

        putEnum("gravityMode", 0x00, "None");
        putEnum("gravityMode", 0x01, "Grounded");
        putEnum("gravityMode", 0x02, "Swimming");

        putEnum("motionType", 0x00, "Grounded");
        putEnum("motionType", 0x01, "Battle");
        putEnum("motionType", 0x02, "Swimming");

        putEnum("moveType", 0x01, "Walking");
        putEnum("moveType", 0x02, "Running");

        putMotionProperty(0x00, null, "motion_attack_start_dist");
        putMotionProperty(0x01, null, "motion_attack_offset");
        putMotionProperty(0x02, null, "motion_move_backjump_dist");
        putMotionProperty(0x03, null, "motion_run_speed");
        putMotionProperty(0x04, null, "motion_run_speed_return");
        putMotionProperty(0x05, null, "motion_run_speed_v0");
        putMotionProperty(0x06, null, "motion_run_speed_acc");
        putMotionProperty(0x07, null, "motion_weight");
        putMotionProperty(0x08, null, "motion_attack_height");
        putMotionProperty(0x09, null, "motion_width");

        putBattleActorProperty(0x0000, "HP", "int", "stat_hp");
        putBattleActorProperty(0x0001, "MP", "int", "stat_mp");
        putBattleActorProperty(0x0002, "maxHP", "int", "stat_maxhp");
        putBattleActorProperty(0x0003, "maxMP", "int", "stat_maxmp");
        putBattleActorProperty(0x0004, "isAlive", "bool", "stat_alive");
        putBattleActorProperty(0x0005, "StatusPoison", "bool", "stat_poison");
        putBattleActorProperty(0x0006, "StatusPetrify", "bool", "stat_stone");
        putBattleActorProperty(0x0007, "StatusZombie", "bool", "stat_zombie");
        putBattleActorProperty(0x0008, "WeakState", "weakState", "stat_weak");
        putBattleActorProperty(0x0009, "STR", "int", "stat_str");
        putBattleActorProperty(0x000A, "DEF", "int", "stat_vit");
        putBattleActorProperty(0x000B, "MAG", "int", "stat_mag");
        putBattleActorProperty(0x000C, "MDF", "int", "stat_spirit");
        putBattleActorProperty(0x000D, "AGI", "int", "stat_dex");
        putBattleActorProperty(0x000E, "LCK", "int", "stat_luck");
        putBattleActorProperty(0x000F, "EVA", "int", "stat_avoid");
        putBattleActorProperty(0x0010, "ACC", "int", "stat_hit");
        putBattleActorProperty(0x0011, "PoisonDamage%", "int", "stat_poison_per");
        putBattleActorProperty(0x0012, "OverdriveMode", "overdriveMode", "stat_limit_type");
        putBattleActorProperty(0x0013, "OverdriveCurrent", "int", "stat_limit_gauge");
        putBattleActorProperty(0x0014, "OverdriveMax", "int", "stat_limit_gauge_max");
        putBattleActorProperty(0x0015, "isOnFrontline", "bool", "stat_inbattle");
        putBattleActorProperty(0x0016, null, "bool", "stat_man");
        putBattleActorProperty(0x0017, null, "bool", "stat_woman");
        putBattleActorProperty(0x0018, null, "bool", "stat_summon");
        putBattleActorProperty(0x0019, null, "bool", "stat_monster");
        putBattleActorProperty(0x001A, null, "bool", "stat_fly");
        putBattleActorProperty(0x001B, "?willDieToAttack", "bool", "stat_will_die");
        putBattleActorProperty(0x001C, "Area", "int", "stat_area");
        putBattleActorProperty(0x001D, "Position", "int", "stat_pos");
        putBattleActorProperty(0x001E, "BattleDistance", "int", "stat_far");
        putBattleActorProperty(0x001F, "EnemyGroup", "int", "stat_group");
        putBattleActorProperty(0x0020, "Armored", "bool", "stat_sp_hard");
        putBattleActorProperty(0x0021, "?ImmuneToFractionalDmg", "bool", "stat_sp_ratio");
        putBattleActorProperty(0x0022, "?ImmuneToLife", "bool", "stat_sp_zombie");
        putBattleActorProperty(0x0023, "?ImmuneToSensor", "bool", "stat_sp_see");
        putBattleActorProperty(0x0024, null, "bool", "stat_sp_live");
        putBattleActorProperty(0x0025, "StatusPowerBreak", "bool", "stat_power_break");
        putBattleActorProperty(0x0026, "StatusMagicBreak", "bool", "stat_magic_break");
        putBattleActorProperty(0x0027, "StatusArmorBreak", "bool", "stat_armor_break");
        putBattleActorProperty(0x0028, "StatusMentalBreak", "bool", "stat_mental_break");
        putBattleActorProperty(0x0029, "StatusConfusion", "bool", "stat_confuse");
        putBattleActorProperty(0x002A, "StatusBerserk", "bool", "stat_berserk");
        putBattleActorProperty(0x002B, "StatusProvoke", "bool", "stat_prov");
        putBattleActorProperty(0x002C, "StatusThreaten", "bool", "stat_threat");
        putBattleActorProperty(0x002D, "StatusSleep", "int", "stat_sleep");
        putBattleActorProperty(0x002E, "StatusSilence", "int", "stat_silence");
        putBattleActorProperty(0x002F, "StatusDarkness", "int", "stat_dark");
        putBattleActorProperty(0x0030, "StatusShell", "int", "stat_shell");
        putBattleActorProperty(0x0031, "StatusProtect", "int", "stat_protess");
        putBattleActorProperty(0x0032, "StatusReflect", "int", "stat_reflect");
        putBattleActorProperty(0x0033, "StatusNulTide", "int", "stat_bawater");
        putBattleActorProperty(0x0034, "StatusNulBlaze", "int", "stat_bafire");
        putBattleActorProperty(0x0035, "StatusNulShock", "int", "stat_bathunder");
        putBattleActorProperty(0x0036, "StatusNulFrost", "int", "stat_bacold");
        putBattleActorProperty(0x0037, "StatusRegen", "int", "stat_regen");
        putBattleActorProperty(0x0038, "StatusHaste", "int", "stat_haste");
        putBattleActorProperty(0x0039, "StatusSlow", "int", "stat_slow");
        putBattleActorProperty(0x003A, "?Sensor", "bool", "ability_see");
        putBattleActorProperty(0x003B, "FirstStrike", "bool", "ability_lead");
        putBattleActorProperty(0x003C, "?Initiative", "bool", "ability_first");
        putBattleActorProperty(0x003D, "CounterAttack", "bool", "ability_counter");
        putBattleActorProperty(0x003E, "EvadeAndCounter", "bool", "ability_counter2");
        putBattleActorProperty(0x003F, null, "bool", "ability_dark");
        putBattleActorProperty(0x0040, null, "bool", "ability_ap2");
        putBattleActorProperty(0x0041, null, "bool", "ability_exp2");
        putBattleActorProperty(0x0042, "?MagicBooster", "bool", "ability_booster");
        putBattleActorProperty(0x0043, "?MagicCounter", "bool", "ability_magic_counter");
        putBattleActorProperty(0x0044, "?Alchemy", "bool", "ability_medicine");
        putBattleActorProperty(0x0045, "?Auto-Potion", "bool", "ability_auto_potion");
        putBattleActorProperty(0x0046, "?Auto-Med", "bool", "ability_auto_cureall");
        putBattleActorProperty(0x0047, "Auto-Phoenix", "bool", "ability_auto_phenix");
        putBattleActorProperty(0x0048, null, "bool", "ability_limitup");
        putBattleActorProperty(0x0049, null, "bool", "ability_dream");
        putBattleActorProperty(0x004A, null, "bool", "ability_pierce");
        putBattleActorProperty(0x004B, null, "bool", "ability_exchange");
        putBattleActorProperty(0x004C, null, "bool", "ability_hp_recover");
        putBattleActorProperty(0x004D, null, "bool", "ability_mp_recover");
        putBattleActorProperty(0x004E, null, "bool", "ability_nonencount");
        putBattleActorProperty(0x004F, "DeathAnimation", "deathAnimation", "stat_death_pattern");
        putBattleActorProperty(0x0050, null, "bool", "stat_event_chr");
        putBattleActorProperty(0x0051, "GetsTurns", "bool", "stat_action");
        putBattleActorProperty(0x0052, "Targetable", "bool", "stat_cursor");
        putBattleActorProperty(0x0053, "VisibleOnCTB", "bool", "stat_ctb_list");
        putBattleActorProperty(0x0054, null, "unknown", "stat_visible");
        putBattleActorProperty(0x0055, "AreaToMoveTo", "int", "stat_move_area");
        putBattleActorProperty(0x0056, "PositionToMoveTo", "int", "stat_move_pos");
        putBattleActorProperty(0x0057, null, "int", "stat_efflv");
        putBattleActorProperty(0x0058, null, "unknown", "stat_model");
        putBattleActorProperty(0x0059, "?Host", "btlChr", "stat_damage_chr");
        putBattleActorProperty(0x005A, null, "unknown", "stat_move_target");
        putBattleActorProperty(0x005B, "AnimationsVariant", "int", "stat_motionlv");
        putBattleActorProperty(0x005C, null, "unknown", "stat_nop");
        putBattleActorProperty(0x005D, null, "bool", "stat_move_flag");
        putBattleActorProperty(0x005E, null, "unknown", "stat_live_motion");
        putBattleActorProperty(0x005F, null, "unknown", "stat_adjust_pos");
        putBattleActorProperty(0x0060, null, "unknown", "stat_height_on");
        putBattleActorProperty(0x0061, null, "unknown", "stat_sleep_recover_flag");
        putBattleActorProperty(0x0062, "AbsorbFire", "bool", "stat_abs_fire");
        putBattleActorProperty(0x0063, "AbsorbIce", "bool", "stat_abs_cold");
        putBattleActorProperty(0x0064, "AbsorbThunder", "bool", "stat_abs_thunder");
        putBattleActorProperty(0x0065, "AbsorbWater", "bool", "stat_abs_water");
        putBattleActorProperty(0x0066, "AbsorbHoly", "bool", "stat_abs_holy");
        putBattleActorProperty(0x0067, "NullFire", "bool", "stat_inv_fire");
        putBattleActorProperty(0x0068, "NullIce", "bool", "stat_inv_cold");
        putBattleActorProperty(0x0069, "NullThunder", "bool", "stat_inv_thunder");
        putBattleActorProperty(0x006A, "NullWater", "bool", "stat_inv_water");
        putBattleActorProperty(0x006B, "NullHoly", "bool", "stat_inv_holy");
        putBattleActorProperty(0x006C, "ResistFire", "bool", "stat_half_fire");
        putBattleActorProperty(0x006D, "ResistIce", "bool", "stat_half_cold");
        putBattleActorProperty(0x006E, "ResistThunder", "bool", "stat_half_thunder");
        putBattleActorProperty(0x006F, "ResistWater", "bool", "stat_half_water");
        putBattleActorProperty(0x0070, "ResistHoly", "bool", "stat_half_holy");
        putBattleActorProperty(0x0071, "WeakFire", "bool", "stat_weak_fire");
        putBattleActorProperty(0x0072, "WeakIce", "bool", "stat_weak_cold");
        putBattleActorProperty(0x0073, "WeakThunder", "bool", "stat_weak_thunder");
        putBattleActorProperty(0x0074, "WeakWater", "bool", "stat_weak_water");
        putBattleActorProperty(0x0075, "WeakHoly", "bool", "stat_weak_holy");
        putBattleActorProperty(0x0076, null, "bool", "stat_adjust_pos_flag");
        putBattleActorProperty(0x0077, "DullHitReactionToPhys", "bool", "stat_inv_physic_motion");
        putBattleActorProperty(0x0078, "DullHitReactionToMag", "bool", "stat_inv_magic_motion");
        putBattleActorProperty(0x0079, "TimesStolenFrom", "int", "stat_steal_count");
        putBattleActorProperty(0x007A, null, "bool", "stat_wait_motion_flag");
        putBattleActorProperty(0x007B, null, "bool", "stat_attack_return_flag");
        putBattleActorProperty(0x007C, null, "unknown", "stat_attack_normal_frame");
        putBattleActorProperty(0x007D, "?Tough (No Delay recoil)", "bool", "stat_disable_move_flag");
        putBattleActorProperty(0x007E, "?Heavy (No lift off ground)", "bool", "stat_disable_jump_flag");
        putBattleActorProperty(0x007F, null, "bool", "stat_bodyhit_flag");
        putBattleActorProperty(0x0080, null, "unknown", "stat_effvar");
        putBattleActorProperty(0x0081, "StealItemCommonType", "command", "stat_item");
        putBattleActorProperty(0x0082, "StealItemCommonAmount", "int", "stat_item_num");
        putBattleActorProperty(0x0083, "StealItemRareType", "command", "stat_rareitem");
        putBattleActorProperty(0x0084, "StealItemRareAmount", "int", "stat_rareitem_num");
        putBattleActorProperty(0x0085, null, "unknown", "stat_magiclv");
        putBattleActorProperty(0x0086, "BirthAnimation", "appearMotion", "stat_appear_motion_flag");
        putBattleActorProperty(0x0087, null, "unknown", "stat_cursor_element");
        putBattleActorProperty(0x0088, null, "unknown", "stat_limit_bar_flag_cam");
        putBattleActorProperty(0x0089, "showOverdriveBar", "bool", "stat_limit_bar_flag");
        putBattleActorProperty(0x008A, "Item1DropChance", "int", "stat_drop1");
        putBattleActorProperty(0x008B, "Item2DropChance", "int", "stat_drop2");
        putBattleActorProperty(0x008C, "GearDropChance", "int", "stat_weapon_drop");
        putBattleActorProperty(0x008D, "StealChance", "int", "stat_steal");
        putBattleActorProperty(0x008E, "?MustBeKilledForBattleEnd", "bool", "stat_exist_flag");
        putBattleActorProperty(0x008F, "?StatusScan", "bool", "stat_live");
        putBattleActorProperty(0x0090, "StatusDistillPower", "bool", "stat_str_memory");
        putBattleActorProperty(0x0091, "?StatusDistillMana", "bool", "stat_mag_memory");
        putBattleActorProperty(0x0092, "?StatusDistillSpeed", "bool", "stat_dex_memory");
        putBattleActorProperty(0x0093, "StatusUnusedDash", "bool", "stat_move_memory");
        putBattleActorProperty(0x0094, "?StatusDistillAbility", "bool", "stat_ability_memory");
        putBattleActorProperty(0x0095, "StatusShield", "bool", "stat_dodge");
        putBattleActorProperty(0x0096, "StatusBoost", "bool", "stat_defend");
        putBattleActorProperty(0x0097, "StatusEject", "bool", "stat_blow");
        putBattleActorProperty(0x0098, "StatusAutoLife", "bool", "stat_relife");
        putBattleActorProperty(0x0099, "StatusCurse", "bool", "stat_curse");
        putBattleActorProperty(0x009A, "StatusDefend", "bool", "stat_defense");
        putBattleActorProperty(0x009B, "StatusGuard", "bool", "stat_protect");
        putBattleActorProperty(0x009C, "StatusSentinel", "bool", "stat_iron");
        putBattleActorProperty(0x009D, "StatusDoom", "bool", "stat_death_sentence");
        putBattleActorProperty(0x009E, null, "motionTypeBitfield", "stat_motion_type");
        putBattleActorProperty(0x009F, "DoomCounterInitial", "int", "stat_death_sentence_start");
        putBattleActorProperty(0x00A0, "DoomCounterCurrent", "int", "stat_death_sentence_count");
        putBattleActorProperty(0x00A1, null, "unknown", "stat_dmg_dir");
        putBattleActorProperty(0x00A2, null, "unknown", "stat_direction_change_flag");
        putBattleActorProperty(0x00A3, null, "unknown", "stat_direction_change_effect");
        putBattleActorProperty(0x00A4, null, "unknown", "stat_direction_fix_flag");
        putBattleActorProperty(0x00A5, null, "unknown", "stat_hit_terminate_flag");
        putBattleActorProperty(0x00A6, "LastDamageTakenHP", "int", "stat_damage_hp");
        putBattleActorProperty(0x00A7, "LastDamageTakenMP", "int", "stat_damage_mp");
        putBattleActorProperty(0x00A8, "LastDamageTakenCTB", "int", "stat_damage_ctb");
        putBattleActorProperty(0x00A9, null, "unknown", "stat_appear_invisible_flag");
        putBattleActorProperty(0x00AA, null, "unknown", "stat_effect_hit_num");
        putBattleActorProperty(0x00AB, null, "bool", "stat_avoid_flag");
        putBattleActorProperty(0x00AC, null, "unknown", "stat_blow_exist_flag");
        putBattleActorProperty(0x00AD, null, "unknown", "stat_escape_flag");
        putBattleActorProperty(0x00AE, "?Visible", "bool", "stat_hide");
        putBattleActorProperty(0x00AF, "StatusResistanceDeath", "int", "stat_def_death");
        putBattleActorProperty(0x00B0, "StatusResistanceZombie", "int", "stat_def_zombie");
        putBattleActorProperty(0x00B1, "StatusResistancePetrify", "int", "stat_def_stone");
        putBattleActorProperty(0x00B2, "StatusResistancePoison", "int", "stat_def_poison");
        putBattleActorProperty(0x00B3, "StatusResistancePowerBreak", "int", "stat_def_power_break");
        putBattleActorProperty(0x00B4, "StatusResistanceMagicBreak", "int", "stat_def_magic_break");
        putBattleActorProperty(0x00B5, "StatusResistanceArmorBreak", "int", "stat_def_armor_break");
        putBattleActorProperty(0x00B6, "StatusResistanceMentalBreak", "int", "stat_def_mental_break");
        putBattleActorProperty(0x00B7, "StatusResistanceConfusion", "int", "stat_def_confuse");
        putBattleActorProperty(0x00B8, "StatusResistanceBerserk", "int", "stat_def_berserk");
        putBattleActorProperty(0x00B9, "StatusResistanceProvoke", "int", "stat_def_prov");
        putBattleActorProperty(0x00BA, "StatusChanceThreaten", "int", "stat_def_threat");
        putBattleActorProperty(0x00BB, "StatusResistanceSleep", "int", "stat_def_sleep");
        putBattleActorProperty(0x00BC, "StatusResistanceSilence", "int", "stat_def_silence");
        putBattleActorProperty(0x00BD, "StatusResistanceDarkness", "int", "stat_def_dark");
        putBattleActorProperty(0x00BE, "StatusResistanceShell", "int", "stat_def_shell");
        putBattleActorProperty(0x00BF, "StatusResistanceProtect", "int", "stat_def_protess");
        putBattleActorProperty(0x00C0, "StatusResistanceReflect", "int", "stat_def_reflect");
        putBattleActorProperty(0x00C1, "StatusResistanceNulTide", "int", "stat_def_bawater");
        putBattleActorProperty(0x00C2, "StatusResistanceNulBlaze", "int", "stat_def_bafire");
        putBattleActorProperty(0x00C3, "StatusResistanceNulShock", "int", "stat_def_bathunder");
        putBattleActorProperty(0x00C4, "StatusResistanceNulFrost", "int", "stat_def_bacold");
        putBattleActorProperty(0x00C5, "StatusResistanceRegen", "int", "stat_def_regen");
        putBattleActorProperty(0x00C6, "StatusResistanceHaste", "int", "stat_def_haste");
        putBattleActorProperty(0x00C7, "StatusResistanceSlow", "int", "stat_def_slow");
        putBattleActorProperty(0x00C8, "StatusImmunityScan", "bool", "stat_def_live");
        putBattleActorProperty(0x00C9, "StatusImmunityDistillPower", "bool", "stat_def_str_memory");
        putBattleActorProperty(0x00CA, "StatusImmunityDistillMana", "bool", "stat_def_mag_memory");
        putBattleActorProperty(0x00CB, "StatusImmunityDistillSpeed", "bool", "stat_def_dex_memory");
        putBattleActorProperty(0x00CC, "StatusImmunityUnusedDash", "bool", "stat_def_move_memory");
        putBattleActorProperty(0x00CD, "StatusImmunityDistillAbility", "bool", "stat_def_ability_memory");
        putBattleActorProperty(0x00CE, "StatusImmunityShield", "bool", "stat_def_dodge");
        putBattleActorProperty(0x00CF, "StatusImmunityBoost", "bool", "stat_def_defend");
        putBattleActorProperty(0x00D0, "StatusImmunityAutoLife", "bool", "stat_def_relife");
        putBattleActorProperty(0x00D1, "StatusImmunityEject", "bool", "stat_def_blow");
        putBattleActorProperty(0x00D2, "StatusImmunityCurse", "bool", "stat_def_curse");
        putBattleActorProperty(0x00D3, "StatusImmunityDefend", "bool", "stat_def_defense");
        putBattleActorProperty(0x00D4, "StatusImmunityGuard", "bool", "stat_def_protect");
        putBattleActorProperty(0x00D5, "StatusImmunitySentinel", "bool", "stat_def_iron");
        putBattleActorProperty(0x00D6, "StatusImmunityDoom", "bool", "stat_def_death_sentence");
        putBattleActorProperty(0x00D7, "VisibleOnFrontlinePartyList", "bool", "stat_hp_list");
        putBattleActorProperty(0x00D8, null, "unknown", "stat_visible_cam");
        putBattleActorProperty(0x00D9, null, "unknown", "stat_visible_out");
        putBattleActorProperty(0x00DA, null, "unknown", "stat_round");
        putBattleActorProperty(0x00DB, null, "unknown", "stat_round_return");
        putBattleActorProperty(0x00DC, null, "unknown", "stat_win_pose");
        putBattleActorProperty(0x00DD, "notDeadPetrifiedOrLowHp", "unknown", "stat_vigor", true);
        putBattleActorProperty(0x00DE, null, "unknown", "stat_fast_model_flag");
        putBattleActorProperty(0x00DF, "notDeadOrPetrified", "unknown", "stat_alive_not_stone", true);
        putBattleActorProperty(0x00E0, null, "unknown", "stat_command_type");
        putBattleActorProperty(0x00E1, null, "unknown", "stat_effect_target_flag");
        putBattleActorProperty(0x00E2, null, "unknown", "stat_magic_effect_ground");
        putBattleActorProperty(0x00E3, null, "unknown", "stat_magic_effect_water");
        putBattleActorProperty(0x00E4, null, "unknown", "stat_idle2_prob");
        putBattleActorProperty(0x00E5, null, "unknown", "stat_attack_motion_type");
        putBattleActorProperty(0x00E6, null, "unknown", "stat_attack_inc_speed");
        putBattleActorProperty(0x00E7, null, "unknown", "stat_attack_dec_speed");
        putBattleActorProperty(0x00E8, "CurrentTurnDelay", "int", "stat_ctb");
        putBattleActorProperty(0x00E9, null, "unknown", "stat_appear_count");
        putBattleActorProperty(0x00EA, null, "unknown", "stat_motion_num");
        putBattleActorProperty(0x00EB, null, "unknown", "stat_info_mes_id");
        putBattleActorProperty(0x00EC, null, "unknown", "stat_live_mes_id");
        putBattleActorProperty(0x00ED, null, "unknown", "stat_visible_eff");
        putBattleActorProperty(0x00EE, null, "unknown", "stat_motion_dispose_flag");
        putBattleActorProperty(0x00EF, null, "unknown", "stat_model_dispose_flag");
        putBattleActorProperty(0x00F0, "ImmunityDelay", "unknown", "stat_def_ctb");
        putBattleActorProperty(0x00F1, null, "unknown", "stat_shadow");
        putBattleActorProperty(0x00F2, null, "unknown", "stat_death");
        putBattleActorProperty(0x00F3, null, "unknown", "stat_death_stone");
        putBattleActorProperty(0x00F4, null, "unknown", "stat_check_pos");
        putBattleActorProperty(0x00F5, null, "unknown", "stat_win_se");
        putBattleActorProperty(0x00F6, null, "unknown", "stat_attack_num");
        putBattleActorProperty(0x00F7, null, "unknown", "stat_near_motion");
        putBattleActorProperty(0x00F8, "presentWithoutDthPtfSlpSil", "unknown", "stat_talk_stat1", true);
        putBattleActorProperty(0x00F9, "presentWithoutDthPtfSlpSilCnfBsk", "unknown", "stat_talk_stat2", true);
        putBattleActorProperty(0x00FA, "?ForceCloseRangeAttackAnim", "bool", "stat_near_motion_set");
        putBattleActorProperty(0x00FB, null, "unknown", "stat_motion_speed_normal");
        putBattleActorProperty(0x00FC, null, "unknown", "stat_motion_speed_normal_start");
        putBattleActorProperty(0x00FD, null, "unknown", "stat_own_attack_near");
        putBattleActorProperty(0x00FE, "presentWithoutDthPtfSlpSilCnfBskYellowHp", "unknown", "stat_talk_stat3", true);
        putBattleActorProperty(0x00FF, null, "unknown", "stat_command_set");
        putBattleActorProperty(0x0100, "RetainsControlWhenProvoked", "bool", "stat_prov_command_flag");
        putBattleActorProperty(0x0101, "ProvokerActor", "btlChr", "stat_prov_chr");
        putBattleActorProperty(0x0102, "MP0", "bool", "stat_use_mp0");
        putBattleActorProperty(0x0103, "CTBIconNumber", "int", "stat_icon_number");
        putBattleActorProperty(0x0104, null, "unknown", "stat_sound_hit_num");
        putBattleActorProperty(0x0105, null, "unknown", "stat_damage_num_pos");
        putBattleActorProperty(0x0106, null, "unknown", "stat_summoner");
        putBattleActorProperty(0x0107, "NullDamage", "bool", "stat_sp_invincible");
        putBattleActorProperty(0x0108, "NullMagic", "bool", "stat_sp_inv_magic");
        putBattleActorProperty(0x0109, "NullPhysical", "bool", "stat_sp_inv_physic");
        putBattleActorProperty(0x010A, "LearnableRonsoRage", "command", "stat_blue_magic");
        putBattleActorProperty(0x010B, "NullSlice", "unknown", "stat_sp_disable_zan");
        putBattleActorProperty(0x010C, "OverkillThreshold", "int", "stat_over_kill_hp");
        putBattleActorProperty(0x010D, null, "unknown", "stat_return_motion_type");
        putBattleActorProperty(0x010E, null, "unknown", "stat_cam_width");
        putBattleActorProperty(0x010F, null, "unknown", "stat_cam_height");
        putBattleActorProperty(0x0110, null, "unknown", "stat_height");
        putBattleActorProperty(0x0111, "YojimboCompatibility", "int", "stat_youjinbo");
        putBattleActorProperty(0x0112, "YojimboGivenGil", "int", "stat_payment");
        putBattleActorProperty(0x0113, "HighestZanmatoLevel", "int", "stat_monster_value_max");
        putBattleActorProperty(0x0114, "TurnsTaken", "int", "stat_command_exe_count");
        putBattleActorProperty(0x0115, "YojimboReaction", "yojimboReaction", "stat_consent");
        putBattleActorProperty(0x0116, null, "unknown", "stat_attack_near_frame");
        putBattleActorProperty(0x0117, "MagusSisterMotivation", "int", "stat_energy");
        putBattleActorProperty(0x0118, null, "unknown", "stat_limit_gauge_add");
        putBattleActorProperty(0x0119, "NearDeath", "bool", "stat_hp_half");
        putBattleActorProperty(0x011A, "?OverdriveAvailable", "int", "stat_limit_gauge_check");
        putBattleActorProperty(0x011B, "HpFull", "unknown", "stat_hp_check");
        putBattleActorProperty(0x011C, "MpFull", "unknown", "stat_mp_check");
        putBattleActorProperty(0x011D, "HasAllNuls", "unknown", "stat_ba_all_check");
        putBattleActorProperty(0x011E, "HasShellOrReflect", "unknown", "stat_shell_reflect");
        putBattleActorProperty(0x011F, "HasProtectOrReflect", "unknown", "stat_protess_reflect");
        putBattleActorProperty(0x0120, "HasHasteOrReflect", "unknown", "stat_haste_reflect");
        putBattleActorProperty(0x0121, null, "unknown", "stat_weak_motion");
        putBattleActorProperty(0x0122, "?BribeImmunity", "unknown", "stat_sp_wairo");
        putBattleActorProperty(0x0123, null, "unknown", "stat_attack_motion_frame");
        putBattleActorProperty(0x0124, null, "unknown", "stat_motion_type_reset");
        putBattleActorProperty(0x0125, null, "unknown", "stat_motion_type_add");
        putBattleActorProperty(0x0126, null, "unknown", "stat_death_status");
        putBattleActorProperty(0x0127, null, "unknown", "stat_target_list");
        putBattleActorProperty(0x0128, null, "unknown", "stat_limit_bar_pos");
        putBattleActorProperty(0x0129, null, "unknown", "stat_center_chr_flag");
        putBattleActorProperty(0x012A, null, "unknown", "stat_same_target_check");
        putBattleActorProperty(0x012B, "APRewardNormal", "int", "stat_get_ap");
        putBattleActorProperty(0x012C, "APRewardOverkill", "int", "stat_get_over_ap");
        putBattleActorProperty(0x012D, "GilReward", "int", "stat_get_gill");
        putBattleActorProperty(0x012E, "BonusSTR", "int", "stat_str_up");
        putBattleActorProperty(0x012F, "BonusDEF", "int", "stat_vit_up");
        putBattleActorProperty(0x0130, "BonusMAG", "int", "stat_mag_up");
        putBattleActorProperty(0x0131, "BonusMDF", "int", "stat_spirit_up");
        putBattleActorProperty(0x0132, "BonusAGI", "int", "stat_dex_up");
        putBattleActorProperty(0x0133, "BonusLCK", "int", "stat_luck_up");
        putBattleActorProperty(0x0134, "BonusEVA", "int", "stat_avoid_up");
        putBattleActorProperty(0x0135, "BonusACC", "int", "stat_hit_up");
        putBattleActorProperty(0x0136, null, "unknown", "stat_use_mp");
        putBattleActorProperty(0x0137, null, "unknown", "stat_use_limit");
        putBattleActorProperty(0x0138, null, "unknown", "stat_use_limit_all");
        putBattleActorProperty(0x0139, "isDoublecasting", "bool", "stat_continue_magic");
        putBattleActorProperty(0x013A, "Item1CommonType", "command", "stat_item1_com");
        putBattleActorProperty(0x013B, "Item1RareType", "command", "stat_item1_rare");
        putBattleActorProperty(0x013C, "Item2CommonType", "command", "stat_item2_com");
        putBattleActorProperty(0x013D, "Item2RareType", "command", "stat_item2_rare");
        putBattleActorProperty(0x013E, "Item1CommonTypeOverkill", "command", "stat_item1_com_over_kill");
        putBattleActorProperty(0x013F, "Item1RareTypeOverkill", "command", "stat_item1_rare_over_kill");
        putBattleActorProperty(0x0140, "Item2CommonTypeOverkill", "command", "stat_item2_com_over_kill");
        putBattleActorProperty(0x0141, "Item2RareTypeOverkill", "command", "stat_item2_rare_over_kill");
        putBattleActorProperty(0x0142, "Item1CommonAmount", "int", "stat_item1_com_num");
        putBattleActorProperty(0x0143, "Item1RareAmount", "int", "stat_item1_rare_num");
        putBattleActorProperty(0x0144, "Item2CommonAmount", "int", "stat_item2_com_num");
        putBattleActorProperty(0x0145, "Item2RareAmount", "int", "stat_item2_rare_num");
        putBattleActorProperty(0x0146, "Item1CommonAmountOverkill", "int", "stat_item1_com_over_kill_num");
        putBattleActorProperty(0x0147, "Item1RareAmountOverkill", "int", "stat_item1_rare_over_kill_num");
        putBattleActorProperty(0x0148, "Item2CommonAmountOverkill", "int", "stat_item2_com_over_kill_num");
        putBattleActorProperty(0x0149, "Item2RareAmountOverkill", "int", "stat_item2_rare_over_kill_num");
        putBattleActorProperty(0x014A, "ReturnsBeforeDeathAnimation", "bool", "stat_death_return");
        putBattleActorProperty(0x014B, null, "unknown", "stat_linear_move_reset");
        putBattleActorProperty(0x014C, null, "unknown", "stat_bodyhit_direct");
        putBattleActorProperty(0x014D, "?recruited (Aeon)", "bool", "stat_join");
        putBattleActorProperty(0x014E, "PermanentAutoLife", "bool", "stat_eternal_relife");
        putBattleActorProperty(0x014F, null, "unknown", "stat_neck_target_flag");
        putBattleActorProperty(0x0150, null, "unknown", "stat_visible_out_on");
        putBattleActorProperty(0x0151, null, "unknown", "stat_regen_damage_flag");
        putBattleActorProperty(0x0152, null, "unknown", "stat_num_print_element");
        putBattleActorProperty(0x0153, "?disableLowHealthSlump", "bool", null);
        // 0x0154 is missing
        putBattleActorProperty(0x0155, "?inBattleNotPetrifiedOrEjected", "bool", null);
        putBattleActorProperty(0x0156, "?wasCaptured", "bool", null);
        // 0x0157 is missing
        // 0x0158 is missing
        putBattleActorProperty(0x0159, "?onlyTargetableBy", "command", null);

        putCommandProperty(0x0000, "damageFormula", "damageFormula");
        putCommandProperty(0x0001, "damageType", "damageType");
        putCommandProperty(0x0002, "affectHP", "bool");
        putCommandProperty(0x0003, "affectMP", "bool");
        putCommandProperty(0x0004, "affectCTB", "bool");
        putCommandProperty(0x0005, "elementHoly", "bool");
        putCommandProperty(0x0006, "elementWater", "bool");
        putCommandProperty(0x0007, "elementThunder", "bool");
        putCommandProperty(0x0008, "elementIce", "bool");
        putCommandProperty(0x0009, "elementFire", "bool");
        putCommandProperty(0x000A, "targetType", "targetType");
    }

    public Map<Integer, ScriptField> getEnumMap(String type) {
        return ENUMERATIONS.computeIfAbsent(type, (t) -> new HashMap<>());
    }

    public Map<String, List<OpcodeChoice>> getOpcodeChoices() {
        Map<String, List<OpcodeChoice>> map = new HashMap<>();
        List<OpcodeChoice> lineEnderOpcodeChoices = new ArrayList<>();
        lineEnderOpcodeChoices.add(new OpcodeChoice(0));
        for (int lineEnderOpcode : ScriptConstants.OPCODE_ENDLINE) {
            lineEnderOpcodeChoices.add(new OpcodeChoice(lineEnderOpcode));
        }
        map.put("void", lineEnderOpcodeChoices);
        for (Map.Entry<Integer, ScriptField> entry : ScriptConstants.COMP_OPERATORS.entrySet()) {
            List<OpcodeChoice> list = map.computeIfAbsent(entry.getValue().type, k -> new ArrayList<>());
            list.add(new OpcodeChoice(entry.getKey()));
        }
        /*
        OPCODE_LABELS[0x19] = "OPNOT";
        OPCODE_LABELS[0x1A] = "OPUMINUS";
        OPCODE_LABELS[0x1B] = "OPFIXADRS";
        OPCODE_LABELS[0x1C] = "OPBNOT";
        OPCODE_LABELS[0x1D] = "LABEL";
        OPCODE_LABELS[0x1E] = "TAG";
        OPCODE_LABELS[0x1F] = "PUSHV";
        OPCODE_LABELS[0x20] = "POPV";
        OPCODE_LABELS[0x21] = "POPVL";
        OPCODE_LABELS[0x22] = "PUSHAR";
        OPCODE_LABELS[0x23] = "POPAR";
        OPCODE_LABELS[0x24] = "POPARL";
        OPCODE_LABELS[0x25] = "POPA";
        OPCODE_LABELS[0x26] = "PUSHA";
        OPCODE_LABELS[0x27] = "PUSHARP";
        OPCODE_LABELS[0x28] = "PUSHX";
        OPCODE_LABELS[0x29] = "PUSHY";
        OPCODE_LABELS[0x2A] = "POPX";
        OPCODE_LABELS[0x2B] = "REPUSH";
        OPCODE_LABELS[0x2C] = "POPY";
        OPCODE_LABELS[0x2D] = "PUSHI";
        OPCODE_LABELS[0x2E] = "PUSHII";
        OPCODE_LABELS[0x2F] = "PUSHF";
        OPCODE_LABELS[0x30] = "JMP";
        OPCODE_LABELS[0x31] = "CJMP";
        OPCODE_LABELS[0x32] = "NCJMP";
        OPCODE_LABELS[0x33] = "JSR";
        OPCODE_LABELS[0x34] = "RTS";
        OPCODE_LABELS[0x35] = "CALL";
        OPCODE_LABELS[0x36] = "REQ";
        OPCODE_LABELS[0x37] = "REQSW";
        OPCODE_LABELS[0x38] = "REQEW";
        OPCODE_LABELS[0x39] = "PREQ";
        OPCODE_LABELS[0x3A] = "PREQSW";
        OPCODE_LABELS[0x3B] = "PREQEW";
        OPCODE_LABELS[0x3C] = "RET";
        OPCODE_LABELS[0x3D] = "RETN";
        OPCODE_LABELS[0x3E] = "RETT";
        OPCODE_LABELS[0x3F] = "RETTN";
        OPCODE_LABELS[0x40] = "HALT";
        OPCODE_LABELS[0x41] = "PUSHN";
        OPCODE_LABELS[0x42] = "PUSHT";
        OPCODE_LABELS[0x43] = "PUSHVP";
        OPCODE_LABELS[0x44] = "PUSHFIX";
        OPCODE_LABELS[0x45] = "FREQ";
        OPCODE_LABELS[0x46] = "TREQ";
        OPCODE_LABELS[0x47] = "BREQ";
        OPCODE_LABELS[0x48] = "BFREQ";
        OPCODE_LABELS[0x49] = "BTREQ";
        OPCODE_LABELS[0x4A] = "FREQSW";
        OPCODE_LABELS[0x4B] = "TREQSW";
        OPCODE_LABELS[0x4C] = "BREQSW";
        OPCODE_LABELS[0x4D] = "BFREQSW";
        OPCODE_LABELS[0x4E] = "BTREQSW";
        OPCODE_LABELS[0x4F] = "FREQEW";
        OPCODE_LABELS[0x50] = "TREQEW";
        OPCODE_LABELS[0x51] = "BREQEW";
        OPCODE_LABELS[0x52] = "BFREQEW";
        OPCODE_LABELS[0x53] = "BTREQEW";
        OPCODE_LABELS[0x54] = "DRET";
        OPCODE_LABELS[0x55] = "POPXJMP";
        OPCODE_LABELS[0x56] = "POPXCJMP";
        OPCODE_LABELS[0x57] = "POPXNCJMP";
        OPCODE_LABELS[0x58] = "CALLPOPA";
        OPCODE_LABELS[0x59] = "POPI0";
        OPCODE_LABELS[0x5A] = "POPI1";
        OPCODE_LABELS[0x5B] = "POPI2";
        OPCODE_LABELS[0x5C] = "POPI3";
        OPCODE_LABELS[0x5D] = "POPF0";
        OPCODE_LABELS[0x5E] = "POPF1";
        OPCODE_LABELS[0x5F] = "POPF2";
        OPCODE_LABELS[0x60] = "POPF3";
        OPCODE_LABELS[0x61] = "POPF4";
        OPCODE_LABELS[0x62] = "POPF5";
        OPCODE_LABELS[0x63] = "POPF6";
        OPCODE_LABELS[0x64] = "POPF7";
        OPCODE_LABELS[0x65] = "POPF8";
        OPCODE_LABELS[0x66] = "POPF9";
        OPCODE_LABELS[0x67] = "PUSHI0";
        OPCODE_LABELS[0x68] = "PUSHI1";
        OPCODE_LABELS[0x69] = "PUSHI2";
        OPCODE_LABELS[0x6A] = "PUSHI3";
        OPCODE_LABELS[0x6B] = "PUSHF0";
        OPCODE_LABELS[0x6C] = "PUSHF1";
        OPCODE_LABELS[0x6D] = "PUSHF2";
        OPCODE_LABELS[0x6E] = "PUSHF3";
        OPCODE_LABELS[0x6F] = "PUSHF4";
        OPCODE_LABELS[0x70] = "PUSHF5";
        OPCODE_LABELS[0x71] = "PUSHF6";
        OPCODE_LABELS[0x72] = "PUSHF7";
        OPCODE_LABELS[0x73] = "PUSHF8";
        OPCODE_LABELS[0x74] = "PUSHF9";
        OPCODE_LABELS[0x75] = "PUSHAINTER";
        OPCODE_LABELS[0x76] = "SYSTEM";
        OPCODE_LABELS[0x77] = "REQWAIT";
        OPCODE_LABELS[0x78] = "PREQWAIT";
        OPCODE_LABELS[0x79] = "REQCHG";
        OPCODE_LABELS[0x7A] = "ACTREQ";
         */
        return map;
    }

    private void putEnum(String type, int idx, String name) {
        putEnum(type, idx, name, null);
    }

    private void putEnum(String type, int idx, String name, String internalName) {
        ScriptField field = new ScriptField(name, type, internalName, idx);
        getEnumMap(type).put(idx, field);
    }

    private void putSaveDataVariable(int idx, String name, String type, String indexType) {
        ScriptField field = new ScriptField(name, type);
        field.idx = idx;
        field.indexType = indexType;
        getEnumMap("saveData").put(idx, field);
    }

    private void putSaveDataVariable(int idx, String name, String type) {
        putSaveDataVariable(idx, name, type, "unknown");
    }

    private void putBattleVariable(int idx, String name, String type, String indexType) {
        ScriptField field = new ScriptField(name, type);
        field.idx = idx;
        field.indexType = indexType;
        getEnumMap("battleVar").put(idx, field);
    }

    private void putBattleVariable(int idx, String name, String type) {
        putBattleVariable(idx, name, type, "unknown");
    }

    private void putBattleActorProperty(int idx, String name, String type, String internalName, boolean getterOnly) {
        ScriptField field = new ScriptField(name, type, internalName, idx);
        getEnumMap("btlChrProperty").put(idx, field);
    }

    private void putBattleActorProperty(int idx, String name, String type, String internalName) {
        ScriptField field = new ScriptField(name, type, internalName, idx);
        getEnumMap("btlChrProperty").put(idx, field);
    }

    private void putMotionProperty(int idx, String name, String internalName) {
        ScriptField field = new ScriptField(name, "float", internalName, idx);
        getEnumMap("motionProperty").put(idx, field);
    }

    private void putCommandProperty(int idx, String name, String type) {
        ScriptField field = new ScriptField(name, type);
        field.idx = idx;
        getEnumMap("commandProperty").put(idx, field);
    }

    private void putMaps(int offset, String name, int endIdx) {
        for (int i = 0; i <= endIdx; i++) {
            String idxStr = String.format("%02d", i);
            putEnum("map", offset + i, name + idxStr);
        }
    }

    private void addEnumsFromAllCsvsInFolder(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) {
                return;
            }
            for (File listFile : files) {
                addEnumsFromAllCsvsInFolder(listFile);
            }
        } else if (file.getPath().endsWith(".csv")) {
            try {
                addEnumsFromCsv(file);
            } catch (IOException e) {
                System.err.println("IOException " + e.getLocalizedMessage());
            }
        }
    }

    private void addEnumsFromCsv(File file) throws IOException {
        List<String[]> lines = FileAccessorWithMods.csvToList(file);
        for (String[] cells : lines) {
            if (cells.length >= 3) {
                String type = nullIfBlankElseTrimmed(cells[0]);
                String indexString = nullIfBlankElseTrimmed(cells[1]);
                if (indexString.startsWith("0x")) {
                    indexString = indexString.substring(2);
                }
                String internalName = nullIfBlankElseTrimmed(cells[2]);
                try {
                    int idx = Integer.parseInt(indexString, 16);
                    String readableName = cells.length >= 4 ? nullIfBlankElseTrimmed(cells[3]) : null;
                    putEnum(type, idx, readableName, internalName);
                } catch (NumberFormatException ignored) {
                    System.err.println("Cannot parse index in csv=" + file.getPath() + " index=" + indexString);
                }
            } else {
                System.err.println("Erroneous line in csv=" + file.getPath() + " line=" + String.join(",", cells));
            }
        }
    }

    private static String nullIfBlankElseTrimmed(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        return s.trim();
    }

    public static record OpcodeChoice(int opcode) {
        @Override
        public String toString() {
            return String.format("%02X: %s", opcode, ScriptConstants.OPCODE_LABELS[opcode]);
        }
    }
}
