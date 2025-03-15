package atel.model;

import main.StringHelper;
import reading.FileAccessorWithMods;

import java.io.*;
import java.util.*;

public abstract class ScriptConstants {
    private static final String ENUM_CSV_ROOT = FileAccessorWithMods.RESOURCES_ROOT + "enums";
    public static String[] FUNCSPACES;
    public static String[] OPCODE_LABELS;
    public static int[] OPCODE_STACKPOPS;
    public static List<Integer> OPCODE_ENDLINE;
    public static final Map<String, Map<Integer, ScriptField>> ENUMERATIONS = new HashMap<>();
    public static final Map<Integer, ScriptField> COMP_OPERATORS = new HashMap<>();
    public static final List<String> INDEX_ENUMS_ONLY = List.of("var", "saveData", "battleVar");

    private static boolean initialized = false;

    public static void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;

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
        putCompOperator(0x10, "OP-B-ON", "unknown", "OPBON");
        putCompOperator(0x11, "OP-B-OFF", "unknown", "OPBOFF");
        putCompOperator(0x12, "<<", "int", "OPSLL");
        putCompOperator(0x13, ">>", "int", "OPSRL");
        putCompOperator(0x14, "+", "int", "OPADD");
        putCompOperator(0x15, "-", "int", "OPSUB");
        putCompOperator(0x16, "*", "int", "OPMUL");
        putCompOperator(0x17, "/", "int", "OPDIV");
        putCompOperator(0x18, "mod", "int", "OPMOD");

        addEnumsFromAllCsvsInFolder(new File(ENUM_CSV_ROOT));

        putSaveDataVariable(0x0003, "MoonflowTreasureProgressionFlags1", "int");
        putSaveDataVariable(0x0004, "MoonflowTreasureProgressionFlags2", "int");
        putSaveDataVariable(0x0005, "ViaPurificoTreasureProgressionFlags1", "int");
        putSaveDataVariable(0x0006, "ViaPurificoTreasureProgressionFlags2", "int"); // Also Besaid Falls treasure at &0x80 for some reason
        putSaveDataVariable(0x0007, "MoonflowTreasureProgressionFlags3", "int");
        putSaveDataVariable(0x0080, "RidingChocoboFlags", "int");
        putSaveDataVariable(0x008D, "CalmLandsQuestProgressionFlags", "int");
        putSaveDataVariable(0x0092, "MushroomRockRoadTreasureFlags", "int");
        putSaveDataVariable(0x0095, "CalmLandsTreasureFlags", "int");
        putSaveDataVariable(0x009B, "BelgemineFightProgressionFlags", "int");
        putSaveDataVariable(0x009F, "MonsterArenaUnlockQuestProgressionFlags", "int");
        putSaveDataVariable(0x00A0, "MonsterArenaAreaSpeciesConquestUnlockFlags", "int");
        putSaveDataVariable(0x00A4, "MonsterArenaOriginalCreationUnlockFlags", "int");
        putSaveDataVariable(0x00A8, "WobblyChocoboRecordMinutes", "int");
        putSaveDataVariable(0x00A9, "WobblyChocoboRecordSeconds", "int");
        putSaveDataVariable(0x00AA, "WobblyChocoboRecordTenths", "int");
        putSaveDataVariable(0x00AB, "DodgerChocoboRecordMinutes", "int");
        putSaveDataVariable(0x00AC, "DodgerChocoboRecordSeconds", "int");
        putSaveDataVariable(0x00AD, "DodgerChocoboRecordTenths", "int");
        putSaveDataVariable(0x00AE, "HyperDodgerChocoboRecordMinutes", "int");
        putSaveDataVariable(0x00AF, "HyperDodgerChocoboRecordSeconds", "int");
        putSaveDataVariable(0x00B0, "HyperDodgerChocoboRecordTenths", "int");
        putSaveDataVariable(0x00B1, "CatcherChocoboRecordMinutes", "int");
        putSaveDataVariable(0x00B2, "CatcherChocoboRecordSeconds", "int");
        putSaveDataVariable(0x00B3, "CatcherChocoboRecordTenths", "int");
        putSaveDataVariable(0x00CE, "BikanelTreasureFlags1", "int");
        putSaveDataVariable(0x00CF, "BikanelTreasureFlags2", "int");
        putSaveDataVariable(0x00D0, "BikanelTreasureFlags3", "int");
        putSaveDataVariable(0x0104, "EnergyBlastProgressionFlags", "int");
        putSaveDataVariable(0x0114, "RemiemTempleProgressionFlags", "int");
        putSaveDataVariable(0x0115, "BesaidVillageTreasureFlags", "int");
        putSaveDataVariable(0x0153, "MusicSpheresOwnedCount", "int");
        putSaveDataVariable(0x0154, "MovieSpheresOwnedCount", "int");
        putSaveDataVariable(0x0193, "DebugSkipJechtIntroScenes", "bool");
        putSaveDataVariable(0x014B, "ControllableCharacterInLuca", "playerChar");
        putSaveDataVariable(0x01C0, "KilikaForestTreasureFlags", "int");
        putSaveDataVariable(0x01C5, "BesaidTreasureFlags", "int");
        putSaveDataVariable(0x01CD, "MacalaniaTreasureFlags", "int");
        putSaveDataVariable(0x01D2, "OmegaRuinsProgressionFlags", "int");
        putSaveDataVariable(0x01D4, "HomeProgressionFlags", "int");
        putSaveDataVariable(0x0205, "ThunderPlainsProgressionFlags", "int");
        putSaveDataVariable(0x0208, "LightningDodgingRewardsToPickUpFlags", "int");
        putSaveDataVariable(0x0210, "LightningDodgingTotalBolts", "int");
        putSaveDataVariable(0x0212, "LightningDodgingTotalDodges", "int");
        putSaveDataVariable(0x0214, "LightningDodgingHighestConsecutiveDodges", "int"); // 440
        putSaveDataVariable(0x024C, "BlitzballWakkaPowerProgress", "int");
        putSaveDataVariable(0x0254, "BlitzballMovementType", "blitzballMovementType");
        putSaveDataVariable(0x0265, "AirshipLocationsVisitedFlags", "int");
        putSaveDataVariable(0x0A00, "GameMoment", "int");
        putSaveDataVariable(0x0A34, "GilLentToOAka", "int");
        putSaveDataVariable(0x0A38, "MacalaniaPricesChosenForOAka", "int");
        putSaveDataVariable(0x0A3C, "JechtShotMinigameCleared", "bool");
        putSaveDataVariable(0x0A3D, "BelgemineFamiliarity", "int");
        putSaveDataVariable(0x0A44, "RemiemAeonsDefeated", "int");
        putSaveDataVariable(0x0A49, "WorldChampionPrerequisitesCleared", "bool");
        putSaveDataVariable(0x0A4A, "SaveSphereInstructionsSeen", "int");
        putSaveDataVariable(0x0A4C, "CelestialMirrorObtained", "bool");
        putSaveDataVariable(0x0A4D, "CelestialsObtainedFlags", "int");
        putSaveDataVariable(0x0A50, "CalmLandsChocoboPositionX", "float");
        putSaveDataVariable(0x0A54, "CalmLandsChocoboPositionY", "float");
        putSaveDataVariable(0x0A58, "CalmLandsChocoboPositionZ", "float");
        putSaveDataVariable(0x0A60, "AlBhedPrimersCollectedCount", "int");
        putSaveDataVariable(0x0A68, "BlitzballMenuReturnMap", "int");
        putSaveDataVariable(0x0A6C, "BlitzballMenuReturnEntrance", "int");
        putSaveDataVariable(0x0A70, "AnimaSealsUnlocked", "int");
        putSaveDataVariable(0x0A80, "CelestialsHalfPoweredFlags", "int");
        putSaveDataVariable(0x0A81, "CelestialsFullPoweredFlags", "int");
        putSaveDataVariable(0x0A83, "isRedButterflyEncounter", "bool");
        putSaveDataVariable(0x0A88, "BlitzballTeamPlayerCount", "int", "blitzballTeam");
        putSaveDataVariable(0x0A8F, "OptionalAeonsRecruitedFlags", "int");
        putSaveDataVariable(0x0A93, "JechtSpheresCollectedCount", "int");
        putSaveDataVariable(0x0A94, "AeonBoostingKeyItemsObtainedFlags", "int");
        putSaveDataVariable(0x0A95, "AirshipDestinationUnlocks", "int");
        putSaveDataVariable(0x0A97, "?CactuarGuardiansQuestProgress", "int");
        putSaveDataVariable(0x0A98, "IsBetweenDjoseFaythTalkAndAirshipBoarding", "bool");
        putSaveDataVariable(0x0A99, "CactuarGuardiansBeaten", "int");
        putSaveDataVariable(0x0A9A, "AlBhedPrimersInstructionsSeen", "bool");
        putSaveDataVariable(0x0A9B, "RemiemRaceTreasureFlags", "int");
        putSaveDataVariable(0x0A9D, "DarkValeforCompletionFlags", "int");
        putSaveDataVariable(0x0A9E, "DarkIfritCompletionFlags", "int");
        putSaveDataVariable(0x0A9F, "DarkIxionCompletionFlags", "int");
        putSaveDataVariable(0x0AA0, "DarkShivaCompletionFlags", "int");
        putSaveDataVariable(0x0AA1, "DarkBahamutCompletionFlags", "int");
        putSaveDataVariable(0x0AA2, "DarkYojimboCompletionFlags", "int");
        putSaveDataVariable(0x0AA3, "DarkAnimaCompletionFlags", "int");
        putSaveDataVariable(0x0AA4, "DarkMagusSistersCompletionFlags", "int");
        putSaveDataVariable(0x0AA5, "PenanceUnlockState", "int");
        putSaveDataVariable(0x0AEB, "BattleDialogLinesSeenFlags", "int");
        putSaveDataVariable(0x0B63, "?TidusBattleTalkativeness", "int");
        putSaveDataVariable(0x0B64, "?WakkaBattleTalkativeness", "int");
        putSaveDataVariable(0x0B65, "?YunaBattleTalkativeness", "int");
        putSaveDataVariable(0x0B66, "?LuluBattleTalkativeness", "int");
        putSaveDataVariable(0x0B67, "?RikkuBattleTalkativeness", "int");
        putSaveDataVariable(0x0B68, "?AuronBattleTalkativeness", "int");
        putSaveDataVariable(0x0B69, "Unused?KimahriBattleTalkativeness", "int");
        putSaveDataVariable(0x1000, "BlitzballPlayerLearnedTechsPage1", "bitfieldFrom_blitzTechP1", "blitzballPlayer");
        putSaveDataVariable(0x10F0, "BlitzballPlayerLearnedTechsPage2", "bitfieldFrom_blitzTechP2", "blitzballPlayer");
        putSaveDataVariable(0x1266, "BlitzballPlayerEquippedTechs", "blitzTech"); // index = blitzballPlayer * 5 + 0..4
        putSaveDataVariable(0x1392, "BlitzballPlayerUnlockedTechSlots", "int", "blitzballPlayer");
        putSaveDataVariable(0x13CE, "BlitzballPlayerCurrentLevel", "int", "blitzballPlayer");
        putSaveDataVariable(0x1568, "BlitzballPlayerCurrentEXP", "int", "blitzballPlayer");
        putSaveDataVariable(0x141A, "BlitzballTeamPlayers", "blitzballPlayer");
        putSaveDataVariable(0x1452, "BlitzballMatchPlayerScore", "int");
        putSaveDataVariable(0x1453, "BlitzballMatchOpponentScore", "int");
        putSaveDataVariable(0x1454, "BlitzballIsSecondHalf?", "bool");
        putSaveDataVariable(0x1455, "BlitzballMarkedPlayerIndex?", "int", "int");
        putSaveDataVariable(0x1465, "BlitzballEnemyTeam", "blitzballTeam");
        putSaveDataVariable(0x1466, "BlitzballCurrentLeagueTeamSeedings", "blitzballTeam", "int");
        putSaveDataVariable(0x146C, "BlitzballCurrentTournamentTeamSlot", "blitzballTeam", "int");
        putSaveDataVariable(0x147A, "BlitzballCurrentLeagueTeamWins", "int", "blitzballTeam");
        putSaveDataVariable(0x1480, "BlitzballCurrentLeagueTeamLosses", "int", "blitzballTeam");
        putSaveDataVariable(0x1486, "BlitzballCurrentLeagueRound", "int");
        putSaveDataVariable(0x1487, "BlitzballCurrentLeagueMatchTeamSlot", "blitzballTeam", "int");
        putSaveDataVariable(0x148E, "BlitzballGameMode", "blitzballGameMode");
        putSaveDataVariable(0x14B3, "BlitzballCurrentTournamentState", "int");
        putSaveDataVariable(0x14B4, null, "int");
        putSaveDataVariable(0x152A, "BlitzballPlayerContractDurations", "int", "blitzballPlayer");
        putSaveDataVariable(0x1566, "BlitzballTotalGamesWon", "int");
        putSaveDataVariable(0x1798, "BlitzballPlayerCostPerGame", "int", "blitzballPlayer");
        putSaveDataVariable(0x1810, "BlitzballLeaguePrizeIndex", "int");
        putSaveDataVariable(0x1816, "BlitzballTournamentPrizeIndex", "int");
        putSaveDataVariable(0x181C, "BlitzballLeagueTopScorerPrizeIndex", "int");
        putSaveDataVariable(0x181E, "BlitzballTournamentTopScorerPrizeIndex", "int");
        putSaveDataVariable(0x1820, "?BlitzballPlayerUncoveredTechsPage1", "bitfieldFrom_blitzTechP1", "blitzballPlayer");
        putSaveDataVariable(0x1910, "?BlitzballPlayerUncoveredTechsPage2", "bitfieldFrom_blitzTechP2", "blitzballPlayer");
        putSaveDataVariable(0x2000, "SphereGridNodeState", "sphereGridNodeState", "int");

        putBattleVariable(0x0170, "BattleDialogLineVoiceFile", "int");
        putBattleVariable(0x028C, "BattleDialogLineString", "system01String");

        putEnum("deathAnimation", 0x00, "Character (Body remains and targetable)", "death_normal");
        putEnum("deathAnimation", 0x01, "Boss (Body remains but untargetable)", "death_nop");
        putEnum("deathAnimation", 0x02, "Humanoid (No Pyreflies, body fades out)", "death_fadeout");
        putEnum("deathAnimation", 0x03, "Fiend (Pyrefly dissipation)", "death_phantom");
        putEnum("deathAnimation", 0x04, "Disintegrate-Machina (Red explosions)", "death_exp");
        putEnum("deathAnimation", 0x05, "Steal-Machina (Same as 02 with machina SFX)", "death_break");
        putEnum("deathAnimation", 0x08, "YAT/YKT", "death_break2");

        putEnum("selector", 0x00, "Any/All");
        putEnum("selector", 0x01, "Highest");
        putEnum("selector", 0x02, "Lowest");
        putEnum("selector", 0x80, "Not");

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
        putEnum("battleWorkerType", 0x04, "EncounterScripts");
        putEnum("battleWorkerType", 0x06, "StartEndHooks");
        putEnum("battleWorkerType", 0x07, "MagicCameraHandler-Command");
        putEnum("battleWorkerType", 0x08, "MagicCameraHandler-Item");
        putEnum("battleWorkerType", 0x09, "MagicCameraHandler-Monmagic1");
        putEnum("battleWorkerType", 0x0A, "MagicCameraHandler-Monmagic2");

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
        putEnum("stringVarType", 0x37, "Treasure Label");

        putEnum("textFlagBitfield", 0x4000, "Al Bhed");

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
        putEnum("blitzballPlayer", 0x3C, "<Empty>");

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

        putEnum("battleDebugFlag", 0x03, "ControlMonsters");
        putEnum("battleDebugFlag", 0x07, "NeverCrit");

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

        putEnum("field", 0x0000, "system");
        putEnum("field", 0x0002, "test00");
        putEnum("field", 0x0005, "test10");
        putEnum("field", 0x0006, "test11");
        putFields(0x000A, "znkd", 14);
        putFields(0x001E, "bjyt", 12);
        putFields(0x0032, "cdsp", 8);
        putFields(0x0041, "bsil", 7);
        putFields(0x005F, "slik", 11);
        putFields(0x0083, "klyt", 12);
        putFields(0x00A5, "lchb", 18);
        putFields(0x00D2, "mihn", 8);
        putFields(0x00DC, "kino", 9);
        putFields(0x00F5, "genk", 16);
        putFields(0x012C, "kami", 4);
        putFields(0x0136, "mcfr", 14);
        putFields(0x014A, "maca", 5);
        putFields(0x0154, "mcyt", 7);
        putFields(0x015E, "bika", 4);
        putFields(0x0168, "azit", 7);
        putFields(0x017C, "hiku", 21);
        putFields(0x0195, "stbv", 1);
        putFields(0x019A, "bvyt", 13);
        putFields(0x01A9, "nagi", 7);
        putFields(0x01BD, "lmyt", 2);
        putFields(0x01E5, "mtgz", 11);
        putFields(0x01F4, "zkrn", 6);
        putFields(0x0203, "dome", 7);
        putFields(0x0235, "ssbt", 4);
        putFields(0x0244, "sins", 9);
        putFields(0x024E, "omeg", 1);
        putFields(0x0259, "zzzz", 3);
        putFields(0x0262, "tori", 3);

        putEnum("btlArea", 0, "system");
        putEnum("btlArea", 1, "bjyt02");
        putEnum("btlArea", 2, "bjyt04");
        putEnum("btlArea", 3, "cdsp00");
        putEnum("btlArea", 4, "cdsp07");
        putEnum("btlArea", 5, "bsil03");
        putEnum("btlArea", 6, "bsil07");
        putEnum("btlArea", 7, "bsil04");
        putEnum("btlArea", 8, "bsil05");
        putEnum("btlArea", 9, "slik02");
        putEnum("btlArea", 10, "klyt00");
        putEnum("btlArea", 11, "klyt01");
        putEnum("btlArea", 12, "lchb02");
        putEnum("btlArea", 13, "lchb03");
        putEnum("btlArea", 14, "lchb04");
        putEnum("btlArea", 15, "cdsp02");
        putEnum("btlArea", 16, "lchb08");
        putEnum("btlArea", 17, "lchb07");
        putEnum("btlArea", 18, "mihn00");
        putEnum("btlArea", 19, "mihn07");
        putEnum("btlArea", 20, "mihn08");
        putEnum("btlArea", 21, "mihn02");
        putEnum("btlArea", 22, "mihn04");
        putEnum("btlArea", 23, "mihn05");
        putEnum("btlArea", 24, "kino00");
        putEnum("btlArea", 25, "kino01");
        putEnum("btlArea", 26, "kino05");
        putEnum("btlArea", 27, "kino02");
        putEnum("btlArea", 28, "kino07");
        putEnum("btlArea", 29, "kino03");
        putEnum("btlArea", 30, "kino04");
        putEnum("btlArea", 31, "genk00");
        putEnum("btlArea", 32, "genk09");
        putEnum("btlArea", 33, "genk16");
        putEnum("btlArea", 34, "kami00");
        putEnum("btlArea", 35, "kami03");
        putEnum("btlArea", 36, "mcfr00");
        putEnum("btlArea", 37, "mcfr01");
        putEnum("btlArea", 38, "mcfr02");
        putEnum("btlArea", 39, "mcfr03");
        putEnum("btlArea", 40, "maca00");
        putEnum("btlArea", 41, "mcyt06");
        putEnum("btlArea", 42, "mcyt00");
        putEnum("btlArea", 43, "maca03");
        putEnum("btlArea", 44, "maca02");
        putEnum("btlArea", 45, "bika00");
        putEnum("btlArea", 46, "bika01");
        putEnum("btlArea", 47, "bika02");
        putEnum("btlArea", 48, "bika03");
        putEnum("btlArea", 49, "azit03");
        putEnum("btlArea", 50, "hiku02");
        putEnum("btlArea", 51, "hiku05");
        putEnum("btlArea", 52, "hiku15");
        putEnum("btlArea", 53, "bvyt00");
        putEnum("btlArea", 54, "bvyt09");
        putEnum("btlArea", 55, "stbv00");
        putEnum("btlArea", 56, "stbv01");
        putEnum("btlArea", 57, "nagi00");
        putEnum("btlArea", 58, "tori01");
        putEnum("btlArea", 59, "tori02");
        putEnum("btlArea", 60, "tori03");
        putEnum("btlArea", 61, "nagi01");
        putEnum("btlArea", 62, "nagi04");
        putEnum("btlArea", 63, "nagi05");
        putEnum("btlArea", 64, "mtgz01");
        putEnum("btlArea", 65, "mtgz02");
        putEnum("btlArea", 66, "mtgz06");
        putEnum("btlArea", 67, "mtgz07");
        putEnum("btlArea", 68, "mtgz08");
        putEnum("btlArea", 69, "zkrn02");
        putEnum("btlArea", 70, "dome00");
        putEnum("btlArea", 71, "dome02");
        putEnum("btlArea", 72, "dome06");
        putEnum("btlArea", 73, "ssbt00");
        putEnum("btlArea", 74, "ssbt01");
        putEnum("btlArea", 75, "ssbt02");
        putEnum("btlArea", 76, "ssbt03");
        putEnum("btlArea", 77, "sins02");
        putEnum("btlArea", 78, "sins03");
        putEnum("btlArea", 79, "sins04");
        putEnum("btlArea", 80, "sins05");
        putEnum("btlArea", 81, "sins06");
        putEnum("btlArea", 82, "sins07");
        putEnum("btlArea", 83, "cdsp01");
        putEnum("btlArea", 84, "lmyt01");
        putEnum("btlArea", 85, "znkd08");
        putEnum("btlArea", 86, "znkd09");
        putEnum("btlArea", 87, "tori00");
        putEnum("btlArea", 88, "omeg00");
        putEnum("btlArea", 89, "omeg01");
        putEnum("btlArea", 90, "test00");
        putEnum("btlArea", 91, "test10");
        putEnum("btlArea", 92, "test11");
        putEnum("btlArea", 93, "zzzz00");
        putEnum("btlArea", 94, "zzzz02");
        putEnum("btlArea", 95, "zzzz03");

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
        putEnum("bgm", 0x0091, "Challenge");
        putEnum("bgm", 0x00A5, "Run!!");
        putEnum("bgm", 0x00AB, "Lulu's Theme");
        putEnum("bgm", 0x00B0, "A Contest of Aeons");

        putEnum("battleTransition", 0x00, "Screen Shatter");
        putEnum("battleTransition", 0x01, "Fade");

        putEnum("battleEndType", 0x01, "Defeat");
        putEnum("battleEndType", 0x02, "Victory");
        putEnum("battleEndType", 0x03, "?Escape");
        putEnum("battleEndType", 0x04, null); // Seems to be a special case override thing?

        putEnum("appearMotion", 0x00, "No Animation", "appear_motion_off");
        putEnum("appearMotion", 0x01, null, "appear_motion_off_trans");
        putEnum("appearMotion", 0x02, null, "appear_motion_on");
        putEnum("appearMotion", 0x03, null, "appear_motion_on_trans");
        putEnum("appearMotion", 0x04, "Hidden", "appear_motion_off_hide");

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
        putEnum("overdriveMode", 0x05, "Sufferer", "limit_type_kyuuti");
        putEnum("overdriveMode", 0x06, "Dancer", "limit_type_karei");
        putEnum("overdriveMode", 0x07, "Avenger", "limit_type_hiai");
        putEnum("overdriveMode", 0x08, "Slayer", "limit_type_gaika");
        putEnum("overdriveMode", 0x09, "Hero", "limit_type_eiyuu");
        putEnum("overdriveMode", 0x0A, "Rook", "limit_type_banjyaku");
        putEnum("overdriveMode", 0x0B, "Victor", "limit_type_syouri");
        putEnum("overdriveMode", 0x0C, "Coward", "limit_type_tijyoku");
        putEnum("overdriveMode", 0x0D, "Ally", "limit_type_taiji");
        putEnum("overdriveMode", 0x0E, "Victim", "limit_type_kugyou");
        putEnum("overdriveMode", 0x0F, "Daredevil", "limit_type_kiki");
        putEnum("overdriveMode", 0x10, "Loner", "limit_type_kokou");
        putEnum("overdriveMode", 0x13, "Aeons Only", "limit_type_syoukan");

        putEnum("damageType", 0x00, "Special", "atc_type_not");
        putEnum("damageType", 0x01, "Physical", "atc_type_physic");
        putEnum("damageType", 0x02, "Magical", "atc_type_magic");

        putEnum("sphereGrid", 0x00, "Original (JP/NTSC) Sphere Grid");
        putEnum("sphereGrid", 0x01, "Standard Sphere Grid");
        putEnum("sphereGrid", 0x02, "Expert Sphere Grid");

        putEnum("weakState", 0x00, "Normal");
        putEnum("weakState", 0x01, "Slightly Weak");
        putEnum("weakState", 0x02, "Very Weak");
        putEnum("weakState", 0x03, "Dead");

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

        for (int i = 0; i <= 0x0013; i++) {
            getEnumMap("btlChr").put(i, getEnumMap("playerChar").get(i));
        }
        for (int i = 1; i <= 10; i++) {
            putEnum("btlChr", 0x0013 + i, "Monster#" + i);
        }
        putEnum("btlChr", 0x00FF, "Actor:None");

        for (int i = 0x1000; i <= 0x1200; i++) {
            putEnum("btlChr", i, "Actors:MonsterType=m" + String.format("%03d", i - 0x1000));
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

        putEnum("magusTarget", 0x00, "AllMonsters");
        putEnum("magusTarget", 0x01, "FrontlineChars");
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
        putEnum("motionType", 0x02, "Swimming");

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
        putBattleActorProperty(0x0081, "StealItemCommonType", "move", "stat_item");
        putBattleActorProperty(0x0082, "StealItemCommonAmount", "int", "stat_item_num");
        putBattleActorProperty(0x0083, "StealItemRareType", "move", "stat_rareitem");
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
        putBattleActorProperty(0x00DD, null, "unknown", "stat_vigor");
        putBattleActorProperty(0x00DE, null, "unknown", "stat_fast_model_flag");
        putBattleActorProperty(0x00DF, null, "unknown", "stat_alive_not_stone");
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
        putBattleActorProperty(0x00F0, "?ImmunityDelay", "unknown", "stat_def_ctb");
        putBattleActorProperty(0x00F1, null, "unknown", "stat_shadow");
        putBattleActorProperty(0x00F2, null, "unknown", "stat_death");
        putBattleActorProperty(0x00F3, null, "unknown", "stat_death_stone");
        putBattleActorProperty(0x00F4, null, "unknown", "stat_check_pos");
        putBattleActorProperty(0x00F5, null, "unknown", "stat_win_se");
        putBattleActorProperty(0x00F6, null, "unknown", "stat_attack_num");
        putBattleActorProperty(0x00F7, null, "unknown", "stat_near_motion");
        putBattleActorProperty(0x00F8, null, "unknown", "stat_talk_stat1");
        putBattleActorProperty(0x00F9, null, "unknown", "stat_talk_stat2");
        putBattleActorProperty(0x00FA, "?ForceCloseRangeAttackAnim", "bool", "stat_near_motion_set");
        putBattleActorProperty(0x00FB, null, "unknown", "stat_motion_speed_normal");
        putBattleActorProperty(0x00FC, null, "unknown", "stat_motion_speed_normal_start");
        putBattleActorProperty(0x00FD, null, "unknown", "stat_own_attack_near");
        putBattleActorProperty(0x00FE, null, "unknown", "stat_talk_stat3");
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
        putBattleActorProperty(0x010A, "LearnableRonsoRage", "move", "stat_blue_magic");
        putBattleActorProperty(0x010B, "?NullSlice", "unknown", "stat_sp_disable_zan");
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
        putBattleActorProperty(0x013A, "Item1CommonType", "move", "stat_item1_com");
        putBattleActorProperty(0x013B, "Item1RareType", "move", "stat_item1_rare");
        putBattleActorProperty(0x013C, "Item2CommonType", "move", "stat_item2_com");
        putBattleActorProperty(0x013D, "Item2RareType", "move", "stat_item2_rare");
        putBattleActorProperty(0x013E, "Item1CommonTypeOverkill", "move", "stat_item1_com_over_kill");
        putBattleActorProperty(0x013F, "Item1RareTypeOverkill", "move", "stat_item1_rare_over_kill");
        putBattleActorProperty(0x0140, "Item2CommonTypeOverkill", "move", "stat_item2_com_over_kill");
        putBattleActorProperty(0x0141, "Item2RareTypeOverkill", "move", "stat_item2_rare_over_kill");
        putBattleActorProperty(0x0142, "Item1CommonAmount", "int", "stat_item1_com_num");
        putBattleActorProperty(0x0143, "Item1RareAmount", "int", "stat_item1_rare_num");
        putBattleActorProperty(0x0144, "Item2CommonAmount", "int", "stat_item2_com_num");
        putBattleActorProperty(0x0145, "Item2RareAmount", "int", "stat_item2_rare_num");
        putBattleActorProperty(0x0146, "Item1CommonAmountOverkill", "int", "stat_item1_com_over_kill_num");
        putBattleActorProperty(0x0147, "Item1RareAmountOverkill", "int", "stat_item1_rare_over_kill_num");
        putBattleActorProperty(0x0148, "Item2CommonAmountOverkill", "int", "stat_item2_com_over_kill_num");
        putBattleActorProperty(0x0149, "Item2RareAmountOverkill", "int", "stat_item2_rare_over_kill_num");
        putBattleActorProperty(0x014A, null, "unknown", "stat_death_return");
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
        putBattleActorProperty(0x0159, "?onlyTargetableBy", "move", null);

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
        putMoveProperty(0x000A, "targetType", "targetType");
    }

    public static Map<Integer, ScriptField> getEnumMap(String type) {
        return ENUMERATIONS.computeIfAbsent(type, (t) -> new HashMap<>());
    }

    private static void putEnum(String type, int idx, String name) {
        putEnum(type, idx, name, null);
    }

    private static void putEnum(String type, int idx, String name, String internalName) {
        ScriptField field = new ScriptField(name, type, internalName, idx);
        getEnumMap(type).put(idx, field);
    }

    private static void putCompOperator(int idx, String name, String type, String internalName) {
        ScriptField field = new ScriptField(name, type, internalName, idx);
        COMP_OPERATORS.put(idx, field);
    }

    private static void putSaveDataVariable(int idx, String name, String type, String indexType) {
        ScriptField field = new ScriptField(name, type);
        field.idx = idx;
        field.indexType = indexType;
        getEnumMap("saveData").put(idx, field);
    }

    private static void putSaveDataVariable(int idx, String name, String type) {
        putSaveDataVariable(idx, name, type, "unknown");
    }

    private static void putBattleVariable(int idx, String name, String type, String indexType) {
        ScriptField field = new ScriptField(name, type);
        field.idx = idx;
        field.indexType = indexType;
        getEnumMap("battleVar").put(idx, field);
    }

    private static void putBattleVariable(int idx, String name, String type) {
        putBattleVariable(idx, name, type, "unknown");
    }

    private static void putBattleActorProperty(int idx, String name, String type, String internalName) {
        ScriptField field = new ScriptField(name, type, internalName, idx);
        getEnumMap("btlChrProperty").put(idx, field);
    }

    private static void putMotionProperty(int idx, String name, String internalName) {
        ScriptField field = new ScriptField(name, "float", internalName, idx);
        getEnumMap("motionProperty").put(idx, field);
    }

    private static void putMoveProperty(int idx, String name, String type) {
        ScriptField field = new ScriptField(name, type);
        field.idx = idx;
        getEnumMap("moveProperty").put(idx, field);
    }

    private static void putFields(int offset, String name, int endIdx) {
        for (int i = 0; i <= endIdx; i++) {
            String idxStr = String.format("%02d", i);
            putEnum("field", offset + i, name + idxStr);
        }
    }

    private static void addEnumsFromAllCsvsInFolder(File file) {
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

    private static void addEnumsFromCsv(File file) throws IOException {
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
}
