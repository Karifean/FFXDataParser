package script.model;

import java.lang.reflect.Field;
import java.util.*;

public abstract class ScriptConstants {
    public static String[] FUNCSPACES;
    public static String[] OPCODE_LABELS;
    public static int[] OPCODE_STACKPOPS;
    public static List<Integer> OPCODE_ENDLINE;
    public static Map<String, Map<Integer, ScriptField>> ENUMERATIONS = new HashMap<>();
    public static Map<Integer, ScriptField> COMP_OPERATORS;

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

        COMP_OPERATORS = new HashMap<>();
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

        putEnum("ambushState", 0x00, "Randomized", "first_attack_normal");
        putEnum("ambushState", 0x01, "Preemptive", "first_attack_player");
        putEnum("ambushState", 0x02, "Ambushed", "first_attack_monster");
        putEnum("ambushState", 0x03, "Neither", "first_attack_random_off");

        putEnum("yojimboReaction", 0x00, "NullReaction");
        putEnum("yojimboReaction", 0x01, "Regular", "youjinbou_consent_pay");
        putEnum("yojimboReaction", 0x02, "Nod", "youjinbou_consent_pay_ok");
        putEnum("yojimboReaction", 0x03, "Headshake", "youjinbou_consent_pay_ng");

        putEnum("targetType", 0x00, "Single", "target_type_single");
        putEnum("targetType", 0x01, "Multi", "target_type_group");
        putEnum("targetType", 0x02, "AllActors?", "target_type_all");
        putEnum("targetType", 0x03, "Self?", "target_type_own");

        putEnum("controllerButton", 0x05, "X (Confirm)");
        putEnum("controllerButton", 0x12, "?Up");
        putEnum("controllerButton", 0x13, "?Right");
        putEnum("controllerButton", 0x14, "?Down");
        putEnum("controllerButton", 0x15, "?Left");

        putEnum("textAlignment", 0x01, "?Left");
        putEnum("textAlignment", 0x03, "?Right");
        putEnum("textAlignment", 0x04, "?Center");

        for (Field field : CharMotions.class.getDeclaredFields()) {
            try {
                int idx = field.getInt(CharMotions.class);
                if (!getEnumMap("motion").containsKey(idx)) {
                    String internalName = field.getName();
                    putEnum("motion", idx, null, internalName);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        putEnum("model", 0x0000, "No Model");
        putEnum("model", 0x0004, "Kimahri");
        putEnum("model", 0x0008, "Seymour (Human Boss)");
        putEnum("model", 0x1002, "Helmet");
        putEnum("model", 0x1006, "Floating Devil");
        putEnum("model", 0x100A, "Wolf");
        putEnum("model", 0x1010, "Flan");
        putEnum("model", 0x1016, "Lizard");
        putEnum("model", 0x101C, "Eagle/Condor");
        putEnum("model", 0x101F, "Wasp");
        putEnum("model", 0x1023, "Floating Eye");
        putEnum("model", 0x1027, "Ragora");
        putEnum("model", 0x102B, "Sahagin (on Land)");
        putEnum("model", 0x102C, "Garuda");
        putEnum("model", 0x102D, "Zu");
        putEnum("model", 0x102E, "Huge Worm");
        putEnum("model", 0x1030, "Defender");
        putEnum("model", 0x1032, "Ghost");
        putEnum("model", 0x1033, "Phlegyas/Achelous");
        putEnum("model", 0x1035, "Remora/Maelspike");
        putEnum("model", 0x1037, "Dual Horn");
        putEnum("model", 0x103A, "Octopus (Via Purifico)");
        putEnum("model", 0x103B, "Drake");
        putEnum("model", 0x1040, "Malboro");
        putEnum("model", 0x1044, "Piranha");
        putEnum("model", 0x104D, "Yellow Element");
        putEnum("model", 0x104E, "White Element");
        putEnum("model", 0x104F, "Red Element");
        putEnum("model", 0x1050, "Gold Element");
        putEnum("model", 0x1051, "Blue Element");
        putEnum("model", 0x1052, "Dark/Black/Nega Element");
        putEnum("model", 0x1055, "Behemoth");
        putEnum("model", 0x1057, "Chimera");
        putEnum("model", 0x1059, "Coeurl");
        putEnum("model", 0x105F, "Demonolith");
        putEnum("model", 0x1060, "Machina (Multi-Arm)");
        putEnum("model", 0x1063, "Weapon (Ultima/Omega/Nemesis)");
        putEnum("model", 0x1065, "Tros");
        putEnum("model", 0x1066, "Sinspawn Geneaux/Genais");
        putEnum("model", 0x1067, "Geneaux Tentacle");
        putEnum("model", 0x1068, "Chocobo Eater");
        putEnum("model", 0x1069, "Neslug");
        putEnum("model", 0x106A, "Mortiphasm");
        putEnum("model", 0x106B, "Sinscale (Winged)");
        putEnum("model", 0x106D, "Geosgaeno");
        putEnum("model", 0x106E, "Oblitzerator");
        putEnum("model", 0x106F, "Extractor");
        putEnum("model", 0x1071, "Sin's Fin (Swimming)");
        putEnum("model", 0x1072, "Sinspawn Echuilles");
        putEnum("model", 0x1073, "Sinscale (Underwater)");
        putEnum("model", 0x1075, "Sinspawn Gui");
        putEnum("model", 0x1077, "Evrae Altana");
        putEnum("model", 0x1079, "Spherimorph");
        putEnum("model", 0x107A, "Crawler");
        putEnum("model", 0x107B, "Negator");
        putEnum("model", 0x107E, "Seymour Natus");
        putEnum("model", 0x107F, "Mortibody");
        putEnum("model", 0x1080, "Sanctuary Keeper");
        putEnum("model", 0x1081, "Spectral Keeper");
        putEnum("model", 0x1082, "Yunalesca");
        putEnum("model", 0x1083, "Seymour Omnis");
        putEnum("model", 0x1084, "Braska's Final Aeon");
        putEnum("model", 0x1085, "Crane");
        putEnum("model", 0x1086, "Biran Ronso");
        putEnum("model", 0x1087, "Yenke Ronso");
        putEnum("model", 0x1088, "Sin's Fin (Flying)");
        putEnum("model", 0x108A, "Sin Core (Back)");
        putEnum("model", 0x108C, "Overdrive Sin");
        putEnum("model", 0x108E, "Seymour Flux");
        putEnum("model", 0x1090, "Kimahri's Spear (in Boss fight)");
        putEnum("model", 0x1098, "Machina (Impaler)");
        putEnum("model", 0x1099, "Ochu");
        putEnum("model", 0x109B, "Sahagin (Swimming)");
        putEnum("model", 0x109E, "Blades (Species)");
        putEnum("model", 0x109F, "Sinspawn Ammes");
        putEnum("model", 0x10A2, "Gate Lock (Evrae Altana Battle)");
        putEnum("model", 0x10AA, "Tanker");
        putEnum("model", 0x10AD, "Yu Pagoda 1");
        putEnum("model", 0x10AE, "Yu Pagoda 2");
        putEnum("model", 0x10AF, "?Tentacle (???)");
        putEnum("model", 0x10B0, "Yu Yevon");
        putEnum("model", 0x10B1, "?Mortiphasm Dummied Out");
        putEnum("model", 0x10B5, "Iron Giant (All)");
        putEnum("model", 0x10B9, "Basilisk");
        putEnum("model", 0x10BB, "Adamantoise");
        putEnum("model", 0x10BC, "Varuna");
        putEnum("model", 0x10BF, "Yevon Artillery Machina (YAT)");
        putEnum("model", 0x10C1, "Bomb");
        putEnum("model", 0x10C3, "Yevon Kicking Machina (YKT)");
        putEnum("model", 0x10C5, "Monk (Rifle)");
        putEnum("model", 0x10C7, "Monk (Flamethrower)");
        putEnum("model", 0x10CA, "Magic Urn (PuPu?)");
        putEnum("model", 0x10CF, "Cactuar");
        putEnum("model", 0x10D1, "Larva/Spirit");
        putEnum("model", 0x10D2, "Barbatos");
        putEnum("model", 0x10D3, "Treasure Chest (Battle)");
        putEnum("model", 0x10D4, "Ogre/Wendigo");
        putEnum("model", 0x10D5, "Guado Guardian");
        putEnum("model", 0x10D6, "Mushroom (Species)");
        putEnum("model", 0x10DF, "Tonberry");
        putEnum("model", 0x1101, "Mimic Parts");
        putEnum("model", 0x1158, "Penance");
        putEnum("model", 0x1159, "Penance's Arm");
        putEnum("model", 0x3001, "Valefor");
        putEnum("model", 0x3002, "Ifrit");
        putEnum("model", 0x3003, "Ixion");
        putEnum("model", 0x3004, "Shiva");
        putEnum("model", 0x3006, "Bahamut");
        putEnum("model", 0x3007, "Anima");
        putEnum("model", 0x3008, "Yojimbo");
        putEnum("model", 0x3009, "Cindy");
        putEnum("model", 0x300A, "Sandy");
        putEnum("model", 0x300B, "Mindy");
        putEnum("model", 0x300D, "?Anima Dummy 1");
        putEnum("model", 0x300E, "?Anima Dummy 2");
        putEnum("model", 0x3017, "Koma Inu / Daigoro");
        putEnum("model", 0x3018, "Katana (Yojimbo)");
        putEnum("model", 0x3019, "Kozuka (Yojimbo)");
        putEnum("model", 0x301F, "Shuriken (Yojimbo?)");
        putEnum("model", 0x6004, "Isaaru");
        putEnum("model", 0x600E, "Belgemine/Ginnem");

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
        putEnum("sfx", 0x80000048, "BoardAirship");
        putEnum("sfx", 0x8000004A, "InstructionsChime");
        putEnum("sfx", 0x00015403, "LightningBoltHit");

        putEnum("bgm", 0x000A, "Unwavering Determination");
        putEnum("bgm", 0x000B, "Secret Maneuvers");
        putEnum("bgm", 0x000C, "Boss Theme");
        putEnum("bgm", 0x000D, "The Summoning");
        putEnum("bgm", 0x000E, "Macalania Woods");
        putEnum("bgm", 0x0010, "?Battle Theme");
        putEnum("bgm", 0x001A, "Seymour's Ambition");
        putEnum("bgm", 0x001B, "Blitz Off!");
        putEnum("bgm", 0x001D, "Thunder Plains");
        putEnum("bgm", 0x0025, "People of the North Pole");
        putEnum("bgm", 0x0029, "Truth Revealed");
        putEnum("bgm", 0x0082, "To Zanarkand");
        putEnum("bgm", 0x0091, "Challenge");
        putEnum("bgm", 0x00A5, "?Run!!");
        putEnum("bgm", 0x00AB, "Lulu's Theme");

        putEnum("battleTransition", 0x00, "Screen Shatter");
        putEnum("battleTransition", 0x01, "Fade");

        putEnum("battleEndType", 0x01, "Game Over");
        putEnum("battleEndType", 0x02, "Victory");
        putEnum("battleEndType", 0x03, "?Escape");

        putEnum("damageFormula", 0x00, "None");
        putEnum("damageFormula", 0x01, "STR vs DEF");
        putEnum("damageFormula", 0x02, "STR (ignore DEF)");
        putEnum("damageFormula", 0x03, "MAG vs MDF");
        putEnum("damageFormula", 0x04, "MAG (ignore MDF)");
        putEnum("damageFormula", 0x05, "Current/16");
        putEnum("damageFormula", 0x06, "Fixed x50");
        putEnum("damageFormula", 0x07, "Healing");
        putEnum("damageFormula", 0x08, "Max/16");
        putEnum("damageFormula", 0x09, "Fixed x~50");
        putEnum("damageFormula", 0x0D, "Ticks/16");
        putEnum("damageFormula", 0x0F, "Special MAG (ignore MDF)");
        putEnum("damageFormula", 0x10, "Fixed x User MaxHP / 10");
        putEnum("damageFormula", 0x15, "Fixed x Gil chosen / 10");
        putEnum("damageFormula", 0x16, "Fixed xKills");
        putEnum("damageFormula", 0x17, "Fixed x9999");

        putEnum("damageType", 0x00, "Special");
        putEnum("damageType", 0x01, "Physical");
        putEnum("damageType", 0x02, "Magical");

        putEnum("actor", 0x0000, "Tidus", "PC_TIDUS");
        putEnum("actor", 0x0001, "Yuna", "PC_YUNA");
        putEnum("actor", 0x0002, "Auron", "PC_AURON");
        putEnum("actor", 0x0003, "Kimahri", "PC_KIMAHRI");
        putEnum("actor", 0x0004, "Wakka", "PC_WAKKA");
        putEnum("actor", 0x0005, "Lulu", "PC_LULU");
        putEnum("actor", 0x0006, "Rikku", "PC_RIKKU");
        putEnum("actor", 0x0007, "Seymour", "PC_SEYMOUR");
        putEnum("actor", 0x0008, "Valefor", "PC_VALEFOR");
        putEnum("actor", 0x0009, "Ifrit", "PC_IFRIT");
        putEnum("actor", 0x000A, "Ixion", "PC_IXION");
        putEnum("actor", 0x000B, "Shiva", "PC_SHIVA");
        putEnum("actor", 0x000C, "Bahamut", "PC_BAHAMUT");
        putEnum("actor", 0x000D, "Anima", "PC_ANIMA");
        putEnum("actor", 0x000E, "Yojimbo", "PC_YOJIMBO");
        putEnum("actor", 0x000F, "Cindy", "PC_MAGUS1");
        putEnum("actor", 0x0010, "Sandy", "PC_MAGUS2");
        putEnum("actor", 0x0011, "Mindy", "PC_MAGUS3");
        putEnum("actor", 0x0012, null, "PC_DUMMY");
        putEnum("actor", 0x0013, null, "PC_DUMMY2");
        for (int i = 1; i <= 10; i++) {
            putEnum("actor", 0x0013 + i, "Monster#" + i);
        }
        putEnum("actor", 0x00FF, "Actor:None");

        for (int i = 0x1000; i <= 0x1200; i++) {
            putEnum("actor", i, "Actors:MonsterType=" + String.format("%04X", i - 0x1000));
        }
        putEnum("actor", 0xFFE6, null, "CHR_OWN_TARGET0");
        putEnum("actor", 0xFFE7, null, "CHR_ALL_PLY3");
        putEnum("actor", 0xFFE8, null, "CHR_ALL_PLAYER2");
        putEnum("actor", 0xFFE9, "AllCharsAndAeons", "CHR_ALL_PLAYER");
        putEnum("actor", 0xFFEA, null, "CHR_PARENT");
        putEnum("actor", 0xFFEB, "AllChars", "CHR_ALL2");
        putEnum("actor", 0xFFEC, "AllAeons", "CHR_ALL_SUMMON");
        putEnum("actor", 0xFFED, null, "CHR_ALL_PLY2");
        putEnum("actor", 0xFFEE, null, "CHR_INPUT");
        putEnum("actor", 0xFFEF, "LastAttacker", "CHR_REACTION");
        putEnum("actor", 0xFFF0, "PredefinedGroup", "CHR_OWN_TARGET");
        putEnum("actor", 0xFFF1, "AllMonsters", "CHR_ALL_MON");
        putEnum("actor", 0xFFF2, "FrontlineChars", "CHR_ALL_PLY");
        putEnum("actor", 0xFFF3, "Self", "CHR_OWN");
        putEnum("actor", 0xFFF4, "CharacterReserve#4", "CHR_PARTY7");
        putEnum("actor", 0xFFF5, "CharacterReserve#3", "CHR_PARTY6");
        putEnum("actor", 0xFFF6, "CharacterReserve#2", "CHR_PARTY5");
        putEnum("actor", 0xFFF7, "CharacterReserve#1", "CHR_PARTY4");
        putEnum("actor", 0xFFF8, "Character#3", "CHR_PARTY3");
        putEnum("actor", 0xFFF9, "Character#2", "CHR_PARTY2");
        putEnum("actor", 0xFFFA, "Character#1", "CHR_PARTY1");
        putEnum("actor", 0xFFFB, "AllActors", "CHR_ALL");
        putEnum("actor", 0xFFFC, "?TargetActorsImmediate", "CHR_TARGET_NOW");
        putEnum("actor", 0xFFFD, "TargetActors", "CHR_TARGET");
        putEnum("actor", 0xFFFE, "ActiveActors", "CHR_ACTIVE");
        putEnum("actor", 0xFFFF, "Actor:Null", "CHR_NOP");

        putActorProperty(0x0000, "HP", "int", "stat_hp");
        putActorProperty(0x0001, "MP", "int", "stat_mp");
        putActorProperty(0x0002, "maxHP", "int", "stat_maxhp");
        putActorProperty(0x0003, "maxMP", "int", "stat_maxmp");
        putActorProperty(0x0004, "isAlive/StatusDeath", "bool", "stat_alive");
        putActorProperty(0x0005, "StatusPoison", "bool", "stat_poison");
        putActorProperty(0x0006, "StatusPetrify", "bool", "stat_stone");
        putActorProperty(0x0007, "StatusZombie", "bool", "stat_zombie");
        putActorProperty(0x0008, "?StatusYellowHP", "bool", "stat_weak");
        putActorProperty(0x0009, "STR", "int", "stat_str");
        putActorProperty(0x000A, "DEF", "int", "stat_vit");
        putActorProperty(0x000B, "MAG", "int", "stat_mag");
        putActorProperty(0x000C, "MDF", "int", "stat_spirit");
        putActorProperty(0x000D, "AGI", "int", "stat_dex");
        putActorProperty(0x000E, "LCK", "int", "stat_luck");
        putActorProperty(0x000F, "EVA", "int", "stat_avoid");
        putActorProperty(0x0010, "ACC", "int", "stat_hit");
        putActorProperty(0x0011, "PoisonDamage%", "int", "stat_poison_per");
        putActorProperty(0x0012, "OverdriveMode", "int", "stat_limit_type");
        putActorProperty(0x0013, "OverdriveCurrent", "int", "stat_limit_gauge");
        putActorProperty(0x0014, "OverdriveMax", "int", "stat_limit_gauge_max");
        putActorProperty(0x0015, "isOnFrontline", "bool", "stat_inbattle");
        putActorProperty(0x0016, null, "bool", "stat_man");
        putActorProperty(0x0017, null, "bool", "stat_woman");
        putActorProperty(0x0018, null, "bool", "stat_summon");
        putActorProperty(0x0019, null, "bool", "stat_monster");
        putActorProperty(0x001A, null, "bool", "stat_fly");
        putActorProperty(0x001B, null, "bool", "stat_will_die");
        putActorProperty(0x001C, "?BattleRow", "int", "stat_area");
        putActorProperty(0x001D, "?BattleArenaStandingPosition", "int", "stat_pos");
        putActorProperty(0x001E, "BattleDistance", "int", "stat_far");
        putActorProperty(0x001F, null, "bool", "stat_group");
        putActorProperty(0x0020, "Armored", "bool", "stat_sp_hard");
        putActorProperty(0x0021, "?ImmuneToFractionalDmg", "bool", "stat_sp_ratio");
        putActorProperty(0x0022, "?ImmuneToLife", "bool", "stat_sp_zombie");
        putActorProperty(0x0023, "?ImmuneToSensor", "bool", "stat_sp_see");
        putActorProperty(0x0024, null, "bool", "stat_sp_live");
        putActorProperty(0x0025, "StatusPowerBreak", "bool", "stat_power_break");
        putActorProperty(0x0026, "StatusMagicBreak", "bool", "stat_magic_break");
        putActorProperty(0x0027, "StatusArmorBreak", "bool", "stat_armor_break");
        putActorProperty(0x0028, "StatusMentalBreak", "bool", "stat_mental_break");
        putActorProperty(0x0029, "StatusConfusion", "bool", "stat_confuse");
        putActorProperty(0x002A, "StatusBerserk", "bool", "stat_berserk");
        putActorProperty(0x002B, "StatusProvoke", "bool", "stat_prov");
        putActorProperty(0x002C, "StatusThreaten", "bool", "stat_threat");
        putActorProperty(0x002D, "StatusDurationSleep", "int", "stat_sleep");
        putActorProperty(0x002E, "StatusDurationSilence", "int", "stat_silence");
        putActorProperty(0x002F, "StatusDurationDarkness", "int", "stat_dark");
        putActorProperty(0x0030, "StatusDurationShell", "int", "stat_shell");
        putActorProperty(0x0031, "StatusDurationProtect", "int", "stat_protess");
        putActorProperty(0x0032, "StatusDurationReflect", "int", "stat_reflect");
        putActorProperty(0x0033, "StatusBlocksNulTide", "int", "stat_bawater");
        putActorProperty(0x0034, "StatusBlocksNulBlaze", "int", "stat_bafire");
        putActorProperty(0x0035, "StatusBlocksNulShock", "int", "stat_bathunder");
        putActorProperty(0x0036, "StatusBlocksNulFrost", "int", "stat_bacold");
        putActorProperty(0x0037, "StatusDurationRegen", "int", "stat_regen");
        putActorProperty(0x0038, "StatusDurationHaste", "int", "stat_haste");
        putActorProperty(0x0039, "StatusDurationSlow", "int", "stat_slow");
        putActorProperty(0x003A, "?Sensor", "bool", "ability_see");
        putActorProperty(0x003B, "?FirstStrike", "bool", "ability_lead");
        putActorProperty(0x003C, "?Initiative", "bool", "ability_first");
        putActorProperty(0x003D, "CounterAttack", "bool", "ability_counter");
        putActorProperty(0x003E, "?EvadeAndCounter", "bool", "ability_counter2");
        putActorProperty(0x003F, null, "bool", "ability_dark");
        putActorProperty(0x0040, null, "bool", "ability_ap2");
        putActorProperty(0x0041, null, "bool", "ability_exp2");
        putActorProperty(0x0042, "?MagicBooster", "bool", "ability_booster");
        putActorProperty(0x0043, "?MagicCounter", "bool", "ability_magic_counter");
        putActorProperty(0x0044, "?Alchemy", "bool", "ability_medicine");
        putActorProperty(0x0045, "?Auto-Potion", "bool", "ability_auto_potion");
        putActorProperty(0x0046, "?Auto-Med", "bool", "ability_auto_cureall");
        putActorProperty(0x0047, "?Auto-Phoenix", "bool", "ability_auto_phenix");
        putActorProperty(0x0048, null, "bool", "ability_limitup");
        putActorProperty(0x0049, null, "bool", "ability_dream");
        putActorProperty(0x004A, null, "bool", "ability_pierce");
        putActorProperty(0x004B, null, "bool", "ability_exchange");
        putActorProperty(0x004C, null, "bool", "ability_hp_recover");
        putActorProperty(0x004D, null, "bool", "ability_mp_recover");
        putActorProperty(0x004E, null, "bool", "ability_nonencount");
        putActorProperty(0x004F, "DeathAnimation", "deathAnimation", "stat_death_pattern");
        putActorProperty(0x0050, null, "unknown", "stat_event_chr");
        putActorProperty(0x0051, "GetsTurns", "bool", "stat_action");
        putActorProperty(0x0052, "Targetable", "bool", "stat_cursor");
        putActorProperty(0x0053, "VisibleOnCTB", "bool", "stat_ctb_list");
        putActorProperty(0x0054, null, "unknown", "stat_visible");
        putActorProperty(0x0055, "?Location1", "int", "stat_move_area");
        putActorProperty(0x0056, "?Location2(Tonberry)", "int", "stat_move_pos");
        putActorProperty(0x0057, null, "int", "stat_efflv");
        putActorProperty(0x0058, null, "unknown", "stat_model");
        putActorProperty(0x0059, "?Host", "actor", "stat_damage_chr");
        putActorProperty(0x005A, null, "unknown", "stat_move_target");
        putActorProperty(0x005B, "AnimationsVariant", "int", "stat_motionlv");
        putActorProperty(0x005C, null, "unknown", "stat_nop");
        putActorProperty(0x005D, null, "bool", "stat_move_flag");
        putActorProperty(0x005E, null, "unknown", "stat_live_motion");
        putActorProperty(0x005F, null, "unknown", "stat_adjust_pos");
        putActorProperty(0x0060, null, "unknown", "stat_height_on");
        putActorProperty(0x0061, null, "unknown", "stat_sleep_recover_flag");
        putActorProperty(0x0062, "AbsorbFire", "bool", "stat_abs_fire");
        putActorProperty(0x0063, "AbsorbIce", "bool", "stat_abs_cold");
        putActorProperty(0x0064, "AbsorbThunder", "bool", "stat_abs_thunder");
        putActorProperty(0x0065, "AbsorbWater", "bool", "stat_abs_water");
        putActorProperty(0x0066, "AbsorbHoly", "bool", "stat_abs_holy");
        putActorProperty(0x0067, "NullFire", "bool", "stat_inv_fire");
        putActorProperty(0x0068, "NullIce", "bool", "stat_inv_cold");
        putActorProperty(0x0069, "NullThunder", "bool", "stat_inv_thunder");
        putActorProperty(0x006A, "NullWater", "bool", "stat_inv_water");
        putActorProperty(0x006B, "NullHoly", "bool", "stat_inv_holy");
        putActorProperty(0x006C, "ResistFire", "bool", "stat_half_fire");
        putActorProperty(0x006D, "ResistIce", "bool", "stat_half_cold");
        putActorProperty(0x006E, "ResistThunder", "bool", "stat_half_thunder");
        putActorProperty(0x006F, "ResistWater", "bool", "stat_half_water");
        putActorProperty(0x0070, "ResistHoly", "bool", "stat_half_holy");
        putActorProperty(0x0071, "WeakFire", "bool", "stat_weak_fire");
        putActorProperty(0x0072, "WeakIce", "bool", "stat_weak_cold");
        putActorProperty(0x0073, "WeakThunder", "bool", "stat_weak_thunder");
        putActorProperty(0x0074, "WeakWater", "bool", "stat_weak_water");
        putActorProperty(0x0075, "WeakHoly", "bool", "stat_weak_holy");
        putActorProperty(0x0076, null, "bool", "stat_adjust_pos_flag");
        putActorProperty(0x0077, null, "bool", "stat_inv_physic_motion");
        putActorProperty(0x0078, null, "bool", "stat_inv_magic_motion");
        putActorProperty(0x0079, "TimesStolenFrom", "int", "stat_steal_count");
        putActorProperty(0x007A, null, "bool", "stat_wait_motion_flag");
        putActorProperty(0x007B, null, "bool", "stat_attack_return_flag");
        putActorProperty(0x007C, null, "unknown", "stat_attack_normal_frame");
        putActorProperty(0x007D, "?Tough (No Delay recoil)", "bool", "stat_disable_move_flag");
        putActorProperty(0x007E, "?Heavy (No lift off ground)", "bool", "stat_disable_jump_flag");
        putActorProperty(0x007F, null, "bool", "stat_bodyhit_flag");
        putActorProperty(0x0080, null, "unknown", "stat_effvar");
        putActorProperty(0x0081, "StealItemCommonType", "move", "stat_item");
        putActorProperty(0x0082, "StealItemCommonAmount", "int", "stat_item_num");
        putActorProperty(0x0083, "StealItemRareType", "move", "stat_rareitem");
        putActorProperty(0x0084, "StealItemRareAmount", "int", "stat_rareitem_num");
        putActorProperty(0x0085, null, "unknown", "stat_magiclv");
        putActorProperty(0x0086, "?showBirthAnimation", "bool", "stat_appear_motion_flag");
        putActorProperty(0x0087, null, "unknown", "stat_cursor_element");
        putActorProperty(0x0088, null, "unknown", "stat_limit_bar_flag_cam");
        putActorProperty(0x0089, "showOverdriveBar", "bool", "stat_limit_bar_flag");
        putActorProperty(0x008A, "Item1DropChance", "int", "stat_drop1");
        putActorProperty(0x008B, "Item2DropChance", "int", "stat_drop2");
        putActorProperty(0x008C, "GearDropChance", "int", "stat_weapon_drop");
        putActorProperty(0x008D, "StealChance", "int", "stat_steal");
        putActorProperty(0x008E, "?MustBeKilledForBattleEnd", "bool", "stat_exist_flag");
        putActorProperty(0x008F, "?StatusScan", "bool", "stat_live");
        putActorProperty(0x0090, "StatusDistillPower", "bool", "stat_str_memory");
        putActorProperty(0x0091, "?StatusDistillMana", "bool", "stat_mag_memory");
        putActorProperty(0x0092, "?StatusDistillSpeed", "bool", "stat_dex_memory");
        putActorProperty(0x0093, "StatusUnusedDash", "bool", "stat_move_memory");
        putActorProperty(0x0094, "?StatusDistillAbility", "bool", "stat_ability_memory");
        putActorProperty(0x0095, "StatusShield", "bool", "stat_dodge");
        putActorProperty(0x0096, "StatusBoost", "bool", "stat_defend");
        putActorProperty(0x0097, "StatusEject", "bool", "stat_blow");
        putActorProperty(0x0098, "StatusAutoLife", "bool", "stat_relife");
        putActorProperty(0x0099, "StatusCurse", "bool", "stat_curse");
        putActorProperty(0x009A, "StatusDefend", "bool", "stat_defense");
        putActorProperty(0x009B, "StatusGuard", "bool", "stat_protect");
        putActorProperty(0x009C, "StatusSentinel", "bool", "stat_iron");
        putActorProperty(0x009D, "StatusDoom", "bool", "stat_death_sentence");
        putActorProperty(0x009E, null, "unknown", "stat_motion_type");
        putActorProperty(0x009F, "DoomCounterInitial", "int", "stat_death_sentence_start");
        putActorProperty(0x00A0, "?DoomCounterCurrent", "int", "stat_death_sentence_count");
        putActorProperty(0x00A1, null, "unknown", "stat_dmg_dir");
        putActorProperty(0x00A2, null, "unknown", "stat_direction_change_flag");
        putActorProperty(0x00A3, null, "unknown", "stat_direction_change_effect");
        putActorProperty(0x00A4, null, "unknown", "stat_direction_fix_flag");
        putActorProperty(0x00A5, null, "unknown", "stat_hit_terminate_flag");
        putActorProperty(0x00A6, "LastDamageTakenHP", "int", "stat_damage_hp");
        putActorProperty(0x00A7, "LastDamageTakenMP", "int", "stat_damage_mp");
        putActorProperty(0x00A8, "LastDamageTakenCTB", "int", "stat_damage_ctb");
        putActorProperty(0x00A9, null, "unknown", "stat_appear_invisible_flag");
        putActorProperty(0x00AA, null, "unknown", "stat_effect_hit_num");
        putActorProperty(0x00AB, null, "bool", "stat_avoid_flag");
        putActorProperty(0x00AC, null, "unknown", "stat_blow_exist_flag");
        putActorProperty(0x00AD, null, "unknown", "stat_escape_flag");
        putActorProperty(0x00AE, "?Visible", "bool", "stat_hide");
        putActorProperty(0x00AF, "?StatusResistanceDeath", "int", "stat_def_death");
        putActorProperty(0x00B0, "?StatusResistanceZombie", "int", "stat_def_zombie");
        putActorProperty(0x00B1, "?StatusResistancePetrify", "int", "stat_def_stone");
        putActorProperty(0x00B2, "?StatusResistancePoison", "int", "stat_def_poison");
        putActorProperty(0x00B3, "?StatusResistancePowerBreak", "int", "stat_def_power_break");
        putActorProperty(0x00B4, "?StatusResistanceMagicBreak", "int", "stat_def_magic_break");
        putActorProperty(0x00B5, "?StatusResistanceArmorBreak", "int", "stat_def_armor_break");
        putActorProperty(0x00B6, "?StatusResistanceMentalBreak", "int", "stat_def_mental_break");
        putActorProperty(0x00B7, "?StatusResistanceConfusion", "int", "stat_def_confuse");
        putActorProperty(0x00B8, "?StatusResistanceBerserk", "int", "stat_def_berserk");
        putActorProperty(0x00B9, "?StatusResistanceProvoke", "int", "stat_def_prov");
        putActorProperty(0x00BA, "StatusChanceThreaten", "int", "stat_def_threat");
        putActorProperty(0x00BB, "StatusResistanceSleep", "int", "stat_def_sleep");
        putActorProperty(0x00BC, "StatusResistanceSilence", "int", "stat_def_silence");
        putActorProperty(0x00BD, "StatusResistanceDarkness", "int", "stat_def_dark");
        putActorProperty(0x00BE, "StatusResistanceShell", "int", "stat_def_shell");
        putActorProperty(0x00BF, "StatusResistanceProtect", "int", "stat_def_protess");
        putActorProperty(0x00C0, "StatusResistanceReflect", "int", "stat_def_reflect");
        putActorProperty(0x00C1, "StatusResistanceNulTide", "int", "stat_def_bawater");
        putActorProperty(0x00C2, "StatusResistanceNulBlaze", "int", "stat_def_bafire");
        putActorProperty(0x00C3, "StatusResistanceNulShock", "int", "stat_def_bathunder");
        putActorProperty(0x00C4, "StatusResistanceNulFrost", "int", "stat_def_bacold");
        putActorProperty(0x00C5, "StatusResistanceRegen", "int", "stat_def_regen");
        putActorProperty(0x00C6, "StatusResistanceHaste", "int", "stat_def_haste");
        putActorProperty(0x00C7, "StatusResistanceSlow", "int", "stat_def_slow");
        putActorProperty(0x00C8, "?StatusImmunityScan", "bool", "stat_def_live");
        putActorProperty(0x00C9, "?StatusImmunityDistillPower", "bool", "stat_def_str_memory");
        putActorProperty(0x00CA, "?StatusImmunityDistillMana", "bool", "stat_def_mag_memory");
        putActorProperty(0x00CB, "?StatusImmunityDistillSpeed", "bool", "stat_def_dex_memory");
        putActorProperty(0x00CC, "?StatusImmunityUnusedDash", "bool", "stat_def_move_memory");
        putActorProperty(0x00CD, "?StatusImmunityDistillAbility", "bool", "stat_def_ability_memory");
        putActorProperty(0x00CE, "?StatusImmunityShield", "bool", "stat_def_dodge");
        putActorProperty(0x00CF, "?StatusImmunityBoost", "bool", "stat_def_defend");
        putActorProperty(0x00D0, "?StatusImmunityAutoLife", "bool", "stat_def_relife");
        putActorProperty(0x00D1, "?StatusImmunityEject", "bool", "stat_def_blow");
        putActorProperty(0x00D2, "?StatusImmunityCurse", "bool", "stat_def_curse");
        putActorProperty(0x00D3, "?StatusImmunityDefend", "bool", "stat_def_defense");
        putActorProperty(0x00D4, "?StatusImmunityGuard", "bool", "stat_def_protect");
        putActorProperty(0x00D5, "?StatusImmunitySentinel", "bool", "stat_def_iron");
        putActorProperty(0x00D6, "?StatusImmunityDoom", "bool", "stat_def_death_sentence");
        putActorProperty(0x00D7, "?VisibleOnFrontlinePartyList", "bool", "stat_hp_list");
        putActorProperty(0x00D8, null, "unknown", "stat_visible_cam");
        putActorProperty(0x00D9, null, "unknown", "stat_visible_out");
        putActorProperty(0x00DA, null, "unknown", "stat_round");
        putActorProperty(0x00DB, null, "unknown", "stat_round_return");
        putActorProperty(0x00DC, null, "unknown", "stat_win_pose");
        putActorProperty(0x00DD, null, "unknown", "stat_vigor");
        putActorProperty(0x00DE, null, "unknown", "stat_fast_model_flag");
        putActorProperty(0x00DF, null, "unknown", "stat_alive_not_stone");
        putActorProperty(0x00E0, null, "unknown", "stat_command_type");
        putActorProperty(0x00E1, null, "unknown", "stat_effect_target_flag");
        putActorProperty(0x00E2, null, "unknown", "stat_magic_effect_ground");
        putActorProperty(0x00E3, null, "unknown", "stat_magic_effect_water");
        putActorProperty(0x00E4, null, "unknown", "stat_idle2_prob");
        putActorProperty(0x00E5, null, "unknown", "stat_attack_motion_type");
        putActorProperty(0x00E6, null, "unknown", "stat_attack_inc_speed");
        putActorProperty(0x00E7, null, "unknown", "stat_attack_dec_speed");
        putActorProperty(0x00E8, "CurrentTurnDelay", "int", "stat_ctb");
        putActorProperty(0x00E9, null, "unknown", "stat_appear_count");
        putActorProperty(0x00EA, null, "unknown", "stat_motion_num");
        putActorProperty(0x00EB, null, "unknown", "stat_info_mes_id");
        putActorProperty(0x00EC, null, "unknown", "stat_live_mes_id");
        putActorProperty(0x00ED, null, "unknown", "stat_visible_eff");
        putActorProperty(0x00EE, null, "unknown", "stat_motion_dispose_flag");
        putActorProperty(0x00EF, null, "unknown", "stat_model_dispose_flag");
        putActorProperty(0x00F0, null, "unknown", "stat_def_ctb");
        putActorProperty(0x00F1, null, "unknown", "stat_shadow");
        putActorProperty(0x00F2, null, "unknown", "stat_death");
        putActorProperty(0x00F3, null, "unknown", "stat_death_stone");
        putActorProperty(0x00F4, null, "unknown", "stat_check_pos");
        putActorProperty(0x00F5, null, "unknown", "stat_win_se");
        putActorProperty(0x00F6, null, "unknown", "stat_attack_num");
        putActorProperty(0x00F7, null, "unknown", "stat_near_motion");
        putActorProperty(0x00F8, null, "unknown", "stat_talk_stat1");
        putActorProperty(0x00F9, null, "unknown", "stat_talk_stat2");
        putActorProperty(0x00FA, "?ForceCloseRangeAttackAnim", "bool", "stat_near_motion_set");
        putActorProperty(0x00FB, null, "unknown", "stat_motion_speed_normal");
        putActorProperty(0x00FC, null, "unknown", "stat_motion_speed_normal_start");
        putActorProperty(0x00FD, null, "unknown", "stat_own_attack_near");
        putActorProperty(0x00FE, null, "unknown", "stat_talk_stat3");
        putActorProperty(0x00FF, null, "unknown", "stat_command_set");
        putActorProperty(0x0100, "?RetainsControlWhenProvoked", "bool", "stat_prov_command_flag");
        putActorProperty(0x0101, "ProvokerActor", "actor", "stat_prov_chr");
        putActorProperty(0x0102, "?Spellspring", "bool", "stat_use_mp0");
        putActorProperty(0x0103, "?CTBIcon", "int", "stat_icon_number");
        putActorProperty(0x0104, null, "unknown", "stat_sound_hit_num");
        putActorProperty(0x0105, null, "unknown", "stat_damage_num_pos");
        putActorProperty(0x0106, null, "unknown", "stat_summoner");
        putActorProperty(0x0107, "NullDamage", "bool", "stat_sp_invincible");
        putActorProperty(0x0108, "NullMagic", "bool", "stat_sp_inv_magic");
        putActorProperty(0x0109, "NullPhysical", "bool", "stat_sp_inv_physic");
        putActorProperty(0x010A, "LearnableRonsoRage", "move", "stat_blue_magic");
        putActorProperty(0x010B, null, "unknown", "stat_sp_disable_zan");
        putActorProperty(0x010C, "OverkillThreshold", "int", "stat_over_kill_hp");
        putActorProperty(0x010D, null, "unknown", "stat_return_motion_type");
        putActorProperty(0x010E, null, "unknown", "stat_cam_width");
        putActorProperty(0x010F, null, "unknown", "stat_cam_height");
        putActorProperty(0x0110, null, "unknown", "stat_height");
        putActorProperty(0x0111, "YojimboCompatibility", "int", "stat_youjinbo");
        putActorProperty(0x0112, "YojimboGivenGil", "int", "stat_payment");
        putActorProperty(0x0113, "ZanmatoLevel", "int", "stat_monster_value_max");
        putActorProperty(0x0114, "TurnsTaken", "int", "stat_command_exe_count");
        putActorProperty(0x0115, "YojimboReaction", "yojimboReaction", "stat_consent");
        putActorProperty(0x0116, null, "unknown", "stat_attack_near_frame");
        putActorProperty(0x0117, "MagusSisterMotivation", "int", "stat_energy");
        putActorProperty(0x0118, null, "unknown", "stat_limit_gauge_add");
        putActorProperty(0x0119, "NearDeath", "bool", "stat_hp_half");
        putActorProperty(0x011A, "?OverdriveAvailable", "int", "stat_limit_gauge_check");
        putActorProperty(0x011B, null, "unknown", "stat_hp_check");
        putActorProperty(0x011C, null, "unknown", "stat_mp_check");
        putActorProperty(0x011D, null, "unknown", "stat_ba_all_check");
        putActorProperty(0x011E, null, "unknown", "stat_shell_reflect");
        putActorProperty(0x011F, null, "unknown", "stat_protess_reflect");
        putActorProperty(0x0120, null, "unknown", "stat_haste_reflect");
        putActorProperty(0x0121, null, "unknown", "stat_weak_motion");
        putActorProperty(0x0122, "?BribeImmunity", "unknown", "stat_sp_wairo");
        putActorProperty(0x0123, null, "unknown", "stat_attack_motion_frame");
        putActorProperty(0x0124, null, "unknown", "stat_motion_type_reset");
        putActorProperty(0x0125, null, "unknown", "stat_motion_type_add");
        putActorProperty(0x0126, null, "unknown", "stat_death_status");
        putActorProperty(0x0127, null, "unknown", "stat_target_list");
        putActorProperty(0x0128, null, "unknown", "stat_limit_bar_pos");
        putActorProperty(0x0129, null, "unknown", "stat_center_chr_flag");
        putActorProperty(0x012A, null, "unknown", "stat_same_target_check");
        putActorProperty(0x012B, "APRewardNormal", "int", "stat_get_ap");
        putActorProperty(0x012C, "APRewardOverkill", "int", "stat_get_over_ap");
        putActorProperty(0x012D, "GilReward", "int", "stat_get_gill");
        putActorProperty(0x012E, "BonusSTR", "int", "stat_str_up");
        putActorProperty(0x012F, "?BonusDEF", "int", "stat_vit_up");
        putActorProperty(0x0130, "?BonusMAG", "int", "stat_mag_up");
        putActorProperty(0x0131, "BonusMDF", "int", "stat_spirit_up");
        putActorProperty(0x0132, "?BonusAGI", "int", "stat_dex_up");
        putActorProperty(0x0133, "?BonusLCK", "int", "stat_luck_up");
        putActorProperty(0x0134, "?BonusEVA", "int", "stat_avoid_up");
        putActorProperty(0x0135, "?BonusACC", "int", "stat_hit_up");
        putActorProperty(0x0136, null, "unknown", "stat_use_mp");
        putActorProperty(0x0137, null, "unknown", "stat_use_limit");
        putActorProperty(0x0138, null, "unknown", "stat_use_limit_all");
        putActorProperty(0x0139, "isDoublecasting", "bool", "stat_continue_magic");
        putActorProperty(0x013A, "?Item1CommonType", "move", "stat_item1_com");
        putActorProperty(0x013B, "?Item1RareType", "move", "stat_item1_rare");
        putActorProperty(0x013C, "?Item2CommonType", "move", "stat_item2_com");
        putActorProperty(0x013D, "?Item2RareType", "move", "stat_item2_rare");
        putActorProperty(0x013E, "?Item1CommonTypeOverkill", "move", "stat_item1_com_over_kill");
        putActorProperty(0x013F, "?Item1RareTypeOverkill", "move", "stat_item1_rare_over_kill");
        putActorProperty(0x0140, "?Item2CommonTypeOverkill", "move", "stat_item2_com_over_kill");
        putActorProperty(0x0141, "?Item2RareTypeOverkill", "move", "stat_item2_rare_over_kill");
        putActorProperty(0x0142, "?Item1CommonAmount", "int", "stat_item1_com_num");
        putActorProperty(0x0143, "?Item1RareAmount", "int", "stat_item1_rare_num");
        putActorProperty(0x0144, "?Item2CommonAmount", "int", "stat_item2_com_num");
        putActorProperty(0x0145, "?Item2RareAmount", "int", "stat_item2_rare_num");
        putActorProperty(0x0146, "?Item1CommonAmountOverkill", "int", "stat_item1_com_over_kill_num");
        putActorProperty(0x0147, "?Item1RareAmountOverkill", "int", "stat_item1_rare_over_kill_num");
        putActorProperty(0x0148, "?Item2CommonAmountOverkill", "int", "stat_item2_com_over_kill_num");
        putActorProperty(0x0149, "?Item2RareAmountOverkill", "int", "stat_item2_rare_over_kill_num");
        putActorProperty(0x014A, null, "unknown", "stat_death_return");
        putActorProperty(0x014B, null, "unknown", "stat_linear_move_reset");
        putActorProperty(0x014C, null, "unknown", "stat_bodyhit_direct");
        putActorProperty(0x014D, "?recruited (Aeon)", "bool", "stat_join");
        putActorProperty(0x014E, "permanentAutoLife", "bool", "stat_eternal_relife");
        putActorProperty(0x014F, null, "unknown", "stat_neck_target_flag");
        putActorProperty(0x0150, null, "unknown", "stat_visible_out_on");
        putActorProperty(0x0151, null, "unknown", "stat_regen_damage_flag");
        putActorProperty(0x0152, null, "unknown", "stat_num_print_element");

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
        ScriptField field = new ScriptField(name, type, internalName);
        field.idx = idx;
        getEnumMap(type).put(idx, field);
    }

    private static void putCompOperator(int idx, String name, String type, String internalName) {
        ScriptField field = new ScriptField(name, type, internalName);
        field.idx = idx;
        COMP_OPERATORS.put(idx, field);
    }

    private static void putActorProperty(int idx, String name, String type, String internalName) {
        ScriptField field = new ScriptField(name, type, internalName);
        field.idx = idx;
        getEnumMap("actorProperty").put(idx, field);
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
}
